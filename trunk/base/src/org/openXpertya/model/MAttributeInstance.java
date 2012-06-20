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
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAttributeInstance extends X_M_AttributeInstance {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MAttributeInstance( Properties ctx,int ignored,String trxName ) {
        super( ctx,0,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }
    }    // MAttributeInstance

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAttributeInstance( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAttributeInstance

    
    public MAttributeInstance( Properties ctx,int M_Attribute_ID,int M_AttributeSetInstance_ID,String trxName ) {
        super( ctx,0,trxName );
        setM_Attribute_ID( M_Attribute_ID );
        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );
    }    // MAttributeInstance
    
    
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Attribute_ID
     * @param M_AttributeSetInstance_ID
     * @param Value
     * @param trxName
     */

    public MAttributeInstance( Properties ctx,int M_Attribute_ID,int M_AttributeSetInstance_ID,String Value,String trxName ) {
        super( ctx,0,trxName );
        setM_Attribute_ID( M_Attribute_ID );
        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );
        setValue( Value );
    }    // MAttributeInstance

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Attribute_ID
     * @param M_AttributeSetInstance_ID
     * @param Value
     * @param trxName
     */

    public MAttributeInstance( Properties ctx,int M_Attribute_ID,int M_AttributeSetInstance_ID,BigDecimal Value,String trxName ) {
        super( ctx,0,trxName );
        setM_Attribute_ID( M_Attribute_ID );
        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );
        setValue( DisplayType.getNumberFormat(DisplayType.Number).format(Value) );    // forDisplay
        setValueNumber( Value );
    }                                   // MAttributeInstance

    public MAttributeInstance( Properties ctx,int M_Attribute_ID,int M_AttributeSetInstance_ID,Timestamp Value,String trxName ) {
        super( ctx,0,trxName );
        setM_Attribute_ID( M_Attribute_ID );
        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );
        setValue(DisplayType.getDateFormat().format(Value));
        setValueDate(Value);
    }    // MAttributeInstance
    
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Attribute_ID
     * @param M_AttributeSetInstance_ID
     * @param M_AttributeValue_ID
     * @param Value
     * @param trxName
     */

    public MAttributeInstance( Properties ctx,int M_Attribute_ID,int M_AttributeSetInstance_ID,int M_AttributeValue_ID,String Value,String trxName ) {
        super( ctx,0,trxName );
        setM_Attribute_ID( M_Attribute_ID );
        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );
        setM_AttributeValue_ID( M_AttributeValue_ID );
        setValue( Value );
    }    // MAttributeInstance
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getValueNumber() {
        BigDecimal bd = super.getValueNumber();

        if( bd == null ) {
            bd = Env.ZERO;
        }

        return bd;
    }    // getValueNumber

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return getValue();
    }    // toString

	/**
	 * Setear el valor real dependiendo del tipo de dato del valor parámetro
	 * 
	 * @param value
	 *            valor
	 */
    public void setRealValue(Object value){
    	String strValue;
    	if(value instanceof BigDecimal){
    		setValueNumber((BigDecimal)value);
    		strValue = DisplayType.getNumberFormat(DisplayType.Number).format(value);
    	} else if(value instanceof Timestamp){
    		setValueDate((Timestamp)value);
    		strValue = DisplayType.getDateFormat().format(value);
    	} else{
    		strValue = (String)value;
    	}
    	setValue(strValue);
    }
}    // MAttributeInstance



/*
 *  @(#)MAttributeInstance.java   02.07.07
 * 
 *  Fin del fichero MAttributeInstance.java
 *  
 *  Versión 2.2
 *
 */
