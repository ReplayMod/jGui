package de.johni0702.minecraft.gui.versions;

import de.johni0702.minecraft.gui.GuiRenderer;
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

//#if MC>=10800
import static net.minecraft.client.renderer.GlStateManager.*;
//#else
//$$ import net.minecraft.client.renderer.OpenGlHelper;
//$$ import static org.lwjgl.opengl.GL11.*;
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
        //#if MC>=10800
        Tessellator tessellator = Tessellator.getInstance();
        //#else
        //$$ Tessellator tessellator = Tessellator.instance;
        //#endif
        //#if MC>=11200
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        //#else
        //#if MC>=10904
        //$$ VertexBuffer vertexBuffer = tessellator.getBuffer();
        //#else
        //#if MC>=10800
        //$$ WorldRenderer vertexBuffer = tessellator.getWorldRenderer();
        //#else
        //$$ Tessellator vertexBuffer = tessellator;
        //#endif
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

    /**
     * Inverts all colors on the screen.
     * @param guiRenderer The GUI Renderer
     * @param right Right border of the inverted rectangle
     * @param bottom Bottom border of the inverted rectangle
     * @param left Left border of the inverted rectangle
     * @param top Top border of the inverted rectangle
     */
    public static void invertColors(GuiRenderer guiRenderer, int right, int bottom, int left, int top) {
        if (left >= right || top >= bottom) return;

        int x = guiRenderer.getOpenGlOffset().getX();
        int y = guiRenderer.getOpenGlOffset().getY();
        right += x;
        left += x;
        bottom += y;
        top += y;

        color(0, 0, 255, 255);
        disableTexture2D();
        enableColorLogic();
        colorLogicOp(GL11.GL_OR_REVERSE);

        MCVer.drawRect(right, bottom, left, top);

        disableColorLogic();
        enableTexture2D();
        color(255, 255, 255, 255);
    }

    public static void drawRect(int x, int y, int width, int height, ReadableColor tl, ReadableColor tr, ReadableColor bl, ReadableColor br) {
        //#if MC>=10800
        Tessellator tessellator = Tessellator.getInstance();
        //#else
        //$$ Tessellator tessellator = Tessellator.instance;
        //#endif
        //#if MC>=11200
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        //#else
        //#if MC>=10904
        //$$ VertexBuffer vertexBuffer = tessellator.getBuffer();
        //#else
        //#if MC>=10800
        //$$ WorldRenderer vertexBuffer = tessellator.getWorldRenderer();
        //#else
        //$$ Tessellator vertexBuffer = tessellator;
        //#endif
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

    //#if MC<=10710
    //$$ public static void color(float r, float g, float b) { GL11.glColor3f(r, g, b); }
    //$$ public static void color(float r, float g, float b, float a) { GL11.glColor4f(r, g, b, a); }
    //$$ public static void enableBlend() { GL11.glEnable(GL_BLEND); }
    //$$ public static void enableTexture2D() { GL11.glEnable(GL_TEXTURE_2D); }
    //$$ public static void enableAlpha() { GL11.glEnable(GL_ALPHA_TEST); }
    //$$ public static void disableTexture2D() { GL11.glDisable(GL_TEXTURE_2D); }
    //$$ public static void disableAlpha() { GL11.glDisable(GL_ALPHA_TEST); }
    //$$ public static void blendFunc(int s, int d) { GL11.glBlendFunc(s, d); }
    //$$ public static void tryBlendFuncSeparate(int l, int r, int vl, int vr) { OpenGlHelper.glBlendFunc(l, r, vl, vr); }
    //$$ public static void shadeModel(int mode) { GL11.glShadeModel(mode); }
    //$$ public static void enableColorLogic() { GL11.glEnable(GL_COLOR_LOGIC_OP); }
    //$$ public static void disableColorLogic() { GL11.glDisable(GL_COLOR_LOGIC_OP); }
    //$$ public static void colorLogicOp(int op) { GL11.glLogicOp(op); }
    //#endif
}
