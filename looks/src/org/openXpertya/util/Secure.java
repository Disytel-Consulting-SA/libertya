/*
 * @(#)Secure.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.util;

import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class Secure {

    /** Descripción de Campos */
    private static Cipher	s_cipher	= null;

    /** Descripción de Campos */
    private static SecretKey	s_key	= null;

    /** Descripción de Campos */
    private static Logger	log	= Logger.getLogger(Secure.class.getName());

    /** Descripción de Campos */
    public static final String	CLEARTEXT	= "xyz";

    /**
     * Descripción de Método
     *
     *
     * @param hexString
     *
     * @return
     */
    public static byte[] convertHexString(String hexString) {

        if ((hexString == null) || (hexString.length() == 0)) {
            return null;
        }

        int	size		= hexString.length() / 2;
        byte[]	retValue	= new byte[size];
        String	inString	= hexString.toLowerCase();

        try {

            for (int i = 0; i < size; i++) {

                int	index	= i * 2;
                int	ii	= Integer.parseInt(inString.substring(index, index + 2), 16);

                retValue[i]	= (byte) ii;
            }

            return retValue;

        } catch (Exception e) {
            log.finest(hexString + " - " + e.getLocalizedMessage());
        }

        return null;

    }		// convertToHexString

    /**
     * Descripción de Método
     *
     *
     * @param bytes
     *
     * @return
     */
    public static String convertToHexString(byte[] bytes) {

        // see also Util.toHex
        int		size	= bytes.length;
        StringBuffer	buffer	= new StringBuffer(size * 2);

        for (int i = 0; i < size; i++) {

            // convert byte to an int
            int	x	= bytes[i];

            // account for int being a signed type and byte being unsigned
            if (x < 0) {
                x	+= 256;
            }

            String	tmp	= Integer.toHexString(x);

            // pad out "1" to "01" etc.
            if (tmp.length() == 1) {
                buffer.append("0");
            }

            buffer.append(tmp);
        }

        return buffer.toString();
    }		// convertToHexString

    /**
     * Descripción de Método
     *
     *
     * @param value
     *
     * @return
     */
    public static String decrypt(String value) {

        if (value == null) {
            return null;
        }

        if (value.length() == 0) {
            return value;
        }

        if (value.startsWith(CLEARTEXT)) {
            return value.substring(3);
        }

        // Needs to be hex String
        byte[]	data	= convertHexString(value);

        if (data == null) {
            return null;
        }

        // Init
        if (s_cipher == null) {
            initCipher();
        }

        // Encrypt
        if ((s_cipher != null) && (value != null) && (value.length() > 0)) {

            try {

                AlgorithmParameters	ap	= s_cipher.getParameters();

                s_cipher.init(Cipher.DECRYPT_MODE, s_key, ap);

                byte[]	out		= s_cipher.doFinal(data);
                String	retValue	= new String(out);

                log.finest(value + " => " + retValue);

                return retValue;

            } catch (Exception ex) {
                log.log(Level.SEVERE, value, ex);
            }
        }

        return value;

    }		// decrypt

    /**
     * Descripción de Método
     *
     *
     * @param value
     *
     * @return
     */
    public static String encrypt(String value) {

        String	clearText	= value;

        if (clearText == null) {
            clearText	= "";
        }

        // Init
        if (s_cipher == null) {
            initCipher();
        }

        // Encrypt
        if (s_cipher != null) {

            try {

                s_cipher.init(Cipher.ENCRYPT_MODE, s_key);

                byte[]	encBytes	= s_cipher.doFinal(clearText.getBytes());
                String	encString	= convertToHexString(encBytes);

                log.finest(value + " => " + encString);

                return encString;

            } catch (Exception ex) {
                log.log(Level.SEVERE, value, ex);
            }
        }

        return CLEARTEXT + value;

    }		// encrypt

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */
    public static int hash(String key) {

        long	tableSize	= 2147483647;		// one less than max int
        long	hashValue	= 0;

        for (int i = 0; i < key.length(); i++) {
            hashValue	= (37 * hashValue) + (key.charAt(i) - 31);
        }

        hashValue	%= tableSize;

        if (hashValue < 0) {
            hashValue	+= tableSize;
        }

        int	retValue	= (int) hashValue;

        return retValue;

    }		// hash

    /**
     * Descripción de Método
     *
     */
    private static synchronized void initCipher() {

        try {

            s_cipher	= Cipher.getInstance("DES/ECB/PKCS5Padding");

            // Key
            if (false) {

                KeyGenerator	keygen	= KeyGenerator.getInstance("DES");

                s_key	= keygen.generateKey();

                byte[]		key	= s_key.getEncoded();
                StringBuffer	sb	= new StringBuffer("Key ").append(s_key.getAlgorithm()).append("(").append(key.length).append(")= ");

                for (int i = 0; i < key.length; i++) {
                    sb.append(key[i]).append(",");
                }

                log.info(sb.toString());

            } else {

                s_key	= new javax.crypto.spec.SecretKeySpec(new byte[] {
                    100, 25, 28, -122, -26, 94, -3, -26
                }, "DES");
            }

        } catch (Exception ex) {
            log.log(Level.SEVERE, "cipher", ex);
        }

    }		// initCipher

    /**
     * Descripción de Método
     *
     *
     * @param args
     */
    public static void main(String[] args) {

        String[]	testString	= new String[] { "This is a test!", "", "This is a verly long test string 1624$%" };
        String[]	digestResult	= new String[] { "702edca0b2181c15d457eacac39de39b", "d41d8cd98f00b204e9800998ecf8427e", "934e7c5c6f5508ff50bc425770a10f45" };

        for (int i = 0; i < testString.length; i++) {

            String	digestString	= getDigest(testString[i]);

            if (digestResult[i].equals(digestString)) {
                log.info("OK - digest");
            } else {
                log.severe("Digest=" + digestString + " <> " + digestResult[i]);
            }
        }

        log.info("IsDigest true=" + isDigest(digestResult[0]));
        log.info("IsDigest false=" + isDigest("702edca0b2181c15d457eacac39DE39J"));
        log.info("IsDigest false=" + isDigest("702e"));

        // -----------------------------------------------------------------------
        // log.info(convertToHexString(new byte[]{Byte.MIN_VALUE, -1, 1, Byte.MAX_VALUE} ));
        //
        String	in	= "4115da655707807F00FF";
        byte[]	bb	= convertHexString(in);
        String	out	= convertToHexString(bb);

        if (in.equalsIgnoreCase(out)) {
            log.info("OK - conversion");
        } else {
            log.severe("Conversion Error " + in + " <> " + out);
        }

        // -----------------------------------------------------------------------
        String	test	= "This is a test!!";
        String	result	= "28bd14203bcefba1c5eaef976e44f1746dc2facaa9e0623c";

        //
        String	test_1	= decrypt(result);

        if (test.equals(test_1)) {
            log.info("OK - dec_1");
        } else {
            log.info("TestDec=" + test_1 + " <> " + test);
        }

        // -----------------------------------------------------------------------
        String	testEnc	= encrypt(test);

        if (result.equals(testEnc)) {
            log.info("OK - enc");
        } else {
            log.severe("TestEnc=" + testEnc + " <> " + result);
        }

        String	testDec	= decrypt(testEnc);

        if (test.equals(testDec)) {
            log.info("OK - dec");
        } else {
            log.info("TestDec=" + testDec + " <> " + test);
        }

    }		// main

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param message
     *
     * @return
     */
    public static String getDigest(String message) {

        MessageDigest	md	= null;

        try {

            md	= MessageDigest.getInstance("MD5");

            // md = MessageDigest.getInstance("SHA-1");

        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }

        // Reset MessageDigest object
        md.reset();

        // Convert String to array of bytes
        byte[]	input	= message.getBytes();

        // feed this array of bytes to the MessageDigest object
        md.update(input);

        // Get the resulting bytes after the encryption process
        byte[]	output	= md.digest();

        md.reset();

        //
        return convertToHexString(output);

    }		// getDigest

    /**
     * Descripción de Método
     *
     *
     * @param value
     *
     * @return
     */
    public static boolean isDigest(String value) {

        if ((value == null) || (value.length() != 32)) {
            return false;
        }

        // needs to be a hex string, so try to convert it
        return (convertHexString(value) != null);

    }		// isDigest
}	// Secure



/*
 * @(#)Secure.java   02.jul 2007
 * 
 *  Fin del fichero Secure.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
