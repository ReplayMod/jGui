//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.mixin;

import de.johni0702.minecraft.gui.versions.callbacks.MouseCallback;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MixinMouseListener {
    @Accessor // Note: for some reason Mixin doesn't include this in the refmap json if it's just a @Shadow field
    abstract int getActiveButton();

    @Inject(method = "method_1611", at = @At("HEAD"), cancellable = true)
    private void mouseDown(boolean[] result, double x, double y, int button, CallbackInfo ci) {
        if (MouseCallback.EVENT.invoker().mouseDown(x, y, button)) {
            result[0] = true;
            ci.cancel();
        }
    }

    @Inject(method = "method_1605", at = @At("HEAD"), cancellable = true)
    private void mouseUp(boolean[] result, double x, double y, int button, CallbackInfo ci) {
        if (MouseCallback.EVENT.invoker().mouseUp(x, y, button)) {
            result[0] = true;
            ci.cancel();
        }
    }

    @Inject(method = "method_1602", at = @At("HEAD"), cancellable = true)
    private void mouseDrag(Element element, double x, double y, double dx, double dy, CallbackInfo ci) {
        if (MouseCallback.EVENT.invoker().mouseDrag(x, y, getActiveButton(), dx, dy)) {
            ci.cancel();
        }
    }

    @Redirect(
            method = "onMouseScroll",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z")
    )
    private boolean mouseScroll(Screen element, double x, double y, double scroll) {
        if (MouseCallback.EVENT.invoker().mouseScroll(x, y, scroll)) {
            return true;
        } else {
            return element.mouseScrolled(x, y, scroll);
        }
    }
}
//#endif
