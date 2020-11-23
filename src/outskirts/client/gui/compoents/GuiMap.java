package outskirts.client.gui.compoents;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiButton;
import outskirts.client.gui.GuiDrag;
import outskirts.client.material.Texture;
import outskirts.event.EventHandler;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.ChunkPos;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class GuiMap extends GuiDrag {

    private Vector2f currentMapOffset = new Vector2f();
    private float scale = 1;

    private Vector2f currentBlock = new Vector2f();

    private Map<ChunkPos, Texture> mapChunkFrags = new HashMap<>();

    public GuiMap() {
        addOnDrawListener(this::onDlaw);
        addOnDraggingListener(e -> currentMapOffset.add(e.dx, e.dy));
        addMouseScrollListener(e -> {
            float oldscale = scale;
            scale = Maths.clamp(scale+Outskirts.getDScroll(), 0.2f, 50);
            float t = scale - oldscale;

            currentMapOffset.addScaled(t, new Vector2f(currentBlock).negate());
        });
        setClipChildren(true);

        addChildren(new GuiButton("Reset").exec(g -> {
            g.addOnClickListener(e -> {
                Outskirts.getWorld().getLoadedChunks().forEach(c -> {
                    BufferedImage bi = Outskirts.renderEngine.getMapRenderer().render(c);
                    Texture tex = Loader.loadTexture(bi);
                    mapChunkFrags.put(ChunkPos.of(c), tex);
                });
            });
            g.addLayoutorAlignParentRR(1,0);
        }));
    }

    @EventHandler
    private void onDlaw(OnDrawEvent e) {

        Vector2f morigin = morigin();

        for (ChunkPos chunkpos : mapChunkFrags.keySet()) {
            float x=chunkpos.x, y=chunkpos.z;

            drawTexture(mapChunkFrags.get(chunkpos), morigin.x +x*scale, morigin.y +y*scale, 16f*scale, 16f*scale);
        }
        drawRect(Colors.WHITE, morigin.x, morigin.y, 10,10);

        currentBlock.set(
                (Outskirts.getMouseX() - morigin.x) / scale,
                (Outskirts.getMouseY() - morigin.y) / scale);
        // Curr Chunk
        drawMappoint(Maths.floor(currentBlock.x, 16f), Maths.floor(currentBlock.y, 16f), (gp,s) -> {
            drawRect(Colors.BLACK10, gp.x, gp.y, 16f*s, 16f*s);
        });
        // Curr Block
        drawMappoint(Maths.floor(currentBlock.x), Maths.floor(currentBlock.y), (gp, s) -> {
            drawRect(Colors.BLACK40, gp.x, gp.y, s, s);
        });

        drawMappoint(Outskirts.getPlayer().position(), (gp,s) -> {
            drawString("PLAYER:"+Outskirts.getPlayer().getName(), gp.x,gp.y,Colors.WHITE);
        });

        drawString(String.format(
                "Scale: %s\n" +
                "Offset: Px-[%s, %s]\n" +
                "Pointing: BlockPos[%s, %s]",
                scale,
                currentMapOffset.x, currentMapOffset.y,
                currentBlock.x, currentBlock.y), getX()+20, getY()+20, Colors.WHITE);
    }

    private void drawMappoint(float x, float z, BiConsumer<Vector2f, Float> ondlaw) {
        ondlaw.accept(morigin().add(x*scale, z*scale), scale);
    }
    private void drawMappoint(Vector2f p, BiConsumer<Vector2f, Float> ondlaw) {
        drawMappoint(p.x, p.y, ondlaw);
    }
    private void drawMappoint(Vector3f p, BiConsumer<Vector2f, Float> ondlaw) {
        drawMappoint(p.x, p.z, ondlaw);
    }

    private Vector2f morigin() {
        return new Vector2f(getX()+getWidth()/2f, getY()+getHeight()/2f).add(currentMapOffset);
    }

}
