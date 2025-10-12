//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.callbacks;

import de.johni0702.minecraft.gui.function.Click;
import de.johni0702.minecraft.gui.utils.Event;

public interface MouseCallback {
    Event<MouseCallback> EVENT = Event.create((listeners) ->
            new MouseCallback() {
                @Override
                public boolean mouseDown(Click click) {
                    for (MouseCallback listener : listeners) {
                        if (listener.mouseDown(click)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean mouseDrag(Click click, double dx, double dy) {
                    for (MouseCallback listener : listeners) {
                        if (listener.mouseDrag(click, dx, dy)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean mouseUp(Click click) {
                    for (MouseCallback listener : listeners) {
                        if (listener.mouseUp(click)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean mouseScroll(double x, double y, double horizontal, double vertical) {
                    for (MouseCallback listener : listeners) {
                        if (listener.mouseScroll(x, y, horizontal, vertical)) {
                            return true;
                        }
                    }
                    return false;
                }
            }
    );

    boolean mouseDown(Click click);
    boolean mouseDrag(Click click, double dx, double dy);
    boolean mouseUp(Click click);
    boolean mouseScroll(double x, double y, double horizontal, double vertical);
}
//#endif
