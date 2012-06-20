package org.openXpertya.sqlj;

import java.io.*;
import java.util.*;
import java.util.regex.*;
 
public final class MACAddress
{
    public final static String getMacAddresses() throws UnsupportedOperationException, IOException
    {
        final ArrayList<String> macAddressList = new ArrayList<String>();
        final String os = System.getProperty("os.name");
        
        final String[] command;
        if(os.startsWith("Windows"))
            command = windowsCommand;
        else if(os.startsWith("Linux"))
            command = linuxCommand;
        else
            throw new UnsupportedOperationException("Unknown operating system: " + os);
        
        final Process process = Runtime.getRuntime().exec(command);
        
        // Discard the stderr
        new Thread()
        {
            public void run()
            {
                try
                {
                    InputStream errorStream = process.getErrorStream();
                    while (errorStream.read() != -1)   {};
                    errorStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            
        }.start();
        
        // Extract the MAC addresses from stdout
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        for (String line = null; (line = reader.readLine()) != null;)
        {
            Matcher matcher = macPattern.matcher(line);
            if (matcher.matches())
            {
                macAddressList.add(matcher.group(1).replaceAll("[-:]"," "));
            }
        }
        reader.close();
        
        StringBuffer macs = new StringBuffer("");
        for (String macAddress : macAddressList)
        {
            macs.append(macAddress + ";");
        }
        
        return macs.toString().substring(0, macs.length()-1);
    }
    
    static private Pattern macPattern = Pattern.compile(".*((:?[0-9a-f]{2}[-:]){5}[0-9a-f]{2}).*", Pattern.CASE_INSENSITIVE);
    static final String[] windowsCommand = {"ipconfig","/all"};
    static final String[] linuxCommand = {"/sbin/ifconfig","-a"};
    
        
}
