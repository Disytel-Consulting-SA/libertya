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



package org.openXpertya.acct;

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;

import org.openXpertya.model.*;
import org.openXpertya.report.core.*;
import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

class AcctViewerData {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param windowNo
     * @param ad_Client_ID
     * @param ad_Table_ID
     */

    public AcctViewerData( Properties ctx,int windowNo,int ad_Client_ID,int ad_Table_ID ) {
        WindowNo     = windowNo;
        AD_Client_ID = ad_Client_ID;

        if( AD_Client_ID == 0 ) {
            AD_Client_ID = Env.getContextAsInt( Env.getCtx(),WindowNo,"AD_Client_ID" );
        }

        if( AD_Client_ID == 0 ) {
            AD_Client_ID = Env.getContextAsInt( Env.getCtx(),"AD_Client_ID" );
        }

        AD_Table_ID = ad_Table_ID;

        //

        ASchemas = MAcctSchema.getClientAcctSchema( ctx,AD_Client_ID );
        ASchema  = ASchemas[ 0 ];
    }    // AcctViewerData

    /** Descripción de Campos */

    public int WindowNo;

    /** Descripción de Campos */

    public int AD_Client_ID;

    /** Descripción de Campos */

    public MAcctSchema[] ASchemas = null;

    /** Descripción de Campos */

    public MAcctSchema ASchema = null;

    // Selection Info

    /** Descripción de Campos */

    public boolean documentQuery = false;

    /** Descripción de Campos */

    public int C_AcctSchema_ID = 0;

    /** Descripción de Campos */

    public String PostingType = "";

    /** Descripción de Campos */

    public int AD_Org_ID = 0;

    /** Descripción de Campos */

    public Timestamp DateFrom = null;

    /** Descripción de Campos */

    public Timestamp DateTo = null;

    // Table Selection Info

    /** Descripción de Campos */

    public int AD_Table_ID;

    /** Descripción de Campos */

    public int Record_ID;

    /** Descripción de Campos */

    public HashMap whereInfo = new HashMap();

    /** Descripción de Campos */

    public HashMap tableInfo = new HashMap();

    // Display Info

    /** Descripción de Campos */

    public boolean displayQty = false;

    /** Descripción de Campos */

    public boolean displaySourceAmt = false;

    /** Descripción de Campos */

    public boolean displayDocumentInfo = false;
    
    /** Descripción de Campos */

    public boolean displayBalance = false;

    //

    /** Descripción de Campos */

    public String sortBy1 = "";

    /** Descripción de Campos */

    public String sortBy2 = "";

    /** Descripción de Campos */

    public String sortBy3 = "";

    /** Descripción de Campos */

    public String sortBy4 = "";

    //

    /** Descripción de Campos */

    public boolean group1 = false;

    /** Descripción de Campos */

    public boolean group2 = false;

    /** Descripción de Campos */

    public boolean group3 = false;

    /** Descripción de Campos */

    public boolean group4 = false;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( AcctViewerData.class );

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        ASchemas = null;
        ASchema  = null;

        //

        whereInfo.clear();
        whereInfo = null;

        //

        Env.clearWinContext( WindowNo );
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param cb
     */

    protected void fillAcctSchema( JComboBox cb ) {

        // KeyNamePair pp = new KeyNamePair(0, "");
        // cb.addItem(pp);

        for( int i = 0;i < ASchemas.length;i++ ) {
            cb.addItem( new KeyNamePair( ASchemas[ i ].getC_AcctSchema_ID(),ASchemas[ i ].getName()));
        }
    }    // fillAcctSchema

    /**
     * Descripción de Método
     *
     *
     * @param cb
     */

    protected void fillPostingType (JComboBox cb)
	{
		int AD_Reference_ID = 125;
		ValueNamePair[] pt = MRefList.getList(AD_Reference_ID, false);
		for (int i = 0; i < pt.length; i++)
			cb.addItem(pt[i]);
	}   //  fillPostingType

