package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.States.State;
import PaooGame.States.GameState;
import PaooGame.States.PuzzleState;
import PaooGame.States.WordPuzzleState; // Importă noua clasă
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
                GameState gameState = refLink.GetGameState();
                if (gameState != null) {
                    // NU deschide puzzle-ul dacă un mesaj este deja afișat.
                    // Asta permite ca 'E' să fie folosit pentru a închide mesajul mai întâi.
                    if (gameState.isWoodSignMessageShowing()) {
                        return;
                    }

                    if (!gameState.isPuzzleSolved(puzzleId)) {
                        if (puzzleId == 99) {
                            refLink.SetState(new WordPuzzleState(refLink));
                        } else {
                            refLink.SetState(new PuzzleState(refLink, puzzleId));
                        }
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
        // Nu desenăm pop-up dacă puzzle-ul e deja rezolvat
        GameState gameState = refLink.GetGameState();
        if (gameState != null && !gameState.isPuzzleSolved(puzzleId)) {
            drawInteractionPopup(g);
        }
    }

    /*!
     * \fn public int getPuzzleId()
     * \brief Returneaza ID-ul puzzle-ului.
     */
    public int getPuzzleId() {
        return puzzleId;
    }
}