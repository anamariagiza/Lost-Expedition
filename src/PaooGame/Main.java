package PaooGame;

import PaooGame.States.LoadingScreenState;
import PaooGame.Graphics.Assets;
import PaooGame.Tiles.Tile;
import PaooGame.States.MenuState;

public class Main
{
    public static void main(String[] args)
    {
        Game paooGame = new Game("Lost Expedition", 1500, 843);
        paooGame.StartGame();
    }
}