package PaooGame.States;

import PaooGame.Entities.Key;
import PaooGame.Graphics.Assets;
import PaooGame.RefLinks;
import PaooGame.Tiles.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordPuzzleState extends State {
    private final String TARGET_WORD_DISPLAY = "LOST EXPEDITION";
    private final String TARGET_WORD_LOGIC = "LOSTEXPEDITION";
    private StringBuilder currentInput = new StringBuilder();
    private boolean solved = false;
    private long lastClickTime = 0;
    private final long CLICK_COOLDOWN = 200; // Previne click-uri duble accidentale

    private class Letter {
        char character;
        Rectangle bounds;
        boolean isVisible;

        Letter(char c, int x, int y) {
            this.character = c;
            this.bounds = new Rectangle(x, y, 40, 40);
            this.isVisible = true;
        }
    }

    private List<Letter> letters;

    public WordPuzzleState(RefLinks refLink) {
        super(refLink);
        letters = new ArrayList<>();

        // Amestecăm literele pentru a le afișa random
        List<Character> charsToPlace = new ArrayList<>();
        for (char c : TARGET_WORD_LOGIC.toCharArray()) {
            charsToPlace.add(c);
        }
        Collections.shuffle(charsToPlace);

        // Poziții predefinite pe ecran
        int[][] positions = {
                {150, 200}, {220, 300}, {290, 180}, {360, 280}, {430, 160},
                {500, 310}, {570, 210}, {640, 320}, {710, 190}, {780, 290},
                {320, 380}, {470, 390}, {620, 150}, {850, 250}
        };

        for (int i = 0; i < charsToPlace.size(); i++) {
            letters.add(new Letter(charsToPlace.get(i), positions[i][0], positions[i][1]));
        }
    }

    @Override
    public void Update() {
        if (solved) return;

        if (refLink.GetMouseManager().isMouseJustClicked() && (System.currentTimeMillis() - lastClickTime > CLICK_COOLDOWN)) {
            lastClickTime = System.currentTimeMillis();
            int mouseX = refLink.GetMouseManager().getMouseX();
            int mouseY = refLink.GetMouseManager().getMouseY();

            for (Letter l : letters) {
                if (l.isVisible && l.bounds.contains(mouseX, mouseY)) {
                    currentInput.append(l.character);
                    l.isVisible = false; // Litera dispare după click

                    // Adăugăm spațiu automat după "LOST"
                    if (currentInput.toString().equals("LOST")) {
                        currentInput.append(" ");
                    }
                    break;
                }
            }
        }

        // Verifică dacă am greșit
        if (!TARGET_WORD_DISPLAY.startsWith(currentInput.toString())) {
            // Reset
            currentInput.setLength(0);
            for (Letter l : letters) {
                l.isVisible = true;
            }
        }

        // Verifică dacă am rezolvat
        if (currentInput.toString().equals(TARGET_WORD_DISPLAY)) {
            solved = true;
            GameState gameState = refLink.GetGameState();
            if (gameState != null) {
                // Adăugăm cheia pe hartă
                Key finalKey = new Key(refLink, 77 * Tile.TILE_WIDTH, 31 * Tile.TILE_HEIGHT, Assets.keyImage, 6);
                gameState.addEntity(finalKey);
            }
            // Revine la joc
            refLink.SetState(refLink.GetPreviousState());
        }
    }

    @Override
    public void Draw(Graphics g) {
        // Desenează starea de joc din spate pentru un efect de overlay
        if (refLink.GetPreviousState() != null) {
            refLink.GetPreviousState().Draw(g);
        }

        // Adaugă un strat semi-transparent
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        // Desenează UI-ul puzzle-ului
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("Formează cuvântul: LOST EXPEDITION", 100, 100);

        // Chenar pentru progres
        g.drawRect(100, 500, 800, 60);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString(currentInput.toString(), 120, 550);

        // Desenează literele
        g.setFont(new Font("Arial", Font.BOLD, 28));
        for (Letter l : letters) {
            if (l.isVisible) {
                g.setColor(Color.YELLOW);
                g.fillRect(l.bounds.x, l.bounds.y, l.bounds.width, l.bounds.height);
                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(l.character), l.bounds.x + 12, l.bounds.y + 30);
            }
        }
    }
}