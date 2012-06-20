/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class NaturalAccountMap extends CCache {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param trxName
     */

    public NaturalAccountMap( Properties ctx,String trxName ) {
        super( "NaturalAccountMap",100 );
        m_ctx     = ctx;
        m_trxName = trxName;
    }    // NaturalAccountMap

//      private String      m_delim = ",";

    /** Descripción de Campos */

    private static int s_keyNo = 0;

    /** Descripción de Campos */

    private Properties m_ctx = null;

    /** Descripción de Campos */

    private String m_trxName = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( NaturalAccountMap.class );

    /**
     * Descripción de Método
     *
     *
     * @param file
     *
     * @return
     */

    public String parseFile( File file ) {
        log.config( file.getAbsolutePath());

        String line = null;

        try {

            // see FileImport

            BufferedReader in = new BufferedReader( new FileReader( file ),10240 );

            // not safe see p108 Network pgm

            String errMsg = "";

            // read lines

            while(( line = in.readLine()) != null && (errMsg.length() == 0) ) {
                errMsg = parseLine( line );
            }

            line = "";
            in.close();

            // Error

            if( errMsg.length() != 0 ) {
                return errMsg;
            }
        } catch( Exception ioe ) {
            String s = ioe.getLocalizedMessage();

            if( (s == null) || (s.length() == 0) ) {
                s = ioe.toString();
            }

            return "Parse Error: Line=" + line + " - " + s;
        }

        return "";
    }    // parse

    /**
     * Descripción de Método
     *
     *
     * @param line
     *
     * @return
     *
     * @throws Exception
     */

    public String parseLine( String line ) throws Exception {
        log.config( line );

        // Fields with ',' are enclosed in "

        StringBuffer    newLine = new StringBuffer();
        StringTokenizer st      = new StringTokenizer( line,"\"",false );

        newLine.append( st.nextToken());    // first part

        while( st.hasMoreElements()) {
            String s = st.nextToken();                // enclosed part

            newLine.append( s.replace( ',',' ' ));    // remove ',' with space

            if( st.hasMoreTokens()) {
                newLine.append( st.nextToken());      // unenclosed
            }
        }

        // add space at the end        - tokenizer does not count empty fields

        newLine.append( " " );

        // Parse Line - replace ",," with ", ,"    - tokenizer does not count empty fields

        String pLine = Util.replace( newLine.toString(),",,",", ," );

        pLine = Util.replace( pLine,",,",", ," );
        st    = new StringTokenizer( pLine,",",false );

        // All fields there ?

        if( st.countTokens() == 1 ) {
            log.log( Level.SEVERE,"Ignored: Require ',' as separator - " + pLine );

            return "";
        }

        if( st.countTokens() < 9 ) {
            log.log( Level.SEVERE,"Ignored: FieldNumber wrong: " + st.countTokens() + " - " + pLine );

            return "";
        }

        // Fill variables

        String Value           = null,
               Name            = null,
               Description     = null,
               AccountType     = null,
               AccountSign     = null,
               IsDocControlled = null,
               IsSummary       = null,
               Default_Account = null;

        //

        for( int i = 0;(i < 8) && st.hasMoreTokens();i++ ) {
            String s = st.nextToken().trim();

            // Ignore, if is it header line

            if( s.startsWith( "[" ) && s.endsWith( "]" )) {
                return "";
            }

            if( s == null ) {
                s = "";
            }

            //

            if( i == 0 ) {           // A - Value
                Value = s;
            } else if( i == 1 ) {    // B - Name
                Name = s;
            } else if( i == 2 ) {    // C - Description
                Description = s;
            } else if( i == 3 ) {    // D - Type
                AccountType = (s.length() > 0)
                              ?String.valueOf( s.charAt( 0 ))
                              :"E";
            } else if( i == 4 ) {    // E - Sign
                AccountSign = (s.length() > 0)
                              ?String.valueOf( s.charAt( 0 ))
                              :"N";
            } else if( i == 5 ) {    // F - DocControlled
                IsDocControlled = (s.length() > 0)
                                  ?String.valueOf( s.charAt( 0 ))
                                  :"N";
            } else if( i == 6 ) {    // G - IsSummary
                IsSummary = (s.length() > 0)
                            ?String.valueOf( s.charAt( 0 ))
                            :"N";
            } else if( i == 7 ) {    // H - Default_Account
                Default_Account = s;
            }
        }

        // Ignore if Value & Name are empty (no error message)

        if( ( (Value == null) || (Value.length() == 0) ) && ( (Name == null) || (Name.length() == 0) ) ) {
            return "";
        }

        // Default Account may be blank

        if( (Default_Account == null) || (Default_Account.length() == 0) ) {

            // Default_Account = String.valueOf(s_keyNo++);

            return "";
        }

        // No Summary Account

        if( (IsSummary == null) || (IsSummary.length() == 0) ) {
            IsSummary = "N";
        }

        if( !IsSummary.equals( "N" )) {
            return "";
        }

        // Validation

        if( (AccountType == null) || (AccountType.length() == 0) ) {
            AccountType = "E";
        }

        if( (AccountSign == null) || (AccountSign.length() == 0) ) {
            AccountSign = "N";
        }

        if( (IsDocControlled == null) || (IsDocControlled.length() == 0) ) {
            IsDocControlled = "N";
        }

        // log.config( "Value=" + Value + ", AcctType=" + AccountType
        // + ", Sign=" + AccountSign + ", Doc=" + docControlled
        // + ", Summary=" + summary + " - " + Name + " - " + Description);

        try {

            // Create Account - save later

            MElementValue na = new MElementValue( m_ctx,Value,Name,Description,AccountType,AccountSign,IsDocControlled.toUpperCase().startsWith( "Y" ),IsSummary.toUpperCase().startsWith( "Y" ),m_trxName );

            // Add to ArrayList

            put( Default_Account,na );
        } catch( Exception e ) {
            return( e.getMessage());
        }

        return "";
    }    // parseLine

    /**
     * Descripción de Método
     *
     *
     * @param AD_Client_ID
     * @param AD_Org_ID
     * @param C_Element_ID
     *
     * @return
     */

    public boolean saveAccounts( int AD_Client_ID,int AD_Org_ID,int C_Element_ID ) {
        log.config( "" );

        Iterator iterator = this.values().iterator();

        while( iterator.hasNext()) {
            MElementValue na = ( MElementValue )iterator.next();

            na.setAD_Client_ID( AD_Client_ID );
            na.setAD_Org_ID( AD_Org_ID );
            na.setC_Element_ID( C_Element_ID );

            if( !na.save()) {
                return false;
            }
        }

        return true;
    }    // saveAccounts

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    public int getC_ElementValue_ID( String key ) {
        MElementValue na = ( MElementValue )this.get( key );

        if( na == null ) {
            return 0;
        }

        return na.getC_ElementValue_ID();
    }    // getC_ElementValue_ID
}    // NaturalAccountMap



/*
 *  @(#)NaturalAccountMap.java   02.07.07
 * 
 *  Fin del fichero NaturalAccountMap.java
 *  
 *  Versión 2.2
 *
 */
