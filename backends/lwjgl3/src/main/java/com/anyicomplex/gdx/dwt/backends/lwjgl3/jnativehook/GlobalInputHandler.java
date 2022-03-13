package com.anyicomplex.gdx.dwt.backends.lwjgl3.jnativehook;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

public class GlobalInputHandler implements NativeMouseInputListener, NativeMouseWheelListener, NativeKeyListener {

    private InputProcessor inputProcessor;

    public void setInputProcessor(InputProcessor processor) {
        inputProcessor = processor;
    }

    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (inputProcessor == null) return;
        System.out.println(nativeEvent.getKeyCode());
        inputProcessor.keyDown(mapKeyCode(nativeEvent.getKeyCode(), nativeEvent.getKeyLocation()));
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        if (inputProcessor == null) return;
        inputProcessor.keyUp(mapKeyCode(nativeEvent.getKeyCode(), nativeEvent.getKeyLocation()));
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
        if (inputProcessor == null) return;
        inputProcessor.keyTyped(nativeEvent.getKeyChar());
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
        if (inputProcessor == null) return;
        inputProcessor.touchDragged(nativeEvent.getX(), nativeEvent.getY(), 0);
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        if (inputProcessor == null) return;
        inputProcessor.mouseMoved(nativeEvent.getX(), nativeEvent.getY());
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        if (inputProcessor == null) return;
        inputProcessor.touchDown(nativeEvent.getX(), nativeEvent.getY(), 0, mapButton(nativeEvent.getButton()));
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        if (inputProcessor == null) return;
        inputProcessor.touchUp(nativeEvent.getX(), nativeEvent.getY(), 0, mapButton(nativeEvent.getButton()));
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeEvent) {
        if (inputProcessor == null || nativeEvent.getScrollType() != NativeMouseWheelEvent.WHEEL_UNIT_SCROLL) return;
        switch (nativeEvent.getWheelDirection()) {
            case NativeMouseWheelEvent.WHEEL_VERTICAL_DIRECTION:
                inputProcessor.scrolled(0, nativeEvent.getScrollAmount());
                break;
            case NativeMouseWheelEvent.WHEEL_HORIZONTAL_DIRECTION:
                inputProcessor.scrolled(nativeEvent.getScrollAmount(), 0);
                break;
        }
    }

    public static int mapKeyCode(int jnhKeyCode, int jnhKeyLocation) {
        switch (jnhKeyCode) {
            case NativeKeyEvent.VC_ESCAPE:
                return Input.Keys.ESCAPE;
            case NativeKeyEvent.VC_F1:
                return Input.Keys.F1;
            case NativeKeyEvent.VC_F2:
                return Input.Keys.F2;
            case NativeKeyEvent.VC_F3:
                return Input.Keys.F3;
            case NativeKeyEvent.VC_F4:
                return Input.Keys.F4;
            case NativeKeyEvent.VC_F5:
                return Input.Keys.F5;
            case NativeKeyEvent.VC_F6:
                return Input.Keys.F6;
            case NativeKeyEvent.VC_F7:
                return Input.Keys.F7;
            case NativeKeyEvent.VC_F8:
                return Input.Keys.F8;
            case NativeKeyEvent.VC_F9:
                return Input.Keys.F9;
            case NativeKeyEvent.VC_F10:
                return Input.Keys.F10;
            case NativeKeyEvent.VC_F11:
                return Input.Keys.F11;
            case NativeKeyEvent.VC_F12:
                return Input.Keys.F12;
            case NativeKeyEvent.VC_F13:
                return Input.Keys.F13;
            case NativeKeyEvent.VC_F14:
                return Input.Keys.F14;
            case NativeKeyEvent.VC_F15:
                return Input.Keys.F15;
            case NativeKeyEvent.VC_F16:
                return Input.Keys.F16;
            case NativeKeyEvent.VC_F17:
                return Input.Keys.F17;
            case NativeKeyEvent.VC_F18:
                return Input.Keys.F18;
            case NativeKeyEvent.VC_F19:
                return Input.Keys.F19;
            case NativeKeyEvent.VC_F20:
                return Input.Keys.F20;
            case NativeKeyEvent.VC_F21:
                return Input.Keys.F21;
            case NativeKeyEvent.VC_F22:
                return Input.Keys.F22;
            case NativeKeyEvent.VC_F23:
                return Input.Keys.F23;
            case NativeKeyEvent.VC_F24:
                return Input.Keys.F24;
            case NativeKeyEvent.VC_BACKQUOTE:
                return Input.Keys.BACK;
            case NativeKeyEvent.VC_1:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_1;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.NUM_1;
                }
            case NativeKeyEvent.VC_2:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_2;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.NUM_2;
                }
            case NativeKeyEvent.VC_3:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_3;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.NUM_3;
                }
            case NativeKeyEvent.VC_4:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_4;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.NUM_4;
                }
            case NativeKeyEvent.VC_5:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_5;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.NUM_5;
                }
            case NativeKeyEvent.VC_6:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_6;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.NUM_6;
                }
            case NativeKeyEvent.VC_7:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_7;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.NUM_7;
                }
            case NativeKeyEvent.VC_8:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_8;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.NUM_8;
                }
            case NativeKeyEvent.VC_9:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_9;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.NUM_9;
                }
            case NativeKeyEvent.VC_0:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_0;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.NUM_0;
                }
            case NativeKeyEvent.VC_MINUS:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_SUBTRACT;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.MINUS;
                }
            case NativeKeyEvent.VC_EQUALS:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_EQUALS;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.EQUALS;
                }
            case NativeKeyEvent.VC_BACKSPACE:
                return Input.Keys.BACKSPACE;
            case NativeKeyEvent.VC_TAB:
                return Input.Keys.TAB;
            case NativeKeyEvent.VC_CAPS_LOCK:
                return Input.Keys.CAPS_LOCK;
            case NativeKeyEvent.VC_A:
                return Input.Keys.A;
            case NativeKeyEvent.VC_B:
                return Input.Keys.B;
            case NativeKeyEvent.VC_C:
                return Input.Keys.C;
            case NativeKeyEvent.VC_D:
                return Input.Keys.D;
            case NativeKeyEvent.VC_E:
                return Input.Keys.E;
            case NativeKeyEvent.VC_F:
                return Input.Keys.F;
            case NativeKeyEvent.VC_G:
                return Input.Keys.G;
            case NativeKeyEvent.VC_H:
                return Input.Keys.H;
            case NativeKeyEvent.VC_I:
                return Input.Keys.I;
            case NativeKeyEvent.VC_J:
                return Input.Keys.J;
            case NativeKeyEvent.VC_K:
                return Input.Keys.K;
            case NativeKeyEvent.VC_L:
                return Input.Keys.L;
            case NativeKeyEvent.VC_M:
                return Input.Keys.M;
            case NativeKeyEvent.VC_N:
                return Input.Keys.N;
            case NativeKeyEvent.VC_O:
                return Input.Keys.O;
            case NativeKeyEvent.VC_P:
                return Input.Keys.P;
            case NativeKeyEvent.VC_Q:
                return Input.Keys.Q;
            case NativeKeyEvent.VC_R:
                return Input.Keys.R;
            case NativeKeyEvent.VC_S:
                return Input.Keys.S;
            case NativeKeyEvent.VC_T:
                return Input.Keys.T;
            case NativeKeyEvent.VC_U:
                return Input.Keys.U;
            case NativeKeyEvent.VC_V:
                return Input.Keys.V;
            case NativeKeyEvent.VC_W:
                return Input.Keys.W;
            case NativeKeyEvent.VC_X:
                return Input.Keys.X;
            case NativeKeyEvent.VC_Y:
                return Input.Keys.Y;
            case NativeKeyEvent.VC_Z:
                return Input.Keys.Z;
            case NativeKeyEvent.VC_OPEN_BRACKET:
                return Input.Keys.LEFT_BRACKET;
            case NativeKeyEvent.VC_CLOSE_BRACKET:
                return Input.Keys.RIGHT_BRACKET;
            case NativeKeyEvent.VC_BACK_SLASH:
                return Input.Keys.BACKSLASH;
            case NativeKeyEvent.VC_SEMICOLON:
                return Input.Keys.SEMICOLON;
            case NativeKeyEvent.VC_QUOTE:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_LEFT:
                        return Input.Keys.COMMA;
                    case NativeKeyEvent.KEY_LOCATION_RIGHT:
                    default:
                        return Input.Keys.PERIOD;
                }
            case NativeKeyEvent.VC_ENTER:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_ENTER;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.ENTER;
                }
            case NativeKeyEvent.VC_COMMA:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_COMMA;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.COMMA;
                }
            case NativeKeyEvent.VC_PERIOD:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_DOT;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.PERIOD;
                }
            case NativeKeyEvent.VC_SLASH:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_NUMPAD:
                        return Input.Keys.NUMPAD_DIVIDE;
                    case NativeKeyEvent.KEY_LOCATION_STANDARD:
                    default:
                        return Input.Keys.SLASH;
                }
            case NativeKeyEvent.VC_SPACE:
                return Input.Keys.SPACE;
            case NativeKeyEvent.VC_PRINTSCREEN:
                return Input.Keys.PRINT_SCREEN;
            case NativeKeyEvent.VC_SCROLL_LOCK:
                return Input.Keys.SCROLL_LOCK;
            case NativeKeyEvent.VC_PAUSE:
                return Input.Keys.PAUSE;
            case NativeKeyEvent.VC_INSERT:
                return Input.Keys.INSERT;
            case NativeKeyEvent.VC_DELETE:
                return Input.Keys.DEL;
            case NativeKeyEvent.VC_HOME:
                return Input.Keys.HOME;
            case NativeKeyEvent.VC_END:
                return Input.Keys.END;
            case NativeKeyEvent.VC_PAGE_UP:
                return Input.Keys.PAGE_UP;
            case NativeKeyEvent.VC_PAGE_DOWN:
                return Input.Keys.PAGE_DOWN;
            case NativeKeyEvent.VC_UP:
                return Input.Keys.UP;
            case NativeKeyEvent.VC_LEFT:
                return Input.Keys.LEFT;
            case NativeKeyEvent.VC_CLEAR:
                return Input.Keys.CLEAR;
            case NativeKeyEvent.VC_RIGHT:
                return Input.Keys.RIGHT;
            case NativeKeyEvent.VC_DOWN:
                return Input.Keys.DOWN;
            case NativeKeyEvent.VC_NUM_LOCK:
                return Input.Keys.NUM_LOCK;
            case NativeKeyEvent.VC_SEPARATOR:
                return Input.Keys.SLASH;
            case NativeKeyEvent.VC_SHIFT:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_LEFT:
                        return Input.Keys.SHIFT_LEFT;
                    case NativeKeyEvent.KEY_LOCATION_RIGHT:
                    default:
                        return Input.Keys.SHIFT_RIGHT;
                }
            case NativeKeyEvent.VC_CONTROL:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_LEFT:
                        return Input.Keys.CONTROL_LEFT;
                    case NativeKeyEvent.KEY_LOCATION_RIGHT:
                    default:
                        return Input.Keys.CONTROL_RIGHT;
                }
            case NativeKeyEvent.VC_ALT:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_LEFT:
                        return Input.Keys.ALT_LEFT;
                    case NativeKeyEvent.KEY_LOCATION_RIGHT:
                    default:
                        return Input.Keys.ALT_RIGHT;
                }
            case NativeKeyEvent.VC_META:
                switch (jnhKeyLocation) {
                    case NativeKeyEvent.KEY_LOCATION_LEFT:
                        return Input.Keys.META_ALT_LEFT_ON;
                    case NativeKeyEvent.KEY_LOCATION_RIGHT:
                    default:
                        return Input.Keys.META_ALT_RIGHT_ON;
                }
            case NativeKeyEvent.VC_CONTEXT_MENU:
                return Input.Keys.MENU;
            case NativeKeyEvent.VC_POWER:
                return Input.Keys.POWER;
            case NativeKeyEvent.VC_MEDIA_PLAY:
                return Input.Keys.MEDIA_PLAY_PAUSE;
            case NativeKeyEvent.VC_MEDIA_STOP:
                return Input.Keys.MEDIA_STOP;
            case NativeKeyEvent.VC_MEDIA_PREVIOUS:
                return Input.Keys.MEDIA_PREVIOUS;
            case NativeKeyEvent.VC_MEDIA_NEXT:
                return Input.Keys.MEDIA_NEXT;
            case NativeKeyEvent.VC_VOLUME_UP:
                return Input.Keys.VOLUME_UP;
            case NativeKeyEvent.VC_VOLUME_DOWN:
                return Input.Keys.VOLUME_DOWN;
            case NativeKeyEvent.VC_SLEEP:
            case NativeKeyEvent.VC_WAKE:
            case NativeKeyEvent.VC_MEDIA_SELECT:
            case NativeKeyEvent.VC_MEDIA_EJECT:
            case NativeKeyEvent.VC_VOLUME_MUTE:
            case NativeKeyEvent.VC_APP_MAIL:
            case NativeKeyEvent.VC_APP_CALCULATOR:
            case NativeKeyEvent.VC_APP_MUSIC:
            case NativeKeyEvent.VC_APP_PICTURES:
            case NativeKeyEvent.VC_BROWSER_SEARCH:
            case NativeKeyEvent.VC_BROWSER_HOME:
            case NativeKeyEvent.VC_BROWSER_BACK:
            case NativeKeyEvent.VC_BROWSER_FORWARD:
            case NativeKeyEvent.VC_BROWSER_STOP:
            case NativeKeyEvent.VC_BROWSER_REFRESH:
            case NativeKeyEvent.VC_BROWSER_FAVORITES:
            case NativeKeyEvent.VC_KATAKANA:
            case NativeKeyEvent.VC_UNDERSCORE:
            case NativeKeyEvent.VC_FURIGANA:
            case NativeKeyEvent.VC_KANJI:
            case NativeKeyEvent.VC_HIRAGANA:
            case NativeKeyEvent.VC_YEN:
            case NativeKeyEvent.VC_SUN_HELP:
            case NativeKeyEvent.VC_SUN_STOP:
            case NativeKeyEvent.VC_SUN_PROPS:
            case NativeKeyEvent.VC_SUN_FRONT:
            case NativeKeyEvent.VC_SUN_OPEN:
            case NativeKeyEvent.VC_SUN_FIND:
            case NativeKeyEvent.VC_SUN_AGAIN:
            case NativeKeyEvent.VC_SUN_UNDO:
            case NativeKeyEvent.VC_SUN_COPY:
            case NativeKeyEvent.VC_SUN_INSERT:
            case NativeKeyEvent.VC_SUN_CUT:
            case NativeKeyEvent.VC_UNDEFINED:
            default:
                return Input.Keys.UNKNOWN;
        }
    }

    public static int mapButton(int jnhButton) {
        switch (jnhButton) {
            case NativeMouseEvent.BUTTON1:
                return Input.Buttons.LEFT;
            case NativeMouseEvent.BUTTON2:
                return Input.Buttons.RIGHT;
            case NativeMouseEvent.BUTTON3:
                return Input.Buttons.MIDDLE;
            case NativeMouseEvent.BUTTON4:
                return Input.Buttons.BACK;
            case NativeMouseEvent.BUTTON5:
                return Input.Buttons.FORWARD;
            case NativeMouseEvent.NOBUTTON:
            default:
                return -1;
        }
    }

}
