package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.States.State;
import PaooGame.States.GameState;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 * @class CaveEntrance
 * @brief Implementeaza un obiect invizibil ce reprezinta intrarea in pestera.
 * Aceasta entitate functioneaza ca un declansator (trigger). Cand jucatorul
 * intra in zona sa de coliziune si apasa tasta de interactiune, jocul
 * va trece la nivelul urmator, cu conditia ca intrarea sa fi fost deblocata in prealabil.
 */
public class CaveEntrance extends Entity {

    /**
     * @brief Constructorul clasei CaveEntrance.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X a coltului stanga-sus al zonei de interactiune.
     * @param y Coordonata Y a coltului stanga-sus al zonei de interactiune.
     * @param width Latimea zonei de coliziune.
     * @param height Inaltimea zonei de coliziune.
     */
    public CaveEntrance(RefLinks refLink, float x, float y, int width, int height) {
        super(refLink, x, y, width, height);
        SetPosition(x, y);
    }

    /**
     * @brief Actualizeaza starea intrarii in fiecare cadru.
     * In acest caz, singura logica de actualizare este verificarea
     * interactiunii cu jucatorul.
     */
    @Override
    public void Update() {
        checkPlayerInteraction();
    }

    /**
     * @brief Deseneaza elementele vizuale ale entitatii.
     * Deoarece intrarea este un obiect invizibil, aceasta metoda nu deseneaza
     * entitatea in sine, ci doar pop-up-ul de interactiune ("E") atunci
     * cand jucatorul este in apropiere.
     * @param g Contextul grafic in care se va desena.
     */
    @Override
    public void Draw(Graphics g) {
        // Obiectul este invizibil, dar putem desena un pop-up de interacțiune
        drawInteractionPopup(g);
    }

    /**
     * @brief Verifica daca jucatorul se afla in zona si a apasat tasta de interactiune.
     * Daca jucatorul se afla in coliziune cu aceasta entitate si apasa tasta 'E',
     * metoda verifica daca intrarea a fost deblocata. Daca este deblocata,
     * salveaza starea curenta si initializeaza nivelul urmator.
     */
    private void checkPlayerInteraction() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds())) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                State currentState = State.GetState();
                if (currentState instanceof GameState gameState) {
                    if (gameState.isCaveEntranceUnlocked()) {
                        //System.out.println("DEBUG CaveEntrance: Intrarea in pestera a fost deblocata. Trecere la nivelul urmator.");
                        gameState.saveCurrentState();
                        gameState.InitLevelInternal(1, false);
                    } else {
                        //System.out.println("DEBUG CaveEntrance: Intrarea in pestera este blocata. Trebuie sa interactionezi cu paznicul.");
                    }
                }
            }
        }
    }


}