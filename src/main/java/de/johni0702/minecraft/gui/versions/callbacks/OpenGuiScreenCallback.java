//#if MC>=11300
package de.johni0702.minecraft.gui.versions.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.GuiScreen;

public interface OpenGuiScreenCallback {
    Event<OpenGuiScreenCallback> EVENT = EventFactory.createArrayBacked(
            OpenGuiScreenCallback.class,
            (listeners) -> (screen) -> {
                for (OpenGuiScreenCallback listener : listeners) {
                    listener.openGuiScreen(screen);
                }
            }
    );

    void openGuiScreen(GuiScreen screen);
}
//#endif
