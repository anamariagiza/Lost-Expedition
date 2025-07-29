package PaooGame.Entities;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.Tiles.Tile;
import PaooGame.Camera.GameCamera;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/*! \class public class Player
    \brief Implementeaza notiunea de erou/jucator (player) in joc.
 */
public class Player {
    private Game game;
    private float x, y;
    private int width, height; // Dimensiunile jucatorului (originale, nescalate)
    private float walkSpeed = 3.0f;
    private float runSpeed = 6.0f;
    private float currentSpeed;

    // Obiecte de animatie pentru mers (Walk)
    private Animation animDown, animUp, animLeft, animRight;

    // NOU: Animații pentru Idle (specifice direcției)
    private Animation animIdleDown, animIdleUp, animIdleLeft, animIdleRight;

    // NOU: Animații pentru Alergat (specifice direcției)
    private Animation animRunDown, animRunUp, animRunLeft, animRunRight;

    // NOU: Animații pentru Sarit (specifice direcției)
    private Animation animJumpDown, animJumpUp, animJumpLeft, animJumpRight;

    // Alte animații de acțiune (declarate acum corect)
    private Animation animClimb;
    private Animation animHurt;
    private Animation animShoot;
    private Animation animBackslash;
    private Animation animHalfslash;
    private Animation animEmote;
    private Animation animCombatIdle;
    private Animation animThrust;
    private Animation animSit;
    private Animation animSpellcast;
    private Animation animSlash;

    private Animation activeAnimation; // Animația curentă afișată

    // Flaguri de stare
    private boolean isMoving;
    private boolean isRunning;
    private boolean isJumping;
    private boolean isAttacking;
    private boolean isHurt;
    private boolean isClimbing;
    private boolean isSitting;
    private boolean isEmoting;
    private boolean isCombatIdle;

    // Flaguri specifice pentru atacuri/acțiuni
    private boolean isThrusting;
    private boolean isHalfslashing;
    private boolean isBackslashing;
    private boolean isShooting;
    private boolean isSpellcasting;
    private boolean isSlashing;

    // NOU: Variabila pentru a retine ultima directie de miscare
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    private Direction lastDirection = Direction.DOWN; // Incepe cu fata (jos)

    private Rectangle bounds; // Dreptunghiul de coliziune

