package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/*! \class public class SpriteSheet
    \brief Clasa retine o referinta catre o imagine formata din dale (sprite sheet)

    Metoda crop() returneaza o dala de dimensiuni fixe (o subimagine) din sprite sheet
    de la adresa (x * latimeDala, y * inaltimeDala)
 */
public class SpriteSheet
{
    private BufferedImage       spriteSheet;              /*!< Referinta catre obiectul BufferedImage ce contine sprite sheet-ul.*/
    // !!! ACESTE VALORI TREBUIE SA CORESPUNDA EXACT CU DIMENSIUNEA REALĂ A UNEI DALE DIN gentle forest.png !!!
    // Si, de asemenea, trebuie sa se potriveasca cu "Tile Width" si "Tile Height" din Tiled Editor.
    private static final int    tileWidth   = 48;   /*!< Latimea unei dale din sprite sheet.*/
    private static final int    tileHeight  = 48;   /*!< Inaltime unei dale din sprite sheet.*/

    /*! \fn public SpriteSheet(BufferedImage sheet)
        \brief Constructor, initializeaza spriteSheet.

        \param img Un obiect BufferedImage valid.
     */
    public SpriteSheet(BufferedImage buffImg)
    {
        /// Retine referinta catre BufferedImage object.
        spriteSheet = buffImg;
    }

    /*! \fn public BufferedImage crop(int x, int y)
        \brief Returneaza un obiect BufferedImage ce contine o subimage (dala).

        Subimaginea este regasita in sprite sheet specificad coltul stanga sus
        al imaginii si apoi latimea si inaltimea (totul in pixeli). Coltul din stanga sus al imaginii
        se obtine inmultind numarul de ordine al dalei cu dimensiunea in pixeli a unei dale.
     */
    public BufferedImage crop(int x, int y)
    {
        if (spriteSheet == null) {
            System.err.println("Eroare: Nu se poate decupa din spriteSheet null.");
            return null;
        }
        int cropX = x * tileWidth;
        int cropY = y * tileHeight;
        if (cropX < 0 || cropY < 0 || cropX + tileWidth > spriteSheet.getWidth() || cropY + tileHeight > spriteSheet.getHeight()) {
            System.err.println("ATENTIE (SpriteSheet.crop): Zona de decupare depaseste limitele sprite sheet-ului! x=" + x + ", y=" + y + ", cropX=" + cropX + ", cropY=" + cropY + ", Sheet Dim: " + spriteSheet.getWidth() + "x" + spriteSheet.getHeight() + ".");
            return null;
        }
        return spriteSheet.getSubimage(cropX, cropY, tileWidth, tileHeight);
    }

    /*! \fn public BufferedImage getSpriteSheet()
        \brief Returneaza referinta catre intregul obiect BufferedImage ce contine sprite sheet-ul.
     */
    public BufferedImage getSpriteSheet() {
        return spriteSheet;
    }

    /*! \fn public static int getTileWidth()
        \brief Returneaza latimea unei dale.
     */
    public static int getTileWidth() {
        return tileWidth;
    }

    /*! \fn public static int getTileHeight()
        \brief Returneaza inaltimea unei dale.
     */
    public static int getTileHeight() {
        return tileHeight;
    }
}