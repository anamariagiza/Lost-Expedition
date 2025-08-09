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
    private float zoomLevel;

    public GameCamera(Game game, float xOffset, float yOffset) {
        this.game = game;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zoomLevel = 1.0f;
    }

    public GameCamera(Game game, float xOffset, float yOffset, float zoomLevel) {
        this.game = game;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zoomLevel = zoomLevel;
    }

    public void centerOnEntity(Player p) {
        xOffset = (p.GetX() - (game.GetGameWindow().GetWndWidth() / 2f) / zoomLevel) + (p.GetWidth() / 2f);
        yOffset = (p.GetY() - (game.GetGameWindow().GetWndHeight() / 2f) / zoomLevel) + (p.GetHeight() / 2f);

        checkBlankSpace();
    }

    public void move(float xAmt, float yAmt) {
        xOffset += xAmt / zoomLevel;
        yOffset += yAmt / zoomLevel;
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
        float visibleWidth = game.GetGameWindow().GetWndWidth() / zoomLevel;
        float visibleHeight = game.GetGameWindow().GetWndHeight() / zoomLevel;
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

    public float getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(float zoomLevel) {
        if (zoomLevel <= 0.1f) this.zoomLevel = 0.1f;
        else if (zoomLevel >= 4.0f) this.zoomLevel = 4.0f;
        else this.zoomLevel = zoomLevel;
        if (game.GetRefLinks().GetPlayer() != null) {
            centerOnEntity(game.GetRefLinks().GetPlayer());
        } else {
            checkBlankSpace();
        }
    }

    public float getXOffset() {  // Changed from getxOffset()
        return xOffset;
    }

    public float getYOffset() {  // Changed from getyOffset()
        return yOffset;
    }

    public void setXOffset(float xOffset) {  // Changed from setxOffset()
        this.xOffset = xOffset;
        checkBlankSpace();
    }

    public void setYOffset(float yOffset) {  // Changed from setyOffset()
        this.yOffset = yOffset;
        checkBlankSpace();
    }
}