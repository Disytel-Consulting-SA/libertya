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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CLabel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MQuery;
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

public class InfoAsset extends Info {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param modal
     * @param WindowNo
     * @param A_Asset_ID
     * @param value
     * @param multiSelection
     * @param whereClause
     */

    public InfoAsset( Frame frame,boolean modal,int WindowNo,int A_Asset_ID,String value,boolean multiSelection,String whereClause ) {
        super( frame,modal,WindowNo,"a","A_Asset_ID",multiSelection,whereClause );
        log.info( value + ", ID=" + A_Asset_ID + ", WHERE=" + whereClause );
        setTitle( Msg.getMsg( Env.getCtx(),"InfoAsset" ));

        //

        statInit();
        initInfo( value,A_Asset_ID,whereClause );

        //

        int no = p_table.getRowCount();

        setStatusLine( Integer.toString( no ) + " " + Msg.getMsg( Env.getCtx(),"SearchRows_EnterQuery" ),false );
        setStatusDB( Integer.toString( no ));

        // AutoQuery

        if( (value != null) && (value.length() > 0) ) {
            executeQuery();
        }

        p_loadedOK = true;

        // Focus
        // fieldValue.requestFocus();

        AEnv.positionCenterWindow( frame,this );
    }    // InfoProduct

    /** Descripción de Campos */

    private static String s_assetFROM = "A_ASSET a" + " LEFT OUTER JOIN M_Product p ON (a.M_Product_ID=p.M_Product_ID)" + " LEFT OUTER JOIN C_BPartner bp ON (a.C_BPartner_ID=bp.C_BPartner_ID)" + " LEFT OUTER JOIN AD_User u ON (a.AD_User_ID=u.AD_User_ID)";

    /** Descripción de Campos */

