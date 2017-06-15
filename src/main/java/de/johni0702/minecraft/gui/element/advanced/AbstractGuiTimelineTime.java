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
import de.johni0702.minecraft.gui.utils.Colors;
import org.lwjgl.util.Dimension;
import org.lwjgl.util.ReadableDimension;

import static de.johni0702.minecraft.gui.element.advanced.AbstractGuiTimeline.BORDER_LEFT;
import static de.johni0702.minecraft.gui.element.advanced.AbstractGuiTimeline.BORDER_RIGHT;

public abstract class AbstractGuiTimelineTime<T extends AbstractGuiTimelineTime<T, U>, U extends AbstractGuiTimeline<U>>
        extends AbstractGuiElement<T> implements IGuiTimelineTime<T, U> {
    private U timeline;

    public AbstractGuiTimelineTime() {
    }

    public AbstractGuiTimelineTime(GuiContainer container) {
        super(container);
    }

    @Override
    public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
        super.draw(renderer, size, renderInfo);

        if (timeline == null || timeline.getLastSize() == null) return;

        int offset = (size.getWidth() - timeline.getLastSize().getWidth()) / 2;
        int visibleLength = (int) (timeline.getLength() * timeline.getZoom());
        int markerInterval = timeline.getMarkerInterval();
        int time = timeline.getOffset() / markerInterval * markerInterval;
        while (time <= timeline.getOffset() + visibleLength) {
            if (time >= timeline.getOffset()) {
                drawTime(renderer, size, time, offset);
            }
            time += markerInterval;
        }
    }

    protected void drawTime(GuiRenderer renderer, ReadableDimension size, int time, int offset) {
        int visibleLength = (int) (timeline.getLength() * timeline.getZoom());
        double positionInVisible = time - timeline.getOffset();
        double fractionOfVisible = positionInVisible / visibleLength;
        int positionX = (int) (BORDER_LEFT + fractionOfVisible * (size.getWidth() - BORDER_LEFT - BORDER_RIGHT)) + offset;
        String str = String.format("%02d:%02d", time / 1000 / 60, time / 1000 % 60);
        int stringWidth = getMinecraft().fontRenderer.getStringWidth(str);
        positionX = Math.max(stringWidth / 2, Math.min(size.getWidth() - stringWidth / 2, positionX));
        renderer.drawCenteredString(positionX, 0, Colors.WHITE, str, true);
    }

    @Override
    public ReadableDimension calcMinSize() {
        return new Dimension(0, 0);
    }

    @Override
    public T setTimeline(U timeline) {
        this.timeline = timeline;
        return getThis();
    }

    @Override
    public U getTimeline() {
        return timeline;
    }
}
