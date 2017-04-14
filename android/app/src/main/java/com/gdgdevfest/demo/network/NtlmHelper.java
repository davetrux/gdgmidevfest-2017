package com.gdgdevfest.demo.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gdgdevfest.demo.Settings;
import com.gdgdevfest.demo.ntlm.NTLMSchemeFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NtlmHelper {

    /*
     * Perform an online check
     */
    public static boolean isOnline(Context ctx){
        ConnectivityManager manager =  (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /*
     * NTLM Authentication
     */
    public String getHttp(String url, String userName, String password, String domain) throws IOException {

        String deviceIP = getLocalIpAddress();

        AbstractHttpClient client = new DefaultHttpClient();

        client.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
        client.getCredentialsProvider().setCredentials(new AuthScope(Settings.WINDOWS_BASE, -1),
                new NTCredentials(userName, password, deviceIP, domain));

        //Set up the HTTP calls
        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);

        //Get the data from the body of the response
        InputStream stream = response.getEntity().getContent();
     	byte byteArray[] = IOUtils.toByteArray(stream);
     	String json = new String( byteArray );
     	stream.close();
        return json;
    }

    public String getLocalIpAddress()
    {
    	String deviceIp = null;
    	boolean keepLookupOn = true;

    	try
    	{
    	    Enumeration availableNetwork = NetworkInterface.getNetworkInterfaces();

    	while( availableNetwork.hasMoreElements() && keepLookupOn )
    	{
    		NetworkInterface intf = (NetworkInterface) availableNetwork.nextElement();
    		Enumeration enumIpAddr = intf.getInetAddresses();


    		while( enumIpAddr.hasMoreElements() )
    		{
    			InetAddress inetAddress = (InetAddress)enumIpAddr.nextElement();

    			deviceIp = inetAddress.getHostAddress();

    			if( !inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(deviceIp) )
    			{
    	            		keepLookupOn = false;
    	            		break;
    			}
    		}
    	}
    	}
    	catch (SocketException ex)
    	{
    	        ex.printStackTrace();
    	}

    	return deviceIp;
    }
}
