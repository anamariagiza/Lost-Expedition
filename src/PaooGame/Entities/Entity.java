package PaooGame.Entities;

import PaooGame.RefLinks;
import java.awt.*;

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

    // Getters
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