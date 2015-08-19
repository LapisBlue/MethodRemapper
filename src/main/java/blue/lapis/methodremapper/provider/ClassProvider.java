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

import blue.lapis.methodremapper.Remapper;
import org.objectweb.asm.ClassReader;

import java.io.IOException;

/**
 * Represents the provider that will load all needed class files for a {@link Remapper}.
 */
public interface ClassProvider {

    /**
     * Gets a class reader for the class with the specified name.
     *
     * @param name The name of the class to load in internal format, e.g. java/lang/Object
     * @return The class reader of the specified class
     * @throws IOException If the class couldn't be loaded
     */
    ClassReader getClass(String name) throws IOException;

}
