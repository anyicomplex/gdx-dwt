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

package com.anyicomplex.gdx.dwt.backends.lwjgl3.utils;

import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.io.File;

import static com.badlogic.gdx.utils.SharedLibraryLoader.*;

/**
 * Simple utility class that helps to work with file paths.
 */
public class Lwjgl3FilePaths {

    private Lwjgl3FilePaths(){}

    /**
     * <p>Build given paths to a single string, will convert all separators to '/' and remove spaces at start and end of each string.</p>
     * <p><b>Note:</b> No guarantee of generated path valid if paths contains invalid string or illegal char.<br>
     * <b>Note:</b> Returned path ends without a file separator.</p>
     *
     * @see Lwjgl3FilePaths#removeStartEndSpaces(String)
     * @see Lwjgl3FilePaths#convertSeparatorsToUnixStyle(String)
     *
     * @param paths paths to build
     * @return built single-string path
     */
    public static String build(String... paths) {
        if (paths == null) throw new NullPointerException("Unable to build path: \npaths cannot be null.");
        StringBuilder builder = new StringBuilder();
        for (String path : paths) {
            if (path == null) continue;
            path = removeStartEndSpaces(path);
            if (path.equals("")) continue;
            path = convertSeparatorsToUnixStyle(path);
            builder.append(path);
            if (!path.endsWith("/")) builder.append("/");
        }
        return removeSeparatorAtEnd(builder.toString());
    }

    /**
     * <p>Build app data path with companyName, appType and appName. All of them can be null.</p>
     * <p><b>Note:</b> Returned path ends without a file separator.</p>
     * 
     * @see Lwjgl3FilePaths#userDataPath()
     *
     * @param companyName company name
     * @param appType app type
     * @param appName app name
     * @return built app data path
     */
    public static String buildAppDataPath(String companyName, String appType, String appName) {
        return build(userDataPath(), companyName, appType, appName);
    }

    /**
     * <p>Build app data path with companyName and appName. All of them can be null.</p>
     * <p><b>Note:</b> Returned path ends without a file separator.</p>
     *
     * @see Lwjgl3FilePaths#buildAppCachePath(String, String, String)
     *
     * @param companyName company name
     * @param appName app name
     * @return built app data path
     */
    public static String buildAppDataPath(String companyName, String appName) {
        return buildAppDataPath(companyName, null, appName);
    }

    /**
     * <p>Build app data path with appName. It can be null.</p>
     * <p><b>Note:</b> Returned path ends without a file separator.</p>
     *
     * @see Lwjgl3FilePaths#buildAppCachePath(String, String, String)
     *
     * @param appName app name
     * @return built app data path
     */
    public static String buildAppDataPath(String appName) {
        return buildAppDataPath(null, null, appName);
    }

    /**
     * <p>Build app config path with companyName, appType and appName. All of them can be null.</p>
     * <p><b>Note:</b> Returned path ends without a file separator.</p>
     *
     * @see Lwjgl3FilePaths#userConfigPath()
     *
     * @param companyName company name
     * @param appType app type
     * @param appName app name
     * @return built app config path
     */
    public static String buildAppConfigPath(String companyName, String appType, String appName) {
        return build(userConfigPath(), companyName, appType, appName);
    }

    /**
     * <p>Build app config path with companyName and appName. All of them can be null.</p>
     * <p><b>Note:</b> Returned path ends without a file separator.</p>
     *
     * @see Lwjgl3FilePaths#userConfigPath()
     *
     * @param companyName company name
     * @param appName app name
     * @return built app config path
     */
    public static String buildAppConfigPath(String companyName, String appName) {
        return buildAppConfigPath(companyName, null, appName);
    }

    /**
     * <p>Build app config path with appName. It can be null.</p>
     * <p><b>Note:</b> Returned path ends without a file separator.</p>
     *
     * @see Lwjgl3FilePaths#buildAppConfigPath(String, String, String)
     *
     * @param appName app name
     * @return built app config path
     */
    public static String buildAppConfigPath(String appName) {
        return buildAppConfigPath(null, null, appName);
    }

