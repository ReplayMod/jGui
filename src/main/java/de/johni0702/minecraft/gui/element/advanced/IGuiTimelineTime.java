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

/**
 * Displays the time of a timeline at its big markers.
 * This should be used with a GuiTimeline in a VerticalLayout, both centered at 0.5.
 *
 * @param <T> Type of this timeline time instance, used for chaining
 * @param <U> Type of the timeline
 */
public interface IGuiTimelineTime<T extends IGuiTimelineTime<T, U>, U extends IGuiTimeline<U>> extends GuiElement<T> {
    /**
     * Returns the timeline of which the time is drawn.
     * @return The timeline or {@code null} if not yet set
     */
    U getTimeline();

    /**
     * Set the timeline of which the time should be drawn.
     * @param timeline The timeline
     * @return {@code this}, for chaining
     */
    T setTimeline(U timeline);
}
