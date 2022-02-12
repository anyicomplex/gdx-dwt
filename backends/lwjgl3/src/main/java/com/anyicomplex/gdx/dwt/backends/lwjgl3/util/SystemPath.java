/*
 * MIT License
 *
 * Copyright (c) 2021 Yi An
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.anyicomplex.gdx.dwt.backends.lwjgl3.util;

/**
 * Simple utility class that provides system pre-defined paths.
 */
public class SystemPath {

    private SystemPath(){}

    /**
     * Gets user home dir path.<br>
     * <b>Note: Returned path ends without a file separator.</b>
     * @return user home dir path
     */
    public static String userHome() {
        String result = System.getProperty("user.home");
        if (result == null) throw new IllegalStateException("Unable to get user home path: \nUnknown error.");
        return PathHelper.removeSeparatorAtEnd(result);
    }

    /**
     * Gets user space application data dir path.<br>
     * <b>Note: Returned path ends without a file separator.</b>
     * @return user data dir path
     */
    public static String userData() {
        String result;
        switch (SystemInfo.getSystemType()) {
            case WINDOWS:
                result = System.getenv("APPDATA");
                if (result == null) {
                    float version = Float.parseFloat(SystemInfo.VERSION);
                    if (version > 5.2) {
                        result = userHome() + "\\AppData\\Roaming";
                    }
                    else {
                        result = userHome() + "\\Application Data";
                    }
                }
                break;
            case LINUX:
            case SOLARIS:
            case AIX:
            case OTHER_UNIX:
                result = System.getenv("XDG_DATA_HOME");
                if (result == null) result = userHome() + "/.local/share";
                break;
            case MAC:
                result = userHome() + "/Library/Application Support";
                break;
            default:
                throw new IllegalStateException("Unable to get user data path: \nUnsupported platform.");
        }
        return result;
    }

    /**
     * Gets user space application config dir path.<br>
     * <b>Note: On Windows XP and older Windows, it will return the same path of {@link SystemPath#userData()}.<br>
     * Note: Returned path ends without a file separator.</b>
     * @return user config dir path
     */
    public static String userConfig() {
        String result;
        switch (SystemInfo.getSystemType()) {
            case WINDOWS:
                result = System.getenv("LOCALAPPDATA");
                if (result == null) result = System.getenv("APPDATA");
                if (result == null) {
                    float version = Float.parseFloat(SystemInfo.VERSION);
                    if (version > 5.2) {
                        result = userHome() + "\\AppData\\Local";
                    }
                    else {
                        result = userHome() + "\\Application Data";
                    }
                }
                break;
            case LINUX:
            case SOLARIS:
            case AIX:
            case OTHER_UNIX:
                result = System.getenv("XDG_CONFIG_HOME");
                if (result == null) result = userHome() + "/.config";
                break;
            case MAC:
                result = userHome() + "/Library/Preferences";
                break;
            default:
                throw new IllegalStateException("Unable to get user config path: \nUnsupported platform.");
        }
        return result;
    }

    /**
     * Gets user space application cache dir path.<br>
     * <b>Note: On Windows, it will return the same path of {@link SystemPath#userConfig()}.<br>
     * Note: Returned path ends without a file separator.</b>
     * @return user cache dir path
     */
    public static String userCache() {
        String result;
        switch (SystemInfo.getSystemType()) {
            case WINDOWS:
                result = System.getenv("LOCALAPPDATA");
                if (result == null) result = System.getenv("APPDATA");
                if (result == null) {
                    float version = Float.parseFloat(SystemInfo.VERSION);
                    if (version > 5.2) {
                        result = userHome() + "\\AppData\\Local";
                    }
                    else {
                        result = userHome() + "\\Application Data";
                    }
                }
                break;
            case LINUX:
            case SOLARIS:
            case AIX:
            case OTHER_UNIX:
                result = System.getenv("XDG_CACHE_HOME");
                if (result == null) result = userHome() + "/.cache";
                break;
            case MAC:
                result = userHome() + "/Library/Caches";
                break;
            default:
                throw new IllegalStateException("Unable to get user cache path: \nUnsupported platform.");
        }
        return result;
    }

    /**
     * Gets system temporary dir path.<br>
     * <b>Note: Returned path ends without a file separator.</b>
     * @return system temporary dir path
     */
    public static String temporary() {
        String result = System.getProperty("java.io.tmpdir");
        if (result == null) throw new IllegalStateException("Unable to get temporary dir path: \nUnknown error.");
        return PathHelper.removeSeparatorAtEnd(result);
    }

}
