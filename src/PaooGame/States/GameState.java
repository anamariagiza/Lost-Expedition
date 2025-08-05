package PaooGame.States;

import PaooGame.Entities.Player;
import PaooGame.Entities.Animal;
import PaooGame.Entities.Trap;
import PaooGame.Entities.Key;
import PaooGame.Entities.Agent;
import PaooGame.Entities.Entity;
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

/*!
 * \class public class GameState extends State
 * \brief Implementeaza starea de joc.
 * In aceasta stare se desfasoara propriu-zis jocul: harta este incarcata si desenata,
 * jucatorul interactioneaza cu lumea etc.
 */
public class GameState extends State {

    private Map currentMap;
    private Player player;
    private FogOfWar fogOfWar; // Added fog of war instance
    private String[] levelPaths = {"/maps/level_1.tmx", "/maps/level_2.tmx", "/maps/level_3.tmx"};
    private int currentLevelIndex;
    private boolean hasLevelKey = false;
    private boolean hasDoorKey = false;

    private float currentZoomTarget = 1.0f;
    private boolean loadFromSaveOnInit = false;
    private String currentObjective = "Exploreaza jungla.";

    private ArrayList<Entity> entities;

    private String collectionMessage = null;
    private long collectionMessageTime = 0;
    private final long MESSAGE_DURATION_MS = 2000;

