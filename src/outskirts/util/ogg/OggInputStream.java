package outskirts.util.ogg;


import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class OggInputStream extends InputStream {
    private int convsize = 16384;
    private byte[] convbuffer;
    private InputStream input;
    private Info oggInfo;
    private boolean endOfStream;
    private SyncState syncState;
    private StreamState streamState;
    private Page page;
    private Packet packet;
    private Comment comment;
    private DspState dspState;
    private Block vorbisBlock;
    byte[] buffer;
    int bytes;
    boolean bigEndian;
    boolean endOfBitStream;
    boolean inited;
    private int readIndex;
    private ByteBuffer pcmBuffer;
    private int total;

    public OggInputStream(InputStream input) throws IOException {
        this.convbuffer = new byte[this.convsize];
        this.oggInfo = new Info();
        this.syncState = new SyncState();
        this.streamState = new StreamState();
        this.page = new Page();
        this.packet = new Packet();
        this.comment = new Comment();
        this.dspState = new DspState();
        this.vorbisBlock = new Block(this.dspState);
        this.bytes = 0;
        this.bigEndian = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
        this.endOfBitStream = true;
        this.inited = false;
        this.pcmBuffer = BufferUtils.createByteBuffer(2048000);
        this.input = input;
        this.total = input.available();
        this.init();
    }

    public int getLength() {
        return this.total;
    }

    public int getChannels() {
        return this.oggInfo.channels;
    }

    public int getRate() {
        return this.oggInfo.rate;
    }

    private void init() throws IOException {
        this.initVorbis();
        this.readPCM();
    }

    public int available() {
        return this.endOfStream ? 0 : 1;
    }

    private void initVorbis() {
        this.syncState.init();
    }

    private boolean getPageAndPacket() {
        int index = this.syncState.buffer(4096);
        this.buffer = this.syncState.data;
        if (this.buffer == null) {
            this.endOfStream = true;
            return false;
        } else {
            try {
                this.bytes = this.input.read(this.buffer, index, 4096);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Failure reading in vorbis");
                this.endOfStream = true;
                return false;
            }

            this.syncState.wrote(this.bytes);
            if (this.syncState.pageout(this.page) != 1) {
                if (this.bytes < 4096) {
                    return false;
                } else {
                    System.err.println("Input does not appear to be an Ogg bitstream.");
                    this.endOfStream = true;
                    return false;
                }
            } else {
                this.streamState.init(this.page.serialno());
                this.oggInfo.init();
                this.comment.init();
                if (this.streamState.pagein(this.page) < 0) {
                    System.err.println("Error reading first page of Ogg bitstream data.");
                    this.endOfStream = true;
                    return false;
                } else if (this.streamState.packetout(this.packet) != 1) {
                    System.err.println("Error reading initial header packet.");
                    this.endOfStream = true;
                    return false;
                } else if (this.oggInfo.synthesis_headerin(this.comment, this.packet) < 0) {
                    System.err.println("This Ogg bitstream does not contain Vorbis audio data.");
                    this.endOfStream = true;
                    return false;
                } else {
                    int i = 0;

                    while(i < 2) {
                        label84:
                        while(true) {
                            int result;
                            do {
                                if (i >= 2) {
                                    break label84;
                                }

                                result = this.syncState.pageout(this.page);
                                if (result == 0) {
                                    break label84;
                                }
                            } while(result != 1);

                            this.streamState.pagein(this.page);

                            while(i < 2) {
                                result = this.streamState.packetout(this.packet);
                                if (result == 0) {
                                    break;
                                }

                                if (result == -1) {
                                    System.err.println("Corrupt secondary header.  Exiting.");
                                    this.endOfStream = true;
                                    return false;
                                }

                                this.oggInfo.synthesis_headerin(this.comment, this.packet);
                                ++i;
                            }
                        }

                        index = this.syncState.buffer(4096);
                        this.buffer = this.syncState.data;

                        try {
                            this.bytes = this.input.read(this.buffer, index, 4096);
                        } catch (Exception var4) {
                            System.err.println("Failed to read Vorbis: ");
                            System.err.println(var4);
                            this.endOfStream = true;
                            return false;
                        }

                        if (this.bytes == 0 && i < 2) {
                            System.err.println("End of file before finding all Vorbis headers!");
                            this.endOfStream = true;
                            return false;
                        }

                        this.syncState.wrote(this.bytes);
                    }

                    this.convsize = 4096 / this.oggInfo.channels;
                    this.dspState.synthesis_init(this.oggInfo);
                    this.vorbisBlock.init(this.dspState);
                    return true;
                }
            }
        }
    }

    private void readPCM() throws IOException {
        boolean wrote = false;

        while(true) {
            if (this.endOfBitStream) {
                if (!this.getPageAndPacket()) {
                    this.syncState.clear();
                    this.endOfStream = true;
                    return;
                }

                this.endOfBitStream = false;
            }

            if (!this.inited) {
                this.inited = true;
                return;
            }

            float[][][] _pcm = new float[1][][];
            int[] _index = new int[this.oggInfo.channels];

            while(!this.endOfBitStream) {
                int result;
                label135:
                while(true) {
                    label133:
                    while(true) {
                        if (this.endOfBitStream) {
                            break label135;
                        }

                        result = this.syncState.pageout(this.page);
                        if (result == 0) {
                            break label135;
                        }

                        if (result == -1) {
                            System.err.println("Corrupt or missing data in bitstream; continuing...");
                        } else {
                            this.streamState.pagein(this.page);

                            while(true) {
                                do {
                                    result = this.streamState.packetout(this.packet);
                                    if (result == 0) {
                                        if (this.page.eos() != 0) {
                                            this.endOfBitStream = true;
                                        }

                                        if (!this.endOfBitStream && wrote) {
                                            return;
                                        }
                                        continue label133;
                                    }
                                } while(result == -1);

                                if (this.vorbisBlock.synthesis(this.packet) == 0) {
                                    this.dspState.synthesis_blockin(this.vorbisBlock);
                                }

                                int samples;
                                while((samples = this.dspState.synthesis_pcmout(_pcm, _index)) > 0) {
                                    float[][] pcm = _pcm[0];
                                    int bout = samples < this.convsize ? samples : this.convsize;

                                    int i;
                                    for(i = 0; i < this.oggInfo.channels; ++i) {
                                        int ptr = i * 2;
                                        int mono = _index[i];

                                        for(int j = 0; j < bout; ++j) {
                                            int val = (int)((double)pcm[i][mono + j] * 32767.0D);
                                            if (val > 32767) {
                                                val = 32767;
                                            }

                                            if (val < -32768) {
                                                val = -32768;
                                            }

                                            if (val < 0) {
                                                val |= 32768;
                                            }

                                            if (this.bigEndian) {
                                                this.convbuffer[ptr] = (byte)(val >>> 8);
                                                this.convbuffer[ptr + 1] = (byte)val;
                                            } else {
                                                this.convbuffer[ptr] = (byte)val;
                                                this.convbuffer[ptr + 1] = (byte)(val >>> 8);
                                            }

                                            ptr += 2 * this.oggInfo.channels;
                                        }
                                    }

                                    i = 2 * this.oggInfo.channels * bout;
                                    if (i >= this.pcmBuffer.remaining()) {
                                        System.err.println("Read block from OGG that was too big to be buffered: " + i);
                                    } else {
                                        this.pcmBuffer.put(this.convbuffer, 0, i);
                                    }

                                    wrote = true;
                                    this.dspState.synthesis_read(bout);
                                }
                            }
                        }
                    }
                }

                if (!this.endOfBitStream) {
                    this.bytes = 0;
                    result = this.syncState.buffer(4096);
                    if (result >= 0) {
                        this.buffer = this.syncState.data;

                        try {
                            this.bytes = this.input.read(this.buffer, result, 4096);
                        } catch (Exception var13) {
                            System.err.println("Failure during vorbis decoding");
                            System.err.println(var13);
                            this.endOfStream = true;
                            return;
                        }
                    } else {
                        this.bytes = 0;
                    }

                    this.syncState.wrote(this.bytes);
                    if (this.bytes == 0) {
                        this.endOfBitStream = true;
                    }
                }
            }

            this.streamState.clear();
            this.vorbisBlock.clear();
            this.dspState.clear();
            this.oggInfo.clear();
        }
    }

    public int read() throws IOException {
        if (this.readIndex >= this.pcmBuffer.position()) {
            this.pcmBuffer.clear();
            this.readPCM();
            this.readIndex = 0;
        }

        if (this.readIndex >= this.pcmBuffer.position()) {
            return -1;
        } else {
            int value = this.pcmBuffer.get(this.readIndex);
            if (value < 0) {
                value += 256;
            }

            ++this.readIndex;
            return value;
        }
    }

    public boolean atEnd() {
        return this.endOfStream && this.readIndex >= this.pcmBuffer.position();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        for(int i = 0; i < len; ++i) {
            try {
                int value = this.read();
                if (value < 0) {
                    if (i == 0) {
                        return -1;
                    }

                    return i;
                }

                b[i] = (byte)value;
            } catch (IOException var6) {
                System.err.println(var6);
                return i;
            }
        }

        return len;
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    public void close() throws IOException {
    }
}

