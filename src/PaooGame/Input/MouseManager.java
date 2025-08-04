package PaooGame.Input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/*!
 * \class public class MouseManager
 * \brief Gestioneaza intrarile de la mouse (pozitie, click-uri).
 */
public class MouseManager implements MouseListener, MouseMotionListener {

    private boolean leftPressed;
    private boolean rightPressed;
    private int mouseX, mouseY;

    // NOU: Pentru a detecta un click o singura data (similar cu KeyManager)
    private boolean justLeftPressed;
    private boolean cantLeftClick;

    public MouseManager() {
        leftPressed = false;
        rightPressed = false;
        mouseX = -1;
        mouseY = -1;

        justLeftPressed = false;
        cantLeftClick = false;
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea mouse-ului.
     */
    public void Update() {
        // NOU: Logica pentru justLeftPressed / cantLeftClick
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

    // Getters
    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    /*!
     * \fn public boolean isMouseJustClicked()
     * \brief Verifica daca a avut loc un click recent (o singura data per click stanga).
     * \return True daca s-a detectat un click nou, false altfel.
     */
    public boolean isMouseJustClicked() {
        return justLeftPressed;
    }


    // Metode MouseListener
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Butonul stanga
            leftPressed = true;
        } else if (e.getButton() == MouseEvent.BUTTON3) { // Butonul dreapta
            rightPressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftPressed = false;
            // NOU: Nu mai setam justLeftPressed aici, este gestionat in Update()
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightPressed = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Nu folosim direct mouseClicked
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // Metode MouseMotionListener
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}