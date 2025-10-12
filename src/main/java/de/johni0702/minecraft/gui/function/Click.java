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

import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import de.johni0702.minecraft.gui.utils.lwjgl.WritablePoint;

public class Click implements InputWithModifiers, ReadablePoint {
    public int x;
    public int y;
    public int button;
    public int modifiers;

    public Click(int x, int y, int button, int modifiers) {
        this.x = x;
        this.y = y;
        this.button = button;
        this.modifiers = modifiers;
    }

    //#if MC>=12109
    //$$ public Click(double x, double y, int button, int modifiers) {
    //$$     this((int) Math.round(x), (int) Math.round(y), button, modifiers);
    //$$ }
    //#else
    public Click(double x, double y, int button) {
        this((int) Math.round(x), (int) Math.round(y), button, InputWithModifiers.currentModifiers());
    }
    //#endif

    //#if MC>=12109
    //$$ public Click(net.minecraft.client.gui.Click click) {
    //$$     this(click.x(), click.y(), click.button(), click.modifiers());
    //$$ }
    //$$ public net.minecraft.client.gui.Click toMC() {
    //$$     return new net.minecraft.client.gui.Click(x, y, new net.minecraft.client.input.MouseInput(button, modifiers));
    //$$ }
    //#endif

    @Override
    public int modifiers() {
        return modifiers;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void getLocation(WritablePoint writablePoint) {
        writablePoint.setLocation(x, y);
    }
}
