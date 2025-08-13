package PaooGame.Graphics;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import PaooGame.Tiles.Tile;
import java.util.ArrayList;

public class Assets {
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

    // PLAYER ANIMATIONS
    public static BufferedImage[] playerDown;
    public static BufferedImage[] playerUp;
    public static BufferedImage[] playerLeft;
    public static BufferedImage[] playerRight;
    public static BufferedImage[] playerIdleDown;
    public static BufferedImage[] playerIdleUp;
    public static BufferedImage[] playerIdleLeft;
    public static BufferedImage[] playerIdleRight;
    public static BufferedImage[] playerRunDown;
    public static BufferedImage[] playerRunUp;
    public static BufferedImage[] playerRunLeft;
    public static BufferedImage[] playerRunRight;
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

    // AGENT ANIMATIONS
    public static BufferedImage[] agentDown;
    public static BufferedImage[] agentUp;
    public static BufferedImage[] agentLeft;
    public static BufferedImage[] agentRight;
    public static BufferedImage[] agentIdleDown;
    public static BufferedImage[] agentIdleUp;
    public static BufferedImage[] agentIdleLeft;
    public static BufferedImage[] agentIdleRight;
    public static BufferedImage[] agentRunDown;
    public static BufferedImage[] agentRunUp;
    public static BufferedImage[] agentRunLeft;
    public static BufferedImage[] agentRunRight;
    public static BufferedImage[] agentJumpDown;
    public static BufferedImage[] agentJumpUp;
    public static BufferedImage[] agentJumpLeft;
    public static BufferedImage[] agentJumpRight;
    public static BufferedImage[] agentClimb;
    public static BufferedImage[] agentHurt;
    public static BufferedImage[] agentShoot;
    public static BufferedImage[] agentBackslash;
    public static BufferedImage[] agentHalfslash;
    public static BufferedImage[] agentEmote;
    public static BufferedImage[] agentCombatIdle;
    public static BufferedImage[] agentThrust;
    public static BufferedImage[] agentSit;
    public static BufferedImage[] agentSpellcast;
    public static BufferedImage[] agentSlash;

    public static BufferedImage[] monkeyWalkAnim;
    public static BufferedImage[] jaguarWalkAnim;
    public static BufferedImage[] jaguarRunAnim;
    public static BufferedImage[] batAnim;

    public static BufferedImage[] npcIdleAnim;

    public static BufferedImage spikeTrapImage;
    public static BufferedImage[] smallTrapAnim;

    public static BufferedImage keyImage;
    public static BufferedImage talismanImage;
    public static BufferedImage puzzleTableImage;

    public static BufferedImage puzzle1Sun;
    public static BufferedImage puzzle1Moon;
    public static BufferedImage puzzle1Star;
    public static BufferedImage puzzle1Bolt;
    public static BufferedImage puzzle2Gems;
    public static BufferedImage puzzle3Scroll;

    public static BufferedImage[] puzzle5CardFaces;
    public static BufferedImage puzzle5CardBack;

    public static BufferedImage[] torchAnim;
    public static BufferedImage trapDisabled;
    public static BufferedImage[] trapActiveAnim;
    public static BufferedImage chestClosed;
    public static BufferedImage chestOpened;


    public static void Init() {
        gameLogo = ImageLoader.LoadImage("/textures/logo.png");
        if (gameLogo == null) {
            System.err.println("Eroare: Nu s-a putut incarca logo.png! Verificati calea si numele fisierului.");
        }
    }

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

        level3TilesetImage = ImageLoader.LoadImage("/textures/tileset_level3.png");
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

        System.out.println("DEBUG ASSETS: Incarc fisierele de animatie pentru jucator...");
        // INCARCARE ANIMATII PLAYER
        BufferedImage playerIdleSheet = ImageLoader.LoadImage("/textures/player/idle.png");
        if (playerIdleSheet != null) {
            playerIdleUp = cropFramesFromArbitrarySheet(playerIdleSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 2, 0, 0);
            playerIdleLeft = cropFramesFromArbitrarySheet(playerIdleSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 2, 1, 0);
            playerIdleDown = cropFramesFromArbitrarySheet(playerIdleSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 2, 2, 0);
            playerIdleRight = cropFramesFromArbitrarySheet(playerIdleSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 2, 3, 0);
        }

