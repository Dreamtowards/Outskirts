package outskirts.client.gui.compoents;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.gui.GuiCheckBox;
import outskirts.client.gui.GuiDrag;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.render.Texture;
import outskirts.event.EventHandler;
import outskirts.event.world.chunk.ChunkMeshBuiltEvent;
import outskirts.util.BitmapImage;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;
import outskirts.world.Chunk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class GuiMap extends GuiDrag {

    private Vector2f currentMapOffset = new Vector2f();
    private float scale = 1;

    private Vector2f currentBlock = new Vector2f();

//    private Set<ChunkPos> texUpdateQueue = new HashSet<>();
//    private Map<ChunkPos, Texture> mapChunkFrags = new HashMap<>();

    public GuiMap() {
//        addOnDrawListener(this::onDlaw);
//        addOnDraggingListener(e -> {
//            currentMapOffset.add(e.dx, e.dy);
//            requestDraw();
//        });
//        addMouseWheelListener(e -> {
//            float oldscale = scale;
//            scale = Maths.clamp(scale+e.getDScroll(), 0.2f, 50);
//            float t = scale - oldscale;
//
//            currentMapOffset.addScaled(t, new Vector2f(currentBlock).negate());
//            requestDraw();
//        });
//        setClipping(true);
//
//        addOnAttachListener(e -> {
//            Outskirts.getWorld().getLoadedChunks().forEach(this::loadChunkImage);
//        });
//
//        addGlobalEventListener(ChunkMeshBuiltEvent.class, e -> {
//            texUpdateQueue.add(ChunkPos.of(e.getChunk()));
//        });
//
//        addChildren(
//                new GuiColumn().exec(g -> {
//                    g.addLayoutorAlignParentLTRB(NaN, 16, 32, NaN);
//                }).addChildren(
//                        new GuiCheckBox("Height Clip .UDR"),
//                        new GuiCheckBox("Active Chunks INFO.")
//                )
//        );
    }

//    private void loadChunkImage(Chunk chunk) {
//        BitmapImage bi = Outskirts.renderEngine.getMapRenderer().render(chunk);
//        mapChunkFrags.put(chunk.getPosition(), Loader.loadTexture(bi));
//    }
//
//    @EventHandler
//    private void onDlaw(OnDrawEvent e) {
//
////        for (ChunkPos cpos : texUpdateQueue) {
////            loadChunkImage(Outskirts.getWorld().getLoadedChunk(cpos));
////        }
////        texUpdateQueue.clear();
////
////
////        Vector2f morigin = morigin();
////
////        for (ChunkPos chunkpos : mapChunkFrags.keySet()) {
////            float x=chunkpos.x, y=chunkpos.z;
////
////            drawTexture(mapChunkFrags.get(chunkpos), morigin.x +x*scale, morigin.y +y*scale, 16f*scale, 16f*scale);
////
////            if (Outskirts.getWorld().getLoadedChunk(x, y) != null) {
////                drawMappoint(x,y, (gp, s) -> {
////                    drawRect(new Vector4f(0,1,0,0.1f), gp.x, gp.y, 16f*s, 16f*s);
////                });
////            }
////        }
////        drawRect(Colors.WHITE, morigin.x, morigin.y, 10,10);
////
////        currentBlock.set(
////                (Outskirts.getMouseX() - morigin.x) / scale,
////                (Outskirts.getMouseY() - morigin.y) / scale);
////        // Curr Chunk
////        drawMappoint(Maths.floor(currentBlock.x, 16f), Maths.floor(currentBlock.y, 16f), (gp,s) -> {
////            drawRect(Colors.WHITE20, gp.x, gp.y, 16f*s, 16f*s);
////        });
////        // Curr Block
////        drawMappoint(Maths.floor(currentBlock.x), Maths.floor(currentBlock.y), (gp, s) -> {
////            drawRect(Colors.WHITE20, gp.x, gp.y, s, s);
////        });
////
////        drawMappoint(Outskirts.getPlayer().position(), (gp,s) -> {
////            drawString(Outskirts.getPlayer().getName(), gp.x,gp.y,Colors.WHITE, 1.2f*s);
////        });
//
//        drawString(String.format(
//                "Scale: %s\n"+
//                "Offset: Px-[%s, %s]\n" +
//                "Pointing: BlockPos[%s, %s]",
//                scale,
//                currentMapOffset.x, currentMapOffset.y,
//                currentBlock.x, currentBlock.y), getX()+20, getY()+20, Colors.WHITE);
//    }
//
//    private void drawMappoint(float x, float z, BiConsumer<Vector2f, Float> ondlaw) {
//        ondlaw.accept(morigin().add(x*scale, z*scale), scale);
//    }
//    private void drawMappoint(Vector2f p, BiConsumer<Vector2f, Float> ondlaw) {
//        drawMappoint(p.x, p.y, ondlaw);
//    }
//    private void drawMappoint(Vector3f p, BiConsumer<Vector2f, Float> ondlaw) {
//        drawMappoint(p.x, p.z, ondlaw);
//    }
//
//    private Vector2f morigin() {
//        return new Vector2f(getX()+getWidth()/2f, getY()+getHeight()/2f).add(currentMapOffset);
//    }

}
