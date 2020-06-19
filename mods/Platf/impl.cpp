
#include <iostream>

#include "jni.h"

#include "ext_testing_JniPlt.h"

JNIEXPORT jint JNICALL Java_ext_testing_JniPlt_testfunc(JNIEnv * env, jclass, jint i, jstring str) {

    std::cout << "locPrint. i" << i << ", s" << env->GetStringUTFChars(str, JNI_FALSE) << std::endl;


    return 12345;
}