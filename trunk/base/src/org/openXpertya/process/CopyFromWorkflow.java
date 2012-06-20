package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.openXpertya.model.MInvoice;
import org.openXpertya.wf.MWorkflow;

public class CopyFromWorkflow extends SvrProcess {

	private int m_AD_Workflow_ID = 0;
	
	@Override
	protected String doIt() throws Exception {
		 
		int To_AD_Workflow_ID = getRecord_ID();

        log.info( "From AD_Workflow_ID=" + m_AD_Workflow_ID + " to " + To_AD_Workflow_ID );

        if( To_AD_Workflow_ID == 0 ) {
            throw new IllegalArgumentException( "Target AD_Workflow_ID == 0" );
        }

        if( m_AD_Workflow_ID == 0 ) {
            throw new IllegalArgumentException( "Source AD_Workflow_ID == 0" );
        }

        MWorkflow from = new MWorkflow(getCtx(), m_AD_Workflow_ID, get_TrxName());
        MWorkflow to   = new MWorkflow(getCtx(), To_AD_Workflow_ID, get_TrxName());

        int no = to.copyNodesFrom(from);

        // Setea el tipo de flujo de trabajo
        to.setWorkflowType(from.getWorkflowType());

    	// Guarda el flujo de trabajo ya seteado 
    	to.save();

        return "@Copied@=" + no;
	    }    // doIt

	@Override
	protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Workflow_ID" )) {
            	m_AD_Workflow_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
	}

}
