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
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.util.DB;
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

public class InfoCashLine extends Info {

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

    public InfoCashLine( Frame frame,boolean modal,int WindowNo,String value,boolean multiSelection,String whereClause ) {
        super( frame,modal,WindowNo,"cl","C_CashLine_ID",multiSelection,whereClause );
        log.info( "InfoCashLine" );
        setTitle( Msg.getMsg( Env.getCtx(),"InfoCashLine" ));

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
            fName.setValue( value );
            executeQuery();
        }

        //

        pack();

        // Focus

        fName.requestFocus();
    }    // InfoCashLine

    /** Descripción de Campos */

    private ArrayList m_queryColumns = new ArrayList();

    /** Descripción de Campos */

    private String m_tableName;

    /** Descripción de Campos */

    private String m_keyColumn;

    // Static Info

    /** Descripción de Campos */

    private CLabel lName = new CLabel( Msg.translate( Env.getCtx(),"Name" ));

    /** Descripción de Campos */

    private CTextField fName = new CTextField( 10 );

    /** Descripción de Campos */

    private CLabel lCashBook_ID = new CLabel( Msg.translate( Env.getCtx(),"C_CashBook_ID" ));

    /** Descripción de Campos */

    private VLookup fCashBook_ID;

//      private CLabel lOrg_ID = new CLabel(Msg.translate(Env.getCtx(), "AD_Org_ID"));
//      private VLookup fOrg_ID;

    /** Descripción de Campos */

    private CLabel lInvoice_ID = new CLabel( Msg.translate( Env.getCtx(),"C_Invoice_ID" ));

    /** Descripción de Campos */

    private VLookup fInvoice_ID;

