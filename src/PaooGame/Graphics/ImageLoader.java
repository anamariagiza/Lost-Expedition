package PaooGame.Graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/*! \class public class ImageLoader
    \brief Clasa ce contine o metoda statica pentru incarcarea unei imagini in memorie.
 */
public class ImageLoader
{
    /*! \fn  public static BufferedImage loadImage(String path)
        \brief Incarca o imagine intr-un obiect BufferedImage si returneaza o referinta catre acesta.

        \param path Calea relativa pentru localizarea fisierul imagine.
     */
    public static BufferedImage LoadImage(String path)
    {
        URL imageUrl = null;
        try
        {
            System.out.println("DEBUG: Se incearca incarcarea resursei de la: " + path);
            imageUrl = ImageLoader.class.getResource(path);
            System.out.println("DEBUG: URL rezultat: " + imageUrl);

            if (imageUrl == null) {
                System.err.println("DEBUG: Resursa NU a fost gasita la calea: " + path + " (URL este null).");
                return null;
            }

            BufferedImage image = ImageIO.read(imageUrl);
            // --- ADAUGA ACEASTA LINIE NOUA ---
            System.out.println("DEBUG: ImageIO.read() pentru " + path + " a returnat: " + (image != null ? "NON-NULL (" + image.getWidth() + "x" + image.getHeight() + ")" : "NULL"));
            // --- SFARSIT LINIE NOUA ---

            if (image == null) {
                System.err.println("DEBUG: ImageIO.read() a returnat NULL pentru calea: " + path + ". Fisierul ar putea fi corupt sau nu este o imagine valida.");
            }
            return image;

        }
        catch(IOException e)
        {
            System.err.println("Eroare IOException la incarcarea imaginii: " + path + ". Mesaj: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Eroare generala la incarcarea imaginii: " + path + ". Mesaj: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}