    /**
     * <p>Build app cache path with companyName, appType and appName. All of them can be null.</p>
     * <p><b>Note:</b> Returned path ends without a file separator.</p>
     *
     * @see Lwjgl3FilePaths#userCachePath()
     *
     * @param companyName company name
     * @param appType app type
     * @param appName app name
     * @return built app cache path
     */
    public static String buildAppCachePath(String companyName, String appType, String appName) {
        if (isWindows) return build(userCachePath(), companyName, appType, appName, "Cache");
        return build(userCachePath(), companyName, appType, appName);
    }

    /**
     * <p>Build app cache path with companyName and appName. All of them can be null.</p>
     * <p><b>Note:</b> Returned path ends without a file separator.</p>
     *
     * @see Lwjgl3FilePaths#buildAppCachePath(String, String, String)
     *
     * @param companyName company name
     * @param appName app name
     * @return built app cache path
     */
    public static String buildAppCachePath(String companyName, String appName) {
        return buildAppCachePath(companyName, null, appName);
    }

    /**
     * <p>Build app cache path with appName. It can be null.</p>
     * <p><b>Note:</b> Returned path ends without a file separator.</p>
     *
     * @see Lwjgl3FilePaths#buildAppCachePath(String, String, String)
     *
     * @param appName app name
     * @return built app cache path
     */
    public static String buildAppCachePath(String appName) {
        return buildAppCachePath(null, null, appName);
    }

    /**
     * If path ends with a {@link File#separator}, the method will remove it.
     * @param path path to remove separator
     * @return path without end separator
     */
    public static String removeSeparatorAtEnd(String path) {
        if (path == null) throw new NullPointerException("Unable to remove separator: \npath cannot be null.");
        if (path.endsWith(File.separator)) path = path.substring(0, path.length() - 1);
        return path;
    }

    /**
     * Gets whether path ends with {@link File#separator}.
     * @param path path
     * @return whether path ends with separator
     */
    public static boolean endsWithSeparator(String path) {
        if (path == null) throw new NullPointerException("Unable to check whether ends with separator: \npath cannot be null.");
        return path.endsWith(File.separator);
    }

