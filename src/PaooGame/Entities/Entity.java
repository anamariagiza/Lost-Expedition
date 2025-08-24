package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import java.awt.*;

/**
 * @class Entity
 * @brief Clasa de baza abstracta pentru toate entitatile din joc.
 * Aceasta clasa defineste proprietatile si metodele comune pentru toate obiectele
 * care nu sunt dale (tiles), cum ar fi jucatorul, inamicii, obiectele interactive etc.
 * Ofera o fundatie pentru pozitie, dimensiune, coliziuni si interactiuni.
 */
public abstract class Entity {
    /** Referinta catre obiectul RefLinks pentru acces facil la componentele jocului.*/
    protected RefLinks refLink;
    /** Coordonatele X si Y ale pozitiei entitatii in lumea jocului (in pixeli).*/
    protected float x, y;
    /** Latimea si inaltimea entitatii (in pixeli).*/
    protected int width, height;
    /** Dreptunghiul de coliziune al entitatii.*/
    protected Rectangle bounds;

    /**
     * @brief Constructorul de initializare al clasei Entity.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X initiala.
     * @param y Coordonata Y initiala.
     * @param width Latimea entitatii.
     * @param height Inaltimea entitatii.
     */
    public Entity(RefLinks refLink, float x, float y, int width, int height) {
        this.refLink = refLink;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle((int)x, (int)y, width, height);
    }

    /**
     * @brief Metoda abstracta pentru actualizarea starii entitatii in fiecare cadru.
     * Trebuie implementata de toate subclasele pentru a defini logica specifica.
     */
    public abstract void Update();

    /**
     * @brief Metoda abstracta pentru desenarea entitatii pe ecran.
     * Trebuie implementata de toate subclasele.
     * @param g Contextul grafic in care se va realiza desenarea.
     */
    public abstract void Draw(Graphics g);

    /**
     * @brief Deseneaza un pop-up de interactiune ("E") deasupra entitatii.
     * Daca jucatorul se afla la o distanta suficient de mica, un indicator
     * vizual este afisat pentru a semnala ca se poate interactiona cu entitatea.
     * @param g Contextul grafic in care se va desena.
     */
    public void drawInteractionPopup(Graphics g) {
        if(refLink.GetPlayer() == null) return;

        double distance = Math.sqrt(Math.pow(refLink.GetPlayer().GetX() - x, 2) + Math.pow(refLink.GetPlayer().GetY() - y, 2));

        if (distance <= 100) {
            int popupWidth = 32;
            int popupHeight = 32;

            // Coordonatele pe ecran
            int drawX = (int)((x - refLink.GetGameCamera().getxOffset()) + (float) width /2 - (float) popupWidth /2);
            int drawY = (int)((y - refLink.GetGameCamera().getyOffset()) - popupHeight - 10);

            // Desenam fundalul semi-transparent
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(drawX, drawY, popupWidth, popupHeight);

            if (Assets.popupImage != null) {
                g.drawImage(Assets.popupImage, drawX, drawY, popupWidth, popupHeight, null);
            } else {
                g.setColor(Color.BLUE);
                g.fillRect(drawX, drawY, popupWidth, popupHeight);
            }
        }
    }

    /**
     * @brief Returneaza coordonata X a entitatii.
     */
    public float GetX() {
        return x;
    }

    /**
     * @brief Returneaza coordonata Y a entitatii.
     */
    public float GetY() {
        return y;
    }

    /**
     * @brief Returneaza latimea entitatii.
     */
    public int GetWidth() {
        return width;
    }

    /**
     * @brief Returneaza inaltimea entitatii.
     */
    public int GetHeight() {
        return height;
    }

    /**
     * @brief Returneaza dreptunghiul de coliziune al entitatii.
     */
    public Rectangle GetBounds() {
        return bounds;
    }

    /**
     * @brief Seteaza noua coordonata X si actualizeaza dreptunghiul de coliziune.
     * @param x Noua coordonata X.
     */
    public void SetX(float x) {
        this.x = x;
        this.bounds.x = (int) x;
    }

    /**
     * @brief Seteaza noua coordonata Y si actualizeaza dreptunghiul de coliziune.
     * @param y Noua coordonata Y.
     */
    public void SetY(float y) {
        this.y = y;
        this.bounds.y = (int) y;
    }

    /**
     * @brief Seteaza pozitia completa a entitatii si actualizeaza dreptunghiul de coliziune.
     * @param x Noua coordonata X.
     * @param y Noua coordonata Y.
     */
    public void SetPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds.setLocation((int) x, (int) y);
    }
}