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
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProjectProduct extends X_C_ProjectProduct {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_ProjectTask_ID
     * @param trxName
     */

    public MProjectProduct( Properties ctx,int C_ProjectProduct,String trxName ) {
        super( ctx,C_ProjectProduct,trxName );

        if( C_ProjectProduct == 0 ) {

            // setC_ProjectTask_ID (0);        //      PK
            // setC_ProjectPhase_ID (0);       //      Parent
            // setC_Task_ID (0);                       //      FK

            // setName (null);

            setQty( Env.ZERO );
        }
    }    // MProjectProduct

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProjectProduct( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProjectProduct

    /**
     * Constructor de la clase ...
     *
     *
     * @param phase
     */

    public MProjectProduct( MProjectTask Task ) {
        this( Task.getCtx(),0,Task.get_TrxName()); 
        //this( Task.getCtx(),Task.getC_ProjectTask_ID(),Task.get_TrxName());
        setClientOrg( Task );
        setC_ProjectTask_ID( Task.getC_ProjectTask_ID());
    }    // MProjectProduct

    /**
     * Constructor de la clase ...
     *
     *
     * @param phase
     * @param task
     */

    public MProjectProduct(MProjectPhase phase,MProjectTask task ) {
        this( task );

        //

        setC_ProjectTask_ID( task.getC_Task_ID());    // FK
        //setSeqNo( task.getSeqNo());
        setName( task.getName());
        setDescription( task.getDescription());
        setHelp( task.getHelp());

        if( task.getM_Product_ID() != 0 ) {
            setM_Product_ID( task.getM_Product_ID());
        }

        //setQty( task.getStandardQty());
    }    // MProjectProduct
    
    public MProjectProduct[]  getProduct() {
    	 
        ArrayList list = new ArrayList();
        String  sql ="SELECT * FROM C_ProjectProduct p INNER JOIN c_projecttask t ON (p.C_ProjectTask_ID=t.C_ProjectTask_ID)WHERE p.C_ProjectTask_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_ProjectTask_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
            	//JOptionPane.showMessageDialog( null,"En MProductProject = "+ rs.getString("name"),"..Fin", JOptionPane.INFORMATION_MESSAGE );
                //list.add( new MProjectTask( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getTasks",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        MProjectProduct[] retValue = new MProjectProduct[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getTasks
}    // MProjectProduct



/*
 *  @(#)MProjectProduct.java   02.07.07
 * 
 *  Fin del fichero MProjectTask.java
 *  
 *  Versión 2.2
 *
 */

