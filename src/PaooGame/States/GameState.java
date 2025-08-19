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
import PaooGame.Entities.DecorativeObject;
import PaooGame.Entities.LevelExit;
import PaooGame.Entities.Torch;
import PaooGame.Entities.Chest;
import PaooGame.Entities.TrapTrigger;
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
import java.util.Arrays;
import java.util.HashMap;

public class GameState extends State {

    private Map currentMap;
    private Player player;
    private FogOfWar fogOfWar;
    private String[] levelPaths = {"/maps/level_1.tmx", "/maps/level_2.tmx", "/maps/level_3.tmx"};
    private int currentLevelIndex;
    private boolean hasLevelKey = false;
    private boolean[] hasDoorKeys;
    private boolean hasTalisman = false;
    private boolean caveEntranceUnlocked = false;
    private String currentObjective = "Aduna cheia si talismanul Lunii. Da talismanul paznicului si intra in pestera.";
    private boolean isObjectiveDisplayed = false;

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

    private String woodSignMessage = null;

    private Agent finalBoss;
    private Chest finalChest;
    private boolean bossDefeated = false;
    private boolean agentIsChasing = false;

    private boolean trapsTriggered = false;
    private long trapActivationTime = 0;
    private final long GLOBAL_ACTIVATION_DELAY_MS = 1000;
    private ArrayList<Trap> arenaTraps;

    private final int[][] puzzleKeyPositions = {
            {18, 22},
            {35, 16},
            {52, 22},
            {69, 13},
            {86, 22}
    };
    private final int[][] puzzleDoorPositions = {{19, 24, 20, 24, 19, 25, 20, 25},{36, 18, 37, 18, 36, 19, 37, 19},{53, 24, 54, 24, 53, 25, 54, 25},{70, 15, 71, 15, 70, 16, 71, 16},{87, 24, 88, 24, 87, 25, 88, 25},{110, 15, 111, 15, 110, 16, 111, 16}};

    private long lastAgentTrapDamageTime = 0;

    private long lastPlayerPunchTime = 0;
    private long lastAgentPunchTime = 0;
    private final long PUNCH_COOLDOWN_MS = 3000;
    private final int PUNCH_DAMAGE = 10;
    private final int COLLISION_RANGE = 5;

    private long lastAgentAttackTime = 0;
    private final long ATTACK_COOLDOWN_MS = 3000;

    public GameState(RefLinks refLink) {
        super(refLink);
        this.currentLevelIndex = 0;
        this.hasLevelKey = false;
        this.hasDoorKeys = new boolean[6];
        this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];
        this.hasTalisman = false;
        this.caveEntranceUnlocked = false;
        this.isObjectiveDisplayed = false;
        this.currentObjective = "Afla ce se afla in spatele usii blocate."; // Obiectivul initial pentru nivelul 1
        this.currentMap = new Map(refLink);
        this.player = new Player(refLink.GetGame(), 0, 0);
        refLink.SetMap(this.currentMap);
        refLink.SetPlayer(this.player);

