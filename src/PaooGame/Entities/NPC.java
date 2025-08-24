package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.States.State;
import PaooGame.States.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * @class NPC
 * @brief Implementeaza un personaj non-jucabil (Non-Player Character) cu care jucatorul poate interactiona.
 * Acest NPC actioneaza ca un "paznic" sau un personaj de misiune. El asteapta ca jucatorul
 * sa ii aduca un obiect specific (talismanul) pentru a debloca calea catre nivelul urmator.
 * Afiseaza mesaje de dialog in functie de starea misiunii.
 */
public class NPC extends Entity {

    /** Animatia de idle a NPC-ului.*/
    private final Animation anim;
    /** Mesajul de dialog care va fi afisat.*/
    private String dialogueMessage;
    /** Flag ce controleaza vizibilitatea casetei de dialog.*/
    private boolean showMessage = false;
    /** Timpul (in ms) la care a inceput afisarea mesajului.*/
    private long messageDisplayTime = 0;
    /** Flag ce indica daca jucatorul a predat deja talismanul acestui NPC.*/
    private boolean hasGivenTalisman = false;

    /**
     * @brief Constructorul clasei NPC.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X a pozitiei NPC-ului.
     * @param y Coordonata Y a pozitiei NPC-ului.
     */
    public NPC(RefLinks refLink, float x, float y) {
        super(refLink, x, y, Assets.NPC_FRAME_WIDTH, Assets.NPC_FRAME_HEIGHT);
        if (Assets.npcIdleAnim != null && Assets.npcIdleAnim.length > 0) {
            this.anim = new Animation(200, Assets.npcIdleAnim);
        } else {
            //System.err.println("DEBUG NPC: Animatia de idle este nula. Desenam placeholder.");
            this.anim = new Animation(200, new BufferedImage[]{new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)});
        }
        this.bounds = new Rectangle((int)x, (int)y, width, height);
    }

    /**
     * @brief Actualizeaza starea NPC-ului in fiecare cadru.
     *
     * Actualizeaza animatia, verifica interactiunea cu jucatorul si gestioneaza
     * timer-ul pentru afisarea mesajelor de dialog.
     */
    @Override
    public void Update() {
        anim.Update();
        checkPlayerInteraction();
        /* Durata (in ms) pentru care un mesaj este vizibil.*/
        long MESSAGE_DURATION_MS = 3000;
        if (showMessage && System.currentTimeMillis() - messageDisplayTime > MESSAGE_DURATION_MS) {
            showMessage = false;
        }
    }

    /**
     * @brief Deseneaza NPC-ul si elementele sale vizuale (caseta de dialog) pe ecran.
     * @param g Contextul grafic in care se va desena.
     */
    @Override
    public void Draw(Graphics g) {
        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()));
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()));
        int scaledWidth = (int)(width);
        int scaledHeight = (int)(height);
        g.drawImage(anim.getCurrentFrame(), drawX, drawY, scaledWidth, scaledHeight, null);

        if (!hasGivenTalisman) {
            drawInteractionPopup(g);
        }

        if (showMessage) {
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(dialogueMessage);
            int textHeight = fm.getHeight();
            int padding = 10;
            int boxWidth = textWidth + 2 * padding;
            int boxHeight = textHeight + 2 * padding;
            int boxX = drawX + (scaledWidth - boxWidth) / 2;
            int boxY = drawY - boxHeight - 10;

            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(boxX, boxY, boxWidth, boxHeight);

            g.setColor(Color.WHITE);
            g.drawString(dialogueMessage, boxX + padding, boxY + padding + fm.getAscent());
        }
    }

    /**
     * @brief Gestioneaza logica de interactiune dintre jucator si NPC.
     * Verifica daca jucatorul este in raza de actiune si apasa tasta 'E'.
     * Afiseaza mesaje diferite in functie de progresul jucatorului in misiune
     * (daca are sau nu talismanul, sau daca l-a predat deja).
     */
    private void checkPlayerInteraction() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        double distance = Math.sqrt(Math.pow(player.GetX() - x, 2) + Math.pow(player.GetY() - y, 2));
        /* Distanta maxima (in pixeli) de la care jucatorul poate interactiona.*/
        int INTERACTION_DISTANCE = 60;
        if (distance <= INTERACTION_DISTANCE && refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
            State currentState = State.GetState();
            if (currentState instanceof GameState gameState) {
                if (!hasGivenTalisman) {
                    if (gameState.hasTalismanCollected()) {
                        dialogueMessage = "Ah, ai adus Talismanul Lunii! Acum poarta spre pestera e deschisa!";
                        showMessage = true;
                        messageDisplayTime = System.currentTimeMillis();
                        hasGivenTalisman = true;
                        gameState.setCaveEntranceUnlocked(true);
                        gameState.removeTalismanFromInventory();
                        //System.out.println("DEBUG NPC: Jucatorul a dat talismanul. Intrarea e deblocata.");
                    } else {
                        dialogueMessage = "Pentru a intra in pestera, trebuie sa imi aduci Talismanul Lunii.";
                        showMessage = true;
                        messageDisplayTime = System.currentTimeMillis();
                        //System.out.println("DEBUG NPC: Jucatorul a interactionat, dar nu are talismanul.");
                    }
                } else {
                    dialogueMessage = "Drumul e deschis. Mergi inainte, exploratorule!";
                    showMessage = true;
                    messageDisplayTime = System.currentTimeMillis();
                }
            }
        }
    }
}