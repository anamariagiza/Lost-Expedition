package PaooGame.Map;

import PaooGame.Graphics.ImageLoader;
import PaooGame.Tiles.Tile;
import PaooGame.RefLinks;
import PaooGame.Camera.GameCamera;
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

/*!
 * \class public class Map
 * \brief Implementeaza notiunea de harta a jocului.
 */
public class Map {
    private RefLinks refLink;
    private int width, height;
    private List<int[][]> tilesGidsLayers;
    private BufferedImage currentMapTilesetImage;
    private FogOfWar fogOfWar;

    public Map(RefLinks refLink) {
        this.refLink = refLink;
    }

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

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea hartii (daca e cazul, pentru animatii etc.).
     */
    public void Update() {
        if (fogOfWar != null) {
            fogOfWar.update();
        }
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza harta pe ecran, ajustand pozitiile cu offset-ul camerei si zoom.
     * \param g Contextul grafic in care sa se realizeze desenarea.
     */
    public void Draw(Graphics g) {
        // Logica de desenare a fost mutată în GameState.java pentru a aplica fog of war-ul corect.
        // Această metodă poate fi lăsată goală sau eliminată complet.
    }

    /*!
     * \fn public void changeTileGid(int x, int y, int newGid, int layerIndex)
     * \brief Schimba GID-ul unei dale la coordonatele specificate in stratul specificat.
     * \param x Coordonata X (coloana) a dalei.
     * \param y Coordonata Y (rand) a dalei.
     * \param newGid Noul GID al dalei.
     * \param layerIndex Indexul stratului in care se face modificarea.
     */
    public void changeTileGid(int x, int y, int newGid, int layerIndex) {
        if (layerIndex >= 0 && layerIndex < tilesGidsLayers.size()) {
            if (x >= 0 && x < width && y >= 0 && y < height) {
                tilesGidsLayers.get(layerIndex)[x][y] = newGid;
            }
        }
    }

    /*!
     * \fn private void loadMap(String path)
     * \brief Incarca harta dintr-un fisier TMX (XML) folosind parsarea manuala.
     * \param path Calea relativa pentru localizarea fisierul .tmx (ex: "/maps/level1.tmx").
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

    /*!
     * \fn public Tile GetTile(int x, int y)
     * \brief Intoarce o referinta catre dala cu numarul de ordine (x, y).
     * \param x Numarul dalei in ordine orizontala.
     * \param y Numarul dalei in ordine verticala.
     */
    public Tile GetTile(int x, int y) {
        // Verificăm dacă coordonatele sunt în afara hărții
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return Tile.GetTile(Tile.WALL_TILE_GID_SOLID); // Returnează un perete solid pentru zonele din afara hărții
        }

        // Căutăm dala de pe stratul cel mai de sus (de la ultimul la primul)
        for (int i = tilesGidsLayers.size() - 1; i >= 0; i--) {
            int gid = tilesGidsLayers.get(i)[x][y];

            // Dacă găsim o dală care nu este goală (transparentă), o returnăm imediat
            if (gid != 0) {
                return Tile.GetTile(gid);
            }
        }

        // Dacă toate straturile la aceste coordonate sunt goale, returnăm o dală goală, nesolidă
        return Tile.GetDefaultTile();
    }

    /*!
     * \fn public int GetWidth()
     * \brief Returneaza latimea hartii.
     */
    public int GetWidth() {
        return width;
    }

    /*!
     * \fn public int GetHeight()
     * \brief Returneaza inaltimea hartii.
     */
    public int GetHeight() {
        return height;
    }

    /*!
     * \fn public List<int[][]> getTilesGidsLayers()
     * \brief Returneaza toate straturile de GID-uri ale hartii.
     */
    public List<int[][]> getTilesGidsLayers() {
        return tilesGidsLayers;
    }

    /*!
     * \fn public BufferedImage getCurrentMapTilesetImage()
     * \brief Returneaza imaginea tileset-ului principal pentru harta curenta.
     */
    public BufferedImage getCurrentMapTilesetImage() {
        return currentMapTilesetImage;
    }
}