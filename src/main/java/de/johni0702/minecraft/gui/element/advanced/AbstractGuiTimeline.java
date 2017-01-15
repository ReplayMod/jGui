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
import de.johni0702.minecraft.gui.RenderInfo;
import de.johni0702.minecraft.gui.container.GuiContainer;
import de.johni0702.minecraft.gui.element.AbstractGuiElement;
import de.johni0702.minecraft.gui.element.GuiTooltip;
import de.johni0702.minecraft.gui.function.Clickable;
import de.johni0702.minecraft.gui.utils.Colors;
import de.johni0702.minecraft.gui.utils.Utils;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.util.*;

public abstract class AbstractGuiTimeline<T extends AbstractGuiTimeline<T>> extends AbstractGuiElement<T> implements IGuiTimeline<T>, Clickable {
    protected static final int TEXTURE_WIDTH = 64;
    protected static final int TEXTURE_HEIGHT = 22;

    protected static final int TEXTURE_X = 0;
    protected static final int TEXTURE_Y = 16;

    protected static final int BORDER_LEFT = 4;
    protected static final int BORDER_RIGHT = 4;
    protected static final int BORDER_TOP = 4;
    protected static final int BORDER_BOTTOM = 3;

    protected static final int MARKER_MIN_DISTANCE = 40;

    private OnClick onClick;

    private int length;
    private int cursorPosition;
    private double zoom = 1;
    private int offset;
    private boolean drawCursor = true;
    private boolean drawMarkers;

    /**
     * Use {@link #getLastSize()} instead.
     */
    @Deprecated
    protected ReadableDimension size;

    public AbstractGuiTimeline() {
    }

    public AbstractGuiTimeline(GuiContainer container) {
        super(container);
    }

