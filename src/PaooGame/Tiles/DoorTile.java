package PaooGame.Tiles;

/*!
 * \class public class DoorTile extends Tile
 * \brief Abstractizeaza notiunea de dala de tip usa (inchisa sau deschisa).
 */
public class DoorTile extends Tile {

    private boolean isSolid;

    /*!
     * \fn public DoorTile(int gid, boolean isSolid)
     * \brief Constructorul de initializare al clasei DoorTile.
     * \param gid GID-ul dalei din Tiled.
     * \param isSolid Daca este true, dala va fi solida (usa inchisa). Altfel, va fi non-solida (usa deschisa).
     */
    public DoorTile(int gid, boolean isSolid) {
        super(gid);
        this.isSolid = isSolid;
    }

    /*!
     * \fn public boolean IsSolid()
     * \brief Suprascrie metoda IsSolid() din clasa de baza pentru a returna starea de soliditate a usii.
     */
    @Override
    public boolean IsSolid() {
        return isSolid;
    }
}