package com.lu.magic.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

public class IOUtil {

    private static int sBufferSize = 1024 * 8;

    public static String readToString(InputStream inputStream) {
        return readToString(inputStream, null);
    }

    public static String readToString(InputStream inputStream, Charset charset) {
        byte[] b = readToBytes(inputStream);
        if (b == null) {
            return "";
        }
        if (charset == null) {
            return new String(b);
        }
        return new String(b, charset);
    }

    public static String readToString(Reader reader) {
        char[] ch = readToChars(reader);
        return new String(ch);
    }

    public static byte[] readToBytes(InputStream iStream) {
        if (iStream == null) return null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] buff = new byte[sBufferSize];
            int len;
            while ((len = iStream.read(buff)) != -1) {
                baos.write(buff, 0, len);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeQuietly(baos);
        }
    }


    public static char[] readToChars(InputStream iStream) {
        return readToChars(iStream, null);
    }

    public static char[] readToChars(InputStream iStream, Charset charset) {
        if (iStream == null) {
            return null;
        }
        InputStreamReader reader;
        if (charset == null) {
            reader = new InputStreamReader(iStream);
        } else {
            reader = new InputStreamReader(iStream, charset);
        }
        char[] result = readToChars(reader);
        closeQuietly(reader);
        return result;
    }

    public static char[] readToChars(Reader reader) {
        if (reader == null) return null;
        CharArrayWriter chWriter = null;
        char[] result = null;

        try {
            chWriter = new CharArrayWriter();
            char[] buff = new char[sBufferSize];
            int len;
            while ((len = reader.read(buff)) != -1) {
                chWriter.write(buff, 0, len);
            }
            result = chWriter.toCharArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(chWriter);
        }
        return result;
    }

    public static void writeByByte(byte[] data, OutputStream output) {
        if (data == null) {
            return;
        }
        try {
            output.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeByString(String data, OutputStream output) {
        writeByString(data, output, null);
    }


    public static void writeByString(String data, OutputStream oStream, Charset charset) {
        if (data == null || oStream == null) {
            return;
        }
        try {
            byte[] b;
            if (charset == null) {
                b = data.getBytes();
            } else {
                b = data.getBytes(charset);
            }
            oStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeByString(String data, Writer writer) {
        if (data == null) {
            return;
        }
        try {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable == null) {
                continue;
            }
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}