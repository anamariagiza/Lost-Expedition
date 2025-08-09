package PaooGame.Tiles;

import PaooGame.Graphics.Assets;
import PaooGame.Graphics.SpriteSheet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/*!
 * \class public class Tile
 * \brief Retine toate dalele intr-un vector si ofera posibilitatea regasirii dupa un id.
 */
public class Tile
{
    public static final Map<Integer, Tile> tiles = new HashMap<>();
    public static final int NO_TILE_GID = 0;

    // GID-uri pentru dalele specifice, ALINIATE CU TILED
    public static final int GRASS_TILE_GID_SOLID = 82;

    // NOU: GID pentru peretele solid de la Nivelul 2
    public static final int WALL_TILE_GID_SOLID = 33;

    // NOU: GID-uri pentru usile de la puzzle-uri
    public static final int DOOR_CLOSED_TOP_LEFT_GID = 56;
    public static final int DOOR_CLOSED_TOP_RIGHT_GID = 57;
    public static final int DOOR_CLOSED_BOTTOM_LEFT_GID = 88;
    public static final int DOOR_CLOSED_BOTTOM_RIGHT_GID = 89;

    public static final int DOOR_OPEN_TOP_LEFT_GID = 60;
    public static final int DOOR_OPEN_TOP_RIGHT_GID = 61;
    public static final int DOOR_OPEN_BOTTOM_LEFT_GID = 92;
    public static final int DOOR_OPEN_BOTTOM_RIGHT_GID = 93;

    public static final int TILE_WIDTH  = SpriteSheet.getTileWidth();
    public static final int TILE_HEIGHT = SpriteSheet.getTileHeight();

    // Referinte la instante de dale
    public static Tile grassTileSolid;

    // NOU: Dala pentru peretele solid de la Nivelul 2
    public static Tile wallTileSolid;

    // NOU: Dalele pentru uși
    public static Tile doorClosedTopLeftTile;
    public static Tile doorClosedTopRightTile;
    public static Tile doorClosedBottomLeftTile;
    public static Tile doorClosedBottomRightTile;

    public static Tile doorOpenTopLeftTile;
    public static Tile doorOpenTopRightTile;
    public static Tile doorOpenBottomLeftTile;
    public static Tile doorOpenBottomRightTile;

    protected final int id;

    /*!
     * \fn public Tile(int gid)
     * \brief Constructorul aferent clasei.
     * \param gid Global ID-ul dalei din Tiled.
     */
    public Tile(int gid)
    {
        this.id = gid;
        tiles.put(gid, this);
    }

