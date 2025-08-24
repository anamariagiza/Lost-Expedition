package PaooGame.Input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @class KeyManager
 * @brief Gestioneaza si proceseaza input-ul de la tastatura.
 * Aceasta clasa implementeaza interfata KeyListener pentru a capta evenimentele
 * de apasare si eliberare a tastelor. Ofera o logica pentru a detecta atat
 * tastele tinute apasat (ex: pentru miscare), cat si apasarile unice de taste
 * (ex: pentru interactiuni), prin mecanismul "just pressed".
 */
public class KeyManager implements KeyListener {
    /** Flag-uri publice pentru acces rapid la starea tastelor de control principale.*/
    public boolean up, down, left, right, enter, space, escape;
    public boolean z;
    public boolean shift;
    public boolean eKey;
    public boolean pKey;
    /** Vectorul brut de stari pentru toate tastele (true = apasat, false = eliberat).*/
    public boolean[] keys;
    /** Vector de flag-uri; true doar in cadrul in care o tasta a fost proaspat apasata.*/
    private final boolean[] justPressed;
    /** Vector ajutator pentru logica "just pressed", pentru a preveni apasari multiple.*/
    private final boolean[] cantPress;

    /**
     * @brief Constructorul clasei KeyManager.
     * Initializeaza vectorii de stari pentru a gestiona input-ul.
     */
    public KeyManager() {
        keys = new boolean[256];
        justPressed = new boolean[keys.length];
        cantPress = new boolean[keys.length];
    }

    /**
     * @brief Metoda apelata de sistem la apasarea unei taste.
     * @param e Evenimentul de la tastatura.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() < 0 || e.getKeyCode() >= keys.length)
            return;
        keys[e.getKeyCode()] = true;
    }

    /**
     * @brief Metoda apelata de sistem la eliberarea unei taste.
     * @param e Evenimentul de la tastatura.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() < 0 || e.getKeyCode() >= keys.length)
            return;
        keys[e.getKeyCode()] = false;
    }

    /**
     * Metoda apelata cand o tasta este "tastata" (apasata si eliberata).
     * Nu este utilizata in acest proiect.
     * @param e Evenimentul de la tastatura.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Nu este necesara implementarea
    }

    /**
     * @brief Actualizeaza starea managerului de taste. TREBUIE apelata o data pe cadru.
     * Aceasta metoda proceseaza input-ul brut din vectorul `keys`.
     * Mai intai, implementeaza logica "just pressed" pentru a detecta apasarile unice.
     * Apoi, actualizeaza flag-urile publice (up, down, etc.) pentru a fi folosite in joc.
     */
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

        // Actualizeaza flag-urile specifice
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
        pKey = keys[KeyEvent.VK_P];
    }

    /**
     * @brief Reseteaza complet starea tuturor tastelor.
     * Este utila la tranzitia intre starile jocului (ex: din GameState in MenuState)
     * pentru a preveni ca o apasare de tasta dintr-o stare sa afecteze starea urmatoare.
     */
    public void clearKeys() {
        for (int i = 0; i < keys.length; i++) {
            justPressed[i] = false;
            cantPress[i] = false;
            keys[i] = false; // Asigura-te ca si starea reala a tastei e resetata
        }
        up = down = left = right = enter = space = escape = z = shift = eKey = pKey = false;
    }
    
    /**
     * @brief Verifica daca o tasta a fost apasata exact in acest cadru.
     * @param keyCode Codul tastei de verificat.
     * @return True daca tasta a fost proaspat apasata, false altfel.
     */
    public boolean isKeyJustPressed(int keyCode) {
        if (keyCode < 0 || keyCode >= keys.length)
            return false;
        return justPressed[keyCode];
    }
}