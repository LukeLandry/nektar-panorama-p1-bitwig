package com.honeyflamemusic;
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class NektarPanoramaP1ExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("e898c4b4-2b0e-4462-b27d-a3863e77fc60");
   
   public NektarPanoramaP1ExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "PANORAMA P1";
   }
   
   @Override
   public String getAuthor()
   {
      return "Honey Flame Music";
   }

   @Override
   public String getVersion()
   {
      return "0.1";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }
   
   @Override
   public String getHardwareVendor()
   {
      return "Nektar";
   }
   
   @Override
   public String getHardwareModel()
   {
      return "PANORAMA P1";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 22;
   }

   @Override
   public int getNumMidiInPorts()
   {
      return 4;
   }

   @Override
   public int getNumMidiOutPorts()
   {
      return 1;
   }

   @Override
   public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
      if (platformType == PlatformType.WINDOWS)
      {
         list.add(new String[]{"MIDIIN4 (PANORAMA P1)", "MIDIIN3 (PANORAMA P1)", "MIDIIN2 (PANORAMA P1"}, new String[]{"MIDIOUT4 (PANORAMA P1)"});
      }
      else if (platformType == PlatformType.MAC)
      {
         list.add(new String[]{"PANORAMA P1 ReWire Host", "PANORAMA P1 Instrument", "PANORAMA P1 Mixer", "PANORAMA P1 Internal"}, new String[]{"PANORAMA P1 ReWire Host"});
      }
      else if (platformType == PlatformType.LINUX)
      {
         list.add(new String[]{"PANORAMA P1 MIDI 2", "PANORAMA P1 MIDI 3", "PANORAMA P1 MIDI 4", "PANORAMA P1 MIDI 1"}, new String[]{"PANORAMA P1 MIDI 2"});
      }
   }

   @Override
   public NektarPanoramaP1Extension createInstance(final ControllerHost host)
   {
      return new NektarPanoramaP1Extension(this, host);
   }
}
