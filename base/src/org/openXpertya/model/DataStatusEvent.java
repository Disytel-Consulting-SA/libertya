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



package org.openXpertya.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.EventObject;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class DataStatusEvent extends EventObject implements Serializable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param source
     * @param totalRows
     * @param changed
     * @param autoSave
     * @param inserting
     */

    public DataStatusEvent( Object source,int totalRows,boolean changed,boolean autoSave,boolean inserting ) {
        super( source );
        m_totalRows = totalRows;
        m_changed   = changed;
        m_autoSave  = autoSave;
        m_inserting = inserting;
    }    // DataStatusEvent

    /** Descripción de Campos */

    private int m_totalRows;

    /** Descripción de Campos */

    private boolean m_changed;

    /** Descripción de Campos */

    private boolean m_autoSave;

    /** Descripción de Campos */

    private boolean m_inserting;

    //

    /** Descripción de Campos */

    private String m_AD_Message = null;

    /** Descripción de Campos */

    private String m_info = null;

    /** Descripción de Campos */

    private boolean m_isError = false;

    /** Descripción de Campos */

    private boolean m_confirmed = false;

    //

    /** Descripción de Campos */

    private boolean m_allLoaded = true;

    /** Descripción de Campos */

    private int m_loadedRows = -1;

    /** Descripción de Campos */

    private int m_currentRow = -1;

    //

    /** Descripción de Campos */

    private int m_changedColumn = 0;

    //

    /** Descripción de Campos */

    public Timestamp Created = null;

    /** Descripción de Campos */

    public Integer CreatedBy = null;

    /** Descripción de Campos */

    public Timestamp Updated = null;

    /** Descripción de Campos */

    public Integer UpdatedBy = null;

    //

    /** Descripción de Campos */

    public String Info = null;

    /** Descripción de Campos */

    public int AD_Table_ID = 0;

    /** Descripción de Campos */

    public Object Record_ID = null;

    /**
     * Descripción de Método
     *
     *
     * @param loadedRows
     */

    public void setLoading( int loadedRows ) {
        m_allLoaded  = false;
        m_loadedRows = loadedRows;
    }    // setLoaded

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLoading() {
        return !m_allLoaded;
    }    // isLoading

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getLoadedRows() {
        return m_loadedRows;
    }    // getLoadedRows

    /**
     * Descripción de Método
     *
     *
     * @param currentRow
     */

    public void setCurrentRow( int currentRow ) {
        m_currentRow = currentRow;
    }    // setCurrentRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getCurrentRow() {
        return m_currentRow;
    }    // getCurrentRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getTotalRows() {
        return m_totalRows;
    }    // getTotalRows

    /**
     * Descripción de Método
     *
     *
     * @param info
     * @param isError
     */

    public void setInfo( String info,boolean isError ) {
        m_info    = info;
        m_isError = isError;
    }    // setInfo

    /**
     * Descripción de Método
     *
     *
     * @param AD_Message
     * @param info
     * @param isError
     */

    public void setInfo( String AD_Message,String info,boolean isError ) {
        m_AD_Message = AD_Message;
        m_info       = info;
        m_isError    = isError;
    }    // setInfo

    /**
     * Descripción de Método
     *
     *
     * @param inserting
     */

    public void setInserting( boolean inserting ) {
        m_inserting = inserting;
    }    // setInserting

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isInserting() {
        return m_inserting;
    }    // isInserting

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getAD_Message() {
        return m_AD_Message;
    }    // getAD_Message

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getInfo() {
        return m_info;
    }    // getInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isError() {
        return m_isError;
    }    // isError

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "DataStatusEvent - " );

        if( m_AD_Message != null ) {
            sb.append( m_AD_Message );
        }

        if( m_info != null ) {
            sb.append( " " ).append( m_info );
        }

        sb.append( " : " ).append( getMessage());

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getMessage() {
        StringBuffer retValue = new StringBuffer();

        if( m_inserting ) {
            retValue.append( "+" );
        }

        retValue.append( m_changed
                         ?( m_autoSave
                            ?"*"
                            :"?" )
                         :" " );

        // current row

        if( m_totalRows == 0 ) {
            retValue.append( m_currentRow );
        } else {
            retValue.append( m_currentRow + 1 );
        }

        // of

        retValue.append( "/" );

        if( m_allLoaded ) {
            retValue.append( m_totalRows );
        } else {
            retValue.append( m_loadedRows ).append( "->" ).append( m_totalRows );
        }

        //

        return retValue.toString();
    }    // getMessage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isChanged() {
        return m_changed;
    }    // isChanged

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isFirstRow() {
        if( m_totalRows == 0 ) {
            return true;
        }

        return m_currentRow == 0;
    }    // isFirstRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLastRow() {
        if( m_totalRows == 0 ) {
            return true;
        }

        return m_currentRow == m_totalRows - 1;
    }    // isLastRow

    /**
     * Descripción de Método
     *
     *
     * @param col
     */

    public void setChangedColumn( int col ) {
        m_changedColumn = col;
    }    // setChangedColumn

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getChangedColumn() {
        return m_changedColumn;
    }    // getChangedColumn

    /**
     * Descripción de Método
     *
     *
     * @param confirmed
     */

    public void setConfirmed( boolean confirmed ) {
        m_confirmed = confirmed;
    }    // setConfirmed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isConfirmed() {
        return m_confirmed;
    }    // isConfirmed
}    // DataStatusEvent



/*
 *  @(#)DataStatusEvent.java   02.07.07
 * 
 *  Fin del fichero DataStatusEvent.java
 *  
 *  Versión 2.2
 *
 */
