package PaooGame;

import PaooGame.Input.KeyManager;
import PaooGame.Map.Map; // ATENTIE: PaooGame.Map.Map, nu Maps.Map
import PaooGame.GameWindow.GameWindow;
import PaooGame.States.State;
import PaooGame.Entities.Player; // ATENTIE: PaooGame.Entities.Player, nu Items.Hero
import PaooGame.Camera.GameCamera; // NOU: Import GameCamera

/*! \class public class RefLinks
    \brief Clasa ce retine o serie de referinte ale unor elemente pentru a fi usor accesibile.

    Altfel ar trebui ca functiile respective sa aiba o serie intreaga de parametri si ar ingreuna programarea.
 */
public class RefLinks
{
    private Game game;
    private Map map;
    private Player player; // ATENTIE: referinta la Player
    private GameCamera gameCamera; // NOU: referinta la GameCamera

    /*! \fn public RefLinks(Game game)
        \brief Constructorul de initializare al clasei.

        \param game Referinta catre obiectul game.
     */
    public RefLinks(Game game)
    {
        this.game = game;
        this.gameCamera = game.GetGameCamera(); // Preia GameCamera de la Game
    }

    /*! \fn public KeyManager GetKeyManager()
        \brief Returneaza referinta catre managerul evenimentelor de tastatura.
     */
    public KeyManager GetKeyManager()
    {
        return game.GetKeyManager();
    }

    /*! \fn public int GetWidth()
        \brief Returneaza latimea ferestrei jocului.
     */
    public int GetWidth()
    {
        return game.GetGameWindow().GetWndWidth();
    }

    /*! \fn public int GetHeight()
        \brief Returneaza inaltimea ferestrei jocului.
     */
    public int GetHeight()
    {
        return game.GetGameWindow().GetWndHeight();
    }

    /*! \fn public Game GetGame()
        \brief Intoarce referinta catre obiectul Game.
     */
    public Game GetGame()
    {
        return game;
    }

    /*! \fn public void SetGame(Game game)
        \brief Seteaza referinta catre un obiect Game.

        \param game Referinta obiectului Game.
     */
    public void SetGame(Game game)
    {
        this.game = game;
    }

    /*! \fn public Map GetMap()
        \brief Intoarce referinta catre harta curenta.
     */
    public Map GetMap()
    {
        return map;
    }

    /*! \fn public void SetMap(Map map)
        \brief Seteaza referinta catre harta curenta.

        \param map Referinta catre harta curenta.
     */
    public void SetMap(Map map)
    {
        this.map = map;
    }

    /*! \fn public Player GetPlayer()
        \brief Intoarce referinta catre obiectul Player.
     */
    public Player GetPlayer() {
        return player;
    }

    /*! \fn public void SetPlayer(Player player)
        \brief Seteaza referinta catre obiectul Player.
     */
    public void SetPlayer(Player player) {
        this.player = player;
    }

    /*! \fn public GameCamera GetGameCamera()
        \brief Intoarce referinta catre obiectul GameCamera.
     */
    public GameCamera GetGameCamera() {
        return gameCamera;
    }

    /*! \fn public void SetState(State state)
        \brief Seteaza starea curenta a jocului folosind metoda statica din State.
        \param state Noua stare a programului (jocului).
     */
    public void SetState(State state) {
        State.SetState(state);
    }
}