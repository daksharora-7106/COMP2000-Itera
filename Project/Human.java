import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Human {

    private double x;
    private double y;

    private double dx;
    private double dy;

    private Random random =
        new Random();

    private static final int SIZE = 15;

    private static final double HUMAN_SPEED = 3.0;

    private static final double DETECTION_RANGE = 150.0;

    private static final int WALL_MARGIN = 70;

    private static final double WALL_AVOIDANCE = 2.5;

    private int health = 100;

    private static final long DAMAGE_COOLDOWN = 600;

    private long lastDamageTime = 0;

    // Human heads to safe zone when health reaches this
    private static final int SAFE_ZONE_THRESHOLD = 30;

    // Once healed to this level, human leaves again
    private static final int LEAVE_SAFE_ZONE_HEALTH = 80;

    private boolean insideSafeZone = false;

    // Healing
    private static final long HEAL_INTERVAL = 3000;

    private long lastHealTime = 0;

    public Human(int x, int y) {

        this.x = x;
        this.y = y;

        chooseNewDirection();
    }

    public void move(
        int width,
        int height,
        ArrayList<Zombie> zombies,
        SafeZone safeZone
    ) {

        /*
         * HUMAN IS INSIDE SAFE ZONE
         */

        if (insideSafeZone) {

            dx = 0;
            dy = 0;

            heal();

            /*
             * Once health reaches 80,
             * human leaves the safe zone.
             */

            if (
                health
                >= LEAVE_SAFE_ZONE_HEALTH
            ) {

                insideSafeZone = false;

                // Start moving through the door
                dx = HUMAN_SPEED;
                dy = 0;
            }

            return;
        }

        /*
         * LOW HEALTH:
         * Go to safe zone.
         */

        if (
            health
            <= SAFE_ZONE_THRESHOLD
        ) {

            moveTowardSafeZone(
                safeZone
            );

        } else {

            /*
             * NORMAL SURVIVAL BEHAVIOUR
             */

            Zombie nearestZombie = null;

            double nearestDistance =
                Double.MAX_VALUE;

            for (
                Zombie zombie
                :
                zombies
            ) {

                double differenceX =
                    x - zombie.getX();

                double differenceY =
                    y - zombie.getY();

                double distance =
                    Math.sqrt(
                        differenceX
                        * differenceX
                        +
                        differenceY
                        * differenceY
                    );

                if (
                    distance
                    < nearestDistance
                ) {

                    nearestDistance =
                        distance;

                    nearestZombie =
                        zombie;
                }
            }

            /*
             * Run from zombie
             */

            if (
                nearestZombie != null
                &&
                nearestDistance
                < DETECTION_RANGE
            ) {

                double directionX =
                    x
                    - nearestZombie.getX();

                double directionY =
                    y
                    - nearestZombie.getY();

                if (
                    nearestDistance
                    > 0
                ) {

                    directionX /=
                        nearestDistance;

                    directionY /=
                        nearestDistance;
                }

                /*
                 * Avoid corners
                 */

                if (
                    x
                    < WALL_MARGIN
                ) {

                    directionX +=
                        WALL_AVOIDANCE;
                }

                if (
                    x
                    >
                    width
                    - SIZE
                    - WALL_MARGIN
                ) {

                    directionX -=
                        WALL_AVOIDANCE;
                }

                if (
                    y
                    < WALL_MARGIN
                ) {

                    directionY +=
                        WALL_AVOIDANCE;
                }

                if (
                    y
                    >
                    height
                    - SIZE
                    - WALL_MARGIN
                ) {

                    directionY -=
                        WALL_AVOIDANCE;
                }

                double length =
                    Math.sqrt(
                        directionX
                        * directionX
                        +
                        directionY
                        * directionY
                    );

                if (
                    length > 0
                ) {

                    dx =
                        directionX
                        / length
                        * HUMAN_SPEED;

                    dy =
                        directionY
                        / length
                        * HUMAN_SPEED;
                }

            } else {

                /*
                 * Roam normally
                 */

                if (
                    random.nextInt(100)
                    < 2
                ) {

                    chooseNewDirection();
                }
            }
        }

        double nextX =
            x + dx;

        double nextY =
            y + dy;

        /*
         * Check safe zone entry.
         *
         * Only low-health humans should
         * intentionally enter.
         */

        if (
            health
            <= SAFE_ZONE_THRESHOLD
            &&
            safeZone.isAtDoor(
                nextX,
                nextY,
                SIZE
            )
        ) {

            /*
             * Move them just inside the door.
             */

            x =
                safeZone.getDoorX()
                - SIZE
                - 5;

            y =
                safeZone.getDoorCentreY()
                - SIZE / 2.0;

            insideSafeZone = true;

            dx = 0;
            dy = 0;

            lastHealTime =
                System.currentTimeMillis();

            return;
        }

        x = nextX;
        y = nextY;

        /*
         * World boundaries
         */

        if (x < 0) {

            x = 0;
            dx = Math.abs(dx);
        }

        if (
            x
            > width - SIZE
        ) {

            x = width - SIZE;
            dx = -Math.abs(dx);
        }

        if (y < 0) {

            y = 0;
            dy = Math.abs(dy);
        }

        if (
            y
            > height - SIZE
        ) {

            y = height - SIZE;
            dy = -Math.abs(dy);
        }
    }

    private void moveTowardSafeZone(
        SafeZone safeZone
    ) {

        double targetX =
            safeZone.getDoorX();

        double targetY =
            safeZone.getDoorCentreY();

        double differenceX =
            targetX - x;

        double differenceY =
            targetY - y;

        double distance =
            Math.sqrt(
                differenceX
                * differenceX
                +
                differenceY
                * differenceY
            );

        if (
            distance > 0
        ) {

            dx =
                differenceX
                / distance
                * HUMAN_SPEED;

            dy =
                differenceY
                / distance
                * HUMAN_SPEED;
        }
    }

    private void heal() {

        long currentTime =
            System.currentTimeMillis();

        if (
            currentTime
            - lastHealTime
            >= HEAL_INTERVAL
        ) {

            health += 10;

            if (
                health > 100
            ) {

                health = 100;
            }

            lastHealTime =
                currentTime;
        }
    }

    private void chooseNewDirection() {

        double angle =
            random.nextDouble()
            * Math.PI
            * 2;

        dx =
            Math.cos(angle)
            * HUMAN_SPEED;

        dy =
            Math.sin(angle)
            * HUMAN_SPEED;
    }

    public boolean takeDamage(
        int damage
    ) {

        if (insideSafeZone) {
            return false;
        }

        long currentTime =
            System.currentTimeMillis();

        if (
            currentTime
            - lastDamageTime
            < DAMAGE_COOLDOWN
        ) {

            return false;
        }

        health -= damage;

        if (
            health < 0
        ) {

            health = 0;
        }

        lastDamageTime =
            currentTime;

        return true;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public boolean isInSafeZone() {
        return insideSafeZone;
    }

    public int getHealth() {
        return health;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public void draw(Graphics g) {

        g.setColor(Color.BLUE);

        g.fillOval(
            (int) x,
            (int) y,
            SIZE,
            SIZE
        );

        /*
         * Health bar
         */

        int barWidth = 24;
        int barHeight = 4;

        int barX =
            (int) x - 4;

        int barY =
            (int) y - 8;

        g.setColor(Color.RED);

        g.fillRect(
            barX,
            barY,
            barWidth,
            barHeight
        );

        int currentHealthWidth =
            (int) (
                barWidth
                * health
                / 100.0
            );

        g.setColor(Color.GREEN);

        g.fillRect(
            barX,
            barY,
            currentHealthWidth,
            barHeight
        );
    }
}