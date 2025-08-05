package PaooGame.Graphics;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import PaooGame.Tiles.Tile;

/*!
 * \class public class Assets
 * \brief Clasa incarca fiecare element grafic necesar jocului.
 */
public class Assets
{
    public static SpriteSheet gameSpriteSheet;
    public static BufferedImage jungleTilesetImage;
    public static BufferedImage level2TilesetImage;
    public static BufferedImage level3TilesetImage;


    public static BufferedImage gameLogo;
    public static BufferedImage backgroundMenu;

    public static final int PLAYER_FRAME_WIDTH = 64;
    public static final int PLAYER_FRAME_HEIGHT = 64;

    // NOU: Dimensiunile cadrelor pentru NPC (Paznic)
    public static final int NPC_FRAME_WIDTH = 61;
    public static final int NPC_FRAME_HEIGHT = 62;

    // Dimensiunile cadrelor pentru animale
    public static final int MONKEY_FRAME_WIDTH = 36;
    public static final int MONKEY_FRAME_HEIGHT = 52;
    public static final int JAGUAR_FRAME_WIDTH = 76;
    public static final int JAGUAR_FRAME_HEIGHT = 41;
    public static final int BAT_FRAME_WIDTH = 19;
    public static final int BAT_FRAME_HEIGHT = 20;


    // Tablouri de cadre pentru animațiile jucătorului
    public static BufferedImage[] playerDown;
    public static BufferedImage[] playerUp;
    public static BufferedImage[] playerLeft;
    public static BufferedImage[] playerRight;

    public static BufferedImage[] playerIdleAllDirections;
    public static BufferedImage[] playerIdleDown;
    public static BufferedImage[] playerIdleUp;
    public static BufferedImage[] playerIdleLeft;
    public static BufferedImage[] playerIdleRight;

    public static BufferedImage[] playerRunAllDirections;
    public static BufferedImage[] playerRunDown;
    public static BufferedImage[] playerRunUp;
    public static BufferedImage[] playerRunLeft;
    public static BufferedImage[] playerRunRight;

    public static BufferedImage[] playerJumpAllDirections;
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


    // Imagini/animatii pentru animale
    public static BufferedImage[] monkeyWalkAnim;
    public static BufferedImage[] jaguarWalkAnim;
    public static BufferedImage[] jaguarRunAnim;
    public static BufferedImage[] batAnim;

    // NOU: Animație pentru NPC
    public static BufferedImage[] npcIdleAnim;

    // Imagini pentru capcane
    public static BufferedImage spikeTrapImage;
    public static BufferedImage[] smallTrapAnim;

    // Imaginea cheii
    public static BufferedImage keyImage;
    // Imaginea talismanului
    public static BufferedImage talismanImage;


    /*!
     * \fn public static void Init()
     \brief Functia initializaza referintele catre elementele grafice utilizate (asset-urile rapide).
     */
    public static void Init()
    {
        gameLogo = ImageLoader.LoadImage("/textures/logo.png");
        if (gameLogo == null) {
            System.err.println("Eroare: Nu s-a putut incarca logo.png! Verificati calea si numele fisierului.");
        }
    }

