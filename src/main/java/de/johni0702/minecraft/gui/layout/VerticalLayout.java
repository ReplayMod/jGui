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
package de.johni0702.minecraft.gui.layout;

import de.johni0702.minecraft.gui.container.GuiContainer;
import de.johni0702.minecraft.gui.element.GuiElement;
import de.johni0702.minecraft.gui.utils.lwjgl.Dimension;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class VerticalLayout implements Layout {
    private static final Data DEFAULT_DATA = new Data(0);

    private final Alignment alignment;

    private int spacing;

    public VerticalLayout() {
        this(Alignment.TOP);
    }

    public VerticalLayout(Alignment alignment) {
        this.alignment = alignment;
    }

    @Override
    public Map<GuiElement, Pair<ReadablePoint, ReadableDimension>> layOut(GuiContainer<?> container, ReadableDimension size) {
        int y = 0;
        int spacing = 0;
        Map<GuiElement, Pair<ReadablePoint, ReadableDimension>> map = new LinkedHashMap<>();
        for (Map.Entry<GuiElement, LayoutData> entry : container.getElements().entrySet()) {
            y += spacing;
            spacing = this.spacing;

            GuiElement element  = entry.getKey();
            Data data = entry.getValue() instanceof Data ? (Data) entry.getValue() : DEFAULT_DATA;
            Dimension elementSize = new Dimension(element.getMinSize());
            ReadableDimension elementMaxSize = element.getMaxSize();
            elementSize.setHeight(Math.min(size.getHeight() - y, Math.min(elementSize.getHeight(), elementMaxSize.getHeight())));
            elementSize.setWidth(Math.min(size.getWidth(), (data.maximizeWidth ? elementMaxSize : elementSize).getWidth()));
            int remainingWidth = size.getWidth() - elementSize.getWidth();
            int x = (int) (data.alignment * remainingWidth);
            map.put(element, Pair.<ReadablePoint, ReadableDimension>of(new Point(x, y), elementSize));
            y += elementSize.getHeight();
        }
        if (alignment != Alignment.TOP) {
            int remaining = size.getHeight() - y;
            if (alignment == Alignment.CENTER) {
                remaining /= 2;
            }
            for (Pair<ReadablePoint, ReadableDimension> pair : map.values()) {
                ((Point) pair.getLeft()).translate(0, remaining);
            }
        }
        return map;
    }

    @Override
    public ReadableDimension calcMinSize(GuiContainer<?> container) {
        int maxWidth = 0;
        int height = 0;
        int spacing = 0;
        for (Map.Entry<GuiElement, LayoutData> entry : container.getElements().entrySet()) {
            height += spacing;
            spacing = this.spacing;

            GuiElement element = entry.getKey();
            ReadableDimension minSize = element.getMinSize();
            int width = minSize.getWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
            height += minSize.getHeight();
        }
        return new Dimension(maxWidth, height);
    }

    public int getSpacing() {
        return this.spacing;
    }

    public VerticalLayout setSpacing(int spacing) {
        this.spacing = spacing;
        return this;
    }

    public static class Data implements LayoutData {
        private double alignment;
        private boolean maximizeWidth;

        public Data() {
            this(0);
        }

        public Data(double alignment) {
            this(alignment, true);
        }

        public Data(double alignment, boolean maximizeWidth) {
            this.alignment = alignment;
            this.maximizeWidth = maximizeWidth;
        }

        public double getAlignment() {
            return this.alignment;
        }

        public boolean isMaximizeWidth() {
            return this.maximizeWidth;
        }

        public void setAlignment(double alignment) {
            this.alignment = alignment;
        }

        public void setMaximizeWidth(boolean maximizeWidth) {
            this.maximizeWidth = maximizeWidth;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return Double.compare(data.alignment, alignment) == 0 &&
                    maximizeWidth == data.maximizeWidth;
        }

        @Override
        public int hashCode() {
            return Objects.hash(alignment, maximizeWidth);
        }

        @Override
        public String toString() {
            return "Data{" +
                    "alignment=" + alignment +
                    ", maximizeWidth=" + maximizeWidth +
                    '}';
        }
    }

    public enum Alignment {
        TOP, BOTTOM, CENTER
    }
}
