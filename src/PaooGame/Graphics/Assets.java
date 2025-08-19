package PaooGame.Graphics;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
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

    public static final int DEFAULT_AGENT_WIDTH = 64;
    public static final int DEFAULT_AGENT_HEIGHT = 64;

    public static BufferedImage[] monkeyWalkAnim;
    public static BufferedImage[] jaguarWalkAnim;
    public static BufferedImage[] batAnim;

    // PLAYER ANIMATIONS
    public static BufferedImage[] playerDown, playerUp, playerLeft, playerRight;
    public static BufferedImage[] playerIdleDown, playerIdleUp, playerIdleLeft, playerIdleRight;
    public static BufferedImage[] playerRunDown, playerRunUp, playerRunLeft, playerRunRight;
    public static BufferedImage[] playerJumpDown, playerJumpUp, playerJumpLeft, playerJumpRight;
    public static BufferedImage[] playerHurt, playerClimb, playerShoot, playerBackslash, playerHalfslash;
    public static BufferedImage[] playerEmote, playerCombatIdle, playerThrust, playerSlash, playerSit, playerSpellcast;
    public static BufferedImage[] playerThrustUp, playerThrustDown, playerThrustLeft, playerThrustRight;
    public static BufferedImage[] playerHalfslashUp, playerHalfslashDown, playerHalfslashLeft, playerHalfslashRight;
    public static BufferedImage[] playerSlashUp, playerSlashDown, playerSlashLeft, playerSlashRight;

    // AGENT ANIMATIONS
    public static BufferedImage[] agentDown, agentUp, agentLeft, agentRight;
    public static BufferedImage[] agentIdleDown, agentIdleUp, agentIdleLeft, agentIdleRight;
    public static BufferedImage[] agentRunDown, agentRunUp, agentRunLeft, agentRunRight;
    public static BufferedImage[] agentJumpDown, agentJumpUp, agentJumpLeft, agentJumpRight;
    public static BufferedImage[] agentHurt, agentClimb, agentShoot, agentBackslash, agentHalfslash;
    public static BufferedImage[] agentEmote, agentCombatIdle, agentThrust, agentSlash, agentSit, agentSpellcast;
    public static BufferedImage[] agentThrustUp, agentThrustDown, agentThrustLeft, agentThrustRight;
    public static BufferedImage[] agentHalfslashUp, agentHalfslashDown, agentHalfslashLeft, agentHalfslashRight;
    public static BufferedImage[] agentSlashUp, agentSlashDown, agentSlashLeft, agentSlashRight;

    public static BufferedImage[] npcIdleAnim;
    public static BufferedImage spikeTrapImage;
    public static BufferedImage keyImage;
    public static BufferedImage talismanImage;
    public static BufferedImage puzzleTableImage;
    public static BufferedImage woodSignImage;
    public static BufferedImage popupImage;
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

    /*!
     * \fn public static void Init()
     * \brief Initializeaza logo-ul jocului, apelat o singură dată.
     */
    public static void Init() {
        gameLogo = ImageLoader.LoadImage("/textures/logo.png");
        if (gameLogo == null) {
            System.err.println("Eroare: Nu s-a putut incarca logo.png! Verificati calea si numele fisierului.");
        }
    }

    /*!
     * \fn public static void LoadGameAssets()
     * \brief Încarcă toate asset-urile principale ale jocului.
     */
    /*!
     * \fn public static void LoadGameAssets()
     * \brief Încarcă toate asset-urile principale ale jocului.
     */
    public static void LoadGameAssets() {
        // BACKGROUNDS & TILESETS
        backgroundMenu = ImageLoader.LoadImage("/textures/menu_background.jpg");
        jungleTilesetImage = ImageLoader.LoadImage("/textures/gentle forest.png");
        gameSpriteSheet = new SpriteSheet(jungleTilesetImage);
        level2TilesetImage = ImageLoader.LoadImage("/textures/tileset_level2.png");
        level3TilesetImage = ImageLoader.LoadImage("/textures/tileset_level3.png");

        // OBIECTE
        keyImage = ImageLoader.LoadImage("/textures/objects/key.png");
        talismanImage = ImageLoader.LoadImage("/textures/objects/talisman.png");
        popupImage = ImageLoader.LoadImage("/textures/objects/pop_up.png");
        puzzleTableImage = ImageLoader.LoadImage("/textures/objects/table.png");
        woodSignImage = ImageLoader.LoadImage("/textures/objects/wood_sign.png");

        // PUZZLES
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
                puzzle5CardFaces[i] = cardFacesSheet.getSubimage((i + 1) * CARD_WIDTH, 0, CARD_WIDTH, CARD_HEIGHT);
            }
        }

        // --- PLAYER ANIMATIONS ---
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
        BufferedImage playerHurtSheet = ImageLoader.LoadImage("/textures/player/hurt.png");
        if (playerHurtSheet != null) playerHurt = cropFramesFromArbitrarySheet(playerHurtSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 6, 0, 0);

        BufferedImage playerClimbSheet = ImageLoader.LoadImage("/textures/player/climb.png");
        if (playerClimbSheet != null) playerClimb = cropFramesFromArbitrarySheet(playerClimbSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 6, 0, 0);

        BufferedImage playerSitSheet = ImageLoader.LoadImage("/textures/player/sit.png");
        if (playerSitSheet != null) playerSit = cropFramesFromArbitrarySheet(playerSitSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 3, 0, 0);

        BufferedImage playerEmoteSheet = ImageLoader.LoadImage("/textures/player/emote.png");
        if (playerEmoteSheet != null) playerEmote = cropFramesFromArbitrarySheet(playerEmoteSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 3, 0, 0);

        BufferedImage playerThrustSheet = ImageLoader.LoadImage("/textures/player/thrust.png");
        if (playerThrustSheet != null) {
            playerThrustUp = cropFramesFromArbitrarySheet(playerThrustSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 8, 0, 0);
            playerThrustLeft = cropFramesFromArbitrarySheet(playerThrustSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 8, 1, 0);
            playerThrustDown = cropFramesFromArbitrarySheet(playerThrustSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 8, 2, 0);
            playerThrustRight = cropFramesFromArbitrarySheet(playerThrustSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 8, 3, 0);
        }
        BufferedImage playerHalfslashSheet = ImageLoader.LoadImage("/textures/player/halfslash.png");
        if (playerHalfslashSheet != null) {
            playerHalfslashUp = cropFramesFromArbitrarySheet(playerHalfslashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 7, 0, 0);
            playerHalfslashLeft = cropFramesFromArbitrarySheet(playerHalfslashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 7, 1, 0);
            playerHalfslashDown = cropFramesFromArbitrarySheet(playerHalfslashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 7, 2, 0);
            playerHalfslashRight = cropFramesFromArbitrarySheet(playerHalfslashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 7, 3, 0);
        }
        BufferedImage playerBackslashSheet = ImageLoader.LoadImage("/textures/player/backslash.png");
        if (playerBackslashSheet != null) playerBackslash = cropFramesFromArbitrarySheet(playerBackslashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 13, 0, 0);

        BufferedImage playerSpellcastSheet = ImageLoader.LoadImage("/textures/player/spellcast.png");
        if (playerSpellcastSheet != null) playerSpellcast = cropFramesFromArbitrarySheet(playerSpellcastSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 7, 0, 0);

        BufferedImage playerShootSheet = ImageLoader.LoadImage("/textures/player/shoot.png");
        if (playerShootSheet != null) playerShoot = cropFramesFromArbitrarySheet(playerShootSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 13, 0, 0);

        BufferedImage playerCombatIdleSheet = ImageLoader.LoadImage("/textures/player/combat_idle.png");
        if (playerCombatIdleSheet != null) playerCombatIdle = cropFramesFromArbitrarySheet(playerCombatIdleSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 4, 2, 0, 0);

        BufferedImage playerSlashSheet = ImageLoader.LoadImage("/textures/player/slash.png");
        if (playerSlashSheet != null) {
            playerSlashUp = cropFramesFromArbitrarySheet(playerSlashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 6, 0, 0);
            playerSlashLeft = cropFramesFromArbitrarySheet(playerSlashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 6, 1, 0);
            playerSlashDown = cropFramesFromArbitrarySheet(playerSlashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 6, 2, 0);
            playerSlashRight = cropFramesFromArbitrarySheet(playerSlashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 6, 3, 0);
        }

        // --- AGENT ANIMATIONS ---
        BufferedImage agentIdleSheet = ImageLoader.LoadImage("/textures/agent/idle.png");
        if (agentIdleSheet != null) {
            agentIdleUp = cropFramesFromArbitrarySheet(agentIdleSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 2, 0, 0);
            agentIdleLeft = cropFramesFromArbitrarySheet(agentIdleSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 2, 1, 0);
            agentIdleDown = cropFramesFromArbitrarySheet(agentIdleSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 2, 2, 0);
            agentIdleRight = cropFramesFromArbitrarySheet(agentIdleSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 2, 3, 0);
        }
        BufferedImage agentWalkSheet = ImageLoader.LoadImage("/textures/agent/walk.png");
        if (agentWalkSheet != null) {
            agentUp = cropFramesFromArbitrarySheet(agentWalkSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 9, 0, 0);
            agentLeft = cropFramesFromArbitrarySheet(agentWalkSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 9, 1, 0);
            agentDown = cropFramesFromArbitrarySheet(agentWalkSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 9, 2, 0);
            agentRight = cropFramesFromArbitrarySheet(agentWalkSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 9, 3, 0);
        }
        BufferedImage agentRunSheet = ImageLoader.LoadImage("/textures/agent/run.png");
        if (agentRunSheet != null) {
            agentRunUp = cropFramesFromArbitrarySheet(agentRunSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 8, 0, 0);
            agentRunLeft = cropFramesFromArbitrarySheet(agentRunSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 8, 1, 0);
            agentRunDown = cropFramesFromArbitrarySheet(agentRunSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 8, 2, 0);
            agentRunRight = cropFramesFromArbitrarySheet(agentRunSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 8, 3, 0);
        }
        BufferedImage agentHurtSheet = ImageLoader.LoadImage("/textures/agent/hurt.png");
        if (agentHurtSheet != null) agentHurt = cropFramesFromArbitrarySheet(agentHurtSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 6, 0, 0);

        BufferedImage agentThrustSheet = ImageLoader.LoadImage("/textures/agent/thrust.png");
        if (agentThrustSheet != null) {
            agentThrustUp = cropFramesFromArbitrarySheet(agentThrustSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 8, 0, 0);
            agentThrustLeft = cropFramesFromArbitrarySheet(agentThrustSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 8, 1, 0);
            agentThrustDown = cropFramesFromArbitrarySheet(agentThrustSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 8, 2, 0);
            agentThrustRight = cropFramesFromArbitrarySheet(agentThrustSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 8, 3, 0);
            agentThrust = agentThrustDown;
        }

        // ADAUGĂ SAU VERIFICĂ ACEST BLOC PENTRU HALFSLASH
        BufferedImage agentHalfslashSheet = ImageLoader.LoadImage("/textures/agent/halfslash.png");
        if(agentHalfslashSheet != null) {
            agentHalfslashUp = cropFramesFromArbitrarySheet(agentHalfslashSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 7, 0, 0);
            agentHalfslashLeft = cropFramesFromArbitrarySheet(agentHalfslashSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 7, 1, 0);
            agentHalfslashDown = cropFramesFromArbitrarySheet(agentHalfslashSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 7, 2, 0);
            agentHalfslashRight = cropFramesFromArbitrarySheet(agentHalfslashSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 7, 3, 0);
        }

        BufferedImage agentBackslashSheet = ImageLoader.LoadImage("/textures/agent/backslash.png");
        if(agentBackslashSheet != null) agentBackslash = cropFramesFromArbitrarySheet(agentBackslashSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 13, 0, 0); // TODO: Verifică numărul de cadre

        BufferedImage agentClimbSheet = ImageLoader.LoadImage("/textures/agent/climb.png");
        if(agentClimbSheet != null) agentClimb = cropFramesFromArbitrarySheet(agentClimbSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 6, 0, 0); // TODO: Verifică numărul de cadre

        BufferedImage agentCombatIdleSheet = ImageLoader.LoadImage("/textures/agent/combat_idle.png");
        if(agentCombatIdleSheet != null) agentCombatIdle = cropFramesFromArbitrarySheet(agentCombatIdleSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 4, 0, 0); // TODO: Verifică numărul de cadre

        BufferedImage agentEmoteSheet = ImageLoader.LoadImage("/textures/agent/emote.png");
        if(agentEmoteSheet != null) agentEmote = cropFramesFromArbitrarySheet(agentEmoteSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 12, 0, 0); // TODO: Verifică numărul de cadre

        BufferedImage agentJumpSheet = ImageLoader.LoadImage("/textures/agent/jump.png");
        if(agentJumpSheet != null) agentJumpDown = cropFramesFromArbitrarySheet(agentJumpSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 5, 0, 0); // TODO: Verifică numărul de cadre

        BufferedImage agentShootSheet = ImageLoader.LoadImage("/textures/agent/shoot.png");
        if(agentShootSheet != null) agentShoot = cropFramesFromArbitrarySheet(agentShootSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 13, 0, 0); // TODO: Verifică numărul de cadre


        // --- ANIMALS & NPCS ---
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
        }

        BufferedImage batSheet = ImageLoader.LoadImage("/textures/animals/bat.png");
        if (batSheet != null) {
            Rectangle[] batFramesData = {
                    new Rectangle(0, 0, 18, 20), new Rectangle(18, 0, 21, 20),
                    new Rectangle(39, 0, 17, 20), new Rectangle(56, 0, 22, 20)
            };
            batAnim = cropFramesFromVariableRectangles(batSheet, batFramesData);
        }

        BufferedImage npcIdleSheet = ImageLoader.LoadImage("/textures/old_man.png");
        if (npcIdleSheet != null) {
            npcIdleAnim = cropFramesFromArbitrarySheet(npcIdleSheet, NPC_FRAME_WIDTH, NPC_FRAME_HEIGHT, 1, 6, 0, 0);
        }

        // --- TRAPS & LEVEL 3 ASSETS ---
        BufferedImage spikesSheet = ImageLoader.LoadImage("/textures/traps/spikes.png");
        if (spikesSheet != null) {
            spikeTrapImage = spikesSheet.getSubimage(0, 0, 39, 25);
        }
        if (level3TilesetImage != null) {
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