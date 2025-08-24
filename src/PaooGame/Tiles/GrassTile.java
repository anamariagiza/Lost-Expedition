package PaooGame.Tiles;

/**
 * @class GrassTile
 * @brief O dala specializata pentru iarba solida.
 *
 * Reprezinta o dala de tip iarba care blocheaza miscarea entitatilor.
 */
public class GrassTile extends Tile
{
    /**
     * @brief Constructorul clasei GrassTile.
     * @param gid GID-ul dalei din Tiled.
     */
    public GrassTile(int gid)
    {
        super(gid);
    }

    /**
     * @brief Suprascrie metoda IsSolid pentru a indica faptul ca dala este solida.
     * @return Returneaza intotdeauna true.
     */
    @Override
    public boolean IsSolid()
    {
        return true;
    }
}