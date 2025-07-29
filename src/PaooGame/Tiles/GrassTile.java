package PaooGame.Tiles;

// import PaooGame.Graphics.Assets; // Nu mai este strict necesar in constructorul tile-ului specific

/*! \class public class GrassTile extends Tile
    \brief Abstractizeaza notiunea de dala de tip iarba.
 */
public class GrassTile extends Tile
{
    /*! \fn public GrassTile(int id)
        \brief Constructorul de initializare al clasei

        \param id Id-ul dalei util in desenarea hartii.
     */
    public GrassTile(int gid)
    {
        super(gid);
    }
    // GrassTile nu suprascrie IsSolid(), deci este non-solid implicit.
}