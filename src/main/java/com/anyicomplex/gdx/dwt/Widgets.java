package com.anyicomplex.gdx.dwt;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Null;

public interface Widgets {

    BitmapFont defaultFont();

    void defaultFont(BitmapFont font);

    Label.LabelStyle defaultLabelStyle();

    void defaultLabelStyle(Label.LabelStyle style);

    Label label(@Null CharSequence text);

    Label label(@Null CharSequence text, BitmapFont font);

    Label label(@Null CharSequence text, Label.LabelStyle style);

}