    private int puzzlesSolved = 0;
    private final int TOTAL_PUZZLES_LEVEL2 = 5;

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
        this.puzzlesSolved = 0;
        InitLevelInternal(this.currentLevelIndex, false);
        System.out.println("✓ GameState initializat (joc nou)");
        refLink.GetGameCamera().setZoomLevel(currentZoomTarget);
        updateObjectiveText();
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
        this.puzzlesSolved = 0;
        InitLevelInternal(this.currentLevelIndex, loadFromSaveOnInit);
        System.out.println("✓ GameState initializat (loadFromSave: " + loadFromSaveOnInit + ")");
        refLink.GetGameCamera().setZoomLevel(currentZoomTarget);
        updateObjectiveText();
    }

    /*!
     * \fn private void updateObjectiveText()
     * \brief Actualizeaza textul obiectivului curent in functie de nivel.
     */
    private void updateObjectiveText() {
        switch (currentLevelIndex) {
            case 0: // Nivelul 1: Jungla
                if (!hasLevelKey && !hasDoorKey) {
                    currentObjective = "Obiectiv: Gaseste cheia pentru pestera si cheia usii!";
                } else if (hasLevelKey && !hasDoorKey) {
                    currentObjective = "Obiectiv: Ai cheia de nivel! Acum gaseste cheia usii!";
                } else if (!hasLevelKey && hasDoorKey) {
                    currentObjective = "Obiectiv: Ai cheia usii! Acum gaseste cheia pentru pestera!";
                } else {
                    currentObjective = "Obiectiv: Ai ambele chei! Acum gaseste intrarea in pestera!";
                }
                break;
            case 1: // Nivelul 2: Pestera
                if (!hasDoorKey) {
                    currentObjective = "Obiectiv: Rezolva " + puzzlesSolved + "/" + TOTAL_PUZZLES_LEVEL2 + " puzzle-uri si gaseste cheia usii!";
                } else {
                    currentObjective = "Obiectiv: Ai cheia usii! Deschide drumul!";
                }
                break;
            case 2: // Nivelul 3: Inca pestera, apoi comoara
                currentObjective = "Obiectiv: Invinge-l pe Magnus Voss si gaseste comoara!";
                break;
            default:
                currentObjective = "Obiectiv: Descopera secretul!";
                break;
        }
    }

    /*!
     * \fn private void InitLevelInternal(int desiredLevelIndex, boolean loadPlayerStateFromDb)
     * \brief Metoda interna de initializare a unui nivel, incarcand harta si pozitionand player-ul si camera.
     * \param desiredLevelIndex Nivelul la care dorim sa trecem (ou de la care sa incarcam).
     * \param loadPlayerStateFromDb Daca este true, se incearca incarcarea pozitiei si vietii jucatorului din DB.
     * Altfel, jucatorul este plasat la pozitia initiala a nivelului.
     */
    private void InitLevelInternal(int desiredLevelIndex, boolean loadPlayerStateFromDb) {
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
                this.puzzlesSolved = loadedData.puzzlesSolved;
                System.out.println("DEBUG GameState: Incarcare nivel din salvare: " + (currentLevelIndex + 1));
            } else {
                System.out.println("DEBUG GameState: Nu s-au putut incarca date salvate. Se porneste un joc nou (Nivel 1).");
                this.currentLevelIndex = 0;
                this.hasLevelKey = false;
                this.hasDoorKey = false;
                this.puzzlesSolved = 0;
            }
        } else {
            this.currentLevelIndex = desiredLevelIndex;
            this.hasLevelKey = false;
            this.hasDoorKey = false;
            this.puzzlesSolved = 0;

            if (currentLevelIndex == 0) { // Nivel 1 (Jungla)
                playerStartX = 100;
                playerStartY = 100;
            } else if (currentLevelIndex == 1) { // Nivelul 2 (Pestera)
                playerStartX = 100 * Tile.TILE_WIDTH;
                playerStartY = 100 * Tile.TILE_HEIGHT;
            } else if (currentLevelIndex == 2) { // Nivelul 3
                playerStartX = 50 * Tile.TILE_WIDTH;
                playerStartY = 50 * Tile.TILE_HEIGHT;
            }
        }

        if (this.currentLevelIndex < 0 || this.currentLevelIndex >= levelPaths.length) {
            System.err.println("Index nivel invalid: " + this.currentLevelIndex + ". Se reseteaza la nivelul 1.");
            this.currentLevelIndex = 0;
            this.hasLevelKey = false;
            this.hasDoorKey = false;
            this.puzzlesSolved = 0;
        }

        currentMap = refLink.GetMap();
        if (currentMap != null) {
            currentMap.LoadMapFromFile(levelPaths[this.currentLevelIndex]);
            System.out.println("DEBUG GameState: Nivelul " + (this.currentLevelIndex + 1) + " incarcat: " + levelPaths[this.currentLevelIndex]);

            // Initialize fog of war for this level
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
        updateObjectiveText();

        entities = new ArrayList<>();
        switch (currentLevelIndex) {
            case 0: // Nivelul 1: Jungla
                entities.add(new Animal(refLink, 53 * Tile.TILE_WIDTH, 5 * Tile.TILE_HEIGHT, 51 * Tile.TILE_WIDTH, 56 * Tile.TILE_WIDTH, Animal.AnimalType.JAGUAR));
                entities.add(new Animal(refLink, 10 * Tile.TILE_WIDTH, 36 * Tile.TILE_HEIGHT, 8 * Tile.TILE_WIDTH, 11 * Tile.TILE_WIDTH, Animal.AnimalType.MONKEY));
                entities.add(new Animal(refLink, 89 * Tile.TILE_WIDTH, 29 * Tile.TILE_HEIGHT, 88 * Tile.TILE_WIDTH, 91 * Tile.TILE_WIDTH, Animal.AnimalType.MONKEY));
                entities.add(new Animal(refLink, 84 * Tile.TILE_WIDTH, 57 * Tile.TILE_HEIGHT, 82 * Tile.TILE_WIDTH, 85 * Tile.TILE_WIDTH, Animal.AnimalType.BAT));
                entities.add(new Trap(refLink, 20 * Tile.TILE_WIDTH, 20 * Tile.TILE_HEIGHT, Assets.spikeTrapImage));
                if (Assets.smallTrapAnim != null && Assets.smallTrapAnim.length > 0) {
                    entities.add(new Trap(refLink, 30 * Tile.TILE_WIDTH, 15 * Tile.TILE_HEIGHT, Assets.smallTrapAnim[0]));
                }

                if (!hasLevelKey) {
                    entities.add(new Key(refLink, 45 * Tile.TILE_WIDTH, 52 * Tile.TILE_HEIGHT, Assets.keyImage, Key.KeyType.NEXT_LEVEL_KEY));
                }
                if (!hasDoorKey) {
                    entities.add(new Key(refLink, 12 * Tile.TILE_WIDTH, 85 * Tile.TILE_HEIGHT, Assets.keyImage, Key.KeyType.DOOR_KEY));
                }
                break;
            case 1: // Nivelul 2: Pestera
                entities.add(new Animal(refLink, 200, 200, 100, 400, Animal.AnimalType.BAT));
                entities.add(new Trap(refLink, 600, 300, Assets.spikeTrapImage));
                currentObjective = "Obiectiv: Rezolva " + puzzlesSolved + "/" + TOTAL_PUZZLES_LEVEL2 + " puzzle-uri si gaseste cheia usii!";
                entities.add(new Trap(refLink, 25 * Tile.TILE_WIDTH, 25 * Tile.TILE_HEIGHT, Assets.smallTrapAnim != null && Assets.smallTrapAnim.length > 0 ? Assets.smallTrapAnim[0] : Assets.spikeTrapImage));
                entities.add(new Trap(refLink, 35 * Tile.TILE_WIDTH, 30 * Tile.TILE_HEIGHT, Assets.smallTrapAnim != null && Assets.smallTrapAnim.length > 0 ? Assets.smallTrapAnim[0] : Assets.spikeTrapImage));

                break;
            case 2: // Nivelul 3: Incaperea finala a pesterii
                entities.add(new Animal(refLink, 450, 450, 400, 500, Animal.AnimalType.JAGUAR));
                entities.add(new Agent(refLink, 600, 600, 550, 650, true));
                currentObjective = "Obiectiv: Invinge-l pe Magnus Voss si gaseste comoara!";
                break;
        }
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea elementelor din joc, inclusiv jucatorul si camera.
     * \return void
     */
    @Override
    public void Update() {
        if (collectionMessage != null && System.currentTimeMillis() - collectionMessageTime > MESSAGE_DURATION_MS) {
            collectionMessage = null;
        }

        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_P) && player != null) {
            saveCurrentState();
            refLink.GetGame().setPreviousState(this);
            refLink.SetState(new PauseState(refLink));
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

        // Update fog of war based on player position
        if (fogOfWar != null) {
            fogOfWar.update();
        }

        if (player != null) {
            player.Update();
            if (player.getHealth() <= 0) {
                refLink.SetState(new GameOverState(refLink));
                return;
            }
            // Conditie pentru trecerea la nivelul urmator cu 'E' (la punctul de iesire)
            if (currentLevelIndex == 0 && hasLevelKey && hasDoorKey) {
                if (player.GetX() / Tile.TILE_WIDTH > 95 && player.GetY() / Tile.TILE_HEIGHT < 20 &&
                        refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                    if (currentLevelIndex + 1 < levelPaths.length) {
                        saveCurrentState();
                        InitLevelInternal(currentLevelIndex + 1, false);
                        System.out.println("DEBUG GameState: Trecere la Nivelul " + (currentLevelIndex + 1) + " prin poarta!");
                    } else {
                        System.out.println("Ultimul nivel atins!");
                    }
                }
            }
            // Conditie pentru activarea unui puzzle in Nivelul 2 cu 'E'
            else if (currentLevelIndex == 1 && puzzlesSolved < TOTAL_PUZZLES_LEVEL2) {
                if (player.GetX() / Tile.TILE_WIDTH > 24 && player.GetX() / Tile.TILE_WIDTH < 26 &&
                        player.GetY() / Tile.TILE_HEIGHT > 24 && player.GetY() / Tile.TILE_HEIGHT < 26 &&
                        refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                    System.out.println("DEBUG GameState: Jucator la masuta puzzle. Activare PuzzleState!");
                    refLink.GetGame().setPreviousState(this);
                    refLink.SetState(new PuzzleState(refLink));
                    return;
                }
            }
            // Conditie pentru deschiderea usii nivelului 2 cu 'E' (dupa rezolvarea tuturor puzzle-urilor si colectarea cheii usii)
            else if (currentLevelIndex == 1 && hasDoorKey && puzzlesSolved >= TOTAL_PUZZLES_LEVEL2) {
                if (player.GetX() / Tile.TILE_WIDTH > 50 && player.GetY() / Tile.TILE_HEIGHT > 50 &&
                        refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                    System.out.println("DEBUG GameState: Usa Nivelului 2 a fost deblocata!");
                    if (currentLevelIndex + 1 < levelPaths.length) {
                        saveCurrentState();
                        InitLevelInternal(currentLevelIndex + 1, false);
                        System.out.println("DEBUG GameState: Trecere la Nivelul " + (currentLevelIndex + 1) + " dupa deschiderea usii!");
                    } else {
                        System.out.println("Ultimul nivel atins!");
                    }
                    return;
                }
            }
        }

        for (int i = entities.size() - 1; i >= 0; i--) {
            Entity e = entities.get(i);
            e.Update();
            if (e instanceof Key) {
                Key k = (Key) e;
                if (k.isCollected()) {
                    entities.remove(i);
                }
            }
        }
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza elementele specifice starii de joc pe ecran.
     * \param g Contextul grafic in care sa se realizeze desenarea.
     */
    @Override
    public void Draw(Graphics g) {
        if (collectionMessage != null && System.currentTimeMillis() - collectionMessageTime > MESSAGE_DURATION_MS) {
            collectionMessage = null;
        }

        if (currentMap != null) {
            // Draw map with smooth fog of war applied
            drawMapWithSmoothFogOfWar(g);
        }

        // Draw entities only if they are visible
        for (Entity e : entities) {
            // Verifica vizibilitatea entității cu o condiție similară cu cea de la player
            // Entitățile se desenează peste hartă, deci nu au nevoie de logica de "revealed tiles"
            if (isEntityVisible(e)) {
                e.Draw(g);
            }
        }

        if (player != null) {
            player.Draw(g);
        } else {
            g.setColor(Color.RED);
            g.drawString("PLAYER NULL!", refLink.GetWidth() / 2, refLink.GetHeight() / 2);
        }

        drawUI(g);
    }

    /*!
     * \fn private void drawMapWithSmoothFogOfWar(Graphics g)
     * \brief Deseneaza harta cu un efect de "fog of war" fluid, sub forma unui cerc cu gradient.
     */
    private void drawMapWithSmoothFogOfWar(Graphics g) {
        if (currentMap == null || player == null) {
            return;
        }

        GameCamera camera = refLink.GetGameCamera();
        Graphics2D g2d = (Graphics2D) g.create();

        // 1. Deseneaza harta normal
        int startTileX = Math.max(0, (int)(camera.getXOffset() / Tile.TILE_WIDTH) - 1);
        int endTileX = Math.min(currentMap.getWidth(), (int)((camera.getXOffset() + refLink.GetWidth() / camera.getZoomLevel()) / Tile.TILE_WIDTH) + 2);
        int startTileY = Math.max(0, (int)(camera.getYOffset() / Tile.TILE_HEIGHT) - 1);
        int endTileY = Math.min(currentMap.getHeight(), (int)((camera.getYOffset() + refLink.GetHeight() / camera.getZoomLevel()) / Tile.TILE_HEIGHT) + 2);

        for (int y = startTileY; y < endTileY; y++) {
            for (int x = startTileX; x < endTileX; x++) {
                Tile tile = currentMap.GetTile(x, y);
                if (tile != null) {
                    int screenX = (int)((x * Tile.TILE_WIDTH - camera.getXOffset()) * camera.getZoomLevel());
                    int screenY = (int)((y * Tile.TILE_HEIGHT - camera.getYOffset()) * camera.getZoomLevel());
                    int tileWidth = (int)(Tile.TILE_WIDTH * camera.getZoomLevel());
                    int tileHeight = (int)(Tile.TILE_HEIGHT * camera.getZoomLevel());
                    tile.Draw(g2d, screenX, screenY, tileWidth, tileHeight);
                }
            }
        }

        // 2. Creează și desenează overlay-ul de "fog of war" cu gradient folosind RadialGradientPaint
        int playerScreenX = (int) ((player.GetX() - camera.getXOffset()) * camera.getZoomLevel() + player.GetWidth() / 2 * camera.getZoomLevel());
        int playerScreenY = (int) ((player.GetY() - camera.getYOffset()) * camera.getZoomLevel() + player.GetHeight() / 2 * camera.getZoomLevel());

        float radius = (float) (fogOfWar.getVisionRadius() * Tile.TILE_WIDTH * camera.getZoomLevel());

        // Definește culorile pentru gradient
        Color transparentBlack = new Color(0, 0, 0, 0);
        Color opaqueBlack = new Color(0, 0, 0, 200); // Poți ajusta opacitatea aici

        // Definește punctele de oprire (fractions) ale gradientului
        float[] dist = {0.0f, 0.7f, 1.0f}; // 0% transparent, 70% transparent, 100% opac
        Color[] colors = {transparentBlack, transparentBlack, opaqueBlack};

        // Creează un obiect RadialGradientPaint
        RadialGradientPaint p = new RadialGradientPaint(
                playerScreenX, playerScreenY, // Centrul gradientului
                radius,                      // Raza cercului
                dist,                        // Punctele de oprire
                colors,                      // Culorile corespunzătoare
                MultipleGradientPaint.CycleMethod.NO_CYCLE // Nu repeta gradientul
        );

        g2d.setPaint(p);
        g2d.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        g2d.dispose();
    }

    /*!
     * \fn private boolean isEntityVisible(Entity entity)
     * \brief Verifica daca o entitate este vizibila in fog of war.
     */
    private boolean isEntityVisible(Entity entity) {
        if (fogOfWar == null || player == null) return true;

        // Calculam distanța dintre centrul jucătorului și centrul entității
        float playerCenterX = player.GetX() + player.GetWidth() / 2;
        float playerCenterY = player.GetY() + player.GetHeight() / 2;
        float entityCenterX = entity.GetX() + entity.GetWidth() / 2;
        float entityCenterY = entity.GetY() + entity.GetHeight() / 2;

        double distance = Math.sqrt(Math.pow(playerCenterX - entityCenterX, 2) + Math.pow(playerCenterY - entityCenterY, 2));

        // Comparăm distanța cu raza de vizibilitate a jucătorului
        return distance <= fogOfWar.getVisionRadius() * Tile.TILE_WIDTH;
    }

    /*!
     * \fn private void drawUI(Graphics g)
     * \brief Deseneaza toate elementele de interfata din joc.
     */
    private void drawUI(Graphics g) {
        drawHealthBar(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        int objectiveWidth = fm.stringWidth(currentObjective);
        int objectiveX = (refLink.GetWidth() - objectiveWidth) / 2;
        g.drawString(currentObjective, objectiveX, 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Nivel curent: " + (currentLevelIndex + 1), 10, 20 + 30);
        g.drawString("Apasa 'E' la poarta de iesire pentru a trece nivelul.", 10, 40 + 30);
        g.drawString("Apasa 'P' pentru Meniu Pauza.", 10, 60 + 30);
        g.drawString("Foloseste W,A,S,D pentru miscare.", 10, 80 + 30);
        g.drawString("Apasa SHIFT pentru alergat.", 10, 100 + 30);
        g.drawString("Apasa SPACE pentru saritura.", 10, 120 + 30);
        g.drawString("Zoom: " + String.format("%.1f", refLink.GetGameCamera().getZoomLevel()), 10, 140 + 30);
        g.drawString("Apasa Z pentru a comuta zoom.", 10, 160 + 30);
        g.drawString("DEBUG: Apasa H pentru daune, G pentru vindecare.", 10, 180 + 30);
        g.drawString("DEBUG: Ai cheia Nivelului 1: " + hasLevelKey, 10, 200 + 30);
        g.drawString("DEBUG: Ai cheia usii Nivel 2: " + hasDoorKey, 10, 220 + 30);
        g.drawString("DEBUG: Puzzle-uri Nivel 2: " + puzzlesSolved + "/" + TOTAL_PUZZLES_LEVEL2, 10, 240 + 30);

        // NOU: Apelăm noua metodă de desenare a mini-hărții, fără Fog of War
        drawMiniMap(g);

        if (collectionMessage != null) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            int msgWidth = fm.stringWidth(collectionMessage);
            g.drawString(collectionMessage, (refLink.GetWidth() - msgWidth) / 2, refLink.GetHeight() / 2);
        }
    }

    /*!
     * \fn private void drawHealthBar(Graphics g)
     * \brief Deseneaza bara de viata a jucatorului in coltul stanga sus.
     */
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

    /*!
     * \fn private void drawMiniMap(Graphics g)
     * \brief Deseneaza mini-harta fara Fog of War.
     */
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

        int playerMiniMapX = miniMapX + (int)(player.GetX() * mapScaleX);
        int playerMiniMapY = miniMapY + (int)(player.GetY() * mapScaleY);
        int playerMiniMapSize = Math.max(2, (int)(player.GetWidth() * mapScaleX));

        g.setColor(Color.CYAN);
        g.fillOval(playerMiniMapX, playerMiniMapY, playerMiniMapSize, playerMiniMapSize);

        for(Entity e : entities) {
            if (e instanceof Key) {
                Key k = (Key)e;
                if (!k.isCollected()) {
                    int keyMiniMapX = miniMapX + (int)(k.GetX() * mapScaleX);
                    int keyMiniMapY = miniMapY + (int)(k.GetY() * mapScaleY);
                    g.setColor(Color.YELLOW);
                    g.fillOval(keyMiniMapX, keyMiniMapY, 5, 5);
                }
            }
        }
    }

    /*!
     * \fn private void saveCurrentState()
     * \brief Salveaza starea curenta a jocului folosind DatabaseManager.
     */
    private void saveCurrentState() {
        if (player != null) {
            refLink.GetDatabaseManager().saveGameData(currentLevelIndex, player.GetX(), player.GetY(), player.getHealth(), hasLevelKey, hasDoorKey, puzzlesSolved);
        } else {
            System.err.println("Eroare: Nu se poate salva jocul, player-ul este null.");
        }
    }

    /*!
     * \fn public void keyCollected()
     * \brief Metoda apelata de clasa Key cand jucatorul colecteaza cheia de nivel.
     */
    public void keyCollected() {
        this.hasLevelKey = true;
        collectionMessage = "Cheia Nivelului 1 colectata!";
        collectionMessageTime = System.currentTimeMillis();
        updateObjectiveText();
        System.out.println("DEBUG GameState: Cheia Nivelului 1 a fost marcata ca fiind colectata.");
    }

    /*!
     * \fn public void doorKeyCollected()
     * \brief Metoda apelata de clasa Key cand jucatorul colecteaza cheia de usa.
     */
    public void doorKeyCollected() {
        this.hasDoorKey = true;
        collectionMessage = "Cheia Usii colectata!";
        collectionMessageTime = System.currentTimeMillis();
        updateObjectiveText();
        System.out.println("DEBUG GameState: Cheia usii Nivel 2 a fost marcata ca fiind colectata.");
    }

    /*!
     * \fn public void puzzleSolved()
     * \brief Marcheaza rezolvarea unui puzzle.
     */
    public void puzzleSolved() {
        this.puzzlesSolved++;
        updateObjectiveText();
        System.out.println("DEBUG GameState: Un puzzle a fost rezolvat. Total: " + this.puzzlesSolved);
    }

    /*!
     * \fn public int getPuzzlesSolved()
     * \brief Returneaza numarul de puzzle-uri rezolvate.
     */
    public int getPuzzlesSolved() {
        return puzzlesSolved;
    }

    /*!
     * \fn public int getTotalPuzzlesLevel2()
     * \brief Returneaza numarul total de puzzle-uri pentru nivelul 2.
     */
    public int getTotalPuzzlesLevel2() {
        return TOTAL_PUZZLES_LEVEL2;
    }

    public Map GetMap() {
        return currentMap;
    }
}