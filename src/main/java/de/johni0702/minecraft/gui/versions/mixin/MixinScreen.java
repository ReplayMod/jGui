//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.mixin;

import de.johni0702.minecraft.gui.versions.callbacks.InitScreenCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//#if MC>=11700
//$$ import com.google.common.collect.Collections2;
//$$ import net.minecraft.client.gui.Element;
//#endif

@Mixin(Screen.class)
public class MixinScreen {

    //#if MC>=11700
    //$$ @Shadow @Final private List<Element> children;
    //#else
    @Shadow
    protected @Final List<AbstractButtonWidget> buttons;
    //#endif

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("HEAD"))
    private void preInit(MinecraftClient minecraftClient_1, int int_1, int int_2, CallbackInfo ci) {
        InitScreenCallback.Pre.EVENT.invoker().preInitScreen((Screen) (Object) this);
    }

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("RETURN"))
    private void init(MinecraftClient minecraftClient_1, int int_1, int int_2, CallbackInfo ci) {
        InitScreenCallback.EVENT.invoker().initScreen(
                (Screen) (Object) this,
                //#if MC>=11700
                //$$ Collections2.transform(Collections2.filter(this.children, it -> it instanceof ClickableWidget), it -> (ClickableWidget) it)
                //#else
                buttons
                //#endif
        );
    }
}
//#endif
