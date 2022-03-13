#ifndef _TRAY_H
#define _TRAY_H

#define TRAY_SUCCESS 0
#define TRAY_ERROR -1

#define TRAY_MENU_TYPE_NORMAL 0
#define TRAY_MENU_TYPE_SEPARATOR 1
#define TRAY_MENU_TYPE_CHECKBOX 2

#define TRAY_LOOPING 0
#define TRAY_EXITED -1

//#define TRAY_APPINDICATOR 1
//#define TRAY_WINAPI 1
#define TRAY_APPKIT 1

typedef struct _tray_menu tray_menu;

typedef struct _tray {
  #ifdef TRAY_WINAPI
  wchar_t *title;
  wchar_t *icon;
  #else
  char *title;
  char *icon;
  #endif
  tray_menu *menu;
} tray;

struct _tray_menu {
  #ifdef TRAY_WINAPI
  wchar_t *icon;
  wchar_t *text;
  #else
  char *icon;
  char *text;
  #endif
  int disabled;
  int checked;
  int radio_check;
  int type;

  void (*cb)(tray_menu *);
  void *context;

  tray_menu *submenu;
};

static void tray_update(tray *tray);

#if defined(TRAY_APPINDICATOR)

#include <gtk/gtk.h>
#include <libappindicator/app-indicator.h>

static AppIndicator *indicator = NULL;
static int loop_result = TRAY_LOOPING;

static void _tray_menu_cb(GtkMenuItem *item, gpointer data) {
  (void)item;
  tray_menu *m = (tray_menu *)data;
  m->cb(m);
}

static GtkMenuShell *_tray_menu(tray_menu *m) {
  GtkMenuShell *menu = (GtkMenuShell *)gtk_menu_new();
  for (; m != NULL && (m->type == TRAY_MENU_TYPE_SEPARATOR || m->text != NULL); m++) {
    GtkWidget *item;
    if (m->submenu == NULL) {
      if (m->type == TRAY_MENU_TYPE_SEPARATOR) {
        item = gtk_separator_menu_item_new();
      }
      else {
        if (m->icon == NULL) {
          if (m->type == TRAY_MENU_TYPE_CHECKBOX) {
            item = gtk_check_menu_item_new_with_label(m->text);
            if (m->radio_check) gtk_check_menu_item_set_draw_as_radio(GTK_CHECK_MENU_ITEM(item), TRUE);
            gtk_check_menu_item_set_active(GTK_CHECK_MENU_ITEM(item), !!m->checked);
          }
          else item = gtk_menu_item_new_with_label(m->text);
        }
        else {
          item = gtk_image_menu_item_new_with_label(m->text);
          GtkWidget *image;
          if (strncmp(m->icon, "/", 1) == 0) image = gtk_image_new_from_file(m->icon);
          else image = gtk_image_new_from_icon_name(m->icon, GTK_ICON_SIZE_MENU);
          gtk_image_menu_item_set_image(GTK_IMAGE_MENU_ITEM(item), image);
        }
        gtk_widget_set_sensitive(item, !m->disabled);
        if (m->cb != NULL) g_signal_connect(item, "activate", G_CALLBACK(_tray_menu_cb), m);
      }
    }
    else  {
      item = gtk_menu_item_new_with_label(m->text);
      gtk_menu_item_set_submenu(GTK_MENU_ITEM(item), GTK_WIDGET(_tray_menu(m->submenu)));
    }
    gtk_widget_show(item);
    gtk_menu_shell_append(menu, item);
  }
  return menu;
}

static int tray_init(tray *tray) {
  if (gtk_init_check(0, NULL) == FALSE) {
    return TRAY_ERROR;
  }
  indicator = app_indicator_new(tray->title == NULL ? "gdwt-tray" : tray->title, tray->icon, APP_INDICATOR_CATEGORY_APPLICATION_STATUS);
  if (tray->title != NULL) app_indicator_set_title(indicator, tray->title);
  app_indicator_set_status(indicator, APP_INDICATOR_STATUS_ACTIVE);
  tray_update(tray);
  return TRAY_SUCCESS;
}

static int tray_loop(int blocking) {
  gtk_main_iteration_do(blocking);
  return loop_result;
}

static void tray_update(tray *tray) {
  app_indicator_set_icon(indicator, tray->icon);
  // GTK is all about reference counting, so previous menu should be destroyed here
  app_indicator_set_menu(indicator, GTK_MENU(_tray_menu(tray->menu)));
}