    /*! \fn public Player(Game game, float x, float y)
        \brief Constructorul de initializare al clasei Player.

        \param game Referinta catre obiectul Game.
        \param x Coordonata x a pozitiei initiale a jucatorului.
        \param y Coordonata y a pozitiei initiale a jucatorului.
     */
    public Player(Game game, float x, float y) {
        this.x = x;
        this.y = y;
        this.game = game;

        this.width = Assets.PLAYER_FRAME_WIDTH;
        this.height = Assets.PLAYER_FRAME_HEIGHT;

        currentSpeed = walkSpeed;

        int animationSpeed = 100; // Viteza de baza pentru mers
        int runAnimationSpeed = 70;
        int jumpAnimationSpeed = 120;
        int attackAnimationSpeed = 80;
        int hurtAnimationSpeed = 150;
        int climbAnimationSpeed = 100;
        int idleCombatSpeed = 200;
        int sitAnimationSpeed = 150;
        int emoteAnimationSpeed = 100;


        // Initializeaza animatiile de mers (Walk)
        animDown = safeAnimation(Assets.playerDown, animationSpeed);
        animUp = safeAnimation(Assets.playerUp, animationSpeed);
        animLeft = safeAnimation(Assets.playerLeft, animationSpeed);
        animRight = safeAnimation(Assets.playerRight, animationSpeed);

        // NOU: Initializare animatii Idle (specifice direcției)
        // Folosim cadrele statice din Assets.playerIdleDown etc.
        animIdleDown = safeAnimation(Assets.playerIdleDown, animationSpeed * 2); // Idle cu fața
        animIdleUp = safeAnimation(Assets.playerIdleUp, animationSpeed * 2);     // Idle cu spatele
        animIdleLeft = safeAnimation(Assets.playerIdleLeft, animationSpeed * 2);   // Idle spre stânga
        animIdleRight = safeAnimation(Assets.playerIdleRight, animationSpeed * 2); // Idle spre dreapta

        // NOU: Initializare animatii Alergat (specifice direcției)
        // Acum folosesc Assets.playerRunDown etc. care trebuie sa fie definite in Assets.java
        animRunDown = safeAnimation(Assets.playerRunDown, runAnimationSpeed);
        animRunUp = safeAnimation(Assets.playerRunUp, runAnimationSpeed);
        animRunLeft = safeAnimation(Assets.playerRunLeft, runAnimationSpeed);
        animRunRight = safeAnimation(Assets.playerRunRight, runAnimationSpeed);

        // NOU: Initializare animatii Sarit (specifice direcției)
        // Acum folosesc Assets.playerJumpDown etc. care trebuie sa fie definite in Assets.java
        animJumpDown = safeAnimation(Assets.playerJumpDown, jumpAnimationSpeed);
        animJumpUp = safeAnimation(Assets.playerJumpUp, jumpAnimationSpeed);
        animJumpLeft = safeAnimation(Assets.playerJumpLeft, jumpAnimationSpeed);
        animJumpRight = safeAnimation(Assets.playerJumpRight, jumpAnimationSpeed);


        // Initializare alte animatii de actiune
        animClimb = safeAnimation(Assets.playerClimb, climbAnimationSpeed);
        animHurt = safeAnimation(Assets.playerHurt, hurtAnimationSpeed);
        animShoot = safeAnimation(Assets.playerShoot, attackAnimationSpeed);
        animBackslash = safeAnimation(Assets.playerBackslash, attackAnimationSpeed);
        animHalfslash = safeAnimation(Assets.playerHalfslash, attackAnimationSpeed);
        animEmote = safeAnimation(Assets.playerEmote, emoteAnimationSpeed);
        animCombatIdle = safeAnimation(Assets.playerCombatIdle, idleCombatSpeed);
        animThrust = safeAnimation(Assets.playerThrust, attackAnimationSpeed);
        animSit = safeAnimation(Assets.playerSit, sitAnimationSpeed);
        animSpellcast = safeAnimation(Assets.playerSpellcast, attackAnimationSpeed);
        animSlash = safeAnimation(Assets.playerSlash, attackAnimationSpeed);


        // Setare animație inițială la idle-down
        activeAnimation = animIdleDown;
        lastDirection = Direction.DOWN; // Asigură-te că începe privind în jos

        // Resetare flaguri de stare
        isMoving = false;
        isRunning = false;
        isJumping = false;
        isAttacking = false;
        isHurt = false;
        isClimbing = false;
        isSitting = false;
        isEmoting = false;
        isCombatIdle = false;
        isThrusting = false;
        isHalfslashing = false;
        isBackslashing = false;
        isShooting = false;
        isSpellcasting = false;
        isSlashing = false;


        bounds = new Rectangle((int)x, (int)y, width, height);
    }

    /*! \fn public void Update()
        \brief Actualizeaza pozitia si imaginea jucatorului.
     */
    public void Update() { // Am eliminat @Override deoarece clasa Player nu extinde State sau o clasa cu metoda Update
        if (game == null || game.GetKeyManager() == null) return;

        GetInput();
        Move();

        // Centreaza camera pe player dupa actualizarea pozitiei
        game.GetRefLinks().GetGameCamera().centerOnEntity(this);

        // Actualizeaza DOAR animația curentă.
        // Daca jucatorul este intr-o stare de "actiune" (atac, lovit, sare), nu se schimba animatia pe baza miscarii
        if (!isAttacking && !isHurt && !isJumping && !isClimbing && !isSitting && !isEmoting && !isCombatIdle &&
                !isThrusting && !isHalfslashing && !isBackslashing && !isShooting && !isSpellcasting && !isSlashing) {
            updateMovementAnimation();
        } else {
            // Asiguram ca animatiile de actiune sunt actualizate
            activeAnimation.Update();
            // Resetam flagurile de stare odata ce animatia s-a terminat
            if (activeAnimation.isFinished()) {
                if (isAttacking) isAttacking = false;
                if (isHurt) isHurt = false;
                if (isJumping) isJumping = false; // NOU: Resetam isJumping doar dupa ce animatia de saritura s-a terminat
                if (isClimbing) isClimbing = false;
                if (isSitting) isSitting = false;
                if (isEmoting) isEmoting = false;
                if (isCombatIdle) isCombatIdle = false;
                if (isThrusting) isThrusting = false;
                if (isHalfslashing) isHalfslashing = false;
                if (isBackslashing) isBackslashing = false;
                if (isShooting) isShooting = false;
                if (isSpellcasting) isSpellcasting = false;
                if (isSlashing) isSlashing = false;

                // Revine la idle dupa terminarea animatiei de actiune, mentinand ultima directie
                updateIdleAnimationBasedOnLastDirection();
            }
        }
    }

