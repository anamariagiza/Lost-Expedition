package PaooGame.Entities;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.States.GameOverState;
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
    // Animații de mișcare
    private Animation animDown, animUp, animLeft, animRight;
    private Animation animIdleDown, animIdleUp, animIdleLeft, animIdleRight;
    private Animation animRunDown, animRunUp, animRunLeft, animRunRight;
    private Animation animJumpDown, animJumpUp, animJumpLeft, animJumpRight;
    private Animation animHurt;
    private Animation animCombatIdle;
    // Animații de atac pentru fiecare direcție
    private Animation animThrustUp, animThrustDown, animThrustLeft, animThrustRight;
    private Animation animHalfslashUp, animHalfslashDown, animHalfslashLeft, animHalfslashRight;
    private Animation animSlashUp, animSlashDown, animSlashLeft, animSlashRight;

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
    private int attackDamage = 20;

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
        // Animații care trebuie să ruleze în buclă (loops = true)
        animDown = new Animation(animationSpeed, Assets.playerDown);
        animUp = new Animation(animationSpeed, Assets.playerUp);
        animLeft = new Animation(animationSpeed, Assets.playerLeft);
        animRight = new Animation(animationSpeed, Assets.playerRight);
        animIdleDown = new Animation(animationSpeed * 2, Assets.playerIdleDown);
        animIdleUp = new Animation(animationSpeed * 2, Assets.playerIdleUp);
        animIdleLeft = new Animation(animationSpeed * 2, Assets.playerIdleLeft);
        animIdleRight = new Animation(animationSpeed * 2, Assets.playerIdleRight);
        animRunDown = new Animation(runAnimationSpeed, Assets.playerRunDown);
        animRunUp = new Animation(runAnimationSpeed, Assets.playerRunUp);
        animRunLeft = new Animation(runAnimationSpeed, Assets.playerRunLeft);
        animRunRight = new Animation(runAnimationSpeed, Assets.playerRunRight);
        // Animații de acțiune care rulează o singură dată (loops = false)
        animHurt = new Animation(hurtAnimationSpeed, Assets.playerHurt, false);
        animJumpDown = new Animation(jumpAnimationSpeed, Assets.playerJumpDown, false);
        animJumpUp = new Animation(jumpAnimationSpeed, Assets.playerJumpUp, false);
        animJumpLeft = new Animation(jumpAnimationSpeed, Assets.playerJumpLeft, false);
        animJumpRight = new Animation(jumpAnimationSpeed, Assets.playerJumpRight, false);

        animHalfslashUp = new Animation(attackAnimationSpeed, Assets.playerHalfslashUp, false);
        animHalfslashDown = new Animation(attackAnimationSpeed, Assets.playerHalfslashDown, false);
        animHalfslashLeft = new Animation(attackAnimationSpeed, Assets.playerHalfslashLeft, false);
        animHalfslashRight = new Animation(attackAnimationSpeed, Assets.playerHalfslashRight, false);

        // Setări inițiale
        activeAnimation = animIdleDown;
        lastDirection = Direction.DOWN;
        isMoving = false;
        isRunning = false;
        isJumping = false;
        isAttacking = false;
        isHurt = false;
        isCombatIdle = false;
        isThrusting = false;
        isHalfslashing = false;
        isSlashing = false;
        this.bounds = new Rectangle((int)x, (int)y, width, height);
    }

    @Override
    public void Update() {
        if (game == null || game.GetKeyManager() == null) return;
        if (isHurt) {
            activeAnimation.Update();
            if (activeAnimation.isFinished()) {
                isHurt = false; // Revino la starea normală după terminarea animației
                if (health <= 0) {
                    // Dacă viața este 0, acum intră în starea de Game Over
                    refLink.SetState(new GameOverState(refLink));
                }
            }
            return; // Oprește orice altă acțiune (mișcare, atac) cât timp ești lovit
        }

        GetInput();
        game.GetRefLinks().GetGameCamera().centerOnEntity(this);
        if (!isAttacking && !isJumping && !isCombatIdle &&
                !isThrusting && !isHalfslashing && !isSlashing) {
            updateMovementAnimation();
        } else {
            activeAnimation.Update();
            if (activeAnimation.isFinished()) {
                isAttacking = false;
                isJumping = false;
                isCombatIdle = false;
                isThrusting = false;
                isHalfslashing = false;
                isSlashing = false;
                updateIdleAnimationBasedOnLastDirection();
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        GameCamera camera = game.GetRefLinks().GetGameCamera();
        BufferedImage playerFrame = GetActiveFrame();
        if (playerFrame != null) {
            int drawX = (int)(x - camera.getxOffset());
            int drawY = (int)(y - camera.getyOffset());
            g.drawImage(playerFrame, drawX, drawY, width, height, null);
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
                    case UP: activeAnimation = animJumpUp; break;
                    case DOWN: activeAnimation = animJumpDown; break;
                    case LEFT: activeAnimation = animJumpLeft; break;
                    case RIGHT: activeAnimation = animJumpRight; break;
                }
                activeAnimation.reset();
            } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_K)) {
                isAttacking = true;
                isHalfslashing = true;
                switch(lastDirection) {
                    case UP: activeAnimation = animHalfslashUp; break;
                    case DOWN: activeAnimation = animHalfslashDown; break;
                    case LEFT: activeAnimation = animHalfslashLeft; break;
                    case RIGHT: activeAnimation = animHalfslashRight; break;
                }
                activeAnimation.reset();
            }
        }
        move(xMove, yMove);
    }

    private void updateMovementAnimation() {
        if (isMoving) {
            if (isRunning) {
                switch(lastDirection) {
                    case UP: activeAnimation = animRunUp; break;
                    case DOWN: activeAnimation = animRunDown; break;
                    case LEFT: activeAnimation = animRunLeft; break;
                    case RIGHT: activeAnimation = animRunRight; break;
                }
            } else {
                switch(lastDirection) {
                    case UP: activeAnimation = animUp; break;
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
            case UP:    activeAnimation = animIdleUp; break;
            case DOWN:  activeAnimation = animIdleDown;  break;
            case LEFT:  activeAnimation = animIdleLeft;  break;
            case RIGHT: activeAnimation = animIdleRight; break;
            default:    activeAnimation = animIdleDown;  break;
        }
        activeAnimation.reset();
    }

    private void move(float xAmt, float yAmt) {
        Map currentMap = game.GetRefLinks().GetMap();
        if (currentMap == null) return;

        int levelIndex = -1;
        if (refLink.GetGameState() != null) {
            levelIndex = refLink.GetGameState().getCurrentLevelIndex();
        }

        // --- Verificare coliziune pe axa X ---
        if (xAmt != 0) {
            float newX = x + xAmt;
            Rectangle proposedBoundsX = new Rectangle((int) newX, (int) y, width, height);
            boolean collision = false;

            int tx = (int) ((xAmt > 0 ? newX + width - 1 : newX) / Tile.TILE_WIDTH);
            int ty_top = (int) (y / Tile.TILE_HEIGHT);
            int ty_bottom = (int) ((y + height - 1) / Tile.TILE_HEIGHT);

            Tile tileTop = currentMap.GetTile(tx, ty_top);
            Tile tileBottom = currentMap.GetTile(tx, ty_bottom);
            boolean isSolid = false;

            if (levelIndex == 0) {
                // REGULA DOAR PENTRU NIVELUL 1
                isSolid = (tileTop.GetId() == Tile.GRASS_TILE_GID_SOLID || tileBottom.GetId() == Tile.GRASS_TILE_GID_SOLID);
            } else if (levelIndex == 1) {
                // REGULA DOAR PENTRU NIVELUL 2
                int topGid = tileTop.GetId();
                int bottomGid = tileBottom.GetId();
                isSolid = (topGid == Tile.WALL_TILE_GID_SOLID || bottomGid == Tile.WALL_TILE_GID_SOLID ||
                        topGid == Tile.DOOR_CLOSED_TOP_LEFT_GID || bottomGid == Tile.DOOR_CLOSED_TOP_LEFT_GID ||
                        topGid == Tile.DOOR_CLOSED_TOP_RIGHT_GID || bottomGid == Tile.DOOR_CLOSED_TOP_RIGHT_GID ||
                        topGid == Tile.DOOR_CLOSED_BOTTOM_LEFT_GID || bottomGid == Tile.DOOR_CLOSED_BOTTOM_LEFT_GID ||
                        topGid == Tile.DOOR_CLOSED_BOTTOM_RIGHT_GID || bottomGid == Tile.DOOR_CLOSED_BOTTOM_RIGHT_GID);
                // ...
            } else if (levelIndex == 2) {
                int objectGidTop = currentMap.getTilesGidsLayers().get(2)[tx][ty_top];
                int objectGidBottom = currentMap.getTilesGidsLayers().get(2)[tx][ty_bottom];

                // Un obiect este solid dacă există (!= 0) ȘI NU este o ușă deschisă
                boolean isObjectTopSolid = (objectGidTop != 0 && objectGidTop != 74 && objectGidTop != 75 && objectGidTop != 120 && objectGidTop != 121);
                boolean isObjectBottomSolid = (objectGidBottom != 0 && objectGidBottom != 74 && objectGidBottom != 75 && objectGidBottom != 120 && objectGidBottom != 121);

                isSolid = (tileTop.GetId() == 64 || tileBottom.GetId() == 64 || isObjectTopSolid || isObjectBottomSolid);
            }
            else {
                // REGULA DEFAULT PENTRU ORICE ALT NIVEL
                isSolid = tileTop.IsSolid() || tileBottom.IsSolid();
            }

            if (isSolid) {
                collision = true;
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

            int ty = (int) ((yAmt > 0 ? newY + height - 1 : newY) / Tile.TILE_HEIGHT);
            int tx_left = (int) (x / Tile.TILE_WIDTH);
            int tx_right = (int) ((x + width - 1) / Tile.TILE_WIDTH);

            Tile tileLeft = currentMap.GetTile(tx_left, ty);
            Tile tileRight = currentMap.GetTile(tx_right, ty);
            boolean isSolid = false;

            if (levelIndex == 0) {
                // REGULA DOAR PENTRU NIVELUL 1
                isSolid = (tileLeft.GetId() == Tile.GRASS_TILE_GID_SOLID || tileRight.GetId() == Tile.GRASS_TILE_GID_SOLID);
            } else if (levelIndex == 1) {
                // REGULA DOAR PENTRU NIVELUL 2
                int leftGid = tileLeft.GetId();
                int rightGid = tileRight.GetId();
                isSolid = (leftGid == Tile.WALL_TILE_GID_SOLID || rightGid == Tile.WALL_TILE_GID_SOLID ||
                        leftGid == Tile.DOOR_CLOSED_TOP_LEFT_GID || rightGid == Tile.DOOR_CLOSED_TOP_LEFT_GID ||
                        leftGid == Tile.DOOR_CLOSED_TOP_RIGHT_GID || rightGid == Tile.DOOR_CLOSED_TOP_RIGHT_GID ||
                        leftGid == Tile.DOOR_CLOSED_BOTTOM_LEFT_GID || rightGid == Tile.DOOR_CLOSED_BOTTOM_LEFT_GID ||
                        leftGid == Tile.DOOR_CLOSED_BOTTOM_RIGHT_GID || rightGid == Tile.DOOR_CLOSED_BOTTOM_RIGHT_GID);
                // ...
            } else if (levelIndex == 2) {
                int objectGidLeft = currentMap.getTilesGidsLayers().get(2)[tx_left][ty];
                int objectGidRight = currentMap.getTilesGidsLayers().get(2)[tx_right][ty];

                // Un obiect este solid dacă există (!= 0) ȘI NU este o ușă deschisă
                boolean isObjectLeftSolid = (objectGidLeft != 0 && objectGidLeft != 74 && objectGidLeft != 75 && objectGidLeft != 120 && objectGidLeft != 121);
                boolean isObjectRightSolid = (objectGidRight != 0 && objectGidRight != 74 && objectGidRight != 75 && objectGidRight != 120 && objectGidRight != 121);

                isSolid = (tileLeft.GetId() == 64 || tileRight.GetId() == 64 || isObjectLeftSolid || isObjectRightSolid);
            }
            else {
                // REGULA DEFAULT PENTRU ORICE ALT NIVEL
                isSolid = tileLeft.IsSolid() || tileRight.IsSolid();
            }

            if (isSolid) {
                collision = true;
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

    public boolean isRunning() {
        return isRunning;
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    @Override public float GetX() { return x; }
    @Override public float GetY() { return y; }
    @Override public int GetWidth() { return width; }
    @Override public int GetHeight() { return height; }
    @Override public Rectangle GetBounds() { return bounds; }

    @Override
    public void SetPosition(float x, float y) {
        super.SetPosition(x,y);
        isMoving = false; isRunning = false; isJumping = false; isAttacking = false;
        isHurt = false; isCombatIdle = false;
        isThrusting = false;
        isHalfslashing = false; isSlashing = false;
        updateIdleAnimationBasedOnLastDirection();
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }

    public void takeDamage(int amount) {
        if(isHurt) return; // Nu permite daune dacă animația de moarte deja rulează

        health -= amount;
        if (health < 0) {
            health = 0;
        }
        System.out.println("DEBUG: James a luat " + amount + " daune. Viata ramasa: " + health);

        // Logica de "hurt" se activează DOAR dacă viața ajunge la 0
        if (health <= 0) {
            isHurt = true;
            activeAnimation = animHurt;
            if (activeAnimation != null) {
                activeAnimation.reset();
            }
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

    public boolean isAttacking() {
        return isAttacking;
    }

    public void updateBoundingBox() {
        this.bounds.setLocation((int) x, (int) y);
    }
}