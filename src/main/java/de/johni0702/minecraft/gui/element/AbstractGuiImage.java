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

import com.google.common.base.Preconditions;
import de.johni0702.minecraft.gui.GuiRenderer;
import de.johni0702.minecraft.gui.RenderInfo;
import de.johni0702.minecraft.gui.container.GuiContainer;
import de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.versions.Image;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public abstract class AbstractGuiImage<T extends AbstractGuiImage<T>>
        extends AbstractGuiElement<T> implements IGuiImage<T> {
    private NativeImageBackedTexture texture;
    private Identifier resourceLocation;
    private int u, v;
    private int uWidth, vHeight;
    private int textureWidth, textureHeight;

    /**
     * Reference to the copied image to prevent it from being garbage collected
     * and subsequently releasing the OpenGL texture.
     */
    private AbstractGuiImage<T> copyOf;

    public AbstractGuiImage() {
    }

    public AbstractGuiImage(GuiContainer container) {
        super(container);
    }

    public AbstractGuiImage(AbstractGuiImage<T> copyOf) {
        this.texture = copyOf.texture;
        this.resourceLocation = copyOf.resourceLocation;
        this.u = copyOf.u;
        this.v = copyOf.v;
        this.uWidth = copyOf.uWidth;
        this.vHeight = copyOf.vHeight;
        this.textureWidth = copyOf.textureWidth;
        this.textureHeight = copyOf.textureHeight;
        this.copyOf = copyOf;
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        super.draw(renderer, size, renderInfo);
        if (texture != null) {
            //#if MC>=12105
            //$$ renderer.bindTexture(texture.getGlTexture());
            //#else
            renderer.bindTexture(texture.getGlId());
            //#endif
        } else {
            renderer.bindTexture(resourceLocation);
        }
        int w = size.getWidth();
        int h = size.getHeight();
        renderer.drawTexturedRect(0, 0, u, v, w, h, uWidth, vHeight, textureWidth, textureHeight);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (texture != null && copyOf == null) {
            //#if MC>=11400
            getMinecraft().execute(new Finalizer(texture));
            //#else
            //$$ getMinecraft().addScheduledTask(new Finalizer(texture));
            //#endif
        }
    }

    @Override
    public ReadableDimension calcMinSize() {
        return new Dimension(0, 0);
    }

    @Override
    public T setTexture(Image img) {
        Preconditions.checkState(copyOf == null, "Cannot change texture of copy.");
        resourceLocation = null;
        if (texture != null) {
            //#if MC>=12105
            //$$ texture.close();
            //#else
            texture.clearGlId();
            //#endif
        }
        texture = img.toTexture();
        textureWidth = uWidth = img.getWidth();
        textureHeight = vHeight = img.getHeight();
        return getThis();
    }

    @Override
    public T setTexture(Identifier resourceLocation) {
        Preconditions.checkState(copyOf == null, "Cannot change texture of copy.");
        if (texture != null) {
            //#if MC>=12105
            //$$ texture.close();
            //#else
            texture.clearGlId();
            //#endif
            texture = null;
        }
        this.resourceLocation = resourceLocation;
        textureWidth = textureHeight = 256;
        return getThis();
    }

    @Override
    public T setTexture(Identifier resourceLocation, int u, int v, int width, int height) {
        setTexture(resourceLocation);
        setUV(u, v);
        setUVSize(width, height);
        return getThis();
    }

    @Override
    public T setU(int u) {
        this.u = u;
        return getThis();
    }

    @Override
    public T setV(int v) {
        this.v = v;
        return getThis();
    }

    @Override
    public T setUV(int u, int v) {
        setU(u);
        return setV(v);
    }

    @Override
    public T setUWidth(int width) {
        this.uWidth = width;
        return getThis();
    }

    @Override
    public T setVHeight(int height) {
        this.vHeight = height;
        return getThis();
    }

    @Override
    public T setUVSize(int width, int height) {
        setUWidth(width);
        return setVHeight(height);
    }

    /**
     * We use a static class here in order to prevent the inner class from keeping the outer class
     * alive after finalization when still unloading the texture.
     */
    private static final class Finalizer implements Runnable {
        private final NativeImageBackedTexture texture;

        public Finalizer(NativeImageBackedTexture texture) {
            this.texture = texture;
        }

        @Override
        public void run() {
            //#if MC>=12105
            //$$ texture.close();
            //#else
            texture.clearGlId();
            //#endif
        }
    }
}
