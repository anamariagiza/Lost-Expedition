package PaooGame.States;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Utils.DatabaseManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/*!
 * \class public class PuzzleState extends State
 * \brief Implementeaza starea de joc dedicata rezolvarii unui puzzle de logica/memorare.
 */
public class PuzzleState extends State {

    // Detalii vizuale pentru UI
    private final Color backgroundColor = new Color(0, 0, 0, 200);
    private final Color textColor = new Color(255, 255, 0);
    private final Color instructionColor = new Color(200, 200, 200);
    private final Color buttonColor = new Color(100, 100, 100);
    private final Color selectedColor = new Color(160, 82, 45);
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 32);
    private final Font textFont = new Font("Arial", Font.BOLD, 20);
    private final Font timerFont = new Font("Arial", Font.BOLD, 28);
    private final Font inputFont = new Font("Consolas", Font.BOLD, 24);
    private final Font instructionFont = new Font("SansSerif", Font.PLAIN, 14);


    // Elemente puzzle
    private String[] symbols = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private ArrayList<String> puzzleSequence;
    private ArrayList<String> playerInput;

    private int sequenceLength = 4;
    private int currentInputIndex = 0;
    private boolean sequenceShown = false;
    private long sequenceDisplayTime = 0;
    private final long DISPLAY_DURATION_MS = 2000;

    // Cronometru puzzle
    private long puzzleStartTime = 0;
    private final long TIME_LIMIT_MS = 15000;
    private boolean puzzleActive = false;
    private boolean puzzleSolved = false;
    private boolean puzzleFailed = false;

    private boolean enterPressed = false;
    private long lastKeyPressTime = 0;
    private final long KEY_COOLDOWN_MS = 150;


    /*!
     * \fn public PuzzleState(RefLinks refLink)
     * \brief Constructorul de initializare al clasei PuzzleState.
     * \param refLink O referinta catre un obiect "shortcut".
     */
    public PuzzleState(RefLinks refLink) {
        super(refLink);
        System.out.println("✓ PuzzleState initializat");
        generatePuzzle();
    }

    /*!
     * \fn private void generatePuzzle()
     * \brief Genereaza o noua secventa de simboluri pentru puzzle.
     */
    private void generatePuzzle() {
        puzzleSequence = new ArrayList<>();
        playerInput = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < sequenceLength; i++) {
            puzzleSequence.add(symbols[rand.nextInt(symbols.length)]);
        }
        sequenceShown = true;
        sequenceDisplayTime = System.currentTimeMillis();
        System.out.println("DEBUG Puzzle: Secventa generata: " + String.join(" ", puzzleSequence));
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea curenta a puzzle-ului.
     */
    @Override
    public void Update() {
        if (puzzleSolved || puzzleFailed) {
            if ((refLink.GetKeyManager().enter || refLink.GetKeyManager().space) && !enterPressed) {
                enterPressed = true;
                if (puzzleSolved) {
                    handlePuzzleSuccess();
                } else {
                    handlePuzzleFailure();
                }
            } else if (!refLink.GetKeyManager().enter && !refLink.GetKeyManager().space) {
                enterPressed = false;
            }
            return;
        }

        if (sequenceShown) {
            if (System.currentTimeMillis() - sequenceDisplayTime > DISPLAY_DURATION_MS) {
                sequenceShown = false;
                puzzleActive = true;
                puzzleStartTime = System.currentTimeMillis();
                currentInputIndex = 0;
                playerInput.clear();
                System.out.println("DEBUG Puzzle: Incepe faza de introducere. Timp ramas: " + TIME_LIMIT_MS / 1000 + "s");
            }
        } else if (puzzleActive) {
            if (System.currentTimeMillis() - puzzleStartTime > TIME_LIMIT_MS) {
                puzzleFailed = true;
                puzzleActive = false;
                System.out.println("DEBUG Puzzle: Timpul a expirat! Puzzle esuat.");
            } else {
                handleInput();
            }
        }
    }

    /*!
     * \fn private void handleInput()
     * \brief Gestioneaza input-ul de la tastatura pentru introducerea simbolurilor.
     */
    private void handleInput() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastKeyPressTime < KEY_COOLDOWN_MS) {
            return;
        }

        String inputSymbol = null;
        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_1)) inputSymbol = symbols[0];
        else if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_2)) inputSymbol = symbols[1];
        else if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_3)) inputSymbol = symbols[2];
        else if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_4)) inputSymbol = symbols[3];
        else if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_5)) inputSymbol = symbols[4];
        else if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_6)) inputSymbol = symbols[5];
        else if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_7)) inputSymbol = symbols[6];
        else if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_8)) inputSymbol = symbols[7];

        if (inputSymbol != null) {
            lastKeyPressTime = currentTime;
            playerInput.add(inputSymbol);
            System.out.println("DEBUG Puzzle: Input jucator: " + inputSymbol + " la index " + currentInputIndex);

            if (currentInputIndex < puzzleSequence.size()) {
                if (!playerInput.get(currentInputIndex).equals(puzzleSequence.get(currentInputIndex))) {
                    puzzleFailed = true;
                    puzzleActive = false;
                    System.out.println("DEBUG Puzzle: Simbol incorect! Puzzle esuat.");
                    return;
                }
            }
            currentInputIndex++;

            if (currentInputIndex == sequenceLength) {
                if (!puzzleFailed) {
                    puzzleSolved = true;
                }
                puzzleActive = false;
            }
        }
    }



    /*!
     * \fn private void handlePuzzleSuccess()
     * \brief Gestioneaza actiunile la rezolvarea cu succes a puzzle-ului.
     */
    private void handlePuzzleSuccess() {
        System.out.println("DEBUG Puzzle: Puzzle rezolvat cu succes!");
        State previousState = refLink.GetPreviousState();
        if (previousState instanceof GameState) {
            GameState gameState = (GameState) previousState;
            gameState.puzzleSolved();
            if (gameState.getPuzzlesSolved() >= gameState.getTotalPuzzlesLevel2()) {
                gameState.doorKeyCollected();
            }
        }
        refLink.SetState(previousState);
    }

    /*!
     * \fn private void handlePuzzleFailure()
     * \brief Gestioneaza actiunile la esuarea puzzle-ului.
     */
    private void handlePuzzleFailure() {
        System.out.println("DEBUG Puzzle: Puzzle esuat! Capcana activata sau Game Over.");
        refLink.SetState(new GameOverState(refLink));
    }


    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza (randeaza) pe ecran starea puzzle-ului.
     * \param g Contextul grafic.
     */
    @Override
    public void Draw(Graphics g) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        int centerX = refLink.GetWidth() / 2;
        int centerY = refLink.GetHeight() / 2;

        g.setColor(textColor);
        g.setFont(titleFont);
        String title = "REZOVALA PUZZLE-UL!";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, centerX - fm.stringWidth(title) / 2, centerY - 150);

        g.setFont(textFont);
        if (sequenceShown) {
            String seqStr = String.join("   ", puzzleSequence);
            g.drawString("MEMOREAZA: " + seqStr, centerX - fm.stringWidth("MEMOREAZA: " + seqStr) / 2, centerY - 50);
            g.setColor(instructionColor);
            g.setFont(instructionFont);
            String instruction = "Secventa va disparea in " + String.format("%.1f", (float)(DISPLAY_DURATION_MS - (System.currentTimeMillis() - sequenceDisplayTime)) / 1000f) + "s...";
            g.drawString(instruction, centerX - fm.stringWidth(instruction) / 2, centerY + 10);
        } else if (puzzleActive) {
            String inputStr = String.join("   ", playerInput);
            String placeholderStr = "";
            for (int i = playerInput.size(); i < sequenceLength; i++) {
                placeholderStr += "_   ";
            }
            g.drawString("INTRODU: " + inputStr + placeholderStr.trim(), centerX - fm.stringWidth("INTRODU: " + inputStr + placeholderStr.trim()) / 2, centerY - 50);

            long timeLeft = TIME_LIMIT_MS - (System.currentTimeMillis() - puzzleStartTime);
            g.setColor(textColor);
            g.setFont(timerFont);
            String timerStr = "Timp: " + String.format("%.1f", (float)timeLeft / 1000f) + "s";
            g.drawString(timerStr, centerX - fm.stringWidth(timerStr) / 2, centerY + 50);

            g.setColor(instructionColor);
            g.setFont(instructionFont);
            String instruction = "Apasa tastele 1-" + symbols.length + " pentru simboluri.";
            g.drawString(instruction, centerX - fm.stringWidth(instruction) / 2, centerY + 100);

        } else if (puzzleSolved) {
            g.setColor(Color.GREEN);
            g.drawString("PUZZLE REZOLVAT CU SUCCES!", centerX - fm.stringWidth("PUZZLE REZOLVAT CU SUCCES!") / 2, centerY - 50);
            g.setColor(instructionColor);
            g.drawString("Apasa ENTER/SPACE pentru a continua.", centerX - fm.stringWidth("Apasa ENTER/SPACE pentru a continua.") / 2, centerY + 50);
        } else if (puzzleFailed) {
            g.setColor(Color.RED);
            g.drawString("PUZZLE ESUAT! CAPCANA ACTIVATA!", centerX - fm.stringWidth("PUZZLE ESUAT! CAPCANA ACTIVATA!") / 2, centerY - 50);
            g.setColor(instructionColor);
            g.drawString("Apasa ENTER/SPACE pentru a continua.", centerX - fm.stringWidth("Apasa ENTER/SPACE pentru a continua.") / 2, centerY + 50);
        }
    }


}