package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/**
 * @class Animation
 * @brief Gestioneaza o secventa de imagini pentru a crea o animatie.
 * Aceasta clasa primeste un set de imagini (cadre) si o viteza, si se ocupa
 * de logica de a afisa cadrul corect la momentul potrivit, creand iluzia
 * de miscare. Poate gestiona atat animatii care se repeta (in bucla), cat
 * si animatii care ruleaza o singura data.
 */
public class Animation {

    /** Viteza animatiei (durata in milisecunde intre cadre).*/
    private final int speed;
    /** Indexul cadrului curent din secventa de animatie.*/
    private int index;
    /** Variabile pentru a gestiona temporizarea schimbarii cadrelor.*/
    private long lastTime, timer;
    /** Vectorul de imagini (cadre) care compun animatia.*/
    private final BufferedImage[] frames;
    /** Flag ce indica daca animatia se reia de la capat (true) sau se opreste la final (false).*/
    private final boolean loops;

    /**
     * @brief Constructor de convenienta pentru animatii care ruleaza in bucla.
     * @param speed Viteza animatiei in milisecunde.
     * @param frames Vectorul de imagini (cadre).
     */
    public Animation(int speed, BufferedImage[] frames) {
        this(speed, frames, true);
    }

    /**
     * @brief Constructor principal pentru clasa Animation.
     * @param speed Viteza animatiei in milisecunde.
     * @param frames Vectorul de imagini (cadre).
     * @param loops True daca animatia trebuie sa se repete, false altfel.
     */
    public Animation(int speed, BufferedImage[] frames, boolean loops) {
        this.speed = speed;
        this.frames = frames;
        this.loops = loops;
        this.index = 0;
        this.timer = 0;
        this.lastTime = System.currentTimeMillis();
    }

    /**
     * @brief Actualizeaza logica animatiei. Trebuie apelata in fiecare cadru al jocului.
     */
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
                    index = 0; // Revine la primul cadru pentru a crea o bucla.
                } else {
                    index = frames.length - 1;
                }
            }
        }
    }

    /**
     * @brief Reseteaza animatia la primul cadru.
     */
    public void reset() {
        index = 0;
        timer = 0;
        lastTime = System.currentTimeMillis();
    }

    /**
     * @brief Verifica daca o animatie care nu este in bucla a ajuns la final.
     * @return True daca animatia s-a terminat, false altfel.
     */
    public boolean isFinished() {
        return !loops && index >= frames.length - 1;
    }

    /**
     * @brief Returneaza imaginea (cadrul) curenta a animatiei.
     * @return Un obiect BufferedImage reprezentand cadrul curent.
     */
    public BufferedImage getCurrentFrame() {
        if (frames == null || frames.length == 0) {
            return null;
        }
        return frames[index];
    }

    /**
     * @brief Returneaza indexul cadrului curent.
     * @return Indexul numeric al cadrului.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @brief Returneaza numarul total de cadre din animatie.
     * @return Numarul de cadre.
     */
    public int getFramesLength() {
        return (frames != null) ? frames.length : 0;
    }
}