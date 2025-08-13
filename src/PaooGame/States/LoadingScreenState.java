package PaooGame.States;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Tiles.Tile;
import PaooGame.Map.Map;
import PaooGame.Entities.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

/*!
 * \class public class LoadingScreenState extends State
 * \brief Implementeaza starea de incarcare a jocului (splash screen cu bara de progres).
 * In aceasta stare se va afisa logo-ul si o bara de progres pe masura ce se incarca toate asset-urile mari
 * ale jocului in background.
 */
public class LoadingScreenState extends State {

    private long startTime;
    private final int MIN_LOADING_TIME_MS = 2000;
    private volatile boolean assetsLoaded = false;
    private volatile boolean tilesInitialized = false;
    private volatile float progress = 0.0f;
    private Thread loadingThread;

    public LoadingScreenState(RefLinks refLink) {
        super(refLink);
        startTime = System.currentTimeMillis();
        System.out.println("✓ LoadingScreenState initializat. Incepe thread-ul de incarcare.");

        loadingThread = new Thread(() -> {
            try {
                // Pasul 1: Incarca asset-urile mari (50% progres)
                System.out.println("LoadingScreenState: Incepe incarcarea asset-urilor...");
                Assets.LoadGameAssets();
                progress = 0.5f;

                // Verifică o animație specifică în loc de "AllDirections"
                if (Assets.playerIdleDown == null || Assets.playerIdleDown.length == 0 || Assets.playerIdleDown[0] == null) {
                    System.err.println("Eroare critica: Cadrele de animatie pentru 'Idle' ale playerului nu au fost incarcate corect. Animațiile nu vor functiona.");
                    assetsLoaded = false;
                } else {
                    assetsLoaded = true;
                }
                System.out.println("LoadingScreenState: Asset-uri incarcate. Progres: " + progress * 100 + "%");


                // Pasul 2: Initializare dale (25% progres)
                System.out.println("LoadingScreenState: Incepe initializarea dalelor...");
                Tile.InitTiles();
                tilesInitialized = true;
                progress = 0.75f;
                System.out.println("LoadingScreenState: Dale initializate. Progres: " + progress * 100 + "%");

                // Pasul 3: Creare obiecte joc (Player, Map) - necesare pentru GameState (25% progres)
                System.out.println("LoadingScreenState: Incepe crearea obiectelor jocului...");
                Player player = new Player(refLink.GetGame(), 100, 100);
                refLink.SetPlayer(player);

                Map map = new Map(refLink);
                refLink.SetMap(map);
                System.out.println("LoadingScreenState: Obiecte joc created. Progres: " + progress * 100 + "%");

                // Finalizarea incarcarii
                progress = 1.0f;
                System.out.println("LoadingScreenState: Incarcare completa. Progres: " + progress * 100 + "%");
            } catch (Exception e) {
                System.err.println("Eroare la incarcarea asset-urilor sau initializarea dalelor/obiectelor joc: " + e.getMessage());
                e.printStackTrace();
                assetsLoaded = false;
                progress = -1.0f;
            }
        });
        loadingThread.start();
    }

    @Override
    public void Update() {
        if (loadingThread.isAlive()) {
            return;
        }

        if (progress < 0) {
            System.err.println("Eroare fatala la incarcarea jocului. Se inchide aplicatia.");
            System.exit(1);
            return;
        }

        if (assetsLoaded && tilesInitialized && (System.currentTimeMillis() - startTime) >= MIN_LOADING_TIME_MS) {
            System.out.println("LoadingScreenState: Trece la MenuState.");
            refLink.SetState(new MenuState(refLink));
        }
    }

    @Override
    public void Draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        if (Assets.gameLogo != null) {
            int logoWidth = Assets.gameLogo.getWidth();
            int logoHeight = Assets.gameLogo.getHeight();
            int drawX = (refLink.GetWidth() - logoWidth) / 2;
            int drawY = (refLink.GetHeight() / 2 - logoHeight / 2) - 50;
            g.drawImage(Assets.gameLogo, drawX, drawY, logoWidth, logoHeight, null);
        }

        int barWidth = refLink.GetWidth() - 200;
        int barHeight = 20;
        int barX = (refLink.GetWidth() - barWidth) / 2;
        int barY = refLink.GetHeight() - 100;

        g.setColor(Color.GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);
        g.setColor(new Color(50, 205, 50));
        int currentProgressWidth = (int)(barWidth * progress);
        g.fillRect(barX, barY, currentProgressWidth, barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);
        String progressText = (int)(progress * 100) + "% Loaded";
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(progressText);
        g.drawString(progressText, barX + (barWidth - textWidth) / 2, barY - 10);
        String statusMessage = "Loading Game Assets...";
        if (progress >= 0.75f && progress < 1.0f) {
            statusMessage = "Initializing Game Components...";
        } else if (progress == 1.0f) {
            statusMessage = "Loading Complete! Starting Game...";
        } else if (progress < 0) {
            statusMessage = "Loading Error! Check Console.";
        }

        int statusWidth = fm.stringWidth(statusMessage);
        g.drawString(statusMessage, refLink.GetWidth() / 2 - statusWidth / 2, refLink.GetHeight() - 130);
    }
}