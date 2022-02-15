package com.anyicomplex.gdx.dwt.factory;

public interface ShellListener {

    void created(Shell shell);

    void iconified(boolean isIconified);

    void maximized(boolean isMaximized);

    void focusLost();

    void focusGained();

    boolean closeRequested();

    void filesDropped(String[] files);

    void refreshRequested();

}
