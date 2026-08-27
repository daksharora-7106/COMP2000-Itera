import java.util.ArrayList;

public class ZombieWave {

    private int count;

    private ArrayList<Vector2D> spawnPoints =
        new ArrayList<>();

    public ZombieWave(
        int count
    ) {

        this.count =
            count;
    }

    public void addSpawnPoint(
        Vector2D point
    ) {

        spawnPoints.add(
            point
        );
    }

    public void trigger(
        World world
    ) {

        if (spawnPoints.isEmpty()) {
            return;
        }

        for (
            int i = 0;
            i < count;
            i++
        ) {

            Vector2D point =
                spawnPoints.get(
                    i
                    %
                    spawnPoints.size()
                );

            world.addCharacter(
                new Zombie(
                    (int) point.getX(),
                    (int) point.getY()
                )
            );
        }
    }
}