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
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MQuery;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InfoPayment extends Info {

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

    public InfoPayment( Frame frame,boolean modal,int WindowNo,String value,boolean multiSelection,String whereClause ) {
        super( frame,modal,WindowNo,"p","C_Payment_ID",multiSelection,whereClause );
        log.info( "InfoPayment" );
        setTitle( Msg.getMsg( Env.getCtx(),"InfoPayment" ));

        //

        try {
            statInit();
            p_loadedOK = initInfo();
        } catch( Exception e ) {
            return;
        }

        //

        int no = p_table.getRowCount();

        setStatusLine( Integer.toString( no ) + " " + Msg.getMsg( Env.getCtx(),"SearchRows_EnterQuery" ),false );
        setStatusDB( Integer.toString( no ));

        if( (value != null) && (value.length() > 0) ) {
            fDocumentNo.setValue( value );
            executeQuery();
        }

        //

        pack();

        // Focus

        fDocumentNo.requestFocus();
    }    // InfoPayment

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

    //
//      private CLabel lOrg_ID = new CLabel(Msg.translate(Env.getCtx(), "AD_Org_ID"));
//      private VLookup fOrg_ID;

    /** Descripción de Campos */

    private CLabel lBPartner_ID = new CLabel( Msg.translate( Env.getCtx(),"C_BPartner_ID" ));

    /** Descripción de Campos */

    private VLookup fBPartner_ID;

    //

    /** Descripción de Campos */

    private CLabel lDateFrom = new CLabel( Msg.translate( Env.getCtx(),"DateTrx" ));

    /** Descripción de Campos */

    private VDate fDateFrom = new VDate( "DateFrom",false,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),"DateFrom" ));

    /** Descripción de Campos */

    private CLabel lDateTo = new CLabel( "-" );

    /** Descripción de Campos */

    private VDate fDateTo = new VDate( "DateTo",false,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),"DateTo" ));

    /** Descripción de Campos */

    private CLabel lAmtFrom = new CLabel( Msg.translate( Env.getCtx(),"PayAmt" ));

    /** Descripción de Campos */

    private VNumber fAmtFrom = new VNumber( "AmtFrom",false,false,true,DisplayType.Amount,Msg.translate( Env.getCtx(),"AmtFrom" ));

    /** Descripción de Campos */

    private CLabel lAmtTo = new CLabel( "-" );

    /** Descripción de Campos */

    private VNumber fAmtTo = new VNumber( "AmtTo",false,false,true,DisplayType.Amount,Msg.translate( Env.getCtx(),"AmtTo" ));

    /** Descripción de Campos */

    private VCheckBox fIsReceipt = new VCheckBox( "IsReceipt",false,false,true,Msg.translate( Env.getCtx(),"IsReceipt" ),"",false );

    /** Descripción de Campos */

    private static final Info_Column[] s_paymentLayout = {
        new Info_Column( " ","p.C_Payment_ID",IDColumn.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"C_BankAccount_ID" ),"(SELECT b.Name || ' ' || ba.AccountNo FROM C_Bank b, C_BankAccount ba WHERE b.C_Bank_ID=ba.C_Bank_ID AND ba.C_BankAccount_ID=p.C_BankAccount_ID)",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"C_BPartner_ID" ),"(SELECT Name FROM C_BPartner bp WHERE bp.C_BPartner_ID=p.C_BPartner_ID)",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"DateTrx" ),"p.DateTrx",Timestamp.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"DueDateShort" ),"p.DueDate",Timestamp.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"DocumentNo" ),"p.DocumentNo",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"TenderType" ),"(SELECT trl.name as TenderType "
        															+ "FROM ad_ref_list rl "
        															+ "INNER JOIN ad_ref_list_trl trl ON rl.ad_ref_list_id = trl.ad_ref_list_id "
        															+ "WHERE rl.ad_reference_id = " + MPayment.TENDERTYPE_AD_Reference_ID 
        															+ "		AND p.TenderType = rl.value "
        															+ "		AND trl.ad_language = '"+Env.getAD_Language(Env.getCtx())+"')",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"IsReceipt" ),"p.IsReceipt",Boolean.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"C_Currency_ID" ),"(SELECT ISO_Code FROM C_Currency c WHERE c.C_Currency_ID=p.C_Currency_ID)",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"PayAmt" ),"p.PayAmt",BigDecimal.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"ConvertedAmount" ),"currencyBase(p.PayAmt,p.C_Currency_ID,p.DateTrx, p.AD_Client_ID,p.AD_Org_ID)",BigDecimal.class ),
        //SUR SOFTWARE: Agrego columna para mostrar el monto pendiente de asignación del pago en la ventana de búsqueda
        new Info_Column( Msg.translate( Env.getCtx(),"OpenAmt" ),"paymentAvailable(p.C_Payment_ID)",BigDecimal.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"DiscountAmt" ),"p.DiscountAmt",BigDecimal.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"WriteOffAmt" ),"p.WriteOffAmt",BigDecimal.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"IsAllocated" ),"p.IsAllocated",Boolean.class )
    };

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
        fIsReceipt.setSelected( !"N".equals( Env.getContext( Env.getCtx(),p_WindowNo,"IsSOTrx" )));
        fIsReceipt.addActionListener( this );

        //
        // fOrg_ID = new VLookup("AD_Org_ID", false, false, true,
        // MLookupFactory.create(Env.getCtx(), 3486, m_WindowNo, DisplayType.TableDir, false),
        // DisplayType.TableDir, m_WindowNo);
        // lOrg_ID.setLabelFor(fOrg_ID);
        // fOrg_ID.setBackground(CompierePLAF.getInfoBackground());

        fBPartner_ID = new VLookup( "C_BPartner_ID",false,false,true,MLookupFactory.get( Env.getCtx(),p_WindowNo,0,3499,DisplayType.Search ));
        lBPartner_ID.setLabelFor( fBPartner_ID );
        fBPartner_ID.setBackground( CompierePLAF.getInfoBackground());

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
        parameterPanel.add( fIsReceipt,new ALayoutConstraint( 0,5 ));

        // 2nd Row

        parameterPanel.add( lDateFrom,new ALayoutConstraint( 1,2 ));
        parameterPanel.add( fDateFrom,null );
        parameterPanel.add( lDateTo,null );
        parameterPanel.add( fDateTo,null );

        // 3rd Row

        parameterPanel.add( lAmtFrom,new ALayoutConstraint( 2,2 ));
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

        StringBuffer where = new StringBuffer( "p.IsActive='Y'" );

        if( p_whereClause.length() > 0 ) {
            where.append( " AND " ).append( Util.replace( p_whereClause,"C_Payment.","p." ));
        }

        prepareTable( s_paymentLayout," C_Payment p",where.toString(),"2,3,4" );

		// Se comenta ya que a mucho volumen de datos demora un tiempo
		// considerable y además cada payment se setea allocated al completar o
		// anular un allocation
        // MAllocationLine.setIsAllocated( Env.getCtx(),0,null );

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
            sql.append( " AND UPPER(p.DocumentNo) LIKE ?" );
        }

        //

        if( fBPartner_ID.getValue() != null ) {
            sql.append( " AND p.C_BPartner_ID=?" );
        }

        //

        if( (fDateFrom.getValue() != null) || (fDateTo.getValue() != null) ) {
            Timestamp from = ( Timestamp )fDateFrom.getValue();
            Timestamp to   = ( Timestamp )fDateTo.getValue();

            if( (from == null) && (to != null) ) {
                sql.append( " AND TRUNC(p.DateTrx) <= ?" );
            } else if( (from != null) && (to == null) ) {
                sql.append( " AND TRUNC(p.DateTrx) >= ?" );
            } else if( (from != null) && (to != null) ) {
                sql.append( " AND TRUNC(p.DateTrx) BETWEEN ? AND ?" );
            }
        }

        //

        if( (fAmtFrom.getValue() != null) || (fAmtTo.getValue() != null) ) {
            BigDecimal from = ( BigDecimal )fAmtFrom.getValue();
            BigDecimal to   = ( BigDecimal )fAmtTo.getValue();

            if( (from == null) && (to != null) ) {
                sql.append( " AND p.PayAmt <= ?" );
            } else if( (from != null) && (to == null) ) {
                sql.append( " AND p.PayAmt >= ?" );
            } else if( (from != null) && (to != null) ) {
                sql.append( " AND p.PayAmt BETWEEN ? AND ?" );
            }
        }

        sql.append( " AND p.IsReceipt=?" );
        log.fine( sql.toString());

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

        if( fDocumentNo.getText().length() > 0 ) {
            pstmt.setString( index++,getSQLText( fDocumentNo ));
        }

        //

        if( fBPartner_ID.getValue() != null ) {
            Integer bp = ( Integer )fBPartner_ID.getValue();

            pstmt.setInt( index++,bp.intValue());
            log.fine( "BPartner=" + bp );
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

        pstmt.setString( index++,fIsReceipt.isSelected()
                                 ?"Y"
                                 :"N" );
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
        log.info( "InfoPayment.zoom" );

        Integer C_Payment_ID = getSelectedRowKey();

        if( C_Payment_ID == null ) {
            return;
        }

        MQuery query = new MQuery( "C_Payment" );

        query.addRestriction( "C_Payment_ID",MQuery.EQUAL,C_Payment_ID );

        int AD_WindowNo = getAD_Window_ID( "C_Payment",fIsReceipt.isSelected());

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
}    // InfoPayment



/*
 *  @(#)InfoPayment.java   02.07.07
 * 
 *  Fin del fichero InfoPayment.java
 *  
 *  Versión 2.2
 *
 */
