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
        fogOfWar = new FogOfWar(width, height);
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea hartii (daca e cazul, pentru animatii etc.).
     */
    public void Update() {
        if (fogOfWar != null && refLink.GetPlayer() != null) {
            fogOfWar.update(refLink.GetPlayer());
        }
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza harta pe ecran, ajustand pozitiile cu offset-ul camerei si zoom.
     * NOU: Harta este desenata in intregime, iar Fog of War este desenat deasupra de GameState.
     * \param g Contextul grafic in care sa se realizeze desenarea.
     */
    public void Draw(Graphics g) {
        if (tilesGidsLayers == null || tilesGidsLayers.isEmpty() || currentMapTilesetImage == null) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
            System.err.println("Eroare: Tileset-ul hartii curente este null sau straturile lipsesc. Nu se poate desena harta.");
            return;
        }

        GameCamera camera = refLink.GetGameCamera();
        float zoom = camera.getZoomLevel();
        int xStart = (int) Math.max(0, camera.getxOffset() / Tile.TILE_WIDTH);
        int xEnd = (int) Math.min(width, (camera.getxOffset() + (refLink.GetWidth() / zoom)) / Tile.TILE_WIDTH + 1);
        int yStart = (int) Math.max(0, camera.getyOffset() / Tile.TILE_HEIGHT);
        int yEnd = (int) Math.min(height, (camera.getyOffset() + (refLink.GetHeight() / zoom)) / Tile.TILE_HEIGHT + 1);

        for (int[][] currentLayerGids : tilesGidsLayers) {
            for (int y = yStart; y < yEnd; y++) {
                for (int x = xStart; x < xEnd; x++) {
                    int gid = currentLayerGids[x][y];
                    if (gid == 0) {
                        continue;
                    }

                    Tile tile = Tile.GetTile(gid);
                    int drawX = (int)((x * Tile.TILE_WIDTH - camera.getxOffset()) * zoom);
                    int drawY = (int)((y * Tile.TILE_HEIGHT - camera.getyOffset()) * zoom);
                    int scaledTileWidth = (int)(Tile.TILE_WIDTH * zoom);
                    int scaledTileHeight = (int)(Tile.TILE_HEIGHT * zoom);

                    if (tile != null) {
                        // Desenam dala normal, fara a aplica aici logica Fog of War
                        tile.Draw(g, drawX, drawY, scaledTileWidth, scaledTileHeight, currentMapTilesetImage);
                    } else {
                        g.setColor(Color.RED);
                        g.fillRect(drawX, drawY, scaledTileWidth, scaledTileHeight);
                        System.err.println("DEBUG: Dala cu GID " + gid + " nu a putut fi incarcata sau este invalida din tileset-ul: " + currentMapTilesetImage.toString());
                    }
                }
            }
        }
        // NOU: Masca de Fog of War nu mai e desenata aici. GameState va face asta.
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
     * \brief Returneaza dala de la coordonatele specificate din primul strat (stratul de baza).
     * Este folosita pentru verificarea coliziunilor.
     * \param x Coordonata X (coloana) a dalei.
     * \param y Coordonata Y (rand) a dalei.
     * \return Obiectul Tile de la pozitia specificata, sau o dala solida implicita daca coordonatele sunt invalide.
     */
    public Tile GetTile(int x, int y) {
        if (tilesGidsLayers == null || tilesGidsLayers.isEmpty()) {
            return Tile.GetDefaultTile();
        }

        int[][] baseLayerGids = tilesGidsLayers.get(0);

        if (x < 0 || y < 0 || x >= width || y >= height) {
            return Tile.grassTileSolid;
        }

        int gid = baseLayerGids[x][y];
        return Tile.GetTile(gid);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /*!
     * \fn public FogOfWar getFogOfWar()
     * \brief Returneaza instanta de FogOfWar pentru harta.
     */
    public FogOfWar getFogOfWar() { // NOU: Getter pentru FogOfWar
        return fogOfWar;
    }
}