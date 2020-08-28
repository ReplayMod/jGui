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
package de.johni0702.minecraft.gui;

import de.johni0702.minecraft.gui.versions.MCVer;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

import java.util.Objects;

public class RenderInfo {
    public final float partialTick;
    public final int mouseX;
    public final int mouseY;
    public final int layer;

    public RenderInfo(float partialTick, int mouseX, int mouseY, int layer) {
        this.partialTick = partialTick;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.layer = layer;
    }

    public RenderInfo offsetMouse(int minusX, int minusY) {
        return new RenderInfo(partialTick, mouseX - minusX, mouseY - minusY, layer);
    }

    public RenderInfo layer(int layer) {
        return this.layer == layer ? this : new RenderInfo(partialTick, mouseX, mouseY, layer);
    }

    public void addTo(CrashReport crashReport) {
        CrashReportSection category = crashReport.addElement("Render info details");
        MCVer.addDetail(category, "Partial Tick", () -> "" + partialTick);
        MCVer.addDetail(category, "Mouse X", () -> "" + mouseX);
        MCVer.addDetail(category, "Mouse Y", () -> "" + mouseY);
        MCVer.addDetail(category, "Layer", () -> "" + layer);
    }

    public float getPartialTick() {
        return this.partialTick;
    }

    public int getMouseX() {
        return this.mouseX;
    }

    public int getMouseY() {
        return this.mouseY;
    }

    public int getLayer() {
        return this.layer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderInfo that = (RenderInfo) o;
        return Float.compare(that.partialTick, partialTick) == 0 &&
                mouseX == that.mouseX &&
                mouseY == that.mouseY &&
                layer == that.layer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(partialTick, mouseX, mouseY, layer);
    }

    @Override
    public String toString() {
        return "RenderInfo{" +
                "partialTick=" + partialTick +
                ", mouseX=" + mouseX +
                ", mouseY=" + mouseY +
                ", layer=" + layer +
                '}';
    }
}