static void tray_exit() { loop_result = TRAY_EXITED; }

#elif defined(TRAY_APPKIT)

#include <objc/objc-runtime.h>
#include <limits.h>

static id app;
static id pool;
static id statusBar;
static id statusItem;
static id statusBarButton;

static id _tray_menu(struct tray_menu *m) {
    id menu = objc_msgSend((id)objc_getClass("NSMenu"), sel_registerName("new"));
    objc_msgSend(menu, sel_registerName("autorelease"));
    objc_msgSend(menu, sel_registerName("setAutoenablesItems:"), false);

    for (; m != NULL && m->text != NULL; m++) {
      if (strcmp(m->text, "-") == 0) {
        objc_msgSend(menu, sel_registerName("addItem:"), 
          objc_msgSend((id)objc_getClass("NSMenuItem"), sel_registerName("separatorItem")));
      } else {
        id menuItem = objc_msgSend((id)objc_getClass("NSMenuItem"), sel_registerName("alloc"));
        objc_msgSend(menuItem, sel_registerName("autorelease"));
        objc_msgSend(menuItem, sel_registerName("initWithTitle:action:keyEquivalent:"),
                  objc_msgSend((id)objc_getClass("NSString"), sel_registerName("stringWithUTF8String:"), m->text),
                  sel_registerName("menuCallback:"),
                  objc_msgSend((id)objc_getClass("NSString"), sel_registerName("stringWithUTF8String:"), ""));
  
        objc_msgSend(menuItem, sel_registerName("setEnabled:"), (m->disabled ? false : true));
          objc_msgSend(menuItem, sel_registerName("setState:"), (m->checked ? 1 : 0));
          objc_msgSend(menuItem, sel_registerName("setRepresentedObject:"),
            objc_msgSend((id)objc_getClass("NSValue"), sel_registerName("valueWithPointer:"), m));
  
          objc_msgSend(menu, sel_registerName("addItem:"), menuItem);
  
          if (m->submenu != NULL) {
            objc_msgSend(menu, sel_registerName("setSubmenu:forItem:"), _tray_menu(m->submenu), menuItem);
      }
    }
  }

  return menu;
}

static void menu_callback(id self, SEL cmd, id sender) {
  struct tray_menu *m =
      (struct tray_menu *)objc_msgSend(objc_msgSend(sender, sel_registerName("representedObject")), 
                  sel_registerName("pointerValue"));

    if (m != NULL && m->cb != NULL) {
      m->cb(m);
    }
}

static int tray_init(struct tray *tray) {
    pool = objc_msgSend((id)objc_getClass("NSAutoreleasePool"),
                          sel_registerName("new"));
  
    objc_msgSend((id)objc_getClass("NSApplication"),
                          sel_registerName("sharedApplication"));
  
    Class trayDelegateClass = objc_allocateClassPair(objc_getClass("NSObject"), "Tray", 0);
    class_addProtocol(trayDelegateClass, objc_getProtocol("NSApplicationDelegate"));
    class_addMethod(trayDelegateClass, sel_registerName("menuCallback:"), (IMP)menu_callback, "v@:@");
    objc_registerClassPair(trayDelegateClass);
  
    id trayDelegate = objc_msgSend((id)trayDelegateClass,
                          sel_registerName("new"));
  
    app = objc_msgSend((id)objc_getClass("NSApplication"),
                          sel_registerName("sharedApplication"));
  
    objc_msgSend(app, sel_registerName("setDelegate:"), trayDelegate);
  
    statusBar = objc_msgSend((id)objc_getClass("NSStatusBar"),
                          sel_registerName("systemStatusBar"));
  
    statusItem = objc_msgSend(statusBar, sel_registerName("statusItemWithLength:"), -1.0);
  
    objc_msgSend(statusItem, sel_registerName("retain"));
    objc_msgSend(statusItem, sel_registerName("setHighlightMode:"), true);
    statusBarButton = objc_msgSend(statusItem, sel_registerName("button"));
    tray_update(tray);
    objc_msgSend(app, sel_registerName("activateIgnoringOtherApps:"), true);
    return 0;
}

