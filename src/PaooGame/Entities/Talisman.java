package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.States.State;
import PaooGame.States.GameState;
import PaooGame.Tiles.Tile;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * @class Talisman
 * @brief Implementeaza un obiect cheie de tip "talisman", necesar pentru a progresa in misiune.
 * Aceasta entitate este un obiect unic ce trebuie colectat de jucator in Nivelul 1
 * pentru a-l putea oferi NPC-ului paznic, deblocand astfel intrarea in pestera.
 */
public class Talisman extends Entity {

    /** Imaginea (sprite-ul) talismanului.*/
    private final BufferedImage image;
    /** Flag ce indica daca talismanul a fost colectat.*/
    private boolean isCollected = false;

    /**
     * @brief Constructorul clasei Talisman.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X a pozitiei talismanului.
     * @param y Coordonata Y a pozitiei talismanului.
     * @param image Imaginea (sprite-ul) care va fi afisata.
     */
    public Talisman(RefLinks refLink, float x, float y, BufferedImage image) {
        super(refLink, x, y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
        this.image = image;
        this.bounds = new Rectangle((int)x, (int)y, width, height);
    }

    /**
     * @brief Actualizeaza starea talismanului in fiecare cadru.
     * Verifica daca jucatorul este in apropiere si apasa tasta de interactiune.
     * Daca este colectat, anunta GameState despre acest eveniment.
     */
    @Override
    public void Update() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds())) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                this.isCollected = true;
                State currentState = State.GetState();
                if (currentState instanceof GameState gameState) {
                    gameState.talismanCollected();
                }
                System.out.println("Talismanul Lunii a fost colectat!");
            }
        }
    }

    /**
     * @brief Deseneaza talismanul pe ecran.
     * Talismanul este afisat doar daca nu a fost inca colectat. Dupa colectare,
     * devine invizibil.
     * @param g Contextul grafic in care se va desena.
     */
    @Override
    public void Draw(Graphics g) {
        if (isCollected) return; // Talismanul nu se mai deseneaza dupa colectare

        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()));
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()));
        int scaledWidth = (int)(width);
        int scaledHeight = (int)(height);
        if (image != null) {
            g.drawImage(image, drawX, drawY, scaledWidth, scaledHeight, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(drawX, drawY, scaledWidth, scaledHeight);
        }
        drawInteractionPopup(g);
    }

    /**
     * @brief Verifica daca talismanul a fost colectat.
     * @return True daca a fost colectat, false altfel.
     */
    public boolean isCollected() {
        return isCollected;
    }
}