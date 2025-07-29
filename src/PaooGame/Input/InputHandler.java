package PaooGame.Input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/*! \class public class InputHandler
    \brief Gestioneaza intrarile de la tastatura si mouse.
 */
public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {

    private boolean[] keys; // Starea tastelor (apasate/eliberate)
    private boolean mouseLeftPressed; // Starea butonului stanga al mouse-ului
    private int mouseX, mouseY; // Coordonatele curente ale mouse-ului

    public InputHandler() {
        keys = new boolean[256]; // Pentru codurile ASCII extinse ale tastelor
    }

    // Metode KeyListener
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() < keys.length) {
            keys[e.getKeyCode()] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() < keys.length) {
            keys[e.getKeyCode()] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Nu este folosit in general pentru jocurile in timp real
    }

    // Metode MouseListener
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Butonul stanga al mouse-ului
            mouseLeftPressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseLeftPressed = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Nu este folosit in general pentru jocurile in timp real (folosim pressed/released)
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Nu este folosit
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Nu este folosit
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

    // Metode Getter pentru starea input-ului
    public boolean isKeyDown(int keyCode) {
        if (keyCode < keys.length) {
            return keys[keyCode];
        }
        return false;
    }

    public boolean isMouseLeftPressed() {
        return mouseLeftPressed;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    // Metoda pentru a detecta apasarea unei taste o singura data (nu tinut apasat)
    public boolean isKeyJustPressed(int keyCode) {
        if (keyCode < keys.length) {
            boolean pressed = keys[keyCode];
            return pressed;
        }
        return false;
    }
}