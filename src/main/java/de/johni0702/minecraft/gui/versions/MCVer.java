package de.johni0702.minecraft.gui.versions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.crash.CrashReportCategory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.ReadableColor;

//#if MC>=11200
import net.minecraft.client.renderer.BufferBuilder;
//#else
//#if MC>=10904
//$$ import net.minecraft.client.renderer.VertexBuffer;
//#else
//$$ import net.minecraft.client.renderer.WorldRenderer;
//#endif
//#endif
//#if MC>=10809
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//#endif

import java.util.concurrent.Callable;

/**
 * Abstraction over things that have changed between different MC versions.
 */
public class MCVer {
    public static ScaledResolution newScaledResolution(Minecraft mc) {
        //#if MC>=10809
        return new ScaledResolution(mc);
        //#else
        //$$ return new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        //#endif
    }

    public static void addDetail(CrashReportCategory category, String name, Callable<String> callable) {
        //#if MC>=10904
        //#if MC>=11200
        category.addDetail(name, callable::call);
        //#else
        //$$ category.setDetail(name, callable::call);
        //#endif
        //#else
        //$$ category.addCrashSectionCallable(name, callable);
        //#endif
    }

    public static void drawRect(int right, int bottom, int left, int top) {
        Tessellator tessellator = Tessellator.getInstance();
        //#if MC>=11200
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        //#else
        //#if MC>=10904
        //$$ VertexBuffer vertexBuffer = tessellator.getBuffer();
        //#else
        //$$ WorldRenderer vertexBuffer = tessellator.getWorldRenderer();
        //#endif
        //#endif
        //#if MC>=10809
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(right, top, 0).endVertex();
        vertexBuffer.pos(left, top, 0).endVertex();
        vertexBuffer.pos(left, bottom, 0).endVertex();
        vertexBuffer.pos(right, bottom, 0).endVertex();
        //#else
        //$$ vertexBuffer.startDrawingQuads();
        //$$ vertexBuffer.addVertex(right, top, 0);
        //$$ vertexBuffer.addVertex(left, top, 0);
        //$$ vertexBuffer.addVertex(left, bottom, 0);
        //$$ vertexBuffer.addVertex(right, bottom, 0);
        //#endif
        tessellator.draw();
    }

    public static void drawRect(int x, int y, int width, int height, ReadableColor tl, ReadableColor tr, ReadableColor bl, ReadableColor br) {
        Tessellator tessellator = Tessellator.getInstance();
        //#if MC>=11200
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        //#else
        //#if MC>=10904
        //$$ VertexBuffer vertexBuffer = tessellator.getBuffer();
        //#else
        //$$ WorldRenderer vertexBuffer = tessellator.getWorldRenderer();
        //#endif
        //#endif
        //#if MC>=10809
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(x, y + height, 0).color(bl.getRed(), bl.getGreen(), bl.getBlue(), bl.getAlpha()).endVertex();
        vertexBuffer.pos(x + width, y + height, 0).color(br.getRed(), br.getGreen(), br.getBlue(), br.getAlpha()).endVertex();
        vertexBuffer.pos(x + width, y, 0).color(tr.getRed(), tr.getGreen(), tr.getBlue(), tr.getAlpha()).endVertex();
        vertexBuffer.pos(x, y, 0).color(tl.getRed(), tl.getGreen(), tl.getBlue(), tl.getAlpha()).endVertex();
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
    }

    public static FontRenderer getFontRenderer() {
        //#if MC>=11200
        return Minecraft.getMinecraft().fontRenderer;
        //#else
        //$$ return Minecraft.getMinecraft().fontRendererObj;
        //#endif
    }

    public static RenderGameOverlayEvent.ElementType getType(RenderGameOverlayEvent.Post event) {
        //#if MC>=10904
        return event.getType();
        //#else
        //$$ return event.type;
        //#endif
    }

    public static float getPartialTicks(RenderGameOverlayEvent.Post event) {
        //#if MC>=10904
        return event.getPartialTicks();
        //#else
        //$$ return event.partialTicks;
        //#endif
    }

    public static float getPartialTicks(GuiScreenEvent.DrawScreenEvent.Post event) {
        //#if MC>=10904
        return event.getRenderPartialTicks();
        //#else
        //$$ return event.renderPartialTicks;
        //#endif
    }

    public static int getMouseX(GuiScreenEvent.DrawScreenEvent.Post event) {
        //#if MC>=10904
        return event.getMouseX();
        //#else
        //$$ return event.mouseX;
        //#endif
    }

    public static int getMouseY(GuiScreenEvent.DrawScreenEvent.Post event) {
        //#if MC>=10904
        return event.getMouseY();
        //#else
        //$$ return event.mouseY;
        //#endif
    }
}
