import java.awt.*;
import java.util.Random;

public class SafeZone {

    private int x;
    private int y;
    private int width;
    private int height;

    private int doorY;
    private int doorHeight;

    public SafeZone(int x, int y, int width, int height) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        doorHeight = 50;
        doorY = y + height / 2 - doorHeight / 2;
    }

    public boolean contains(
        double objectX,
        double objectY,
        int objectSize
    ) {

        double centreX = objectX + objectSize / 2.0;
        double centreY = objectY + objectSize / 2.0;

        return centreX > x
            && centreX < x + width
            && centreY > y
            && centreY < y + height;
    }

    public boolean isDoorY(
        double objectY,
        int objectSize
    ) {

        double centreY =
            objectY + objectSize / 2.0;

        return centreY >= doorY
            && centreY <= doorY + doorHeight;
    }

    /*
     * Returns true if a normal human is trying
     * to enter the building through a wall.
     */
    public boolean blocksHumanMovement(
        double currentX,
        double currentY,
        double nextX,
        double nextY,
        int humanSize,
        boolean allowedToEnter
    ) {

        boolean nextIntersectsBuilding =
            nextX + humanSize > x
            && nextX < x + width
            && nextY + humanSize > y
            && nextY < y + height;

        if (!nextIntersectsBuilding) {
            return false;
        }

        /*
         * Injured humans can enter ONLY
         * through the door.
         */
        if (
            allowedToEnter
            &&
            isDoorY(nextY, humanSize)
            &&
            currentX >= x + width - humanSize
        ) {

            return false;
        }

        return true;
    }

    /*
     * Zombies have no key.
     * The entire building, including the door,
     * acts like a solid object to them.
     */
    public boolean wouldZombieEnter(
        double zombieX,
        double zombieY,
        int zombieSize
    ) {

        return zombieX + zombieSize > x
            && zombieX < x + width
            && zombieY + zombieSize > y
            && zombieY < y + height;
    }

    public int getDoorX() {
        return x + width;
    }

    public int getDoorCentreY() {
        return doorY + doorHeight / 2;
    }

    /*
     * Give each recovering human a different
     * position inside the safe zone.
     */
    public int getRandomRestX(
        Random random,
        int humanSize
    ) {

        return x + 25
            + random.nextInt(
                width - humanSize - 50
            );
    }

    public int getRandomRestY(
        Random random,
        int humanSize
    ) {

        return y + 45
            + random.nextInt(
                height - humanSize - 65
            );
    }

    public void draw(Graphics g) {

        Graphics2D g2 =
            (Graphics2D) g;

        // Interior
        g.setColor(
            new Color(180, 255, 180)
        );

        g.fillRect(
            x,
            y,
            width,
            height
        );

        // Walls
        g2.setColor(
            new Color(0, 110, 0)
        );

        g2.setStroke(
            new BasicStroke(4)
        );

        // Top wall
        g2.drawLine(
            x,
            y,
            x + width,
            y
        );

        // Left wall
        g2.drawLine(
            x,
            y,
            x,
            y + height
        );

        // Bottom wall
        g2.drawLine(
            x,
            y + height,
            x + width,
            y + height
        );

        // Right wall above door
        g2.drawLine(
            x + width,
            y,
            x + width,
            doorY
        );

        // Right wall below door
        g2.drawLine(
            x + width,
            doorY + doorHeight,
            x + width,
            y + height
        );

        g2.setStroke(
            new BasicStroke(1)
        );

        // Title
        g.setColor(Color.BLACK);

        g.setFont(
            new Font(
                "Arial",
                Font.BOLD,
                18
            )
        );

        g.drawString(
            "SAFE ZONE",
            x + 30,
            y + 30
        );
    }
}