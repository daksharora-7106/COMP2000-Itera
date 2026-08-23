import java.awt.*;
import java.util.Random;

public class Human {

    private int x;
    private int y;

    private int dx;
    private int dy;

    private Random random = new Random();

    private static final int DETECTION_RANGE = 120;
    private static final int ESCAPE_SPEED = 3;

    private boolean infected = false;

    public Human(int x, int y) {

        this.x = x;
        this.y = y;

        chooseNewDirection();
    }

    public void move(
        int width,
        int height,
        int zombieX,
        int zombieY
    ) {

        // Infected humans no longer try to escape
        if (!infected) {

            double distance = Math.sqrt(
                Math.pow(x - zombieX, 2)
                +
                Math.pow(y - zombieY, 2)
            );

            // Run away if zombie is close
            if (distance < DETECTION_RANGE) {

                double directionX = x - zombieX;
                double directionY = y - zombieY;

                if (distance > 0) {

                    dx = (int) Math.round(
                        directionX / distance * ESCAPE_SPEED
                    );

                    dy = (int) Math.round(
                        directionY / distance * ESCAPE_SPEED
                    );
                }

            } else {

                // Otherwise roam normally
                if (random.nextInt(100) < 2) {
                    chooseNewDirection();
                }
            }

        } else {

            // Infected humans just roam
            if (random.nextInt(100) < 2) {
                chooseNewDirection();
            }
        }

        x += dx;
        y += dy;

        if (x < 0) {
            x = 0;
            dx = Math.abs(dx);
        }

        if (x > width - 15) {
            x = width - 15;
            dx = -Math.abs(dx);
        }

        if (y < 0) {
            y = 0;
            dy = Math.abs(dy);
        }

        if (y > height - 15) {
            y = height - 15;
            dy = -Math.abs(dy);
        }
    }

    private void chooseNewDirection() {

        dx = random.nextInt(5) - 2;
        dy = random.nextInt(5) - 2;

        if (dx == 0 && dy == 0) {
            dx = 1;
        }
    }

    public void infect() {
        infected = true;
    }

    public boolean isInfected() {
        return infected;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void draw(Graphics g) {

        if (infected) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.BLUE);
        }

        g.fillOval(x, y, 15, 15);
    }
}