/*
 *    El contenido de este fichero estÃ¡ sujeto a la  Licencia PÃºblica openXpertya versiÃ³n 1.1 (LPO)
 * en tanto en cuanto forme parte Ã­ntegra del total del producto denominado:  openXpertya, soluciÃ³n 
 * empresarial global , y siempre segÃºn los tÃ©rminos de dicha licencia LPO.
 *    Una copia  Ã­ntegra de dicha  licencia estÃ¡ incluida con todas  las fuentes del producto.
 *    Partes del cÃ³digo son CopyRight (c) 2002-2007 de IngenierÃ­a InformÃ¡tica Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  ConsultorÃ­a y  Soporte en  Redes y  TecnologÃ­as  de  la
 * InformaciÃ³n S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de cÃ³digo original de  terceros, recogidos en el  ADDENDUM  A, secciÃ³n 3 (A.3) de dicha
 * licencia  LPO,  y si dicho cÃ³digo es extraido como parte del total del producto, estarÃ¡ sujeto a
 * su respectiva licencia original.  
 *     MÃ¡s informaciÃ³n en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.apps.search;

import java.awt.Frame;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CLabel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.grid.ed.VCheckBox;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MQuery;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;
/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InfoInvoice extends Info {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param modal
     * @param WindowNo
     * @param value
     * @param multiSelection
     * @param whereClause
     */

    public InfoInvoice( Frame frame,boolean modal,int WindowNo,String value,boolean multiSelection,String whereClause ) {
        super( frame,modal,WindowNo,"i","C_Invoice_ID",multiSelection,whereClause );
        log.info( "InfoInvoice" );
        setTitle( Msg.getMsg( Env.getCtx(),"InfoInvoice" ));

        //

        try {
            statInit();
            p_loadedOK = initInfo();
        } catch( Exception e ) {
            return;
        }

        //

        int no = p_table.getRowCount();
        //p_table.getSelectedRow()
        setStatusLine( Integer.toString( no ) + " " + Msg.getMsg( Env.getCtx(),"SearchRows_EnterQuery" ),false );
        setStatusDB( Integer.toString( no ));

        if( (value != null) && (value.length() > 0) ) {
            fDocumentNo.setValue( value );
            executeQuery();
        } else if (fBPartner_ID.getValue() != null) {
        	executeQuery();
        }
        	

        //

        pack();

        // Focus

        fDocumentNo.requestFocus();
    }    // InfoInvoice

    /** Descripción de Campos */

    private Info_Column[] m_generalLayout;

    /** Descripción de Campos */

    private ArrayList m_queryColumns = new ArrayList();

    /** Descripción de Campos */

    private String m_tableName;

    /** Descripción de Campos */

    private String m_keyColumn;

    // Static Info

    /** Descripción de Campos */

    private CLabel lDocumentNo = new CLabel( Msg.translate( Env.getCtx(),"DocumentNo" ));

    /** Descripción de Campos */

    private CTextField fDocumentNo = new CTextField( 10 );

    /** Descripción de Campos */

    private CLabel lDescription = new CLabel( Msg.translate( Env.getCtx(),"Description" ));

    /** Descripción de Campos */

    private CTextField fDescription = new CTextField( 10 );

//      private CLabel lPOReference = new CLabel(Msg.translate(Env.getCtx(), "POReference"));
//      private CTextField fPOReference = new CTextField(10);
    //
