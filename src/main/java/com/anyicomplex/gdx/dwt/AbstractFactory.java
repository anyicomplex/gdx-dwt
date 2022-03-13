package com.anyicomplex.gdx.dwt;

import com.anyicomplex.gdx.dwt.factory.Form;
import com.anyicomplex.gdx.dwt.factory.FormConfiguration;
import com.anyicomplex.gdx.dwt.widgets.Popup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public abstract class AbstractFactory implements Factory {

    public Form window(Form parentForm, Window window, FormConfiguration config) {
        return null;
    }

    public Form dialog(Form parentForm, Dialog dialog, FormConfiguration config) {
        return null;
    }

    public Form tooltip(Form parentForm, Tooltip<? extends Actor> tooltip, FormConfiguration config) {
        return null;
    }

    public Form popup(Form parentForm, Popup<? extends Actor> popup, FormConfiguration config) {
        return null;
    }

}
