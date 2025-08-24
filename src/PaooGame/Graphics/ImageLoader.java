package PaooGame.Graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;


/**
 * @class ImageLoader
 * @brief O clasa utilitara ce contine o metoda statica pentru incarcarea unei imagini in memorie.
 * Rolul acestei clase este de a abstractiza procesul de citire a unui fisier de imagine
 * din resursele proiectului si de a-l converti intr-un obiect BufferedImage.
 */
public class ImageLoader
{
    /**
     * @brief Incarca o imagine dintr-un fisier si returneaza un obiect BufferedImage.
     * @param path Calea relativa catre fisierul imagine, incepand de la radacina folderului de resurse (ex: "/textures/logo.png").
     * @return Un obiect BufferedImage care contine imaginea incarcata, sau null in caz de eroare.
     */
    public static BufferedImage LoadImage(String path)
    {
        URL imageUrl = null;
        try
        {
            //System.out.println("DEBUG: Se incearca incarcarea resursei de la: " + path);
            imageUrl = ImageLoader.class.getResource(path);
            System.out.println("DEBUG: URL rezultat: " + imageUrl);

            if (imageUrl == null) {
                //System.err.println("DEBUG: Resursa NU a fost gasita la calea: " + path + " (URL este null).");
                return null;
            }

            BufferedImage image = ImageIO.read(imageUrl);
            //System.out.println("DEBUG: ImageIO.read() pentru " + path + " a returnat: " + (image != null ? "NON-NULL (" + image.getWidth() + "x" + image.getHeight() + ")" : "NULL"));

            if (image == null) {
                //System.err.println("DEBUG: ImageIO.read() a returnat NULL pentru calea: " + path + ". Fisierul ar putea fi corupt sau nu este o imagine valida.");
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