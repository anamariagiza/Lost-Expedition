package PaooGame.Tiles;

import java.awt.image.BufferedImage;

/*!
 * \class public class SoilTile extends Tile
 * \brief Abstractizeaza notiunea de dala de tip sol/pamant.
 */
public class SoilTile extends Tile
{
    /*!
     * \fn public SoilTile(int id)
     * \brief Constructorul de initializare al clasei
     * \param id Id-ul dalei util in desenarea hartii.
     */
    public SoilTile(int gid)
    {
        super(gid);
    }

    // SoilTile nu suprascrie IsSolid(), deci este non-solid implicit.
}