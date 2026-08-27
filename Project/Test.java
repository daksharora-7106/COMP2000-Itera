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

    // Collectible weapon
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

        /*
         * Create Fast Forward controls
         */
        fastForward = new FastForward();

        /*
         * Create Safe Zone
         * Top-left corner
         */
        safeZone = new SafeZone(
            0,
            0,
            180,
            180
        );

        /*
         * Create collectible weapon
         * near the centre of the map
         */
        weapon = new Weapon(
            WORLD_WIDTH / 2,
            WORLD_HEIGHT / 2
        );

        /*
         * Create 19 humans at random positions
         */
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
         * Create original zombie
         * Bottom-right corner
         */
        int zombieX =
            WORLD_WIDTH - 80;

        int zombieY =
            WORLD_HEIGHT - 80;

        zombies.add(
            new Zombie(
                zombieX,
                zombieY
            )
        );

        /*
         * Create timer
         */
        timer = new Timer(
            30,
            e -> {

                /*
                 * Update timer speed
                 * depending on 1x / 2x / 5x
                 */
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

    /*
     * Start simulation only after
     * the JFrame has been created
     */
    public void startSimulation() {

        timer.start();
    }

    /*
     * Allows main() to add the
     * FastForward panel to the JFrame
     */
    public FastForward getFastForward() {

        return fastForward;
    }

    private void updateHumans() {

        /*
         * Don't update characters until
         * the JPanel has a real size
         */
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

        /*
         * Don't update characters until
         * the JPanel has a real size
         */
        if (
            getWidth() <= 0
            ||
            getHeight() <= 0
        ) {

            return;
        }

        ArrayList<Human> humansToConvert =
            new ArrayList<>();

        /*
         * Update every zombie
         */
        for (Zombie zombie : zombies) {

            Human deadHuman =
                zombie.move(
                    getWidth(),
                    getHeight(),
                    humans,
                    safeZone
                );

            /*
             * If a human reaches 0 health,
             * prepare them for conversion
             */
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
         * Convert dead humans
         * into full zombies
         */
        for (
            Human human
            :
            humansToConvert
        ) {

            if (
                humans.remove(
                    human
                )
            ) {

                zombies.add(
                    new Zombie(
                        human.getX(),
                        human.getY()
                    )
                );
            }
        }
    }

    /*
     * Weapon logic
     */
    private void updateWeapon() {

        /*
         * Human collects the weapon
         */
        if (!weapon.isCollected()) {

            for (Human human : humans) {

                /*
                 * Calculate the centre
                 * position of the human
                 */
                double humanCentreX =
                    human.getX() + 7.5;

                double humanCentreY =
                    human.getY() + 7.5;

                /*
                 * Calculate the centre
                 * position of the weapon
                 */
                double weaponCentreX =
                    weapon.getX() + 15.0;

                double weaponCentreY =
                    weapon.getY() + 5.0;

                /*
                 * Calculate distance between
                 * human and weapon centres
                 */
                double distance =
                    Math.hypot(
                        humanCentreX - weaponCentreX,
                        humanCentreY - weaponCentreY
                    );

                /*
                 * Collect weapon when the
                 * human reaches it
                 */
                if (distance <= 25) {

                    human.collectWeapon();

                    weapon.collect();

                    break;
                }
            }
        }

        /*
         * Armed human can defeat
         * one zombie
         */
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

                /*
                 * Armed human defeats zombie
                 * when close enough
                 */
                if (distance <= 22) {

                    zombies.remove(i);

                    /*
                     * Weapon can only
                     * be used once
                     */
                    human.useWeapon();

                    return;
                }
            }
        }
    }

    @Override
    protected void paintComponent(
        Graphics g
    ) {

        super.paintComponent(g);

        /*
         * Background
         */
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

        /*
         * Draw Safe Zone
         */
        safeZone.draw(g);

        /*
         * Draw Weapon
         */
        weapon.draw(g);

        /*
         * Draw Humans
         */
        for (
            Human human
            :
            humans
        ) {

            human.draw(g);
        }

        /*
         * Draw Zombies
         */
        for (
            Zombie zombie
            :
            zombies
        ) {

            zombie.draw(g);
        }

        /*
         * Population Counter
         */
        g.setColor(
            Color.BLACK
        );

        g.setFont(
            new Font(
                "Arial",
                Font.BOLD,
                16
            )
        );

        g.drawString(
            "Humans: "
            + humans.size(),
            getWidth() - 240,
            30
        );

        g.drawString(
            "Zombies: "
            + zombies.size(),
            getWidth() - 125,
            30
        );
    }

    public static void main(
        String[] args
    ) {

        /*
         * Create everything on
         * Swing's Event Dispatch Thread
         */
        SwingUtilities.invokeLater(
            () -> {

                try {

                    JFrame frame =
                        new JFrame(
                            "Zombie Survival Simulation"
                        );

                    Test simulation =
                        new Test();

                    /*
                     * Main JFrame layout
                     */
                    frame.setLayout(
                        new BorderLayout()
                    );

                    /*
                     * Simulation in centre
                     */
                    frame.add(
                        simulation,
                        BorderLayout.CENTER
                    );

                    /*
                     * Fast Forward buttons
                     * at the bottom
                     */
                    frame.add(
                        simulation.getFastForward(),
                        BorderLayout.SOUTH
                    );

                    frame.setDefaultCloseOperation(
                        JFrame.EXIT_ON_CLOSE
                    );

                    /*
                     * Uses preferred size from Test
                     */
                    frame.pack();

                    frame.setLocationRelativeTo(
                        null
                    );

                    /*
                     * Make window visible first
                     */
                    frame.setVisible(
                        true
                    );

                    /*
                     * Then start simulation
                     */
                    simulation.startSimulation();

                } catch (RuntimeException exception) {

                    /*
                     * Display a readable error
                     * if simulation startup fails
                     */
                    JOptionPane.showMessageDialog(
                        null,
                        "The simulation could not start:\n"
                        + exception.getMessage(),
                        "Simulation Error",
                        JOptionPane.ERROR_MESSAGE
                    );

                    /*
                     * Also print technical details
                     * for debugging
                     */
                    exception.printStackTrace();
                }
            }
        );
    }
}