//      private CLabel lOrg_ID = new CLabel(Msg.translate(Env.getCtx(), "AD_Org_ID"));
//      private VLookup fOrg_ID;

    /** Descripción de Campos */

    private CLabel lBPartner_ID = new CLabel( Msg.translate( Env.getCtx(),"C_BPartner_ID" ));

    /** Descripción de Campos */

    private VLookup fBPartner_ID;

    /** Descripción de Campos */

    private CLabel lOrder_ID = new CLabel( Msg.translate( Env.getCtx(),"C_Order_ID" ));

    /** Descripción de Campos */

    private VLookup fOrder_ID;

    /** Descripción de Campos */

    private VCheckBox fIsPaid = new VCheckBox( "IsPaid",false,false,true,Msg.translate( Env.getCtx(),"IsPaid" ),"",false );

    /** Descripción de Campos */

    private VCheckBox fIsSOTrx = new VCheckBox( "IsSOTrx",false,false,true,Msg.translate( Env.getCtx(),"IsSOTrx" ),"",false );

    //

    /** Descripción de Campos */

    private CLabel lDateFrom = new CLabel( Msg.translate( Env.getCtx(),"DateInvoiced" ));

    /** Descripción de Campos */

    private VDate fDateFrom = new VDate( "DateFrom",false,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),"DateFrom" ));

    /** Descripción de Campos */

    private CLabel lDateTo = new CLabel( "-" );

    /** Descripción de Campos */

    private VDate fDateTo = new VDate( "DateTo",false,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),"DateTo" ));

    /** Descripción de Campos */

    private CLabel lAmtFrom = new CLabel( Msg.translate( Env.getCtx(),"GrandTotal" ));

    /** Descripción de Campos */

    private VNumber fAmtFrom = new VNumber( "AmtFrom",false,false,true,DisplayType.Amount,Msg.translate( Env.getCtx(),"AmtFrom" ));

    /** Descripción de Campos */

    private CLabel lAmtTo = new CLabel( "-" );

    /** Descripción de Campos */

    private VNumber fAmtTo = new VNumber( "AmtTo",false,false,true,DisplayType.Amount,Msg.translate( Env.getCtx(),"AmtTo" ));

    
    private boolean isSOTrx = true;
    
    private static final int MY_WIDTH = 830;
    /** Descripción de Campos */

    private static final Info_Column[] s_invoiceLayout = {
        new Info_Column( " ","i.C_Invoice_ID",IDColumn.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"C_BPartner_ID" ),"(SELECT Name FROM C_BPartner bp WHERE bp.C_BPartner_ID=i.C_BPartner_ID)",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"DateInvoiced" ),"i.DateInvoiced",Timestamp.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"C_DocType_ID" ),"(SELECT name FROM c_doctype d WHERE d.c_doctype_id = i.c_doctypetarget_id)",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"DocumentNo" ),"i.DocumentNo",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"C_Currency_ID" ),"(SELECT ISO_Code FROM C_Currency c WHERE c.C_Currency_ID=i.C_Currency_ID)",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"GrandTotal" ),"i.GrandTotal",BigDecimal.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"ConvertedAmount" ),"currencyBase(i.GrandTotal, i.C_Currency_ID, i.DateAcct, i.AD_Client_ID, i.AD_Org_ID)",BigDecimal.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"OpenAmt" ),"invoiceOpen(C_Invoice_ID,C_InvoicePaySchedule_ID)",BigDecimal.class),
        new Info_Column( Msg.translate( Env.getCtx(),"IsPaid" ),"i.IsPaid",Boolean.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"IsSOTrx" ),"i.IsSOTrx",Boolean.class ),
			new Info_Column(
					Msg.translate(Env.getCtx(), "DocStatus"),
					"(SELECT "+(Env.isBaseLanguage(Env.getCtx(), "C_Invoice") ? "rl.name":"rlt.name")+" FROM ad_ref_list rl " +
					"INNER JOIN ad_ref_list_trl rlt ON rl.ad_ref_list_id = rlt.ad_ref_list_id " +
					"WHERE rl.ad_reference_id = " + MInvoice.DOCSTATUS_AD_Reference_ID + 
						" AND rl.value = i.docstatus " + 
					(Env.isBaseLanguage(Env.getCtx(), "C_Invoice") ? "": " AND ad_language = '"+ Env.getAD_Language(Env.getCtx())+ "'") + 
					" limit 1)", String.class),
        new Info_Column( Msg.translate( Env.getCtx(),"Description" ),"i.Description",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"POReference" ),"i.POReference",String.class ),
       //Modificado
        //new Info_Column( Msg.translate( Env.getCtx(),"C_InvoicePaySchedule_ID" ),"(SELECT C_InvoicePaySchedule_ID FROM C_InvoicePaySchedule d WHERE d.C_Invoice_ID=i.C_Invoice_ID AND i.C_InvoicePaySchedule_ID=d.C_InvoicePaySchedule_ID)",KeyNamePair.class ),
      //  new Info_Column( Msg.translate( Env.getCtx(),"C_InvoicePaySchedule_ID" ),"(SELECT C_InvoicePaySchedule_ID FROM C_InvoicePaySchedule d WHERE d.C_Invoice_ID=i.C_Invoice_ID AND i.C_InvoicePaySchedule_ID=d.C_InvoicePaySchedule_ID)",KeyNamePair.class,"C_InvoicePaySchedule_ID" )
        //Fin modificado
        new Info_Column( "","''",KeyNamePair.class,"i.C_InvoicePaySchedule_ID" )
       
    };
   
    /** Descripción de Campos */


    private static int INDEX_PAYSCHEDULE = s_invoiceLayout.length-1;    // last item


    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void statInit() throws Exception {
        lDocumentNo.setLabelFor( fDocumentNo );
        fDocumentNo.setBackground( CompierePLAF.getInfoBackground());
        fDocumentNo.addActionListener( this );
        lDescription.setLabelFor( fDescription );
        fDescription.setBackground( CompierePLAF.getInfoBackground());
        fDescription.addActionListener( this );

        // lPOReference.setLabelFor(lPOReference);
        // fPOReference.setBackground(CompierePLAF.getInfoBackground());
        // fPOReference.addActionListener(this);

        isSOTrx = !"N".equals( Env.getContext( Env.getCtx(),p_WindowNo,"IsSOTrx" ));
        fIsPaid.setSelected( false );
        fIsPaid.addActionListener( this );
        if (isSOTrx) {
        	fIsSOTrx.setText(Msg.translate(Env.getCtx(),"OnlySaleTrx"));
        } else {
        	fIsSOTrx.setText(Msg.translate(Env.getCtx(),"OnlyPurchaseTrx"));
        }
        fIsSOTrx.setSelected( true );
        fIsSOTrx.addActionListener( this );

        //
        // fOrg_ID = new VLookup("AD_Org_ID", false, false, true,
        // MLookupFactory.create(Env.getCtx(), 3486, m_WindowNo, DisplayType.TableDir, false),
        // DisplayType.TableDir, m_WindowNo);
        // lOrg_ID.setLabelFor(fOrg_ID);
        // fOrg_ID.setBackground(CompierePLAF.getInfoBackground());
        // C_Invoice.C_BPartner_ID

        fBPartner_ID = new VLookup( "C_BPartner_ID",false,false,true,MLookupFactory.get( Env.getCtx(),p_WindowNo,0,3499,DisplayType.Search ));
        lBPartner_ID.setLabelFor( fBPartner_ID );
        fBPartner_ID.setBackground( CompierePLAF.getInfoBackground());

        // C_Invoice.C_Order_ID

        fOrder_ID = new VLookup( "C_Order_ID",false,false,true,MLookupFactory.get( Env.getCtx(),p_WindowNo,0,4247,DisplayType.Search ));
        lOrder_ID.setLabelFor( fOrder_ID );
        fOrder_ID.setBackground( CompierePLAF.getInfoBackground());

        //

        lDateFrom.setLabelFor( fDateFrom );
        fDateFrom.setBackground( CompierePLAF.getInfoBackground());
        fDateFrom.setToolTipText( Msg.translate( Env.getCtx(),"DateFrom" ));
        lDateTo.setLabelFor( fDateTo );
        fDateTo.setBackground( CompierePLAF.getInfoBackground());
        fDateTo.setToolTipText( Msg.translate( Env.getCtx(),"DateTo" ));
        lAmtFrom.setLabelFor( fAmtFrom );
        fAmtFrom.setBackground( CompierePLAF.getInfoBackground());
        fAmtFrom.setToolTipText( Msg.translate( Env.getCtx(),"AmtFrom" ));
        lAmtTo.setLabelFor( fAmtTo );
        fAmtTo.setBackground( CompierePLAF.getInfoBackground());
        fAmtTo.setToolTipText( Msg.translate( Env.getCtx(),"AmtTo" ));

        //

        parameterPanel.setLayout( new ALayout());

        // First Row

        parameterPanel.add( lDocumentNo,new ALayoutConstraint( 0,0 ));
        parameterPanel.add( fDocumentNo,null );
        parameterPanel.add( lBPartner_ID,null );
        parameterPanel.add( fBPartner_ID,null );
        parameterPanel.add( fIsSOTrx,new ALayoutConstraint( 0,5 ));
        parameterPanel.add( fIsPaid,null );

        // 2nd Row

        parameterPanel.add( lDescription,new ALayoutConstraint( 1,0 ));
        parameterPanel.add( fDescription,null );
        parameterPanel.add( lDateFrom,null );
        parameterPanel.add( fDateFrom,null );
        parameterPanel.add( lDateTo,null );
        parameterPanel.add( fDateTo,null );

        // 3rd Row

        parameterPanel.add( lOrder_ID,new ALayoutConstraint( 2,0 ));
        parameterPanel.add( fOrder_ID,null );
        parameterPanel.add( lAmtFrom,null );
        parameterPanel.add( fAmtFrom,null );
        parameterPanel.add( lAmtTo,null );
        parameterPanel.add( fAmtTo,null );

        // parameterPanel.add(lOrg_ID, null);
        // parameterPanel.add(fOrg_ID, null);

    }    // statInit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean initInfo() {

        // Set Defaults

        String bp = Env.getContext( Env.getCtx(),p_WindowNo,"C_BPartner_ID" );

        if( (bp != null) && (bp.length() != 0) ) {
            fBPartner_ID.setValue( new Integer( bp ));
        }

        // prepare table

        StringBuffer where = new StringBuffer( "i.IsActive='Y'" );

        if( p_whereClause.length() > 0 ) {
            where.append( " AND " ).append( Util.replace( p_whereClause,"C_Invoice.","i." ));
        }

