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

import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAttributeUse extends X_M_AttributeUse {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MAttributeUse( Properties ctx,int ignored,String trxName ) {
        super( ctx,ignored,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }
    }    // MAttributeUse

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAttributeUse( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAttributeUse

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

        // also used for afterDelete

        String sql = "UPDATE M_AttributeSet mas" + " SET IsInstanceAttribute='Y' " + "WHERE M_AttributeSet_ID=" + getM_AttributeSet_ID() + " AND IsInstanceAttribute='N'" + " AND (IsSerNo='Y' OR IsLot='Y' OR IsGuaranteeDate='Y'" + " OR EXISTS (SELECT * FROM M_AttributeUse mau" + " INNER JOIN M_Attribute ma ON (mau.M_Attribute_ID=ma.M_Attribute_ID) " + "WHERE mau.M_AttributeSet_ID=mas.M_AttributeSet_ID" + " AND mau.IsActive='Y' AND ma.IsActive='Y'" + " AND ma.IsInstanceAttribute='Y')" + ")";
        int no = DB.executeUpdate( sql,get_TrxName());

        if( no != 0 ) {
            log.fine( "afterSave - Set Instance Attribute" );
        }

        //

        sql = "UPDATE M_AttributeSet mas" + " SET IsInstanceAttribute='N' " + "WHERE M_AttributeSet_ID=" + getM_AttributeSet_ID() + " AND IsInstanceAttribute='Y'" + "     AND IsSerNo='N' AND IsLot='N' AND IsGuaranteeDate='N'" + " AND NOT EXISTS (SELECT * FROM M_AttributeUse mau" + " INNER JOIN M_Attribute ma ON (mau.M_Attribute_ID=ma.M_Attribute_ID) " + "WHERE mau.M_AttributeSet_ID=mas.M_AttributeSet_ID" + " AND mau.IsActive='Y' AND ma.IsActive='Y'" + " AND ma.IsInstanceAttribute='Y')";
        no = DB.executeUpdate( sql,get_TrxName());

        if( no != 0 ) {
            log.fine( "afterSave - Reset Instance Attribute" );
        }

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        afterSave( false,success );

        return success;
    }    // afterDelete
}    // MAttributeUse



/*
 *  @(#)MAttributeUse.java   02.07.07
 * 
 *  Fin del fichero MAttributeUse.java
 *  
 *  Versión 2.2
 *
 */
