package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import PaooGame.States.State;
import PaooGame.States.GameState;
import java.awt.event.KeyEvent;
import java.awt.*;

/**
 * @class DecorativeObject
 * @brief O clasa versatila pentru entitati decorative, care pot fi optionale solide.
 * Aceasta entitate este folosita pentru a plasa diverse obiecte in lume, cum ar fi
 * mese, semne de lemn sau orice alt element vizual care nu este o dala.
 * Obiectele pot fi solide (pentru coliziuni) si pot afisa un mesaj de dialog la interactiune.
 */
public class DecorativeObject extends Entity {

    /** Imaginea (sprite-ul) obiectului.*/
    private final BufferedImage image;
    /** Flag ce indica daca obiectul este solid (blocheaza miscarea).*/
    private final boolean solid;
    /** Mesajul de afisat la interactiune (daca exista).*/
    private String dialogueMessage;

    /**
     * @brief Constructorul clasei DecorativeObject.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X a pozitiei obiectului.
     * @param y Coordonata Y a pozitiei obiectului.
     * @param width Latimea obiectului.
     * @param height Inaltimea obiectului.
     * @param image Imaginea (sprite-ul) care va fi afisata.
     * @param solid True daca obiectul trebuie sa fie solid, false altfel.
     */
    public DecorativeObject(RefLinks refLink, float x, float y, int width, int height, BufferedImage image, boolean solid) {
        super(refLink, x, y, width, height);
        this.image = image;
        this.solid = solid;
    }

    /**
     * @brief Actualizeaza starea obiectului in fiecare cadru.
     * Gestioneaza logica de afisare a mesajelor de dialog. Daca jucatorul este in
     * apropiere si apasa tasta 'E', mesajul este afisat sau ascuns, interactionand
     * cu starea globala a jocului (GameState).
     */
    @Override
    public void Update() {
        if (refLink.GetPlayer() == null) return;

        // Verifica daca jucatorul este in zona de interactiune
        if (this.bounds.intersects(refLink.GetPlayer().GetBounds())) {
            // Verifica daca a fost apasata tasta 'E' si daca obiectul are un mesaj
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E) && dialogueMessage != null) {

                GameState gameState = refLink.GetGameState();
                if (gameState != null) {
                    // Daca mesajul acestui panou este deja afisat, inchide-l
                    if (gameState.isWoodSignMessageShowing() && gameState.getWoodSignMessage().equals(this.dialogueMessage)) {
                        gameState.showWoodSignMessage(null);
                        gameState.setObjectiveDisplayed(true);
                    }
                    // Altfel, daca niciun alt mesaj nu este afisat, deschide-l pe acesta
                    else if (!gameState.isWoodSignMessageShowing()) {
                        gameState.showWoodSignMessage(dialogueMessage);
                        gameState.setObjectiveDisplayed(false);
                    }
                }
            }
        }
    }

    /**
     * @brief Deseneaza obiectul decorativ pe ecran.
     * @param g Contextul grafic in care se va desena.
     */
    @Override
    public void Draw(Graphics g) {
        if (image != null) {
            int drawX = (int)((x - refLink.GetGameCamera().getxOffset()));
            int drawY = (int)((y - refLink.GetGameCamera().getyOffset()));
            int scaledWidth = (int)(width);
            int scaledHeight = (int)(height);
            g.drawImage(image, drawX, drawY, scaledWidth, scaledHeight, null);

            // Desenam pop-up-ul E doar daca este un panou de lemn si nu se afiseaza deja un mesaj
            if (image == Assets.woodSignImage && State.GetState() instanceof GameState && !((GameState) State.GetState()).isWoodSignMessageShowing()) {
                drawInteractionPopup(g);
            }
        }
    }

    /**
     * @brief Verifica daca obiectul este solid.
     * @return True daca obiectul este solid, false altfel.
     */
    public boolean isSolid() {
        return solid;
    }

    /**
     * @brief Seteaza mesajul de dialog pentru acest obiect.
     * @param message Textul care va fi afisat la interactiune.
     */
    public void setDialogueMessage(String message) {
        this.dialogueMessage = message;
    }

    /**
     * @brief Returneaza mesajul de dialog al obiectului.
     * @return Mesajul de dialog.
     */
    public String getDialogueMessage() {
        return this.dialogueMessage;
    }
}