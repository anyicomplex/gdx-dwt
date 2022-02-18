package com.anyicomplex.gdx.dwt.backends.lwjgl3;

import com.anyicomplex.gdx.dwt.Factory;
import com.anyicomplex.gdx.dwt.Gdwt;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.factory.Lwjgl3Shell;
import com.anyicomplex.gdx.dwt.factory.Shell;
import com.anyicomplex.gdx.dwt.factory.ShellConfiguration;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Lwjgl3Factory implements Factory {

    @Override
    public Shell frame(ApplicationListener listener, ShellConfiguration config) {
        return new Lwjgl3Shell(listener, config);
    }

    @Override
    public Shell dialog(Shell parentShell, ApplicationListener listener, ShellConfiguration config) {
        ShellConfiguration dialogConfig = ShellConfiguration.copy(config);
        dialogConfig.parentShell = parentShell;
        dialogConfig.windowHideMinimizeButton = true;
        dialogConfig.windowHideMaximizeButton = true;
        dialogConfig.shellType = Shell.ShellType.Dialog;
        return frame(listener, dialogConfig);
    }

    @Override
    public Shell tooltip(Shell parentShell, ApplicationListener listener, ShellConfiguration config) {
        return null;
    }

    @Override
    public Shell popup(ApplicationListener listener, ShellConfiguration config) {
        return null;
    }

    public static Lwjgl3ApplicationConfiguration generateLwjgl3Config(ShellConfiguration config) {
        Lwjgl3ApplicationConfiguration lwjgl3Config = new Lwjgl3ApplicationConfiguration();
        lwjgl3Config.setInitialVisible(config.initialVisible);
        lwjgl3Config.disableAudio(config.disableAudio);
        lwjgl3Config.setAudioConfig(config.audioDeviceSimultaneousSources, config.audioDeviceBufferSize, config.audioDeviceBufferCount);
        lwjgl3Config.setBackBufferConfig(config.r, config.g, config.b, config.a, config.depth, config.stencil, config.samples);
        lwjgl3Config.enableGLDebugOutput(config.debug, config.debugStream);
        lwjgl3Config.setForegroundFPS(config.foregroundFPS);
        lwjgl3Config.setHdpiMode(config.hdpiMode);
        lwjgl3Config.setIdleFPS(config.idleFPS);
        lwjgl3Config.setMaxNetThreads(config.maxNetThreads);
        lwjgl3Config.setPreferencesConfig(config.preferencesDirectory == null ?
                Gdwt.toolkit.prefsDir(null, null) : config.preferencesDirectory, config.preferencesFileType);
        lwjgl3Config.useOpenGL3(config.useGL30, config.gles30ContextMajorVersion, config.gles30ContextMinorVersion);
        lwjgl3Config.setAutoIconify(config.autoIconify);
        lwjgl3Config.setDecorated(config.windowDecorated);
        lwjgl3Config.setFullscreenMode(config.fullscreenMode);
        lwjgl3Config.setInitialBackgroundColor(config.initialBackgroundColor);
        lwjgl3Config.setMaximized(config.windowMaximized);
        lwjgl3Config.setMaximizedMonitor(config.maximizedMonitor);
        lwjgl3Config.setResizable(config.windowResizable);
        lwjgl3Config.setTitle(config.title);
        lwjgl3Config.setWindowedMode(config.windowWidth, config.windowHeight);
        lwjgl3Config.setWindowIcon(config.windowIconFileType, config.windowIconPaths);
        lwjgl3Config.setWindowPosition(config.windowX, config.windowY);
        lwjgl3Config.setWindowSizeLimits(config.windowMinWidth, config.windowMinHeight, config.windowMaxWidth, config.windowMaxHeight);

        // Prevent error
        if (config.title == null) lwjgl3Config.setTitle("");
        if (config.windowIconPaths == null || config.windowIconPaths.length < 1) {
            lwjgl3Config.setWindowIcon();
        }

        return lwjgl3Config;
    }

}
