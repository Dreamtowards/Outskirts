package outskirts.network.login.packet;

import outskirts.network.Packet;
import outskirts.network.PacketBuffer;

public class CPacketLogin extends Packet {

    private String uuid;
    private String token;
    private long protocol;

    public CPacketLogin() {}

    /**
     * there the "token" just almost equals "password".
     * but just always uses hex_string of hash/digest of password. hex_str(hash("password"))
     */
    public CPacketLogin(String uuid, String token, long protocolDigest) {
        this.uuid = uuid;
        this.token = token;
        this.protocol = protocolDigest;
    }

    @Override
    public void read(PacketBuffer buf) {
        uuid = buf.readString();
        token = buf.readString();
        protocol = buf.readLong();
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(uuid);
        buf.writeString(token);
        buf.writeLong(protocol);
    }

    public String getUUID() {
        return uuid;
    }

    public String getToken() {
        return token;
    }

    public long getProtocol() {
        return protocol;
    }
}
