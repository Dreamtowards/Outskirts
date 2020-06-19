package ext.ntty.pkt;

import outskirts.network.Packet;
import outskirts.network.PacketBuffer;

public class SPacketTmpSendFileAndOpen extends Packet {

    private String filename;

    private byte[] filedata;

    public SPacketTmpSendFileAndOpen() {}

    public SPacketTmpSendFileAndOpen(String filename, byte[] filedata) {
        this.filename = filename;
        this.filedata = filedata;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(filename);
        buf.writeByteArray(filedata);
    }

    @Override
    public void read(PacketBuffer buf) {
        filename = buf.readString();
        filedata = buf.readByteArray();
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getFiledata() {
        return filedata;
    }
}
