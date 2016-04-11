package main;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.DiscoveryListener;

public class MyDiscoveryListener implements DiscoveryListener {

	public Object lock=new Object();
	private static String connectionURL=null;
	private static Vector vecDevices=new Vector();
	
	//methods of DiscoveryListener
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
	    //add the device to the vector
	    if(!vecDevices.contains(btDevice)){
	        vecDevices.addElement(btDevice);
	    }
	}

	//implement this method since services are not being discovered
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
	    if(servRecord!=null && servRecord.length>0){
	        connectionURL=servRecord[0].getConnectionURL(0,false);
	    }
	    synchronized(lock){
	        lock.notify();
	    }
	}

	//implement this method since services are not being discovered
	public void serviceSearchCompleted(int transID, int respCode) {
	    synchronized(lock){
	        lock.notify();
	    }
	}


	public void inquiryCompleted(int discType) {
	    synchronized(lock){
	        lock.notify();
	    }

	}//end method
	
	public Vector getDevices()
	{
		return vecDevices;
	}
	
	public String getURL()
	{
		return connectionURL;
	}
	
	public void setURL(String url)
	{
		connectionURL = url;
		return;
	}
}
