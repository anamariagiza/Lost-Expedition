package PaooGame.Input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {
    public boolean up, down, left, right, enter, space, escape;
    public boolean z;
    public boolean shift;
    public boolean eKey; // NOU: variabila pentru tasta E
    public boolean nKey; // NOU: variabila pentru tasta N
    public boolean pKey; // NOU: variabila pentru tasta P
    public boolean[] keys;
    private boolean[] justPressed;
    private boolean[] cantPress;

    public KeyManager() {
        keys = new boolean[256];
        justPressed = new boolean[keys.length];
        cantPress = new boolean[keys.length];
    }

    public void Update() {
        // Logica pentru justPressed / cantPress
        for (int i = 0; i < keys.length; i++) {
            if (cantPress[i] && !keys[i]) {
                cantPress[i] = false;
            } else if (justPressed[i]) {
                cantPress[i] = true;
                justPressed[i] = false;
            }
            if (keys[i] && !cantPress[i]) {
                justPressed[i] = true;
            }
        }

        up = keys[KeyEvent.VK_W];
        down = keys[KeyEvent.VK_S];
        left = keys[KeyEvent.VK_A];
        right = keys[KeyEvent.VK_D];
        enter = keys[KeyEvent.VK_ENTER];
        space = keys[KeyEvent.VK_SPACE];
        escape = keys[KeyEvent.VK_ESCAPE];
        z = keys[KeyEvent.VK_Z];
        shift = keys[KeyEvent.VK_SHIFT];
        eKey = keys[KeyEvent.VK_E];
        nKey = keys[KeyEvent.VK_N];
        pKey = keys[KeyEvent.VK_P];
    }

    public boolean isKeyJustPressed(int keyCode) {
        if (keyCode < 0 || keyCode >= keys.length)
            return false;
        return justPressed[keyCode];
    }

    // NOU: Metoda pentru a reseta toate starile justPressed si cantPress
    // Aceasta va fi apelata la schimbarea starii jocului (ex: din meniu in joc)
    public void clearKeys() {
        for (int i = 0; i < keys.length; i++) {
            justPressed[i] = false;
            cantPress[i] = false;
            keys[i] = false; // Asigură-te că și starea reală a tastei e resetată
        }
        up = down = left = right = enter = space = escape = z = shift = eKey = nKey = pKey = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() < 0 || e.getKeyCode() >= keys.length)
            return;
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() < 0 || e.getKeyCode() >= keys.length)
            return;
        keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}