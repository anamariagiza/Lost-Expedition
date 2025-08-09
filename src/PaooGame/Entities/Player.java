package PaooGame.Entities;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.Tiles.Tile;
import PaooGame.Camera.GameCamera;
import PaooGame.Map.Map;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/*!
 * \class public class Player extends Entity
 * \brief Implementeaza notiunea de erou/jucator (player) in joc.
 */
public class Player extends Entity {

    private Game game;
    private float walkSpeed = 3.0f;
    private float runSpeed = 6.0f;
    private float currentSpeed;

    // Proprietati de viata
    private int health;
    private int maxHealth = 100;

    // Obiecte de animatie pentru mers (Walk)
    private Animation animDown, animUp, animLeft, animRight;
    // Animații pentru Idle (specifice direcției)
    private Animation animIdleDown, animIdleUp, animIdleLeft, animIdleRight;
    // Animații pentru Alergat (specifice direcției)
    private Animation animRunDown, animRunUp, animRunLeft, animRunRight;
    // Animații pentru Sarit (specifice direcției)
    private Animation animJumpDown, animJumpUp, animJumpLeft, animJumpRight;
    // Animații de acțiune (doar cele folosite)
    private Animation animHurt;
    private Animation animCombatIdle;
    private Animation animThrust;
    private Animation animHalfslash;
    private Animation animSlash;

    private Animation activeAnimation; // Animația curentă afișată

    // Flaguri de stare
    private boolean isMoving;
    private boolean isRunning;
    private boolean isJumping;
    private boolean isAttacking;
    private boolean isHurt;
    private boolean isCombatIdle;

    // Flaguri specifice pentru atacuri/acțiuni (doar cele folosite)
    private boolean isThrusting;
    private boolean isHalfslashing;
    private boolean isSlashing;
    // Variabila pentru a retine ultima directie de miscare
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    private Direction lastDirection = Direction.DOWN;

    /*!
     * \fn public Player(Game game, float x, float y)
     * \brief Constructorul de initializare al clasei Player.
     * \param game Referinta catre obiectul Game.
     * \param x Coordonata x a pozitiei initiale a jucatorului.
     * \param y Coordonata y a pozitiei initiale a jucatorului.
     */
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


        // Initializeaza animatiile
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

        // Resetare flaguri de stare
        isMoving = false; isRunning = false; isJumping = false; isAttacking = false;
        isHurt = false; isCombatIdle = false; isThrusting = false;
        isHalfslashing = false; isSlashing = false;

