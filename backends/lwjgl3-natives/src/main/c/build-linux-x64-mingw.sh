#!/usr/bin/sh

FILE_NAME=gdwt64.dll

x86_64-w64-mingw32-gcc \
-D_FILE_OFFSET_BITS=64 \
-std=c99 \
-Wall \
-Wextra \
-O3 \
-s \
-Iinclude \
-Iinclude/windows \
-I$WINDOWS_X64_JAVA_HOME/include \
-I$WINDOWS_X64_JAVA_HOME/include/win32 \
-I/usr/x86_64-w64-mingw32/sys-root/mingw/include/gdiplus \
src/windows/windows_natives.c \
src/tray.c \
-fpic \
-shared \
-o ../resources/$FILE_NAME \
-luser32 \
-lgdi32 \
-lgdiplus \
-lshlwapi \
-ladvapi32