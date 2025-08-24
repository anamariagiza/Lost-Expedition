package PaooGame.Map;

import PaooGame.Tiles.Tile;
import PaooGame.RefLinks;
import PaooGame.Camera.GameCamera;

import java.awt.*;

/**
 * @class FogOfWar
 * @brief Implementeaza notiunea de "ceata de razboi" (Fog of War) pentru harta jocului.
 * Aceasta clasa gestioneaza doua aspecte ale vizibilitatii:
 * 1. Dezvaluirea permanenta a hartii (`revealedTiles`): Odata ce o zona este
 * explorata, ea ramane vizibila (dar intunecata).
 * 2. Un "cerc de lumina" dinamic in jurul jucatorului, care arata zona
 * vizibila in timp real.
 */
public class FogOfWar {

    /** Referinta catre obiectul RefLinks.*/
    private final RefLinks refLink;
    /** Latimea si inaltimea hartii in numar de dale.*/
    private final int mapWidthTiles;
    private final int mapHeightTiles;
    /** Matrice booleana ce stocheaza daca o dala a fost sau nu descoperita permanent.*/
    private final boolean[][] revealedTiles;

    /** Raza de vizibilitate a jucatorului, masurata in dale.*/
    private static final int VISION_RADIUS_TILES = 5;

    /**
     * @brief Constructorul clasei FogOfWar.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param mapWidthTiles Latimea hartii in numar de dale.
     * @param mapHeightTiles Inaltimea hartii in numar de dale.
     */
    public FogOfWar(RefLinks refLink, int mapWidthTiles, int mapHeightTiles) {
        this.refLink = refLink;
        this.mapWidthTiles = mapWidthTiles;
        this.mapHeightTiles = mapHeightTiles;
        revealedTiles = new boolean[mapWidthTiles][mapHeightTiles];
        // Initial, toate dalele sunt ascunse (false)
        //System.out.println("DEBUG FogOfWar: Initializat cu dimensiunile " + mapWidthTiles + "x" + mapHeightTiles);
    }

    /**
     * @brief Actualizeaza starea de dezvaluire a hartii pe baza pozitiei jucatorului.
     * Aceasta metoda marcheaza dalele din jurul jucatorului ca fiind descoperite
     * permanent in matricea `revealedTiles`.
     */
    public void update() {
        if (refLink.GetPlayer() == null) return;
        int playerTileX = (int) ((refLink.GetPlayer().GetX() + refLink.GetPlayer().GetWidth() / 2) / Tile.TILE_WIDTH);
        int playerTileY = (int) ((refLink.GetPlayer().GetY() + refLink.GetPlayer().GetHeight() / 2) / Tile.TILE_HEIGHT);
        // Ensure player tile coordinates are within bounds
        playerTileX = Math.max(0, Math.min(mapWidthTiles - 1, playerTileX));
        playerTileY = Math.max(0, Math.min(mapHeightTiles - 1, playerTileY));

        for (int yOffset = -VISION_RADIUS_TILES; yOffset <= VISION_RADIUS_TILES; yOffset++) {
            for (int xOffset = -VISION_RADIUS_TILES; xOffset <= VISION_RADIUS_TILES; xOffset++) {
                int checkX = playerTileX + xOffset;
                int checkY = playerTileY + yOffset;

                if (checkX >= 0 && checkX < mapWidthTiles && checkY >= 0 && checkY < mapHeightTiles) {
                    double distance = Math.sqrt(Math.pow(xOffset, 2) + Math.pow(yOffset, 2));
                    if (distance <= VISION_RADIUS_TILES) {
                        revealedTiles[checkX][checkY] = true;
                    }
                }
            }
        }
    }