    /*! \fn private void GetInput()
        \brief Citeste input-ul de la tastatura si seteaza directia de miscare si animatia corespunzatoare.
     */
    private void GetInput() {
        float moveX = 0;
        float moveY = 0;
        isMoving = false;
        isRunning = game.GetKeyManager().keys[KeyEvent.VK_SHIFT];
        currentSpeed = isRunning ? runSpeed : walkSpeed;

        // Resetam starile de actiune (exceptie: isJumping, care se reseteaza la terminarea animatiei)
        isAttacking = false;
        isHurt = false;
        isClimbing = false;
        isSitting = false;
        isEmoting = false;
        isCombatIdle = false;
        isThrusting = false;
        isHalfslashing = false;
        isBackslashing = false;
        isShooting = false;
        isSpellcasting = false;
        isSlashing = false;


        // Mișcarea orizontală și verticală este PERMISĂ în timpul săriturii.
        // Blochează acțiunile (atac, hurt etc.) dar nu mișcarea simplă.
        if (!isAttacking && !isHurt && !isClimbing && !isSitting && !isEmoting && !isCombatIdle &&
                !isThrusting && !isHalfslashing && !isBackslashing && !isShooting && !isSpellcasting && !isSlashing) {
            if (game.GetKeyManager().up) {
                moveY = -currentSpeed;
                isMoving = true;
                lastDirection = Direction.UP;
            }
            if (game.GetKeyManager().down) {
                moveY = currentSpeed;
                isMoving = true;
                lastDirection = Direction.DOWN;
            }
            if (game.GetKeyManager().left) {
                moveX = -currentSpeed;
                isMoving = true;
                lastDirection = Direction.LEFT;
            }
            if (game.GetKeyManager().right) {
                moveX = currentSpeed;
                isMoving = true;
                lastDirection = Direction.RIGHT;
            }
        }

        // Acțiuni (prioritare față de mișcare simplă)
        if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_SPACE) && !isJumping) {
            isJumping = true;
            // NOU: Alege animația de săritură în funcție de ultima direcție
            switch(lastDirection) {
                case UP:    activeAnimation = animJumpUp;    break;
                case DOWN:  activeAnimation = animJumpDown;  break;
                case LEFT:  activeAnimation = animJumpLeft;  break;
                case RIGHT: activeAnimation = animJumpRight; break;
                default:    activeAnimation = animJumpDown;  break; // Fallback dacă nu ai animatiiJumpX
            }
            activeAnimation.reset();
        } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_J)) {
            isAttacking = true; isThrusting = true;
            activeAnimation = animThrust;
            activeAnimation.reset();
        } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_K)) {
            isAttacking = true; isHalfslashing = true;
            activeAnimation = animHalfslash;
            activeAnimation.reset();
        } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_L)) {
            isAttacking = true; isBackslashing = true;
            activeAnimation = animBackslash;
            activeAnimation.reset();
        } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_U)) {
            isShooting = true;
            activeAnimation = animShoot;
            activeAnimation.reset();
        } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_I)) {
            isSpellcasting = true;
            activeAnimation = animSpellcast;
            activeAnimation.reset();
        } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_C)) {
            isClimbing = true;
            activeAnimation = animClimb;
            activeAnimation.reset();
        } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_X)) {
            isSitting = true;
            activeAnimation = animSit;
            activeAnimation.reset();
        } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
            isEmoting = true;
            activeAnimation = animEmote;
            activeAnimation.reset();
        } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_B)) {
            isCombatIdle = true;
            activeAnimation = animCombatIdle;
            activeAnimation.reset();
        } else if (game.GetKeyManager().isKeyJustPressed(KeyEvent.VK_SLASH)) {
            isAttacking = true; isSlashing = true;
            activeAnimation = animSlash;
            activeAnimation.reset();
        }

        // Aplică mișcarea (Orizontală și Verticală - nu mai e blocată de isJumping)
        x += moveX;
        y += moveY;

        bounds.setLocation((int)x, (int)y);
    }

    /*! \fn private void updateMovementAnimation()
        \brief Actualizeaza animatia de miscare/idle.
     */
    private void updateMovementAnimation() {
        if (isMoving) {
            if (isRunning) {
                // NOU: Alege animația de alergat în funcție de direcție
                switch(lastDirection) {
                    case UP:    activeAnimation = animRunUp;    break;
                    case DOWN:  activeAnimation = animRunDown;  break;
                    case LEFT:  activeAnimation = animRunLeft;  break;
                    case RIGHT: activeAnimation = animRunRight; break;
                    // Fallback-ul ar trebui să fie handle-uit de safeAnimation, dar poți adăuga un default aici
                    // default:    activeAnimation = animRunDown;  break;
                }
            } else { // Când merge (walk)
                // Alege animația de mers în funcție de direcție
                switch(lastDirection) {
                    case UP:    activeAnimation = animUp;    break;
                    case DOWN:  activeAnimation = animDown;  break;
                    case LEFT:  activeAnimation = animLeft;  break;
                    case RIGHT: activeAnimation = animRight; break;
                }
            }
        } else { // Nu se misca
            // NOU: Când nu se mișcă, setează animația de idle bazată pe ultima direcție
            updateIdleAnimationBasedOnLastDirection();
        }
        activeAnimation.Update();
    }

    /*! \fn private void updateIdleAnimationBasedOnLastDirection()
        \brief Seteaza animatia de idle bazata pe ultima directie de miscare.
     */
    private void updateIdleAnimationBasedOnLastDirection() {
        // NOU: Alege animația de idle bazată pe ultima direcție
        switch(lastDirection) {
            case UP:    activeAnimation = animIdleUp;    break;
            case DOWN:  activeAnimation = animIdleDown;  break;
            case LEFT:  activeAnimation = animIdleLeft;  break;
            case RIGHT: activeAnimation = animIdleRight; break;
            default:    activeAnimation = animIdleDown;  break; // Fallback
        }
        activeAnimation.reset(); // Resetează animația la primul cadru pentru a simula o poziție de idle statică
    }

    /*! \fn private void Move()
        \brief Aplica miscarea jucatorului si gestioneaza coliziunile cu marginile hartii.
     */
    private void Move() {
        PaooGame.Map.Map currentMap = game.GetRefLinks().GetMap();
        if (currentMap != null) {
            int mapWidthPx = currentMap.getWidth() * Tile.TILE_WIDTH;
            int mapHeightPx = currentMap.getHeight() * Tile.TILE_HEIGHT;

            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (x + width > mapWidthPx) x = mapWidthPx - width;
            if (y + height > mapHeightPx) y = mapHeightPx - height;
        }
    }

    /*! \fn public void Draw(Graphics g, GameCamera camera)
        \brief Deseneaza jucatorul pe ecran, ajustand pozitiile cu offset-ul camerei si zoom.
        Aceasta metoda este apelata din GameState.
     */
    public void Draw(Graphics g, GameCamera camera) {
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

    /*! \fn public BufferedImage GetActiveFrame()
        \brief Returneaza cadrul curent al animatiei active a jucatorului.
     */
    public BufferedImage GetActiveFrame() {
        return activeAnimation.getCurrentFrame();
    }

    // Metode Getter pentru pozitie si dimensiuni
    public float GetX() {
        return x;
    }

    public float GetY() {
        return y;
    }

    public int GetWidth() {
        return width;
    }

    public int GetHeight() {
        return height;
    }

    /*! \fn public void SetPosition(float x, float y)
        \brief Seteaza pozitia jucatorului.
        Utilizata la incarcarea unui nou nivel.
     */
    public void SetPosition(float x, float y) {
        this.x = x;
        this.y = y;
        bounds.setLocation((int)x, (int)y);
        // Resetam toate flagurile si setam animatia initiala la idle, bazata pe directia initiala
        isMoving = false;
        isRunning = false;
        isJumping = false;
        isAttacking = false;
        isHurt = false;
        isClimbing = false;
        isSitting = false;
        isEmoting = false;
        isCombatIdle = false;
        isThrusting = false;
        isHalfslashing = false;
        isBackslashing = false;
        isShooting = false;
        isSpellcasting = false;
        isSlashing = false;
        updateIdleAnimationBasedOnLastDirection(); // Seteaza animatia de idle corecta
    }

    // Metoda safeAnimation mutată aici, ca metodă privată a clasei Player
    private Animation safeAnimation(BufferedImage[] frames, int speed) {
        BufferedImage defaultFrame = new BufferedImage(Assets.PLAYER_FRAME_WIDTH, Assets.PLAYER_FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        return (frames != null && frames.length > 0) ? new Animation(speed, frames) : new Animation(100, new BufferedImage[]{defaultFrame});
    }
}