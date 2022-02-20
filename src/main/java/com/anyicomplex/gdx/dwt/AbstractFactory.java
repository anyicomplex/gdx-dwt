package com.anyicomplex.gdx.dwt;

import com.anyicomplex.gdx.dwt.factory.Shell;
import com.anyicomplex.gdx.dwt.factory.ShellConfiguration;
import com.anyicomplex.gdx.dwt.widgets.Popup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public abstract class AbstractFactory implements Factory {

    public Shell window(Shell parentShell, Window window, ShellConfiguration config) {
        return null;
    }

    public Shell dialog(Shell parentShell, Dialog dialog, ShellConfiguration config) {
        return null;
    }

    public Shell tooltip(Shell parentShell, Tooltip<? extends Actor> tooltip, ShellConfiguration config) {
        return null;
    }

    public Shell popup(Shell parentShell, Popup<? extends Actor> popup, ShellConfiguration config) {
        return null;
    }

}
