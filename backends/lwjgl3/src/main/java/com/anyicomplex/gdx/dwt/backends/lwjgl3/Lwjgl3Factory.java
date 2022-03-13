package com.anyicomplex.gdx.dwt.backends.lwjgl3;

import com.anyicomplex.gdx.dwt.AbstractFactory;
import com.anyicomplex.gdx.dwt.Gdwt;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.factory.Lwjgl3Form;
import com.anyicomplex.gdx.dwt.factory.Form;
import com.anyicomplex.gdx.dwt.factory.FormConfiguration;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Lwjgl3Factory extends AbstractFactory {

    @Override
    public Form window(ApplicationListener listener, FormConfiguration config) {
        return new Lwjgl3Form(listener, config);
    }

    @Override
    public Form dialog(Form parentForm, ApplicationListener listener, FormConfiguration config) {
        FormConfiguration dialogConfig = FormConfiguration.copy(config);
        dialogConfig.formType = Form.FormType.Dialog;
        dialogConfig.parentForm = parentForm;
        dialogConfig.windowHideMinimizeButton = true;
        dialogConfig.windowHideMaximizeButton = true;
        return window(listener, dialogConfig);
    }

    @Override
    public Form tooltip(Form parentForm, ApplicationListener listener, FormConfiguration config) {
        FormConfiguration tooltipConfig = FormConfiguration.copy(config);
        tooltipConfig.formType = Form.FormType.Tooltip;
        tooltipConfig.parentForm = parentForm;
        tooltipConfig.windowDecorated = false;
        return window(listener, tooltipConfig);
    }

    @Override
    public Form popup(Form parentForm, ApplicationListener listener, FormConfiguration config) {
        FormConfiguration popupConfig = FormConfiguration.copy(config);
        popupConfig.formType = Form.FormType.Popup;
        popupConfig.parentForm = parentForm;
        popupConfig.windowDecorated = false;
        return window(listener, popupConfig);
    }

    public static Lwjgl3ApplicationConfiguration generateLwjgl3Config(FormConfiguration config) {
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
