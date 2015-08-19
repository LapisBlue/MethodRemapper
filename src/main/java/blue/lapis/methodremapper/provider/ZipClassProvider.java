/*
 * Copyright (c) 2015, Minecrell <https://github.com/Minecrell>
 * Copyright (c) 2015, Lapis <https://github.com/LapisBlue>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package blue.lapis.methodremapper.provider;

import static com.google.common.base.Preconditions.checkNotNull;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Represents a standard {@link ClassProvider} backed by a simple Java ZIP or JAR file.
 */
public class ZipClassProvider implements ClassProvider {

    /**
     * The file extension used for Java classes.
     */
    public static final String CLASS_EXTENSION = ".class";

    private final ZipFile zip;

    /**
     * Creates a new {@link ZipClassProvider} for the specified {@link ZipFile}.
     *
     * @param zip The zip file to load the classes from
     */
    public ZipClassProvider(ZipFile zip) {
        this.zip = checkNotNull(zip, "zip");
    }

    @Override
    public ClassReader getClass(String name) throws IOException {
        return getClassFile(name + CLASS_EXTENSION);
    }

    /**
     * Loads a class from the specified class file path.
     *
     * @param file The class file path in the zip file
     * @return The loaded class
     * @throws IOException If the class couldn't be loaded
     */
    public ClassReader getClassFile(String file) throws IOException {
        return getClassFile(this.zip.getEntry(file));
    }

    /**
     * Loads a class from the specified {@link ZipEntry}.
     *
     * @param entry The zip entry to load the class from
     * @return The loaded class
     * @throws IOException If the class couldn't be loaded
     */
    public ClassReader getClassFile(ZipEntry entry) throws IOException {
        if (entry != null) {
            InputStream in = this.zip.getInputStream(entry);
            try {
                return new ClassReader(in);
            } finally {
                in.close();
            }
        } else {
            return null;
        }
    }

}
