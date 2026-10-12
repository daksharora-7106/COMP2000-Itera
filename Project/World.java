import java.util.ArrayList;
import java.util.stream.Collectors;

public class World {

    private ArrayList<Character> characters = new ArrayList<>();

    private ArrayList<Resource> resources = new ArrayList<>();

    private ArrayList<Building> buildings = new ArrayList<>();

    private ArrayList<SafePoint> safePoints = new ArrayList<>();

    private ArrayList<ZombieWave> waves = new ArrayList<>();

    public void addCharacter(Character character) {

        characters.add(character);
    }

    public void addResource(Resource resource) {

        resources.add(resource);
    }

    public void addBuilding(Building building) {

        buildings.add(building);
    }

    public void addSafePoint(SafePoint safePoint) {

        safePoints.add(safePoint);
    }

    public void addWave(ZombieWave wave) {

        waves.add(wave);
    }

    public void update() {

        characters.removeIf(character -> !character.isAlive());
    }

    public void spawnWave(ZombieWave wave) {

        wave.trigger(this);
    }

    public boolean isCleared() {

        for (Character character : characters) {

            if (character instanceof Zombie && character.isAlive()) {

                return false;
            }
        }

        return true;
    }

    public ArrayList<Character> getCharacters() {

        return characters;
    }

    public ArrayList<Human> getHumans() {

        return characters.stream()
                .filter(Human.class::isInstance)
                .map(Human.class::cast)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Zombie> getZombies() {

        return characters.stream()
                .filter(Zombie.class::isInstance)
                .map(Zombie.class::cast)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
