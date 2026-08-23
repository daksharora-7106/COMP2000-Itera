import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Test extends JPanel {

    private ArrayList<Human> humans = new ArrayList<>();
    private Zombie zombie;
    private Random random = new Random();

    public Test() {

        // Create 19 humans at random positions
        for (int i = 0; i < 19; i++) {

            int x = random.nextInt(750) + 20;
            int y = random.nextInt(500) + 20;

            humans.add(new Human(x, y));
        }

        // Create 1 zombie at a random position
        int zombieX = random.nextInt(750) + 20;
        int zombieY = random.nextInt(500) + 20;

        zombie = new Zombie(zombieX, zombieY);

        // Update simulation every 30 milliseconds
        Timer timer = new Timer(30, e -> {

            // Move humans
            for (Human human : humans) {
                human.move(
                    getWidth(),
                    getHeight(),
                    zombie.getX(),
                    zombie.getY()
                );
            }

            // Zombie chases closest human
            zombie.move(
                getWidth(),
                getHeight(),
                humans
            );

            repaint();
        });

        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw humans
        for (Human human : humans) {
            human.draw(g);
        }

        // Draw zombie
        zombie.draw(g);
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Zombie Survival Simulation");

        Test simulation = new Test();

        frame.add(simulation);

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}