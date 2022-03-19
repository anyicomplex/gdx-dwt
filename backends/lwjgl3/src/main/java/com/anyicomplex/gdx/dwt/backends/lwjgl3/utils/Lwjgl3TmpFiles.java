package com.anyicomplex.gdx.dwt.backends.lwjgl3.utils;

import com.anyicomplex.gdx.dwt.utils.ChecksumUtils;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import static com.badlogic.gdx.utils.SharedLibraryLoader.is64Bit;

public class Lwjgl3TmpFiles {

    private Lwjgl3TmpFiles(){}

    private static boolean isEmpty(String string) {
        return string == null || string.length() < 1;
    }

    public static final String TMP_STORAGE_PATH_KEY = "com.anyicomplex.gdx.dwt.tmpStoragePath";
    public static final String TMP_STORAGE_PATH_TAG = "Gdwt-TmpStorage";

    public static String getTmpStorageDirPathProperty(String type) {
        return System.getProperty(isEmpty(type) ? TMP_STORAGE_PATH_KEY : TMP_STORAGE_PATH_KEY + "." + type);
    }

    public static void setTmpStorageDirPathProperty(String type, String path) {
        System.setProperty(isEmpty(type) ? TMP_STORAGE_PATH_KEY : TMP_STORAGE_PATH_KEY + "." + type, path);
    }

    public static String getTmpStorageDirPathProperty() {
        return getTmpStorageDirPathProperty(null);
    }

    public static void setTmpStorageDirPathProperty(String path) {
        setTmpStorageDirPathProperty(null, path);
    }

    public static String getTmpLibDirPathProperty() {
        return getTmpStorageDirPathProperty(TMP_FILE_TYPE_LIB);
    }

    public static void setTmpLibDirPathProperty(String path) {
        setTmpStorageDirPathProperty(TMP_FILE_TYPE_LIB, path);
    }

    public static String getTmpBinDirPathProperty() {
        return getTmpStorageDirPathProperty(TMP_FILE_TYPE_BIN);
    }

    public static void setTmpBinDirPathProperty(String path) {
        setTmpStorageDirPathProperty(TMP_FILE_TYPE_BIN, path);
    }

    public static String getTmpImageDirPathProperty() {
        return getTmpStorageDirPathProperty(TMP_FILE_TYPE_IMAGE);
    }

    public static void setTmpImageDirPathProperty(String path) {
        setTmpStorageDirPathProperty(TMP_FILE_TYPE_IMAGE, path);
    }

    public static FileHandle getTmpStorageDir(String type) {
        FileHandle result = null;
        File file;
        if (getTmpStorageDirPathProperty(type) == null) {
            try {
                file = getTmpFileWithTag(type, TMP_STORAGE_PATH_TAG);
                if (file == null) throw new IllegalStateException("Resource file extraction path is invalid!");
                result = new Lwjgl3FileHandle(file, Files.FileType.Absolute);
            }
            catch (RuntimeException e) {
                file = getTmpFileWithLibPath(TMP_STORAGE_PATH_TAG);
                if (file != null) {
                    result = new Lwjgl3FileHandle(file, Files.FileType.Absolute);
                    result = result.parent();
                    return result;
                }
                throw e;
            }
        }
        else {
            file = getTmpFileWithProperty(type, TMP_STORAGE_PATH_TAG);
            if (file != null) result = new Lwjgl3FileHandle(file, Files.FileType.Absolute);
        }
        if (result == null) {
            file = getTmpFileWithLibPath(TMP_STORAGE_PATH_TAG);
            if (file != null) result = new Lwjgl3FileHandle(file, Files.FileType.Absolute);
        }
        if (result != null) result = result.parent();
        return result;
    }

    public static FileHandle getTmpStorageDir() {
        return getTmpStorageDir(null);
    }

    public static FileHandle getTmpLibDir() {
        return getTmpStorageDir(TMP_FILE_TYPE_LIB);
    }

    public static FileHandle getTmpBinDir() {
        return getTmpStorageDir(TMP_FILE_TYPE_BIN);
    }

    public static FileHandle getTmpImageDir() {
        return getTmpStorageDir(TMP_FILE_TYPE_IMAGE);
    }

