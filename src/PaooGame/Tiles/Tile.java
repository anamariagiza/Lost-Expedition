package PaooGame.Tiles;

import PaooGame.Graphics.Assets;
import PaooGame.Graphics.SpriteSheet;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/*! \class public class Tile
    \brief Retine toate dalele intr-un vector si ofera posibilitatea regasirii dupa un id.
 */
public class Tile
{
    public static final Map<Integer, Tile> tiles = new HashMap<>();

    public static final int NO_TILE_GID = 0;

    // GID-uri pentru dalele specifice.
    // !!! ACESTEA TREBUIE SA CORESPUNDA CU GID-urile reale din TILED EDITOR pentru tileset-ul tau "gentle forest.png" !!!
    // Ex: Daca primul tile (sus-stanga) din gentle forest.png este iarba si Tiled ii da GID 1, atunci GRASS_TILE_GID = 1.
    // Verifica-le manual in Tiled!
    public static final int GRASS_TILE_GID = 1;
    public static final int MOUNTAIN_TILE_GID = 2;
    public static final int WATER_TILE_GID = 3;
    public static final int TREE_TILE_GID = 4;
    public static final int SOIL_TILE_GID = 5;


    public static Tile grassTile;
    public static Tile mountainTile;
    public static Tile waterTile;
    public static Tile treeTile;
    public static Tile soilTile;

    public static final int TILE_WIDTH  = SpriteSheet.getTileWidth();
    public static final int TILE_HEIGHT = SpriteSheet.getTileHeight();

    protected final int id;

    /*! \fn public Tile(int gid)
        \brief Constructorul aferent clasei.

        \param gid Global ID-ul dalei din Tiled.
     */
    public Tile(int gid)
    {
        this.id = gid;
        if (tiles.containsKey(gid)) {
            // System.err.println("Avertisment: Se incearca inregistrarea unei dale cu GID-ul " + gid + " care exista deja!");
        }
        tiles.put(gid, this);
    }

    /*! \fn public static void InitTiles()
        \brief Metoda statica pentru a initializa toate tipurile de dale.
        Aceasta ar trebui apelata o singura data, dupa Assets.LoadGameAssets().
     */
    public static void InitTiles() {
        grassTile = new GrassTile(GRASS_TILE_GID);
        mountainTile = new MountainTile(MOUNTAIN_TILE_GID);
        waterTile = new WaterTile(WATER_TILE_GID);
        treeTile = new TreeTile(TREE_TILE_GID);
        soilTile = new SoilTile(SOIL_TILE_GID);
    }


    /*! \fn public void Update()
        \brief Actualizeaza proprietatile dalei.
     */
    public void Update()
    {

    }

    /*! \fn public void Draw(Graphics g, int x, int y, int width, int height)
        \brief Deseneaza in fereastra dala, cu dimensiuni specificate (pentru zoom).
        \param g Contextul grafic in care sa se realizeze desenarea
        \param x Coordonata x in cadrul ferestrei unde sa fie desenata dala
        \param y Coordonata y in cadrul ferestrei unde sa fie desenata dala
        \param width Latimea la care sa fie desenata dala (scalata)
        \param height Inaltimea la care sa fie desenata dala (scalata)
     */
    public void Draw(Graphics g, int x, int y, int width, int height)
    {
        BufferedImage tileImage = Assets.getTileImageByGID(this.id);
        if (tileImage != null) {
            g.drawImage(tileImage, x, y, width, height, null);
        } else {
            // Desenam un patrat rosu pentru dalele care nu au imagine.
            // Acest lucru ar trebui să te ajute să identifici GID-urile problematice.
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
            // Poți activa următorul System.err.println dacă vrei să vezi GID-urile specifice care dau erori,
            // dar va umple consola rapid.
            System.err.println("DEBUG: Dala cu GID " + this.id + " nu are imagine sau decuparea a esuat."); //
        }
    }

    /*! \fn public void Draw(Graphics g, int x, int y)
        \brief Deseneaza in fereastra dala cu dimensiunile implicite (TILE_WIDTH, TILE_HEIGHT).
        Aceasta metoda este de fallback si ar trebui preferabil sa folosesti Draw cu latime/inaltime explicite.
        \param g Contextul grafic in care sa se realizeze desenarea
        \param x Coordonata x in cadrul ferestrei unde sa fie desenata dala
        \param y Coordonata y in cadrul ferestrei unde sa fie desenata dala
     */
    public void Draw(Graphics g, int x, int y) {
        Draw(g, x, y, TILE_WIDTH, TILE_HEIGHT);
    }


    /*! \fn public boolean IsSolid()
        \brief Returneaza proprietatea de dala solida (supusa coliziunilor) sau nu.
     */
    public boolean IsSolid()
    {
        return false; // Implicit, dalele nu sunt solide
    }

    /*! \fn public int GetId()
        \brief Returneaza GID-ul dalei.
     */
    public int GetId()
    {
        return id;
    }

    /*! \fn public static Tile GetTile(int gid)
        \brief Returneaza o instanta de Tile pe baza GID-ului.
        Cauta in HashMap-ul de dale deja inregistrate. Daca nu o gaseste,
        creaza o noua instanta generica de Tile si o inregistreaza (pentru dale non-specifice).
     */
    public static Tile GetTile(int gid) {
        if (gid == NO_TILE_GID) {
            return null; // Dala goala
        }
        Tile tile = tiles.get(gid);
        if (tile == null) {
            tile = new Tile(gid);
        }
        return tile;
    }
}