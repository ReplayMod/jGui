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

import de.johni0702.minecraft.gui.GuiRenderer;
import de.johni0702.minecraft.gui.RenderInfo;
import de.johni0702.minecraft.gui.container.GuiContainer;
import de.johni0702.minecraft.gui.function.Clickable;
import de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import de.johni0702.minecraft.gui.utils.lwjgl.WritableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.WritablePoint;
import net.minecraft.util.Identifier;

//#if MC>=10904
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
//#endif

//#if MC>=10800
import static com.mojang.blaze3d.platform.GlStateManager.*;
//#endif
import static de.johni0702.minecraft.gui.versions.MCVer.*;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public abstract class AbstractGuiTexturedButton<T extends AbstractGuiTexturedButton<T>> extends AbstractGuiClickable<T> implements Clickable, IGuiTexturedButton<T> {
    private Identifier texture;

    //#if MC>=11903
    //$$ private SoundEvent sound = SoundEvents.UI_BUTTON_CLICK.value();
    //#elseif MC>=10904
    private SoundEvent sound = SoundEvents.UI_BUTTON_CLICK;
    //#endif

    private ReadableDimension textureSize = new ReadableDimension() {
        @Override
        public int getWidth() {
            return getMaxSize().getWidth();
        }

        @Override
        public int getHeight() {
            return getMaxSize().getHeight();
        }

        @Override
        public void getSize(WritableDimension dest) {
            getMaxSize().getSize(dest);
        }
    };

    private ReadableDimension textureTotalSize;

    private ReadablePoint textureNormal;

    private ReadablePoint textureHover;

    private ReadablePoint textureDisabled;

    public AbstractGuiTexturedButton() {
    }

    public AbstractGuiTexturedButton(GuiContainer container) {
        super(container);
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        super.draw(renderer, size, renderInfo);

        renderer.bindTexture(texture);

        ReadablePoint texture = textureNormal;
        if (!isEnabled()) {
            texture = textureDisabled;
        } else if (isMouseHovering(new Point(renderInfo.mouseX, renderInfo.mouseY))) {
            texture = textureHover;
        }

        if (texture == null) { // Button is disabled but we have no texture for that
            //#if MC>=11700
            //$$ // TODO anything reasonable we can do here? do we even care?
            //#else
            color4f(0.5f, 0.5f, 0.5f, 1);
            //#endif
            texture = textureNormal;
        }

        enableBlend();
        blendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        renderer.drawTexturedRect(0, 0, texture.getX(), texture.getY(), size.getWidth(), size.getHeight(),
                textureSize.getWidth(), textureSize.getHeight(),
                textureTotalSize.getWidth(), textureTotalSize.getHeight());
    }

    @Override
    public ReadableDimension calcMinSize() {
        return new Dimension(0, 0);
    }

    @Override
    public void onClick() {
        //#if MC>=10904
        AbstractGuiButton.playClickSound(getMinecraft(), sound);
        //#else
        //$$ AbstractGuiButton.playClickSound(getMinecraft());
        //#endif
        super.onClick();
    }

    @Override
    public T setTexture(Identifier resourceLocation, int size) {
        return setTexture(resourceLocation, size, size);
    }

    @Override
    public T setTexture(Identifier resourceLocation, int width, int height) {
        this.texture = resourceLocation;
        this.textureTotalSize = new Dimension(width, height);
        return getThis();
    }

    @Override
    public T setTextureSize(int size) {
        return setTextureSize(size, size);
    }

    @Override
    public T setTextureSize(int width, int height) {
        this.textureSize = new Dimension(width, height);
        return getThis();
    }

    @Override
    public T setTexturePosH(final int x, final int y) {
        return setTexturePosH(new Point(x, y));
    }

    @Override
    public T setTexturePosV(final int x, final int y) {
        return setTexturePosV(new Point(x, y));
    }

    @Override
    public T setTexturePosH(final ReadablePoint pos) {
        this.textureNormal = pos;
        this.textureHover = new ReadablePoint() {
            @Override
            public int getX() {
                return pos.getX() + textureSize.getWidth();
            }

            @Override
            public int getY() {
                return pos.getY();
            }

            @Override
            public void getLocation(WritablePoint dest) {
                dest.setLocation(getX(), getY());
            }
        };
        return getThis();
    }

    @Override
    public T setTexturePosV(final ReadablePoint pos) {
        this.textureNormal = pos;
        this.textureHover = new ReadablePoint() {
            @Override
            public int getX() {
                return pos.getX();
            }

            @Override
            public int getY() {
                return pos.getY() + textureSize.getHeight();
            }

            @Override
            public void getLocation(WritablePoint dest) {
                dest.setLocation(getX(), getY());
            }
        };
        return getThis();
    }

    @Override
    public T setTexturePos(int normalX, int normalY, int hoverX, int hoverY) {
        return setTexturePos(new Point(normalX, normalY), new Point(hoverX, hoverY));
    }

    @Override
    public T setTexturePos(ReadablePoint normal, ReadablePoint hover) {
        this.textureNormal = normal;
        this.textureHover = hover;
        return getThis();
    }

    @Override
    public T setTexturePos(int normalX, int normalY, int hoverX, int hoverY, int disabledX, int disabledY) {
        return setTexturePos(new Point(normalX, normalY), new Point(hoverX, hoverY), new Point(disabledX, disabledY));
    }

    @Override
    public T setTexturePos(ReadablePoint normal, ReadablePoint hover, ReadablePoint disabled) {
        this.textureDisabled = disabled;
        return setTexturePos(normal, hover);
    }

    //#if MC>=10904
    @Override
    public T setSound(SoundEvent sound) {
        this.sound = sound;
        return getThis();
    }

    public SoundEvent getSound() {
        return this.sound;
    }
    //#endif

    public Identifier getTexture() {
        return this.texture;
    }

    public ReadableDimension getTextureSize() {
        return this.textureSize;
    }

    public ReadableDimension getTextureTotalSize() {
        return this.textureTotalSize;
    }

    public ReadablePoint getTextureNormal() {
        return this.textureNormal;
    }

    public ReadablePoint getTextureHover() {
        return this.textureHover;
    }

    public ReadablePoint getTextureDisabled() {
        return this.textureDisabled;
    }
}