static int tray_loop(int blocking) {
    id until = (blocking ? 
      objc_msgSend((id)objc_getClass("NSDate"), sel_registerName("distantFuture")) : 
      objc_msgSend((id)objc_getClass("NSDate"), sel_registerName("distantPast")));
  
    id event = objc_msgSend(app, sel_registerName("nextEventMatchingMask:untilDate:inMode:dequeue:"), 
                ULONG_MAX, 
                until, 
                objc_msgSend((id)objc_getClass("NSString"), 
                  sel_registerName("stringWithUTF8String:"), 
                  "kCFRunLoopDefaultMode"), 
                true);
    if (event) {
      objc_msgSend(app, sel_registerName("sendEvent:"), event);
    }
    return 0;
}

static void tray_update(struct tray *tray) {
  objc_msgSend(statusBarButton, sel_registerName("setImage:"), 
    objc_msgSend((id)objc_getClass("NSImage"), sel_registerName("imageNamed:"), 
      objc_msgSend((id)objc_getClass("NSString"), sel_registerName("stringWithUTF8String:"), tray->icon)));

  objc_msgSend(statusItem, sel_registerName("setMenu:"), _tray_menu(tray->menu));
}

static void tray_exit() { objc_msgSend(app, sel_registerName("terminate:"), app); }

#elif defined(TRAY_WINAPI)
#include <windows.h>
#include <shellapi.h>

#define WM_TRAY_CALLBACK_MESSAGE (WM_USER + 1)
#define WC_TRAY_CLASS_NAME L"TRAY"
#define ID_TRAY_FIRST 1000

static WNDCLASSEXW wc;
static NOTIFYICONDATAW nid;
static HWND hWnd;
static HMENU hmenu = NULL;

static LRESULT CALLBACK _tray_wnd_proc(HWND hWnd, UINT msg, WPARAM wparam, LPARAM lparam) {
  switch (msg) {
  case WM_CLOSE:
    DestroyWindow(hWnd);
    return TRAY_SUCCESS;
  case WM_DESTROY:
    PostQuitMessage(0);
    return TRAY_SUCCESS;
  case WM_TRAY_CALLBACK_MESSAGE:
    if (lparam == WM_LBUTTONUP || lparam == WM_RBUTTONUP) {
      POINT p;
      GetCursorPos(&p);
      SetForegroundWindow(hWnd);
      WORD cmd = TrackPopupMenu(hmenu, TPM_LEFTALIGN | TPM_RIGHTBUTTON | TPM_RETURNCMD | TPM_NONOTIFY, p.x, p.y, 0, hWnd, NULL);
      SendMessageW(hWnd, WM_COMMAND, cmd, NULL);
      return TRAY_SUCCESS;
    }
    break;
  case WM_COMMAND:
    if (wparam >= ID_TRAY_FIRST) {
      MENUITEMINFOW item = {
          .cbSize = sizeof(MENUITEMINFOW), 
          .fMask = MIIM_ID | MIIM_DATA
      };
      if (GetMenuItemInfoW(hmenu, wparam, FALSE, &item)) {
        tray_menu *menu = (tray_menu *)item.dwItemData;
        if (menu != NULL && menu->cb != NULL) {
          menu->cb(menu);
        }
      }
      return TRAY_SUCCESS;
    }
    break;
  }
  return DefWindowProcW(hWnd, msg, wparam, lparam);
}

static void Icon2Bitmap(HICON hIcon, HBITMAP *hResult) {
  HDC hDC = GetDC(NULL);
  HDC hMemDC = CreateCompatibleDC(hDC);
  int cx = GetSystemMetrics(SM_CXMENUCHECK);
  int cy = GetSystemMetrics(SM_CYMENUCHECK);
  HBITMAP hMemBmp = CreateCompatibleBitmap(hDC, cx, cy);
  HGDIOBJ hOrgBMP = SelectObject(hMemDC, hMemBmp);
  DrawIconEx(hMemDC, 0, 0, hIcon, cx, cy, 0, GetSysColorBrush(COLOR_MENU), DI_NORMAL);
  *hResult = hMemBmp;
  hMemBmp = NULL;
  SelectObject(hMemDC, hOrgBMP);
  DeleteDC(hMemDC);
  ReleaseDC(NULL, hDC);
  DestroyIcon(hIcon);
}

