/*
 * This file is part of jGui API, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2025 johni0702 <https://github.com/johni0702>
 * Copyright (c) contributors
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
package de.johni0702.minecraft.gui.function;

//#if MC>=11400
import de.johni0702.minecraft.gui.versions.MCVer.Keyboard;
//#else
//$$ import org.lwjgl.input.Keyboard;
//#endif

public class KeyInput implements InputWithModifiers {
    public int key;
    public int scancode; // Note: unavailable on 1.12.2 and below
    public int modifiers;

    public KeyInput(int key, int scancode, int modifiers) {
        this.key = key;
        this.scancode = scancode;
        this.modifiers = modifiers;
    }

    //#if MC>=12109
    //$$ public KeyInput(net.minecraft.client.input.KeyInput mc) {
    //$$     this(mc.key(), mc.scancode(), mc.modifiers());
    //$$ }
    //$$ public net.minecraft.client.input.KeyInput toMC() {
    //$$     return new net.minecraft.client.input.KeyInput(key, scancode, modifiers);
    //$$ }
    //#endif

    //#if MC<11300
    //$$ public KeyInput(int key) {
    //$$     this(key, key, InputWithModifiers.currentModifiers());
    //$$ }
    //#endif

    @Override
    public int modifiers() {
        return modifiers;
    }

    public boolean isEscape() {
        return key == Keyboard.KEY_ESCAPE;
    }
}