    /*!
     * \fn public static void LoadGameAssets()
     \brief Incarca asset-urile mari ale jocului (tileset, dale specifice, player) care dureaza mai mult.
     * Aceasta metoda va fi apelata din LoadingScreenState.
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


        level2TilesetImage = ImageLoader.LoadImage("/textures/tileset_level2.png");
        if (level2TilesetImage == null) {
            System.err.println("Eroare: Nu s-a putut incarca tileset_level2.png! Verificati calea si numele fisierului.");
        }

        level3TilesetImage = ImageLoader.LoadImage("/textures/level_3.png");
        if (level3TilesetImage == null) {
            System.err.println("Eroare: Nu s-a putut incarca level_3.png! Verificati calea si numele fisierului.");
        }

        // --- INCARCAREA CADRELOR DE ANIMAȚIE DIN FIȘIERE INDIVIDUALE ---
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

        // NOU: Incarca spritesheet-urile SEPARATE pentru animale
        BufferedImage monkeySheet = ImageLoader.LoadImage("/textures/animals/monkey.png");
        BufferedImage jaguarSheet = ImageLoader.LoadImage("/textures/animals/jaguar.png");
        BufferedImage batSheet = ImageLoader.LoadImage("/textures/animals/bat.png");

        // NOU: Incarca imaginile capcanelor
        BufferedImage spikesSheet = ImageLoader.LoadImage("/textures/traps/spikes.png");
        BufferedImage trapSheet = ImageLoader.LoadImage("/textures/traps/trap.png");

        // NOU: Incarca spritesheet-ul pentru NPC
        BufferedImage npcIdleSheet = ImageLoader.LoadImage("/textures/old_man.png");
        BufferedImage talismanLoadedImage = ImageLoader.LoadImage("/textures/talisman.png");

        // NOU: Incarca imaginea cheii
        keyImage = ImageLoader.LoadImage("/textures/objects/key.png");
        if (keyImage == null) {
            System.err.println("Eroare: Nu s-a putut incarca key.png! Verificati calea. Se va folosi placeholder.");
            keyImage = new BufferedImage(Tile.TILE_WIDTH, Tile.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics gKey = keyImage.getGraphics();
            gKey.setColor(Color.YELLOW);
            gKey.fillRect(0, 0, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
            gKey.dispose();
        }

        // NOU: Incarca imaginea talismanului
        if (talismanLoadedImage != null) {
            talismanImage = talismanLoadedImage;
        } else {
            System.err.println("Eroare: Nu s-a putut incarca talisman.png! Se va folosi placeholder.");
            talismanImage = new BufferedImage(Tile.TILE_WIDTH, Tile.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics gTal = talismanImage.getGraphics();
            gTal.setColor(Color.GREEN);
            gTal.fillRect(0, 0, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
            gTal.setColor(Color.YELLOW);
            gTal.fillOval(4, 4, Tile.TILE_WIDTH - 8, Tile.TILE_HEIGHT - 8);
            gTal.dispose();
        }


        // --- DE CUPAREA CADRELOR DE ANIMAȚIE DIN FIECARE SHEET INDIVIDUAL ---

        System.out.println("DEBUG ASSETS: Incerc decupare player sheets...");
        playerUp    = cropFramesFromSheet(walkSheet, 1, 9, 0, 0);
        playerLeft  = cropFramesFromSheet(walkSheet, 1, 9, 1, 0);
        playerDown  = cropFramesFromSheet(walkSheet, 1, 9, 2, 0);
        playerRight = cropFramesFromSheet(walkSheet, 1, 9, 3, 0);
        if (playerUp == null) System.err.println("DEBUG ASSETS: playerUp a esuat la decupare.");
        if (playerLeft == null) System.err.println("DEBUG ASSETS: playerLeft a esuat la decupare.");
        if (playerDown == null) System.err.println("DEBUG ASSETS: playerDown a esuat la decupare.");
        if (playerRight == null) System.err.println("DEBUG ASSETS: playerRight a esuat la decupare.");


        playerIdleAllDirections = cropFramesFromSheet(idleSheet, 4, 2);
        if (playerIdleAllDirections != null && playerIdleAllDirections.length >= 8) {
            playerIdleUp    = new BufferedImage[]{playerIdleAllDirections[0]};
            playerIdleLeft  = new BufferedImage[]{playerIdleAllDirections[2]};
            playerIdleDown  = new BufferedImage[]{playerIdleAllDirections[4]};
            playerIdleRight = new BufferedImage[]{playerIdleAllDirections[6]};
        } else {
            System.err.println("DEBUG ASSETS: playerIdleAllDirections a esuat la decupare sau este incomplet.");
            playerIdleUp = playerUp != null && playerUp.length > 0 ? new BufferedImage[]{playerUp[0]} : null;
            playerIdleLeft = playerLeft != null && playerLeft.length > 0 ? new BufferedImage[]{playerLeft[0]} : null;
            playerIdleDown = playerDown != null && playerDown.length > 0 ? new BufferedImage[]{playerDown[0]} : null;
            playerIdleRight = playerRight != null && playerRight.length > 0 ? new BufferedImage[]{playerRight[0]} : null;
        }


        playerRunAllDirections = cropFramesFromSheet(runSheet, 4, 8);
        if (playerRunAllDirections != null) {
            playerRunUp    = cropFramesFromSheet(runSheet, 1, 8, 0, 0);
            playerRunLeft  = cropFramesFromSheet(runSheet, 1, 8, 1, 0);
            playerRunDown  = cropFramesFromSheet(runSheet, 1, 8, 2, 0);
            playerRunRight = cropFramesFromSheet(runSheet, 1, 8, 3, 0);
        } else {
            System.err.println("DEBUG ASSETS: playerRunAllDirections a esuat la decupare.");
        }


        playerJumpAllDirections = cropFramesFromSheet(jumpSheet, 4, 5);
        if (playerJumpAllDirections != null) {
            playerJumpUp    = cropFramesFromSheet(jumpSheet, 1, 5, 0, 0);
            playerJumpLeft  = cropFramesFromSheet(jumpSheet, 1, 5, 1, 0);
            playerJumpDown  = cropFramesFromSheet(jumpSheet, 1, 5, 2, 0);
            playerJumpRight = cropFramesFromSheet(jumpSheet, 1, 5, 3, 0);
        } else {
            System.err.println("DEBUG ASSETS: playerJumpAllDirections a esuat la decupare.");
        }


        System.out.println("DEBUG ASSETS: Incerc decupare climbSheet...");
        playerClimb = cropFramesFromSheet(climbSheet, 1, 6);
        if (playerClimb == null) System.err.println("DEBUG ASSETS: playerClimb a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare hurtSheet...");
        playerHurt = cropFramesFromSheet(hurtSheet, 1, 6);
        if (playerHurt == null) System.err.println("DEBUG ASSETS: playerHurt a esuat la decupare.");
        System.out.println("DEBUG ASSETS: Incerc decupare sitSheet...");
        playerSit = cropFramesFromSheet(sitSheet, 4, 3);
        if (playerSit == null) System.err.println("DEBUG ASSETS: playerSit a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare emoteSheet...");
        playerEmote = cropFramesFromSheet(emoteSheet, 4, 3);
        if (playerEmote == null) System.err.println("DEBUG ASSETS: playerEmote a esuat la decupare.");
        System.out.println("DEBUG ASSETS: Incerc decupare thrustSheet...");
        playerThrust = cropFramesFromSheet(thrustSheet, 4, 8);
        if (playerThrust == null) System.err.println("DEBUG ASSETS: playerThrust a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare halfslashSheet...");
        playerHalfslash = cropFramesFromSheet(halfslashSheet, 4, 7);
        if (playerHalfslash == null) System.err.println("DEBUG ASSETS: playerHalfslash a esuat la decupare.");
        System.out.println("DEBUG ASSETS: Incerc decupare backslashSheet...");
        playerBackslash = cropFramesFromSheet(backslashSheet, 4, 13);
        if (playerBackslash == null) System.err.println("DEBUG ASSETS: playerBackslash a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare spellcastSheet...");
        playerSpellcast = cropFramesFromSheet(spellcastSheet, 4, 7);
        if (playerSpellcast == null) System.err.println("DEBUG ASSETS: playerSpellcast a esuat la decupare.");
        System.out.println("DEBUG ASSETS: Incerc decupare shootSheet...");
        playerShoot = cropFramesFromSheet(shootSheet, 4, 13);
        if (playerShoot == null) System.err.println("DEBUG ASSETS: playerShoot a esuat la decupare.");

        System.out.println("DEBUG ASSETS: Incerc decupare combatIdleSheet...");
        playerCombatIdle = cropFramesFromSheet(combatIdleSheet, 4, 2);
        if (playerCombatIdle == null) System.err.println("DEBUG ASSETS: playerCombatIdle a esuat la decupare.");
        System.out.println("DEBUG ASSETS: Incerc decupare slashSheet...");
        playerSlash = cropFramesFromSheet(slashSheet, 4, 6);
        if (playerSlash == null) System.err.println("DEBUG ASSETS: playerSlash a esuat la decupare.");

        // NOU: Decupare cadre pentru NPC
        if (npcIdleSheet != null) {
            // Spritesheet-ul are 6 cadre pe 1 rand
            npcIdleAnim = cropFramesFromArbitrarySheet(npcIdleSheet, NPC_FRAME_WIDTH, NPC_FRAME_HEIGHT, 1, 6, 0, 0);
            if (npcIdleAnim == null) System.err.println("DEBUG ASSETS: Decupare npcIdleAnim a esuat.");
        } else { System.err.println("DEBUG ASSETS: Nu s-a putut incarca old_man.png."); }

        System.out.println("DEBUG ASSETS: Toate incercarile de decupare a playerului au fost executate.");

        // NOU: Decupare/Incarcare imagini pentru animale
        System.out.println("DEBUG ASSETS: Incerc incarcare/decupare animale (fisiere separate)...");

        if (monkeySheet != null) {
            Rectangle[] monkeyFramesData = {
                    new Rectangle(0, 0, 36, 52),
                    new Rectangle(36, 0, 36, 52),
                    new Rectangle(72, 0, 28, 52),
                    new Rectangle(100, 0, 32, 52),
                    new Rectangle(132, 0, 28, 52),
                    new Rectangle(160, 0, 34, 52),
                    new Rectangle(194, 0, 39, 52),
                    new Rectangle(233, 0, 41, 52),
                    new Rectangle(274, 0, 29, 52),
                    new Rectangle(303, 0, 36, 52),
                    new Rectangle(339, 0, 38, 52),
                    new Rectangle(377, 0, 37, 52),
                    new Rectangle(414, 0, 36, 52)
            };
            monkeyWalkAnim = cropFramesFromVariableRectangles(monkeySheet, monkeyFramesData);
            if (monkeyWalkAnim == null) System.err.println("DEBUG ASSETS: Decupare monkeyWalkAnim a esuat.");
        } else { System.err.println("DEBUG ASSETS: Nu s-a putut incarca monkey.png."); }

        if (jaguarSheet != null) {
            Rectangle[] jaguarFramesData = {
                    new Rectangle(0, 0, 76, 41),
                    new Rectangle(76, 0, 73, 41),
                    new Rectangle(149, 0, 75, 41),
                    new Rectangle(224, 0, 71, 41),
                    new Rectangle(295, 0, 70, 41),
                    new Rectangle(365, 0, 66, 41),
                    new Rectangle(431, 0, 63, 41),
                    new Rectangle(494, 0, 75, 41)
            };
            jaguarWalkAnim = cropFramesFromVariableRectangles(jaguarSheet, jaguarFramesData);
            if (jaguarWalkAnim == null) System.err.println("DEBUG ASSETS: Decupare jaguarWalkAnim a esuat.");

        } else { System.err.println("DEBUG ASSETS: Nu s-a putut incarca jaguar.png."); }

        if (batSheet != null) {
            Rectangle[] batFramesData = {
                    new Rectangle(0, 0, 18, 20),
                    new Rectangle(18, 0, 21, 20),
                    new Rectangle(39, 0, 17, 20),
                    new Rectangle(56, 0, 22, 20)
            };
            batAnim = cropFramesFromVariableRectangles(batSheet, batFramesData);
            if (batAnim == null) System.err.println("DEBUG ASSETS: Decupare batAnim a esuat.");
        } else { System.err.println("DEBUG ASSETS: Nu s-a putut incarca bat.png."); }


        // NOU: Incarcare imagini pentru capcane (din fisiere reale)
        System.out.println("DEBUG ASSETS: Incerc incarcare/decupare capcane (fisiere separate)...");

        if (spikesSheet != null) {
            spikeTrapImage = spikesSheet.getSubimage(0, 0, 39, 25);
            if (spikeTrapImage == null) System.err.println("DEBUG ASSETS: Decupare spikeTrapImage a esuat.");
        } else { System.err.println("DEBUG ASSETS: Nu s-a putut incarca spikes.png.");
            spikeTrapImage = new BufferedImage(Tile.TILE_WIDTH, Tile.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics gSpike = spikeTrapImage.getGraphics();
            gSpike.setColor(Color.RED);
            gSpike.fillRect(0, 0, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
            gSpike.dispose();
        }

        if (trapSheet != null) {
            Rectangle[] smallTrapFramesData = {
                    new Rectangle(0, 0, 39, 25),
                    new Rectangle(39, 0, 45, 25),
                    new Rectangle(84, 0, 51, 25)
            };
            smallTrapAnim = cropFramesFromVariableRectangles(trapSheet, smallTrapFramesData);
            if (smallTrapAnim == null) System.err.println("DEBUG ASSETS: Decupare smallTrapAnim a esuat.");
        } else { System.err.println("DEBUG ASSETS: Nu s-a putut incarca trap.png.");
            smallTrapAnim = new BufferedImage[]{new BufferedImage(Tile.TILE_WIDTH, Tile.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB)};
            Graphics gSmall = smallTrapAnim[0].getGraphics();
            gSmall.setColor(Color.ORANGE);
            gSmall.fillRect(0, 0, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
            gSmall.dispose();
        }


        System.out.println("DEBUG ASSETS: Toate incercarile de decupare au fost executate.");
    }

    /*! \fn private static BufferedImage[] cropFramesFromSheet(BufferedImage sheet, int rows, int cols)
     \brief Decupeaza toate cadrele dintr-un sprite sheet individual cu o structura de grila, folosind dimensiunile player-ului.
     * Utila pentru spritesheet-uri in care cadrele sunt de dimensiunea PLAYER_FRAME_WIDTH/HEIGHT.
     * \param sheet Imaginea sprite sheet-ului.
     * \param rows Numarul de randuri de cadre din sheet.
     * \param cols Numarul de coloane de cadre din sheet.
     * \return Un tablou de BufferedImage-uri cu toate cadrele decupate. Returneaza null daca sheet e null sau daca apare o eroare de decupare.
     */
    private static BufferedImage[] cropFramesFromSheet(BufferedImage sheet, int rows, int cols) {
        return cropFramesFromArbitrarySheet(sheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, rows, cols, 0, 0);
    }

