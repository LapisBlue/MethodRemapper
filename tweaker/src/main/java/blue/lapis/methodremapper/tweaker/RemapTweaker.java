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

import blue.lapis.methodremapper.RemapperConfig;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.List;

public class RemapTweaker implements ITweaker {

    private static final String CONFIG = System.getProperty("remapper.config", RemapperConfig.STANDARD_FILE_NAME);
    private static final String TRANSFORMER = RemapTweaker.class.getPackage().getName() + ".RemapTransformer";

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader loader) {
        Launch.blackboard.put("remapper.config", CONFIG);
        loader.registerTransformer(TRANSFORMER);
    }

    @Override
    public String getLaunchTarget() {
        throw new UnsupportedOperationException(); // This is supposed to be used standalone
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }

}
