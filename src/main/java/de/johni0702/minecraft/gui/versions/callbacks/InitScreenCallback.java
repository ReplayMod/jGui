//#if MC>=11400
//$$ package de.johni0702.minecraft.gui.versions.callbacks;
//$$
//$$ import net.minecraft.client.gui.Screen;
//$$ import net.minecraft.client.gui.widget.AbstractButtonWidget;
//$$ import net.fabricmc.fabric.api.event.Event;
//$$ import net.fabricmc.fabric.api.event.EventFactory;
//$$
//$$ import java.util.List;
//$$
//$$ public interface InitScreenCallback {
//$$     Event<InitScreenCallback> EVENT = EventFactory.createArrayBacked(InitScreenCallback.class,
//$$             (listeners) -> (screen, buttons) -> {
//$$                 for (InitScreenCallback listener : listeners) {
//$$                     listener.initScreen(screen, buttons);
//$$                 }
//$$             }
//$$     );
//$$
//$$     void initScreen(Screen screen, List<AbstractButtonWidget> buttons);
//$$ }
//#endif
