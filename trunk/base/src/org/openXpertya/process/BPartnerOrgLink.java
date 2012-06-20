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



package org.openXpertya.process;

import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MLocator;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MRoleOrgAccess;
import org.openXpertya.model.MWarehouse;
import org.openXpertya.util.ErrorUsuarioOXP;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class BPartnerOrgLink extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_Org_ID;

    /** Descripción de Campos */

    private int p_AD_OrgType_ID;

    /** Descripción de Campos */

    private int p_C_BPartner_ID;

    /** Descripción de Campos */

    private int p_AD_Role_ID;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Org_ID" )) {
                p_AD_Org_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "AD_OrgType_ID" )) {
                p_AD_OrgType_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "AD_Role_ID" )) {
                p_AD_Role_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_C_BPartner_ID = getRecord_ID();
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "C_BPartner_ID=" + p_C_BPartner_ID + ", AD_Org_ID=" + p_AD_Org_ID + ", AD_OrgType_ID=" + p_AD_OrgType_ID + ", AD_Role_ID=" + p_AD_Role_ID );

        if( p_C_BPartner_ID == 0 ) {
            throw new ErrorUsuarioOXP( "No Business Partner ID" );
        }

        MBPartner bp = new MBPartner( getCtx(),p_C_BPartner_ID,get_TrxName());

        if( bp.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "Business Partner not found - C_BPartner_ID=" + p_C_BPartner_ID );
        }

        // BP Location

        MBPartnerLocation[] locs = bp.getLocations( false );

        if( (locs == null) || (locs.length == 0) ) {
            throw new IllegalArgumentException( "Business Partner has no Location" );
        }

        // Location

        int C_Location_ID = locs[ 0 ].getC_Location_ID();

        if( C_Location_ID == 0 ) {
            throw new IllegalArgumentException( "Business Partner Location has no Address" );
        }

        // Create Org

        boolean newOrg = p_AD_Org_ID == 0;
        MOrg    org    = new MOrg( getCtx(),p_AD_Org_ID,get_TrxName());

        if( newOrg ) {
            org.setValue( bp.getValue());
            org.setName( bp.getName());
            org.setDescription( bp.getDescription());

            if( !org.save()) {
                throw new Exception( "Organization not saved" );
            }
        } else    // check if linked to already
        {
            int C_BPartner_ID = org.getLinkedC_BPartner_ID();

            if( C_BPartner_ID > 0 ) {
                throw new IllegalArgumentException( "Organization '" + org.getName() + "' already linked (to C_BPartner_ID=" + C_BPartner_ID + ")" );
            }
        }

        p_AD_Org_ID = org.getAD_Org_ID();

        // Update Org Info

        MOrgInfo oInfo = org.getInfo();

        oInfo.setAD_OrgType_ID( p_AD_OrgType_ID );

        if( newOrg ) {
            oInfo.setC_Location_ID( C_Location_ID );
        }

        // Create Warehouse

        MWarehouse wh = null;

        if( !newOrg ) {
            MWarehouse[] whs = MWarehouse.getForOrg( getCtx(),p_AD_Org_ID );

            if( (whs != null) && (whs.length > 0) ) {
                wh = whs[ 0 ];    // pick first
            }
        }

        // New Warehouse

        if( wh == null ) {
            wh = new MWarehouse( org );

            if( !wh.save()) {
                throw new Exception( "Warehouse not saved" );
            }
        }

        // Create Locator

        MLocator mLoc = wh.getDefaultLocator();

        if( mLoc == null ) {
            mLoc = new MLocator( wh,"Standard" );
            mLoc.setIsDefault( true );
            mLoc.save();
        }

        // Update/Save Org Info

        oInfo.setM_Warehouse_ID( wh.getM_Warehouse_ID());

        if( !oInfo.save()) {
            throw new Exception( "Organization Info not saved" );
        }

        // Update BPartner

        bp.setAD_OrgBP_ID( p_AD_Org_ID );

        if( bp.getAD_Org_ID() != 0 ) {
            bp.setClientOrg( bp.getAD_Client_ID(),0 );    // Shared BPartner
        }

        // Save BP

        if( !bp.save()) {
            throw new Exception( "Business Partner not updated" );
        }

        // Limit to specific Role

        if( p_AD_Role_ID != 0 ) {
            boolean          found       = false;
            MRoleOrgAccess[] orgAccesses = MRoleOrgAccess.getOfOrg( getCtx(),p_AD_Org_ID );

            // delete all accesses except the specific

            for( int i = 0;i < orgAccesses.length;i++ ) {
                if( orgAccesses[ i ].getAD_Role_ID() == p_AD_Role_ID ) {
                    found = true;
                } else {
                    orgAccesses[ i ].delete( true );
                }
            }

            // create access

            if( !found ) {
                MRoleOrgAccess orgAccess = new MRoleOrgAccess( org,p_AD_Role_ID );

                orgAccess.save();
            }
        }

        // Reset Client Role

        MRole.getDefault( getCtx(),true );

        return "Business Partner - Organization Link created";
    }    // doIt
}    // BPartnerOrgLink



/*
 *  @(#)BPartnerOrgLink.java   02.07.07
 * 
 *  Fin del fichero BPartnerOrgLink.java
 *  
 *  Versión 2.2
 *
 */
