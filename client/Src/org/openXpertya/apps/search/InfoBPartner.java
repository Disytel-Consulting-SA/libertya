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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CLabel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.grid.ed.VCheckBox;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRole;
import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InfoBPartner extends Info {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param modal
     * @param WindowNo
     * @param value
     * @param isSOTrx
     * @param multiSelection
     * @param whereClause
     */

    public InfoBPartner( Frame frame,boolean modal,int WindowNo,String value,boolean isSOTrx,boolean multiSelection,String whereClause ) {
        super( frame,modal,WindowNo,"C_BPartner","C_BPartner_ID",multiSelection,whereClause );
        this.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        log.info( value );
        setTitle( Msg.getMsg( Env.getCtx(),"InfoBPartner" ));
        m_isSOTrx = isSOTrx;

        //

        statInit();
        initInfo( value,whereClause );

        //

        int no = p_table.getRowCount();

        setStatusLine( Integer.toString( no ) + " " + Msg.getMsg( Env.getCtx(),"SearchRows_EnterQuery" ),false );
        setStatusDB( Integer.toString( no ));

        // AutoQuery

        /* dREHER, hago que siempre ejecute query, para displayar todo el archivo
        if( (value != null) && (value.length() > 0) ) {
            executeQuery();
        }
        */
        executeQuery();

        p_loadedOK = true;

        // Focus

        // fieldValue.requestFocus();
        
        // dREHER, setear foco en nombre por defecto
        // TODO: bug no setea foco, cambie orden de presentacion de campos, revisar
        fieldName.requestFocus();
        
        
        AEnv.positionCenterWindow( frame,this );
    }    // InfoBPartner

    /** Descripción de Campos */

    private boolean m_isSOTrx = false;

    /** Descripción de Campos */

    private int m_AD_User_ID_index = -1;

    /** Descripción de Campos */
    
 // dREHER, columna que muestra el nombre del Socio del Negocio
    protected int INDEX_NAME = 2;
 // dREHER, guardo la ultima linea seleccionada
    private int lastSelectedRow = 0;

    private int m_C_BPartner_Location_ID_index = -1;

   /** Descripción de Campos */
   private static String s_partnerFROM = 	"C_BPartner" +
    										" LEFT OUTER JOIN AD_User c ON (C_BPartner.C_BPartner_ID=c.C_BPartner_ID AND c.IsActive='Y')" + 
    										" LEFT OUTER JOIN C_BPartner_Location l ON (C_BPartner.C_BPartner_ID=l.C_BPartner_ID AND l.IsActive='Y')" + 
    										" LEFT OUTER JOIN C_Location a ON (l.C_Location_ID=a.C_Location_ID) " +
   											" INNER JOIN (SELECT MAX (C_BPartner_Location_ID) as C_BPartner_Location_ID FROM C_BPartner_Location WHERE IsActive='Y' Group BY C_BPartner_ID) AS foo ON l.C_BPartner_Location_ID = foo.C_BPartner_Location_ID ";
   
    /** Descripción de Campos */

    private static Info_Column[] s_partnerLayout = {
        new Info_Column( " ","C_BPartner.C_BPartner_ID",IDColumn.class ),new Info_Column( Msg.translate( Env.getCtx(),"Value" ),"C_BPartner.Value",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"Name" ),"C_BPartner.Name",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"Contact" ),"c.Name AS Contact",KeyNamePair.class,"c.AD_User_ID" ),new Info_Column( Msg.translate( Env.getCtx(),"SO_CreditAvailable" ),"C_BPartner.SO_CreditLimit-C_BPartner.SO_CreditUsed AS SO_CreditAvailable",BigDecimal.class,true,true,null ),new Info_Column( Msg.translate( Env.getCtx(),"SO_CreditUsed" ),"C_BPartner.SO_CreditUsed",BigDecimal.class ),new Info_Column( Msg.translate( Env.getCtx(),"Phone" ),"c.Phone",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"Postal" ),"a.Postal",KeyNamePair.class,"l.C_BPartner_Location_ID" ),new Info_Column( Msg.translate( Env.getCtx(),"City" ),"a.City",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"TotalOpenBalance" ),"C_BPartner.TotalOpenBalance",BigDecimal.class ),new Info_Column( Msg.translate( Env.getCtx(),"Revenue" ),"C_BPartner.ActualLifetimeValue",BigDecimal.class ), new Info_Column( Msg.translate( Env.getCtx(),"TaxID" ),"C_BPartner.TaxID",String.class ) 
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

    /** Descripción de Campos */

    private CLabel labelContact = new CLabel();

    /** Descripción de Campos */

    private CTextField fieldContact = new CTextField( 10 );

    /** Descripción de Campos */

    private CLabel labelEMail = new CLabel();

    /** Descripción de Campos */

    private CTextField fieldEMail = new CTextField( 10 );

    /** Descripción de Campos */

    private CLabel labelPostal = new CLabel();

    /** Descripción de Campos */

    private CTextField fieldPostal = new CTextField( 10 );

    /** Descripción de Campos */

    private CLabel labelPhone = new CLabel();

    /** Descripción de Campos */

    private CTextField fieldPhone = new CTextField( 10 );
    
    /** Descripción de Campos */

    private CLabel labelTaxID = new CLabel();

    /** Descripción de Campos */

    private CTextField fieldTaxID = new CTextField( 10 );

    /** Descripción de Campos */

    private VCheckBox checkAND = new VCheckBox();

    /** Descripción de Campos */

    private VCheckBox checkCustomer = new VCheckBox();

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
        labelContact.setText( Msg.getMsg( Env.getCtx(),"Contact" ));
        fieldContact.setBackground( CompierePLAF.getInfoBackground());
        fieldContact.addActionListener( this );
        labelEMail.setText( Msg.getMsg( Env.getCtx(),"EMail" ));
        fieldEMail.setBackground( CompierePLAF.getInfoBackground());
        fieldEMail.addActionListener( this );
        labelPostal.setText( Msg.getMsg( Env.getCtx(),"Postal" ));
        fieldPostal.setBackground( CompierePLAF.getInfoBackground());
        fieldPostal.addActionListener( this );
        labelPhone.setText( Msg.translate( Env.getCtx(),"Phone" ));
        fieldPhone.setBackground( CompierePLAF.getInfoBackground());
        fieldPhone.addActionListener( this );
        labelTaxID.setText( Msg.translate( Env.getCtx(),"TaxID" ));
        fieldTaxID.setBackground( CompierePLAF.getInfoBackground());
        fieldTaxID.addActionListener( this );
        checkAND.setText( Msg.getMsg( Env.getCtx(),"SearchAND" ));
        checkAND.setToolTipText( Msg.getMsg( Env.getCtx(),"SearchANDInfo" ));
        checkAND.setSelected( true );
        checkAND.addActionListener( this );

        checkCustomer.setSelected( true );
        checkCustomer.setFocusable( false );
        checkCustomer.setRequestFocusEnabled( false );
        checkCustomer.addActionListener( this );

        //

        parameterPanel.setLayout( new ALayout());

        //
        
 // dREHER, reestructuro orden para setear por defecto Name y no Value
        
        parameterPanel.add( labelName,new ALayoutConstraint( 0,0 ));
        parameterPanel.add( fieldName,null );
        // parameterPanel.add( labelValue,new ALayoutConstraint( 0,0 ));
        // parameterPanel.add( fieldValue,null );

        parameterPanel.add( labelContact,null );
        parameterPanel.add( fieldContact,null );
        parameterPanel.add( labelPhone,null );
        parameterPanel.add( fieldPhone,null );
        parameterPanel.add( checkCustomer,null );

        //
        // dREHER
        // parameterPanel.add( labelName,new ALayoutConstraint( 1,0 ));
        // parameterPanel.add( fieldName,null );
        
        parameterPanel.add( labelEMail,new ALayoutConstraint( 1,0 ) );
        parameterPanel.add( fieldEMail,null );
		// Si la localización argentina está activa entonces coloco el campo
		// para búsqueda por CUIT o DNI, sino el codigo postal caso contrario
		boolean localeARActive = CalloutInvoiceExt
				.ComprobantesFiscalesActivos();
        if(localeARActive){
            parameterPanel.add( labelTaxID,null );
            parameterPanel.add( fieldTaxID,null );
        }
        else{
        	parameterPanel.add( labelPostal,null );
        	parameterPanel.add( fieldPostal,null );
        }
        parameterPanel.add( checkAND,null );
        
        if( m_isSOTrx ) {
            checkCustomer.setText( Msg.getMsg( Env.getCtx(),"OnlyCustomers" ));
            parameterPanel.remove(checkAND);
        } else {
            checkCustomer.setText( Msg.getMsg( Env.getCtx(),"OnlyVendors" ));
            parameterPanel.remove(checkAND);
        }
        
        // dREHER, cuando escribo sobre el nombre busco incrementalmente en el listado de socios disponible
		this.fieldName.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
