package outskirts.world;

import outskirts.client.Outskirts;
import outskirts.client.render.Light;
import outskirts.entity.Entity;
import outskirts.entity.player.EntityPlayer;
import outskirts.physics.dynamics.DiscreteDynamicsWorld;
import outskirts.storage.Savable;
import outskirts.storage.dat.DATArray;
import outskirts.storage.dat.DATObject;
import outskirts.util.GameTimer;
import outskirts.util.logging.Log;

import java.util.*;

public abstract class World implements Savable { // impl Tickable ..?

    private List<Entity> entities = new ArrayList<>();

    public List<Light> lights = new ArrayList<>();

    public DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld();
    public float tmpTickFactor = 1;

    public void addEntity(Entity entity) {
        entity.setWorld(this);
        entities.add(entity);
        dynamicsWorld.addCollisionObject(entity.getRigidBody());
    }

    public void removeEntity(Entity entity) {
        entity.setWorld(null);
        entities.remove(entity);
        dynamicsWorld.removeCollisionObject(entity.getRigidBody());
    }

    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public void onTick() {

        Outskirts.getProfiler().push("Physics");
        if (tmpTickFactor != 0)
        dynamicsWorld.stepSimulation(1f/GameTimer.TPS *tmpTickFactor);
        Outskirts.getProfiler().pop();

    }

    @Override
    public void onRead(DATObject mp) {
        // clear first.?

        List lsEntities = (List)mp.get("entities");
        for (Object o : lsEntities) {
            addEntity(Entity.createEntity((DATObject)o));
        }
        Log.LOGGER.info("read entities: {}", lsEntities.size());

    }

    @Override
    public Map onWrite(DATObject mp) {
        DATArray lsEntities = new DATArray();
        for (Entity entity : entities) {
            if (entity instanceof EntityPlayer)
                continue;
            lsEntities.add(entity.onWrite(new DATObject()));
        }
        mp.put("entities", lsEntities);
        Log.LOGGER.info("write entities: {}", lsEntities.size());

        DATObject mpMetadata = new DATObject();
        mpMetadata.put("modify_time", System.currentTimeMillis());
        mp.put("metadata", mpMetadata);

        return mp;
    }
}
