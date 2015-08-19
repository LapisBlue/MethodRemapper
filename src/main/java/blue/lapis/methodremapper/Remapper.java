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
package blue.lapis.methodremapper;

import static com.google.common.base.Preconditions.checkNotNull;

import blue.lapis.methodremapper.provider.ClassProvider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Represents the main remapper that will remap given classes using the provided
 * method mappings.
 */
public class Remapper {

    static final Logger logger = LoggerFactory.getLogger(Remapper.class);

    private final ClassProvider provider;
    private final Map<String, Map<String, String>> classes;

    /**
     * Creates a new {@link Remapper} instance using the specified provider and
     * mappings.
     *
     * @param provider The provider of the classes that will be scanned
     * @param mappings The method mappings to use
     */
    public Remapper(ClassProvider provider, ImmutableTable<String, String, String> mappings) {
        this.provider = checkNotNull(provider, "provider");
        this.classes = Maps.newHashMap(mappings.rowMap());
    }

    /**
     * Gets the {@link ClassProvider} of this {@link Remapper}.
     *
     * @return The class provider of this remapper
     */
    public ClassProvider getProvider() {
        return this.provider;
    }

    /**
     * Gets the mapping (new method name) for the specified method.
     *
     * @param owner The full qualified owning class of the method in internal
     *        format, e.g. java/lang/Object
     * @param method The method name
     * @return The mapping (new method name) of the specified method, or
     *         {@code null} if not found
     * @throws IOException If loading classes from the provider fails
     */
    public String getMapping(String owner, String method) throws IOException {
        Map<String, String> mappings = getMappings(owner, null);
        if (mappings != null) {
            return mappings.get(method);
        }

        return null;
    }

    private Map<String, String> getMappings(String name, ClassReader reader) throws IOException {
        if (this.classes.containsKey(name)) {
            return this.classes.get(name);
        }

        logger.trace("Creating mappings for {}", name);

        if (reader == null) {
            logger.trace("Loading class {}", name);
            reader = this.provider.getClass(name);
        }

        Map<String, String> mappings = null;
        if (reader != null) {
            Map<String, String> builder = findMappings(reader.getSuperName(), null);

            String[] interfaces = reader.getInterfaces();
            if (interfaces != null) {
                for (String iface : interfaces) {
                    builder = findMappings(iface, builder);
                }
            }

            if (builder != null) {
                mappings = ImmutableMap.copyOf(builder);
            }
        }

        this.classes.put(name, mappings);
        return mappings;
    }

    private Map<String, String> findMappings(String name, Map<String, String> builder) throws IOException {
        if (name != null) {
            Map<String, String> mappings = getMappings(name, null);
            if (mappings != null) {
                if (builder == null) {
                    builder = Maps.newHashMap();
                }

                builder.putAll(mappings);
            }
        }

        return builder;
    }

    /**
     * Loads and remaps the given class file using the mappings of this
     * {@link Remapper}. This will scan the class hierarchy of the given class
     * for matching mappings and finally remap the method names to their new
     * names. If one of the super classes was not scanned for matching mappings
     * yet it will be queried from the provided {@link ClassProvider}.
     *
     * @param name The class name to remap
     * @return The remapped class
     * @throws IOException If loading classes from the provider fails
     */
    public byte[] remap(String name) throws IOException {
        return remap(this.provider.getClass(name));
    }

    /**
     * Remaps the given class file using the mappings of this {@link Remapper}.
     * This will scan the class hierarchy of the given class for matching
     * mappings and finally remap the method names to their new names. If one of
     * the super classes was not scanned for matching mappings yet it will be
     * queried from the provided {@link ClassProvider}.
     *
     * @param bytes The class bytes to read from
     * @return The remapped class
     * @throws IOException If loading classes from the provider fails
     */
    public byte[] remap(byte[] bytes) throws IOException {
        return remap(new ClassReader(bytes));
    }

    /**
     * Remaps the given class file using the mappings of this {@link Remapper}.
     * This will scan the class hierarchy of the given class for matching
     * mappings and finally remap the method names to their new names. If one of
     * the super classes was not scanned for matching mappings yet it will be
     * queried from the provided {@link ClassProvider}.
     *
     * @param reader The class reader to read the class from
     * @return The remapped class
     * @throws IOException If loading classes from the provider fails
     */
    public byte[] remap(ClassReader reader) throws IOException {
        String name = reader.getClassName();

        // Make sure the mappings for this class are loaded
        Map<String, String> mappings = getMappings(name, reader);

        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor visitor;

        if (mappings != null) {
            logger.debug("Remapping {} with {}", name, mappings);
            visitor = new RemapClassVisitor(writer, this, mappings);
        } else {
            logger.trace("Remapping {}", name);
            visitor = new RemapInvokeClassVisitor(writer, this);
        }

        reader.accept(visitor, 0);
        return writer.toByteArray();
    }

}
