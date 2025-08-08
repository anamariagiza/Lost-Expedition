package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.States.State;
import PaooGame.States.GameState;
import PaooGame.States.PuzzleState;
import PaooGame.Tiles.Tile;

import java.awt.*;
import java.awt.event.KeyEvent;

/*!
 * \class public class PuzzleTrigger extends Entity
 * \brief Implementeaza un obiect invizibil care reprezinta o masuta cu un puzzle.
 * Acest obiect este punctul de interactiune pentru a lansa un puzzle.
 */
public class PuzzleTrigger extends Entity {

    private int puzzleId;

    /*!
     * \fn public PuzzleTrigger(RefLinks refLink, float x, float y, int width, int height, int puzzleId)
     * \brief Constructorul de initializare al clasei PuzzleTrigger.
     * \param refLink Referinta catre obiectul RefLinks.
     * \param x Coordonata X a pozitiei.
     * \param y Coordonata Y a pozitiei.
     * \param width Latimea zonei de coliziune.
     * \param height Inaltimea zonei de coliziune.
     * \param puzzleId ID-ul unic al puzzle-ului asociat.
     */
    public PuzzleTrigger(RefLinks refLink, float x, float y, int width, int height, int puzzleId) {
        super(refLink, x, y, width, height);
        this.puzzleId = puzzleId;
        SetPosition(x, y);
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea trigger-ului (verifica interacțiunea cu jucatorul).
     */
    @Override
    public void Update() {
        checkPlayerInteraction();
    }

    /*!
     * \fn private void checkPlayerInteraction()
     * \brief Verifica interacțiunea jucatorului cu masuta de puzzle.
     */
    private void checkPlayerInteraction() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds())) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    GameState gameState = (GameState) currentState;
                    if (!gameState.isPuzzleSolved(puzzleId)) { // Previne re-intrarea intr-un puzzle deja rezolvat
                        System.out.println("DEBUG PuzzleTrigger: Trecere la PuzzleState pentru puzzle-ul #" + puzzleId);
                        refLink.SetStateWithPrevious(new PuzzleState(refLink, puzzleId));
                    } else {
                        System.out.println("DEBUG PuzzleTrigger: Puzzle-ul #" + puzzleId + " a fost deja rezolvat.");
                    }
                }
            }
        }
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza un dreptunghi de debug pentru a vizualiza zona de coliziune a trigger-ului.
     */
    @Override
    public void Draw(Graphics g) {
        // Obiectul e invizibil. Pentru debug, poti decomenta liniile de mai jos:
        // g.setColor(new Color(0, 255, 0, 100));
        // g.fillRect((int)((x - refLink.GetGameCamera().getxOffset()) * refLink.GetGameCamera().getZoomLevel()),
        //            (int)((y - refLink.GetGameCamera().getyOffset()) * refLink.GetGameCamera().getZoomLevel()),
        //            (int)(width * refLink.GetGameCamera().getZoomLevel()),
        //            (int)(height * refLink.GetGameCamera().getZoomLevel()));
    }

    /*!
     * \fn public int getPuzzleId()
     * \brief Returneaza ID-ul puzzle-ului.
     */
    public int getPuzzleId() {
        return puzzleId;
    }
}