        BufferedImage playerWalkSheet = ImageLoader.LoadImage("/textures/player/walk.png");
        if (playerWalkSheet != null) {
            playerUp = cropFramesFromArbitrarySheet(playerWalkSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 9, 0, 0);
            playerLeft = cropFramesFromArbitrarySheet(playerWalkSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 9, 1, 0);
            playerDown = cropFramesFromArbitrarySheet(playerWalkSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 9, 2, 0);
            playerRight = cropFramesFromArbitrarySheet(playerWalkSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 9, 3, 0);
        }

        BufferedImage playerRunSheet = ImageLoader.LoadImage("/textures/player/run.png");
        if (playerRunSheet != null) {
            playerRunUp = cropFramesFromArbitrarySheet(playerRunSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 8, 0, 0);
            playerRunLeft = cropFramesFromArbitrarySheet(playerRunSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 8, 1, 0);
            playerRunDown = cropFramesFromArbitrarySheet(playerRunSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 8, 2, 0);
            playerRunRight = cropFramesFromArbitrarySheet(playerRunSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 8, 3, 0);
        }

        BufferedImage playerJumpSheet = ImageLoader.LoadImage("/textures/player/jump.png");
        if (playerJumpSheet != null) {
            playerJumpUp = cropFramesFromArbitrarySheet(playerJumpSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 5, 0, 0);
            playerJumpLeft = cropFramesFromArbitrarySheet(playerJumpSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 5, 1, 0);
            playerJumpDown = cropFramesFromArbitrarySheet(playerJumpSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 5, 2, 0);
            playerJumpRight = cropFramesFromArbitrarySheet(playerJumpSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 5, 3, 0);
        }

        BufferedImage playerClimbSheet = ImageLoader.LoadImage("/textures/player/climb.png");
        if (playerClimbSheet != null) playerClimb = cropFramesFromArbitrarySheet(playerClimbSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 6, 0, 0);

        BufferedImage playerHurtSheet = ImageLoader.LoadImage("/textures/player/hurt.png");
        if (playerHurtSheet != null) playerHurt = cropFramesFromArbitrarySheet(playerHurtSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 6, 0, 0);

        BufferedImage playerSitSheet = ImageLoader.LoadImage("/textures/player/sit.png");
        if (playerSitSheet != null) playerSit = cropFramesFromArbitrarySheet(playerSitSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 3, 0, 0);

        BufferedImage playerEmoteSheet = ImageLoader.LoadImage("/textures/player/emote.png");
        if (playerEmoteSheet != null) playerEmote = cropFramesFromArbitrarySheet(playerEmoteSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 3, 0, 0);

        BufferedImage playerThrustSheet = ImageLoader.LoadImage("/textures/player/thrust.png");
        if (playerThrustSheet != null) playerThrust = cropFramesFromArbitrarySheet(playerThrustSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 8, 0, 0);

        BufferedImage playerHalfslashSheet = ImageLoader.LoadImage("/textures/player/halfslash.png");
        if (playerHalfslashSheet != null) playerHalfslash = cropFramesFromArbitrarySheet(playerHalfslashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 7, 0, 0);

        BufferedImage playerBackslashSheet = ImageLoader.LoadImage("/textures/player/backslash.png");
        if (playerBackslashSheet != null) playerBackslash = cropFramesFromArbitrarySheet(playerBackslashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 13, 0, 0);

        BufferedImage playerSpellcastSheet = ImageLoader.LoadImage("/textures/player/spellcast.png");
        if (playerSpellcastSheet != null) playerSpellcast = cropFramesFromArbitrarySheet(playerSpellcastSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 7, 0, 0);

        BufferedImage playerShootSheet = ImageLoader.LoadImage("/textures/player/shoot.png");
        if (playerShootSheet != null) playerShoot = cropFramesFromArbitrarySheet(playerShootSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 13, 0, 0);

        BufferedImage playerCombatIdleSheet = ImageLoader.LoadImage("/textures/player/combat_idle.png");
        if (playerCombatIdleSheet != null) playerCombatIdle = cropFramesFromArbitrarySheet(playerCombatIdleSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 2, 0, 0);

