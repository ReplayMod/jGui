/*
 * This file is part of jGui API, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 johni0702 <https://github.com/johni0702>
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
package de.johni0702.minecraft.gui.element;

public interface IGuiNumberField<T extends IGuiNumberField<T>> extends IGuiTextField<T> {
    byte getByte();
    short getShort();
    int getInteger();
    long getLong();
    float getFloat();
    double getDouble();

    T setValue(int value);
    T setValue(double value);

    T setMinValue(Double minValue);
    T setMaxValue(Double maxValue);

    T setMinValue(int minValue);
    T setMaxValue(int maxValue);

    T setValidateOnFocusChange(boolean validateOnFocusChange);

    /**
     * Sets the amount of digits allowed after the decimal point.
     * A value of {@code 0} is equal to integer precision.
     * Negative values are not allowed.
     * @param precision Number of digits allowed
     */
    T setPrecision(int precision);
}
