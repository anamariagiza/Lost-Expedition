package PaooGame;

import PaooGame.States.LoadingScreenState;

public class Main
{
    public static void main(String[] args)
    {
        Game paooGame = new Game("PaooGame", 1500, 843);
        paooGame.StartGame();

        // Linia care crea direct GameState a fost eliminata.
        // Acum, jocul va incepe cu LoadingScreenState, care va initializa
        // Map, Player si va face tranzitia la MenuState si apoi la GameState.
    }
}