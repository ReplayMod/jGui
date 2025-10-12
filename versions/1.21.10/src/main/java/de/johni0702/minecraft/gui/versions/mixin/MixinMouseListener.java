package de.johni0702.minecraft.gui.versions.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.johni0702.minecraft.gui.versions.callbacks.MouseCallback;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mouse.class)
public abstract class MixinMouseListener {
    @WrapOperation(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(Lnet/minecraft/client/gui/Click;Z)Z"))
    private boolean mouseDown(Screen screen, Click click, boolean doubled, Operation<Boolean> original) {
        if (MouseCallback.EVENT.invoker().mouseDown(new de.johni0702.minecraft.gui.function.Click(click))) {
            return true;
        }
        return original.call(screen, click, doubled);
    }

    @WrapOperation(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(Lnet/minecraft/client/gui/Click;)Z"))
    private boolean mouseUp(Screen screen, Click click, Operation<Boolean> original) {
        if (MouseCallback.EVENT.invoker().mouseUp(new de.johni0702.minecraft.gui.function.Click(click))) {
            return true;
        }
        return original.call(screen, click);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseDragged(Lnet/minecraft/client/gui/Click;DD)Z"))
    private boolean mouseDrag(Screen screen, Click click, double dx, double dy, Operation<Boolean> original) {
        if (MouseCallback.EVENT.invoker().mouseDrag(new de.johni0702.minecraft.gui.function.Click(click), dx, dy)) {
            return true;
        }
        return original.call(screen, click, dx, dy);
    }

    @WrapOperation(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDDD)Z"))
    private boolean mouseScroll(Screen screen, double x, double y, double horizontal, double vertical, Operation<Boolean> original) {
        if (MouseCallback.EVENT.invoker().mouseScroll(x, y, horizontal, vertical)) {
            return true;
        }
        return original.call(screen, x, y, horizontal, vertical);
    }
}
