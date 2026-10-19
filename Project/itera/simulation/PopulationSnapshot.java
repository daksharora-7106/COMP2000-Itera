package itera.simulation;

import itera.model.human.Human;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/** Immutable values collected on the simulation thread, with no live entity references. */
public final class PopulationSnapshot {
    public record RoleSummary(long count, long wounded, double averageHealth) { }

    private static final class RoleAccumulator {
        private long count;
        private long wounded;
        private long totalHealth;

        private void add(Human human) {
            count++;
            if (human.getHealth() < 100) wounded++;
            totalHealth += human.getHealth();
        }

        private RoleAccumulator combine(RoleAccumulator other) {
            count += other.count;
            wounded += other.wounded;
            totalHealth += other.totalHealth;
            return this;
        }

        private RoleSummary toSummary() {
            return new RoleSummary(count, wounded, (double) totalHealth / count);
        }
    }

    private final Map<String, RoleSummary> roles;

    public PopulationSnapshot(List<Human> humans) {
        Map<String, RoleSummary> collected = humans.stream()
            .filter(Human::isAlive)
            .collect(Collectors.groupingBy(human -> human.getClass().getSimpleName(),
                TreeMap::new,
                Collector.of(RoleAccumulator::new, RoleAccumulator::add,
                    RoleAccumulator::combine, RoleAccumulator::toSummary)));
        roles = Collections.unmodifiableMap(collected);
    }

    public Map<String, RoleSummary> getRoles() { return roles; }
}
