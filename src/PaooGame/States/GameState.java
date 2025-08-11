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
import PaooGame.Entities.LevelExit; // ## MODIFICARE ##: Am adaugat importul necesar
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

    private final int[][] puzzleKeyPositions = {
            {18, 22},
            {35, 16},
            {52, 22},
            {69, 13},
            {86, 22}
    };

    private final int[][] puzzleDoorPositions = {
            {19, 24, 20, 24, 19, 25, 20, 25},
            {36, 18, 37, 18, 36, 19, 37, 19},
            {53, 24, 54, 24, 53, 25, 54, 25},
            {70, 15, 71, 15, 70, 16, 71, 16},
            {87, 24, 88, 24, 87, 25, 88, 25},
            {110, 15, 111, 15, 110, 16, 111, 16}
    };

    public GameState(RefLinks refLink) {
        super(refLink);
        this.currentLevelIndex = 0;
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
        System.out.println("✓ GameState initializat (joc nou)");
        refLink.GetGameCamera().setZoomLevel(currentZoomTarget);
    }

    public GameState(RefLinks refLink, int startLevel) {
        super(refLink);
        this.currentLevelIndex = startLevel;
        this.loadFromSaveOnInit = false;

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
        System.out.println("✓ GameState initializat (joc nou de la nivelul " + (startLevel + 1) + ")");
        refLink.GetGameCamera().setZoomLevel(currentZoomTarget);
    }

    public GameState(RefLinks refLink, boolean loadFromSave) {
        super(refLink);
        this.loadFromSaveOnInit = loadFromSave;

        this.hasLevelKey = false;
        this.hasDoorKeys = new boolean[6];
        this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];
        this.hasTalisman = false;
        this.caveEntranceUnlocked = false;

        this.currentMap = new Map(refLink);
        this.player = new Player(refLink.GetGame(), 0, 0);
        refLink.SetMap(this.currentMap);
        refLink.SetPlayer(this.player);

        InitLevelInternal(0, true);
        System.out.println("✓ GameState initializat (incarcare joc)");
        refLink.GetGameCamera().setZoomLevel(currentZoomTarget);
    }


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
                this.hasDoorKeys = loadedData.hasDoorKeys;

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
                this.hasDoorKeys = new boolean[6];
                this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];
                this.hasTalisman = false;
            }
        } else {
            this.currentLevelIndex = desiredLevelIndex;
            if (currentLevelIndex == 0) {
                playerStartX = 86 * Tile.TILE_WIDTH;
                playerStartY = 86 * Tile.TILE_HEIGHT;
            } else if (currentLevelIndex == 1) {
                playerStartX = 2 * Tile.TILE_WIDTH;
                playerStartY = 26 * Tile.TILE_HEIGHT;
            } else if (currentLevelIndex == 2) {
                playerStartX = 29 * Tile.TILE_WIDTH;
                playerStartY = 38 * Tile.TILE_HEIGHT;
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
                    entities.add(new Key(refLink, 12 * Tile.TILE_WIDTH, 85 * Tile.TILE_HEIGHT, Assets.keyImage, 0));
                }
                break;
            case 1:
                int[][] puzzleTableCoordinates = {
                        {19, 20}, {36, 14}, {53, 20}, {70, 11}, {87, 20}
                };
                for (int[] coords : puzzleTableCoordinates) {
                    float pixelX = coords[0] * Tile.TILE_WIDTH;
                    float pixelY = coords[1] * Tile.TILE_HEIGHT - (48 / 2);
                    // ## MODIFICARE ##: Am adaugat 'true' pentru a face mesele solide
                    entities.add(new DecorativeObject(refLink, pixelX, pixelY, 96, 48, Assets.puzzleTableImage, true));
                }

                if (!isPuzzleSolved(1)) entities.add(new PuzzleTrigger(refLink, 19 * Tile.TILE_WIDTH, 20 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 1));
                if (!isPuzzleSolved(2)) entities.add(new PuzzleTrigger(refLink, 36 * Tile.TILE_WIDTH, 14 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 2));
                if (!isPuzzleSolved(3)) entities.add(new PuzzleTrigger(refLink, 53 * Tile.TILE_WIDTH, 20 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 3));
                if (!isPuzzleSolved(4)) entities.add(new PuzzleTrigger(refLink, 70 * Tile.TILE_WIDTH, 11 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 4));
                if (!isPuzzleSolved(5)) entities.add(new PuzzleTrigger(refLink, 87 * Tile.TILE_WIDTH, 20 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 5));

                // ## MODIFICARE ##: Adaugarea trigger-ului pentru iesirea la Nivelul 3
                entities.add(new LevelExit(refLink, 110 * Tile.TILE_WIDTH, 14 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH * 2, Tile.TILE_HEIGHT));

                for (int i = 1; i <= TOTAL_PUZZLES_LEVEL2; i++) {
                    if (isPuzzleSolved(i)) {
                        int keyTileX = puzzleKeyPositions[i - 1][0];
                        int keyTileY = puzzleKeyPositions[i - 1][1];
                        entities.add(new Key(refLink, (float)keyTileX * Tile.TILE_WIDTH, (float)keyTileY * Tile.TILE_HEIGHT, Assets.keyImage, i));
                    }
                }
                break;
            case 2:
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
                        openPuzzleDoor(i + 1);
                        hasDoorKeys[i] = false;
                        collectionMessage = "Usa s-a deschis!";
                        collectionMessageTime = System.currentTimeMillis();
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

    private void openPuzzleDoor(int doorId) {
        if (currentMap != null && currentLevelIndex == 1) {
            int layerIndex = 1;
            int[] openGids = {Tile.DOOR_OPEN_TOP_LEFT_GID, Tile.DOOR_OPEN_TOP_RIGHT_GID, Tile.DOOR_OPEN_BOTTOM_LEFT_GID, Tile.DOOR_OPEN_BOTTOM_RIGHT_GID};

            int[] doorCoords = puzzleDoorPositions[doorId - 1];

            int tileX_TL = doorCoords[0];
            int tileY_TL = doorCoords[1];
            int tileX_TR = doorCoords[2];
            int tileY_TR = doorCoords[3];
            int tileX_BL = doorCoords[4];
            int tileY_BL = doorCoords[5];
            int tileX_BR = doorCoords[6];
            int tileY_BR = doorCoords[7];

            currentMap.changeTileGid(tileX_TL, tileY_TL, openGids[0], layerIndex);
            currentMap.changeTileGid(tileX_TR, tileY_TR, openGids[1], layerIndex);
            currentMap.changeTileGid(tileX_BL, tileY_BL, openGids[2], layerIndex);
            currentMap.changeTileGid(tileX_BR, tileY_BR, openGids[3], layerIndex);

            System.out.println("Usa " + doorId + " a fost deschisa!");
        }
    }

    @Override
    public void Draw(Graphics g) {
        GameCamera camera = refLink.GetGameCamera();
        if (currentMap == null || camera == null) return;

        float zoom = camera.getZoomLevel();
        int xStart = (int) Math.max(0, camera.getxOffset() / Tile.TILE_WIDTH);
        int xEnd = (int) Math.min(currentMap.GetWidth(), (camera.getxOffset() + refLink.GetWidth() / zoom) / Tile.TILE_WIDTH + 1);
        int yStart = (int) Math.max(0, camera.getyOffset() / Tile.TILE_HEIGHT);
        int yEnd = (int) Math.min(currentMap.GetHeight(), (camera.getyOffset() + refLink.GetHeight() / zoom) / Tile.TILE_HEIGHT + 1);

        for (int[][] layerGids : currentMap.getTilesGidsLayers()) {
            for (int y = yStart; y < yEnd; y++) {
                for (int x = xStart; x < xEnd; x++) {
                    int gid = layerGids[x][y];
                    if (gid == 0) continue;

                    if (currentLevelIndex != 1 || !isSpecialDoorTile(gid)) {
                        Tile.GetTile(gid).Draw(g,
                                (int)((x * Tile.TILE_WIDTH - camera.getxOffset()) * zoom),
                                (int)((y * Tile.TILE_HEIGHT - camera.getyOffset()) * zoom),
                                (int)(Tile.TILE_WIDTH * zoom),
                                (int)(Tile.TILE_HEIGHT * zoom),
                                currentMap.getCurrentMapTilesetImage());
                    }
                }
            }
        }

        for (Entity e : entities) {
            e.Draw(g);
        }
        if (player != null) {
            player.Draw(g);
        }

        if (currentLevelIndex == 1) {
            for (int[][] layerGids : currentMap.getTilesGidsLayers()) {
                for (int y = yStart; y < yEnd; y++) {
                    for (int x = xStart; x < xEnd; x++) {
                        int gid = layerGids[x][y];
                        if (gid != 0 && isSpecialDoorTile(gid)) {
                            Tile.GetTile(gid).Draw(g,
                                    (int)((x * Tile.TILE_WIDTH - camera.getxOffset()) * zoom),
                                    (int)((y * Tile.TILE_HEIGHT - camera.getyOffset()) * zoom),
                                    (int)(Tile.TILE_WIDTH * zoom),
                                    (int)(Tile.TILE_HEIGHT * zoom),
                                    currentMap.getCurrentMapTilesetImage());
                        }
                    }
                }
            }
        }

        drawRadialFogOverlay(g);
        drawUI(g);
    }

    private boolean isSpecialDoorTile(int gid) {
        return gid == Tile.DOOR_OPEN_TOP_LEFT_GID ||
                gid == Tile.DOOR_OPEN_TOP_RIGHT_GID ||
                gid == Tile.DOOR_OPEN_BOTTOM_LEFT_GID ||
                gid == Tile.DOOR_OPEN_BOTTOM_RIGHT_GID;
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

        if (!hasDoorKeys[0]) {
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
            int msgWidth = g.getFontMetrics().stringWidth(collectionMessage);
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

    public void passToLevel2() {
        System.out.println("Trecere la Nivelul 2!");
        boolean[] preservedDoorKeys = this.hasDoorKeys;
        this.currentLevelIndex = 1;
        this.hasTalisman = false;
        this.caveEntranceUnlocked = false;
        this.loadFromSaveOnInit = false;
        this.puzzlesSolved = new boolean[TOTAL_PUZZLES_LEVEL2 + 1];
        InitLevelInternal(this.currentLevelIndex, this.loadFromSaveOnInit);
        this.hasDoorKeys = preservedDoorKeys;
        System.out.println("DEBUG: Starea cheilor a fost transferata la Nivelul 2. Stare cheie 0: " + this.hasDoorKeys[0]);
    }

    // ## MODIFICARE ##: Metoda noua pentru trecerea la Nivelul 3
    public void passToLevel3() {
        System.out.println("Trecere la Nivelul 3!");
        this.currentLevelIndex = 2;
        InitLevelInternal(this.currentLevelIndex, false);
    }

    // ## MODIFICARE ##: Metoda noua pentru a oferi acces la lista de entitati
    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void keyCollected() {
        this.hasLevelKey = true;
        collectionMessage = "Cheia Nivelului 1 colectata!";
        collectionMessageTime = System.currentTimeMillis();
    }

    public void talismanCollected() {
        this.hasTalisman = true;
        collectionMessage = "Talismanul Soarelui colectat!";
        collectionMessageTime = System.currentTimeMillis();
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

    public void doorKeyCollected(int doorId) {
        if (doorId >= 0 && doorId < hasDoorKeys.length) {
            hasDoorKeys[doorId] = true;
            collectionMessage = "Cheia Usii " + (doorId + 1) + " colectata!";
            collectionMessageTime = System.currentTimeMillis();
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