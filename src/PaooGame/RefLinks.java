package PaooGame;
import PaooGame.Input.KeyManager;
import PaooGame.Input.MouseManager;
import PaooGame.Map.Map;
import PaooGame.States.State;
import PaooGame.Entities.Player;
import PaooGame.Camera.GameCamera;
import PaooGame.Utils.DatabaseManager;
import PaooGame.States.GameState;

/**
 * @class RefLinks
 * @brief O clasa ce centralizeaza referintele catre obiectele principale ale jocului.
 * Acest obiect actioneaza ca un "hub" sau "shortcut", oferind o cale simpla
 * de a accesa componente cheie ale jocului.
 */
public class RefLinks
{
    /** Referinta catre obiectul principal Game.*/
    private final Game game;
    private Map map;
    private Player player;
    private final GameCamera gameCamera;
    private final DatabaseManager dbManager;
    private final MouseManager mouseManager;
    private GameState persistedGameState = null;

    /**
     * @brief Constructorul clasei RefLinks.
     * @param game Referinta catre obiectul principal Game.
     */
    public RefLinks(Game game)
    {
        this.game = game;
        this.gameCamera = game.GetGameCamera();
        this.dbManager = new DatabaseManager();
        this.mouseManager = game.GetMouseManager();
    }

    /**
     * @brief Returneaza referinta catre managerul de input de la tastatura.
     */
    public KeyManager GetKeyManager()
    {
        return game.GetKeyManager();
    }

    /**
     * @brief Seteaza referinta catre harta curenta.
     * @param map Referinta catre harta curenta.
     */
    public void SetMap(Map map)
    {
        this.map = map;
    }

    /**
     * @brief Seteaza referinta catre obiectul jucatorului.
     * @param player Referinta catre jucator.
     */
    public void SetPlayer(Player player) {
        this.player = player;
    }

    /**
     * @brief Seteaza starea curenta a jocului.
     * @param state Noua stare a jocului.
     */
    public void SetState(State state) {
        State.SetState(state);
    }

    /**
     * @brief Salveaza instanta GameState cand jocul intra in pauza.
     * @param state Instanta GameState de salvat.
     */
    public void setPersistedGameState(GameState state) {
        this.persistedGameState = state;
    }

    /**
     * @brief Seteaza referinta catre starea nivelului 1.
     * @param level1State Starea de salvat.
     */
    public void SetLevel1State(GameState level1State) {
        this.level1State = level1State;
    }

    /**
     * @brief Returneaza referinta catre managerul de input de la mouse.
     */
    public MouseManager GetMouseManager() {
        return mouseManager;
    }

    /**
     * @brief Returneaza latimea ferestrei jocului.
     */
    public int GetWidth()
    {
        return game.GetGameWindow().GetWndWidth();
    }

    /**
     * @brief Returneaza inaltimea ferestrei jocului.
     */
    public int GetHeight()
    {
        return game.GetGameWindow().GetWndHeight();
    }

    /**
     * @brief Returneaza referinta catre obiectul principal al jocului.
     */
    public Game GetGame()
    {
        return game;
    }

    /**
     * @brief Returneaza referinta catre harta curenta.
     */
    public Map GetMap()
    {
        return map;
    }

    /**
     * @brief Returneaza referinta catre obiectul jucatorului.
     */
    public Player GetPlayer() {
        return player;
    }

    /**
     * @brief Returneaza referinta catre camera jocului.
     */
    public GameCamera GetGameCamera() {
        return gameCamera;
    }

    /**
     * @brief Returneaza starea anterioara a jocului.
     */
    public State GetPreviousState() {
        return State.GetPreviousStateStatic();
    }

    /**
     * @brief Returneaza referinta catre managerul bazei de date.
     */
    public DatabaseManager GetDatabaseManager() {
        return dbManager;
    }

    /**
     * @brief Returneaza instanta GameState care a fost pusa pe pauza.
     */
    public GameState getPersistedGameState() {
        return persistedGameState;
    }

    /** Referinta catre starea nivelului 1 (utilizat pentru resetare).*/
    private GameState level1State;

    /**
     * @brief Returneaza starea salvata a nivelului 1.
     */
    public GameState GetLevel1State() {
        return level1State;
    }

    /**
     * @brief Returneaza starea curenta a jocului, doar daca este de tip GameState.
     * @return Instanta GameState curenta, sau null daca jocul nu este in starea de joc.
     */
    public GameState GetGameState() {
        State currentState = State.GetState();
        if (currentState instanceof GameState) {
            return (GameState) currentState;
        }
        return null;
    }
}