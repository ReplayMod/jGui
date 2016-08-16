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
package de.johni0702.minecraft.gui.element.advanced;

import de.johni0702.minecraft.gui.element.GuiElement;
import de.johni0702.minecraft.gui.element.IGuiClickable;
import de.johni0702.minecraft.gui.utils.Consumer;

import java.util.Map;
import java.util.function.Function;

public interface IGuiDropdownMenu<V, T extends IGuiDropdownMenu<V, T>> extends GuiElement<T> {
    T setValues(V... values);

    T setSelected(int selected);

    T setSelected(V value);

    V getSelectedValue();

    T setOpened(boolean opened);

    int getSelected();

    V[] getValues();

    boolean isOpened();

    T onSelection(Consumer<Integer> consumer);

    /**
     * Returns an unmodifiable map of values with their GUI elements.
     * The GUI elements may be modified.<br>
     * The returned map is only valid until {@link #setValues(Object[])} is
     * called, at which point new GUI elements are created.<br>
     * This may return null if {@link #setValues(Object[])} has not yet been called.
     * @return Unmodifiable, ordered map of entries
     */
    Map<V, IGuiClickable> getDropdownEntries();

    /**
     * Set the function used to convert the values to display strings.
     * If not set, {@code Object::toString} is used.
     * @param toString Function used to convert
     * @return {@code this}, for chaining
     */
    T setToString(Function<V, String> toString);
}
