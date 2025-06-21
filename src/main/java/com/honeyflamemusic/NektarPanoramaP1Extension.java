package com.honeyflamemusic;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;

public class NektarPanoramaP1Extension extends ControllerExtension
{
   protected NektarPanoramaP1Extension(final NektarPanoramaP1ExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }

   private SysexMessages sysexMessages = SysexMessages.getInstance();
   private MidiIn midiIn;
   private MidiOut midiOut;
   private ControllerHardware hardware;
   private ControllerBindings bindings;
   private ControllerState state;

   @Override
   public void init()
   {
      final ControllerHost host = getHost();      

      midiIn = host.getMidiInPort(0);
      midiOut = host.getMidiOutPort(0);

      midiIn.setMidiCallback((ShortMidiMessageReceivedCallback) msg -> onMidi0(msg));
      midiIn.setSysexCallback((String data) -> onSysex0(data));

      // TODO: Perform your driver initialization here.
      sysexMessages.setHost(host);
      sysexMessages.sendInitMessage(midiOut);

      hardware = new ControllerHardware(host);
      state = new ControllerState(host, midiOut);
      bindings = new ControllerBindings(host, hardware, state);
      bindings.bindControls();

      midiOut.sendMidi(ShortMidiMessage.CONTROL_CHANGE, ControllerHardware.MIXER_BUTTON, 127);
      host.scheduleTask(host::requestFlush, 250);

//      host.scheduleTask(() -> state.sendData(), 500);

//      midiOut.sendSysex(sysexMessages.getSomeSysex());
//      midiOut.sendSysex(sysexMessages.getButtonLabelSysex());

      // For now just show a popup notification for verification that it is running.
      host.showPopupNotification("NektarPanoramaP1 Initialized");

   }

   @Override
   public void exit()
   {
      // TODO: Perform any cleanup once the driver exits
      sysexMessages.sendExitMessage(midiOut);
      // For now just show a popup notification for verification that it is no longer running.
      getHost().showPopupNotification("NektarPanoramaP1 Exited");
   }

   @Override
   public void flush()
   {
      // TODO Send any updates you need here.
      state.sendData();
   }

   /** Called when we receive short MIDI message on port 0. */
   private void onMidi0(ShortMidiMessage msg) 
   {
      // TODO: Implement your MIDI input handling code here.

   }

   /** Called when we receive sysex MIDI message on port 0. */
   private void onSysex0(final String data) 
   {
      getHost().println("Received sysex:" + sysexMessages.formatSysex(data) );
      // MMC Transport Controls:
//      if (data.equals("f07f7f0605f7"))
//            mTransport.rewind();
//      else if (data.equals("f07f7f0604f7"))
//            mTransport.fastForward();
//      else if (data.equals("f07f7f0601f7"))
//            mTransport.stop();
//      else if (data.equals("f07f7f0602f7"))
//            mTransport.play();
//      else if (data.equals("f07f7f0606f7"))
//            mTransport.record();
   }

//   private Transport mTransport;
}
