package PaooGame.Tiles;

import java.awt.image.BufferedImage;

/*!
 * \class public class GrassTile extends Tile
 * \brief Abstractizeaza notiunea de dala de tip iarba.
 */
public class GrassTile extends Tile
{
    /*!
     * \fn public GrassTile(int id)
     * \brief Constructorul de initializare al clasei
     * \param id Id-ul dalei util in desenarea hartii.
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
        return true; // Iarba este SOLIDĂ conform cerințelor actuale
    }
}