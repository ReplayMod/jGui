//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.callbacks;

import de.johni0702.minecraft.gui.utils.Event;

public interface RenderHudCallback {
    Event<RenderHudCallback> EVENT = Event.create((listeners) ->
            (partialTicks) -> {
                for (RenderHudCallback listener : listeners) {
                    listener.renderHud(partialTicks);
                }
            }
    );

    void renderHud(float partialTicks);
}
//#endif
