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

import de.johni0702.minecraft.gui.utils.NonNull;
import de.johni0702.minecraft.gui.utils.lwjgl.Color;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableColor;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import de.johni0702.minecraft.gui.utils.lwjgl.WritableDimension;
import de.johni0702.minecraft.gui.versions.MCVer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

//#if MC>=12106
//$$ import net.minecraft.client.texture.AbstractTexture;
//$$ import org.joml.Matrix3x2fStack;
//#endif

//#if MC>=12105
//$$ import com.mojang.blaze3d.buffers.GpuBuffer;
//$$ import com.mojang.blaze3d.systems.RenderPass;
//$$ import com.mojang.blaze3d.textures.GpuTexture;
//$$ import com.mojang.blaze3d.pipeline.RenderPipeline;
//$$ import com.mojang.blaze3d.textures.TextureFormat;
//$$ import net.minecraft.client.gl.Framebuffer;
//$$ import net.minecraft.client.gl.RenderPipelines;
//$$ import net.minecraft.client.texture.GlTexture;
//$$ import java.util.OptionalDouble;
//$$ import java.util.OptionalInt;
//#endif

//#if MC>=12102
//#if MC<12105
//$$ import net.minecraft.client.gl.ShaderProgramKeys;
//#endif
//#endif

//#if MC>=12100
//$$ import net.minecraft.client.render.RenderLayer;
//$$ import net.minecraft.client.render.VertexConsumer;
//$$ import net.minecraft.client.render.VertexConsumerProvider;
//#endif

//#if MC>=12000
//$$ import net.minecraft.client.render.BufferBuilder;
//#if MC<12105
//$$ import net.minecraft.client.render.BufferRenderer;
//#endif
//$$ import net.minecraft.client.render.Tessellator;
//$$ import net.minecraft.client.render.VertexFormats;
//$$ import org.joml.Matrix4f;
//#endif

//#if MC>=11700
//#if MC<12105
//$$ import com.mojang.blaze3d.platform.GlStateManager;
//#endif
//$$ import net.minecraft.client.render.GameRenderer;
//$$ import net.minecraft.client.render.VertexFormat;
//#else
//#endif

//#if MC>=10800
import com.mojang.blaze3d.platform.GlStateManager;
import static com.mojang.blaze3d.platform.GlStateManager.*;
//#else
//$$ import net.minecraft.client.renderer.OpenGlHelper;
//$$ import static de.johni0702.minecraft.gui.versions.MCVer.*;
//#endif

import static de.johni0702.minecraft.gui.versions.MCVer.*;
import static org.lwjgl.opengl.GL11.*;

public class MinecraftGuiRenderer implements GuiRenderer {

    private final MinecraftClient mc = getMinecraft();

    //#if MC>=12000
    //$$ private final DrawContext context;
    //#else
    private final DrawableHelper gui = new DrawableHelper(){};
    //#endif

    //#if MC>=12106
    //$$ private final Matrix3x2fStack matrixStack;
    //#else
    private final MatrixStack matrixStack;
    //#endif

    @NonNull
    //#if MC>=11400
    private final int scaledWidth = newScaledResolution(mc).getScaledWidth();
    private final int scaledHeight = newScaledResolution(mc).getScaledHeight();
    private final double scaleFactor = newScaledResolution(mc).getScaleFactor();
    //#else
    //$$ private final int scaledWidth = newScaledResolution(mc).getScaledWidth();
    //$$ private final int scaledHeight = newScaledResolution(mc).getScaledHeight();
    //$$ private final double scaleFactor = newScaledResolution(mc).getScaleFactor();
    //#endif

    //#if MC>=12000
    //$$ public MinecraftGuiRenderer(DrawContext context) {
    //$$     this.context = context;
    //$$     this.matrixStack = context.getMatrices();
        //#if MC>=12102 && MC<12106
        //$$ context.draw();
        //#endif
    //$$ }
    //#else
    public MinecraftGuiRenderer(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }
    //#endif

    @Override
    public ReadablePoint getOpenGlOffset() {
        return new Point(0, 0);
    }

    //#if MC>=12000
    //$$ @Override
    //$$ public DrawContext getContext() {
    //$$     return context;
    //$$ }
    //#endif

    //#if MC<12106
    @Override
    public MatrixStack getMatrixStack() {
        return matrixStack;
    }
    //#endif

