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
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MBOMProduct extends X_M_BOMProduct {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_BOMProduct_ID
     * @param trxName
     */

    public MBOMProduct( Properties ctx,int M_BOMProduct_ID,String trxName ) {
        super( ctx,M_BOMProduct_ID,trxName );

        if( M_BOMProduct_ID == 0 ) {

            // setM_BOM_ID (0);

            setBOMProductType( BOMPRODUCTTYPE_StandardProduct );    // S
            setBOMQty( Env.ONE );

            // e-evolution setIsPhantom (false);

            setLeadTimeOffset( 0 );

            // setLine (0);    // @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM M_BOMProduct WHERE M_BOM_ID=@M_BOM_ID@

        }
    }    // MBOMProduct

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MBOMProduct( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MBOMProduct

    /** Descripción de Campos */

    private MBOM m_bom = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private MBOM getBOM() {
        if( (m_bom == null) && (getM_BOM_ID() != 0) ) {
            m_bom = MBOM.get( getCtx(),getM_BOM_ID());
        }

        return m_bom;
    }    // getBOM

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Product

        if( getBOMProductType().equals( BOMPRODUCTTYPE_OutsideProcessing )) {
            if( getM_ProductBOM_ID() != 0 ) {
                setM_ProductBOM_ID( 0 );
            }
        } else if( getM_ProductBOM_ID() == 0 ) {
            log.saveError( "Error",Msg.parseTranslation( getCtx(),"@NotFound@ @M_ProductBOM_ID@" ));

            return false;
        }

        // Operation

        if( getM_ProductOperation_ID() == 0 ) {
            if( getSeqNo() != 0 ) {
                setSeqNo( 0 );
            }
        } else if( getSeqNo() == 0 ) {
            log.saveError( "Error",Msg.parseTranslation( getCtx(),"@NotFound@ @SeqNo@" ));

            return false;
        }

        // Product Attribute Instance

        if( getM_AttributeSetInstance_ID() != 0 ) {
            getBOM();

            if( (m_bom != null) && MBOM.BOMTYPE_Make_To_Order.equals( m_bom.getBOMType())) {
                ;
            } else {
                log.saveError( "Error",Msg.parseTranslation( getCtx(),"Reset @M_AttributeSetInstance_ID@: Not Make-to-Order" ));
                setM_AttributeSetInstance_ID( 0 );

                return false;
            }
        }

        // Alternate

        if(( getBOMProductType().equals( BOMPRODUCTTYPE_Alternative ) || getBOMProductType().equals( BOMPRODUCTTYPE_AlternativeDefault )) && (getM_BOMAlternative_ID() == 0) ) {
            log.saveError( "Error",Msg.parseTranslation( getCtx(),"@NotFound@ @M_BOMAlternative_ID@" ));

            return false;
        }

        // Operation

        if( getM_ProductOperation_ID() != 0 ) {
            if( getSeqNo() == 0 ) {
                log.saveError( "Error",Msg.parseTranslation( getCtx(),"@NotFound@ @SeqNo@" ));

                return false;
            }
        } else    // no op
        {
            if( getSeqNo() != 0 ) {
                setSeqNo( 0 );
            }

            if( getLeadTimeOffset() != 0 ) {
                setLeadTimeOffset( 0 );
            }
        }

        // Set Line Number

        if( getLine() == 0 ) {
            String sql = "SELECT NVL(MAX(Line),0)+10 FROM M_BOMProduct WHERE M_BOM_ID=?";
            int ii = DB.getSQLValue( get_TrxName(),sql,getM_BOM_ID());

            setLine( ii );
        }

        return true;
    }    // beforeSave
}    // MBOMProduct



/*
 *  @(#)MBOMProduct.java   02.07.07
 * 
 *  Fin del fichero MBOMProduct.java
 *  
 *  Versión 2.2
 *
 */
