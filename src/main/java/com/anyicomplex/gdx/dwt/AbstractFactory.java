package com.anyicomplex.gdx.dwt;

import com.anyicomplex.gdx.dwt.factory.Form;
import com.anyicomplex.gdx.dwt.factory.FormConfiguration;
import com.anyicomplex.gdx.dwt.widgets.Popup;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class AbstractFactory implements Factory {

    private static class WindowApplication implements ApplicationListener {
        private Stage stage;
        private final Window window;
        public WindowApplication(Window window) {
            if (window == null) throw new NullPointerException("window cannot be null.");
            this.window = window;
        }
        @Override
        public void create() {
            stage = new Stage();
            stage.addActor(window);
            stage.setViewport(new ScreenViewport());
            Gdx.input.setInputProcessor(stage);
        }
        @Override
        public void resize(int width, int height) {
            stage.getViewport().update(width, height);
            window.setBounds(0, 0, width, height);
        }
        @Override
        public void render() {
            ScreenUtils.clear(Color.BLACK);
            stage.act();
            stage.draw();
        }
        @Override
        public void pause() {}
        @Override
        public void resume() {}
        @Override
        public void dispose() {
            stage.dispose();
        }
    }

    public static FormConfiguration generateFormConfig(Window window, FormConfiguration config) {
        FormConfiguration formConfig = FormConfiguration.copy(config);
        formConfig.title = window.getTitleLabel().getText().toString();
        formConfig.windowWidth = (int) window.getWidth();
        formConfig.windowHeight = (int) window.getHeight();
        formConfig.windowMinWidth = config.windowMinWidth == -1 ? (int) window.getMinWidth() : config.windowMinWidth;
        formConfig.windowMinHeight = config.windowMinHeight == -1 ? (int) window.getMinHeight() : config.windowMinHeight;
        float maxWidth = window.getMaxWidth();
        float maxHeight = window.getMaxHeight();
        formConfig.windowMaxWidth = config.windowMaxWidth == -1 ? (maxWidth < 1 ? -1 : (int) maxWidth) : config.windowMaxWidth;
        formConfig.windowMaxHeight = config.windowMaxHeight == -1 ? (maxHeight < 1 ? -1 : (int) maxHeight) : config.windowMaxHeight;
        return formConfig;
    }

    public Form window(Window window, FormConfiguration config) {
        if (window == null) throw new NullPointerException("window cannot be null.");
        if (config.windowDecorated) window.getTitleTable().clearChildren();
        return window(new WindowApplication(window), generateFormConfig(window, config));
    }

    public Form dialog(Form parentForm, Dialog dialog, FormConfiguration config) {
        if (dialog == null) throw new NullPointerException("dialog cannot be null.");
        if (config.windowDecorated) dialog.getTitleTable().clearChildren();
        return dialog(parentForm, new WindowApplication(dialog), generateFormConfig(dialog, config));
    }

    public Form tooltip(Form parentForm, Tooltip<? extends Actor> tooltip, FormConfiguration config) {
        return null;
    }

    public Form popup(Form parentForm, Popup<? extends Actor> popup, FormConfiguration config) {
        return null;
    }

}
