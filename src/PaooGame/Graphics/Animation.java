package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/*! \class public class Animation
    \brief Implementeaza o animatie, gestionand o secventa de cadre.
 */
public class Animation {

    private int speed;          /*!< Viteza animatiei (milisecunde pe cadru).*/
    private int index;          /*!< Indexul curent al cadrului animatiei.*/
    private long lastTime;      /*!< Timpul la ultima actualizare a animatiei.*/
    private long timer;         /*!< Contor pentru timpul trecut intre cadre.*/
    public BufferedImage[] frames; /*!< Tablou cu toate cadrele animatiei.*/

    /*! \fn public Animation(int speed, BufferedImage[] frames)
        \brief Constructorul de initializare al clasei Animation.

        \param speed Viteza in milisecunde pentru fiecare cadru.
        \param frames Tabloul de imagini (cadre) ale animatiei.
     */
    public Animation(int speed, BufferedImage[] frames) {
        this.speed = speed;
        this.frames = frames;
        index = 0;
        timer = 0;
        lastTime = System.currentTimeMillis();
    }

    /*! \fn public void Update()
        \brief Actualizeaza animatia, schimband cadrul curent daca a trecut suficient timp.
     */
    public void Update() {
        if (frames == null || frames.length == 0) {
            return; // Nu actualiza daca nu sunt cadre
        }

        timer += System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();

        if (timer > speed) {
            index++;
            timer = 0;
            if (index >= frames.length) {
                index = 0; // Revine la primul cadru cand se termina secventa
            }
        }
    }

    /*! \fn public BufferedImage getCurrentFrame()
        \brief Returneaza cadrul curent al animatiei.
     */
    public BufferedImage getCurrentFrame() {
        if (frames == null || frames.length == 0) {
            return null; // Niciun cadru disponibil
        }
        return frames[index];
    }

    // --- Noi metode adaugate pentru a respecta incapsularea ---

    /*! \fn public void reset()
        \brief Reseteaza animatia la primul cadru si timer-ul.
     */
    public void reset() {
        index = 0;
        timer = 0;
        lastTime = System.currentTimeMillis();
    }

    /*! \fn public int getIndex()
        \brief Returneaza indexul cadrului curent.
     */
    public int getIndex() {
        return index;
    }

    /*! \fn public boolean isFinished()
        \brief Verifica daca animatia a ajuns la ultimul cadru (si timer-ul s-a resetat).
        Utila pentru animatii one-shot (atac, saritura).
     */
    public boolean isFinished() {
        return index == frames.length - 1 && timer <= 0;
    }

    /*! \fn public int getFramesLength()
        \brief Returneaza numarul total de cadre ale animatiei.
     */
    public int getFramesLength() {
        return (frames != null) ? frames.length : 0;
    }
}