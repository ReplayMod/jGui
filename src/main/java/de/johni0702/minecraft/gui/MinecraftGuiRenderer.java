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

import de.johni0702.minecraft.gui.utils.lwjgl.Color;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableColor;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import de.johni0702.minecraft.gui.utils.lwjgl.WritableDimension;
import de.johni0702.minecraft.gui.versions.MCVer;
import lombok.NonNull;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

//#if MC>=11300
import net.minecraft.client.MainWindow;
//#else
//$$ import net.minecraft.client.gui.ScaledResolution;
//#endif

//#if MC>=10800
import net.minecraft.client.renderer.GlStateManager;
import static net.minecraft.client.renderer.GlStateManager.*;
//#else
//$$ import net.minecraft.client.renderer.OpenGlHelper;
//$$ import static de.johni0702.minecraft.gui.versions.MCVer.*;
//#endif

import static de.johni0702.minecraft.gui.versions.MCVer.*;
import static org.lwjgl.opengl.GL11.*;

public class MinecraftGuiRenderer implements GuiRenderer {

    private final Gui gui = new Gui(){};

    @NonNull
    //#if MC>=11300
    private final MainWindow size;
    //#else
    //$$ private final ScaledResolution size;
    //#endif

    //#if MC>=11300
    public MinecraftGuiRenderer(MainWindow size) {
    //#else
    //$$ public MinecraftGuiRenderer(ScaledResolution size) {
    //#endif
        this.size = size;
    }

    @Override
    public ReadablePoint getOpenGlOffset() {
        return new Point(0, 0);
    }

    @Override
    public ReadableDimension getSize() {
        return new ReadableDimension() {
            @Override
            public int getWidth() {
                return size.getScaledWidth();
            }

            @Override
            public int getHeight() {
                return size.getScaledHeight();
            }

            @Override
            public void getSize(WritableDimension dest) {
                dest.setSize(getWidth(), getHeight());
            }
        };
    }

    @Override
    public void setDrawingArea(int x, int y, int width, int height) {
        // glScissor origin is bottom left corner whereas otherwise it's top left
        y = size.getScaledHeight() - y - height;

        //#if MC>=11300
        int f = (int) size.getGuiScaleFactor();
        //#else
        //$$ int f = size.getScaleFactor();
        //#endif
        GL11.glScissor(x * f, y * f, width * f, height * f);
    }

    @Override
    public void bindTexture(ResourceLocation location) {
        MCVer.getMinecraft().getTextureManager().bindTexture(location);
    }

    @Override
    public void bindTexture(ITextureObject texture) {
        //#if MC>=10800
        GlStateManager.bindTexture(texture.getGlTextureId());
        //#else
        //$$ GL11.glBindTexture(GL_TEXTURE_2D, texture.getGlTextureId());
        //#endif
    }

    @Override
    public void drawTexturedRect(int x, int y, int u, int v, int width, int height) {
        //#if MC>=11400
        //$$ gui.blit(x, y, u, v, width, height);
        //#else
        gui.drawTexturedModalRect(x, y, u, v, width, height);
        //#endif
    }

    @Override
    public void drawTexturedRect(int x, int y, int u, int v, int width, int height, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        color(1, 1, 1);
        //#if MC>=11400
        //$$ DrawableHelper.blit(x, y, width, height, u, v, uWidth, vHeight, textureWidth, textureHeight);
        //#else
        Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, textureWidth, textureHeight);
        //#endif
    }

    @Override
    public void drawRect(int x, int y, int width, int height, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
        color(1, 1, 1);
        enableBlend();
    }

    @Override
    public void drawRect(int x, int y, int width, int height, ReadableColor color) {
        drawRect(x, y, width, height, color(color));
    }

    @Override
    public void drawRect(int x, int y, int width, int height, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        drawRect(x, y, width, height, color(topLeftColor), color(topRightColor), color(bottomLeftColor), color(bottomRightColor));
    }

    @Override
    public void drawRect(int x, int y, int width, int height, ReadableColor tl, ReadableColor tr, ReadableColor bl, ReadableColor br) {
        disableTexture2D();
        enableBlend();
        disableAlpha();
        tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        shadeModel(GL_SMOOTH);
        MCVer.drawRect(x, y, width, height, tl, tr, bl, br);
        shadeModel(GL_FLAT);
        enableAlpha();
        enableTexture2D();
    }

    @Override
    public int drawString(int x, int y, int color, String text) {
        return drawString(x, y, color, text, false);
    }

    @Override
    public int drawString(int x, int y, ReadableColor color, String text) {
        return drawString(x, y, color(color), text);
    }

    @Override
    public int drawCenteredString(int x, int y, int color, String text) {
        return drawCenteredString(x, y, color, text, false);
    }

    @Override
    public int drawCenteredString(int x, int y, ReadableColor color, String text) {
        return drawCenteredString(x, y, color(color), text);
    }

    @Override
    public int drawString(int x, int y, int color, String text, boolean shadow) {
        FontRenderer fontRenderer = MCVer.getFontRenderer();
        int ret = shadow ? fontRenderer.drawStringWithShadow(text, x, y, color) : fontRenderer.drawString(text, x, y, color);
        color(1, 1, 1);
        return ret;
    }

    @Override
    public int drawString(int x, int y, ReadableColor color, String text, boolean shadow) {
        return drawString(x, y, color(color), text, shadow);
    }

    @Override
    public int drawCenteredString(int x, int y, int color, String text, boolean shadow) {
        FontRenderer fontRenderer = MCVer.getFontRenderer();
        x-=fontRenderer.getStringWidth(text) / 2;
        return drawString(x, y, color, text, shadow);
    }

    @Override
    public int drawCenteredString(int x, int y, ReadableColor color, String text, boolean shadow) {
        return drawCenteredString(x, y, color(color), text, shadow);
    }

    private int color(ReadableColor color) {
        return color.getAlpha() << 24
                | color.getRed() << 16
                | color.getGreen() << 8
                | color.getBlue();
    }

    private ReadableColor color(int color) {
        return new Color((color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff, (color >> 24) & 0xff);
    }

    private void color(int r, int g, int b) {
        //#if MC>=10800
        //#if MC>=11300
        GlStateManager.color3f(r, g, b);
        //#else
        //$$ GlStateManager.color(r, g, b);
        //#endif
        //#else
        //$$ MCVer.color(r, g, b);
        //#endif
    }
}
