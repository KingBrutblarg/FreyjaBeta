#include <jni.h>
#include <string>

extern "C" JNIEXPORT jboolean JNICALL
Java_com_angeluz_freyja_LlamaBridge_initModel(
        JNIEnv* env, jobject /*thiz*/, jstring /*path*/) {
    // Stub: simula carga correcta
    return JNI_TRUE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_angeluz_freyja_LlamaBridge_infer(
        JNIEnv* env, jobject /*thiz*/, jstring prompt_) {
    const char* cprompt = env->GetStringUTFChars(prompt_, nullptr);
    std::string user = cprompt ? cprompt : "";
    env->ReleaseStringUTFChars(prompt_, cprompt);

    std::string reply = "🧠 (stub nativo) Recibí: " + user +
                        "\n— Integración llama.cpp pendiente —";
    return env->NewStringUTF(reply.c_str());
}
