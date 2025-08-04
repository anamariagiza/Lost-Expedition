package PaooGame.Map;

import PaooGame.Entities.Player;
import PaooGame.Tiles.Tile;

/*!
 * \class public class FogOfWar
 * \brief Implementeaza notiunea de "Fog of War" pentru harta jocului.
 * Controleaza vizibilitatea dalelor pe masura ce jucatorul exploreaza.
 */
public class FogOfWar {

    private int mapWidthTiles;
    private int mapHeightTiles;
    private boolean[][] revealedTiles; // True daca dala a fost descoperita permanent

    // NOU: Nu mai avem currentVisibilityTiles aici, vizibilitatea curenta se calculeaza la desen
    // NOU: Raza de vizibilitate a jucatorului in pixeli
    private static final int VISION_RADIUS_PIXELS = 150; // Raza vizuala a jucatorului in pixeli

    /*!
     * \fn public FogOfWar(int mapWidthTiles, int mapHeightTiles)
     * \brief Constructorul clasei FogOfWar.
     * \param mapWidthTiles Latimea hartii in dale.
     * \param mapHeightTiles Inaltimea hartii in dale.
     */
    public FogOfWar(int mapWidthTiles, int mapHeightTiles) {
        this.mapWidthTiles = mapWidthTiles;
        this.mapHeightTiles = mapHeightTiles;
        revealedTiles = new boolean[mapWidthTiles][mapHeightTiles];
        // Initial, toate dalele sunt ascunse (false)
    }

    /*!
     * \fn public void update(Player player)
     * \brief Actualizeaza starea Fog of War pe baza pozitiei jucatorului.
     * Marcheaza dalele ca fiind descoperite permanent.
     * \param player Referinta catre obiectul Player.
     */
    public void update(Player player) {
        // Coordonatele centrului jucatorului in pixeli in lumea hartii
        int playerCenterX = (int)(player.GetX() + player.GetWidth() / 2);
        int playerCenterY = (int)(player.GetY() + player.GetHeight() / 2);

        // Convertim raza de vizibilitate din pixeli in dale
        int visionRadiusTiles = (int) Math.ceil((double) VISION_RADIUS_PIXELS / Tile.TILE_WIDTH);

        // Iteram prin dalele din jurul jucatorului pentru a le marca ca "descoperite"
        // Facem o iterare mai larga pentru a acoperi intreaga raza vizuala
        for (int yOffset = -visionRadiusTiles -1; yOffset <= visionRadiusTiles + 1; yOffset++) {
            for (int xOffset = -visionRadiusTiles -1; xOffset <= visionRadiusTiles + 1; xOffset++) {
                int checkTileX = playerCenterX / Tile.TILE_WIDTH + xOffset;
                int checkTileY = playerCenterY / Tile.TILE_HEIGHT + yOffset;

                if (checkTileX >= 0 && checkTileX < mapWidthTiles && checkTileY >= 0 && checkTileY < mapHeightTiles) {
                    // Calculam centrul dalei in pixeli
                    int tilePixelCenterX = checkTileX * Tile.TILE_WIDTH + Tile.TILE_WIDTH / 2;
                    int tilePixelCenterY = checkTileY * Tile.TILE_HEIGHT + Tile.TILE_HEIGHT / 2;

                    // Verificam distanta de la centrul jucatorului la centrul dalei
                    double distance = Math.sqrt(Math.pow(playerCenterX - tilePixelCenterX, 2) + Math.pow(playerCenterY - tilePixelCenterY, 2));

                    if (distance <= VISION_RADIUS_PIXELS) {
                        revealedTiles[checkTileX][checkTileY] = true;
                    }
                }
            }
        }
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
     * \fn public int getVisionRadiusPixels()
     * \brief Returneaza raza de vizibilitate a jucatorului in pixeli.
     */
    public int getVisionRadiusPixels() {
        return VISION_RADIUS_PIXELS;
    }
}