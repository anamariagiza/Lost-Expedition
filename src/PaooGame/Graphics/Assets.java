package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/*! \class public class Assets
    \brief Clasa incarca fiecare element grafic necesar jocului.

    Game assets include tot ce este folosit intr-un joc: imagini, sunete, harti etc.
 */
public class Assets
{
    public static SpriteSheet gameSpriteSheet;
    public static BufferedImage jungleTilesetImage;

    public static BufferedImage gameLogo;
    public static BufferedImage backgroundMenu;

    public static final int PLAYER_FRAME_WIDTH = 64;
    public static final int PLAYER_FRAME_HEIGHT = 64;


    // Tablouri de cadre pentru animațiile jucătorului
    // Animații de Mers (Walk) - deja directionale
    public static BufferedImage[] playerDown;
    public static BufferedImage[] playerUp;
    public static BufferedImage[] playerLeft;
    public static BufferedImage[] playerRight;

    // NOU: Animații de Idle (acum extrase directional)
    public static BufferedImage[] playerIdleAllDirections; // Va contine toate cadrele din idle.png
    public static BufferedImage[] playerIdleDown;
    public static BufferedImage[] playerIdleUp;
    public static BufferedImage[] playerIdleLeft;
    public static BufferedImage[] playerIdleRight;

    // NOU: Animații de Alergat (acum extrase directional)
    public static BufferedImage[] playerRunAllDirections; // Va contine toate cadrele din run.png
    public static BufferedImage[] playerRunDown;
    public static BufferedImage[] playerRunUp;
    public static BufferedImage[] playerRunLeft;
    public static BufferedImage[] playerRunRight;

    // NOU: Animații de Sarit (acum extrase directional)
    public static BufferedImage[] playerJumpAllDirections; // Va contine toate cadrele din jump.png
    public static BufferedImage[] playerJumpDown;
    public static BufferedImage[] playerJumpUp;
    public static BufferedImage[] playerJumpLeft;
    public static BufferedImage[] playerJumpRight;


    public static BufferedImage[] playerClimb;
    public static BufferedImage[] playerHurt;
    public static BufferedImage[] playerShoot;
    public static BufferedImage[] playerBackslash;
    public static BufferedImage[] playerHalfslash;
    public static BufferedImage[] playerEmote;
    public static BufferedImage[] playerCombatIdle;
    public static BufferedImage[] playerThrust;
    public static BufferedImage[] playerSit;
    public static BufferedImage[] playerSpellcast;
    public static BufferedImage[] playerSlash;


    /*! \fn public static void Init()
        \brief Functia initializaza referintele catre elementele grafice utilizate (asset-urile rapide).
     */
    public static void Init()
    {
        gameLogo = ImageLoader.LoadImage("/textures/logo.png");
        if (gameLogo == null) {
            System.err.println("Eroare: Nu s-a putut incarca logo.png! Verificati calea si numele fisierului.");
        }
    }

