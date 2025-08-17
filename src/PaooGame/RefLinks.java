package PaooGame;
import PaooGame.Input.KeyManager;
import PaooGame.Input.MouseManager;
import PaooGame.Map.Map;
import PaooGame.GameWindow.GameWindow;
import PaooGame.States.State;
import PaooGame.Entities.Player;
import PaooGame.Camera.GameCamera;
import PaooGame.Utils.DatabaseManager;
import PaooGame.States.GameState;

/*!
 * \class public class RefLinks
 * \brief Clasa ce retine o serie de referinte ale unor elemente pentru a fi usor accesibile.
 * Altfel ar trebui ca functiile respective sa aiba o serie intreaga de parametri si ar ingreuna programarea.
 */
public class RefLinks
{
    private Game game;
    private Map map;
    private Player player;
    private GameCamera gameCamera;
    private DatabaseManager dbManager;
    private MouseManager mouseManager;
    private GameState persistedGameState = null;

    /*!
     * \fn public RefLinks(Game game)
     * \brief Constructorul de initializare al clasei.
     * \param game Referinta catre obiectul game.
     */
    public RefLinks(Game game)
    {
        this.game = game;
        this.gameCamera = game.GetGameCamera();
        this.dbManager = new DatabaseManager();
        this.mouseManager = game.GetMouseManager();
    }

    /*!
     * \fn public KeyManager GetKeyManager()
     * \brief Returneaza referinta catre managerul evenimentelor de tastatura.
     */
    public KeyManager GetKeyManager()
    {
        return game.GetKeyManager();
    }

    /*!
     * \fn public MouseManager GetMouseManager()
     * \brief Returneaza referinta catre managerul evenimentelor de mouse.
     */
    public MouseManager GetMouseManager() {
        return mouseManager;
    }

    /*!
     * \fn public int GetWidth()
     * \brief Returneaza latimea ferestrei jocului.
     */
    public int GetWidth()
    {
        return game.GetGameWindow().GetWndWidth();
    }

    /*!
     * \fn public int GetHeight()
     * \brief Returneaza inaltimea ferestrei jocului.
     */
    public int GetHeight()
    {
        return game.GetGameWindow().GetWndHeight();
    }

    /*!
     * \fn public Game GetGame()
     * \brief Intoarce referinta catre obiectul Game.
     */
    public Game GetGame()
    {
        return game;
    }

    /*!
     * \fn public void SetGame(Game game)
     * \brief Seteaza referinta catre un obiect Game.
     * \param game Referinta obiectului Game.
     */
    public void SetGame(Game game)
    {
        this.game = game;
    }

    /*!
     * \fn public Map GetMap()
     * \brief Intoarce referinta catre harta curenta.
     */
    public Map GetMap()
    {
        return map;
    }

    /*!
     * \fn public void SetMap(Map map)
     * \brief Seteaza referinta catre harta curenta.
     * \param map Referinta catre harta curenta.
     */
    public void SetMap(Map map)
    {
        this.map = map;
    }

    /*!
     * \fn public Player GetPlayer()
     * \brief Intoarce referinta catre obiectul Player.
     */
    public Player GetPlayer() {
        return player;
    }

    /*!
     * \fn public void SetPlayer(Player player)
     * \brief Seteaza referinta catre obiectul Player.
     */
    public void SetPlayer(Player player) {
        this.player = player;
    }

    /*!
     * \fn public GameCamera GetGameCamera()
     * \brief Intoarce referinta catre obiectul GameCamera.
     */
    public GameCamera GetGameCamera() {
        return gameCamera;
    }

    /*!
     * \fn public void SetState(State state)
     * \brief Seteaza starea curenta a jocului folosind metoda statica din State.
     * \param state Noua stare a programului (jocului).
     */
    public void SetState(State state) {
        State.SetState(state);
    }

    /*!
     * \fn public State GetPreviousState()
     * \brief Returneaza starea anterioara salvata.
     */
    public State GetPreviousState() {
        return State.GetPreviousStateStatic();
    }

    /*!
     * \fn public DatabaseManager GetDatabaseManager()
     * \brief Intoarce referinta catre obiectul DatabaseManager.
     */
    public DatabaseManager GetDatabaseManager() {
        return dbManager;
    }

    public GameState getPersistedGameState() {
        return persistedGameState;
    }

    public void setPersistedGameState(GameState state) {
        this.persistedGameState = state;
    }

    private GameState level1State;

    public void SetLevel1State(GameState level1State) {
        this.level1State = level1State;
    }

    public GameState GetLevel1State() {
        return level1State;
    }
}