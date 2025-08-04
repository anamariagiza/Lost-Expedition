package PaooGame.GameWindow;

import javax.swing.*;
import java.awt.*;

/*!
 * \class GameWindow
 * \brief Implementeaza notiunea de fereastra a jocului.
 * Membrul wndFrame este un obiect de tip JFrame care va avea utilitatea unei
 * ferestre grafice si totodata si cea a unui container (toate elementele
 * grafice vor fi continute de fereastra).
 */
public class GameWindow
{
    private JFrame  wndFrame;
    /*!< fereastra principala a jocului*/
    private String  wndTitle;
    /*!< titlul ferestrei*/
    private int     wndWidth;
    /*!< latimea ferestrei in pixeli*/
    private int     wndHeight;
    /*!< inaltimea ferestrei in pixeli*/

    private Canvas  canvas;
    /*!< "panza/tablou" in care se poate desena*/

    private Rectangle originalBounds;

    /*!
     * \fn GameWindow(String title, int width, int height)
     * \brief Constructorul cu parametri al clasei GameWindow
     * Retine proprietatile ferestrei proprietatile (titlu, latime, inaltime)
     * in variabilele membre deoarece vor fi necesare pe parcursul jocului.
     * Crearea obiectului va trebui urmata de crearea ferestrei propriuzise
     * prin apelul metodei BuildGameWindow()
     * \param title Titlul ferestrei.
     * \param width Latimea ferestrei in pixeli.
     * \param height Inaltimea ferestrei in pixeli.
     */
    public GameWindow(String title, int width, int height){
        wndTitle    = title;
        wndWidth    = width;
        wndHeight   = height;
        wndFrame    = null;
    }

    /*!
     * \fn private void BuildGameWindow()
     * \brief Construieste/creaza fereastra si seteaza toate proprietatile
     * necesare: dimensiuni, pozitionare in centrul ecranului, operatia de
     * inchidere, invalideaza redimensionarea ferestrei, afiseaza fereastra.
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
        wndFrame.setResizable(true);
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

    /*!
     * \fn public void setFullScreen(boolean fullScreen)
     * \brief Comuta modul de vizualizare al ferestrei intre fullscreen si fereastra.
     * \param fullScreen true pentru fullscreen, false pentru fereastra.
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

    /*!
     * \fn public int GetWndWidth()
     * \brief Returneaza latimea ferestrei.
     */
    public int GetWndWidth()
    {
        return wndFrame.getWidth();
    }

    /*!
     * \fn public int GetWndHeight()
     * \brief Returneaza inaltimea ferestrei.
     */
    public int GetWndHeight()
    {
        return wndFrame.getHeight();
    }

    /*!
     * \fn public Canvas GetCanvas()
     * \brief Returneaza referinta catre canvas-ul din fereastra pe care se poate desena.
     */
    public Canvas GetCanvas() {
        return canvas;
    }
}