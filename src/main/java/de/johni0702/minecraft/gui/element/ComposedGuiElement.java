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

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ComposedGuiElement<T extends ComposedGuiElement<T>> extends GuiElement<T> {
    Collection<GuiElement> getChildren();

    default  <C, R> R forEach(Class<C> ofType, Function<C, R> function) {
        return forEach((elem, elemLayer) -> elem.forEach(elemLayer, ofType, function));
    }

    default <C, R> R forEach(BiFunction<ComposedGuiElement<?>, Integer, R> recurse) {
        int maxLayer = getMaxLayer();
        for (int i = maxLayer; i >= 0; i--) {
            R result = recurse.apply(this, i);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    default <C, R> R forEach(int layer, Class<C> ofType, Function<C, R> function) {
        return forEach(layer, ofType, (elem, elemLayer) -> elem.forEach(elemLayer, ofType, function), function);
    }

    <C, R> R forEach(int layer, Class<C> ofType, BiFunction<ComposedGuiElement<?>, Integer, R> recurse, Function<C, R> function);

    default  <C> void invokeAll(Class<C> ofType, Consumer<C> consumer) {
        forEach((elem, elemLayer) -> {
            elem.invokeAll(elemLayer, ofType, consumer);
            return null;
        });
    }

    default  <C> void invokeAll(int layer, Class<C> ofType, Consumer<C> consumer) {
        forEach(layer, ofType, (elem, elemLayer) -> {
            elem.invokeAll(elemLayer, ofType, consumer);
            return null;
        }, obj -> {
            consumer.accept(obj);
            return null;
        });
    }

    default <C> boolean invokeHandlers(Class<C> ofType, Function<C, Boolean> handle) {
        return forEach((elem, elemLayer) -> elem.invokeHandlers(elemLayer, ofType, handle) ? true : null) == Boolean.TRUE;
    }

    default <C> boolean invokeHandlers(int layer, Class<C> ofType, Function<C, Boolean> handle) {
        return forEach(
                layer,
                ofType,
                (elem, elemLayer) -> elem.invokeHandlers(elemLayer, ofType, handle) ? true : null,
                obj -> handle.apply(obj) ? true : null
        ) == Boolean.TRUE;
    }

    /**
     * Returns the highest layer this element or any of its children take part in.
     * Events will be called for this composed jgui element for all layers between
     * layer 0 (inclusive) and the returned maximum layer (inclusive).
     * @return Highest layer relevant to this element
     */
    int getMaxLayer();
}
