package PaooGame.Entities;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.States.GameState;
import PaooGame.States.State;
import PaooGame.Tiles.Tile;
import PaooGame.Camera.GameCamera;
import PaooGame.Map.Map;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/*!
 * \class public class Player extends Entity
 * \brief Implementeaza notiunea de erou/jucator (player) in joc.
 */
public class Player extends Entity {

    private Game game;
    private float walkSpeed = 3.0f;
    private float runSpeed = 6.0f;
    private float currentSpeed;

    private int health;
    private int maxHealth = 100;
    private Animation animDown, animUp, animLeft, animRight;
    private Animation animIdleDown, animIdleUp, animIdleLeft, animIdleRight;
    private Animation animRunDown, animRunUp, animRunLeft, animRunRight;
    private Animation animJumpDown, animJumpUp, animJumpLeft, animJumpRight;
    private Animation animHurt;
    private Animation animCombatIdle;
    private Animation animThrust;
    private Animation animHalfslash;
    private Animation animSlash;
    public Animation activeAnimation;

    private boolean isMoving;
    private boolean isRunning;
    private boolean isJumping;
    private boolean isAttacking;
    private boolean isHurt;
    private boolean isCombatIdle;
    private boolean isThrusting;
    private boolean isHalfslashing;
    private boolean isSlashing;
    private enum Direction { UP, DOWN, LEFT, RIGHT }
    private Direction lastDirection = Direction.DOWN;

    private int attackDamage = 25;

    public Player(Game game, float x, float y) {
        super(game.GetRefLinks(), x, y, Assets.PLAYER_FRAME_WIDTH, Assets.PLAYER_FRAME_HEIGHT);
        this.game = game;

        currentSpeed = walkSpeed;
        health = maxHealth;

        int animationSpeed = 100;
        int runAnimationSpeed = 70;
        int jumpAnimationSpeed = 120;
        int attackAnimationSpeed = 80;
        int hurtAnimationSpeed = 150;
        int idleCombatSpeed = 200;

        animDown = safeAnimation(Assets.playerDown, animationSpeed);
        animUp = safeAnimation(Assets.playerUp, animationSpeed);
        animLeft = safeAnimation(Assets.playerLeft, animationSpeed);
        animRight = safeAnimation(Assets.playerRight, animationSpeed);
        animIdleDown = safeAnimation(Assets.playerIdleDown, animationSpeed * 2);
        animIdleUp = safeAnimation(Assets.playerIdleUp, animationSpeed * 2);
        animIdleLeft = safeAnimation(Assets.playerIdleLeft, animationSpeed * 2);
        animIdleRight = safeAnimation(Assets.playerIdleRight, animationSpeed * 2);
        animRunDown = safeAnimation(Assets.playerRunDown, runAnimationSpeed);
        animRunUp = safeAnimation(Assets.playerRunUp, runAnimationSpeed);
        animRunLeft = safeAnimation(Assets.playerRunLeft, runAnimationSpeed);
        animRunRight = safeAnimation(Assets.playerRunRight, runAnimationSpeed);
        animJumpDown = safeAnimation(Assets.playerJumpDown, jumpAnimationSpeed);
        animJumpUp = safeAnimation(Assets.playerJumpUp, jumpAnimationSpeed);
        animJumpLeft = safeAnimation(Assets.playerJumpLeft, jumpAnimationSpeed);
        animJumpRight = safeAnimation(Assets.playerJumpRight, jumpAnimationSpeed);
        animHurt = safeAnimation(Assets.playerHurt, hurtAnimationSpeed);
        animHalfslash = safeAnimation(Assets.playerHalfslash, attackAnimationSpeed);
        animCombatIdle = safeAnimation(Assets.playerCombatIdle, idleCombatSpeed);
        animThrust = safeAnimation(Assets.playerThrust, attackAnimationSpeed);
        animSlash = safeAnimation(Assets.playerSlash, attackAnimationSpeed);

        activeAnimation = animIdleDown;
        lastDirection = Direction.DOWN;
        isMoving = false; isRunning = false; isJumping = false; isAttacking = false;
        isHurt = false; isCombatIdle = false;
        isThrusting = false;
        isHalfslashing = false; isSlashing = false;

        this.bounds = new Rectangle((int)x, (int)y, width, height);
    }

