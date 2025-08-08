package PaooGame;

import PaooGame.States.GameState;

public class Main
{
    public static void main(String[] args)
    {
        Game paooGame = new Game("PaooGame", 1500, 843);
        paooGame.StartGame();

        // Modificare temporara pentru a porni direct la nivelul 2
        // Aici se inițializează un GameState care începe la nivelul 2 (index 1)
        paooGame.GetRefLinks().SetState(new GameState(paooGame.GetRefLinks(), 1));
    }
}