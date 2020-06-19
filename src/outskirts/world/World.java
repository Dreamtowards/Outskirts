package outskirts.world;

import outskirts.client.Outskirts;
import outskirts.client.render.Light;
import outskirts.entity.Entity;
import outskirts.event.Events;
import outskirts.event.world.terrain.TerrainLoadedEvent;
import outskirts.event.world.terrain.TerrainUnloadedEvent;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.dynamics.DiscreteDynamicsWorld;
import outskirts.util.GameTimer;
import outskirts.util.Maths;
import outskirts.world.terrain.Terrain;

import java.util.*;

public abstract class World { // impl Tickable ..?

    private List<Entity> entities = new ArrayList<>();
    protected Map<Long, Terrain> terrains = new HashMap<>();

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

    ///// UTIL METHODS /////

    public List<AABB> getAABBs(AABB range) {
        List<AABB> result = new ArrayList<>();
        for (Entity entity : entities) {
            if (AABB.intersects(entity.getRigidBody().getAABB(), range)) {
                result.add(entity.getRigidBody().getAABB());
            }
        }
        return result;
    }

    public float getTerrainHeight(float x, float z) {
        Terrain terr = getLoadedTerrain(x, z);
        return terr == null ? 0 : terr.getHeight(x, z);
    }

    public void onTick() {

        Outskirts.getProfiler().push("Physics");
        if (tmpTickFactor != 0)
        dynamicsWorld.stepSimulation(1f/GameTimer.TPS *tmpTickFactor);
        Outskirts.getProfiler().pop();

    }


    public final Terrain provideTerrain(float x, float z) {
        Terrain terrain = getLoadedTerrain(x, z);
        if (terrain == null) {
            terrain = loadTerrain(Maths.floor(x, Terrain.SIZE), Maths.floor(z, Terrain.SIZE));

            Events.EVENT_BUS.post(new TerrainLoadedEvent(terrain));

            terrains.put(Terrain.posLong(terrain.x, terrain.z), terrain);
        }
        return terrain;
    }
    public final Terrain provideTerrain(long poslong) {  // toolmethod
        return provideTerrain((int)(poslong >> 32), (int)poslong);
    }

    /**
     * load/gen a not loaded terrain. calls by provideTerrain(), requires nonnull
     * just construct a new Terrain and return, do not needs do other sth like stores to loadedList
     */
    protected abstract Terrain loadTerrain(int x, int z);



    public final Terrain getLoadedTerrain(float x, float z) {
        return terrains.get(Terrain.posLong(Maths.floor(x, Terrain.SIZE), Maths.floor(z, Terrain.SIZE)));
    }
    public final Terrain getLoadedTerrain(long poslong) {
        return getLoadedTerrain((int)(poslong >> 32), (int)poslong);
    }



    public Terrain unloadTerrain(float x, float z) {
        Terrain terrain = getLoadedTerrain(x, z);

        Events.EVENT_BUS.post(new TerrainUnloadedEvent(terrain));

        terrains.remove(Terrain.posLong(terrain.x, terrain.z));

        return terrain;
    }
    public final Terrain unloadTerrain(long poslong) {
        return unloadTerrain((int)(poslong>>32), (int)poslong);
    }
    public final void unloadAllTerrains() {
        for (Long terrpos : terrains.keySet().toArray(new Long[0])) {
            unloadTerrain(terrpos);
        }
    }


    public final Collection<Terrain> getTerrains() {
        return terrains.values();
    }
}
