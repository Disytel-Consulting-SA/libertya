package org.openXpertya.grid;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JLabel;

import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.grid.CreateFromModel.CreateFromSaveException;
import org.openXpertya.grid.CreateFromModel.ProcessParameter;
import org.openXpertya.model.MTab;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_Process;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class VCreateFromProcessParameter extends VCreateFrom implements VetoableChangeListener {

	@Override
	protected CreateFromModel createHelper(){
    	return new CreateFromProcessParameterModel();
    }
	
	public VCreateFromProcessParameter(MTab mTab) {
		super(mTab);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void customMethod(PO ol, PO iol) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void customizarPanel() {
		// TODO Auto-generated method stub
	}

	@Override
	boolean dynInit() throws Exception {
		if( p_mTab.getValue( "AD_Process_ID" ) == null ) {
            ADialog.error( 0,this,"SaveErrorRowNotFound" );
            return false;
        }
		processLabel = new JLabel(Msg.translate(getCtx(), "AD_Process_ID"));
		processLookup = VComponentsFactory.VLookupFactory(
				X_AD_Process.Table_Name + "_ID", X_AD_Process.Table_Name,
				p_WindowNo, DisplayType.Search);
		processLookup.addVetoableChangeListener( this );
		processLabel.setVisible(true);
		processLookup.setVisible(true);
		parameterStdPanel.setVisible(false);
//		automatico.setVisible(false);
//		bPartnerField.setVisible(false);
//		orderLabel.setVisible(false);
//		orderField.setVisible(false);
		return true;
	}

	@Override
	void initBPDetails(int C_BPartner_ID) {
		// TODO Auto-generated method stub

	}

	@Override
	void info() {
		// TODO Auto-generated method stub

	}

	@Override
	void save() throws CreateFromSaveException {
		int processID = (( Integer )p_mTab.getValue( "AD_Process_ID" )).intValue();
    	((CreateFromProcessParameterModel)getHelper()).save(processID, getTrxName(), getSelectedSourceEntities(), this);
	}

	@Override
	protected boolean lazyEvaluation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean addSecurityValidation() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void vetoableChange(PropertyChangeEvent arg0)
			throws PropertyVetoException {
		if ((X_AD_Process.Table_Name + "_ID").equals(arg0.getPropertyName())) {
			Integer processID = ((Integer) arg0.getNewValue()).intValue();
			loadProcess(processID);
		}
		tableChanged(null);
	}

	protected void loadProcess(Integer processID){
        List<ProcessParameter> data = new ArrayList<ProcessParameter>();
        StringBuffer sql = ((CreateFromProcessParameterModel)getHelper()).getProcessParameterQuery();

    	PreparedStatement pstmt = null;
    	ResultSet rs 			= null;

        try {
            pstmt = DB.prepareStatement( sql.toString());
            pstmt.setInt( 1,processID );
            rs = pstmt.executeQuery();
            ProcessParameter processPara;
            while( rs.next()) {
                processPara = new ProcessParameter();
                ((CreateFromProcessParameterModel)getHelper()).loadProcessParameter(processPara, rs);
                data.add(processPara);
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql.toString(),e );
    	} finally {
    		try {
	    		if (rs != null) rs.close();
	    		if (pstmt != null) pstmt.close();
    		}	catch (Exception e) {}
    	}

    	loadTable(data);
	}
	
	@Override
	protected CreateFromTableModel createTableModelInstance() {
		return new ProcessParameterTableModel();
	}
	
	 /**
     * Modelo de tabla para presentación de los pagos de una cuenta bancaria.
     */
    protected class ProcessParameterTableModel extends CreateFromTableModel {

		// Constantes de índices de las columnas en la grilla.
    	public static final int COL_IDX_SEQNO    = 1;
    	public static final int COL_IDX_NAME    = 2;
    	public static final int COL_IDX_COLUMNNAME = 3;
    	    	
		@Override
		protected void setColumnClasses() {
	        setColumnClass(COL_IDX_SEQNO, Integer.class);
	        setColumnClass(COL_IDX_NAME, String.class);
	        setColumnClass(COL_IDX_COLUMNNAME, String.class);
		}

		@Override
		protected void setColumnNames() {
	        setColumnName(COL_IDX_SEQNO, Msg.translate( Env.getCtx(),"SeqNo" ));
	        setColumnName(COL_IDX_NAME, Msg.getElement( Env.getCtx(),"Name" ));
	        setColumnName(COL_IDX_COLUMNNAME, Msg.translate( Env.getCtx(),"ColumnName" ));
		}

		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			ProcessParameter processParameter = getProcessParameter(rowIndex);
			Object value = null;
			switch (colIndex) {
				case COL_IDX_SEQNO:
					value = processParameter.seqNo; break;
				case COL_IDX_NAME:
					value = processParameter.name; break;
				case COL_IDX_COLUMNNAME:
					value = processParameter.columnName; break;
				default:
					value = super.getValueAt(rowIndex, colIndex); break;
			}
			return value;
		}

		/**
		 * Devuelve el parámetro ubicado en una determinada fila
		 * @param rowIndex Índice de la fila
		 * @return {@link ProcessParameter}
		 */
		public ProcessParameter getProcessParameter(int rowIndex) {
			return (ProcessParameter)getSourceEntity(rowIndex);
		}
		
		@Override
		protected void updateColumns() {
		}
    }
}
