package ext.ntty;

import ext.ntty.pkt.CPacketKeepAliveResp;
import ext.ntty.pkt.SPacketKeepAlive;
import ext.ntty.pkt.SPacketTMPRedPrint;
import ext.ntty.pkt.SPacketTmpSendFileAndOpen;
import outskirts.client.Outskirts;
import outskirts.event.EventHandler;
import outskirts.network.ChannelHandler;
import outskirts.util.*;
import outskirts.util.logging.Log;

import java.io.*;

@SideOnly(Side.CLIENT)
public final class PacketHandlerTstClient {

    private ChannelHandler connection;

    public PacketHandlerTstClient(ChannelHandler connection) {
        this.connection = connection;
    }

    /**
     * 当当前连接接受到SPacketKeepAlive数据包时 本方法监听器将触发(因为已注册到连接上了)。
     * 本包为心跳检测，一旦客户端受到就返回给服务器 随后服务器将可检连接测延迟 以及检测是否在超时时间内收到回复
     */
    @EventHandler
    private void handleKeepalive(SPacketKeepAlive packet) {
        connection.sendPacket(new CPacketKeepAliveResp()); // just callback to server
    }

    /**
     * 接受到定制数据包 SPacketTmpSendFileAndOpen。本包内包含一个文件名 和文件内容bytes。
     * 收到该包后将包内的文件存储到系统的临时文件夹，并打开(事实上还是有些危险的这种操作 但这里也只是测试用了。。)
     */
    // 这里使用了事件处理器的非异步属性。因为关于系统的操作和IO操作可能偏大。 避免在netty的eventloop中
    @EventHandler(scheduler = Outskirts.class) // (may)huge IO operation AND system-opening operation, avoid in network eventloop
    private void handleFileAndOpen(SPacketTmpSendFileAndOpen packet) throws IOException {
        Log.info("Client OpenFileThread: " + Thread.currentThread()); // async=true is NettyIO thread

        // write to tmpdir
        File tmpDestfile = new File(FileUtils.getTempDirectory(), packet.getFilename());

        IOUtils.write(
                new ByteArrayInputStream(packet.getFiledata()),
                new FileOutputStream(tmpDestfile)
        );

        // open the file
        Log.info("Client: opening..");
        SystemUtils.openURL(tmpDestfile.getAbsolutePath());
    }

    /**
     * 当接收到RedPrint数据包。
     */
    @EventHandler
    private void handleRedPrint(SPacketTMPRedPrint packet) {

        Log.warn("ServerSent/RedPrint: "+packet.getPrintstuff());
    }

}
