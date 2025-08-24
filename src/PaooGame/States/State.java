package PaooGame.States;

import PaooGame.RefLinks;

import java.awt.*;

/**
 * @class State
 * @brief Clasa abstracta ce defineste structura de baza pentru toate starile jocului.
 * Implementeaza sablonul de proiectare State. O stare reprezinta o anumita
 * parte a jocului (meniu, nivel, pauza). Utilizarea starilor permite o
 * separare clara a logicii si a randarii pentru fiecare componenta a jocului.
 */
public abstract class State
{
    /** Referinta statica catre starea anterioara a jocului.*/
    private static State previousState  = null;
    /** Referinta statica catre starea curenta a jocului. Accesibila global.*/
    private static State currentState   = null;
    /** Referinta catre obiectul de legaturi (handler).*/
    protected RefLinks refLink;

    /**
     * @brief Constructorul clasei State.
     * @param refLink Referinta catre obiectul RefLinks pentru a accesa componentele jocului.
     */
    public State(RefLinks refLink)
    {
        this.refLink = refLink;
    }

    /**
     * @brief Seteaza starea curenta a jocului.
     * @param state Noua stare care va deveni activa.
     */
    public static void SetState(State state)
    {
        previousState = currentState;
        currentState = state;
    }

    /**
     * @brief Returneaza starea curenta a jocului.
     * @return Starea activa in acest moment.
     */
    public static State GetState()
    {
        return currentState;
    }

    /**
     * @brief Returneaza referinta catre starea anterioara a jocului.
     * Utila pentru a reveni la starea din care s-a intrat in starea curenta.
     */
    public static State GetPreviousStateStatic() {
        return previousState;
    }

    /**
     * @brief Metoda abstracta pentru actualizarea logicii starii.
     */
    public abstract void Update();

    /**
     * @brief Metoda abstracta pentru desenarea elementelor grafice ale starii.
     * @param g Contextul grafic in care se va desena.
     */
    public abstract void Draw(Graphics g);
}