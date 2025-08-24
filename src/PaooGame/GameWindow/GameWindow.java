package PaooGame.GameWindow;

import javax.swing.*;
import java.awt.*;

/**
 * @class GameWindow
 * @brief Gestioneaza fereastra principala a jocului.
 * * Aceasta clasa este responsabila pentru crearea si configurarea ferestrei (JFrame)
 * in care va fi desenat jocul. Contine, de asemenea, panza (Canvas)
 * pe care se realizeaza randarea efectiva.
 */
public class GameWindow
{
    /** Fereastra principala a jocului (obiectul JFrame).*/
    private JFrame  wndFrame;
    /** Titlul si dimensiunile finale ale ferestrei.*/
    private final String  wndTitle;
    private final int     wndWidth;
    private final int     wndHeight;

    /** Suprafata de desenare (panza) pe care va fi randat jocul.*/
    private Canvas  canvas;

    /** Stocheaza dimensiunile si pozitia ferestrei pentru a putea reveni din modul fullscreen.*/
    private Rectangle originalBounds;

    /**
     * @brief Constructorul clasei GameWindow.
     * @param title Titlul ferestrei.
     * @param width Latimea ferestrei.
     * @param height Inaltimea ferestrei.
     */
    public GameWindow(String title, int width, int height){
        wndTitle    = title;
        wndWidth    = width;
        wndHeight   = height;
        wndFrame    = null;
    }

    /**
     * @brief Construieste si afiseaza fereastra jocului.
     * * Seteaza titlul, dimensiunile, operatia de inchidere si alte proprietati ale JFrame-ului.
     * Creeaza si adauga panza (Canvas) la fereastra.
     */
    public void BuildGameWindow()
    {
        if(wndFrame != null)
        {
            return;
        }
        wndFrame = new JFrame(wndTitle);
        wndFrame.setSize(wndWidth, wndHeight);
        wndFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wndFrame.setResizable(false);
        wndFrame.setLocationRelativeTo(null);
        wndFrame.setVisible(true);
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(wndWidth, wndHeight));
        canvas.setMaximumSize(new Dimension(wndWidth, wndHeight));
        canvas.setMinimumSize(new Dimension(wndWidth, wndHeight));
        wndFrame.add(canvas);
        wndFrame.pack();
        originalBounds = wndFrame.getBounds();
    }

    /**
     * @brief Comuta modul de vizualizare al ferestrei intre fullscreen si fereastra.
     * @param fullScreen true pentru fullscreen, false pentru fereastra.
     */
    public void setFullScreen(boolean fullScreen) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            if (fullScreen) {
                originalBounds = wndFrame.getBounds();
                wndFrame.dispose();
                wndFrame.setUndecorated(true);
                gd.setFullScreenWindow(wndFrame);
                wndFrame.setVisible(true);
            } else {
                wndFrame.dispose();
                wndFrame.setUndecorated(false);
                gd.setFullScreenWindow(null);
                wndFrame.setBounds(originalBounds);
                wndFrame.setVisible(true);
            }
            canvas.setPreferredSize(wndFrame.getContentPane().getSize());
            canvas.setMaximumSize(wndFrame.getContentPane().getSize());
            canvas.setMinimumSize(wndFrame.getContentPane().getSize());
            wndFrame.pack();
        } else {
            System.err.println("Modul fullscreen nu este suportat pe acest dispozitiv.");
        }
    }

    /**
     * @brief Returneaza latimea ferestrei.
     * @return Latimea ferestrei in pixeli.
     */
    public int GetWndWidth()
    {
        return wndFrame.getWidth();
    }

    /**
     * @brief Returneaza inaltimea ferestrei.
     * @return Inaltimea ferestrei in pixeli.
     */
    public int GetWndHeight()
    {
        return wndFrame.getHeight();
    }

    /**
     * @brief Returneaza referinta catre canvas-ul din fereastra pe care se poate desena.
     * @return Obiectul Canvas al ferestrei.
     */
    public Canvas GetCanvas() {
        return canvas;
    }
}