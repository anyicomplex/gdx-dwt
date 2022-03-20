package com.anyicomplex.gdx.dwt.backends.lwjgl3.factory;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.utils.Lwjgl3FilePaths;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.utils.Lwjgl3TmpFiles;
import com.anyicomplex.gdx.dwt.utils.ChecksumUtils;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.badlogic.gdx.utils.SharedLibraryLoader.isLinux;
import static com.badlogic.gdx.utils.SharedLibraryLoader.isWindows;

public class Lwjgl3Tray {

    private Lwjgl3Tray(){}

    private static long handle;
    static volatile boolean initialized = false;

    private static ByteBuffer iconBuffer;
    private static PointerBuffer itemsBuffer;
    private static ByteBuffer tooltipBuffer;

    private static final Array<Lwjgl3TrayItem> items = new Array<>();

    private static volatile FileHandle icon;
    private static volatile FileHandle tmp;

    public static void setTooltip(String tooltip) {
        checkInitted();
        nsetTooltip(tooltip);
    }

    private static void nsetTooltip(String tooltip) {
        if (tooltip == null || tooltip.length() < 1) {
            nsetTooltip(handle, 0);
            return;
        }
        if (tooltipBuffer != null) MemoryUtil.memFree(tooltipBuffer);
        byte[] content;
        if (isWindows) content = tooltip.getBytes(StandardCharsets.UTF_16LE);
        else content = tooltip.getBytes(StandardCharsets.UTF_8);
        tooltipBuffer = MemoryUtil.memAlloc(content.length + 1);
        MemoryUtil.memSet(tooltipBuffer, 0);
        tooltipBuffer.put(content);
        tooltipBuffer.rewind();
        nsetTooltip(handle, MemoryUtil.memAddress(tooltipBuffer));
    }

    public static String getTooltip() {
        checkInitted();
        if (tooltipBuffer != null) {
            tooltipBuffer.rewind();
            byte[] tooltip = new byte[tooltipBuffer.remaining() - 1];
            tooltipBuffer.get(tooltip, 0, tooltip.length);
            tooltipBuffer.rewind();
            return new String(tooltip);
        }
        return null;
    }

    public static void setIcon(FileHandle icon) {
        checkInitted();
        nsetIcon(icon);
    }

    private static void nsetIcon(FileHandle icon) {
        if (icon == null || !icon.exists() || icon.isDirectory()) {
            throw new IllegalArgumentException("icon not available!");
        }
        if (tmp != null) tmp.delete();
        if (iconBuffer != null) MemoryUtil.memFree(iconBuffer);
        String filePath;
        if (isLinux) {
            boolean shouldCopy = false;
            switch (icon.type()) {
                case Internal:
                case Classpath:
                default:
                    shouldCopy = true;
                    break;
                case Local:
                case Absolute:
                case External:
                    if (!icon.extension().equalsIgnoreCase("png")) shouldCopy = true;
                    break;
            }
            if (shouldCopy) {
                tmp = Lwjgl3TmpFiles.getTmpImage(icon, ChecksumUtils.sha512(icon.read()) + "." + icon.extension() + ".png",
                        true);
                filePath = tmp.pathWithoutExtension();
            }
            else {
                tmp = null;
                if (icon.type() == Files.FileType.Local) filePath = new FileHandle(new File(Lwjgl3Files.localPath, icon.path())).pathWithoutExtension();
                else filePath = icon.pathWithoutExtension();
            }
        }
        else {
            switch (icon.type()) {
                case Internal:
                case Classpath:
                default:
                    tmp = Lwjgl3TmpFiles.getTmpImage(icon);
                    filePath = tmp.file().getAbsolutePath();
                    break;
                case Local:
                    filePath = new File(Lwjgl3Files.localPath, icon.path()).getAbsolutePath();
                    break;
                case Absolute:
                case External:
                    filePath = icon.file().getAbsolutePath();
                    break;
            }
        }
        filePath = Lwjgl3FilePaths.convertSeparatorsToNativeStyle(filePath);
        byte[] content;
        if (isWindows) content = filePath.getBytes(StandardCharsets.UTF_16LE);
        else content = filePath.getBytes(StandardCharsets.UTF_8);
        iconBuffer = MemoryUtil.memAlloc(content.length + 1);
        MemoryUtil.memSet(iconBuffer, 0);
        iconBuffer.put(content);
        iconBuffer.rewind();
        nsetIcon(handle, MemoryUtil.memAddress(iconBuffer));
        Lwjgl3Tray.icon = icon;
    }

    public static FileHandle getIcon() {
        checkInitted();
        return icon;
    }

    public static Array<Lwjgl3TrayItem> getItems() {
        return items;
    }

    public static Lwjgl3TrayItem addSeparator() {
        checkInitted();
        Lwjgl3TrayItem item = new Lwjgl3TrayItem(null, Lwjgl3TrayItem.Lwjgl3TrayItemType.Separator);
        items.add(item);
        return item;
    }

