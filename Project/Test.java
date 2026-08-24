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
         *
         * IMPORTANT:
         * We DO NOT start it here.
         *
         * It will start only after
         * the JFrame is visible.
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
         * Extra protection:
         * Don't update characters until
         * the JPanel has a real size.
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
         * Extra protection:
         * Don't update characters until
         * the JPanel has a real size.
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
                 * Make window visible FIRST
                 */
                frame.setVisible(
                    true
                );

                /*
                 * THEN start simulation
                 */
                simulation.startSimulation();
            }
        );
    }
}