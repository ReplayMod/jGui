package de.johni0702.minecraft.gui.versions.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.johni0702.minecraft.gui.function.CharInput;
import de.johni0702.minecraft.gui.function.KeyInput;
import de.johni0702.minecraft.gui.versions.callbacks.KeyboardCallback;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Keyboard.class)
public class MixinKeyboardListener {
    @WrapOperation(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(Lnet/minecraft/client/input/KeyInput;)Z"))
    private boolean keyPressed(Screen screen, net.minecraft.client.input.KeyInput mcKeyInput, Operation<Boolean> original) {
        if (KeyboardCallback.EVENT.invoker().keyPressed(new KeyInput(mcKeyInput))) {
            return true;
        }
        return original.call(screen, mcKeyInput);
    }

    @WrapOperation(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyReleased(Lnet/minecraft/client/input/KeyInput;)Z"))
    private boolean keyReleased(Screen screen, net.minecraft.client.input.KeyInput mcKeyInput, Operation<Boolean> original) {
        if (KeyboardCallback.EVENT.invoker().keyReleased(new KeyInput(mcKeyInput))) {
            return true;
        }
        return original.call(screen, mcKeyInput);
    }

    @WrapOperation(method = "onChar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;charTyped(Lnet/minecraft/client/input/CharInput;)Z"))
    private boolean charTyped(Screen screen, net.minecraft.client.input.CharInput mcCharInput, Operation<Boolean> original) {
        if (KeyboardCallback.EVENT.invoker().charTyped(new CharInput(mcCharInput))) {
            return true;
        }
        return original.call(screen, mcCharInput);
    }
}
