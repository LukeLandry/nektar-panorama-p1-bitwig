package com.honeyflamemusic;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

public class ControllerBindings {

    private ControllerHardware hardware;
    private ControllerHost host;
    private ControllerState state;

    public ControllerBindings(ControllerHost host, ControllerHardware hardware, ControllerState state) {
        this.host = host;
        this.hardware = hardware;
        this.state = state;
        midiOut = host.getMidiOutPort(0);
    }

    private MidiOut midiOut;
    private TrackBank trackBank;
    private CursorTrack cursorTrack;
    private CursorDevice cursorDevice;
    private CursorRemoteControlsPage cursorRemoteControlsPage;
    private Transport transport;
    private Application application;

    public void bindControls() {

        trackBank = host.createTrackBank(8, 8, 0);
        for (int i = 0; i < 8; i++) {
            final int trackNumber = i;
            Track t = trackBank.getItemAt(i);
            t.volume().addBinding(hardware.getFader(i));
            t.volume().value().addValueObserver(hardware.createDoubleValueChangedCallback(0, ControllerHardware.FADER + i));
            t.volume().displayedValue().addValueObserver(s -> state.setVolumes(trackNumber, s));
            t.pan().addBinding(hardware.getPanEncoders().get(i));
            t.pan().value().addValueObserver(hardware.createDoubleValueChangedCallback(0, ControllerHardware.PAN_ENCODER + i));
            t.pan().displayedValue().addValueObserver(s -> state.setPans(trackNumber, s));
            t.arm().addBinding(hardware.getSelectButton(i).pressedAction());
            t.arm().addValueObserver(hardware.createBooleanValueChangedCallback(0, ControllerHardware.SELECT_BUTTON + i));
            t.name().addValueObserver(s -> state.setTrackName(trackNumber, s));
        }


        state.addControllerLayoutModeListener(this::refreshHardware);

        cursorTrack = host.createCursorTrack(0, 0);
        cursorTrack.selectNextAction().addBinding(hardware.getTrackRightButton().pressedAction());
        cursorTrack.selectPreviousAction().addBinding(hardware.getTrackLeftButton().pressedAction());
        cursorTrack.name().addValueObserver(state::setCurrentTrackName);

        cursorDevice = cursorTrack.createCursorDevice();
        cursorDevice.selectNextAction().addBinding(hardware.getPatchRightButton().pressedAction());
        cursorDevice.selectPreviousAction().addBinding(hardware.getPatchLeftButton().pressedAction());
        cursorDevice.name().addValueObserver(state::setCurrentDeviceName);

        cursorRemoteControlsPage = cursorDevice.createCursorRemoteControlsPage(8);
        cursorRemoteControlsPage.pageNames().addValueObserver(s->state.setRemotePageNames(Arrays.asList(s)));
        cursorRemoteControlsPage.selectedPageIndex().addValueObserver(s->{
            state.setCurrentPageIndex(s);
        });

        for (int i = 0; i < 8; i++) {
            int remoteNumber = i;
            cursorRemoteControlsPage.getParameter(i).addBinding(hardware.getParamEncoders().get(i));
            cursorRemoteControlsPage.getParameter(i).value().addValueObserver(hardware.createDoubleValueChangedCallback(0, ControllerHardware.PARAM_ENCODER + i));
            cursorRemoteControlsPage.getParameter(i).name().addValueObserver(s -> state.setParameterName(remoteNumber, s));
            cursorRemoteControlsPage.getParameter(i).displayedValue().addValueObserver(s -> state.setParameterValues(remoteNumber, s));
        }

        host.createMasterTrack(0).volume().addBinding(hardware.getMasterFader());
        host.createMasterTrack(0).volume().value().addValueObserver(hardware.createDoubleValueChangedCallback(0, ControllerHardware.MASTER_FADER));

        transport = host.createTransport();

        transport.playAction().addBinding(hardware.getPlayButton().pressedAction());
        transport.stopAction().addBinding(hardware.getStopButton().pressedAction());
        transport.isPlaying().addValueObserver(hardware.createBooleanValueChangedCallback(0, ControllerHardware.PLAY_BUTTON));
        transport.recordAction().addBinding(hardware.getStopButton().pressedAction());
        transport.rewindAction().addBinding(hardware.getRewindButton().pressedAction());
        transport.fastForwardAction().addBinding(hardware.getForwardButton().pressedAction());

        transport.isArrangerRecordEnabled().addBinding(hardware.getRecordButton().pressedAction());
        transport.isArrangerRecordEnabled().addValueObserver(hardware.createBooleanValueChangedCallback(0, ControllerHardware.RECORD_BUTTON));
        transport.isArrangerLoopEnabled().addBinding(hardware.getLoopButton().pressedAction());
        transport.isArrangerLoopEnabled().addValueObserver(hardware.createBooleanValueChangedCallback(0, ControllerHardware.LOOP_BUTTON));
        transport.isMetronomeEnabled().addBinding(hardware.getClickButton().pressedAction());
        transport.jumpToPreviousCueMarkerAction().addBinding(hardware.getPageLeftButton().pressedAction());
        transport.jumpToNextCueMarkerAction().addBinding(hardware.getPageRightButton().pressedAction());

        application = host.createApplication();
        application.undoAction().addBinding(hardware.getUndoButton().pressedAction());

        state.addControllerLayoutModeListener(hardware.createControllerLayoutModeCallback());


        host.createAction(() -> state.setControllerLayoutMode(ControllerLayoutMode.MIXER), () -> "set mixer layout").addBinding(hardware.getMixerButton().pressedAction());
        host.createAction(() -> state.setControllerLayoutMode(ControllerLayoutMode.CONTROLS), () -> "set controls layout").addBinding(hardware.getInstrumentButton().pressedAction());
//        host.createAction(() -> state.setControllerLayoutMode(ControllerLayoutMode.TRANSPORT), () -> "set transport layout").addBinding(hardware.getTransportButton().pressedAction());

        host.createAction(state::refresh, () -> "refresh").addBinding(hardware.getFkeysButton().releasedAction());


        host.createAction(() -> cursorRemoteControlsPage.selectedPageIndex().set(0), () -> "set page 0").addBinding(hardware.getActionButton(0).pressedAction());
        host.createAction(() -> cursorRemoteControlsPage.selectedPageIndex().set(1), () -> "set page 1").addBinding(hardware.getActionButton(1).pressedAction());
        host.createAction(() -> cursorRemoteControlsPage.selectedPageIndex().set(2), () -> "set page 2").addBinding(hardware.getActionButton(2).pressedAction());
        host.createAction(() -> cursorRemoteControlsPage.selectedPageIndex().set(3), () -> "set page 3").addBinding(hardware.getActionButton(3).pressedAction());
        host.createAction(state::openRemotePagesMenu, () -> "open remotes menu").addBinding(hardware.getActionButton(4).pressedAction());

    }

