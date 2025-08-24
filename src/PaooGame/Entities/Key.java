package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.States.State;
import PaooGame.States.GameState;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * @class Key
 * @brief Implementeaza un obiect de tip "cheie" care poate fi colectat de jucator.
 * Cheile sunt entitati interactive esentiale pentru progresia in joc.
 * Ele pot fi de diferite tipuri, de exemplu pentru a debloca usi specifice
 * (asociate unui puzzle) sau pentru a permite trecerea la nivelul urmator.
 */
public class Key extends Entity {

    /**
     * @enum KeyType
     * @brief Defineste tipurile de chei disponibile in joc.
     */
    public enum KeyType {
        NEXT_LEVEL_KEY,
        DOOR_KEY
    }

    /** Constante pentru dimensiunile implicite ale imaginii cheii.*/
    private static final int DEFAULT_KEY_WIDTH = 32;
    private static final int DEFAULT_KEY_HEIGHT = 32;
    /** Imaginea (sprite-ul) cheii.*/
    private final BufferedImage keyImage;

    /** Flag ce indica daca cheia a fost colectata.*/
    private boolean collected = false;
    /** Tipul cheii, conform enum-ului KeyType.*/
    private final KeyType type;
    /** ID-ul puzzle-ului care a generat aceasta cheie (relevant doar pentru DOOR_KEY).*/
    private int associatedPuzzleId = -1;

    /**
     * @brief Constructor principal pentru clasa Key.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X a pozitiei cheii.
     * @param y Coordonata Y a pozitiei cheii.
     * @param image Imaginea (sprite-ul) cheii.
     * @param type Tipul cheii (din enum-ul KeyType).
     */
    public Key(RefLinks refLink, float x, float y, BufferedImage image, KeyType type) {
        super(refLink, x, y, DEFAULT_KEY_WIDTH, DEFAULT_KEY_HEIGHT);
        SetPosition(x, y);

        this.keyImage = image;
        this.type = type;
    }

    /**
     * @brief Constructor secundar (convenience) pentru cheile de tip DOOR_KEY.
     * Seteaza automat tipul la DOOR_KEY si stocheaza ID-ul puzzle-ului asociat.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X a pozitiei cheii.
     * @param y Coordonata Y a pozitiei cheii.
     * @param image Imaginea (sprite-ul) cheii.
     * @param puzzleId ID-ul puzzle-ului care a generat cheia.
     */
    public Key(RefLinks refLink, float x, float y, BufferedImage image, int puzzleId) {
        this(refLink, x, y, image, KeyType.DOOR_KEY);
        this.associatedPuzzleId = puzzleId;
    }

    /**
     * @brief Actualizeaza starea cheii in fiecare cadru.
     * Daca cheia nu a fost colectata, verifica interactiunea cu jucatorul.
     * Daca a fost deja colectata, nu executa nicio logica.
     */
    @Override
    public void Update() {
        if (collected) {
            return;
        }
        checkPlayerInteraction();
    }

    /**
     * @brief Deseneaza cheia pe ecran.
     * Cheia este desenata doar daca nu a fost inca colectata. De asemenea,
     * afiseaza pop-up-ul de interactiune cand jucatorul este in apropiere.
     * @param g Contextul grafic in care se va desena.
     */
    @Override
    public void Draw(Graphics g) {
        if (collected) {
            return;
        }
        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()));
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()));
        int scaledWidth = (int)(width);
        int scaledHeight = (int)(height);
        if (keyImage != null) {
            g.drawImage(keyImage, drawX, drawY, scaledWidth, scaledHeight, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(drawX, drawY, scaledWidth, scaledHeight);
        }
        drawInteractionPopup(g);
    }

    /**
     * @brief Verifica daca jucatorul este in zona si a apasat tasta de interactiune pentru a colecta cheia.
     * La colectare, anunta GameState despre eveniment, astfel incat starea jocului
     * sa fie actualizata corespunzator (ex: jucatorul detine acum cheia X).
     */
    private void checkPlayerInteraction() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds())) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                //System.out.println("DEBUG Key: Cheia de la puzzle-ul " + associatedPuzzleId + " a fost colectata!");
                collected = true;

                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    GameState gameState = (GameState) currentState;
                    switch (type) {
                        case NEXT_LEVEL_KEY:
                            gameState.keyCollected();
                            break;
                        case DOOR_KEY:
                            gameState.doorKeyCollected(associatedPuzzleId);
                            break;
                    }
                } else {
                    //System.err.println("DEBUG Key: Colectare cheie in afara GameState-ului!");
                }
            }
        }
    }

    /**
     * @brief Verifica daca cheia a fost colectata.
     * @return True daca a fost colectata, false altfel.
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * @brief Seteaza starea de colectare a cheii.
     * @param collected Noua stare de colectare.
     */
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    /**
     * @brief Returneaza ID-ul puzzle-ului asociat cu aceasta cheie.
     * @return ID-ul puzzle-ului, sau -1 daca nu este o cheie de tip DOOR_KEY.
     */
    public int getAssociatedPuzzleId() {
        return associatedPuzzleId;
    }

    /**
     * @brief Returneaza tipul cheii.
     * @return Tipul cheii, din enum-ul KeyType.
     */
    public KeyType getType() {
        return type;
    }
}