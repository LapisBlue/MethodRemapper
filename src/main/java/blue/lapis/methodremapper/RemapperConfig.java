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

import static com.google.common.base.Charsets.UTF_8;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableTable;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Represents a simple mappings loader using a simple text file.
 *
 * <p>The text file consists out of 3 parts for a mapping each separated by a single space on each line:
 * <ul>
 *     <li>The full qualified class name of the owning class in internal format, e.g. java/lang/Object</li>
 *     <li>The method name and <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.3">descriptor</a> of the source
 *     method.</li>
 *     <li>The new method name.</li>
 * </ul>
 * </p>
 * <p>Example: {@code java/lang/Object toString()Ljava/lang/Object; asString}</p>
 */
public final class RemapperConfig implements LineProcessor<ImmutableTable<String, String, String>> {

    /**
     * The recommended standard file name for mapping configurations.
     */
    public static final String STANDARD_FILE_NAME = "remap.txt";
    private static final Splitter LINE_SPLITTER = Splitter.on(' ').trimResults();

    private final ImmutableTable.Builder<String, String, String> builder = ImmutableTable.builder();

    private RemapperConfig() {
    }

    @Override
    public boolean processLine(String line) throws IOException {
        if (line.isEmpty() || line.charAt(0) == '#') {
            return true;
        }

        List<String> parts = LINE_SPLITTER.splitToList(line);
        if (parts.size() != 3) {
            Remapper.logger.warn("Invalid mapping: {}", line);
            return true;
        }

        this.builder.put(parts.get(0), parts.get(1), parts.get(2));
        return true;
    }

    @Override
    public ImmutableTable<String, String, String> getResult() {
        return this.builder.build();
    }

    /**
     * Loads the mappings from the specified {@link File} or resource. If a file using the specified name exists, it will be loaded from the file. If
     * not, it will be loaded as resource from the JAR.
     *
     * @param name The name of the file to load the mappings from
     * @return The loaded mappings
     * @throws IOException If the mappings couldn't be loaded
     */
    public static ImmutableTable<String, String, String> loadMappings(String name) throws IOException {
        File file = new File(name);
        if (file.exists()) {
            return loadMappings(file);
        }

        return loadMappings(Resources.getResource(name));
    }

    /**
     * Loads the mappings from the specified {@link File}.
     *
     * @param file The file to load the mappings from
     * @return The loaded mappings
     * @throws IOException If the mappings couldn't be loaded
     */
    public static ImmutableTable<String, String, String> loadMappings(File file) throws IOException {
        return Files.readLines(file, UTF_8, new RemapperConfig());
    }

    /**
     * Loads the mappings from the specified {@link URL} (resource).
     *
     * @param resource The resource to load the mappings from
     * @return The loaded mappings
     * @throws IOException If the mappings couldn't be loaded
     */
    public static ImmutableTable<String, String, String> loadMappings(URL resource) throws IOException {
        return Resources.readLines(resource, UTF_8, new RemapperConfig());
    }
    
}