    /*! \fn public static void LoadGameAssets()
        \brief Incarca asset-urile mari ale jocului (tileset, dale specifice, player) care dureaza mai mult.
        Aceasta metoda va fi apelata din LoadingScreenState.
     */
    public static void LoadGameAssets() {
        backgroundMenu = ImageLoader.LoadImage("/textures/menu_background.jpg");
        if (backgroundMenu == null) {
            System.err.println("Eroare: Nu s-a putut incarca menu_background.jpg! Verificati calea si numele fisierului.");
        }

        jungleTilesetImage = ImageLoader.LoadImage("/textures/gentle forest.png");
        if (jungleTilesetImage == null) {
            System.err.println("Eroare: Nu s-a putut incarca gentle forest.png! Verificati calea si numele fisierului.");
        }
        gameSpriteSheet = new SpriteSheet(jungleTilesetImage);

        // --- INCARCAREA CADRELOR DE ANIMAȚIE DIN FIȘIERE INDIVIDUALE ---
        // Calea include acum "/player/"
        BufferedImage idleSheet = ImageLoader.LoadImage("/textures/player/idle.png");
        BufferedImage walkSheet = ImageLoader.LoadImage("/textures/player/walk.png");
        BufferedImage runSheet = ImageLoader.LoadImage("/textures/player/run.png");
        BufferedImage jumpSheet = ImageLoader.LoadImage("/textures/player/jump.png");
        BufferedImage climbSheet = ImageLoader.LoadImage("/textures/player/climb.png");
        BufferedImage hurtSheet = ImageLoader.LoadImage("/textures/player/hurt.png");
        BufferedImage sitSheet = ImageLoader.LoadImage("/textures/player/sit.png");
        BufferedImage emoteSheet = ImageLoader.LoadImage("/textures/player/emote.png");
        BufferedImage thrustSheet = ImageLoader.LoadImage("/textures/player/thrust.png");
        BufferedImage halfslashSheet = ImageLoader.LoadImage("/textures/player/halfslash.png");
        BufferedImage backslashSheet = ImageLoader.LoadImage("/textures/player/backslash.png");
        BufferedImage spellcastSheet = ImageLoader.LoadImage("/textures/player/spellcast.png");
        BufferedImage shootSheet = ImageLoader.LoadImage("/textures/player/shoot.png");
        BufferedImage combatIdleSheet = ImageLoader.LoadImage("/textures/player/combat_idle.png");
        BufferedImage slashSheet = ImageLoader.LoadImage("/textures/player/slash.png");


        // --- DE CUPAREA CADRELOR DE ANIMAȚIE DIN FIECARE SHEET INDIVIDUAL ---
        // Ordinea specificata: 0=Spate, 1=Stanga, 2=Fata, 3=Dreapta

        System.out.println("DEBUG ASSETS: Incerc decupare walkSheet...");
        // WALK (walk.png: 576x256 pixeli -> 4 rânduri, 9 coloane de 64x64)
        playerUp    = cropFramesFromSheet(walkSheet, 1, 9, 0, 0); // Rând 0 (Spate)
        playerLeft  = cropFramesFromSheet(walkSheet, 1, 9, 1, 0); // Rând 1 (Stanga)
        playerDown  = cropFramesFromSheet(walkSheet, 1, 9, 2, 0); // Rând 2 (Fata)
        playerRight = cropFramesFromSheet(walkSheet, 1, 9, 3, 0); // Rând 3 (Dreapta)
        if (playerUp == null) System.err.println("DEBUG ASSETS: playerUp a esuat la decupare.");
        if (playerLeft == null) System.err.println("DEBUG ASSETS: playerLeft a esuat la decupare.");
        if (playerDown == null) System.err.println("DEBUG ASSETS: playerDown a esuat la decupare.");
        if (playerRight == null) System.err.println("DEBUG ASSETS: playerRight a esuat la decupare.");


        System.out.println("DEBUG ASSETS: Incerc decupare idleSheet...");
        // IDLE (idle.png: 128x256 pixeli -> 4 rânduri, 2 coloane de 64x64)
        // Decupam toate cadrele, apoi extragem cadrele specifice fiecarei directii
        playerIdleAllDirections = cropFramesFromSheet(idleSheet, 4, 2);
        if (playerIdleAllDirections != null && playerIdleAllDirections.length >= 8) {
            // Extragem primul cadru al fiecarei animatii de directie ca poza de idle statica
            playerIdleUp    = new BufferedImage[]{playerIdleAllDirections[0]}; // Primul cadru din rândul UP (index 0)
            playerIdleLeft  = new BufferedImage[]{playerIdleAllDirections[2]}; // Primul cadru din rândul LEFT (index 2)
            playerIdleDown  = new BufferedImage[]{playerIdleAllDirections[4]}; // Primul cadru din rândul DOWN (index 4)
            playerIdleRight = new BufferedImage[]{playerIdleAllDirections[6]}; // Primul cadru din rândul RIGHT (index 6)
        } else {
            System.err.println("DEBUG ASSETS: playerIdleAllDirections a esuat la decupare sau este incomplet.");
            // Fallback: folosim primul cadru din mers ca idle
            playerIdleUp = playerUp != null && playerUp.length > 0 ? new BufferedImage[]{playerUp[0]} : null;
            playerIdleLeft = playerLeft != null && playerLeft.length > 0 ? new BufferedImage[]{playerLeft[0]} : null;
            playerIdleDown = playerDown != null && playerDown.length > 0 ? new BufferedImage[]{playerDown[0]} : null;
            playerIdleRight = playerRight != null && playerRight.length > 0 ? new BufferedImage[]{playerRight[0]} : null;
        }


        System.out.println("DEBUG ASSETS: Incerc decupare runSheet...");
        // RUN (run.png: 512x256 pixeli -> 4 rânduri, 8 coloane de 64x64)
        playerRunAllDirections = cropFramesFromSheet(runSheet, 4, 8);
        if (playerRunAllDirections != null) {
            playerRunUp    = cropFramesFromSheet(runSheet, 1, 8, 0, 0); // Rând 0 (Spate)
            playerRunLeft  = cropFramesFromSheet(runSheet, 1, 8, 1, 0); // Rând 1 (Stanga)
            playerRunDown  = cropFramesFromSheet(runSheet, 1, 8, 2, 0); // Rând 2 (Fata)
            playerRunRight = cropFramesFromSheet(runSheet, 1, 8, 3, 0); // Rând 3 (Dreapta)
        } else {
            System.err.println("DEBUG ASSETS: playerRunAllDirections a esuat la decupare.");
        }


        System.out.println("DEBUG ASSETS: Incerc decupare jumpSheet...");
        // JUMP (jump.png: 320x256 pixeli -> 4 rânduri, 5 coloane de 64x64)
        playerJumpAllDirections = cropFramesFromSheet(jumpSheet, 4, 5);
        if (playerJumpAllDirections != null) {
            playerJumpUp    = cropFramesFromSheet(jumpSheet, 1, 5, 0, 0); // Rând 0 (Spate)
            playerJumpLeft  = cropFramesFromSheet(jumpSheet, 1, 5, 1, 0); // Rând 1 (Stanga)
            playerJumpDown  = cropFramesFromSheet(jumpSheet, 1, 5, 2, 0); // Rând 2 (Fata)
            playerJumpRight = cropFramesFromSheet(jumpSheet, 1, 5, 3, 0); // Rând 3 (Dreapta)
        } else {
            System.err.println("DEBUG ASSETS: playerJumpAllDirections a esuat la decupare.");
        }


        System.out.println("DEBUG ASSETS: Incerc decupare climbSheet...");
        // CLIMB (climb.png: 384x64 pixeli -> 1 rând, 6 coloane de 64x64)
        playerClimb = cropFramesFromSheet(climbSheet, 1, 6);
        if (playerClimb == null) System.err.println("DEBUG ASSETS: playerClimb a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare hurtSheet...");
        // HURT (hurt.png: 384x64 pixeli -> 1 rând, 6 coloane de 64x64)
        playerHurt = cropFramesFromSheet(hurtSheet, 1, 6);
        if (playerHurt == null) System.err.println("DEBUG ASSETS: playerHurt a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare sitSheet...");
        // SIT (sit.png: 192x256 pixeli -> 4 rânduri, 3 coloane de 64x64)
        playerSit = cropFramesFromSheet(sitSheet, 4, 3);
        if (playerSit == null) System.err.println("DEBUG ASSETS: playerSit a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare emoteSheet...");
        // EMOTE (emote.png: 192x256 pixeli -> 4 rânduri, 3 coloane de 64x64)
        playerEmote = cropFramesFromSheet(emoteSheet, 4, 3);
        if (playerEmote == null) System.err.println("DEBUG ASSETS: playerEmote a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare thrustSheet...");
        // THRUST (thrust.png: 512x256 pixeli -> 4 rânduri, 8 coloane de 64x64)
        playerThrust = cropFramesFromSheet(thrustSheet, 4, 8);
        if (playerThrust == null) System.err.println("DEBUG ASSETS: playerThrust a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare halfslashSheet...");
        // HALFSLASH (halfslash.png: 448x256 pixeli -> 4 rânduri, 7 coloane de 64x64)
        playerHalfslash = cropFramesFromSheet(halfslashSheet, 4, 7);
        if (playerHalfslash == null) System.err.println("DEBUG ASSETS: playerHalfslash a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare backslashSheet...");
        // BACKSLASH (backslash.png: 832x256 pixeli -> 4 rânduri, 13 coloane de 64x64)
        playerBackslash = cropFramesFromSheet(backslashSheet, 4, 13);
        if (playerBackslash == null) System.err.println("DEBUG ASSETS: playerBackslash a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare spellcastSheet...");
        // SPELLCAST (spellcast.png: 448x256 pixeli -> 4 rânduri, 7 coloane de 64x64)
        playerSpellcast = cropFramesFromSheet(spellcastSheet, 4, 7);
        if (playerSpellcast == null) System.err.println("DEBUG ASSETS: playerSpellcast a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare shootSheet...");
        // SHOOT (shoot.png: 832x256 pixeli -> 4 rânduri, 13 coloane de 64x64)
        playerShoot = cropFramesFromSheet(shootSheet, 4, 13);
        if (playerShoot == null) System.err.println("DEBUG ASSETS: playerShoot a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare combatIdleSheet...");
        // COMBAT IDLE (combat_idle.png: 128x256 pixeli -> 4 rânduri, 2 coloane de 64x64)
        playerCombatIdle = cropFramesFromSheet(combatIdleSheet, 4, 2);
        if (playerCombatIdle == null) System.err.println("DEBUG ASSETS: playerCombatIdle a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare slashSheet...");
        // SLASH (slash.png: 384x256 pixeli -> 4 rânduri, 6 coloane de 64x64)
        playerSlash = cropFramesFromSheet(slashSheet, 4, 6);
        if (playerSlash == null) System.err.println("DEBUG ASSETS: playerSlash a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Toate incercarile de decupare au fost executate.");
    }

