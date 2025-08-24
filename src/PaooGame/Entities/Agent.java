package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.Tiles.Tile;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @class Agent
 * @brief Implementeaza antagonistul principal al jocului, Agentul lui Magnus Voss.
 * Aceasta clasa extinde Entity si defineste un inamic cu comportamente specifice,
 * cum ar fi patrularea intre doua puncte, urmarirea jucatorului si atacarea acestuia.
 * Agentul are propria sa viata, daune si un set complet de animatii.
 */
public class Agent extends Entity {

    /** Constante pentru dimensiunile si vitezele implicite ale agentului.*/
    private static final int DEFAULT_AGENT_WIDTH = 64;
    private static final int DEFAULT_AGENT_HEIGHT = 64;
    private static final float DEFAULT_SPEED = 0.8f;
    private static final float CHASE_SPEED = 1.5f;

    /** Animatiile agentului pentru diverse stari si directii.*/
    private final Animation animIdleDown;
    private final Animation animIdleUp;
    private final Animation animIdleLeft;
    private final Animation animIdleRight;
    private final Animation animWalkDown;
    private final Animation animWalkUp;
    private final Animation animWalkLeft;
    private final Animation animWalkRight;
    private final Animation animRunDown;
    private final Animation animRunUp;
    private final Animation animRunLeft;
    private final Animation animRunRight;
    private final Animation animHurt;
    private final Animation animHalfslashUp;
    private final Animation animHalfslashDown;
    private final Animation animHalfslashLeft;
    private final Animation animHalfslashRight;
    private Animation activeAnimation;

    /** Atribute de stare si miscare ce definesc comportamentul curent al agentului.*/
    private final float speed;
    private final float patrolStartX;
    private final float patrolEndX;
    private boolean movingRight = true;
    private final boolean isPatrolling;
    private boolean isChasing = false;
    private boolean isAttacking = false;
    private boolean isDefeated = false;
    private Direction lastDirection;

    /** Atribute de lupta ale agentului.*/
    private float xMove, yMove;
    private int health;
    private final int maxHealth = 100;

    /** Cooldowns pentru a preveni actiuni la fiecare cadru si a asigura un ritm de joc echilibrat.*/
    private long lastDamageTakenTime = 0;
    private long lastAttackTime = 0;

    /** Enum intern pentru a gestiona directiile de miscare si animatiile corespunzatoare.*/
    private enum Direction { UP, DOWN, LEFT, RIGHT }

    /**
     * @brief Constructorul clasei Agent.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X initiala a agentului.
     * @param y Coordonata Y initiala a agentului.
     * @param patrolStartX Limita stanga a patrularii (in pixeli).
     * @param patrolEndX Limita dreapta a patrularii (in pixeli).
     * @param isPatrolling Flag initial pentru starea de patrulare.
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

        // Initializarea tuturor animatiilor, similar cu Player
        int animSpeed = 150;
        int attackSpeed = 100;

        // Animatii care ruleaza an bucla
        animIdleDown = new Animation(animSpeed * 2, Assets.agentIdleDown);
        animIdleUp = new Animation(animSpeed * 2, Assets.agentIdleUp);
        animIdleLeft = new Animation(animSpeed * 2, Assets.agentIdleLeft);
        animIdleRight = new Animation(animSpeed * 2, Assets.agentIdleRight);
        animWalkDown = new Animation(animSpeed, Assets.agentDown);
        animWalkUp = new Animation(animSpeed, Assets.agentUp);
        animWalkLeft = new Animation(animSpeed, Assets.agentLeft);
        animWalkRight = new Animation(animSpeed, Assets.agentRight);
        animRunDown = new Animation(100, Assets.agentRunDown);
        animRunUp = new Animation(100, Assets.agentRunUp);
        animRunLeft = new Animation(100, Assets.agentRunLeft);
        animRunRight = new Animation(100, Assets.agentRunRight);


        animHalfslashUp = new Animation(attackSpeed, Assets.agentHalfslashUp, false);
        animHalfslashDown = new Animation(attackSpeed, Assets.agentHalfslashDown, false);
        animHalfslashLeft = new Animation(attackSpeed, Assets.agentHalfslashLeft, false);
        animHalfslashRight = new Animation(attackSpeed, Assets.agentHalfslashRight, false);
        animHurt = new Animation(animSpeed, Assets.agentHurt, false);

        activeAnimation = animIdleDown;
        lastDirection = Direction.DOWN;
    }

    /**
     * @brief Actualizeaza starea agentului in fiecare cadru.
     * Aceasta metoda este apelata in bucla principala a jocului si gestioneaza logica
     * de comportament a agentului (stari, miscare, interactiuni).
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

    /**
     * @brief Deseneaza agentul pe ecran.
     * @param g Contextul grafic in care se va desena.
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

    /**
     * @brief Aplica daune agentului si actualizeaza starea acestuia.
     * @param amount Cantitatea de viata de scazut.
     */
    public void takeDamage(int amount) {
        if (isDefeated) return;
        health -= amount;
        System.out.println("Agent a luat " + amount + " daune. Viata ramasa: " + health);
        if (health <= 0) {
            health = 0;
            isDefeated = true;
            activeAnimation = animHurt;
            // Adăugăm o verificare pentru a preveni eroarea NullPointerException
            if (activeAnimation != null) {
                activeAnimation.reset();
            }
        }
    }

