package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.States.State;
import PaooGame.States.GameState;
import PaooGame.Tiles.Tile;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/*!
 * \class public class NPC extends Entity
 * \brief Implementeaza notiunea de NPC (Non-Player Character) - Paznicul pesterii.
 * Un NPC este un caracter static cu care jucatorul poate interactiona.
 */
public class NPC extends Entity {

    private Animation anim;
    private String dialogueMessage;
    private boolean showMessage = false;
    private long messageDisplayTime = 0;
    private final long MESSAGE_DURATION_MS = 3000;
    private final int INTERACTION_DISTANCE = 60; // Distanta in pixeli pentru interactiune
    private boolean hasGivenTalisman = false;

    /*!
     * \fn public NPC(RefLinks refLink, float x, float y)
     * \brief Constructorul de initializare al clasei NPC.
     * \param refLink Referinta catre obiectul RefLinks.
     * \param x Coordonata X initiala.
     * \param y Coordonata Y initiala.
     */
    public NPC(RefLinks refLink, float x, float y) {
        super(refLink, x, y, Assets.NPC_FRAME_WIDTH, Assets.NPC_FRAME_HEIGHT);

        // Initializam animatia de idle
        if (Assets.npcIdleAnim != null && Assets.npcIdleAnim.length > 0) {
            this.anim = new Animation(200, Assets.npcIdleAnim);
        } else {
            System.err.println("DEBUG NPC: Animatia de idle este nula. Desenam placeholder.");
            this.anim = new Animation(200, new BufferedImage[]{new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)});
        }

        this.bounds = new Rectangle((int)x, (int)y, width, height);
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea NPC-ului (animatia si interacțiunea).
     */
    @Override
    public void Update() {
        anim.Update();
        checkPlayerInteraction();

        // Verificam daca a expirat timpul de afisare a mesajului
        if (showMessage && System.currentTimeMillis() - messageDisplayTime > MESSAGE_DURATION_MS) {
            showMessage = false;
        }
    }

    /*!
     * \fn private void checkPlayerInteraction()
     * \brief Verifica interacțiunea jucatorului cu NPC-ul.
     */
    private void checkPlayerInteraction() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        // Verificam distanta pentru a permite interacțiunea
        double distance = Math.sqrt(Math.pow(player.GetX() - x, 2) + Math.pow(player.GetY() - y, 2));

        if (distance <= INTERACTION_DISTANCE && refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
            State currentState = State.GetState();
            if (currentState instanceof GameState) {
                GameState gameState = (GameState) currentState;

                if (!hasGivenTalisman) {
                    if (gameState.hasTalismanCollected()) {
                        dialogueMessage = "Ah, ai adus Talismanul Soarelui! Acum poarta spre pestera e deschisa!";
                        showMessage = true;
                        messageDisplayTime = System.currentTimeMillis();
                        hasGivenTalisman = true;
                        gameState.setCaveEntranceUnlocked(true);
                        gameState.removeTalismanFromInventory();
                        System.out.println("DEBUG NPC: Jucatorul a dat talismanul. Intrarea e deblocata.");
                    } else {
                        dialogueMessage = "Pentru a intra in pestera, trebuie sa imi aduci Talismanul Soarelui.";
                        showMessage = true;
                        messageDisplayTime = System.currentTimeMillis();
                        System.out.println("DEBUG NPC: Jucatorul a interactionat, dar nu are talismanul.");
                    }
                } else {
                    dialogueMessage = "Drumul e deschis. Mergi inainte, exploratorule!";
                    showMessage = true;
                    messageDisplayTime = System.currentTimeMillis();
                }
            }
        }
    }


    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza NPC-ul pe ecran.
     * \param g Contextul grafic.
     */
    @Override
    public void Draw(Graphics g) {
        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()) * refLink.GetGameCamera().getZoomLevel());
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()) * refLink.GetGameCamera().getZoomLevel());
        int scaledWidth = (int)(width * refLink.GetGameCamera().getZoomLevel());
        int scaledHeight = (int)(height * refLink.GetGameCamera().getZoomLevel());

        g.drawImage(anim.getCurrentFrame(), drawX, drawY, scaledWidth, scaledHeight, null);

        // Desenam mesajul de dialog
        if (showMessage) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(drawX - 50, drawY - 40, scaledWidth + 100, 30);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(dialogueMessage);
            g.drawString(dialogueMessage, drawX - 50 + (scaledWidth + 100 - textWidth) / 2, drawY - 25 + fm.getAscent() / 2);
        }
    }
}