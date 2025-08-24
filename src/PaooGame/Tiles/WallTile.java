package PaooGame.Tiles;

/**
 * @class WallTile
 * @brief O dala specializata pentru pereti.
 * Reprezinta o dala de tip perete care blocheaza miscarea entitatilor.
 */
public class WallTile extends Tile {

    /**
     * @brief Constructorul clasei WallTile.
     * @param gid GID-ul dalei din Tiled.
     */
    public WallTile(int gid) {
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