package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.States.GameState;
import PaooGame.States.State;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 * @class LevelExit
 * @brief Implementeaza un declansator (trigger) invizibil pentru trecerea la nivelul urmator.
 * Aceasta entitate este plasata la finalul unui nivel (in acest caz, Nivelul 2)
 * si, la interactiunea cu jucatorul, initiaza tranzitia catre nivelul urmator (Nivelul 3).
 */
public class LevelExit extends Entity {

    /**
     * @brief Constructorul clasei LevelExit.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X a coltului stanga-sus al zonei de iesire.
     * @param y Coordonata Y a coltului stanga-sus al zonei de iesire.
     * @param width Latimea zonei de iesire.
     * @param height Inaltimea zonei de iesire.
     */
    public LevelExit(RefLinks refLink, float x, float y, int width, int height) {
        super(refLink, x, y, width, height);
    }

    /**
     * @brief Actualizeaza starea entitatii in fiecare cadru.
     * Verifica daca jucatorul se afla in zona de iesire si a apasat tasta
     * de interactiune. Daca da, apeleaza metoda de tranzitie la nivelul urmator.
     */
    @Override
    public void Update() {
        Player player = refLink.GetPlayer();
        if (player != null && this.bounds.intersects(player.GetBounds())) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    ((GameState) currentState).passToLevel3();
                }
            }
        }
    }

    /**
     * @brief Deseneaza entitatea pe ecran.
     * Aceasta metoda este intentionat goala, deoarece LevelExit este un
     * obiect invizibil. Poate fi folosita pentru debug, pentru a desena
     * un contur al zonei de coliziune.
     * @param g Contextul grafic in care se va desena.
     */
    @Override
    public void Draw(Graphics g) {
        // Acest obiect este invizibil. Poti desena un chenar pentru debug.
        // g.setColor(Color.BLUE);
        // g.drawRect((int)x, (int)y, width, height);
    }
}