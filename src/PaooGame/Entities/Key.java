package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Tiles.Tile;
import PaooGame.States.State;
import PaooGame.States.GameState;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/*!
 * \class public class Key extends Entity
 * \brief Implementeaza notiunea de cheie, un obiect colectabil care deblocheaza progresul.
 */
public class Key extends Entity {

    public enum KeyType {
        NEXT_LEVEL_KEY,
        DOOR_KEY
    }

    private static final int DEFAULT_KEY_WIDTH = 32;
    private static final int DEFAULT_KEY_HEIGHT = 32;
    private BufferedImage keyImage;

    private boolean collected = false;
    private KeyType type;

    // NOU: ID-ul puzzle-ului asociat acestei chei
    private int associatedPuzzleId = -1;

    /*!
     * \fn public Key(RefLinks refLink, float x, float y, BufferedImage image, KeyType type)
     * \brief Constructorul de initializare al clasei Key cu specificarea tipului.
     * \param refLink Referinta catre obiectul RefLinks.
     * \param x Coordonata X initiala.
     * \param y Coordonata Y initiala.
     * \param image Imaginea cheii.
     * \param type Tipul cheii (NEXT_LEVEL_KEY, DOOR_KEY).
     */
    public Key(RefLinks refLink, float x, float y, BufferedImage image, KeyType type) {
        super(refLink, x, y, DEFAULT_KEY_WIDTH, DEFAULT_KEY_HEIGHT);
        SetPosition(x, y);

        this.keyImage = image;
        this.type = type;
    }

    // NOU: Constructor pentru cheile de puzzle
    public Key(RefLinks refLink, float x, float y, BufferedImage image, int puzzleId) {
        this(refLink, x, y, image, KeyType.DOOR_KEY);
        this.associatedPuzzleId = puzzleId;
    }


    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea cheii (verifica interacțiunea cu jucatorul).
     */
    @Override
    public void Update() {
        if (collected) {
            return;
        }
        checkPlayerInteraction();
    }

    /*!
     * \fn private void checkPlayerInteraction()
     * \brief Verifica interacțiunea jucatorului cu cheia (coliziune + apasarea tastei E).
     */
    private void checkPlayerInteraction() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds())) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                System.out.println("DEBUG Key: Cheia de tip " + type + " a fost colectata!");
                collected = true;

                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    GameState gameState = (GameState) currentState;
                    switch (type) {
                        case NEXT_LEVEL_KEY:
                            gameState.keyCollected();
                            break;
                        case DOOR_KEY:
                            // NOU: nu se mai deschide usa la colectarea cheii.
                            gameState.doorKeyCollected();
                            break;
                    }
                } else {
                    System.err.println("DEBUG Key: Colectare cheie in afara GameState-ului!");
                }
            }
        }
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza cheia pe ecran.
     * \param g Contextul grafic.
     */
    @Override
    public void Draw(Graphics g) {
        if (collected) {
            return;
        }
        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()) * refLink.GetGameCamera().getZoomLevel());
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()) * refLink.GetGameCamera().getZoomLevel());
        int scaledWidth = (int)(width * refLink.GetGameCamera().getZoomLevel());
        int scaledHeight = (int)(height * refLink.GetGameCamera().getZoomLevel());
        if (keyImage != null) {
            g.drawImage(keyImage, drawX, drawY, scaledWidth, scaledHeight, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(drawX, drawY, scaledWidth, scaledHeight);
        }
    }

    /*!
     * \fn public boolean isCollected()
     * \brief Returneaza starea colectarii cheii.
     */
    public boolean isCollected() {
        return collected;
    }

    /*!
     * \fn public void setCollected(boolean collected)
     * \brief Seteaza starea colectarii cheii (util la incarcare joc).
     */
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    /*!
     * \fn public KeyType getType()
     * \brief Returneaza tipul cheii.
     */
    public KeyType getType() {
        return type;
    }
}