//				System.out.println("tecla=" + ke.getKeyCode() + ":" + ke.getKeyChar());
				x_refresh(ke.getKeyChar(), ke.getKeyCode());
			}
			
			public void keyPressed(KeyEvent arg0) 
			{
				int keyCode = Integer.valueOf(arg0.getKeyCode());
				// Si hay una fila seleccionada y se presiona enter, entonces dispose
				if(p_table.getSelectedRow() > -1
						&& keyCode==10) // enter
					dispose(true);
			}
			
		});
        
        
    }    // statInit
    
    /* dREHER, este metodo realiza la busqueda incremental sobre los productos */
    private void x_refresh(char c, int code) {
		// String seek = String.valueOf(c).toLowerCase();

		String seek = this.fieldName.getText().trim().replace("%", "");
		int row = this.p_table.getSelectedRow();
		int rows = this.p_table.getRowCount();
		boolean find = false;

		// si no hay criterio especificado, deseleccionar de la p_table
		// esto es para permitir al usuario realizar una nueva búsqueda
		if (seek.length() == 0)
		{
			p_table.clearSelection();
			return; 
		}
		
		if(row > 0)
			lastSelectedRow = row;
		
//		System.out.println(lastSelectedRow);
		
		for (int i = 0; i < rows; i++) {
			String cont = "";
			Object x = this.p_table.getValueAt(i, INDEX_NAME);
			if (x != null)
				cont = (String) x.toString().toLowerCase();

			if (cont.indexOf(seek) > -1) {
				this.p_table.setRowSelectionInterval(i, i);
				this.p_table.scrollRectToVisible(this.p_table.getCellRect(i, 0,
						true));
				find = true;
				break;
			}
		}

		if (!find && row > -1) {
			this.p_table.setRowSelectionInterval(row, row);
		}

	}
                                             
    /**
     * Descripción de Método
     *
     *
     * @param value
     * @param whereClause
     */

    private void initInfo( String value,String whereClause ) {

        // Create Grid

        StringBuffer where = new StringBuffer();

        where.append( "C_BPartner.IsSummary='N' AND C_BPartner.IsActive='Y'" );
// Comentado: utilizar IN no es performante.  En lugar de ésto, se incluyo un nuevo inner join en s_partnerFROM
//        where.append(" AND l.C_BPartner_Location_ID IN (SELECT MAX (C_BPartner_Location_ID) FROM C_BPartner_Location WHERE IsActive='Y' Group BY C_BPartner_ID )");        
        
        if( (whereClause != null) && (whereClause.length() > 0) ) {
            where.append( " AND " ).append( whereClause );
        }

        //

        prepareTable( s_partnerLayout,s_partnerFROM,where.toString(),"C_BPartner.Value" );

        // Get indexes

        for( int i = 0;i < p_layout.length;i++ ) {
            if( p_layout[ i ].getIDcolSQL().indexOf( "AD_User_ID" ) != -1 ) {
                m_AD_User_ID_index = i;
            }

            if( p_layout[ i ].getIDcolSQL().indexOf( "C_BPartner_Location_ID" ) != -1 ) {
                m_C_BPartner_Location_ID_index = i;
            }
        }

        // Set Value

        if( value == null ) {
            value = "%";
        }

        if( !value.endsWith( "%" )) {
            value += "%";
        }
        
        // Put query string in Name if not numeric

        if( value.equals( "%" )) {
            fieldName.setText( value );

            // No Numbers entered

        } else if(( value.indexOf( "0" ) + value.indexOf( "1" ) + value.indexOf( "2" ) + value.indexOf( "3" ) + value.indexOf( "4" ) + value.indexOf( "5" ) + value.indexOf( "6" ) + value.indexOf( "7" ) + value.indexOf( "8" ) + value.indexOf( "9" )) == -10 ) {
            if( value.startsWith( "%" )) {
                fieldName.setText( value );
            } else {
                fieldName.setText( "%" + value );
            }
        }

        // Number entered

        else {
            boolean forTaxID = bPartnerFor("TaxID", value) > 0;
            boolean forValue = bPartnerFor("Value", value) > 0;
        	if(forTaxID && !forValue){
        		fieldTaxID.setText( value );
        	}
        	else{
        		fieldValue.setText( value );
        	}
        }
    }    // initInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String getSQLWhere() {
        ArrayList list = new ArrayList();

        // => Value

        String value = fieldValue.getText().toUpperCase();

        if( !( value.equals( "" ) || value.equals( "%" ))) {
            list.add( "UPPER(C_BPartner.Value) LIKE ?" );
        }

        // => Name

        String name = fieldName.getText().toUpperCase();

        if( !( name.equals( "" ) || name.equals( "%" ))) {
            list.add( "UPPER(C_BPartner.Name) LIKE ?" );
        }

        // => Contact

        String contact = fieldContact.getText().toUpperCase();

        if( !( contact.equals( "" ) || contact.equals( "%" ))) {
            list.add( "UPPER(c.Name) LIKE ?" );
        }

        // => EMail

        String email = fieldEMail.getText().toUpperCase();

        if( !( email.equals( "" ) || email.equals( "%" ))) {
            list.add( "UPPER(c.EMail) LIKE ?" );
        }

        // => Phone

        String phone = fieldPhone.getText().toUpperCase();

        if( !( phone.equals( "" ) || phone.equals( "%" ))) {
            list.add( "UPPER(c.Phone) LIKE ?" );
        }

        // => Postal

        String postal = fieldPostal.getText().toUpperCase();

        if( !( postal.equals( "" ) || postal.equals( "%" ))) {
            list.add( "UPPER(a.Postal) LIKE ?" );
        }

        // => TaxID

        String taxID = fieldTaxID.getText().toUpperCase();

        if( !( taxID.equals( "" ) || taxID.equals( "%" ))) {
            list.add( "UPPER(C_BPartner.TaxID) LIKE ?" );
        }

        
        StringBuffer sql  = new StringBuffer();
        int          size = list.size();

        // Just one

        if( size == 1 ) {
            sql.append( " AND " ).append( list.get( 0 ));
        } else if( size > 1 ) {
            boolean AND = checkAND.isSelected();

            sql.append( " AND " );

            if( !AND ) {
                sql.append( "(" );
            }

            for( int i = 0;i < size;i++ ) {
                if( i > 0 ) {
                    sql.append( AND
                                ?" AND "
                                :" OR " );
                }

                sql.append( list.get( i ));
            }

            if( !AND ) {
                sql.append( ")" );
            }
        }

        // Static SQL

        if( checkCustomer.isSelected()) {
            sql.append( " AND " );

            if( m_isSOTrx ) {
                sql.append( "C_BPartner.IsCustomer='Y'" );
            } else {
                sql.append( "C_BPartner.IsVendor='Y'" );
            }
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

    protected void setParameters( PreparedStatement pstmt ) throws SQLException {
        int index = 1;

        // => Value

        String value = fieldValue.getText().toUpperCase();

        if( !( value.equals( "" ) || value.equals( "%" ))) {
            // if( !value.endsWith( "%" )) {
            // Modificado por el if siguiente para que no ingrese siempre el % al final del value 
            if( value.equals( "" ) ) {	
                value += "%";
            }

            pstmt.setString( index++,value );
            log.fine( "Value: " + value );
        }

        // => Name

        String name = fieldName.getText().toUpperCase();

        if( !( name.equals( "" ) || name.equals( "%" ))) {
            // if( !name.endsWith( "%" )) {
            // Modificado por el if siguiente para que no ingrese siempre el % al final del name 
            if( name.equals( "" ) ) {
                name += "%";
            }

            pstmt.setString( index++,name );
            log.fine( "Name: " + name );
        }

        // => Contact

        String contact = fieldContact.getText().toUpperCase();

        if( !( contact.equals( "" ) || contact.equals( "%" ))) {
            // if( !contact.endsWith( "%" )) {
            // Modificado por el if siguiente para que no ingrese siempre el % al final del contact 
            if( contact.equals( "" ) ) {
                contact += "%";
            }

            pstmt.setString( index++,contact );
            log.fine( "Contact: " + contact );
        }

        // => EMail

        String email = fieldEMail.getText().toUpperCase();

        if( !( email.equals( "" ) || email.equals( "%" ))) {
            //if( !email.endsWith( "%" )) {
            // Modificado por el if siguiente para que no ingrese siempre el % al final del email
            if( email.equals( "" ) ) {
                email += "%";
            }

            pstmt.setString( index++,email );
            log.fine( "EMail: " + email );
        }

        // => Phone

        String phone = fieldPhone.getText().toUpperCase();

        if( !( phone.equals( "" ) || phone.equals( "%" ))) {
            //if( !phone.endsWith( "%" )) {
            // Modificado por el if siguiente para que no ingrese siempre el % al final del phone
            if( phone.equals( "" ) ) {
                phone += "%";
            }

            pstmt.setString( index++,phone );
            log.fine( "Phone: " + phone );
        }

        // => Postal

        String postal = fieldPostal.getText().toUpperCase();

        if( !( postal.equals( "" ) || postal.equals( "%" ))) {
            //if( !postal.endsWith( "%" )) {
            // Modificado por el if siguiente para que no ingrese siempre el % al final del postal
            if( postal.equals( "" ) ) {
                postal += "%";
            }

            pstmt.setString( index++,postal );
            log.fine( "Postal: " + postal );
        }
        
        // => TaxID

        String taxID = fieldTaxID.getText().toUpperCase();

        if( !( taxID.equals( "" ) || taxID.equals( "%" ))) {
            //if( !postal.endsWith( "%" )) {
            // Modificado por el if siguiente para que no ingrese siempre el % al final del taxID
            if( taxID.equals( "" ) ) {
            	taxID += "%";
            }

            pstmt.setString( index++,taxID );
            log.fine( "TaxID: " + taxID );
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

        int AD_User_ID             = 0;
        int C_BPartner_Location_ID = 0;

        if( m_AD_User_ID_index != -1 ) {
            Object data = p_table.getModel().getValueAt( row,m_AD_User_ID_index );

            if( data instanceof KeyNamePair ) {
                AD_User_ID = (( KeyNamePair )data ).getKey();
            }
        }

        if( m_C_BPartner_Location_ID_index != -1 ) {
            Object data = p_table.getModel().getValueAt( row,m_C_BPartner_Location_ID_index );

            if( data instanceof KeyNamePair ) {
                C_BPartner_Location_ID = (( KeyNamePair )data ).getKey();
            }
        }

        // publish for Callout to read

        Integer ID = getSelectedRowKey();

        Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"C_BPartner_ID",(ID == null)
                ?"0"
                :ID.toString());
        Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"AD_User_ID",String.valueOf( AD_User_ID ));
        Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"C_BPartner_Location_ID",String.valueOf( C_BPartner_Location_ID ));
    }    // saveSelectionDetail

    /**
     * Descripción de Método
     *
     */

    void showHistory() {
        log.info( "InfoBPartner.showHistory" );

        Integer C_BPartner_ID = getSelectedRowKey();

        if( C_BPartner_ID == null ) {
            return;
        }

        InvoiceHistory ih = new InvoiceHistory( this,C_BPartner_ID.intValue(),0 );

        ih.setVisible( true );
        ih = null;
    }    // showHistory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasHistory() {
        return true;
    }    // hasHistory

    /**
     * Descripción de Método
     *
     */

    void zoom() {
        log.info( "InfoBPartner.zoom" );

        Integer C_BPartner_ID = getSelectedRowKey();

        if( C_BPartner_ID == null ) {
            return;
        }

        // AEnv.zoom(MBPartner.Table_ID, C_BPartner_ID.intValue(), true);  //      SO

        MQuery query = new MQuery( "C_BPartner" );

        query.addRestriction( "C_BPartner_ID",MQuery.EQUAL,C_BPartner_ID );

        // zoom (123, query);

        int AD_WindowNo = getAD_Window_ID( "C_BPartner",true );    // SO

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
        log.info( "InfoBPartner.customize" );
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
    
    
    protected int bPartnerFor(String columnName, String text){
    	StringBuffer sql = new StringBuffer();
    	sql.append(" SELECT count(*) ");
    	sql.append(" FROM ").append(getTableName());
    	sql.append(" WHERE ").append(getTableName()).append(".").append(columnName).append(" LIKE ").append("?");
    	sql.append(" AND IsActive='Y'");
    	if( m_isSOTrx ) {
            sql.append( " AND C_BPartner.IsCustomer='Y' " );
        } else {
            sql.append( " AND C_BPartner.IsVendor='Y' " );
        }
    	String sqlReal = MRole.getDefault().addAccessSQL( sql.toString(),getTableName(),MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );
    	return DB.getSQLValue(PluginUtils.getPluginInstallerTrxName(), sqlReal, text);    	
    }
}    // InfoBPartner



/*
 *  @(#)InfoBPartner.java   02.07.07
 * 
 *  Fin del fichero InfoBPartner.java
 *  
 *  Versión 2.2
 *
 */
