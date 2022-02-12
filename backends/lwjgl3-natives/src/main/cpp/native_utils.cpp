#include "com_anyicomplex_gdx_dwt_backends_lwjgl3_system_NativeUtils.h"
#include <stdlib.h>

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_NativeUtils_freeByteBuffer
  (JNIEnv * env, jclass clazz, jobjectArray buffers) {
      for (int i = 0; i < env->GetArrayLength(buffers); i ++) {
          free(env->GetDirectBufferAddress(env->GetObjectArrayElement(buffers, i)));
      }
  }