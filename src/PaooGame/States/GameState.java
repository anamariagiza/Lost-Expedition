package PaooGame.States;

import PaooGame.Entities.Player;
import PaooGame.Map.Map;
import PaooGame.RefLinks;
import PaooGame.Camera.GameCamera;
import PaooGame.Tiles.Tile;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.image.BufferedImage;

/*! \class public class GameState extends State
    \brief Implementeaza starea de joc.
    In aceasta stare se desfasoara propriu-zis jocul: harta este incarcata si desenata,
    jucatorul interactioneaza cu lumea etc.
 */
public class GameState extends State {

    private Map currentMap;
    private Player player;
    private String[] levelPaths = {"/maps/level_1.tmx", "/maps/level2.tmx", "/maps/level3.tmx"};
    private int currentLevelIndex;

    private float currentZoomTarget = 1.0f; // NOU: Tinta zoom-ului, incepe cu 1.0

    /*! \fn public GameState(RefLinks refLink)
        \brief Constructorul de initializare al clasei GameState.
        \param refLink O referinta catre un obiect "shortcut".
     */
    public GameState(RefLinks refLink) {
        super(refLink);
        this.currentLevelIndex = 0;
        InitLevel(this.currentLevelIndex);
        System.out.println("✓ GameState initializat");
        // Seteaza zoom-ul initial al camerei la intrarea in GameState
        refLink.GetGameCamera().setZoomLevel(currentZoomTarget);
    }

    /*! \fn private void InitLevel(int levelIndex)
        \brief Metoda de initializare a unui nivel, incarcand harta si pozitionand player-ul si camera.
     */
    private void InitLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levelPaths.length) {
            currentLevelIndex = levelIndex;
            currentMap = refLink.GetMap();
            if (currentMap != null) {
                currentMap.LoadMapFromFile(levelPaths[currentLevelIndex]);
                System.out.println("Nivelul " + (currentLevelIndex + 1) + " incarcat: " + levelPaths[currentLevelIndex]);
            } else {
                System.err.println("Eroare: Obiectul Map nu a fost initializat in RefLinks!");
                return;
            }

            player = refLink.GetPlayer();
            if (player != null) {
                player.SetPosition(100, 100);
            } else {
                System.err.println("Eroare: Obiectul Player nu a fost initializat in RefLinks!");
            }

            if (currentMap != null && player != null) {
                refLink.GetGameCamera().centerOnEntity(player);
            }

        } else {
            System.err.println("Index nivel invalid: " + levelIndex + ". Se incearca incarcarea primului nivel.");
            currentLevelIndex = 0;
            InitLevel(currentLevelIndex);
        }
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea elementelor din joc, inclusiv jucatorul si camera.
     */
    @Override
    public void Update() {
        // Verificam input pentru a schimba nivelul
        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_N)) {
            if (currentLevelIndex + 1 < levelPaths.length) {
                InitLevel(currentLevelIndex + 1);
            } else {
                System.out.println("Ultimul nivel atins!");
            }
        }
        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_P)) {
            if (currentLevelIndex - 1 >= 0) {
                InitLevel(currentLevelIndex - 1);
            } else {
                System.out.println("Primul nivel atins!");
            }
        }
        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
            refLink.SetState(new MenuState(refLink));
        }

        // NOU: Logică de comutare zoom între 1.0 și 1.5
        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_Z)) { // Folosim tasta 'Z'
            if (currentZoomTarget == 1.0f) {
                currentZoomTarget = 1.5f;
            } else {
                currentZoomTarget = 1.0f;
            }
            refLink.GetGameCamera().setZoomLevel(currentZoomTarget);
            System.out.println("Zoom Level comutat la: " + currentZoomTarget);
        }


        // Logica de zoom manuala (+/-) poate fi păstrată pentru debugging sau eliminată
        // dacă vrei doar cele două opțiuni fixe.
        /*
        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_PLUS) || refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_EQUALS)) {
            refLink.GetGameCamera().setZoomLevel(refLink.GetGameCamera().getZoomLevel() + 0.1f);
            System.out.println("Zoom Level: " + refLink.GetGameCamera().getZoomLevel());
        }
        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_MINUS)) {
            refLink.GetGameCamera().setZoomLevel(refLink.GetGameCamera().getZoomLevel() - 0.1f);
            System.out.println("Zoom Level: " + refLink.GetGameCamera().getZoomLevel());
        }
        */

        if (currentMap != null) {
            currentMap.Update();
        }
        if (player != null) {
            player.Update();
        }
    }

    /*! \fn public void Draw(Graphics g)
        \brief Deseneaza elementele specifice starii de joc pe ecran.
        \param g Contextul grafic in care sa se realizeze desenarea.
     */
    @Override
    public void Draw(Graphics g) {
        GameCamera camera = refLink.GetGameCamera();

        if (currentMap != null) {
            currentMap.Draw(g);
        }

        if (player != null) {
            player.Draw(g, camera);
        } else {
            g.setColor(Color.RED);
            g.drawString("PLAYER NULL!", refLink.GetWidth() / 2, refLink.GetHeight() / 2);
        }

        // Informatii de debug
        g.setColor(Color.WHITE);
        g.drawString("Nivel curent: " + (currentLevelIndex + 1), 10, 20);
        g.drawString("Apasa N/P pentru a schimba nivelul.", 10, 40);
        g.drawString("Apasa ESC pentru Meniu.", 10, 60);
        g.drawString("Foloseste W,A,S,D pentru miscare.", 10, 80);
        g.drawString("Apasa SHIFT pentru alergat.", 10, 100);
        g.drawString("Apasa SPACE pentru saritura.", 10, 120);
        g.drawString("Zoom: " + String.format("%.1f", camera.getZoomLevel()), 10, 140);
        g.drawString("Apasa Z pentru a comuta zoom.", 10, 160); // NOU: instructiune
    }

    public Map GetMap() {
        return currentMap;
    }
}