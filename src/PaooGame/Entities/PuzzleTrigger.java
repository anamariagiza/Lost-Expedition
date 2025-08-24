package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.States.State;
import PaooGame.States.GameState;
import PaooGame.States.PuzzleState;
import PaooGame.States.WordPuzzleState; // Importă noua clasă
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @class PuzzleTrigger
 * @brief Implementeaza un obiect invizibil care functioneaza ca punct de interactiune pentru a lansa un puzzle.
 * Aceasta entitate este de obicei plasata peste un obiect vizual (cum ar fi o masa)
 * si asteapta ca jucatorul sa apese tasta de interactiune. La activare, schimba
 * starea jocului intr-o stare de puzzle corespunzatoare (PuzzleState sau WordPuzzleState).
 */
public class PuzzleTrigger extends Entity {

    /* ID-ul unic al puzzle-ului pe care il activeaza acest declansator.*/
    private final int puzzleId;

    /**
     * @brief Constructorul clasei PuzzleTrigger.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X a zonei de activare.
     * @param y Coordonata Y a zonei de activare.
     * @param width Latimea zonei de activare.
     * @param height Inaltimea zonei de activare.
     * @param puzzleId ID-ul puzzle-ului asociat.
     */
    public PuzzleTrigger(RefLinks refLink, float x, float y, int width, int height, int puzzleId) {
        super(refLink, x, y, width, height);
        this.puzzleId = puzzleId;
        SetPosition(x, y);
    }

    /**
     * @brief Actualizeaza starea declansatorului in fiecare cadru.
     * Apeleaza metoda care verifica interactiunea cu jucatorul.
     */
    @Override
    public void Update() {
        checkPlayerInteraction();
    }

    /**
     * @brief Deseneaza elementele vizuale ale entitatii.
     * Deoarece este un obiect invizibil, deseneaza doar pop-up-ul de interactiune ("E")
     * si doar daca puzzle-ul asociat nu a fost inca rezolvat.
     * @param g Contextul grafic in care se va desena.
     */
    @Override
    public void Draw(Graphics g) {
        // Nu desenăm pop-up dacă puzzle-ul e deja rezolvat
        GameState gameState = refLink.GetGameState();
        if (gameState != null && !gameState.isPuzzleSolved(puzzleId)) {
            drawInteractionPopup(g);
        }
    }

    /**
     * @brief Verifica daca jucatorul este in zona si a apasat tasta de interactiune.
     * Daca sunt indeplinite conditiile, si puzzle-ul nu este deja rezolvat,
     * schimba starea curenta a jocului in starea de puzzle corespunzatoare.
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

    /**
     * @brief Returneaza ID-ul puzzle-ului asociat cu acest declansator.
     * @return ID-ul puzzle-ului.
     */
    public int getPuzzleId() {
        return puzzleId;
    }
}