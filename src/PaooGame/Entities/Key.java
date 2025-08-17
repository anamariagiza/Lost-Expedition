package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Tiles.Tile;
import PaooGame.States.State;
import PaooGame.States.GameState;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Key extends Entity {

    public enum KeyType {
        NEXT_LEVEL_KEY,
        DOOR_KEY
    }

    private static final int DEFAULT_KEY_WIDTH = 32;
    private static final int DEFAULT_KEY_HEIGHT = 32;
    private BufferedImage keyImage;

    private boolean collected = false;
    private KeyType type;
    private int associatedPuzzleId = -1;

    public Key(RefLinks refLink, float x, float y, BufferedImage image, KeyType type) {
        super(refLink, x, y, DEFAULT_KEY_WIDTH, DEFAULT_KEY_HEIGHT);
        SetPosition(x, y);

        this.keyImage = image;
        this.type = type;
    }

    public Key(RefLinks refLink, float x, float y, BufferedImage image, int puzzleId) {
        this(refLink, x, y, image, KeyType.DOOR_KEY);
        this.associatedPuzzleId = puzzleId;
    }

    @Override
    public void Update() {
        if (collected) {
            return;
        }
        checkPlayerInteraction();
    }

    private void checkPlayerInteraction() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds())) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                System.out.println("DEBUG Key: Cheia de la puzzle-ul " + associatedPuzzleId + " a fost colectata!");
                collected = true;

                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    GameState gameState = (GameState) currentState;
                    switch (type) {
                        case NEXT_LEVEL_KEY:
                            gameState.keyCollected();
                            break;
                        case DOOR_KEY:
                            gameState.doorKeyCollected(associatedPuzzleId);
                            break;
                    }
                } else {
                    System.err.println("DEBUG Key: Colectare cheie in afara GameState-ului!");
                }
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        if (collected) {
            return;
        }
        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()));
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()));
        int scaledWidth = (int)(width);
        int scaledHeight = (int)(height);
        if (keyImage != null) {
            g.drawImage(keyImage, drawX, drawY, scaledWidth, scaledHeight, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(drawX, drawY, scaledWidth, scaledHeight);
        }
        drawInteractionPopup(g);
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public int getAssociatedPuzzleId() {
        return associatedPuzzleId;
    }

    public KeyType getType() {
        return type;
    }
}