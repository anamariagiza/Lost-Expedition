package PaooGame.Graphics;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * @class Assets
 * @brief Clasa statica ce gestioneaza toate resursele grafice ale jocului.
 * Aceasta clasa pre-incarca toate imaginile necesare la pornirea jocului
 * si le stocheaza in campuri statice pentru a fi accesibile rapid din orice
 * parte a codului, fara a fi nevoie sa se citeasca in mod repetat de pe disc.
 */
public class Assets {

    /** Tilesets si Imagini de Baza.*/
    public static SpriteSheet gameSpriteSheet;
    public static BufferedImage jungleTilesetImage;
    public static BufferedImage level2TilesetImage;
    public static BufferedImage level3TilesetImage;

    public static BufferedImage gameLogo;
    public static BufferedImage backgroundMenu;

    /** Constante pentru Dimensiunile Cadrelor.*/
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

    /** Animatii Animale.*/
    public static BufferedImage[] monkeyWalkAnim;
    public static BufferedImage[] jaguarWalkAnim;
    public static BufferedImage[] batAnim;

    /** Animatii Jucator (Player).*/
    public static BufferedImage[] playerDown, playerUp, playerLeft, playerRight;
    public static BufferedImage[] playerIdleDown, playerIdleUp, playerIdleLeft, playerIdleRight;
    public static BufferedImage[] playerRunDown, playerRunUp, playerRunLeft, playerRunRight;
    public static BufferedImage[] playerJumpDown, playerJumpUp, playerJumpLeft, playerJumpRight;
    public static BufferedImage[] playerHurt;
    public static BufferedImage[] playerHalfslashUp, playerHalfslashDown, playerHalfslashLeft, playerHalfslashRight;

    /** Animatii Inamic (Agent).*/
    public static BufferedImage[] agentDown, agentUp, agentLeft, agentRight;
    public static BufferedImage[] agentIdleDown, agentIdleUp, agentIdleLeft, agentIdleRight;
    public static BufferedImage[] agentRunDown, agentRunUp, agentRunLeft, agentRunRight;
    public static BufferedImage[] agentHurt;
    public static BufferedImage[] agentHalfslashUp, agentHalfslashDown, agentHalfslashLeft, agentHalfslashRight;

    /** Animatii NPC.*/
    public static BufferedImage[] npcIdleAnim;

    /** Obiecte si Interfata Utilizator (UI).*/
    public static BufferedImage spikeTrapImage;
    public static BufferedImage keyImage;
    public static BufferedImage talismanImage;
    public static BufferedImage puzzleTableImage;
    public static BufferedImage woodSignImage;
    public static BufferedImage popupImage;

    /** Resurse Puzzle-uri.*/
    public static BufferedImage puzzle1Sun;
    public static BufferedImage puzzle1Moon;
    public static BufferedImage puzzle1Star;
    public static BufferedImage puzzle1Bolt;
    public static BufferedImage puzzle2Gems;
    public static BufferedImage puzzle3Scroll;
    public static BufferedImage[] puzzle5CardFaces;
    public static BufferedImage puzzle5CardBack;

    /** Resurse Capcane si Nivel 3.*/
    public static BufferedImage trapDisabled;
    public static BufferedImage[] trapActiveAnim;
    public static BufferedImage chestClosed;
    public static BufferedImage chestOpened;

    /**
     * @brief Metoda de initializare initiala.
     * Incarca doar resursele absolut necesare pentru afisarea ecranului de incarcare.
     */
    public static void Init() {
        gameLogo = ImageLoader.LoadImage("/textures/logo.png");
        if (gameLogo == null) {
            System.err.println("Eroare: Nu s-a putut incarca logo.png! Verificati calea si numele fisierului.");
        }
    }

