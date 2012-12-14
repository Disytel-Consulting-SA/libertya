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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.attribute.RecommendedAtributeInstance;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */


public class MProduct extends X_M_Product {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     *
     * @return
     */

    public static MProduct get( Properties ctx,int M_Product_ID ) {
        Integer  key      = new Integer( M_Product_ID );
        MProduct retValue = ( MProduct )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MProduct( ctx,M_Product_ID,null );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param whereClause
     * @param trxName
     *
     * @return
     */

    public static MProduct[] get( Properties ctx,String whereClause,String trxName ) {
        String sql = "SELECT * FROM M_Product";

        if( (whereClause != null) && (whereClause.length() > 0) ) {
            sql += " WHERE AD_Client_ID=? AND " + whereClause;
        }

        ArrayList         list         = new ArrayList();
        int               AD_Client_ID = Env.getAD_Client_ID( ctx );
        PreparedStatement pstmt        = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProduct( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MProduct[] retValue = new MProduct[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     *
     * @return
     */

    public static boolean isProductStocked( Properties ctx,int M_Product_ID ) {
        boolean  retValue = false;
        MProduct product  = get( ctx,M_Product_ID );

        return product.isStocked();
    }    // isProductStocked
    
    /**
     * @param productID
     * @param trxName
     * @return id de la ubicación del artículo
     */
    public static Integer getLocatorID(Integer productID, String trxName){
		return DB.getSQLValue(trxName,
				"SELECT m_locator_id FROM m_product WHERE m_product_id = ?",
				productID);
    } 

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "M_Product",40,5 );    // 5 minutes

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MProduct.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param trxName
     */

    public MProduct( Properties ctx,int M_Product_ID,String trxName ) {
        super( ctx,M_Product_ID,trxName );

        if( M_Product_ID == 0 ) {

            // setValue (null);
            // setName (null);
            // setM_Product_Category_ID (0);
            // setC_TaxCategory_ID (0);
            // setC_UOM_ID (0);
            //

            setProductType( PRODUCTTYPE_Item );    // I
            setIsBOM( false );                     // N
            setIsInvoicePrintDetails( false );
            setIsPickListPrintDetails( false );
            setIsPurchased( true );                // Y
            setIsSold( true );                     // Y
            setIsStocked( true );                  // Y
            setIsSummary( false );
            setIsVerified( false );                // N
            setIsWebStoreFeatured( false );
            setIsSelfService( true );
            setProcessing( false );                // N
        }
    }                                              // MProduct

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProduct( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProduct

    /**
     * Constructor de la clase ...
     *
     *
     * @param et
     */

    public MProduct( MExpenseType et ) {
        this( et.getCtx(),0,et.get_TrxName());
        setProductType( MProduct.PRODUCTTYPE_ExpenseType );
        setExpenseType( et );
    }    // MProduct

    /**
     * Constructor de la clase ...
     *
     *
     * @param resource
     * @param resourceType
     */

    public MProduct( MResource resource,MResourceType resourceType ) {
        this( resource.getCtx(),0,resource.get_TrxName());
        setProductType( MProduct.PRODUCTTYPE_Resource );
        setResource( resource );
        setResource( resourceType );
    }    // MProduct

    /**
     * Constructor de la clase ...
     *
     *
     * @param impP
     */

    public MProduct( X_I_Product impP ) {
        this( impP.getCtx(),0,impP.get_TrxName());
        setClientOrg( impP );
        setUpdatedBy( impP.getUpdatedBy());

        //

        setValue( impP.getValue());
        setName( impP.getName());
        setDescription( impP.getDescription());
        setDocumentNote( impP.getDocumentNote());
        setHelp( impP.getHelp());
        setUPC( impP.getUPC());
        setSKU( impP.getSKU());
        setC_UOM_ID( impP.getC_UOM_ID());
        setM_Product_Category_ID( impP.getM_Product_Category_ID());
        setProductType( impP.getProductType());
        setImageURL( impP.getImageURL());
        setDescriptionURL( impP.getDescriptionURL());
        setC_TaxCategory_ID(impP.getC_TaxCategory_ID());
        setCheckoutPlace(impP.getCheckoutPlace());
        setIsSold(impP.isSold());
        setIsPurchased(impP.isPurchased());
        setIsBOM(impP.isBOM());
        setM_Product_Family_ID(impP.getM_Product_Family_ID());
        
    }    // MProduct

    /** Descripción de Campos */

    private transient MProductDownload[] m_downloads = null;

    /**
     * Descripción de Método
     *
     *
     * @param parent
     *
     * @return
     */

    public boolean setExpenseType( MExpenseType parent ) {
        boolean changed = false;

        if( !PRODUCTTYPE_ExpenseType.equals( getProductType())) {
            setProductType( PRODUCTTYPE_ExpenseType );
            changed = true;
        }

        if( parent.getS_ExpenseType_ID() != getS_ExpenseType_ID()) {
            setS_ExpenseType_ID( parent.getS_ExpenseType_ID());
            changed = true;
        }

        if( parent.isActive() != isActive()) {
            setIsActive( parent.isActive());
            changed = true;
        }

        //

        if( !parent.getValue().equals( getValue())) {
            setValue( parent.getValue());
            changed = true;
        }

        if( !parent.getName().equals( getName())) {
            setName( parent.getName());
            changed = true;
        }

        if( ( (parent.getDescription() == null) && (getDescription() != null) ) || ( (parent.getDescription() != null) &&!parent.getDescription().equals( getDescription()))) {
            setDescription( parent.getDescription());
            changed = true;
        }

        if( parent.getC_UOM_ID() != getC_UOM_ID()) {
            setC_UOM_ID( parent.getC_UOM_ID());
            changed = true;
        }

        if( parent.getM_Product_Category_ID() != getM_Product_Category_ID()) {
            setM_Product_Category_ID( parent.getM_Product_Category_ID());
            changed = true;
        }

        if( parent.getC_TaxCategory_ID() != getC_TaxCategory_ID()) {
            setC_TaxCategory_ID( parent.getC_TaxCategory_ID());
            changed = true;
        }

        //

        return changed;
    }    // setExpenseType

    /**
     * Descripción de Método
     *
     *
     * @param parent
     *
     * @return
     */

    public boolean setResource( MResource parent ) {
        boolean changed = false;

        if( !PRODUCTTYPE_Resource.equals( getProductType())) {
            setProductType( PRODUCTTYPE_Resource );
            changed = true;
        }

        if( parent.getS_Resource_ID() != getS_Resource_ID()) {
            setS_Resource_ID( parent.getS_Resource_ID());
            changed = true;
        }

        if( parent.isActive() != isActive()) {
            setIsActive( parent.isActive());
            changed = true;
        }

        //

        if( !parent.getValue().equals( getValue())) {
            setValue( parent.getValue());
            changed = true;
        }

        if( !parent.getName().equals( getName())) {
            setName( parent.getName());
            changed = true;
        }

        if( ( (parent.getDescription() == null) && (getDescription() != null) ) || ( (parent.getDescription() != null) &&!parent.getDescription().equals( getDescription()))) {
            setDescription( parent.getDescription());
            changed = true;
        }

        //

        return changed;
    }    // setResource

    /**
     * Descripción de Método
     *
     *
     * @param parent
     *
     * @return
     */

    public boolean setResource( MResourceType parent ) {
        boolean changed = false;

        if( PRODUCTTYPE_Resource.equals( getProductType())) {
            setProductType( PRODUCTTYPE_Resource );
            changed = true;
        }

        //

        if( parent.getC_UOM_ID() != getC_UOM_ID()) {
            setC_UOM_ID( parent.getC_UOM_ID());
            changed = true;
        }

        if( parent.getM_Product_Category_ID() != getM_Product_Category_ID()) {
            setM_Product_Category_ID( parent.getM_Product_Category_ID());
            changed = true;
        }

        if( parent.getC_TaxCategory_ID() != getC_TaxCategory_ID()) {
            setC_TaxCategory_ID( parent.getC_TaxCategory_ID());
            changed = true;
        }

        //

        return changed;
    }    // setResource

    /** Descripción de Campos */

    private Integer m_precision = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getStandardPrecision() {
        if( m_precision == null ) {
            MUOM uom = MUOM.get( getCtx(),getC_UOM_ID());

            m_precision = new Integer( uom.getStdPrecision());
        }

        return m_precision.intValue();
    }    // getStandardPrecision

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getA_Asset_Group_ID() {
        MProductCategory pc = MProductCategory.get( getCtx(),getM_Product_Category_ID(), get_TrxName());

        return pc.getA_Asset_Group_ID();
    }    // getA_Asset_Group_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCreateAsset() {
        MProductCategory pc = MProductCategory.get( getCtx(),getM_Product_Category_ID(), get_TrxName());

        return pc.getA_Asset_Group_ID() != 0;
    }    // isCreated

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOneAssetPerUOM() {
        MProductCategory pc = MProductCategory.get( getCtx(),getM_Product_Category_ID(), get_TrxName());

        if( pc.getA_Asset_Group_ID() == 0 ) {
            return false;
        }

        MAssetGroup ag = MAssetGroup.get( getCtx(),pc.getA_Asset_Group_ID());

        return ag.isOneAssetPerUOM();
    }    // isOneAssetPerUOM

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isItem() {
        return PRODUCTTYPE_Item.equals( getProductType());
    }    // isItem
    
    public boolean isAsset() {
        return PRODUCTTYPE_Assets.equals( getProductType());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isStocked() {
        return super.isStocked() && (isItem() || isAsset());
    }    // isStocked

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isService() {

        // PRODUCTTYPE_Service, PRODUCTTYPE_Resource, PRODUCTTYPE_Online

        return !isItem();    //
    }                        // isService

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getUOMSymbol() {
        return MUOM.get( getCtx(),getC_UOM_ID()).getUOMSymbol();
    }    // getUOMSymbol

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MProductDownload[] getProductDownloads( boolean requery ) {
        if( (m_downloads != null) &&!requery ) {
            return m_downloads;
        }

        //

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM M_ProductDownload " + "WHERE M_Product_ID=? AND IsActive='Y' ORDER BY Name";

        //

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getM_Product_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProductDownload( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        m_downloads = new MProductDownload[ list.size()];
        list.toArray( m_downloads );

        return m_downloads;
    }    // getProductDownloads

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean hasDownloads() {
        getProductDownloads( false );

        return (m_downloads != null) && (m_downloads.length > 0);
    }    // hasDownloads

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MProduct[" );

        sb.append( getID()).append( "-" ).append( getValue()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Check Storage

        if( !newRecord &&                                              //
                (( is_ValueChanged( "IsActive" ) &&!isActive())        // now not active
                || ( is_ValueChanged( "IsStocked" ) &&!isStocked())    // now not stocked
                || ( is_ValueChanged( "ProductType" )                  // from Item
				&& (PRODUCTTYPE_Item.equals(get_ValueOld("ProductType")) || PRODUCTTYPE_Assets
						.equals(get_ValueOld("ProductType")))))) {
            MStorage[] storages = MStorage.getOfProduct( getCtx(),getID(),get_TrxName());
            BigDecimal OnHand   = Env.ZERO;
            BigDecimal Ordered  = Env.ZERO;
            BigDecimal Reserved = Env.ZERO;

            for( int i = 0;i < storages.length;i++ ) {
                OnHand   = OnHand.add( storages[ i ].getQtyOnHand());
                Ordered  = OnHand.add( storages[ i ].getQtyOrdered());
                Reserved = OnHand.add( storages[ i ].getQtyReserved());
            }

            String errMsg = "";

            if( OnHand.compareTo( Env.ZERO ) != 0 ) {
                errMsg = "@QtyOnHand@ = " + OnHand;
            }

            if( Ordered.compareTo( Env.ZERO ) != 0 ) {
                errMsg += " - @QtyOrdered@ = " + Ordered;
            }

            if( Reserved.compareTo( Env.ZERO ) != 0 ) {
                errMsg += " - @QtyReserved@" + Reserved;
            }

            if( errMsg.length() > 0 ) {
                log.saveError( "Error",Msg.parseTranslation( getCtx(),errMsg ));

                return false;
            }
        }        

        // Validación de campo Value duplicado: no se permiten artículos con el mismo código.
		if (sameColumnValueValidation(get_TableName(), "Value", "M_Product_ID",
				getValue(), newRecord, true)) {
			return false;
		}
               
        // Reset Stocked if not Item

		if (isStocked()
				&& !(PRODUCTTYPE_Item.equals(getProductType()) || PRODUCTTYPE_Assets
						.equals(getProductType()))) {
            setIsStocked( false );
        }
        
        // Quita espacios en blanco que pueden producir errores en la lectura del UPC
        if (getUPC() != null) {
        	setUPC(getUPC().trim());
        }
        
        // Se valida que si el Articulo tiene movimientos de Stock no se pueda cambiar el Conjunto de atributos        
        if( !newRecord && ( is_ValueChanged( "M_AttributeSet_ID" ))){
        	int count = Integer.parseInt(DB.getSQLObject(get_TrxName(), "SELECT COUNT(*) FROM M_Product p INNER JOIN M_InoutLine i ON (p.M_Product_ID = i.M_Product_ID) INNER JOIN M_InventoryLine il ON (p.M_Product_ID = il.M_Product_ID) INNER JOIN M_MovementLine m ON (p.M_Product_ID = m.M_Product_ID) INNER JOIN M_TransferLine t ON (p.M_Product_ID = t.M_Product_ID) WHERE p.M_Product_ID = ? ", new Object[]{getM_Product_ID()}).toString());
        	if (count > 0){
        		log.saveError("ProductUsed", "");
    			return false;
        	}
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
        if( !success ) {
            return success;
        }

        // Value/Name change in Account

        if( !newRecord && ( is_ValueChanged( "Value" ) || is_ValueChanged( "Name" ))) {
            MAccount.updateValueDescription( getCtx(),"M_Product_ID=" + getM_Product_ID(),get_TrxName());
        }

        // Name/Description Change in Asset        MAsset.setValueNameDescription

        if( !newRecord && ( is_ValueChanged( "Name" ) || is_ValueChanged( "Description" ))) {
            String sql;
            
            sql = " UPDATE A_Asset " +
            		" SET Name = (SELECT SUBSTR(bp.Name || ' - ' || p.Name,1,60) FROM M_Product p, C_BPartner bp WHERE p.M_Product_ID=A_Asset.M_Product_ID AND bp.C_BPartner_ID=A_Asset.C_BPartner_ID) " +
            		" , Description = (SELECT p.Description FROM M_Product p WHERE p.M_Product_ID=A_Asset.M_Product_ID) " +
            		" WHERE IsActive='Y'  AND M_Product_ID = " + getM_Product_ID();
            
            int no = DB.executeUpdate( sql,get_TrxName());

            log.fine( "Asset Description updated #" + no );
        }
        
        // New - Acct, Tree, Costing

        if( newRecord ) {
            insert_Accounting( "M_Product_Acct","M_Product_Category_Acct","p.M_Product_Category_ID=" + getM_Product_Category_ID());
            insert_Tree( MTree_Base.TREETYPE_Product );

            MAcctSchema[] mass = MAcctSchema.getClientAcctSchema( getCtx(),getAD_Client_ID());

            for( int i = 0;i < mass.length;i++ ) {
                MProductCosting pc = new MProductCosting( this,mass[ i ].getC_AcctSchema_ID());

                pc.save();
            }
        }
        else 
        {
        	// Actualizacion de configuracion contable para el artículo (si la misma no es manual)
        	if (is_ValueChanged( "M_Product_Category_ID"))
        	{
        		String ismanual = DB.getSQLValueString(get_TrxName(), "SELECT ismanual FROM M_Product_ACCT WHERE M_Product_ID = ?", getM_Product_ID());
            	if (ismanual.equalsIgnoreCase("N"))
            	{
            		DB.executeUpdate("DELETE FROM M_Product_Acct WHERE M_Product_ID = " + getM_Product_ID() );
            		insert_Accounting( "M_Product_Acct","M_Product_Category_Acct","p.M_Product_Category_ID=" + getM_Product_Category_ID());
            	}
        	}
        }        

        /*
         * Adición por Matías Cap - Disytel
         * 
         * Si se cambió el conjunto de atributos del artículo, 
         * pasar todo el stock que tenía en ese conjunto (determinado por las instancias del conjunto de atributos) 
         * y agregarlo a una tupla del producto sin instancia, o sea, stock solamente del producto  
         */
        
        //Si no es nuevo registro y cambió el conjunto de atributos y el valor del conjunto de atributos realmente se cambió 
        //(puede ser que lo haya cambiado pero después volvió a colocar el mismo valor)
        if(!newRecord && is_ValueChanged("M_AttributeSet_ID")){
        	
        	//Pasar el stock al producto
        	
        	//Obtengo el conjunto de atributos viejo
            int setOld = get_ValueOldAsInt("M_AttributeSet_ID");
            
        	//Saco las m_storage de las instancias de ese producto
        	String sql = "SELECT ms.m_attributesetinstance_id,qtyonhand,qtyreserved,qtyordered,m_locator_id FROM m_storage as ms INNER JOIN m_attributesetinstance as mast ON (ms.m_attributesetinstance_id = mast.m_attributesetinstance_id) INNER JOIN m_attributeset as mas ON (mast.m_attributeset_id = mas.m_attributeset_id) WHERE (m_product_id = ?) and (mas.m_attributeset_id = ?)";
        	
        	//Ejecuto el script
        	PreparedStatement ps = null;
        	ResultSet rs = null;
        	int m_product_id = getM_Product_ID(); 
        	BigDecimal onHand = new BigDecimal(0);
        	BigDecimal reserved = new BigDecimal(0);
        	BigDecimal ordered = new BigDecimal(0);        	
        	
        	try{
        		ps = DB.prepareStatement(sql, get_TrxName());
        		
        		//Seteo el producto y el conjunto de atributos viejo al sql
        		ps.setInt(1, m_product_id);
        		ps.setInt(2, setOld);
        		
        		rs = ps.executeQuery();
        		int attrInstance = 0;
        		int m_locator_id = 0;
        		
        		//Mientras haya instancias con stock, sumo las cantidades
        		while(rs.next()){
        			m_locator_id = rs.getInt("m_locator_id");
        			attrInstance = rs.getInt("m_attributesetinstance_id");
        			onHand = onHand.add(rs.getBigDecimal("qtyonhand"));
        			reserved = reserved.add(rs.getBigDecimal("qtyreserved"));
        			ordered = ordered.add(rs.getBigDecimal("qtyordered"));        			
        		}
        		
        		//Si entró dentro del while 
        		if(attrInstance != 0){
	        		//Actualizar el producto si estaba la tupla sino la inserto
	        		sql = "UPDATE m_storage SET qtyonhand = qtyonhand + "+onHand.intValue()+",qtyreserved = qtyreserved + "+reserved.intValue()+",qtyordered = qtyordered + "+ordered.intValue()+" WHERE (m_product_id = "+m_product_id+") AND (m_attributesetinstance_id = 0)";
	        		
	        		int rowsAffected = DB.executeUpdate(sql, get_TrxName());
	        		
	        		//Si no hizo update significa que no estaba la tupla en m_storage, hay que agregarla
	        		if(rowsAffected == 0){
	        			//Insertar la tupla
	        			MStorage m_storage = new MStorage(getCtx(),0,get_TrxName());
	        			
	        			//Seteo los valores
	        			m_storage.setM_Product_ID(m_product_id);
	        			m_storage.setM_Locator_ID(m_locator_id);
	        			m_storage.setM_AttributeSetInstance_ID(0);
	        			m_storage.setQtyOnHand(onHand);
	        			m_storage.setQtyReserved(reserved);
	        			m_storage.setQtyOrdered(ordered);
	        			
	        			//Guardo
	        			if(!m_storage.save()){
	        				log.severe("No se guardó el m_storage del producto "+m_product_id);
	        				throw new Exception();
	        			}
	        			
	        			//Registro el log
	        			log.fine("Tupla insertada dentro en m_storage sin instancias del producto "+m_product_id+" copiando el stock de cada instancia creada anteriormente");
	        		}
	        		
	        		//Elimino todas las tuplas de m_storage con las instancias anteriores
	        		
	        		//Saco las m_storage de las instancias de ese producto
	            	sql = "SELECT ms.m_attributesetinstance_id,qtyonhand,qtyreserved,qtyordered,m_locator_id FROM m_storage as ms INNER JOIN m_attributesetinstance as mast ON (ms.m_attributesetinstance_id = mast.m_attributesetinstance_id) INNER JOIN m_attributeset as mas ON (mast.m_attributeset_id = mas.m_attributeset_id) WHERE (m_product_id = ?) and (mas.m_attributeset_id = ?)";
	            	
	            	try{
	            		ps = DB.prepareStatement(sql, get_TrxName());
	            		
	            		//Seteo el producto y el conjunto de atributos viejo al sql
	            		ps.setInt(1, m_product_id);
	            		ps.setInt(2, setOld);
	            		
	            		rs = ps.executeQuery();
	            		            		
	            		//Mientras haya instancias con stock, sumo las cantidades
	            		while(rs.next()){
	            			attrInstance = rs.getInt("m_attributesetinstance_id");
	            			//Elimino la tupla de ese producto en esa instancia
	            			sql = "DELETE FROM m_storage WHERE (m_product_id = "+m_product_id+") AND (m_attributesetinstance_id = "+attrInstance+")";
	            			
	            			//Ejecutar
	            			DB.executeUpdate(sql, get_TrxName());
	            			
	            			//Registro el log
	            			log.fine("Eliminación de la tupla con el producto "+m_product_id+" y la instancia "+attrInstance);	            			
	            		}
	            	} catch(Exception e){
	            		throw new Exception();
	            	}
	            }        		        		
        	} catch(Exception e){
        		log.severe("No se completó el proceso de pasaje de stock");
        		e.printStackTrace();
        	} finally{
        		try{
        			ps.close();
            		rs.close();
        		} catch(Exception e){
        			log.severe("No se completó el proceso de pasaje de stock");
        			ps = null;
        			e.printStackTrace();
        		}
        	}
        	
        }
        
        /*
         * Fin adición Matías Cap - Disytel
         */
        
        // Sincronización y actualización de UPCs asociados al artículo
        success = updateProductUPCs();
        
        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {

        // Check Storage

        if( isStocked() || PRODUCTTYPE_Item.equals( getProductType())) {
            MStorage[] storages = MStorage.getOfProduct( getCtx(),getID(),get_TrxName());
            BigDecimal OnHand   = Env.ZERO;
            BigDecimal Ordered  = Env.ZERO;
            BigDecimal Reserved = Env.ZERO;

            for( int i = 0;i < storages.length;i++ ) {
                OnHand   = OnHand.add( storages[ i ].getQtyOnHand());
                Ordered  = OnHand.add( storages[ i ].getQtyOrdered());
                Reserved = OnHand.add( storages[ i ].getQtyReserved());
            }

            String errMsg = "";

            if( OnHand.compareTo( Env.ZERO ) != 0 ) {
                errMsg = "@QtyOnHand@ = " + OnHand;
            }

            if( Ordered.compareTo( Env.ZERO ) != 0 ) {
                errMsg += " - @QtyOrdered@ = " + Ordered;
            }

            if( Reserved.compareTo( Env.ZERO ) != 0 ) {
                errMsg += " - @QtyReserved@" + Reserved;
            }

            if( errMsg.length() > 0 ) {
                log.saveError( "Error",Msg.parseTranslation( getCtx(),errMsg ));

                return false;
            }
        }

        // delete costing

        MProductCosting[] costings = MProductCosting.getOfProduct( getCtx(),getID(),get_TrxName());

        for( int i = 0;i < costings.length;i++ ) {
            costings[ i ].delete( true,get_TrxName());
        }

        //

        return delete_Accounting( "M_Product_Acct" );
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        if( success ) {
            delete_Tree( MTree_Base.TREETYPE_Product );
        }

        return true;
    }    // afterDelete
    
    public static RecommendedAtributeInstance[] getRecommendedAtributeInstance(int MProductID, BigDecimal Qty, boolean forceSingleAttributeInstance, int MWarehouseID) throws SQLException {
    	return getRecommendedAtributeInstance(MProductID, Qty, forceSingleAttributeInstance, MWarehouseID, 0, true);
    }
    
    public static RecommendedAtributeInstance[] getRecommendedAtributeInstance(int MProductID, BigDecimal Qty, boolean forceSingleAttributeInstance, int MWarehouseID, int baseAttrInst) throws SQLException {
    	return getRecommendedAtributeInstance(MProductID, Qty, forceSingleAttributeInstance, MWarehouseID, baseAttrInst, true);
    }
    
    public static RecommendedAtributeInstance[] getRecommendedAtributeInstance(int MProductID, BigDecimal Qty, boolean forceSingleAttributeInstance, int MWarehouseID, int baseAttrInst, boolean agrupar) throws SQLException {
    	ArrayList<RecommendedAtributeInstance> lista = new ArrayList<RecommendedAtributeInstance>();
    	
    	/*
    	 *  Atenci�n:
    	 *  =========
    	 *  
    	 *  La consulta puede devolver varias veces el mismo M_AttributeSetInstance_id (MASI) ya que en M_Storage pueden
    	 *  existir diferentes M_Locator_ID con el mismo MASI y M_Product_ID. La PK de M_Storage es, justamente:
    	 *  
    	 *  (m_attributesetinstance_id, m_locator_id, m_product_id)
    	 *  
    	 *  Para solucionarlo (y que no devuelva varios MASI repetidos con distintos Qty), se agreg� el parametro "agrupar" 
    	 *  que agrupa los resultados por MASI y suma los QtyOnHand. Para los fines pr�cticos de una sugerencia autom�tica 
    	 *  en M_Order, alcanza. Para la generaci�n de M_InOut, no.
    	 *  
    	 *  Es por eso que tambi�n se agreg� un nuevo valor en RecommendedAtributeInstance (RAI) con el M_Locator_ID en caso 
    	 *  que haya mas de un MASI para ese Product. 
    	 *  
    	 *  Cuando el parametro "agrupar" es true, M_Locator_ID siempre ser� null y en RAI nunca habr� dos o m�s MASI iguales. 
    	 *  Caso contrario, cuando "agrupar" = false, la funcion podr� devolver varios RAI con iguales MASI, pero tendr�n 
    	 *  diferente M_Locator_ID (aquellos que tengan igual MASI).
    	 *  
    	 *  En todos los casos, QtyOnHand tendr� un valor correcto, y la suma de todos ellos en RAI nunca ser� mayor al Qty
    	 *  originalmente indicado. Si se pudo encontrar la Qty indicada en su totalidad, se podr� usar el valor directamente
    	 *  en las lineas del documento, sin tener que hacer validaciones adicionales.
    	 *   
    	 */
    	
    	/*
    	 * Tuve que agregar dos parametros adicionales al m�todo getRecommendedAtributeInstance:
    	 * 
    	 * El primero se utiliza para indicar el M_Warehouse_ID, ya que la Clave Primaria de M_Storage es la terna: 
    	 * 
    	 * (m_attributesetinstance_id, m_locator_id, m_product_id).
    	 * 
    	 * Eso es: solo buscar instancias de atributos que se encuentren disponibles en el Almac�n elegido en la Orden
    	 * /Alabar�n/Etc.
    	 * 
    	 * El segundo par�metro es para indicar un M_Attributesetinstance_id "base". El problema que se presenta es:
    	 * 
    	 * Tengo un Articulo "Zapatilla" con atributo "Color". El vendedor realiza el alta de una Zapatilla con una instancia 
    	 * de atributo "Color Verde"; la idea es que en ningun momento la instancia de atributos seleccionada cambie a una 
    	 * con color un color diferente. Ej: de "Verde" a "Rojo".
    	 * 
    	 * La funci�n buscar�a solo aquellas instancias de atributos base que compartan exactamente los mismos valores de 
    	 * atributos al atributo "base" (el elegido originalmente y/o que aparece en las lineas de los documentos). Las 
    	 * �nicas diferencias que puede haber es en los valores "especiales" (que aparecen en M_AttributeSetInstance): 
    	 * Numero de serie, Fecha de vencimiento, D�as de Garant�a, Lote.
    	 * 
    	 * La funci�n verifica que no haya ningun valor en M_AttributeInstance que sea diferente entre la instancia "base" 
    	 * y la nueva instancia que detect� con "mejores condiciones" (o sea, Fecha de vencimiento).
    	 * 
    	 */
    	
    	/* 
    	 * CONSULTA VIEJA � - La dejo dentro de un comentario ya que es el "esqueleto" de la consulta actual.
    	 * 
    	 *	" SELECT s.M_AttributeSetInstance_id, s.qtyonhand, COALESCE(Asi.duedate, adddays(now(),365)) AS duedate2 " + 
    	 *	" FROM M_Storage s " + 
    	 *	" INNER JOIN M_AttributeSetInstance asi ON (asi.M_AttributeSetInstance_id != 0 AND asi.M_AttributeSet_id != 0 AND s.M_AttributeSetInstance_id = asi.M_AttributeSetInstance_id) " +  
    	 *	" WHERE (Asi.duedate IS NULL OR Asi.duedate > now()) AND s.M_AttributeSet_id = asi.M_AttributeSet_id AND s.m_product_id = ? AND s.qtyonhand >= ? " + 
    	 *	" ORDER BY duedate2 ASC, s.qtyonhand ASC ";
    	 *
    	 */
    	
    	StringBuffer sql = new StringBuffer();
    	
    	if (agrupar)
    		sql.append(" SELECT s.M_AttributeSetInstance_id, SUM(s.qtyonhand), MIN(COALESCE(Asi.duedate, adddays(now(),365))) AS duedate2, null ");
    	else
    		sql.append(" SELECT s.M_AttributeSetInstance_id, s.qtyonhand, COALESCE(Asi.duedate, adddays(now(),365)) AS duedate2, s.M_Locator_ID ");
    	
    	sql.append(" FROM M_Storage s  " +
    		" INNER JOIN M_Product p ON (s.M_Product_ID=p.M_Product_ID) " + 
    		" INNER JOIN M_AttributeSetInstance asi ON (asi.M_AttributeSetInstance_id != 0 AND asi.M_AttributeSet_id != 0 AND s.M_AttributeSetInstance_id = asi.M_AttributeSetInstance_id) " + 
    		" WHERE (Asi.duedate IS NULL OR Asi.duedate > now())  " +
    		
    		// Busca que las instancias sean del mismo conjunto que la seleccionada en el articulo
    		" AND p.M_AttributeSet_id = asi.M_AttributeSet_id  " +
    		
    		// .. y que los valores personalizados no sean diferentes (leer los comentarios de m�s arriba)
    		" AND NOT EXISTS ( " +
    		"   SELECT *  " +
    		"   FROM m_attributeinstance ai1 " + 
    		"   INNER JOIN m_attributeinstance ai2 ON (ai1.m_attribute_id = ai2.m_attribute_id AND ai1.m_attributesetinstance_id != ai2.m_attributesetinstance_id) " + 
    		"   WHERE ai1.m_attributesetinstance_id = s.M_AttributeSetInstance_id AND ai2.m_attributesetinstance_id = ? AND ai1.value != ai2.value " +
    		" ) " +
    		" AND s.m_product_id = ? ");
    	
    	if (agrupar) {
    		/*
    		 *  TODO: Puse "> 0" para que no neutralice resultados. FIXME
    		 *  
    		 */ 
    		sql.append(" AND s.qtyonhand > 0 ");
    		
    		sql.append(" GROUP BY s.M_AttributeSetInstance_id " +
    			" HAVING SUM(s.qtyonhand) >= ? " +
    			" ORDER BY duedate2 ASC, SUM(s.qtyonhand) ASC ");
    	} else {
    		sql.append(" AND s.qtyonhand >= ? " +
    			" ORDER BY duedate2 ASC, s.qtyonhand ASC ");
    	}

    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	BigDecimal CurrQty = BigDecimal.ZERO;
    	BigDecimal BigQty = (Qty);
    	
    	try {
    		if (baseAttrInst == 0) { 
    			// TODO: FIXME: Considero al MASI de M_Product como un valor "por Defecto".
    			baseAttrInst = DB.getSQLValue(null, "SELECT M_AttributeSetInstance_id FROM M_Product WHERE M_Product_ID = ? ", MProductID);
    		}
    		
    		ps = DB.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, null);
    		int i = 1;
    		
    		ps.setInt(i++, baseAttrInst);
    		ps.setInt(i++, MProductID);
    		ps.setBigDecimal(i++, forceSingleAttributeInstance ? Qty : BigDecimal.ONE);
    		
    		rs = ps.executeQuery();
    		
    		while (rs.next() && CurrQty.compareTo(BigQty) < 0) {
    			RecommendedAtributeInstance rai = new RecommendedAtributeInstance(rs.getInt(1), rs.getBigDecimal(2), agrupar ? null : (Integer)rs.getObject(4));
    			
    			BigDecimal tmp = CurrQty.add(rai.getQtyOnHand());
    			
    			// La suma de los Qty devueltos nunca es mayor a la Qty indicada.
    			
    			if (tmp.compareTo(BigQty) > 0) {
    				rai.setQtyOnHand(BigQty.subtract(CurrQty));
    				tmp = BigQty;
    			}
    			
    			CurrQty = tmp;
    			
    			lista.add(rai);
    		}
    		
    		if (CurrQty.compareTo(BigQty) < 0) {
    			// No se pudo satisfacer el pedido
    			lista.clear();
    		} else if (forceSingleAttributeInstance && lista.size() > 1) {
    			// No se pudo satisfacer el pedido
    			lista.clear();
    		} else {
    			// Todo OK
    		}
    	} catch (SQLException e) {
    		// TODO: ERROR: 
    		lista.clear();
    		throw e;
    	} finally {
    		try {
    			if (ps != null) ps.close();
    		} catch (SQLException e1) {}
    		try {
    			if (rs != null) rs.close();
    		} catch (SQLException e1) {}
    	}
    	
    	// RecommendedAtributeInstance[] r = new RecommendedAtributeInstance[lista.size()];
    	
    	
    	return lista.toArray(new RecommendedAtributeInstance[lista.size()]);
    }
    
    /**
     * Devuelve una Map con las conversiones de UM definidas para este artículo (no contiene
     * la conversión por defecto a la UM del artículo).
     * La clave es el ID de la UOM destino de la conversión, y el valor es la instancia
     * de MUOMConversion.
     * @return <code>Map<Integer,MUOMConversion></code>
     */
    public Map<Integer,MUOMConversion> getUOMConversions() {
    	Map<Integer,MUOMConversion> uoms = new HashMap<Integer,MUOMConversion>();
    	MUOMConversion[] uomConversions = MUOMConversion.getProductConversions(getCtx(), getM_Product_ID(), true);
    	for (MUOMConversion uomConversion : uomConversions) {
			if (uomConversion.getC_UOM_To_ID() != getC_UOM_ID()) {
				uoms.put(uomConversion.getC_UOM_To_ID(), uomConversion);
			}
		}
    	return uoms;
    }
    
    /**
     * @return Devuelve el conjunto de artículos que pueden ser destino de un fraccionamiento
     * de este artículo. Si no contiene ningún artículo configurado devuelve una lista vacía.
     */
    public List<MProductFraction> getProductFractions() {
    	return MProductFraction.getOfProduct(getCtx(), getM_Product_ID(), get_TrxName());
    }
    
    /**
     * Indica si un artículo se encuentra dentro de los posibles artículos destino de
     * fraccionamiento de este artículo.
     * @param anotherProductID ID del artículo a consultar
     * @return true si el artículo está en la lista, false caso contrario.
     */
    public boolean isProductFraction(int anotherProductID) {
    	boolean isProductFraction = false;
    	for (MProductFraction productFraction : getProductFractions()) {
			if (productFraction.getM_Product_To_ID() == anotherProductID) {
				isProductFraction = true;
				break;
			}
		}
    	return isProductFraction;
    }
    
    /**
     * Indica si este artículo es convertible a una determinada UM
     * @param uomID ID de la UM a la cual se quiere convertir el artículo
     * @return true si existe una conversión definida, false caso contrario.
     */
    public boolean hasConversionToUOM(int uomID) {
    	return getUOMConversions().keySet().contains(uomID);
    }
    
    /**
     * Sincronización de UPC u UPCs asociados en la tabla M_ProductUPC
	 * El valor del campo UPC de este artículo es el UPC predeterminado, con lo 
	 * cual se debe agregar (si no existe) y marcar como predeterminado en la tabla
	 * M_ProductUPC.
     */
    private boolean updateProductUPCs() {
        /*
         * NO UTILIZAR is_ValueChanged("UPC") para realizar la actualización solo si se cambió
         * el código UPC en este artículo ya que esto tiene un bug. Si se actualiza el campo
         * UPC en este artículo con un código de UPC asociado a otro artículo, esta 
         * actualización falla (correctamente) debido a que los UPC deben ser únicos. Al 
         * realizar el usuario una segunda operación de guardado (con el código UPC erróneo
         * que ya está asociado a otro artículo), el is_ValueChanged("UPC") devuelve false indicando
         * que este campo no cambió (pero en realidad sí cambió en la primera edición del usuario)
         * con lo cual la actualización de UPCs no se ejecutaría, el error de UPC duplicado no
         * se detecta y se guarda en el artículo un UPC que en verdad pertenece a otro artículo.
         * 
         * Es por esto que se optó por realizar SIEMPRE la sincronización de UPCs a pesar de que
         * el UPC del artículo no haya cambiado (cosa que no podemos saber con 100% de seguridad
         * mediante el método que proporciona PO ya que tiene el bug anteriormente mencionado)
         */
    	
    	boolean success = true;
    	// Cuando se borra el valor del UPC hay que desmarcar el UPC predeterminado
    	// en la tabla MProductUPC.
    	boolean clearDefault = getUPC() == null || getUPC().length() <= 0;
    	// Instancia de UPC existente igual al UPC guardado en este artículo.
    	MProductUPC existentUPC = null;
    	
    	// Se obtienen todos los UPCs asociados al artículo
    	List<MProductUPC> productUPCs = 
    		MProductUPC.getOfProduct(getCtx(), getM_Product_ID(), get_TrxName());
    	
    	// Se realiza la búsqueda de condiciones...
    	for (MProductUPC productUPC : productUPCs) {
			// Cuando no se asigna ningún valor al UPC de este artículo
    		// se le quita la marca de predeterminado a todos los UPCs asciados
    		// al artículo (debería ser uno solo). Esto se realiza para mantener un
    		// sincronismo entre el UPC asociado por defecto y el UPC indicado en el
    		// artículo.
    		if (clearDefault) {
				if (productUPC.isDefault()) {
					productUPC.setIsDefault(false);
					productUPC.setProductUPCUpdate(true);
					success = success && productUPC.save();
				}
			// Cuando no hay que desmarcar los predeterminados es porque se ha ingresado
			// un UPC para este artículo, aquí se busca si el UPC ya se encuentra en la
			// la tabla de UPCs asociados al artículo, para luego marcalo como 
			// predeterminado (en caso de existir)
    		} else if(productUPC.getUPC().equals(getUPC())) {
				existentUPC = productUPC;
				existentUPC.setProductUPCUpdate(true);
				break;
			}
		}
    	
    	// Solo para el modo de edición o agregado de un UPC
    	if (!clearDefault) {
        	// Si el UPC ya estaba asociado al artículo se lo marca como predeterminado
        	// y activo.
        	if (existentUPC != null) {
        		existentUPC.setIsDefault(true);
        		existentUPC.setIsActive(true);
        		success = success && existentUPC.save();
        	// Si no existe, se crea un nuevo UPC asociado al artículo y se lo marca como
        	// predeterminado.
        	} else {
        		MProductUPC newProductUPC = new MProductUPC(this, getUPC());
        		newProductUPC.setIsDefault(true);
        		newProductUPC.setProductUPCUpdate(true);
        		success = success && newProductUPC.save();
        	}
    	}
    	
    	return success;
    }
    
}    // MProduct



/*
 *  @(#)MProduct.java   02.07.07
 * 
 *  Fin del fichero MProduct.java
 *  
 *  Versión 2.2
 *
 */
