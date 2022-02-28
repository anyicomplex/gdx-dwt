#include "com_anyicomplex_gdx_dwt_backends_lwjgl3_system_windows_WindowsNatives.h"
#include "jni_etc.h"
#include <windows.h>
#include <shlwapi.h>

#ifdef __cplusplus
extern "C" {
#endif

#define FONT_REGISTRY_KEY_NAME_W L"Software\\Microsoft\\Windows NT\\CurrentVersion\\Fonts"
#define FONT_DIR_SUFFIX_W L"\\Fonts\\"

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

BOOL FontDataMatchSystemFontsW(LPVOID fontData, DWORD dataSize, WCHAR **wfile) {
  WCHAR windir[MAX_PATH];
  GetWindowsDirectoryW(&windir, MAX_PATH);
  WCHAR fontdir[wcslen(windir) + wcslen(FONT_DIR_SUFFIX_W) + 1];
  wcscpy(fontdir, windir);
  wcscat(fontdir, FONT_DIR_SUFFIX_W);
  WCHAR match[wcslen(fontdir) + 2];
  wcscpy(match, fontdir);
  wcscat(match, L"*");
  HANDLE hFind = INVALID_HANDLE_VALUE;
  WIN32_FIND_DATAW ffd;
  hFind = FindFirstFileW(&match, &ffd);
  if (hFind != INVALID_HANDLE_VALUE) {
    while (FindNextFileW(hFind, &ffd) != 0) {
      if (!(ffd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) && ffd.nFileSizeLow == dataSize) {
        WCHAR wfilepath[wcslen(fontdir) + wcslen(ffd.cFileName) + 1];
        wcscpy(wfilepath, fontdir);
        wcscat(wfilepath, ffd.cFileName);
        FILE *file = _wfopen(wfilepath, L"rb");
        void *fileData = malloc(dataSize);
        fread(fileData, 1, dataSize, file);
        fclose(file);
        if (!memcmp(fontData, fileData, dataSize)) {
          *wfile = wfilepath;
          free(fileData);
          FindClose(hFind);
          return TRUE;
        }
        free(fileData);
      }
    }
    FindClose(hFind);
  }
  return FALSE;
}

BOOL WKeyMatch(WCHAR **wkeys, DWORD ckeys, WCHAR *wcsmatch, WCHAR **pwkey) {
  if (wkeys) {
    WCHAR *wkey = NULL;
    for (DWORD i = 0; i < ckeys; i ++) {
      if (wkeys[i]) {
        if (wcslen(wkeys[i]) >= wcslen(wcsmatch)) {
          if (!wcsncmp(wkeys[i], wcsmatch, wcslen(wcsmatch))) {
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
  }
  return FALSE;
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
  if (WKeyMatch(wkeys, ckeys, wfullname, &wkey) || WKeyMatch(wkeys, ckeys, wfamily, &wkey)) {
    if (RegQueryValueExW(hKeyFont, wkey, 0, NULL, NULL, &wfilesize) == ERROR_SUCCESS) {
      WCHAR buf[wfilesize / sizeof(WCHAR) + 1];
      if (RegQueryValueExW(hKeyFont, wkey, 0, NULL, (LPBYTE)buf, &wfilesize) == ERROR_SUCCESS) {
        WCHAR windir[MAX_PATH];
        GetWindowsDirectoryW(&windir, MAX_PATH);
        WCHAR fontdir[wcslen(windir) + wcslen(FONT_DIR_SUFFIX_W) + 1];
        wcscpy(fontdir, windir);
        wcscat(fontdir, FONT_DIR_SUFFIX_W);
        WCHAR *wfile = calloc(wcslen(fontdir) + wcslen(buf) + 1, sizeof(WCHAR));
        if(PathIsRelativeW(buf)) {
          wcscpy(wfile, fontdir);
          wcscat(wfile, buf);
        }
        else wcscpy(wfile, buf);
        jfile = (*env)->NewString(env, wfile, wcslen(wfile));
        free(wfile);
      }
    }
  }
  else {
    HDC hDC = params->hDC;
    SelectObject(hDC, CreateFontIndirectW(&(lpelfe->elfLogFont)));
    DWORD dataSize = GetFontData(hDC, 0, 0, NULL, 0);
      if (dataSize != GDI_ERROR) {
        void *data = malloc(dataSize);
        if (GetFontData(hDC, 0, 0, data, dataSize) == dataSize) {
          WCHAR *wfile;
          if (FontDataMatchSystemFontsW(data, dataSize, &wfile)) jfile = (*env)->NewString(env, wfile, wcslen(wfile));
        }
        free(data);
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
    HDC hDC = CreateCompatibleDC(NULL);
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
            DWORD cchValueName = cbMaxValueNameLen + 1;
            WCHAR valueName[cbMaxValueNameLen];
            DWORD type;
            if (RegEnumValueW(hKeyFont, i, valueName, &cchValueName, NULL, &type, NULL, NULL) == ERROR_SUCCESS) {
              if (type == REG_SZ) {
                WCHAR *wkey = calloc(cchValueName, sizeof(WCHAR));
                wcscpy(wkey, valueName);
                wkeys[i] = wkey;
              }
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
  HDC hDC;
  JNIEnv *env;
  jclass jfontcls;
  jobjectArray jfonts;
  jmethodID jfontinit;
  LOGFONTW targetlf;
  HKEY hKeyFont;
  WCHAR **wkeys;
  DWORD ckeys;
  BOOL *done;
} GETDEFAULTFONTPARAMS;

BOOL CALLBACK GetDefaultFontSubproc(const ENUMLOGFONTEXW *lpelfe, const NEWTEXTMETRICEXW *lpntme, DWORD FontType, LPARAM lParam) {
  GETDEFAULTFONTPARAMS *params = (GETDEFAULTFONTPARAMS *)lParam;
  if (*(params->done)) return TRUE;
  LOGFONTW targetlf = params->targetlf;
  HDC hDC = params->hDC;
  SelectObject(hDC, CreateFontIndirectW(&targetlf));
  DWORD data1Size = GetFontData(hDC, 0, 0, NULL, 0);
  if (data1Size != GDI_ERROR) {
    void *data1 = malloc(data1Size);
    if (GetFontData(hDC, 0, 0, data1, data1Size) == data1Size) {
      SelectObject(hDC, CreateFontIndirectW(&(lpelfe->elfLogFont)));
      DWORD data2Size = GetFontData(hDC, 0, 0, NULL, 0);
      if (data1Size == data2Size) {
        void *data2 = malloc(data2Size);
        if (GetFontData(hDC, 0, 0, data2, data2Size) == data2Size) {
          if (!memcmp(data1, data2, data1Size)) {
            JNIEnv *env = params->env;
            jclass jfontcls = params->jfontcls;
            jobjectArray jfonts = params->jfonts;
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
            if (WKeyMatch(wkeys, ckeys, wfullname, &wkey) || WKeyMatch(wkeys, ckeys, wfamily, &wkey)) {
              if (RegQueryValueExW(hKeyFont, wkey, 0, NULL, NULL, &wfilesize) == ERROR_SUCCESS) {
                WCHAR buf[wfilesize / sizeof(WCHAR) + 1];
                if (RegQueryValueExW(hKeyFont, wkey, 0, NULL, (LPBYTE)buf, &wfilesize) == ERROR_SUCCESS) {
                  WCHAR windir[MAX_PATH];
                  GetWindowsDirectoryW(&windir, MAX_PATH);
                  WCHAR fontdir[wcslen(windir) + wcslen(FONT_DIR_SUFFIX_W) + 1];
                  wcscpy(fontdir, windir);
                  wcscat(fontdir, FONT_DIR_SUFFIX_W);
                  WCHAR *wfile = calloc(wcslen(fontdir) + wcslen(buf) + 1, sizeof(WCHAR));
                  if (PathIsRelativeW(buf)) {
                    wcscpy(wfile, fontdir);
                    wcscat(wfile, buf);
                  }
                  else wcscpy(wfile, buf);
                  jfile = (*env)->NewString(env, wfile, wcslen(wfile));
                  free(wfile);
                }
              }
            }
            else {
              WCHAR *wfile;
              if (FontDataMatchSystemFontsW(data1, data1Size, &wfile)) jfile = (*env)->NewString(env, wfile, wcslen(wfile));
            }
            jobject jfont = (*env)->NewObject(env, jfontcls, jfontinit, jstyle, jfamily, jfile, jfullname, mono);
            if (jfont != NULL) (*env)->SetObjectArrayElement(env, jfonts, 0, jfont);
            (*env)->DeleteLocalRef(env, jstyle);
            (*env)->DeleteLocalRef(env, jfullname);
            (*env)->DeleteLocalRef(env, jfamily);
            (*env)->DeleteLocalRef(env, jfont);
            if (jfile != NULL) (*env)->DeleteLocalRef(env, jfile);
            *(params->done) = TRUE;
          }
        }
        free(data2);
      }
    }
    free(data1);
  }
  return TRUE;
}

BOOL CALLBACK GetDefaultFontProc(const ENUMLOGFONTEXW *lpelfe, const NEWTEXTMETRICEXW *lpntme, DWORD FontType, LPARAM lParam) {
  GETDEFAULTFONTPARAMS *params = (GETDEFAULTFONTPARAMS *)lParam;
  LOGFONTW lf = lpelfe->elfLogFont;
  EnumFontFamiliesExW(params->hDC, &lf, (FONTENUMPROCW)(GetDefaultFontSubproc), (LPARAM)(params), 0);
  return TRUE;
}

JNIEXPORT jobject JNICALL Java_com_anyicomplex_gdx_dwt_backends_lwjgl3_system_windows_WindowsNatives_getDefaultFont
  (JNIEnv *env, jclass clazz) {
    jclass jfontcls = (*env)->FindClass(env, LWJGL3_FONT_HANDLE_CLASS_NAME);
    if (jfontcls == NULL) return NULL;
    jmethodID jfontinit = (*env)->GetMethodID(env, jfontcls, CLASS_INIT_NAME, LWJGL3_FONT_HANDLE_INIT_SIGNATURE);
    if (jfontinit == NULL) return NULL;
    NONCLIENTMETRICSW metrics = {
      .cbSize = sizeof(NONCLIENTMETRICSW)
    };
    if (!SystemParametersInfoW(SPI_GETNONCLIENTMETRICS, sizeof(NONCLIENTMETRICSW), (PVOID)(&metrics), 0)) return NULL;
    jobjectArray jfonts = (*env)->NewObjectArray(env, 1, jfontcls, NULL);
    if (jfonts == NULL) return NULL;
    HDC hDC = CreateCompatibleDC(NULL);
    HKEY hKeyFont;
    if (RegOpenKeyExW(HKEY_LOCAL_MACHINE, FONT_REGISTRY_KEY_NAME_W, 0, KEY_READ, &hKeyFont) == ERROR_SUCCESS) {
      DWORD cValues;
      DWORD cbMaxValueNameLen;
      if (RegQueryInfoKeyW(hKeyFont, NULL, NULL, NULL, NULL, NULL, NULL, &cValues, &cbMaxValueNameLen, NULL, NULL, NULL) == ERROR_SUCCESS) {
        if (cValues) {
          WCHAR **wkeys = calloc(cValues, sizeof(WCHAR *));
          for (DWORD i = 0; i < cValues; i ++) {
            DWORD cchValueName = cbMaxValueNameLen + 1;
            WCHAR valueName[cbMaxValueNameLen];
            DWORD type;
            if (RegEnumValueW(hKeyFont, i, valueName, &cchValueName, NULL, &type, NULL, NULL) == ERROR_SUCCESS) {
              if (type == REG_SZ) {
                WCHAR *wkey = calloc(cchValueName, sizeof(WCHAR));
                wcscpy(wkey, valueName);
                wkeys[i] = wkey;
              }
            }
          }
          BOOL done = FALSE;
          BOOL *pdone = &done;
          GETDEFAULTFONTPARAMS params = {
            .hDC = hDC, 
            .env = env, 
            .jfontcls = jfontcls, 
            .jfonts = jfonts, 
            .jfontinit = jfontinit, 
            .targetlf = metrics.lfMessageFont, 
            .hKeyFont = hKeyFont, 
            .done = pdone, 
            .wkeys = wkeys, 
            .ckeys = cValues
          };
          LOGFONTW lf;
          memcpy(&lf, &params.targetlf, sizeof(lf));
          memset(&lf.lfFaceName, 0, sizeof(lf.lfFaceName));
          EnumFontFamiliesExW(hDC, &lf, (FONTENUMPROCW)(GetDefaultFontProc), (LPARAM)(&params), 0);
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