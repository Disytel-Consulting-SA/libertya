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

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MExpenseType extends X_S_ExpenseType {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param S_ExpenseType_ID
     * @param trxName
     */

    public MExpenseType( Properties ctx,int S_ExpenseType_ID,String trxName ) {
        super( ctx,S_ExpenseType_ID,trxName );
    }    // MExpenseType

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MExpenseType( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MExpenseType

    /** Descripción de Campos */

    private MProduct m_product = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProduct getProduct() {
        if( m_product == null ) {
            MProduct[] products = MProduct.get( getCtx(),"S_ExpenseType_ID=" + getS_ExpenseType_ID(),get_TrxName());

            if( products.length > 0 ) {
                m_product = products[ 0 ];
            }
        }

        return m_product;
    }    // getProduct

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( newRecord ) {
            if( (getValue() == null) || (getValue().length() == 0) ) {
                setValue( getName());
            }

            m_product = new MProduct( this );

            return m_product.save( get_TrxName());
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

        MProduct prod = getProduct();

        if( prod.setExpenseType( this )) {
            prod.save( get_TrxName());
        }

        return success;
    }    // afterSave
}    // MExpenseType



/*
 *  @(#)MExpenseType.java   02.07.07
 * 
 *  Fin del fichero MExpenseType.java
 *  
 *  Versión 2.2
 *
 */
