package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.Tiles.Tile;
import java.awt.*;
import java.awt.image.BufferedImage;

/*!
 * \class public class Agent extends Entity
 * \brief Implementeaza antagonistul principal al jocului, Agentul lui Magnus Voss.
 * Acesta este un inamic cu o prezenta vizuala si un comportament de baza.
 */
public class Agent extends Entity {

    private static final int DEFAULT_AGENT_WIDTH = 64;
    private static final int DEFAULT_AGENT_HEIGHT = 64;
    private static final float DEFAULT_SPEED = 0.8f;
    private static final float CHASE_SPEED = 1.5f;

    private Animation animIdleDown, animIdleUp, animIdleLeft, animIdleRight;
    private Animation animWalkDown, animWalkUp, animWalkLeft, animWalkRight;
    private Animation animRunDown, animRunUp, animRunLeft, animRunRight;
    private Animation animThrust;
    private Animation animSlash;
    private Animation activeAnimation;

    private float speed;
    private float patrolStartX, patrolEndX;
    private boolean movingRight = true;
    private boolean isPatrolling = false;
    private boolean isChasing = false;
    private boolean isAttacking = false;
    private Direction lastDirection = Direction.DOWN;

    private float xMove, yMove;

    private int damage = 30;

    private int health;
    private int maxHealth = 100;
    private long lastDamageTakenTime = 0;
    private final long DAMAGE_COOLDOWN_MS = 500;
    private final long ATTACK_COOLDOWN_MS = 1000;
    private long lastAttackTime = 0;
    private enum Direction { UP, DOWN, LEFT, RIGHT }

    private Animation animHurt;
    private boolean isDefeated = false;