    /*!
     * \fn public static void InitTiles()
     * \brief Metoda statica pentru a initializa toate tipurile de dale.
     * Aceasta ar trebui apelata o singura data, dupa Assets.LoadGameAssets().
     */
    public static void InitTiles() {
        grassTileSolid = new GrassTile(GRASS_TILE_GID_SOLID);
        wallTileSolid = new WallTile(WALL_TILE_GID_SOLID);

        doorClosedTopLeftTile = new DoorTile(DOOR_CLOSED_TOP_LEFT_GID, true);
        doorClosedTopRightTile = new DoorTile(DOOR_CLOSED_TOP_RIGHT_GID, true);
        doorClosedBottomLeftTile = new DoorTile(DOOR_CLOSED_BOTTOM_LEFT_GID, true);
        doorClosedBottomRightTile = new DoorTile(DOOR_CLOSED_BOTTOM_RIGHT_GID, true);

        doorOpenTopLeftTile = new DoorTile(DOOR_OPEN_TOP_LEFT_GID, false);
        doorOpenTopRightTile = new DoorTile(DOOR_OPEN_TOP_RIGHT_GID, false);
        doorOpenBottomLeftTile = new DoorTile(DOOR_OPEN_BOTTOM_LEFT_GID, false);
        doorOpenBottomRightTile = new DoorTile(DOOR_OPEN_BOTTOM_RIGHT_GID, false);


        if (!tiles.containsKey(GRASS_TILE_GID_SOLID)) tiles.put(GRASS_TILE_GID_SOLID, grassTileSolid);
        if (!tiles.containsKey(WALL_TILE_GID_SOLID)) tiles.put(WALL_TILE_GID_SOLID, wallTileSolid);

        if (!tiles.containsKey(DOOR_CLOSED_TOP_LEFT_GID)) tiles.put(DOOR_CLOSED_TOP_LEFT_GID, doorClosedTopLeftTile);
        if (!tiles.containsKey(DOOR_CLOSED_TOP_RIGHT_GID)) tiles.put(DOOR_CLOSED_TOP_RIGHT_GID, doorClosedTopRightTile);
        if (!tiles.containsKey(DOOR_CLOSED_BOTTOM_LEFT_GID)) tiles.put(DOOR_CLOSED_BOTTOM_LEFT_GID, doorClosedBottomLeftTile);
        if (!tiles.containsKey(DOOR_CLOSED_BOTTOM_RIGHT_GID)) tiles.put(DOOR_CLOSED_BOTTOM_RIGHT_GID, doorClosedBottomRightTile);

        if (!tiles.containsKey(DOOR_OPEN_TOP_LEFT_GID)) tiles.put(DOOR_OPEN_TOP_LEFT_GID, doorOpenTopLeftTile);
        if (!tiles.containsKey(DOOR_OPEN_TOP_RIGHT_GID)) tiles.put(DOOR_OPEN_TOP_RIGHT_GID, doorOpenTopRightTile);
        if (!tiles.containsKey(DOOR_OPEN_BOTTOM_LEFT_GID)) tiles.put(DOOR_OPEN_BOTTOM_LEFT_GID, doorOpenBottomLeftTile);
        if (!tiles.containsKey(DOOR_OPEN_BOTTOM_RIGHT_GID)) tiles.put(DOOR_OPEN_BOTTOM_RIGHT_GID, doorOpenBottomRightTile);
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza proprietatile dalei.
     */
    public void Update()
    {
    }

    /*!
     * \fn public void Draw(Graphics g, int x, int y, int width, int height, BufferedImage tilesetImage)
     * \brief Deseneaza in fereastra dala, cu dimensiuni specificate (per zoom).
     * \param g Contextul grafic in care sa se realizeze desenarea
     * \param x Coordonata x in cadrul ferestrei unde sa fie desenata dala
     * \param y Coordonata y in cadrul ferestrei unde sa fie desenata dala
     * \param width Latimea la care sa fie desenata dala (scalata)
     * \param height Inaltimea la care sa fie desenata dala (scalata)
     * \param tilesetImage Imaginea tileset-ului din care sa se decupeze.
     */
    public void Draw(Graphics g, int x, int y, int width, int height, BufferedImage tilesetImage)
    {
        BufferedImage tileImage = Assets.getTileImageByGID(this.id, tilesetImage);
        if (tileImage != null) {
            g.drawImage(tileImage, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }

    /*!
     * \fn public void Draw(Graphics g, int x, int y, int width, int height)
     * \brief Metoda simplificata de Draw pentru fog of war, foloseste tileset-ul implicit.
     * \param g Contextul grafic in care sa se realizeze desenarea
     * \param x Coordonata x in cadrul ferestrei unde sa fie desenata dala
     * \param y Coordonata y in cadrul ferestrei unde sa fie desenata dala
     * \param width Latimea la care sa fie desenata dala (scalata)
     * \param height Inaltimea la care sa fie desenata dala (scalata)
     */
    public void Draw(Graphics g, int x, int y, int width, int height)
    {
        Draw(g, x, y, width, height, Assets.jungleTilesetImage);
    }

    /*!
     * \fn public void Draw(Graphics g, int x, int y)
     * \brief Metoda veche de Draw, nu ar trebui sa mai fie folosita direct.
     */
    public void Draw(Graphics g, int x, int y) {
        Draw(g, x, y, TILE_WIDTH, TILE_HEIGHT, Assets.jungleTilesetImage);
    }

    /*!
     * \fn public boolean IsSolid()
     * \brief Returneaza proprietatea de dala solida (supusa coliziunilor) ou nu.
     */
    public boolean IsSolid()
    {
        return false;
    }

    /*!
     * \fn public int GetId()
     * \brief Returneaza GID-ul dalei.
     */
    public int GetId()
    {
        return id;
    }

    /*!
     * \fn public static Tile GetTile(int gid)
     * \brief Returneaza o instanta de Tile pe baza GID-ului.
     * Cauta in HashMap-ul de dale deja inregistrate. Daca nu o gaseste,
     * creaza o noua instanta generica de Tile si o inregistreaza (pentru dale non-specifice).
     */
    public static Tile GetTile(int gid) {
        if (gid == NO_TILE_GID) {
            return GetDefaultTile();
        }
        Tile tile = tiles.get(gid);
        if (tile == null) {
            if (gid == GRASS_TILE_GID_SOLID) {
                return new GrassTile(gid);
            }
            if (gid == WALL_TILE_GID_SOLID) {
                return new WallTile(gid);
            }
            if (gid == DOOR_CLOSED_TOP_LEFT_GID || gid == DOOR_CLOSED_TOP_RIGHT_GID ||
                    gid == DOOR_CLOSED_BOTTOM_LEFT_GID || gid == DOOR_CLOSED_BOTTOM_RIGHT_GID) {
                return new DoorTile(gid, true);
            }
            if (gid == DOOR_OPEN_TOP_LEFT_GID || gid == DOOR_OPEN_TOP_RIGHT_GID ||
                    gid == DOOR_OPEN_BOTTOM_LEFT_GID || gid == DOOR_OPEN_BOTTOM_RIGHT_GID) {
                return new DoorTile(gid, false);
            }

            return new Tile(gid) {};
        }
        return tile;
    }

    /*!
     * \fn public static Tile GetDefaultTile()
     * \brief Returneaza o instanta de dala implicita, non-solida.
     * Utila pentru cazurile cand se cere un tile in afara hartii sau cu GID 0.
     * \return O dala generica, non-solida (cu GID 0).
     */
    public static Tile GetDefaultTile() {
        return new Tile(NO_TILE_GID) {};
    }
}