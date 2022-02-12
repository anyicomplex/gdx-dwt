package com.anyicomplex.gdx.dwt.backends.lwjgl3;

import com.anyicomplex.gdx.dwt.Factory;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.factory.Lwjgl3Shell;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.glfw.GLFWNativeUtils;
import com.anyicomplex.gdx.dwt.factory.ShellConfiguration;
import com.anyicomplex.gdx.dwt.factory.Shell;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import org.lwjgl.glfw.GLFW;

public class Lwjgl3Factory implements Factory {

    @Override
    public Shell frame(ApplicationListener listener, ShellConfiguration config) {
        Lwjgl3Application app = (Lwjgl3Application) Gdx.app;
        Lwjgl3ApplicationConfiguration lwjgl3Config = generateLwjgl3Config(config);
        lwjgl3Config.setInitialVisible(false);
        Lwjgl3Window lwjgl3Window = app.newWindow(listener, lwjgl3Config);
        app.postRunnable(new Runnable() {
            @Override
            public void run() {
                long handle = lwjgl3Window.getWindowHandle();
                if (config.windowHideMaximizeButton) GLFWNativeUtils.glfwHideWindowMaximizeButton(handle);
                if (config.windowHideMinimizeButton) GLFWNativeUtils.glfwHideWindowMinimizeButton(handle);
                if (config.initialVisible) GLFW.glfwShowWindow(handle);
            }
        });
        return new Lwjgl3Shell(lwjgl3Window);
    }

    private Lwjgl3ApplicationConfiguration generateLwjgl3Config(ShellConfiguration config) {
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
        lwjgl3Config.setPreferencesConfig(config.preferencesDirectory, config.preferencesFileType);
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
