package com.anyicomplex.gdx.dwt;

import com.anyicomplex.gdx.dwt.factory.ShellConfiguration;
import com.anyicomplex.gdx.dwt.factory.Shell;
import com.anyicomplex.gdx.dwt.widgets.Popup;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public interface Factory {

    Shell window(ApplicationListener listener, ShellConfiguration config);

    Shell dialog(Shell parentShell, ApplicationListener listener, ShellConfiguration config);

    Shell tooltip(Shell parentShell, ApplicationListener listener, ShellConfiguration config);

    Shell popup(Shell parentShell, ApplicationListener listener, ShellConfiguration config);

    Shell window(Shell parentShell, Window window, ShellConfiguration config);

    Shell dialog(Shell parentShell, Dialog dialog, ShellConfiguration config);

    Shell tooltip(Shell parentShell, Tooltip<? extends Actor> tooltip, ShellConfiguration config);

    Shell popup(Shell parentShell, Popup<? extends Actor> popup, ShellConfiguration config);

}
