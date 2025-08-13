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

    private int damage = 30;

    private int health;
    private int maxHealth = 100;
    private long lastDamageTakenTime = 0;
    private final long DAMAGE_COOLDOWN_MS = 500;
    private final long ATTACK_COOLDOWN_MS = 1000;
    private long lastAttackTime = 0;

    private enum Direction { UP, DOWN, LEFT, RIGHT }


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

        this.bounds = new Rectangle(0, 0, width, height);

        // Inițializarea animațiilor Agentului
        animIdleDown = safeAnimation(Assets.agentIdleDown, 200);
        animIdleUp = safeAnimation(Assets.agentIdleUp, 200);
        animIdleLeft = safeAnimation(Assets.agentIdleLeft, 200);
        animIdleRight = safeAnimation(Assets.agentIdleRight, 200);

        animWalkDown = safeAnimation(Assets.agentDown, 150);
        animWalkUp = safeAnimation(Assets.agentUp, 150);
        animWalkLeft = safeAnimation(Assets.agentLeft, 150);
        animWalkRight = safeAnimation(Assets.agentRight, 150);

        animRunDown = safeAnimation(Assets.agentRunDown, 100);
        animRunUp = safeAnimation(Assets.agentRunUp, 100);
        animRunLeft = safeAnimation(Assets.agentRunLeft, 100);
        animRunRight = safeAnimation(Assets.agentRunRight, 100);

        animThrust = safeAnimation(Assets.agentThrust, 100);
        animSlash = safeAnimation(Assets.agentSlash, 100);

        activeAnimation = animIdleDown;
        lastDirection = Direction.DOWN;

        this.health = maxHealth;
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea agentului (miscarea, interacțiunea).
     */
    @Override
    public void Update() {
        if(health > 0) {
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

    /*!
     * \fn private void moveAgent()
     * \brief Implementeaza logica de miscare a agentului (patrulare orizontala).
     */
    private void moveAgent() {
        if (movingRight) {
            if (x + width < patrolEndX) {
                x += speed;
                lastDirection = Direction.RIGHT;
                activeAnimation = animWalkDown;
            } else {
                movingRight = false;
            }
        } else {
            if (x > patrolStartX) {
                x -= speed;
                lastDirection = Direction.LEFT;
                activeAnimation = animWalkDown;
            } else {
                movingRight = true;
            }
        }
        bounds.x = (int) x;
    }

    /*!
     * \fn private void chasePlayer()
     * \brief Implementeaza logica de urmarire a jucatorului.
     */
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

        if (distance > 10) {
            x += (dx / distance) * CHASE_SPEED;
            y += (dy / distance) * CHASE_SPEED;

            if (Math.abs(dx) > Math.abs(dy)) {
                lastDirection = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
            } else {
                lastDirection = (dy > 0) ? Direction.DOWN : Direction.UP;
            }
            activeAnimation = animRunDown;
        } else {
            activeAnimation = animIdleDown;
        }

        bounds.setLocation((int)x, (int)y);
    }

    /*!
     * \fn public void setChaseMode(boolean chasing)
     * \brief Seteaza modul de urmarire al agentului.
     */
    public void setChaseMode(boolean chasing) {
        this.isChasing = chasing;
    }

    /*!
     * \fn private void checkPlayerCollision()
     * \brief Verifica coliziunea cu jucatorul si aplica daune.
     */
    private void checkPlayerCollision() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds()) && !isAttacking && System.currentTimeMillis() - lastAttackTime > ATTACK_COOLDOWN_MS) {
            System.out.println("DEBUG Agent: Coliziune cu jucatorul!");
            player.takeDamage(damage);
            isAttacking = true;
            activeAnimation = animThrust;
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
        health -= amount;
        if (health < 0) {
            health = 0;
        }
        System.out.println("Agent a luat " + amount + " daune. Viata ramasa: " + health);
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
        if (health <= 0) return;

        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()) * refLink.GetGameCamera().getZoomLevel());
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()) * refLink.GetGameCamera().getZoomLevel());
        int scaledWidth = (int)(width * refLink.GetGameCamera().getZoomLevel());
        int scaledHeight = (int)(height * refLink.GetGameCamera().getZoomLevel());
        BufferedImage currentFrame = activeAnimation.getCurrentFrame();

        if (currentFrame != null) {
            boolean flip = lastDirection == Direction.LEFT;
            if (flip) {
                g.drawImage(currentFrame, drawX + scaledWidth, drawY, -scaledWidth, scaledHeight, null);
            } else {
                g.drawImage(currentFrame, drawX, drawY, scaledWidth, scaledHeight, null);
            }
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(drawX, drawY, scaledWidth, scaledHeight);
        }

        drawHealthBar(g);
    }

    private void drawHealthBar(Graphics g) {
        int barWidth = width;
        int barHeight = 5;
        int healthWidth = (int)(((double)health / maxHealth) * barWidth);

        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()) * refLink.GetGameCamera().getZoomLevel());
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()) * refLink.GetGameCamera().getZoomLevel());

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
}