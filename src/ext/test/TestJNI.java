package ext.test;

import ext.testing.JniPlt;
import org.junit.Test;
import outskirts.util.logging.Log;

public class TestJNI {

    @Test
    public void jniParamRetInvk() {

        System.load("/Users/dreamtowards/Projects/Outskirts/a.dylib");

        Log.info("jav before call jni");

        int ri = JniPlt.testfunc(11, "javastr");

        Log.info("jav after call jni, ri: " + ri);

    }

}
