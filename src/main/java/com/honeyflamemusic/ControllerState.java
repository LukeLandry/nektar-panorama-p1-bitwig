package com.honeyflamemusic;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiOut;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ControllerState {

    private SysexMessages sysexMessages = SysexMessages.getInstance();
    private ControllerHost host;
    private MidiOut midiOut;

    private boolean layoutChanged = true;
    private ControllerLayoutMode controllerLayoutMode = ControllerLayoutMode.MIXER;
    private List<String> trackNames = initializeToEmpty(9);
    private boolean trackNamesChanged;
    private List<String> volumes = initializeToEmpty(8);
    private boolean volumesChanged;
    private List<String> pans = initializeToEmpty(8);
    private boolean pansChanged;
    private List<String> parameterNames = initializeToEmpty(8);
    private boolean parameterNamesChanged;
    private List<String> parameterValues = initializeToEmpty(8);
    private boolean parameterValuesChanged;
    private String currentTrackName = "";
    private boolean currentTrackNameChanged;
    private String currentDeviceName = "";
    private boolean currentDeviceNameChanged;
    private List<String> remotePageNames = Collections.emptyList();
    private boolean remotePageNamesChanged;
    private int currentPageIndex = 0;
    private boolean currentPageIndexChanged;

    private boolean headerBarChanged;

    private List<String> sendNames;
    private boolean sendNamesChanged;

    List<ControllerLayoutModeCallback> listeners = new ArrayList<>();

    public ControllerState(ControllerHost host, MidiOut midiOut) {
        this.host = host;
        this.midiOut = midiOut;
    }

    public void refresh() {
        setControllerLayoutMode(controllerLayoutMode);
    }

    public void addControllerLayoutModeListener(ControllerLayoutModeCallback callback) {
        listeners.add(callback);
    }


    public ControllerLayoutMode getControllerLayoutMode() {
        return controllerLayoutMode;
    }

    public void setControllerLayoutMode(final ControllerLayoutMode mode) {
        host.println("Received changed to mode " + mode.name());
        controllerLayoutMode = mode;
        layoutChanged = true;
        listeners.stream().forEach(l -> host.scheduleTask(() -> l.valueChanged(mode), 50));
    }

    public void setTrackName(int index, String name) {
        trackNames.set(index, shortenText(name, 4));
        trackNamesChanged = true;
    }

    public void setVolumes(int index, String volume) {
        volumes.set(index, volume.replaceAll(" dB", ""));
        volumesChanged = true;
    }

    public void setPans(int index, String pan) {
        pans.set(index, pan);
        pansChanged = true;
    }

    public void setParameterName(int index, String name) {
        parameterNames.set(index, shortenText(name, 12));
        parameterNamesChanged = true;
    }

    public void setParameterValues(int index, String value) {
        parameterValues.set(index, shortenText(value,12));
        parameterValuesChanged = true;
    }

    public void setCurrentTrackName(String value) {
        currentTrackName = shortenText(value, 12);
        currentTrackNameChanged = true;
        headerBarChanged = true;
    }

    public void setCurrentDeviceName(String deviceName) {
        currentDeviceName = shortenText(deviceName, 12);
        currentDeviceNameChanged = true;
        headerBarChanged = true;
    }

    public void setRemotePageNames(List<String> pageNames) {
        this.remotePageNames = pageNames;
        remotePageNamesChanged = true;
        headerBarChanged = true;
    }

    public void setCurrentPageIndex(int pageIndex) {
        this.currentPageIndex = pageIndex;
        currentPageIndexChanged = true;
        headerBarChanged = true;
        setActionLight();
    }


    public void openRemotePagesMenu() {
        // TODO show additional pages
    }


    public void sendData() {

        try {
            if (controllerLayoutMode == ControllerLayoutMode.MIXER) {

                if (layoutChanged || trackNamesChanged) {
                    sysexMessages.sendElementNames(midiOut, controllerLayoutMode, trackNames);
                    trackNamesChanged = false;
                }

                if (layoutChanged || volumesChanged) {
                    sysexMessages.sendElementValuesSecondRow(midiOut, controllerLayoutMode, volumes);
                    volumesChanged = false;
                }

                if (layoutChanged || pansChanged) {
                    sysexMessages.sendElementValues(midiOut, controllerLayoutMode, pans);
                    pansChanged = false;
                }

            }

            else if (controllerLayoutMode == ControllerLayoutMode.CONTROLS) {
                if (layoutChanged || parameterNamesChanged) {
                    sysexMessages.sendElementNames(midiOut, controllerLayoutMode, parameterNames);
                    parameterNamesChanged = false;
                }

                if (layoutChanged || parameterValuesChanged) {
                    sysexMessages.sendElementValues(midiOut, controllerLayoutMode, parameterValues);
                    parameterValuesChanged = false;
                }

                if (layoutChanged || currentDeviceNameChanged) {
                    sysexMessages.sendTitle(midiOut, controllerLayoutMode, currentDeviceName);
                }
            }

            if (layoutChanged || headerBarChanged) {
                String currentPageName = "";
                if (currentPageIndex >= 0 && remotePageNames != null && remotePageNames.size() > currentPageIndex) {
                    currentPageName = remotePageNames.get(currentPageIndex);
                }

                List<String> headerBarParts = Arrays.asList(currentTrackName, currentDeviceName, currentPageName);
                sysexMessages.sendHeaderBar(midiOut, controllerLayoutMode, headerBarParts);
                headerBarChanged = false;
            }

            if (layoutChanged || remotePageNamesChanged) {
                List<String> buttonLabels = initializeToEmpty(5);
                if (remotePageNames != null && !remotePageNames.isEmpty()) {
                    for (int i = 0; i < remotePageNames.size() && i < 4; i++) {
                        buttonLabels.set(i, shortenText(remotePageNames.get(i), 8));
                    }
                    buttonLabels.set(4, remotePageNames.size() > 4 ? "More" : "");
                }
                sysexMessages.sendButtonBar(midiOut, controllerLayoutMode, buttonLabels);
                host.scheduleTask(this::setActionLight, 50);
                remotePageNamesChanged = false;

            }

            if (layoutChanged) {
                // TODO put something relevant here
                sysexMessages.sendTopLine(midiOut, controllerLayoutMode, "Honey Flame Music");
                sysexMessages.sendTopLineValue(midiOut, controllerLayoutMode, "is #1");
            }

            layoutChanged = false;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            host.println(sw.toString());
            throw e;
        }

    }

    private List<String> initializeToEmpty(int count) {
        List<String> l = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            l.add("");
        }
        return l;
    }

    private String shortenText(String fullText, int maxLength) {
        if (fullText.length() <= maxLength) {
            return fullText;
        }

        String text = fullText.replaceAll("\\s+", "");
        if (text.length() <= maxLength) {
            return text;
        }

        text = text.replaceAll("[aeiou]", "");
        if (text.length() <= maxLength) {
            return text;
        } else {
            return text.substring(0, maxLength);
        }
    }

    public void setActionLight() {
        for (int i = 0; i < 4; i++) {
            midiOut.sendMidi(ShortMidiMessage.CONTROL_CHANGE, ControllerHardware.ACTION_BUTTON + i, i == currentPageIndex ? 127 : 0);
        }
    }


}
