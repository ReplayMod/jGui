//#if MC>=11300
package de.johni0702.minecraft.gui.versions.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PreTickCallback {
    Event<PreTickCallback> EVENT = EventFactory.createArrayBacked(
            PreTickCallback.class,
            (listeners) -> () -> {
                for (PreTickCallback listener : listeners) {
                    listener.preTick();
                }
            }
    );

    void preTick();
}
//#endif
