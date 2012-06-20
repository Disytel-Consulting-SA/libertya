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



package org.openXpertya.report.core;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MRole;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

class RModelData {

    /**
     * Constructor de la clase ...
     *
     *
     * @param TableName
     */

    public RModelData( String TableName ) {
        m_TableName = TableName;
    }    // RModelData

    /** Descripción de Campos */

    public ArrayList rows = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_rows = new ArrayList();

    /** Descripción de Campos */

    public ArrayList rowsMeta = new ArrayList();

    /** Descripción de Campos */

    public ArrayList cols = new ArrayList();

    /** Descripción de Campos */

    private String m_TableName;

    /** Descripción de Campos */

    public HashMap functions = new HashMap();

    /** Descripción de Campos */

    public ArrayList groups = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_groupRows = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_groupRowsIndicator = null;

    /** Descripción de Campos */

    private static final BigDecimal ONE = new BigDecimal( 1.0 );

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( RModelData.class );

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        rows.clear();
        m_rows.clear();
        rowsMeta.clear();
        cols.clear();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param whereClause
     * @param orderClause
     */

    public void query( Properties ctx,String whereClause,String orderClause ) {
        RColumn rc = null;

        // Create SQL

        StringBuffer sql  = new StringBuffer( "SELECT " );
        int          size = cols.size();

        for( int i = 0;i < size;i++ ) {
            rc = ( RColumn )cols.get( i );

            if( i > 0 ) {
                sql.append( "," );
            }

            sql.append( rc.getColSQL());
        }

        sql.append( " FROM " ).append( m_TableName ).append( " " ).append( RModel.TABLE_ALIAS );

        if( (whereClause != null) && (whereClause.length() > 0) ) {
            sql.append( " WHERE " ).append( whereClause );
        }

        String finalSQL = MRole.getDefault( ctx,false ).addAccessSQL( sql.toString(),RModel.TABLE_ALIAS,MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO );

        if( (orderClause != null) && (orderClause.length() > 0) ) {
            finalSQL += " ORDER BY " + orderClause;
        }

        log.fine( "RModelData.query SQL=" + finalSQL );

        // FillData

        int index = 1;    // rowset index

        m_rows.clear();

        try {
            Statement stmt = DB.createStatement();
            ResultSet rs   = stmt.executeQuery( finalSQL );

            while( rs.next()) {
                ArrayList row = new ArrayList( size );

                index = 1;

                // Columns

                for( int i = 0;i < size;i++ ) {
                    rc = ( RColumn )cols.get( i );

                    // Get ID

                    if( rc.isIDcol()) {
                        row.add( new KeyNamePair( rs.getInt( index++ ),rs.getString( index++ )));

                        // Null check

                    } else if( rs.getObject( index ) == null ) {
                        index++;
                        row.add( null );
                    } else if( rc.getColClass() == String.class ) {
                        row.add( rs.getString( index++ ));
                    } else if( rc.getColClass() == BigDecimal.class ) {
                        row.add( rs.getBigDecimal( index++ ));
                    } else if( rc.getColClass() == Double.class ) {
                        row.add( new Double( rs.getDouble( index++ )));
                    } else if( rc.getColClass() == Integer.class ) {
                        row.add( new Integer( rs.getInt( index++ )));
                    } else if( rc.getColClass() == Timestamp.class ) {
                        row.add( rs.getTimestamp( index++ ));
                    } else if( rc.getColClass() == Boolean.class ) {
                        row.add( new Boolean( "Y".equals( rs.getString( index++ ))));
                    } else    // should not happen
                    {
                        row.add( rs.getString( index++ ));
                    }
                }

                m_rows.add( row );
            }

            rs.close();
            stmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"RModelData.query - Index=" + index + "," + rc,e );
            e.printStackTrace();
        }

