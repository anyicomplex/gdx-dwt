package com.anyicomplex.gdx.dwt.backends.lwjgl3.factory;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.utils.FilePaths;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.utils.TmpFiles;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.badlogic.gdx.utils.SharedLibraryLoader.isWindows;

public class Lwjgl3TrayItem implements Disposable, Disableable {

    public interface OnClickListener {
        boolean onClick(Lwjgl3TrayItem item);
    }

    private volatile OnClickListener onClickListener;

    public void performClick() {
        if (onClickListener == null || onClickListener.onClick(this)) Lwjgl3Tray.update();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    private volatile boolean disposed = false;

    public boolean isDisposed() {
        return disposed;
    }

    private void checkDisposed() {
        if (disposed) throw new IllegalStateException("The object is disposed. Please create a new object for use.");
    }

    @Override
    public void setDisabled(boolean disabled) {
        checkDisposed();
        nsetDisabled(handle, disabled);
    }

    @Override
    public boolean isDisabled() {
        checkDisposed();
        return nisDisabled(handle);
    }

    public enum Lwjgl3TrayItemType {
        Normal,
        Separator,
        CheckBox
    }

    private static int type2Int(Lwjgl3TrayItemType type) {
        if (type == null) return 0;
        switch (type) {
            case Normal:
            default:
                return 0;
            case Separator:
                return 1;
            case CheckBox:
                return 2;
        }
    }

    private static Lwjgl3TrayItemType int2Type(int type) {
        switch (type) {
            case 0:
            default:
                return Lwjgl3TrayItemType.Normal;
            case 1:
                return Lwjgl3TrayItemType.Separator;
            case 2:
                return Lwjgl3TrayItemType.CheckBox;
        }
    }

    private ByteBuffer iconBuffer;
    private PointerBuffer itemsBuffer;
    private ByteBuffer textBuffer;

    private final Array<Lwjgl3TrayItem> items = new Array<>();

    public Array<Lwjgl3TrayItem> getItems() {
        return items;
    }

    private volatile FileHandle icon;
    private volatile FileHandle tmp;

    public void setIcon(FileHandle icon) {
        checkDisposed();
        if (icon == null || !icon.exists() || icon.isDirectory()) {
            nsetIcon(handle, 0);
            return;
        }
        if (tmp != null) tmp.delete();
        if (iconBuffer != null) MemoryUtil.memFree(iconBuffer);
        String filePath;
        switch (icon.type()) {
            case Internal:
            case Classpath:
            default:
                tmp = TmpFiles.getTmpImage(icon);
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
        filePath = FilePaths.convertSeparatorsToNativeStyle(filePath);
        byte[] content;
        if (isWindows) content = filePath.getBytes(StandardCharsets.UTF_16LE);
        else content = filePath.getBytes(StandardCharsets.UTF_8);
        iconBuffer = MemoryUtil.memAlloc(content.length + 1);
        MemoryUtil.memSet(iconBuffer, 0);
        iconBuffer.put(content);
        iconBuffer.rewind();
        nsetIcon(handle, MemoryUtil.memAddress(iconBuffer));
        this.icon = icon;
    }

    public FileHandle getIcon() {
        checkDisposed();
        return icon;
    }

    public void setText(String text) {
        checkDisposed();
        if (text == null || text.length() < 1) {
            nsetText(handle, 0);
            return;
        }
        if (textBuffer != null) MemoryUtil.memFree(textBuffer);
        byte[] content;
        if (isWindows) content = text.getBytes(StandardCharsets.UTF_16LE);
        else content = text.getBytes(StandardCharsets.UTF_8);
        textBuffer = MemoryUtil.memAlloc(content.length + 1);
        MemoryUtil.memSet(textBuffer, 0);
        textBuffer.put(content);
        textBuffer.rewind();
        nsetText(handle, MemoryUtil.memAddress(textBuffer));
    }

    public String getText() {
        checkDisposed();
        if (textBuffer != null) {
            textBuffer.rewind();
            byte[] title = new byte[textBuffer.remaining() - 1];
            textBuffer.get(title, 0, title.length);
            textBuffer.rewind();
            return new String(title);
        }
        return null;
    }

    public void setChecked(boolean checked) {
        checkDisposed();
        nsetChecked(handle, checked);
    }

    public boolean isChecked() {
        checkDisposed();
        return nisChecked(handle);
    }

    public void toggle() {
        setChecked(!isChecked());
    }

    public void setRadioCheck(boolean radioCheck) {
        checkDisposed();
        nsetRadioCheck(handle, radioCheck);
    }

    public boolean isRadioCheck() {
        checkDisposed();
        return nisRadioCheck(handle);
    }

    public void setType(Lwjgl3TrayItemType type) {
        checkDisposed();
        nsetType(handle, type2Int(type));
    }

    public Lwjgl3TrayItemType getType() {
        checkDisposed();
        return int2Type(ngetType(handle));
    }

    final long handle;

    public Lwjgl3TrayItem(FileHandle icon, String text, boolean disabled, boolean checked, boolean radioCheck,
                          Lwjgl3TrayItemType type, OnClickListener onClickListener) {
        this(text, type, onClickListener);
        setIcon(icon);
        setDisabled(disabled);
        setChecked(checked);
        setRadioCheck(radioCheck);
    }

    public Lwjgl3TrayItem(String text, Lwjgl3TrayItemType type) {
        if ((text == null || text.length() < 1) && type != Lwjgl3TrayItemType.Separator)
            throw new IllegalArgumentException("Text can only be empty if type is separator.");
        handle = nalloc();
        if (handle == 0) throw new IllegalStateException("Failed to allocate Lwjgl3TrayItem.");
        setText(text);
        setType(type);
        nsetCallback(handle);
    }

    public Lwjgl3TrayItem(String text, Lwjgl3TrayItemType type, OnClickListener onClickListener) {
        this(text, type);
        setOnClickListener(onClickListener);
    }

    @Override
    public void dispose() {
        disposed = true;
        nfree(handle);
        if (iconBuffer != null) MemoryUtil.memFree(iconBuffer);
        if (itemsBuffer != null) MemoryUtil.memFree(itemsBuffer);
        if (textBuffer != null) MemoryUtil.memFree(textBuffer);
    }

    void update() {
        checkDisposed();
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
    }

    private static native long nalloc();
    private static native void nfree(long handle);
    private static native void nsetIcon(long handle, long buffer);
    private static native void nsetText(long handle, long buffer);
    private static native void nsetDisabled(long handle, boolean disabled);
    private static native void nsetChecked(long handle, boolean checked);
    private static native void nsetRadioCheck(long handle, boolean checked);
    private static native void nsetType(long handle, int type);
    private static native long nsetItems(long handle, long buffer);
    private static native boolean nisDisabled(long handle);
    private static native boolean nisChecked(long handle);
    private static native boolean nisRadioCheck(long handle);
    private static native int ngetType(long handle);

    private native void nsetCallback(long handle);

}
