package PaooGame.Graphics;

import java.awt.image.BufferedImage;

public class Animation {

    private int speed;
    private int index;
    private long lastTime;
    private long timer;
    public BufferedImage[] frames;
    private boolean loops;

    public Animation(int speed, BufferedImage[] frames) {
        this(speed, frames, true);
    }

    public Animation(int speed, BufferedImage[] frames, boolean loops) {
        this.speed = speed;
        this.frames = frames;
        this.loops = loops;
        this.index = 0;
        this.timer = 0;
        this.lastTime = System.currentTimeMillis();
    }

    public void Update() {
        if (frames == null || frames.length == 0 || isFinished()) {
            return;
        }

        timer += System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();

        if (timer > speed) {
            index++;
            timer = 0;
            if (index >= frames.length) {
                if (loops) {
                    index = 0;
                } else {
                    index = frames.length - 1;
                }
            }
        }
    }

    public boolean isFinished() {
        return !loops && index >= frames.length - 1;
    }

    public BufferedImage getCurrentFrame() {
        if (frames == null || frames.length == 0) {
            return null;
        }
        return frames[index];
    }

    public void reset() {
        index = 0;
        timer = 0;
        lastTime = System.currentTimeMillis();
    }

    public int getIndex() {
        return index;
    }

    public int getFramesLength() {
        return (frames != null) ? frames.length : 0;
    }
}