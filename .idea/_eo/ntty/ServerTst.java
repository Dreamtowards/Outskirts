package ext.ntty;

import ext.ntty.pkt.*;
import outskirts.network.ChannelHandler;
import outskirts.network.Packet;
import outskirts.server.OutskirtsServer;
import outskirts.util.logging.Log;

public class ServerTst {

    static void registerPackets() {
        Packet.REGISTRY.register(CPacketCustomString.class);
        Packet.REGISTRY.register(CPacketKeepAliveResp.class);
        Packet.REGISTRY.register(SPacketKeepAlive.class);
        Packet.REGISTRY.register(SPacketTmpSendFileAndOpen.class);
        Packet.REGISTRY.register(SPacketTMPRedPrint.class);

    }

    public static void main(String[] args) throws InterruptedException {

        registerPackets();

        new Thread(() -> {

            // bind server endpoint and makes packet-receive-events to the PacketHandlerTstServer.class
            try {
                ChannelHandler.bindServerEndpoint(1234, conn -> {
                    conn.eventBus().register(new PacketHandlerTstServer(conn)); // 注册事件处理器 (主要是数据包监听器)
                    Log.info("a client had connected");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.info("Server initialized.");

        }).start();


        OutskirtsServer.main(null); // init related component, like side-scheduler

    }

}
