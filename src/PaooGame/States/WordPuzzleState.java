package PaooGame.States;

import PaooGame.Entities.Key;
import PaooGame.Entities.Player;
import PaooGame.Graphics.Assets;
import PaooGame.RefLinks;
import PaooGame.Tiles.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @class WordPuzzleState
 * @brief Implementeaza un mini-joc de tip puzzle cu cuvinte.
 * In aceasta stare, jucatorul trebuie sa formeze o propozitie tinta facand clic
 * pe literele amestecate pe ecran, intr-un interval de timp limitat.
 */
public class WordPuzzleState extends State {
    /** Variabile constante ale puzzle-ului.*/
    private final String HINT_TEXT = "Calatorie neasteptata.";
    private final String TARGET_WORD_DISPLAY = "LOST EXPEDITION";
    private final String TARGET_WORD_LOGIC = "LOSTEXPEDITION";
    private StringBuilder currentInput = new StringBuilder();
    private boolean solved = false;

    /** Variabile pentru cronometru si penalizare in caz de esec.*/
    private final long puzzleStartTime;
    private final long TIME_LIMIT_MS = 100000;
    private final int DAMAGE_PENALTY = 20;

    /** Variabile pentru a gestiona interactiunea cu mouse-ul.*/
    private long lastClickTime = 0;
    private final long CLICK_COOLDOWN = 200;

    /**
     * @class Letter
     * @brief Clasa interna ajutatoare pentru a stoca datele fiecarei litere din puzzle.
     */
    private static class Letter {
        char character;
        Rectangle bounds;
        boolean isVisible;

        Letter(char c) {
            this.character = c;
            this.bounds = new Rectangle(0, 0, 40, 40); // Coordonatele vor fi calculate dinamic
            this.isVisible = true;
        }
    }

    /** Lista tuturor literelor care pot fi selectate in puzzle.*/
    private List<Letter> letters;

    /** Matrice de coordonate relative pentru a pozitiona literele pe ecran.*/
    private final float[][] relativePositions = {
            {0.15f, 0.30f}, {0.23f, 0.50f}, {0.31f, 0.28f}, {0.39f, 0.48f}, {0.47f, 0.26f},
            {0.55f, 0.51f}, {0.63f, 0.31f}, {0.71f, 0.52f}, {0.79f, 0.29f}, {0.87f, 0.49f},
            {0.28f, 0.65f}, {0.43f, 0.66f}, {0.58f, 0.64f}, {0.73f, 0.65f}
    };

    /**
     * @brief Constructorul clasei WordPuzzleState.
     * @param refLink O referinta catre obiectul RefLinks.
     */
    public WordPuzzleState(RefLinks refLink) {
        super(refLink);
        this.puzzleStartTime = System.currentTimeMillis();
        letters = new ArrayList<>();

        List<Character> charsToPlace = new ArrayList<>();
        for (char c : TARGET_WORD_LOGIC.toCharArray()) {
            charsToPlace.add(c);
        }
        Collections.shuffle(charsToPlace);

        for (char c : charsToPlace) {
            letters.add(new Letter(c));
        }
    }

