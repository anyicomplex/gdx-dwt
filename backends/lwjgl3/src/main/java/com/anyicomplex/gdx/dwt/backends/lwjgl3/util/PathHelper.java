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
 * Simple utility class that helps to build file storage path.
 * @see SystemPath
 */
public class PathHelper {

    private PathHelper(){}

    /**
     * System file separator, on Windows is '\' and on Unix-like is '/'.
     */
    public static final String SEPARATOR = System.getProperty("file.separator");

    /**
     * Build given paths to a single string, will convert all separators to {@link PathHelper#SEPARATOR} and remove spaces at start and end of each string.<br>
     * <b>Note: No guarantee of generated path valid if paths contains invalid string or illegal char.<br>
     * Note: Returned path ends without a file separator.</b>
     *
     * @see PathHelper#removeStartEndSpaces(String)
     * @see PathHelper#convertSeparator(String)
     *
     * @param paths paths to build
     * @return built single-string path
     */
    public static String build(String... paths) {
        if (paths == null) throw new NullPointerException("Unable to build path: \npaths cannot be null");
        StringBuilder builder = new StringBuilder();
        for (String path : paths) {
            if (path == null) continue;
            path = removeStartEndSpaces(path);
            if (path.equals("")) continue;
            path = convertSeparator(path);
            builder.append(path);
            if (!path.endsWith(SEPARATOR)) builder.append(SEPARATOR);
        }
        return removeSeparatorAtEnd(builder.toString());
    }

    /**
     * Build app data path with companyName, appType and appName. All of them can be null.<br>
     * <b>Note: Returned path ends without a file separator.</b>
     * 
     * @see SystemPath#userData()
     *
     * @param companyName company name
     * @param appType app type
     * @param appName app name
     * @return built app data path
     */
    public static String buildAppDataPath(String companyName, String appType, String appName) {
        return build(SystemPath.userData(), companyName, appType, appName);
    }

    /**
     * Build app data path with companyName and appName. All of them can be null.<br>
     * <b>Note: Returned path ends without a file separator.</b>
     *
     * @see PathHelper#buildAppCachePath(String, String, String) 
     *
     * @param companyName company name
     * @param appName app name
     * @return built app data path
     */
    public static String buildAppDataPath(String companyName, String appName) {
        return buildAppDataPath(companyName, null, appName);
    }

    /**
     * Build app data path with appName. It can be null.<br>
     * <b>Note: Returned path ends without a file separator.</b>
     *
     * @see PathHelper#buildAppCachePath(String, String, String)
     *
     * @param appName app name
     * @return built app data path
     */
    public static String buildAppDataPath(String appName) {
        return buildAppDataPath(null, null, appName);
    }

    /**
     * Build app config path with companyName, appType and appName. All of them can be null.<br>
     * <b>Note: Returned path ends without a file separator.</b>
     *
     * @see SystemPath#userConfig()
     *
     * @param companyName company name
     * @param appType app type
     * @param appName app name
     * @return built app config path
     */
    public static String buildAppConfigPath(String companyName, String appType, String appName) {
        return build(SystemPath.userConfig(), companyName, appType, appName);
    }

    /**
     * Build app config path with companyName and appName. All of them can be null.<br>
     * <b>Note: Returned path ends without a file separator.</b>
     *
     * @see SystemPath#userConfig()
     *
     * @param companyName company name
     * @param appName app name
     * @return built app config path
     */
    public static String buildAppConfigPath(String companyName, String appName) {
        return buildAppConfigPath(companyName, null, appName);
    }

    /**
     * Build app config path with appName. It can be null.<br>
     * <b>Note: Returned path ends without a file separator.</b>
     *
     * @see PathHelper#buildAppConfigPath(String, String, String)
     *
     * @param appName app name
     * @return built app config path
     */
    public static String buildAppConfigPath(String appName) {
        return buildAppConfigPath(null, null, appName);
    }

    /**
     * Build app cache path with companyName, appType and appName. All of them can be null.<br>
     * <b>Note: Returned path ends without a file separator.</b>
     *
     * @see PathHelper#buildAppConfigPath(String, String, String)
     *
     * @param companyName company name
     * @param appType app type
     * @param appName app name
     * @return built app cache path
     */
    public static String buildAppCachePath(String companyName, String appType, String appName) {
        if (SystemInfo.isWindows()) return build(SystemPath.userConfig(), companyName, appType, appName, "Cache");
        return build(SystemPath.userCache(), companyName, appType, appName);
    }

    /**
     * Build app cache path with companyName and appName. All of them can be null.<br>
     * <b>Note: Returned path ends without a file separator.</b>
     *
     * @see PathHelper#buildAppCachePath(String, String, String)
     *
     * @param companyName company name
     * @param appName app name
     * @return built app cache path
     */
    public static String buildAppCachePath(String companyName, String appName) {
        return buildAppCachePath(companyName, null, appName);
    }

    /**
     * Build app cache path with appName. It can be null.<br>
     * <b>Note: Returned path ends without a file separator.</b>
     *
     * @see PathHelper#buildAppCachePath(String, String, String)
     *
     * @param appName app name
     * @return built app cache path
     */
    public static String buildAppCachePath(String appName) {
        return buildAppCachePath(null, null, appName);
    }

    /**
     * If path ends with a {@link PathHelper#SEPARATOR}, the method will remove it.
     * @param path path to remove separator
     * @return path without end separator
     */
    public static String removeSeparatorAtEnd(String path) {
        if (path == null) throw new NullPointerException("Unable to remove separator: \npath cannot be null.");
        if (path.endsWith(SEPARATOR)) path = path.substring(0, path.length() - 1);
        return path;
    }

    /**
     * Gets whether path ends with {@link PathHelper#SEPARATOR}.
     * @param path path
     * @return whether path ends with separator
     */
    public static boolean endsWithSeparator(String path) {
        if (path == null) throw new NullPointerException("Unable to check whether ends with separator: \npath cannot be null.");
        return path.endsWith(SEPARATOR);
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
     * Convert all separators in given path to {@link PathHelper#SEPARATOR}.
     * @param path path
     * @return path that separators converted
     */
    public static String convertSeparator(String path) {
        if (path == null) throw new NullPointerException("Unable to convert separator: \npath cannot be null.");
        if (SEPARATOR.equals("/")) return path.replace("\\", SEPARATOR);
        if (SEPARATOR.equals("\\")) return path.replace("/", SEPARATOR);
        return path;
    }

}
