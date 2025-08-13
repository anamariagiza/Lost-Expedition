package PaooGame.States;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Utils.DatabaseManager;
import PaooGame.Entities.Key;
import PaooGame.Tiles.Tile;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/*!
 * \class public class PuzzleState
 * \brief Implementeaza starea de joc dedicata rezolvarii unui puzzle de logica/memorare.
 */
public class PuzzleState extends State {

    private final Color backgroundColor = new Color(0, 0, 0, 200);
    private final Color textColor = new Color(255, 255, 0);
    private final Color instructionColor = new Color(200, 200, 200);
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 32);
    private final Font textFont = new Font("Arial", Font.BOLD, 20);
    private final Font timerFont = new Font("Arial", Font.BOLD, 28);
    private final Font instructionFont = new Font("SansSerif", Font.PLAIN, 14);
    private int puzzleId;
    private long puzzleStartTime = 0;
    private final long TIME_LIMIT_MS = 60000;
    private boolean puzzleActive = false;
    private boolean puzzleSolved = false;
    private boolean puzzleFailed = false;

    private String currentPuzzleTitle;
    private String currentObjective;
    private long messageDisplayTime = 0;
    private final long MESSAGE_DURATION_MS = 2000;
    // Puzzle 1: Potrivirea simbolurilor
    private String[][] grid1;
    private String playerChoice1;
    private final String[] symbols = {"SOARE", "LUNA", "STEAUA", "FULGER"};
    private final int SYMBOL_SIZE = 40;
    private List<BufferedImage> options1;
    private List<Rectangle> optionBounds1;

    // Puzzle 2: Ordinea pietrelor pretioase
    private List<String> correctOrder2;
    private List<String> playerOrder2;
    private String clue2;
    private final String[] gems = {"SAFIR", "SMARALD", "RUBIN", "DIAMANT"};
    private final int GEM_WIDTH = 50;
    private final int GEM_HEIGHT = 50;
    private BufferedImage[] gemSprites;
    private String selectedGem2 = null;
    private int wrongAttempts2 = 0;
    private final int MAX_WRONG_ATTEMPTS = 3;
    private List<Rectangle> gemBounds2;
    private List<Rectangle> dropZoneBounds2;
    // Puzzle 3: Ghicitoarea antica
    private String riddle3;
    private List<String> answers3;
    private int correctAnswerIndex3;
    private int selectedAnswerIndex3 = -1;
    private final String[] riddles = {
            "Am orase, dar nu am case. Am paduri, dar nu am copaci. Am apa, dar nu sunt peste. Ce sunt?",
            "Ma poti tine fara sa ma atingi. Ma poti sparge cu un singur cuvant. Ce sunt?"
    };
    private final String[][] riddleAnswers = {
            {"O harta", "Un ocean", "Un desert"},
            {"O sticla", "O promisiune", "Un balon"}
    };
    private final int[] correctRiddleAnswers = {0, 1};
    private List<Rectangle> answerBounds3;
    // Puzzle 4: Joc de matematica
    private List<String> questions4;
    private List<Integer> answers4;
    private String playerInput4 = "";
    private int currentQuestionIndex4 = 0;
    private final int TOTAL_QUESTIONS_4 = 6;
    private boolean waitingForInput4 = true;
    private int correctAnswersCount4 = 0;
    private Rectangle answerBox4;
    private String lastAnswerStatus4 = "";
    private long lastStatusTime4 = 0;
    // Puzzle 5: Găsește perechea
    private List<Integer> cardLayout5;
    private boolean[] revealedCards5;
    private int firstCardIndex5 = -1;
    private int secondCardIndex5 = -1;
    private int pairsFound5 = 0;
    private final int GRID_SIZE_5 = 4;
    private final int CARD_SIZE_5 = 60;
    private long cardRevealTime5 = 0;
    private final long CARD_REVEAL_DURATION_MS = 1000;
    private List<Rectangle> cardBounds5;
    private boolean enterPressed = false;
    private long lastKeyPressTime = 0;
    private final long KEY_COOLDOWN_MS = 50;

    public PuzzleState(RefLinks refLink, int puzzleId) {
        super(refLink);
        this.puzzleId = puzzleId;
        generatePuzzle();
    }

    private void generatePuzzle() {
        puzzleActive = true;
        puzzleStartTime = System.currentTimeMillis();
        Random rand = new Random();
        switch (puzzleId) {
            case 1:
                currentPuzzleTitle = "Potrivirea simbolurilor";
                currentObjective = "Alege simbolul care lipsește (clic pe imagine).";
                grid1 = new String[3][3];
                grid1[0][0] = symbols[0]; grid1[0][1] = symbols[1];
                grid1[0][2] = symbols[2];
                grid1[2][0] = symbols[0]; grid1[2][1] = symbols[1]; grid1[2][2] = symbols[2];
                grid1[1][0] = symbols[3]; grid1[1][2] = symbols[3];
                grid1[1][1] = "?";

                options1 = new ArrayList<>(Arrays.asList(
                        Assets.puzzle1Sun,
                        Assets.puzzle1Moon,
                        Assets.puzzle1Star,
                        Assets.puzzle1Bolt
                ));
                optionBounds1 = new ArrayList<>();
                break;
            case 2:
                currentPuzzleTitle = "Ordinea pietrelor prețioase";
                currentObjective = "Aseaza pietrele in ordine (clic pe pietre).";
                correctOrder2 = Arrays.asList(gems[0], gems[1], gems[2], gems[3]);
                playerOrder2 = new ArrayList<>(Arrays.asList("?", "?", "?", "?"));
                clue2 = "Smaraldul e între rubin și diamant.";
                if (Assets.puzzle2Gems != null) {
                    gemSprites = new BufferedImage[4];
                    gemSprites[0] = Assets.puzzle2Gems.getSubimage(0, 0, GEM_WIDTH, GEM_HEIGHT);
                    gemSprites[1] = Assets.puzzle2Gems.getSubimage(GEM_WIDTH, 0, GEM_WIDTH, GEM_HEIGHT);
                    gemSprites[2] = Assets.puzzle2Gems.getSubimage(0, GEM_HEIGHT, GEM_WIDTH, GEM_HEIGHT);
                    gemSprites[3] = Assets.puzzle2Gems.getSubimage(GEM_WIDTH, GEM_HEIGHT, GEM_WIDTH, GEM_HEIGHT);
                }
                gemBounds2 = new ArrayList<>();
                dropZoneBounds2 = new ArrayList<>();
                break;
            case 3:
                currentPuzzleTitle = "Ghicitoarea antica";
                currentObjective = "Alege raspunsul corect (clic pe chenar).";
                int riddleIndex = rand.nextInt(riddles.length);
                riddle3 = riddles[riddleIndex];
                answers3 = Arrays.asList(riddleAnswers[riddleIndex]);
                correctAnswerIndex3 = correctRiddleAnswers[riddleIndex];
                answerBounds3 = new ArrayList<>();
                break;
            case 4:
                currentPuzzleTitle = "Joc de matematica";
                currentObjective = "Rezolva 6 probleme de matematica in 60 de secunde.";
                questions4 = new ArrayList<>();
                answers4 = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    int a = rand.nextInt(50) + 1;
                    int b = rand.nextInt(50) + 1;
                    questions4.add(a + " + " + b + " = ?");
                    answers4.add(a + b);
                }
                for (int i = 0; i < 3; i++) {
                    int a = rand.nextInt(50) + 20;
                    int b = rand.nextInt(a - 10) + 1;
                    questions4.add(a + " - " + b + " = ?");
                    answers4.add(a - b);
                }
                break;
            case 5:
                currentPuzzleTitle = "Găsește perechea";
                currentObjective = "Gaseste toate perechile de carti inainte de a expira timpul.";

                List<Integer> tempCardIds = new ArrayList<>();
                for(int i = 0; i < 8; i++) {
                    tempCardIds.add(i);
                    tempCardIds.add(i);
                }
                Collections.shuffle(tempCardIds);
                cardLayout5 = tempCardIds;

                revealedCards5 = new boolean[16];
                cardBounds5 = new ArrayList<>();
                break;
        }
    }

    @Override
    public void Update() {
        if (puzzleSolved || puzzleFailed) {
            // Fix: Permite în continuare apăsarea tastei ENTER pentru a ieși
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_ENTER)) {
                if (puzzleSolved) {
                    handlePuzzleSuccess();
                } else {
                    handlePuzzleFailure();
                }
            }
            return;
        }

        if (puzzleActive) {
            if (System.currentTimeMillis() - puzzleStartTime > TIME_LIMIT_MS) {
                puzzleFailed = true;
                puzzleActive = false;
            } else {
                handleInput();
            }

            // Validare automată pentru puzzle-ul 2
            if (puzzleId == 2 && playerOrder2.stream().noneMatch("?"::equals)) {
                if (checkOrder()) {
                    puzzleSolved = true;
                    puzzleActive = false;
                } else {
                    wrongAttempts2++;
                    if (wrongAttempts2 >= MAX_WRONG_ATTEMPTS) {
                        puzzleFailed = true;
                        puzzleActive = false;
                    } else {
                        playerOrder2.clear();
                        for (int i = 0; i < 4; i++) {
                            playerOrder2.add("?");
                        }
                    }
                }
            }

            // Validare automată pentru puzzle-ul 4
            if (puzzleId == 4 && currentQuestionIndex4 >= TOTAL_QUESTIONS_4) {
                puzzleSolved = true;
                puzzleActive = false;
            }

            if (puzzleId == 4 && !lastAnswerStatus4.isEmpty() && System.currentTimeMillis() - lastStatusTime4 > MESSAGE_DURATION_MS) {
                lastAnswerStatus4 = "";
            }

            // Logica pentru a verifica potrivirea cartilor la puzzle-ul 5
            if (puzzleId == 5 && cardRevealTime5 > 0 && System.currentTimeMillis() - cardRevealTime5 > CARD_REVEAL_DURATION_MS) {
                // Logica pentru a ascunde cărțile dacă nu se potrivesc
                if (cardLayout5.get(firstCardIndex5).equals(cardLayout5.get(secondCardIndex5))) {
                    pairsFound5++;
                    if (pairsFound5 >= 8) {
                        puzzleSolved = true;
                        puzzleActive = false;
                    }
                } else {
                    revealedCards5[firstCardIndex5] = false;
                    revealedCards5[secondCardIndex5] = false;
                }
                firstCardIndex5 = -1;
                secondCardIndex5 = -1;
                cardRevealTime5 = 0;
            }
        }
    }

    private void handleInput() {
        if (puzzleId == 4) {
            handleMathInput();
            return;
        }

        if (refLink.GetMouseManager().isMouseJustClicked()) {
            int mouseX = refLink.GetMouseManager().getMouseX();
            int mouseY = refLink.GetMouseManager().getMouseY();

            switch (puzzleId) {
                case 1:
                    for (int i = 0; i < optionBounds1.size(); i++) {
                        if (optionBounds1.get(i).contains(mouseX, mouseY)) {
                            playerChoice1 = symbols[i];
                            if (playerChoice1.equals(symbols[3])) {
                                puzzleSolved = true;
                            } else {
                                puzzleFailed = true;
                            }
                            puzzleActive = false;
                            break;
                        }
                    }
                    break;
                case 2:
                    for (int i = 0; i < gemBounds2.size(); i++) {
                        if (gemBounds2.get(i).contains(mouseX, mouseY)) {
                            placeGem(i);
                            break;
                        }
                    }
                    break;
                case 3:
                    for (int i = 0; i < answerBounds3.size(); i++) {
                        if (answerBounds3.get(i).contains(mouseX, mouseY)) {
                            selectedAnswerIndex3 = i;
                            if (selectedAnswerIndex3 == correctAnswerIndex3) {
                                puzzleSolved = true;
                            } else {
                                puzzleFailed = true;
                            }
                            puzzleActive = false;
                            break;
                        }
                    }
                    break;
                case 5:
                    if (cardRevealTime5 == 0) {
                        for (int i = 0; i < cardBounds5.size(); i++) {
                            if (cardBounds5.get(i).contains(mouseX, mouseY) && !revealedCards5[i]) {
                                if (firstCardIndex5 == -1) {
                                    firstCardIndex5 = i;
                                    revealedCards5[i] = true;
                                } else if (secondCardIndex5 == -1 && i != firstCardIndex5) {
                                    secondCardIndex5 = i;
                                    revealedCards5[i] = true;
                                    cardRevealTime5 = System.currentTimeMillis();
                                }
                                break;
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void handleMathInput() {
        if (waitingForInput4) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_ENTER)) {
                if (currentQuestionIndex4 >= TOTAL_QUESTIONS_4) {
                    puzzleSolved = true;
                    puzzleActive = false;
                    return;
                }
                try {
                    if (Integer.parseInt(playerInput4) == answers4.get(currentQuestionIndex4)) {
                        correctAnswersCount4++;
                        lastAnswerStatus4 = "CORECT!";
                        lastStatusTime4 = System.currentTimeMillis();
                        playerInput4 = "";
                        currentQuestionIndex4++;
                        if (currentQuestionIndex4 >= TOTAL_QUESTIONS_4) {
                            puzzleSolved = true;
                            puzzleActive = false;
                        }
                    } else {
                        lastAnswerStatus4 = "INCORECT!";
                        lastStatusTime4 = System.currentTimeMillis();
                        playerInput4 = "";
                    }
                } catch (NumberFormatException e) {
                    playerInput4 = "";
                }
            } else {
                for (int i = 0; i <= 9; i++) {
                    if (refLink.GetKeyManager().isKeyJustPressed(i + 48)) {
                        playerInput4 += (char) (i + 48);
                    }
                }
                if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_BACK_SPACE)) {
                    if (!playerInput4.isEmpty()) {
                        playerInput4 = playerInput4.substring(0, playerInput4.length() - 1);
                    }
                }
            }
        }
    }

    private void placeGem(int gemIndex) {
        String gemToPlace = gems[gemIndex];
        int emptySlot = playerOrder2.indexOf("?");
        if (emptySlot != -1) {
            playerOrder2.set(emptySlot, gemToPlace);
        }
    }

    private boolean checkOrder() {
        return playerOrder2.equals(correctOrder2);
    }

    private void handlePuzzleSuccess() {
        if (refLink.GetPreviousState() instanceof GameState) {
            GameState gameState = (GameState) refLink.GetPreviousState();
            gameState.puzzleSolved(puzzleId);
        }
        refLink.SetState(refLink.GetPreviousState());
    }

    private void handlePuzzleFailure() {
        System.out.println("DEBUG Puzzle: Puzzle esuat! Capcana activata sau Game Over.");
        refLink.SetState(new GameOverState(refLink));
    }

    @Override
    public void Draw(Graphics g) {
        if (refLink.GetPreviousState() != null) {
            refLink.GetPreviousState().Draw(g);
        }

        g.setColor(backgroundColor);
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        int centerX = refLink.GetWidth() / 2;
        int centerY = refLink.GetHeight() / 2;

        g.setColor(textColor);
        g.setFont(titleFont);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(currentPuzzleTitle, centerX - fm.stringWidth(currentPuzzleTitle) / 2, centerY - 150);

        g.setFont(textFont);
        g.drawString(currentObjective, centerX - fm.stringWidth(currentObjective) / 2, centerY - 100);
        if(puzzleActive) {
            long timeLeft = TIME_LIMIT_MS - (System.currentTimeMillis() - puzzleStartTime);
            g.setFont(timerFont);
            g.setColor(Color.RED);
            String timerStr = "Timp: " + String.format("%.1f", (float)timeLeft / 1000f) + "s";
            g.drawString(timerStr, 10, 30);
        }

        switch (puzzleId) {
            case 1: drawPuzzle1(g, centerX, centerY); break;
            case 2: drawPuzzle2(g, centerX, centerY); break;
            case 3: drawPuzzle3(g, centerX, centerY); break;
            case 4: drawPuzzle4(g, centerX, centerY); break;
            case 5: drawPuzzle5(g, centerX, centerY); break;
        }

        if (puzzleSolved) {
            g.setColor(Color.GREEN);
            g.setFont(titleFont);
            String msg = "PUZZLE REZOLVAT CU SUCCES!";
            g.drawString(msg, centerX - fm.stringWidth(msg) / 2, centerY);
            g.setFont(instructionFont);
            g.setColor(instructionColor);
            String instruction = "Apasa ENTER pentru a continua.";
            g.drawString(instruction, centerX - fm.stringWidth(instruction) / 2, centerY + 50);
        } else if (puzzleFailed) {
            g.setColor(Color.RED);
            g.setFont(titleFont);
            String msg = "PUZZLE ESUAT! CAPCANA ACTIVATA!";
            g.drawString(msg, centerX - fm.stringWidth(msg) / 2, centerY);
            g.setFont(instructionFont);
            g.setColor(instructionColor);
            String instruction = "Apasa ENTER pentru a continua.";
            g.drawString(instruction, centerX - fm.stringWidth(instruction) / 2, centerY + 50);
        }
    }

    private void drawPuzzle1(Graphics g, int centerX, int centerY) {
        int gridSize = 3;
        int tileSize = 60;
        int gridX = centerX - (gridSize * tileSize) / 2;
        int gridY = centerY - (gridSize * tileSize) / 2;
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                int tileX = gridX + c * tileSize;
                int tileY = gridY + r * tileSize;
                g.setColor(Color.GRAY);
                g.fillRect(tileX, tileY, tileSize, tileSize);
                g.setColor(Color.BLACK);
                g.drawRect(tileX, tileY, tileSize, tileSize);
                BufferedImage image = null;
                switch (grid1[r][c]) {
                    case "SOARE": image = Assets.puzzle1Sun; break;
                    case "LUNA": image = Assets.puzzle1Moon; break;
                    case "STEAUA": image = Assets.puzzle1Star; break;
                    case "FULGER": image = Assets.puzzle1Bolt; break;
                }

                if (image != null) {
                    g.drawImage(image, tileX + (tileSize - SYMBOL_SIZE) / 2, tileY + (tileSize - SYMBOL_SIZE) / 2, SYMBOL_SIZE, SYMBOL_SIZE, null);
                }
            }
        }

        g.setFont(instructionFont);
        g.setColor(instructionColor);

        String optionsText = "Alege simbolul care lipsește.";
        g.drawString(optionsText, centerX - g.getFontMetrics().stringWidth(optionsText)/2, gridY + gridSize * tileSize + 50);
        int optionsY = gridY + gridSize * tileSize + 100;
        int optionWidth = 60;
        int optionSpacing = 20;
        int optionsTotalWidth = options1.size() * (optionWidth + optionSpacing);
        int startX = centerX - optionsTotalWidth / 2;
        optionBounds1.clear();
        for (int i = 0; i < options1.size(); i++) {
            int optionX = startX + i * (optionWidth + optionSpacing);
            g.setColor(Color.GRAY);
            g.fillRect(optionX, optionsY, optionWidth, optionWidth);
            g.setColor(Color.BLACK);
            g.drawRect(optionX, optionsY, optionWidth, optionWidth);
            optionBounds1.add(new Rectangle(optionX, optionsY, optionWidth, optionWidth));
            g.drawImage(options1.get(i), optionX + (optionWidth - SYMBOL_SIZE) / 2, optionsY + (optionWidth - SYMBOL_SIZE) / 2, SYMBOL_SIZE, SYMBOL_SIZE, null);
        }
    }

    private void drawPuzzle2(Graphics g, int centerX, int centerY) {
        g.setColor(Color.WHITE);
        g.setFont(textFont);
        FontMetrics fm = g.getFontMetrics();
        g.drawString("Indiciu: " + clue2, centerX - fm.stringWidth("Indiciu: " + clue2) / 2, centerY - 50);
        int pedestalSize = 60;
        int spacing = 20;
        int totalWidth = (4 * pedestalSize) + (3 * spacing);
        int startX = centerX - totalWidth / 2;

        for (int i = 0; i < 4; i++) {
            int x = startX + i * (pedestalSize + spacing);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, centerY, pedestalSize, pedestalSize);
            g.setColor(Color.WHITE);
            g.drawRect(x, centerY, pedestalSize, pedestalSize);
            if (!playerOrder2.get(i).equals("?")) {
                BufferedImage gemImage = null;
                switch (playerOrder2.get(i)) {
                    case "SAFIR": gemImage = gemSprites[0]; break;
                    case "SMARALD": gemImage = gemSprites[1]; break;
                    case "RUBIN": gemImage = gemSprites[2]; break;
                    case "DIAMANT": gemImage = gemSprites[3]; break;
                }

                if (gemImage != null) {
                    g.drawImage(gemImage, x + (pedestalSize - GEM_WIDTH) / 2, centerY + (pedestalSize - GEM_HEIGHT) / 2, GEM_WIDTH, GEM_HEIGHT, null);
                }
            }
        }

        int optionsY = centerY + pedestalSize + 50;
        int optionWidth = 60;
        int optionSpacing = 20;
        int optionsTotalWidth = gemSprites.length * (optionWidth + optionSpacing);
        int optionsStartX = centerX - optionsTotalWidth / 2;
        gemBounds2.clear();

        for (int i = 0; i < gemSprites.length; i++) {
            int optionX = optionsStartX + i * (optionWidth + optionSpacing);
            g.drawImage(gemSprites[i], optionX + (optionWidth - GEM_WIDTH)/2, optionsY + (optionWidth - GEM_HEIGHT)/2, GEM_WIDTH, GEM_HEIGHT, null);
            gemBounds2.add(new Rectangle(optionX, optionsY, optionWidth, optionWidth));
        }

        g.setFont(instructionFont);
        g.setColor(instructionColor);
        String optionsText = "Alege pietrele apasand pe ele.";
        g.drawString(optionsText, centerX - fm.stringWidth(optionsText)/2, optionsY + optionWidth + 40);

        String attemptsText = "Incercari ramase: " + (MAX_WRONG_ATTEMPTS - wrongAttempts2);
        g.drawString(attemptsText, centerX - fm.stringWidth(attemptsText)/2, optionsY + optionWidth + 70);
    }

    private void drawPuzzle3(Graphics g, int centerX, int centerY) {
        if (Assets.puzzle3Scroll != null) {
            int scrollWidth = 1000;
            int scrollHeight = 600;
            int scrollX = centerX - scrollWidth / 2;
            int scrollY = centerY - scrollHeight / 2;
            g.drawImage(Assets.puzzle3Scroll, scrollX, scrollY, scrollWidth, scrollHeight, null);

            g.setColor(Color.BLACK);
            g.setFont(textFont);
            FontMetrics fm = g.getFontMetrics();
            String riddleText = riddle3;
            g.drawString(riddleText, scrollX + (scrollWidth - fm.stringWidth(riddleText)) / 2, scrollY + 150);

            int answerWidth = 250;
            int answerHeight = 70;
            int answerX = centerX - (3 * answerWidth + 2 * 20) / 2;
            int answerY = centerY + 50;
            answerBounds3.clear();

            for (int i = 0; i < answers3.size(); i++) {
                int currentX = answerX + i * (answerWidth + 20);
                if (selectedAnswerIndex3 == i) {
                    g.setColor(new Color(255, 255, 0, 150));
                } else {
                    g.setColor(new Color(150, 150, 150, 150));
                }
                g.fillRect(currentX, answerY, answerWidth, answerHeight);
                g.setColor(Color.BLACK);
                g.drawRect(currentX, answerY, answerWidth, answerHeight);
                answerBounds3.add(new Rectangle(currentX, answerY, answerWidth, answerHeight));

                String answerText = answers3.get(i);
                int textWidth = fm.stringWidth(answerText);
                g.drawString(answerText, currentX + (answerWidth - textWidth) / 2, answerY + (answerHeight + fm.getAscent()) / 2);
            }
        } else {
            // Desenam placeholder-uri
        }
    }

    private void drawPuzzle4(Graphics g, int centerX, int centerY) {
        if (currentQuestionIndex4 >= TOTAL_QUESTIONS_4) {
            return;
        }

        g.setColor(Color.WHITE);
        g.setFont(textFont);
        FontMetrics fm = g.getFontMetrics();
        g.drawString("Problema: " + questions4.get(currentQuestionIndex4), centerX - fm.stringWidth("Problema: " + questions4.get(currentQuestionIndex4)) / 2, centerY - 50);
        g.drawString("Răspuns: " + playerInput4, centerX - fm.stringWidth("Răspuns: " + playerInput4) / 2, centerY + 20);
        g.drawString("Apăsați ENTER pentru a confirma.", centerX - fm.stringWidth("Apăsați ENTER pentru a confirma.") / 2, centerY + 80);
        if (!lastAnswerStatus4.isEmpty()) {
            String msg = lastAnswerStatus4;
            g.setColor(msg.equals("CORECT!") ? Color.GREEN : Color.RED);
            g.drawString(msg, centerX - fm.stringWidth(msg) / 2, centerY + 50);
        }
    }

    // Inlocuieste complet aceasta metoda in PuzzleState.java

    private void drawPuzzle5(Graphics g, int centerX, int centerY) {
        // Folosim noile constante pentru latime si inaltime
        int cardWidth = 62;
        int cardHeight = 86;
        int cardSpacing = 10;

        // Calculeaza pozitia de start a grilei de carti
        int totalGridWidth = GRID_SIZE_5 * (cardWidth + cardSpacing) - cardSpacing;
        int totalGridHeight = GRID_SIZE_5 * (cardHeight + cardSpacing) - cardSpacing;
        int startX = centerX - totalGridWidth / 2;
        int startY = centerY - totalGridHeight / 2 - 20; // Un pic mai sus pe ecran

        cardBounds5.clear();
        for (int i = 0; i < GRID_SIZE_5 * GRID_SIZE_5; i++) {
            int row = i / GRID_SIZE_5;
            int col = i % GRID_SIZE_5;
            int cardX = startX + col * (cardWidth + cardSpacing);
            int cardY = startY + row * (cardHeight + cardSpacing);

            // Adaugam dreptunghiul de coliziune cu dimensiunile corecte
            cardBounds5.add(new Rectangle(cardX, cardY, cardWidth, cardHeight));

            // Verificam daca trebuie sa desenam fata sau spatele cartii
            if (revealedCards5[i]) {
                int cardId = cardLayout5.get(i); // Obtinem ID-ul cartii (0-7)
                if (Assets.puzzle5CardFaces != null && cardId < Assets.puzzle5CardFaces.length) {
                    // Desenam fata corecta a cartii
                    g.drawImage(Assets.puzzle5CardFaces[cardId], cardX, cardY, cardWidth, cardHeight, null);
                }
            } else {
                // Desenam spatele cartii
                if (Assets.puzzle5CardBack != null) {
                    g.drawImage(Assets.puzzle5CardBack, cardX, cardY, cardWidth, cardHeight, null);
                }
            }
        }
    }
}