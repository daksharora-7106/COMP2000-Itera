package itera.simulation;

import itera.model.Zombie.Bloater;
import itera.model.Zombie.Runner;
import itera.model.Zombie.Stalker;
import itera.model.Vector2D;
import itera.model.Zombie.Zombie;

import java.util.ArrayList;
import java.util.Random;

public class ZombieWave {

    private int count;

    private ArrayList<Vector2D> spawnPoints = new ArrayList<>();

    private Random random = new Random();

    public ZombieWave(int count) {

        this.count = count;
    }

    public void addSpawnPoint(Vector2D point) {

        spawnPoints.add(point);
    }

    /**
     * Creates the configured number of zombies and places them at the spawn
     * points in round-robin order.
     *
     * @param world the world that receives the spawned zombies
     */
    public void trigger(World world) {

        if (spawnPoints.isEmpty()) {
            return;
        }

        for (int i = 0; i < count; i++) {

            // Reuse spawn points cyclically when the wave is larger than the list.
            Vector2D point = spawnPoints.get(i % spawnPoints.size());

            int zombieType = random.nextInt(4);

            Zombie zombie;

            if (zombieType == 0) {

                zombie = new Zombie((int) point.getX(), (int) point.getY());

            } else if (zombieType == 1) {

                zombie = new Runner((int) point.getX(), (int) point.getY());

            } else if (zombieType == 2) {

                zombie = new Stalker((int) point.getX(), (int) point.getY());

            } else {

                zombie = new Bloater((int) point.getX(), (int) point.getY());
            }

            world.addCharacter(zombie);
        }
    }
}
