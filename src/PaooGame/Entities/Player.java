package PaooGame.Entities;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.States.GameOverState;
import PaooGame.Tiles.Tile;
import PaooGame.Camera.GameCamera;
import PaooGame.Map.Map;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * @class Player
 * @brief Implementeaza notiunea de erou/jucator (player) in joc.
 * Aceasta clasa extinde Entity si este controlata direct de utilizator.
 * Gestioneaza input-ul, miscarea, coliziunile cu mediul, animatiile,
 * starile de actiune (mers, alergat, sarit, atac), viata si lupta.
 */
public class Player extends Entity {

    /** Referinta la obiectul principal al jocului.*/
    private final Game game;
    /** Atribute pentru viteza de miscare a jucatorului.*/
    private final float walkSpeed = 3.0f;
    private float currentSpeed;

    /** Atribute pentru starea de sanatate a jucatorului.*/
    private int health;
    private final int maxHealth = 100;

    /** Toate animatiile de miscare ale jucatorului.*/
    private final Animation animDown;
    private final Animation animUp;
    private final Animation animLeft;
    private final Animation animRight;
    private final Animation animIdleDown;
    private final Animation animIdleUp;
    private final Animation animIdleLeft;
    private final Animation animIdleRight;
    private final Animation animRunDown;
    private final Animation animRunUp;
    private final Animation animRunLeft;
    private final Animation animRunRight;
    private final Animation animJumpDown;
    private final Animation animJumpUp;
    private final Animation animJumpLeft;
    private final Animation animJumpRight;
    private final Animation animHurt;
    private final Animation animHalfslashUp;
    private final Animation animHalfslashDown;
    private final Animation animHalfslashLeft;
    private final Animation animHalfslashRight;

    /** Animatia activa in cadrul curent.*/
    public Animation activeAnimation;

    /** Flag-uri booleene ce definesc starea curenta a jucatorului.*/
    private boolean isMoving;
    private boolean isRunning;
    private boolean isJumping;
    private boolean isAttacking;
    private boolean isHurt;
    private boolean isCombatIdle;
    private boolean isThrusting;
    private boolean isHalfslashing;
    private boolean isSlashing;

    /** Enum intern pentru a gestiona directia in care este orientat jucatorul.*/
    private enum Direction { UP, DOWN, LEFT, RIGHT }
    /** Ultima directie in care s-a miscat jucatorul.*/
    private Direction lastDirection = Direction.DOWN;

    /**
     * @brief Constructorul clasei Player.
     * @param game Referinta la obiectul principal al jocului.
     * @param x Coordonata X initiala a jucatorului.
     * @param y Coordonata Y initiala a jucatorului.
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
        /* Animatii care nu sunt in loop */
        animHurt = new Animation(hurtAnimationSpeed, Assets.playerHurt, false);
        animJumpDown = new Animation(jumpAnimationSpeed, Assets.playerJumpDown, false);
        animJumpUp = new Animation(jumpAnimationSpeed, Assets.playerJumpUp, false);
        animJumpLeft = new Animation(jumpAnimationSpeed, Assets.playerJumpLeft, false);
        animJumpRight = new Animation(jumpAnimationSpeed, Assets.playerJumpRight, false);

        animHalfslashUp = new Animation(attackAnimationSpeed, Assets.playerHalfslashUp, false);
        animHalfslashDown = new Animation(attackAnimationSpeed, Assets.playerHalfslashDown, false);
        animHalfslashLeft = new Animation(attackAnimationSpeed, Assets.playerHalfslashLeft, false);
        animHalfslashRight = new Animation(attackAnimationSpeed, Assets.playerHalfslashRight, false);

        /* Setari initiale */
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

