#include "com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives.h"
#include <fontconfig/fontconfig.h>
#include "constants.h"
#include <X11/Xlib.h>
#include <X11/Xatom.h>
#include "MwmUtil.h"
#include <gtk/gtk.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jobjectArray JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_nsystemFonts
  (JNIEnv *env, jclass clazz) {
    FcConfig *config = FcInitLoadConfigAndFonts();
    FcPattern *pat = FcPatternCreate();
    FcObjectSet *os = FcObjectSetCreate();
    FcObjectSetAdd(os, FC_STYLE);
    FcObjectSetAdd(os, FC_FAMILY);
    FcObjectSetAdd(os, FC_FILE);
    FcObjectSetAdd(os, FC_FULLNAME);
    FcObjectSetAdd(os, FC_SPACING);
    // FcObjectSet *os = FcObjectSetBuild(FC_STYLE, FC_FAMILY, FC_FILE, FC_FULLNAME, FC_SPACING); // Crashes on my machine, I don't know why :(
    FcFontSet* fs = FcFontList(config, pat, os);
    if (!fs) {
      FcObjectSetDestroy(os);
      FcPatternDestroy(pat);
      FcConfigDestroy(config);
      FcFini();
      return NULL;
    }
    jclass jfontcls = env->FindClass("com/anyicomplex/gdx/dwt/backends/lwjgl3/toolkit/Lwjgl3Font");
    if (jfontcls == NULL) {
      FcObjectSetDestroy(os);
      FcPatternDestroy(pat);
      FcConfigDestroy(config);
      FcFini();
      return NULL;
    }
    jobjectArray jfonts = env->NewObjectArray(fs->nfont, jfontcls, NULL);
    if (jfonts == NULL) {
      FcObjectSetDestroy(os);
      FcPatternDestroy(pat);
      FcConfigDestroy(config);
      FcFini();
      return NULL;
    }
    jmethodID jfontinit = env->GetMethodID(jfontcls, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V");
    for (jsize i = 0; fs && i < fs->nfont; i ++) {
      FcPattern *font = fs->fonts[i];
      FcChar8 *style, *family, *file, *fullname;
      jint spacing;
      jstring jstyle =  FcPatternGetString(font, FC_STYLE, 0, &style) == FcResultMatch ? env->NewStringUTF(reinterpret_cast<char *>(style)) : NULL;
      jstring jfamily = FcPatternGetString(font, FC_FAMILY, 0, &family) == FcResultMatch ? env->NewStringUTF(reinterpret_cast<char *>(family)) : NULL;
      jstring jfile = FcPatternGetString(font, FC_FILE, 0, &file) == FcResultMatch ? env->NewStringUTF(reinterpret_cast<char *>(file)) : NULL;
      jstring jfullname = FcPatternGetString(font, FC_FULLNAME, 0, &fullname) == FcResultMatch ? env->NewStringUTF(reinterpret_cast<char *>(fullname)) : NULL;
      spacing = FcPatternGetInteger(font, FC_SPACING, 0, &spacing) == FcResultMatch ? spacing : INT_INVALID;
      jobject jfont = env->NewObject(jfontcls, jfontinit, jstyle, jfamily, jfile, jfullname, spacing);
      if (jstyle != NULL) env->DeleteLocalRef(jstyle);
      if (jfamily != NULL) env->DeleteLocalRef(jfamily);
      if (jfile != NULL) env->DeleteLocalRef(jfile);
      if (jfullname != NULL) env->DeleteLocalRef(jfullname);
      if (jfont == NULL) continue;
      env->SetObjectArrayElement(jfonts, i, jfont);
      env->DeleteLocalRef(jfont);
      }
    FcFontSetDestroy(fs);
    FcConfigDestroy(config);
    FcFini();
    env->DeleteLocalRef(jfontcls);
    return jfonts;
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_nhideXWindowButtons
  (JNIEnv *env, jclass clazz, jlong jdisplay, jlong jw, jint maximize, jint minimize) {
    Display *display = (Display *)jdisplay;
    Window w = (Window)jw;
    PropMwmHints hints;
    Atom mwm_hints = XInternAtom(display, _XA_MWM_HINTS, False);
    hints.functions = MWM_FUNC_RESIZE | MWM_FUNC_MOVE | MWM_FUNC_CLOSE;
    if (!maximize) hints.functions = hints.functions | MWM_FUNC_MAXIMIZE;
    if (!minimize) hints.functions = hints.functions | MWM_FUNC_MINIMIZE;
    hints.flags = MWM_HINTS_FUNCTIONS;
    XChangeProperty(display, w, mwm_hints, mwm_hints, 32, PropModeReplace, (unsigned char*)&hints, PROP_MWM_HINTS_ELEMENTS);
  }

JNIEXPORT jobject JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_ngetGtkDefaultFont
  (JNIEnv *env, jclass clazz) {
    if (gtk_init_check(0, NULL) == FALSE) return NULL;
    g_type_class_unref (g_type_class_ref (GTK_TYPE_IMAGE_MENU_ITEM));
    GValue value = G_VALUE_INIT;
    g_object_get_property(G_OBJECT(gtk_settings_get_default()), "gtk-font-name", &value);
    const char *name = g_value_get_string(&value);
    FcConfig* config = FcInitLoadConfigAndFonts();
    FcPattern* pat = FcNameParse(reinterpret_cast<const FcChar8 *>(name));
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
    jint spacing;
    jstring jstyle =  FcPatternGetString(font, FC_STYLE, 0, &style) == FcResultMatch ? env->NewStringUTF(reinterpret_cast<char *>(style)) : NULL;
    jstring jfamily = FcPatternGetString(font, FC_FAMILY, 0, &family) == FcResultMatch ? env->NewStringUTF(reinterpret_cast<char *>(family)) : NULL;
    jstring jfile = FcPatternGetString(font, FC_FILE, 0, &file) == FcResultMatch ? env->NewStringUTF(reinterpret_cast<char *>(file)) : NULL;
    jstring jfullname = FcPatternGetString(font, FC_FULLNAME, 0, &fullname) == FcResultMatch ? env->NewStringUTF(reinterpret_cast<char *>(fullname)) : NULL;
    spacing = FcPatternGetInteger(font, FC_SPACING, 0, &spacing) == FcResultMatch ? spacing : INT_INVALID;
    FcPatternDestroy(font);
    FcPatternDestroy(pat);
    FcConfigDestroy(config);
    FcFini();
    jclass jfontcls = env->FindClass("com/anyicomplex/gdx/dwt/backends/lwjgl3/toolkit/Lwjgl3Font");
    jmethodID jfontinit = env->GetMethodID(jfontcls, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V");
    jobject jfont = env->NewObject(jfontcls, jfontinit, jstyle, jfamily, jfile, jfullname, spacing);
    if (jstyle != NULL) env->DeleteLocalRef(jstyle);
    if (jfamily != NULL) env->DeleteLocalRef(jfamily);
    if (jfile != NULL) env->DeleteLocalRef(jfile);
    if (jfullname != NULL) env->DeleteLocalRef(jfullname);
    env->DeleteLocalRef(jfontcls);
    return jfont;
  }

#ifdef __cplusplus
}
#endif