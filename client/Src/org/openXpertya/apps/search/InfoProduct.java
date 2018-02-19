/*
 *    El contenido de este fichero est� sujeto a la  Licencia P�blica openXpertya versi�n 1.1 (LPO)
 * en tanto en cuanto forme parte �ntegra del total del producto denominado:  openXpertya, soluci�n
 * empresarial global , y siempre seg�n los t�rminos de dicha licencia LPO.
 *    Una copia  �ntegra de dicha  licencia est� incluida con todas  las fuentes del producto.
 *    Partes del c�digo son CopyRight (c) 2002-2007 de Ingenier�a Inform�tica Integrada S.L., otras
 * partes son  CopyRight (c) 2002-2007 de  Consultor�a y  Soporte en  Redes y  Tecnolog�as  de  la
 * Informaci�n S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de c�digo original de  terceros, recogidos en el  ADDENDUM  A, secci�n 3 (A.3) de dicha
 * licencia  LPO,  y si dicho c�digo es extraido como parte del total del producto, estar� sujeto a
 * su respectiva licencia original.
 *     M�s informaci�n en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.apps.search;

import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.grid.ed.VComboBox;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MPriceList;
import org.openXpertya.model.MPriceListVersion;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRole;
import org.openXpertya.model.X_M_Warehouse;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public class InfoProduct extends Info implements ActionListener {

	/**
	 * Nombres de preference para obtener los caracteres comodín al inicio y fin
	 * de los campos value, name y unique
	 */
	private static final String WILCARD_PREFIX_PREFERENCE_NAME = "InfoProduct_PrefixWildcard";
	private static final String WILCARD_SUFIX_PREFERENCE_NAME = "InfoProduct_SufixWildcard";
	private static final String DEFAULT_USER_PRICELIST_ID = "M_PriceList_Version_ID";
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param modal
     * @param WindowNo
     * @param M_Warehouse_ID
     * @param M_PriceList_ID
     * @param value
     * @param multiSelection
     * @param whereClause
     */

    public InfoProduct( Frame frame,boolean modal,int WindowNo,int M_Warehouse_ID,int M_PriceList_ID,String value,boolean multiSelection,String whereClause ) {
        super( frame,modal,WindowNo,"p","M_Product_ID",multiSelection,whereClause );
        this.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        log.info( value + ", Wh=" + M_Warehouse_ID + ", PL=" + M_PriceList_ID + ", WHERE=" + whereClause );
        setTitle( Msg.getMsg( Env.getCtx(),"InfoProduct" ));

        //

        statInit();
        initInfo( value,M_Warehouse_ID,M_PriceList_ID );
        m_C_BPartner_ID = Env.getContextAsInt( Env.getCtx(),WindowNo,"C_BPartner_ID" );

        //

        int no = p_table.getRowCount();

        setStatusLine( Integer.toString( no ) + " " + Msg.getMsg( Env.getCtx(),"SearchRows_EnterQuery" ),false );
        setStatusDB( Integer.toString( no ));

        // AutoQuery

       
        /* dREHER
    	if( (value != null) && (value.length() > 0) ) {
        	executeQuery();
    	}
	
         * Fuerzo ejecucion del query siempre, ya muestra el listado completo e incorporo busqueda incremental
         */
        executeQuery();
        log.info("InfoProducto.java, ejecuto Query forzado para desplegar lista de productos!");

        p_loadedOK = true;

        // Focus

        // fieldValue.requestFocus();
        
        // dREHER, seteado por defecto el campo nombre con el foco, para realizar busqueda incremental
        // TODO: bug no setea foco, cambie orden de presentacion de campos, revisar
        fieldName.requestFocus();
        
        AEnv.positionCenterWindow( frame,this );
        
    }    // InfoProduct

    /** Descripci�n de Campos */
    
    
    //Modificado por Lucas Hernandez - Kunan
    private static  String s_productFrom_version = "M_Product p" + " LEFT OUTER JOIN M_ProductPrice pr ON (p.M_Product_ID=pr.M_Product_ID AND pr.IsActive='Y')" + " LEFT OUTER JOIN M_ProductUpc pu ON (p.M_Product_ID=pu.M_Product_ID AND pu.IsActive='Y') " + " LEFT OUTER JOIN M_AttributeSet pa ON (p.M_AttributeSet_ID=pa.M_AttributeSet_ID)" + " LEFT OUTER JOIN M_Product_Upc_Instance pui ON (p.M_Product_ID=pui.M_Product_ID AND pui.IsActive='Y')";
    private static String s_productFrom= "M_Product p" + "p.IsActive='Y'";
   
    // dREHER, guardo la ultima linea seleccionada
    private int lastSelectedRow = 0;
    
    /** Descripci�n de Campos */
    
    private boolean check=false;
    protected boolean validatePriceList=false;
		
    protected Info_Column[] s_productLayout = getInfoColumns();
   
   
	protected Info_Column[] getInfoColumns()
	{
		Info_Column[] col =
		{
	        new Info_Column( " ","DISTINCT(p.M_Product_ID)",IDColumn.class ),
	        //new Info_Column( Msg.translate( Env.getCtx(),"Discontinued" ).substring( 0,1 ),"p.Discontinued",Boolean.class ),
	        new Info_Column( Msg.translate( Env.getCtx(),"Value" ),"p.Value",String.class ),
	        new Info_Column( Msg.translate( Env.getCtx(),"Name" ),"p.Name",String.class ),
	        new Info_Column( Msg.translate( Env.getCtx(),"QtyAvailable" ),"infoproductbomqty('bomQtyAvailable',p.M_Product_ID,?,0) AS QtyAvailable",Double.class,true,true,null ),
	        new Info_Column( Msg.translate( Env.getCtx(),"PriceList" ),"bomPriceList(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceList",BigDecimal.class ),
	        new Info_Column( Msg.translate( Env.getCtx(),"PriceStd" ),"bomPriceStd(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceStd",BigDecimal.class ),
	        //new Info_Column( Msg.translate( Env.getCtx(),"QtyOnHand" ),"bomQtyOnHand(p.M_Product_ID,?,0) AS QtyOnHand",Double.class ),
	        new Info_Column( Msg.translate( Env.getCtx(),"QtyOnHand" ),"infoproductbomqty('bomQtyOnHand',p.M_Product_ID,?,0) AS QtyOnHand",Double.class ),
	        //new Info_Column( Msg.translate( Env.getCtx(),"QtyReserved" ),"bomQtyReserved(p.M_Product_ID,?,0) AS QtyReserved",Double.class ),
	        new Info_Column( Msg.translate( Env.getCtx(),"QtyReserved" ),"infoproductbomqty('bomQtyReserved',p.M_Product_ID,?,0) AS QtyReserved",Double.class ),
	        //new Info_Column( Msg.translate( Env.getCtx(),"QtyOrdered" ),"bomQtyOrdered(p.M_Product_ID,?,0) AS QtyOrdered",Double.class )
	        new Info_Column( Msg.translate( Env.getCtx(),"QtyOrdered" ),"infoproductbomqty('bomQtyOrdered',p.M_Product_ID,?,0) AS QtyOrdered",Double.class )
	        //new Info_Column( Msg.translate( Env.getCtx(),"Unconfirmed" ),"(SELECT SUM(c.TargetQty) FROM M_InOutLineConfirm c INNER JOIN M_InOutLine il ON (c.M_InOutLine_ID=il.M_InOutLine_ID) INNER JOIN M_InOut i ON (il.M_InOut_ID=i.M_InOut_ID) WHERE c.Processed='N' AND i.M_Warehouse_ID=? AND il.M_Product_ID=p.M_Product_ID) AS Unconfirmed",Double.class ),
	        //new Info_Column( Msg.translate( Env.getCtx(),"Margin" ),"bomPriceStd(p.M_Product_ID, pr.M_PriceList_Version_ID)-bomPriceLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS Margin",BigDecimal.class ),new Info_Column( Msg.translate( Env.getCtx(),"PriceLimit" ),"bomPriceLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceLimit",BigDecimal.class ),
	        //new Info_Column( Msg.translate( Env.getCtx(),"IsInstanceAttribute" ),"pa.IsInstanceAttribute",Boolean.class )
		};	
		return col;
	}
   
   
	
	

    /** Descripci�n de Campos */

	// dREHER
    // protected int INDEX_NAME = 3;
	protected int INDEX_NAME = 2;  // dREHER, orden del campo Name

    /** Descripci�n de Campos */

    protected int INDEX_PATTRIBUTE = s_productLayout.length - 1;  // last item
    
    

    //

    /** Descripci�n de Campos */

    private CLabel labelValue = new CLabel();

    /** Descripci�n de Campos */

    private CTextField fieldValue = new CTextField( 10 );
    /** Descripci�n de Campos */

    private CLabel labelName = new CLabel();

    /** Descripci�n de Campos */

    private CTextField fieldName = new CTextField( 10 );

    /** Descripci�n de Campos */

    private CLabel labelUPC = new CLabel();

    /** Descripci�n de Campos */

    private CTextField fieldUPC = new CTextField( 10 );

    /** Descripci�n de Campos */

  //  private CLabel labelSKU = new CLabel();

    /** Descripci�n de Campos */

    private CTextField fieldSKU = new CTextField( 10 );

    /** Descripci�n de Campos */

    private CLabel labelPriceList = new CLabel();

    /** Descripci�n de Campos */

    private VComboBox pickPriceList = new VComboBox();

    /** Descripci�n de Campos */

    private CLabel labelWarehouse = new CLabel();

    /** Descripci�n de Campos */

    private VComboBox pickWarehouse = new VComboBox();

    /** Descripci�n de Campos */

    protected CButton m_InfoPAttributeButton = new CButton( Env.getImageIcon( "PAttribute16.gif" ));

    /** Descripci�n de Campos */

    protected CButton m_PAttributeButton = null;

    /** Descripci�n de Campos */

    protected int m_M_AttributeSetInstance_ID = -1;

    /** Descripci�n de Campos */

    private String m_pAttributeWhere = null;

    /** Descripci�n de Campos */

    private int m_C_BPartner_ID = 0;
    
    /** Descripci�n de Campos */
    //By Lucas Hernandez - Kunan

    protected int m_Product_UPC_Instance_ID = -1;
    
 // dREHER, agrego posibilidad de filtrar productos que correspondan al socio de negocios actual
	private JCheckBox checkBP = new JCheckBox();
	private CLabel labelBP = new CLabel();

	/** Label para el campo de filtro único */
    private CLabel labelUnique = new CLabel();

    /** Campo de filtro único */
    private CTextField fieldUnique = new CTextField( 10 );
	
    /**
     * Descripci�n de M�todo
     *
     */

    private void statInit() {
        Properties ctx = Env.getCtx();
        
    	labelValue.setText( Msg.getMsg( Env.getCtx(),"Value" ));
        labelValue.setToolTipText(Msg.getElementDescription(ctx, "Value"));
        fieldValue.setBackground( CompierePLAF.getInfoBackground());
        fieldValue.setText("%");
        fieldValue.addActionListener( this );
        
        labelName.setText( Msg.getMsg( Env.getCtx(),"Name" ));
        labelName.setToolTipText(Msg.getElementDescription(Env.getCtx(),"Name"));
        fieldName.setBackground( CompierePLAF.getInfoBackground());
        fieldName.setText("%");
        fieldName.addActionListener( this );
        labelUPC.setText( Msg.translate( Env.getCtx(),"UPC" ));
        labelUPC.setToolTipText(Msg.getElementDescription(ctx, "UPC"));
        
     // dREHER, seteo check de filtro por BP activo
        labelBP.setText("Filtra por socio actual");
        labelBP.setToolTipText("Filtro los productos que estan asociados al Socio de Negocios actual");
        checkBP.setSelected(false);
        checkBP.addActionListener(this);
        
        fieldUPC.setBackground( CompierePLAF.getInfoBackground());
        fieldUPC.addActionListener( this );
      //labelSKU.setText( Msg.translate( Env.getCtx(),"SKU" ));
       // fieldSKU.setBackground( CompierePLAF.getInfoBackground());
       // fieldSKU.addActionListener( this );
        labelWarehouse.setText( Msg.getMsg( Env.getCtx(),"Warehouse" ));
        labelWarehouse.setToolTipText(Msg.getElementDescription(ctx, "M_Warehouse_ID"));
        
        pickWarehouse.setBackground( CompierePLAF.getInfoBackground());
        labelPriceList.setText( Msg.getMsg( Env.getCtx(),"PriceListVersion" ));
        labelPriceList.setToolTipText(Msg.getElementDescription(ctx, "M_PriceList_Version_ID"));
        pickPriceList.setBackground( CompierePLAF.getInfoBackground());
        m_InfoPAttributeButton.setMargin( new Insets( 2,2,2,2 ));
        m_InfoPAttributeButton.setToolTipText( Msg.getMsg( Env.getCtx(),"InfoPAttribute" ));
        m_InfoPAttributeButton.addActionListener( this );
        m_InfoPAttributeButton.setVisible(false);
        
        labelUnique.setText( Msg.getMsg( ctx,"UniqueField" ));
        labelUnique.setToolTipText(Msg.getMsg(ctx, "UniqueFieldDescription"));
        fieldUnique.setBackground( CompierePLAF.getInfoBackground());
        fieldUnique.addActionListener( this );
        
        // Line 1

        parameterPanel.setLayout( new ALayout());
        
        // dREHER, reestructuro orden para setear por defecto Name y no Value
        
        // parameterPanel.add( labelValue,new ALayoutConstraint( 0,0 ));
        // parameterPanel.add( fieldValue,null );
        
        // Campo único
        parameterPanel.add( labelUnique,new ALayoutConstraint( 0,0 ));
        parameterPanel.add( fieldUnique,null );
        
        // Line 2
        
        parameterPanel.add( labelName,new ALayoutConstraint( 1,0 ));
        parameterPanel.add( fieldName,null );
       
        
        parameterPanel.add( labelUPC,null );
        parameterPanel.add( fieldUPC,null );
        parameterPanel.add( labelWarehouse,null );
        parameterPanel.add( pickWarehouse,null );
        parameterPanel.add( m_InfoPAttributeButton );

        // Line 3
        parameterPanel.add( labelValue,new ALayoutConstraint( 2,0 ));
        parameterPanel.add( fieldValue,null );
        // parameterPanel.add( labelName,new ALayoutConstraint( 1,0 ));
        // parameterPanel.add( fieldName,null );
       // parameterPanel.add( labelSKU,null );
       // parameterPanel.add( fieldSKU,null );
        parameterPanel.add( labelPriceList,null );
        parameterPanel.add( pickPriceList,null );
        
     // dREHER, agrego filtro por socio de negocios
        parameterPanel.add( labelBP,null );
        parameterPanel.add( checkBP,null );

        // Product Attribute Instance

        m_PAttributeButton = ConfirmPanel.createPAttributeButton( true );
        confirmPanel.addButton( m_PAttributeButton );
        m_PAttributeButton.addActionListener( this );
        m_PAttributeButton.setEnabled( false );
        
        // Line 3
        
        SwingUtilities.invokeLater(new Runnable() {	
			@Override
			public void run() {
				// fieldValue.requestFocus();
				//fieldName.requestFocus();
				fieldUnique.requestFocus();
			}
		});
        
     // dREHER, cuando escribo sobre el nombre busco incrementalmente en el listado de productos disponible
		fieldName.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				// System.out.println("tecla=" + ke.getKeyCode() + ":" + ke.getKeyChar());
				x_refresh(ke.getKeyChar());
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
    private void x_refresh(char c) {
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
     * Descripci�n de M�todo
     *
     *
     * @param value
     * @param M_Warehouse_ID
     * @param M_PriceList_ID
     */

    private void initInfo( String value,int M_Warehouse_ID,int M_PriceList_ID ) {

        // Pick init

        fillPicks( M_PriceList_ID );

        int M_PriceList_Version_ID = findPLV( M_PriceList_ID );

        if( !value.endsWith( "%" )) {
        	value += "%";
        }
        
        // Set Value or Name
        if( value.startsWith( "%" )) {
            fieldName.setText(value);
//            fieldName.setText( value.substring( 1,value.length() - 1 ));
        } else {
            fieldValue.setText( value );
        }

        // Set Warehouse
        if( M_Warehouse_ID == 0 ) {
            M_Warehouse_ID = Env.getContextAsInt( Env.getCtx(),"#M_Warehouse_ID" );
        }

        if( M_Warehouse_ID != 0 ) {
            setWarehouse( M_Warehouse_ID );
        }

        // Set PriceList Version

        if( M_PriceList_Version_ID != 0 ) {
        	 setPriceListVersion( M_PriceList_Version_ID );
        }

        // Create Grid

        StringBuffer where = new StringBuffer();
        where.append( "p.IsActive='Y' " );
        
        if( M_Warehouse_ID != 0 ) {
            where.append( " AND p.IsSummary='N'" );
        }

        // dynamic Where Clause

        if( (p_whereClause != null) && (p_whereClause.length() > 0) ) {
            where.append( " AND " )    // replace fully qalified name with alias
                .append( Util.replace( p_whereClause,"M_Product.","p." ));
        }

        //
        	prepareTable(getProductLayout(),
			s_productFrom_version,
			where.toString(),
			getSqlOrderBy());
        //

        pickWarehouse.addActionListener( this );
        pickPriceList.addActionListener( this );
    }    // initInfo

    protected String getSqlOrderBy() {
    	// dREHER, ordenar por defecto por nombre del producto
    	return "Name";
    	// return "QtyAvailable ASC";
    }
    
    /**
     * Descripci�n de M�todo
     *
     *
     * @param M_PriceList_ID
     */

    private void fillPicks( int M_PriceList_ID ) {
    	

        // Price List

        String SQL = "SELECT M_PriceList_Version.M_PriceList_Version_ID," + " M_PriceList_Version.Name || ' (' || c.Iso_Code || ')' AS ValueName " + "FROM M_PriceList_Version, M_PriceList pl, C_Currency c " + "WHERE M_PriceList_Version.M_PriceList_ID=pl.M_PriceList_ID" + " AND pl.C_Currency_ID=c.C_Currency_ID" + " AND M_PriceList_Version.IsActive='Y' AND pl.IsActive='Y'";

        // Same PL currency as original one

        if( M_PriceList_ID != 0 ) {
            SQL += " AND EXISTS (SELECT * FROM M_PriceList xp WHERE xp.M_PriceList_ID=" + M_PriceList_ID + " AND pl.C_Currency_ID=xp.C_Currency_ID)";
        }

        // Add Access & Order

        SQL = MRole.getDefault().addAccessSQL( SQL,"M_PriceList_Version",true,false )    // fully qualidfied - RO
              + " ORDER BY M_PriceList_Version.Name";

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );
            ResultSet         rs    = pstmt.executeQuery();

            if(validateListPrice(0)==true){
            	pickPriceList.addItem( new KeyNamePair(0,"" ));
            	validatePriceList=true;
            }
            
            while( rs.next()) {
                KeyNamePair kn = new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 ));
                check=validateListPrice(rs.getInt(1));                
                if(check==true){
                	pickPriceList.addItem( kn );
                	validatePriceList=true;
                }
            }            

            rs.close();
            pstmt.close();
            
            if(pickPriceList.getItemCount()==0){
            	pickPriceList.setEnabled(false);
            }

            // Warehouse

            SQL = MRole.getDefault().addAccessSQL( "SELECT M_Warehouse_ID, Value || ' - ' || Name AS ValueName " + "FROM M_Warehouse " + "WHERE IsActive='Y'","M_Warehouse",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO ) + " ORDER BY Value";
            pickWarehouse.addItem( new KeyNamePair( 0,"" ));
            pstmt = DB.prepareStatement( SQL );
            rs    = pstmt.executeQuery();

            while( rs.next()) {
                KeyNamePair kn = new KeyNamePair( rs.getInt( "M_Warehouse_ID" ),rs.getString( "ValueName" ));

                pickWarehouse.addItem( kn );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"InfoProduct.fillPicks",e );
            setStatusLine( e.getLocalizedMessage(),true );
        }
    }    // fillPicks
    
    /**
     * Descripci�n de M�todo
     *
     *
     * @param M_Warehouse_ID
     */

    private void setWarehouse( int M_Warehouse_ID ) {
        for( int i = 0;i < pickWarehouse.getItemCount();i++ ) {
            KeyNamePair kn = ( KeyNamePair )pickWarehouse.getItemAt( i );

            if( kn.getKey() == M_Warehouse_ID ) {
                pickWarehouse.setSelectedIndex( i );

                return;
            }
        }
    }    // setWarehouse

    /**
     * Descripci�n de M�todo
     *
     *
     * @param M_PriceList_Version_ID
     */

    private void setPriceListVersion( int M_PriceList_Version_ID ) {
        log.config( "InfoProduct.setPriceListVersion - " + M_PriceList_Version_ID );

        for( int i = 0;i < pickPriceList.getItemCount();i++ ) {
            KeyNamePair kn = ( KeyNamePair )pickPriceList.getItemAt( i );

            if( kn.getKey() == M_PriceList_Version_ID ) {
                pickPriceList.setSelectedIndex( i );

                return;
            }
        }

        log.fine( "NOT found" );
    }    // setPriceList

    /**
     * Descripci�n de M�todo
     *
     *
     * @param M_PriceList_ID
     *
     * @return
     */

    private int findPLV( int M_PriceList_ID ) {
    	String plv = MPreference.searchCustomPreferenceValue(
    			DEFAULT_USER_PRICELIST_ID,
				Env.getAD_Client_ID(Env.getCtx()),
				Env.getAD_Org_ID(Env.getCtx()),
				Env.getAD_User_ID(Env.getCtx()), true);
    	
    	if(!Util.isEmpty(plv, true)){
    		int priceList_default = 0;
    		try {
    			priceList_default = Integer.parseInt(plv);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		if(priceList_default > 0){
    			return priceList_default;
    		}
    	}
    	
    	Timestamp priceDate = null;

        // Sales Order Date

        String dateStr = Env.getContext( Env.getCtx(),p_WindowNo,"DateOrdered" );

        if( (dateStr != null) && (dateStr.length() > 0) ) {
            priceDate = Env.getContextAsDate( Env.getCtx(),p_WindowNo,"DateOrdered" );
        } else    // Invoice Date
        {
            dateStr = Env.getContext( Env.getCtx(),p_WindowNo,"DateInvoiced" );

            if( (dateStr != null) && (dateStr.length() > 0) ) {
                priceDate = Env.getContextAsDate( Env.getCtx(),p_WindowNo,"DateInvoiced" );
            }
        }

        // Today

        if( priceDate == null ) {
            priceDate = new Timestamp( System.currentTimeMillis());
        }

        //

        log.config( "InfoProduct.findPLV - " + M_PriceList_ID + " - " + priceDate );

        int    retValue = 0;
        String SQL      = "SELECT plv.M_PriceList_Version_ID, plv.ValidFrom " + "FROM M_PriceList pl, M_PriceList_Version plv " + "WHERE pl.M_PriceList_ID=plv.M_PriceList_ID" + " AND plv.IsActive='Y'" + " AND pl.M_PriceList_ID=? "    // 1
                          + "ORDER BY plv.ValidFrom DESC";

        // find newest one

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,M_PriceList_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next() && (retValue == 0) ) {
                Timestamp plDate = rs.getTimestamp( 2 );

                if( !priceDate.before( plDate )) {
                    retValue = rs.getInt( 1 );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"InfoProduct.findPLV",e );
        }

        Env.setContext( Env.getCtx(),p_WindowNo,"M_PriceList_Version_ID",retValue );

        return retValue;
    }    // findPLV

    protected void instanceFound(int msi){

    }
    
    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    protected String getSQLWhere() {
        StringBuffer where = new StringBuffer();

		// Obtener los caracteres de comodín al inicio y fin a incorporar en la
		// cláusula para los campos value, name y unique 
		String prefixWildcard = MPreference.searchCustomPreferenceValue(
				WILCARD_PREFIX_PREFERENCE_NAME,
				Env.getAD_Client_ID(Env.getCtx()),
				Env.getAD_Org_ID(Env.getCtx()),
				Env.getAD_User_ID(Env.getCtx()), true);
		prefixWildcard = Util.isEmpty(prefixWildcard, true)?"":prefixWildcard;
		
		String sufixWildcard = MPreference.searchCustomPreferenceValue(
				WILCARD_SUFIX_PREFERENCE_NAME,
				Env.getAD_Client_ID(Env.getCtx()),
				Env.getAD_Org_ID(Env.getCtx()),
				Env.getAD_User_ID(Env.getCtx()), true);
		sufixWildcard = Util.isEmpty(sufixWildcard, true)?"":sufixWildcard;
		
        // Optional PLV

        int        M_PriceList_Version_ID = 0;
        KeyNamePair pl                     = ( KeyNamePair )pickPriceList.getSelectedItem();

        if( pl != null ) {
            M_PriceList_Version_ID = pl.getKey();
        }

        if( M_PriceList_Version_ID != 0 ) {
            where.append( " AND pr.M_PriceList_Version_ID=?" );
        }

        // Product Attribute Search

        if( m_pAttributeWhere != null ) {
            where.append( m_pAttributeWhere );

            return where.toString();
        }

        // => Value

        String value = fieldValue.getText().toUpperCase();

        if( !( value.equals( "" ) || value.equals( "%" ))) {
            where.append( " AND UPPER(p.Value) LIKE ?" );
        }

        // => Name

        String name = fieldName.getText().toUpperCase();

        if( !( name.equals( "" ) || name.equals( "%" ))) {
            where.append( " AND UPPER(p.Name) LIKE ?" );
        }

        // => UPC

        String upc = fieldUPC.getText().toUpperCase();

        if( !( upc.equals( "" ) || upc.equals( "%" ))) {
        	where.append(" AND ((UPPER(p.UPC) LIKE ?)");
        	where.append(" OR (UPPER(pui.UPC) LIKE '"+ upc + "')");
        	where.append(" OR (UPPER(pu.UPC) LIKE '"+ upc + "'))");
        }

        // => SKU

        String sku = fieldSKU.getText().toUpperCase();

        if( !( sku.equals( "" ) || sku.equals( "%" ))) {
            where.append( " AND UPPER(p.SKU) LIKE ?" );
        }
        
        // => dREHER isFilterByBussinesPartner ?
		boolean isBP = checkBP.isSelected();
		if (isBP){
			log.finest("Detecto marcado filtro por proveedor asociado");
			if(m_C_BPartner_ID > 0){
				log.finest("Encontro socio de negocios activo C_BPartner_ID=" + m_C_BPartner_ID);
				where.append(" AND p.M_Product_ID IN (SELECT M_Product_ID FROM M_Product_PO WHERE C_BPartner_ID=" + m_C_BPartner_ID + " )" );
			}
		}
		
		// => Unique

        String unique = fieldUnique.getText().toUpperCase();

		if (!Util.isEmpty(unique, true) && !unique.equals("%")) {
			// Agrego el prefijo y sufijo de la preference
			unique = prefixWildcard+unique+sufixWildcard;
			// Reemplazar los espaciones por comodines
        	unique = unique.replaceAll(" ", "%");
        	where.append(" AND ( ");
        	// Value
        	where.append(" (UPPER(p.Value) LIKE '"+unique+"') ");
            where.append(" OR ");
            // Name
            where.append(" (UPPER(p.Name) LIKE '"+unique+"') ");
            where.append(" OR ");
            // UPC
            where.append(" ((UPPER(p.UPC) LIKE '"+unique+"')");
        	where.append(" OR (UPPER(pui.UPC) LIKE '"+unique+"')");
        	where.append(" OR (UPPER(pu.UPC) LIKE '"+unique+ "'))");
            where.append(" OR ");
            // Codigo de proveedor
			where.append(" (p.M_Product_ID IN (SELECT M_Product_ID FROM M_Product_PO po WHERE UPPER(po.vendorproductno) LIKE '"
					+ unique + "' ");
            if(m_C_BPartner_ID > 0){
            	where.append(" AND po.C_BPartner_ID = ").append(m_C_BPartner_ID);
            }
            where.append(" )) ");
            where.append(" ) ");
        }

        return where.toString();
    }    // getSQLWhere

    /**
     * Descripci�n de M�todo
     *
     *
     * @param pstmt
     *
     * @throws SQLException
     */

    protected void setParameters( PreparedStatement pstmt ) throws SQLException {
        int index = 1;

        // Obtener los caracteres de comodín al inicio y fin a incorporar en la
 		// cláusula para los campos value, name y unique 
 		String prefixWildcard = MPreference.searchCustomPreferenceValue(
 				WILCARD_PREFIX_PREFERENCE_NAME,
 				Env.getAD_Client_ID(Env.getCtx()),
 				Env.getAD_Org_ID(Env.getCtx()),
 				Env.getAD_User_ID(Env.getCtx()), true);
 		prefixWildcard = Util.isEmpty(prefixWildcard, true)?"":prefixWildcard;
 		
 		String sufixWildcard = MPreference.searchCustomPreferenceValue(
 				WILCARD_SUFIX_PREFERENCE_NAME,
 				Env.getAD_Client_ID(Env.getCtx()),
 				Env.getAD_Org_ID(Env.getCtx()),
 				Env.getAD_User_ID(Env.getCtx()), true);
 		sufixWildcard = Util.isEmpty(sufixWildcard, true)?"":sufixWildcard;
        
        // => Warehouse

        int         M_Warehouse_ID = 0;
        KeyNamePair wh             = ( KeyNamePair )pickWarehouse.getSelectedItem();

        if( wh != null ) {
            M_Warehouse_ID = wh.getKey();
        }
        
        String parameter_M_Warehouse_IDs = Integer.toString(M_Warehouse_ID);
        // Si el ID de Almacén es 0 entonces no se ha seleciconado ningún almacén
        if (M_Warehouse_ID == 0){
        	// Itero por todos los almacénes del combo agregando los ID al parametro M_Warehouse_IDs
        	for( int i = 0;i < getPickWarehouse().getItemCount();i++ ) {
        		M_Warehouse_ID = (( KeyNamePair )pickWarehouse.getItemAt(i)).getKey();
        		X_M_Warehouse warehouse = new X_M_Warehouse(Env.getCtx(), M_Warehouse_ID, null);
        		// TODO Se deberá agregar un parámetro a la ventana InfoProduct para poder indicar si se quiere contemplar 
        		// o no aquellas organizaciones que tiene la marca Stock Disponible para la Venta.
        		if(warehouse.isStockAvailableForSale()){
                	// El parametro M_Warehouse_IDs es una concatenación de los id separados por un guión. 
        			parameter_M_Warehouse_IDs= parameter_M_Warehouse_IDs.concat(Integer.toString(M_Warehouse_ID)+"-");        			
        		}
        	}
        }    

        for( int i = 0;i < p_layout.length;i++ ) {
            if( p_layout[ i ].getColSQL().indexOf( "?" ) != -1 ) {
                pstmt.setString( index++, parameter_M_Warehouse_IDs);
            }
        }

        log.fine( "M_Warehouse_ID=" + M_Warehouse_ID + " (" + ( index - 1 ) + "*)" );

        // => PriceList

        int         M_PriceList_Version_ID = 0;
        KeyNamePair pl                     = ( KeyNamePair )pickPriceList.getSelectedItem();

        if( pl != null ) {
            M_PriceList_Version_ID = pl.getKey();
        }

        if( M_PriceList_Version_ID != 0 ) {
            pstmt.setInt( index++,M_PriceList_Version_ID );
            log.fine( "M_PriceList_Version_ID=" + M_PriceList_Version_ID );
        }

        // Rest of Parameter in Query for Attribute Search

        if( m_pAttributeWhere != null ) {
            return;
        }

        // => Value

        String value = fieldValue.getText().toUpperCase();

        if( !( value.equals( "" ) || value.equals( "%" ))) {
        	// if( !value.endsWith( "%" )) {
        	// Modificado por el if siguiente para que no ingrese siempre el % al final del value 
        	if( value.equals( "" ) ) {
                value += "%";
            }
        	// Agrego el prefijo y sufijo en la preference
        	value = prefixWildcard+value+sufixWildcard;
        	// Los espaciones en blanco se traducen a comodines
        	value = value.replaceAll(" ", "%");
        	
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
        	// Agrego el prefijo y sufijo en la preference
        	name = prefixWildcard+name+sufixWildcard;
        	// Los espaciones en blanco se traducen a comodines
        	name = name.replaceAll(" ", "%");
        	
            pstmt.setString( index++,name ); //Sin quitar el %
            log.fine( "Name: " + name );
        }

        // => UPC

        String upc = fieldUPC.getText().toUpperCase();

        if( !( upc.equals( "" ) || upc.equals( "%" ))) {
           	// if( !upc.endsWith( "%" )) {
           	// Modificado por el if siguiente para que no ingrese siempre el % al final del upc
           	if( upc.equals( "" ) ) {
                upc += "%";
            }

            pstmt.setString( index++,upc );
            log.fine( "UPC: " + upc );
        }

        // => SKU

        String sku = fieldSKU.getText().toUpperCase();

        if( !( sku.equals( "" ) || sku.equals( "%" ))) {
           	// if( !sku.endsWith( "%" )) {
           	// Modificado por el if siguiente para que no ingrese siempre el % al final del sku
           	if( sku.equals( "" ) ) {
                sku += "%";
            }

            pstmt.setString( index++,sku );
            log.fine( "SKU: " + sku );
        }
    }    // setParameters

    /**
     * Descripci�n de M�todo
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // don't requery if fieldValue and fieldName are empty

        if( ( (e.getSource() == pickWarehouse) || (e.getSource() == pickPriceList) ) && ( (fieldValue.getText().length() == 0) && (fieldName.getText().length() == 0) ) ) {
            return;
        }

        // Product Attribute Search

        if( e.getSource().equals( m_InfoPAttributeButton )) {
            cmd_InfoPAttribute();

            return;
        }

        m_pAttributeWhere = null;

        // Query Product Attribure Instance

        int row = p_table.getSelectedRow();

        if( e.getSource().equals( m_PAttributeButton ) && (row != -1) ) {
            Integer productInteger = getSelectedRowKey();
            String  productName    = ( String )p_table.getValueAt( row,INDEX_NAME );
            KeyNamePair warehouse = ( KeyNamePair )pickWarehouse.getSelectedItem();

            if( (productInteger == null) || (productInteger.intValue() == 0) || (warehouse == null) ) {
                return;
            }

            String             title = warehouse.getName() + " - " + productName;
            PAttributeInstance pai   = new PAttributeInstance( this,title,warehouse.getKey(),0,productInteger.intValue(),m_C_BPartner_ID,p_WindowNo );

            m_M_AttributeSetInstance_ID = pai.getM_AttributeSetInstance_ID();

            if( m_M_AttributeSetInstance_ID != -1 ) {
                dispose( true );
            }

            return;
        }

        //

        super.actionPerformed( e );
    }    // actionPerformed

    /**
     * Descripci�n de M�todo
     *
     */

    protected void enableButtons() {
        m_M_AttributeSetInstance_ID = -1;

        if( m_PAttributeButton != null ) {
            int     row     = p_table.getSelectedRow();
            boolean enabled = false;

            if( row >= 0 ) {
                Object value = p_table.getValueAt( row,INDEX_PATTRIBUTE );

                enabled = Boolean.TRUE.equals( value );
            }

            m_PAttributeButton.setEnabled( enabled );
        }

        super.enableButtons();
    }    // enableButtons

    /**
     * Descripci�n de M�todo
     *
     */

    private void cmd_InfoPAttribute() {
        InfoPAttribute ia = new InfoPAttribute( this );

        m_pAttributeWhere = ia.getWhereClause();

        if( m_pAttributeWhere != null ) {
            executeQuery();
        }
    }    // cmdInfoAttribute

    /**
     * Descripci�n de M�todo
     *
     */

    void showHistory() {
        log.info( "InfoProduct.showHistory" );

        Integer M_Product_ID = getSelectedRowKey();

        if( M_Product_ID == null ) {
            return;
        }

        InvoiceHistory ih = new InvoiceHistory( this,0,M_Product_ID.intValue());

        ih.setVisible( true );
        ih = null;
    }    // showHistory

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    boolean hasHistory() {
        return true;
    }    // hasHistory

    /**
     * Descripci�n de M�todo
     *
     */

    void zoom() {
        log.info( "InfoProduct.zoom" );

        Integer M_Product_ID = getSelectedRowKey();

        if( M_Product_ID == null ) {
            return;
        }

        // AEnv.zoom(MProduct.Table_ID, M_Product_ID.intValue(), true);    //      SO

        MQuery query = new MQuery( "M_Product" );

        query.addRestriction( "M_Product_ID",MQuery.EQUAL,M_Product_ID);

        // zoom (140, query);

        int AD_WindowNo = getAD_Window_ID( "M_Product",true );    // SO

        zoom( AD_WindowNo,query );
    }    // zoom

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    boolean hasZoom() {
        return true;
    }    // hasZoom

    /**
     * Descripci�n de M�todo
     *
     */

    void customize() {
        log.info( "InfoProduct.customize" );
    }    // customize

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    boolean hasCustomize() {
        return false;    // for now
    }                    // hasCustomize

    /**
     * Descripci�n de M�todo
     *
     */

    void saveSelectionDetail() {

        // publish for Callout to read

        Integer ID = getSelectedRowKey();

        Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID",(ID == null)
                ?"0"
                :ID.toString());

        KeyNamePair kn = ( KeyNamePair )pickPriceList.getSelectedItem();
        if(kn!=null){
        	Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_PriceList_Version_ID",kn.getID());
        }
        kn = ( KeyNamePair )pickWarehouse.getSelectedItem();
        Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_Warehouse_ID",kn.getID());
        Env.setContext(Env.getCtx(), "M_Warehouse_ID", kn.getID());

        //

        if( m_M_AttributeSetInstance_ID == -1 ) {    // not selected
            Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID","0" );
        } else {
            Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID",String.valueOf( m_M_AttributeSetInstance_ID ));
        }
    }    // saveSelectionDetail

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    protected Info_Column[] getProductLayout() {
        // Euro 13

        MClient client = MClient.get( Env.getCtx());

        if( "FRIE".equals( client.getValue())) {
            final Info_Column[] frieLayout = {
                new Info_Column( " ","p.M_Product_ID",IDColumn.class ),

                // new Info_Column(Msg.translate(Env.getCtx(), "Value"), "p.Value", String.class),

                new Info_Column( Msg.translate( Env.getCtx(),"Name" ),"p.Name",String.class ),
                new Info_Column( Msg.translate( Env.getCtx(),"QtyAvailable" ),"infoproductbomqty('bomQtyAvailable',p.M_Product_ID,?,0) AS QtyAvailable",Double.class,true,true,null ),
                //new Info_Column( Msg.translate( Env.getCtx(),"PriceList" ),"bomPriceList(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceList",BigDecimal.class ),
                //new Info_Column( Msg.translate( Env.getCtx(),"PriceStd" ),"bomPriceStd(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceStd",BigDecimal.class ),
                new Info_Column( "Einzel MWSt","pr.PriceStd * 1.16",BigDecimal.class ),new Info_Column( "Einzel kompl","(pr.PriceStd+13) * 1.16",BigDecimal.class ),
                new Info_Column( "Satz kompl","((pr.PriceStd+13) * 1.16) * 4",BigDecimal.class ),new Info_Column( Msg.translate( Env.getCtx(),"QtyOnHand" ),"infoproductbomqty('bomQtyOnHand',p.M_Product_ID,?,0) AS QtyOnHand",Double.class ),
                new Info_Column( Msg.translate( Env.getCtx(),"QtyReserved" ),"infoproductbomqty('bomQtyReserved',p.M_Product_ID,?,0) AS QtyReserved",Double.class ),new Info_Column( Msg.translate( Env.getCtx(),"QtyOrdered" ),"infoproductbomqty('bomQtyOrdered',p.M_Product_ID,?,0) AS QtyOrdered",Double.class ),
                new Info_Column( Msg.translate( Env.getCtx(),"Discontinued" ).substring( 0,1 ),"p.Discontinued",Boolean.class ),new Info_Column( Msg.translate( Env.getCtx(),"Margin" ),"bomPriceStd(p.M_Product_ID, pr.M_PriceList_Version_ID)-bomPriceLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS Margin",BigDecimal.class ),
                new Info_Column( Msg.translate( Env.getCtx(),"PriceLimit" ),"bomPriceLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceLimit",BigDecimal.class ),
                new Info_Column( Msg.translate( Env.getCtx(),"IsInstanceAttribute" ),"pa.IsInstanceAttribute",Boolean.class ),
            };
	        
            INDEX_NAME       = 2;
            INDEX_PATTRIBUTE = s_productLayout.length - 1;    // last item

            return frieLayout;
        }

        return s_productLayout;
    }

	protected VComboBox getPickPriceList() {
		return pickPriceList;
	}

	protected void setPickPriceList(VComboBox pickPriceList) {
		this.pickPriceList = pickPriceList;
	}

	protected VComboBox getPickWarehouse() {
		return pickWarehouse;
	}

	protected void setPickWarehouse(VComboBox pickWarehouse) {
		this.pickWarehouse = pickWarehouse;
	}
	
	/**
	 * Retorna la lista de precios seleccionada
	 */
	protected int getSelectedPriceListVersionID()
	{
		// si no hay ninguna seleccionada devolver 0
		if (pickPriceList.getSelectedIndex() == -1)
			return 0;
		KeyNamePair pair = (KeyNamePair)pickPriceList.getSelectedItem();
		return Integer.parseInt(pair.getID());
	}
	
	public static boolean validateListPrice(int pricelist){
		boolean res=false;
		MPriceListVersion plv= new MPriceListVersion(Env.getCtx(),pricelist,null);
		MRole mrole=new MRole(Env.getCtx(),Env.getAD_Role_ID(Env.getCtx()),null);
		MPriceList pl =new MPriceList(Env.getCtx(),plv.getM_PriceList_ID(),null);
		if(pl.isSOPriceList()==true){
			if(mrole.isviewsalesprice()==true){			
				res=true;	
			}
		}
		if(pl.isSOPriceList()==false){
			if(mrole.isviewpurchaseprice()==true){			
				if(pricelist!=0){
					res=true;
				}
				else{
					if(mrole.isviewsalesprice()==true){
						res=true;
					}
				}
			}
		}
		return res;
	}
	
	@Override
	protected int getInfoWidth() {
    	return INFO_WIDTH+140;
    }
}    // InfoProduct



/*
 *  @(#)InfoProduct.java   02.07.07
 *
 *  Fin del fichero InfoProduct.java
 *
 *  Versión 2.1
 *
 */
