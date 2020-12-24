package outskirts.init;

import outskirts.block.Block;
import outskirts.client.Loader;
import outskirts.client.material.Texture;
import outskirts.command.Command;
import outskirts.command.server.*;
import outskirts.entity.Entity;
import outskirts.entity.EntityStaticMesh;
import outskirts.entity.player.EntityPlayerMP;
import outskirts.entity.player.EntityPlayerSP;
import outskirts.init.ex.Models;
import outskirts.item.Item;
import outskirts.item.ItemBlock;
import outskirts.network.Packet;
import outskirts.network.login.packet.CPacketLogin;
import outskirts.network.login.packet.SPacketDisconnect;
import outskirts.network.login.packet.SPacketLoginSuccess;
import outskirts.network.play.packet.SPacketChatMessage;
import outskirts.util.IOUtils;
import outskirts.util.Identifier;
import outskirts.util.Side;
import outskirts.util.logging.Log;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class Init {

    private static void registerPackets() {


        Packet.registerPacket(CPacketLogin.class);
        Packet.registerPacket(SPacketDisconnect.class);
        Packet.registerPacket(SPacketLoginSuccess.class);

        Packet.registerPacket(outskirts.network.play.packet.SPacketDisconnect.class);
        Packet.registerPacket(SPacketChatMessage.class);


        Packet.buildRegistry();
    }

    private static void registerCommands() {

        Command.REGISTRY.register(new CommandShutdown());
        Command.REGISTRY.register(new CommandKick());
        Command.REGISTRY.register(new CommandSummon());
        Command.REGISTRY.register(new CommandPlayerlist());
        Command.REGISTRY.register(new CommandSay());

    }



    private static void registerEntities(Side side) {

        Entity.REGISTRY.register(side.isClient() ? new EntityPlayerSP() : new EntityPlayerMP());

        Entity.REGISTRY.register(new EntityStaticMesh());
    }


    //use event to register/release..?
    public static void registerAll(Side side) {

        Blocks.init();

        if (side.isClient()) {
            for (String id : Block.REGISTRY.keys()) {
                Block.REGISTRY.get(id).theTxFrag =
                        Block.TEXTURE_ATLAS.register(
                                Loader.loadPNG(new Identifier("materials/mc/"+new Identifier(id).getPath()+".png").getInputStream()));
            }
            Block.TEXTURE_ATLAS.buildAtlas();

            try {
                IOUtils.write(new ByteArrayInputStream(Loader.savePNG(Texture.glfGetTexImage(Block.TEXTURE_ATLAS.getAtlasTexture()))), new FileOutputStream("blxatlas.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        Items.init();

        for (Block b : Block.REGISTRY.values()) {
            Item.REGISTRY.register(new ItemBlock(b));
        }

        if (side.isClient()) {

            Sounds.init();

            Textures.init();

            Models.init();

        } else {

            registerCommands();
        }

        registerPackets();

        registerEntities(side);
    }

}
