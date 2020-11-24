package outskirts.event.asminvok;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import outskirts.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

public abstract class ASMInvoker {


    public abstract void invoke(Object obj, Object param0);


    private static String _INVOKER_BASE = ASMInvoker.class.getCanonicalName().replace(".", "/"); // "outskirts/event/asminvoke/ASMInvoker"

    private static long _nextClassId = 0;

    /**
     * public class IvkInstan extends ASMInvoker {
     *     AOverride
     *     public void invoke(Object owner, Object param) {
     *         ((AnExampEHandlerClass)owner).anHander((GuiEvent)param);
     *     }
     * }
     */
    // ownerclass(outskirts/event/asminvoke/examp/AnExampEHandlerClass)  paramclass(outskirts/event/gui/GuiEvent)  methodname(anHander)
    public static ASMInvoker create(Method method) {
        final String instname = "outskirts/event/asminvok/inst/MethodInvokeInstance" +(_nextClassId++);
        String ownerclass = method.getDeclaringClass().getCanonicalName().replace(".", "/");
        String paramclass = method.getParameterTypes()[0].getCanonicalName().replace(".", "/");
        String methodname = method.getName();
        boolean isPublic = Modifier.isPublic(method.getModifiers());

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, instname, null, _INVOKER_BASE, null);

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, _INVOKER_BASE, "<init>", "()V", false);
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
        Class<?> clazz =
                isPublic ? ReflectionUtils.UNSAFE.defineClass(instname.replace("/", "."), b, 0, b.length, ClassLoader.getSystemClassLoader(), null)
                         : ReflectionUtils.UNSAFE.defineAnonymousClass(method.getDeclaringClass(), b, null);
        return (ASMInvoker)ReflectionUtils.newInstance(clazz);
    }


}