    /**
     * @brief Randeaza efectul de "cerc de lumina" dinamic in jurul jucatorului.
     * Foloseste un `RadialGradientPaint` pentru a crea un efect de gradient circular
     * care este complet transparent in centrul (pozitia jucatorului) si devine
     * opac (negru) la marginea razei de vizibilitate.
     * @param g Contextul grafic in care se va desena.
     */
    public void render(Graphics g) {
        if (refLink.GetPlayer() == null) return;
        GameCamera camera = refLink.GetGameCamera();
        Graphics2D g2d = (Graphics2D) g.create();

        int playerScreenX = (int) ((refLink.GetPlayer().GetX() - camera.getxOffset()) + refLink.GetPlayer().GetWidth() / 2);
        int playerScreenY = (int) ((refLink.GetPlayer().GetY() - camera.getyOffset()) + refLink.GetPlayer().GetHeight() / 2);
        float radius = (float) (VISION_RADIUS_TILES * Tile.TILE_WIDTH);

        Color transparentBlack = new Color(0, 0, 0, 0);
        Color opaqueBlack = new Color(0, 0, 0, 220);

        float[] dist = {0.0f, 0.7f, 1.0f};
        Color[] colors = {transparentBlack, transparentBlack, opaqueBlack};
        RadialGradientPaint p = new RadialGradientPaint(
                playerScreenX, playerScreenY,
                radius,
                dist,
                colors,
                MultipleGradientPaint.CycleMethod.NO_CYCLE
        );
        g2d.setPaint(p);
        g2d.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
        g2d.dispose();
    }

    /**
     * @brief Verifica daca o dala este vizibila in prezent (in cercul de lumina).
     * @param x Coordonata X a dalei de verificat.
     * @param y Coordonata Y a dalei de verificat.
     * @return True daca dala este in raza de vizibilitate curenta a jucatorului, false altfel.
     */
    public boolean isTileVisible(int x, int y) {
        if (refLink.GetPlayer() == null) return false;
        if (x < 0 || x >= mapWidthTiles || y < 0 || y >= mapHeightTiles) return false;
        int playerTileX = (int) ((refLink.GetPlayer().GetX() + refLink.GetPlayer().GetWidth() / 2) / Tile.TILE_WIDTH);
        int playerTileY = (int) ((refLink.GetPlayer().GetY() + refLink.GetPlayer().GetHeight() / 2) / Tile.TILE_HEIGHT);
        playerTileX = Math.max(0, Math.min(mapWidthTiles - 1, playerTileX));
        playerTileY = Math.max(0, Math.min(mapHeightTiles - 1, playerTileY));

        double distance = Math.sqrt(Math.pow(playerTileX - x, 2) + Math.pow(playerTileY - y, 2));
        return distance <= VISION_RADIUS_TILES;
    }

    /**
     * @brief Verifica daca o dala a fost descoperita permanent (explorata).
     * @param x Coordonata X a dalei de verificat.
     * @param y Coordonata Y a dalei de verificat.
     * @return True daca dala a fost vizitata cel putin o data, false altfel.
     */
    public boolean isTileRevealed(int x, int y) {
        if (x < 0 || x >= mapWidthTiles || y < 0 || y >= mapHeightTiles) {
            return false;
        }
        return revealedTiles[x][y];
    }

    /**
     * @brief Dezvaluie instantaneu intreaga harta. Util pentru debug.
     */
    public void revealAllTiles() {
        for (int x = 0; x < mapWidthTiles; x++) {
            for (int y = 0; y < mapHeightTiles; y++) {
                revealedTiles[x][y] = true;
            }
        }
        //System.out.println("DEBUG FogOfWar: Toate dalele au fost dezvaluite!");
    }

    /**
     * @brief Reseteaza ceata de razboi, ascunzand din nou toata harta.
     */
    public void resetFogOfWar() {
        for (int x = 0; x < mapWidthTiles; x++) {
            for (int y = 0; y < mapHeightTiles; y++) {
                revealedTiles[x][y] = false;
            }
        }
        //System.out.println("DEBUG FogOfWar: Fog of War resetat!");
    }

    /**
     * @brief Returneaza raza de vizibilitate a jucatorului, in dale.
     */
    public int getVisionRadius() {
        return VISION_RADIUS_TILES;
    }

    /**
     * @brief Calculeaza si returneaza procentajul hartii care a fost explorat.
     * @return Procentajul de explorare (0-100).
     */
    public float getExplorationPercentage() {
        int totalTiles = mapWidthTiles * mapHeightTiles;
        int revealedCount = 0;

        for (int x = 0; x < mapWidthTiles; x++) {
            for (int y = 0; y < mapHeightTiles; y++) {
                if (revealedTiles[x][y]) {
                    revealedCount++;
                }
            }
        }

        return (float) revealedCount / totalTiles * 100.0f;
    }
}