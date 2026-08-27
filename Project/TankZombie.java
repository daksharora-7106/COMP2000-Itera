import java.awt.Color;

public class TankZombie extends Zombie {

    public TankZombie(int x, int y) {
        super(x, y);
    }

    @Override
    protected double getSpeed() {
        return 1.2;
    }

    @Override
    protected int getDamage() {
        return 35;
    }

    @Override
    protected Color getColor() {
        return Color.DARK_GRAY;
    }
}