#include "com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives.h"
#include <fontconfig/fontconfig.h>
#include "constants.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jobjectArray JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_linux_LinuxNatives_nsystemFonts
  (JNIEnv *env, jclass clazz) {
    FcConfig *config = FcInitLoadConfigAndFonts();
    FcPattern *pat = FcPatternCreate();
    FcObjectSet *os = FcObjectSetBuild(FC_STYLE, FC_FAMILY, FC_FILE, FC_FULLNAME, FC_SPACING);
    FcFontSet *fs = FcFontList(config, pat, os);
    if (!fs) return NULL;
    jclass jfontcls = env->FindClass("com/anyicomplex/gdx/dwt/backends/lwjgl3/toolkit/Lwjgl3Font");
    jmethodID jfontinit = env->GetMethodID(jfontcls, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V");
    jobjectArray jfonts = env->NewObjectArray(fs->nfont, jfontcls, NULL);
    for (jsize i = 0; i < fs->nfont; i ++) {
      FcPattern *font = fs->fonts[i];
      FcChar8 *style, *family, *file, *fullname;
      jint spacing = FcPatternGetInteger(font, FC_SPACING, 0, &spacing) == FcResultMatch ? spacing : INT_INVALID;
      char *cstyle = reinterpret_cast<char *>(style);
      char *cfamily = reinterpret_cast<char *>(family);
      char *cfile = reinterpret_cast<char *>(file);
      char *cfullname = reinterpret_cast<char *>(fullname);
      jstring jstyle = FcPatternGetString(font, FC_STYLE, 0, &style) == FcResultMatch ? env->NewStringUTF(cstyle) : NULL;
      jstring jfamily = FcPatternGetString(font, FC_FAMILY, 0, &family) == FcResultMatch ? env->NewStringUTF(cfamily) : NULL;
      jstring jfile = FcPatternGetString(font, FC_FILE, 0, &file) == FcResultMatch ? env->NewStringUTF(cfile) : NULL;
      jstring jfullname = FcPatternGetString(font, FC_FULLNAME, 0, &fullname) == FcResultMatch ? env->NewStringUTF(cfullname) : NULL;
      jobject jfont = env->NewObject(jfontcls, jfontinit, jstyle, jfamily, jfile, jfullname, spacing);
      env->SetObjectArrayElement(jfonts, i, jfont);
    }
    FcFontSetDestroy(fs);
    return jfonts;
  }

#ifdef __cplusplus
}
#endif