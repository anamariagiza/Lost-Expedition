package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import java.awt.*;
import java.awt.image.BufferedImage;

/*!
 * \class public abstract class Entity
 * \brief Clasa de baza abstracta pentru toate entitatile din joc (jucator, inamici, obiecte interactive).
 * Ofera proprietati si metode comune precum pozitia, dimensiunea si bounding box-ul.
 */
public abstract class Entity {

    protected RefLinks refLink;
    protected float x, y;
    protected int width, height;
    protected Rectangle bounds;

    /*!
     * \fn public Entity(RefLinks refLink, float x, float y, int width, int height)
     * \brief Constructorul de initializare al clasei Entity.
     * \param refLink Referinta catre obiectul RefLinks.
     * \param x Coordonata X initiala.
     * \param y Coordonata Y initiala.
     * \param width Latimea entitatii.
     * \param height Inaltimea entitatii.
     */
    public Entity(RefLinks refLink, float x, float y, int width, int height) {
        this.refLink = refLink;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle((int)x, (int)y, width, height);
    }

    /*!
     * \fn public abstract void Update()
     * \brief Actualizeaza starea entitatii in fiecare cadru de joc.
     */
    public abstract void Update();

    /*!
     * \fn public abstract void Draw(Graphics g)
     * \brief Deseneaza entitatea pe ecran.
     * \param g Contextul grafic in care sa se realizeze desenarea.
     */
    public abstract void Draw(Graphics g);

    /*!
     * \fn public void drawInteractionPopup(Graphics g)
     * \brief Deseneaza pop-up-ul cu tasta E deasupra entitatii.
     */
    public void drawInteractionPopup(Graphics g) {
        if(refLink.GetPlayer() == null) return;

        double distance = Math.sqrt(Math.pow(refLink.GetPlayer().GetX() - x, 2) + Math.pow(refLink.GetPlayer().GetY() - y, 2));

        if (distance <= 100) {
            int popupWidth = 32;
            int popupHeight = 32;

            // Coordonatele pe ecran
            int drawX = (int)((x - refLink.GetGameCamera().getxOffset()) + width/2 - popupWidth/2);
            int drawY = (int)((y - refLink.GetGameCamera().getyOffset()) - popupHeight - 10);

            // Desenăm fundalul semi-transparent
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

    // Getters și Setters
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

    public Rectangle GetBounds() {
        return bounds;
    }

    // Setters
    public void SetX(float x) {
        this.x = x;
        this.bounds.x = (int) x;
    }

    public void SetY(float y) {
        this.y = y;
        this.bounds.y = (int) y;
    }

    /*!
     * \fn public void SetPosition(float x, float y)
     * \brief Seteaza pozitia entitatii si actualizeaza bounding box-ul.
     * \param x Coordonata X noua.
     * \param y Coordonata Y noua.
     */
    public void SetPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds.setLocation((int) x, (int) y);
    }
}