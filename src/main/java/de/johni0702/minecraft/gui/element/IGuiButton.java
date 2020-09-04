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

import de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import net.minecraft.util.Identifier;

//#if MC>=10904
import net.minecraft.sound.SoundEvent;
//#endif

public interface IGuiButton<T extends IGuiButton<T>> extends IGuiClickable<T> {
    T setLabel(String label);

    T setI18nLabel(String label, Object... args);

    //#if MC>=10904
    T setSound(SoundEvent sound);
    //#endif

    String getLabel();

    Identifier getTexture();
    T setTexture(Identifier identifier);

    ReadableDimension getTextureSize();
    T setTextureSize(ReadableDimension size);
    default T setTextureSize(int width, int height) {
        return setTextureSize(new Dimension(width, height));
    }
    default T setTextureSize(int size) {
        return setTextureSize(size, size);
    }

    default T setTexture(Identifier identifier, int width, int height) {
        return setTexture(identifier).setTextureSize(width, height);
    }
    default T setTexture(Identifier resourceLocation, int size) {
        return setTexture(resourceLocation, size, size);
    }

    T setSpriteUV(ReadablePoint uv);
    ReadablePoint getSpriteUV();
    default T setSpriteUV(int u, int v) {
        return setSpriteUV(new Point(u, v));
    }

    T setSpriteSize(ReadableDimension size);
    ReadableDimension getSpriteSize();
    default T setSpriteSize(int width, int height) {
        return setSpriteSize(new Dimension(width, height));
    }

    default T setSprite(int u, int v, int width, int height) {
        return setSpriteUV(u, v).setSpriteSize(width, height);
    }
}
