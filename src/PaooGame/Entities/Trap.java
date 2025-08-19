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
    private static final int DAMAGE_AMOUNT = 5;

    private boolean active = false;
    private boolean isAnimating = false;
    private Animation activeAnimation;
    private BufferedImage stillImage;
    private long activationTime = 0;
    private final long ACTIVE_DURATION_MS = 2000;

    public Trap(RefLinks refLink, float x, float y) {
        super(refLink, x, y, DEFAULT_TRAP_WIDTH, DEFAULT_TRAP_HEIGHT);
        this.bounds = new Rectangle((int) x, (int) y, width, height);
        this.activeAnimation = new Animation(150, Arrays.asList(Assets.trapActiveAnim).toArray(new BufferedImage[0]));
        this.stillImage = Assets.trapDisabled;
    }

    public Trap(RefLinks refLink, float x, float y, BufferedImage image) {
        super(refLink, x, y, DEFAULT_TRAP_WIDTH, DEFAULT_TRAP_HEIGHT);
        this.bounds = new Rectangle((int) x, (int) y, width, height);
        this.activeAnimation = null;
        this.stillImage = image;
    }

    public void setActive(boolean active) {
        if (this.active == active) return;
        this.active = active;
        if (active) {
            isAnimating = true;
            if(activeAnimation != null) {
                this.activeAnimation.reset();
            }
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
        if (isAnimating && activeAnimation != null) {
            this.activeAnimation.Update();
            if (this.activeAnimation.isFinished()) {
                isAnimating = false;
            }
        }

        if (active && System.currentTimeMillis() - activationTime >= ACTIVE_DURATION_MS) {
            setActive(false);
            System.out.println("DEBUG Trap: Capcana s-a dezactivat.");
        }
    }

    @Override
    public void Draw(Graphics g) {
        BufferedImage imageToDraw;
        if (active || isAnimating) {
            imageToDraw = (activeAnimation != null) ? activeAnimation.getCurrentFrame() : stillImage;
        } else {
            imageToDraw = stillImage;
        }

        if (imageToDraw != null) {
            int drawX = (int) ((x - refLink.GetGameCamera().getxOffset()));
            int drawY = (int) ((y - refLink.GetGameCamera().getyOffset()));
            int scaledWidth = (int) (width);
            int scaledHeight = (int) (height);
            g.drawImage(imageToDraw, drawX, drawY, scaledWidth, scaledHeight, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect((int) x, (int) y, width, height);
        }
    }
}