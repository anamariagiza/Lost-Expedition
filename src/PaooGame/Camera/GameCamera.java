package PaooGame.Camera;

import PaooGame.Game;
import PaooGame.Entities.Player;
import PaooGame.Tiles.Tile;
import PaooGame.States.GameState;
import PaooGame.Map.Map;
import PaooGame.States.State;

/*!
 * \class public class GameCamera
 * \brief Implementeaza notiunea de camera a jocului.
 * Camera decide ce portiune din harta este vizibila pe ecran.
 */
public class GameCamera {

    private Game game;
    private float xOffset, yOffset;

    public GameCamera(Game game, float xOffset, float yOffset) {
        this.game = game;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public void centerOnEntity(Player p) {
        xOffset = p.GetX() - (game.GetGameWindow().GetWndWidth() / 2f) + (p.GetWidth() / 2f);
        yOffset = p.GetY() - (game.GetGameWindow().GetWndHeight() / 2f) + (p.GetHeight() / 2f);
        checkBlankSpace();
    }

    public void move(float xAmt, float yAmt) {
        xOffset += xAmt;
        yOffset += yAmt;
        checkBlankSpace();
    }

    private void checkBlankSpace() {
        Map currentMap = null;
        State currentState = State.GetState();
        if (currentState instanceof GameState) {
            GameState gameState = (GameState) currentState;
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

    public float getxOffset() {
        return xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
        checkBlankSpace();
    }

    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
        checkBlankSpace();
    }
}