        process();
    }    // query

    /**
     * Descripción de Método
     *
     */

    private void process() {
        log.fine( "RModelData.process - Start Rows=" + m_rows.size());

        // Row level Funcions
        // would come here

        // Group by Values
        //Modificado por ConSerTi, el tamaño de gSize
        int      gSize         = groups.size();
        int[]    groupBys      = new int[ gSize ];
        Object[] groupBysValue = new Object[ gSize ];
        Object   INITVALUE     = new Object();

        for( int i = 0;i < gSize;i++ ) {
            groupBys[ i ]      = (( Integer )groups.get( i )).intValue();
            groupBysValue[ i ] = INITVALUE;
            log.fine( "GroupBy level=" + i + " col=" + groupBys[ i ] );
        }

        // Add additional row to force group change

        if( gSize > 0 ) {
            ArrayList newRow = new ArrayList();

            for( int c = 0;c < cols.size();c++ ) {
                newRow.add( "" );
            }

            m_rows.add( newRow );
        }

        // Function Values - Function - GroupValue

        int      fSize    = functions.size();
        int[]    funcCols = new int[ fSize ];
        String[] funcFuns = new String[ fSize ];
        int      index    = 0;
        Iterator it       = functions.keySet().iterator();

        while( it.hasNext()) {
            Object key = it.next();

            funcCols[ index ] = (( Integer )key ).intValue();
            funcFuns[ index ] = functions.get( key ).toString();
            log.fine( "Function " + funcFuns[ index ] + " col=" + funcCols[ index ] );
            index++;
        }

        BigDecimal[][] funcVals   = new BigDecimal[ fSize ][ gSize + 1 ];
        int            totalIndex = gSize;    // place for overall total

        log.fine( "FunctionValues = [ " + fSize + " * " + ( gSize + 1 ) + " ]" );

        for( int f = 0;f < fSize;f++ ) {
            for( int g = 0;g < gSize + 1;g++ ) {
                funcVals[ f ][ g ] = Env.ZERO;
            }
        }

        rows.clear();

        // Copy m_rows into rows

        for( int r = 0;r < m_rows.size();r++ ) {
            ArrayList row = ( ArrayList )m_rows.get( r );

            // do we have a group break

            boolean[] haveBreak = new boolean[ groupBys.length ];

            for( int level = 0;level < groupBys.length;level++ ) {
                int idx = groupBys[ level ];

                if( groupBysValue[ level ] == INITVALUE ) {
                    haveBreak[ level ] = false;
                } else if( !groupBysValue[ level ].equals( row.get( idx ))) {
                    haveBreak[ level ] = true;
                } else {
                    haveBreak[ level ] = false;
                }

                // previous level had a break

                if( (level > 0) && haveBreak[ level - 1 ] ) {
                    haveBreak[ level ] = true;
                }
            }

            // create group levels - reverse order

            for( int level = groupBys.length - 1;level >= 0;level-- ) {
                int idx = groupBys[ level ];

                if( groupBysValue[ level ] == INITVALUE ) {
                    groupBysValue[ level ] = row.get( idx );
                } else if( haveBreak[ level ] ) {

                    // log.fine( "GroupBy Change level=" + level + " col=" + idx + " - " + groupBysValue[level]);
                    // create new row

                    ArrayList newRow = new ArrayList();
                    log.fine("El tamanio de cols es ="+cols.size());
                    for( int c = 0;c < cols.size();c++ ) {
                        if( c == idx && c<=groupBysValue.length )    // the group column
                        {
                        	log.fine("El tamaño justo antes="+groupBysValue.length+", y c="+c);
                            if( (groupBysValue[ c ] == null) || (groupBysValue[ c ].toString().length() == 0) ) {
                                newRow.add( "=" );
                            } else {
                                newRow.add( groupBysValue[ c ] );
                            }
                        } else {
                            boolean found = false;

                            for( int fc = 0;fc < funcCols.length;fc++ ) {
                                if( c == funcCols[ fc ] ) {

                                    // newRow.add("fc= " + fc + " gl=" + level + " " + funcFuns[fc]);

                                    newRow.add( funcVals[ fc ][ level ] );
                                    funcVals[ fc ][ level ] = Env.ZERO;
                                    found                   = true;
                                }
                            }

                            if( !found ) {
                                newRow.add( null );
                            }
                        }
                    }    // for all columns

                    //

                    m_groupRows.add( new Integer( rows.size()));    // group row indicator
                    rows.add( newRow );
                    groupBysValue[ level ] = row.get( idx );
                }
            }    // for all groups

            // functions

            for( int fc = 0;fc < funcCols.length;fc++ ) {
                int col = funcCols[ fc ];

                // convert value to big decimal

                Object     value = row.get( col );
                BigDecimal bd    = Env.ZERO;

                if( value == null ) {
                    ;
                } else if( value instanceof BigDecimal ) {
                    bd = ( BigDecimal )value;
                } else {
                    try {
                        bd = new BigDecimal( value.toString());
                    } catch( Exception e ) {
                    }
                }

                for( int level = 0;level < gSize + 1;level++ ) {
                    if( funcFuns[ fc ].equals( RModel.FUNCTION_SUM )) {
                        funcVals[ fc ][ level ] = funcVals[ fc ][ level ].add( bd );
                    } else if( funcFuns[ fc ].equals( RModel.FUNCTION_COUNT )) {
                        funcVals[ fc ][ level ] = funcVals[ fc ][ level ].add( ONE );
                    }
                }    // for all group levels
            }        // for all functions

            rows.add( row );
        }            // for all m_rows

        // total row

        if( functions.size() > 0 ) {
            ArrayList newRow = new ArrayList();

            for( int c = 0;c < cols.size();c++ ) {
                boolean found = false;

                for( int fc = 0;fc < funcCols.length;fc++ ) {
                    if( c == funcCols[ fc ] ) {
                        newRow.add( funcVals[ fc ][ totalIndex ] );
                        found = true;
                    }
                }

                if( !found ) {
                    newRow.add( null );
                }
            }                                               // for all columns

            // remove empty row added earlier to force group change

            if( gSize > 0 ) {
                rows.remove( rows.size() - 1 );
            }

            m_groupRows.add( new Integer( rows.size()));    // group row indicator
            rows.add( newRow );
        }

        log.fine( "RModelData.process - End Rows=" + rows.size());
        m_rows.clear();
    }    // process

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    public boolean isGroupRow( int row ) {

        // build boolean Array

        if( m_groupRowsIndicator == null ) {
            m_groupRowsIndicator = new ArrayList( rows.size());

            for( int r = 0;r < rows.size();r++ ) {
                m_groupRowsIndicator.add( new Boolean( m_groupRows.contains( new Integer( r ))));
            }
        }

        if( (row < 0) || (row >= m_groupRowsIndicator.size())) {
            return false;
        }

        return(( Boolean )m_groupRowsIndicator.get( row )).booleanValue();
    }    // isGroupRow

    /**
     * Descripción de Método
     *
     *
     * @param from
     * @param to
     */

    public void moveRow( int from,int to ) {
        if( (from < 0) || (to >= rows.size())) {
            throw new IllegalArgumentException( "Row from invalid" );
        }

        if( (to < 0) || (to >= rows.size())) {
            throw new IllegalArgumentException( "Row to invalid" );
        }

        // Move Data

        Object temp = rows.get( from );

        rows.remove( from );
        rows.add( to,temp );

        // Move Description indicator >>> m_groupRows is not in sync after row move !!

        if( m_groupRowsIndicator != null ) {
            temp = m_groupRowsIndicator.get( from );
            m_groupRowsIndicator.remove( from );
            m_groupRowsIndicator.add( to,temp );
        }
    }    // moveRow
    
    
    public void reSort(HashMap m)
    {
    	// copiar los datos temporalmente a fin de limpiar rows
    	ArrayList temp = new ArrayList(rows);
    	rows.clear();
    	
    	// insertar ordenado en rows
    	int totalRows = m.size();
    	for( int index = 0;index < totalRows; index++ ) {
   			rows.add(temp.get((Integer)m.get(index)));    			
        }
    	
    	// ya no hay totales por grupo consistentes con el nuevo ordenamiento
    	m_groupRowsIndicator.clear();
    	for( int index = 0;index < totalRows; index++ ) 
        	m_groupRowsIndicator.add(false);
    	
    	// garbage collector
    	temp.clear();
    	temp = null;
    }
    
}    // RModelData



/*
 *  @(#)RModelData.java   02.07.07
 * 
 *  Fin del fichero RModelData.java
 *  
 *  Versión 2.2
 *
 */
