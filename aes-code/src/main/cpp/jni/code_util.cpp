//
// Created by Administrator on 2023/2/28.
//

#include <jni.h>
#include <stdio.h>

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_aiven_acode_MyAesUtil_openFile(JNIEnv* env, jobject instance, jstring path) {
    FILE* fp;
    jboolean result = JNI_FALSE;
    const char* filename = env->GetStringUTFChars(path, JNI_FALSE);
    fp = fopen(filename, "wb");
    if (fp != NULL) {
        fprintf(fp, "ThisisAJniTest");
        fclose(fp);
        fp = NULL;
        result = JNI_TRUE;
    }
    env->ReleaseStringUTFChars(path, filename);
    return result;
}