//        prepareTable( s_invoiceLayout," C_Invoice_v i",    // corrected for CM
        prepareTable( s_invoiceLayout," C_Invoice_v i",where.toString(),"2,3,4" );

        //

// Comentado: En realidad no es lógico que al disparar el Info se gestione
//			  la actualización del campo isPaid, primeramente porque no es
//			  performante, pero adicionalmente debido a que el estado del 
//			  mismo ya es actualizado al crear/modificar/anular allocations
//        MAllocationLine.setIsPaid( Env.getCtx(),0,null );

        return true;
    }    // initInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String getSQLWhere() {
        StringBuffer sql = new StringBuffer();

        if( fDocumentNo.getText().length() > 0 ) {
            sql.append( " AND UPPER(i.DocumentNo) LIKE ?" );
        }

        if( fDescription.getText().length() > 0 ) {
            sql.append( " AND UPPER(i.Description) LIKE ?" );
        }

        // if (fPOReference.getText().length() > 0)
        // sql.append(" AND UPPER(i.POReference) LIKE ?");
        //

        if( fBPartner_ID.getValue() != null ) {
            sql.append( " AND i.C_BPartner_ID=?" );
        }

        //

        if( fOrder_ID.getValue() != null ) {
            sql.append( " AND i.C_Order_ID=?" );
        }

        //

        if( (fDateFrom.getValue() != null) || (fDateTo.getValue() != null) ) {
            Timestamp from = ( Timestamp )fDateFrom.getValue();
            Timestamp to   = ( Timestamp )fDateTo.getValue();

            if( (from == null) && (to != null) ) {
                sql.append( " AND TRUNC(i.DateInvoiced) <= ?" );
            } else if( (from != null) && (to == null) ) {
                sql.append( " AND TRUNC(i.DateInvoiced) >= ?" );
            } else if( (from != null) && (to != null) ) {
                sql.append( " AND TRUNC(i.DateInvoiced) BETWEEN ? AND ?" );
            }
        }

        //

        if( (fAmtFrom.getValue() != null) || (fAmtTo.getValue() != null) ) {
            BigDecimal from = ( BigDecimal )fAmtFrom.getValue();
            BigDecimal to   = ( BigDecimal )fAmtTo.getValue();

            if( (from == null) && (to != null) ) {
                sql.append( " AND i.GrandTotal <= ?" );
            } else if( (from != null) && (to == null) ) {
                sql.append( " AND i.GrandTotal >= ?" );
            } else if( (from != null) && (to != null) ) {
                sql.append( " AND i.GrandTotal BETWEEN ? AND ?" );
            }
        }

        //

        //sql.append( " AND i.IsPaid=? AND i.IsSOTrx=?" );

        sql.append( " AND i.IsPaid=? ");
        if (fIsSOTrx.isSelected()) {
        	sql.append(" AND i.IsSOTrx=?" );
        }
            
        log.fine( "............InfoInvoice.setWhereClause"+ sql.toString());

        return sql.toString();
    }    // getSQLWhere

    /**
     * Descripción de Método
     *
     *
     * @param pstmt
     *
     * @throws SQLException
     */

    protected void setParameters( PreparedStatement pstmt ) throws SQLException {
        int index = 1;
        log.fine("En setPArameter con pstm= "+pstmt +" y con el index= "+ index);

        if( fDocumentNo.getText().length() > 0 ) {
            pstmt.setString( index++,getSQLText( fDocumentNo ));
        }

        if( fDescription.getText().length() > 0 ) {
            pstmt.setString( index++,getSQLText( fDescription ));
        }

        // if (fPOReference.getText().length() > 0)
        // pstmt.setString(index++, getSQLText(fPOReference));
        //

        if( fBPartner_ID.getValue() != null ) {
            Integer bp = ( Integer )fBPartner_ID.getValue();
            pstmt.setInt( index++,bp.intValue());
            log.fine( "BPartner=" + bp );
        }

        //

        if( fOrder_ID.getValue() != null ) {
            Integer order = ( Integer )fOrder_ID.getValue();

            pstmt.setInt( index++,order.intValue());
            log.fine( "Order=" + order );
        }

        //

        if( (fDateFrom.getValue() != null) || (fDateTo.getValue() != null) ) {
            Timestamp from = ( Timestamp )fDateFrom.getValue();
            Timestamp to   = ( Timestamp )fDateTo.getValue();

            log.fine( "Date From=" + from + ", To=" + to );

            if( (from == null) && (to != null) ) {
                pstmt.setTimestamp( index++,to );
            } else if( (from != null) && (to == null) ) {
                pstmt.setTimestamp( index++,from );
            } else if( (from != null) && (to != null) ) {
                pstmt.setTimestamp( index++,from );
                pstmt.setTimestamp( index++,to );
            }
        }

        //

        if( (fAmtFrom.getValue() != null) || (fAmtTo.getValue() != null) ) {
            BigDecimal from = ( BigDecimal )fAmtFrom.getValue();
            BigDecimal to   = ( BigDecimal )fAmtTo.getValue();

            log.fine( "Amt From=" + from + ", To=" + to );

            if( (from == null) && (to != null) ) {
                pstmt.setBigDecimal( index++,to );
            } else if( (from != null) && (to == null) ) {
                pstmt.setBigDecimal( index++,from );
            } else if( (from != null) && (to != null) ) {
                pstmt.setBigDecimal( index++,from );
                pstmt.setBigDecimal( index++,to );
            }
        }
        log.fine("En setPArameter  con el index= "+ index);
        pstmt.setString( index++,fIsPaid.isSelected()
                                 ?"Y"
                                 :"N" );
        if (fIsSOTrx.isSelected()) {
        	pstmt.setString( index++,isSOTrx
        							?"Y"
        							:"N" );
        }
    }    // setParameters

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

    /**
     * Descripción de Método
     *
     */

    void zoom() {
        log.info( "InfoInvoice.zoom" );

        Integer C_Invoice_ID = getSelectedRowKey();
        
        if( C_Invoice_ID == null ) {
            return;
        }
        
        MQuery query = new MQuery( "C_Invoice" );
        
        query.addRestriction( "C_Invoice_ID",MQuery.EQUAL,C_Invoice_ID );

        int AD_WindowNo = getAD_Window_ID( "C_Invoice",fIsSOTrx.isSelected());
        
        zoom( AD_WindowNo,query );
    }    // zoom

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasZoom() {
        return true;
    }    // hasZoom

    /**
     * Descripción de Método
     *
     */
    

    void saveSelectionDetail() {

        // publish for Callout to read

        Integer ID = getSelectedRowKey();
       

        Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"C_Invoice_ID",(ID == null)
                ?"0"
                :ID.toString());

        //
        

        int C_InvoicePaySchedule_ID = 0;
        int row                     = p_table.getSelectedRow();
        log.fine("En save SelectionDetail, con row= " + row);

        if( row >= 0) {
        	log.fine("En save SelectionDetail, row >=0");
            Object value = p_table.getValueAt( row,INDEX_PAYSCHEDULE );
            

            if( (value != null) && (value instanceof KeyNamePair) ) {
            	
                C_InvoicePaySchedule_ID = (( KeyNamePair )value ).getKey();
            }
        }

        if( C_InvoicePaySchedule_ID < 0 ) {    // not selected
        	log.fine("En save SelectionDetail, c_invoicePayScheluleID<= 0, con C_InvoicePaySchedule_ID = " + C_InvoicePaySchedule_ID);
            Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"C_InvoicePaySchedule_ID","0" );
        } else {
        	log.fine("En save SelectionDetail, c_invoicePayScheluleID>0, con C_InvoicePaySchedule_ID = " + C_InvoicePaySchedule_ID);
            Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"C_InvoicePaySchedule_ID",String.valueOf( C_InvoicePaySchedule_ID ));
        }
    }    // saveSelectionDetail

	@Override
	protected int getInfoWidth() {
		return MY_WIDTH;
	}
    
    
}    // InfoInvoice



/*
 *  @(#)InfoInvoice.java   02.07.07
 * 
 *  Fin del fichero InfoInvoice.java
 *  
 *  VersiÃ³n 2.0
 *
 */
