package com.honeyflamemusic;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.BooleanValueChangedCallback;
import com.bitwig.extension.callback.DoubleValueChangedCallback;
import com.bitwig.extension.callback.IntegerValueChangedCallback;
import com.bitwig.extension.controller.api.*;

import java.util.ArrayList;
import java.util.List;

public class ControllerHardware {

    public static final int PARAM_ENCODER = 64;
    public static final int PAN_ENCODER = 48;
    public static final int FADER = 0;
    public static final int SELECT_BUTTON = 16;
    public static final int MASTER_FADER = 14;
    public static final int TOGGLE_MUTE = 97;
    public static final int TOGGLE_VIEW = 98;
    public static final int DATA_ENCODER = 111;
    public static final int ACTION_BUTTON = 106;
    public static final int MIXER_BUTTON = 99;
    public static final int INSTRUMENT_BUTTON = 100;
    public static final int TRANSPORT_BUTTON = 101;
    public static final int INTERNAL_BUTTON = 102;
    public static final int SHIFT_BUTTON = 96;
    public static final int TRACK_LEFT = 91;
    public static final int TRACK_RIGHT = 92;
    public static final int PATCH_LEFT = 93;
    public static final int PATCH_RIGHT = 94;
    public static final int VIEW_BUTTON = 95;
    public static final int FKEYS_BUTTON = 103;
    public static final int PAGE_LEFT = 86;
    public static final int PAGE_RIGHT = 87;
    public static final int UNDO_BUTTON = 88;
    public static final int CLICK_BUTTON = 89;
    public static final int MODE_BUTTON = 90;
    public static final int LOOP_BUTTON = 80;
    public static final int REWIND = 81;
    public static final int FORWARD = 82;
    public static final int STOP_BUTTON = 83;
    public static final int PLAY_BUTTON = 84;
    public static final int RECORD_BUTTON = 85;
    private ControllerHost host;
    private HardwareSurface surface;
    private MidiIn midiIn;
    private MidiOut midiOut;

    private List<HardwareSlider> faders = new ArrayList<>();

    private List<RelativeHardwareKnob> paramEncoders = new ArrayList<>();
    private List<RelativeHardwareKnob> panEncoders = new ArrayList<>();
    private HardwareSlider masterFader;

    private List<HardwareButton> selectButtons = new ArrayList<>();
    private HardwareButton toggleViewButton;
    private HardwareButton toggleMuteButton;
    private HardwareButton mixerButton;
    private HardwareButton instrumentButton;
    private HardwareButton transportButton;
    private HardwareButton internalButton;

    private RelativeHardwareKnob dataEncoder;
    private List<HardwareButton> actionButtons = new ArrayList<>();

    private HardwareButton shiftButton;
    private HardwareButton trackLeftButton;
    private HardwareButton trackRightButton;
    private HardwareButton patchLeftButton;
    private HardwareButton patchRightButton;
    private HardwareButton viewButton;

    private HardwareButton fkeysButton;
    private HardwareButton pageLeftButton;
    private HardwareButton pageRightButton;
    private HardwareButton undoButton;
    private HardwareButton clickButton;
    private HardwareButton modeButton;

    private HardwareButton loopButton;
    private HardwareButton rewindButton;
    private HardwareButton forwardButton;
    private HardwareButton stopButton;
    private HardwareButton playButton;
    private HardwareButton recordButton;

