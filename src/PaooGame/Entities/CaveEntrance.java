package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.States.State;
import PaooGame.States.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;

/*!
 * \class public class CaveEntrance extends Entity
 * \brief Implementeaza un obiect invizibil care reprezinta intrarea in pestera.
 * Acest obiect este punctul de interactiune pentru a trece la nivelul urmator.
 */
public class CaveEntrance extends Entity {

    /*!
     * \fn public CaveEntrance(RefLinks refLink, float x, float y, int width, int height)
     * \brief Constructorul de initializare al clasei CaveEntrance.
     * \param refLink Referinta catre obiectul RefLinks.
     * \param x Coordonata X a pozitiei.
     * \param y Coordonata Y a pozitiei.
     * \param width Latimea zonei de coliziune.
     * \param height Inaltimea zonei de coliziune.
     */
    public CaveEntrance(RefLinks refLink, float x, float y, int width, int height) {
        super(refLink, x, y, width, height);
        SetPosition(x, y);
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea intrarii (verifica interacțiunea cu jucatorul).
     */
    @Override
    public void Update() {
        checkPlayerInteraction();
    }

    /*!
     * \fn private void checkPlayerInteraction()
     * \brief Verifica interacțiunea jucatorului cu intrarea in pestera.
     */
    private void checkPlayerInteraction() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds())) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    GameState gameState = (GameState) currentState;
                    if (gameState.isCaveEntranceUnlocked()) {
                        System.out.println("DEBUG CaveEntrance: Intrarea in pestera a fost deblocata. Trecere la nivelul urmator.");
                        gameState.saveCurrentState();
                        gameState.InitLevelInternal(1, false);
                    } else {
                        System.out.println("DEBUG CaveEntrance: Intrarea in pestera este blocata. Trebuie sa interactionezi cu paznicul.");
                    }
                }
            }
        }
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza un dreptunghi de debug pentru a vizualiza zona de coliziune a intrarii.
     */
    @Override
    public void Draw(Graphics g) {
        // De obicei, acest obiect este invizibil.
        // Pentru debug, poți desena o formă semi-transparentă.
        // g.setColor(new Color(255, 0, 0, 100));
        // g.fillRect((int)x, (int)y, width, height);
    }
}