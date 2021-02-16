package outskirts.init;

import outskirts.init.ex.Models;
import outskirts.network.Packet;
import outskirts.network.login.packet.CPacketLogin;
import outskirts.network.login.packet.SPacketDisconnect;
import outskirts.network.login.packet.SPacketLoginSuccess;
import outskirts.network.play.packet.SPacketChatMessage;
import outskirts.util.Side;

public final class Init {


    //use event to register/release..?
    public static void registerAll(Side side) {

        Materials.init();  MaterialTextures.init();

        Items.init();
//        for (Block b : Block.REGISTRY.values()) {
//            Item.REGISTRY.register(new ItemBlock(b));
//        }

        Commands.init();

        if (side.isClient()) {

            Sounds.init();

            Textures.init();

            Models.init();
        }

        registerPackets();

        Entities.init();

    }


    private static void registerPackets() {


        //todo: refact
        Packet.registerPacket(CPacketLogin.class);
        Packet.registerPacket(SPacketDisconnect.class);
        Packet.registerPacket(SPacketLoginSuccess.class);

        Packet.registerPacket(outskirts.network.play.packet.SPacketDisconnect.class);
        Packet.registerPacket(SPacketChatMessage.class);


        Packet.buildRegistry();
    }

}