    public ControllerHardware(ControllerHost host) {
        this.host = host;
        this.midiIn = host.getMidiInPort(0);
        this.midiOut = host.getMidiOutPort(0);


        surface = host.createHardwareSurface();

        // top encoders
        for (int i = 0; i < 8; i++) {
            paramEncoders.add(createEncoder("PARAM_ENCODER_" + i, 0, PARAM_ENCODER +i));
        }

        // mixer section
        for (int i = 0; i < 8; i++) {
            panEncoders.add(createEncoder("PAN_ENCODER_" + i, 0, PAN_ENCODER +i));
            faders.add(createSlider("FADER_" + i, 0, FADER +i));
            selectButtons.add(createPressAndReleaseButton("SELECT_BUTTON_" + i, 0, SELECT_BUTTON +i));
        }
        masterFader = createSlider("MASTER_FADER", 0, MASTER_FADER);
        toggleMuteButton = createPressAndReleaseButton("TOGGLE_MUTE", 0, TOGGLE_MUTE);
        toggleViewButton = createPressAndReleaseButton("TOGGLE_VIEW",0, TOGGLE_VIEW);

        // screen controls
        dataEncoder = createEncoder("DATA", 0, DATA_ENCODER);
        for (int i = 0; i < 5; i++) {
            actionButtons.add(createPressAndReleaseButton("ACTION_BUTTON_" + i, 0, ACTION_BUTTON +i));
        }

        // mode buttons
        mixerButton = createPressAndReleaseButton("MIXER", 0, MIXER_BUTTON);
        instrumentButton = createPressAndReleaseButton("INSTRUMENT", 0, INSTRUMENT_BUTTON);
        transportButton = createPressAndReleaseButton("TRANSPORT", 0, TRANSPORT_BUTTON);
        internalButton = createPressAndReleaseButton("INTERNAL", 0, INTERNAL_BUTTON);

        // navigation section
        shiftButton = createPressAndReleaseButton("SHIFT", 0, SHIFT_BUTTON);
        trackLeftButton = createPressAndReleaseButton("TRACK_LEFT", 0, TRACK_LEFT);
        trackRightButton = createPressAndReleaseButton("TRACK_RIGHT", 0, TRACK_RIGHT);
        patchLeftButton = createPressAndReleaseButton("PATCH_LEFT", 0, PATCH_LEFT);
        patchRightButton = createPressAndReleaseButton("PATCH_RIGHT", 0, PATCH_RIGHT);
        viewButton = createPressAndReleaseButton("VIEW", 0, VIEW_BUTTON);

        // transport
        fkeysButton = createPressAndReleaseButton("FKEYS", 0, FKEYS_BUTTON);
        pageLeftButton = createPressAndReleaseButton("PAGE_LEFT", 0, PAGE_LEFT);
        pageRightButton = createPressAndReleaseButton("PAGE_RIGHT", 0, PAGE_RIGHT);
        undoButton = createPressAndReleaseButton("UNDO", 0, UNDO_BUTTON);
        clickButton = createPressAndReleaseButton("CLICK", 0, CLICK_BUTTON);
        modeButton = createPressAndReleaseButton("MODE", 0, MODE_BUTTON);
        loopButton = createPressAndReleaseButton("LOOP", 0, LOOP_BUTTON);
        rewindButton = createPressAndReleaseButton("REWIND", 0, REWIND);
        forwardButton = createPressAndReleaseButton("FORWARD", 0, FORWARD);
        stopButton = createPressAndReleaseButton("STOP", 0, STOP_BUTTON);
        playButton = createPressAndReleaseButton("PLAY", 0, PLAY_BUTTON);
        recordButton = createPressAndReleaseButton("RECORD", 0, RECORD_BUTTON);

    }

    public List<HardwareSlider> getFaders() {
        return faders;
    }

    public HardwareSlider getFader(int i) {
        return faders.size() > i ? faders.get(i) : null;
    }

    public List<RelativeHardwareKnob> getParamEncoders() {
        return paramEncoders;
    }

    public RelativeHardwareKnob getParamEncoder(int i) {
        return paramEncoders.size() > i ? paramEncoders.get(i) : null;
    }

    public List<RelativeHardwareKnob> getPanEncoders() {
        return panEncoders;
    }

    public RelativeHardwareKnob getPanEncoder(int i) {
        return panEncoders.size() > i ? panEncoders.get(i) : null;
    }

    public List<HardwareButton> getSelectButtons() {
        return selectButtons;
    }

    public HardwareButton getSelectButton(int i) {
        return selectButtons.size() > i ? selectButtons.get(i) : null;
    }

    public HardwareSlider getMasterFader() {
        return masterFader;
    }

    public HardwareButton getToggleViewButton() {
        return toggleViewButton;
    }

    public HardwareButton getToggleMuteButton() {
        return toggleMuteButton;
    }

