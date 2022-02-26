#include "com_anyicomplex_gdx_dwt_backends_lwjgl3_system_windows_WindowsNatives.h"
#include "jni_etc.h"
#include <windows.h>
#include <shlwapi.h>

#ifdef __cplusplus
extern "C" {
#endif

#define STARTS_WITH(string_to_check, prefix) (strncmp(string_to_check, prefix, ((sizeof(prefix) / sizeof(prefix[0])) - 1)) ? 0 : ((sizeof(prefix) / sizeof(prefix[0])) - 1))
#define STARTS_WITH_W(string_to_check, prefix) (wcsncmp(string_to_check, prefix, ((sizeof(prefix) / sizeof(prefix[0])) - 1)) ? 0 : ((sizeof(prefix) / sizeof(prefix[0])) - 1))

#define FONT_REGISTRY_KEY_NAME_W L"Software\\Microsoft\\Windows NT\\CurrentVersion\\Fonts"

#define FONT_DIR_W L"C:\\Windows\\Fonts\\"
#define WINE_FONT_DIR_W L"Z:\\usr\\share\\wine\\fonts\\"
#define USER_FONT_DIR_PREFIX_W L"C:\\Users\\"
#define USER_FONT_DIR_SUFFIX_W L"\\AppData\\Local\\Microsoft\\Windows\\Fonts\\"

BOOL FileExists(LPCWSTR szPath) {
  DWORD dwAttrib = GetFileAttributesW(szPath);
  return (dwAttrib != INVALID_FILE_ATTRIBUTES &&  !(dwAttrib & FILE_ATTRIBUTE_DIRECTORY));
}

typedef struct _COUNTFONTPARAMS {
  HDC hDC;
  jsize *nfont;
} COUNTFONTPARAMS;

BOOL CALLBACK CountFontSubproc(const ENUMLOGFONTEXW *lpelfe, const NEWTEXTMETRICEXW *lpntme, DWORD FontType, LPARAM lParam) {
  COUNTFONTPARAMS *params = (COUNTFONTPARAMS *)lParam;
  jsize *nfont = params->nfont;
  *nfont += 1;
  return TRUE;
}

BOOL CALLBACK CountFontProc(const ENUMLOGFONTEXW *lpelfe, const NEWTEXTMETRICEXW *lpntme, DWORD FontType, LPARAM lParam) {
  COUNTFONTPARAMS *params = (COUNTFONTPARAMS *)lParam;
  LOGFONTW lf = lpelfe->elfLogFont;
  EnumFontFamiliesExW(params->hDC, &lf, (FONTENUMPROCW)(CountFontSubproc), (LPARAM)(params), 0);
  return TRUE;
}

typedef struct _ENUMFONTPARAMS {
  HDC hDC;
  JNIEnv *env;
  jclass jfontcls;
  jobjectArray jfonts;
  jmethodID jfontinit;
  jsize *ifont;
  HKEY hKeyFont;
  WCHAR **wkeys;
  DWORD ckeys;
} ENUMFONTPARAMS;

BOOL wkeyMatch(WCHAR **wkeys, DWORD ckeys, WCHAR *wcsmatch, WCHAR **pwkey) {
  if (wkeys) {
    WCHAR *wkey = NULL;
    for (DWORD i = 0; i < ckeys; i ++) {
      if (wkeys[i]) {
        if (wcslen(wkeys[i]) >= wcslen(wcsmatch)) {
          if (wcsncmp(wkeys[i], wcsmatch, wcslen(wcsmatch))) {
            if (wkey) {
              if (wcslen(wkey) > wcslen(wkeys[i])) wkey = wkeys[i];
            }
            else wkey = wkeys[i];
          }
        }
      }
    }
    if (wkey) {
      *pwkey = wkey;
      return TRUE;
    }
    else return FALSE;
  }
  else return FALSE;
}

BOOL CALLBACK EnumFontSubproc(const ENUMLOGFONTEXW *lpelfe, const NEWTEXTMETRICEXW *lpntme, DWORD FontType, LPARAM lParam) {
  ENUMFONTPARAMS *params = (ENUMFONTPARAMS *)lParam;
  JNIEnv *env = params->env;
  jclass jfontcls = params->jfontcls;
  jobjectArray jfonts = params->jfonts;
  jmethodID jfontinit = params->jfontinit;
  jsize *ifont = params->ifont;
  const WCHAR *wstyle = lpelfe->elfStyle;
  jstring jstyle = (*env)->NewString(env, wstyle, wcslen(wstyle));
  const WCHAR *wfullname = lpelfe->elfFullName;
  jstring jfullname = (*env)->NewString(env, wfullname, wcslen(wfullname));
  LOGFONTW lf = lpelfe->elfLogFont;
  WCHAR *wfamily = lf.lfFaceName;
  jstring jfamily = (*env)->NewString(env, wfamily, wcslen(wfamily));
  jboolean mono = ((lf.lfPitchAndFamily & FIXED_PITCH) == FIXED_PITCH) ? JNI_TRUE : JNI_FALSE;
  DWORD wfilesize;
  jstring jfile = NULL;
  WCHAR **wkeys = params->wkeys;
  DWORD ckeys = params->ckeys;
  WCHAR *wkey;
  HKEY hKeyFont = params->hKeyFont;
  if (wkeyMatch(wkeys, ckeys, wfamily, &wkey)) {
    if (RegQueryValueExW(hKeyFont, wkey, 0, NULL, NULL, &wfilesize) == ERROR_SUCCESS) {
      WCHAR buf[wfilesize / sizeof(WCHAR)];
      if (RegQueryValueExW(hKeyFont, wkey, 0, NULL, (LPBYTE)buf, &wfilesize) == ERROR_SUCCESS) {
        WCHAR *wfile = malloc((wcslen(FONT_DIR_W) + wcslen(buf)) * sizeof(WCHAR) + 1);
        if(PathIsRelativeW(buf)) {
          wcscpy(wfile, FONT_DIR_W);
          wcscat(wfile, buf);
          if (!FileExists(wfile)) {
            free(wfile);
            wfile = malloc((wcslen(WINE_FONT_DIR_W) + wcslen(buf)) * sizeof(WCHAR) + 1);
            wcscpy(wfile, WINE_FONT_DIR_W);
            wcscat(wfile, buf);
            if (!FileExists(wfile)) {
              DWORD usernamesize;
              if (GetUserNameW(NULL, &usernamesize)) {
                WCHAR username[usernamesize / sizeof(WCHAR)];
                if (GetUserNameW(username, &usernamesize)) {
                  free(wfile);
                  wfile = malloc((wcslen(USER_FONT_DIR_PREFIX_W) + wcslen(username) + wcslen(USER_FONT_DIR_SUFFIX_W) + wcslen(buf)) * sizeof(WCHAR) + 1);
                  wcscpy(wfile, USER_FONT_DIR_PREFIX_W);
                  wcscpy(wfile, username);
                  wcscpy(wfile, USER_FONT_DIR_SUFFIX_W);
                  wcscat(wfile, buf);
                }
              }
            }
          }
        }
        else wcscpy(wfile, buf);
        if (FileExists(wfile)) jfile = (*env)->NewString(env, wfile, wcslen(wfile));
        free(wfile);
      }
    }
  }
  jobject jfont = (*env)->NewObject(env, jfontcls, jfontinit, jstyle, jfamily, jfile, jfullname, mono);
  if (jfont != NULL) (*env)->SetObjectArrayElement(env, jfonts, *ifont, jfont);
  (*env)->DeleteLocalRef(env, jstyle);
  (*env)->DeleteLocalRef(env, jfullname);
  (*env)->DeleteLocalRef(env, jfamily);
  (*env)->DeleteLocalRef(env, jfont);
  if (jfile != NULL) (*env)->DeleteLocalRef(env, jfile);
  *ifont += 1;
  return TRUE;
}

BOOL CALLBACK EnumFontProc(const ENUMLOGFONTEXW *lpelfe, const NEWTEXTMETRICEXW *lpntme, DWORD FontType, LPARAM lParam) {
  ENUMFONTPARAMS *params = (ENUMFONTPARAMS *)lParam;
  LOGFONTW lf = lpelfe->elfLogFont;
  EnumFontFamiliesExW(params->hDC, &lf, (FONTENUMPROCW)(EnumFontSubproc), (LPARAM)(params), 0);
  return TRUE;
}

JNIEXPORT jobjectArray JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_windows_WindowsNatives_getSystemFonts
  (JNIEnv *env, jclass clazz) {
    jclass jfontcls = (*env)->FindClass(env, LWJGL3_FONT_HANDLE_CLASS_NAME);
    if (jfontcls == NULL) return NULL;
    jmethodID jfontinit = (*env)->GetMethodID(env, jfontcls, CLASS_INIT_NAME, LWJGL3_FONT_HANDLE_INIT_SIGNATURE);
    if (jfontinit == NULL) return NULL;
    LOGFONTW lf;
    memset(&lf, 0, sizeof(lf));
    lf.lfCharSet = DEFAULT_CHARSET;
    HDC hDC = GetDC(NULL);
    jsize nfont = 0;
    jsize *pnfont = &nfont;
    COUNTFONTPARAMS cparams = {
      .hDC = hDC, 
      .nfont = pnfont
    };
    EnumFontFamiliesExW(hDC, &lf, (FONTENUMPROCW)(CountFontProc), (LPARAM)(&cparams), 0);
    jobjectArray jfonts = (*env)->NewObjectArray(env, nfont, jfontcls, NULL);
    if (jfonts == NULL) return NULL;
    jsize ifont = 0;
    jsize *pifont = &ifont;
    HKEY hKeyFont;
    if (RegOpenKeyExW(HKEY_LOCAL_MACHINE, FONT_REGISTRY_KEY_NAME_W, 0, KEY_READ, &hKeyFont) == ERROR_SUCCESS) {
      DWORD cValues;
      DWORD cbMaxValueNameLen;
      if (RegQueryInfoKeyW(hKeyFont, NULL, NULL, NULL, NULL, NULL, NULL, &cValues, &cbMaxValueNameLen, NULL, NULL, NULL) == ERROR_SUCCESS) {
        if (cValues) {
          WCHAR **wkeys = calloc(cValues, sizeof(WCHAR *));
          for (DWORD i = 0; i < cValues; i ++) {
            WCHAR valueName[cbMaxValueNameLen];
            DWORD cchValueName = cbMaxValueNameLen;
            DWORD type;
            if (RegEnumValueW(hKeyFont, i, valueName, &cchValueName, NULL, &type, NULL, NULL) == ERROR_SUCCESS) {
              if (type == REG_SZ) {
                WCHAR *wkey = calloc(cchValueName, sizeof(WCHAR));
                wcscpy(wkey, valueName);
                wkeys[i] = wkey;
              }
              else wkeys[i] = NULL;
            }
          }
          ENUMFONTPARAMS eparams = {
          .hDC = hDC, 
          .env = env, 
          .jfontcls = jfontcls, 
          .jfonts = jfonts, 
          .jfontinit = jfontinit, 
          .ifont = pifont, 
          .hKeyFont = hKeyFont, 
          .wkeys = wkeys, 
          .ckeys = cValues
          };
          EnumFontFamiliesExW(hDC, &lf, (FONTENUMPROCW)(EnumFontProc), (LPARAM)(&eparams), 0);
          RegCloseKey(hKeyFont);
          free(wkeys);
        }
      }
    }
    (*env)->DeleteLocalRef(env, jfontcls);
    ReleaseDC(NULL, hDC);
    return jfonts;
  }

typedef struct _GETDEFAULTFONTPARAMS {
  JNIEnv *env;
  jclass jfontcls;
  jobjectArray jfonts;
  jmethodID jfontinit;
  HKEY hKeyFont;
  WCHAR **wkeys;
  DWORD ckeys;
} GETDEFAULTFONTPARAMS;

BOOL CALLBACK GetDefaultFontProc(const ENUMLOGFONTEXW *lpelfe, const NEWTEXTMETRICEXW *lpntme, DWORD FontType, LPARAM lParam) {
  GETDEFAULTFONTPARAMS *params = (GETDEFAULTFONTPARAMS *)lParam;
  jobjectArray jfonts = params->jfonts;
  JNIEnv *env = params->env;
  if ((*env)->GetObjectArrayElement(env, jfonts, 0) != NULL) return TRUE;
  jclass jfontcls = params->jfontcls;
  jmethodID jfontinit = params->jfontinit;
  const WCHAR *wstyle = lpelfe->elfStyle;
   jstring jstyle = (*env)->NewString(env, wstyle, wcslen(wstyle));
  const WCHAR *wfullname = lpelfe->elfFullName;
  jstring jfullname = (*env)->NewString(env, wfullname, wcslen(wfullname));
  LOGFONTW lf = lpelfe->elfLogFont;
  WCHAR *wfamily = lf.lfFaceName;
  jstring jfamily = (*env)->NewString(env, wfamily, wcslen(wfamily));
  jboolean mono = ((lf.lfPitchAndFamily & FIXED_PITCH) == FIXED_PITCH) ? JNI_TRUE : JNI_FALSE;
  DWORD wfilesize;
  jstring jfile = NULL;
  WCHAR **wkeys = params->wkeys;
  DWORD ckeys = params->ckeys;
  WCHAR *wkey;
  HKEY hKeyFont = params->hKeyFont;
  if (wkeyMatch(wkeys, ckeys, wfamily, &wkey)) {
    if (RegQueryValueExW(hKeyFont, wkey, 0, NULL, NULL, &wfilesize) == ERROR_SUCCESS) {
      WCHAR buf[wfilesize / sizeof(WCHAR)];
      if (RegQueryValueExW(hKeyFont, wkey, 0, NULL, (LPBYTE)buf, &wfilesize) == ERROR_SUCCESS) {
        WCHAR *wfile = malloc((wcslen(FONT_DIR_W) + wcslen(buf)) * sizeof(WCHAR) + 1);
        if(PathIsRelativeW(buf)) {
          wcscpy(wfile, FONT_DIR_W);
          wcscat(wfile, buf);
          if (!FileExists(wfile)) {
            free(wfile);
            wfile = malloc((wcslen(WINE_FONT_DIR_W) + wcslen(buf)) * sizeof(WCHAR) + 1);
            wcscpy(wfile, WINE_FONT_DIR_W);
            wcscat(wfile, buf);
            if (!FileExists(wfile)) {
              DWORD usernamesize;
              if (GetUserNameW(NULL, &usernamesize)) {
                WCHAR username[usernamesize / sizeof(WCHAR)];
                if (GetUserNameW(username, &usernamesize)) {
                  free(wfile);
                  wfile = malloc((wcslen(USER_FONT_DIR_PREFIX_W) + wcslen(username) + wcslen(USER_FONT_DIR_SUFFIX_W) + wcslen(buf)) * sizeof(WCHAR) + 1);
                  wcscpy(wfile, USER_FONT_DIR_PREFIX_W);
                  wcscpy(wfile, username);
                  wcscpy(wfile, USER_FONT_DIR_SUFFIX_W);
                  wcscat(wfile, buf);
                }
              }
            }
          }
        }
        else wcscpy(wfile, buf);
        if (FileExists(wfile)) jfile = (*env)->NewString(env, wfile, wcslen(wfile));
        free(wfile);
      }
    }
  }
  jobject jfont = (*env)->NewObject(env, jfontcls, jfontinit, jstyle, jfamily, jfile, jfullname, mono);
  if (jfont != NULL) (*env)->SetObjectArrayElement(env, jfonts, 0, jfont);
  (*env)->DeleteLocalRef(env, jstyle);
  (*env)->DeleteLocalRef(env, jfullname);
  (*env)->DeleteLocalRef(env, jfamily);
  (*env)->DeleteLocalRef(env, jfont);
  if (jfile != NULL) (*env)->DeleteLocalRef(env, jfile);
  return TRUE;
}

JNIEXPORT jobject JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_windows_WindowsNatives_getDefaultFont
  (JNIEnv *env, jclass clazz) {
    jclass jfontcls = (*env)->FindClass(env, LWJGL3_FONT_HANDLE_CLASS_NAME);
    if (jfontcls == NULL) return NULL;
    jmethodID jfontinit = (*env)->GetMethodID(env, jfontcls, CLASS_INIT_NAME, LWJGL3_FONT_HANDLE_INIT_SIGNATURE);
    if (jfontinit == NULL) return NULL;
    HDC hDC = GetDC(NULL);
    LOGFONTW lf;
    NONCLIENTMETRICSW metrics = {
      .cbSize = sizeof(NONCLIENTMETRICSW)
    };
    if (SystemParametersInfoW(SPI_GETNONCLIENTMETRICS, sizeof(NONCLIENTMETRICSW), (PVOID)(&metrics), 0)) {
      lf = metrics.lfMessageFont;
      if (EnumFontFamiliesExW(hDC, &lf, NULL, NULL, 0) != ERROR_SUCCESS) {
        GetObjectW(GetStockObject(SYSTEM_FONT), sizeof(LOGFONT), &lf); 
      }
    }
    else return NULL;
    jobjectArray jfonts = (*env)->NewObjectArray(env, 1, jfontcls, NULL);
    if (jfonts == NULL) return NULL;
    HKEY hKeyFont;
    if (RegOpenKeyExW(HKEY_LOCAL_MACHINE, FONT_REGISTRY_KEY_NAME_W, 0, KEY_READ, &hKeyFont) == ERROR_SUCCESS) {
      DWORD cValues;
      DWORD cbMaxValueNameLen;
      if (RegQueryInfoKeyW(hKeyFont, NULL, NULL, NULL, NULL, NULL, NULL, &cValues, &cbMaxValueNameLen, NULL, NULL, NULL) == ERROR_SUCCESS) {
        if (cValues) {
          WCHAR **wkeys = calloc(cValues, sizeof(WCHAR *));
          for (DWORD i = 0; i < cValues; i ++) {
            WCHAR valueName[cbMaxValueNameLen];
            DWORD cchValueName = cbMaxValueNameLen;
            DWORD type;
            if (RegEnumValueW(hKeyFont, i, valueName, &cchValueName, NULL, &type, NULL, NULL) == ERROR_SUCCESS) {
              if (type == REG_SZ) {
                WCHAR *wkey = calloc(cchValueName, sizeof(WCHAR));
                wcscpy(wkey, valueName);
                wkeys[i] = wkey;
              }
              else wkeys[i] = NULL;
            }
          }
          GETDEFAULTFONTPARAMS params = {
            .env = env, 
            .jfontcls = jfontcls, 
            .jfonts = jfonts,
            .jfontinit = jfontinit, 
            .hKeyFont = hKeyFont, 
            .wkeys = wkeys, 
            .ckeys = cValues
          };
          EnumFontFamiliesExW(hDC, &lf, (FONTENUMPROCW)(GetDefaultFontProc), (LPARAM)&params, 0);
          RegCloseKey(hKeyFont);
          free(wkeys);
        }
      }
    }
    (*env)->DeleteLocalRef(env, jfontcls);
    ReleaseDC(NULL, hDC);
    return (*env)->GetObjectArrayElement(env, jfonts, 0);
  }

JNIEXPORT void JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_windows_WindowsNatives_open
  (JNIEnv *env, jclass clazz, jstring jfile) {
    const jchar* file = (*env)->GetStringChars(env, jfile, JNI_FALSE);
    ShellExecuteW(NULL, L"open", file, NULL, NULL , SW_SHOWNORMAL);
    (*env)->DeleteLocalRef(env, jfile);
  }

#ifdef __cplusplus
}
#endif