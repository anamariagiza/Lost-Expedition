package PaooGame;

public class Main
{
    public static void main(String[] args)
    {
        // Poti schimba acest constructor pentru a începe de la un nivel diferit.
        // De exemplu: 'new Game("Lost Expedition", 1500, 843, 3)' pentru a porni de la nivelul 3.
        Game paooGame = new Game("Lost Expedition", 1500, 843);
        paooGame.StartGame();
    }
}