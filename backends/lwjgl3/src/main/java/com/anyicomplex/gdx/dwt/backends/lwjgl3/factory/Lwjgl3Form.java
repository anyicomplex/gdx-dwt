package com.anyicomplex.gdx.dwt.backends.lwjgl3.factory;

import com.anyicomplex.gdx.dwt.Gdwt;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.Lwjgl3Factory;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.glfw.GLFWNativeUtils;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.windows.WindowsNatives;
import com.anyicomplex.gdx.dwt.factory.Form;
import com.anyicomplex.gdx.dwt.factory.FormConfiguration;
import com.anyicomplex.gdx.dwt.factory.FormListener;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;

import java.nio.IntBuffer;

import static com.badlogic.gdx.utils.SharedLibraryLoader.isWindows;

public class Lwjgl3Form extends Form {

    private final Array<Form> childForms = new Array<>();
    private final Form parentForm;
    private final FormType formType;

    private final ApplicationListener applicationListener;
    private final FormConfiguration formConfig;
    private final Lwjgl3ApplicationConfiguration lwjgl3Config;
    private final boolean isRootForm;

    private Lwjgl3Window window;

    private final IntBuffer tmpBuffer;
    private final IntBuffer tmpBuffer2;

    @Override
    public FormType type() {
        return formType;
    }

    @Override
    public boolean isRootForm() {
        return isRootForm;
    }

    @Override
    public Form parentForm() {
        return parentForm;
    }

    @Override
    public Array<Form> childForms() {
        return childForms;
    }

    public Lwjgl3Form(ApplicationListener listener, FormConfiguration config) {
        if (config == null) throw new GdxRuntimeException("config cannot be null.");
        this.tmpBuffer = BufferUtils.createIntBuffer(1);
        this.tmpBuffer2 = BufferUtils.createIntBuffer(1);
        applicationListener = listener;
        formConfig = FormConfiguration.copy(config);
        lwjgl3Config = Lwjgl3Factory.generateLwjgl3Config(formConfig);
        lwjgl3Config.setInitialVisible(false);
        FormListener formListener = formConfig.formListener;
        isRootForm = Gdwt.toolkit.rootForm() == null;
        formType = formConfig.formType;
        if (isRootForm) parentForm = null;
        else if (formType == FormType.Normal) parentForm = Gdwt.toolkit.rootForm();
        else {
            switch (formType) {
                case Dialog:
                case Tooltip:
                case Popup:
                    parentForm = formConfig.parentForm;
                    break;
                default:
                    parentForm = null;
                    break;
            }
        }
        lwjgl3Config.setWindowListener(new Lwjgl3WindowListener() {
            @Override
            public void created(Lwjgl3Window window) {
                Lwjgl3Form.this.window = window;
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        long handle = window.getWindowHandle();
                        if (formConfig.windowDecorated) GLFWNativeUtils.glfwHideWindowButtons(handle,
                                formConfig.windowHideMaximizeButton, formConfig.windowHideMinimizeButton);
                        boolean shouldFocusParent = false;
                        if (!isRootForm && parentForm != null) {
                            parentForm.childForms().add(Lwjgl3Form.this);
                            switch (formType) {
                                case Dialog:
                                    GLFWNativeUtils.glfwSetWindowIsDialog(handle, ((Lwjgl3Form) parentForm).getWindow().getWindowHandle());
                                    break;
                                case Tooltip:
                                    GLFWNativeUtils.glfwSetWindowIsTooltip(handle, ((Lwjgl3Form) parentForm).getWindow().getWindowHandle());
                                    GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_FOCUS_ON_SHOW, GLFW.GLFW_FALSE);
                                    GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_FLOATING, GLFW.GLFW_TRUE);
                                    shouldFocusParent = true;
                                    break;
                                case Popup:
                                    GLFWNativeUtils.glfwSetWindowIsPopup(handle, ((Lwjgl3Form) parentForm).getWindow().getWindowHandle());
                                    GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_FOCUS_ON_SHOW, GLFW.GLFW_FALSE);
                                    GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_FLOATING, GLFW.GLFW_TRUE);
                                    shouldFocusParent = true;
                                    break;
                            }
                        }
                        if (formConfig.initialVisible) GLFW.glfwShowWindow(handle);
                        if (shouldFocusParent) GLFW.glfwFocusWindow(((Lwjgl3Form) parentForm).getWindow().getWindowHandle());
                    }
                });
                if (formListener != null) formListener.created(Lwjgl3Form.this);
            }
            @Override
            public void iconified(boolean isIconified) {
                if (formListener != null) formListener.iconified(isIconified);
            }
            @Override
            public void maximized(boolean isMaximized) {
                if (formListener != null) formListener.maximized(isMaximized);
            }
            @Override
            public void focusLost() {
                if (formListener != null) formListener.focusLost();
            }
            @Override
            public void focusGained() {
                if (formListener != null) formListener.focusGained();
            }
            @Override
            public boolean closeRequested() {
                if (formListener == null) {
                    closeAllChildForms();
                    if (isWindows && formType == FormType.Dialog && parentForm != null)
                        WindowsNatives.enableWindow(GLFWNativeWin32.glfwGetWin32Window(((Lwjgl3Form) parentForm).window.getWindowHandle()), true);
                    return true;
                }
                else {
                    boolean close = formListener.closeRequested();
                    if (close) {
                        closeAllChildForms();
                        if (isWindows && formType == FormType.Dialog && parentForm != null)
                            WindowsNatives.enableWindow(GLFWNativeWin32.glfwGetWin32Window(((Lwjgl3Form) parentForm).window.getWindowHandle()), true);
                    }
                    return close;
                }
            }
            @Override
            public void filesDropped(String[] files) {
                if (formListener != null) formListener.filesDropped(files);
            }
            @Override
            public void refreshRequested() {
                if (formListener != null) formListener.refreshRequested();
            }
        });
        if (!isRootForm) {
            ((Lwjgl3Application)Gdx.app).newWindow(listener, lwjgl3Config);
        }
    }

    public void loop() {
        if (isRootForm) {
            if (formConfig.enableSystemTray) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Lwjgl3Tray.init(formConfig.systemTrayTooltip,
                                new Lwjgl3FileHandle(formConfig.systemTrayIconPath, formConfig.systemTrayIconFileType));
                        while (true) {
                            if (!Lwjgl3Tray.loop(false)) break;
                        }
                    }
                }).start();
            }
            new Lwjgl3Application(applicationListener, lwjgl3Config);
            if (formConfig.enableSystemTray) Lwjgl3Tray.unInit();
        }
        else {
            throw new GdxRuntimeException("Not root form!");
        }
    }

    public Lwjgl3Window getWindow() {
        return window;
    }

    @Override
    public void close() {
        window.closeWindow();
        if (parentForm != null) parentForm.childForms().removeValue(this, true);
        closeAllChildForms();
    }

    @Override
    public int positionX() {
        return window.getPositionX();
    }

    @Override
    public int positionY() {
        return window.getPositionY();
    }

    @Override
    public int width() {
        GLFW.glfwGetWindowSize(window.getWindowHandle(), tmpBuffer, tmpBuffer2);
        return tmpBuffer.get(0);
    }

    @Override
    public int height() {
        GLFW.glfwGetWindowSize(window.getWindowHandle(), tmpBuffer, tmpBuffer2);
        return tmpBuffer2.get(0);
    }

}
