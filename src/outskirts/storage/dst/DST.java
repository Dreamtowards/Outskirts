package outskirts.storage.dst;

import outskirts.util.IOUtils;

import java.io.*;
import java.util.*;

import static outskirts.util.IOUtils.*;

/**
 * DST Binary Stroage Libs.
 *
 * DST Specificated 10 Types Object. 6 Primitives(4INT, 2FLOAT). 1 Stroage(BYTE_ARRAY). 1 String(STRING). 2 Struct(LIST, MAP).
 *
 * ?DAT
 *
 * BYTE  { int8 }
 * SHORT { int16 }
 * INT   { int32 }
 * LONG  { int64 }
 *
 * FLOAT  { float32 }
 * DOUBLE { float64 }
 *
 * BYTE_ARRAY {
 *     bytesCount: int32,
 *     [int8, int8, int8 ...]
 * }
 * STRING {
 *     bytesCount: int16,
 *     [int8, int8 ...]
 * }
 *
 * LIST {
 *     size: int16,
 *     type: int8,
 *     [{...} ...]
 * }
 * MAP {
 *     size: int16,
 *     ['STRING', type: int8, {...} ...]
 * }
 * goo.des Data Essential Structure
 *
 */

/*

        dreamtowards.apt
        dreamtowards.pax










        dreamtowards.playerdata.gem
        dreamtowards.gem GEM
        dreamtowards.gel GEL
        dreamtowards.gum GUM
        dreamtowards.jam JAM
        dreamtowards.jet JET
        dreamtowards.vet VET
        dreamtowards.jab JAB
        dreamtowards.ivy IVY
        dreamtowards.ode ODE
        dreamtowards.pat PAT
        dreamtowards.apo APO
        dreamtowards.bln BLN -
        dreamtowards.det DET
        dreamtowards.dex DEX
        dreamtowards.jim JIM
        dreamtowards.lum LUM -lw
        dreamtowards.lym LYM
        dreamtowards.obe OBE
        dreamtowards.obv OBV
        dreamtowards.oca OCA
        dreamtowards.ods ODS -norm
        dreamtowards.odd ODD
        dreamtowards.ope OPE -norm
        dreamtowards.obb OBB
        dreamtowards.oxy OXY -other field
        dreamtowards.rav RAV -of
        dreamtowards.rax RAX -of
        dreamtowards.rya RYA
        dreamtowards.tav TAV -sub
        dreamtowards.vae VAE
        dreamtowards.vax VAX
        dreamtowards.zek ZEK  APOObject

        dreamtowards.deo
        dreamtowards.dem
        dreamtowards.bin


        dreamtowards.sap SAP -
        dreamtowards.jay JAY -
        dreamtowards.bal BAL -
        dreamtowards.oba OBA -rou
        dreamtowards.ken KEN -usrnm

        dreamtowards.nux NUX
        dreamtowards.vag VAG

        dreamtowards.lar LAR
        dreamtowards.olm OLM
 */
public class DST {

    private static final int VERS = 9;

    public static final String[] TYPES_NAME = {null, "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE_ARRAY", "STRING", "LIST", "MAP"};

    public static final Class<?>[] TYPES = {null, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, byte[].class, String.class, List.class, Map.class};


    public static final byte NULL = 0;
    public static final byte BYTE = 1;
    public static final byte SHORT = 2;
    public static final byte INT = 3;
    public static final byte LONG = 4;
    public static final byte FLOAT = 5;
    public static final byte DOUBLE = 6;
    public static final byte BYTE_ARRAY = 7;
    public static final byte STRING = 8;
    public static final byte LIST = 9;
    public static final byte MAP = 10;

    public static Object read(InputStream is, byte type) throws IOException {
        switch (type) {
            case BYTE:
                return readByte(is);
            case SHORT:
                return readShort(is);
            case INT:
                return readInt(is);
            case LONG:
                return readLong(is);
            case FLOAT:
                return Float.intBitsToFloat(readInt(is));
            case DOUBLE:
                return Double.longBitsToDouble(readLong(is));
            case BYTE_ARRAY:
                return IOUtils.readFully(is, new byte[readInt(is)]);
            case STRING:
                return IOUtils.readUTF(is);
            case LIST: {
                int size = readInt(is);
                byte lsType = readByte(is);
                List<Object> ls = new ArrayList<>(size);
                for (int i = 0;i < size;i++) {
                    ls.add(DST.read(is, lsType));
                }
                return new DArray(ls); }
            case MAP: {
                int size = readInt(is);
                Map<String, Object> mp = new HashMap<>(size);
                for (int i = 0;i < size;i++) {
                    String k = readUTF(is);
                    byte vType = readByte(is);
                    Object v = DST.read(is, vType);
                    mp.put(k, v);
                }
                return new DObject(mp); }
            default:
                throw new IllegalArgumentException("Illegal type.");
        }
    }

    public static void write(OutputStream os, byte type, Object obj) throws IOException {
        switch (type) {
            case BYTE:
                IOUtils.writeByte(os, (byte)obj); break;
            case SHORT:
                IOUtils.writeShort(os, (short)obj); break;
            case INT:
                IOUtils.writeInt(os, (int)obj); break;
            case LONG:
                IOUtils.writeLong(os, (long)obj); break;
            case FLOAT:
                IOUtils.writeInt(os, Float.floatToIntBits((float)obj)); break;
            case DOUBLE:
                IOUtils.writeLong(os, Double.doubleToLongBits((double)obj)); break;
            case BYTE_ARRAY: {
                byte[] arr = (byte[])obj;
                IOUtils.writeInt(os, arr.length);
                IOUtils.writeFully(os, arr); break; }
            case STRING:
                IOUtils.writeUTF(os, (String)obj); break;
            case LIST: {
                List ls = (List)obj;
                writeInt(os, ls.size());
                byte lsType = ls.isEmpty()?NULL: DST.type(ls.get(0));
                writeByte(os, lsType);
                for (Object ite : ls) {
                    DST.write(os, lsType, ite);
                }
                break; }
            case MAP: {
                Map<String, Object> mp = (Map)obj;
                writeInt(os, mp.size());
                for (Map.Entry<String, Object> e : mp.entrySet()) {
                    writeUTF(os, e.getKey());
                    byte vType = DST.type(e.getValue());
                    writeByte(os, vType);
                    DST.write(os, vType, e.getValue());
                }
                break; }
            default:
                throw new IllegalArgumentException("Illegal type.");
        }
    }

    public static byte type(Object obj) {
        Class<?> cls = obj.getClass();
        for (byte t = BYTE;t <= STRING;t++) {
            if (cls == TYPES[t]) return t;
        }
        if (obj instanceof List)
            return LIST;
        if (obj instanceof Map)
            return MAP;
        throw new IllegalArgumentException("Illegal type.");
    }

    public static String toString(Object obj) {
        return DST.type(obj) == BYTE_ARRAY ? Arrays.toString((byte[])obj) : obj.toString();
    }

//    public static boolean isPrimitive(byte type) {
//        return false;
//    }

    public static boolean isStruct(byte type) {
        return type==LIST || type==MAP;
    }





}
