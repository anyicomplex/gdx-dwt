package com.anyicomplex.gdx.dwt.backends.lwjgl3.jnativehook;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.utils.Lwjgl3FilePaths;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.utils.Lwjgl3TmpFiles;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.utils.SharedLibraryHelper;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.github.kwhat.jnativehook.NativeLibraryLocator;

import java.io.File;
import java.util.Iterator;

import static com.badlogic.gdx.utils.SharedLibraryLoader.*;

public class SharedLibraryLocator implements NativeLibraryLocator {

    @Override
    public Iterator<File> getLibraries() {
        Array<File> files = new Array<>(1);
        String system;
        if (isWindows) system = "windows";
        else if (isLinux) system = "linux";
        else if (isMac) system = "darwin";
        else throw new IllegalStateException("Unable to load JNativeHook library: \nUnsupported platform.");
        String arch = isARM ? (is64Bit ? "arm64" : "arm") : (is64Bit ? "x86_64" : "x86");
        FileHandle jnhLib = new Lwjgl3FileHandle(Lwjgl3FilePaths.build(
                        "com/github/kwhat/jnativehook/lib",
                        "/" + system + "/" + arch + "/",
                        SharedLibraryHelper.mapLibName("JNativeHook")), Files.FileType.Classpath);
        files.add(Lwjgl3TmpFiles.getTmpLib(jnhLib, "JNativeHook-2.2.1", jnhLib.name(), false).file());
        return files.iterator();
    }

}
