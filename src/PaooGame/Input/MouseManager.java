package PaooGame.Input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @class MouseManager
 * @brief Gestioneaza si proceseaza input-ul de la mouse.
 * Aceasta clasa implementeaza interfetele MouseListener si MouseMotionListener
 * pentru a capta toate evenimentele relevante ale mouse-ului, cum ar fi apasarile
 * de butoane si miscarea cursorului. Ofera o logica "just clicked" pentru a
 * detecta clicuri unice, esentiala pentru interactiunea cu butoanele din meniuri.
 */
public class MouseManager implements MouseListener, MouseMotionListener {

    /** Flag ce indica daca butonul stang/drept este tinut apasat.*/
    private boolean leftPressed;
    private boolean rightPressed;

    /** Coordonatele curente ale cursorului mouse-ului.*/
    private int mouseX, mouseY;

    /** Flag-uri pentru a gestiona logica de "un singur clic".*/
    private boolean justLeftPressed;
    private boolean cantLeftClick;

    /**
     * @brief Constructorul clasei MouseManager.
     */
    public MouseManager() {
        leftPressed = false;
        rightPressed = false;
        mouseX = -1;
        mouseY = -1;

        justLeftPressed = false;
        cantLeftClick = false;
    }

    /**
     * @brief Metoda apelata de sistem la apasarea unui buton al mouse-ului.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Butonul stanga
            leftPressed = true;
        } else if (e.getButton() == MouseEvent.BUTTON3) { // Butonul dreapta
            rightPressed = true;
        }
    }

    /**
     * @brief Metoda apelata de sistem la eliberarea unui buton al mouse-ului.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftPressed = false;
            /* Nu mai setam justLeftPressed aici, este gestionat in Update() */
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightPressed = false;
        }
    }

    /**
     * @brief Metoda apelata de sistem la miscarea mouse-ului cu un buton apasat.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /**
     * @brief Metoda apelata de sistem la miscarea mouse-ului fara butoane apasate.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /**
     * @brief Metode Listener Neutilizate
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // Nu folosim direct mouseClicked
    }

    /**
     * @brief Actualizeaza starea managerului de mouse. TREBUIE apelata o data pe cadru.
     * Aceasta metoda implementeaza logica "just clicked". Functioneaza ca un
     * mecanism de debounce pentru a se asigura ca o singura apasare a mouse-ului
     * este inregistrata ca un singur eveniment de clic in logica jocului.
     */
    public void Update() {
        if (cantLeftClick && !leftPressed) {
            cantLeftClick = false;
        } else if (justLeftPressed) {
            cantLeftClick = true;
            justLeftPressed = false;
        }
        if (leftPressed && !cantLeftClick) {
            justLeftPressed = true;
        }
    }

    /**
     * @brief Verifica daca butonul stang al mouse-ului este tinut apasat.
     */
    public boolean isLeftPressed() {
        return leftPressed;
    }

    /**
     * @brief Verifica daca butonul drept al mouse-ului este tinut apasat.
     */
    public boolean isRightPressed() {
        return rightPressed;
    }

    /**
     * @brief Returneaza coordonata X curenta a mouse-ului.
     */
    public int getMouseX() {
        return mouseX;
    }

    /**
     * @brief Returneaza coordonata Y curenta a mouse-ului.
     */
    public int getMouseY() {
        return mouseY;
    }

    /**
     * @brief Verifica daca butonul stang al mouse-ului a fost apasat exact in acest cadru.
     * @return True daca a fost un clic nou, false altfel.
     */
    public boolean isMouseJustClicked() {
        return justLeftPressed;
    }

    /**
     * @brief Metoda apelata cand cursorul mouse-ului intra in componenta.
     * Neutilizata in acest proiect.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * @brief Metoda apelata cand cursorul mouse-ului iese din componenta.
     * Neutilizata in acest proiect.
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

}