package PaooGame.Graphics;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import PaooGame.Tiles.Tile;
import java.util.ArrayList;

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

    public static final int NPC_FRAME_WIDTH = 61;
    public static final int NPC_FRAME_HEIGHT = 62;

    public static final int MONKEY_FRAME_WIDTH = 36;
    public static final int MONKEY_FRAME_HEIGHT = 52;
    public static final int JAGUAR_FRAME_WIDTH = 76;
    public static final int JAGUAR_FRAME_HEIGHT = 41;
    public static final int BAT_FRAME_WIDTH = 19;
    public static final int BAT_FRAME_HEIGHT = 20;

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

    public static BufferedImage[] agentIdleDown;
    public static BufferedImage[] agentWalk;

    public static BufferedImage[] monkeyWalkAnim;
    public static BufferedImage[] jaguarWalkAnim;
    public static BufferedImage[] jaguarRunAnim;
    public static BufferedImage[] batAnim;

    public static BufferedImage[] npcIdleAnim;

    public static BufferedImage spikeTrapImage;
    public static BufferedImage[] smallTrapAnim;

    public static BufferedImage keyImage;
    public static BufferedImage talismanImage;

    // ## MODIFICARE ##: Am adaugat referinta pentru imaginea mesei de puzzle
    public static BufferedImage puzzleTableImage;

    // --- Asset-uri noi pentru puzzle-uri ---
    public static BufferedImage puzzle1Sun;
    public static BufferedImage puzzle1Moon;
    public static BufferedImage puzzle1Star;
    public static BufferedImage puzzle1Bolt;
    public static BufferedImage puzzle2Gems;
    public static BufferedImage puzzle3Scroll;

    public static BufferedImage[] puzzle5CardFaces;
    public static BufferedImage puzzle5CardBack;




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

        System.out.println("DEBUG ASSETS: Incarc imagini pentru puzzle-uri...");
        puzzle1Sun = ImageLoader.LoadImage("/textures/puzzles/sun.png");
        puzzle1Moon = ImageLoader.LoadImage("/textures/puzzles/moon.png");
        puzzle1Star = ImageLoader.LoadImage("/textures/puzzles/star.png");
        puzzle1Bolt = ImageLoader.LoadImage("/textures/puzzles/bolt.png");

        puzzle2Gems = ImageLoader.LoadImage("/textures/puzzles/crystals.png");

        puzzle3Scroll = ImageLoader.LoadImage("/textures/puzzles/ancient_scroll.png");

        BufferedImage cardFacesSheet = ImageLoader.LoadImage("/textures/puzzles/card_faces.png");

        if (cardFacesSheet != null) {
            puzzle5CardFaces = new BufferedImage[8];
            int CARD_WIDTH = 62;
            int CARD_HEIGHT = 86;
            puzzle5CardBack = cardFacesSheet.getSubimage(0, 0, CARD_WIDTH, CARD_HEIGHT);
            for (int i = 0; i < 8; i++) {
                puzzle5CardFaces[i] = cardFacesSheet.getSubimage((i+1) * CARD_WIDTH, 0, CARD_WIDTH, CARD_HEIGHT);
            }
        }

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

        BufferedImage agentIdleSheet = ImageLoader.LoadImage("/textures/agent/idle.png");
        BufferedImage agentWalkSheet = ImageLoader.LoadImage("/textures/agent/walk.png");

        BufferedImage monkeySheet = ImageLoader.LoadImage("/textures/animals/monkey.png");
        BufferedImage jaguarSheet = ImageLoader.LoadImage("/textures/animals/jaguar.png");
        BufferedImage batSheet = ImageLoader.LoadImage("/textures/animals/bat.png");

        BufferedImage spikesSheet = ImageLoader.LoadImage("/textures/traps/spikes.png");
        BufferedImage trapSheet = ImageLoader.LoadImage("/textures/traps/trap.png");

        BufferedImage npcIdleSheet = ImageLoader.LoadImage("/textures/old_man.png");
        BufferedImage talismanLoadedImage = ImageLoader.LoadImage("/textures/talisman.png");

        keyImage = ImageLoader.LoadImage("/textures/objects/key.png");


        // ## MODIFICARE ##: Am adaugat incarcarea efectiva a imaginii mesei de puzzle
        puzzleTableImage = ImageLoader.LoadImage("/textures/objects/table.png");
        if (puzzleTableImage == null) {
            System.err.println("Eroare: Nu s-a putut incarca imaginea pentru masa de puzzle (table.png)!");
        }

        if (keyImage == null) {
            System.err.println("Eroare: Nu s-a putut incarca key.png! Verificati calea. Se va folosi placeholder.");
            keyImage = new BufferedImage(Tile.TILE_WIDTH, Tile.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics gKey = keyImage.getGraphics();
            gKey.setColor(Color.YELLOW);
            gKey.fillRect(0, 0, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
            gKey.dispose();
        }

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
        // ## MODIFICĂ ACEST BLOC ##
        if (playerIdleAllDirections != null && playerIdleAllDirections.length >= 8) {
            playerIdleUp    = new BufferedImage[]{playerIdleAllDirections[0], playerIdleAllDirections[1]};
            playerIdleLeft  = new BufferedImage[]{playerIdleAllDirections[2], playerIdleAllDirections[3]};
            playerIdleDown = new BufferedImage[]{
                    playerIdleAllDirections[4], // Cadru 1 (afisat de 4 ori)
                    playerIdleAllDirections[4],
                    playerIdleAllDirections[4],
                    playerIdleAllDirections[4],
                    playerIdleAllDirections[5]  // Cadru 2 (afisat o data)
            };
            playerIdleRight = new BufferedImage[]{playerIdleAllDirections[6], playerIdleAllDirections[7]};
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

        // ## ADAUGĂ ACEST BLOC ##
        if (agentIdleSheet != null) {
            agentIdleDown = cropFramesFromSheet(agentIdleSheet, 1, 2, 2, 0);
        } else {
            System.err.println("EROARE: Nu s-a putut incarca /textures/agent/idle.png");
        }
        if (agentWalkSheet != null) {
            agentWalk = cropFramesFromSheet(agentWalkSheet, 1, 9, 2, 0);
        } else {
            System.err.println("EROARE: Nu s-a putut incarca /textures/agent/walk.png");
        }

        if (npcIdleSheet != null) {
            npcIdleAnim = cropFramesFromArbitrarySheet(npcIdleSheet, NPC_FRAME_WIDTH, NPC_FRAME_HEIGHT, 1, 6, 0, 0);
            if (npcIdleAnim == null) System.err.println("DEBUG ASSETS: Decupare npcIdleAnim a esuat.");
        } else { System.err.println("DEBUG ASSETS: Nu s-a putut incarca old_man.png.");
        }

        System.out.println("DEBUG ASSETS: Toate incercarile de decupare a playerului au fost executate.");
        System.out.println("DEBUG ASSETS: Incerc incarcare/decupare animale (fisiere separate)...");
        if (monkeySheet != null) {
            Rectangle[] monkeyFramesData = {
                    new Rectangle(0, 0, 36, 52), new Rectangle(36, 0, 36, 52),
                    new Rectangle(72, 0, 28, 52), new Rectangle(100, 0, 32, 52),
                    new Rectangle(132, 0, 28, 52), new Rectangle(160, 0, 34, 52),
                    new Rectangle(194, 0, 39, 52), new Rectangle(233, 0, 41, 52),
                    new Rectangle(274, 0, 29, 52), new Rectangle(303, 0, 36, 52),
                    new Rectangle(339, 0, 38, 52), new Rectangle(377, 0, 37, 52),
                    new Rectangle(414, 0, 36, 52)
            };
            monkeyWalkAnim = cropFramesFromVariableRectangles(monkeySheet, monkeyFramesData);
            if (monkeyWalkAnim == null) System.err.println("DEBUG ASSETS: Decupare monkeyWalkAnim a esuat.");
        } else { System.err.println("DEBUG ASSETS: Nu s-a putut incarca monkey.png.");
        }

        if (jaguarSheet != null) {
            Rectangle[] jaguarFramesData = {
                    new Rectangle(0, 0, 76, 41), new Rectangle(76, 0, 73, 41),
                    new Rectangle(149, 0, 75, 41), new Rectangle(224, 0, 71, 41),
                    new Rectangle(295, 0, 70, 41), new Rectangle(365, 0, 66, 41),
                    new Rectangle(431, 0, 63, 41), new Rectangle(494, 0, 75, 41)
            };
            jaguarWalkAnim = cropFramesFromVariableRectangles(jaguarSheet, jaguarFramesData);
            if (jaguarWalkAnim == null) System.err.println("DEBUG ASSETS: Decupare jaguarWalkAnim a esuat.");
        } else { System.err.println("DEBUG ASSETS: Nu s-a putut incarca jaguar.png.");
        }

        if (batSheet != null) {
            Rectangle[] batFramesData = {
                    new Rectangle(0, 0, 18, 20), new Rectangle(18, 0, 21, 20),
                    new Rectangle(39, 0, 17, 20), new Rectangle(56, 0, 22, 20)
            };
            batAnim = cropFramesFromVariableRectangles(batSheet, batFramesData);
            if (batAnim == null) System.err.println("DEBUG ASSETS: Decupare batAnim a esuat.");
        } else { System.err.println("DEBUG ASSETS: Nu s-a putut incarca bat.png.");
        }


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
                    new Rectangle(0, 0, 39, 25), new Rectangle(39, 0, 45, 25), new Rectangle(84, 0, 51, 25)
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

    private static BufferedImage[] cropFramesFromSheet(BufferedImage sheet, int rows, int cols) {
        return cropFramesFromArbitrarySheet(sheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, rows, cols, 0, 0);
    }

    private static BufferedImage[] cropFramesFromSheet(BufferedImage sheet, int numRowsToCrop, int numColsToCrop, int startRow, int startCol) {
        return cropFramesFromArbitrarySheet(sheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, numRowsToCrop, numColsToCrop, startRow, startCol);
    }

    private static BufferedImage[] cropFramesFromArbitrarySheet(BufferedImage sheet, int frameWidth, int frameHeight, int numRowsToCrop, int numColsToCrop, int startRow, int startCol) {
        if (sheet == null) {
            return null;
        }
        ArrayList<BufferedImage> frames = new ArrayList<>();
        for (int r = 0; r < numRowsToCrop; r++) {
            for (int c = 0; c < numColsToCrop; c++) {
                int x = (startCol + c) * frameWidth;
                int y = (startRow + r) * frameHeight;
                try {
                    if (x < 0 || y < 0 || x + frameWidth > sheet.getWidth() || y + frameHeight > sheet.getHeight()) {
                        System.err.println("ATENTIE (cropFramesFromArbitrarySheet - fixed grid): Cadrul (" + (startRow+r) + "," + (startCol+c) + ") depaseste limitele sheet-ului.");
                        continue;
                    }
                    frames.add(sheet.getSubimage(x, y, frameWidth, frameHeight));
                } catch (Exception e) {
                    System.err.println("Eroare (cropFramesFromArbitrarySheet - fixed grid) la decuparea cadrului (" + (startRow+r) + "," + (startCol+c) + ")");
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return frames.toArray(new BufferedImage[0]);
    }

    private static BufferedImage[] cropFramesFromVariableRectangles(BufferedImage sheet, Rectangle[] framesData) {
        if (sheet == null || framesData == null || framesData.length == 0) {
            return null;
        }
        BufferedImage[] frames = new BufferedImage[framesData.length];
        for (int i = 0; i < framesData.length; i++) {
            Rectangle rect = framesData[i];
            try {
                if (rect.x < 0 || rect.y < 0 || rect.x + rect.width > sheet.getWidth() || rect.y + rect.height > sheet.getHeight()) {
                    System.err.println("ATENTIE (cropFramesFromVariableRectangles): Cadrul " + i + " depaseste limitele sheet-ului.");
                    return null;
                }
                frames[i] = sheet.getSubimage(rect.x, rect.y, rect.width, rect.height);
            } catch (Exception e) {
                System.err.println("Eroare (cropFramesFromVariableRectangles) la decuparea cadrului " + i);
                e.printStackTrace();
                return null;
            }
        }
        return frames;
    }

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
                System.err.println("ATENTIE: Decuparea dalei cu GID " + gid + " depaseste limitele imaginii tileset.");
                return null;
            }
            return tilesetImage.getSubimage(tileX, tileY, tileWidth, tileHeight);
        } catch (Exception e) {
            System.err.println("Eroare la extragerea dalei cu GID " + gid + " din tileset la X: " + tileX + ", Y: " + tileY);
            e.printStackTrace();
            return null;
        }
    }
}