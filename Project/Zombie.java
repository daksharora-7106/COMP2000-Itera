import java.awt.*;
import java.util.ArrayList;

public class Zombie {

    private double x;
    private double y;

    private static final int SIZE = 18;

    // Human speed is 3.0, so zombie is 50% slower
    private static final double ZOMBIE_SPEED = 2.0;

    private static final double ATTACK_DISTANCE = 15.0;

    private static final int DAMAGE = 20;

    public Zombie(int x, int y) {

        this.x = x;
        this.y = y;
    }

    public Human move(
        int width,
        int height,
        ArrayList<Human> humans,
        SafeZone safeZone
    ) {

        Human nearestHuman = null;
        double nearestDistance = Double.MAX_VALUE;

        // Find nearest human who is outside the safe zone
        for (Human human : humans) {

            if (human.isInSafeZone()) {
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

        // No humans left outside safe zone
        if (nearestHuman == null) {
            return null;
        }

        // Attack if close enough
        if (nearestDistance <= ATTACK_DISTANCE) {

            boolean damageDone =
                nearestHuman.takeDamage(DAMAGE);

            // Convert only when health reaches 0
            if (
                damageDone
                &&
                nearestHuman.isDead()
            ) {

                return nearestHuman;
            }

            return null;
        }

        // Move towards nearest human
        double directionX =
            nearestHuman.getX() - x;

        double directionY =
            nearestHuman.getY() - y;

        double moveX =
            directionX
            / nearestDistance
            * ZOMBIE_SPEED;

        double moveY =
            directionY
            / nearestDistance
            * ZOMBIE_SPEED;

        double newX = x + moveX;
        double newY = y + moveY;

        // Zombies cannot enter safe zone
        if (
            !safeZone.wouldZombieEnter(
                newX,
                newY,
                SIZE
            )
        ) {

            x = newX;
            y = newY;

        } else {

            // Try moving along the safe-zone wall
            boolean canMoveX =
                !safeZone.wouldZombieEnter(
                    newX,
                    y,
                    SIZE
                );

            boolean canMoveY =
                !safeZone.wouldZombieEnter(
                    x,
                    newY,
                    SIZE
                );

            if (canMoveX) {
                x = newX;
            }

            if (canMoveY) {
                y = newY;
            }
        }

        // Keep zombie inside world
        if (x < 0) {
            x = 0;
        }

        if (x > width - SIZE) {
            x = width - SIZE;
        }

        if (y < 0) {
            y = 0;
        }

        if (y > height - SIZE) {
            y = height - SIZE;
        }

        return null;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public void draw(Graphics g) {

        g.setColor(Color.RED);

        g.fillOval(
            (int) x,
            (int) y,
            SIZE,
            SIZE
        );
    }
}