package PaooGame.Tiles;

/*!
 * \class public class WallTile extends Tile
 * \brief Abstractizeaza notiunea de dala de tip perete.
 */
public class WallTile extends Tile {

    /*!
     * \fn public WallTile(int gid)
     * \brief Constructorul de initializare al clasei WallTile.
     * \param gid GID-ul dalei din Tiled.
     */
    public WallTile(int gid) {
        super(gid);
    }

    /*!
     * \fn public boolean IsSolid()
     * \brief Suprascrie metoda IsSolid() din clasa de baza pentru a returna intotdeauna true.
     */
    @Override
    public boolean IsSolid() {
        return true;
    }
}