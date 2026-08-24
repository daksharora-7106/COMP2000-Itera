import java.awt.*;

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

        // Door in the middle of right wall
        this.doorHeight = 50;
        this.doorY = y + (height / 2) - (doorHeight / 2);
    }

    public boolean contains(
        double objectX,
        double objectY,
        int objectSize
    ) {

        double centreX = objectX + objectSize / 2.0;
        double centreY = objectY + objectSize / 2.0;

        return centreX >= x
            && centreX <= x + width
            && centreY >= y
            && centreY <= y + height;
    }

    public boolean isAtDoor(
        double objectX,
        double objectY,
        int objectSize
    ) {

        double centreY =
            objectY + objectSize / 2.0;

        return objectX <= x + width + 10
            && objectX + objectSize >= x + width - 10
            && centreY >= doorY
            && centreY <= doorY + doorHeight;
    }

    public int getDoorX() {
        return x + width;
    }

    public int getDoorCentreY() {
        return doorY + doorHeight / 2;
    }

    // Zombies treat the entire building as solid
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

    public void draw(Graphics g) {

        Graphics2D g2 =
            (Graphics2D) g;

        // Building interior
        g.setColor(
            new Color(
                180,
                255,
                180
            )
        );

        g.fillRect(
            x,
            y,
            width,
            height
        );

        // Building walls
        g2.setColor(
            new Color(
                0,
                110,
                0
            )
        );

        g2.setStroke(
            new BasicStroke(4)
        );

        // Top
        g2.drawLine(
            x,
            y,
            x + width,
            y
        );

        // Left
        g2.drawLine(
            x,
            y,
            x,
            y + height
        );

        // Bottom
        g2.drawLine(
            x,
            y + height,
            x + width,
            y + height
        );

        // Right wall ABOVE door
        g2.drawLine(
            x + width,
            y,
            x + width,
            doorY
        );

        // Right wall BELOW door
        g2.drawLine(
            x + width,
            doorY + doorHeight,
            x + width,
            y + height
        );

        g2.setStroke(
            new BasicStroke(1)
        );

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

        g.setFont(
            new Font(
                "Arial",
                Font.PLAIN,
                12
            )
        );

        g.drawString(
            "LOCKED",
            x + width - 55,
            doorY - 8
        );
    }
}