    /**
     * @brief Actualizeaza logica puzzle-ului in fiecare cadru.
     */
    @Override
    public void Update() {
        if (solved) return;

        // Verifica daca timpul a expirat
        if (System.currentTimeMillis() - puzzleStartTime > TIME_LIMIT_MS) {
            System.out.println("Timpul a expirat! Puzzle esuat.");

            // Aplica penalizarea direct prin RefLinks
            Player player = refLink.GetPlayer();
            if (player != null) {
                player.takeDamage(DAMAGE_PENALTY);
            }

            // Revine la joc
            refLink.SetState(refLink.GetPreviousState());
            return;
        }

        // Logica de click pe litere
        if (refLink.GetMouseManager().isMouseJustClicked() && (System.currentTimeMillis() - lastClickTime > CLICK_COOLDOWN)) {
            lastClickTime = System.currentTimeMillis();
            int mouseX = refLink.GetMouseManager().getMouseX();
            int mouseY = refLink.GetMouseManager().getMouseY();

            for (Letter l : letters) {
                if (l.isVisible && l.bounds.contains(mouseX, mouseY)) {
                    currentInput.append(l.character);
                    l.isVisible = false;

                    if (currentInput.toString().equals("LOST")) {
                        currentInput.append(" ");
                    }
                    break;
                }
            }
        }

        // Logica de verificare si resetare a cuvantului
        if (!TARGET_WORD_DISPLAY.startsWith(currentInput.toString())) {
            currentInput.setLength(0);
            for (Letter l : letters) {
                l.isVisible = true;
            }
        }

        // Logica de finalizare cu succes
        if (currentInput.toString().equals(TARGET_WORD_DISPLAY)) {
            solved = true;
            State prevState = refLink.GetPreviousState();
            if (prevState instanceof GameState) {
                GameState gameState = (GameState) prevState;
                Key finalKey = new Key(refLink, 77 * Tile.TILE_WIDTH, 31 * Tile.TILE_HEIGHT, Assets.keyImage, 6);
                gameState.addEntity(finalKey);
            }
            refLink.SetState(refLink.GetPreviousState());
        }
    }

    /**
     * @brief Deseneaza (randeaza) starea curenta a puzzle-ului.
     * @param g Contextul grafic in care se va desena.
     */
    @Override
    public void Draw(Graphics g) {
        int screenWidth = refLink.GetWidth();
        int screenHeight = refLink.GetHeight();

        if (refLink.GetPreviousState() != null) {
            refLink.GetPreviousState().Draw(g);
        }

        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Deseneaza indiciul
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fmTitle = g.getFontMetrics();
        int titleWidth = fmTitle.stringWidth(HINT_TEXT);
        g.drawString(HINT_TEXT, (screenWidth - titleWidth) / 2, screenHeight / 10);

        // Deseneaza cronometrul
        long timeLeftMs = TIME_LIMIT_MS - (System.currentTimeMillis() - puzzleStartTime);
        if (timeLeftMs < 0) timeLeftMs = 0;
        String timerStr = String.format("Timp Ramas: %.1f", timeLeftMs / 1000.0);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fmTimer = g.getFontMetrics();
        int timerWidth = fmTimer.stringWidth(timerStr);
        g.drawString(timerStr, screenWidth - timerWidth - 20, 40);

        // Deseneaza chenarul de progres
        int boxWidth = (int)(screenWidth * 0.6);
        int boxHeight = 60;
        int boxX = (screenWidth - boxWidth) / 2;
        int boxY = (int)(screenHeight * 0.8);
        g.drawRect(boxX, boxY, boxWidth, boxHeight);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fmInput = g.getFontMetrics();
        int inputWidth = fmInput.stringWidth(currentInput.toString());
        g.drawString(currentInput.toString(), boxX + (boxWidth - inputWidth) / 2, boxY + fmInput.getAscent() + 5);

        // Calculeaza dinamic pozitiile literelor si le deseneaza
        g.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fmLetter = g.getFontMetrics();
        for (int i = 0; i < letters.size(); i++) {
            Letter l = letters.get(i);
            if (l.isVisible) {
                l.bounds.x = (int)(screenWidth * relativePositions[i][0]);
                l.bounds.y = (int)(screenHeight * relativePositions[i][1]);

                g.setColor(Color.YELLOW);
                g.fillRect(l.bounds.x, l.bounds.y, l.bounds.width, l.bounds.height);
                g.setColor(Color.BLACK);
                String charStr = String.valueOf(l.character);
                int charWidth = fmLetter.stringWidth(charStr);
                g.drawString(charStr, l.bounds.x + (l.bounds.width - charWidth) / 2, l.bounds.y + fmLetter.getAscent() + 5);
            }
        }
    }
}