package uk.rgu.csdm.ubs.heat;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import uk.rgu.csdm.ubs.data.Processor;

import java.util.HashMap;
import java.util.Map;

public class InputHandler
{
  private static InputHandler instance;

  private static Map<String, String> usb1Data = new HashMap();

  private static final String USB1 = "USB1";

  private static HeatMap heatMap;

  private InputHandler()
  {

  }

  public static InputHandler getInstance()
  {
    if(instance == null)
    {
      instance = new InputHandler();
    }
    return instance;
  }

  public void setHeatMap(HeatMap heatMap)
  {
    this.heatMap = heatMap;
  }

  public void connectSerialPort()
  {
    try
    {
      final SerialPort serialPort1 = new SerialPort("COM5");
      serialPort1.openPort();
      serialPort1.addEventListener(new SerialPortEventListener()
      {
        @Override public void serialEvent(SerialPortEvent serialPortEvent)
        {
          try
          {
            byte[] data = serialPort1.readBytes();
            StringBuilder sb = new StringBuilder();
            for (byte b : data)
            {
              sb.append(String.format("%02X", b));
            }
            usb1Data.put(USB1, sb.toString());
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
      });

      final SerialPort serialPort2 = new SerialPort("COM4");
      serialPort2.openPort();
      serialPort2.addEventListener(new SerialPortEventListener()
      {
        @Override public void serialEvent(SerialPortEvent serialPortEvent)
        {
          try
          {
            byte[] data = serialPort2.readBytes();
            StringBuilder sb = new StringBuilder();
            for (byte b : data)
            {
              sb.append(String.format("%02X", b));
            }
            String usb1CurrentData = usb1Data.get(USB1);
            if(usb1CurrentData == null)
            {
              return;
            }
            heatMap.updateData(Processor.getInstance().processFrame(sb.toString()), true);
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
      });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

}
