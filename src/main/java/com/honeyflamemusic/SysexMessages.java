package com.honeyflamemusic;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiOut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SysexMessages {

    private final static SysexMessages instance = new SysexMessages();
    public static SysexMessages getInstance() {
        return instance;
    }

    public void sendInitMessage(MidiOut port) {
        if (host.platformIsLinux()) {
            // I don't know what this does, but the Nektar implementation does this
            port.sendSysex(linuxOnlyInitMessage);
        }
        port.sendSysex(initMessage);
        port.sendSysex(initMessage2);
    }

    public void sendExitMessage(MidiOut port) {
        port.sendSysex(exitMessage1);
        port.sendSysex(exitMessage2);
    }

    public void sendElementNames(MidiOut port, ControllerLayoutMode mode, List<String> trackNames) {
        host.println("Track names being sent: " + trackNames.stream().collect(Collectors.joining(", ")));
        port.sendSysex(getWriteArrayToDisplay(mode.getLayoutValue(), controlNames, trackNames, 1));
    }

    public void sendElementValuesSecondRow(MidiOut port, ControllerLayoutMode mode, List<String> volumes) {
        host.println("Volumes being sent: " + volumes.stream().collect(Collectors.joining(", ")));
        port.sendSysex(getWriteArrayToDisplay(mode.getLayoutValue(), controlValues, volumes, 9));
    }

    public void sendElementValues(MidiOut port, ControllerLayoutMode mode, List<String> pans) {
        host.println("Pans being sent: " + pans.stream().collect(Collectors.joining(", ")));
        port.sendSysex(getWriteArrayToDisplay(mode.getLayoutValue(), controlValues, pans, 1));
    }

    public void sendTitle(MidiOut port, ControllerLayoutMode mode, String title) {
        host.println("Title being sent: " + title);
        port.sendSysex(getWriteMessageToDisplaySysex(mode.getLayoutValue(), pageTitle, title));
    }

    public void sendTopLine(MidiOut port, ControllerLayoutMode mode, String text) {
        port.sendSysex(getWriteMessageToDisplaySysex(mode.getLayoutValue(), messageLine, text));
    }

    public void sendTopLineValue(MidiOut port, ControllerLayoutMode mode, String text) {
        port.sendSysex(getWriteMessageToDisplaySysex(mode.getLayoutValue(), messageValue, text));
    }

    public void sendHeaderBar(MidiOut port, ControllerLayoutMode mode, List<String> headerFields) {
        port.sendSysex(getWriteArrayToDisplay(mode.getLayoutValue(), headerLine, headerFields, 1));
    }

    public void sendButtonBar(MidiOut port, ControllerLayoutMode mode, List<String> labels) {
        host.println("Button labels: " + labels.stream().collect(Collectors.joining(", ")));
        port.sendSysex(getWriteArrayToDisplay(mode.getLayoutValue(), buttonLabels, labels, 1));
    }

//    public void sendMenu(MidiOut port, List<String> menuItems) {
//        port.sendSysex(getWriteArrayToDisplay("00", menu, menuItems, 1));
//    }

    public String formatSysex(String message) {
        StringBuffer sb = new StringBuffer(message.substring(0, 2));
        for (int i = 2; i < message.length(); i += 2) {
            sb.append(" ");
            sb.append(message.substring(i, i+2));
        }
        return sb.toString();
    }


    private final String initMessage = "F0 00 01 77 7F 01 08 02 00 00 01 01 73 F7";
    private final String initMessage2 = "F0 00 01 77 7F 01 09 03 00 00 01 3E 34 F7";
    private final String linuxOnlyInitMessage = "F0 00 01 77 7F 01 08 01 00 00 01 01 75 F7";
    private final String exitMessage1 = "F0 00 01 77 7F 01 09 00 00 00 01 00 75 F7";  // select internal page
    private final String exitMessage2 = "F0 00 01 77 7F 01 08 02 00 00 01 00 74 F7";

    private final String surfaceSelect0Message = "F0 00 01 77 7F 01 09 00 00 00 01 00 75 F7";
    // N/A for P1 private final String surfaceSelect1Message = "F0 00 01 77 7F 01 09 03 00 00 01 36 3C F7";
    private final String surfaceSelect1Message = "F0 00 01 77 7F 01 09 03 00 00 01 3E 34 F7";
    // N/A for P1 private final String surfaceSelect2Message = "F0 00 01 77 7F 01 09 03 00 00 01 37 3B F7";
    private final String surfaceSelect3Message = "F0 00 01 77 7F 01 09 03 00 00 01 00 72 F7";

    private final String sysexPrefix = "F0 00 01 77 7F 01";
    private final String writeMessage = "06";

    private final String headerLine = "01";
    private final String messageLine = "02";
    private final String messageValue = "03";
    private final String buttonLabels = "04";
    private final String toggleModeDisplays = "05";


    private final String pageTitle = "05";
    private final String controlNames = "06";
    private final String controlValues = "07";
    private final String menu = "08";


    private final String someSysex = "F0 00 01 77 7F 01 06 02 06 01 00 00 02 00 00 03 00 00 04 00 00 05 00 00 06 00 00 07 00 00 08 00 00 09 01 4D F7";

//    private final String buttonLabelSysex = "F0 00 01 77 7F 01 06 02 04 00 05 00 00 00 00 00 00 01 01 2B 00 02 05 53 65 6E 64 73 00 03 04 55 73 65 72 00 04 07 44 65 76 69 63 65 73 00 05 00";
    private final String buttonLabelSysex = "F0 00 01 77 7F 01 06 02 04 00 05 00 00 00 00 00 00 01 01 2B 00 02 07 42 72 6F 77 73 65 72 00 03 06 50 72 65 73 65 74 00 04 06 52 65 6D 6F 74 65 00 05 05 50 61 67 65 73 F7";



    private SysexMessages() {
//
    }

    private ControllerHost host;
    public void setHost(ControllerHost host) {
        this.host = host;
    }


//    private String getMixerFaderValuesSysex(List<String> values) {
//        return getMixerValuesSysex(aboveFaderSection, values);
//    }

//    private String getMixerEncoderValuesSysex(List<String> values) {
//        return getMixerValuesSysex(aboveEncoderSection, values);
//    }


    private String getWriteMessageToDisplaySysex(String layoutByte, String partByte, String message) {
        return getWriteArrayToDisplay(layoutByte, partByte, Collections.singletonList(message), 1);
    }

    private String getWriteValueToDisplaySysex(String layoutByte, String partByte, String message) {
        List<String> sysexParts = new ArrayList<>();
        sysexParts.add(sysexPrefix);
        sysexParts.add(writeMessage);
        sysexParts.add(layoutByte);
        sysexParts.add(partByte);
        sysexParts.add(message);
        return buildSysex(sysexParts);
    }

    private String getWriteArrayToDisplay(String layoutByte, String partByte, List<String> messages, int startingIndex) {
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            parts.add(hexFormat(i + startingIndex));
            parts.add(textToSysex(messages.get(i)));
            if (i < messages.size() - 1) {
                parts.add("00");
            }
        }
        String partsString = parts.stream().collect(Collectors.joining(" "));
        host.println("Parts string: " + partsString);
        return getWriteValueToDisplaySysex(layoutByte, partByte, partsString);
    }

    private String buildSysex(List<String> parts) {
        String s = parts.stream().collect(Collectors.joining(" "));
        s.trim();
        if (!s.endsWith("F7") && !s.contains("F7")) {
            s = s + " F7";
        }
        return s;
    }

    private String hexFormat(int i) {
        String s = Integer.toHexString(i);
        if (s.length() < 2) {
            s = "0" + s;
        }
        return s;
    }

    private String hexFormat(char c) {
        String s = Integer.toHexString(c);
        if (s.length() < 2) {
            s = "0" + s;
        }
        return s;
    }


    private String textToSysex(String text) {
        List<String> bytes = new ArrayList<>();
        bytes.add(hexFormat(text.length()));
        for (int j = 0; j < text.length(); j++) {
            bytes.add(hexFormat(text.charAt(j)));
        }
        return bytes.stream().collect(Collectors.joining(" "));
    }

}
