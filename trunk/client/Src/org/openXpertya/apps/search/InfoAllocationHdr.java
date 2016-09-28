package org.openXpertya.apps.search;

import java.awt.Frame;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CLabel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class InfoAllocationHdr extends Info {
	
	private static final Info_Column[] s_allocationHdrLayout = {
        new Info_Column( " ","C_AllocationHdr_ID",IDColumn.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"C_DocType_ID" ),"(SELECT name FROM c_doctype d WHERE d.c_doctype_id = C_AllocationHdr.c_doctype_id)",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"DateTrx" ),"C_AllocationHdr.DateTrx",Timestamp.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"DateAcct" ),"C_AllocationHdr.DateAcct",Timestamp.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"GrandTotal" ),"C_AllocationHdr.GrandTotal",BigDecimal.class ),
		new Info_Column( Msg.translate( Env.getCtx(),"Pendiente" ),"(select case when C_AllocationHdr.allocationtype in ('OPA','OP','RC','RCA') then POCRAvailable(C_AllocationHdr.c_allocationhdr_id) else null end)",BigDecimal.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"Description" ),"C_AllocationHdr.Description",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"Documentno" ),"C_AllocationHdr.Documentno",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"Processed" ),"C_AllocationHdr.Processed",Boolean.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"IsActive" ),"C_AllocationHdr.IsActive",Boolean.class )
	};

	public InfoAllocationHdr(Frame frame, boolean modal, int WindowNo,
			String tableName, String keyColumn, boolean multiSelection,
			String whereClause) {
		super(frame, modal, WindowNo, tableName, keyColumn, multiSelection, whereClause);
		log.info( "AllocationHdr" );
        setTitle( "AllocationHdr" );
        
        statInit();
        initInfo();
        
        int no = p_table.getRowCount();

        setStatusLine( Integer.toString( no ) + " " + Msg.getMsg( Env.getCtx(),"SearchRows_EnterQuery" ),false );
        setStatusDB( Integer.toString( no ));

        executeQuery();

        p_loadedOK = true;

        fBPartner.requestFocus();
        
        AEnv.positionCenterWindow( frame,this );
        
	}
	
	private CLabel lBPartner = new CLabel(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
    private CLabel lDocumentNo = new CLabel( Msg.translate( Env.getCtx(),"DocumentNo" ));
	
	private VLookup fBPartner = new VLookup( "C_BPartner_ID",false,false,true,MLookupFactory.get( Env.getCtx(),p_WindowNo,0,3499,DisplayType.Search ));
	
    private CTextField fDocumentNo = new CTextField( 10 );
	
	private void statInit(){ 
		lBPartner.setLabelFor( fBPartner );
		fBPartner.setBackground( CompierePLAF.getInfoBackground());
		fBPartner.addActionListener( this );
		
        lDocumentNo.setLabelFor( fDocumentNo );
        fDocumentNo.setBackground( CompierePLAF.getInfoBackground());
        fDocumentNo.addActionListener( this );

        parameterPanel.setLayout( new ALayout());

        parameterPanel.add( lBPartner,new ALayoutConstraint( 0,0 ) );
        parameterPanel.add( fBPartner,null );
        parameterPanel.add( lDocumentNo,new ALayoutConstraint( 0,5 ) );
        parameterPanel.add( fDocumentNo,null );
    }    // statInit
	
	private boolean initInfo() {
		
        String bp = Env.getContext( Env.getCtx(),p_WindowNo,"C_BPartner_ID" );

        if( (bp != null) && (bp.length() != 0) ) {
            fBPartner.setValue( new Integer( bp ));
        }

		prepareTable(
				s_allocationHdrLayout,
				"C_AllocationHdr",
				Util.isEmpty(p_whereClause, true)?"":p_whereClause, 
				"C_AllocationHdr.documentno");
		
        return true;
    }    // 

	@Override
	protected String getSQLWhere() {
		StringBuffer whereClause = new StringBuffer();
		
		if(Util.isEmpty(p_whereClause, true)){
			whereClause.append(" (1=1) ");
		}

        if( fDocumentNo.getText().length() > 0 ) {
        	whereClause.append( " AND UPPER(C_AllocationHdr.DocumentNo) LIKE ?" );
        }
		
		if(fBPartner.getValue() != null){
			whereClause.append(" AND C_AllocationHdr.C_BPartner_ID = ? ");
		}
		

		return whereClause.toString();
	}

	@Override
	protected void setParameters(PreparedStatement pstmt) throws SQLException {
        int index = 1;
        log.fine("En setPArameter con pstm= "+pstmt +" y con el index= "+ index);

        if( fDocumentNo.getText().length() > 0 ) {
            pstmt.setString( index++,getSQLText( fDocumentNo ));
        }
        
        if( fBPartner.getValue() != null ) {
            Integer bp = ( Integer )fBPartner.getValue();
            pstmt.setInt( index++,bp.intValue());
            log.fine( "BPartner=" + bp );
        }
	}
	
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasZoom() {
        return true;
    }    //

    /**
     * Descripción de Método
     *
     *
     * @param f
     *
     * @return
     */

    private String getSQLText( CTextField f ) {
        String s = f.getText().toUpperCase();

        if( !s.endsWith( "%" )) {
            s += "%";
        }

        log.fine( "String=" + s );

        return s;
    }    // getSQLText


}
