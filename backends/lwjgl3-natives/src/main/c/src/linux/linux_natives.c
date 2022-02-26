#include "com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives.h"
#include "jni_etc.h"
#include <fontconfig/fontconfig.h>
#include <X11/Xlib.h>
#include <X11/Xatom.h>
#include "MwmUtil.h"
#include <gtk/gtk.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jobjectArray JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_getSystemFonts
  (JNIEnv *env, jclass clazz) {
    FcConfig *config = FcInitLoadConfigAndFonts();
    FcPattern *pat = FcPatternCreate();
    FcObjectSet *os = FcObjectSetCreate();
    FcObjectSetAdd(os, FC_STYLE);
    FcObjectSetAdd(os, FC_FAMILY);
    FcObjectSetAdd(os, FC_FILE);
    FcObjectSetAdd(os, FC_FULLNAME);
    FcObjectSetAdd(os, FC_SPACING);
    FcFontSet* fs = FcFontList(config, pat, os);
    if (!fs) {
      FcObjectSetDestroy(os);
      FcPatternDestroy(pat);
      FcConfigDestroy(config);
      FcFini();
      return NULL;
    }
    jclass jfontcls = (*env)->FindClass(env, LWJGL3_FONT_HANDLE_CLASS_NAME);
    if (jfontcls == NULL) {
      FcObjectSetDestroy(os);
      FcPatternDestroy(pat);
      FcConfigDestroy(config);
      FcFini();
      return NULL;
    }
    jobjectArray jfonts = (*env)->NewObjectArray(env, fs->nfont, jfontcls, NULL);
    if (jfonts == NULL) {
      FcObjectSetDestroy(os);
      FcPatternDestroy(pat);
      FcConfigDestroy(config);
      FcFini();
      return NULL;
    }
    jmethodID jfontinit = (*env)->GetMethodID(env, jfontcls, CLASS_INIT_NAME, LWJGL3_FONT_HANDLE_INIT_SIGNATURE);
    if (jfontinit == NULL) {
      FcObjectSetDestroy(os);
      FcPatternDestroy(pat);
      FcConfigDestroy(config);
      FcFini();
      return NULL;
    }
    for (jsize i = 0; fs && i < fs->nfont; i ++) {
      FcPattern *font = fs->fonts[i];
      FcChar8 *style, *family, *file, *fullname;
      int spacing;
      jstring jstyle =  FcPatternGetString(font, FC_STYLE, 0, &style) == FcResultMatch ? (*env)->NewStringUTF(env, style) : NULL;
      jstring jfamily = FcPatternGetString(font, FC_FAMILY, 0, &family) == FcResultMatch ? (*env)->NewStringUTF(env, family) : NULL;
      jstring jfile = FcPatternGetString(font, FC_FILE, 0, &file) == FcResultMatch ? (*env)->NewStringUTF(env, file) : NULL;
      jstring jfullname = FcPatternGetString(font, FC_FULLNAME, 0, &fullname) == FcResultMatch ? (*env)->NewStringUTF(env, fullname) : NULL;
      jboolean mono = FcPatternGetInteger(font, FC_SPACING, 0, &spacing) == FcResultMatch ? (spacing == 100 ? JNI_TRUE : JNI_FALSE) : JNI_FALSE;
      jobject jfont = (*env)->NewObject(env, jfontcls, jfontinit, jstyle, jfamily, jfile, jfullname, mono);
      if (jstyle != NULL) (*env)->DeleteLocalRef(env, jstyle);
      if (jfamily != NULL) (*env)->DeleteLocalRef(env, jfamily);
      if (jfile != NULL) (*env)->DeleteLocalRef(env, jfile);
      if (jfullname != NULL) (*env)->DeleteLocalRef(env, jfullname);
      if (jfont == NULL) continue;
      (*env)->SetObjectArrayElement(env, jfonts, i, jfont);
      (*env)->DeleteLocalRef(env, jfont);
      }
    FcFontSetDestroy(fs);
    FcConfigDestroy(config);
    FcFini();
    (*env)->DeleteLocalRef(env, jfontcls);
    return jfonts;
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_hideXWindowButtons
  (JNIEnv *env, jclass clazz, jlong jdisplay, jlong jw, jboolean maximize, jboolean minimize) {
    Display *display = (Display *)jdisplay;
    Window w = (Window)jw;
    PropMwmHints hints;
    Atom mwm_hints = XInternAtom(display, _XA_MWM_HINTS, False);
    hints.functions = MWM_FUNC_RESIZE | MWM_FUNC_MOVE | MWM_FUNC_CLOSE;
    if (maximize == JNI_FALSE) hints.functions = hints.functions | MWM_FUNC_MAXIMIZE;
    if (minimize == JNI_FALSE) hints.functions = hints.functions | MWM_FUNC_MINIMIZE;
    hints.flags = MWM_HINTS_FUNCTIONS;
    XChangeProperty(display, w, mwm_hints, mwm_hints, 32, PropModeReplace, (unsigned char*)&hints, PROP_MWM_HINTS_ELEMENTS);
  }

JNIEXPORT jobject JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_getGtkDefaultFont
  (JNIEnv *env, jclass clazz) {
    if (gtk_init_check(0, NULL) == FALSE) return NULL;
    g_type_class_unref (g_type_class_ref (GTK_TYPE_IMAGE_MENU_ITEM));
    GValue value = G_VALUE_INIT;
    g_object_get_property(G_OBJECT(gtk_settings_get_default()), "gtk-font-name", &value);
    const char *name = g_value_get_string(&value);
    FcConfig* config = FcInitLoadConfigAndFonts();
    FcPattern* pat = FcNameParse(name);
    FcConfigSubstitute(config, pat, FcMatchPattern);
    FcDefaultSubstitute(pat);
    FcResult res;
    FcPattern* font = FcFontMatch(config, pat, &res);
    if (!font) {
      FcPatternDestroy(pat);
      FcConfigDestroy(config);
      FcFini();
      return NULL;
    }
    FcChar8 *style, *family, *file, *fullname;
    int spacing;
    jstring jstyle =  FcPatternGetString(font, FC_STYLE, 0, &style) == FcResultMatch ? (*env)->NewStringUTF(env, style) : NULL;
    jstring jfamily = FcPatternGetString(font, FC_FAMILY, 0, &family) == FcResultMatch ? (*env)->NewStringUTF(env, family) : NULL;
    jstring jfile = FcPatternGetString(font, FC_FILE, 0, &file) == FcResultMatch ? (*env)->NewStringUTF(env, file) : NULL;
    jstring jfullname = FcPatternGetString(font, FC_FULLNAME, 0, &fullname) == FcResultMatch ? (*env)->NewStringUTF(env, fullname) : NULL;
    jboolean mono = FcPatternGetInteger(font, FC_SPACING, 0, &spacing) == FcResultMatch ? (spacing == 100 ? JNI_TRUE : JNI_FALSE) : JNI_FALSE;
    FcPatternDestroy(font);
    FcPatternDestroy(pat);
    FcConfigDestroy(config);
    FcFini();
    jclass jfontcls = (*env)->FindClass(env, LWJGL3_FONT_HANDLE_CLASS_NAME);
    if (jfontcls == NULL) return NULL;
    jmethodID jfontinit = (*env)->GetMethodID(env, jfontcls, CLASS_INIT_NAME, LWJGL3_FONT_HANDLE_INIT_SIGNATURE);
    if (jfontcls == NULL) return NULL;
    jobject jfont = (*env)->NewObject(env, jfontcls, jfontinit, jstyle, jfamily, jfile, jfullname, mono);
    if (jstyle != NULL) (*env)->DeleteLocalRef(env, jstyle);
    if (jfamily != NULL) (*env)->DeleteLocalRef(env, jfamily);
    if (jfile != NULL) (*env)->DeleteLocalRef(env, jfile);
    if (jfullname != NULL) (*env)->DeleteLocalRef(env, jfullname);
    if (jfont == NULL) return NULL;
    (*env)->DeleteLocalRef(env, jfontcls);
    return jfont;
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_grabPointer
  (JNIEnv *env, jclass clazz, jlong jdisplay, jlong jw) {
      Display *display = (Display *)jdisplay;
      Window w = (Window)jw;
      XGrabPointer(display, w, True, ButtonPressMask, GrabModeAsync, GrabModeAsync, None, None, CurrentTime);
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_ungrabPointer
  (JNIEnv *env, jclass clazz, jlong jdisplay) {
    XUngrabPointer((Display *)jdisplay, CurrentTime);
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_setXWindowIsDialog
  (JNIEnv *env, jclass clazz, jlong jdisplay, jlong jw, jlong jparent) {
    if (jparent) {
      Display *display = (Display *)jdisplay;
      Window w = (Window)jw;
      Window parent = (Window)jparent;
      Atom window_type = XInternAtom(display, "_NET_WM_WINDOW_TYPE", False);
      Atom wm_state = XInternAtom(display, "_NET_WM_STATE", False);
      Atom type_dialog = XInternAtom(display, "_NET_WM_WINDOW_TYPE_DIALOG", False);
      XChangeProperty(display, w, window_type, XA_ATOM, 32, PropModeReplace, (unsigned char *)&type_dialog, 1);
      Atom modal = XInternAtom(display, "_NET_WM_STATE_MODAL", False);
      XChangeProperty(display, w, wm_state, XA_ATOM, 32, PropModeReplace, (unsigned char *)&modal, 1);
      XSetTransientForHint(display, w, parent);
    }
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_setXWindowIsNormal
  (JNIEnv *env, jclass clazz, jlong jdisplay, jlong jw) {
    Display *display = (Display *)jdisplay;
    Window w = (Window)jw;
    Atom window_type = XInternAtom(display, "_NET_WM_WINDOW_TYPE", False);
    Atom wm_state = XInternAtom(display, "_NET_WM_STATE", False);
    Atom type_normal = XInternAtom(display, "_NET_WM_WINDOW_TYPE_NORMAL", False);
    XChangeProperty(display, w, window_type, XA_ATOM, 32, PropModeReplace, (unsigned char *)&type_normal, 1);
    XChangeProperty(display, w, wm_state, XA_ATOM, 32, PropModeReplace, None, 0);
    XSetTransientForHint(display, w, None);
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_setXWindowIsTooltip
  (JNIEnv *env, jclass clazz, jlong jdisplay, jlong jw, jlong jparent) {
    if (jparent) {
      Display *display = (Display *)jdisplay;
      Window w = (Window)jw;
      Window parent = (Window)jparent;
      Atom window_type = XInternAtom(display, "_NET_WM_WINDOW_TYPE", False);
      Atom wm_state = XInternAtom(display, "_NET_WM_STATE", False);
      Atom type_tooltip = XInternAtom(display, "_NET_WM_WINDOW_TYPE_TOOLTIP", False);
      Atom state[2] = {
        XInternAtom(display, "_NET_WM_STATE_SKIP_PAGER", False), 
        XInternAtom(display, "_NET_WM_STATE_SKIP_TASKBAR", False)
      };
      XChangeProperty(display, w, window_type, XA_ATOM, 32, PropModeReplace, (unsigned char *)&type_tooltip, 1);
      XChangeProperty(display, w, wm_state, XA_ATOM, 32, PropModeReplace, (unsigned char *)&state, 2);
      XSetTransientForHint(display, w, parent);
    }
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_setXWindowIsPopup
  (JNIEnv *env, jclass clazz, jlong jdisplay, jlong jw, jlong jparent) {
    if (jparent) {
      Display *display = (Display *)jdisplay;
      Window w = (Window)jw;
      Window parent = (Window)jparent;
      Atom window_type = XInternAtom(display, "_NET_WM_WINDOW_TYPE", False);
      Atom wm_state = XInternAtom(display, "_NET_WM_STATE", False);
      Atom type_popup = XInternAtom(display, "_NET_WM_WINDOW_TYPE_POPUP_MENU", False);
      Atom state[2] = {
        XInternAtom(display, "_NET_WM_STATE_SKIP_PAGER", False), 
        XInternAtom(display, "_NET_WM_STATE_SKIP_TASKBAR", False)
      };
      XChangeProperty(display, w, window_type, XA_ATOM, 32, PropModeReplace, (unsigned char *)&type_popup, 1);
      XChangeProperty(display, w, wm_state, XA_ATOM, 32, PropModeReplace, (unsigned char *)&state, 2);
      XSetTransientForHint(display, w, parent);
    }
  }

#ifdef __cplusplus
}
#endif