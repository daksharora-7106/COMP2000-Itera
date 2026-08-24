import java.awt.*;

public class Weapon {

    private int x;
    private int y;

    private static final int SIZE = 30;

    private boolean collected = false;

    /*
     * Create weapon at a specific
     * position in the simulation
     */
    public Weapon(int x, int y) {

        this.x = x;
        this.y = y;
    }

    /*
     * Check whether a human
     * has already collected it
     */
    public boolean isCollected() {

        return collected;
    }

    /*
     * Called when a human
     * picks up the weapon
     */
    public void collect() {

        collected = true;
    }

    public int getX() {

        return x;
    }

    public int getY() {

        return y;
    }

    /*
     * Draw weapon on screen
     */
    public void draw(Graphics g) {

        /*
         * Once collected,
         * don't draw it anymore
         */
        if (collected) {

            return;
        }

        /*
         * Weapon label
         */
        g.setColor(Color.BLACK);

        g.setFont(
            new Font(
                "Arial",
                Font.BOLD,
                14
            )
        );

        g.drawString(
            "WEAPON",
            x - 15,
            y - 12
        );

        /*
         * Draw bright orange blade
         * so it is easy to see
         */
        g.setColor(Color.ORANGE);

        g.fillRect(
            x,
            y,
            SIZE,
            10
        );

        /*
         * Draw black handle
         */
        g.setColor(Color.BLACK);

        g.fillRect(
            x + SIZE - 5,
            y - 5,
            5,
            20
        );
    }
}