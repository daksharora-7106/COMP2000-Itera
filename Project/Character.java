import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public abstract class Character {

    protected int health;
    protected double speed;

    protected Vector2D position;

    protected int size;

    public Character(
        int health,
        double speed,
        double x,
        double y,
        int size
    ) {

        this.health = health;
        this.speed = speed;

        this.position =
            new Vector2D(x, y);

        this.size = size;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void move(Vector2D direction) {

        position.add(
            direction.getX() * speed,
            direction.getY() * speed
        );
    }

    public void takeDamage(int amount) {

        health -= amount;

        if (health < 0) {
            health = 0;
        }
    }

    public int getHealth() {
        return health;
    }

    public int getX() {
        return (int) position.getX();
    }

    public int getY() {
        return (int) position.getY();
    }

    public Vector2D getPosition() {
        return position;
    }

    public double getSpeed() {
        return speed;
    }

    public int getSize() {
        return size;
    }

    public abstract void draw(Graphics g);
}


/*
 * HUMAN
 */
class Human extends Character {

    protected int stamina = 100;

    protected ArrayList<Resource> inventory =
        new ArrayList<>();

    protected static final double NORMAL_SPEED = 3.0;

    protected static final double DETECTION_RANGE = 150;

    protected boolean insideSafePoint = false;

    protected long lastDamageTime = 0;
    protected long lastHealTime = 0;

    protected static final long DAMAGE_COOLDOWN = 600;
    protected static final long HEAL_INTERVAL = 3000;

    protected Random random = new Random();

    protected double dx;
    protected double dy;

    public Human(int x, int y) {

        super(
            100,
            NORMAL_SPEED,
            x,
            y,
            15
        );

        chooseRandomDirection();
    }

    public void useItem(Resource item) {

        if (inventory.contains(item)) {
            item.use(this);
        }
    }

    public void interact(Object object) {

        if (object instanceof Resource resource) {

            inventory.add(resource);

        } else if (object instanceof Building building) {

            building.interact(this);
        }
    }

    public void update(
        int worldWidth,
        int worldHeight,
        ArrayList<Zombie> zombies,
        SafePoint safePoint
    ) {

        if (insideSafePoint) {

            dx = 0;
            dy = 0;

            healInsideSafePoint();

            if (health >= 80) {

                insideSafePoint = false;

                position.setX(
                    safePoint.getDoorX() + 20
                );

                position.setY(
                    safePoint.getDoorCentreY()
                );

                chooseRandomDirection();
            }

            return;
        }

        if (health <= 20) {

            moveTowardsSafePoint(
                safePoint
            );

            double distance =
                position.distanceTo(
                    new Vector2D(
                        safePoint.getDoorX(),
                        safePoint.getDoorCentreY()
                    )
                );

            if (distance <= 25) {

                insideSafePoint = true;

                Vector2D restPosition =
                    safePoint.getRandomRestPosition(
                        random,
                        size
                    );

                position.setX(
                    restPosition.getX()
                );

                position.setY(
                    restPosition.getY()
                );

                dx = 0;
                dy = 0;

                lastHealTime =
                    System.currentTimeMillis();

                return;
            }

        } else {

            Zombie nearestZombie =
                findNearestZombie(zombies);

            if (nearestZombie != null) {

                double distance =
                    position.distanceTo(
                        nearestZombie.getPosition()
                    );

                if (distance <= DETECTION_RANGE) {

                    fleeFrom(
                        nearestZombie,
                        worldWidth,
                        worldHeight
                    );

                } else {

                    roam();
                }

            } else {

                roam();
            }
        }

        double nextX =
            position.getX() + dx;

        double nextY =
            position.getY() + dy;

        if (
            safePoint.blocksHumanMovement(
                position.getX(),
                position.getY(),
                nextX,
                nextY,
                size,
                health <= 20
            )
        ) {

            chooseRandomDirection();

        } else {

            position.add(dx, dy);
        }

        keepInsideWorld(
            worldWidth,
            worldHeight
        );
    }

    protected Zombie findNearestZombie(
        ArrayList<Zombie> zombies
    ) {

        Zombie nearest = null;

        double nearestDistance =
            Double.MAX_VALUE;

        for (Zombie zombie : zombies) {

            double distance =
                position.distanceTo(
                    zombie.getPosition()
                );

            if (distance < nearestDistance) {

                nearestDistance = distance;
                nearest = zombie;
            }
        }

        return nearest;
    }

    protected void fleeFrom(
        Zombie zombie,
        int worldWidth,
        int worldHeight
    ) {

        double directionX =
            position.getX()
            - zombie.getX();

        double directionY =
            position.getY()
            - zombie.getY();

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

        int margin = 60;

        if (position.getX() < margin) {
            directionX += 1.8;
        }

        if (
            position.getX()
            > worldWidth - size - margin
        ) {
            directionX -= 1.8;
        }

        if (position.getY() < margin) {
            directionY += 1.8;
        }

        if (
            position.getY()
            > worldHeight - size - margin
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
                * speed;

            dy =
                directionY
                / length
                * speed;
        }
    }

    protected void moveTowardsSafePoint(
        SafePoint safePoint
    ) {

        double directionX =
            safePoint.getDoorX()
            - position.getX();

        double directionY =
            safePoint.getDoorCentreY()
            - position.getY();

        double distance =
            Math.sqrt(
                directionX * directionX
                +
                directionY * directionY
            );

        if (distance > 0) {

            dx =
                directionX
                / distance
                * speed;

            dy =
                directionY
                / distance
                * speed;
        }
    }

    protected void roam() {

        if (
            random.nextInt(100)
            < 2
        ) {

            chooseRandomDirection();
        }

        position.add(
            dx,
            dy
        );
    }

    protected void chooseRandomDirection() {

        double angle =
            random.nextDouble()
            * Math.PI
            * 2;

        dx =
            Math.cos(angle)
            * speed;

        dy =
            Math.sin(angle)
            * speed;
    }

    protected void keepInsideWorld(
        int width,
        int height
    ) {

        if (position.getX() < 0) {

            position.setX(0);
            dx = Math.abs(dx);
        }

        if (
            position.getX()
            > width - size
        ) {

            position.setX(
                width - size
            );

            dx = -Math.abs(dx);
        }

        if (position.getY() < 0) {

            position.setY(0);
            dy = Math.abs(dy);
        }

        if (
            position.getY()
            > height - size
        ) {

            position.setY(
                height - size
            );

            dy = -Math.abs(dy);
        }
    }

    public boolean receiveZombieHit(
        int damage
    ) {

        if (insideSafePoint) {
            return false;
        }

        long now =
            System.currentTimeMillis();

        if (
            now - lastDamageTime
            < DAMAGE_COOLDOWN
        ) {

            return false;
        }

        takeDamage(damage);

        lastDamageTime = now;

        return true;
    }

    protected void healInsideSafePoint() {

        long now =
            System.currentTimeMillis();

        if (
            now - lastHealTime
            >= HEAL_INTERVAL
        ) {

            health += 10;

            if (health > 100) {
                health = 100;
            }

            lastHealTime = now;
        }
    }

    public boolean isInSafePoint() {
        return insideSafePoint;
    }

    @Override
    public void draw(Graphics g) {

        g.setColor(Color.BLUE);

        g.fillOval(
            getX(),
            getY(),
            size,
            size
        );

        drawHealthBar(g);
    }

    protected void drawHealthBar(
        Graphics g
    ) {

        int width = 24;
        int height = 4;

        int x =
            getX() - 4;

        int y =
            getY() - 8;

        g.setColor(Color.RED);

        g.fillRect(
            x,
            y,
            width,
            height
        );

        int remaining =
            (int) (
                width
                * health
                / 100.0
            );

        g.setColor(Color.GREEN);

        g.fillRect(
            x,
            y,
            remaining,
            height
        );
    }

    /*
     * Draw a short character type label
     * underneath the character.
     */
    protected void drawTypeLabel(
        Graphics g,
        String label
    ) {

        g.setColor(Color.BLACK);

        g.setFont(
            new Font(
                "Arial",
                Font.BOLD,
                10
            )
        );

        g.drawString(
            label,
            getX() - 5,
            getY() + size + 12
        );
    }
}


/*
 * CIVILIAN
 */
class Civilian extends Human {

    private int fearLevel = 0;

    public Civilian(
        int x,
        int y
    ) {

        super(x, y);
    }

    public void flee() {
        fearLevel++;
    }

    @Override
    public void draw(Graphics g) {

        super.draw(g);

        drawTypeLabel(
            g,
            "C"
        );
    }
}


/*
 * SOLDIER
 */
class Soldier extends Human {

    private int ammo = 10;

    public Soldier(
        int x,
        int y
    ) {

        super(x, y);
    }

    public void shoot(
        Character target
    ) {

        if (ammo > 0) {

            target.takeDamage(20);

            ammo--;
        }
    }

    @Override
    public void draw(Graphics g) {

        super.draw(g);

        drawTypeLabel(
            g,
            "S"
        );
    }
}


/*
 * MEDIC
 */
class Medic extends Human {

    private int medKits = 3;

    public Medic(
        int x,
        int y
    ) {

        super(x, y);
    }

    public void heal(
        Character target
    ) {

        if (
            medKits > 0
            &&
            target.health < 100
        ) {

            target.health += 20;

            if (target.health > 100) {
                target.health = 100;
            }

            medKits--;
        }
    }

    @Override
    public void draw(Graphics g) {

        super.draw(g);

        drawTypeLabel(
            g,
            "M"
        );
    }
}


/*
 * ZOMBIE
 */
class Zombie extends Character {

    protected double detectionRange = 500;

    protected static final int DAMAGE = 20;

    protected static final double ATTACK_DISTANCE = 20;

    public Zombie(
        int x,
        int y
    ) {

        super(
            100,
            1.5,
            x,
            y,
            18
        );
    }

    public Human update(
        int worldWidth,
        int worldHeight,
        ArrayList<Human> humans,
        SafePoint safePoint
    ) {

        Human target =
            findClosestHuman(
                humans
            );

        if (target == null) {
            return null;
        }

        if (target.isInSafePoint()) {
            return null;
        }

        double distance =
            position.distanceTo(
                target.getPosition()
            );

        if (
            distance
            <= ATTACK_DISTANCE
        ) {

            boolean attacked =
                target.receiveZombieHit(
                    DAMAGE
                );

            if (
                attacked
                &&
                !target.isAlive()
            ) {

                return target;
            }

            return null;
        }

        chase(target);

        double nextX =
            position.getX();

        double nextY =
            position.getY();

        if (
            safePoint.wouldZombieEnter(
                nextX,
                nextY,
                size
            )
        ) {

            return null;
        }

        keepInsideWorld(
            worldWidth,
            worldHeight
        );

        return null;
    }

    protected Human findClosestHuman(
        ArrayList<Human> humans
    ) {

        Human closest = null;

        double closestDistance =
            Double.MAX_VALUE;

        for (Human human : humans) {

            if (human.isInSafePoint()) {
                continue;
            }

            double distance =
                position.distanceTo(
                    human.getPosition()
                );

            if (
                distance
                < closestDistance
            ) {

                closestDistance =
                    distance;

                closest =
                    human;
            }
        }

        return closest;
    }

    public void attack(
        Character target
    ) {

        target.takeDamage(
            DAMAGE
        );
    }

    public void chase(
        Character target
    ) {

        double directionX =
            target.getX()
            - position.getX();

        double directionY =
            target.getY()
            - position.getY();

        double distance =
            Math.sqrt(
                directionX * directionX
                +
                directionY * directionY
            );

        if (distance > 0) {

            position.add(
                directionX
                / distance
                * speed,

                directionY
                / distance
                * speed
            );
        }
    }

    protected void keepInsideWorld(
        int width,
        int height
    ) {

        if (position.getX() < 0) {
            position.setX(0);
        }

        if (
            position.getX()
            > width - size
        ) {

            position.setX(
                width - size
            );
        }

        if (position.getY() < 0) {
            position.setY(0);
        }

        if (
            position.getY()
            > height - size
        ) {

            position.setY(
                height - size
            );
        }
    }

    @Override
    public void draw(
        Graphics g
    ) {

        g.setColor(Color.RED);

        g.fillOval(
            getX(),
            getY(),
            size,
            size
        );

        drawTypeLabel(
            g,
            "Z"
        );
    }

    protected void drawTypeLabel(
        Graphics g,
        String label
    ) {

        g.setColor(Color.BLACK);

        g.setFont(
            new Font(
                "Arial",
                Font.BOLD,
                10
            )
        );

        g.drawString(
            label,
            getX() - 5,
            getY() + size + 12
        );
    }
}


/*
 * RUNNER
 */
class Runner extends Zombie {

    private double sprintSpeed = 2.0;

    public Runner(
        int x,
        int y
    ) {

        super(x, y);

        sprint();
    }

    public void sprint() {

        speed =
            sprintSpeed;
    }

    @Override
    public void draw(
        Graphics g
    ) {

        g.setColor(
            Color.ORANGE
        );

        g.fillOval(
            getX(),
            getY(),
            size,
            size
        );

        drawTypeLabel(
            g,
            "R"
        );
    }
}


/*
 * STALKER
 */
class Stalker extends Zombie {

    private int stealth = 100;

    public Stalker(
        int x,
        int y
    ) {

        super(x, y);

        speed = 1.2;
    }

    public void ambush(
        Character target
    ) {

        target.takeDamage(30);
    }

    @Override
    public void draw(
        Graphics g
    ) {

        g.setColor(
            Color.MAGENTA
        );

        g.fillOval(
            getX(),
            getY(),
            size,
            size
        );

        drawTypeLabel(
            g,
            "S"
        );
    }
}


/*
 * BLOATER
 */
class Bloater extends Zombie {

    private double blastRadius = 60;
    private int blastDamage = 60;

    public Bloater(
        int x,
        int y
    ) {

        super(x, y);

        health = 150;
        speed = 0.8;
        size = 22;
    }

    public boolean shouldExplode(
        ArrayList<Human> humans
    ) {

        for (Human human : humans) {

            if (human.isInSafePoint()) {
                continue;
            }

            double distance =
                position.distanceTo(
                    human.getPosition()
                );

            if (distance <= blastRadius) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<Human> explode(
        ArrayList<Human> humans
    ) {

        ArrayList<Human> killedHumans =
            new ArrayList<>();

        for (Human human : humans) {

            if (human.isInSafePoint()) {
                continue;
            }

            double distance =
                position.distanceTo(
                    human.getPosition()
                );

            if (distance <= blastRadius) {

                human.takeDamage(
                    blastDamage
                );

                if (!human.isAlive()) {
                    killedHumans.add(human);
                }
            }
        }

        health = 0;

        return killedHumans;
    }

    public double getBlastRadius() {
        return blastRadius;
    }

    @Override
    public void draw(
        Graphics g
    ) {

        g.setColor(
            Color.DARK_GRAY
        );

        g.fillOval(
            getX(),
            getY(),
            size,
            size
        );

        drawTypeLabel(
            g,
            "B"
        );
    }
}