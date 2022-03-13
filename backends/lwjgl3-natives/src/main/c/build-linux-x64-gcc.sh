#!/usr/bin/sh

FILE_NAME=libgdwt64.so

gcc \
-D_FILE_OFFSET_BITS=64 \
-std=c99 \
-Wall \
-Wextra \
-O3 \
-s \
$(pkg-config --cflags gtk+-3.0) \
$(pkg-config --cflags fontconfig) \
$(pkg-config --cflags x11) \
-Iinclude \
-Iinclude/linux \
-I$LINUX_X64_JAVA_HOME/include \
-I$LINUX_X64_JAVA_HOME/include/linux \
src/linux/linux_natives.c \
-fpic \
-shared \
-o ../resources/$FILE_NAME \
$(pkg-config --libs gtk+-3.0) \
$(pkg-config --libs fontconfig) \
$(pkg-config --libs x11)