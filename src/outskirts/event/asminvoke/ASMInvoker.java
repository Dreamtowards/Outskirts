package outskirts.event.asminvoke;

import outskirts.event.asminvoke.inst.IvkInstan;
import outskirts.util.CollectionUtils;

import java.lang.reflect.Method;

public abstract class ASMInvoker {

    public abstract void invoke(Object owner, Object param);

    public static ASMInvoker create(Method method) {

        return new IvkInstan();
    }

    public static ASMClassLoader CLSLOADER = new ASMClassLoader();
    public static class ASMClassLoader extends ClassLoader {
        public Class<?> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    public static byte[] replaceBytesOnce(byte[] search, byte[] replacement, byte[] datasrc) {
        int i = CollectionUtils.indexOf(datasrc, search, 0);
        if (i == -1)
            return null;
        byte[] dataout = new byte[datasrc.length-search.length+replacement.length];
        System.arraycopy(datasrc, 0, dataout, 0, i);
        System.arraycopy(replacement, 0, dataout, i, replacement.length);
        System.arraycopy(datasrc, i+search.length, dataout, i+replacement.length, datasrc.length-i-search.length);
        return dataout;
    }
}