    /*!
     * \fn public Agent(RefLinks refLink, float x, float y, float patrolStartX, float patrolEndX, boolean isPatrolling)
     * \brief Constructorul de initializare al clasei Agent.
     * \param refLink Referinta catre obiectul RefLinks.
     * \param x Coordonata X initiala.
     * \param y Coordonata Y initiala.
     * \param patrolStartX Limita de start a patrularii pe axa X (pixeli).
     * \param patrolEndX Limita de final a patrularii pe axa X (pixeli).
     * \param isPatrolling Daca este true, agentul va patrula, altfel va sta pe loc.
     */
    public Agent(RefLinks refLink, float x, float y, float patrolStartX, float patrolEndX, boolean isPatrolling) {
        super(refLink, x, y, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT);
        this.speed = DEFAULT_SPEED;
        this.patrolStartX = patrolStartX;
        this.patrolEndX = patrolEndX;
        this.isPatrolling = isPatrolling;
        this.xMove = 0;
        this.yMove = 0;
        this.bounds = new Rectangle(0, 0, width, height);
        this.health = maxHealth;

        // Inițializarea animațiilor
        animIdleDown = new Animation(200, Assets.agentIdleDown);
        animIdleUp = new Animation(200, Assets.agentIdleUp);
        animIdleLeft = new Animation(200, Assets.agentIdleLeft);
        animIdleRight = new Animation(200, Assets.agentIdleRight);
        animWalkDown = new Animation(150, Assets.agentDown);
        animWalkUp = new Animation(150, Assets.agentUp);
        animWalkLeft = new Animation(150, Assets.agentLeft);
        animWalkRight = new Animation(150, Assets.agentRight);
        animRunDown = new Animation(100, Assets.agentRunDown);
        animRunUp = new Animation(100, Assets.agentRunUp);
        animRunLeft = new Animation(100, Assets.agentRunLeft);
        animRunRight = new Animation(100, Assets.agentRunRight);
        animThrust = new Animation(100, Assets.agentThrust);
        animSlash = new Animation(100, Assets.agentSlash);
        animHurt = new Animation(150, Assets.agentHurt, false); // Animație non-looping

        activeAnimation = animIdleDown;
        lastDirection = Direction.DOWN;
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea agentului (miscarea, interacțiunea).
     */
    @Override
    public void Update() {
        if (isDefeated) {
            activeAnimation.Update();
            return;
        }

        if (isAttacking) {
            activeAnimation.Update();
            if (activeAnimation.isFinished()) {
                isAttacking = false;
                lastAttackTime = System.currentTimeMillis();
            }
        } else if (isChasing) {
            chasePlayer();
        } else if (isPatrolling) {
            moveAgent();
        } else {
            updateIdleAnimationBasedOnLastDirection();
        }
        activeAnimation.Update();
        checkPlayerCollision();
        checkPlayerAttackCollision();
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

    /*!
     * \fn private void moveAgent()
     * \brief Implementeaza logica de miscare a agentului (patrulare orizontala).
     */
    private void moveAgent() {
        // Resetare xMove la început pentru a evita comportamentele residuale
        xMove = 0;

        if (movingRight) {
            if (x + width < patrolEndX) {
                xMove = speed;
                lastDirection = Direction.RIGHT;
                activeAnimation = animWalkRight;
            } else {
                movingRight = false;
                updateIdleAnimationBasedOnLastDirection();
            }
        } else {
            if (x > patrolStartX) {
                xMove = -speed;
                lastDirection = Direction.LEFT;
                activeAnimation = animWalkLeft;
            } else {
                movingRight = true;
                updateIdleAnimationBasedOnLastDirection();
            }
        }

        // Mișcare doar dacă xMove nu este 0
        if (xMove != 0) {
            move(xMove, 0);
        }
    }

    private void chasePlayer() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        float playerCenterX = player.GetX() + player.GetWidth() / 2;
        float playerCenterY = player.GetY() + player.GetHeight() / 2;
        float agentCenterX = x + width / 2;
        float agentCenterY = y + height / 2;

        float dx = playerCenterX - agentCenterX;
        float dy = playerCenterY - agentCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        float currentSpeed = player.isRunning() ? CHASE_SPEED : DEFAULT_SPEED;

        if (distance > 10) {
            // Resetare variabile de mișcare pentru a evita comportamentele residuale
            xMove = 0;
            yMove = 0;

            xMove = (dx / distance) * currentSpeed;
            yMove = (dy / distance) * currentSpeed;

            // Determinare direcție principală pentru animație
            if (Math.abs(dx) > Math.abs(dy)) {
                lastDirection = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
            } else {
                lastDirection = (dy > 0) ? Direction.DOWN : Direction.UP;
            }

            // Setare animație corectă
            switch (lastDirection) {
                case UP:
                    activeAnimation = (currentSpeed == CHASE_SPEED) ? animRunUp : animWalkUp;
                    break;
                case DOWN:
                    activeAnimation = (currentSpeed == CHASE_SPEED) ? animRunDown : animWalkDown;
                    break;
                case LEFT:
                    activeAnimation = (currentSpeed == CHASE_SPEED) ? animRunLeft : animWalkLeft;
                    break;
                case RIGHT:
                    activeAnimation = (currentSpeed == CHASE_SPEED) ? animRunRight : animWalkRight;
                    break;
            }

            move(xMove, yMove);
        } else {
            // Resetare mișcare când este aproape de jucător
            xMove = 0;
            yMove = 0;
            updateIdleAnimationBasedOnLastDirection();
        }
    }

    /*!
     * \fn public void setChaseMode(boolean chasing)
     * \brief Seteaza modul de urmarire al agentului.
     */
    public void setChaseMode(boolean chasing) {
        this.isChasing = chasing;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    /*!
     * \fn private void checkPlayerCollision()
     * \brief Verifica coliziunea cu jucatorul si aplica daune.
     */
    private void checkPlayerCollision() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds()) && !isAttacking() && System.currentTimeMillis() - lastAttackTime > ATTACK_COOLDOWN_MS) {
            System.out.println("DEBUG Agent: Coliziune cu jucatorul!");
            player.takeDamage(damage);
            isAttacking = true;
            // Alege animația de atac corectă pe baza direcției
            switch(lastDirection) {
                case UP: activeAnimation = animThrust; break;
                case DOWN: activeAnimation = animThrust; break;
                case LEFT: activeAnimation = animThrust; break;
                case RIGHT: activeAnimation = animThrust; break;
            }
            activeAnimation.reset();
            lastAttackTime = System.currentTimeMillis();
        }
    }

    private void checkPlayerAttackCollision() {
        Player player = refLink.GetPlayer();
        if (player == null || System.currentTimeMillis() - lastDamageTakenTime < DAMAGE_COOLDOWN_MS) return;

        Rectangle playerAttackBounds = player.getAttackBounds();
        if (playerAttackBounds != null && this.bounds.intersects(playerAttackBounds)) {
            takeDamage(player.getAttackDamage());
            lastDamageTakenTime = System.currentTimeMillis();
        }
    }

    public void takeDamage(int amount) {
        if (isDefeated) return;

        health -= amount;
        System.out.println("Agent a luat " + amount + " daune. Viata ramasa: " + health);

        if (health <= 0) {
            health = 0;
            isDefeated = true;
            activeAnimation = animHurt;
            activeAnimation.reset();
        }
    }

    public int getHealth() {
        return health;
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza agentul pe ecran.
     * \param g Contextul grafic.
     */
    @Override
    public void Draw(Graphics g) {
        int drawX = (int)(x - refLink.GetGameCamera().getxOffset());
        int drawY = (int)(y - refLink.GetGameCamera().getyOffset());
        BufferedImage currentFrame = activeAnimation.getCurrentFrame();
        if (currentFrame != null) {
            g.drawImage(currentFrame, drawX, drawY, width, height, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(drawX, drawY, width, height);
        }

        if (!isDefeated) {
            drawHealthBar(g);
        }
    }

    private void drawHealthBar(Graphics g) {
        int barWidth = width;
        int barHeight = 5;
        int healthWidth = (int)(((double)health / maxHealth) * barWidth);

        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()));
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()));

        g.setColor(Color.RED);
        g.fillRect(drawX, drawY - 10, barWidth, barHeight);
        g.setColor(Color.GREEN);
        g.fillRect(drawX, drawY - 10, healthWidth, barHeight);
        g.setColor(Color.BLACK);
        g.drawRect(drawX, drawY - 10, barWidth, barHeight);
    }

    private Animation safeAnimation(BufferedImage[] frames, int speed) {
        BufferedImage defaultFrame = new BufferedImage(DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        return (frames != null && frames.length > 0) ? new Animation(speed, frames) : new Animation(100, new BufferedImage[]{defaultFrame});
    }

    // Metoda de miscare a Agentului cu verificare de coliziune
    private void move(float xAmt, float yAmt) {
        if (xAmt != 0) {
            float newX = x + xAmt;
            Rectangle proposedBoundsX = new Rectangle((int) newX, (int) y, width, height);
            boolean collision = false;
            int tx = (int) ((xAmt > 0 ? newX + width - 1 : newX) / Tile.TILE_WIDTH);
            int ty_top = (int) (y / Tile.TILE_HEIGHT);
            int ty_bottom = (int) ((y + height - 1) / Tile.TILE_HEIGHT);

            if (refLink.GetMap().GetTile(tx, ty_top).GetId() == 64 || refLink.GetMap().GetTile(tx, ty_bottom).GetId() == 64) {
                collision = true;
            }
            if (!collision && (refLink.GetMap().GetTile(tx, ty_top).IsSolid() || refLink.GetMap().GetTile(tx, ty_bottom).IsSolid())) {
                collision = true;
            }

            // Verificare coliziune cu entitati solide
            if (!collision) {
                for (Entity e : refLink.GetGameState().getEntities()) {
                    if (e.equals(this)) continue;
                    if (e instanceof DecorativeObject && ((DecorativeObject) e).isSolid() && proposedBoundsX.intersects(e.GetBounds())) {
                        collision = true;
                        break;
                    }
                }
            }

            if (!collision) {
                x = newX;
            }
        }

        if (yAmt != 0) {
            float newY = y + yAmt;
            Rectangle proposedBoundsY = new Rectangle((int) x, (int) newY, width, height);
            boolean collision = false;
            int ty = (int) ((yAmt > 0 ? newY + height - 1 : newY) / Tile.TILE_HEIGHT);
            int tx_left = (int) (x / Tile.TILE_WIDTH);
            int tx_right = (int) ((x + width - 1) / Tile.TILE_WIDTH);

            if (refLink.GetMap().GetTile(tx_left, ty).GetId() == 64 || refLink.GetMap().GetTile(tx_right, ty).GetId() == 64) {
                collision = true;
            }
            if (!collision && (refLink.GetMap().GetTile(tx_left, ty).IsSolid() || refLink.GetMap().GetTile(tx_right, ty).IsSolid())) {
                collision = true;
            }

            // Verificare coliziune cu entitati solide
            if (!collision) {
                for (Entity e : refLink.GetGameState().getEntities()) {
                    if (e.equals(this)) continue;
                    if (e instanceof DecorativeObject && ((DecorativeObject) e).isSolid() && proposedBoundsY.intersects(e.GetBounds())) {
                        collision = true;
                        break;
                    }
                }
            }

            if (!collision) {
                y = newY;
            }
        }
        bounds.setLocation((int) x, (int) y);
    }
}