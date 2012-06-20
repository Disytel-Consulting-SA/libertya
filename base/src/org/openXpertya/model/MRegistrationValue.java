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

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRegistrationValue extends X_A_RegistrationValue implements Comparable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MRegistrationValue( Properties ctx,int ignored,String trxName ) {
        super( ctx,0,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }
    }    // MRegistrationValue

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRegistrationValue( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRegistrationValue

    /**
     * Constructor de la clase ...
     *
     *
     * @param registration
     * @param A_RegistrationAttribute_ID
     * @param Name
     */

    public MRegistrationValue( MRegistration registration,int A_RegistrationAttribute_ID,String Name ) {
        super( registration.getCtx(),0,registration.get_TrxName());
        setClientOrg( registration );
        setA_Registration_ID( registration.getA_Registration_ID());

        //

        setA_RegistrationAttribute_ID( A_RegistrationAttribute_ID );
        setName( Name );
    }    // MRegistrationValue

    /** Descripción de Campos */

    private String m_registrationAttribute = null;

    /** Descripción de Campos */

    private String m_registrationAttributeDescription = null;

    /** Descripción de Campos */

    private int m_seqNo = -1;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getRegistrationAttribute() {
        if( m_registrationAttribute == null ) {
            int                    A_RegistrationAttribute_ID = getA_RegistrationAttribute_ID();
            MRegistrationAttribute att                        = MRegistrationAttribute.get( getCtx(),A_RegistrationAttribute_ID,get_TrxName());

            m_registrationAttribute            = att.getName();
            m_registrationAttributeDescription = att.getDescription();
            m_seqNo                            = att.getSeqNo();
        }

        return m_registrationAttribute;
    }    // getRegistrationAttribute

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getRegistrationAttributeDescription() {
        if( m_registrationAttributeDescription == null ) {
            getRegistrationAttribute();
        }

        return m_registrationAttributeDescription;
    }    // getRegistrationAttributeDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSeqNo() {
        if( m_seqNo == -1 ) {
            getRegistrationAttribute();
        }

        return m_seqNo;
    }    // getSeqNo

    /**
     * Descripción de Método
     *
     *
     * @param o
     *
     * @return
     */

    public int compareTo( Object o ) {
        if( o == null ) {
            return 0;
        }

        MRegistrationValue oo      = ( MRegistrationValue )o;
        int                compare = getSeqNo() - oo.getSeqNo();

        return compare;
    }    // compareTo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append( getSeqNo()).append( ": " ).append( getRegistrationAttribute()).append( "=" ).append( getName());

        return sb.toString();
    }    // toString
}    // MRegistrationValue



/*
 *  @(#)MRegistrationValue.java   02.07.07
 * 
 *  Fin del fichero MRegistrationValue.java
 *  
 *  Versión 2.2
 *
 */
