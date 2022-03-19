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

package com.anyicomplex.gdx.dwt.utils;

import com.badlogic.gdx.utils.StreamUtils;
import com.badlogic.gdx.utils.compression.CRC;
import sun.misc.CRC16;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Simple utility class that wraps {@link MessageDigest}, {@link CRC16}, {@link CRC32}, {@link Adler32}, {@link CRC}
 * to help generate data hash.
 * @see MessageDigest
 * @see CRC16
 * @see CRC32
 * @see Adler32
 * @see CRC
 */
public final class ChecksumUtils {

    private ChecksumUtils(){}

    public enum HashType {
        MD2,
        MD5,
        SHA1,
        SHA224,
        SHA256,
        SHA384,
        SHA512,
        CRC16,
        CRC32,
        GDX_CRC32,
        Adler32
    }

    public static String hashType2String(HashType type) {
        switch (type) {
            case MD2:
                return "MD2";
            case MD5:
                return "MD5";
            case SHA1:
                return "SHA-1";
            case SHA224:
                return "SHA-224";
            case SHA256:
                return "SHA-256";
            case SHA384:
                return "SHA-384";
            case SHA512:
                return "SHA-512";
        }
        return null;
    }

    public static Checksum hashType2Checksum(HashType type) {
        switch (type) {
            case CRC32:
                return new CRC32();
            case Adler32:
                return new Adler32();
        }
        return null;
    }

    public static boolean isMessageDigest(HashType type) {
        switch (type) {
            case MD2:
            case MD5:
            case SHA1:
            case SHA224:
            case SHA256:
            case SHA384:
            case SHA512:
                return true;
        }
        return false;
    }

    public static boolean isMessageDigestSupported(String type) {
        if (type == null) return false;
        try {
            MessageDigest.getInstance(type);
            return true;
        } catch (NoSuchAlgorithmException ignored) {
            return false;
        }
    }

    public static boolean isMessageDigestSupported(HashType type) {
        return isMessageDigestSupported(hashType2String(type));
    }

    public static boolean isInternalChecksum(HashType type) {
        switch (type) {
            case CRC32:
            case Adler32:
                return true;
        }
        return false;
    }

    public static boolean isInternalChecksumSupported(HashType type) {
        switch (type) {
            case CRC32:
                try {
                    new CRC32();
                    return true;
                }
                catch (Exception ignored) {
                    return false;
                }
            case Adler32:
                try {
                    new Adler32();
                    return true;
                }
                catch (Exception ignored) {
                    return false;
                }
            default:
                return false;
        }
    }

    public static boolean isSupported(HashType type) {
        if (isMessageDigest(type)) return isMessageDigestSupported(type);
        if (isInternalChecksum(type)) return isInternalChecksumSupported(type);
        switch (type) {
            case CRC16:
                try {
                    new CRC16();
                    return true;
                }
                catch (Exception ignored) {
                    return false;
                }
            case GDX_CRC32:
                try {
                    new CRC();
                    return true;
                }
                catch (Exception ignored) {
                    return false;
                }
            default:
                return false;
        }
    }

    private static final int BUFFER_SIZE = 4096;

