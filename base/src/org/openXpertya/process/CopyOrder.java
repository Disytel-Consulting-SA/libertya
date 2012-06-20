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



package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MOrder;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CopyOrder extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_Order_ID = 0;

    /** Descripción de Campos */

    private int p_C_DocType_ID = 0;

    /** Descripción de Campos */

    private Timestamp p_DateDoc = null;

    /** Descripción de Campos */

    private boolean p_IsCloseDocument = false;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_Order_ID" )) {
                p_C_Order_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_DocType_ID" )) {
                p_C_DocType_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DateDoc" )) {
                p_DateDoc = ( Timestamp )para[ i ].getParameter();
            } else if( name.equals( "IsCloseDocument" )) {
                p_IsCloseDocument = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "C_Order_ID=" + p_C_Order_ID + ", C_DocType_ID=" + p_C_DocType_ID + ", CloseDocument=" + p_IsCloseDocument );

        if( p_C_Order_ID == 0 ) {
            throw new IllegalArgumentException( "No Order" );
        }

        MDocType dt = MDocType.get( getCtx(),p_C_DocType_ID );

        if( dt.getID() == 0 ) {
            throw new IllegalArgumentException( "No DocType" );
        }

        if( p_DateDoc == null ) {
            p_DateDoc = new Timestamp( System.currentTimeMillis());
        }

        //

        MOrder from     = new MOrder( getCtx(),p_C_Order_ID,get_TrxName());
        MOrder newOrder = MOrder.copyFrom( from,p_DateDoc,dt.getC_DocType_ID(),dt.isSOTrx(),false,true,null );    // copy ASI

        newOrder.setC_DocTypeTarget_ID( p_C_DocType_ID );

        boolean OK = newOrder.save();

        if( !OK ) {
            throw new IllegalStateException( "Could not create new Order" );
        }

        //

        if( p_IsCloseDocument ) {
            MOrder original = new MOrder( getCtx(),p_C_Order_ID,get_TrxName());

            original.setDocAction( MOrder.DOCACTION_Complete );
            original.processIt( MOrder.DOCACTION_Complete );
            original.save();
            original.setDocAction( MOrder.DOCACTION_Close );
            original.processIt( MOrder.DOCACTION_Close );
            original.save();
        }

        //
        // Env.setSOTrx(getCtx(), newOrder.isSOTrx());
        // return "@C_Order_ID@ " + newOrder.getDocumentNo();

        return dt.getName() + ": " + newOrder.getDocumentNo();
    }    // doIt
}    // CopyOrder



/*
 *  @(#)CopyOrder.java   02.07.07
 * 
 *  Fin del fichero CopyOrder.java
 *  
 *  Versión 2.2
 *
 */
