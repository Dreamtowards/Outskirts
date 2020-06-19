package outskirts.server;

import outskirts.command.Command;
import outskirts.command.CommandSender;
import outskirts.command.ConsoleCommandSender;
import outskirts.entity.player.EntityPlayerMP;
import outskirts.event.Events;
import outskirts.event.server.ServerTickEvent;
import outskirts.init.Init;
import outskirts.network.ChannelHandler;
import outskirts.network.login.PacketHandlerLoginServer;
import outskirts.server.management.PlayerList;
import outskirts.server.management.PlayerManager;
import outskirts.util.GameTimer;
import outskirts.util.Side;
import outskirts.util.SystemUtils;
import outskirts.util.Validate;
import outskirts.util.concurrent.Scheduler;
import outskirts.util.logging.Log;
import outskirts.world.WorldServer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class OutskirtsServer {

    private static OutskirtsServer INSTANCE;

    private boolean running;

    private Thread thread = Thread.currentThread();
    private Scheduler scheduler = new Scheduler(thread);

    private PlayerList onlinePlayers = new PlayerList();
    private PlayerManager playerManager = new PlayerManager();

    private Map<String, WorldServer> worlds = new HashMap<>();

    public void run() {
        try
        {
            this.startServer();

            while (this.running)
            {
                long startnano = System.nanoTime();

                this.runTick();

                this.sync(startnano, 1_000_000_000 / GameTimer.TPS);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            this.stopServer();
        }
    }

    private void startServer() throws InterruptedException {

        this.running = true;
        INSTANCE = this;
        Thread.currentThread().setName("Server thread");

        Log.info("Init Registrations...");

        Init.registerAll(Side.SERVER);

        Log.info("Init world...");
        loadWorld(ServerSettings.DEFAULT_WORLD);

        Log.info("Starting server in port %s", ServerSettings.SERVER_PORT);

        ChannelHandler.bindServerEndpoint(ServerSettings.SERVER_PORT, conn -> {
            conn.eventBus().register(new PacketHandlerLoginServer(conn));
        });

        this.startConsoleInputThread();

    }

    private void runTick() {

        Events.EVENT_BUS.post(new ServerTickEvent());

        scheduler.processTasks();

        for (WorldServer world : worlds.values()) {

            world.onTick();

        }



    }

    private void stopServer() {
        Log.info("Stopping server...");

        Log.info("Saving players..");
        for (EntityPlayerMP player : getOnlinePlayers()) {
            // todo saves players
        }

        Log.info("Saving worlds...");
        for (WorldServer world : worlds.values()) {
            Log.info("- saving \"%s\"...", world.getRegistryID());
            world.unloadAllTerrains();
        }

        Log.info("Server stopped.");
    }

    public static void addScheduledTask(Runnable runnable) {
        INSTANCE.scheduler.addScheduledTask(runnable);
    }

    public static Scheduler getScheduler() {
        return INSTANCE.scheduler;
    }

    public static WorldServer loadWorld(String worldname) {
        Validate.validState(!getWorlds().containsKey(worldname), "World \"%s\" already been loaded.", worldname);

        return getWorlds().put(worldname, new WorldServer(new File(ServerSettings.DIR_SAVES, worldname)));
    }

    public static Map<String, WorldServer> getWorlds() {
        return INSTANCE.worlds;
    }

    public static PlayerManager getPlayerManager() {
        return INSTANCE.playerManager;
    }

    public static PlayerList getOnlinePlayers() {
        return INSTANCE.onlinePlayers;
    }

    public static boolean isRunning() {
        return INSTANCE.running;
    }

    public static void shutdown() {
        INSTANCE.running = false;
    }


    private void startConsoleInputThread() {
        CommandSender sender = new ConsoleCommandSender();

        Thread thread = new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String line;
                while ((line = br.readLine()) != null) {
                    Command.dispatchCommand(sender, line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }, "ServerConsoleInputThread");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * @param startnano step start time in nanotime
     * @param stepnano fixed step length in nanotime
     */
    private void sync(long startnano, long stepnano) throws InterruptedException {
        long usednano = System.nanoTime() - startnano;

        if (usednano > stepnano) {
            long overload = usednano - stepnano;
            Log.warn("Server overload! tick over %sms (used %sms), skipping %s tick(s).", overload/1_000_000, usednano/1_000_000, overload/stepnano);
        } else {
            SystemUtils.nanosleep(stepnano - usednano);
        }
    }









    public static void main(String[] args) {

        Side.CURRENT = Side.SERVER;

        new OutskirtsServer().run();
    }

}
