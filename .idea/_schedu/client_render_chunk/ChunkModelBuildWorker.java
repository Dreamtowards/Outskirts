package outskirts.client.render.chunk;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.material.ModelData;
import outskirts.world.chunk.Octree;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChunkModelBuildWorker implements Runnable {

    private Queue<RenderSection> queueSections = new ConcurrentLinkedQueue<>();

    @Override
    public void run() {
        try
        {
            while (true)
            {
                if (!queueSections.isEmpty())
                {
                    buildAndUpload(queueSections.poll());
                }

                //if no any interval, if-expression probably not be exec as expect
                Thread.sleep(1);
            }
        }
        catch (Throwable t)
        {
            throw new RuntimeException("ChunkModelBuildWorker occurred fatal exception and stop working.", t);
        }
    }

    public void queueBuildTask(RenderSection renderSection) {
        queueSections.offer(renderSection);
    }

    private void buildAndUpload(RenderSection renderSection) {

        Octree octree = Outskirts.getWorld()
                .getLoadedChunk(renderSection.getPosition().x, renderSection.getPosition().z)
                .octree(renderSection.getPosition().y);

//        if (octree==null)return;
        ModelData modelData = ChunkModelGenerator.INSTANCE.generateModel(
                Outskirts.getWorld(),
                octree);

        renderSection.setEmpty(modelData.positions.length == 0);

        Outskirts.addScheduledTask(() -> {
            renderSection.setModel(
                    Loader.loadModel(modelData.indices, modelData.positions, modelData.textureCoords, modelData.normals)
            );
//            Log.info("Uploaded Modeled RenderSection.");
        });


    }
}
