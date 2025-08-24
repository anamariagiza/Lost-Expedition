package PaooGame.Tiles;

import PaooGame.Graphics.Assets;
import PaooGame.Graphics.SpriteSheet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * @class Tile
 * @brief Clasa de baza pentru toate dalele din joc si fabrica pentru crearea acestora.
 * Implementeaza sablonul de proiectare Flyweight. O singura instanta pentru fiecare
 * tip de dala este creata si stocata intr-o harta statica. Cand se cere o dala,
 * se returneaza instanta existenta, economisind astfel memorie.
 */
public class Tile
{
    /** Harta statica ce stocheaza instantele unice de dale (Flyweight pattern).*/
    public static final Map<Integer, Tile> tiles = new HashMap<>();
    /** GID-uri (Global ID) pentru dale speciale, folosite pentru a identifica tipuri specifice de dale in cod.*/
    public static final int NO_TILE_GID = 0;

    public static final int GRASS_TILE_GID_SOLID = 82;
    public static final int WALL_TILE_GID_SOLID = 33;
    public static final int[] ROCK_TILE_GIDS = {216, 97, 232, 217, 233, 249, 234, 235, 236, 221};

    public static final int DOOR_CLOSED_TOP_LEFT_GID = 56;
    public static final int DOOR_CLOSED_TOP_RIGHT_GID = 57;
    public static final int DOOR_CLOSED_BOTTOM_LEFT_GID = 88;
    public static final int DOOR_CLOSED_BOTTOM_RIGHT_GID = 89;
    public static final int DOOR_OPEN_TOP_LEFT_GID = 60;
    public static final int DOOR_OPEN_TOP_RIGHT_GID = 61;
    public static final int DOOR_OPEN_BOTTOM_LEFT_GID = 92;
    public static final int DOOR_OPEN_BOTTOM_RIGHT_GID = 93;

    /** Dimensiunile standard ale unei dale, preluate din clasa SpriteSheet.*/
    public static final int TILE_WIDTH  = SpriteSheet.getTileWidth();
    public static final int TILE_HEIGHT = SpriteSheet.getTileHeight();

    /** GID-ul unic al acestei dalei.*/
    protected final int id;

    /**
     * @brief Constructorul clasei Tile. Este protejat pentru a incuraja crearea prin metoda fabrica GetTile.
     * @param gid GID-ul dalei.
     */
    public Tile(int gid)
    {
        this.id = gid;
        tiles.put(gid, this);
    }

    /**
     * @brief Metoda de initializare a dalelor. Poate ramane goala datorita incarcarii "la cerere".
     */
    public static void InitTiles() {
        // Aceasta metoda poate ramane goala, deoarece GetTile creeaza dalele la nevoie
    }

    /**
     * @brief Metoda fabrica (Factory) pentru a obtine o instanta de dala.
     * Cauta in harta statica o dala cu GID-ul dat. Daca nu exista, o creeaza,
     * o adauga in harta si o returneaza. Altfel, returneaza instanta existenta.
     * @param gid GID-ul dalei dorite.
     * @return O instanta a clasei Tile sau a unei subclase.
     */
    public static Tile GetTile(int gid) {
        if (gid == NO_TILE_GID) {
            return GetDefaultTile();
        }
        Tile tile = tiles.get(gid);
        if (tile == null) {
            if (gid == GRASS_TILE_GID_SOLID) {
                tile = new GrassTile(gid);
            } else if (gid == WALL_TILE_GID_SOLID || gid == 64) {
                tile = new WallTile(gid);
            } else if (gid == DOOR_CLOSED_TOP_LEFT_GID || gid == DOOR_CLOSED_TOP_RIGHT_GID ||
                    gid == DOOR_CLOSED_BOTTOM_LEFT_GID || gid == DOOR_CLOSED_BOTTOM_RIGHT_GID) {
                tile = new DoorTile(gid, true);
            } else if (gid == DOOR_OPEN_TOP_LEFT_GID || gid == DOOR_OPEN_TOP_RIGHT_GID ||
                    gid == DOOR_OPEN_BOTTOM_LEFT_GID || gid == DOOR_OPEN_BOTTOM_RIGHT_GID) {
                tile = new DoorTile(gid, false);
            }
            // Logica pentru usa finala de la Nivelul 3
            else if (gid == 70 || gid == 71 || gid == 116 || gid == 117) {
                tile = new DoorTile(gid, true);
            }
            else if (gid == 74 || gid == 75 || gid == 120 || gid == 121) {
                tile = new DoorTile(gid, false);
            }
            else {
                boolean isRock = false;
                for (int rockGid : ROCK_TILE_GIDS) {
                    if (gid == rockGid) {
                        tile = new RockTile(gid);
                        isRock = true;
                        break;
                    }
                }
                if (!isRock) {
                    tile = new Tile(gid) {};
                }
            }
            tiles.put(gid, tile);
        }
        return tile;
    }

    /**
     * @brief Returneaza o dala goala, non-solida, pentru GID-ul 0.
     */
    public static Tile GetDefaultTile() {
        return new Tile(NO_TILE_GID) {};
    }

    /**
     * @brief Metoda de actualizare a logicii unei dale (daca este necesar, ex: dale animate).
     */
    public void Update() { }

    /**
     * @brief Deseneaza dala la pozitia specificata.
     * @param g Contextul grafic.
     * @param x Pozitia X pe ecran.
     * @param y Pozitia Y pe ecran.
     * @param width Latimea de desenare.
     * @param height Inaltimea de desenare.
     * @param tilesetImage Imaginea tileset-ului din care se va extrage dala.
     */
    public void Draw(Graphics g, int x, int y, int width, int height, BufferedImage tilesetImage) {
        BufferedImage tileImage = Assets.getTileImageByGID(this.id, tilesetImage);
        if (tileImage != null) {
            g.drawImage(tileImage, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }

    /**
     * @brief Verifica daca dala este solida (blocheaza miscarea).
     * @return False in mod implicit. Subclasele suprascriu aceasta metoda.
     */
    public boolean IsSolid() {
        return false;
    }

    /**
     * @brief Returneaza GID-ul dalei.
     * @return ID-ul global al dalei.
     */
    public int GetId() {
        return id;
    }
}