/*
 * @(#)LDAP.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.db;

import com.sun.jndi.ldap.*;
import com.sun.security.auth.module.*;

import java.util.*;

import javax.naming.*;
import javax.naming.ldap.*;

import javax.security.auth.login.*;

/**
 *      LDAP Management
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: LDAP.java,v 1.5 2005/05/14 05:31:26 jjanke Exp $
 */
public class LDAP {

    /**
     *      LDAP Constructor
     *
     * @throws Exception
     */
    public LDAP() throws Exception {

        super();

//      testNT ();                              //      funciona
//      testKerberos ();                //      no comprobado
        NamingEnumeration	en	= null;

        /** Log de salida de ejemplo
         * System.out.println("context --------------------------");
         * System.out.println(System.getProperty("auth.login.defaultCallbackHandler"));    //      null
         * LoginContext lc = new LoginContext("jjanke");
         *
         * java.lang.SecurityException: Unable to locate a login configuration
         * at com.sun.security.auth.login.ConfigFile.<init>(ConfigFile.java:97)
         * at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
         * at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:39)
         * at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:27)
         * at java.lang.reflect.Constructor.newInstance(Constructor.java:274)
         * at java.lang.Class.newInstance0(Class.java:308)
         * at java.lang.Class.newInstance(Class.java:261)
         * at javax.security.auth.login.Configuration$3.run(Configuration.java:221)
         * at java.security.AccessController.doPrivileged(Native Method)
         * at javax.security.auth.login.Configuration.getConfiguration(Configuration.java:215)
         * at javax.security.auth.login.LoginContext$1.run(LoginContext.java:170)
         * at java.security.AccessController.doPrivileged(Native Method)
         * at javax.security.auth.login.LoginContext.init(LoginContext.java:167)
         * at javax.security.auth.login.LoginContext.<init>(LoginContext.java:292)
         */

        // http://forum.java.sun.com/thread.jspa?threadID=578338&tstart=240
        // http://java.sun.com/products/jndi/tutorial/trailmap.html
        Hashtable	env	= new Hashtable();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

        // env.put(Context.PROVIDER_URL, "ldap://admin:389/dc=openxpertya,dc=org");
        // env.put(Context.PROVIDER_URL, "ldap://admin.openxpertya.org:389");
        env.put(Context.PROVIDER_URL, "ldap://admin.openxpertya.org");

        /*  */
        env.put(Context.SECURITY_AUTHENTICATION, "none");

        /** Contexto de ejemplo a enviar
         * env.put(Context.SECURITY_AUTHENTICATION, "EXTERNAL");
         * env.put(Context.SECURITY_PRINCIPAL, "jjanke");
         *
         * env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");
         * /      env.put(Context.SECURITY_PRINCIPAL, "openxp");
         * env.put(Context.SECURITY_PRINCIPAL, "s=Fundesle, g=openxp, o=openxp");
         *
         * env.put(Context.SECURITY_CREDENTIALS, "pass");
         * /*
         */
        try {

            LdapContext		ctx	= new InitialLdapContext(env, null);
            StartTlsResponse	tls	= (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());

            // http://msdn.microsoft.com/library/default.asp?url=/library/en-us/dsml/dsml/ldap_controls_and_session_support.asp
            ctx.setRequestControls(new Control[] { new com.sun.jndi.ldap.BasicControl("1.2.840.113556.1.4.417") });

            // ctx.setRequestControls(new Control[] {
            // new SortControl(new String[] {"cn"}, true)
            // });
            System.out.println("Listado ---------------------------------");
            en	= ctx.list("");

            while (en.hasMore()) {
                System.out.println(en.next());
            }

            tls.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            new LDAP();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }		// main

    /**
     *      testNT
     *      @throws LoginException
     */
    private void testNT() throws LoginException {

      System.out.println("Systema NT ----------------------------");

        //NTSystem	ntsystem	= new NTSystem();
            
       // System.out.println(ntsystem);
       // System.out.println(ntsystem.getDomain());
       // System.out.println(ntsystem.getDomainSID());
       // System.out.println(ntsystem.getName());
       // System.out.println(ntsystem.getUserSID());
        System.out.println("NT login ----------------------------");

      //  NTLoginModule	ntlogin	= new NTLoginModule();

      //  System.out.println(ntlogin);

        Map	map	= new HashMap();

        map.put("debug", "true");
        map.put("debugNative", "true");
      //  ntlogin.initialize(null, null, null, map);
      //  System.out.println(ntlogin.login());

    }		// testNT

    /**
     *      testKerberos
     *      @throws LoginException
     */
    private void tetKerberos() throws LoginException {

        System.out.println("Krb login ----------------------------");

        Map	map	= new HashMap();

        map.put("debug", "true");
        map.put("debugNative", "true");

        Krb5LoginModule	klogin	= new Krb5LoginModule();

        System.out.println(klogin);
        map.put("principal", "jjanke");
        klogin.initialize(null, null, null, map);
        System.out.println(klogin.login());

        /**
         * ** No krb5.ini file found in entire system 
         * Debug is  true storeKey false useTicketCache false useKeyTab false doNotPrompt false ticketCache is null KeyTab is null refreshKrb5Config is false principal is jjanke tryFirstPass is false useFirstPass is false storePass is false clearPass is false
         * [Krb5LoginModule] authentication failed
         * Could not load configuration file c:\winnt\krb5.ini (The system cannot find the file specified)
         * javax.security.auth.login.LoginException: Could not load configuration file c:\winnt\krb5.ini (The system cannot find the file specified)
         */
    }
}



/*
 * @(#)LDAP.java   02.jul 2007
 * 
 *  Fin del fichero LDAP.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007