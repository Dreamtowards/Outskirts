package outskirts.lang.langdev.compiler.codegen;

import outskirts.util.IOUtils;
import outskirts.util.Intptr;
import outskirts.util.Validate;

public final class Opcodes {

    public static final byte
            _NULL = 0,
//            LSTOREP = 1,
            LLOADP = 2,
            LDC = 3,
            DUP = 4,
            INVOKEFUNC = 5,
            JMP = 6,
            JMP_F = 7,
            ADD_I32 = 8,
            ICMP = 9,
            CMP_EQ = 10,
            CMP_NE = 11,
            CMP_LT = 12,
            CMP_GT = 13,
            CMP_LE = 14,
            CMP_GE = 15,
            POP = 16,
            MUL_I32 = 17,
            LDPTR = 18,
            STPTR = 19,
            STACKALLOC = 20,
            GETFIELD = 21,
            PUTFIELD = 22,
            POPCPY = 23,
            PTRCPY = 24,
            LOADV = 25,
            STKPTR_OFF = 27,
            RET = 28,
            INC_I32 = 29,
            DEC_I32 = 30,
            SUB_I32 = 31,
            MALLOC = 32,
            CAST_I8_I32 = 33,
            CAST_I32_I8 = 34,
            STATIC_ADDR = 35;

    public static final String[] _NAMES = {
            "_NULL",
            "_UNDEFINED",
            "LLOADP",
            "LDC",
            "DUP",
            "INVOKEPROC",
            "JMP",
            "JMP_F",
            "ADD_I32",
            "ICMP",
            "CMP_EQ", "CMP_NE", "CMP_LT", "CMP_GT", "CMP_LE", "CMP_GE",
            "POP",
            "MUL_I32",
            "LDPTR",
            "STPTR",
            "STACKALLOC",
            "GETFIELD",
            "PUTFIELD",
            "POPCPY",
            "PTRCPY",
            "LOADV","",
            "STKPTR_OFF",
            "RET",
            "INC_I32",
            "DEC_I32",
            "SUB_I32",
            "MALLOC",
            "CAST_I8_I32",
            "CAST_I32_I8",
            "STATIC_ADDR"
    };

    public static String _InstructionComment(CodeBuf buf, int idx) {
        return _InstructionComment(buf, buf.toByteArray(), Intptr.of(idx));
    }

    public static String _InstructionComment(CodeBuf buf, byte[] code, Intptr idx) {

        String head = String.format("#%-3s %-12s", idx.i, _NAMES[code[idx.i]].toLowerCase());
        String comm = "";  //"unsupported.";
        switch (code[idx.i++]) {
            case LLOADP: comm = "slot["+code[idx.i++]+"]"; break;
            case LDC: {
                short i = IOUtils.readShort(code, idx.i);
                comm = "CI["+i+"]: "+buf.cp.get(i);
                idx.i+=2;
                break;
            }
            case JMP:
            case JMP_F: comm = "to: #"+IOUtils.readShort(code, idx.i); idx.i+=2; break;
            case STACKALLOC:
            case DUP:
            case POP:
            case STKPTR_OFF:
            case RET:
            case LOADV:
            case POPCPY:
            case PTRCPY: comm = "n="+code[idx.i++]; break;
            case LDPTR:
            case STPTR: comm = "sz="+code[idx.i++]; break;
            case INVOKEFUNC: comm = "fn: $"+buf.cp.get(IOUtils.readShort(code, idx.i)); idx.i+=2; break;
            case GETFIELD:
            case PUTFIELD:   comm = "fl: $"+buf.cp.get(IOUtils.readShort(code, idx.i)); idx.i+=2; break;
        }
        return head + (comm.isEmpty() ? "" : "// "+comm);
    }
}