    /**
     * Generate hash of input data depends on type that {@link MessageDigest} supported.
     * 
     * @param type hash type
     * @param input the input data
     * @return hash
     */
    public static byte[] messageDigestHash(String type, byte[] input) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(type);
            return messageDigest.digest(input);
        }
        catch (NoSuchAlgorithmException ignored) {
            return null;
        }
    }

    /**
     * Generate hash of input data depends on type that {@link MessageDigest} supported.
     * @see ChecksumUtils#messageDigestHash(String, byte[])
     * 
     * @param type hash type
     * @param input the input data
     * @return hash
     */
    public static byte[] messageDigestHash(String type, String input, Charset charset) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        return messageDigestHash(type, input.getBytes(charset));
    }

    public static byte[] messageDigestHash(String type, String input, String charsetName) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        try {
            return messageDigestHash(type, input.getBytes(charsetName));
        }
        catch (UnsupportedEncodingException ignored) {
            return null;
        }
    }

    public static byte[] messageDigestHash(String type, String input) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        return messageDigestHash(type, input.getBytes());
    }

    public static byte[] messageDigestHash(String type, InputStream input) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(type);
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, length);
            }
            return messageDigest.digest();
        }
        catch (NoSuchAlgorithmException | IOException ignored) {
            return null;
        }
        finally {
            StreamUtils.closeQuietly(input);
        }
    }

    public static long checksumHash(Checksum checksum, byte[] input) {
        if (checksum == null) throw new NullPointerException("checksum cannot be null.");
        if (input == null) throw new NullPointerException("input cannot be null.");
        checksum.update(input, 0, input.length);
        return checksum.getValue();
    }

    public static long checksumHash(Checksum checksum, String input, Charset charset) {
        if (checksum == null) throw new NullPointerException("checksum cannot be null.");
        if (input == null) throw new NullPointerException("input cannot be null.");
        return checksumHash(checksum, input.getBytes(charset));
    }

    public static long checksumHash(Checksum checksum, String input, String charsetName) {
        if (checksum == null) throw new NullPointerException("checksum cannot be null.");
        if (input == null) throw new NullPointerException("input cannot be null.");
        try {
            return checksumHash(checksum, input.getBytes(charsetName));
        }
        catch (UnsupportedEncodingException ignored) {
            return 0L;
        }
    }

    public static long checksumHash(Checksum checksum, String input) {
        if (checksum == null) throw new NullPointerException("checksum cannot be null.");
        if (input == null) throw new NullPointerException("input cannot be null.");
        return checksumHash(checksum, input.getBytes());
    }

    public static long checksumHash(Checksum checksum, InputStream input) {
        if (checksum == null) throw new NullPointerException("checksum cannot be null.");
        if (input == null) throw new NullPointerException("input cannot be null.");
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) != -1) {
                checksum.update(buffer, 0, length);
            }
            return checksum.getValue();
        }
        catch (IOException ignored) {
            return 0L;
        }
        finally {
            StreamUtils.closeQuietly(input);
        }
    }

    public static String hash(HashType type, byte[] input) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        if (isMessageDigest(type)) {
            byte[] hash = messageDigestHash(hashType2String(type), input);
            return hash == null ? null : byteArray2HexString(hash);
        }
        if (isInternalChecksum(type)) {
            return Long.toString(checksumHash(hashType2Checksum(type), input), 16);
        }
        if (isSupported(type)) {
            switch (type) {
                case CRC16:
                    return Integer.toString(crc16Hash(input), 16);
                case GDX_CRC32:
                    return Integer.toUnsignedString(gdxcrc32Hash(input), 16);
            }
        }
        return null;
    }

    public static String hash(HashType type, String input, Charset charset) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        return hash(type, input.getBytes(charset));
    }

    public static String hash(HashType type, String input, String charsetName) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        try {
            return hash(type, input.getBytes(charsetName));
        }
        catch (UnsupportedEncodingException ignored) {
            return null;
        }
    }

    public static String hash(HashType type, String input) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        return hash(type, input.getBytes());
    }

    public static String hash(HashType type, InputStream input) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        if (isMessageDigest(type)) {
            byte[] hash = messageDigestHash(hashType2String(type), input);
            return hash == null ? null : byteArray2HexString(hash);
        }
        if (isInternalChecksum(type)) {
            return Long.toString(checksumHash(hashType2Checksum(type), input), 16);
        }
        if (isSupported(type)) {
            switch (type) {
                case CRC16:
                    return Integer.toString(crc16Hash(input), 16);
                case GDX_CRC32:
                    return Integer.toUnsignedString(gdxcrc32Hash(input), 16);
            }
        }
        return null;
    }

    /**
     * Generate md2 hash of input data.
     * 
     * @param input the input data
     * @return md2 hash
     */
    public static String md2(byte[] input) {
        return hash(HashType.MD2, input);
    }

    public static String md2(String input, Charset charset) {
        return hash(HashType.MD2, input, charset);
    }

    public static String md2(String input, String charsetName) {
        return hash(HashType.MD2, input, charsetName);
    }
    
    public static String md2(String input) {
        return hash(HashType.MD2, input.getBytes());
    }

    public static String md2(InputStream input) {
        return hash(HashType.MD2, input);
    }

    /**
     * Generate md5 hash of input data.
     *
     * @param input the input data
     * @return md5 hash
     */
    public static String md5(byte[] input) {
        return hash(HashType.MD5, input);
    }

    public static String md5(String input, Charset charset) {
        return hash(HashType.MD5, input, charset);
    }

    public static String md5(String input, String charsetName) {
        return hash(HashType.MD5, input, charsetName);
    }
    
    public static String md5(String input) {
        return hash(HashType.MD5, input.getBytes());
    }

    public static String md5(InputStream input) {
        return hash(HashType.MD5, input);
    }

    /**
     * Generate sha-1 hash of input data.
     *
     * @param input the input data
     * @return sha-1 hash
     */
    public static String sha1(byte[] input) {
        return hash(HashType.SHA1, input);
    }

    public static String sha1(String input, Charset charset) {
        return hash(HashType.SHA1, input, charset);
    }

    public static String sha1(String input, String charsetName) {
        return hash(HashType.SHA1, input, charsetName);
    }
    
    public static String sha1(String input) {
        return hash(HashType.SHA1, input.getBytes());
    }

    public static String sha1(InputStream input) {
        return hash(HashType.SHA1, input);
    }

    /**
     * Generate sha-224 hash of input data.
     *
     * @param input the input data
     * @return sha-224 hash
     */
    public static String sha224(byte[] input) {
        return hash(HashType.SHA224, input);
    }

    public static String sha224(String input, Charset charset) {
        return hash(HashType.SHA224, input, charset);
    }

    public static String sha224(String input, String charsetName) {
        return hash(HashType.SHA224, input, charsetName);
    }

    public static String sha224(String input) {
        return hash(HashType.SHA224, input.getBytes());
    }

    public static String sha224(InputStream input) {
        return hash(HashType.SHA224, input);
    }

    /**
     * Generate sha-256 hash of input data.
     *
     * @param input the input data
     * @return sha-256 hash
     */
    public static String sha256(byte[] input) {
        return hash(HashType.SHA256, input);
    }

    public static String sha256(String input, Charset charset) {
        return hash(HashType.SHA256, input, charset);
    }

    public static String sha256(String input, String charsetName) {
        return hash(HashType.SHA256, input, charsetName);
    }

    public static String sha256(String input) {
        return hash(HashType.SHA256, input.getBytes());
    }

    public static String sha256(InputStream input) {
        return hash(HashType.SHA256, input);
    }

    /**
     * Generate sha-384 hash of input data.
     *
     * @param input the input data
     * @return sha-384 hash
     */
    public static String sha384(byte[] input) {
        return hash(HashType.SHA384, input);
    }

    public static String sha384(String input, Charset charset) {
        return hash(HashType.SHA384, input, charset);
    }

    public static String sha384(String input, String charsetName) {
        return hash(HashType.SHA384, input, charsetName);
    }

    public static String sha384(String input) {
        return hash(HashType.SHA384, input.getBytes());
    }

    public static String sha384(InputStream input) {
        return hash(HashType.SHA384, input);
    }

    /**
     * Generate sha-512 hash of input data.
     *
     * @param input the input data
     * @return sha-512 hash
     */
    public static String sha512(byte[] input) {
        return hash(HashType.SHA512, input);
    }

    public static String sha512(String input, Charset charset) {
        return hash(HashType.SHA512, input, charset);
    }

    public static String sha512(String input, String charsetName) {
        return hash(HashType.SHA512, input, charsetName);
    }

    public static String sha512(String input) {
        return hash(HashType.SHA512, input.getBytes());
    }

    public static String sha512(InputStream input) {
        return hash(HashType.SHA512, input);
    }

    public static String crc32(byte[] input) {
        return hash(HashType.CRC32, input);
    }

    public static String crc32(String input, Charset charset) {
        return hash(HashType.CRC32, input, charset);
    }

    public static String crc32(String input, String charsetName) {
        return hash(HashType.CRC32, input, charsetName);
    }

    public static String crc32(String input) {
        return hash(HashType.CRC32, input.getBytes());
    }

    public static String crc32(InputStream input) {
        return hash(HashType.CRC32, input);
    }

    public static String adler32(byte[] input) {
        return hash(HashType.Adler32, input);
    }

    public static String adler32(String input, Charset charset) {
        return hash(HashType.Adler32, input, charset);
    }

    public static String adler32(String input, String charsetName) {
        return hash(HashType.Adler32, input, charsetName);
    }

    public static String adler32(String input) {
        return hash(HashType.Adler32, input.getBytes());
    }

    public static String adler32(InputStream input) {
        return hash(HashType.Adler32, input);
    }

    public static String crc16(byte[] input) {
        return hash(HashType.CRC16, input);
    }

    public static String crc16(String input, Charset charset) {
        return hash(HashType.CRC16, input, charset);
    }

    public static String crc16(String input, String charsetName) {
        return hash(HashType.CRC16, input, charsetName);
    }

    public static String crc16(String input) {
        return hash(HashType.CRC16, input.getBytes());
    }

    public static String crc16(InputStream input) {
        return hash(HashType.CRC16, input);
    }

    public static String gdxcrc32(byte[] input) {
        return hash(HashType.GDX_CRC32, input);
    }

    public static String gdxcrc32(String input, Charset charset) {
        return hash(HashType.GDX_CRC32, input, charset);
    }

    public static String gdxcrc32(String input, String charsetName) {
        return hash(HashType.GDX_CRC32, input, charsetName);
    }

    public static String gdxcrc32(String input) {
        return hash(HashType.GDX_CRC32, input.getBytes());
    }

    public static String gdxcrc32(InputStream input) {
        return hash(HashType.GDX_CRC32, input);
    }

    public static int gdxcrc32Hash(byte[] input) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        if (!isSupported(HashType.GDX_CRC32)) return -1;
        CRC crc = new CRC();
        crc.Update(input);
        return crc.GetDigest();
    }

    public static int gdxcrc32Hash(String input, Charset charset) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        return gdxcrc32Hash(input.getBytes(charset));
    }

    public static int gdxcrc32Hash(String input, String charsetName) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        try {
            return gdxcrc32Hash(input.getBytes(charsetName));
        }
        catch (UnsupportedEncodingException ignored) {
            return -1;
        }
    }

    public static int gdxcrc32Hash(String input) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        return gdxcrc32Hash(input.getBytes());
    }

    public static int gdxcrc32Hash(InputStream input) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        if (!isSupported(HashType.GDX_CRC32)) return -1;
        CRC crc = new CRC();
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) != -1) {
                crc.Update(buffer, 0, length);
            }
            return crc.GetDigest();
        }
        catch (IOException ignored) {
            return -1;
        }
        finally {
            StreamUtils.closeQuietly(input);
        }
    }

    public static int crc16Hash(byte[] input) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        if (!isSupported(HashType.CRC16)) return 0;
        CRC16 crc16 = new CRC16();
        for (byte b : input) {
            crc16.update(b);
        }
        return crc16.value;
    }

    public static int crc16Hash(String input, Charset charset) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        return crc16Hash(input.getBytes(charset));
    }

    public static int crc16Hash(String input, String charsetName) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        try {
            return crc16Hash(input.getBytes(charsetName));
        }
        catch (UnsupportedEncodingException ignored) {
            return 0;
        }
    }

    public static int crc16Hash(String input) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        return crc16Hash(input.getBytes());
    }

    public static int crc16Hash(InputStream input) {
        if (input == null) throw new NullPointerException("input cannot be null.");
        if (!isSupported(HashType.CRC16)) return 0;
        CRC16 crc16 = new CRC16();
        try {
            byte b;
            while ((b = (byte) input.read()) != -1) {
                crc16.update(b);
            }
            return crc16.value;
        }
        catch (IOException ignored) {
            return 0;
        }
        finally {
            StreamUtils.closeQuietly(input);
        }
    }

    private static String byteArray2HexString(byte[] input) {
        StringBuilder hex = new StringBuilder();
        for (byte b : input) {
            hex.append(String.format("%02X", b));
        }
        while (hex.length() < 32) hex.insert(0, '0');
        return hex.toString();
    }

}