static HMENU _tray_menu(tray_menu *m, UINT *id) {
  HMENU hmenu = CreatePopupMenu();
  for (; m != NULL && (m->type == TRAY_MENU_TYPE_SEPARATOR || m->text != NULL); m++, (*id)++) {
    if (m->type == TRAY_MENU_TYPE_SEPARATOR) {
      InsertMenuW(hmenu, *id, MF_SEPARATOR, TRUE, L"");
    } else {
      MENUITEMINFOW item;
      memset(&item, 0, sizeof(item));
      item.cbSize = sizeof(MENUITEMINFOW);
      item.fMask = MIIM_ID | MIIM_STATE | MIIM_STRING | MIIM_DATA;
      item.fType = 0;
      item.fState = 0;
      if (m->submenu != NULL) {
        item.fMask = item.fMask | MIIM_SUBMENU;
        item.hSubMenu = _tray_menu(m->submenu, id);
      }
      if (m->icon != NULL) {
        item.fMask = item.fMask | MIIM_BITMAP;
        HICON hIcon = LoadImageW(NULL, m->icon, IMAGE_ICON, GetSystemMetrics(SM_CXMENUCHECK), GetSystemMetrics(SM_CYMENUCHECK), LR_LOADFROMFILE);
        HBITMAP hBitmap;
        Icon2Bitmap(hIcon, &hBitmap);
        item.hbmpItem = hBitmap;
      }
      if (m->disabled) item.fState |= MFS_DISABLED;
      if (m->type == TRAY_MENU_TYPE_CHECKBOX) {
        if (m->radio_check) {
          item.fMask = item.fMask | MIIM_FTYPE;
          item.fType = item.fType | MFT_RADIOCHECK;
        }
        if (m->checked) item.fState |= MFS_CHECKED;
      }
      item.wID = *id;
      item.dwTypeData = m->text;
      item.dwItemData = (ULONG_PTR)m;

      InsertMenuItemW(hmenu, *id, TRUE, &item);
    }
  }
  return hmenu;
}

static int tray_init(tray *tray) {
  memset(&wc, 0, sizeof(wc));
  wc.cbSize = sizeof(WNDCLASSEXW);
  wc.lpfnWndProc = _tray_wnd_proc;
  wc.hInstance = GetModuleHandleW(NULL);
  wc.lpszClassName = WC_TRAY_CLASS_NAME;
  if (!RegisterClassExW(&wc)) {
    return TRAY_ERROR;
  }

  hWnd = CreateWindowExW(0, WC_TRAY_CLASS_NAME, NULL, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  if (hWnd == NULL) return TRAY_ERROR;
  UpdateWindow(hWnd);

  memset(&nid, 0, sizeof(nid));
  nid.cbSize = sizeof(NOTIFYICONDATAW);
  nid.hWnd = hWnd;
  nid.uID = 0;
  nid.uFlags = NIF_ICON | NIF_MESSAGE | NIF_TIP;
  if (tray->title != NULL) wcscpy(nid.szTip, tray->title);
  nid.uCallbackMessage = WM_TRAY_CALLBACK_MESSAGE;
  Shell_NotifyIconW(NIM_ADD, &nid);

  tray_update(tray);
  return TRAY_SUCCESS;
}

static int tray_loop(int blocking) {
  MSG msg;
  if (blocking) {
    GetMessageW(&msg, NULL, 0, 0);
  } else {
    PeekMessageW(&msg, NULL, 0, 0, PM_REMOVE);
  }
  if (msg.message == WM_QUIT) {
    return -1;
  }
  TranslateMessage(&msg);
  DispatchMessageW(&msg);
  return 0;
}

static void tray_update(tray *tray) {
  HMENU prevmenu = hmenu;
  UINT id = ID_TRAY_FIRST;
  hmenu = _tray_menu(tray->menu, &id);
  SendMessageW(hWnd, WM_INITMENUPOPUP, (WPARAM)hmenu, 0);
  HICON icon;
  ExtractIconExW(tray->icon, 0, NULL, &icon, 1);
  if (nid.hIcon) {
    DestroyIcon(nid.hIcon);
  }
  nid.hIcon = icon;
  Shell_NotifyIconW(NIM_MODIFY, &nid);

  if (prevmenu != NULL) {
    DestroyMenu(prevmenu);
  }
}

static void tray_exit() {
  Shell_NotifyIconW(NIM_DELETE, &nid);
  if (nid.hIcon != 0) {
    DestroyIcon(nid.hIcon);
  }
  if (hmenu != 0) {
    DestroyMenu(hmenu);
  }
  PostQuitMessage(0);
  UnregisterClassW(WC_TRAY_CLASS_NAME, GetModuleHandleW(NULL));
}
#else
static int tray_init(tray *tray) { return TRAY_ERROR; }
static int tray_loop(int blocking) { return TRAY_EXITED; }
static void tray_update(tray *tray) {}
static void tray_exit();
#endif

#endif /* _TRAY_H */
