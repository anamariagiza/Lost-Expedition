package PaooGame.Map;

import PaooGame.Entities.Player;
import PaooGame.Tiles.Tile;
import PaooGame.RefLinks;

/*!
 * \class public class FogOfWar
 * \brief Implementeaza notiunea de "Fog of War" pentru harta jocului.
 * Controleaza vizibilitatea dalelor pe masura ce jucatorul exploreaza.
 */
public class FogOfWar {

    private RefLinks refLink;
    private int mapWidthTiles;
    private int mapHeightTiles;
    private boolean[][] revealedTiles; // True daca dala a fost descoperita permanent

    private static final int VISION_RADIUS_TILES = 5; // Raza de vizibilitate a jucatorului in dale

    /*!
     * \fn public FogOfWar(RefLinks refLink, int mapWidthTiles, int mapHeightTiles)
     * \brief Constructorul clasei FogOfWar.
     * \param refLink Referinta catre obiectul RefLinks.
     * \param mapWidthTiles Latimea hartii in dale.
     * \param mapHeightTiles Inaltimea hartii in dale.
     */
    public FogOfWar(RefLinks refLink, int mapWidthTiles, int mapHeightTiles) {
        this.refLink = refLink;
        this.mapWidthTiles = mapWidthTiles;
        this.mapHeightTiles = mapHeightTiles;
        revealedTiles = new boolean[mapWidthTiles][mapHeightTiles];
        // Initial, toate dalele sunt ascunse (false)
        System.out.println("DEBUG FogOfWar: Initializat cu dimensiunile " + mapWidthTiles + "x" + mapHeightTiles);
    }

    /*!
     * \fn public void update()
     * \brief Actualizeaza starea Fog of War pe baza pozitiei jucatorului.
     * Marcheaza dalele ca fiind descoperite permanent.
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

    /*!
     * \fn public boolean isTileVisible(int x, int y)
     * \brief Verifica daca o dala este in raza vizuala curenta a jucatorului.
     * \param x Coordonata X a dalei.
     * \param y Coordonata Y a dalei.
     * \return True daca dala este in raza vizuala, false altfel.
     */
    public boolean isTileVisible(int x, int y) {
        if (refLink.GetPlayer() == null) return false;
        if (x < 0 || x >= mapWidthTiles || y < 0 || y >= mapHeightTiles) return false;

        int playerTileX = (int) ((refLink.GetPlayer().GetX() + refLink.GetPlayer().GetWidth() / 2) / Tile.TILE_WIDTH);
        int playerTileY = (int) ((refLink.GetPlayer().GetY() + refLink.GetPlayer().GetHeight() / 2) / Tile.TILE_HEIGHT);

        // Ensure player tile coordinates are within bounds
        playerTileX = Math.max(0, Math.min(mapWidthTiles - 1, playerTileX));
        playerTileY = Math.max(0, Math.min(mapHeightTiles - 1, playerTileY));

        double distance = Math.sqrt(Math.pow(playerTileX - x, 2) + Math.pow(playerTileY - y, 2));

        return distance <= VISION_RADIUS_TILES;
    }

    /*!
     * \fn public boolean isTileRevealed(int x, int y)
     * \brief Verifica daca o dala a fost descoperita permanent (explorata).
     * \param x Coordonata X a dalei.
     * \param y Coordonata Y a dalei.
     * \return True daca dala a fost descoperita, false altfel.
     */
    public boolean isTileRevealed(int x, int y) {
        if (x < 0 || x >= mapWidthTiles || y < 0 || y >= mapHeightTiles) {
            return false;
        }
        return revealedTiles[x][y];
    }

    /*!
     * \fn public void revealAllTiles()
     * \brief Dezvaluie toate dalele (pentru debug sau cheat codes).
     */
    public void revealAllTiles() {
        for (int x = 0; x < mapWidthTiles; x++) {
            for (int y = 0; y < mapHeightTiles; y++) {
                revealedTiles[x][y] = true;
            }
        }
        System.out.println("DEBUG FogOfWar: Toate dalele au fost dezvaluite!");
    }

    /*!
     * \fn public void resetFogOfWar()
     * \brief Reseteaza fog of war-ul (ascunde toate dalele din nou).
     */
    public void resetFogOfWar() {
        for (int x = 0; x < mapWidthTiles; x++) {
            for (int y = 0; y < mapHeightTiles; y++) {
                revealedTiles[x][y] = false;
            }
        }
        System.out.println("DEBUG FogOfWar: Fog of War resetat!");
    }

    /*!
     * \fn public int getVisionRadius()
     * \brief Returneaza raza de vizibilitate a jucatorului.
     */
    public int getVisionRadius() {
        return VISION_RADIUS_TILES;
    }

    /*!
     * \fn public float getExplorationPercentage()
     * \brief Calculeaza procentajul hartii explorat.
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