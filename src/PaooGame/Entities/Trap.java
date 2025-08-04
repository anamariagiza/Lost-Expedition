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
    private static final int DAMAGE_AMOUNT = 20;

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

        this.bounds = new Rectangle(0, 0, width, height);
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea capcanei (verifica interacțiunea cu jucatorul).
     */
    @Override
    public void Update() {
        checkPlayerCollision();
    }

    /*!
     * \fn private void checkPlayerCollision()
     * \brief Verifica coliziunea jucatorului cu capcana.
     */
    private void checkPlayerCollision() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds())) {
            player.takeDamage(DAMAGE_AMOUNT);
            System.out.println("DEBUG Trap: Coliziune cu jucatorul!");
        }
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