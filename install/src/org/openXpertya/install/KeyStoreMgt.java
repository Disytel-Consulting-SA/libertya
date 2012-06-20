/*
 * @(#)KeyStoreMgt.java   21.abr 2007  Versión 2.2
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



package org.openXpertya.install;

import org.openXpertya.install.KeyStoreMgt;
import org.openXpertya.OpenXpertya;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;

import sun.security.tools.KeyTool;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.net.InetAddress;

import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.JFrame;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 21.04.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class KeyStoreMgt {

    /** Descripción de Campo */
    private static CLogger	log	= CLogger.getCLogger(KeyStoreMgt.class);

    /** Descripción de Campo */
    public static String	KEYSTORE_NAME	= "myKeystore";

    /** Descripción de Campo */
    public static String	KEYSTORE_DIRECTORY	= "keystore";

    /** Descripción de Campo */
    public static String	CERTIFICATE_ALIAS	= "libertya";

    /** Descripción de Campo */
    private File	m_file	= null;

    /** Descripción de Campo */
    private char[]	m_password	= null;

    /** Descripción de Campo */
    private KeyStore	m_keyStore	= null;

	private String organizationUnit;
	private String location;
	private String state;
	private String country;
	private String commonName;
	private String organization;
	
    /**
     * Constructor ...
     *
     *
     * @param fileName
     * @param password
     */
    public KeyStoreMgt(String fileName, char[] password) {

        log.info(fileName);
        m_file		= new File(fileName);
        m_password	= password;

    }		// KeyStoreMgt

    /**
     * Descripción de Método
     *
     *
     * @param alias
     * @param parent
     */
    private void createCertificate(String alias, JFrame parent) {

        log.info("");

        try {

            File	dir	= m_file.getParentFile();

            if (!dir.exists()) {
                dir.mkdir();
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "directorio", e);
        }

        String	dname	= getDname(this, parent);

        if (dname == null) {
            return;
        }

        //
        try {

            genkey(alias, m_password, m_file.getAbsolutePath(), dname);
            selfcert(alias, m_password, m_file.getAbsolutePath(), dname);

        } catch (Exception e) {
            log.log(Level.SEVERE, "certificado", e);
        }

    }		// createCertificate

    /**
     * Descripción de Método
     *
     *
     * @param in
     *
     * @return
     */
    public static String escapeCommas(String in) {

        if ((in == null) || (in.indexOf(",") == -1)) {
            return in;
        }

        StringBuffer	out	= new StringBuffer();
        char[]		chars	= in.toCharArray();

        for (int i = 0; i < chars.length; i++) {

            if (chars[i] == ',') {
                out.append('\\').append(',');
            } else {
                out.append(chars[i]);
            }
        }

        return out.toString();

    }		// escapeCommas

    /**
     * Descripción de Método
     *
     *
     * @param alias
     * @param password
     * @param fileName
     * @param dname
     */
    public static void genkey(String alias, char[] password, String fileName, String dname) {

        StringBuffer	cmd	= new StringBuffer("-genkey -keyalg rsa");

        cmd.append(" -alias ").append(alias);
        cmd.append(" -dname \"").append(dname).append("\"");
        cmd.append(" -keypass ").append(password).append(" -validity 1800");  // validez del certificado 1800 días

        if (fileName.indexOf(' ') != -1) {
            cmd.append(" -keystore \"").append(fileName).append("\" -storepass ").append(password);
        } else {
            cmd.append(" -keystore ").append(fileName).append(" -storepass ").append(password);
        }

        keytool(cmd.toString());

    }		// genkey

    /**
     * Descripción de Método
     *
     *
     * @param cmd
     */
    public static void keytool(String cmd) {

        log.info("keytool " + cmd);

        ArrayList	list		= new ArrayList();
        StringTokenizer	st		= new StringTokenizer(cmd, " ");
        String		quoteBuffer	= null;

        while (st.hasMoreTokens()) {

            String	token	= st.nextToken();

            // System.out.println("= " + token + " = quoteBuffer=" + quoteBuffer + " - Size=" + list.size() );
            if (quoteBuffer == null) {

                if (token.startsWith("\"")) {
                    quoteBuffer	= token.substring(1);
                } else {
                    list.add(token);
                }

            } else {
                quoteBuffer	+= " " + token;
            }

            if (token.endsWith("\"")) {

                String	str	= quoteBuffer.substring(0, quoteBuffer.length() - 1);

                // System.out.println("  Buffer= " + str );
                list.add(str);
                quoteBuffer	= null;
            }

        }	// all tokens

        //
        String[]	args	= new String[list.size()];

        list.toArray(args);

        // System.out.println(" args #" + args.length);
        try 
        {
        	KeyTool.main(args);
        }
        catch (Exception e)
        {
        	log.severe("KeyTool.main()");
        }

    }		// ketyool

    /**
     * Descripción de Método
     *
     *
     * @param args
     */
    public static void main(String[] args) {

        OpenXpertya.startupEnvironment(true);
        System.out.println(new KeyStoreMgt("C:/ServidorOXP/keystore/myKeystore2", "myPassword".toCharArray()).verify(null));

    }		// main

    /**
     * Descripción de Método
     *
     *
     * @param alias
     * @param password
     * @param fileName
     * @param dname
     */
    public static void selfcert(String alias, char[] password, String fileName, String dname) {

        StringBuffer	cmd	= new StringBuffer("-selfcert");

        cmd.append(" -alias ").append(alias);
        cmd.append(" -dname \"").append(dname).append("\"");
        cmd.append(" -keypass ").append(password).append(" -validity 1800");   // validez del certificado 1800 días

        if (fileName.indexOf(' ') != -1) {
            cmd.append(" -keystore \"").append(fileName).append("\" -storepass ").append(password);
        } else {
            cmd.append(" -keystore ").append(fileName).append(" -storepass ").append(password);
        }

        keytool(cmd.toString());

    }		// selfcert

    /**
     * Descripción de Método
     *
     *
     * @param parent
     *
     * @return
     */
    public String verify(JFrame parent) {

        KeyStore	ks	= null;

        try {
            ks	= getKeyStore();
        } catch (Exception e) {

            log.log(Level.SEVERE, "obtiene KeyStore", e);

            return e.getMessage();
        }

        // No KeyStore
        if (ks == null) {

            createCertificate(CERTIFICATE_ALIAS, parent);

            try {
                ks	= getKeyStore();
            } catch (Exception e) {

                log.log(Level.SEVERE, "nueva KeyStore", e);

                return e.getMessage();
            }

        }	// new key store

        // No KeyStore
        if (ks == null) {
            return "No hay KeyStore";
        }

        // Verify Certificate
        Certificate	cert	= null;

        try {
            cert	= getCertificate(CERTIFICATE_ALIAS);
        } catch (Exception e) {

            log.log(Level.SEVERE, "certificado", e);

            return e.getMessage();
        }

        if (cert == null) {
            return "No se encuentra el certificado";
        }

        return null;	// OK

    }			// verify

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param alias
     *
     * @return
     *
     * @throws Exception
     */
    public Certificate getCertificate(String alias) throws Exception {

        log.config("Alias=" + alias);

        Date	date	= m_keyStore.getCreationDate(alias);

        if (date == null) {	// no certificate
            return null;
        }

        log.fine("Creado=" + date);

        //
        Key	key	= m_keyStore.getKey(alias, m_password);

        if (CLogMgt.isLevelFinest()) {
            log.info("Key=" + key);	// Multiple lines
        } else {
            log.fine(key.getAlgorithm());
        }

        //
        Certificate	cert	= m_keyStore.getCertificate(alias);

        if (CLogMgt.isLevelFinest()) {
            log.info("Certificado = " + cert);		// Multiple lines
        } else {
            log.fine(cert.getType());
        }

        // log.fine("Certificado - Tipo=" + cert.getType()
        // + " - PublicKey=" + cert.getPublicKey());
        return cert;

    }		// getCertificate

    /**
     * Descripción de Método
     *
     *
     * @param parent
     *
     * @return
     */
    public static String getDname(KeyStoreMgt mgt, JFrame parent) {

        String	cn	= null;

        if (mgt.getCommonName() == null) {
		    try {
		
		        InetAddress	address	= InetAddress.getLocalHost();
		
		        cn	= address.getCanonicalHostName();
		
		    } catch (Exception e) {}
        } else {
        	cn = mgt.getCommonName();
        }
        	
        
		String ou = mgt.getOrganization() != null
			? mgt.getOrganization()
			: System.getProperty("user.name");
		String o = mgt.getOrganizationUnit() != null
			? mgt.getOrganizationUnit()
			: "UsuarioLibertya";
		String l = mgt.getLocation() != null
			? mgt.getLocation()
			: "MiCiudad";
		String s = mgt.getState() != null
			? mgt.getState()
			: "";
		String c = mgt.getCountry() != null
			? mgt.getCountry()
			: System.getProperty("user.country");

        //
        if (parent != null) {

            KeyStoreDialog	skd	= new KeyStoreDialog(parent, cn, ou, o, l, s, c);

            if (!skd.isOK()) {
                return null;
            }

            cn	= skd.getCN();
            ou	= skd.getOU();
            o	= skd.getO();
            l	= skd.getL();
            s	= skd.getS();
            c	= skd.getC();
        }

        //
        if ((cn == null) || (cn.length() == 0)) {

            log.severe("No hay Nombre com" + "\u00fa" + "n"+" (CN)");

            return null;
        }

        if ((ou == null) || (ou.length() == 0)) {

            log.severe("No hay Unidad organizativa (OU)");

            return null;
        }

        if ((o == null) || (o.length() == 0)) {

            log.severe("No hay Organizaci" + "\u00f3" + "n"+" (O)");

            return null;
        }

        if ((c == null) || (c.length() == 0)) {

            log.severe("No hay Pa" + "\u00ed" + "s (C)");

            return null;
        }

        // Escape commas
        StringBuffer	dname	= new StringBuffer();

        dname.append("CN=").append(escapeCommas(cn));		// common name; nombre común
        dname.append(", OU=").append(escapeCommas(ou));		// org unit; unidad organizativa
        dname.append(", O=").append(escapeCommas(o));		// org; organización

        if ((l != null) && (l.length() > 0)) {
            dname.append(", L=").append(escapeCommas(l));	// locality; ciudad o localidad
        }

        if ((s != null) && (s.length() > 0)) {
            dname.append(", S=").append(escapeCommas(s));	// state; estado o provincia
        }

        dname.append(", C=").append(escapeCommas(c));		// country; país según normativa de dos digitos internacional

        return dname.toString();

    }		// getDname

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */
    public KeyStore getKeyStore() throws Exception {

        try {
            m_keyStore	= KeyStore.getInstance("JKS");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Instancia", e);
        }

        // Load Existing
        if (m_file.exists()) {

            log.fine(m_file.toString());

            InputStream	is	= null;

            try {
                is	= new FileInputStream(m_file);
            } catch (Exception e) {

                log.log(Level.SEVERE, "carga", e);

                return null;
            }

            m_keyStore.load(is, m_password);

        } else {
            return null;	// does not exist
        }

        //
        log.fine("Proveedor=" + m_keyStore.getProvider() + " - Tipo=" + m_keyStore.getType());

        //
        return m_keyStore;

    }		// getKeyStore

    /**
     * Descripción de Método
     *
     *
     * @param baseDir
     *
     * @return
     */
    public static String getKeystoreFileName(String baseDir) {

        String	fileName	= baseDir;

        if (fileName == null) {
            fileName	= "";
        } else if (!fileName.endsWith(File.separator)) {
            fileName	+= File.separator;
        }

        fileName	+= KEYSTORE_DIRECTORY + File.separator + KEYSTORE_NAME;

        return fileName;

    }		// getKeystoreFileName

	/**
	 * @param organizationUnit the organizationUnit to set
	 */
	public void setOrganizationUnit(String organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @param commonName the commonName to set
	 */
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/**
	 * @return the organizationUnit
	 */
	private String getOrganizationUnit() {
		String ret = null; 
		if (organizationUnit != null && organizationUnit.trim().length() > 0) {
			ret = organizationUnit.trim();
		}
		return ret;
	}

	/**
	 * @return the location
	 */
	private String getLocation() {
		String ret = null;
		if (location != null && location.trim().length() > 0) {
			ret = location.trim();
		}
		return location;
	}

	/**
	 * @return the state
	 */
	private String getState() {
		String ret = null;
		if (state != null && state.trim().length() > 0) {
			ret = state.trim();
		}
		return ret;
	}

	/**
	 * @return the country
	 */
	private String getCountry() {
		String ret = null;
		if (country != null && country.trim().length() > 0) {
			ret = country.trim();
		}
		return ret;
	}

	/**
	 * @return the commonName
	 */
	private String getCommonName() {
		String ret = null;
		if (commonName != null && commonName.trim().length() > 0) {
			ret = commonName.trim();
		}
		return ret;
	}

	/**
	 * @return the organization
	 */
	private String getOrganization() {
		String ret = null;
		if (organization != null && organization.trim().length() > 0) {
			ret = organization.trim();
		}
		return ret;
	}
	
	
}	// MyKeyStore



/*
 * @(#)KeyStoreMgt.java   21.abr 2007
 * 
 *  Fin del fichero KeyStoreMgt.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 21.abr 2007
