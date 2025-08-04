package PaooGame.Tiles;

import java.awt.image.BufferedImage;

/*!
 * \class public class MountainTile extends Tile
 * \brief Abstractizeaza notiunea de dala de tip munte sau piatra.
 */
public class MountainTile extends Tile {

    /*!
     * \fn public MountainTile(int id)
     * \brief Constructorul de initializare al clasei
     * \param id Id-ul dalei util in desenarea hartii.
     */
    public MountainTile(int gid)
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
        return false; // NOU: Muntele este NON-SOLID conform cerințelor actuale
    }
}