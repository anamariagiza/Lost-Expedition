package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Tiles.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;

/*!
 * \class public class Trap extends Entity
 * \brief Implementeaza notiunea de capcana.
 * Capcanele sunt obstacole statice care provoaca daune jucatorului la contact.
 */
public class Trap extends Entity {

    private static final int DEFAULT_TRAP_WIDTH = 48;
    private static final int DEFAULT_TRAP_HEIGHT = 48;

    private BufferedImage trapImage;

    /*!
     * \fn public Trap(RefLinks refLink, float x, float y, BufferedImage image)
     * \brief Constructorul de initializare al clasei Trap.
     * \param refLink Referinta catre obiectul RefLinks.
     * \param x Coordonata X initiala.
     * \param y Coordonata Y initiala.
     * \param image Imaginea capcanei.
     */
    public Trap(RefLinks refLink, float x, float y, BufferedImage image) {
        super(refLink, x, y, DEFAULT_TRAP_WIDTH, DEFAULT_TRAP_HEIGHT);
        this.trapImage = image;

        // Am folosit metoda SetPosition din clasa de baza pentru a ne asigura ca bounds e initializat corect.
        SetPosition(x, y);
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea capcanei. Logica de daune este mutata in GameState.
     */
    @Override
    public void Update() {
        // Logica de coliziune a fost mutată în GameState.java
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza capcana pe ecran.
     * \param g Contextul grafic.
     */
    @Override
    public void Draw(Graphics g) {
        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()) * refLink.GetGameCamera().getZoomLevel());
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()) * refLink.GetGameCamera().getZoomLevel());
        int scaledWidth = (int)(width * refLink.GetGameCamera().getZoomLevel());
        int scaledHeight = (int)(height * refLink.GetGameCamera().getZoomLevel());

        if (trapImage != null) {
            g.drawImage(trapImage, drawX, drawY, scaledWidth, scaledHeight, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(drawX, drawY, scaledWidth, scaledHeight);
        }
    }
}