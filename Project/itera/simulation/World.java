package itera.simulation;

import itera.model.Building.Building;
import itera.model.Character;
import itera.model.Human.Human;
import itera.model.Resource.Resource;
import itera.model.SafePoint;
import itera.model.Zombie.Zombie;

import java.util.ArrayList;

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

        // Dead characters remain until this cleanup step so the current tick can
        // finish processing deaths and conversions consistently.
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

        ArrayList<Human> humans = new ArrayList<>();

        // Build a typed view without exposing the mixed character list directly.
        for (Character character : characters) {

            if (character instanceof Human human) {

                humans.add(human);
            }
        }

        return humans;
    }

    public ArrayList<Zombie> getZombies() {

        ArrayList<Zombie> zombies = new ArrayList<>();

        // Keep callers from needing repeated instanceof checks for every character.
        for (Character character : characters) {

            if (character instanceof Zombie zombie) {

                zombies.add(zombie);
            }
        }

        return zombies;
    }
}
