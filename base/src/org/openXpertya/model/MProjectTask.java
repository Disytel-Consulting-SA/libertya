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
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.logging.Level;

import org.openXpertya.util.DB;


import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProjectTask extends X_C_ProjectTask {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_ProjectTask_ID
     * @param trxName
     */

    public MProjectTask( Properties ctx,int C_ProjectTask_ID,String trxName ) {
        super( ctx,C_ProjectTask_ID,trxName );

        if( C_ProjectTask_ID == 0 ) {

            // setC_ProjectTask_ID (0);        //      PK
            // setC_ProjectPhase_ID (0);       //      Parent
            // setC_Task_ID (0);                       //      FK

            setSeqNo( 0 );

            // setName (null);

            setQty( Env.ZERO );
        }
    }    // MProjectTask

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProjectTask( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProjectTask

    /**
     * Constructor de la clase ...
     *
     *
     * @param phase
     */

    public MProjectTask( MProjectPhase phase ) {
        this( phase.getCtx(),0,phase.get_TrxName());
        setClientOrg( phase );
        setC_ProjectPhase_ID( phase.getC_ProjectPhase_ID());
    }    // MProjectTask

    /**
     * Constructor de la clase ...
     *
     *
     * @param phase
     * @param task
     */

    public MProjectTask( MProjectPhase phase,MProjectTypeTask task ) {
        this( phase );

        //

        setC_Task_ID( task.getC_Task_ID());    // FK
        setSeqNo( task.getSeqNo());
        setName( task.getName());
        setDescription( task.getDescription());
        setHelp( task.getHelp());

        if( task.getM_Product_ID() != 0 ) {
            setM_Product_ID( task.getM_Product_ID());
        }

        setQty( task.getStandardQty());
    }    // MProjectTask
//Añadida, estaba en MProjectPhase
    
    public MProjectProduct[]  getProduct() {
   	 
    	//JOptionPane.showMessageDialog( null,"En MProductProject getC_ProjectTask_ID()= "+ getC_ProjectTask_ID(),"..Fin", JOptionPane.INFORMATION_MESSAGE );
        ArrayList list = new ArrayList();
        String    sql  = "SELECT p.* FROM C_ProjectProduct p INNER JOIN c_projecttask t ON (p.C_ProjectTask_ID=t.C_ProjectTask_ID)WHERE p.C_ProjectTask_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_ProjectTask_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
            	//JOptionPane.showMessageDialog( null,"En MProductProject = "+ rs.getString("name"),"..Fin", JOptionPane.INFORMATION_MESSAGE );
                list.add( new MProjectProduct( getCtx(),rs,get_TrxName()));
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
}    // MProjectTask



/*
 *  @(#)MProjectTask.java   02.07.07
 * 
 *  Fin del fichero MProjectTask.java
 *  
 *  Versión 2.2
 *
 */