    /**
     * @brief Seteaza modul de urmarire al agentului.
     * @param chasing True pentru a activa modul de urmarire, false pentru a-l dezactiva.
     */
    public void setChaseMode(boolean chasing) {
        this.isChasing = chasing;
    }

    /**
     * @brief Misca agentul si gestioneaza coliziunile cu mediul inconjurator.
     * Verifica coliziunile pe fiecare axa separat pentru a preveni blocarea in colturi.
     * @param xAmt Valoarea miscarii pe axa X.
     * @param yAmt Valoarea miscarii pe axa Y.
     */
    private void move(float xAmt, float yAmt) {
        if (xAmt != 0) {
            float newX = x + xAmt;
            Rectangle proposedBoundsX = new Rectangle((int) newX, (int) y, width, height);
            boolean collision = false;
            int tx = (int) ((xAmt > 0 ? newX + width - 1 : newX) / Tile.TILE_WIDTH);
            int ty_top = (int) (y / Tile.TILE_HEIGHT);
            int ty_bottom = (int) ((y + height - 1) / Tile.TILE_HEIGHT);
            if (refLink.GetMap().GetTile(tx, ty_top).IsSolid() || refLink.GetMap().GetTile(tx, ty_bottom).IsSolid()) {
                collision = true;
            }

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
            if (refLink.GetMap().GetTile(tx_left, ty).IsSolid() || refLink.GetMap().GetTile(tx_right, ty).IsSolid()) {
                collision = true;
            }

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

    /**
     * @brief Implementeaza logica de miscare a agentului in modul de patrulare.
     */
    private void moveAgent() {
        // Resetare xMove la anceput pentru a evita comportamentele residuale
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

        // Miscare doar daca xMove nu este 0
        if (xMove != 0) {
            move(xMove, 0);
        }
    }

    /**
     * @brief Implementeaza logica de urmarire a jucatorului.
     */
    private void chasePlayer() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        float playerCenterX = player.GetX() + (float) player.GetWidth() / 2;
        float playerCenterY = player.GetY() + (float) player.GetHeight() / 2;
        float agentCenterX = x + (float) width / 2;
        float agentCenterY = y + (float) height / 2;

        float dx = playerCenterX - agentCenterX;
        float dy = playerCenterY - agentCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        float currentSpeed = player.isRunning() ? CHASE_SPEED : DEFAULT_SPEED;

        xMove = 0;
        yMove = 0;
        if (distance > 10) {
            // Resetare variabile de miscare pentru a evita comportamentele residuale

            xMove = (dx / distance) * currentSpeed;
            yMove = (dy / distance) * currentSpeed;

            // Determinare directie principala pentru animatie
            if (Math.abs(dx) > Math.abs(dy)) {
                lastDirection = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
            } else {
                lastDirection = (dy > 0) ? Direction.DOWN : Direction.UP;
            }

            // Setare animatie corecta
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
            // Resetare miscare cand este aproape de jucator
            updateIdleAnimationBasedOnLastDirection();
        }
    }

    /**
     * @brief Verifica coliziunea cu jucatorul si initiaza un atac.
     */
    private void checkPlayerCollision() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        long ATTACK_COOLDOWN_MS = 1000;
        if (this.bounds.intersects(player.GetBounds()) && !isAttacking() && System.currentTimeMillis() - lastAttackTime > ATTACK_COOLDOWN_MS) {
            int damage = 10;
            player.takeDamage(damage);
            isAttacking = true;

            // MODIFICARE: Folosim animatia halfslash an functie de directie
            switch(lastDirection) {
                case UP: activeAnimation = animHalfslashUp; break;
                case LEFT: activeAnimation = animHalfslashLeft; break;
                case RIGHT: activeAnimation = animHalfslashRight; break;
                default: activeAnimation = animHalfslashDown; break;
            }

            if (activeAnimation != null) {
                activeAnimation.reset();
            }
            lastAttackTime = System.currentTimeMillis();
        }
    }

    /**
     * @brief Verifica coliziunea cu zona de atac a jucatorului.
     */
    private void checkPlayerAttackCollision() {
        Player player = refLink.GetPlayer();
        long DAMAGE_COOLDOWN_MS = 500;
        if (player == null || System.currentTimeMillis() - lastDamageTakenTime < DAMAGE_COOLDOWN_MS) return;

        Rectangle playerAttackBounds = player.getAttackBounds();
        if (playerAttackBounds != null && this.bounds.intersects(playerAttackBounds)) {
            takeDamage(player.getAttackDamage());
            lastDamageTakenTime = System.currentTimeMillis();
        }
    }

    /**
     * @brief Seteaza animatia de idle corespunzatoare ultimei directii de miscare.
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
     * @brief Deseneaza bara de viata a agentului deasupra acestuia.
     * @param g Contextul grafic in care se va desena.
     */
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

    /**
     * @brief Verifica daca agentul este in timpul unei animatii de atac.
     * @return True daca ataca, false altfel.
     */
    public boolean isAttacking() {
        return isAttacking;
    }

    /**
     * @brief Returneaza viata curenta a agentului.
     * @return Viata curenta.
     */
    public int getHealth() {
        return health;
    }
}