        BufferedImage playerSlashSheet = ImageLoader.LoadImage("/textures/player/slash.png");
        if (playerSlashSheet != null) playerSlash = cropFramesFromArbitrarySheet(playerSlashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 6, 0, 0);


        System.out.println("DEBUG ASSETS: Incarc fisierele de animatie pentru agent...");
        // INCARCARE ANIMATII AGENT
        BufferedImage agentIdleSheet = ImageLoader.LoadImage("/textures/agent/idle.png");
        if (agentIdleSheet != null) {
            agentIdleUp = cropFramesFromArbitrarySheet(agentIdleSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 2, 0, 0);
            agentIdleLeft = cropFramesFromArbitrarySheet(agentIdleSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 2, 1, 0);
            agentIdleDown = cropFramesFromArbitrarySheet(agentIdleSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 2, 2, 0);
            agentIdleRight = cropFramesFromArbitrarySheet(agentIdleSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 2, 3, 0);
        }

        BufferedImage agentWalkSheet = ImageLoader.LoadImage("/textures/agent/walk.png");
        if (agentWalkSheet != null) {
            agentUp = cropFramesFromArbitrarySheet(agentWalkSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 9, 0, 0);
            agentLeft = cropFramesFromArbitrarySheet(agentWalkSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 9, 1, 0);
            agentDown = cropFramesFromArbitrarySheet(agentWalkSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 9, 2, 0);
            agentRight = cropFramesFromArbitrarySheet(agentWalkSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 9, 3, 0);
        }

        BufferedImage agentRunSheet = ImageLoader.LoadImage("/textures/agent/run.png");
        if (agentRunSheet != null) {
            agentRunUp = cropFramesFromArbitrarySheet(agentRunSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 8, 0, 0);
            agentRunLeft = cropFramesFromArbitrarySheet(agentRunSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 8, 1, 0);
            agentRunDown = cropFramesFromArbitrarySheet(agentRunSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 8, 2, 0);
            agentRunRight = cropFramesFromArbitrarySheet(agentRunSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 8, 3, 0);
        }

        BufferedImage agentThrustSheet = ImageLoader.LoadImage("/textures/agent/thrust.png");
        if (agentThrustSheet != null) {
            agentThrust = cropFramesFromArbitrarySheet(agentThrustSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 8, 0, 0);
        } else {
            System.err.println("EROARE: Nu s-a putut incarca /textures/agent/thrust.png");
        }

        BufferedImage agentSlashSheet = ImageLoader.LoadImage("/textures/agent/slash.png");
        if (agentSlashSheet != null) {
            agentSlash = cropFramesFromArbitrarySheet(agentSlashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 6, 0, 0);
        } else {
            System.err.println("EROARE: Nu s-a putut incarca /textures/agent/slash.png");
        }

