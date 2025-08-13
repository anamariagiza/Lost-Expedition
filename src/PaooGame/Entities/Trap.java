package PaooGame.Entities;

import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.RefLinks;
import PaooGame.Tiles.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Trap extends Entity {

    private static final int DEFAULT_TRAP_WIDTH = 48;
    private static final int DEFAULT_TRAP_HEIGHT = 48;
    private static final int DAMAGE_AMOUNT = 40;

    private boolean active = false;
    private boolean isAnimating = false;
    private Animation activeAnimation;
    private long activationTime = 0;
    private final long ACTIVE_DURATION_MS = 2000;

    public Trap(RefLinks refLink, float x, float y) {
        super(refLink, x, y, DEFAULT_TRAP_WIDTH, DEFAULT_TRAP_HEIGHT);
        SetPosition(x, y);
        this.activeAnimation = new Animation(150, Arrays.asList(Assets.trapActiveAnim[0], Assets.trapActiveAnim[1], Assets.trapActiveAnim[2]).toArray(new BufferedImage[0]));
    }

    public void setActive(boolean active) {
        if (this.active == active) return;
        this.active = active;
        if (active) {
            isAnimating = true;
            this.activeAnimation.reset();
            activationTime = System.currentTimeMillis();
        } else {
            isAnimating = false;
        }
    }

    public boolean isActive() {
        return active;
    }

    public int getDamage() {
        return DAMAGE_AMOUNT;
    }

    @Override
    public void Update() {
        if (isAnimating) {
            this.activeAnimation.Update();
            if (this.activeAnimation.isFinished()) {
                isAnimating = false;
            }
        }

        // Timer pentru închiderea capcanei după 2 secunde
        if (active && System.currentTimeMillis() - activationTime >= ACTIVE_DURATION_MS) {
            setActive(false);
            System.out.println("DEBUG Trap: Capcana s-a dezactivat.");
        }
    }

    @Override
    public void Draw(Graphics g) {
        BufferedImage imageToDraw;
        if (active || isAnimating) {
            imageToDraw = activeAnimation.getCurrentFrame();
        } else {
            imageToDraw = Assets.trapDisabled;
        }

        if (imageToDraw != null) {
            int drawX = (int) ((x - refLink.GetGameCamera().getxOffset()) * refLink.GetGameCamera().getZoomLevel());
            int drawY = (int) ((y - refLink.GetGameCamera().getyOffset()) * refLink.GetGameCamera().getZoomLevel());
            int scaledWidth = (int) (width * refLink.GetGameCamera().getZoomLevel());
            int scaledHeight = (int) (height * refLink.GetGameCamera().getZoomLevel());
            g.drawImage(imageToDraw, drawX, drawY, scaledWidth, scaledHeight, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect((int) x, (int) y, width, height);
        }
    }
}