package com.anyicomplex.gdx.dwt.factory;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.HdpiMode;

import java.io.PrintStream;
import java.util.Arrays;

public class FormConfiguration {

    public int windowX = -1;
    public int windowY = -1;
    public int windowWidth = 640;
    public int windowHeight = 480;
    public int windowMinWidth = -1, windowMinHeight = -1, windowMaxWidth = -1, windowMaxHeight = -1;
    public boolean windowResizable = true;
    public boolean windowDecorated = true;
    public boolean windowMaximized = false;
    public Graphics.Monitor maximizedMonitor;
    public boolean autoIconify = true;
    public Files.FileType windowIconFileType;
    public String[] windowIconPaths;
    public FormListener formListener;
    public Graphics.DisplayMode fullscreenMode;
    public String title;
    public Color initialBackgroundColor = Color.BLACK;
    public boolean initialVisible = true;
    public boolean vSyncEnabled = true;

    public boolean disableAudio = false;

    /** The maximum number of threads to use for network requests. Default is {@link Integer#MAX_VALUE}. */
    public int maxNetThreads = Integer.MAX_VALUE;

    public int audioDeviceSimultaneousSources = 16;
    public int audioDeviceBufferSize = 512;
    public int audioDeviceBufferCount = 9;

    public boolean useGL30 = false;
    public int gles30ContextMajorVersion = 3;
    public int gles30ContextMinorVersion = 2;

    public int r = 8, g = 8, b = 8, a = 8;
    public int depth = 16, stencil = 0;
    public int samples = 0;
    public boolean transparentFramebuffer;

    public int idleFPS = 60;
    public int foregroundFPS = 0;

    public String preferencesDirectory;
    public Files.FileType preferencesFileType = Files.FileType.Absolute;

    public HdpiMode hdpiMode = HdpiMode.Logical;

    public boolean debug = false;
    public PrintStream debugStream = System.err;

    public boolean windowHideMaximizeButton = false;
    public boolean windowHideMinimizeButton = false;

    public boolean enableSystemTray = false;
    public Files.FileType systemTrayIconFileType;
    public String systemTrayIconPath;
    public String systemTrayTooltip = null;

    public Form parentForm;
    public Form.FormType formType = Form.FormType.Normal;

    public static FormConfiguration copy(FormConfiguration config) {
        FormConfiguration copy = new FormConfiguration();
        copy.set(config);
        return copy;
    }

    public void set(FormConfiguration config){
        windowX = config.windowX;
        windowY = config.windowY;
        windowWidth = config.windowWidth;
        windowHeight = config.windowHeight;
        windowMinWidth = config.windowMinWidth;
        windowMinHeight = config.windowMinHeight;
        windowMaxWidth = config.windowMaxWidth;
        windowMaxHeight = config.windowMaxHeight;
        windowResizable = config.windowResizable;
        windowDecorated = config.windowDecorated;
        windowMaximized = config.windowMaximized;
        maximizedMonitor = config.maximizedMonitor;
        autoIconify = config.autoIconify;
        windowIconFileType = config.windowIconFileType;
        if (config.windowIconPaths != null)
            windowIconPaths = Arrays.copyOf(config.windowIconPaths, config.windowIconPaths.length);
        formListener = config.formListener;
        fullscreenMode = config.fullscreenMode;
        title = config.title;
        initialBackgroundColor = config.initialBackgroundColor;
        initialVisible = config.initialVisible;
        vSyncEnabled = config.vSyncEnabled;
        disableAudio = config.disableAudio;
        maxNetThreads = config.maxNetThreads;
        audioDeviceSimultaneousSources = config.audioDeviceSimultaneousSources;
        audioDeviceBufferSize = config.audioDeviceBufferSize;
        audioDeviceBufferCount = config.audioDeviceBufferCount;
        useGL30 = config.useGL30;
        gles30ContextMajorVersion = config.gles30ContextMajorVersion;
        gles30ContextMinorVersion = config.gles30ContextMinorVersion;
        r = config.r;
        g = config.g;
        b = config.b;
        a = config.a;
        depth = config.depth;
        stencil = config.stencil;
        samples = config.samples;
        transparentFramebuffer = config.transparentFramebuffer;
        idleFPS = config.idleFPS;
        foregroundFPS = config.foregroundFPS;
        preferencesDirectory = config.preferencesDirectory;
        preferencesFileType = config.preferencesFileType;
        hdpiMode = config.hdpiMode;
        debug = config.debug;
        debugStream = config.debugStream;
        windowHideMaximizeButton = config.windowHideMaximizeButton;
        windowHideMinimizeButton = config.windowHideMinimizeButton;
        enableSystemTray = config.enableSystemTray;
        systemTrayIconFileType = config.systemTrayIconFileType;
        systemTrayIconPath = config.systemTrayIconPath;
        systemTrayTooltip = config.systemTrayTooltip;
        parentForm = config.parentForm;
        formType = config.formType;
    }

}
