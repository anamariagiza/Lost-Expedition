package PaooGame.Tiles;

/*!
 * \class public class RockTile extends Tile
 * \brief Abstractizeaza notiunea de dala de tip piatra.
 */
public class RockTile extends Tile {

    /*!
     * \fn public RockTile(int gid)
     * \brief Constructorul de initializare al clasei RockTile.
     * \param gid GID-ul dalei din Tiled.
     */
    public RockTile(int gid) {
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