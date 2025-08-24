package PaooGame.Entities;

import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.RefLinks;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * @class Trap
 * @brief Implementeaza o entitate de tip capcana care poate provoca daune.
 * Aceasta clasa gestioneaza doua tipuri de capcane:
 * 1. Capcane dinamice/animate (ex: tepii din podea in Nivelul 3), care se pot
 * activa si dezactiva, avand o animatie.
 * 2. Capcane statice (ex: tepii din Nivelul 1), care au o singura imagine si
 * sunt mereu periculoase la contact (logica de daune este in GameState).
 */
public class Trap extends Entity {

    /// Constante pentru dimensiunile si daunele implicite ale capcanei.
    private static final int DEFAULT_TRAP_WIDTH = 48;
    private static final int DEFAULT_TRAP_HEIGHT = 48;
    private static final int DAMAGE_AMOUNT = 5;

    /// Atribute de stare care definesc comportamentul curent al capcanei.
    private boolean active = false;
    private boolean isAnimating = false;
    private final Animation activeAnimation;
    private final BufferedImage stillImage;
    private long activationTime = 0;

    /**
     * @brief Constructor pentru capcane animate (dinamice).
     * Folosit pentru capcanele care au o stare activa/inactiva si o animatie.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X a pozitiei capcanei.
     * @param y Coordonata Y a pozitiei capcanei.
     */
    public Trap(RefLinks refLink, float x, float y) {
        super(refLink, x, y, DEFAULT_TRAP_WIDTH, DEFAULT_TRAP_HEIGHT);
        this.bounds = new Rectangle((int) x, (int) y, width, height);
        this.activeAnimation = new Animation(150, Arrays.asList(Assets.trapActiveAnim).toArray(new BufferedImage[0]));
        this.stillImage = Assets.trapDisabled;
    }

    /**
     * @brief Constructor pentru capcane statice.
     * Folosit pentru capcanele care au o singura imagine si sunt mereu periculoase.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X a pozitiei capcanei.
     * @param y Coordonata Y a pozitiei capcanei.
     * @param image Imaginea statica a capcanei.
     */
    public Trap(RefLinks refLink, float x, float y, BufferedImage image) {
        super(refLink, x, y, DEFAULT_TRAP_WIDTH, DEFAULT_TRAP_HEIGHT);
        this.bounds = new Rectangle((int) x, (int) y, width, height);
        this.activeAnimation = null;
        this.stillImage = image;
    }

    /**
     * @brief Actualizeaza starea capcanei in fiecare cadru.
     * Ruleaza animatia de activare si gestioneaza timer-ul pentru
     * dezactivarea automata a capcanei dupa o anumita perioada.
     */
    @Override
    public void Update() {
        if (isAnimating && activeAnimation != null) {
            this.activeAnimation.Update();
            if (this.activeAnimation.isFinished()) {
                isAnimating = false;
            }
        }

        long ACTIVE_DURATION_MS = 2000;
        if (active && System.currentTimeMillis() - activationTime >= ACTIVE_DURATION_MS) {
            setActive(false);
            //System.out.println("DEBUG Trap: Capcana s-a dezactivat.");
        }
    }

    /**
     * @brief Deseneaza capcana pe ecran.
     * Afiseaza animatia daca este activa sau imaginea statica/inactiva in caz contrar.
     * @param g Contextul grafic in care se va desena.
     */
    @Override
    public void Draw(Graphics g) {
        //System.out.println("DEBUG: Se deseneaza capcana la pozitia (" + x + ", " + y + ")");

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

    /**
     * @brief Activeaza sau dezactiveaza capcana.
     * @param active True pentru a activa, false pentru a dezactiva.
     */
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

    /**
     * @brief Verifica daca capcana este activa.
     * @return True daca este activa, false altfel.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @brief Returneaza cantitatea de daune pe care o provoaca capcana.
     * @return Valoarea daunelor.
     */
    public int getDamage() {
        return DAMAGE_AMOUNT;
    }


}