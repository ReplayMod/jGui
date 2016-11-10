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
import de.johni0702.minecraft.gui.OffsetGuiRenderer;
import de.johni0702.minecraft.gui.RenderInfo;
import de.johni0702.minecraft.gui.container.GuiContainer;
import de.johni0702.minecraft.gui.function.Clickable;
import de.johni0702.minecraft.gui.function.Draggable;
import de.johni0702.minecraft.gui.utils.Utils;
import org.lwjgl.util.Dimension;
import org.lwjgl.util.Point;
import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;

public abstract class AbstractGuiHorizontalScrollbar<T extends AbstractGuiHorizontalScrollbar<T>> extends AbstractGuiElement<T> implements Clickable, Draggable, IGuiHorizontalScrollbar<T> {
    protected static final int TEXTURE_FG_X = 0;
    protected static final int TEXTURE_FG_Y = 0;
    protected static final int TEXTURE_FG_WIDTH = 62;
    protected static final int TEXTURE_FG_HEIGHT = 7;
    protected static final int TEXTURE_BG_X = 0;
    protected static final int TEXTURE_BG_Y = 7;
    protected static final int TEXTURE_BG_WIDTH = 64;
    protected static final int TEXTURE_BG_HEIGHT = 9;

    protected static final int BORDER_TOP = 1;
    protected static final int BORDER_BOTTOM = 1;
    protected static final int BORDER_LEFT = 1;
    protected static final int BORDER_RIGHT = 1;

    private Runnable onValueChanged;

    private double zoom = 1;
    private double offset;

    private ReadablePoint startDragging;
    private boolean dragging;

    public AbstractGuiHorizontalScrollbar() {
    }

    public AbstractGuiHorizontalScrollbar(GuiContainer container) {
        super(container);
    }

    @Override
    protected ReadableDimension calcMinSize() {
        return new Dimension(0, 0);
    }

    @Override
    public boolean mouseClick(ReadablePoint position, int button) {
        Point pos = new Point(position);
        if (getContainer() != null) {
            getContainer().convertFor(this, pos);
        }

        if (isMouseHoveringBar(pos) && isEnabled()) {
            dragging = true;
            updateValue(pos);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDrag(ReadablePoint position, int button, long timeSinceLastCall) {
        if (dragging) {
            Point pos = new Point(position);
            if (getContainer() != null) {
                getContainer().convertFor(this, pos);
            }
            updateValue(pos);
        }
        return dragging;
    }

    @Override
    public boolean mouseRelease(ReadablePoint position, int button) {
        if (dragging) {
            Point pos = new Point(position);
            if (getContainer() != null) {
                getContainer().convertFor(this, pos);
            }
            updateValue(pos);
            dragging = false;
            startDragging = null;
            return true;
        } else {
            return false;
        }
    }

    protected boolean isMouseHoveringBar(ReadablePoint pos) {
        int bodyWidth = getLastSize().getWidth() - BORDER_LEFT - BORDER_RIGHT;
        int barOffset = (int) (bodyWidth * offset) + BORDER_LEFT;
        int barWidth = (int) (bodyWidth * zoom);
        return pos.getX() >= barOffset && pos.getY() > BORDER_TOP
                && pos.getX() <= barOffset + barWidth && pos.getY() < getLastSize().getHeight() - BORDER_BOTTOM;
    }

    protected void updateValue(ReadablePoint position) {
        if (getLastSize() == null) {
            return;
        }
        if (startDragging != null) {
            double d = position.getX() - startDragging.getX();
            offset += d / (getLastSize().getWidth() - BORDER_LEFT - BORDER_RIGHT);
            checkOffset();
            onValueChanged();
        }
        startDragging = position;
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        super.draw(renderer, size, renderInfo);
        int width = size.getWidth();
        int height = size.getHeight();

        renderer.bindTexture(TEXTURE);

        // Draw background
        // We have to increase the border size as there is one pixel row which is part of the border while drawing
        // but isn't during position calculations due to shadows
        Utils.drawDynamicRect(renderer, width, height, TEXTURE_BG_X, TEXTURE_BG_Y, TEXTURE_BG_WIDTH, TEXTURE_BG_HEIGHT,
                BORDER_TOP + 1, BORDER_BOTTOM, BORDER_LEFT + 1, BORDER_RIGHT);

        // Draw slider
        int bodyWidth = size.getWidth() - BORDER_LEFT - BORDER_RIGHT;
        int barOffset = (int) (bodyWidth * offset) + BORDER_LEFT;
        int barWidth = (int) (bodyWidth * zoom);
        Utils.drawDynamicRect(new OffsetGuiRenderer(renderer, new Point(barOffset, BORDER_TOP), size),
                barWidth, height - (BORDER_TOP + 1) - BORDER_BOTTOM,
                TEXTURE_FG_X, TEXTURE_FG_Y, TEXTURE_FG_WIDTH, TEXTURE_FG_HEIGHT, 2, 1, 1, 1);
    }

    public void onValueChanged() {
        if (onValueChanged != null) {
            onValueChanged.run();
        }
    }

    @Override
    public T onValueChanged(Runnable runnable) {
        this.onValueChanged = runnable;
        return getThis();
    }

    @Override
    public T setPosition(double pos) {
        this.offset = pos;
        checkOffset();
        onValueChanged();
        return getThis();
    }

    @Override
    public double getPosition() {
        return offset;
    }

    @Override
    public T setZoom(double zoom) {
        this.zoom = Math.min(1, Math.max(0.0001, zoom));
        checkOffset();
        onValueChanged();
        return getThis();
    }

    @Override
    public double getZoom() {
        return zoom;
    }

    /**
     * Make sure the offset is far enough to the left so we don't look over the edge on the right.
     */
    private void checkOffset() {
        if (offset < 0) {
            offset = 0;
        } else if (zoom + offset > 1) {
            offset = 1 - zoom;
        }
    }
}
