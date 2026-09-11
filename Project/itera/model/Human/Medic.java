package itera.model.Human;

import itera.model.Character;

import java.awt.Graphics;

public class Medic extends Human {
    private int medKits = 3;

    public Medic(int x, int y) {
        super(x, y);
    }

    public void heal(Character target) {
        if (medKits > 0 && target.getHealth() < 100) {
            target.restoreHealth(20);

            medKits--;
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        drawTypeLabel(g, "M");
    }
}
