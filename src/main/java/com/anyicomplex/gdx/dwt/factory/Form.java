package com.anyicomplex.gdx.dwt.factory;

import com.badlogic.gdx.utils.Array;

public abstract class Form {

    public enum FormType {
        Normal,
        Dialog,
        Tooltip,
        Popup
    }

    public abstract void close();

    public abstract FormType type();

    public abstract boolean isRootForm();

    public abstract Form parentForm();

    public abstract Array<Form> childForms();

    public void closeAllChildForms() {
        Array<Form> forms = childForms();
        if (forms != null) {
            for (Form form : forms) form.close();
        }
    }

    public void closeChildForms(FormType type) {
        Array<Form> forms = childForms();
        if (forms != null) {
            for (Form form : forms) {
                if (form.type() == type) form.close();
            }
        }
    }

    public abstract int positionX();

    public abstract int positionY();

    public abstract int width();

    public abstract int height();

    public boolean contains(int screenX, int screenY) {
        int positionX = positionX();
        int positionY = positionY();
        int width = width();
        int height = height();
        return positionX <= screenX && positionX + width >= screenX && positionY <= screenY && positionY + height >= screenY;
    }

}
