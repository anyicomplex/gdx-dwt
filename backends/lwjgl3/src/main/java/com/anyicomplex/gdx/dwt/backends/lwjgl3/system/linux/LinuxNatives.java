package com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.toolkit.Lwjgl3FontHandle;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.utils.Lwjgl3TmpFiles;
import com.anyicomplex.xdg.utils.*;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.files.FileHandle;

public class LinuxNatives {

    public static native Lwjgl3FontHandle[] getSystemFonts();

    public static native Lwjgl3FontHandle getGtkDefaultFont();

    public static native void hideXWindowButtons(long display, long w, boolean maximize, boolean minimize);

    public static native void setXWindowIsDialog(long display, long w, long parent);

    public static native void setXWindowIsNormal(long display, long w);

    public static native void setXWindowIsTooltip(long display, long w, long parent);

    public static native void setXWindowIsPopup(long display, long w, long parent);

    public static void validXDGUtilsScripts() {
        String outputPath = "xdg-utils-" + XDGUtils.SCRIPT_VERSION;
        FileHandle XDGDesktopIconBin = new Lwjgl3FileHandle(XDGDesktopIcon.FILE_NAME, Files.FileType.Internal);
        FileHandle XDGDesktopMenuBin = new Lwjgl3FileHandle(XDGDesktopMenu.FILE_NAME, Files.FileType.Internal);
        FileHandle XDGEmailBin = new Lwjgl3FileHandle(XDGEmail.FILE_NAME, Files.FileType.Internal);
        FileHandle XDGIconResourceBin = new Lwjgl3FileHandle(XDGIconResource.FILE_NAME, Files.FileType.Internal);
        FileHandle XDGMimeBin = new Lwjgl3FileHandle(XDGMime.FILE_NAME, Files.FileType.Internal);
        FileHandle XDGOpenBin = new Lwjgl3FileHandle(XDGOpen.FILE_NAME, Files.FileType.Internal);
        FileHandle XDGScreenSaverBin = new Lwjgl3FileHandle(XDGScreenSaver.FILE_NAME, Files.FileType.Internal);
        FileHandle XDGSettingsBin = new Lwjgl3FileHandle(XDGSettings.FILE_NAME, Files.FileType.Internal);
        Lwjgl3TmpFiles.getTmpBin(XDGDesktopIconBin, outputPath, XDGDesktopMenuBin.name(), false);
        Lwjgl3TmpFiles.getTmpBin(XDGDesktopMenuBin, outputPath, XDGDesktopMenuBin.name(), false);
        Lwjgl3TmpFiles.getTmpBin(XDGEmailBin, outputPath, XDGEmailBin.name(), false);
        Lwjgl3TmpFiles.getTmpBin(XDGIconResourceBin, outputPath, XDGIconResourceBin.name(), false);
        Lwjgl3TmpFiles.getTmpBin(XDGMimeBin, outputPath, XDGMimeBin.name(), false);
        Lwjgl3TmpFiles.getTmpBin(XDGOpenBin, outputPath, XDGOpenBin.name(), false);
        Lwjgl3TmpFiles.getTmpBin(XDGScreenSaverBin, outputPath, XDGScreenSaverBin.name(), false);
        Lwjgl3TmpFiles.getTmpBin(XDGSettingsBin, outputPath, XDGSettingsBin.name(), false);
    }

    public static void open(String path) {
        validXDGUtilsScripts();
        XDGOpen.process(null, path);
    }

    public static native int XGrabPointer(long display, long w);

    public static native int XUngrabPointer(long display);

}
