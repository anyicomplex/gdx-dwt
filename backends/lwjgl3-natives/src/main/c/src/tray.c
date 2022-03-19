#include "com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3Tray.h"
#include <tray.h>
#include "jni_etc.h"

#ifdef __cplusplus
extern "C" {
#endif

#if defined (_WIN32) || defined (_WIN64)
#define TRAY_WINAPI 1
#elif defined (__linux__) || defined (linux) || defined (__linux)
#define TRAY_APPINDICATOR 1
#elif defined (__APPLE__) || defined (__MACH__)
#define TRAY_APPKIT 1
#endif

static tray *ptray;

typedef struct _cb_params {
  JavaVM *jvm;
  jobject thiz;
} cb_params;

static void on_click_cb(tray_item *item) {
  cb_params *params = item->context;
  JavaVM *jvm = params->jvm;
  if (jvm == NULL) return;
  JNIEnv *env;
  if ((*jvm)->GetEnv(jvm, &env, JNI_VERSION_1_1) != JNI_OK) return;
  jobject thiz = params->thiz;
  (*env)->CallVoidMethod(env, thiz, (*env)->GetMethodID(env, (*env)->GetObjectClass(env, thiz), 
  LWJGL3_TRAY_ITEM_PERFORM_CLICK_NAME, 
  LWJGL3_TRAY_ITEM_PERFORM_CLICK_SINGATURE));
  }

JNIEXPORT jlong JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3Tray_nalloc
  (JNIEnv *env, jclass clazz) {
      tray *handle = malloc(sizeof(tray));
      memset(handle, 0, sizeof(tray));
      ptray = handle;
      return handle;
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3Tray_nfree
  (JNIEnv *env, jclass clazz, jlong handle) {
    free((tray *)handle);
  }

JNIEXPORT jboolean JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3Tray_ninit
  (JNIEnv *env, jclass clazz, jlong handle) {
      return tray_init(handle) == TRAY_SUCCESS ? JNI_TRUE : JNI_FALSE;
  }

JNIEXPORT jboolean JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3Tray_nloop
  (JNIEnv *env, jclass clazz, jboolean blocking) {
      return tray_loop(blocking == JNI_TRUE ? TRAY_TRUE : TRAY_FALSE) == TRAY_SUCCESS ? JNI_TRUE : JNI_FALSE;
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3Tray_nupdate
  (JNIEnv *env, jclass clazz, jlong handle) {
      tray_update(handle);
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3Tray_nexit
  (JNIEnv *env, jclass clazz) {
      tray_exit();
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3Tray_nsetTooltip
  (JNIEnv *env, jclass clazz, jlong handle, jlong tooltip) {
      #ifdef  TRAY_WINAPI
      #else
      ((tray *)handle)->tooltip = tooltip;
      #endif
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3Tray_nsetIcon
  (JNIEnv *env, jclass clazz, jlong handle, jlong buffer) {
    ((tray *)handle)->icon_path = buffer;
  }

JNIEXPORT jlong JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3Tray_nsetItems
  (JNIEnv *env, jclass clazz, jlong handle, jlong items) {
    if (items == NULL) {
      ((tray *)handle)->items = NULL;
      return NULL;
    }
    ((tray *)handle)->items = items;
    return items;
  }

JNIEXPORT jlong JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nalloc
  (JNIEnv *env, jclass clazz) {
    tray_item *handle = malloc(sizeof(tray_item));
    memset(handle, 0, sizeof(tray_item));
    return handle;
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nfree
  (JNIEnv *env, jclass clazz, jlong handle) {
    if (handle != NULL) {
      tray_item *item = handle;
      cb_params *params = item->context;
      if (params != NULL) {
        (*env)->DeleteGlobalRef(env, params->thiz);
        free(params);
        item->context = NULL;
      }
      free(item);
    }
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nsetIcon
  (JNIEnv *env, jclass clazz, jlong handle, jlong buffer) {
    ((tray *)handle)->icon_path = buffer;
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nsetText
  (JNIEnv *env, jclass clazz, jlong handle, jlong text) {
    #ifdef  TRAY_WINAPI
    #else
    ((tray_item *)handle)->text = text;
    #endif
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nsetDisabled
  (JNIEnv *env, jclass clazz, jlong handle, jboolean disabled) {
    ((tray_item *)handle)->disabled = disabled == JNI_TRUE ? TRAY_TRUE : TRAY_FALSE;
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nsetChecked
  (JNIEnv *env, jclass clazz, jlong handle, jboolean checked) {
    ((tray_item *)handle)->checked = checked == JNI_TRUE ? TRAY_TRUE : TRAY_FALSE;
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nsetRadioCheck
  (JNIEnv *env, jclass clazz, jlong handle, jboolean radio_check) {
    ((tray_item *)handle)->radio_check = radio_check == JNI_TRUE ? TRAY_TRUE : TRAY_FALSE;
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nsetType
  (JNIEnv *env, jclass clazz, jlong handle, jint type) {
    ((tray_item *)handle)->type = type;
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nsetItems
  (JNIEnv *env, jclass clazz, jlong handle, jlong items) {
    if (items == NULL) {
      ((tray_item *)handle)->items = NULL;
      return NULL;
    }
    ((tray_item *)handle)->items = items;
    return items;
  }

JNIEXPORT jboolean JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nisDisabled
  (JNIEnv *env, jclass clazz, jlong handle) {
    return ((tray_item *)handle)->disabled ? JNI_TRUE : JNI_FALSE;
  }

JNIEXPORT jboolean JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nisChecked
  (JNIEnv *env, jclass clazz, jlong handle) {
    return ((tray_item *)handle)->checked ? JNI_TRUE : JNI_FALSE;
  }

JNIEXPORT jboolean JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nisRadioCheck
  (JNIEnv *env, jclass clazz, jlong handle) {
    return ((tray_item *)handle)->radio_check ? JNI_TRUE : JNI_FALSE;
  }

JNIEXPORT jint JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_ngetType
  (JNIEnv *env, jclass clazz, jlong handle) {
    return ((tray_item *)handle)->type;
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_factory_Lwjgl3TrayItem_nsetCallback
  (JNIEnv *env, jobject thiz, jlong handle) {
    tray_item *item = handle;
    cb_params *params = item->context;
    if (params != NULL) {
      (*env)->DeleteGlobalRef(env, params->thiz);
      free(params);
      item->context = NULL;
    }
    params = malloc(sizeof(cb_params));
    JavaVM *jvm;
    if ((*env)->GetJavaVM(env, &jvm) != JNI_OK) jvm = NULL;
    params->jvm = jvm;
    params->thiz = (*env)->NewGlobalRef(env, thiz);
    item->context = params;
    item->cb = on_click_cb;
  }

#ifdef __cplusplus
}
#endif