    @Override
    public void Update() {
        if (game == null || game.GetKeyManager() == null) return;

        if (isHurt) {
            activeAnimation.Update();
            return;
        }

        GetInput();

        game.GetRefLinks().GetGameCamera().centerOnEntity(this);
        if (!isAttacking && !isHurt && !isJumping && !isCombatIdle &&
                !isThrusting && !isHalfslashing && !isSlashing) {
            updateMovementAnimation();
        } else {
            activeAnimation.Update();
            if (activeAnimation.isFinished()) {
                isAttacking = false;
                isHurt = false; isJumping = false;
                isCombatIdle = false; isThrusting = false;
                isHalfslashing = false; isSlashing = false;
                updateIdleAnimationBasedOnLastDirection();
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        GameCamera camera = game.GetRefLinks().GetGameCamera();
        BufferedImage playerFrame = GetActiveFrame();
        if (playerFrame != null) {
            float zoom = camera.getZoomLevel();
            int drawX = (int)((x - camera.getxOffset()) * zoom);
            int drawY = (int)((y - camera.getyOffset()) * zoom);
            int scaledWidth = (int)(width * zoom);
            int scaledHeight = (int)(height * zoom);
            g.drawImage(playerFrame, drawX, drawY, scaledWidth, scaledHeight, null);
        }
    }

    private void GetInput() {
        isMoving = false;
        float xMove = 0;
        float yMove = 0;

        isRunning = game.GetKeyManager().shift;
        currentSpeed = isRunning ? runSpeed : walkSpeed;
        if (game.GetKeyManager().up) {
            yMove = -currentSpeed; isMoving = true;
            lastDirection = Direction.UP;
        }
        if (game.GetKeyManager().down) {
            yMove = currentSpeed;
            isMoving = true; lastDirection = Direction.DOWN;
        }
        if (game.GetKeyManager().left) {
            xMove = -currentSpeed;
            isMoving = true; lastDirection = Direction.LEFT;
        }
        if (game.GetKeyManager().right) {
            xMove = currentSpeed;
            isMoving = true; lastDirection = Direction.RIGHT;
        }

        if (!isJumping && !isAttacking && !isHurt && !isCombatIdle &&
                !isThrusting && !isHalfslashing && !isSlashing) {
            if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_SPACE)) {
                isJumping = true;
                switch(lastDirection) {
                    case UP: activeAnimation = animJumpUp;
                        break;
                    case DOWN: activeAnimation = animJumpDown; break;
                    case LEFT: activeAnimation = animJumpLeft; break;
                    case RIGHT: activeAnimation = animJumpRight; break;
                }
                activeAnimation.reset();
            } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                isAttacking = true;
                isThrusting = true; activeAnimation = animThrust; activeAnimation.reset();
            } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_K)) {
                isAttacking = true;
                isHalfslashing = true; activeAnimation = animHalfslash; activeAnimation.reset();
            } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_SLASH)) {
                isAttacking = true;
                isSlashing = true; activeAnimation = animSlash; activeAnimation.reset();
            }
        }
        move(xMove, yMove);
    }

    private void updateMovementAnimation() {
        if (isMoving) {
            if (isRunning) {
                switch(lastDirection) {
                    case UP: activeAnimation = animRunUp;
                        break;
                    case DOWN: activeAnimation = animRunDown; break;
                    case LEFT: activeAnimation = animRunLeft; break;
                    case RIGHT: activeAnimation = animRunRight; break;
                }
            } else {
                switch(lastDirection) {
                    case UP: activeAnimation = animUp;
                        break;
                    case DOWN: activeAnimation = animDown; break;
                    case LEFT: activeAnimation = animLeft; break;
                    case RIGHT: activeAnimation = animRight; break;
                }
            }
        } else {
            updateIdleAnimationBasedOnLastDirection();
        }
        activeAnimation.Update();
    }

    private void updateIdleAnimationBasedOnLastDirection() {
        switch(lastDirection) {
            case UP:    activeAnimation = animIdleUp;
                break;
            case DOWN:  activeAnimation = animIdleDown;  break;
            case LEFT:  activeAnimation = animIdleLeft;  break;
            case RIGHT: activeAnimation = animIdleRight;
                break;
            default:    activeAnimation = animIdleDown;  break;
        }
        activeAnimation.reset();
    }

    private void move(float xAmt, float yAmt) {
        Map currentMap = game.GetRefLinks().GetMap();
        if (currentMap == null) return;

        // --- Verificare coliziune pe axa X ---
        if (xAmt != 0) {
            float newX = x + xAmt;
            Rectangle proposedBoundsX = new Rectangle((int) newX, (int) y, width, height);

            boolean collision = false;
            // 1. Verificare coliziune cu dale solide
            int tx = (int) ((xAmt > 0 ? newX + width - 1 : newX) / Tile.TILE_WIDTH);
            int ty_top = (int) (y / Tile.TILE_HEIGHT);
            int ty_bottom = (int) ((y + height - 1) / Tile.TILE_HEIGHT);

            // Verificare coliziune cu dala ID 64
            if (currentMap.GetTile(tx, ty_top).GetId() == 64 || currentMap.GetTile(tx, ty_bottom).GetId() == 64) {
                collision = true;
            }
            if (currentMap.GetTile(tx, ty_top).IsSolid() || currentMap.GetTile(tx, ty_bottom).IsSolid()) {
                collision = true;
            }


            // 2. Verificare coliziune cu entitati solide (ex: mese)
            if (!collision) {
                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    ArrayList<Entity> entities = ((GameState)currentState).getEntities();
                    if (entities != null) {
                        for (Entity e : entities) {
                            if (e.equals(this)) continue;
                            if (e instanceof DecorativeObject && ((DecorativeObject)e).isSolid()) {
                                if (proposedBoundsX.intersects(e.GetBounds())) {
                                    collision = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (!collision) {
                x = newX;
            }
        }

        // --- Verificare coliziune pe axa Y ---
        if (yAmt != 0) {
            float newY = y + yAmt;
            Rectangle proposedBoundsY = new Rectangle((int) x, (int) newY, width, height);

            boolean collision = false;
            // 1. Verificare coliziune cu dale solide
            int ty = (int) ((yAmt > 0 ? newY + height - 1 : newY) / Tile.TILE_HEIGHT);
            int tx_left = (int) (x / Tile.TILE_WIDTH);
            int tx_right = (int) ((x + width - 1) / Tile.TILE_WIDTH);

            // Verificare coliziune cu dala ID 64
            if (currentMap.GetTile(tx_left, ty).GetId() == 64 || currentMap.GetTile(tx_right, ty).GetId() == 64) {
                collision = true;
            }
            if (currentMap.GetTile(tx_left, ty).IsSolid() || currentMap.GetTile(tx_right, ty).IsSolid()) {
                collision = true;
            }


            // 2. Verificare coliziune cu entitati solide (ex: mese)
            if (!collision) {
                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    ArrayList<Entity> entities = ((GameState)currentState).getEntities();
                    if (entities != null) {
                        for (Entity e : entities) {
                            if (e.equals(this)) continue;
                            if (e instanceof DecorativeObject && ((DecorativeObject)e).isSolid()) {
                                if (proposedBoundsY.intersects(e.GetBounds())) {
                                    collision = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (!collision) {
                y = newY;
            }
        }

        // Asiguram ca jucatorul ramane in limitele harti
        int mapWidthPx = currentMap.GetWidth() * Tile.TILE_WIDTH;
        int mapHeightPx = currentMap.GetHeight() * Tile.TILE_HEIGHT;
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + width > mapWidthPx) x = mapWidthPx - width;
        if (y + height > mapHeightPx) y = mapHeightPx - height;

        this.bounds.setLocation((int)x, (int)y);
    }

    public BufferedImage GetActiveFrame() {
        return activeAnimation.getCurrentFrame();
    }

    public Animation getActiveAnimation() {
        return activeAnimation;
    }

    public boolean isHurt() {
        return isHurt;
    }

    @Override public float GetX() { return x;
    }
    @Override public float GetY() { return y;
    }
    @Override public int GetWidth() { return width;
    }
    @Override public int GetHeight() { return height;
    }
    @Override public Rectangle GetBounds() { return bounds;
    }

    @Override
    public void SetPosition(float x, float y) {
        super.SetPosition(x,y);
        isMoving = false; isRunning = false; isJumping = false; isAttacking = false;
        isHurt = false; isCombatIdle = false;
        isThrusting = false;
        isHalfslashing = false; isSlashing = false;
        updateIdleAnimationBasedOnLastDirection();
    }

    public int getHealth() { return health;
    }
    public int getMaxHealth() { return maxHealth;
    }
    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) { health = 0; }
        System.out.println("DEBUG: James a luat " + amount + " daune. Viata ramasa: " + health);
        if (health <= 0) {
            isHurt = true;
            activeAnimation = animHurt;
            activeAnimation.reset();
        }
    }
    public void setHealth(int health) {
        this.health = health;
        if (this.health < 0) { this.health = 0; }
        System.out.println("DEBUG: Viata lui James a fost setata la " + this.health);
    }
    public void resetHealth() {
        health = maxHealth;
        System.out.println("DEBUG: Viata lui James a fost resetata la " + health);
    }

    private Animation safeAnimation(BufferedImage[] frames, int speed) {
        BufferedImage defaultFrame = new BufferedImage(Assets.PLAYER_FRAME_WIDTH, Assets.PLAYER_FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        return (frames != null && frames.length > 0) ? new Animation(speed, frames) : new Animation(100, new BufferedImage[]{defaultFrame});
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public Rectangle getAttackBounds() {
        if (isAttacking) {
            int attackWidth = 40;
            int attackHeight = 40;
            int attackX = (int) x + width / 2;
            int attackY = (int) y + height / 2;

            switch (lastDirection) {
                case UP:    attackY -= (height / 2 + attackHeight); break;
                case DOWN:  attackY += height / 2; break;
                case LEFT:  attackX -= (width / 2 + attackWidth); break;
                case RIGHT: attackX += width / 2; break;
            }
            return new Rectangle(attackX, attackY, attackWidth, attackHeight);
        }
        return null;
    }
}