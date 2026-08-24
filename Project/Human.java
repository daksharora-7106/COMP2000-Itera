import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Human {

    private double x;
    private double y;

    private double dx;
    private double dy;

    private final Random random = new Random();

    private static final int SIZE = 15;

    private static final double HUMAN_SPEED = 3.0;

    private static final double DETECTION_RANGE = 150.0;

    private static final int WALL_MARGIN = 60;

    private int health = 100;

    private static final int SAFE_ZONE_THRESHOLD = 30;

    private static final int LEAVE_SAFE_ZONE_HEALTH = 80;

    private static final long DAMAGE_COOLDOWN = 600;

    private static final long HEAL_INTERVAL = 3000;

    private long lastDamageTime = 0;

    private long lastHealTime = 0;

    private boolean insideSafeZone = false;

    private double safeX;
    private double safeY;

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

            // Keep human completely still
            x = safeX;
            y = safeY;

            dx = 0;
            dy = 0;

            heal();

            /*
             * Human leaves once health reaches 80
             */
            if (health >= LEAVE_SAFE_ZONE_HEALTH) {

                insideSafeZone = false;

                // Put human just outside the door
                x = safeZone.getDoorX() + SIZE + 5;

                y =
                    safeZone.getDoorCentreY()
                    - SIZE / 2.0;

                dx = HUMAN_SPEED;
                dy = 0;
            }

            return;
        }

        /*
         * Human seeks safe zone only
         * when health is 30 or lower
         */
        boolean seekingSafeZone =
            health <= SAFE_ZONE_THRESHOLD;

        if (seekingSafeZone) {

            moveTowardsSafeZone(
                safeZone
            );

        } else {

            Zombie nearestZombie =
                findNearestZombie(
                    zombies
                );

            /*
             * Run if zombie is close
             */
            if (nearestZombie != null) {

                double differenceX =
                    x - nearestZombie.getX();

                double differenceY =
                    y - nearestZombie.getY();

                double distance =
                    Math.sqrt(
                        differenceX * differenceX
                        +
                        differenceY * differenceY
                    );

                if (distance < DETECTION_RANGE) {

                    fleeFromZombie(
                        nearestZombie,
                        width,
                        height
                    );

                } else {

                    roam();
                }

            } else {

                roam();
            }
        }

        /*
         * Calculate next position
         */
        double nextX = x + dx;
        double nextY = y + dy;

        /*
         * CHECK IF LOW-HEALTH HUMAN
         * HAS REACHED THE SAFE-ZONE DOOR
         */

        double doorX =
            safeZone.getDoorX();

        double doorY =
            safeZone.getDoorCentreY();

        double distanceToDoor =
            Math.sqrt(
                Math.pow(x - doorX, 2)
                +
                Math.pow(y - doorY, 2)
            );

        /*
         * If the human is injured and gets
         * close enough to the door,
         * allow them to enter
         */
        if (
            seekingSafeZone
            &&
            distanceToDoor <= 25
        ) {

            insideSafeZone = true;

            /*
             * Give each human their own
             * resting location inside
             */
            safeX =
                safeZone.getRandomRestX(
                    random,
                    SIZE
                );

            safeY =
                safeZone.getRandomRestY(
                    random,
                    SIZE
                );

            x = safeX;
            y = safeY;

            dx = 0;
            dy = 0;

            lastHealTime =
                System.currentTimeMillis();

            return;
        }

        /*
         * SAFE-ZONE WALL COLLISION
         *
         * Humans who are not properly entering
         * through the door treat the safe zone
         * like a solid building.
         */
        if (
            safeZone.blocksHumanMovement(
                x,
                y,
                nextX,
                nextY,
                SIZE,
                seekingSafeZone
            )
        ) {

            /*
             * Pick a new direction instead
             * of walking through the wall
             */
            chooseNewDirection();

            nextX = x + dx;
            nextY = y + dy;

            /*
             * If the new direction still
             * enters the wall, stay still
             * for this frame
             */
            if (
                safeZone.blocksHumanMovement(
                    x,
                    y,
                    nextX,
                    nextY,
                    SIZE,
                    seekingSafeZone
                )
            ) {

                nextX = x;
                nextY = y;
            }
        }

        x = nextX;
        y = nextY;

        /*
         * WORLD BOUNDARIES
         */

        if (x < 0) {

            x = 0;
            dx = Math.abs(dx);
        }

        if (x > width - SIZE) {

            x = width - SIZE;
            dx = -Math.abs(dx);
        }

        if (y < 0) {

            y = 0;
            dy = Math.abs(dy);
        }

        if (y > height - SIZE) {

            y = height - SIZE;
            dy = -Math.abs(dy);
        }
    }

    private Zombie findNearestZombie(
        ArrayList<Zombie> zombies
    ) {

        Zombie nearestZombie = null;

        double nearestDistance =
            Double.MAX_VALUE;

        for (Zombie zombie : zombies) {

            double differenceX =
                x - zombie.getX();

            double differenceY =
                y - zombie.getY();

            double distance =
                Math.sqrt(
                    differenceX * differenceX
                    +
                    differenceY * differenceY
                );

            if (distance < nearestDistance) {

                nearestDistance = distance;

                nearestZombie = zombie;
            }
        }

        return nearestZombie;
    }

    private void fleeFromZombie(
        Zombie zombie,
        int width,
        int height
    ) {

        double directionX =
            x - zombie.getX();

        double directionY =
            y - zombie.getY();

        double distance =
            Math.sqrt(
                directionX * directionX
                +
                directionY * directionY
            );

        if (distance > 0) {

            directionX /= distance;
            directionY /= distance;
        }

        /*
         * Prevent humans from getting
         * trapped in world corners
         */

        if (x < WALL_MARGIN) {
            directionX += 1.8;
        }

        if (
            x
            > width
            - SIZE
            - WALL_MARGIN
        ) {

            directionX -= 1.8;
        }

        if (y < WALL_MARGIN) {
            directionY += 1.8;
        }

        if (
            y
            > height
            - SIZE
            - WALL_MARGIN
        ) {

            directionY -= 1.8;
        }

        double length =
            Math.sqrt(
                directionX * directionX
                +
                directionY * directionY
            );

        if (length > 0) {

            dx =
                directionX
                / length
                * HUMAN_SPEED;

            dy =
                directionY
                / length
                * HUMAN_SPEED;
        }
    }

    private void moveTowardsSafeZone(
        SafeZone safeZone
    ) {
    
        // Human aims towards the Safe Zone door
        double targetX =
            safeZone.getDoorX() + 5;
    
        double targetY =
            safeZone.getDoorCentreY();
    
        double differenceX =
            targetX - x;
    
        double differenceY =
            targetY - y;
    
        double distance =
            Math.sqrt(
                differenceX * differenceX
                +
                differenceY * differenceY
            );
    
        if (distance > 0) {
    
            // Same speed as normal human movement
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

    private void roam() {

        /*
         * Small chance of changing
         * direction while roaming
         */
        if (
            random.nextInt(100)
            < 2
        ) {

            chooseNewDirection();
        }
    }

    private void heal() {

        long currentTime =
            System.currentTimeMillis();

        /*
         * Heal 10 HP every 3 seconds
         */
        if (
            currentTime
            - lastHealTime
            >= HEAL_INTERVAL
        ) {

            health += 10;

            if (health > 100) {
                health = 100;
            }

            lastHealTime =
                currentTime;
        }
    }

    private void chooseNewDirection() {

        /*
         * Allow movement in any
         * 360-degree direction
         */
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

        /*
         * Humans cannot be attacked
         * while inside safe zone
         */
        if (insideSafeZone) {
            return false;
        }

        long currentTime =
            System.currentTimeMillis();

        /*
         * Prevent zombie from damaging
         * human every 30 milliseconds
         */
        if (
            currentTime
            - lastDamageTime
            < DAMAGE_COOLDOWN
        ) {

            return false;
        }

        health -= damage;

        if (health < 0) {
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

        /*
         * Draw human
         */
        g.setColor(Color.BLUE);

        g.fillOval(
            (int) x,
            (int) y,
            SIZE,
            SIZE
        );

        /*
         * HEALTH BAR
         */

        int barWidth = 24;
        int barHeight = 4;

        int barX =
            (int) x - 4;

        int barY =
            (int) y - 8;

        /*
         * Empty health
         */
        g.setColor(Color.RED);

        g.fillRect(
            barX,
            barY,
            barWidth,
            barHeight
        );

        /*
         * Current health
         */
        int remainingHealthWidth =
            (int) (
                barWidth
                * health
                / 100.0
            );

        g.setColor(Color.GREEN);

        g.fillRect(
            barX,
            barY,
            remainingHealthWidth,
            barHeight
        );
    }
}