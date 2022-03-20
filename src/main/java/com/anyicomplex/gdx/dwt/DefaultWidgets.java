package com.anyicomplex.gdx.dwt;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class DefaultWidgets implements Widgets {

    @Override
    public Texture colorTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.drawPixel(0, 0);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public BitmapFont defaultFont() {
        return null;
    }

    @Override
    public void defaultFont(BitmapFont font) {

    }

    @Override
    public Label.LabelStyle defaultLabelStyle() {
        return null;
    }

    @Override
    public void defaultLabelStyle(Label.LabelStyle style) {

    }

    @Override
    public Label label(CharSequence text) {
        return null;
    }

    @Override
    public Label label(CharSequence text, BitmapFont font) {
        return null;
    }

    @Override
    public Label label(CharSequence text, Label.LabelStyle style) {
        return null;
    }

}
