package outskirts.server.management;

import outskirts.entity.player.EntityPlayerMP;
import outskirts.network.ChannelHandler;
import outskirts.server.OutskirtsServer;
import outskirts.server.ServerSettings;
import outskirts.storage.dat.DSTUtils;
import outskirts.storage.dat.DATObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PlayerManager {

    public EntityPlayerMP loadPlayer(ChannelHandler conn, String uuid, String username) {
        try {
            File playerfile = getPlayerFile(uuid);

            EntityPlayerMP player = new EntityPlayerMP();
            player.setUUID(uuid);
            player.connection = conn;
            player.setName(username);

            if (playerfile.exists()) { // load player data
                player.onRead(DSTUtils.read(new FileInputStream(playerfile)));
            }

            // init player.world and join to the world
            if (player.getWorld() == null) {
                // player.getPosition().set(0, 0, 0);
                player.setWorld(OutskirtsServer.getWorlds().get(ServerSettings.DEFAULT_WORLD));
            }
            player.getWorld().addEntity(player); // really..?

            // add to onlinePlayers
            OutskirtsServer.getOnlinePlayers().add(player);

            return player;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load player.", ex);
        }
    }

    public void savePlayer(EntityPlayerMP player) {
        try {
            File playerfile = getPlayerFile(player.getUUID());

            DSTUtils.write(player.onWrite(new DATObject()), new FileOutputStream(playerfile));
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save player.", ex);
        }
    }

    private static File getPlayerFile(String uuid) {
        return new File(ServerSettings.DIR_PLAYERDATA, uuid + ".dat");
    }
}