    /**
     * @brief Actualizeaza starea jucatorului in fiecare cadru.
     * Gestioneaza starile (ranit, atac, miscare), preia input-ul si centreaza camera.
     */
    @Override
    public void Update() {
        if (game == null || game.GetKeyManager() == null) return;
        if (isHurt) {
            activeAnimation.Update();
            if (activeAnimation.isFinished()) {
                isHurt = false; // Revine la starea normala dupa terminarea animatiei
                if (health <= 0) {
                    // Daca viata este 0, acum intra in starea de Game Over
                    refLink.SetState(new GameOverState(refLink));
                }
            }
            return; // Opreste orice alta actiune (miscare, atac) cat timp esti lovit
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

    /**
     * @brief Deseneaza jucatorul pe ecran.
     * @param g Contextul grafic in care se va desena.
     */
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

    /**
     * @brief Aplica daune jucatorului si gestioneaza starea de "ranit" sau de "game over".
     * @param amount Cantitatea de viata de scazut.
     */
    public void takeDamage(int amount) {
        if(isHurt) return; // Nu permite daune daca animatia de moarte deja ruleaza

        health -= amount;
        if (health < 0) {
            health = 0;
        }
        //System.out.println("DEBUG: James a luat " + amount + " daune. Viata ramasa: " + health);

        // Logica de "hurt" se activeaza DOAR daca viata ajunge la 0
        if (health <= 0) {
            isHurt = true;
            activeAnimation = animHurt;
            if (activeAnimation != null) {
                activeAnimation.reset();
            }
        }
    }

    /**
     * @brief Seteaza pozitia jucatorului si reseteaza toate flag-urile de stare si actiune.
     * @param x Noua coordonata X.
     * @param y Noua coordonata Y.
     */
    @Override
    public void SetPosition(float x, float y) {
        super.SetPosition(x,y);
        isMoving = false; isRunning = false; isJumping = false; isAttacking = false;
        isHurt = false; isCombatIdle = false;
        isThrusting = false;
        isHalfslashing = false; isSlashing = false;
        updateIdleAnimationBasedOnLastDirection();
    }

    /**
     * @brief Reseteaza viata jucatorului la valoarea maxima.
     */
    public void resetHealth() {
        health = maxHealth;
        //System.out.println("DEBUG: Viata lui James a fost resetata la " + health);
    }

    /**
     * @brief Seteaza o noua valoare pentru viata jucatorului.
     * @param health Noua valoare a vietii.
     */
    public void setHealth(int health) {
        this.health = health;
        if (this.health < 0) { this.health = 0; }
        //System.out.println("DEBUG: Viata lui James a fost setata la " + this.health);
    }

    /**
     * @brief Actualizeaza pozitia dreptunghiului de coliziune pentru a se potrivi cu coordonatele curente.
     */
    public void updateBoundingBox() {
        this.bounds.setLocation((int) x, (int) y);
    }

    /**
     * @brief Citeste input-ul de la tastatura si actualizeaza starea si miscarea jucatorului.
     */
    private void GetInput() {
        isMoving = false;
        float xMove = 0;
        float yMove = 0;

        isRunning = game.GetKeyManager().shift;
        float runSpeed = 6.0f;
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

    /**
     * @brief Misca jucatorul si gestioneaza coliziunile cu mediul.
     * Verifica coliziunile pe fiecare axa separat si are reguli specifice pentru fiecare nivel.
     * @param xAmt Valoarea de miscare pe axa X.
     * @param yAmt Valoarea de miscare pe axa Y.
     */
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

                // Un obiect este solid daca exista (!= 0) sI NU este o usa deschisa
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
                // REGULA DOAR PENTRU NIVELUL 3
            } else if (levelIndex == 2) {
                int objectGidLeft = currentMap.getTilesGidsLayers().get(2)[tx_left][ty];
                int objectGidRight = currentMap.getTilesGidsLayers().get(2)[tx_right][ty];

                // Un obiect este solid daca exista (!= 0) sI NU este o usa deschisa
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

    /**
     * @brief Actualizeaza animatia activa in functie de starea de miscare (mers, alergat, static).
     */
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

    /**
     * @brief Seteaza animatia de "idle" (static) corespunzatoare ultimei directii in care era orientat jucatorul.
     */
    private void updateIdleAnimationBasedOnLastDirection() {
        switch(lastDirection) {
            case UP:    activeAnimation = animIdleUp; break;
            case LEFT:  activeAnimation = animIdleLeft;  break;
            case RIGHT: activeAnimation = animIdleRight; break;
            default:    activeAnimation = animIdleDown;  break;
        }
        activeAnimation.reset();
    }

    /**
     * @brief Returneaza viata curenta a jucatorului.
     */
    public int getHealth() { return health; }

    /**
     * @brief Returneaza viata maxima a jucatorului.
     */
    public int getMaxHealth() { return maxHealth; }

    /**
     * @brief Returneaza cadrul (imaginea) curent al animatiei active.
     */
    public BufferedImage GetActiveFrame() {
        return activeAnimation.getCurrentFrame();
    }

    /**
     * @brief Returneaza obiectul animatiei active.
     */
    public Animation getActiveAnimation() {
        return activeAnimation;
    }

    /**
     * @brief Verifica daca jucatorul este in starea "ranit" (animatia de moarte).
     */
    public boolean isHurt() {
        return isHurt;
    }

    /**
     * @brief Verifica daca jucatorul alearga.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * @brief Returneaza ultima directie in care a fost orientat jucatorul.
     */
    public Direction getLastDirection() {
        return lastDirection;
    }

    /**
     * @brief Returneaza daunele pe care le provoaca jucatorul.
     */
    public int getAttackDamage() {
        /* Daunele provocate de un atac al jucatorului.*/
        return 20;
    }

    /**
     * @brief Calculeaza si returneaza dreptunghiul de coliziune pentru atacul curent.
     * @return Un obiect Rectangle reprezentand zona de atac, sau null daca jucatorul nu ataca.
     */
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

    /**
     * @brief Verifica daca jucatorul este in timpul unei animatii de atac.
     */
    public boolean isAttacking() {
        return isAttacking;
    }

    /**
     * @brief Getters suprascrisi din clasa Entity.
     */
    @Override public float GetX() { return x; }

    @Override public float GetY() { return y; }

    @Override public int GetWidth() { return width; }

    @Override public int GetHeight() { return height; }
    
    @Override public Rectangle GetBounds() { return bounds; }
}