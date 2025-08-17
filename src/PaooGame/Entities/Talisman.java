package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.States.State;
import PaooGame.States.GameState;
import PaooGame.Tiles.Tile;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Talisman extends Entity {

    private BufferedImage image;
    private boolean isCollected = false;

    public Talisman(RefLinks refLink, float x, float y, BufferedImage image) {
        super(refLink, x, y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
        this.image = image;
        this.bounds = new Rectangle((int)x, (int)y, width, height);
    }

    public boolean isCollected() {
        return isCollected;
    }

    @Override
    public void Update() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds())) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                this.isCollected = true;
                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    GameState gameState = (GameState) currentState;
                    gameState.talismanCollected();
                }
                System.out.println("Talismanul Lunii a fost colectat!");
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        if (isCollected) return; // Talismanul nu se mai deseneaza dupa colectare

        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()));
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()));
        int scaledWidth = (int)(width);
        int scaledHeight = (int)(height);
        if (image != null) {
            g.drawImage(image, drawX, drawY, scaledWidth, scaledHeight, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(drawX, drawY, scaledWidth, scaledHeight);
        }
        drawInteractionPopup(g);
    }
}