package outskirts.entity;

public abstract class EntityCreature extends Entity {

    private int health;
    private int maxHealth;

    private String name;


    public final int getHealth() {
        return health;
    }
    public final void setHealth(int health) {
        assert health <= getMaxHealth();
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public final String getName() {
        return name;
    }
    public final void setName(String name) {
        this.name = name;
    }
}
