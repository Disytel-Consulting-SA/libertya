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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAttributeSet extends X_M_AttributeSet {

	public static enum Casos {CasoDefault,
		PedidoProveedor,
		AlbaranProveedor,
		PedidoCliente,
		AlbaranCliente,
		Merma,
		OrdenProduccionComponente,
		OrdenProduccionLinea,
		PedidoClientePedidoCredito,
		PedidoClientePedidoAlmacen,
		PrecioInstanciaProducto,
		ProductUpcByInstance
	}
	
	public static class CondicionesCasos {
		public CondicionesCasos(boolean allowNewRecord, boolean allowExistingRecords, boolean newRecordMandatory, boolean atributeSetInstenceMandatory, boolean autoSuggestAttributeSetInstance) {
			this.allowNewRecord = allowNewRecord;
			this.allowExistingRecords = allowExistingRecords;
			this.newRecordMandatory = newRecordMandatory;
			this.atributeSetInstenceMandatory = atributeSetInstenceMandatory;
			this.autoSuggestAttributeSetInstance = autoSuggestAttributeSetInstance;
		}
		
		/**
		 * @return the allowNewRecord
		 */
		public boolean isAllowNewRecord() {
			return allowNewRecord;
		}
		/**
		 * @return the allowExistingRecords
		 */
		public boolean isAllowExistingRecords() {
			return allowExistingRecords;
		}
		/**
		 * @return the newRecordMandatory
		 */
		public boolean isNewRecordMandatory() {
			return newRecordMandatory;
		}
		/**
		 * @return the atributeSetInstenceMandatory
		 */
		public boolean isAtributeSetInstenceMandatory() {
			return atributeSetInstenceMandatory;
		}
		
		/**
		 * @return the autoSuggestAttributeSetInstance
		 */
		public boolean isAutoSuggestAttributeSetInstance() {
			return autoSuggestAttributeSetInstance;
		}

		/**
		 * @param autoSuggestAttributeSetInstance the autoSuggestAttributeSetInstance to set
		 */
		public void setAutoSuggestAttributeSetInstance(boolean autoSuggestAttributeSetInstance) {
			this.autoSuggestAttributeSetInstance = autoSuggestAttributeSetInstance;
		}
		
		private boolean allowNewRecord;
		private boolean allowExistingRecords;
		private boolean newRecordMandatory;
		private boolean atributeSetInstenceMandatory;
		private boolean autoSuggestAttributeSetInstance;
	}
	
	private static Map<Casos, CondicionesCasos> s_condiciones = new EnumMap<Casos, CondicionesCasos>(Casos.class);
	private static Map<Integer, Casos> s_casosPorWindow = new TreeMap<Integer, Casos>();
	
	static {
		s_condiciones.put(Casos.PedidoProveedor, new CondicionesCasos(true, true, false, false, false));
		s_condiciones.put(Casos.AlbaranProveedor, new CondicionesCasos(true, false, true, true, false));
		s_condiciones.put(Casos.PedidoCliente, new CondicionesCasos(false, true, false, false, false));
		s_condiciones.put(Casos.AlbaranCliente, new CondicionesCasos(false, true, false, true, true));
		s_condiciones.put(Casos.Merma, new CondicionesCasos(false, true, false, true, false));
		s_condiciones.put(Casos.OrdenProduccionComponente, new CondicionesCasos(false, true, false, true, false));
		s_condiciones.put(Casos.OrdenProduccionLinea, new CondicionesCasos(true, true, false, true, false));
		s_condiciones.put(Casos.PedidoClientePedidoCredito, new CondicionesCasos(false, true, false, true, true));
		s_condiciones.put(Casos.PedidoClientePedidoAlmacen, new CondicionesCasos(false, true, false, true, true));
		s_condiciones.put(Casos.PrecioInstanciaProducto, new CondicionesCasos(false, true, false, true, false));
		s_condiciones.put(Casos.ProductUpcByInstance, new CondicionesCasos(false, true, false, false, false));
		s_condiciones.put(Casos.CasoDefault, new CondicionesCasos(true, true, false, false, false));
	}
	
	public static CondicionesCasos GetCondicionesAtributos(Casos caso) {
		return s_condiciones.get(caso);
	}
	
	private static CondicionesCasos GetCondicionesAtributosByWindowNo(int WindowNo) {
		return GetCondicionesAtributosByWindowNo(WindowNo, 0);
	}
	
	/** Devuelve el Caso a partir del nombre de la tabla y/o de una instancia de PO. 
	 * 
	 * Tanto TableName como po pueden ser null, pero no ambos. 
	 * 
	 * @param TableName
	 * @param po
	 * @param IsSOTrx
	 * @return
	 */
	public static Casos GetCasoByTableName(String TableName, PO po, int DocTypeTargetID, boolean IsSOTrx) {
		Casos caso = null;
		
		//
		
		if (TableName == null && po == null)
			return null;
		
		//
		
		if (TableName == null)
			TableName = po.get_TableName();
		
		if (po != null && DocTypeTargetID == 0) {
			Integer t = (Integer)po.get_Value("C_DocTypeTarget_ID");
			if (t != null)
				DocTypeTargetID = t;
		}
		
		// 
		
		if (TableName.equalsIgnoreCase("C_OrderLine") && DocTypeTargetID != 0) {
			MDocType DocType = MDocType.get(Env.getCtx(), DocTypeTargetID);
	    	
	    	/*
	    	 *  Modificar C_OrderLine para que haga el mismo tratamiento con los atributos en caso de que el pedido 
	    	 *  sea �Pedido a Credito� o �Pedido de Almacen�
	    	 */
	    	if (DocType.getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder)) { 
	    		if (DocType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_WarehouseOrder))
	    			caso = Casos.PedidoClientePedidoAlmacen;
	    		else if (DocType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_OnCreditOrder))
	    			caso = Casos.PedidoClientePedidoCredito;
	    	}
	    	
	    	if (caso != null)
	    		return caso;
		}
		
		// Pedido
		
		if (TableName.equalsIgnoreCase("c_orderline") && !IsSOTrx)
			caso = Casos.PedidoProveedor;
		else if (TableName.equalsIgnoreCase("c_orderline") && IsSOTrx)
			caso = Casos.PedidoCliente;
		
		// Albar�n
		
		else if (TableName.equalsIgnoreCase("m_inoutline") && !IsSOTrx)
			caso = Casos.AlbaranProveedor;
		else if (TableName.equalsIgnoreCase("m_inoutline") && IsSOTrx)
			caso = Casos.AlbaranCliente;
		
		// Producción
		
		else if (TableName.equalsIgnoreCase("c_production_order"))
			caso = Casos.OrdenProduccionComponente;
		else if (TableName.equalsIgnoreCase("c_production_orderline"))
			caso = Casos.OrdenProduccionLinea;
		
		else if (TableName.equalsIgnoreCase(X_M_ProductPriceInstance.Table_Name))
			caso = Casos.PrecioInstanciaProducto;
		
		else if (TableName.equalsIgnoreCase(X_M_Product_Upc_Instance.Table_Name))
			caso = Casos.ProductUpcByInstance;
		
		if (caso == null)
			caso = Casos.CasoDefault;
		
		return caso;
	}
	
	public static CondicionesCasos GetCondicionesAtributosByWindowNo(int WindowNo, int TabNo) {
		CondicionesCasos cc = null;
		boolean IsSOTrx = "Y".equals(Env.getContext(Env.getCtx(), WindowNo, "IsSOTrx"));
		int TabID = Env.getContextAsInt(Env.getCtx(), WindowNo, TabNo, "AD_Tab_ID");
		int WindowID = DB.getSQLValue(null, "SELECT AD_Window_ID from AD_Tab WHERE AD_Tab_ID = ?", TabID);
		String TableName = DB.getSQLValueString(null, "SELECT AD_Table.TableName from AD_Tab INNER JOIN AD_Table ON (AD_Table.AD_Table_ID=AD_Tab.AD_Table_ID) WHERE AD_Tab_ID = ?", TabID);
		int DocTypeTargetID = 0;
		
		if (TableName == null)
			return null;
		
		for (int i = TabNo; DocTypeTargetID == 0 && i >= 0; i--)
			DocTypeTargetID =  Env.getContextAsInt(Env.getCtx(), WindowNo, i, "C_DocTypeTarget_ID");
				
		synchronized (s_casosPorWindow) {
			if (!s_casosPorWindow.containsKey(WindowID)) {
				Casos caso = GetCasoByTableName(TableName, null, DocTypeTargetID, IsSOTrx);
				s_casosPorWindow.put(WindowID, caso);
			}
			
			cc = GetCondicionesAtributos(s_casosPorWindow.get(WindowID));
			
			// s_casosPorWindow.clear();
		}
		
		return cc;
	}
	
	public static boolean ProductNeedsInstanceAttribute(int M_Product_ID, String trxName) {
		/*
		return "Y".equals((String)DB.getSQLObject(null, 
				"SELECT COALESCE(IsInstanceAttribute, 'N') FROM M_Product AS p LEFT JOIN M_AttributeSet AS mas ON (p.M_AttributeSet_ID=mas.M_AttributeSet_ID) WHERE p.M_Product_ID = ?", 
				new Object[]{M_Product_ID}));
				*/
		int M_AttributeSet_ID = DB.getSQLValue(null, "SELECT M_AttributeSet_ID FROM M_Product WHERE M_Product_ID = ? ", M_Product_ID);
		
		if (M_AttributeSet_ID > 0) { 
			MAttributeSet mas = get(Env.getCtx(), M_AttributeSet_ID, trxName);
			return mas.isMandatory();
		}
		
		return false;
	}
	
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_AttributeSet_ID
     *
     * @return
     */

    public static MAttributeSet get( Properties ctx,int M_AttributeSet_ID, String trxName ) {
        Integer       key      = new Integer( M_AttributeSet_ID );
        MAttributeSet retValue = ( MAttributeSet )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MAttributeSet( ctx,M_AttributeSet_ID, trxName );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "M_AttributeSet",20 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_AttributeSet_ID
     * @param trxName
     */

    public MAttributeSet( Properties ctx,int M_AttributeSet_ID,String trxName ) {
        super( ctx,M_AttributeSet_ID,trxName );

        if( M_AttributeSet_ID == 0 ) {

            // setName (null);

            setIsGuaranteeDate( false );
            setIsGuaranteeDateMandatory( false );
            setIsLot( false );
            setIsLotMandatory( false );
            setIsSerNo( false );
            setIsSerNoMandatory( false );
            setIsInstanceAttribute( false );
            setMandatoryType( MANDATORYTYPE_NotMandatary );
        }
    }    // MAttributeSet

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAttributeSet( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAttributeSet

    /** Descripción de Campos */

    private MAttribute[] m_instanceAttributes = null;

    /** Descripción de Campos */

    private MAttribute[] m_productAttributes = null;

    /**
     * Descripción de Método
     *
     *
     * @param instanceAttributes
     *
     * @return
     */

    public MAttribute[] getMAttributes( boolean instanceAttributes ) {
        if( ( (m_instanceAttributes == null) && instanceAttributes ) || ((m_productAttributes == null) &&!instanceAttributes) ) {
            String sql = "SELECT mau.M_Attribute_ID " + "FROM M_AttributeUse mau" + " INNER JOIN M_Attribute ma ON (mau.M_Attribute_ID=ma.M_Attribute_ID) " + "WHERE mau.IsActive='Y' AND ma.IsActive='Y'" + " AND mau.M_AttributeSet_ID=? AND ma.IsInstanceAttribute=? "    // #1,2
                         + "ORDER BY mau.SeqNo";
            ArrayList         list  = new ArrayList();
            PreparedStatement pstmt = null;

            try {
                pstmt = DB.prepareStatement( sql,get_TrxName());
                pstmt.setInt( 1,getM_AttributeSet_ID());
                pstmt.setString( 2,instanceAttributes
                                   ?"Y"
                                   :"N" );

                ResultSet rs = pstmt.executeQuery();

                while( rs.next()) {
                    MAttribute ma = new MAttribute( getCtx(),rs.getInt( 1 ),get_TrxName());

                    list.add( ma );
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( SQLException ex ) {
                log.log( Level.SEVERE,"getMAttributes",ex );
            }

            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( SQLException ex1 ) {
            }

            pstmt = null;

            // Differentiate attributes

            if( instanceAttributes ) {
                m_instanceAttributes = new MAttribute[ list.size()];
                list.toArray( m_instanceAttributes );
            } else {
                m_productAttributes = new MAttribute[ list.size()];
                list.toArray( m_productAttributes );
            }
        }

        //

        if( instanceAttributes ) {
          /*
        	if( isInstanceAttribute() != m_instanceAttributes.length > 0 ) {
                setIsInstanceAttribute( m_instanceAttributes.length > 0 );
            }
            */
        }

        // Return

        if( instanceAttributes ) {
            return m_instanceAttributes;
        }

        return m_productAttributes;
    }    // getMAttributes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMandatory() {
        return !MANDATORYTYPE_NotMandatary.equals( getMandatoryType()) || isLotMandatory() || isSerNoMandatory() || isGuaranteeDateMandatory();
    }    // isMandatory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMandatoryAlways() {
        return MANDATORYTYPE_AlwaysMandatory.equals( getMandatoryType());
    }    // isMandatoryAlways

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMandatoryShipping() {
        return MANDATORYTYPE_WhenShipping.equals( getMandatoryType());
    }    // isMandatoryShipping

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
      
        if( !isInstanceAttribute() && ( isSerNo() || isLot() || isGuaranteeDate() || isDueDate() )) {
            setIsInstanceAttribute( true );
        }
        

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {

    	// Comentado por eloy 18-12-07
    	//
    	// Usaremos la instancia del producto para identificar registros que requieran exclusividad 
    	//a la hora de introducir atributos
    	
        // Set Instance Attribute

//        if( !isInstanceAttribute()) {
//            String sql = "UPDATE M_AttributeSet mas" + " SET IsInstanceAttribute='Y' " + "WHERE M_AttributeSet_ID=" + getM_AttributeSet_ID() + " AND IsInstanceAttribute='N'" + " AND (IsSerNo='Y' OR IsLot='Y' OR IsGuaranteeDate='Y' OR IsDueDate='Y' " + " OR EXISTS (SELECT * FROM M_AttributeUse mau" + " INNER JOIN M_Attribute ma ON (mau.M_Attribute_ID=ma.M_Attribute_ID) " + "WHERE mau.M_AttributeSet_ID=mas.M_AttributeSet_ID" + " AND mau.IsActive='Y' AND ma.IsActive='Y'" + " AND ma.IsInstanceAttribute='Y')" + ")";
//            int no = DB.executeUpdate( sql,get_TrxName());
//
//            if( no != 0 ) {
//                log.warning( "Set Instance Attribute" );
//                setIsInstanceAttribute( true );
//            }
//        }
//
//        // Reset Instance Attribute
//
//        if( isInstanceAttribute() &&!isSerNo() &&!isLot() &&!isGuaranteeDate()) {
//            String sql = "UPDATE M_AttributeSet mas" + " SET IsInstanceAttribute='N' " + "WHERE M_AttributeSet_ID=" + getM_AttributeSet_ID() + " AND IsInstanceAttribute='Y'" + "     AND IsSerNo='N' AND IsLot='N' AND IsGuaranteeDate='N' AND IsDueDate='N' " + " AND NOT EXISTS (SELECT * FROM M_AttributeUse mau" + " INNER JOIN M_Attribute ma ON (mau.M_Attribute_ID=ma.M_Attribute_ID) " + "WHERE mau.M_AttributeSet_ID=mas.M_AttributeSet_ID" + " AND mau.IsActive='Y' AND ma.IsActive='Y'" + " AND ma.IsInstanceAttribute='Y')";
//            int no = DB.executeUpdate( sql,get_TrxName());
//
//            if( no != 0 ) {
//                log.warning( "Reset Instance Attribute" );
//                setIsInstanceAttribute( false );
//            }
//        }

        return success;
    }    // afterSave
    
    public boolean isInstanceUniquePerUnit() {
    	return isInstanceAttribute() && isSerNo() && isSerNoMandatory();
    }
    
}    // MAttributeSet



/*
 *  @(#)MAttributeSet.java   02.07.07
 * 
 *  Fin del fichero MAttributeSet.java
 *  
 *  Versión 2.2
 *
 */