    {
        setTooltip(new GuiTooltip(){
            @Override
            public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
                setText(getTooltipText(renderInfo));
                super.draw(renderer, size, renderInfo);
            }
        }.setText("00:00"));
    }

    protected String getTooltipText(RenderInfo renderInfo) {
        int ms = getTimeAt(renderInfo.mouseX, renderInfo.mouseY);
        int s = ms / 1000 % 60;
        int m = ms / 1000 / 60;
        return String.format("%02d:%02d", m, s);
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        this.size = size;
        super.draw(renderer, size, renderInfo);

        int width = size.getWidth();
        int height = size.getHeight();

        renderer.bindTexture(TEXTURE);

        // We have to increase the border size as there is one pixel row which is part of the border while drawing
        // but isn't during position calculations due to shadows
        Utils.drawDynamicRect(renderer, width, height, TEXTURE_X, TEXTURE_Y, TEXTURE_WIDTH, TEXTURE_HEIGHT,
                BORDER_TOP + 1, BORDER_BOTTOM, BORDER_LEFT + 1, BORDER_RIGHT);

        if (drawMarkers) {
            drawMarkers(renderer, size);
        }

        drawTimelineCursor(renderer, size);
    }

    /**
     * Draws the timeline cursor.
     * This is separate from the main draw method so subclasses can repaint the cursor
     * in case it got drawn over by other elements.
     * @param renderer Gui renderer used to draw the cursor
     * @param size Size of the drawable area
     */
    protected void drawTimelineCursor(GuiRenderer renderer, ReadableDimension size) {
        if (!drawCursor) return;
        int height = size.getHeight();
        renderer.bindTexture(TEXTURE);

        int visibleLength = (int) (length * zoom);
        int cursor = MathHelper.clamp(cursorPosition, offset, offset + visibleLength);
        double positionInVisible = cursor - offset;
        double fractionOfVisible = positionInVisible / visibleLength;
        int cursorX = (int) (BORDER_LEFT + fractionOfVisible * (size.getWidth() - BORDER_LEFT - BORDER_RIGHT));

        // Pin
        renderer.drawTexturedRect(cursorX - 2, BORDER_TOP - 1, 64, 0, 5, 4);
        // Needle
        for (int y = BORDER_TOP - 1; y < height - BORDER_BOTTOM; y += 11) {
            int segmentHeight = Math.min(11, height - BORDER_BOTTOM - y);
            renderer.drawTexturedRect(cursorX - 2, y, 64, 4, 5, segmentHeight);
        }
    }

    protected void drawMarkers(GuiRenderer renderer, ReadableDimension size) {
        int visibleLength = (int) (length * zoom);
        int markerInterval = getMarkerInterval();
        int smallInterval = Math.max(markerInterval / 5, 1);
        int time = offset / markerInterval * markerInterval;
        while (time <= offset + visibleLength) {
            if (time >= offset) {
                drawMarker(renderer, size, time, time % markerInterval == 0);
            }
            time += smallInterval;
        }
    }

    protected void drawMarker(GuiRenderer renderer, ReadableDimension size, int time, boolean big) {
        int visibleLength = (int) (length * zoom);
        double positionInVisible = time - offset;
        double fractionOfVisible = positionInVisible / visibleLength;
        int positionX = (int) (BORDER_LEFT + fractionOfVisible * (size.getWidth() - BORDER_LEFT - BORDER_RIGHT));
        int height = size.getHeight() / (big ? 3 : 6);
        ReadableColor color = big ? Colors.LIGHT_GRAY : Colors.WHITE;
        renderer.drawRect(positionX, size.getHeight() - BORDER_BOTTOM - height, 1, height, color);
    }

    /**
     * Returns the time which the mouse is at.
     * @param mouseX X coordinate of the mouse
     * @param mouseY Y coordinate of the mouse
     * @return The time or -1 if the mouse isn't on the timeline
     */
    protected int getTimeAt(int mouseX, int mouseY) {
        if (getLastSize() == null) {
            return -1;
        }
        Point mouse = new Point(mouseX, mouseY);
        getContainer().convertFor(this, mouse);
        mouseX = mouse.getX();
        mouseY = mouse.getY();

        if (mouseX < 0 || mouseY < 0
                || mouseX > size.getWidth() || mouseY > size.getHeight()) {
            return -1;
        }

        int width = size.getWidth();
        int bodyWidth = width - BORDER_LEFT - BORDER_RIGHT;
        double segmentLength = length * zoom;
        double segmentTime =  segmentLength * (mouseX - BORDER_LEFT) / bodyWidth;
        return Math.min(Math.max((int) Math.round(offset + segmentTime), 0), length);
    }

    public void onClick(int time) {
        if (onClick != null) {
            onClick.run(time);
        }
    }

    @Override
    public ReadableDimension calcMinSize() {
        return new Dimension(0, 0);
    }

    @Override
    public T setLength(int length) {
        this.length = length;
        return getThis();
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public T setCursorPosition(int position) {
        this.cursorPosition = position;
        return getThis();
    }

    @Override
    public int getCursorPosition() {
        return cursorPosition;
    }

    @Override
    public T setZoom(double zoom) {
        this.zoom = Math.min(zoom, 1);
        checkOffset();
        return getThis();
    }

    @Override
    public double getZoom() {
        return zoom;
    }

    @Override
    public T setOffset(int offset) {
        this.offset = offset;
        checkOffset();
        return getThis();
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public T onClick(OnClick onClick) {
        this.onClick = onClick;
        return getThis();
    }

    @Override
    public boolean mouseClick(ReadablePoint position, int button) {
        int time = getTimeAt(position.getX(), position.getY());
        if (time != -1) {
            onClick(time);
            return true;
        }
        return false;
    }

    @Override
    public boolean getMarkers() {
        return drawMarkers;
    }

    @Override
    public T setMarkers(boolean active) {
        this.drawMarkers = active;
        return getThis();
    }

    @Override
    public T setMarkers() {
        return setMarkers(true);
    }

    @Override
    public int getMarkerInterval() {
        if (size == null) {
            return length;
        }
        int width = size.getWidth() - BORDER_LEFT - BORDER_RIGHT; // Width of the drawn timeline
        double segmentLength = length * zoom; // Length of the drawn timeline
        int maxMarkers = width / MARKER_MIN_DISTANCE; // Max. amount of markers that can fit in the timeline
        int minInterval = (int) (segmentLength / maxMarkers); // Min. interval between those markers
        final int S = 1000;
        final int M = 60*S;
        final int[] snapTo = {S, 2*S, 5*S, 10*S, 15*S, 20*S, 30*S, M, 2*M, 5*M, 10*M, 15*M, 30*M};
        // Find next greater snap
        for (int snap : snapTo) {
            if (snap > minInterval) {
                return snap;
            }
        }
        return snapTo[snapTo.length - 1];
    }

    @Override
    public T setCursor(boolean active) {
        this.drawCursor = active;
        return getThis();
    }

    @Override
    public boolean getCursor() {
        return drawCursor;
    }

    /**
     * Make sure the offset is far enough to the left so we don't look over the edge on the right.
     */
    private void checkOffset() {
        int visibleLength = (int) (length * zoom);
        if (visibleLength + offset > length) {
            offset = length - visibleLength;
        }
    }

    @Override
    protected ReadableDimension getLastSize() {
        return super.getLastSize();
    }
}