        InitLevelInternal(this.currentLevelIndex, false);
        System.out.println("✓ GameState initializat (joc nou)");
    }

    public GameState(RefLinks refLink, int startLevel) {
        super(refLink);
        this.currentLevelIndex = startLevel;
        this.isObjectiveDisplayed = false;

        this.hasLevelKey = false;
        this.hasDoorKeys = new boolean[6];
        this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];
        this.hasTalisman = false;
        this.caveEntranceUnlocked = false;

        this.currentMap = new Map(refLink);
        this.player = new Player(refLink.GetGame(), 0, 0);
        refLink.SetMap(this.currentMap);
        refLink.SetPlayer(this.player);

        InitLevelInternal(this.currentLevelIndex, false);
        updateObjective();
        System.out.println("✓ GameState initializat (joc nou de la nivelul " + (startLevel + 1) + ")");
    }

    public GameState(RefLinks refLink, boolean loadFromSave) {
        super(refLink);
        this.isObjectiveDisplayed = false;

        this.currentMap = new Map(refLink);
        this.player = new Player(refLink.GetGame(), 0, 0);
        refLink.SetMap(this.currentMap);
        refLink.SetPlayer(this.player);

        InitLevelInternal(0, true);
        updateObjective();
        System.out.println("✓ GameState initializat (incarcare joc)");
    }

    private void updateObjective() {
        if (currentLevelIndex == 1) {
            currentObjective = "Rezolva puzzle-urile si ajungi in ultima camera.";
        } else if (currentLevelIndex == 2) {
            currentObjective = "Invinge agentul, aduna ultima cheie si ia comoara.";
        } else {
            currentObjective = "Aduna cheia si talismanul apoi intra in pestera.";
        }
    }

    public void InitLevelInternal(int desiredLevelIndex, boolean loadPlayerStateFromDb) {
        DatabaseManager.SaveGameData loadedData = null;
        float playerStartX = 100;
        float playerStartY = 100;

        if (loadPlayerStateFromDb) {
            loadedData = refLink.GetDatabaseManager().loadGameData();
            if (loadedData != null) {
                this.currentLevelIndex = loadedData.levelIndex;
                playerStartX = loadedData.playerX;
                playerStartY = loadedData.playerY;
                this.hasLevelKey = loadedData.hasKey;
                this.hasDoorKeys = loadedData.hasDoorKeys;
                this.isObjectiveDisplayed = true;

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
                            puzzlesSolved[0] = false; // Fallback
                        }
                    }
                }
                System.out.println("DEBUG GameState: Incarcare nivel din salvare: " + (currentLevelIndex + 1));
            } else {
                System.out.println("DEBUG GameState: Nu s-au putut incarca date salvate. Se porneste un joc nou (Nivel 1).");
                this.currentLevelIndex = 0;
                this.hasLevelKey = false;
                this.hasDoorKeys = new boolean[6];
                this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];
                this.hasTalisman = false;
                this.caveEntranceUnlocked = false;
            }
        } else {
            this.currentLevelIndex = desiredLevelIndex;
            if (currentLevelIndex == 0) {
                playerStartX = 3 * Tile.TILE_WIDTH;
                playerStartY = 3 * Tile.TILE_HEIGHT;
            } else if (currentLevelIndex == 1) {
                playerStartX = 2 * Tile.TILE_WIDTH;
                playerStartY = 26 * Tile.TILE_HEIGHT;
            } else if (currentLevelIndex == 2) {
                playerStartX = 37 * Tile.TILE_WIDTH;
                playerStartY = 57 * Tile.TILE_HEIGHT;
            }
        }

        if (this.currentLevelIndex < 0 || this.currentLevelIndex >= levelPaths.length) {
            System.err.println("Index nivel invalid: " + this.currentLevelIndex + ". Se reseteaza la nivelul 1.");
            this.currentLevelIndex = 0;
            this.hasLevelKey = false;
            this.hasDoorKeys = new boolean[6];
            this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];
        }

        this.currentMap.LoadMapFromFile(levelPaths[this.currentLevelIndex]);
        System.out.println("DEBUG GameState: Nivelul " + (this.currentLevelIndex + 1) + " incarcat: " + levelPaths[this.currentLevelIndex]);
        this.fogOfWar = new FogOfWar(refLink, this.currentMap.GetWidth(), this.currentMap.GetHeight());

        this.player.SetPosition(playerStartX, playerStartY);
        if (loadedData != null) {
            this.player.setHealth(loadedData.playerHealth);
        } else {
            this.player.resetHealth();
        }
        System.out.println("DEBUG GameState: Jucator pozitionat la X:" + player.GetX() + ", Y:" + player.GetY() + ", HP:" + player.getHealth());
        refLink.GetGameCamera().centerOnEntity(this.player);

        entities = new ArrayList<>();
        switch (currentLevelIndex) {
            case 0:
                entities.add(new Animal(refLink, 53 * Tile.TILE_WIDTH, 5 * Tile.TILE_HEIGHT, 51 * Tile.TILE_WIDTH, 56 * Tile.TILE_WIDTH, Animal.AnimalType.JAGUAR));
                entities.add(new Animal(refLink, 10 * Tile.TILE_WIDTH, 36 * Tile.TILE_HEIGHT, 8 * Tile.TILE_WIDTH, 11 * Tile.TILE_WIDTH, Animal.AnimalType.MONKEY));
                entities.add(new Animal(refLink, 89 * Tile.TILE_WIDTH, 29 * Tile.TILE_HEIGHT, 88 * Tile.TILE_WIDTH, 91 * Tile.TILE_WIDTH, Animal.AnimalType.MONKEY));
                entities.add(new Animal(refLink, 84 * Tile.TILE_WIDTH, 57 * Tile.TILE_HEIGHT, 82 * Tile.TILE_WIDTH, 85 * Tile.TILE_WIDTH, Animal.AnimalType.BAT));
                entities.add(new Trap(refLink, 66 * Tile.TILE_WIDTH, 31 * Tile.TILE_HEIGHT, Assets.spikeTrapImage));
                entities.add(new Trap(refLink, 67 * Tile.TILE_WIDTH, 38 * Tile.TILE_HEIGHT, Assets.spikeTrapImage));
                entities.add(new Trap(refLink, 66 * Tile.TILE_WIDTH, 45 * Tile.TILE_HEIGHT, Assets.spikeTrapImage));
                caveGuardianNPC = new NPC(refLink, 93 * Tile.TILE_WIDTH, 92 * Tile.TILE_HEIGHT);
                entities.add(caveGuardianNPC);
                entities.add(new Talisman(refLink, 45 * Tile.TILE_WIDTH, 52 * Tile.TILE_HEIGHT, Assets.talismanImage));
                caveEntrance = new CaveEntrance(refLink, 91 * Tile.TILE_WIDTH, 88 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH * 2, Tile.TILE_HEIGHT * 2);
                entities.add(caveEntrance);
                if (!hasDoorKeys[0]) {
                    entities.add(new Key(refLink, 12 * Tile.TILE_WIDTH, 85 * Tile.TILE_HEIGHT, Assets.keyImage, 0)); // Aceasta cheie va deschide prima usa de la nivelul 2
                }
                DecorativeObject woodSign1 = new DecorativeObject(refLink, 2 * Tile.TILE_WIDTH, 1 * Tile.TILE_HEIGHT, 64, 64, Assets.woodSignImage, false);
                woodSign1.setDialogueMessage("Aduna cheia si talismanul Lunii. Da talismanul paznicului si intra in pestera.");
                entities.add(woodSign1);
                break;
            case 1:
                int[][] puzzleTableCoordinates = {
                        {19, 20}, {36, 14}, {53, 20}, {70, 11}, {87, 20}
                };
                for (int[] coords : puzzleTableCoordinates) {
                    float pixelX = coords[0] * Tile.TILE_WIDTH;
                    float pixelY = coords[1] * Tile.TILE_HEIGHT - (48 / 2);
                    entities.add(new DecorativeObject(refLink, pixelX, pixelY, 96, 48, Assets.puzzleTableImage, true));
                }

                if (!isPuzzleSolved(1)) entities.add(new PuzzleTrigger(refLink, 19 * Tile.TILE_WIDTH, 20 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 1));
                if (!isPuzzleSolved(2)) entities.add(new PuzzleTrigger(refLink, 36 * Tile.TILE_WIDTH, 14 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 2));
                if (!isPuzzleSolved(3)) entities.add(new PuzzleTrigger(refLink, 53 * Tile.TILE_WIDTH, 20 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 3));
                if (!isPuzzleSolved(4)) entities.add(new PuzzleTrigger(refLink, 70 * Tile.TILE_WIDTH, 11 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 4));
                if (!isPuzzleSolved(5)) entities.add(new PuzzleTrigger(refLink, 87 * Tile.TILE_WIDTH, 20 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 5));
                entities.add(new LevelExit(refLink, 110 * Tile.TILE_WIDTH, 14 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH * 2, Tile.TILE_HEIGHT));
                for (int i = 1; i <= TOTAL_PUZZLES_LEVEL2; i++) {
                    if (isPuzzleSolved(i)) {
                        int keyTileX = puzzleKeyPositions[i - 1][0];
                        int keyTileY = puzzleKeyPositions[i - 1][1];
                        entities.add(new Key(refLink, (float)keyTileX * Tile.TILE_WIDTH, (float)keyTileY * Tile.TILE_HEIGHT, Assets.keyImage, i));
                    }
                }
                DecorativeObject woodSign2 = new DecorativeObject(refLink, 3 * Tile.TILE_WIDTH, 25 * Tile.TILE_HEIGHT, 64, 64, Assets.woodSignImage, false);
                woodSign2.setDialogueMessage("Rezolva puzzle-urile si mergi spre camera finala.");
                entities.add(woodSign2);
                break;
            case 2:
                arenaTraps = new ArrayList<>();
                int[][] torchPositions = {
                        {38, 54}, {41, 54}, {24, 27}, {24, 30}, {55, 27}, {55, 30}, {38, 17}, {41, 17}
                };
                for (int[] pos : torchPositions) {
                    entities.add(new Torch(refLink, (float)pos[0] * Tile.TILE_WIDTH, (float)pos[1] * Tile.TILE_HEIGHT));
                }

                finalChest = new Chest(refLink, 37 * Tile.TILE_WIDTH, 3 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
                finalChest.setCanInteract(false);
                entities.add(finalChest);

                entities.add(new DecorativeObject(refLink, 75 * Tile.TILE_WIDTH, 26 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, Assets.puzzleTableImage, true));
                entities.add(new Key(refLink, 77 * Tile.TILE_WIDTH, 31 * Tile.TILE_HEIGHT, Assets.keyImage, 6));
                int[][] trapTiles = {
                        {29,22}, {30,22}, {31,22}, {32,22}, {29,23}, {30,23}, {31,23}, {32,23},
                        {29,36}, {30,36}, {31,36}, {32,36}, {29,37}, {30,37}, {31,37}, {32,37},
                        {38,29}, {39,29}, {40,29}, {41,29}, {38,30}, {39,30}, {40,30}, {41,30},
                        {47,22}, {48,22}, {49,22}, {50,22}, {47,23}, {48,23}, {49,23}, {50,23},
                        {47,36}, {48,36}, {49,36}, {50,36}, {47,37}, {48,37}, {49,37}, {50,37}
                };
                for (int[] pos : trapTiles) {
                    Trap trap = new Trap(refLink, (float)pos[0] * Tile.TILE_WIDTH, (float)pos[1] * Tile.TILE_HEIGHT);
                    entities.add(trap);
                    arenaTraps.add(trap);
                }

                int[][] triggerGroups = {
                        {46, 21, 51, 24}, {37, 28, 42, 31}, {28, 21, 33, 24},
                        {46, 35, 51, 38}, {28, 35, 33, 38}
                };
                for(int[] group : triggerGroups) {
                    int startX = group[0];
                    int startY = group[1];
                    int endX = group[2];
                    int endY = group[3];
                    for(int y = startY; y <= endY; y++) {
                        for(int x = startX; x <= endX; x++) {
                            entities.add(new TrapTrigger(refLink, (float)x * Tile.TILE_WIDTH, (float)y * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT));
                        }
                    }
                }

                finalBoss = new Agent(refLink, 36 * Tile.TILE_WIDTH, 22 * Tile.TILE_HEIGHT, 36 * Tile.TILE_WIDTH, 43 * Tile.TILE_WIDTH, true);
                entities.add(finalBoss);

                DecorativeObject woodSign3 = new DecorativeObject(refLink, 37 * Tile.TILE_WIDTH, 56 * Tile.TILE_HEIGHT, 64, 64, Assets.woodSignImage, false);
                woodSign3.setDialogueMessage("Invinge inamicul, aduna ultima cheie si gaseste comoara.");
                entities.add(woodSign3);

                break;
        }
    }

    public void Update() {
        if (collectionMessage != null && System.currentTimeMillis() - collectionMessageTime > MESSAGE_DURATION_MS) {
            collectionMessage = null;
        }

        if (woodSignMessage != null && refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
            woodSignMessage = null;
            isObjectiveDisplayed = true;
            return;
        }

        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_P) && player != null) {
            refLink.setPersistedGameState(this);
            refLink.SetState(new PauseState(refLink));
            return;
        }

        if (currentMap != null) {
            currentMap.Update();
        }

        if (fogOfWar != null) {
            fogOfWar.update();
        }

        if (player != null) {
            player.Update();
            if (player.getHealth() <= 0 && !player.isHurt()) {
                refLink.SetState(new GameOverState(refLink));
                return;
            }
            if (player.isHurt() && player.activeAnimation.isFinished()) {
                refLink.SetState(new GameOverState(refLink));
                return;
            }
        }

        boolean playerInContactWithAnimal = false;
        if (currentLevelIndex == 2 && !trapsTriggered) {
            for (Entity e : entities) {
                if (e instanceof TrapTrigger && e.GetBounds().intersects(player.GetBounds())) {
                    trapsTriggered = true;
                    trapActivationTime = System.currentTimeMillis();
                    System.out.println("DEBUG GameState: Declanșatoarele de capcane au fost atinse! Timer de 1 secunda pornit.");
                    break;
                }
            }
        }

        if (currentLevelIndex == 2 && trapsTriggered && System.currentTimeMillis() - trapActivationTime >= GLOBAL_ACTIVATION_DELAY_MS) {
            for (Trap trap : arenaTraps) {
                trap.setActive(true);
            }
            trapsTriggered = false;
        }

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

            if (caveEntrance != null && player.GetBounds().intersects(caveEntrance.GetBounds()) && refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                if (isCaveEntranceUnlocked() && hasDoorKeys[0]) {
                    passToLevel2();
                    return;
                } else if (!isCaveEntranceUnlocked()) {
                    collectionMessage = "Intrarea in pestera este blocata. Vorbeste cu paznicul.";
                    collectionMessageTime = System.currentTimeMillis();
                } else if (!hasDoorKeys[0]) {
                    collectionMessage = "Ai nevoie de cheie pentru a deschide aceasta usa!";
                    collectionMessageTime = System.currentTimeMillis();
                }
            }
        }

        if (currentLevelIndex == 1 && refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
            checkAndOpenDoor();
        }

        if (currentLevelIndex == 1) {
            if (!currentMap.GetTile(puzzleDoorPositions[5][0], puzzleDoorPositions[5][1]).IsSolid() &&
                    player.GetBounds().intersects(new Rectangle(puzzleDoorPositions[5][0] * Tile.TILE_WIDTH, puzzleDoorPositions[5][1] * Tile.TILE_HEIGHT, Tile.TILE_WIDTH * 2, Tile.TILE_HEIGHT))) {
                passToLevel3();
                return;
            }
        }

        if (currentLevelIndex == 2) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                checkAndOpenFinalDoor();
            }
            if (finalBoss != null) {
                int playerTileX = (int) (player.GetX() / Tile.TILE_WIDTH);
                int playerTileY = (int) (player.GetY() / Tile.TILE_HEIGHT);

                if (!agentIsChasing && ( (playerTileX >= 39 && playerTileX <= 40) && playerTileY == 39) ) {
                    System.out.println("DEBUG GameState: Player-ul a calcat pe dalele de declansare. Agentul va incepe urmarirea.");
                    agentIsChasing = true;
                    finalBoss.setChaseMode(true);
                }

                if (finalBoss.getHealth() <= 0 && !bossDefeated) {
                    System.out.println("DEBUG GameState: Agentul a fost invins!");
                    bossDefeated = true;
                    finalChest.setCanInteract(true);
                }

                // Logica de lupta - Jucator vs Agent
                if (player.isAttacking() && player.GetBounds().intersects(finalBoss.GetBounds()) &&
                        System.currentTimeMillis() - lastAgentAttackTime > ATTACK_COOLDOWN_MS) {
                    finalBoss.takeDamage(PUNCH_DAMAGE);
                    lastAgentAttackTime = System.currentTimeMillis();
                }

                if (finalBoss.isAttacking() && finalBoss.GetBounds().intersects(player.GetBounds()) &&
                        System.currentTimeMillis() - lastAgentAttackTime > ATTACK_COOLDOWN_MS) {
                    player.takeDamage(PUNCH_DAMAGE);
                    lastAgentAttackTime = System.currentTimeMillis();
                }
            }
        }

        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity e = it.next();
            e.Update();
            if (e instanceof Key) {
                Key k = (Key) e;
                if (k.isCollected()) {
                    int associatedId = k.getAssociatedPuzzleId();
                    if (associatedId >= 0 && associatedId < hasDoorKeys.length) {
                        hasDoorKeys[associatedId] = true;
                        collectionMessage = "Cheia colectata pentru usa " + (associatedId + 1) + "!";
                        collectionMessageTime = System.currentTimeMillis();
                        System.out.println("DEBUG GameState: Cheia pentru usa " + (associatedId + 1) + " a fost colectata.");
                    }
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
                Trap trap = (Trap) e;
                if (trap.isActive()) {
                    // Logica de daune pentru jucator
                    if (player.GetBounds().intersects(trap.GetBounds())) {
                        if (System.currentTimeMillis() - lastTrapDamageTime >= TRAP_DAMAGE_COOLDOWN_MS) {
                            player.takeDamage(trap.getDamage());
                            lastTrapDamageTime = System.currentTimeMillis();
                        }
                    }
                    // Logica de daune pentru agent
                    if (finalBoss != null && finalBoss.GetBounds().intersects(trap.GetBounds())) {
                        if (System.currentTimeMillis() - lastAgentTrapDamageTime >= TRAP_DAMAGE_COOLDOWN_MS) {
                            finalBoss.takeDamage(trap.getDamage());
                            lastAgentTrapDamageTime = System.currentTimeMillis();
                        }
                    }
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
                if (player != null) {
                    for (Entity e : entities) {
                        if (e instanceof Animal && player.GetBounds().intersects(e.GetBounds())) {
                            player.takeDamage(((Animal) e).getDamage());
                            break;
                        }
                    }
                    lastAnimalDamageTime = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        if (currentMap == null || player == null) return; 
        GameCamera camera = refLink.GetGameCamera();
        int xStart = (int) Math.max(0, camera.getxOffset() / Tile.TILE_WIDTH);
        int xEnd = (int) Math.min(currentMap.GetWidth(), (camera.getxOffset() + refLink.GetWidth()) / Tile.TILE_WIDTH + 1);
        int yStart = (int) Math.max(0, camera.getyOffset() / Tile.TILE_HEIGHT);
        int yEnd = (int) Math.min(currentMap.GetHeight(), (camera.getyOffset() + refLink.GetHeight()) / Tile.TILE_HEIGHT + 1);

        // PRIMUL STRAT: Desenează Ground (dale de fundal, inclusiv capcanele, dar nu și pereții)
        for (int[][] layerGids : currentMap.getTilesGidsLayers()) { 
            for (int y = yStart; y < yEnd; y++) { 
                for (int x = xStart; x < xEnd; x++) { 
                    int gid = layerGids[x][y]; 
                    // Sarim peste dalele goale si de perete (ID 64)
                    if (gid == 0 || gid == 64) continue; 
                    Tile.GetTile(gid).Draw(g, 
                    (int)((x * Tile.TILE_WIDTH - camera.getxOffset())), 
                    (int)((y * Tile.TILE_HEIGHT - camera.getyOffset())), 
                            Tile.TILE_WIDTH, 
                            Tile.TILE_HEIGHT, 
                            currentMap.getCurrentMapTilesetImage()); 
                }
            }
        }

        // AL DOILEA STRAT: Desenează Entitățile (împărțit în două etape pentru ordinea corectă)

        // Pasul 2.1: Desenează entitățile de fundal care trebuie să fie MEREU SUB JUCĂTOR
        for (Entity e : entities) {
            // Aici adaugi orice alt tip de entitate care trebuie să fie la nivelul solului
            if (e instanceof Trap || e instanceof Chest || e instanceof Key || e instanceof Talisman || e instanceof PuzzleTrigger || e instanceof TrapTrigger) {
                e.Draw(g);
            }
        }

        // Pasul 2.2: Pregătim o listă nouă cu entitățile dinamice (personaje) pentru sortare
        ArrayList<Entity> dynamicEntities = new ArrayList<>();
        dynamicEntities.add(player); // Adăugăm jucătorul

        // Adăugăm și alte entități mobile sau interactive care trebuie sortate după axa Y
        for (Entity e : entities) {
            if (e instanceof Agent || e instanceof Animal || e instanceof NPC || e instanceof DecorativeObject || e instanceof Torch) {
                dynamicEntities.add(e);
            }
        }

        // Sortăm DOAR entitățile dinamice după axa Y pentru efectul de adâncime
        dynamicEntities.sort((e1, e2) -> Float.compare(e1.GetY(), e2.GetY()));

        // Pasul 2.3: Desenăm entitățile dinamice sortate deasupra celor de fundal
        for (Entity e : dynamicEntities) {
            e.Draw(g);
        }

        // AL TREILEA STRAT: Desenează Objects (dalele de perete) deasupra entităților.
        for (int[][] layerGids : currentMap.getTilesGidsLayers()) { 
            for (int y = yStart; y < yEnd; y++) { 
                for (int x = xStart; x < xEnd; x++) { 
                    int gid = layerGids[x][y]; 
                    if (gid == 64) { 
                        Tile.GetTile(gid).Draw(g, 
                        (int)((x * Tile.TILE_WIDTH - camera.getxOffset())), 
                        (int)((y * Tile.TILE_HEIGHT - camera.getyOffset())), 
                                Tile.TILE_WIDTH, 
                                Tile.TILE_HEIGHT, 
                                currentMap.getCurrentMapTilesetImage()); 
                    }
                }
            }
        }

        // Desenează ușile speciale de la nivelul 1 și alte elemente de UI.
        if (currentLevelIndex == 1) { 
            for (int[][] layerGids : currentMap.getTilesGidsLayers()) { 
                for (int y = yStart; y < yEnd; y++) { 
                    for (int x = xStart; x < xEnd; x++) { 
                        int gid = layerGids[x][y]; 
                        if (gid != 0 && gid != 64 && isSpecialDoorTile(gid)) { 
                            Tile.GetTile(gid).Draw(g, 
                            (int)((x * Tile.TILE_WIDTH - camera.getxOffset())), 
                            (int)((y * Tile.TILE_HEIGHT - camera.getyOffset())), 
                                    Tile.TILE_WIDTH, 
                                    Tile.TILE_HEIGHT, 
                                    currentMap.getCurrentMapTilesetImage()); 
                        }
                    }
                }
            }

            for (int i = 0; i < puzzleDoorPositions.length; i++) { 
                if (puzzleDoorPositions[i] != null && puzzleDoorPositions[i].length >= 2) { 
                    if (!currentMap.GetTile(puzzleDoorPositions[i][0], puzzleDoorPositions[i][1]).IsSolid()) { 
                        continue; 
                    }
                    int tileX = puzzleDoorPositions[i][0]; 
                    int tileY = puzzleDoorPositions[i][1]; 

                    Entity tempDoorEntity = new Entity(refLink, (float)tileX * Tile.TILE_WIDTH, (float)tileY * Tile.TILE_HEIGHT, Tile.TILE_WIDTH * 2, Tile.TILE_HEIGHT * 2) { 
                        @Override
                        public void Update() {}
                        @Override
                        public void Draw(Graphics g) { 
                            drawInteractionPopup(g); 
                        }
                    };
                    tempDoorEntity.Draw(g); 
                }
            }
        }

        if (fogOfWar != null) { 
            fogOfWar.render(g); 
        }

        if (isWoodSignMessageShowing()) { 
            int wndWidth = refLink.GetWidth(); 
            int wndHeight = refLink.GetHeight(); 

            int boxWidth = 500; 
            int boxHeight = 150; 
            int boxX = wndWidth / 2 - boxWidth / 2; 
            int boxY = wndHeight / 2 - boxHeight / 2; 
            g.setColor(new Color(0, 0, 0, 180)); 
            g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20); 

            g.setColor(Color.WHITE); 
            g.setFont(new Font("Arial", Font.BOLD, 18)); 
            FontMetrics fm = g.getFontMetrics(); 
            if (woodSignMessage != null) { 
                String[] lines = woodSignMessage.split("\n"); 
                int lineHeight = fm.getHeight(); 
                int startY = boxY + (boxHeight - lines.length * lineHeight) / 2 + fm.getAscent(); 
                for (String line : lines) { 
                    if (line != null) { 
                        int textWidth = fm.stringWidth(line); 
                        g.drawString(line, wndWidth / 2 - textWidth / 2, startY); 
                        startY += lineHeight; 
                    }
                }
            }

            g.setFont(new Font("Arial", Font.PLAIN, 14)); 
            String instruction = "Apasati 'E' pentru a inchide."; 
            int instructionWidth = g.getFontMetrics().stringWidth(instruction); 
            g.drawString(instruction, wndWidth / 2 - instructionWidth / 2, boxY + boxHeight - 20); 
        }

        drawUI(g);
    }

    // Această funcție este necesară pentru a desena mini-harta, HP-ul, mesajele de colectare și obiectivele
    private void drawUI(Graphics g) {
        drawHealthBar(g);
        drawMiniMap(g);

        // Desenează mesajul de colectare (centrat în mijloc)
        if (collectionMessage != null) {
            Font messageFont = new Font("Arial", Font.BOLD, 14);
            g.setFont(messageFont);
            FontMetrics metrics = g.getFontMetrics(messageFont);

            int boxWidth = metrics.stringWidth(collectionMessage) + 40;
            int boxHeight = metrics.getHeight() + 20;
            int boxX = (refLink.GetWidth() - boxWidth) / 2;
            int boxY = (refLink.GetHeight() - boxHeight) / 2; // Acum se desenează în centru

            g.setColor(new Color(255, 255, 255, 150));
            g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);

            g.setColor(Color.BLACK);
            int textX = boxX + (boxWidth - metrics.stringWidth(collectionMessage)) / 2;
            int textY = boxY + (boxHeight - metrics.getHeight()) / 2 + metrics.getAscent();
            g.drawString(collectionMessage, textX, textY);
        }

        // Desenează obiectivul (rămâne centrat sus)
        if (isObjectiveDisplayed) {
            Font objectiveFont = new Font("Arial", Font.BOLD, 16);
            FontMetrics fm = g.getFontMetrics(objectiveFont);
            String objectiveText = "Obiectiv: " + currentObjective;
            int objectiveWidth = fm.stringWidth(objectiveText);

            int objectiveX = (refLink.GetWidth() - objectiveWidth) / 2;
            int padding = 10;
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(objectiveX - padding, 10 - padding, objectiveWidth + 2 * padding, fm.getHeight() + 2 * padding);

            g.setColor(Color.WHITE);
            g.setFont(objectiveFont);
            g.drawString(objectiveText, objectiveX, 30);
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
        int miniMapHeight = 150;
        float mapPixelWidth = currentMap.GetWidth() * Tile.TILE_WIDTH;
        float mapPixelHeight = currentMap.GetHeight() * Tile.TILE_HEIGHT;
        int miniMapWidth = (int) (mapPixelWidth / mapPixelHeight * miniMapHeight);
        int padding = 10;
        int miniMapX = refLink.GetWidth() - miniMapWidth - padding;
        int miniMapY = padding;

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(miniMapX, miniMapY, miniMapWidth, miniMapHeight);
        g.setColor(Color.WHITE);
        g.drawRect(miniMapX, miniMapY, miniMapWidth, miniMapHeight);

        float mapScaleX = (float)miniMapWidth / mapPixelWidth;
        float mapScaleY = (float)miniMapHeight / mapPixelHeight;

        for (int yTile = 0; yTile < currentMap.GetHeight(); yTile++) {
            for (int xTile = 0; xTile < currentMap.GetWidth(); xTile++) {
                Tile tile = currentMap.GetTile(xTile, yTile);
                if (tile != null) {
                    if (tile.IsSolid()) {
                        g.setColor(new Color(87, 51, 35));
                    } else {
                        g.setColor(new Color(48, 43, 59));
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
            boolean isTalisman = (e instanceof Talisman) && !((Talisman) e).isCollected();
            if (isKey || isTalisman) {
                int entityMiniMapX = miniMapX + (int)(e.GetX() * mapScaleX);
                int entityMiniMapY = miniMapY + (int)(e.GetY() * mapScaleY);
                g.setColor(Color.YELLOW);
                g.fillOval(entityMiniMapX, entityMiniMapY, 5, 5);
            }
        }

        int playerMiniMapX = miniMapX + (int)(player.GetX() * mapScaleX);
        int playerMiniMapY = miniMapY + (int)(player.GetY() * mapScaleY);
        g.setColor(Color.CYAN);
        g.fillOval(playerMiniMapX, playerMiniMapY, 5, 5);
    }

    public void removeTalismanFromInventory() {
        hasTalisman = false;
        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity e = it.next();
            if (e instanceof Talisman) {
                it.remove();
            }
        }
        System.out.println("Talismanul a fost predat paznicului.");
        collectionMessage = "Ai dat talismanul paznicului! Intrarea in pestera este deschisa!";
        collectionMessageTime = System.currentTimeMillis();
    }

    public boolean isCaveEntranceUnlocked() {
        return caveEntranceUnlocked;
    }

    public void setCaveEntranceUnlocked(boolean caveEntranceUnlocked) {
        this.caveEntranceUnlocked = caveEntranceUnlocked;
    }

    public void passToLevel2() {
        if (currentLevelIndex < levelPaths.length - 1) {
            // Salvează starea cheilor înainte de a schimba nivelul
            boolean[] savedDoorKeys = this.hasDoorKeys;

            // Creează o nouă stare a jocului pentru nivelul următor
            GameState nextState = new GameState(refLink, currentLevelIndex + 1);

            // Restaurează starea cheilor
            nextState.setHasDoorKeys(savedDoorKeys);

            refLink.SetState(nextState);
            System.out.println("DEBUG GameState: Trecere la nivelul " + (currentLevelIndex + 2));
        } else {
            System.out.println("DEBUG GameState: Ai terminat jocul!");
            refLink.SetState(new GameOverState(refLink));
        }
    }

    public void setHasDoorKeys(boolean[] keys) {
        this.hasDoorKeys = keys;
    }

    private void checkAndOpenDoor() {
        if (player == null || currentMap == null) return;
        int playerTileX = (int) (player.GetX() / Tile.TILE_WIDTH);
        int playerTileY = (int) (player.GetY() / Tile.TILE_HEIGHT);

        int interactionRange = 2;
        for (int i = 0; i < puzzleDoorPositions.length; i++) {
            int[] doorCoords = puzzleDoorPositions[i];
            if (Math.abs(playerTileX - doorCoords[0]) <= interactionRange &&
                    Math.abs(playerTileY - doorCoords[1]) <= interactionRange) {

                if (currentMap.GetTile(doorCoords[0], doorCoords[1]).IsSolid()) {
                    if (hasDoorKeys[i]) {
                        openDoor(i); // Apelează openDoor cu indexul corect al ușii (0 pentru prima ușă)
                        return;
                    } else {
                        collectionMessage = "Usa este blocata. Ai nevoie de cheie!";
                        collectionMessageTime = System.currentTimeMillis();
                        return;
                    }
                }
            }
        }
    }

    private void openDoor(int doorIndex) {
        if (doorIndex >= 0 && doorIndex < hasDoorKeys.length) {
            if (hasDoorKeys[doorIndex]) {
                int[] doorCoords = puzzleDoorPositions[doorIndex];
                if (doorCoords.length == 8) {
                    int x1 = doorCoords[0];
                    int y1 = doorCoords[1];
                    int x2 = doorCoords[2];
                    int y2 = doorCoords[3];
                    int x3 = doorCoords[4];
                    int y3 = doorCoords[5];
                    int x4 = doorCoords[6];
                    int y4 = doorCoords[7];

                    // Am corectat aici: folosim GID-urile pentru ușa deschisă, nu 0
                    currentMap.changeTileGid(x1, y1, Tile.DOOR_OPEN_TOP_LEFT_GID, 1);
                    currentMap.changeTileGid(x2, y2, Tile.DOOR_OPEN_TOP_RIGHT_GID, 1);
                    currentMap.changeTileGid(x3, y3, Tile.DOOR_OPEN_BOTTOM_LEFT_GID, 1);
                    currentMap.changeTileGid(x4, y4, Tile.DOOR_OPEN_BOTTOM_RIGHT_GID, 1);

                    hasDoorKeys[doorIndex] = false;
                    collectionMessage = "Usa s-a deschis!";
                    collectionMessageTime = System.currentTimeMillis();
                    System.out.println("DEBUG GameState: Usa " + (doorIndex + 1) + " s-a deschis.");
                }
            } else {
                collectionMessage = "Ai nevoie de cheia potrivita!";
                collectionMessageTime = System.currentTimeMillis();
            }
        }
    }

    private void checkAndOpenFinalDoor() {
        // Verifică dacă jucătorul este aproape de cheia finală pentru a deschide ușa
        if (player.GetBounds().intersects(new Rectangle(77 * Tile.TILE_WIDTH, 31 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT))) {
            if (hasDoorKeys[5]) {
                // Coordonatele corecte pentru ușa finală de la sfârșitul nivelului 2
                int tileX = 110;
                int tileY = 15;

                // Deschide ușa finală
                currentMap.changeTileGid(tileX, tileY, Tile.DOOR_OPEN_TOP_LEFT_GID, 1);
                currentMap.changeTileGid(tileX+1, tileY, Tile.DOOR_OPEN_TOP_RIGHT_GID, 1);
                currentMap.changeTileGid(tileX, tileY+1, Tile.DOOR_OPEN_BOTTOM_LEFT_GID, 1);
                currentMap.changeTileGid(tileX+1, tileY+1, Tile.DOOR_OPEN_BOTTOM_RIGHT_GID, 1);

                hasDoorKeys[5] = false;
                collectionMessage = "Drumul catre nivel 3 s-a deschis!";
                collectionMessageTime = System.currentTimeMillis();
            } else {
                collectionMessage = "Ai nevoie de cheie!";
                collectionMessageTime = System.currentTimeMillis();
            }
        }
    }

    private boolean isSpecialDoorTile(int gid) {
        return gid == Tile.DOOR_OPEN_TOP_LEFT_GID ||
                gid == Tile.DOOR_OPEN_TOP_RIGHT_GID ||
                gid == Tile.DOOR_OPEN_BOTTOM_LEFT_GID ||
                gid == Tile.DOOR_OPEN_BOTTOM_RIGHT_GID;
    }

    /*!
     * \fn public void HandlePuzzleFailure()
     * \brief Gestioneaza logica de esec a unui puzzle: scade HP, reseteaza pozitia
     * jucatorului sau schimba nivelul in caz de game over.
     */
    /*!
     * \fn public void HandlePuzzleFailure()
     * \brief Gestioneaza logica de esec a unui puzzle: scade HP, reseteaza pozitia
     * jucatorului sau schimba nivelul in caz de game over.
     */
    public void HandlePuzzleFailure() {
        // Scade 20 HP
        player.takeDamage(20);

        // Afiseaza un mesaj pentru jucator
        showWoodSignMessage("Ai esuat! Ai pierdut 20 HP.\nIncearca din nou.");

        // Daca viata a ajuns la zero, reseteaza la Nivelul 1
        if (player.getHealth() <= 0) {
            // Seteaza starea jocului inapoi la nivelul 1
            refLink.SetState(refLink.GetLevel1State());
            // Afiseaza un mesaj de game over
            showWoodSignMessage("Ai ramas fara HP!\nTe intorci la inceput.");
            // Reseteaza viata jucatorului la valoarea initiala
            player.setHealth(100);
        } else {
            // Altfel, reseteaza pozitia jucatorului la punctul de start al nivelului 2
            // Atentie: Coordonatele de start pot fi diferite.
            // Acestea sunt doar un exemplu.
            player.SetX(12 * Tile.TILE_WIDTH);
            player.SetY(17 * Tile.TILE_HEIGHT);
            player.updateBoundingBox();
        }
    }


    public void setHasTalisman(boolean hasTalisman) {
        this.hasTalisman = hasTalisman;
    }

    public boolean isHasTalisman() {
        return this.hasTalisman;
    }

    public boolean isPuzzleSolved(int puzzleId) {
        if (puzzleId > 0 && puzzleId <= TOTAL_PUZZLES_LEVEL2) {
            return puzzlesSolved[puzzleId];
        }
        return false;
    }

    public void onPuzzleFailure() {
        if (player == null) return;

        player.takeDamage(20);

        if (player.getHealth() <= 0) {
            System.out.println("DEBUG GameState: Viata < 0. Trecere la GameOverState.");
            refLink.SetState(new GameOverState(refLink));
        } else {
            System.out.println("DEBUG GameState: Puzzle esuat. Jucatorul a luat daune si a fost resetat la startul nivelului 2.");
            // Poziția de start a Nivelului 2
            float playerStartX = 2 * Tile.TILE_WIDTH;
            float playerStartY = 26 * Tile.TILE_HEIGHT;
            player.SetPosition(playerStartX, playerStartY);
        }
    }

    public void puzzleSolved(int puzzleId) {
        if (puzzleId > 0 && puzzleId <= TOTAL_PUZZLES_LEVEL2) {
            puzzlesSolved[puzzleId] = true;
            if (puzzleId - 1 < puzzleKeyPositions.length) {
                int keyTileX = puzzleKeyPositions[puzzleId - 1][0];
                int keyTileY = puzzleKeyPositions[puzzleId - 1][1];
                entities.add(new Key(refLink, (float)keyTileX * Tile.TILE_WIDTH, (float)keyTileY * Tile.TILE_HEIGHT, Assets.keyImage, puzzleId));
            }
        }
    }


    public void solvePuzzle(int puzzleId) {
        if (puzzleId > 0 && puzzleId <= TOTAL_PUZZLES_LEVEL2) {
            puzzlesSolved[puzzleId] = true;
            collectionMessage = "Puzzle rezolvat!";
            collectionMessageTime = System.currentTimeMillis();
            System.out.println("DEBUG GameState: Puzzle " + puzzleId + " rezolvat.");
        }
    }

    public String getSolvedPuzzlesAsString() {
        StringBuilder solved = new StringBuilder();
        for (int i = 1; i <= TOTAL_PUZZLES_LEVEL2; i++) {
            if (puzzlesSolved[i]) {
                if (solved.length() > 0) {
                    solved.append(",");
                }
                solved.append(i);
            }
        }
        return solved.toString();
    }

    public String getWoodSignMessage() {
        return woodSignMessage;
    }

    public void setWoodSignMessage(String woodSignMessage) {
        this.woodSignMessage = woodSignMessage;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public Map GetMap() {
        return currentMap;
    }

    public void endGame() {
        refLink.SetState(new EndGameState(refLink));
    }

    public boolean hasTalismanCollected() {
        return hasTalisman;
    }

    public void passToLevel3() {
        System.out.println("Trecere la Nivelul 3!");
        this.currentLevelIndex = 2;
        InitLevelInternal(this.currentLevelIndex, false);
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
            refLink.GetDatabaseManager().saveGameData(currentLevelIndex, player.GetX(), player.GetY(), player.getHealth(), hasLevelKey, hasDoorKeys, solvedPuzzlesString.toString());
        } else {
            System.err.println("Eroare: Nu se poate salva jocul, player-ul este null.");
        }
    }

    public void talismanCollected() {
        this.hasTalisman = true;
        collectionMessage = "Talismanul Lunii colectat!";
        collectionMessageTime = System.currentTimeMillis();
    }

    public void keyCollected() {
        this.hasLevelKey = true;
        collectionMessage = "Cheia Nivelului 1 colectata!";
        collectionMessageTime = System.currentTimeMillis();
    }

    public void doorKeyCollected(int doorId) {
        if (doorId >= 0 && doorId < hasDoorKeys.length) {
            hasDoorKeys[doorId] = true;
            collectionMessage = "Cheia Usii " + (doorId + 1) + " colectata!";
            collectionMessageTime = System.currentTimeMillis();
        }
    }

    public void showWoodSignMessage(String message) {
        this.woodSignMessage = message;
    }

    public boolean isWoodSignMessageShowing() {
        return woodSignMessage != null;
    }
}