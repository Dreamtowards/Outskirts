package outskirts.init;

import outskirts.network.Packet;
import outskirts.network.login.packet.CPacketLogin;
import outskirts.network.login.packet.SPacketDisconnect;
import outskirts.network.login.packet.SPacketLoginSuccess;
import outskirts.network.play.packet.SPacketChatMessage;

public final class Packets {

    private static void register(Packet p) {
        Packet.REGISTRY.register(p);
    }

    public static void init() {

        // LOGIN
        register(new CPacketLogin());
        register(new SPacketDisconnect());
        register(new SPacketLoginSuccess());

        // PLAY
        register(new outskirts.network.play.packet.SPacketDisconnect());
        register(new SPacketChatMessage());


        Packet.buildRegistry();

    }

}
