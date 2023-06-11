package de.johni0702.minecraft.gui.versions.mixin;

import de.johni0702.minecraft.gui.versions.ScreenExt;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;

//#if MC>=12000
//$$ import org.spongepowered.asm.mixin.Unique;
//#else
import org.spongepowered.asm.mixin.Shadow;
//#endif

@Mixin(Screen.class)
public abstract class Mixin_PassEventsExt implements ScreenExt {
    //#if MC>=12000
    //$$ @Unique
    //$$ private boolean passEvents;
    //#else
    @Shadow
    public boolean passEvents;
    //#endif

    @Override
    public boolean doesPassEvents() {
        return this.passEvents;
    }

    @Override
    public void setPassEvents(boolean passEvents) {
        this.passEvents = passEvents;
    }
}
