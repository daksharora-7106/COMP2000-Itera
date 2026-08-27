import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Test extends JPanel {

    private ArrayList<Human> humans = new ArrayList<>();
    private ArrayList<Zombie> zombies = new ArrayList<>();

    private Random random = new Random();

    private SafeZone safeZone;
    private FastForward fastForward;
    private Weapon weapon;

    private Timer timer;

    private static final int WORLD_WIDTH = 1200;
    private static final int WORLD_HEIGHT = 800;

    public Test() {

        setPreferredSize(
            new Dimension(
                WORLD_WIDTH,
                WORLD_HEIGHT
            )
        );

        // Fast forward controls
        fastForward = new FastForward();

        // Safe zone
        safeZone = new SafeZone(
            0,
            0,
            180,
            180
        );

        // Weapon
        weapon = new Weapon(
            WORLD_WIDTH / 2,
            WORLD_HEIGHT / 2
        );

        // Create 19 humans
        for (int i = 0; i < 19; i++) {

            int humanX;
            int humanY;

            do {

                humanX =
                    random.nextInt(
                        WORLD_WIDTH - 100
                    ) + 40;

                humanY =
                    random.nextInt(
                        WORLD_HEIGHT - 120
                    ) + 40;

            } while (
                humanX < 230
                &&
                humanY < 230
            );

            humans.add(
                new Human(
                    humanX,
                    humanY
                )
            );
        }

        /*
         * THREE DIFFERENT ZOMBIE TYPES
         */

        // Normal Zombie - RED
        zombies.add(
            new Zombie(
                WORLD_WIDTH - 100,
                WORLD_HEIGHT - 100
            )
        );

        // Fast Zombie - ORANGE
        zombies.add(
            new FastZombie(
                WORLD_WIDTH - 250,
                WORLD_HEIGHT - 150
            )
        );

        // Tank Zombie - DARK GRAY
        zombies.add(
            new TankZombie(
                WORLD_WIDTH - 400,
                WORLD_HEIGHT - 100
            )
        );

        // Simulation timer
        timer = new Timer(
            30,
            e -> {

                timer.setDelay(
                    fastForward.getDelay()
                );

                updateHumans();
                updateZombies();
                updateWeapon();

                repaint();
            }
        );
    }

    public void startSimulation() {
        timer.start();
    }

    public FastForward getFastForward() {
        return fastForward;
    }

    private void updateHumans() {

        if (
            getWidth() <= 0
            ||
            getHeight() <= 0
        ) {
            return;
        }

        for (Human human : humans) {

            human.move(
                getWidth(),
                getHeight(),
                zombies,
                safeZone
            );
        }
    }

    private void updateZombies() {

        if (
            getWidth() <= 0
            ||
            getHeight() <= 0
        ) {
            return;
        }

        ArrayList<Human> humansToConvert =
            new ArrayList<>();

        for (Zombie zombie : zombies) {

            Human deadHuman =
                zombie.move(
                    getWidth(),
                    getHeight(),
                    humans,
                    safeZone
                );

            if (
                deadHuman != null
                &&
                !humansToConvert.contains(
                    deadHuman
                )
            ) {

                humansToConvert.add(
                    deadHuman
                );
            }
        }

        /*
         * Convert dead humans into zombies.
         * Randomly choose a zombie type.
         */

        for (Human human : humansToConvert) {

            if (humans.remove(human)) {

                int zombieType =
                    random.nextInt(3);

                if (zombieType == 0) {

                    zombies.add(
                        new Zombie(
                            human.getX(),
                            human.getY()
                        )
                    );

                } else if (zombieType == 1) {

                    zombies.add(
                        new FastZombie(
                            human.getX(),
                            human.getY()
                        )
                    );

                } else {

                    zombies.add(
                        new TankZombie(
                            human.getX(),
                            human.getY()
                        )
                    );
                }
            }
        }
    }

    private void updateWeapon() {

        // Human collects weapon
        if (!weapon.isCollected()) {

            for (Human human : humans) {

                double distance =
                    Math.hypot(
                        human.getX() - weapon.getX(),
                        human.getY() - weapon.getY()
                    );

                if (distance <= 22) {

                    human.collectWeapon();
                    weapon.collect();

                    break;
                }
            }
        }

        // Armed human defeats one zombie
        for (Human human : humans) {

            if (!human.isArmed()) {
                continue;
            }

            for (
                int i = zombies.size() - 1;
                i >= 0;
                i--
            ) {

                Zombie zombie =
                    zombies.get(i);

                double distance =
                    Math.hypot(
                        human.getX() - zombie.getX(),
                        human.getY() - zombie.getY()
                    );

                if (distance <= 22) {

                    zombies.remove(i);

                    human.useWeapon();

                    return;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        // Background
        g.setColor(
            new Color(
                220,
                220,
                220
            )
        );

        g.fillRect(
            0,
            0,
            getWidth(),
            getHeight()
        );

        // Safe zone
        safeZone.draw(g);

        // Weapon
        weapon.draw(g);

        // Humans
        for (Human human : humans) {
            human.draw(g);
        }

        // Zombies
        for (Zombie zombie : zombies) {
            zombie.draw(g);
        }

        // Population counters
        g.setColor(Color.BLACK);

        g.setFont(
            new Font(
                "Arial",
                Font.BOLD,
                16
            )
        );

        g.drawString(
            "Humans: " + humans.size(),
            getWidth() - 240,
            30
        );

        g.drawString(
            "Zombies: " + zombies.size(),
            getWidth() - 125,
            30
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
            () -> {

                JFrame frame =
                    new JFrame(
                        "Zombie Survival Simulation"
                    );

                Test simulation =
                    new Test();

                frame.setLayout(
                    new BorderLayout()
                );

                frame.add(
                    simulation,
                    BorderLayout.CENTER
                );

                frame.add(
                    simulation.getFastForward(),
                    BorderLayout.SOUTH
                );

                frame.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
                );

                frame.pack();

                frame.setLocationRelativeTo(
                    null
                );

                frame.setVisible(
                    true
                );

                simulation.startSimulation();
            }
        );
    }
}