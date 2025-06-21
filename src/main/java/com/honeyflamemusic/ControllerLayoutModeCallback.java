package com.honeyflamemusic;

import com.bitwig.extension.callback.ValueChangedCallback;

public interface ControllerLayoutModeCallback extends ValueChangedCallback {
    void valueChanged(ControllerLayoutMode newValue);
}
