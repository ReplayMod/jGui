package de.johni0702.minecraft.gui.versions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReportSection;
import org.lwjgl.opengl.GL11;

//#if MC>=11900
//#else
import net.minecraft.text.LiteralText;
//#endif

//#if MC>=11700
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#else
//#endif

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

//#if MC<10800
//$$ import net.minecraft.client.renderer.OpenGlHelper;
//$$ import static org.lwjgl.opengl.GL11.*;
//#endif

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Abstraction over things that have changed between different MC versions.
 */
public class MCVer {
    public static MinecraftClient getMinecraft() {
        return MinecraftClient.getInstance();
    }

    private static class ScissorBounds {
        private static final ScissorBounds DISABLED = new ScissorBounds(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
        private final int x;
        private final int y;
        private final int width;
        private final int height;

        private ScissorBounds(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScissorBounds that = (ScissorBounds) o;
            return x == that.x && y == that.y && width == that.width && height == that.height;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, width, height);
        }
    }
    private static final ArrayDeque<ScissorBounds> scissorStateStack = new ArrayDeque<>();
    private static ScissorBounds scissorState = ScissorBounds.DISABLED;

    public static void pushScissorState() {
        scissorStateStack.push(scissorState);
    }

    public static void popScissorState() {
        setScissorBounds(scissorStateStack.pop());
    }

    public static void setScissorBounds(int x, int y, int width, int height) {
        setScissorBounds(new ScissorBounds(x, y, width, height));
    }

    public static void setScissorDisabled() {
        setScissorBounds(ScissorBounds.DISABLED);
    }

    private static void setScissorBounds(ScissorBounds newState) {
        ScissorBounds oldState = MCVer.scissorState;
        if (Objects.equals(oldState, newState)) {
            return;
        }

        scissorState = newState;

        boolean isEnabled = newState != ScissorBounds.DISABLED;
        boolean wasEnabled = oldState != ScissorBounds.DISABLED;

        if (isEnabled) {
            if (!wasEnabled) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
            }
            GL11.glScissor(scissorState.x, scissorState.y, scissorState.width, scissorState.height);
        } else {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    //#if MC>=11400
    public static Window newScaledResolution(MinecraftClient mc) {
        //#if MC>=11500
        return mc.getWindow();
        //#else
        //$$ return mc.window;
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

    //#if MC<12105
    public static void bindTexture(Identifier identifier) {
        //#if MC>=12105
        //$$ RenderSystem.setShaderTexture(0, getMinecraft().getTextureManager().getTexture(identifier).getGlTexture());
        //#elseif MC>=11700
        //$$ RenderSystem.setShaderTexture(0, identifier);
        //#elseif MC>=11500
        getMinecraft().getTextureManager().bindTexture(identifier);
        //#else
        //$$ getMinecraft().getTextureManager().bindTexture(identifier);
        //#endif
    }
    //#endif

    public static TextRenderer getFontRenderer() {
        return getMinecraft().textRenderer;
    }

    //#if FABRIC<=0
    //$$ public static RenderGameOverlayEvent.ElementType getType(RenderGameOverlayEvent event) {
        //#if MC>=10904
        //$$ return event.getType();
        //#else
        //$$ return event.type;
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

    public static Text literalText(String str) {
        //#if MC>=11900
        //$$ return Text.literal(str);
        //#else
        return new LiteralText(str);
        //#endif
    }

    public static Identifier identifier(String id) {
        //#if MC>=12100
        //$$ return Identifier.of(id);
        //#else
        return new Identifier(id);
        //#endif
    }

    public static Identifier identifier(String namespace, String path) {
        //#if MC>=12100
        //$$ return Identifier.of(namespace, path);
        //#else
        return new Identifier(namespace, path);
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
            //#if MC>=11903
            //$$ // These are now always enabled and we no longer need to manually toggle it when opening screens
            //#else
            getMinecraft().keyboard.setRepeatEvents(enabled);
            //#endif
        }
    }
    //#endif
}