    /*! \fn private static BufferedImage[] cropFramesFromSheet(BufferedImage sheet, int numRowsToCrop, int numColsToCrop, int startRow, int startCol)
     \brief Decupeaza o sub-sectiune de cadre dintr-un sprite sheet, specificand randul si coloana de start, folosind dimensiunile player-ului.
     * Utila pentru a extrage o animatie specifica dintr-un sheet mai mare (cum ar fi run.png care contine mai multe animatii de mers).
     * \param sheet Imaginea sprite sheet-ului.
     * \param numRowsToCrop Numarul de randuri de cadre de decupat (de obicei 1 pentru o singura animatie).
     * \param numColsToCrop Numarul de coloane de cadre de decupat (lungimea animatiei).
     * \param startRow Randul de start al animatiei (incepand de la 0).
     * \param startCol Coloana de start a animatiei (incepand de la 0).
     * \return Un tablou de BufferedImage-uri cu cadrele decupate. Returneaza null daca sheet e null sau daca apare o eroare de decupare.
     */
    private static BufferedImage[] cropFramesFromSheet(BufferedImage sheet, int numRowsToCrop, int numColsToCrop, int startRow, int startCol) {
        return cropFramesFromArbitrarySheet(sheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, numRowsToCrop, numColsToCrop, startRow, startCol);
    }

