import java.awt.Color;

public class FastZombie extends Zombie {

    public FastZombie(int x, int y) {
        super(x, y);
    }

    @Override
    protected double getSpeed() {
        return 3.5;
    }

    @Override
    protected int getDamage() {
        return 10;
    }

    @Override
    protected Color getColor() {
        return Color.ORANGE;
    }
}