//      private CLabel lCharge_ID = new CLabel(Msg.translate(Env.getCtx(), "C_Charge_ID"));
//      private VLookup fCharge_ID;

    /** Descripción de Campos */

    private CLabel lBankAccount_ID = new CLabel( Msg.translate( Env.getCtx(),"C_BankAccount_ID" ));

    /** Descripción de Campos */

    private VLookup fBankAccount_ID;

    /** Descripción de Campos */

    private CCheckBox cbAbsolute = new CCheckBox( Msg.translate( Env.getCtx(),"AbsoluteAmt" ));

    //

    /** Descripción de Campos */

    private CLabel lDateFrom = new CLabel( Msg.translate( Env.getCtx(),"StatementDate" ));

    /** Descripción de Campos */

    private VDate fDateFrom = new VDate( "DateFrom",false,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),"DateFrom" ));

    /** Descripción de Campos */

    private CLabel lDateTo = new CLabel( "-" );

    /** Descripción de Campos */

    private VDate fDateTo = new VDate( "DateTo",false,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),"DateTo" ));

    /** Descripción de Campos */

    private CLabel lAmtFrom = new CLabel( Msg.translate( Env.getCtx(),"Amount" ));

    /** Descripción de Campos */

    private VNumber fAmtFrom = new VNumber( "AmtFrom",false,false,true,DisplayType.Amount,Msg.translate( Env.getCtx(),"AmtFrom" ));

    /** Descripción de Campos */

    private CLabel lAmtTo = new CLabel( "-" );

    /** Descripción de Campos */

    private VNumber fAmtTo = new VNumber( "AmtTo",false,false,true,DisplayType.Amount,Msg.translate( Env.getCtx(),"AmtTo" ));

    /** Descripción de Campos */

    private static final Info_Column[] s_cashLayout = {
        new Info_Column( " ","cl.C_CashLine_ID",IDColumn.class ),new Info_Column( Msg.translate( Env.getCtx(),"C_CashBook_ID" ),"(SELECT cb.Name FROM C_CashBook cb WHERE cb.C_CashBook_ID=c.C_CashBook_ID)",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"Name" ),"c.Name",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"StatementDate" ),"c.StatementDate",Timestamp.class ),new Info_Column( Msg.translate( Env.getCtx(),"Line" ),"cl.Line",Integer.class ),

        // new Info_Column(Msg.translate(Env.getCtx(), "C_Currency_ID"),
        // "(SELECT ISO_Code FROM C_Currency c WHERE c.C_Currency_ID=cl.C_Currency_ID)", String.class),

        new Info_Column( Msg.translate( Env.getCtx(),"Amount" ),"cl.Amount",BigDecimal.class,true,true,null ),
        //SUR SOFTWARE: Agrego columna para mostrar el monto pendiente de asignación del pago en la ventana de búsqueda
        new Info_Column( Msg.translate( Env.getCtx(),"OpenAmt" ),"cashlineAvailable(cl.C_CashLine_ID)",BigDecimal.class ),

        //

        new Info_Column( Msg.translate( Env.getCtx(),"C_Invoice_ID" ),"(SELECT i.DocumentNo||'_'||" + DB.TO_CHAR( "i.DateInvoiced",DisplayType.Date,Env.getAD_Language( Env.getCtx())) + "||'_'||" + DB.TO_CHAR( "i.GrandTotal",DisplayType.Amount,Env.getAD_Language( Env.getCtx())) + " FROM C_Invoice i WHERE i.C_Invoice_ID=cl.C_Invoice_ID)",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"C_BankAccount_ID" ),"(SELECT b.Name||' '||ba.AccountNo FROM C_Bank b, C_BankAccount ba WHERE b.C_Bank_ID=ba.C_Bank_ID AND ba.C_BankAccount_ID=cl.C_BankAccount_ID)",String.class ),new Info_Column( Msg.translate( Env.getCtx(),"C_Charge_ID" ),"(SELECT ca.Name FROM C_Charge ca WHERE ca.C_Charge_ID=cl.C_Charge_ID)",String.class ),

        //

        new Info_Column( Msg.translate( Env.getCtx(),"DiscountAmt" ),"cl.DiscountAmt",BigDecimal.class ),new Info_Column( Msg.translate( Env.getCtx(),"WriteOffAmt" ),"cl.WriteOffAmt",BigDecimal.class ),new Info_Column( Msg.translate( Env.getCtx(),"Description" ),"cl.Description",String.class )
    };

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void statInit() throws Exception {
        lName.setLabelFor( fName );
        fName.setBackground( CompierePLAF.getInfoBackground());
        fName.addActionListener( this );

        //
        // fOrg_ID = new VLookup("AD_Org_ID", false, false, true,
        // MLookupFactory.create(Env.getCtx(), 3486, m_WindowNo, DisplayType.TableDir, false),
        // DisplayType.TableDir, m_WindowNo);
        // lOrg_ID.setLabelFor(fOrg_ID);
        // fOrg_ID.setBackground(CompierePLAF.getInfoBackground());
        // 5249 - C_Cash.C_CashBook_ID

        fCashBook_ID = new VLookup( "C_CashBook_ID",false,false,true,MLookupFactory.get( Env.getCtx(),p_WindowNo,0,5249,DisplayType.TableDir ));
        lCashBook_ID.setLabelFor( fCashBook_ID );
        fCashBook_ID.setBackground( CompierePLAF.getInfoBackground());

        // 5354 - C_CashLine.C_Invoice_ID

        fInvoice_ID = new VLookup( "C_Invoice_ID",false,false,true,MLookupFactory.get( Env.getCtx(),p_WindowNo,0,5354,DisplayType.Search ));
        lInvoice_ID.setLabelFor( fInvoice_ID );
        fInvoice_ID.setBackground( CompierePLAF.getInfoBackground());

        // 5295 - C_CashLine.C_BankAccount_ID

        fBankAccount_ID = new VLookup( "C_BankAccount_ID",false,false,true,MLookupFactory.get( Env.getCtx(),p_WindowNo,0,5295,DisplayType.TableDir ));
        lBankAccount_ID.setLabelFor( fBankAccount_ID );
        fBankAccount_ID.setBackground( CompierePLAF.getInfoBackground());

        // 5296 - C_CashLine.C_Charge_ID
        // 5291 - C_CashLine.C_Cash_ID
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

        parameterPanel.add( lCashBook_ID,new ALayoutConstraint( 0,0 ));
        parameterPanel.add( fCashBook_ID,null );
        parameterPanel.add( lName,null );
        parameterPanel.add( fName,null );
        parameterPanel.add( cbAbsolute,new ALayoutConstraint( 0,5 ));

        // 2nd Row

        parameterPanel.add( lInvoice_ID,new ALayoutConstraint( 1,0 ));
        parameterPanel.add( fInvoice_ID,null );
        parameterPanel.add( lDateFrom,null );
        parameterPanel.add( fDateFrom,null );
        parameterPanel.add( lDateTo,null );
        parameterPanel.add( fDateTo,null );

        // 3rd Row

        parameterPanel.add( lBankAccount_ID,new ALayoutConstraint( 2,0 ));
        parameterPanel.add( fBankAccount_ID,null );
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

        // prepare table

        StringBuffer where = new StringBuffer( "cl.IsActive='Y'" );

        if( p_whereClause.length() > 0 ) {
            where.append( " AND " ).append( Util.replace( p_whereClause,"C_CashLine.","cl." ));
        }

        prepareTable( s_cashLayout,"C_CashLine cl INNER JOIN C_Cash c ON (cl.C_Cash_ID=c.C_Cash_ID)",where.toString(),"2,3,cl.Line" );

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

        if( fName.getText().length() > 0 ) {
            sql.append( " AND UPPER(c.Name) LIKE ?" );
        }

        //

        if( fCashBook_ID.getValue() != null ) {
            sql.append( " AND c.C_CashBook_ID=?" );
        }

        //

        if( fInvoice_ID.getValue() != null ) {
            sql.append( " AND cl.C_Invoice_ID=?" );
        }

        //

        if( (fDateFrom.getValue() != null) || (fDateTo.getValue() != null) ) {
            Timestamp from = ( Timestamp )fDateFrom.getValue();
            Timestamp to   = ( Timestamp )fDateTo.getValue();

            if( (from == null) && (to != null) ) {
                sql.append( " AND TRUNC(c.StatementDate) <= ?" );
            } else if( (from != null) && (to == null) ) {
                sql.append( " AND TRUNC(c.StatementDate) >= ?" );
            } else if( (from != null) && (to != null) ) {
                sql.append( " AND TRUNC(c.StatementDate) BETWEEN ? AND ?" );
            }
        }

        //

        if( (fAmtFrom.getValue() != null) || (fAmtTo.getValue() != null) ) {
            BigDecimal from = ( BigDecimal )fAmtFrom.getValue();
            BigDecimal to   = ( BigDecimal )fAmtTo.getValue();

            if( cbAbsolute.isSelected()) {
                sql.append( " AND ABS(cl.Amount)" );
            } else {
                sql.append( " AND cl.Amount" );
            }

            //

            if( (from == null) && (to != null) ) {
                sql.append( " <=?" );
            } else if( (from != null) && (to == null) ) {
                sql.append( " >=?" );
            } else if( (from != null) && (to != null) ) {
                if( from.compareTo( to ) == 0 ) {
                    sql.append( " =?" );
                } else {
                    sql.append( " BETWEEN ? AND ?" );
                }
            }
        }

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

        if( fName.getText().length() > 0 ) {
            pstmt.setString( index++,getSQLText( fName ));
        }

        //

        if( fCashBook_ID.getValue() != null ) {
            Integer cb = ( Integer )fCashBook_ID.getValue();

            pstmt.setInt( index++,cb.intValue());
            log.fine( "CashBook=" + cb );
        }

        //

        if( fInvoice_ID.getValue() != null ) {
            Integer i = ( Integer )fInvoice_ID.getValue();

            pstmt.setInt( index++,i.intValue());
            log.fine( "Invoice=" + i );
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

            if( cbAbsolute.isSelected()) {
                if( from != null ) {
                    from = from.abs();
                }

                if( to != null ) {
                    to = to.abs();
                }
            }

            log.fine( "Amt From=" + from + ", To=" + to + ", Absolute=" + cbAbsolute.isSelected());

            if( (from == null) && (to != null) ) {
                pstmt.setBigDecimal( index++,to );
            } else if( (from != null) && (to == null) ) {
                pstmt.setBigDecimal( index++,from );
            } else if( (from != null) && (to != null) ) {
                if( from.compareTo( to ) == 0 ) {
                    pstmt.setBigDecimal( index++,from );
                } else {
                    pstmt.setBigDecimal( index++,from );
                    pstmt.setBigDecimal( index++,to );
                }
            }
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
}    // InfoCashLine



/*
 *  @(#)InfoCashLine.java   02.07.07
 * 
 *  Fin del fichero InfoCashLine.java
 *  
 *  Versión 2.2
 *
 */
