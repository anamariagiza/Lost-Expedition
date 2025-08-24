package PaooGame.Tiles;

/**
 * @class RockTile
 * @brief O dala specializata pentru piatra/stanca solida.
 * Reprezinta o dala de tip piatra care blocheaza miscarea entitatilor.
 */
public class RockTile extends Tile {

    /**
     * @brief Constructorul clasei RockTile.
     * @param gid GID-ul dalei din Tiled.
     */
    public RockTile(int gid) {
        super(gid);
    }

    /**
     * @brief Suprascrie metoda IsSolid pentru a indica faptul ca dala este solida.
     * @return Returneaza intotdeauna true.
     */
    @Override
    public boolean IsSolid() {
        return true;
    }
}