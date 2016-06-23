package org.dangcat.install.swing.event;

import java.util.EventListener;
import java.util.EventObject;

public interface ValueChangedListener extends EventListener
{
    void onValueChanged(EventObject eventObject);
}
