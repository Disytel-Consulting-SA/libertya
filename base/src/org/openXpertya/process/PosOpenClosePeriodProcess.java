package org.openXpertya.process; 

import java.util.logging.Level;

import org.openXpertya.model.MPeriodControl;
import org.openXpertya.model.X_C_PosPeriodControl;
import org.openXpertya.util.CacheMgt;
import org.openXpertya.util.ErrorUsuarioOXP;

public class PosOpenClosePeriodProcess extends SvrProcess {
	
	private int p_C_PosPeriodControl_ID = 0;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_C_PosPeriodControl_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		log.info( "C_PosPeriodControl_ID=" + p_C_PosPeriodControl_ID );

        X_C_PosPeriodControl pc = new X_C_PosPeriodControl( getCtx(),p_C_PosPeriodControl_ID,get_TrxName());

        if( pc.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@  @C_PeriodControl_ID@=" + p_C_PosPeriodControl_ID );
        }

        // Permanently closed

        if( MPeriodControl.PERIODACTION_PermanentlyClosePeriod.equals( pc.getPeriodStatus())) {
            throw new ErrorUsuarioOXP( "@PeriodStatus@ = " + pc.getPeriodStatus());
        }

        // No Action

        if( MPeriodControl.PERIODACTION_NoAction.equals( pc.getPeriodAction())) {
            return "@OK@";
        }

        // Open

        if( MPeriodControl.PERIODACTION_OpenPeriod.equals( pc.getPeriodAction())) {
            pc.setPeriodStatus( MPeriodControl.PERIODSTATUS_Open );
        }

        // Close

        if( MPeriodControl.PERIODACTION_ClosePeriod.equals( pc.getPeriodAction())) {
            pc.setPeriodStatus( MPeriodControl.PERIODSTATUS_Closed );
        }

        // Close Permanently

        if( MPeriodControl.PERIODACTION_PermanentlyClosePeriod.equals( pc.getPeriodAction())) {
            pc.setPeriodStatus( MPeriodControl.PERIODSTATUS_PermanentlyClosed );
        }

        pc.setPeriodAction( MPeriodControl.PERIODACTION_NoAction );

        //

        boolean ok = pc.save();

        // Reset Cache

        CacheMgt.get().reset( "C_PeriodControl",pc.getC_Posperiodcontrol_ID() );
        CacheMgt.get().reset( "C_PosPeriodControl",pc.getC_Posperiodcontrol_ID());

        if( !ok ) {
            return "@Error@";
        }

        return "@OK@";
	}

	

}
