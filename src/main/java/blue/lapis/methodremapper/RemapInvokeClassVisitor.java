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
import static org.objectweb.asm.Opcodes.ASM5;

import com.google.common.base.Throwables;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;

/**
 * The basic {@link ClassVisitor} that will only remap method calls to the
 * remapped methods.
 */
public class RemapInvokeClassVisitor extends ClassVisitor {

    private final Remapper remapper;

    /**
     * Creates a new {@link RemapInvokeClassVisitor} using the specified
     * {@link ClassVisitor} and {@link Remapper}.
     *
     * @param cv The parent class visitor, may be {@code null}
     * @param remapper The remapper
     */
    public RemapInvokeClassVisitor(ClassVisitor cv, Remapper remapper) {
        super(ASM5, cv);
        this.remapper = checkNotNull(remapper, "remapper");
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodVisitor(ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                try {
                    String mapping = remapper.getMapping(owner, name + desc);
                    super.visitMethodInsn(opcode, owner, mapping != null ? mapping : name, desc, itf);
                } catch (IOException e) {
                    throw Throwables.propagate(e);
                }
            }

        };
    }

}
