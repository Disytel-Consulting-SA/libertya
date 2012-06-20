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

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MDunningRunEntry extends X_C_DunningRunEntry {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_DunningRunEntry_ID
     * @param trxName
     */

    public MDunningRunEntry( Properties ctx,int C_DunningRunEntry_ID,String trxName ) {
        super( ctx,C_DunningRunEntry_ID,trxName );

        if( C_DunningRunEntry_ID == 0 ) {

            // setC_BPartner_ID (0);
            // setC_BPartner_Location_ID (0);
            // setAD_User_ID (0);

            // setSalesRep_ID (0);
            // setC_Currency_ID (0);

            setAmt( Env.ZERO );
            setQty( Env.ZERO );
            setProcessed( false );
        }
    }    // MDunningRunEntry

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MDunningRunEntry( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MDunningRunEntry

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     */

    public MDunningRunEntry( MDunningRun parent ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setC_DunningRun_ID( parent.getC_DunningRun_ID());
        m_parent = parent;
    }    // MDunningRunEntry

    /** Descripción de Campos */

    private MDunningRun m_parent = null;

    /**
     * Descripción de Método
     *
     *
     * @param bp
     * @param isSOTrx
     */

    public void setBPartner( MBPartner bp,boolean isSOTrx ) {
        setC_BPartner_ID( bp.getC_BPartner_ID());

        MBPartnerLocation[] locations = bp.getLocations( false );

        // Location

        if( locations.length == 1 ) {
            setC_BPartner_Location_ID( locations[ 0 ].getC_BPartner_Location_ID());
        } else {
            for( int i = 0;i < locations.length;i++ ) {
                MBPartnerLocation location = locations[ i ];

                if(( location.isPayFrom() && isSOTrx ) || ( location.isRemitTo() &&!isSOTrx )) {
                    setC_BPartner_Location_ID( location.getC_BPartner_Location_ID());

                    break;
                }
            }
        }

        if( getC_BPartner_Location_ID() == 0 ) {
            String msg = "@C_BPartner_ID@ " + bp.getName();

            if( isSOTrx ) {
                msg += " @No@ @IsPayFrom@";
            } else {
                msg += " @No@ @IsRemitTo@";
            }

            throw new IllegalArgumentException( msg );
        }

        // User with location

        MUser[] users = MUser.getOfBPartner( getCtx(),bp.getC_BPartner_ID());

        if( users.length == 1 ) {
            setAD_User_ID( users[ 0 ].getAD_User_ID());
        } else {
            for( int i = 0;i < users.length;i++ ) {
                MUser user = users[ i ];

                if( user.getC_BPartner_Location_ID() == getC_BPartner_Location_ID()) {
                    setAD_User_ID( users[ i ].getAD_User_ID());

                    break;
                }
            }
        }

        //

        setSalesRep_ID( bp.getSalesRep_ID());
    }    // setBPartner
}    // MDunningRunEntry



/*
 *  @(#)MDunningRunEntry.java   02.07.07
 * 
 *  Fin del fichero MDunningRunEntry.java
 *  
 *  Versión 2.2
 *
 */