    public static final String TMP_FILE_TYPE_LIB = is64Bit ? "lib64" : "lib";
    public static final String TMP_FILE_TYPE_BIN = is64Bit ? "bin64" : "bin";
    public static final String TMP_FILE_TYPE_IMAGE = "images";

    public static String randomUUID() {
        return new UUID(MathUtils.random.nextLong(), MathUtils.random.nextLong()).toString();
    }

    public static FileHandle getTmpFile(String type, FileHandle inputFile, String outputPath, String outputName, boolean deleteOnExit) {
        if (inputFile == null || !inputFile.exists() || inputFile.isDirectory()) throw new IllegalArgumentException("inputFile is not available!");
        FileHandle result = null;
        File file;
        if (getTmpStorageDirPathProperty(type) == null) {
            try {
                String sha512 = ChecksumUtils.sha512(inputFile.read());
                file = getTmpFileWithTag(type, Lwjgl3FilePaths.build(outputPath,
                        isEmpty(outputName) ? sha512 + "." + inputFile.extension() : outputName));
                if (file == null) throw new IllegalStateException("Resource file extraction path is invalid!");
                result = new Lwjgl3FileHandle(file, Files.FileType.Absolute);
                if (!result.exists()) result.write(inputFile.read(), false);
                if (!ChecksumUtils.sha512(result.read()).equalsIgnoreCase(sha512)) result.write(inputFile.read(), false);
            }
            catch (RuntimeException e) {
                file = getTmpFileWithLibPath(inputFile.name());
                if (file != null) {
                    result = new Lwjgl3FileHandle(file, Files.FileType.Absolute);
                    if (deleteOnExit) result.file().deleteOnExit();
                    return result;
                }
                throw e;
            }
        }
        else {
            file = getTmpFileWithProperty(type, inputFile.name());
            if (file != null) result = new Lwjgl3FileHandle(file, Files.FileType.Absolute);
        }
        if (result == null) {
            file = getTmpFileWithLibPath(inputFile.name());
            if (file != null) result = new Lwjgl3FileHandle(file, Files.FileType.Absolute);
        }
        if (result != null) if (deleteOnExit) result.file().deleteOnExit();
        return result;
    }

    public static FileHandle getTmpFile(String type, FileHandle inputFile, String outputPath) {
        return getTmpFile(type, inputFile, outputPath, null, true);
    }

    public static FileHandle getTmpFile(String type, FileHandle inputFile, String outputName, boolean deleteOnExit) {
        return getTmpFile(type, inputFile, null, outputName, deleteOnExit);
    }

    public static FileHandle getTmpFile(String type, FileHandle inputFile) {
        return getTmpFile(type, inputFile, null, true);
    }

    public static FileHandle getTmpFile(FileHandle inputFile, String outputPath, String outputName, boolean deleteOnExit) {
        return getTmpFile(null, inputFile, outputPath, outputName, deleteOnExit);
    }

    public static FileHandle getTmpFile(FileHandle inputFile, String outputPath) {
        return getTmpFile(null, inputFile, outputPath);
    }

    public static FileHandle getTmpFile(FileHandle inputFile, String outputName, boolean deleteOnExit) {
        return getTmpFile(null, inputFile, outputName, deleteOnExit);
    }

    public static FileHandle getTmpFile(FileHandle inputFile) {
        return getTmpFile(null, inputFile);
    }

    public static FileHandle getTmpLib(FileHandle inputFile, String outputPath, String outputName, boolean deleteOnExit) {
        return getTmpFile(TMP_FILE_TYPE_LIB, inputFile, outputPath, outputName, deleteOnExit);
    }

    public static FileHandle getTmpLib(FileHandle inputFile, String outputPath) {
        return getTmpFile(TMP_FILE_TYPE_LIB, inputFile, outputPath);
    }

    public static FileHandle getTmpLib(FileHandle inputFile, String outputName, boolean deleteOnExit) {
        return getTmpFile(TMP_FILE_TYPE_LIB, inputFile, outputName, deleteOnExit);
    }