    private void refreshHardware(ControllerLayoutMode controllerLayoutMode) {
        try {
            MidiOut midiOut = host.getMidiOutPort(0);

            if (controllerLayoutMode == ControllerLayoutMode.MIXER) {

                for (int i = 0; i < 8; i++) {
                    if (i < trackBank.getSizeOfBank()) {
                        midiOut.sendMidi(ShortMidiMessage.CONTROL_CHANGE, ControllerHardware.FADER + i, doubleToMidiData(trackBank.getItemAt(i).volume().get()));
                        midiOut.sendMidi(ShortMidiMessage.CONTROL_CHANGE, ControllerHardware.PAN_ENCODER + i, doubleToMidiData(trackBank.getItemAt(i).pan().get()));
                        midiOut.sendMidi(ShortMidiMessage.CONTROL_CHANGE, ControllerHardware.SELECT_BUTTON + i, trackBank.getItemAt(i).arm().get() ? 127 : 0);
                    }
                }

            }

//            int pageSelected = cursorRemoteControlsPage.selectedPageIndex().get();
//            for (int i = 0; i < 4; i++) {
//                midiOut.sendMidi(ShortMidiMessage.CONTROL_CHANGE, ControllerHardware.ACTION_BUTTON + i, i == pageSelected ? 127 : 0);
//            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            host.println(sw.toString());
        }

    }

    private int doubleToMidiData(double d) {
        return (int)Math.round (d * 127.0);
    }


}
