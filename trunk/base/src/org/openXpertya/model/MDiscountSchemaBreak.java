/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2005 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c)  2003-2005 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.0, 22.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MDiscountSchemaBreak extends X_M_DiscountSchemaBreak {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_DiscountSchemaBreak_ID
     * @param trxName
     */

    public MDiscountSchemaBreak( Properties ctx,int M_DiscountSchemaBreak_ID,String trxName ) {
        super( ctx,M_DiscountSchemaBreak_ID,trxName );
        if (M_DiscountSchemaBreak_ID == 0) {
        	setApplicationPolicy(APPLICATIONPOLICY_AllTrue);
        	setIsAppliedEveryday(true);
        	setIsAppliedOnMonday(false);
        	setIsAppliedOnTuesday(false);
        	setIsAppliedOnWednesday(false);
        	setIsAppliedOnThursday(false);
        	setIsAppliedOnFriday(false);
        	setIsAppliedOnSaturday(false);
        	setIsAppliedOnSunday(false);
        }
    }    // MDiscountSchemaBreak

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MDiscountSchemaBreak( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MDiscountSchemaBreak

    /**
     * Indica si este corte es aplicable para una serie de parámetros, tomando la fecha
     * actual como referencia.
     * @param value Monto o Cantidad de consulta
     * @param productID ID del artículo de consulta
     * @param productCategoryID ID de subfamilia de consulta. Si es 0, se consulta la 
     * subfamilia asociada al artículo directamente.
     * @return <code>true</code> si el corte es aplicable según su configuración,
     * <code>false</code> si no lo es.
     */
    public boolean applies(BigDecimal value, int productID, int productCategoryID, int bPartnerID) {
    	return applies(value, productID, productCategoryID, null);
    }
    
    /**
     * Indica si este corte es aplicable para una serie de parámetros
     * @param value Monto o Cantidad de consulta
     * @param productID ID del artículo de consulta
     * @param productCategoryID ID de subfamilia de consulta. Si es 0, se consulta la 
     * subfamilia asociada al artículo directamente.
     * @param date Fecha de aplicación
     * @return <code>true</code> si el corte es aplicable según su configuración,
     * <code>false</code> si no lo es.
     */
    public boolean applies(BigDecimal value, int productID, int productCategoryID, Date date) {
        /*
         * Refactorizado y Recodificado por Franco Bonafine - Disytel (2010-05-19)
         * - Definición de condiciones necesarias.
         * - Agrupación de reglas por operador.
         * - Validación de fecha de aplicación.
         */
    	
    	// No aplica si el corte no está activo.
    	if( !isActive()) {
            return false;
        }

    	// -------------------------------------------------------------------------
        // -- Condiciones Necesarias 
    	// -------------------------------------------------------------------------
    	// Las condiciones necesarias se deben cumplir si o si para que este corte
    	// sea aplicable.

    	// El valor no puede ser menor que el valor de corte. 
        if(value.compareTo( getBreakValue()) < 0) {
            return false;
        }
        
    	// -------------------------------------------------------------------------
        // -- Condiciones agrupadas por Operador (AND u OR)
    	// -------------------------------------------------------------------------
        if (!evaluateConditionSet(productID, productCategoryID)) {
        	return false;
        }

    	// -------------------------------------------------------------------------
        // -- Día de Aplicación
    	// -------------------------------------------------------------------------
        if (date == null) {
        	date = Env.getDate();
        }
        
        if (!isApplicableOnDate(date)) {
        	return false;
        }
        
        // Si llegó hasta aquí entonces el corte es aplicable.
        return true;
        
    }    // applies

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MDiscountSchemaBreak[" );

        sb.append( getID()).append( "-Seq=" ).append( getSeqNo());

        if( getM_Product_Category_ID() != 0 ) {
            sb.append( ",M_Product_Category_ID=" ).append( getM_Product_Category_ID());
        }

        if( getM_Product_ID() != 0 ) {
            sb.append( ",M_Product_ID=" ).append( getM_Product_ID());
        }

        sb.append( ",Break=" ).append( getBreakValue());

        if( isBPartnerFlatDiscount()) {
            sb.append( ",FlatDiscount" );
        } else {
            sb.append( ",Discount=" ).append( getBreakDiscount());
        }

        sb.append( "]" );

        return sb.toString();
    }    // toString

	/* (non-Javadoc)
	 * @see org.openXpertya.model.PO#beforeSave(boolean)
	 */
	@Override
	protected boolean beforeSave(boolean newRecord) {

		// El descuento debe ser aplicado algún día de la semana (o todos)
		if (!isAppliedEveryday() && !isAppliedOnMonday() &&
				!isAppliedOnTuesday() && !isAppliedOnWednesday() &&
				!isAppliedOnThursday() && !isAppliedOnFriday() &&
				!isAppliedOnSaturday() && !isAppliedOnSunday()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "NeedDiscountAppliedDayError"));
			return false;
		}
		
		// Si se aplica todos los días se borran las otras marcas de cada día
		// (el isAppliedEveryday() debe tener precedencia frente a los isAppliedOnXXX())
		if (isAppliedEveryday()) {
			setIsAppliedOnSunday(false);
			setIsAppliedOnTuesday(false);
			setIsAppliedOnWednesday(false);
			setIsAppliedOnThursday(false);
			setIsAppliedOnFriday(false);
			setIsAppliedOnSaturday(false);
			setIsAppliedOnSunday(false);
		}
		
		// Si el esquema de descuento es de tipo Financiero entonces se deben
		// colocar los campos relacionados a artículos en null y el valor de
		// corte en 0
		MDiscountSchema discountSchema = new MDiscountSchema(getCtx(),
				getM_DiscountSchema_ID(), get_TrxName());
		if (discountSchema.getDiscountContextType().equals(
				MDiscountSchema.DISCOUNTCONTEXTTYPE_Financial)) {
			setBreakValue(BigDecimal.ZERO);
			setM_Product_Category_ID(0);
			setM_Product_Gamas_ID(0);
			setM_Product_ID(0);
			setC_BPartner_ID(0);
		}
		
		return true;
	}

	/**
	 * Devuelve una lista con los IDs de las Entidades Comerciales que son actualmente
	 * proveedoras de un artículo.
	 * @param productID ID del artículo a consultar
	 * @return {@link List} con los IDs. Si el artículo no contiene proveedores actualmente
	 * devuelve una lista vacía.
	 */
	private List<Integer> getProductCurrentVendorsIDs(int productID) {
		List<Integer> vendorIDs = new ArrayList<Integer>();
		MProductPO[] productPOs = MProductPO.getOfProduct(getCtx(), productID, get_TrxName());
		for (MProductPO productPO : productPOs) {
			if (productPO.isCurrentVendor()) {
				vendorIDs.add(productPO.getC_BPartner_ID());
			}
		}
		return vendorIDs;
	}
	
	/**
	 * Devuelve el ID de Familia de un artículo determinado.
	 * @param productID ID del artículo
	 * @return ID de la familia a la que pertenece el artículo o <code>-1</code>
	 * si el artículo no pertenece a ninguna familia.
	 */
	private int getProductGamasID(int productID) {
		MProductGamas gamas = MProductGamas.getOfProduct(getCtx(), productID, get_TrxName());
		// Si no existe Familia para el artículo devuelve -1 para que la condición de Familia
		// no se haga Verdadera en caso de que el corte no tenga una familia asociada
		// (M_Product_Gamas_ID = 0). Si pasara esto, la evaluación de la condición de Familia
		// siempre daría Verdadero para artículos que no tienen ninguna familia asociada, lo
		// cual no es semánticamente correcto.
		return gamas == null ? -1 : gamas.getM_Product_Gamas_ID();
	}
	
	/**
	 * Evalua las condiciones formadas por las configuraciones de filtro del corte, 
	 * teniendo en cuenta la política de evaluación de las mismas.
	 * @param productID ID de artículo a consultar
	 * @param productCategoryID ID de Subfamilia a consultar (si es 0 se consulta la
	 * subfamiliar del artículo)
	 * @return <code>true</code> si el corte es aplicable en cuanto a estos filtros,
	 * <code>false</code> en caso contrario.
	 */
	private boolean evaluateConditionSet(int productID, int productCategoryID) {
        // Se evaluan el conjunto de condiciones del corte según el operador 
        // configurado como política de aplicación. Las condiciones evaluadas por
        // el operador son:
        // 1. Familia    (M_Product_Gamas_ID)
        // 2. Subfamilia (M_Product_Category_ID)
        // 3. Artículo   (M_Product_ID)
        // 4. Proveedor  (C_BPartner_ID)

        // Definimos las variables que contienen el resultado de la evaluación de
        // cada una de las condiciones.
        boolean productGamasCondition;
        boolean productCategoryCondition;
        boolean productCondition;
        boolean vendorCondition;
        boolean allEmptyCondition;   // Indica si todas las entradas están vacías
        boolean result;              // Contiene el resultado final de la evaluación
                
        // 1. Evaluación de condición de Familia.
        // El artículo pertene a la familia asociada al corte. 
        productGamasCondition = 
        	getM_Product_Gamas_ID() == getProductGamasID(productID);
        
        // 2. Evaluación de la condición de Subfamilia.
        // El parámetro de subfamilia es una subfamilia válida y es igual a la subfamilia 
        // del corte, o la subfamilia a la que pertenece el artículo es igual a la 
        // subfamilia del corte.
        productCategoryCondition =
        	(productCategoryID > 0 && productCategoryID == getM_Product_Category_ID())
        	|| (MProductCategory.isCategory(getM_Product_Category_ID(), productID));
        
        // 3. Evaluación de la condición de Artículo
        // El parámetro de artículo es un artículo válido y es el mismo que está 
        // asociado al corte.
        productCondition = (productID > 0) && (getM_Product_ID() == productID);
        	
        // 4. Evaluación de la condición de Proveedor
        // El listado de proveedores actuales del artículo contiene el proveedor
        // asociado al corte.
        vendorCondition =
        	getProductCurrentVendorsIDs(productID).contains(getC_BPartner_ID());
        
        // 99. Todas las entradas vacías.
        // Si el corte no contiene Familia, Subfamilia, Artículo o Proveedor (ninguna
        // de ellas) entonces, independientemente del operador, el corte es aplicable.
        allEmptyCondition =
        	getM_Product_Gamas_ID() == 0 
        	&& getM_Product_Category_ID() == 0
        	&& getM_Product_ID() == 0
        	&& getC_BPartner_ID() == 0;
		
        // Caso AND: Todas las condiciones deben ser verdaderas (o todas las reglas del
        // corte vacías)
        if (APPLICATIONPOLICY_AllTrue.equals(getApplicationPolicy())) {
        	// Dado que se realiza una comparación por AND, las condiciones cuyas entradas
        	// sean vacías se convierten en verdaderas para que no surtan efecto en la 
        	// evaluación de la conjunción (e.d. se ignoran entradas vacías)
        	productGamasCondition = (getM_Product_Gamas_ID() == 0 ? true : productGamasCondition);
        	productCategoryCondition = (getM_Product_Category_ID() == 0 ? true : productCategoryCondition);
        	productCondition = (getM_Product_ID() == 0 ? true : productCondition);
        	vendorCondition = (getC_BPartner_ID() == 0 ? true : vendorCondition);
        	
        	// Se evaluan las condiciones mediante el operador AND y se computa
        	// el resultado final.
        	result = 
        		allEmptyCondition
        		|| (productGamasCondition
        			&& productCategoryCondition
        			&& productCondition
        			&& vendorCondition);

        // Caso OR: Al menos una condición debe ser verdadera (o todas las reglas del
        // corte vacías)
        } else if (APPLICATIONPOLICY_AnyTrue.equals(getApplicationPolicy())) {
        	// De la misma forma que en la comparación por AND, se deben ignorar las entradas
        	// vacías en este caso. Ya que las entradas vacías producen condiciones evaluadas
        	// a Falso no es necesario convertir el valor de ninguna condición aquí, ya que
        	// en una comparación mediante OR el False no determina el resultado final.
        	result = 
        		allEmptyCondition
        		|| productGamasCondition
        		|| productCategoryCondition
        		|| productCondition
        		|| vendorCondition;
        
        // Aplicación Desconocida: esto no debería producirce, en caso de que así sea
        // el resultado de la evaluación es Falso y se loguea el error.	
        } else {
        	log.severe("Invalid Break Application Policy: " + getApplicationPolicy());
        	result = false;
        }
        
        return result;
	}
	
	/**
	 * Indica si este corte es aplicable para una fecha determinada verificando
	 * los parámetros de día de aplicación configurados en el mismo. 
	 * @param date Fecha de consulta
	 * @return <code>true</code> si el corte es aplicable, <code>false</code>
	 * caso contrario.
	 */
	public boolean isApplicableOnDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		return 
			isAppliedEveryday()
				|| (dayOfWeek == Calendar.MONDAY && isAppliedOnMonday())
				|| (dayOfWeek == Calendar.TUESDAY && isAppliedOnTuesday())
				|| (dayOfWeek == Calendar.WEDNESDAY && isAppliedOnWednesday())
				|| (dayOfWeek == Calendar.THURSDAY && isAppliedOnThursday())
				|| (dayOfWeek == Calendar.FRIDAY && isAppliedOnFriday())
				|| (dayOfWeek == Calendar.SATURDAY && isAppliedOnSaturday())
				|| (dayOfWeek == Calendar.SUNDAY && isAppliedOnSunday());
	}

}    // MDiscountSchemaBreak



/*
 *  @(#)MDiscountSchemaBreak.java   22.03.06
 * 
 *  Fin del fichero MDiscountSchemaBreak.java
 *  
 *  Versión 2.0
 *
 */
