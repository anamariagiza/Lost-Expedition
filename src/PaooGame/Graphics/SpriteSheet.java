package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/**
 * @class SpriteSheet
 * @brief Gestioneaza o imagine de tip foaie de sprite-uri (spritesheet).
 * Aceasta clasa retine o referinta catre o imagine mare care contine
 * mai multe imagini mai mici (dale, cadre de animatie) aranjate intr-o grila.
 * Ofera o metoda simpla, `crop`, pentru a extrage o singura imagine (dala)
 * din aceasta grila pe baza coordonatelor ei (rand si coloana).
 */
public class SpriteSheet
{
    /** Referinta catre obiectul BufferedImage ce contine foaia de sprite-uri.*/
    private final BufferedImage       spriteSheet;

    /** Latimea si inaltimea fixa a unei dale in pixeli.*/
    private static final int    tileWidth   = 48;
    private static final int    tileHeight  = 48;

    /**
     * @brief Constructorul clasei SpriteSheet.
     * @param buffImg Un obiect BufferedImage valid care contine intreaga foaie de sprite-uri.
     */
    public SpriteSheet(BufferedImage buffImg)
    {
        spriteSheet = buffImg;
    }

    /**
     * @brief Extrage o singura imagine (dala) din foaia de sprite-uri.
     * @param x Indexul coloanei de unde se va decupa dala (incepand de la 0).
     * @param y Indexul randului de unde se va decupa dala (incepand de la 0).
     * @return Un obiect BufferedImage continand dala decupata, sau null daca decuparea esueaza.
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
        // Metoda getSubimage() extrage o sub-imagine dreptunghiulara.
        return spriteSheet.getSubimage(cropX, cropY, tileWidth, tileHeight);
    }

    /**
     * @brief Returneaza referinta catre intreaga foaie de sprite-uri.
     * @return Obiectul BufferedImage al foii de sprite-uri.
     */
    public BufferedImage getSpriteSheet() {
        return spriteSheet;
    }

    /**
     * @brief Returneaza latimea standard a unei dale.
     * @return Latimea dalei in pixeli.
     */
    public static int getTileWidth() {
        return tileWidth;
    }

    /**
     * @brief Returneaza inaltimea standard a unei dale.
     * @return Inaltimea dalei in pixeli.
     */
    public static int getTileHeight() {
        return tileHeight;
    }
}