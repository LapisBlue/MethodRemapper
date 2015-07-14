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
package blue.lapis.methodremapper.tweaker;

import blue.lapis.methodremapper.Remapper;
import blue.lapis.methodremapper.RemapperConfig;
import blue.lapis.methodremapper.provider.ClassProvider;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableTable;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class RemapTransformer implements IClassTransformer, ClassProvider {

    protected final Remapper remapper;

    public RemapTransformer() throws IOException {
        this(Launch.blackboard.get("remapper.config"));
    }

    protected RemapTransformer(Object config) throws IOException {
        ImmutableTable<String, String, String> mappings;

        if (config instanceof String) {
            mappings = RemapperConfig.loadMappings((String) config);
        } else if (config instanceof File) {
            mappings = RemapperConfig.loadMappings((File) config);
        } else if (config instanceof URL) {
            mappings = RemapperConfig.loadMappings((URL) config);
        } else {
            throw new UnsupportedOperationException(config.toString());
        }

        this.remapper = new Remapper(this, mappings);
    }

    @Override
    public ClassReader getClass(String name) throws IOException {
        byte[] bytes = Launch.classLoader.getClassBytes(name);
        if (bytes != null) {
            return new ClassReader(bytes);
        } else {
            return null;
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }

        try {
            return this.remapper.remap(new ClassReader(basicClass));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

}