    public HardwareButton getMixerButton() {
        return mixerButton;
    }

    public HardwareButton getInstrumentButton() {
        return instrumentButton;
    }

    public HardwareButton getTransportButton() {
        return transportButton;
    }

    public HardwareButton getInternalButton() {
        return internalButton;
    }

    public RelativeHardwareKnob getDataEncoder() {
        return dataEncoder;
    }

    public List<HardwareButton> getActionButtons() {
        return actionButtons;
    }

    public HardwareButton getActionButton(int i) {
        return actionButtons.size() > i ? actionButtons.get(i) : null;
    }

    public HardwareButton getShiftButton() {
        return shiftButton;
    }

    public HardwareButton getTrackLeftButton() {
        return trackLeftButton;
    }

    public HardwareButton getTrackRightButton() {
        return trackRightButton;
    }

    public HardwareButton getPatchLeftButton() {
        return patchLeftButton;
    }

    public HardwareButton getPatchRightButton() {
        return patchRightButton;
    }

    public HardwareButton getViewButton() {
        return viewButton;
    }

    public HardwareButton getFkeysButton() {
        return fkeysButton;
    }

    public HardwareButton getPageLeftButton() {
        return pageLeftButton;
    }

    public HardwareButton getPageRightButton() {
        return pageRightButton;
    }

    public HardwareButton getUndoButton() {
        return undoButton;
    }

    public HardwareButton getClickButton() {
        return clickButton;
    }

    public HardwareButton getModeButton() {
        return modeButton;
    }

    public HardwareButton getLoopButton() {
        return loopButton;
    }

    public HardwareButton getRewindButton() {
        return rewindButton;
    }

    public HardwareButton getForwardButton() {
        return forwardButton;
    }

    public HardwareButton getStopButton() {
        return stopButton;
    }

    public HardwareButton getPlayButton() {
        return playButton;
    }

    public HardwareButton getRecordButton() {
        return recordButton;
    }

    private HardwareButton createPressAndReleaseButton(String id, int channel, int controlNumber) {
        HardwareButton button = surface.createHardwareButton(id);
        button.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(channel, controlNumber, 127));
        button.releasedAction().setActionMatcher(midiIn.createCCActionMatcher(channel, controlNumber, 0));
        return button;
    }

    private RelativeHardwareKnob createEncoder(String id, int channel, int controlNumber) {
        RelativeHardwareKnob knob = surface.createRelativeHardwareKnob(id);
        knob.setAdjustValueMatcher(midiIn.createRelative2sComplementCCValueMatcher(channel, controlNumber, 64));
        return knob;
    }

    private HardwareSlider createSlider(String id, int channel, int controlNumber) {
        HardwareSlider slider = surface.createHardwareSlider(id);
        slider.setAdjustValueMatcher(midiIn.createAbsoluteCCValueMatcher(channel, controlNumber));
        return slider;
    }

    public DoubleValueChangedCallback createDoubleValueChangedCallback(int channel, int controlNumber) {
        return value -> midiOut.sendMidi(ShortMidiMessage.CONTROL_CHANGE + channel, controlNumber, doubleToMidiData(value));
    }

    public BooleanValueChangedCallback createBooleanValueChangedCallback(int channel, int controlNumber) {
        return value -> midiOut.sendMidi(ShortMidiMessage.CONTROL_CHANGE + channel, controlNumber, value ? 127 : 0);
    }

    public ControllerLayoutModeCallback createControllerLayoutModeCallback() {
        return this::controllerLayoutModeChanged;
    }

    private void controllerLayoutModeChanged(ControllerLayoutMode mode) {
        midiOut.sendMidi(ShortMidiMessage.CONTROL_CHANGE, switch(mode) {
            case MIXER -> MIXER_BUTTON;
            case CONTROLS -> INSTRUMENT_BUTTON;
            case TRANSPORT -> TRANSPORT_BUTTON;
            default -> MIXER_BUTTON;
        }, 127);
    }

    private int doubleToMidiData(double d) {
        return (int)Math.round (d * 127.0);
    }

}