    public static Lwjgl3TrayItem addCheckBox(String text, boolean disabled, boolean checked, boolean radioCheck, Lwjgl3TrayItem.OnClickListener onClickListener) {
        checkInitted();
        Lwjgl3TrayItem item = new Lwjgl3TrayItem(text, Lwjgl3TrayItem.Lwjgl3TrayItemType.CheckBox);
        item.setDisabled(disabled);
        item.setChecked(checked);
        item.setRadioCheck(radioCheck);
        item.setOnClickListener(onClickListener);
        items.add(item);
        return item;
    }

    public static Lwjgl3TrayItem addCheckBox(String text, boolean disabled, boolean checked, Lwjgl3TrayItem.OnClickListener onClickListener) {
        checkInitted();
        return addCheckBox(text, disabled, checked, false, onClickListener);
    }

    public static Lwjgl3TrayItem addRadioButton(String text, boolean disabled, boolean checked, Lwjgl3TrayItem.OnClickListener onClickListener) {
        checkInitted();
        return addCheckBox(text, disabled, checked, true, onClickListener);
    }

    public static Lwjgl3TrayItem addTrayItem(FileHandle icon, String text, boolean disabled, boolean checked, boolean radioCheck,
                                             Lwjgl3TrayItem.Lwjgl3TrayItemType type, Lwjgl3TrayItem.OnClickListener onClickListener) {
        checkInitted();
        Lwjgl3TrayItem item = new Lwjgl3TrayItem(icon, text, disabled, checked, radioCheck, type, onClickListener);
        items.add(item);
        return item;
    }

    public static Lwjgl3TrayItem addTrayItem(String text, Lwjgl3TrayItem.Lwjgl3TrayItemType type, Lwjgl3TrayItem.OnClickListener onClickListener) {
        checkInitted();
        Lwjgl3TrayItem item = new Lwjgl3TrayItem(text, type, onClickListener);
        items.add(item);
        return item;
    }

    public static Lwjgl3TrayItem addLabel(String text, Lwjgl3TrayItem.OnClickListener onClickListener) {
        checkInitted();
        Lwjgl3TrayItem item = new Lwjgl3TrayItem(text, Lwjgl3TrayItem.Lwjgl3TrayItemType.Normal, onClickListener);
        items.add(item);
        return item;
    }

    public static Lwjgl3TrayItem addLabel(FileHandle icon, String text, Lwjgl3TrayItem.OnClickListener onClickListener) {
        checkInitted();
        Lwjgl3TrayItem item = new Lwjgl3TrayItem(text, Lwjgl3TrayItem.Lwjgl3TrayItemType.Normal, onClickListener);
        item.setIcon(icon);
        items.add(item);
        return item;
    }

    public static Lwjgl3TrayItem addSubmenu(String text, Lwjgl3TrayItem[] items) {
        checkInitted();
        Lwjgl3TrayItem submenu = new Lwjgl3TrayItem(text, Lwjgl3TrayItem.Lwjgl3TrayItemType.Normal);
        submenu.getItems().addAll(items);
        Lwjgl3Tray.items.add(submenu);
        return submenu;
    }

    static boolean init(String tooltip, FileHandle icon) {
        if (initialized) throw new IllegalStateException("Cannot init twice!");
        handle = nalloc();
        if (handle == 0) throw new IllegalStateException("Failed to allocate Lwjgl3Tray.");
        nsetTooltip(tooltip);
        nsetIcon(icon);
        initialized = ninit(handle);
        return initialized;
    }

    private static void checkInitted() {
        if (!initialized) throw new IllegalStateException("The system tray is not initialized!");
    }

    static void unInit() {
        checkInitted();
        nexit();
        nfree(handle);
        initialized = false;
        if (iconBuffer != null) MemoryUtil.memFree(iconBuffer);
        if (itemsBuffer != null) MemoryUtil.memFree(itemsBuffer);
        if (tooltipBuffer != null) MemoryUtil.memFree(tooltipBuffer);
        for (Lwjgl3TrayItem item : items) {
            item.dispose();
        }
    }

    static boolean loop(boolean blocking) {
        if (!initialized) return false;
        return nloop(blocking);
    }

    public static void update() {
        checkInitted();
        setIcon(icon);
        if (itemsBuffer != null) MemoryUtil.memFree(itemsBuffer);
        if (items.size >= 1) {
            itemsBuffer = MemoryUtil.memAllocPointer(items.size + 1);
            MemoryUtil.memSet(itemsBuffer, 0);
            for (Lwjgl3TrayItem item : items) {
                item.update();
                itemsBuffer.put(item.handle);
            }
            itemsBuffer.rewind();
            nsetItems(handle, MemoryUtil.memAddress(itemsBuffer));
        }
        else {
            nsetItems(handle, 0);
        }
        nupdate(handle);
    }

    private static native long nalloc();
    private static native void nfree(long handle);
    private static native boolean ninit(long handle);
    private static native boolean nloop(boolean blocking);
    private static native void nupdate(long handle);
    private static native void nexit();
    private static native void nsetTooltip(long handle, long buffer);
    private static native void nsetIcon(long handle, long buffer);
    private static native long nsetItems(long handle, long buffer);

}
