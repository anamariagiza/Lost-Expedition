package PaooGame;

/**
 * @class Main
 * @brief Clasa principala a aplicatiei, care contine punctul de intrare.
 */
public class Main {
    /**
     * @brief Punctul de intrare (entry point) al programului.
     * @param args Argumentele din linia de comanda (neutilizate).
     */
    public static void main(String[] args) {
        Game paooGame = new Game("Lost Expedition", 1500, 843);
        paooGame.StartGame();
    }
}