    /**
     * Descripción de Método
     *
     *
     * @param cb
     */

    protected void fillTable( JComboBox cb ) {
        ValueNamePair select = null;

        //

        String sql = "SELECT AD_Table_ID, TableName FROM AD_Table t " + "WHERE EXISTS (SELECT * FROM AD_Column c" + " WHERE t.AD_Table_ID=c.AD_Table_ID AND c.ColumnName='Posted')" + " AND IsView='N'";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                int    id        = rs.getInt( 1 );
                String tableName = rs.getString( 2 );
                String name      = Msg.translate( Env.getCtx(),tableName + "_ID" );

                //

                ValueNamePair pp = new ValueNamePair( tableName,name );

                cb.addItem( pp );
                tableInfo.put( tableName,new Integer( id ));

                if( id == AD_Table_ID ) {
                    select = pp;
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"AcctViewerData.fillTable",e );
        }

        if( select != null ) {
            cb.setSelectedItem( select );
        }
    }    // fillTable

    /**
     * Descripción de Método
     * Modificado por Mauricio Calgaro
     * Fecha Modificación 13-12-2010
     * @param cb
     */

    protected void fillOrg( JComboBox cb ) {
        KeyNamePair pp = new KeyNamePair( 0,"" );

        cb.addItem( pp );

        String sql = "SELECT AD_Org_ID, Name FROM AD_Org WHERE AD_Client_ID=? AND ISACTIVE='Y' ORDER BY Value";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                cb.addItem( new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 )));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"AcctViewerData.fillOrg",e );
        }
    }    // fillOrg

    /**
     * Descripción de Método
     *
     *
     * @param tableName
     * @param columnName
     * @param selectSQL
     *
     * @return
     */

    protected String getButtonText( String tableName,String columnName,String selectSQL ) {

        // SELECT (<embedded>) FROM tableName avd WHERE avd.<selectSQL>

        StringBuffer sql      = new StringBuffer( "SELECT (" );
        Language     language = Env.getLanguage( Env.getCtx());

        sql.append( MLookupFactory.getLookup_TableDirEmbed( language,columnName,"avd" )).append( ") FROM " ).append( tableName ).append( " avd WHERE avd." ).append( selectSQL );

        String retValue = "<" + selectSQL + ">";

        try {
            Statement stmt = DB.createStatement();
            ResultSet rs   = stmt.executeQuery( sql.toString());

            if( rs.next()) {
                retValue = rs.getString( 1 );
            }

            rs.close();
            stmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"AcctViewerData.actionButton",e );
        }

        return retValue;
    }    // getButtonText

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected RModel query() {

        // Set Where Clause

        StringBuffer whereClause = new StringBuffer();

        // Add Organization

        if( C_AcctSchema_ID != 0 ) {
            whereClause.append( RModel.TABLE_ALIAS ).append( ".C_AcctSchema_ID=" ).append( C_AcctSchema_ID );
        }

        //

        if( documentQuery ) {
            if( whereClause.length() > 0 ) {
                whereClause.append( " AND " );
            }

            whereClause.append( RModel.TABLE_ALIAS ).append( ".AD_Table_ID=" ).append( AD_Table_ID ).append( " AND " ).append( RModel.TABLE_ALIAS ).append( ".Record_ID=" ).append( Record_ID );
        } else {
            if( whereClause.length() > 0 ) {
                whereClause.append( " AND " );
            }

            whereClause.append( RModel.TABLE_ALIAS ).append( ".PostingType='" ).append( PostingType ).append( "'" );

            // get values (Queries)

            Iterator it = whereInfo.values().iterator();

            while( it.hasNext()) {
                String where = ( String )it.next();

                if( (where != null) && (where.length() > 0) )    // add only if not empty
                {
                    if( whereClause.length() > 0 ) {
                        whereClause.append( " AND " );
                    }

                    whereClause.append( RModel.TABLE_ALIAS ).append( "." ).append( where );
                }
            }

            if( (DateFrom != null) || (DateTo != null) ) {
                if( whereClause.length() > 0 ) {
                    whereClause.append( " AND " );
                }

                if( (DateFrom != null) && (DateTo != null) ) {
                    whereClause.append( "TRUNC(" ).append( RModel.TABLE_ALIAS ).append( ".DateAcct) BETWEEN " ).append( DB.TO_DATE( DateFrom )).append( " AND " ).append( DB.TO_DATE( DateTo ));
                } else if( DateFrom != null ) {
                    whereClause.append( "TRUNC(" ).append( RModel.TABLE_ALIAS ).append( ".DateAcct) >= " ).append( DB.TO_DATE( DateFrom ));
                } else {    // DateTo != null
                    whereClause.append( "TRUNC(" ).append( RModel.TABLE_ALIAS ).append( ".DateAcct) <= " ).append( DB.TO_DATE( DateTo ));
                }
            }

            // Add Organization

            if( AD_Org_ID != 0 ) {
                if( whereClause.length() > 0 ) {
                    whereClause.append( " AND " );
                }

                whereClause.append( RModel.TABLE_ALIAS ).append( ".AD_Org_ID=" ).append( AD_Org_ID );
            }
        }

        // Set Order By Clause

        StringBuffer orderClause = new StringBuffer();

        if( sortBy1.length() > 0 ) {
            orderClause.append( RModel.TABLE_ALIAS ).append( "." ).append( sortBy1 );
        }

        if( sortBy2.length() > 0 ) {
            if( orderClause.length() > 0 ) {
                orderClause.append( "," );
            }

            orderClause.append( RModel.TABLE_ALIAS ).append( "." ).append( sortBy2 );
        }

        if( sortBy3.length() > 0 ) {
            if( orderClause.length() > 0 ) {
                orderClause.append( "," );
            }

            orderClause.append( RModel.TABLE_ALIAS ).append( "." ).append( sortBy3 );
        }

        if( sortBy4.length() > 0 ) {
            if( orderClause.length() > 0 ) {
                orderClause.append( "," );
            }

            orderClause.append( RModel.TABLE_ALIAS ).append( "." ).append( sortBy4 );
        }

        if( orderClause.length() == 0 ) {
            orderClause.append( RModel.TABLE_ALIAS ).append( ".Fact_Acct_ID" );
        }

        RModel rm = getRModel();

        // Groups

        if( group1 && (sortBy1.length() > 0) ) {
            rm.setGroup( sortBy1 );
        }

        if( group2 && (sortBy2.length() > 0) ) {
            rm.setGroup( sortBy2 );
        }

        if( group3 && (sortBy3.length() > 0) ) {
            rm.setGroup( sortBy3 );
        }

        if( group4 && (sortBy4.length() > 0) ) {
            rm.setGroup( sortBy4 );
        }

        // Totals

        rm.setFunction( "AmtAcctDr",RModel.FUNCTION_SUM );
        rm.setFunction( "AmtAcctCr",RModel.FUNCTION_SUM );
        
        if (displayBalance)
        	rm.setFunction( "AmtAcctDr - AmtAcctCr",RModel.FUNCTION_SUM );
        
        rm.query( Env.getCtx(),whereClause.toString(),orderClause.toString());

        return rm;
    }    // query

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private RModel getRModel() {
        Properties ctx = Env.getCtx();
        RModel     rm  = new RModel( "Fact_Acct" );

        // Add Key (Lookups)

        ArrayList keys = createKeyColumns();
        // Disytel: comentadas debido a que estaban duplicadas
      //  rm.addColumn( new RColumn( ctx,"AmtSourceDr",DisplayType.Amount ));
      //  rm.addColumn( new RColumn( ctx,"AmtSourceCr",DisplayType.Amount ));
        for( int i = 0;i < keys.size();i++ ) {
            String column = ( String )keys.get( i );

            if( (column != null) && column.startsWith( "Date" )) {
                rm.addColumn( new RColumn( ctx,column,DisplayType.Date ));
            } else if( (column != null) && column.endsWith( "_ID" )) {
                rm.addColumn( new RColumn( ctx,column,DisplayType.TableDir ));
            }
        }

        // Info

        if( !keys.contains( "DateAcct" )) {
            rm.addColumn( new RColumn( ctx,"DateAcct",DisplayType.Date ));
        }

        if( !keys.contains( "C_Period_ID" )) {
            rm.addColumn( new RColumn( ctx,"C_Period_ID",DisplayType.TableDir ));
        }

        rm.addColumn( new RColumn( ctx,"AmtAcctDr",DisplayType.Amount ));
        rm.addColumn( new RColumn( ctx,"AmtAcctCr",DisplayType.Amount ));

        if( displayBalance ) {
			rm.addColumn( new RColumn( ctx,"Balance",DisplayType.Amount, "AmtAcctDr - AmtAcctCr" ));
        }
        
        if( displaySourceAmt ) {
            if( !keys.contains( "DateTrx" )) {
                rm.addColumn( new RColumn( ctx,"DateTrx",DisplayType.Date ));
            }

            rm.addColumn( new RColumn( ctx,"C_Currency_ID",DisplayType.TableDir ));
            
            // Disytel: comentadas segun referencia 1.9
            // rm.addColumn( new RColumn( ctx,"AmtSourceDr",DisplayType.Amount ));
            // rm.addColumn( new RColumn( ctx,"AmtSourceCr",DisplayType.Amount ));
            
            // Disytel: fue necesario indicar la division sin espacios intermedios a fin de que el convert no realice un split erroneo del query 
            rm.addColumn( new RColumn( ctx,"Rate",DisplayType.Amount,"CASE WHEN (AmtSourceDr + AmtSourceCr) = 0 THEN 0" + " ELSE ((AmtAcctDr + AmtAcctCr)/(AmtSourceDr + AmtSourceCr)) END" ));

        }

        if( displayQty ) {
            rm.addColumn( new RColumn( ctx,"C_UOM_ID",DisplayType.TableDir ));
            rm.addColumn( new RColumn( ctx,"Qty",DisplayType.Quantity ));
        }
		if (displayDocumentInfo)
		{
			rm.addColumn(new RColumn(ctx, "Description", DisplayType.String));
			// Las siguientes deben ser las ultimas columnas:
			rm.addColumn(new RColumn(ctx, "AD_Table_ID", DisplayType.ID));
            rm.addColumn( new RColumn( ctx,"Record_ID",DisplayType.ID ));
        }
		
		
        return rm;
    }    // createRModel

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private ArrayList createKeyColumns() {
        ArrayList columns = new ArrayList();

        // Sorting Fields

        columns.add( sortBy1 );    // may add ""

        if( !columns.contains( sortBy2 )) {
            columns.add( sortBy2 );
        }

        if( !columns.contains( sortBy3 )) {
            columns.add( sortBy3 );
        }

        if( !columns.contains( sortBy4 )) {
            columns.add( sortBy4 );
        }

        // Add Account Segments

        MAcctSchemaElement[] elements = ASchema.getAcctSchemaElements();

        for( int i = 0;i < elements.length;i++ ) {
            MAcctSchemaElement ase        = elements[ i ];
            String             columnName = ase.getColumnName();

            if( !columns.contains( columnName )) {
                columns.add( columnName );
            }
        }

        //

        return columns;
    }    // createKeyColumns
}    // AcctViewerData



/*
 *  @(#)AcctViewerData.java   02.07.07
 *
 *  Fin del fichero AcctViewerData.java
 *
 *  Versión 2.2
 *
 */