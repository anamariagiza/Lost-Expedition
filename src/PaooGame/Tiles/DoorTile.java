package PaooGame.Tiles;

/**
 * @class DoorTile
 * @brief O dala specializata care reprezinta o usa.
 * Aceasta clasa extinde `Tile` si permite definirea starii de soliditate
 * in momentul crearii. O usa poate fi solida (inchisa) sau non-solida (deschisa).
 */
public class DoorTile extends Tile {

    /** Stocheaza starea de soliditate a usii.*/
    private final boolean isSolid;

    /**
     * @brief Constructorul clasei DoorTile.
     * @param gid GID-ul dalei din Tiled.
     * @param isSolid True daca dala trebuie sa fie solida (usa inchisa), false altfel.
     */
    public DoorTile(int gid, boolean isSolid) {
        super(gid);
        this.isSolid = isSolid;
    }

    /**
     * @brief Suprascrie metoda IsSolid pentru a returna starea de soliditate a usii.
     * @return True daca usa este solida, false altfel.
     */
    @Override
    public boolean IsSolid() {
        return isSolid;
    }
}