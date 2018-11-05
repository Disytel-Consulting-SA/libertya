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

public class InfoOrder extends Info {

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

    public InfoOrder( Frame frame,boolean modal,int WindowNo,String value,boolean multiSelection,String whereClause, boolean addSecurityValidation ) {
        super( frame,modal,WindowNo,"o","C_Order_ID",multiSelection,whereClause );
        log.info( "InfoOrder" );
        setTitle( Msg.getMsg( Env.getCtx(),"InfoOrder" ));
        setAddSecurityValidation(addSecurityValidation);
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
    }    // InfoOrder

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

    /** Descripción de Campos */

    private CLabel lPOReference = new CLabel( Msg.translate( Env.getCtx(),"POReference" ));

    /** Descripción de Campos */

    private CTextField fPOReference = new CTextField( 10 );

    //
//      private CLabel lOrg_ID = new CLabel(Msg.translate(Env.getCtx(), "AD_Org_ID"));
//      private VLookup fOrg_ID;

    /** Descripción de Campos */

    private CLabel lBPartner_ID = new CLabel( Msg.translate( Env.getCtx(),"C_BPartner_ID" ));

    /** Descripción de Campos */

    private VLookup fBPartner_ID;

    //

    /** Descripción de Campos */

    private CLabel lDateFrom = new CLabel( Msg.translate( Env.getCtx(),"DateOrdered" ));

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

    /** Descripción de Campos */

    private VCheckBox fIsSOTrx = new VCheckBox( "IsSOTrx",false,false,true,Msg.translate( Env.getCtx(),"IsSOTrx" ),"",false );

    /** Descripción de Campos */

    private static final Info_Column[] s_invoiceLayout = {
        new Info_Column( " ","o.C_Order_ID",IDColumn.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"C_BPartner_ID" ),"(SELECT Name FROM C_BPartner bp WHERE bp.C_BPartner_ID=o.C_BPartner_ID)",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"DateOrdered" ),"o.DateOrdered",Timestamp.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"DocumentNo" ),"o.DocumentNo",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"C_Currency_ID" ),"(SELECT ISO_Code FROM C_Currency c WHERE c.C_Currency_ID=o.C_Currency_ID)",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"GrandTotal" ),"o.GrandTotal",BigDecimal.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"ConvertedAmount" ),"currencyBase(o.GrandTotal,o.C_Currency_ID,o.DateAcct, o.AD_Client_ID,o.AD_Org_ID)",BigDecimal.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"IsSOTrx" ),"o.IsSOTrx",Boolean.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"Description" ),"o.Description",String.class ),
        new Info_Column( Msg.translate( Env.getCtx(),"POReference" ),"o.POReference",String.class )
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
        lDescription.setLabelFor( fDescription );
        fDescription.setBackground( CompierePLAF.getInfoBackground());
        fDescription.addActionListener( this );
        lPOReference.setLabelFor( lPOReference );
        fPOReference.setBackground( CompierePLAF.getInfoBackground());
        fPOReference.addActionListener( this );
        fIsSOTrx.setSelected( !"N".equals( Env.getContext( Env.getCtx(),p_WindowNo,"IsSOTrx" )));
        fIsSOTrx.addActionListener( this );

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
        parameterPanel.add( fIsSOTrx,new ALayoutConstraint( 0,5 ));

        // 2nd Row

        parameterPanel.add( lDescription,new ALayoutConstraint( 1,0 ));
        parameterPanel.add( fDescription,null );
        parameterPanel.add( lDateFrom,null );
        parameterPanel.add( fDateFrom,null );
        parameterPanel.add( lDateTo,null );
        parameterPanel.add( fDateTo,null );

        // 3rd Row

//        parameterPanel.add( lPOReference,new ALayoutConstraint( 2,0 ));
//        parameterPanel.add( fPOReference,null );
        parameterPanel.add( lAmtFrom,new ALayoutConstraint( 2,2 ) );
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

        StringBuffer where = new StringBuffer( "o.IsActive='Y'" );

        if( p_whereClause.length() > 0 ) {
            where.append( " AND " ).append( Util.replace( p_whereClause,"C_Order.","o." ));
        }

        prepareTable( s_invoiceLayout," C_Order o",where.toString(),"2,3,4" );

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
            sql.append( " AND UPPER(o.DocumentNo) LIKE ?" );
        }

        if( fDescription.getText().length() > 0 ) {
            sql.append( " AND UPPER(o.Description) LIKE ?" );
        }

        if( fPOReference.getText().length() > 0 ) {
            sql.append( " AND UPPER(o.POReference) LIKE ?" );
        }

        //

        if( fBPartner_ID.getValue() != null ) {
            sql.append( " AND o.C_BPartner_ID=?" );
        }

        //

        if( (fDateFrom.getValue() != null) || (fDateTo.getValue() != null) ) {
            Timestamp from = ( Timestamp )fDateFrom.getValue();
            Timestamp to   = ( Timestamp )fDateTo.getValue();

            if( (from == null) && (to != null) ) {
                sql.append( " AND TRUNC(o.DateOrdered) <= ?" );
            } else if( (from != null) && (to == null) ) {
                sql.append( " AND TRUNC(o.DateOrdered) >= ?" );
            } else if( (from != null) && (to != null) ) {
                sql.append( " AND TRUNC(o.DateOrdered) BETWEEN ? AND ?" );
            }
        }

        //

        if( (fAmtFrom.getValue() != null) || (fAmtTo.getValue() != null) ) {
            BigDecimal from = ( BigDecimal )fAmtFrom.getValue();
            BigDecimal to   = ( BigDecimal )fAmtTo.getValue();

            if( (from == null) && (to != null) ) {
                sql.append( " AND o.GrandTotal <= ?" );
            } else if( (from != null) && (to == null) ) {
                sql.append( " AND o.GrandTotal >= ?" );
            } else if( (from != null) && (to != null) ) {
                sql.append( " AND o.GrandTotal BETWEEN ? AND ?" );
            }
        }

        sql.append( " AND o.IsSOTrx=?" );
        log.finer( sql.toString());

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
        log.fine("En setPArameter del infoOrder con pstm= "+pstmt +" y con el index= "+ index);

        if( fDocumentNo.getText().length() > 0 ) {
            pstmt.setString( index++,getSQLText( fDocumentNo ));
        }

        if( fDescription.getText().length() > 0 ) {
            pstmt.setString( index++,getSQLText( fDescription ));
        }

        if( fPOReference.getText().length() > 0 ) {
            pstmt.setString( index++,getSQLText( fPOReference ));
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
        log.fine("En setPArameter  con el index= "+ index);

        pstmt.setString( index++,fIsSOTrx.isSelected()
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
        log.info( "" );

        Integer C_Order_ID = getSelectedRowKey();

        if( C_Order_ID == null ) {
            return;
        }

        MQuery query = new MQuery( "C_Order" );

        query.addRestriction( "C_Order_ID",MQuery.EQUAL,C_Order_ID );

        int AD_WindowNo = getAD_Window_ID( "C_Order",fIsSOTrx.isSelected());

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
}    // InfoOrder



/*
 *  @(#)InfoOrder.java   02.07.07
 * 
 *  Fin del fichero InfoOrder.java
 *  
 *  Versión 2.2
 *
 */
