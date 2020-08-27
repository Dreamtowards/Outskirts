package outskirts.event._asminvok;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import outskirts.util.ReflectionUtils;
import outskirts.util.SystemUtils;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

public abstract class ASMInvoker {

    private static final String BASECNAME = ASMInvoker.class.getCanonicalName().replace(".", "/"); // "outskirts/event/asminvoke/ASMInvoker"

    public abstract void invoke(Object owner, Object param);


    private static long nextClassId = 0;

    /**
     * public class IvkInstan extends ASMInvoker {
     *     AOverride
     *     public void invoke(Object owner, Object param) {
     *         ((AnExampEHandlerClass)owner).anHander((GuiEvent)param);
     *     }
     * }
     */
    public static ASMInvoker create(Method method) {
        return ASMInvoker.create(
                method.getDeclaringClass().getCanonicalName().replace(".", "/"),
                method.getParameterTypes()[0].getCanonicalName().replace(".", "/"),
                method.getName());
    }
    // ownerclass(outskirts/event/asminvoke/examp/AnExampEHandlerClass)  paramclass(outskirts/event/gui/GuiEvent)  methodname(anHander)
    public static ASMInvoker create(String ownerclass, String paramclass, String methodname) {
        final String instName = "outskirts/event/_asminvok/inst/MethodInvokeInstance" +(nextClassId++);

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, instName, null, BASECNAME, null);

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, BASECNAME, "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, ownerclass);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, paramclass);
            mv.visitMethodInsn(INVOKEVIRTUAL, ownerclass, methodname, "(L"+paramclass+";)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        byte[] b = cw.toByteArray();
        Class<?> clazz = SystemUtils.ASMCLASSLOADER.define(instName.replace("/", "."), b);
        // SystemUtils.UNSAFE.defineAnonymousClass(ASMInvoker.class, b, null);
        return (ASMInvoker)ReflectionUtils.newInstance(clazz);
    }


}
