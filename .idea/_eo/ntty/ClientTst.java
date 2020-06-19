package ext.ntty;

import ext.ntty.pkt.CPacketCustomString;
import outskirts.client.main.Main;
import outskirts.network.ChannelHandler;
import outskirts.util.logging.Log;

import java.io.*;

public class ClientTst {

    public static void main(String[] args) throws InterruptedException {

        // 先注册包
        ServerTst.registerPackets();


        // 粗略异步加载测试代码，若不异步将会和主体程序堵上
        new Thread(() -> {
            try {
                ChannelHandler conn = ChannelHandler.createConnection("localhost", 1234);

                // 给连接实例的 EventBus 注册事件监听器。 PacketHandlerTstClient可译为 "数据包监听器测试客户端"
                conn.eventBus().register(new PacketHandlerTstClient(conn));

                Log.info("Client initialized.");

                Log.info("start sending 1,000,000 packets. curr: %s", System.currentTimeMillis());
                for (int i = 0;i < 1_000_000;i++) {
                    conn.sendPacket(new CPacketCustomString(String.valueOf(i)));
                    if (i % 10_000 == 0)
                        Log.info(i);
                }
                Log.info("sending test finished. curr: %s", System.currentTimeMillis());


                // 命令输入监听

                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String line;
                while ((line = br.readLine()) != null) {
                    Log.info("Client inputline: " + line);
                    // 当有命令行输入时 发送自定义命令数据包到服务器(连接对面)
                    conn.sendPacket(new CPacketCustomString(line));
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();


        // 粗略加载框架

        // init some sutff
        System.setProperty("org.lwjgl.librarypath", new File("libraries/lwjgl-2.9.3/windows").getAbsolutePath());
        Main.main(args);

        // 测试命令：
        // cmd_printfullcontentABCABC    //让服务端打印文字 也让客户端也打印一遍通过服务器的命令
        // reqping                       //查询延迟ping/ms
        // cmd_sendFileF:\Projects\_RESP\LICENSE.txt  //让服务器发来一个文件并打开
    }

}
