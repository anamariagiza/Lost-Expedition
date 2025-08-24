package PaooGame.Entities;

import PaooGame.RefLinks;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @class TrapTrigger
 * @brief Implementeaza o zona de declansare invizibila pentru capcane.
 * Aceasta entitate nu are o reprezentare vizuala si nici o logica de
 * actualizare complexa. Rolul ei este de a servi drept o zona de detectie.
 * Cand jucatorul intra in coliziune cu un TrapTrigger, clasa GameState
 * va detecta acest eveniment si va activa un grup de capcane.
 */
public class TrapTrigger extends Entity {

    /**
     * @brief Constructorul clasei TrapTrigger.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X a coltului stanga-sus al zonei de declansare.
     * @param y Coordonata Y a coltului stanga-sus al zonei de declansare.
     * @param width Latimea zonei de declansare.
     * @param height Inaltimea zonei de declansare.
     */
    public TrapTrigger(RefLinks refLink, float x, float y, int width, int height) {
        super(refLink, x, y, width, height);
        this.bounds = new Rectangle((int) x, (int) y, width, height);
    }

    /**
     * @brief Actualizeaza starea entitatii in fiecare cadru.
     * Metoda este intentionat goala. Logica de coliziune si de activare
     * a capcanelor este gestionata centralizat in clasa GameState.
     */
    @Override
    public void Update() {
        // Nicio logica de daune aici. Doar detecteaza coliziunea in GameState.
    }

    /**
     * @brief Deseneaza entitatea pe ecran.
     * Metoda este intentionat goala, deoarece declansatorul (trigger)
     * este un obiect complet invizibil in lumea jocului.
     * @param g Contextul grafic in care s-ar desena.
     */
    @Override
    public void Draw(Graphics g) {
        // Obiectul este invizibil.
    }
}