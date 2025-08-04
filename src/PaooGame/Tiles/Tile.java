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
    public static final int GRASS_TILE_GID_SOLID = 82; // GID-ul real al ierbii tale (82) care este SOLID
    // NOU: Acestea sunt acum doar pentru referință, IsSolid() va fi 'false' pentru ele
    public static final int MOUNTAIN_TILE_GID = 2;
    public static final int WATER_TILE_GID = 3;
    public static final int TREE_TILE_GID = 4;
    public static final int SOIL_TILE_GID = 5;


    public static final int TILE_WIDTH  = SpriteSheet.getTileWidth();
    public static final int TILE_HEIGHT = SpriteSheet.getTileHeight();

    // Referinte la instante de dale
    public static Tile grassTileSolid;
    public static Tile soilTile;
    public static Tile mountainTile;
    public static Tile waterTile;
    public static Tile treeTile;


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
        soilTile = new SoilTile(SOIL_TILE_GID);
        mountainTile = new MountainTile(MOUNTAIN_TILE_GID);
        waterTile = new WaterTile(WATER_TILE_GID);
        treeTile = new TreeTile(TREE_TILE_GID);

        if (!tiles.containsKey(GRASS_TILE_GID_SOLID)) tiles.put(GRASS_TILE_GID_SOLID, grassTileSolid);
        if (!tiles.containsKey(SOIL_TILE_GID)) tiles.put(SOIL_TILE_GID, soilTile);
        if (!tiles.containsKey(MOUNTAIN_TILE_GID)) tiles.put(MOUNTAIN_TILE_GID, mountainTile);
        if (!tiles.containsKey(WATER_TILE_GID)) tiles.put(WATER_TILE_GID, waterTile);
        if (!tiles.containsKey(TREE_TILE_GID)) tiles.put(TREE_TILE_GID, treeTile);
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
        // Dalele generice sunt non-solide implicit.
        // Sub-clasele GrassTile, MountainTile, WaterTile, TreeTile vor suprascrie aceasta metoda.
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
            // NOU: Daca dala nu este inregistrata explicit, dar e GID-ul de iarba (82), o facem solida.
            if (gid == GRASS_TILE_GID_SOLID) {
                return new GrassTile(gid); // Este solid
            }
            // Toate celelalte GID-uri care nu au fost definite explicit si nu sunt GID 82
            // vor fi tratate ca dale generice NON-SOLIDE.
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