    public static FileHandle getTmpLib(FileHandle inputFile) {
        return getTmpFile(TMP_FILE_TYPE_LIB, inputFile);
    }

    public static FileHandle getTmpBin(FileHandle inputFile, String outputPath, String outputName, boolean deleteOnExit) {
        return getTmpFile(TMP_FILE_TYPE_BIN, inputFile, outputPath, outputName, deleteOnExit);
    }

    public static FileHandle getTmpBin(FileHandle inputFile, String outputPath) {
        return getTmpFile(TMP_FILE_TYPE_BIN, inputFile, outputPath);
    }

    public static FileHandle getTmpBin(FileHandle inputFile, String outputName, boolean deleteOnExit) {
        return getTmpFile(TMP_FILE_TYPE_BIN, inputFile, outputName, deleteOnExit);
    }

    public static FileHandle getTmpBin(FileHandle inputFile) {
        return getTmpFile(TMP_FILE_TYPE_BIN, inputFile);
    }

    public static FileHandle getTmpImage(FileHandle inputFile, String outputPath, String outputName, boolean deleteOnExit) {
        return getTmpFile(TMP_FILE_TYPE_IMAGE, inputFile, outputPath, outputName, deleteOnExit);
    }

    public static FileHandle getTmpImage(FileHandle inputFile, String outputPath) {
        return getTmpFile(TMP_FILE_TYPE_IMAGE, inputFile, outputPath);
    }

    public static FileHandle getTmpImage(FileHandle inputFile, String outputName, boolean deleteOnExit) {
        return getTmpFile(TMP_FILE_TYPE_IMAGE, inputFile, outputName, deleteOnExit);
    }

    public static FileHandle getTmpImage(FileHandle inputFile) {
        return getTmpFile(TMP_FILE_TYPE_IMAGE, inputFile);
    }

    private static File getTmpFileWithTag(String type, String name) {
        File result = new File(System.getProperty("java.io.tmpdir") + "/" +
                TMP_STORAGE_PATH_TAG + "/" + System.getProperty("user.name") + (isEmpty(type) ? "" : ("/" + type)), name);
        if (!valid(result)) result = new File(System.getProperty("user.home") + "/." +
                TMP_STORAGE_PATH_TAG + "/" + (isEmpty(type) ? "" : ("/" + type)), name);
        if (!valid(result)) result = new File(".tmp/" +
                TMP_STORAGE_PATH_TAG + "/" + (isEmpty(type) ? "" : ("/" + type)), name);
        return valid(result) ? result : null;
    }

    private static File getTmpFileWithProperty(String type, String name) {
        File result = new File(getTmpStorageDirPathProperty(type), name);
        return valid(result) ? result : null;
    }

    private static File getTmpFileWithLibPath(String name) {
        File result = new File(System.getProperty("java.library.path"), name);
        return valid(result) ? result : null;
    }

    private static boolean valid(File file) {
        if (file.exists()) {
            return canWrite(file) && canExecute(file);
        }
        else {
            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) return false;
            if (!parent.isDirectory()) return false;
            File testFile = new File(parent, randomUUID());
            try {
                if (!testFile.createNewFile()) return false;
                return canWrite(testFile) && canExecute(testFile);
            } catch (IOException ignored) {
                return false;
            }
            finally {
                testFile.delete();
            }
        }
    }

    private static boolean canWrite(File file) {
        if (file == null) return false;
        try {
            Method canWrite = File.class.getMethod("canWrite");
            if ((boolean) canWrite.invoke(file)) return true;
            Method setWritable = File.class.getMethod("setWritable", boolean.class, boolean.class);
            setWritable.invoke(file, true, false);
            return (boolean) canWrite.invoke(file);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ignored) {}
        return false;
    }

    private static boolean canExecute(File file) {
        if (file == null) return false;
        try {
            Method canExecute = File.class.getMethod("canExecute");
            if ((boolean) canExecute.invoke(file)) return true;
            Method setExecutable = File.class.getMethod("setExecutable", boolean.class, boolean.class);
            setExecutable.invoke(file, true, false);
            return (boolean) canExecute.invoke(file);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ignored) {}
        return false;
    }

}
