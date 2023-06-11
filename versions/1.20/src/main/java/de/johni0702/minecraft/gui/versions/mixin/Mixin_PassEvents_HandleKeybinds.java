package de.johni0702.minecraft.gui.versions.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.johni0702.minecraft.gui.versions.ScreenExt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(MinecraftClient.class)
public class Mixin_PassEvents_HandleKeybinds {
    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"))
    )
    private Screen doesScreenPassEvents(Screen screen) {
        if (screen instanceof ScreenExt ext && ext.doesPassEvents()) {
            screen = null;
        }
        return screen;
    }
}
