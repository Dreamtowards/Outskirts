package outskirts.lang.langdev.compiler.codegen;

import outskirts.util.IOUtils;
import outskirts.util.Intptr;

import java.util.Locale;

public final class Opcodes {

    public static final byte
            _NULL = 0,
            STORE = 1,
            LOAD = 2,
            LDC = 3,
            DUP = 4,
            INVOKEFUNC = 5,
            JMP = 6,
            JMP_F = 7,
            I32ADD = 8,
            ICMP = 9,
            CMP_EQ = 10,
            CMP_NE = 11,
            CMP_LT = 12,
            CMP_GT = 13,
            CMP_LE = 14,
            CMP_GE = 15,
            POP = 16,
            I32MUL = 17;

    public static final String[] _NAMES = {
            "_NULL",
            "STORE",
            "LOAD",
            "LDC",
            "DUP",
            "INVOKEFUNC",
            "JMP",
            "JMP_F",
            "I32ADD",
            "ICMP",
            "CMP_EQ", "CMP_NE", "CMP_LT", "CMP_GT", "CMP_LE", "CMP_GE",
            "POP",
            "I32MUL"
    };


    public static String _InstructionComment(CodeBuf buf, Intptr idx) {
        byte[] code = buf.toByteArray();

        String head = String.format("#%-3s %-8s", idx.i, _NAMES[code[idx.i]].toLowerCase());
        String comm = ""; //"unsupported.";
        switch (code[idx.i++]) {
            case STORE:
            case LOAD: comm = "slot: "+ code[idx.i++]; break;
            case LDC: {
                short i = IOUtils.readShort(code, idx.i);
                comm = "cidx: "+i+ " // "+buf.constantpool.get(i);
                idx.i+=2;
                break;
            }
            case JMP:
            case JMP_F: comm = "to: #"+IOUtils.readShort(code, idx.i); idx.i+=2; break;
            case DUP:
            case POP:  comm = "n="+code[idx.i++]; break;
        }
        return head + (comm.isEmpty() ? "" : "// "+comm);
    }
}