    /*! \fn private static BufferedImage[] cropFramesFromSheet(BufferedImage sheet, int rows, int cols)
        \brief Decupeaza toate cadrele dintr-un sprite sheet individual cu o structura de grila.
        \param sheet Imaginea sprite sheet-ului.
        \param rows Numarul de randuri de cadre din sheet.
        \param cols Numarul de coloane de cadre din sheet.
        \return Un tablou de BufferedImage-uri cu toate cadrele decupate. Returneaza null daca sheet e null sau daca apare o eroare de decupare.
     */
    private static BufferedImage[] cropFramesFromSheet(BufferedImage sheet, int rows, int cols) {
        if (sheet == null) {
            return null;
        }
        BufferedImage[] frames = new BufferedImage[rows * cols];
        int frameIndex = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                try {
                    int x = c * PLAYER_FRAME_WIDTH;
                    int y = r * PLAYER_FRAME_HEIGHT;
                    if (x < 0 || y < 0 || x + PLAYER_FRAME_WIDTH > sheet.getWidth() || y + PLAYER_FRAME_HEIGHT > sheet.getHeight()) {
                        System.err.println("ATENTIE (cropFramesFromSheet): Cadrul (" + r + "," + c + ") depaseste limitele sheet-ului " + sheet.toString() + " (Dim: " + sheet.getWidth() + "x" + sheet.getHeight() + ", Incercat: x=" + x + ", y=" + y + ", w=" + PLAYER_FRAME_WIDTH + ", h=" + PLAYER_FRAME_HEIGHT + ").");
                        return null;
                    }
                    frames[frameIndex++] = sheet.getSubimage(x, y, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT);
                } catch (Exception e) {
                    System.err.println("Eroare (cropFramesFromSheet) la decuparea cadrului (" + r + "," + c + ") din sheet " + sheet.toString() + ". Mesaj: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return frames;
    }

    /*! \fn private static BufferedImage[] cropFramesFromSheet(BufferedImage sheet, int numRowsToCrop, int numColsToCrop, int startRow, int startCol)
        \brief Decupeaza o sub-sectiune de cadre dintr-un sprite sheet, specificand randul si coloana de start.
        Utila pentru a extrage o animatie specifica dintr-un sheet mai mare (cum ar fi run.png care contine mai multe animatii de mers).
        \param sheet Imaginea sprite sheet-ului.
        \param numRowsToCrop Numarul de randuri de cadre de decupat (de obicei 1 pentru o singura animatie).
        \param numColsToCrop Numarul de coloane de cadre de decupat (lungimea animatiei).
        \param startRow Randul de start al animatiei (incepand de la 0).
        \param startCol Coloana de start a animatiei (incepand de la 0).
        \return Un tablou de BufferedImage-uri cu cadrele decupate. Returneaza null daca sheet e null sau daca apare o eroare de decupare.
     */
    private static BufferedImage[] cropFramesFromSheet(BufferedImage sheet, int numRowsToCrop, int numColsToCrop, int startRow, int startCol) {
        if (sheet == null) {
            return null;
        }
        BufferedImage[] frames = new BufferedImage[numRowsToCrop * numColsToCrop];
        int frameIndex = 0;
        for (int r = 0; r < numRowsToCrop; r++) {
            for (int c = 0; c < numColsToCrop; c++) {
                int actualCol = startCol + c;
                int actualRow = startRow + r;
                try {
                    int x = actualCol * PLAYER_FRAME_WIDTH;
                    int y = actualRow * PLAYER_FRAME_HEIGHT;
                    if (x < 0 || y < 0 || x + PLAYER_FRAME_WIDTH > sheet.getWidth() || y + PLAYER_FRAME_HEIGHT > sheet.getHeight()) {
                        System.err.println("ATENTIE (cropFramesFromSheet sub-sectiune): Cadrul (" + actualRow + "," + actualCol + ") depaseste limitele sheet-ului " + sheet.toString() + ". (Dim: " + sheet.getWidth() + "x" + sheet.getHeight() + ", Incercat: x=" + x + ", y=" + y + ", w=" + PLAYER_FRAME_WIDTH + ", h=" + PLAYER_FRAME_HEIGHT + ").");
                        return null;
                    }
                    frames[frameIndex++] = sheet.getSubimage(x, y, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT);
                } catch (Exception e) {
                    System.err.println("Eroare (cropFramesFromSheet sub-sectiune) la decuparea cadrului (" + actualRow + "," + actualCol + ") din sheet " + sheet.toString() + ". Mesaj: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return frames;
    }


    /*! \fn public static BufferedImage getTileImageByGID(int gid)
        \brief Returneaza imaginea unei dale pe baza GID-ului din Tiled.
        Va fi apelata doar DUPA ce Assets.LoadGameAssets() a fost chemata.
     */
    public static BufferedImage getTileImageByGID(int gid) {
        if (gid == 0 || gameSpriteSheet == null || gameSpriteSheet.getSpriteSheet() == null) {
            return null;
        }
        int tileWidth = SpriteSheet.getTileWidth();
        int tileHeight = SpriteSheet.getTileHeight();

        if (tileWidth == 0 || jungleTilesetImage == null) {
            System.err.println("Eroare: Latimea dalei (tileWidth) este 0 sau tileset-ul este null. Verificati SpriteSheet.java si incarcarea tileset-ului.");
            return null;
        }
        int columns = jungleTilesetImage.getWidth() / tileWidth;

        int index = gid - 1;

        int tileX = (index % columns) * tileWidth;
        int tileY = (index / columns) * tileHeight;

        try {
            if (tileX < 0 || tileY < 0 || tileX + tileWidth > jungleTilesetImage.getWidth() ||
                    tileY + tileHeight > jungleTilesetImage.getHeight()) {
                System.err.println("ATENTIE: Decuparea dalei cu GID " + gid + " depaseste limitele imaginii tileset. " +
                        "Verificati coordonatele (x=" + tileX + ", y=" + tileY + ", w=" + tileWidth + ", h=" + tileHeight + ")" +
                        " pe o imagine de " + jungleTilesetImage.getWidth() + "x" + jungleTilesetImage.getHeight() + ".");
                return null;
            }
            return gameSpriteSheet.getSpriteSheet().getSubimage(tileX, tileY, tileWidth, tileHeight);
        } catch (Exception e) {
            System.err.println("Eroare la extragerea dalei cu GID " + gid + " la X: " + tileX + ", Y: " + tileY + ". Mesaj: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}