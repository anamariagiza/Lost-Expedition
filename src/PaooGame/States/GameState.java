package PaooGame.States;

import PaooGame.Entities.Player;
import PaooGame.Entities.Animal;
import PaooGame.Entities.Trap;
import PaooGame.Entities.Key;
import PaooGame.Entities.Agent;
import PaooGame.Entities.Entity;
import PaooGame.Entities.NPC;
import PaooGame.Entities.Talisman;
import PaooGame.Entities.CaveEntrance;
import PaooGame.Entities.PuzzleTrigger;
import PaooGame.Map.Map;
import PaooGame.Map.FogOfWar;
import PaooGame.RefLinks;
import PaooGame.Camera.GameCamera;
import PaooGame.Tiles.Tile;
import PaooGame.Utils.DatabaseManager;
import PaooGame.Graphics.Assets;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;

/*!
 * \class public class GameState extends State
 * \brief Implementeaza starea de joc.
 * In aceasta stare se desfasoara propriu-zis jocul: harta este incarcata si desenata,
 * jucatorul interactioneaza cu lumea etc.
 */
public class GameState extends State {

    private Map currentMap;
    private Player player;
    private FogOfWar fogOfWar;
    private String[] levelPaths = {"/maps/level_1.tmx", "/maps/level_2.tmx", "/maps/level_3.tmx"};
    private int currentLevelIndex;
    private boolean hasLevelKey = false;
    private boolean hasDoorKey = false;
    private boolean hasTalisman = false;
    private boolean caveEntranceUnlocked = false;
    private float currentZoomTarget = 1.0f;
    private boolean loadFromSaveOnInit = false;
    private String currentObjective = "Aduna obiectele necesare si ajungi la intrarea pesterii.";

    private ArrayList<Entity> entities;
    private boolean[] puzzlesSolved;

    private NPC caveGuardianNPC;
    private CaveEntrance caveEntrance;

    private String collectionMessage = null;
    private long collectionMessageTime = 0;
    private final long MESSAGE_DURATION_MS = 2000;
    private final int TOTAL_PUZZLES_LEVEL2 = 5;

    private long lastAnimalDamageTime = 0;
    private final long ANIMAL_DAMAGE_COOLDOWN_MS = 4000;
    private long lastTrapDamageTime = 0;
    private final long TRAP_DAMAGE_COOLDOWN_MS = 2000;
    private final int TRAP_DAMAGE_PERCENTAGE = 50;