    /**
     * @brief Incarca toate resursele grafice ale jocului in memorie.
     * Aceasta metoda este de obicei apelata dintr-un ecran de incarcare.
     */
    public static void LoadGameAssets() {
        // Incarcarea fundalului meniului
        backgroundMenu = ImageLoader.LoadImage("/textures/menu_background.jpg");

        // Incarcarea sprite sheet-urilor pentru cele 3 nivele
        jungleTilesetImage = ImageLoader.LoadImage("/textures/gentle forest.png");
        gameSpriteSheet = new SpriteSheet(jungleTilesetImage);
        level2TilesetImage = ImageLoader.LoadImage("/textures/tileset_level2.png");
        level3TilesetImage = ImageLoader.LoadImage("/textures/tileset_level3.png");

        // Incarcarea obiectelor
        keyImage = ImageLoader.LoadImage("/textures/objects/key.png");
        talismanImage = ImageLoader.LoadImage("/textures/objects/talisman.png");
        popupImage = ImageLoader.LoadImage("/textures/objects/pop_up.png");
        puzzleTableImage = ImageLoader.LoadImage("/textures/objects/table.png");
        woodSignImage = ImageLoader.LoadImage("/textures/objects/wood_sign.png");

        // Incarcarea elementelor de puzzle pentru nivelul 2
        puzzle1Sun = ImageLoader.LoadImage("/textures/puzzles/sun.png");
        puzzle1Moon = ImageLoader.LoadImage("/textures/puzzles/moon.png");
        puzzle1Star = ImageLoader.LoadImage("/textures/puzzles/star.png");
        puzzle1Bolt = ImageLoader.LoadImage("/textures/puzzles/bolt.png");
        puzzle2Gems = ImageLoader.LoadImage("/textures/puzzles/crystals.png");
        puzzle3Scroll = ImageLoader.LoadImage("/textures/puzzles/ancient_scroll.png");
        BufferedImage cardFacesSheet = ImageLoader.LoadImage("/textures/puzzles/card_faces.png");
        if (cardFacesSheet != null) {
            puzzle5CardFaces = new BufferedImage[8];
            int CARD_WIDTH = 60;
            int CARD_HEIGHT = 84;
            puzzle5CardBack = cardFacesSheet.getSubimage(0, 0, CARD_WIDTH, CARD_HEIGHT);
            for (int i = 0; i < 8; i++) {
                puzzle5CardFaces[i] = cardFacesSheet.getSubimage((i + 1) * CARD_WIDTH, 0, CARD_WIDTH, CARD_HEIGHT);
            }
        }

        // Incarcarea animatiilor pentru player
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

        BufferedImage playerHalfslashSheet = ImageLoader.LoadImage("/textures/player/halfslash.png");
        if (playerHalfslashSheet != null) {
            playerHalfslashUp = cropFramesFromArbitrarySheet(playerHalfslashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 7, 0, 0);
            playerHalfslashLeft = cropFramesFromArbitrarySheet(playerHalfslashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 7, 1, 0);
            playerHalfslashDown = cropFramesFromArbitrarySheet(playerHalfslashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 7, 2, 0);
            playerHalfslashRight = cropFramesFromArbitrarySheet(playerHalfslashSheet, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT, 1, 7, 3, 0);
        }

        //Incarcarea animatiilor pentru agent
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

        BufferedImage agentHalfslashSheet = ImageLoader.LoadImage("/textures/agent/halfslash.png");
        if(agentHalfslashSheet != null) {
            agentHalfslashUp = cropFramesFromArbitrarySheet(agentHalfslashSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 7, 0, 0);
            agentHalfslashLeft = cropFramesFromArbitrarySheet(agentHalfslashSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 7, 1, 0);
            agentHalfslashDown = cropFramesFromArbitrarySheet(agentHalfslashSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 7, 2, 0);
            agentHalfslashRight = cropFramesFromArbitrarySheet(agentHalfslashSheet, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT, 1, 7, 3, 0);
        }

        // Incarcarea animatiilor pentru NPC si animale
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

        // Incarcarea animatiei pentru capcana
        BufferedImage spikesSheet = ImageLoader.LoadImage("/textures/traps/spikes.png");
        if (spikesSheet != null) {
            spikeTrapImage = spikesSheet.getSubimage(0, 0, 39, 25);
        }
        // Incarcarea animatiei pentru capcanele de la nivelul 3
        if (level3TilesetImage != null) {
            trapDisabled = getTileImageByGID(45, level3TilesetImage);
            trapActiveAnim = new BufferedImage[3];
            trapActiveAnim[0] = getTileImageByGID(46, level3TilesetImage);
            trapActiveAnim[1] = getTileImageByGID(44, level3TilesetImage);
            trapActiveAnim[2] = getTileImageByGID(43, level3TilesetImage);
            chestClosed = getTileImageByGID(522, level3TilesetImage);
            chestOpened = getTileImageByGID(614, level3TilesetImage);
        }
    }

    /**
     * @brief Metoda ajutatoare pentru a decupa cadre dintr-un spritesheet cu o grila regulata.
     * @param sheet Imaginea spritesheet completa.
     * @param frameWidth Latimea unui singur cadru.
     * @param frameHeight Inaltimea unui singur cadru.
     * @param numRowsToCrop Numarul de randuri de decupat.
     * @param numColsToCrop Numarul de coloane de decupat.
     * @param startRow Randul de start (index 0).
     * @param startCol Coloana de start (index 0).
     * @return Un vector de BufferedImage continand cadrele decupate.
     */
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

    /**
     * @brief Metoda ajutatoare pentru a decupa cadre dintr-un spritesheet unde cadrele au dimensiuni variabile.
     * @param sheet Imaginea spritesheet completa.
     * @param framesData Un vector de Rectangle, unde fiecare dreptunghi defineste pozitia si dimensiunea unui cadru.
     * @return Un vector de BufferedImage continand cadrele decupate.
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

    /**
     * @brief Metoda utilitara pentru a extrage imaginea unei dale dintr-un tileset, pe baza GID-ului.
     * @param gid Global ID-ul dalei (din Tiled). GID-urile incep de la 1.
     * @param tilesetImage Imaginea completa a tileset-ului.
     * @return Un BufferedImage continand doar dala specificata.
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