    /**
     * Remove all spaces at path start and end.
     * @param path path
     * @return path without start and end spaces
     */
    public static String removeStartEndSpaces(String path) {
        if (path == null) throw new NullPointerException("Unable to remove space: \npath cannot be null.");
        while (path.startsWith(" ")) {
            path = path.substring(1);
        }
        while (path.endsWith(" ")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * Convert all separators in given path to '/'.
     * @param path path
     * @return path that separators converted
     */
    public static String convertSeparatorsToUnixStyle(String path) {
        if (path == null) throw new NullPointerException("Unable to convert separator: \npath cannot be null.");
        return path.replace('\\', '/');
    }

    /**
     * Convert all separators in given path to '\'.
     * @param path path
     * @return path that separators converted
     */
    public static String convertSeparatorsToWindowsStyle(String path) {
        if (path == null) throw new NullPointerException("Unable to convert separator: \npath cannot be null.");
        return path.replace('/', '\\');
    }

    /**
     * Convert all separators in given path to {@link File#separator}.
     * @param path path
     * @return path that separators converted
     */
    public static String convertSeparatorsToNativeStyle(String path) {
        if (path == null) throw new NullPointerException("Unable to convert separator: \npath cannot be null.");
        String separator = File.separator;
        if (separator.equals("/")) return path.replace("\\", separator);
        if (separator.equals("\\")) return path.replace("/", separator);
        return path;
    }

    /**
     * <p>Gets user home dir path.</p>
     * <p><b>Note:</b> Returned path ends without a file separator.</p>
     * @return user home dir path
     */
    public static String userHomePath() {
        String result = System.getProperty("user.home");
        if (result == null) throw new IllegalStateException("Unable to get user home path: \nUnknown error.");
        return removeSeparatorAtEnd(result);
    }

    /**
     * <p>Gets user space application data dir path.</p>
     * <p><b>Note:</b> Returned path ends without a file separator.</p>
     * @return user data dir path
     */
    public static String userDataPath() {
        String result;
        if (isWindows) {
            result = System.getenv("APPDATA");
            if (result == null) {
                float version = Float.parseFloat(System.getProperty("os.version"));
                if (version > 5.2) {
                    result = userHomePath() + "\\AppData\\Roaming";
                }
                else {
                    result = userHomePath() + "\\Application Data";
                }
            }
        }
        else if (isLinux) {
            result = System.getenv("XDG_DATA_HOME");
            if (result == null) result = userHomePath() + "/.local/share";
        }
        else if (isMac) {
            result = userHomePath() + "/Library/Application Support";
        }
        else throw new IllegalStateException("Unable to get user data path: \nUnsupported platform.");
        return result;
    }

    /**
     * <p>Gets user space application config dir path.</p>
     * <p><b>Note:</b> On Windows XP and older Windows, it will return the same path of {@link Lwjgl3FilePaths#userDataPath()}.<br>
     * <b>Note:</b> Returned path ends without a file separator.</p>
     * @return user config dir path
     */
    public static String userConfigPath() {
        String result;
        if (isWindows) {
            result = System.getenv("LOCALAPPDATA");
            if (result == null) result = System.getenv("APPDATA");
            if (result == null) {
                float version = Float.parseFloat(System.getProperty("os.version"));
                if (version > 5.2) {
                    result = userHomePath() + "\\AppData\\Local";
                }
                else {
                    result = userHomePath() + "\\Application Data";
                }
            }
            result = convertSeparatorsToUnixStyle(result);
        }
        else if (isLinux) {
            result = System.getenv("XDG_CONFIG_HOME");
            if (result == null) result = userHomePath() + "/.config";
        }
        else if (isMac) {
            result = userHomePath() + "/Library/Preferences";
        }
        else throw new IllegalStateException("Unable to get user config path: \nUnsupported platform.");
        return result;
    }

    /**
     * <p>Gets user space application cache dir path.</p>
     * <p><b>Note:</b> On Windows, it will return the same path of {@link Lwjgl3FilePaths#userConfigPath()}.<br>
     * <b>Note:</b> Returned path ends without a file separator.</p>
     * @return user cache dir path
     */
    public static String userCachePath() {
        String result;
        if (isWindows) {
            result = System.getenv("LOCALAPPDATA");
            if (result == null) result = System.getenv("APPDATA");
            if (result == null) {
                float version = Float.parseFloat(System.getProperty("os.version"));
                if (version > 5.2) {
                    result = userHomePath() + "\\AppData\\Local";
                }
                else {
                    result = userHomePath() + "\\Application Data";
                }
            }
            result = convertSeparatorsToUnixStyle(result);
        }
        else if (isLinux) {
            result = System.getenv("XDG_CACHE_HOME");
            if (result == null) result = userHomePath() + "/.cache";
        }
        else if (isMac) {
            result = userHomePath() + "/Library/Caches";
        }
        else throw new IllegalStateException("Unable to get user cache path: \nUnsupported platform.");
        return result;
    }

    /**
     * <p>Gets system temporary dir path.</p>
     * <p><b>Note:</b> Returned path ends without a file separator.</p>
     * @return system temporary dir path
     */
    public static String tmpDirPath() {
        String result = System.getProperty("java.io.tmpdir");
        if (result == null) throw new IllegalStateException("Unable to get temporary dir path: \nUnknown error.");
        return convertSeparatorsToUnixStyle(removeSeparatorAtEnd(result));
    }

}
