package org.openXpertya.print.fiscal.util;

public class ArrayUtils {

	public static byte[] append(byte [] array, byte [] otherArray) {
		byte [] result = new byte[array.length + otherArray.length];
		System.arraycopy(array, 0, result, 0, array.length);
		System.arraycopy(otherArray, 0, result, array.length, otherArray.length);
		return result;
	}
	
	public static byte[] append(byte[] array, byte value) {
		byte[] valueArray = { value };
		return append(array, valueArray);
	}
	
	public static byte[] redim(byte[] array, int length) {
		byte[] result = new byte[length];
		System.arraycopy(array, 0, result, 0, length);
		return result;
	}
	
	//Ader Javier : 10 oct 2010
	/**
	 * Remueve el prefijo de logitud prefixLength; SE ASUME
	 * que array tiene longitud mayor o igual a prefixLength.
	 * Ejemplo: [10,11,12,13,14], prefixLength = 3 -> [10,11,12]
	 * y con prefixLength[5] = []
	 * @param array
	 * @param prefixLength
	 * @return
	 */
	public static byte[] removePrefix(byte[] array, int prefixLength)
	{
		int newLength =array.length - prefixLength;
		byte[] newArray = new byte[newLength];
		if (newLength == 0)
			return newArray;
		System.arraycopy(array, prefixLength, newArray,0, newLength);
		return newArray;
	}
}