    @Override
    public ReadableDimension getSize() {
        return new ReadableDimension() {
            @Override
            public int getWidth() {
                return scaledWidth;
            }

            @Override
            public int getHeight() {
                return scaledHeight;
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
        y = scaledHeight - y - height;

        int f = (int) scaleFactor;
        MCVer.setScissorBounds(x * f, y * f, width * f, height * f);
    }

    private Identifier boundTexture;
    //#if MC>=12105
    //$$ private GpuTexture boundTextureGpu;
    //#else
    private int boundTextureGpu;
    //#endif

    @Override
    public void bindTexture(Identifier location) {
        boundTexture = location;
        //#if MC>=12105
        //$$ boundTextureGpu = null;
        //#else
        boundTextureGpu = 0;
        //#endif
    }

    @Override
    public void bindTexture(int glId) {
        boundTexture = null;
        //#if MC>=12105
        //#if MC>=12106
        //$$ boundTextureGpu = new GlTexture(GlTexture.USAGE_TEXTURE_BINDING, null, TextureFormat.RGBA8, 0, 0, 0, 1, glId) {
        //#else
        //$$ boundTextureGpu = new GlTexture(null, TextureFormat.RGBA8, 0, 0, 0, glId) {
        //#endif
        //$$     {
        //#if MC>=12111
        //#else
        //$$         this.needsReinit = false;
        //#endif
        //$$     }
        //$$ };
        //#else
        boundTextureGpu = glId;
        //#endif
    }

    //#if MC>=12105
    //$$ public void bindTexture(GpuTexture texture) {
    //$$     boundTexture = null;
    //$$     boundTextureGpu = texture;
    //$$ }
    //#endif

    @Override
    public void drawTexturedRect(int x, int y, int u, int v, int width, int height) {
        drawTexturedRect(x, y, u, v, width, height, width, height, 256, 256);
    }

    @Override
    public void drawTexturedRect(int x, int y, int u, int v, int width, int height, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        color(1, 1, 1);

        //#if MC<12105
        if (boundTexture != null) {
            MCVer.bindTexture(boundTexture);
        } else {
            //#if MC>=11700
            //$$ RenderSystem.setShaderTexture(0, boundTextureGpu);
            //#elseif MC>=10800
            GlStateManager.bindTexture(boundTextureGpu);
            //#else
            //$$ GL11.glBindTexture(GL_TEXTURE_2D, boundTextureGpu);
            //#endif
        }
        //#endif

        //#if MC>=12000
        //$$ drawTexturedRect(x, x + width, y, y + height, u / (float) textureWidth, (u + uWidth) / (float) textureWidth, v / (float) textureHeight, (v + vHeight) / (float) textureHeight);
        //#elseif MC>=11600
        DrawableHelper.drawTexture(matrixStack, x, y, width, height, u, v, uWidth, vHeight, textureWidth, textureHeight);
        //#else
        //#if MC>=11400
        //$$ DrawableHelper.blit(x, y, width, height, u, v, uWidth, vHeight, textureWidth, textureHeight);
        //#else
        //$$ Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, textureWidth, textureHeight);
        //#endif
        //#endif
    }

    //#if MC>=12000
    //$$ private void drawTexturedRect(int x1, int x2, int y1, int y2, float u1, float u2, float v1, float v2) {
        //#if MC>=12106
        //$$ Identifier identifier;
        //$$ if (boundTexture != null) {
        //$$     identifier = boundTexture;
        //$$ } else {
        //$$     identifier = Identifier.of("jgui", "__tmp_texture__");
        //$$     mc.getTextureManager().registerTexture(identifier, new AbstractTexture() {
        //$$         { glTextureView = RenderSystem.getDevice().createTextureView(boundTextureGpu); }
        //$$         @Override public void close() {} // ignore later `destroyTexture` call
        //$$     });
        //$$ }
        //$$
        //$$ context.drawTexturedQuad(identifier, x1, y1, x2, y2, u1, u2, v1, v2);
        //$$
        //$$ if (boundTexture == null) {
        //$$     mc.getTextureManager().destroyTexture(identifier);
        //$$ }
        //#else
        //#if MC<12105
        //#if MC>=12102
        //$$ RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
        //#else
        //$$ RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        //#endif
    //#endif
    //$$     Matrix4f matrix = matrixStack.peek().getPositionMatrix();
    //$$     Tessellator tessellator = Tessellator.getInstance();
        //#if MC>=12100
        //$$ BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        //$$ bufferBuilder.vertex(matrix, x1, y1, 0).texture(u1, v1).color(255, 255, 255, 255);
        //$$ bufferBuilder.vertex(matrix, x1, y2, 0).texture(u1, v2).color(255, 255, 255, 255);
        //$$ bufferBuilder.vertex(matrix, x2, y2, 0).texture(u2, v2).color(255, 255, 255, 255);
        //$$ bufferBuilder.vertex(matrix, x2, y1, 0).texture(u2, v1).color(255, 255, 255, 255);
        //$$ try (var builtBuffer = bufferBuilder.end()) {
            //#if MC>=12105
            //$$ GpuTexture texture;
            //$$ if (boundTexture != null) {
            //$$     texture = mc.getTextureManager().getTexture(boundTexture).getGlTexture();
            //$$ } else {
            //$$     texture = boundTextureGpu;
            //$$ }
            //$$ RenderPipeline renderPipeline = RenderPipelines.GUI_TEXTURED;
            //$$ GpuBuffer vertBuffer = renderPipeline.getVertexFormat().uploadImmediateVertexBuffer(builtBuffer.getBuffer());
            //$$ RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(builtBuffer.getDrawParameters().mode());
            //$$ GpuBuffer indexBuffer = shapeIndexBuffer.getIndexBuffer(builtBuffer.getDrawParameters().indexCount());
            //$$ VertexFormat.IndexType indexType = shapeIndexBuffer.getIndexType();
            //$$ Framebuffer framebuffer = getMinecraft().getFramebuffer();
            //$$ try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(framebuffer.getColorAttachment(), OptionalInt.empty(), framebuffer.getDepthAttachment(), OptionalDouble.empty())) {
            //$$     renderPass.setPipeline(renderPipeline);
            //$$     renderPass.setVertexBuffer(0, vertBuffer);
            //$$     if (RenderSystem.SCISSOR_STATE.isEnabled()) {
            //$$         renderPass.enableScissor(RenderSystem.SCISSOR_STATE);
            //$$     }
            //$$     renderPass.bindSampler("Sampler0", texture);
            //$$     renderPass.setIndexBuffer(indexBuffer, indexType);
            //$$     renderPass.drawIndexed(0, builtBuffer.getDrawParameters().indexCount());
            //$$ }
            //#else
            //$$ BufferRenderer.drawWithGlobalProgram(builtBuffer);
            //#endif
        //$$ }
        //#else
        //$$ BufferBuilder bufferBuilder = tessellator.getBuffer();
        //$$ bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        //$$ bufferBuilder.vertex(matrix, x1, y1, 0).texture(u1, v1).next();
        //$$ bufferBuilder.vertex(matrix, x1, y2, 0).texture(u1, v2).next();
        //$$ bufferBuilder.vertex(matrix, x2, y2, 0).texture(u2, v2).next();
        //$$ bufferBuilder.vertex(matrix, x2, y1, 0).texture(u2, v1).next();
        //$$ BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        //#endif
        //#endif
    //$$ }
    //#endif

    @Override
    public void drawRect(int x, int y, int width, int height, int color) {
        //#if MC>=12000
        //$$ context.fill(x, y, x + width, y + height, color);
        //#if MC>=12102 && MC<12106
        //$$ context.draw();
        //#endif
        //#else
        DrawableHelper.fill(
                //#if MC>=11600
                matrixStack,
                //#endif
                x, y, x + width, y + height, color);
        //#endif
        color(1, 1, 1);
        //#if MC<12105
        enableBlend();
        //#endif
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
    //#if MC>=12106
    //$$     context.fillGradient(x, y, x + width, y + height, color(tl), color(bl));
    //$$ }
    //#else
        drawRect(x, y, width, height, tl, tr, bl, br, false);
    }

    private void drawRect(int x, int y, int width, int height, ReadableColor tl, ReadableColor tr, ReadableColor bl, ReadableColor br, boolean highlight) {
        //#if MC<11904
        disableTexture();
        //#endif
        //#if MC<12105
        enableBlend();
        blendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        //#endif
        //#if MC>=12100
        //#elseif MC>=11700
        //$$ setShader(GameRenderer::getPositionColorShader);
        //#else
        disableAlphaTest();
        shadeModel(GL_SMOOTH);
        //#endif

        //#if MC>=12100
        //$$ VertexConsumerProvider.Immediate provider = getMinecraft().getBufferBuilders().getEntityVertexConsumers();
        //$$ VertexConsumer vertexConsumer = provider.getBuffer(highlight ? RenderLayer.getGuiTextHighlight() : RenderLayer.getGui());
        //$$ vertexConsumer.vertex(x, y + height, 0).color(bl.getRed(), bl.getGreen(), bl.getBlue(), bl.getAlpha());
        //$$ vertexConsumer.vertex(x + width, y + height, 0).color(br.getRed(), br.getGreen(), br.getBlue(), br.getAlpha());
        //$$ vertexConsumer.vertex(x + width, y, 0).color(tr.getRed(), tr.getGreen(), tr.getBlue(), tr.getAlpha());
        //$$ vertexConsumer.vertex(x, y, 0).color(tl.getRed(), tl.getGreen(), tl.getBlue(), tl.getAlpha());
        //$$ provider.draw();
        //#else
        //#if MC>=10800
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        //#else
        //$$ Tessellator tessellator = Tessellator.instance;
        //$$ Tessellator vertexBuffer = tessellator;
        //#endif
        //#if MC>=10809
        //#if MC>=11700
        //$$ vertexBuffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        //#else
        vertexBuffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        //#endif
        vertexBuffer.vertex(x, y + height, 0).color(bl.getRed(), bl.getGreen(), bl.getBlue(), bl.getAlpha()).next();
        vertexBuffer.vertex(x + width, y + height, 0).color(br.getRed(), br.getGreen(), br.getBlue(), br.getAlpha()).next();
        vertexBuffer.vertex(x + width, y, 0).color(tr.getRed(), tr.getGreen(), tr.getBlue(), tr.getAlpha()).next();
        vertexBuffer.vertex(x, y, 0).color(tl.getRed(), tl.getGreen(), tl.getBlue(), tl.getAlpha()).next();
        //#else
        //$$ vertexBuffer.startDrawingQuads();
        //$$ vertexBuffer.setColorRGBA(bl.getRed(), bl.getGreen(), bl.getBlue(), bl.getAlpha());
        //$$ vertexBuffer.addVertex(x, y + height, 0);
        //$$ vertexBuffer.setColorRGBA(br.getRed(), br.getGreen(), br.getBlue(), br.getAlpha());
        //$$ vertexBuffer.addVertex(x + width, y + height, 0);
        //$$ vertexBuffer.setColorRGBA(tr.getRed(), tr.getGreen(), tr.getBlue(), tr.getAlpha());
        //$$ vertexBuffer.addVertex(x + width, y, 0);
        //$$ vertexBuffer.setColorRGBA(tl.getRed(), tl.getGreen(), tl.getBlue(), tl.getAlpha());
        //$$ vertexBuffer.addVertex(x, y, 0);
        //#endif
        tessellator.draw();
        //#endif

        //#if MC>=11700
        //#else
        shadeModel(GL_FLAT);
        enableAlphaTest();
        //#endif
        //#if MC<11904
        enableTexture();
        //#endif
    }
    //#endif

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
        TextRenderer fontRenderer = MCVer.getFontRenderer();
        try {
            //#if MC>=12106
            //$$ context.drawText(fontRenderer, text, x, y, color | 0xff000000, shadow);
            //$$ return x + fontRenderer.getWidth(text);
            //#elseif MC>=12000
            //$$ int nx = context.drawText(fontRenderer, text, x, y, color, shadow);
            //#if MC>=12102
            //$$ context.draw();
            //#endif
            //$$ return nx;
            //#else
            if (shadow) {
                return fontRenderer.drawWithShadow(
                        //#if MC>=11600
                        matrixStack,
                        //#endif
                        text, x, y, color);
            } else {
                return fontRenderer.draw(
                        //#if MC>=11600
                        matrixStack,
                        //#endif
                        text, x, y, color);
            }
            //#endif
        } finally {
            color(1, 1, 1);
        }
    }

    @Override
    public int drawString(int x, int y, ReadableColor color, String text, boolean shadow) {
        return drawString(x, y, color(color), text, shadow);
    }

    @Override
    public int drawCenteredString(int x, int y, int color, String text, boolean shadow) {
        TextRenderer fontRenderer = MCVer.getFontRenderer();
        x-=fontRenderer.getWidth(text) / 2;
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

    private void color(float r, float g, float b) {
        //#if MC>=12106
        //#elseif MC>=11700
        //$$ RenderSystem.setShaderColor(r, g, b, 1);
        //#else
        //#if MC>=10800
        //#if MC>=11400
        GlStateManager.color4f(r, g, b, 1);
        //#else
        //$$ GlStateManager.color(r, g, b);
        //#endif
        //#else
        //$$ MCVer.color(r, g, b);
        //#endif
        //#endif
    }

    @Override
    public void invertColors(int right, int bottom, int left, int top) {
        if (left >= right || top >= bottom) return;

        //#if MC>=12106
        //$$ context.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, left, top, right, bottom, 0xff0000ff);
        //#else
        color(0, 0, 1);
        //#if MC<11904
        disableTexture();
        //#endif
        //#if MC<12100
        enableColorLogicOp();
        //#if MC>=11700
        //$$ logicOp(GlStateManager.LogicOp.OR_REVERSE);
        //#else
        logicOp(GL11.GL_OR_REVERSE);
        //#endif
        //#endif

        drawRect(right, bottom, right - left, bottom - top, ReadableColor.WHITE, ReadableColor.WHITE, ReadableColor.WHITE, ReadableColor.WHITE, true);

        //#if MC<12100
        disableColorLogicOp();
        //#endif
        //#if MC<11904
        enableTexture();
        //#endif
        color(1, 1, 1);
        //#endif
    }
}
