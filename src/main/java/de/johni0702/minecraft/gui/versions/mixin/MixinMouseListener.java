//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.mixin;

import de.johni0702.minecraft.gui.function.Click;
import de.johni0702.minecraft.gui.versions.callbacks.MouseCallback;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC>=11700
//#else
import net.minecraft.client.gui.Element;
//#endif

@Mixin(Mouse.class)
public abstract class MixinMouseListener {
    @Accessor // Note: for some reason Mixin doesn't include this in the refmap json if it's just a @Shadow field
    abstract int getActiveButton();

    @Inject(method = "method_1611", at = @At("HEAD"), cancellable = true)
    //#if MC>=11700
    //$$ private static void mouseDown(boolean[] result, Screen screen, double x, double y, int button, CallbackInfo ci) {
    //#else
    private void mouseDown(boolean[] result, double x, double y, int button, CallbackInfo ci) {
    //#endif
        if (MouseCallback.EVENT.invoker().mouseDown(new Click(x, y, button))) {
            result[0] = true;
            ci.cancel();
        }
    }

    @Inject(method = "method_1605", at = @At("HEAD"), cancellable = true)
    //#if MC>=11700
    //$$ private static void mouseUp(boolean[] result, Screen screen, double x, double y, int button, CallbackInfo ci) {
    //#else
    private void mouseUp(boolean[] result, double x, double y, int button, CallbackInfo ci) {
    //#endif
        if (MouseCallback.EVENT.invoker().mouseUp(new Click(x, y, button))) {
            result[0] = true;
            ci.cancel();
        }
    }

    //#if MC>=12005
    //$$ @Inject(method = "method_55795", at = @At("HEAD"), cancellable = true)
    //#else
    @Inject(method = "method_1602", at = @At("HEAD"), cancellable = true)
    //#endif
    //#if MC>=11700
    //$$ private void mouseDrag(Screen screen, double x, double y, double dx, double dy, CallbackInfo ci) {
    //#else
    private void mouseDrag(Element element, double x, double y, double dx, double dy, CallbackInfo ci) {
    //#endif
        if (MouseCallback.EVENT.invoker().mouseDrag(new Click(x, y, getActiveButton()), dx, dy)) {
            ci.cancel();
        }
    }

    @Redirect(
            method = "onMouseScroll",
            at = @At(value = "INVOKE",
                    //#if MC>=12002
                    //$$ target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDDD)Z"
                    //#else
                    target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z"
                    //#endif
            )
    )
    private boolean mouseScroll(Screen element, double x, double y,
                                //#if MC>=12002
                                //$$ double horizontal,
                                //#endif
                                double vertical
    ) {
        //#if MC<12002
        double horizontal = 0;
        //#endif
        if (MouseCallback.EVENT.invoker().mouseScroll(x, y, horizontal, vertical)) {
            return true;
        } else {
            //#if MC>=12002
            //$$ return element.mouseScrolled(x, y, horizontal, vertical);
            //#else
            return element.mouseScrolled(x, y, vertical);
            //#endif
        }
    }
}
//#endif
