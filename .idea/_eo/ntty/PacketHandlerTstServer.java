package ext.ntty;

import ext.ntty.pkt.*;
import outskirts.event.EventHandler;
import outskirts.event.Events;
import outskirts.event.server.ServerTickEvent;
import outskirts.network.ChannelHandler;
import outskirts.server.OutskirtsServer;
import outskirts.util.IOUtils;
import outskirts.util.Side;
import outskirts.util.SideOnly;
import outskirts.util.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SideOnly(Side.SERVER)
public final class PacketHandlerTstServer {

    private static final int PING_TIMEOUT_THRESHOLD = 10 * 1000;
    private long lastPingSendTime;
    private long lastPingBackTime = -1;
    private int ping;

    private ChannelHandler connection;

    public PacketHandlerTstServer(ChannelHandler connection) {
        this.connection = connection;

        // 仅将onTick函数 注册到全局EVENT_BUS上。用于定期发送心跳和做相关检查/更新。
        // 但是channelHandler的EventBus不会投递onTick事件，因此运行上没有什么冲突。。
        Events.EVENT_BUS.register(this::onTick);
    }

    /**
     * 该事件由全局总线 系统Tick(嘀嗒) 事件发起投递。并非连接实例投递。Tick周期为1/20秒。(看上去很快)
     * 通常的应用程序可能不需要这么快的速度。但一些即时反应系统则要求高一些。
     */
    @EventHandler
    private void onTick(ServerTickEvent event) {

        // 检测心跳回应是否超时
        // checking pinging timeout
        if (lastPingBackTime != -1 && System.currentTimeMillis() - lastPingBackTime > PING_TIMEOUT_THRESHOLD) {
//            connection.closeChannel("Pinging timeout.");
            return;
        }

        // 定时发起ping/心跳包
        // sending ping
        lastPingSendTime = System.currentTimeMillis();
        connection.sendPacket(new SPacketKeepAlive());

    }

    /**
     * 接受到客户端回复的心跳回应 数据包
     */
    @EventHandler
    private void processKeepalive(CPacketKeepAliveResp packet) {
        lastPingBackTime = System.currentTimeMillis();
        ping = (int)(lastPingBackTime - lastPingSendTime);
    }

    /**
     * 处理客户端发来的自定义命令数据包
     */
    @EventHandler(scheduler = OutskirtsServer.class)
    private void processCustomString(CPacketCustomString packet) throws IOException {
        if (packet.getContent().startsWith("cmd_printfullcontent")) {

            Log.warn("Server Print: " + packet.getContent());

            connection.sendPacket(new SPacketTMPRedPrint("serverprintcontent> " + packet.getContent()));
        } else if (packet.getContent().startsWith("cmd_sendFile")) {

            String tmpFilepath = packet.getContent().substring("cmd_sendFile".length());
            File file = new File(tmpFilepath);
            Log.warn("sending " + tmpFilepath);
            connection.sendPacket(new SPacketTmpSendFileAndOpen(
                    file.getName(),
                    IOUtils.toByteArray(new FileInputStream(file))
            ));

            connection.sendPacket(new SPacketTMPRedPrint("sending " + tmpFilepath));
        } else if (packet.getContent().equals("reqping")) {
            Log.info("Server ping to the client: " + ping);

            connection.sendPacket(new SPacketTMPRedPrint("Server ping to the client: " + ping));
        } else {
            if (Math.random() < 0.0001f)
                Log.info(packet.getContent());

//            Log.warn("Server: received an unspecified content (%s) in %s", packet.getContent(), System.currentTimeMillis());

//            connection.sendPacket(new SPacketTMPRedPrint("Server: received an unspecified content"));
        }
    }

}
