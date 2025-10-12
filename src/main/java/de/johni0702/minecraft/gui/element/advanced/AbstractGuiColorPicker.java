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

import de.johni0702.minecraft.gui.GuiRenderer;
import de.johni0702.minecraft.gui.OffsetGuiRenderer;
import de.johni0702.minecraft.gui.RenderInfo;
import de.johni0702.minecraft.gui.container.GuiContainer;
import de.johni0702.minecraft.gui.element.AbstractComposedGuiElement;
import de.johni0702.minecraft.gui.element.AbstractGuiElement;
import de.johni0702.minecraft.gui.element.GuiElement;
import de.johni0702.minecraft.gui.function.Click;
import de.johni0702.minecraft.gui.function.Clickable;
import de.johni0702.minecraft.gui.function.Draggable;
import de.johni0702.minecraft.gui.utils.Consumer;
import de.johni0702.minecraft.gui.utils.lwjgl.Color;
import de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableColor;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;

import java.util.Collection;
import java.util.Collections;

public abstract class AbstractGuiColorPicker<T extends AbstractGuiColorPicker<T>>
        extends AbstractComposedGuiElement<T> implements IGuiColorPicker<T>, Clickable {
    protected static final int PICKER_SIZE = 100;
    private static final ReadableColor OUTLINE_COLOR = new Color(255, 255, 255);

    private Color color = new Color();

    private boolean opened;

    private Consumer<ReadableColor> onSelection;

    private GuiPicker picker = new GuiPicker();

    public AbstractGuiColorPicker() {
    }

    public AbstractGuiColorPicker(GuiContainer container) {
        super(container);
    }

    @Override
    public int getMaxLayer() {
        return opened ? 1 : 0;
    }

    @Override
    protected ReadableDimension calcMinSize() {
        return new Dimension(3, 3);
    }

    @Override
    public void layout(ReadableDimension size, RenderInfo renderInfo) {
        super.layout(size, renderInfo);
        if (size == null) return;
        if (renderInfo.layer == 1) {
            ReadableDimension offsetSize = new Dimension(PICKER_SIZE, PICKER_SIZE);
            picker.layout(offsetSize, renderInfo);
        }
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        super.draw(renderer, size, renderInfo);
        if (renderInfo.layer == 0) {
            int width = size.getWidth();
            int height = size.getHeight();

            // Draw outline
            renderer.drawRect(0, 0, width, height, OUTLINE_COLOR);
            // Draw color
            renderer.drawRect(1, 1, width - 2, height - 2, color);
        } else if (renderInfo.layer == 1) {
            ReadablePoint offsetPoint = new Point(0, size.getHeight());
            ReadableDimension offsetSize = new Dimension(PICKER_SIZE, PICKER_SIZE);
            OffsetGuiRenderer offsetRenderer = new OffsetGuiRenderer(renderer, offsetPoint, offsetSize);
            offsetRenderer.startUsing();
            try {
                picker.draw(offsetRenderer, offsetSize, renderInfo);
            } finally {
                offsetRenderer.stopUsing();
            }
        }
    }

    protected void getColorAtPosition(int x, int y, Color color) {
        if (x < 0 || y < 0 || x >= PICKER_SIZE || y >= PICKER_SIZE) {
            throw new IndexOutOfBoundsException();
        }

        if (x < 5) { // Grey
            int intensity = 255 - y * 255 / PICKER_SIZE;
            color.set(intensity, intensity, intensity);
        } else { // Colored
            float hue = (x - 5f) / (PICKER_SIZE - 5f);
            float saturation = Math.min(y / (PICKER_SIZE / 2f), 1);
            float brightness = Math.min(2 - y / (PICKER_SIZE / 2f), 1);
            color.fromHSB(hue, saturation, brightness);
        }
    }

    @Override
    public T setColor(ReadableColor color) {
        this.color.setColor(color);
        return getThis();
    }

    @Override
    public T setOpened(boolean opened) {
        this.opened = opened;
        return getThis();
    }

    @Override
    public Collection<GuiElement> getChildren() {
        return opened ? Collections.<GuiElement>singleton(picker) : Collections.<GuiElement>emptyList();
    }

    @Override
    public T onSelection(Consumer<ReadableColor> consumer) {
        this.onSelection = consumer;
        return getThis();
    }

    public void onSelection(Color oldColor) {
        if (onSelection != null) {
            onSelection.consume(oldColor);
        }
    }

    @Override
    public boolean mouseClick(Click click) {
        Point pos = new Point(click);
        if (getContainer() != null) {
            getContainer().convertFor(this, pos);
        }

        if (isEnabled()) {
            if (isMouseHovering(pos)) {
                setOpened(!isOpened());
                return true;
            }
        }
        return false;
    }

    protected boolean isMouseHovering(ReadablePoint pos) {
        return pos.getX() > 0 && pos.getY() > 0
                && pos.getX() < getLastSize().getWidth() && pos.getY() < getLastSize().getHeight();
    }

    public Color getColor() {
        return this.color;
    }

    public boolean isOpened() {
        return this.opened;
    }

    protected class GuiPicker extends AbstractGuiElement<GuiPicker> implements Clickable, Draggable {
        private boolean dragging;

        @Override
        protected GuiPicker getThis() {
            return this;
        }

        @Override
        public int getLayer() {
            return 1;
        }

        @Override
        protected ReadableDimension calcMinSize() {
            return new Dimension(PICKER_SIZE, PICKER_SIZE);
        }

        @Override
        public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
            super.draw(renderer, size, renderInfo);
            Color color = new Color();
            for (int x = 0; x < PICKER_SIZE; x++) {
                for (int y = 0; y < PICKER_SIZE; y++) {
                    getColorAtPosition(x, y, color);
                    renderer.drawRect(x, y, 1, 1, color);
                }
            }
        }

        @Override
        public boolean mouseClick(Click click) {
            if (isEnabled()) {
                Point pos = new Point(click);
                AbstractGuiColorPicker parent = AbstractGuiColorPicker.this;
                if (parent.getContainer() != null) {
                    parent.getContainer().convertFor(parent, pos, 1);
                }
                pos.translate(0, -AbstractGuiColorPicker.this.getLastSize().getHeight());

                if (isMouseHovering(pos)) {
                    Color oldColor = new Color(color);
                    getColorAtPosition(pos.getX(), pos.getY(), color);
                    dragging = true;
                    onSelection(oldColor);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean mouseDrag(Click click) {
            return dragging && mouseClick(click);
        }

        @Override
        public boolean mouseRelease(Click click) {
            if (dragging) {
                dragging = false;
                return true;
            }
            return false;
        }

        protected boolean isMouseHovering(ReadablePoint pos) {
            return pos.getX() > 0 && pos.getY() > 0
                    && pos.getX() < PICKER_SIZE && pos.getY() < PICKER_SIZE;
        }
    }
}
