import java.awt.*;
import java.util.ArrayList;

public class Zombie {

    private int x;
    private int y;

    private static final int CHASE_SPEED = 2;
    private static final int CATCH_DISTANCE = 18;

    public Zombie(int x, int y) {

        this.x = x;
        this.y = y;
    }

    public void move(
        int width,
        int height,
        ArrayList<Human> humans
    ) {

        Human nearestHuman = null;
        double nearestDistance = Double.MAX_VALUE;

        // Find nearest human who is NOT already infected
        for (Human human : humans) {

            if (human.isInfected()) {
                continue;
            }

            double differenceX = human.getX() - x;
            double differenceY = human.getY() - y;

            double distance = Math.sqrt(
                differenceX * differenceX
                +
                differenceY * differenceY
            );

            if (distance < nearestDistance) {

                nearestDistance = distance;
                nearestHuman = human;
            }
        }

        // If there is still a healthy human
        if (nearestHuman != null) {

            // If caught, infect them
            if (nearestDistance <= CATCH_DISTANCE) {

                nearestHuman.infect();

            } else {

                // Otherwise chase them
                double directionX = nearestHuman.getX() - x;
                double directionY = nearestHuman.getY() - y;

                int dx = (int) Math.round(
                    directionX / nearestDistance * CHASE_SPEED
                );

                int dy = (int) Math.round(
                    directionY / nearestDistance * CHASE_SPEED
                );

                x += dx;
                y += dy;
            }
        }

        if (x < 0) {
            x = 0;
        }

        if (x > width - 18) {
            x = width - 18;
        }

        if (y < 0) {
            y = 0;
        }

        if (y > height - 18) {
            y = height - 18;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void draw(Graphics g) {

        g.setColor(Color.RED);
        g.fillOval(x, y, 18, 18);
    }
}