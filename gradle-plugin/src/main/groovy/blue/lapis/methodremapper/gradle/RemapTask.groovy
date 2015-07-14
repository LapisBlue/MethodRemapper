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
package blue.lapis.methodremapper.gradle

import blue.lapis.methodremapper.Remapper
import blue.lapis.methodremapper.RemapperConfig
import blue.lapis.methodremapper.provider.ZipClassProvider

import com.google.common.collect.ImmutableTable
import com.google.common.io.Files
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.AbstractArchiveTask

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class RemapTask extends DefaultTask {

    @InputFile
    File config

    private File inputJar
    AbstractArchiveTask inputTask

    File outputJar

    @TaskAction
    public void remap() throws IOException {
        ImmutableTable<String, String, String> mappings = RemapperConfig.loadMappings(this.config);

        def inputJar = getInputJar()
        def outputJar = getOutputJar()

        def tmp = outputJar
        if (inputJar == outputJar) {
            tmp = new File(temporaryDir, outputJar.name)
        }

        def zip = new ZipFile(inputJar)
        try {
            new ZipOutputStream(tmp.newOutputStream()).withStream { ZipOutputStream out ->
                def loader = new ZipClassProvider(zip)
                def remapper = new Remapper(loader, mappings)

                zip.entries().each {
                    ZipEntry entryOut = new ZipEntry(it)
                    if (!it.directory && it.name.endsWith(ZipClassProvider.CLASS_EXTENSION)) {
                        def bytes = remapper.remap(loader.getClassFile(it))
                        entryOut.size = bytes.length
                        entryOut.compressedSize = -1
                        out.putNextEntry(entryOut)
                        out.write(bytes)
                    } else {
                        out.putNextEntry(entryOut)
                        out << zip.getInputStream(it)
                    }
                }
            }
        } finally {
            zip.close()
        }

        if (tmp != outputJar) {
            Files.copy(tmp, outputJar)
        }
    }

    @InputFile
    File getInputJar() {
        if (inputTask != null) {
            return inputTask.archivePath
        }
        inputJar
    }

    private void clear() {
        this.inputJar = null;
        if (inputTask != null) {
            dependsOn.remove inputTask
            this.inputTask = null
        }
    }

    void setInputJar(File inputJar) {
        clear()
        this.inputJar = inputJar
    }

    void setInputTask(AbstractArchiveTask parent) {
        clear()
        dependsOn parent
        this.inputTask = parent
    }

    @OutputFile
    File getOutputJar() {
        outputJar ?: getInputJar()
    }

}
