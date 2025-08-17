package PaooGame.Entities;

import PaooGame.Graphics.Assets;
import PaooGame.RefLinks;
import PaooGame.States.GameState;
import PaooGame.States.State;
import PaooGame.Tiles.Tile;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Chest extends Entity {
    private boolean opened = false;
    private boolean canInteract = false;

    public Chest(RefLinks refLink, float x, float y, int width, int height) {
        super(refLink, x, y, width, height);
        SetPosition(x, y);
    }

    public void setCanInteract(boolean canInteract) {
        this.canInteract = canInteract;
    }

    public boolean isCanInteract() {
        return canInteract;
    }

    @Override
    public void Update() {
        if (refLink.GetPlayer() == null) return;
        if (this.bounds.intersects(refLink.GetPlayer().GetBounds()) && canInteract) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E) && !opened) {
                opened = true;
                System.out.println("DEBUG Chest: Cufarul a fost deschis! Jocul se termina.");

                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    ((GameState) currentState).endGame();
                }
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        BufferedImage image = opened ? Assets.chestOpened : Assets.chestClosed;
        if (image != null) {
            int drawX = (int)((x - refLink.GetGameCamera().getxOffset()));
            int drawY = (int)((y - refLink.GetGameCamera().getyOffset()));
            int scaledWidth = (int)(width);
            int scaledHeight = (int)(height);
            g.drawImage(image, drawX, drawY, scaledWidth, scaledHeight, null);
        }
        drawInteractionPopup(g);
    }
}