package PaooGame.Map;

import PaooGame.Graphics.ImageLoader;
import PaooGame.Tiles.Tile;
import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;

/**
 * @class Map
 * @brief Gestioneaza incarcarea, stocarea si accesul la hartile jocului.
 * Aceasta clasa citeste datele unei harti dintr-un fisier in format TMX,
 * parseaza straturile de dale (layers) si ofera metode pentru a interactiona
 * cu harta, cum ar fi obtinerea unei dale de la o anumita coordonata.
 */
public class Map {
    /** Referinta catre obiectul RefLinks.*/
    private final RefLinks refLink;
    /** Latimea si inaltimea hartii in numar de dale.*/
    private int width, height;
    /** O lista de matrici, fiecare reprezentand un strat (layer) de GID-uri de dale.*/
    private List<int[][]> tilesGidsLayers;
    /** Imaginea tileset-ului corespunzator hartii curente.*/
    private BufferedImage currentMapTilesetImage;
    /** Obiectul care gestioneaza ceata de razboi pentru aceasta harta.*/
    private FogOfWar fogOfWar;

    /**
     * @brief Constructorul clasei Map.
     * @param refLink Referinta catre obiectul RefLinks.
     */
    public Map(RefLinks refLink) {
        this.refLink = refLink;
    }

    /**
     * @brief Incarca o harta dintr-un fisier TMX.
     * @param path Calea catre fisierul .tmx in resurse (ex: "/maps/level_1.tmx").
     */
    public void LoadMapFromFile(String path) {
        if (path.contains("level_1.tmx")) {
            this.currentMapTilesetImage = Assets.jungleTilesetImage;
        } else if (path.contains("level_2.tmx")) {
            this.currentMapTilesetImage = Assets.level2TilesetImage;
        } else if (path.contains("level_3.tmx")) {
            this.currentMapTilesetImage = Assets.level3TilesetImage;
        }
        else {
            this.currentMapTilesetImage = Assets.jungleTilesetImage;
            System.err.println("Avertisment: Nume harta necunoscut, folosind tileset-ul implicit: " + path);
        }

        loadMap(path);
        fogOfWar = new FogOfWar(refLink, width, height);
    }

    /**
     * @brief Actualizeaza starea hartii (in acest caz, doar ceata de razboi).
     */
    public void Update() {
        if (fogOfWar != null) {
            fogOfWar.update();
        }
    }

    /**
     * @brief Deseneaza harta pe ecran. Metoda este goala deoarece randarea se face in GameState.
     * @param g Contextul grafic.
     */
    public void Draw(Graphics g) {
        // Logica de desenare a fost mutata in GameState.java pentru a aplica fog of war-ul corect.
    }

    /**
     * @brief Schimba GID-ul unei dale la coordonatele si stratul specificate.
     * @param x Coordonata X (coloana) a dalei.
     * @param y Coordonata Y (rand) a dalei.
     * @param newGid Noul GID al dalei.
     * @param layerIndex Indexul stratului in care se face modificarea.
     */
    public void changeTileGid(int x, int y, int newGid, int layerIndex) {
        if (layerIndex >= 0 && layerIndex < tilesGidsLayers.size()) {
            if (x >= 0 && x < width && y >= 0 && y < height) {
                tilesGidsLayers.get(layerIndex)[x][y] = newGid;
            }
        }
    }

    /**
     * @brief Returneaza o referinta catre dala de la coordonatele (x, y).
     * @param x Numarul coloanei dalei.
     * @param y Numarul randului dalei.
     * @return Obiectul Tile corespunzator.
     */
    public Tile GetTile(int x, int y) {
        // Verificam daca coordonatele sunt in afara hartii
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return Tile.GetTile(Tile.WALL_TILE_GID_SOLID); // Returneaza un perete solid pentru zonele din afara hartii
        }

        // Cautam dala de pe stratul cel mai de sus (de la ultimul la primul)
        for (int i = tilesGidsLayers.size() - 1; i >= 0; i--) {
            int gid = tilesGidsLayers.get(i)[x][y];

            // Daca gasim o dala care nu este goala (transparenta), o returnam imediat
            if (gid != 0) {
                return Tile.GetTile(gid);
            }
        }

        // Daca toate straturile la aceste coordonate sunt goale, returnam o dala goala, nesolida
        return Tile.GetDefaultTile();
    }

    /**
     * @brief Parseaza un fisier TMX si populeaza structurile de date ale hartii.
     * @param path Calea catre fisierul .tmx.
     */
    private void loadMap(String path) {
        tilesGidsLayers = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputStream is = ImageLoader.class.getResourceAsStream(path);
            if (is == null) {
                throw new IOException("Fisierul de harta nu a fost gasit: " + path);
            }
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            Element mapElement = doc.getDocumentElement();
            width = Integer.parseInt(mapElement.getAttribute("width"));
            height = Integer.parseInt(mapElement.getAttribute("height"));

            NodeList layerNodes = mapElement.getElementsByTagName("layer");
            for (int layerIdx = 0; layerIdx < layerNodes.getLength(); layerIdx++) {
                Element layerElement = (Element) layerNodes.item(layerIdx);
                int[][] currentLayerGids = new int[width][height];

                NodeList dataNodes = layerElement.getElementsByTagName("data");
                if (dataNodes.getLength() > 0) {
                    Element dataElement = (Element) dataNodes.item(0);
                    String encoding = dataElement.getAttribute("encoding");

                    if ("csv".equals(encoding)) {
                        String csvData = dataElement.getTextContent().trim();
                        String[] tileGids = csvData.split("[,\\s]+");

                        int tileIndex = 0;
                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                if (tileIndex < tileGids.length) {
                                    try {
                                        currentLayerGids[x][y] = Integer.parseInt(tileGids[tileIndex]);
                                    } catch (NumberFormatException e) {
                                        System.err.println("Eroare de formatare numar pentru GID-ul dalei: " + tileGids[tileIndex]);
                                        currentLayerGids[x][y] = Tile.NO_TILE_GID;
                                    }
                                } else {
                                    System.err.println("Date insuficiente pentru harta in stratul " + layerIdx + ". Coordonata: [" + x + "," + y + "]");
                                    currentLayerGids[x][y] = Tile.NO_TILE_GID;
                                }
                                tileIndex++;
                            }
                        }
                    } else {
                        System.err.println("Encoding-ul datelor dalei '" + encoding + "' nu este suportat. Suportat doar 'csv'.");
                        System.exit(1);
                    }
                } else {
                    System.err.println("Elementul <data> nu a fost gasit in stratul de harta " + layerIdx + ".");
                    System.exit(1);
                }
                tilesGidsLayers.add(currentLayerGids);
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Eroare la incarcarea sau parsarea hartii din fisierul TMX: " + path);
            System.exit(1);
        }
    }

    /**
     * @brief Returneaza latimea hartii in numar de dale.
     */
    public int GetWidth() {
        return width;
    }

    /**
     * @brief Returneaza inaltimea hartii in numar de dale.
     */
    public int GetHeight() {
        return height;
    }

    /**
     * @brief Returneaza lista de straturi (layers) ale hartii.
     */
    public List<int[][]> getTilesGidsLayers() {
        return tilesGidsLayers;
    }

    /**
     * @brief Returneaza imaginea tileset-ului pentru harta curenta.
     */
    public BufferedImage getCurrentMapTilesetImage() {
        return currentMapTilesetImage;
    }
}