    /*!
     * \fn public GameState(RefLinks refLink)
     * \brief Constructorul de initializare al clasei GameState.
     * Folosit pentru a porni un joc nou.
     * \param refLink O referinta catre un obiect "shortcut".
     */
    public GameState(RefLinks refLink) {
        super(refLink);
        this.currentLevelIndex = 0;
        this.hasLevelKey = false;
        this.hasDoorKey = false;
        this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];
        this.hasTalisman = false;
        this.caveEntranceUnlocked = false;
        InitLevelInternal(this.currentLevelIndex, false);
        System.out.println("✓ GameState initializat (joc nou)");
        refLink.GetGameCamera().setZoomLevel(currentZoomTarget);
    }

    /*!
     * \fn public GameState(RefLinks refLink, int desiredLevelIndex)
     * \brief Constructor de initializare care incepe jocul de la un nivel specificat.
     * \param refLink O referinta catre un obiect "shortcut".
     * \param desiredLevelIndex Indexul nivelului de la care sa inceapa jocul.
     */
    public GameState(RefLinks refLink, int desiredLevelIndex) {
        super(refLink);
        this.currentLevelIndex = desiredLevelIndex;
        this.hasLevelKey = false;
        this.hasDoorKey = false;
        this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];
        this.hasTalisman = false;
        this.caveEntranceUnlocked = false;
        InitLevelInternal(this.currentLevelIndex, false);
        System.out.println("✓ GameState initializat (nivel specificat: " + (desiredLevelIndex + 1) + ")");
        refLink.GetGameCamera().setZoomLevel(currentZoomTarget);
    }

    /*!
     * \fn public GameState(RefLinks refLink, boolean loadFromSave)
     * \brief Constructor de initializare extins, care permite incarcarea dintr-un fisier de salvare.
     * \param refLink O referinta catre un obiect "shortcut".
     * \param loadFromSave Daca este true, incearca sa incarca progresul din baza de date.
     */
    public GameState(RefLinks refLink, boolean loadFromSave) {
        super(refLink);
        this.loadFromSaveOnInit = loadFromSave;
        this.currentLevelIndex = 0;
        this.hasLevelKey = false;
        this.hasDoorKey = false;
        this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];
        this.hasTalisman = false;
        this.caveEntranceUnlocked = false;
        InitLevelInternal(this.currentLevelIndex, loadFromSaveOnInit);
        System.out.println("✓ GameState initializat (loadFromSave: " + loadFromSaveOnInit + ")");
        refLink.GetGameCamera().setZoomLevel(currentZoomTarget);
    }

    /*!
     * \fn private void updateObjectiveText()
     * \brief Actualizeaza textul obiectivului curent in functie de nivel.
     */
    private void updateObjectiveText() {
        // Obiectivul va fi acum afișat static în drawUI
    }

    /*!
     * \fn public void InitLevelInternal(int desiredLevelIndex, boolean loadPlayerStateFromDb)
     * \brief Metoda interna de initializare a unui nivel, incarcand harta si pozitionand player-ul si camera.
     * \param desiredLevelIndex Nivelul la care dorim sa trecem (ou de la care sa incarcam).
     * \param loadPlayerStateFromDb Daca este true, se incearca incarcarea pozitiei si vietii jucatorului din DB.
     * Altfel, jucatorul este plasat la pozitia initiala a nivelului.
     */
    public void InitLevelInternal(int desiredLevelIndex, boolean loadPlayerStateFromDb) {
        DatabaseManager.SaveGameData loadedData = null;
        float playerStartX = 100;
        float playerStartY = 100;

        if (loadFromSaveOnInit) {
            loadedData = refLink.GetDatabaseManager().loadGameData();
            if (loadedData != null) {
                this.currentLevelIndex = loadedData.levelIndex;
                playerStartX = loadedData.playerX;
                playerStartY = loadedData.playerY;
                this.hasLevelKey = loadedData.hasKey;
                this.hasDoorKey = loadedData.hasDoorKey;

                String solvedPuzzles = loadedData.puzzlesSolvedString;
                if (solvedPuzzles != null && !solvedPuzzles.isEmpty()) {
                    String[] solvedIds = solvedPuzzles.split(",");
                    for (String id : solvedIds) {
                        try {
                            int solvedId = Integer.parseInt(id);
                            if (solvedId > 0 && solvedId <= TOTAL_PUZZLES_LEVEL2) {
                                puzzlesSolved[solvedId] = true;
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Eroare la parsarea ID-ului de puzzle salvat: " + id);
                        }
                    }
                }

                System.out.println("DEBUG GameState: Incarcare nivel din salvare: " + (currentLevelIndex + 1));
            } else {
                System.out.println("DEBUG GameState: Nu s-au putut incarca date salvate. Se porneste un joc nou (Nivel 1).");
                this.currentLevelIndex = 0;
                this.hasLevelKey = false;
                this.hasDoorKey = false;
                this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];
            }
        } else {
            this.currentLevelIndex = desiredLevelIndex;
            this.hasLevelKey = false;
            this.hasDoorKey = false;
            this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];

            if (currentLevelIndex == 0) { // Nivel 1 (Jungla)
                playerStartX = 86 * Tile.TILE_WIDTH;
                playerStartY = 86 * Tile.TILE_HEIGHT;
            } else if (currentLevelIndex == 1) { // Nivelul 2 (Pestera)
                playerStartX = 2 * Tile.TILE_WIDTH;
                playerStartY = 26 * Tile.TILE_HEIGHT;
            } else if (currentLevelIndex == 2) { // Nivelul 3
                playerStartX = 29 * Tile.TILE_WIDTH;
                playerStartY = 38 * Tile.TILE_HEIGHT;
            }
        }

        if (this.currentLevelIndex < 0 || this.currentLevelIndex >= levelPaths.length) {
            System.err.println("Index nivel invalid: " + this.currentLevelIndex + ". Se reseteaza la nivelul 1.");
            this.currentLevelIndex = 0;
            this.hasLevelKey = false;
            this.hasDoorKey = false;
            this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];
        }

        currentMap = refLink.GetMap();
        if (currentMap != null) {
            currentMap.LoadMapFromFile(levelPaths[this.currentLevelIndex]);
            System.out.println("DEBUG GameState: Nivelul " + (this.currentLevelIndex + 1) + " incarcat: " + levelPaths[this.currentLevelIndex]);
            fogOfWar = new FogOfWar(refLink, currentMap.getWidth(), currentMap.getHeight());
        } else {
            System.err.println("Eroare: Obiectul Map nu a fost initializat in RefLinks!");
            return;
        }

        player = refLink.GetPlayer();
        if (player != null) {
            player.SetPosition(playerStartX, playerStartY);
            if (loadedData != null) {
                player.setHealth(loadedData.playerHealth);
            } else {
                player.resetHealth();
            }
            System.out.println("DEBUG GameState: Jucator pozitionat la X:" + player.GetX() + ", Y:" + player.GetY() + ", HP:" + player.getHealth());
        } else {
            System.err.println("Eroare: Obiectul Player nu a fost initializat in RefLinks!");
        }

        if (currentMap != null && player != null) {
            refLink.GetGameCamera().centerOnEntity(player);
        }

        entities = new ArrayList<>();
        switch (currentLevelIndex) {
            case 0: // Nivelul 1 (Jungla)
                entities.add(new Animal(refLink, 53 * Tile.TILE_WIDTH, 5 * Tile.TILE_HEIGHT, 51 * Tile.TILE_WIDTH, 56 * Tile.TILE_WIDTH, Animal.AnimalType.JAGUAR));
                entities.add(new Animal(refLink, 10 * Tile.TILE_WIDTH, 36 * Tile.TILE_HEIGHT, 8 * Tile.TILE_WIDTH, 11 * Tile.TILE_WIDTH, Animal.AnimalType.MONKEY));
                entities.add(new Animal(refLink, 89 * Tile.TILE_WIDTH, 29 * Tile.TILE_HEIGHT, 88 * Tile.TILE_WIDTH, 91 * Tile.TILE_WIDTH, Animal.AnimalType.MONKEY));
                entities.add(new Animal(refLink, 84 * Tile.TILE_WIDTH, 57 * Tile.TILE_HEIGHT, 82 * Tile.TILE_WIDTH, 85 * Tile.TILE_WIDTH, Animal.AnimalType.BAT));
                entities.add(new Trap(refLink, 66 * Tile.TILE_WIDTH, 31 * Tile.TILE_HEIGHT, Assets.spikeTrapImage));
                entities.add(new Trap(refLink, 67 * Tile.TILE_WIDTH, 38 * Tile.TILE_HEIGHT, Assets.spikeTrapImage));
                entities.add(new Trap(refLink, 66 * Tile.TILE_WIDTH, 45 * Tile.TILE_HEIGHT, Assets.spikeTrapImage));

                caveGuardianNPC = new NPC(refLink, 85 * Tile.TILE_WIDTH, 90 * Tile.TILE_HEIGHT);
                entities.add(caveGuardianNPC);
                entities.add(new Talisman(refLink, 45 * Tile.TILE_WIDTH, 52 * Tile.TILE_HEIGHT, Assets.talismanImage));

                caveEntrance = new CaveEntrance(refLink, 85 * Tile.TILE_WIDTH, 92 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH * 3, Tile.TILE_HEIGHT * 3);
                entities.add(caveEntrance);
                if (!hasDoorKey) {
                    entities.add(new Key(refLink, 12 * Tile.TILE_WIDTH, 85 * Tile.TILE_HEIGHT, Assets.keyImage, Key.KeyType.DOOR_KEY));
                }
                break;
            case 1: // Nivelul 2 (Pestera)
                if (!isPuzzleSolved(1)) entities.add(new PuzzleTrigger(refLink, 19 * Tile.TILE_WIDTH, 20 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 1));
                if (!isPuzzleSolved(2)) entities.add(new PuzzleTrigger(refLink, 36 * Tile.TILE_WIDTH, 14 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 2));
                if (!isPuzzleSolved(3)) entities.add(new PuzzleTrigger(refLink, 53 * Tile.TILE_WIDTH, 20 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 3));
                if (!isPuzzleSolved(4)) entities.add(new PuzzleTrigger(refLink, 70 * Tile.TILE_WIDTH, 11 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 4));
                if (!isPuzzleSolved(5)) entities.add(new PuzzleTrigger(refLink, 87 * Tile.TILE_WIDTH, 20 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 5));
                break;
            case 2: // Nivelul 3
                entities.add(new Animal(refLink, 450, 450, 400, 500, Animal.AnimalType.JAGUAR));
                entities.add(new Agent(refLink, 600, 600, 550, 650, true));
                break;
        }
    }

    @Override
    public void Update() {
        if (collectionMessage != null && System.currentTimeMillis() - collectionMessageTime > MESSAGE_DURATION_MS) {
            collectionMessage = null;
        }

        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_P) && player != null) {
            saveCurrentState();
            refLink.SetStateWithPrevious(new PauseState(refLink));
            return;
        }

        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_H)) {
            if (player != null) {
                player.takeDamage(20);
            }
        }
        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_G)) {
            if (player != null) {
                player.takeDamage(-20);
            }
        }

        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_Z)) {
            if (currentZoomTarget == 1.0f) {
                currentZoomTarget = 1.5f;
            } else {
                currentZoomTarget = 1.0f;
            }
            refLink.GetGameCamera().setZoomLevel(currentZoomTarget);
            System.out.println("Zoom Level comutat la: " + currentZoomTarget);
        }

        if (currentLevelIndex == 1) {
            if (getPuzzlesSolvedCount() >= TOTAL_PUZZLES_LEVEL2 && !hasDoorKey) {
                doorKeyCollected();
            }
        }

        if (currentMap != null) {
            currentMap.Update();
        }

        if (fogOfWar != null) {
            fogOfWar.update();
        }

        if (player != null) {
            player.Update();
            if (player.getHealth() <= 0) {
                refLink.SetState(new GameOverState(refLink));
                return;
            }
        }

        boolean playerInContactWithAnimal = false;
        boolean playerInContactWithTrap = false;

        if (currentLevelIndex == 0) {
            if (caveGuardianNPC != null && player.GetBounds().intersects(caveGuardianNPC.GetBounds())) {
                if (hasTalisman && refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                    setCaveEntranceUnlocked(true);
                    removeTalismanFromInventory();
                } else if (!hasTalisman && refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                    collectionMessage = "Nu am talismanul! Trebuie sa-l gasesti pentru a intra.";
                    collectionMessageTime = System.currentTimeMillis();
                }
            }

            if (caveEntrance != null && player.GetBounds().intersects(caveEntrance.GetBounds()) && isCaveEntranceUnlocked()) {
                passToLevel2();
                return;
            }
        }

        if (currentLevelIndex == 1) {
            if (hasDoorKey && refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E) && player.GetBounds().intersects(new Rectangle(200, 200, 100, 100))) {
                openDoorAtLevel2();
                hasDoorKey = false;
            }
        }

        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity e = it.next();
            e.Update();
            if (e instanceof Key) {
                Key k = (Key) e;
                if (k.isCollected()) {
                    it.remove();
                }
            } else if (e instanceof Talisman) {
                Talisman t = (Talisman) e;
                if (t.isCollected()) {
                    it.remove();
                }
            }
            if (e instanceof Animal) {
                if (player.GetBounds().intersects(e.GetBounds())) {
                    playerInContactWithAnimal = true;
                }
            }
            if (e instanceof Trap) {
                if (player.GetBounds().intersects(e.GetBounds())) {
                    playerInContactWithTrap = true;
                }
            }
            if (e instanceof PuzzleTrigger) {
                if (isPuzzleSolved(((PuzzleTrigger) e).getPuzzleId())) {
                    it.remove();
                }
            }
        }

        if (playerInContactWithAnimal) {
            if (System.currentTimeMillis() - lastAnimalDamageTime >= ANIMAL_DAMAGE_COOLDOWN_MS) {
                if(player != null) {
                    for(Entity e : entities) {
                        if (e instanceof Animal && player.GetBounds().intersects(e.GetBounds())) {
                            player.takeDamage(((Animal) e).getDamage());
                            break;
                        }
                    }
                    lastAnimalDamageTime = System.currentTimeMillis();
                }
            }
        }

        if (playerInContactWithTrap) {
            if (System.currentTimeMillis() - lastTrapDamageTime >= TRAP_DAMAGE_COOLDOWN_MS) {
                if (player != null) {
                    int damage = player.getMaxHealth() * TRAP_DAMAGE_PERCENTAGE / 100;
                    player.takeDamage(damage);
                    lastTrapDamageTime = System.currentTimeMillis();
                }
            }
        }
    }

    public void openDoorAtLevel2() {
        if (currentMap != null && currentLevelIndex == 1) {
            int layerIndex = 0;
            currentMap.changeTileGid(56, 57, 60, layerIndex);
            currentMap.changeTileGid(88, 89, 92, layerIndex);
            currentMap.changeTileGid(56, 58, 61, layerIndex);
            currentMap.changeTileGid(88, 90, 93, layerIndex);
            collectionMessage = "Usa s-a deschis!";
            collectionMessageTime = System.currentTimeMillis();
        }
    }

    @Override
    public void Draw(Graphics g) {
        if (collectionMessage != null && System.currentTimeMillis() - collectionMessageTime > MESSAGE_DURATION_MS) {
            collectionMessage = null;
        }

        if (currentMap != null) {
            drawFullMap(g);
        }

        for (Entity e : entities) {
            e.Draw(g);
        }

        if (player != null) {
            player.Draw(g);
        }

        drawRadialFogOverlay(g);

        drawUI(g);
    }

    // Metode de desenare repuse aici pentru a rezolva erorile
    private void drawFullMap(Graphics g) {
        if (currentMap == null) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
            return;
        }

        GameCamera camera = refLink.GetGameCamera();
        float zoom = camera.getZoomLevel();
        int xStart = (int) Math.max(0, camera.getxOffset() / Tile.TILE_WIDTH);
        int xEnd = (int) Math.min(currentMap.getWidth(), (camera.getxOffset() + (refLink.GetWidth() / zoom)) / Tile.TILE_WIDTH + 1);
        int yStart = (int) Math.max(0, camera.getyOffset() / Tile.TILE_HEIGHT);
        int yEnd = (int) Math.min(currentMap.getHeight(), (camera.getyOffset() + (refLink.GetHeight() / zoom)) / Tile.TILE_HEIGHT + 1);
        for (int[][] currentLayerGids : currentMap.getTilesGidsLayers()) {
            for (int y = yStart; y < yEnd; y++) {
                for (int x = xStart; x < xEnd; x++) {
                    int gid = currentLayerGids[x][y];
                    if (gid == 0) continue;

                    Tile tile = Tile.GetTile(gid);
                    if (tile != null) {
                        int drawX = (int)((x * Tile.TILE_WIDTH - camera.getxOffset()) * zoom);
                        int drawY = (int)((y * Tile.TILE_HEIGHT - camera.getyOffset()) * zoom);
                        int scaledTileWidth = (int)(Tile.TILE_WIDTH * zoom);
                        int scaledTileHeight = (int)(Tile.TILE_HEIGHT * zoom);

                        tile.Draw(g, drawX, drawY, scaledTileWidth, scaledTileHeight, currentMap.getCurrentMapTilesetImage());
                    }
                }
            }
        }
    }

    private void drawRadialFogOverlay(Graphics g) {
        if (player == null) return;
        GameCamera camera = refLink.GetGameCamera();
        Graphics2D g2d = (Graphics2D) g.create();

        int playerScreenX = (int) ((player.GetX() - camera.getxOffset()) * camera.getZoomLevel() + player.GetWidth() / 2 * camera.getZoomLevel());
        int playerScreenY = (int) ((player.GetY() - camera.getyOffset()) * camera.getZoomLevel() + player.GetHeight() / 2 * camera.getZoomLevel());
        float radius = (float) (fogOfWar.getVisionRadius() * Tile.TILE_WIDTH * camera.getZoomLevel());

        Color transparentBlack = new Color(0, 0, 0, 0);
        Color opaqueBlack = new Color(0, 0, 0, 220);

        float[] dist = {0.0f, 0.7f, 1.0f};
        Color[] colors = {transparentBlack, transparentBlack, opaqueBlack};
        RadialGradientPaint p = new RadialGradientPaint(
                playerScreenX, playerScreenY,
                radius,
                dist,
                colors,
                MultipleGradientPaint.CycleMethod.NO_CYCLE
        );
        g2d.setPaint(p);
        g2d.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
        g2d.dispose();
    }


    private boolean isEntityVisible(Entity entity) {
        if (fogOfWar == null || player == null) return true;
        float playerCenterX = player.GetX() + player.GetWidth() / 2;
        float playerCenterY = player.GetY() + player.GetHeight() / 2;
        float entityCenterX = entity.GetX() + entity.GetWidth() / 2;
        float entityCenterY = entity.GetY() + entity.GetHeight() / 2;
        double distance = Math.sqrt(Math.pow(playerCenterX - entityCenterX, 2) + Math.pow(playerCenterY - entityCenterY, 2));

        return distance <= fogOfWar.getVisionRadius() * Tile.TILE_WIDTH;
    }

    private void drawUI(Graphics g) {
        drawHealthBar(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        int objectiveWidth = fm.stringWidth(currentObjective);
        int objectiveX = (refLink.GetWidth() - objectiveWidth) / 2;
        g.drawString("Obiectiv: Aduna obiectele necesare si ajungi la intrarea pesterii.", objectiveX, 30);


        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        int startY = 60;
        int lineHeight = 15;

        g.drawString("Nivel curent: " + (currentLevelIndex + 1), 10, startY);

        g.drawString("Tasks:", 10, startY + lineHeight * 2);
        if (!hasTalisman) {
            g.setColor(Color.YELLOW);
            g.drawString("1. Colecteaza talismanul pentru paznicul pesterii.", 10, startY + lineHeight * 3);
        } else {
            g.setColor(Color.GREEN);
            g.drawString("1. ✔ Colecteaza talismanul pentru paznicul pesterii.", 10, startY + lineHeight * 3);
        }

        if (!hasDoorKey) {
            g.setColor(Color.YELLOW);
            g.drawString("2. Aduna cheia pentru a deschide usa Nivelului 2.", 10, startY + lineHeight * 4);
        } else {
            g.setColor(Color.GREEN);
            g.drawString("2. ✔ Aduna cheia pentru a deschide usa Nivelului 2.", 10, startY + lineHeight * 4);
        }

        g.setColor(Color.WHITE);
        g.drawString("Apasa 'P' pentru Meniu Pauza.", 10, startY + lineHeight * 6);
        g.drawString("Apasa Z pentru a comuta zoom: " + String.format("%.1f", refLink.GetGameCamera().getZoomLevel()), 10, startY + lineHeight * 7);
        if (currentLevelIndex == 1) {
            g.drawString("Puzzle-uri Nivel 2: " + getPuzzlesSolvedCount() + "/" + TOTAL_PUZZLES_LEVEL2, 10, startY + lineHeight * 9);
        }

        drawMiniMap(g);

        if (collectionMessage != null) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            int msgWidth = fm.stringWidth(collectionMessage);
            g.drawString(collectionMessage, (refLink.GetWidth() - msgWidth) / 2, refLink.GetHeight() / 2);
        }
    }

    private void drawHealthBar(Graphics g) {
        if (player == null) return;
        int barWidth = 150;
        int barHeight = 20;
        int x = 10;
        int y = 10;

        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, barWidth, barHeight);

        int currentHealthWidth = (int) (((float) player.getHealth() / player.getMaxHealth()) * barWidth);
        g.setColor(new Color(50, 205, 50));
        g.fillRect(x, y, currentHealthWidth, barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(x, y, barWidth, barHeight);

        g.setFont(new Font("Arial", Font.BOLD, 12));
        String healthText = "HP: " + player.getHealth() + "/" + player.getMaxHealth();
        int textWidth = g.getFontMetrics().stringWidth(healthText);
        g.setColor(Color.WHITE);
        g.drawString(healthText, x + (barWidth - textWidth) / 2, y + barHeight - 5);
    }

    private void drawMiniMap(Graphics g) {
        if (currentMap == null || player == null) return;
        int miniMapWidth = 200;
        int miniMapHeight = 200;
        int padding = 10;
        int miniMapX = refLink.GetWidth() - miniMapWidth - padding;
        int miniMapY = padding;

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(miniMapX, miniMapY, miniMapWidth, miniMapHeight);
        g.setColor(Color.WHITE);
        g.drawRect(miniMapX, miniMapY, miniMapWidth, miniMapHeight);
        float mapScaleX = (float)miniMapWidth / (currentMap.getWidth() * Tile.TILE_WIDTH);
        float mapScaleY = (float)miniMapHeight / (currentMap.getHeight() * Tile.TILE_HEIGHT);
        for (int yTile = 0; yTile < currentMap.getHeight(); yTile++) {
            for (int xTile = 0; xTile < currentMap.getWidth(); xTile++) {
                Tile tile = currentMap.GetTile(xTile, yTile);
                if (tile != null) {
                    if (tile.IsSolid()) {
                        g.setColor(Color.DARK_GRAY);
                    } else {
                        g.setColor(new Color(50, 100, 50));
                    }
                    g.fillRect(miniMapX + (int)(xTile * Tile.TILE_WIDTH * mapScaleX),
                            miniMapY + (int)(yTile * Tile.TILE_HEIGHT * mapScaleY),
                            (int)(Tile.TILE_WIDTH * mapScaleX) + 1,
                            (int)(Tile.TILE_HEIGHT * mapScaleY) + 1);
                }
            }
        }

        for(Entity e : entities) {
            boolean isKey = (e instanceof Key) && !((Key) e).isCollected();
            boolean isAnimal = e instanceof Animal;

            boolean isTalisman = (e instanceof Talisman) && !((Talisman) e).isCollected();
            if (isKey || isAnimal || isTalisman) {
                int entityMiniMapX = miniMapX + (int)(e.GetX() * mapScaleX);
                int entityMiniMapY = miniMapY + (int)(e.GetY() * mapScaleY);
                g.setColor(Color.YELLOW);
                g.fillOval(entityMiniMapX, entityMiniMapY, 5, 5);
            }
        }

        int playerMiniMapX = miniMapX + (int)(player.GetX() * mapScaleX);
        int playerMiniMapY = miniMapY + (int)(player.GetY() * mapScaleY);
        int playerMiniMapSize = Math.max(2, (int)(player.GetWidth() * mapScaleX));

        g.setColor(Color.CYAN);
        g.fillOval(playerMiniMapX, playerMiniMapY, playerMiniMapSize, playerMiniMapSize);
    }

    public void saveCurrentState() {
        if (player != null) {
            StringBuilder solvedPuzzlesString = new StringBuilder();
            for (int i = 1; i <= TOTAL_PUZZLES_LEVEL2; i++) {
                if (puzzlesSolved[i]) {
                    if (solvedPuzzlesString.length() > 0) {
                        solvedPuzzlesString.append(",");
                    }
                    solvedPuzzlesString.append(i);
                }
            }
            refLink.GetDatabaseManager().saveGameData(currentLevelIndex, player.GetX(), player.GetY(), player.getHealth(), hasLevelKey, hasDoorKey, solvedPuzzlesString.toString());
        } else {
            System.err.println("Eroare: Nu se poate salva jocul, player-ul este null.");
        }
    }

    public void passToLevel2() {
        System.out.println("Trecere la Nivelul 2!");
        this.currentLevelIndex = 1;
        this.hasTalisman = false;
        this.caveEntranceUnlocked = false;
        this.loadFromSaveOnInit = false;
        InitLevelInternal(this.currentLevelIndex, this.loadFromSaveOnInit);
    }

    public void keyCollected() {
        this.hasLevelKey = true;
        collectionMessage = "Cheia Nivelului 1 colectata!";
        collectionMessageTime = System.currentTimeMillis();
        System.out.println("DEBUG GameState: Cheia Nivelului 1 a fost marcata ca fiind colectata.");
    }

    public void talismanCollected() {
        this.hasTalisman = true;
        collectionMessage = "Talismanul Soarelui colectat!";
        collectionMessageTime = System.currentTimeMillis();
        System.out.println("DEBUG GameState: Talismanul a fost marcat ca fiind colectat.");
    }

    public void removeTalismanFromInventory() {
        this.hasTalisman = false;
        collectionMessage = "Talismanul a fost predat paznicului.";
        collectionMessageTime = System.currentTimeMillis();
        Iterator<Entity> it = entities.iterator();
        while(it.hasNext()){
            Entity e = it.next();
            if (e instanceof Talisman) {
                it.remove();
                break;
            }
        }
    }

    public boolean hasTalismanCollected() {
        return hasTalisman;
    }

    public void setCaveEntranceUnlocked(boolean unlocked) {
        this.caveEntranceUnlocked = unlocked;
    }

    public boolean isCaveEntranceUnlocked() {
        return caveEntranceUnlocked;
    }

    public void doorKeyCollected() {
        this.hasDoorKey = true;
        collectionMessage = "Cheia Usii colectata!";
        collectionMessageTime = System.currentTimeMillis();
        System.out.println("DEBUG GameState: Cheia usii Nivel 2 a fost marcata ca fiind colectata.");
    }

    public void puzzleSolved(int puzzleId) {
        if (puzzleId > 0 && puzzleId <= TOTAL_PUZZLES_LEVEL2) {
            puzzlesSolved[puzzleId] = true;
            System.out.println("DEBUG GameState: Puzzle #" + puzzleId + " a fost rezolvat.");
        }
    }

    public boolean isPuzzleSolved(int puzzleId) {
        if (puzzleId > 0 && puzzleId <= TOTAL_PUZZLES_LEVEL2) {
            return puzzlesSolved[puzzleId];
        }
        return false;
    }

    public int getPuzzlesSolvedCount() {
        int count = 0;
        for (int i = 1; i <= TOTAL_PUZZLES_LEVEL2; i++) {
            if (puzzlesSolved[i]) count++;
        }
        return count;
    }

    public int getTotalPuzzlesLevel2() {
        return TOTAL_PUZZLES_LEVEL2;
    }

    public Map GetMap() {
        return currentMap;
    }
}