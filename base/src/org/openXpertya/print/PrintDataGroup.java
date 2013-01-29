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



package org.openXpertya.print;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PrintDataGroup {

    /**
     * Constructor de la clase ...
     *
     */

    public PrintDataGroup() {}    // PrintDataGroup

    /** Descripción de Campos */

    static public final String DELIMITER = "~";

    /** Descripción de Campos */

    static public final String TOTAL = "=TOTAL=";

    /** Descripción de Campos */

    static private final Object NULL = new String();

    /** Descripción de Campos */

    private ArrayList m_groups = new ArrayList();

    /** Descripción de Campos */

    private HashMap m_groupMap = new HashMap();

    /** Descripción de Campos */

    private ArrayList m_functions = new ArrayList();

    /** Descripción de Campos */

    private HashMap m_groupFunction = new HashMap();

    /**
     * Descripción de Método
     *
     *
     * @param groupColumnName
     */

    public void addGroupColumn( String groupColumnName ) {
        m_groups.add( groupColumnName );
    }    // addGroup

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getGroupColumnCount() {
        return m_groups.size();
    }    // getGroupColumnCount

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     *
     * @return
     */

    public boolean isGroupColumn( String columnName ) {
        if( (columnName == null) || (m_groups.size() == 0) ) {
            return false;
        }

        for( int i = 0;i < m_groups.size();i++ ) {
            if( columnName.equals( m_groups.get( i ))) {
                return true;
            }
        }

        return false;
    }    // isGroupColumn

    /**
     * Descripción de Método
     *
     *
     * @param groupColumnName
     * @param value
     *
     * @return
     */

    public Object groupChange( String groupColumnName,Object value ) {
        if( !isGroupColumn( groupColumnName )) {
            return null;
        }

        Object newValue = value;

        if( newValue == null ) {
            newValue = NULL;
        }

        //

        if( m_groupMap.containsKey( groupColumnName )) {
            Object oldValue = m_groupMap.get( groupColumnName );

            if( newValue.equals( oldValue )) {
                return null;
            }

            m_groupMap.put( groupColumnName,newValue );

            return oldValue;
        }

        m_groupMap.put( groupColumnName,newValue );

        return null;
    }    // groupChange

    /**
     * Descripción de Método
     *
     *
     * @param functionColumnName
     * @param function
     */

    public void addFunction( String functionColumnName,char function ) {
        m_functions.add( functionColumnName + DELIMITER + function );

        if( !m_groups.contains( TOTAL )) {
            m_groups.add( TOTAL );
        }
    }    // addFunction

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     *
     * @return
     */

    public boolean isFunctionColumn( String columnName ) {
        if( (columnName == null) || (m_functions.size() == 0) ) {
            return false;
        }

        for( int i = 0;i < m_functions.size();i++ ) {
            String f = ( String )m_functions.get( i );

            if( f.startsWith( columnName )) {
                return true;
            }
        }

        return false;
    }    // isFunctionColumn

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     *
     * @return
     */

    public char[] getFunctions( String columnName ) {
        ArrayList list = new ArrayList();    // the final function List
        Iterator  it   = m_groupFunction.keySet().iterator();

        while( it.hasNext()) {
            String group_function = ( String )it.next();                                                     // =TOTAL=~LoadSeq

            if( group_function.startsWith( columnName )) {
                group_function = group_function.substring( group_function.lastIndexOf( DELIMITER ) + 1 );    // LoadSeq

                for( int i = 0;i < m_functions.size();i++ ) {
                    String col_function = (( String )m_functions.get( i ));    // LoadSeq~A

                    if( col_function.startsWith( group_function )) {
                        String function = col_function.substring( col_function.lastIndexOf( DELIMITER ) + 1 );

                        if( !list.contains( function )) {
                            list.add( function );
                        }
                    }
                }
            }
        }

        // Return Value

        char[] retValue = new char[ list.size()];

        for( int i = 0;i < retValue.length;i++ ) {
            retValue[ i ] = (( String )list.get( i )).charAt( 0 );
        }

        // log.finest( "PrintDataGroup.getFunctions for " + columnName + "/" + retValue.length, new String(retValue));

        return retValue;
    }    // getFunctions

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     * @param function
     *
     * @return
     */

    public boolean isFunctionColumn( String columnName,char function ) {
        if( (columnName == null) || (m_functions.size() == 0) ) {
            return false;
        }

        String key = columnName + DELIMITER + function;

        for( int i = 0;i < m_functions.size();i++ ) {
            String f = ( String )m_functions.get( i );

            if( f.equals( key )) {
                return true;
            }
        }

        return false;
    }    // isFunctionColumn

    /**
     * Descripción de Método
     *
     *
     * @param functionColumnName
     * @param functionValue
     */

    public void addValue( String functionColumnName,BigDecimal functionValue ) {
        if( !isFunctionColumn( functionColumnName )) {
            return;
        }

        // Group Breaks

        for( int i = 0;i < m_groups.size();i++ ) {
            String            groupColumnName = ( String )m_groups.get( i );
            String            key             = groupColumnName + DELIMITER + functionColumnName;
            PrintDataFunction pdf             = ( PrintDataFunction )m_groupFunction.get( key );

            if( pdf == null ) {
                pdf = new PrintDataFunction();
            }

            pdf.addValue( functionValue );
            m_groupFunction.put( key,pdf );
        }
    }    // addValue

    /**
     * Descripción de Método
     *
     *
     * @param groupColumnName
     * @param functionColumnName
     * @param function
     *
     * @return
     */

    public BigDecimal getValue( String groupColumnName,String functionColumnName,char function ) {
        String            key = groupColumnName + DELIMITER + functionColumnName;
        PrintDataFunction pdf = ( PrintDataFunction )m_groupFunction.get( key );

        if( pdf == null ) {
            return null;
        }

        return pdf.getValue( function );
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @param groupColumnName
     * @param functionColumnName
     */

    public void reset( String groupColumnName,String functionColumnName ) {
        String            key = groupColumnName + DELIMITER + functionColumnName;
        PrintDataFunction pdf = ( PrintDataFunction )m_groupFunction.get( key );

        if( pdf != null ) {
            pdf.reset();
        }
    }    // reset

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return toString( false );
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param withData
     *
     * @return
     */

    public String toString( boolean withData ) {
        StringBuffer sb = new StringBuffer( "PrintDataGroup[" );

        sb.append( "Groups=" );

        for( int i = 0;i < m_groups.size();i++ ) {
            if( i != 0 ) {
                sb.append( "," );
            }

            sb.append( m_groups.get( i ));
        }

        if( withData ) {
            Iterator it = m_groupMap.keySet().iterator();

            while( it.hasNext()) {
                Object key   = it.next();
                Object value = m_groupMap.get( key );

                sb.append( ":" ).append( key ).append( "=" ).append( value );
            }
        }

        sb.append( ";Functions=" );

        for( int i = 0;i < m_functions.size();i++ ) {
            if( i != 0 ) {
                sb.append( "," );
            }

            sb.append( m_functions.get( i ));
        }

        if( withData ) {
            Iterator it = m_groupFunction.keySet().iterator();

            while( it.hasNext()) {
                Object key   = it.next();
                Object value = m_groupFunction.get( key );

                sb.append( ":" ).append( key ).append( "=" ).append( value );
            }
        }

        sb.append( "]" );

        return sb.toString();
    }    // toString
}    // PrintDataGroup



/*
 *  @(#)PrintDataGroup.java   23.03.06
 * 
 *  Fin del fichero PrintDataGroup.java
 *  
 *  Versión 2.2
 *
 */
