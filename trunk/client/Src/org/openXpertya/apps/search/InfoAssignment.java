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



package org.openXpertya.apps.search;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.Lookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InfoAssignment extends Info {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param modal
     * @param WindowNo
     * @param value
     * @param multiSelection
     * @param whereClause
     */

    public InfoAssignment( Frame frame,boolean modal,int WindowNo,String value,boolean multiSelection,String whereClause ) {
        super( frame,modal,WindowNo,"ra","S_ResourceAssigment_ID",multiSelection,whereClause );
        log.info( value );
        setTitle( Msg.getMsg( Env.getCtx(),"InfoAssignment" ));

        //

        if( !initLookups()) {
            return;
        }

        statInit();
        initInfo( value,whereClause );

        //

        int no = p_table.getRowCount();

        setStatusLine( Integer.toString( no ) + " " + Msg.getMsg( Env.getCtx(),"SearchRows_EnterQuery" ),false );
        setStatusDB( Integer.toString( no ));

        // AutoQuery
        // if (value != null && value.length() > 0)
        // executeQuery();

        p_loadedOK = true;
        AEnv.positionCenterWindow( frame,this );
    }    // InfoAssignment

    //

    /** Descripción de Campos */

    private CLabel labelResourceType = new CLabel( Msg.translate( Env.getCtx(),"S_ResourceType_ID" ));

    /** Descripción de Campos */

    private VLookup fieldResourceType;

    /** Descripción de Campos */

    private CLabel labelResource = new CLabel( Msg.translate( Env.getCtx(),"S_Resource_ID" ));

    /** Descripción de Campos */

    private VLookup fieldResource;

    /** Descripción de Campos */

    private CLabel labelFrom = new CLabel( Msg.translate( Env.getCtx(),"DateFrom" ));

    /** Descripción de Campos */

    private VDate fieldFrom = new VDate( DisplayType.Date );

    /** Descripción de Campos */

    private CLabel labelTo = new CLabel( Msg.translate( Env.getCtx(),"DateTo" ));

    /** Descripción de Campos */

    private VDate fieldTo = new VDate( DisplayType.Date );

    /** Descripción de Campos */

    private CButton bNew = new CButton();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean initLookups() {
        try {
            int    AD_Column_ID = 6851;    // S_Resource.S_ResourceType_ID
            Lookup lookup       = MLookupFactory.get( Env.getCtx(),p_WindowNo,0,AD_Column_ID,DisplayType.TableDir );

            fieldResourceType = new VLookup( "S_ResourceType_ID",false,false,true,lookup );
            AD_Column_ID = 6826;    // S_ResourceAssignment.S_Resource_ID
            lookup       = MLookupFactory.get( Env.getCtx(),p_WindowNo,0,AD_Column_ID,DisplayType.TableDir );
            fieldResource = new VLookup( "S_Resource_ID",false,false,true,lookup );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"InfoAssignment.initLookup" );

            return false;
        }

        //

        bNew.setIcon( Env.getImageIcon( "New16.gif" ));

        return true;
    }    // initLookups

    /**
     * Descripción de Método
     *
     */

    private void statInit() {
        parameterPanel.setLayout( new ALayout());
        parameterPanel.add( labelResourceType,new ALayoutConstraint( 0,0 ));
        parameterPanel.add( labelResource,null );
        parameterPanel.add( labelFrom,null );
        parameterPanel.add( labelTo,null );

        // parameterPanel.add(labelPhone, null);
        // parameterPanel.add(checkFuzzy, null);
        //

        parameterPanel.add( fieldResourceType,new ALayoutConstraint( 1,0 ));
        parameterPanel.add( fieldResource,null );
        parameterPanel.add( fieldFrom,null );
        parameterPanel.add( fieldTo,null );
        parameterPanel.add( bNew,null );

        // parameterPanel.add(checkCustomer, null);

    }    // statInit

    /** Descripción de Campos */

    private static String s_assignmentFROM = "S_ResourceAssignment ra, S_ResourceType rt, S_Resource r, C_UOM uom";

    /** Descripción de Campos */

    private static String s_assignmentWHERE = "ra.IsActive='Y' AND ra.S_Resource_ID=r.S_Resource_ID " + "AND r.S_ResourceType_ID=rt.S_ResourceType_ID AND rt.C_UOM_ID=uom.C_UOM_ID";

    /** Descripción de Campos */

    private static Info_Column[] s_assignmentLayout = {
        new Info_Column( " ","ra.S_ResourceAssignment_ID",IDColumn.class ),new Info_Column( Msg.translate( Env.getCtx(),"S_ResourceType_ID" ),"rt.Name",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"S_Resource_ID" ),"r.Name",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"AssignDateFrom" ),"ra.AssignDateFrom",Timestamp.class ),new Info_Column( Msg.translate( Env.getCtx(),"Qty" ),"ra.Qty",Double.class ),new Info_Column( Msg.translate( Env.getCtx(),"C_UOM_ID" ),"uom.UOMSymbol",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"AssignDateTo" ),"ra.AssignDateTo",Timestamp.class ),new Info_Column( Msg.translate( Env.getCtx(),"IsConfirmed" ),"ra.IsConfirmed",Boolean.class )
    };

    /**
     * Descripción de Método
     *
     *
     * @param value
     * @param whereClause
     */

    private void initInfo( String value,String whereClause ) {

        // C_BPartner bp, AD_User c, C_BPartner_Location l, C_Location a

        // Create Grid

        StringBuffer where = new StringBuffer( s_assignmentWHERE );

        if( (whereClause != null) && (whereClause.length() > 0) ) {
            where.append( " AND " ).append( whereClause );
        }

        //

        prepareTable( s_assignmentLayout,s_assignmentFROM,where.toString(),"rt.Name,r.Name,ra.AssignDateFrom" );
    }    // initInfo

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // don't requery if fieldValue and fieldName are empty
        // return;
        //

        super.actionPerformed( e );
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String getSQLWhere() {
        StringBuffer sql = new StringBuffer();

        //

        Integer S_ResourceType_ID = ( Integer )fieldResourceType.getValue();

        if( S_ResourceType_ID != null ) {
            sql.append( " AND rt.S_ResourceType_ID=" ).append( S_ResourceType_ID.intValue());
        }

        //

        Integer S_Resource_ID = ( Integer )fieldResource.getValue();

        if( S_Resource_ID != null ) {
            sql.append( " AND r.S_Resource_ID=" ).append( S_Resource_ID.intValue());
        }

        //

        Timestamp ts = fieldFrom.getTimestamp();

        if( ts != null ) {
            sql.append( " AND TRUNC(ra.AssignDateFrom)>=" ).append( DB.TO_DATE( ts,false ));
        }

        //

        ts = fieldTo.getTimestamp();

        if( ts != null ) {
            sql.append( " AND TRUNC(ra.AssignDateTo)<=" ).append( DB.TO_DATE( ts,false ));
        }

        return sql.toString();
    }    // getSQLWhere

    /**
     * Descripción de Método
     *
     *
     * @param pstmt
     *
     * @throws SQLException
     */

    protected void setParameters( PreparedStatement pstmt ) throws SQLException {}

    /**
     * Descripción de Método
     *
     */

    void showHistory() {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasHistory() {
        return false;
    }

    /**
     * Descripción de Método
     *
     */

    void customize() {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasCustomize() {
        return false;
    }

    /**
     * Descripción de Método
     *
     */

    void zoom() {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasZoom() {
        return false;
    }

    /**
     * Descripción de Método
     *
     */

    void saveSelectionDetail() {}
}    // InfoAssignment



/*
 *  @(#)InfoAssignment.java   02.07.07
 * 
 *  Fin del fichero InfoAssignment.java
 *  
 *  Versión 2.2
 *
 */
