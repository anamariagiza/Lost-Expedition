package PaooGame.Camera;

import PaooGame.Game;
import PaooGame.Entities.Player;
import PaooGame.Tiles.Tile;
import PaooGame.States.GameState;
import PaooGame.Map.Map;
import PaooGame.States.State;

/**
 * @class GameCamera
 * @brief Implementeaza notiunea de camera a jocului.
 * Camera decide ce portiune din harta este vizibila pe ecran la un moment dat.
 * Aceasta urmareste jucatorul si se asigura ca nu se afiseaza spatii goale
 * in afara limitelor hartii.
 */
public class GameCamera {

    /** Referinta finala catre obiectul principal al jocului, injectata la initializare.*/
    private final Game game;
    /** Offset-ul camerei pe axele X si Y. Reprezinta coordonata coltului stanga-sus a camerei in lumea jocului.*/
    private float xOffset, yOffset;

    /**
     * @brief Constructorul clasei GameCamera.
     * @param game Referinta catre obiectul principal al jocului.
     * @param xOffset Pozitia initiala a camerei pe axa X.
     * @param yOffset Pozitia initiala a camerei pe axa Y.
     */
    public GameCamera(Game game, float xOffset, float yOffset) {
        this.game = game;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    /**
     * @brief Centreaza camera pe o entitate data (de obicei, jucatorul).
     * Calculeaza offset-ul necesar pentru ca entitatea sa apara in centrul ferestrei.
     * @param p Entitatea (Player) pe care camera trebuie sa o urmareasca.
     */
    public void centerOnEntity(Player p) {
        xOffset = p.GetX() - (game.GetGameWindow().GetWndWidth() / 2f) + (p.GetWidth() / 2f);
        yOffset = p.GetY() - (game.GetGameWindow().GetWndHeight() / 2f) + (p.GetHeight() / 2f);
        checkBlankSpace();
    }

    /**
     * @brief Verifica si corecteaza pozitia camerei pentru a nu afisa spatiu in afara hartii.
     * Daca offset-ul camerei depaseste limitele hartii (stanga, dreapta, sus sau jos),
     * acesta este ajustat pentru a ramane in interiorul limitelor.
     */
    private void checkBlankSpace() {
        Map currentMap = null;
        State currentState = State.GetState();
        if (currentState instanceof GameState gameState) {
            currentMap = gameState.GetMap();
        }

        if (currentMap == null) {
            return;
        }

        int mapWidthPx = currentMap.GetWidth() * Tile.TILE_WIDTH;
        int mapHeightPx = currentMap.GetHeight() * Tile.TILE_HEIGHT;
        float visibleWidth = game.GetGameWindow().GetWndWidth();
        float visibleHeight = game.GetGameWindow().GetWndHeight();

        if (xOffset < 0) {
            xOffset = 0;
        } else if (xOffset + visibleWidth > mapWidthPx) {
            if (mapWidthPx < visibleWidth) {
                xOffset = (mapWidthPx - visibleWidth) / 2.0f;
            } else {
                xOffset = mapWidthPx - visibleWidth;
            }
        }

        if (yOffset < 0) {
            yOffset = 0;
        } else if (yOffset + visibleHeight > mapHeightPx) {
            if (mapHeightPx < visibleHeight) {
                yOffset = (mapHeightPx - visibleHeight) / 2.0f;
            } else {
                yOffset = mapHeightPx - visibleHeight;
            }
        }
    }

    /**
     * @brief Returneaza offset-ul curent al camerei pe axa X.
     * @return Valoarea offset-ului pe axa X.
     */
    public float getxOffset() {
        return xOffset;
    }

    /**
     * @brief Returneaza offset-ul curent al camerei pe axa Y.
     * @return Valoarea offset-ului pe axa Y.
     */
    public float getyOffset() {
        return yOffset;
    }

}