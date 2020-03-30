package de.johni0702.minecraft.gui.versions;

import de.johni0702.minecraft.gui.GuiRenderer;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.crash.CrashReportSection;
import org.lwjgl.opengl.GL11;

//#if FABRIC>=1
//#else
//$$ import net.minecraftforge.client.event.GuiScreenEvent;
//$$ import net.minecraftforge.client.event.RenderGameOverlayEvent;
//#endif

//#if MC>=11400
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
//#else
//$$ import net.minecraft.client.gui.GuiScreen;
//$$ import net.minecraft.client.gui.ScaledResolution;
//#endif

//#if MC>=10809
import net.minecraft.client.render.VertexFormats;
//#endif

//#if MC>=10800
import static com.mojang.blaze3d.platform.GlStateManager.*;
//#else
//$$ import net.minecraft.client.renderer.OpenGlHelper;
//$$ import static org.lwjgl.opengl.GL11.*;
//#endif

import java.util.concurrent.Callable;

/**
 * Abstraction over things that have changed between different MC versions.
 */
public class MCVer {
    public static MinecraftClient getMinecraft() {
        return MinecraftClient.getInstance();
    }

    //#if MC>=11400
    public static Window newScaledResolution(MinecraftClient mc) {
        //#if MC>=11500
        //$$ return mc.getWindow();
        //#else
        return mc.window;
        //#endif
    }
    //#else
    //$$ public static ScaledResolution newScaledResolution(Minecraft mc) {
        //#if MC>=10809
        //$$ return new ScaledResolution(mc);
        //#else
        //$$ return new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        //#endif
    //$$ }
    //#endif

    public static void addDetail(CrashReportSection category, String name, Callable<String> callable) {
        //#if MC>=10904
        //#if MC>=11200
        category.add(name, callable::call);
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
        //#if MC>=10904
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        //#else
        //#if MC>=10800
        //$$ WorldRenderer vertexBuffer = tessellator.getWorldRenderer();
        //#else
        //$$ Tessellator vertexBuffer = tessellator;
        //#endif
        //#endif
        //#if MC>=10809
        vertexBuffer.begin(GL11.GL_QUADS, VertexFormats.POSITION);
        vertexBuffer.vertex(right, top, 0).next();
        vertexBuffer.vertex(left, top, 0).next();
        vertexBuffer.vertex(left, bottom, 0).next();
        vertexBuffer.vertex(right, bottom, 0).next();
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

        color4f(0, 0, 255, 255);
        disableTexture();
        enableColorLogicOp();
        logicOp(GL11.GL_OR_REVERSE);

        MCVer.drawRect(right, bottom, left, top);

        disableColorLogicOp();
        enableTexture();
        color4f(255, 255, 255, 255);
    }

    public static void drawRect(int x, int y, int width, int height, ReadableColor tl, ReadableColor tr, ReadableColor bl, ReadableColor br) {
        //#if MC>=10800
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        //#else
        //$$ Tessellator tessellator = Tessellator.instance;
        //$$ Tessellator vertexBuffer = tessellator;
        //#endif
        //#if MC>=10809
        vertexBuffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
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
    }

    public static TextRenderer getFontRenderer() {
        return getMinecraft().textRenderer;
    }

    //#if FABRIC<=0
    //$$ public static RenderGameOverlayEvent.ElementType getType(RenderGameOverlayEvent.Post event) {
        //#if MC>=10904
        //$$ return event.getType();
        //#else
        //$$ return event.type;
        //#endif
    //$$ }
    //$$
    //$$ public static float getPartialTicks(RenderGameOverlayEvent.Post event) {
        //#if MC>=10904
        //$$ return event.getPartialTicks();
        //#else
        //$$ return event.partialTicks;
        //#endif
    //$$ }
    //$$
    //$$ public static float getPartialTicks(GuiScreenEvent.DrawScreenEvent.Post event) {
        //#if MC>=10904
        //$$ return event.getRenderPartialTicks();
        //#else
        //$$ return event.renderPartialTicks;
        //#endif
    //$$ }
    //$$
    //$$ public static int getMouseX(GuiScreenEvent.DrawScreenEvent.Post event) {
        //#if MC>=10904
        //$$ return event.getMouseX();
        //#else
        //$$ return event.mouseX;
        //#endif
    //$$ }
    //$$
    //$$ public static int getMouseY(GuiScreenEvent.DrawScreenEvent.Post event) {
        //#if MC>=10904
        //$$ return event.getMouseY();
        //#else
        //$$ return event.mouseY;
        //#endif
    //$$ }
    //#endif

    public static void setClipboardString(String text) {
        //#if MC>=11400
        getMinecraft().keyboard.setClipboard(text);
        //#else
        //$$ GuiScreen.setClipboardString(text);
        //#endif
    }

    public static String getClipboardString() {
        //#if MC>=11400
        return getMinecraft().keyboard.getClipboard();
        //#else
        //$$ return GuiScreen.getClipboardString();
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

    //#if MC>=11400
    public static abstract class Keyboard {
        public static final int KEY_ESCAPE = GLFW.GLFW_KEY_ESCAPE;
        public static final int KEY_HOME = GLFW.GLFW_KEY_HOME;
        public static final int KEY_END = GLFW.GLFW_KEY_END;
        public static final int KEY_UP = GLFW.GLFW_KEY_UP;
        public static final int KEY_DOWN = GLFW.GLFW_KEY_DOWN;
        public static final int KEY_LEFT = GLFW.GLFW_KEY_LEFT;
        public static final int KEY_RIGHT = GLFW.GLFW_KEY_RIGHT;
        public static final int KEY_BACK = GLFW.GLFW_KEY_BACKSPACE;
        public static final int KEY_DELETE = GLFW.GLFW_KEY_DELETE;
        public static final int KEY_RETURN = GLFW.GLFW_KEY_ENTER;
        public static final int KEY_TAB = GLFW.GLFW_KEY_TAB;
        public static final int KEY_A = GLFW.GLFW_KEY_A;
        public static final int KEY_C = GLFW.GLFW_KEY_C;
        public static final int KEY_V = GLFW.GLFW_KEY_V;
        public static final int KEY_X = GLFW.GLFW_KEY_X;

        public static void enableRepeatEvents(boolean enabled) {
            getMinecraft().keyboard.enableRepeatEvents(enabled);
        }
    }
    //#endif
}
