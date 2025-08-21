package PaooGame;

import PaooGame.GameWindow.GameWindow;
import PaooGame.Graphics.Assets;
import PaooGame.States.MenuState;
import PaooGame.Tiles.Tile;
import PaooGame.States.State;
import PaooGame.States.LoadingScreenState;
import PaooGame.Input.KeyManager;
import PaooGame.Input.MouseManager;
import PaooGame.Entities.Player;
import PaooGame.Camera.GameCamera;
import PaooGame.States.GameState;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

/*!
 * \class Game
 * \brief Clasa principala a intregului proiect.
 * Implementeaza Game - Loop (Update -> Draw)
 * Gestioneaza starea curenta a jocului (meniu, joc, pauza etc.)
 * si input-ul utilizatorului.
 */
public class Game implements Runnable
{
    private GameWindow wnd;
    private boolean runState;
    private Thread gameThread;
    private BufferStrategy bs;
    private Graphics g;

    private State currentState;

    private KeyManager keyManager;
    private MouseManager mouseManager;
    private RefLinks refLink;

    private boolean fullScreenMode = false;
    private Player player;
    private GameCamera gameCamera;

    private int startLevel = 0;

    /*!
     * \fn public Game(String title, int width, int height)
     * \brief Constructor de initializare al clasei Game.
     * \param title Titlul ferestrei.
     * \param width Latimea ferestrei in pixeli.
     * \param height Inaltimea ferestrei in pixeli.
     */
    public Game(String title, int width, int height)
    {
        wnd = new GameWindow(title, width, height);
        runState = false;
        keyManager = new KeyManager();
        mouseManager = new MouseManager();
        gameCamera = new GameCamera(this, 0, 0);
        refLink = new RefLinks(this);
        Assets.Init(); // Apelăm Assets.Init() aici
        Assets.LoadGameAssets(); // Apelăm Assets.LoadGameAssets() aici
        Tile.InitTiles(); // Apelăm Tile.InitTiles() aici
    }

    public Game(String title, int width, int height, int startLevel)
    {
        wnd = new GameWindow(title, width, height);
        runState = false;
        keyManager = new KeyManager();
        mouseManager = new MouseManager();
        gameCamera = new GameCamera(this, 0, 0);
        refLink = new RefLinks(this);
        this.startLevel = startLevel;
        Assets.Init(); // Apelăm Assets.Init() aici
        Assets.LoadGameAssets(); // Apelăm Assets.LoadGameAssets() aici
        Tile.InitTiles(); // Apelăm Tile.InitTiles() aici
    }

    /*!
     * \fn private void InitGame()
     * \brief  Metoda construieste fereastra jocului, initializeaza aseturile, si starea initiala.
     */
    private void InitGame()
    {
        wnd.BuildGameWindow();
        wnd.GetCanvas().addKeyListener(keyManager);
        wnd.GetCanvas().addMouseListener(mouseManager);
        wnd.GetCanvas().addMouseMotionListener(mouseManager);
        wnd.GetCanvas().setFocusable(true);
        wnd.GetCanvas().requestFocusInWindow();

        refLink.SetState(new LoadingScreenState(refLink));
    }

    /*!
     * \fn public void run()
     * \brief Functia ce va rula in thread-ul creat (Game Loop).
     */
    public void run()
    {
        InitGame();
        long oldTime = System.nanoTime();
        long currentTime;

        final int framesPerSecond = 60;
        final double timeFrame = 1000000000 / framesPerSecond;
        while (runState == true)
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

    /*!
     * \fn public synchronized void StartGame()
     * \brief Creaza si starteaza firul separat de executie (thread).
     */
    public synchronized void StartGame()
    {
        if(runState == false)
        {
            runState = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
        else
        {
            return;
        }
    }

    /*!
     * \fn public synchronized void stop()
     * \brief Opreste executie thread-ului.
     */
    public synchronized void StopGame()
    {
        if(runState == true)
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
        else
        {
            return;
        }
    }

    /*!
     * \fn private void Update()
     * \brief Actualizeaza starea elementelor din joc, delegand catre starea curenta.
     * De asemenea, gestioneaza input-ul global, cum ar fi comutarea fullscreen.
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

    /*!
     * \fn private void Draw()
     * \brief Deseneaza elementele grafice in fereastra, delegand catre starea curenta.
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
        if (currentState != null) {
            currentState.Draw(g);
        }

        bs.show();
        g.dispose();
    }

    /*!
     * \fn public GameWindow GetGameWindow()
     * \brief Returneaza referinta catre fereastra jocului.
     */
    public GameWindow GetGameWindow() {
        return wnd;
    }

    /*!
     * \fn public KeyManager GetKeyManager()
     * \brief Returneaza referinta catre obiectul de gestionare a input-ului tastaturii.
     */
    public KeyManager GetKeyManager() {
        return keyManager;
    }

    /*!
     * \fn public MouseManager GetMouseManager()
     * \brief Returneaza referinta catre obiectul de gestionare a input-ului mouse-ului.
     */
    public MouseManager GetMouseManager() {
        return mouseManager;
    }


    /*!
     * \fn public Player GetPlayer()
     * \brief Returneaza referinta catre obiectul Player.
     */
    public Player GetPlayer() {
        return player;
    }

    /*!
     * \fn public void setPlayer(Player player)
     * \brief Seteaza referinta catre obiectul Player.
     * Folosit de LoadingScreenState pentru a seta playerul in Game.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /*!
     * \fn public GameCamera GetGameCamera()
     * \brief Returneaza referinta catre obiectul GameCamera.
     */
    public GameCamera GetGameCamera() {
        return gameCamera;
    }

    /*!
     * \fn public RefLinks GetRefLinks()
     * \brief Returneaza referinta catre obiectul RefLinks.
     */
    public RefLinks GetRefLinks() {
        return refLink;
    }
}