        this.bounds = new Rectangle((int)x, (int)y, width, height);
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza pozitia si imaginea jucatorului.
     */
    @Override
    public void Update() {
        if (game == null || game.GetKeyManager() == null) return;
        GetInput();

        game.GetRefLinks().GetGameCamera().centerOnEntity(this);
        if (!isAttacking && !isHurt && !isJumping && !isCombatIdle &&
                !isThrusting && !isHalfslashing && !isSlashing) {
            updateMovementAnimation();
        } else {
            activeAnimation.Update();
            if (activeAnimation.isFinished()) {
                isAttacking = false; isHurt = false; isJumping = false;
                isCombatIdle = false; isThrusting = false;
                isHalfslashing = false; isSlashing = false;

                updateIdleAnimationBasedOnLastDirection();
            }
        }
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza jucatorul pe ecran, ajustand pozitiile cu offset-ul camerei si zoom.
     * Aceasta metoda este apelata din GameState.
     * \param g Contextul grafic in care sa se realizeze desenarea.
     */
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
        } else {
            float zoom = camera.getZoomLevel();
            int drawX = (int)((x - camera.getxOffset()) * zoom);
            int drawY = (int)((y - camera.getyOffset()) * zoom);
            int scaledWidth = (int)(width * zoom);
            int scaledHeight = (int)(height * zoom);
            g.setColor(Color.MAGENTA);
            g.fillRect(drawX, drawY, scaledWidth, scaledHeight);
            System.err.println("DEBUG: Cadrul playerului este NULL. Desenam placeholder.");
        }
    }


    /*!
     * \fn private void GetInput()
     * \brief Citeste input-ul de la tastatura si seteaza directia de miscare si animatia corespunzatoare.
     */
    private void GetInput() {
        isMoving = false;
        float xMove = 0;
        float yMove = 0;

        isRunning = game.GetKeyManager().shift;
        currentSpeed = isRunning ? runSpeed : walkSpeed;

        isAttacking = false; isHurt = false;
        isCombatIdle = false; isThrusting = false;
        isHalfslashing = false; isSlashing = false;

        // NOU: Am mutat logica de miscare aici, pentru a fi posibila deplasarea
        // in timpul animatiei de saritura.

        if (game.GetKeyManager().up) {
            yMove = -currentSpeed;
            isMoving = true;
            lastDirection = Direction.UP;
        }
        if (game.GetKeyManager().down) {
            yMove = currentSpeed;
            isMoving = true;
            lastDirection = Direction.DOWN;
        }
        if (game.GetKeyManager().left) {
            xMove = -currentSpeed;
            isMoving = true;
            lastDirection = Direction.LEFT;
        }
        if (game.GetKeyManager().right) {
            xMove = currentSpeed;
            isMoving = true;
            lastDirection = Direction.RIGHT;
        }

        // Acțiuni (prioritare față de mișcare simplă)
        // Aici am adaugat o conditie, pentru a nu incepe o animatie noua
        // in timp ce o alta este deja in desfasurare
        if (!isJumping && !isAttacking && !isHurt && !isCombatIdle &&
                !isThrusting && !isHalfslashing && !isSlashing) {
            if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_SPACE)) {
                isJumping = true;
                switch(lastDirection) {
                    case UP:    activeAnimation = animJumpUp;    break;
                    case DOWN:  activeAnimation = animJumpDown;  break;
                    case LEFT:  activeAnimation = animJumpLeft;  break;
                    case RIGHT: activeAnimation = animJumpRight; break;
                    default:    activeAnimation = animJumpDown;  break;
                }
                activeAnimation.reset();
            } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_J)) {
                isAttacking = true; isThrusting = true; activeAnimation = animThrust; activeAnimation.reset();
            } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_K)) {
                isAttacking = true; isHalfslashing = true; activeAnimation = animHalfslash; activeAnimation.reset();
            } // Linia pentru 'E' (isEmoting) A FOST ELIMINATĂ DEFINITIV.
            // Acum tasta 'E' este disponibilă exclusiv pentru interacțiuni (Key.java)
            else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_B)) {
                isCombatIdle = true; activeAnimation = animCombatIdle; activeAnimation.reset();
            } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_SLASH)) {
                isAttacking = true; isSlashing = true; activeAnimation = animSlash; activeAnimation.reset();
            }
        }

        move(xMove, yMove);
    }

    /*!
     * \fn private void updateMovementAnimation()
     * \brief Actualizeaza animatia de miscare/idle.
     */
    private void updateMovementAnimation() {
        if (isMoving) {
            if (isRunning) {
                switch(lastDirection) {
                    case UP:    activeAnimation = animRunUp;    break;
                    case DOWN:  activeAnimation = animRunDown;  break;
                    case LEFT:  activeAnimation = animRunLeft;  break;
                    case RIGHT: activeAnimation = animRunRight; break;
                }
            } else {
                switch(lastDirection) {
                    case UP:    activeAnimation = animUp;    break;
                    case DOWN:  activeAnimation = animDown;  break;
                    case LEFT:  activeAnimation = animLeft;  break;
                    case RIGHT: activeAnimation = animRight; break;
                }
            }
        } else {
            updateIdleAnimationBasedOnLastDirection();
        }
        activeAnimation.Update();
    }

    /*!
     * \fn private void updateIdleAnimationBasedOnLastDirection()
     * \brief Seteaza animatia de idle bazata pe ultima directie de miscare.
     */
    private void updateIdleAnimationBasedOnLastDirection() {
        switch(lastDirection) {
            case UP:    activeAnimation = animIdleUp;    break;
            case DOWN:  activeAnimation = animIdleDown;  break;
            case LEFT:  activeAnimation = animIdleLeft;  break;
            case RIGHT: activeAnimation = animIdleRight; break;
            default:    activeAnimation = animIdleDown;  break;
        }
        activeAnimation.reset();
    }


    /*!
     * \fn private void move(float xAmt, float yAmt)
     * \brief Aplica miscarea jucatorului, tinand cont de coliziunile cu dalele solide.
     * \param xAmt Cantitatea de miscare pe axa X.
     * \param yAmt Cantitatea de miscare pe axa Y.
     */
    private void move(float xAmt, float yAmt) {
        Map currentMap = game.GetRefLinks().GetMap();
        if (currentMap == null) return;

        float newX = x + xAmt;
        float newY = y + yAmt;

        if (xAmt != 0) {
            int tx = (int) ((xAmt > 0 ? newX + width -1 : newX) / Tile.TILE_WIDTH);

            int ty_top = (int) (y / Tile.TILE_HEIGHT);
            int ty_bottom = (int) ((y + height - 1) / Tile.TILE_HEIGHT);

            boolean collision = false;
            if (currentMap.GetTile(tx, ty_top).IsSolid() || currentMap.GetTile(tx, ty_bottom).IsSolid()) {
                collision = true;
            }

            if (!collision) {
                x = newX;
            } else {
                if (xAmt < 0) {
                    x = tx * Tile.TILE_WIDTH + Tile.TILE_WIDTH;
                } else {
                    x = tx * Tile.TILE_WIDTH - width;
                }
            }
        }

        if (yAmt != 0) {
            int ty = (int) ((yAmt > 0 ? newY + height -1 : newY) / Tile.TILE_HEIGHT);

            int tx_left = (int) (x / Tile.TILE_WIDTH);
            int tx_right = (int) ((x + width - 1) / Tile.TILE_WIDTH);

            boolean collision = false;
            if (currentMap.GetTile(tx_left, ty).IsSolid() || currentMap.GetTile(tx_right, ty).IsSolid()) {
                collision = true;
            }

            if (!collision) {
                y = newY;
            } else {
                if (yAmt < 0) {
                    y = ty * Tile.TILE_HEIGHT + Tile.TILE_HEIGHT;
                } else {
                    y = ty * Tile.TILE_HEIGHT - height;
                }
            }
        }

        int mapWidthPx = currentMap.GetWidth() * Tile.TILE_WIDTH;
        int mapHeightPx = currentMap.GetHeight() * Tile.TILE_HEIGHT;

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + width > mapWidthPx) x = mapWidthPx - width;
        if (y + height > mapHeightPx) y = mapHeightPx - height;

        this.bounds.setLocation((int)x, (int)y);
    }

    /*!
     * \fn public BufferedImage GetActiveFrame()
     * \brief Returneaza cadrul curent al animatiei active a jucatorului.
     */
    public BufferedImage GetActiveFrame() {
        return activeAnimation.getCurrentFrame();
    }

    @Override public float GetX() { return x; }
    @Override public float GetY() { return y; }
    @Override public int GetWidth() { return width; }
    @Override public int GetHeight() { return height; }
    @Override public Rectangle GetBounds() { return bounds; }


    @Override
    public void SetPosition(float x, float y) {
        super.SetPosition(x,y);
        isMoving = false;
        isRunning = false;
        isJumping = false;
        isAttacking = false;
        isHurt = false;
        isCombatIdle = false;
        isThrusting = false;
        isHalfslashing = false;
        isSlashing = false;
        updateIdleAnimationBasedOnLastDirection();
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) { health = 0; } else if (health > maxHealth) { health = maxHealth; }
        System.out.println("DEBUG: James a luat " + amount + " daune. Viata ramasa: " + health);
    }
    public void setHealth(int health) {
        this.health = health;
        if (this.health < 0) { this.health = 0; } else if (this.health > maxHealth) { this.health = maxHealth; }
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
}