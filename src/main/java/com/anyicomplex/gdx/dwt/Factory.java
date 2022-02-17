package com.anyicomplex.gdx.dwt;

import com.anyicomplex.gdx.dwt.factory.ShellConfiguration;
import com.anyicomplex.gdx.dwt.factory.Shell;
import com.badlogic.gdx.ApplicationListener;

public interface Factory {

    Shell frame(ApplicationListener listener, ShellConfiguration config);

    Shell dialog(Shell parentShell, ApplicationListener listener, ShellConfiguration config);

    Shell tooltip(Shell parentShell, ApplicationListener listener, ShellConfiguration config);

    Shell popup(ApplicationListener listener, ShellConfiguration config);

}