    /*! \fn private static BufferedImage[] cropFramesFromArbitrarySheet(BufferedImage sheet, int frameWidth, int frameHeight, int numRowsToCrop, int numColsToCrop, int startRow, int startCol)
     \brief Decupeaza cadre dintr-un sprite sheet cu dimensiuni arbitrare ale cadrelor, dar grid fix.
     * Aceasta este metoda generica pe care o vor folosi celelalte cropFramesFromSheet.
     * Este destinata spritesheet-urilor cu un grid uniform.
     * \param sheet Imaginea sprite sheet-ului.
     * \param frameWidth Latimea unui singur cadru.
     * \param frameHeight Inaltimea unui singur cadru.
     * \param numRowsToCrop Numarul de randuri de cadre de decupat.
     * \param numColsToCrop Numarul de coloane de cadre de decupat.
     * \param startRow Randul de start al animatiei (incepand de la 0).
     * \param startCol Coloana de start a animatiei (incepand de la 0).
     * \return Un tablou de BufferedImage-uri cu cadrele decupate. Returneaza null daca sheet e null sau daca apare o eroare de decupare.
     */
    private static BufferedImage[] cropFramesFromArbitrarySheet(BufferedImage sheet, int frameWidth, int frameHeight, int numRowsToCrop, int numColsToCrop, int startRow, int startCol) {
        if (sheet == null) {
            return null;
        }
        BufferedImage[] frames = new BufferedImage[numRowsToCrop * numColsToCrop];
        int frameIndex = 0;
        for (int r = 0; r < numRowsToCrop; r++) {
            for (int c = 0; c < numColsToCrop; c++) {
                int x = (startCol + c) * frameWidth;
                int y = (startRow + r) * frameHeight;
                try {
                    if (x < 0 || y < 0 || x + frameWidth > sheet.getWidth() || y + frameHeight > sheet.getHeight()) {
                        System.err.println("ATENTIE (cropFramesFromArbitrarySheet - fixed grid): Cadrul (" + (startRow+r) + "," + (startCol+c) + ") depaseste limitele sheet-ului " + sheet.toString() + ". (Dim: " + sheet.getWidth() + "x" + sheet.getHeight() + ", Incercat: x=" + x + ", y=" + y + ", w=" + frameWidth + ", h=" + frameHeight + ").");
                        return null;
                    }
                    frames[frameIndex++] = sheet.getSubimage(x, y, frameWidth, frameHeight);
                } catch (Exception e) {
                    System.err.println("Eroare (cropFramesFromArbitrarySheet - fixed grid) la decuparea cadrului (" + (startRow+r) + "," + (startCol+c) + ") din sheet " + sheet.toString() + ". Mesaj: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return frames;
    }

    /*! \fn private static BufferedImage[] cropFramesFromVariableRectangles(BufferedImage sheet, Rectangle[] framesData)
     \brief Decupeaza cadre dintr-un sprite sheet folosind o lista de dreptunghiuri de decupare precise.
     * Aceasta metoda este destinata spritesheet-urilor cu cadre de latimi si/sau inaltimi variabile,
     * unde fiecare cadru este definit de propriile sale coordonate si dimensiuni.
     * \param sheet Imaginea sprite sheet-ului.
     * \param framesData Un array de obiecte Rectangle, fiecare definind (x, y, width, height) pentru un cadru.
     * \return Un tablou de BufferedImage-uri cu cadrele decupate. Returneaza null daca sheet e null sau daca apare o eroare de decupare.
     */
    private static BufferedImage[] cropFramesFromVariableRectangles(BufferedImage sheet, Rectangle[] framesData) {
        if (sheet == null || framesData == null || framesData.length == 0) {
            return null;
        }
        BufferedImage[] frames = new BufferedImage[framesData.length];
        for (int i = 0; i < framesData.length; i++) {
            Rectangle rect = framesData[i];
            try {
                if (rect.x < 0 || rect.y < 0 || rect.x + rect.width > sheet.getWidth() || rect.y + rect.height > sheet.getHeight()) {
                    System.err.println("ATENTIE (cropFramesFromVariableRectangles): Cadrul " + i + " (" + rect.x + "," + rect.y + "," + rect.width + "," + rect.height + ") depaseste limitele sheet-ului " + sheet.toString() + ". (Dim: " + sheet.getWidth() + "x" + sheet.getHeight() + ").");
                    return null;
                }
                frames[i] = sheet.getSubimage(rect.x, rect.y, rect.width, rect.height);
            } catch (Exception e) {
                System.err.println("Eroare (cropFramesFromVariableRectangles) la decuparea cadrului " + i + " din sheet " + sheet.toString() + ". Mesaj: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        return frames;
    }


    /*! \fn public static BufferedImage getTileImageByGID(int gid, BufferedImage tilesetImage)
     \brief Returneaza imaginea unei dale pe baza GID-ului din Tiled, dintr-o imagine tileset specificata.
     * Aceasta metoda este apelata de Tile.Draw().
     * \param gid Global ID-ul dalei.
     * \param tilesetImage Imaginea tileset-ului din care sa se decupeze.
     * \return Imaginea dalei corespunzatoare, ou null daca nu poate fi decupata.
     */
    public static BufferedImage getTileImageByGID(int gid, BufferedImage tilesetImage) {
        if (gid == 0 || tilesetImage == null) {
            return null;
        }

        int tileWidth = SpriteSheet.getTileWidth();
        int tileHeight = SpriteSheet.getTileHeight();

        if (tileWidth == 0) {
            System.err.println("Eroare: Latimea dalei (tileWidth) este 0. Verificati SpriteSheet.java.");
            return null;
        }
        int columns = tilesetImage.getWidth() / tileWidth;

        int index = gid - 1;

        int tileX = (index % columns) * tileWidth;
        int tileY = (index / columns) * tileHeight;

        try {
            if (tileX < 0 || tileY < 0 || tileX + tileWidth > tilesetImage.getWidth() ||
                    tileY + tileHeight > tilesetImage.getHeight()) {
                System.err.println("ATENTIE: Decuparea dalei cu GID " + gid + " depaseste limitele imaginii tileset " + tilesetImage.toString() + ". " +
                        "Verificati coordonatele (x=" + tileX + ", y=" + tileY + ", w=" + tileWidth + ", h=" + tileHeight + ").");
                return null;
            }
            return tilesetImage.getSubimage(tileX, tileY, tileWidth, tileHeight);
        } catch (Exception e) {
            System.err.println("Eroare la extragerea dalei cu GID " + gid + " din tileset " + tilesetImage.toString() + " la X: " + tileX + ", Y: " + tileY + ". Mesaj: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}