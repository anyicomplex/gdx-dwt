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
 * Simple utility class that provides operating system info.
 */
public class SystemInfo {

    private SystemInfo(){}

    /**
     * Operating system name
     */
    public static final String NAME = System.getProperty("os.name");
    /**
     * Operating system version
     */
    public static final String VERSION = System.getProperty("os.version");
    /**
     * Operating system arch
     */
    public static final String ARCH = System.getProperty("os.arch");

    /**
     * Operating system types
     */
    public enum SystemType {
        WINDOWS,
        MAC,
        LINUX,
        SOLARIS,
        AIX,
        OTHER_UNIX,
        UNKNOWN
    }

    private static final SystemType type;
    
    /**
     * Gets operating system type.
     * @return system type
     */
    public static SystemType getSystemType() {
        return type;
    }

    static {
        String osName = NAME.toLowerCase();
        if (osName.contains("win")) type = SystemType.WINDOWS;
        else if (osName.contains("nux")) type = SystemType.LINUX;
        else if (osName.contains("mac")) type = SystemType.MAC;
        else if (osName.contains("sunos")) type = SystemType.SOLARIS;
        else if (osName.contains("aix")) type = SystemType.AIX;
        else if (osName.contains("nix")) type = SystemType.OTHER_UNIX;
        else type = SystemType.UNKNOWN;
    }

    /**
     * Validates whether system is Windows.
     * @return whether system is Windows
     */
    public static boolean isWindows() {
        return type == SystemType.WINDOWS;
    }

    /**
     * Validates whether system is Windows 2000.
     * @return whether system is Windows 2000
     */
    public static boolean isWindows2000() {
        return isWindows() && VERSION.equals("5.0");
    }

    /**
     * Validates whether system is Windows XP or Windows Server 2003/2003 R2.
     * @return whether system is Windows XP or Windows Server 2003/2003 R2
     */
    public static boolean isWindowsXP() {
        return isWindows() && (VERSION.equals("5.1") || VERSION.equals("5.2"));
    }

    /**
     * Validates whether system is Windows Vista or Windows Server 2008.
     * @return whether system is Windows Vista or Windows Server 2008
     */
    public static boolean isWindowsVista() {
        return isWindows() && VERSION.equals("6.0");
    }

    /**
     * Validates whether system is Windows 7 or Windows Server 2008 R2.
     * @return whether system is Windows 7 or Windows Server 2008 R2
     */
    public static boolean isWindows7() {
        return isWindows() && VERSION.equals("6.1");
    }

    /**
     * Validates whether system is Windows 8 or Windows Server 2012.
     * @return whether system is Windows 8 or Windows Server 2012
     */
    public static boolean isWindows8() {
        return isWindows() && VERSION.equals("6.2");
    }

    /**
     * Validates whether system is Windows 8.1 or Windows Server 2012 R2.
     * @return whether system is Windows 8.1 or Windows Server 2012 R2
     */
    public static boolean isWindows8_1() {
        return isWindows() && VERSION.equals("6.3");
    }

    /**
     * Validates whether system is Windows 10 or Windows Server 2016/2019.
     * @return whether system is Windows 10 or Windows Server 2016/2019
     */
    public static boolean isWindows10() {
        return isWindows() && VERSION.equals("10.0") && !NAME.contains("11") && !NAME.contains("2022");
    }

    /**
     * Validates whether system is Windows 11 or Windows Server 2022.
     * @return whether system is Windows 11 or Windows Server 2022
     */
    public static boolean isWindows11() {
        return isWindows() && VERSION.equals("10.0") && (NAME.contains("11") || NAME.contains("2022"));
    }

    /**
     * Validates whether system is Linux.
     * @return whether system is Linux
     */
    public static boolean isLinux() {
        return type == SystemType.LINUX;
    }

    /**
     * Validates whether system is Mac.
     * @return whether system is Mac
     */
    public static boolean isMac() {
        return type == SystemType.MAC;
    }

    /**
     * Validates whether system is Solaris.
     * @return whether system is Solaris
     */
    public static boolean isSolaris() {
        return type == SystemType.SOLARIS;
    }

    /**
     * Validates whether system is Aix.
     * @return whether system is Aix
     */
    public static boolean isAix() {
        return type == SystemType.AIX;
    }

    /**
     * Validates whether system is Unix.<br>
     * Note: Mac is Unix, Linux Is Not UniX.
     * @return whether system is Unix
     */
    public static boolean isUnix() {
        return isMac() || isSolaris() || isAix() || type == SystemType.OTHER_UNIX;
    }

    /**
     * Validates whether system is Unix-like.
     * @return whether system is Unix-like
     */
    public static boolean isUnixLike() {
        return isUnix() || isLinux();
    }

    /**
     * Validates whether system is 64-bit.
     * @return whether system is 64-bit
     */
    public static boolean is64Bit() {
        return ARCH.contains("64");
    }

}