        System.out.println("DEBUG ASSETS: Incerc incarcare/decupare animale (fisiere separate)...");
        BufferedImage monkeySheet = ImageLoader.LoadImage("/textures/animals/monkey.png");
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
        } else {
            System.err.println("DEBUG ASSETS: Nu s-a putut incarca monkey.png.");
        }

        BufferedImage jaguarSheet = ImageLoader.LoadImage("/textures/animals/jaguar.png");
        if (jaguarSheet != null) {
            Rectangle[] jaguarFramesData = {
                    new Rectangle(0, 0, 76, 41), new Rectangle(76, 0, 73, 41),
                    new Rectangle(149, 0, 75, 41), new Rectangle(224, 0, 71, 41),
                    new Rectangle(295, 0, 70, 41), new Rectangle(365, 0, 66, 41),
                    new Rectangle(431, 0, 63, 41), new Rectangle(494, 0, 75, 41)
            };
            jaguarWalkAnim = cropFramesFromVariableRectangles(jaguarSheet, jaguarFramesData);
            if (jaguarWalkAnim == null) System.err.println("DEBUG ASSETS: Decupare jaguarWalkAnim a esuat.");
        } else {
            System.err.println("DEBUG ASSETS: Nu s-a putut incarca jaguar.png.");
        }

        BufferedImage batSheet = ImageLoader.LoadImage("/textures/animals/bat.png");
        if (batSheet != null) {
            Rectangle[] batFramesData = {
                    new Rectangle(0, 0, 18, 20), new Rectangle(18, 0, 21, 20),
                    new Rectangle(39, 0, 17, 20), new Rectangle(56, 0, 22, 20)
            };
            batAnim = cropFramesFromVariableRectangles(batSheet, batFramesData);
            if (batAnim == null) System.err.println("DEBUG ASSETS: Decupare batAnim a esuat.");
        } else {
            System.err.println("DEBUG ASSETS: Nu s-a putut incarca bat.png.");
        }


        System.out.println("DEBUG ASSETS: Incerc incarcare/decupare capcane (fisiere separate)...");
        BufferedImage spikesSheet = ImageLoader.LoadImage("/textures/traps/spikes.png");
        if (spikesSheet != null) {
            spikeTrapImage = spikesSheet.getSubimage(0, 0, 39, 25);
            if (spikeTrapImage == null) System.err.println("DEBUG ASSETS: Decupare spikeTrapImage a esuat.");
        } else {
            System.err.println("DEBUG ASSETS: Nu s-a putut incarca spikes.png.");
            spikeTrapImage = new BufferedImage(Tile.TILE_WIDTH, Tile.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics gSpike = spikeTrapImage.getGraphics();
            gSpike.setColor(Color.RED);
            gSpike.fillRect(0, 0, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
            gSpike.dispose();
        }

        BufferedImage trapSheet = ImageLoader.LoadImage("/textures/traps/trap.png");
        if (trapSheet != null) {
            Rectangle[] smallTrapFramesData = {
                    new Rectangle(0, 0, 39, 25), new Rectangle(39, 0, 45, 25), new Rectangle(84, 0, 51, 25)
            };
            smallTrapAnim = cropFramesFromVariableRectangles(trapSheet, smallTrapFramesData);
            if (smallTrapAnim == null) System.err.println("DEBUG ASSETS: Decupare smallTrapAnim a esuat.");
        } else {
            System.err.println("DEBUG ASSETS: Nu s-a putut incarca trap.png.");
            smallTrapAnim = new BufferedImage[]{new BufferedImage(Tile.TILE_WIDTH, Tile.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB)};
            Graphics gSmall = smallTrapAnim[0].getGraphics();
            gSmall.setColor(Color.ORANGE);
            gSmall.fillRect(0, 0, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
            gSmall.dispose();
        }

        if (level3TilesetImage != null) {
            System.out.println("DEBUG ASSETS: Incerc decupare animatii Nivel 3...");

            int[] torchGIDs = {423, 424, 425, 426, 427, 428, 429, 430};
            torchAnim = new BufferedImage[torchGIDs.length];
            for (int i = 0; i < torchGIDs.length; i++) {
                torchAnim[i] = getTileImageByGID(torchGIDs[i], level3TilesetImage);
            }

            trapDisabled = getTileImageByGID(45, level3TilesetImage);
            trapActiveAnim = new BufferedImage[3];
            trapActiveAnim[0] = getTileImageByGID(46, level3TilesetImage);
            trapActiveAnim[1] = getTileImageByGID(44, level3TilesetImage);
            trapActiveAnim[2] = getTileImageByGID(43, level3TilesetImage);

            chestClosed = getTileImageByGID(522, level3TilesetImage);
            chestOpened = getTileImageByGID(614, level3TilesetImage);
        }

        BufferedImage npcIdleSheet = ImageLoader.LoadImage("/textures/old_man.png");
        if (npcIdleSheet != null) {
            npcIdleAnim = cropFramesFromArbitrarySheet(npcIdleSheet, NPC_FRAME_WIDTH, NPC_FRAME_HEIGHT, 1, 6, 0, 0);
            if (npcIdleAnim == null) System.err.println("DEBUG ASSETS: Decupare npcIdleAnim a esuat.");
        } else {
            System.err.println("DEBUG ASSETS: Nu s-a putut incarca old_man.png.");
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
                        System.err.println("ATENTIE (cropFramesFromArbitrarySheet - fixed grid): Cadrul (" + (startRow + r) + "," + (startCol + c) + ") depaseste limitele sheet-ului.");
                        continue;
                    }
                    frames.add(sheet.getSubimage(x, y, frameWidth, frameHeight));
                } catch (Exception e) {
                    System.err.println("Eroare (cropFramesFromArbitrarySheet - fixed grid) la decuparea cadrului (" + (startRow + r) + "," + (startCol + c) + ")");
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