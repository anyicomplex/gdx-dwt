package com.anyicomplex.gdx.dwt;

import com.anyicomplex.gdx.dwt.factory.Form;
import com.anyicomplex.gdx.dwt.factory.FormConfiguration;
import com.anyicomplex.gdx.dwt.widgets.Popup;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public interface Factory {

    Form window(ApplicationListener listener, FormConfiguration config);

    Form dialog(Form parentForm, ApplicationListener listener, FormConfiguration config);

    Form tooltip(Form parentForm, ApplicationListener listener, FormConfiguration config);

    Form popup(Form parentForm, ApplicationListener listener, FormConfiguration config);

    Form window(Window window, FormConfiguration config);

    Form dialog(Form parentForm, Dialog dialog, FormConfiguration config);

    Form tooltip(Form parentForm, Tooltip<? extends Actor> tooltip, FormConfiguration config);

    Form popup(Form parentForm, Popup<? extends Actor> popup, FormConfiguration config);

}
