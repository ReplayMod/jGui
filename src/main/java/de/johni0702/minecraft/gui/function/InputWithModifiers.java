package de.johni0702.minecraft.gui.function;

import net.minecraft.util.Util;

//#if MC>=12109
//#else
import net.minecraft.client.gui.screen.Screen;
//#endif

//#if MC>=11300
import org.lwjgl.glfw.GLFW;
//#endif

public interface InputWithModifiers {
    int modifiers();

    default boolean hasCtrl() {
        return (modifiers() & _CTRL_MOD) != 0;
    }
    default boolean hasShift() {
        return (modifiers() & _SHIFT_MOD) != 0;
    }
    default boolean hasAlt() {
        return (modifiers() & _ALT_MOD) != 0;
    }

    //#if MC>=11300
    int _CTRL_MOD = Util.getOperatingSystem() == Util.OperatingSystem.OSX ? GLFW.GLFW_MOD_SUPER : GLFW.GLFW_MOD_CONTROL;
    int _SHIFT_MOD = GLFW.GLFW_MOD_SHIFT;
    int _ALT_MOD = GLFW.GLFW_MOD_ALT;
    //#else
    //$$ int _CTRL_MOD = 1;
    //$$ int _SHIFT_MOD = 2;
    //$$ int _ALT_MOD = 4;
    //#endif

    //#if MC>=12109
    //$$ // Current modifiers are now always passed with events. Global getters are gone.
    //#else
    static int currentModifiers() {
        int ctrl = Screen.hasControlDown() ? _CTRL_MOD : 0;
        int shift = Screen.hasShiftDown() ? _SHIFT_MOD : 0;
        int alt = Screen.hasAltDown() ? _ALT_MOD : 0;
        return ctrl | shift | alt;
    }
    //#endif
}
