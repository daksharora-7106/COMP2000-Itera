import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Test extends JPanel {

    private ArrayList<Human> humans = new ArrayList<>();
    private ArrayList<Zombie> zombies = new ArrayList<>();

    private Random random = new Random();

    private SafeZone safeZone;

    private static final int WORLD_WIDTH = 1200;
    private static final int WORLD_HEIGHT = 800;

    public Test() {

        setPreferredSize(
            new Dimension(WORLD_WIDTH, WORLD_HEIGHT)
        );

        // Create safe zone at top-left
        safeZone = new SafeZone(
            0,
            0,
            180,
            180
        );

        /*
         * Create 19 humans.
         *
         * Every human receives its OWN random X and Y position.
         * Humans cannot initially spawn near the safe zone.
         */
        for (int i = 0; i < 19; i++) {

            int humanX;
            int humanY;

            do {

                humanX = random.nextInt(WORLD_WIDTH - 100) + 40;
                humanY = random.nextInt(WORLD_HEIGHT - 120) + 40;

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
         * ORIGINAL ZOMBIE
         *
         * Explicitly place it at bottom-right.
         */
        int zombieX = WORLD_WIDTH - 80;
        int zombieY = WORLD_HEIGHT - 80;

        zombies.add(
            new Zombie(
                zombieX,
                zombieY
            )
        );

        /*
         * Start simulation.
         */
        Timer timer = new Timer(
            30,
            e -> {

                updateHumans();
                updateZombies();

                repaint();
            }
        );

        timer.start();
    }

    private void updateHumans() {

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
                !humansToConvert.contains(deadHuman)
            ) {

                humansToConvert.add(deadHuman);
            }
        }

        /*
         * Convert dead humans into zombies.
         */
        for (Human human : humansToConvert) {

            if (humans.remove(human)) {

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
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        /*
         * Background
         */
        g.setColor(
            new Color(220, 220, 220)
        );

        g.fillRect(
            0,
            0,
            getWidth(),
            getHeight()
        );

        /*
         * Safe zone
         */
        safeZone.draw(g);

        /*
         * Humans
         */
        for (Human human : humans) {
            human.draw(g);
        }

        /*
         * Zombies
         */
        for (Zombie zombie : zombies) {
            zombie.draw(g);
        }

        /*
         * Population counter
         */
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

        JFrame frame =
            new JFrame(
                "Zombie Survival Simulation"
            );

        Test simulation =
            new Test();

        frame.add(simulation);

        frame.pack();

        frame.setDefaultCloseOperation(
            JFrame.EXIT_ON_CLOSE
        );

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}