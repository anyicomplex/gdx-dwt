package com.anyicomplex.gdx.dwt.factory;

public interface FormListener {

    void created(Form form);

    void iconified(boolean isIconified);

    void maximized(boolean isMaximized);

    void focusLost();

    void focusGained();

    boolean closeRequested();

    void filesDropped(String[] files);

    void refreshRequested();

}
