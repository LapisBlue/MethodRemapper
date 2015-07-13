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
package blue.lapis.pore.remapper;

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

public final class RemapperConfig implements LineProcessor<ImmutableTable<String, String, String>> {

    public static final String DEFAULT_FILE_NAME = "remap.txt";
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

    public static ImmutableTable<String, String, String> loadMappings(String name) throws IOException {
        File file = new File(name);
        if (file.exists()) {
            return loadMappings(file);
        }

        return loadMappings(Resources.getResource(name));
    }

    public static ImmutableTable<String, String, String> loadMappings(File file) throws IOException {
        return Files.readLines(file, UTF_8, new RemapperConfig());
    }

    public static ImmutableTable<String, String, String> loadMappings(URL resource) throws IOException {
        return Resources.readLines(resource, UTF_8, new RemapperConfig());
    }
    
}
