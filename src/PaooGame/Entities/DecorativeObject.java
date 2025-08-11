package PaooGame.Entities;

import PaooGame.RefLinks;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * \class DecorativeObject
 * \brief O clasa pentru entitati decorative, care pot fi optionale solide.
 */
public class DecorativeObject extends Entity {

    private BufferedImage image;
    private boolean solid; // ## MODIFICARE ##: Am adaugat o variabila pentru a sti daca obiectul este solid

    /**
     * \fn public DecorativeObject(RefLinks refLink, float x, float y, int width, int height, BufferedImage image, boolean solid)
     * \brief Constructorul clasei.
     * \param solid Parametru nou care defineste daca obiectul are coliziune.
     */
    public DecorativeObject(RefLinks refLink, float x, float y, int width, int height, BufferedImage image, boolean solid) {
        super(refLink, x, y, width, height);
        this.image = image;
        this.solid = solid; // Se seteaza starea de coliziune
    }

    /**
     * \fn public boolean isSolid()
     * \brief Metoda noua care returneaza daca obiectul este solid.
     * Aceasta este metoda pe care clasa Player incearca sa o apeleze.
     */
    public boolean isSolid() {
        return solid;
    }

    @Override
    public void Update() {
        // Obiect static, nu face nimic in Update
    }

    @Override
    public void Draw(Graphics g) {
        if (image != null) {
            int drawX = (int)((x - refLink.GetGameCamera().getxOffset()) * refLink.GetGameCamera().getZoomLevel());
            int drawY = (int)((y - refLink.GetGameCamera().getyOffset()) * refLink.GetGameCamera().getZoomLevel());
            int scaledWidth = (int)(width * refLink.GetGameCamera().getZoomLevel());
            int scaledHeight = (int)(height * refLink.GetGameCamera().getZoomLevel());

            g.drawImage(image, drawX, drawY, scaledWidth, scaledHeight, null);
        }
    }
}