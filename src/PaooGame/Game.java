package PaooGame;

import PaooGame.GameWindow.GameWindow;
import PaooGame.Graphics.Assets;
import PaooGame.Tiles.Tile;
import PaooGame.States.State;
import PaooGame.States.LoadingScreenState;
import PaooGame.Input.KeyManager;
import PaooGame.Input.MouseManager;
import PaooGame.Entities.Player;
import PaooGame.Camera.GameCamera;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

/**
 * @class Game
 * @brief Clasa principala a intregului proiect.
 * Implementeaza bucla principala a jocului (Game Loop), gestioneaza starea
 * curenta a jocului, input-ul si toate componentele majore ale motorului de joc.
 */
public class Game implements Runnable
{
    /** Fereastra principala a jocului.*/
    private final GameWindow wnd;
    private boolean runState = false;

    /** Firul de executie a jocului. */
    private Thread gameThread;

    /** Grafica si randare. */
    private BufferStrategy bs;
    private Graphics g;

    private State currentState;

    /**  Input. */
    private final KeyManager keyManager = new KeyManager();
    private final MouseManager mouseManager;

    /** Obiecte de legaturi (handler). */
    private final RefLinks refLink;

    private boolean fullScreenMode = false;
    private Player player;
    private final GameCamera gameCamera;

    /**
     * @brief Constructorul clasei Game.
     * @param title Titlul ferestrei jocului.
     * @param width Latimea ferestrei in pixeli.
     * @param height Inaltimea ferestrei in pixeli.
     */
    public Game(String title, int width, int height)
    {
        wnd = new GameWindow(title, width, height);
        mouseManager = new MouseManager();
        gameCamera = new GameCamera(this, 0, 0);
        refLink = new RefLinks(this);
        Assets.Init();
        Assets.LoadGameAssets();
        Tile.InitTiles();
    }

    /**
     * @brief Metoda principala a firului de executie (Game Loop).
     */
    public void run()
    {
        InitGame();
        long oldTime = System.nanoTime();
        long currentTime;

        final int framesPerSecond = 60;
        final double timeFrame = .0 / framesPerSecond;

        // Bucla principala a jocului
        while (runState)
        {
            currentTime = System.nanoTime();
            if((currentTime - oldTime) > timeFrame)
            {
                Update();
                Draw();
                oldTime = currentTime;
            }
        }
    }

    /**
     * @brief Porneste firul de executie al jocului.
     */
    public synchronized void StartGame()
    {
        if(!runState)
        {
            runState = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    /**
     * @brief Opreste firul de executie al jocului in mod sigur.
     */
    public synchronized void StopGame()
    {
        if(runState)
        {
            runState = false;
            try
            {
                gameThread.join();
            }
            catch(InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @brief Actualizeaza toata logica jocului pentru cadrul curent.
     */
    private void Update()
    {
        keyManager.Update();
        mouseManager.Update();
        if (keyManager.isKeyJustPressed(KeyEvent.VK_F11)) {
            fullScreenMode = !fullScreenMode;
            wnd.setFullScreen(fullScreenMode);
        }

        currentState = State.GetState();
        if (currentState != null) {
            currentState.Update();
        }
    }

    /**
     * @brief Deseneaza starea curenta a jocului pe ecran.
     */
    private void Draw()
    {
        bs = wnd.GetCanvas().getBufferStrategy();
        if(bs == null)
        {
            try
            {
                wnd.GetCanvas().createBufferStrategy(3);
                return;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        g = bs.getDrawGraphics();
        g.clearRect(0, 0, wnd.GetWndWidth(), wnd.GetWndHeight());

        currentState = State.GetState();

        // Desenam starea curenta
        if (currentState != null) {
            currentState.Draw(g);
        }

        bs.show();
        g.dispose();
    }

    /**
     * @brief Metoda privata de initializare a componentelor jocului.
     */
    private void InitGame()
    {
        // Adaugarea listener-ilor pentru input
        wnd.BuildGameWindow();
        wnd.GetCanvas().addKeyListener(keyManager);
        wnd.GetCanvas().addMouseListener(mouseManager);
        wnd.GetCanvas().addMouseMotionListener(mouseManager);
        wnd.GetCanvas().setFocusable(true);
        wnd.GetCanvas().requestFocusInWindow();

        refLink.SetState(new LoadingScreenState(refLink));
    }

    /**
     * @brief Returneaza referinta catre fereastra jocului.
     */
    public GameWindow GetGameWindow() {
        return wnd;
    }

    /**
     * @brief Returneaza referinta catre managerul de input de la tastatura.
     */
    public KeyManager GetKeyManager() {
        return keyManager;
    }

    /**
     * @brief Returneaza referinta catre managerul de input de la mouse.
     */
    public MouseManager GetMouseManager() {
        return mouseManager;
    }

    /**
     * @brief Returneaza referinta catre obiectul Player.
     */
    public Player GetPlayer() {
        return player;
    }

    /**
     * @brief Seteaza referinta catre obiectul Player.
     * @param player Instanta obiectului Player.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * @brief Returneaza referinta catre camera jocului.
     */
    public GameCamera GetGameCamera() {
        return gameCamera;
    }

    /**
     * @brief Returneaza referinta catre obiectul de legaturi (shortcuts).
     */
    public RefLinks GetRefLinks() {
        return refLink;
    }
}