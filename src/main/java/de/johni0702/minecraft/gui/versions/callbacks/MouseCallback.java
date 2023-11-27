//#if FABRIC>=1
package de.johni0702.minecraft.gui.versions.callbacks;

import de.johni0702.minecraft.gui.utils.Event;

public interface MouseCallback {
    Event<MouseCallback> EVENT = Event.create((listeners) ->
            new MouseCallback() {
                @Override
                public boolean mouseDown(double x, double y, int button) {
                    for (MouseCallback listener : listeners) {
                        if (listener.mouseDown(x, y, button)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean mouseDrag(double x, double y, int button, double dx, double dy) {
                    for (MouseCallback listener : listeners) {
                        if (listener.mouseDrag(x, y, button, dx, dy)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean mouseUp(double x, double y, int button) {
                    for (MouseCallback listener : listeners) {
                        if (listener.mouseUp(x, y, button)) {
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

    boolean mouseDown(double x, double y, int button);
    boolean mouseDrag(double x, double y, int button, double dx, double dy);
    boolean mouseUp(double x, double y, int button);
    boolean mouseScroll(double x, double y, double horizontal, double vertical);
}
//#endif
