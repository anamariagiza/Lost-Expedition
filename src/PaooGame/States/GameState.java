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

    // Adaug o referinta la NPC si CaveEntrance pentru a usura verificarea
    private NPC caveGuardianNPC;
    private CaveEntrance caveEntrance;

    private String collectionMessage = null;
    private long collectionMessageTime = 0;
    private final long MESSAGE_DURATION_MS = 2000;

    private int puzzlesSolved = 0;
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
        this.puzzlesSolved = 0;
        this.hasTalisman = false;
        this.caveEntranceUnlocked = false;
        InitLevelInternal(this.currentLevelIndex, false);
        System.out.println("✓ GameState initializat (joc nou)");
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
        this.puzzlesSolved = 0;
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
                playerStartX = 86 * Tile.TILE_WIDTH;
                playerStartY = 86 * Tile.TILE_HEIGHT;
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

        entities = new ArrayList<>();
        switch (currentLevelIndex) {
            case 0: // Nivelul 1: Jungla
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

                // Dimensiunile pentru intrarea in pestera sunt aproximative, ajusta-le
                caveEntrance = new CaveEntrance(refLink, 85 * Tile.TILE_WIDTH, 92 * Tile.TILE_HEIGHT, Tile.TILE_WIDTH * 3, Tile.TILE_HEIGHT * 3);
                entities.add(caveEntrance);
                if (!hasDoorKey) {
                    entities.add(new Key(refLink, 12 * Tile.TILE_WIDTH, 85 * Tile.TILE_HEIGHT, Assets.keyImage, Key.KeyType.DOOR_KEY));
                }
                break;
            case 1: // Nivelul 2: Pestera
                entities.add(new Animal(refLink, 200, 200, 100, 400, Animal.AnimalType.BAT));
                entities.add(new Trap(refLink, 50 * Tile.TILE_WIDTH, 50 * Tile.TILE_HEIGHT, Assets.spikeTrapImage));
                entities.add(new Trap(refLink, 25 * Tile.TILE_WIDTH, 25 * Tile.TILE_HEIGHT, Assets.spikeTrapImage));
                entities.add(new Trap(refLink, 35 * Tile.TILE_WIDTH, 30 * Tile.TILE_HEIGHT, Assets.spikeTrapImage));

                break;
            case 2: // Nivelul 3: Incaperea finala a pesterii
                entities.add(new Animal(refLink, 450, 450, 400, 500, Animal.AnimalType.JAGUAR));
                entities.add(new Agent(refLink, 600, 600, 550, 650, true));
                break;
        }
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea elementelor din joc, delegand catre starea curenta.
     * \return void
     */
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
        }

        boolean playerInContactWithAnimal = false;
        boolean playerInContactWithTrap = false;

        // ------ MODIFICARE LOGICA PENTRU TRECEREA LA NIVELUL 2 ------
        // 1. Verificam daca player-ul e langa NPC
        if (caveGuardianNPC != null && player.GetBounds().intersects(caveGuardianNPC.GetBounds())) {
            // 2. Verificam daca player-ul are talismanul si apasa tasta de actiune
            if (hasTalisman && refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                // 3. Deblocam intrarea in pestera si ii dam talismanul NPC-ului
                setCaveEntranceUnlocked(true);
                removeTalismanFromInventory();

                // Afisam un mesaj pentru confirmare
                collectionMessage = "Talismanul a fost predat! Intrarea in pestera este deblocata.";
                collectionMessageTime = System.currentTimeMillis();
            } else if (!hasTalisman && refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                collectionMessage = "Nu am talismanul! Trebuie sa-l gasesti pentru a intra.";
                collectionMessageTime = System.currentTimeMillis();
            }
        }

        // 4. Verificam daca player-ul se afla la intrarea in pestera si este deblocata
        if (caveEntrance != null && player.GetBounds().intersects(caveEntrance.GetBounds()) && isCaveEntranceUnlocked()) {
            passToLevel2();
            return; // Imediat ce trecem la noul nivel, iesim din update.
        }
        // -----------------------------------------------------------

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

        // 1. Desenează toată harta, fără nicio logică de fog of war.
        if (currentMap != null) {
            drawFullMap(g);
        }

        // 2. Desenează entitățile (inamici, chei, etc.)
        for (Entity e : entities) {
            e.Draw(g);
        }

        // 3. Desenează jucătorul deasupra tuturor.
        if (player != null) {
            player.Draw(g);
        }

        // 4. Aplică un overlay radial pentru a crea efectul de fog of war.
        drawRadialFogOverlay(g);

        // 5. Desenează elementele de UI (bara de viață, mini-map, mesaje, etc.)
        drawUI(g);
    }

    // Metoda noua care desenează întreaga hartă fără fog of war
    private void drawFullMap(Graphics g) {
        if (currentMap == null) {
            // Fallback: fundal negru dacă harta nu există
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

        // Iterăm prin fiecare strat al hărții pentru a le desena pe toate.
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

    // Metoda noua care aplica un overlay radial pentru fog of war
    private void drawRadialFogOverlay(Graphics g) {
        if (player == null) return;

        GameCamera camera = refLink.GetGameCamera();
        Graphics2D g2d = (Graphics2D) g.create();

        int playerScreenX = (int) ((player.GetX() - camera.getxOffset()) * camera.getZoomLevel() + player.GetWidth() / 2 * camera.getZoomLevel());
        int playerScreenY = (int) ((player.GetY() - camera.getyOffset()) * camera.getZoomLevel() + player.GetHeight() / 2 * camera.getZoomLevel());

        float radius = (float) (fogOfWar.getVisionRadius() * Tile.TILE_WIDTH * camera.getZoomLevel());

        Color transparentBlack = new Color(0, 0, 0, 0);
        Color opaqueBlack = new Color(0, 0, 0, 220); // Negru semi-opac pentru efect

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


    /*!
     * \fn private boolean isEntityVisible(Entity entity)
     * \brief Verifica daca o entitate este vizibila in fog of war.
     */
    private boolean isEntityVisible(Entity entity) {
        if (fogOfWar == null || player == null) return true;
        float playerCenterX = player.GetX() + player.GetWidth() / 2;
        float playerCenterY = player.GetY() + player.GetHeight() / 2;
        float entityCenterX = entity.GetX() + entity.GetWidth() / 2;
        float entityCenterY = entity.GetY() + entity.GetHeight() / 2;
        double distance = Math.sqrt(Math.pow(playerCenterX - entityCenterX, 2) + Math.pow(playerCenterY - entityCenterY, 2));

        return distance <= fogOfWar.getVisionRadius() * Tile.TILE_WIDTH;
    }

    /*!
     * \fn private void drawUI(Graphics g)
     * \brief Deseneaza toate elementele de interfata din joc, cu modificarile cerute.
     */
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
            g.drawString("Puzzle-uri Nivel 2: " + puzzlesSolved + "/" + TOTAL_PUZZLES_LEVEL2, 10, startY + lineHeight * 9);
        }

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
     * \brief Deseneaza mini-harta fara Fog of War, afisand cheile si animalele ca puncte galbene.
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

    /*!
     * \fn public void saveCurrentState()
     * \brief Salveaza starea curenta a jocului folosind DatabaseManager.
     */
    public void saveCurrentState() {
        if (player != null) {
            refLink.GetDatabaseManager().saveGameData(currentLevelIndex, player.GetX(), player.GetY(), player.getHealth(), hasLevelKey, hasDoorKey, puzzlesSolved);
        } else {
            System.err.println("Eroare: Nu se poate salva jocul, player-ul este null.");
        }
    }

    // Metoda noua pentru a trece la nivelul 2
    public void passToLevel2() {
        System.out.println("Trecere la Nivelul 2!");
        this.currentLevelIndex = 1;
        this.hasTalisman = false; // Reseteaza talismanul pentru ca a fost predat
        this.caveEntranceUnlocked = false; // Blocam intrarea pentru noul nivel
        this.loadFromSaveOnInit = false; // Incepem nivelul de la pozitia de start, nu din salvare
        InitLevelInternal(this.currentLevelIndex, this.loadFromSaveOnInit);
    }

    /*!
     * \fn public void keyCollected()
     * \brief Metoda apelata de clasa Key cand jucatorul colecteaza cheia de nivel.
     */
    public void keyCollected() {
        this.hasLevelKey = true;
        collectionMessage = "Cheia Nivelului 1 colectata!";
        collectionMessageTime = System.currentTimeMillis();
        System.out.println("DEBUG GameState: Cheia Nivelului 1 a fost marcata ca fiind colectata.");
    }

    /*!
     * \fn public void talismanCollected()
     * \brief Metoda apelata de clasa Talisman cand jucatorul colecteaza talismanul.
     */
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
        // Stergem talismanul din lista de entitati
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
        // collectionMessage este setat deja in metoda Update
    }

    public boolean isCaveEntranceUnlocked() {
        return caveEntranceUnlocked;
    }

    /*!
     * \fn public void doorKeyCollected()
     * \brief Metoda apelata de clasa Key cand jucatorul colecteaza cheia de usa.
     */
    public void doorKeyCollected() {
        this.hasDoorKey = true;
        collectionMessage = "Cheia Usii colectata!";
        collectionMessageTime = System.currentTimeMillis();
        System.out.println("DEBUG GameState: Cheia usii Nivel 2 a fost marcata ca fiind colectata.");
    }

    /*!
     * \fn public void puzzleSolved()
     * \brief Marcheaza rezolvarea unui puzzle.
     */
    public void puzzleSolved() {
        this.puzzlesSolved++;
        System.out.println("DEBUG GameState: Un puzzle a fost rezolvat. Total: " + this.puzzlesSolved);
    }


    /*!
     * \fn public int getPuzzlesSolved()
     * \brief Marcheaza rezolvarea unui puzzle.
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