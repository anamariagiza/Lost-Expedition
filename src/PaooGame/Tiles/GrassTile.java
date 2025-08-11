package PaooGame.Tiles;

/*!
 * \class public class GrassTile extends Tile
 * \brief Abstractizeaza notiunea de dala de tip iarba.
 */
public class GrassTile extends Tile
{
    /*!
     * \fn public GrassTile(int gid)
     * \brief Constructorul de initializare al clasei
     * \param gid Id-ul dalei util in desenarea hartii.
     */
    public GrassTile(int gid)
    {
        super(gid);
    }

    /*!
     * \fn public boolean IsSolid()
     * \brief Suprascrie metoda IsSolid() din clasa de baza in sensul ca va fi luat in calcul in caz de coliziune.
     */
    @Override
    public boolean IsSolid()
    {
        return true;
    }
}