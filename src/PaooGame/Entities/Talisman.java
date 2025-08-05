package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.States.State;
import PaooGame.States.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/*!
 * \class public class Talisman extends Entity
 * \brief Implementeaza notiunea de Talisman, un obiect colectabil esential pentru progresul in joc.
 */
public class Talisman extends Entity {

    private static final int DEFAULT_TALISMAN_WIDTH = 32;
    private static final int DEFAULT_TALISMAN_HEIGHT = 32;
    private BufferedImage talismanImage;

    private boolean collected = false;
    // NOU: Am eliminat canCollect, isKeyJustPressed face asta deja

    /*!
     * \fn public Talisman(RefLinks refLink, float x, float y, BufferedImage image)
     * \brief Constructorul de initializare al clasei Talisman.
     * \param refLink Referinta catre obiectul RefLinks.
     * \param x Coordonata X initiala.
     * \param y Coordonata Y initiala.
     * \param image Imaginea talismanului.
     */
    public Talisman(RefLinks refLink, float x, float y, BufferedImage image) {
        super(refLink, x, y, DEFAULT_TALISMAN_WIDTH, DEFAULT_TALISMAN_HEIGHT);

        SetPosition(x, y);

        this.talismanImage = image;
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea talismanului (verifica interacțiunea cu jucatorul).
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
     * \brief Verifica interacțiunea jucatorului cu talismanul (coliziune + apasarea tastei E).
     */
    private void checkPlayerInteraction() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds())) {
            // NOU: Am revenit la isKeyJustPressed(KeyEvent.VK_E)
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                System.out.println("DEBUG Talisman: Talismanul a fost colectat!");
                collected = true;

                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    GameState gameState = (GameState) currentState;
                    gameState.talismanCollected();
                } else {
                    System.err.println("DEBUG Talisman: Colectare talisman in afara GameState-ului!");
                }
            }
        }
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza talismanul pe ecran.
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

        if (talismanImage != null) {
            g.drawImage(talismanImage, drawX, drawY, scaledWidth, scaledHeight, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(drawX, drawY, scaledWidth, scaledHeight);
        }
    }

    /*!
     * \fn public boolean isCollected()
     * \brief Returneaza starea colectarii talismanului.
     */
    public boolean isCollected() {
        return collected;
    }

    /*!
     * \fn public void setCollected(boolean collected)
     * \brief Seteaza starea colectarii talismanului (util la incarcare joc).
     */
    public void setCollected(boolean collected) {
        this.collected = collected;
    }
}