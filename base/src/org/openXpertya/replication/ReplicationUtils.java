package org.openXpertya.replication;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ReplicationUtils {

	
    /**
     * Comprime un string y convierte a un bytearray
     * http://www.java-tips.org/java-se-tips/java.util.zip/how-to-compress-a-byte-array.html
     */
    public static byte[] compressString(String content) throws Exception
    {
        byte[] input = content.getBytes();
        
        // Compressor with highest level of compression
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);
        
        // Give the compressor the data to compress
        compressor.setInput(input);
        compressor.finish();
        
        // Create an expandable byte array to hold the compressed data.
        // It is not necessary that the compressed data will be smaller than
        // the uncompressed data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
        
        // Compress the data
        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        bos.close();

        // Get the compressed data
        byte[] compressedData = bos.toByteArray();
		return compressedData;
    }
    
    
    /**
     * Descomprime un bytearray y genera el String resultante
     * http://www.exampledepot.com/egs/java.util.zip/DecompArray.html
     */
    public static String decompressString(byte[] compressedData) throws Exception
    {
    	// Create decompressor and give it the data to decompress
    	Inflater decompressor = new Inflater();
    	decompressor.setInput(compressedData);
    	
    	// Create an expandable byte array to hold the decompressed data
    	ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);
    			
    	// Decompress data
    	byte[] buf = new byte[1024];
    	while (!decompressor.finished()) {
    		int count = decompressor.inflate(buf);
    		bos.write(buf, 0, count);
    	}
    		
    	bos.close();
    	
    	String result = bos.toString();
    	
    	return result;
    	
    }

    
    /** Metodo para testeos */
//    public static void main (String[] args) {
//    	try	{
//    	String source = "THIS IS A TEST";
//    	byte[] sourceb = compressString(source);
//    	String target = decompressString(sourceb);
//    	System.out.print(target);
//    	}
//    	catch (Exception e ) {
//    		e.printStackTrace();
//    	}
//    }
    
}