    private static final Info_Column[] s_assetLayout = {
        new Info_Column( " ","a.A_Asset_ID",IDColumn.class ),new Info_Column( Msg.translate( Env.getCtx(),"Value" ),"a.Value",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"Name" ),"a.Name",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"M_Product_ID" ),"p.Name",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"C_BPartner_ID" ),"bp.Name",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"AD_User_ID" ),"u.Name",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"AssetServiceDate" ),"a.AssetServiceDate",Timestamp.class ),new Info_Column( Msg.translate( Env.getCtx(),"GuaranteeDate" ),"a.GuaranteeDate",Timestamp.class ),new Info_Column( Msg.translate( Env.getCtx(),"VersionNo" ),"a.VersionNo",String.class )
    };

    //

    /** Descripción de Campos */

    private CLabel labelValue = new CLabel();

    /** Descripción de Campos */

    private CTextField fieldValue = new CTextField( 10 );

    /** Descripción de Campos */

    private CLabel labelName = new CLabel();

    /** Descripción de Campos */

    private CTextField fieldName = new CTextField( 10 );

    //

    /** Descripción de Campos */

    private CLabel lBPartner_ID = new CLabel( Msg.translate( Env.getCtx(),"C_BPartner_ID" ));

    /** Descripción de Campos */

    private VLookup fBPartner_ID;

    /** Descripción de Campos */

    private CLabel lProduct_ID = new CLabel( Msg.translate( Env.getCtx(),"M_Product_ID" ));

    /** Descripción de Campos */

    private VLookup fProduct_ID;

    /**
     * Descripción de Método
     *
     */

    private void statInit() {
        labelValue.setText( Msg.getMsg( Env.getCtx(),"Value" ));
        fieldValue.setBackground( CompierePLAF.getInfoBackground());
        fieldValue.addActionListener( this );
        labelName.setText( Msg.getMsg( Env.getCtx(),"Name" ));
        fieldName.setBackground( CompierePLAF.getInfoBackground());
        fieldName.addActionListener( this );

        // From A_Asset.

        fBPartner_ID = new VLookup( "C_BPartner_ID",false,false,true,MLookupFactory.get( Env.getCtx(),p_WindowNo,0,8065,DisplayType.Search ));
        lBPartner_ID.setLabelFor( fBPartner_ID );
        fBPartner_ID.setBackground( CompierePLAF.getInfoBackground());
        fProduct_ID = new VLookup( "M_Product_ID",false,false,true,MLookupFactory.get( Env.getCtx(),p_WindowNo,0,8047,DisplayType.Search ));
        lProduct_ID.setLabelFor( fProduct_ID );
        fProduct_ID.setBackground( CompierePLAF.getInfoBackground());

        //

        parameterPanel.setLayout( new ALayout());

        //

        parameterPanel.add( labelValue,new ALayoutConstraint( 0,0 ));
        parameterPanel.add( fieldValue,null );
        parameterPanel.add( lBPartner_ID,null );
        parameterPanel.add( fBPartner_ID,null );

        //

        parameterPanel.add( labelName,new ALayoutConstraint( 1,0 ));
        parameterPanel.add( fieldName,null );
        parameterPanel.add( lProduct_ID,null );
        parameterPanel.add( fProduct_ID,null );
    }    // statInit

    /**
     * Descripción de Método
     *
     *
     * @param value
     * @param A_Asset_ID
     * @param whereClause
     */

    private void initInfo( String value,int A_Asset_ID,String whereClause ) {

        // Create Grid

        StringBuffer where = new StringBuffer();

        where.append( "a.IsActive='Y'" );

        if( (whereClause != null) && (whereClause.length() > 0) ) {
            where.append( " AND " ).append( whereClause );
        }

        //

        prepareTable( s_assetLayout,s_assetFROM,where.toString(),"a.Value" );

        // Set Value

        if( value == null ) {
            value = "%";
        }

        if( !value.endsWith( "%" )) {
            value += "%";
        }
    }    // initInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String getSQLWhere() {
        StringBuffer sql = new StringBuffer();

        // => Value

        String value = fieldValue.getText().toUpperCase();

        if( !( value.equals( "" ) || value.equals( "%" ))) {
            sql.append( " AND UPPER(a.Value) LIKE ?" );
        }

        // => Name

        String name = fieldName.getText().toUpperCase();

        if( !( name.equals( "" ) || name.equals( "%" ))) {
            sql.append( " AND UPPER(a.Name) LIKE ?" );
        }

        // C_BPartner_ID

        Integer C_BPartner_ID = ( Integer )fBPartner_ID.getValue();

        if( C_BPartner_ID != null ) {
            sql.append( " AND a.C_BPartner_ID=" ).append( C_BPartner_ID );
        }

        // M_Product_ID

        Integer M_Product_ID = ( Integer )fProduct_ID.getValue();

        if( M_Product_ID != null ) {
            sql.append( " AND a.M_Product_ID=" ).append( M_Product_ID );
        }

        //

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

    protected void setParameters( PreparedStatement pstmt ) throws SQLException {
        int index = 1;

        // => Value

        String value = fieldValue.getText().toUpperCase();

        if( !( value.equals( "" ) || value.equals( "%" ))) {
            if( !value.endsWith( "%" )) {
                value += "%";
            }

            pstmt.setString( index++,value );
            log.fine( "Value: " + value );
        }

        // => Name

        String name = fieldName.getText().toUpperCase();

        if( !( name.equals( "" ) || name.equals( "%" ))) {
            if( !name.endsWith( "%" )) {
                name += "%";
            }

            pstmt.setString( index++,name );
            log.fine( "Name: " + name );
        }
    }    // setParameters

    /**
     * Descripción de Método
     *
     */

    public void saveSelectionDetail() {
        int row = p_table.getSelectedRow();

        if( row == -1 ) {
            return;
        }

        // publish for Callout to read

        Integer ID = getSelectedRowKey();

        Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"A_Asset_ID",(ID == null)
                ?"0"
                :ID.toString());
    }    // saveSelectionDetail

    /**
     * Descripción de Método
     *
     */

    void showHistory() {
        log.info( "InfoAsset.showHistory" );
    }    // showHistory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasHistory() {
        return false;
    }    // hasHistory

    /**
     * Descripción de Método
     *
     */

    void zoom() {
        log.info( "InfoAsset.zoom" );

        Integer A_Asset_ID = getSelectedRowKey();

        if( A_Asset_ID == null ) {
            return;
        }

        MQuery query = new MQuery( "A_Asset" );

        query.addRestriction( "A_Asset_ID",MQuery.EQUAL,A_Asset_ID );

        int AD_WindowNo = getAD_Window_ID( "A_Asset",true );

        zoom( AD_WindowNo,query );
    }    // zoom

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasZoom() {
        return true;
    }    // hasZoom

    /**
     * Descripción de Método
     *
     */

    void customize() {
        log.info( "InfoAsset.customize" );
    }    // customize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasCustomize() {
        return false;    // for now
    }                    // hasCustomize
}    // InfoAsset



/*
 *  @(#)InfoAsset.java   02.07.07
 * 
 *  Fin del fichero InfoAsset.java
 *  
 *  Versión 2.2
 *
 */
