package main;

import java.util.Vector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import FreescanComm.*;
import javax.microedition.io.*;
import main.MyDiscoveryListener;

public class Main {
//
//	private static RemoteDeviceDiscovery remote = new RemoteDeviceDiscovery();
//
//	public static void main(String[] args) {
//		
//		/* Local device for detecting bluetooth devices */
//		LocalDevice local;
//		/* Retreieve a vector of devices */
//		Vector v = remote.getDevices();
//		
//		/* Check if Freescan bluetooth is available */
//		if(!v.toString().contains("000195070907"))
//		{
//			System.out.println("Serial not found");
//			return;
//		}
//		
//		/* RemoteDevice object for referring to Freescan */
//		RemoteDevice remote = null;
//		
//		try
//		{
//			/* Scan through vector to find Freescan Bluetooth adapter */
//			for(Enumeration e = v.elements(); e.hasMoreElements(); )
//			{
//				remote = (RemoteDevice)e.nextElement();
//				if(remote.getFriendlyName(false).contains("Freescan"))
//				{
//					System.out.println("Freescan Found");
//					break;
//				}
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
//		
//		try
//		{
//			local = LocalDevice.getLocalDevice();
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
//		
//		String name = null;
//		try
//		{
//			name = remote.getFriendlyName(false);
//		}
//		catch(Exception e)
//		{
//			name = remote.getBluetoothAddress();
//		}
//		
//		System.out.println("Device found: " + name);
//		
//		UUID[] uuid = new UUID[] {new UUID(0x1101)};
//		
//		MyDiscoveryListener listener = new MyDiscoveryListener();
//		InputStream conn = 
//		try
//		{
//			DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
//			int a = agent.searchServices(null, uuid, remote, listener);
//			System.out.println("Result from service search: " + a);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
//		
//		
////		Freescan free = new Freescan();
////		
////		free.connect("com25");
//		//if(free.connect("com15")) System.out.println("Connected to Freescan");
//		
//		return;
//	}
//
//}


/**
* A simple SPP client that connects with an SPP server
*/


//object used for waiting
private static Object lock=new Object();

//vector containing the devices discovered
private static Vector vecDevices=new Vector();

private static String connectionURL=null;


public static void main(String[] args) throws IOException {

    //SampleSPPClient client=new SampleSPPClient();
	MyDiscoveryListener client = new MyDiscoveryListener();
	
    //display local device address and name
    LocalDevice localDevice = LocalDevice.getLocalDevice();
    System.out.println("Address: "+localDevice.getBluetoothAddress());
    System.out.println("Name: "+localDevice.getFriendlyName());

    //find devices
    DiscoveryAgent agent = localDevice.getDiscoveryAgent();

    System.out.println("Starting device inquiry...");
    agent.startInquiry(DiscoveryAgent.GIAC, client);

    try {
        synchronized(client.lock){
            client.lock.wait();
        }
    }
    catch (InterruptedException e) {
        e.printStackTrace();
    }


    System.out.println("Device Inquiry Completed. ");

    //print all devices in vecDevices
    int deviceCount=client.getDevices().size();

    if(deviceCount <= 0){
        System.out.println("No Devices Found .");
        System.exit(0);
    }
    else{
        //print bluetooth device addresses and names in the format [ No. address (name) ]
        System.out.println("Bluetooth Devices: ");
        for (int i = 0; i <deviceCount; i++) {
            RemoteDevice remoteDevice=(RemoteDevice)client.getDevices().elementAt(i);
            System.out.println((i+1)+". "+remoteDevice.getBluetoothAddress()+" ("+remoteDevice.getFriendlyName(true)+")");
        }
    }

    System.out.print("Choose Device index: ");
    BufferedReader bReader=new BufferedReader(new InputStreamReader(System.in));

    String chosenIndex=bReader.readLine();
    int index=Integer.parseInt(chosenIndex.trim());

    //check for spp service
    RemoteDevice remoteDevice=(RemoteDevice)client.getDevices().elementAt(index-1);
    UUID[] uuidSet = new UUID[15];
    uuidSet[0]=new UUID(0x000C);
    uuidSet[1] = new UUID(0x0008);
    uuidSet[2] = new UUID(0x0100);
    uuidSet[3] = new UUID(0x000F);
    uuidSet[4] = new UUID(0x1000);
    uuidSet[5] = new UUID(0x1001);
    uuidSet[6] = new UUID(0x1002);
    uuidSet[7] = new UUID(0x1105);
    uuidSet[8] = new UUID(0x1106);
    uuidSet[9] = new UUID(0x1115);
    uuidSet[10] = new UUID(0x1116);
    uuidSet[11] = new UUID(0x1117);
    uuidSet[12] = new UUID(0x0001);
    uuidSet[13] = new UUID(0x0003);
    uuidSet[14] = new UUID(0x1101);
    
    //client.setURL(remoteDevice.getBluetoothAddress());
    
    System.out.println("\nSearching for service...");
    agent.searchServices(null,uuidSet,remoteDevice,client);

    try {
        synchronized(client.lock){
            client.lock.wait();
        }
    }
    catch (InterruptedException e) {
        e.printStackTrace();
    }

    if(client.getURL()==null){
        System.out.println("Device does not support Simple SPP Service.");
        System.exit(0);
    }

    //connect to the server and send a line of text
    StreamConnection streamConnection=(StreamConnection)Connector.open(client.getURL());

    //send string
    OutputStream outStream=streamConnection.openOutputStream();
    PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
    pWriter.write("Test String from SPP Client\r\n");
    pWriter.flush();


    //read response
    InputStream inStream=streamConnection.openInputStream();
    BufferedReader bReader2=new BufferedReader(new InputStreamReader(inStream));
    String lineRead=bReader2.readLine();
    System.out.println(lineRead);


}//main


}