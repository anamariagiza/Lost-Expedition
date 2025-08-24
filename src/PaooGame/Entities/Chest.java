package PaooGame.Entities;

import PaooGame.Graphics.Assets;
import PaooGame.RefLinks;
import PaooGame.States.GameState;
import PaooGame.States.State;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * @class Chest
 * @brief Implementeaza entitatea "cufar", obiectivul final al jocului.
 * Acest cufar este obiectul cu care jucatorul trebuie sa interactioneze la finalul
 * nivelului 3 pentru a castiga jocul. Initial, interactiunea cu el poate fi
 * blocata, devenind posibila doar dupa indeplinirea unei conditii (ex: invingerea unui boss).
 */
public class Chest extends Entity {
    /** Flag ce indica daca cufarul a fost deschis.*/
    private boolean opened = false;
    /** Flag ce controleaza daca jucatorul poate interactiona cu cufarul.*/
    private boolean canInteract = false;

    /**
     * @brief Constructorul clasei Chest.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X a pozitiei cufarului.
     * @param y Coordonata Y a pozitiei cufarului.
     * @param width Latimea cufarului.
     * @param height Inaltimea cufarului.
     */
    public Chest(RefLinks refLink, float x, float y, int width, int height) {
        super(refLink, x, y, width, height);
        SetPosition(x, y);
    }

    /**
     * @brief Actualizeaza starea cufarului in fiecare cadru.
     * Verifica daca jucatorul este in apropiere, daca are permisiunea sa interactioneze
     * si daca apasa tasta 'E'. Daca toate conditiile sunt indeplinite, cufarul se deschide
     * si declanseaza starea de final a jocului (EndGameState).
     */
    @Override
    public void Update() {
        if (refLink.GetPlayer() == null) return;
        if (this.bounds.intersects(refLink.GetPlayer().GetBounds()) && canInteract) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E) && !opened) {
                opened = true;
                //System.out.println("DEBUG Chest: Cufarul a fost deschis! Jocul se termina.");

                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    ((GameState) currentState).endGame();
                }
            }
        }
    }

    /**
     * @brief Deseneaza cufarul pe ecran.
     * Afiseaza imaginea corespunzatoare starii (inchis sau deschis) si
     * pop-up-ul de interactiune.
     * @param g Contextul grafic in care se va desena.
     */
    @Override
    public void Draw(Graphics g) {
        BufferedImage image = opened ? Assets.chestOpened : Assets.chestClosed;
        if (image != null) {
            int drawX = (int)((x - refLink.GetGameCamera().getxOffset()));
            int drawY = (int)((y - refLink.GetGameCamera().getyOffset()));
            int scaledWidth = (int)(width);
            int scaledHeight = (int)(height);
            g.drawImage(image, drawX, drawY, scaledWidth, scaledHeight, null);
        }
        drawInteractionPopup(g);
    }

    /**
     * @brief Permite sau blocheaza interactiunea cu cufarul.
     * @param canInteract True pentru a permite interactiunea, false pentru a o bloca.
     */
    public void setCanInteract(boolean canInteract) {
        this.canInteract = canInteract;
    }

    /**
     * @brief Verifica daca interactiunea cu cufarul este permisa.
     * @return True daca se poate interactiona, false altfel.
     */
    public boolean isCanInteract() {
        return canInteract;
    }
}