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



package org.openXpertya.process;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProcessInfo implements Serializable {

	/** codigo compatibilidad Adempiere Jasper 

     * Get transaction name for this process
     * @return String
     */
    public String getTransactionName()
    {
        return m_transactionName;
    }

    /**
     * Set transaction name from this process
     * @param trxName
     */
    public void setTransactionName(String trxName)
    {
        m_transactionName = trxName;
    }
    
/** codigo compatibilidad Adempiere Jasper **/


	
    /**
     * Constructor de la clase ...
     *
     *
     * @param Title
     * @param AD_Process_ID
     * @param Table_ID
     * @param Record_ID
     */

    public ProcessInfo( String Title,int AD_Process_ID,int Table_ID,int Record_ID ) {
        setTitle( Title );
        setAD_Process_ID( AD_Process_ID );
        setTable_ID( Table_ID );
        setRecord_ID( Record_ID );
    }    // ProcessInfo

    /**
     * Constructor de la clase ...
     *
     *
     * @param Title
     * @param AD_Process_ID
     */

    public ProcessInfo( String Title,int AD_Process_ID ) {
        this( Title,AD_Process_ID,0,0 );
    }    // ProcessInfo

    /** Descripción de Campos */

    static final long serialVersionUID = -1993220053515488725L;

    
    // Añadido por Dataware S.L.
    // Se añade propiedad m_WindowNo
    /** Descripción de Campos */
    
    private int m_WindowNo;
    
    /** Descripción de Campos */

    private String m_Title;

    /** Descripción de Campos */

    private int m_AD_Process_ID;

    /** Descripción de Campos */

    private int m_Table_ID;

    /** Descripción de Campos */

    private int m_Record_ID;

    /** Descripción de Campos */

    private Integer m_AD_User_ID;

    /** Descripción de Campos */

    private Integer m_AD_Client_ID;

    /** Descripción de Campos */

    private String m_ClassName = null;

    // -- Optional --

    /** Descripción de Campos */

    private int m_AD_PInstance_ID = 0;

    /** Descripción de Campos */

    private String m_Summary = "";

    /** Descripción de Campos */

    private boolean m_Error = false;

    /* General Data Object */

    /** Descripción de Campos */

    private Serializable m_SerializableObject = null;

    /* General Data Object */

    /** Descripción de Campos */

    private transient Object m_TransientObject = null;

    /** Descripción de Campos */

    private int m_EstSeconds = 5;

    /** Descripción de Campos */

    private ArrayList m_logs = null;

    /** Descripción de Campos */

    private ProcessInfoParameter[] m_parameter = null;
    
    private DocActionStatusListener m_docActionStatusListener;
    
/** Compatibilidad Jasper Adempiere Batch                       */
    /** Transaction Name            */
    private String              m_transactionName = null;
/** Compatibilidad Jasper Adempiere Batch                       */

    private JasperReportDTO jasperReportDTO = null;
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "ProcessInfo[" );

        sb.append( m_Title ).append( ",Process_ID=" ).append( m_AD_Process_ID );

        if( m_AD_PInstance_ID != 0 ) {
            sb.append( ",AD_PInstance_ID=" ).append( m_AD_PInstance_ID );
        }

        if( m_Record_ID != 0 ) {
            sb.append( ",Record_ID=" ).append( m_Record_ID );
        }

        if( m_ClassName != null ) {
            sb.append( ",ClassName=" ).append( m_ClassName );
        }

        sb.append( ",Error=" ).append( isError());

        if( m_TransientObject != null ) {
            sb.append( ",Transient=" ).append( m_TransientObject );
        }

        if( m_SerializableObject != null ) {
            sb.append( ",Serializable=" ).append( m_SerializableObject );
        }

        sb.append( ",Summary=" ).append( getSummary()).append( ",Log=" ).append( (m_logs == null)
                ?0
                :m_logs.size());

        // .append(getLogInfo(false));

        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param summary
     */

    public void setSummary( String summary ) {
        m_Summary = summary;
    }    // setSummary

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSummary() {
        return m_Summary;
    }    // getSummary

    
    // Inicio Dataware S.L.
    // Anadimos los metodos get y set de la propiedad WindowNo
    /**
     * Descripción de Método
     *
     *
     * @param window
     */

    public void setWindowNo( int window ) {
    	m_WindowNo = window;
    }    // setWindowNo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getWindowNo() {
        return m_WindowNo;
    }    // getWindowNo
    // Fin Dataware S.L.  
   
    /**
     * Descripción de Método
     *
     *
     * @param translatedSummary
     * @param error
     */

    public void setSummary( String translatedSummary,boolean error ) {
        setSummary( translatedSummary );
        setError( error );
    }    // setSummary

    /**
     * Descripción de Método
     *
     *
     * @param additionalSummary
     */

    public void addSummary( String additionalSummary ) {
        m_Summary += additionalSummary;
    }    // addSummary

    /**
     * Descripción de Método
     *
     *
     * @param error
     */

    public void setError( boolean error ) {
        m_Error = error;
    }    // setError

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isError() {
        return m_Error;
    }    // isError

    /**
     * Descripción de Método
     *
     *
     * @param html
     *
     * @return
     */

    public String getLogInfo( boolean html ) {
        if( m_logs == null ) {
            return "";
        }

        //

        StringBuffer     sb         = new StringBuffer();
        SimpleDateFormat dateFormat = DisplayType.getDateFormat( DisplayType.DateTime );

        if( html ) {
            sb.append( "<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"2\">" );
        }

        //

        for( int i = 0;i < m_logs.size();i++ ) {
            if( html ) {
                sb.append( "<tr>" );
            } else if( i > 0 ) {
                sb.append( "\n" );
            }

            //

            ProcessInfoLog log = ( ProcessInfoLog )m_logs.get( i );

            //

            if( log.getP_Date() != null ) {
                sb.append( html
                           ?"<td>"
                           :"" ).append( dateFormat.format( log.getP_Date())).append( html
                        ?"</td>"
                        :" \t" );
            }

            //

            if( log.getP_Number() != null ) {
                sb.append( html
                           ?"<td>"
                           :"" ).append( log.getP_Number()).append( html
                        ?"</td>"
                        :" \t" );
            }

            //

            if( log.getP_Msg() != null ) {
                sb.append( html
                           ?"<td>"
                           :"" ).append( Msg.parseTranslation( Env.getCtx(),log.getP_Msg())).append( html
                        ?"</td>"
                        :"" );
            }

            //

            if( html ) {
                sb.append( "</tr>" );
            }
        }

        if( html ) {
            sb.append( "</table>" );
        }

        return sb.toString();
    }    // getLogInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getLogInfo() {
        return getLogInfo( false );
    }    // getLogInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_PInstance_ID() {
        return m_AD_PInstance_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param AD_PInstance_ID
     */

    public void setAD_PInstance_ID( int AD_PInstance_ID ) {
        m_AD_PInstance_ID = AD_PInstance_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Process_ID() {
        return m_AD_Process_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param AD_Process_ID
     */

    public void setAD_Process_ID( int AD_Process_ID ) {
        m_AD_Process_ID = AD_Process_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getClassName() {
        return m_ClassName;
    }

    /**
     * Descripción de Método
     *
     *
     * @param ClassName
     */

    public void setClassName( String ClassName ) {
        m_ClassName = ClassName;

        if( (m_ClassName != null) && (m_ClassName.length() == 0) ) {
            m_ClassName = null;
        }
    }    // setClassName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getTransientObject() {
        return m_TransientObject;
    }

    /**
     * Descripción de Método
     *
     *
     * @param TransientObject
     */

    public void setTransientObject( Object TransientObject ) {
        m_TransientObject = TransientObject;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Serializable getSerializableObject() {
        return m_SerializableObject;
    }

    /**
     * Descripción de Método
     *
     *
     * @param SerializableObject
     */

    public void setSerializableObject( Serializable SerializableObject ) {
        m_SerializableObject = SerializableObject;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getEstSeconds() {
        return m_EstSeconds;
    }

    /**
     * Descripción de Método
     *
     *
     * @param EstSeconds
     */

    public void setEstSeconds( int EstSeconds ) {
        m_EstSeconds = EstSeconds;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getTable_ID() {
        return m_Table_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     */

    public void setTable_ID( int AD_Table_ID ) {
        m_Table_ID = AD_Table_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRecord_ID() {
        return m_Record_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param Record_ID
     */

    public void setRecord_ID( int Record_ID ) {
        m_Record_ID = Record_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTitle() {
        return m_Title;
    }

    /**
     * Descripción de Método
     *
     *
     * @param Title
     */

    public void setTitle( String Title ) {
        m_Title = Title;
    }    // setTitle

    /**
     * Descripción de Método
     *
     *
     * @param AD_Client_ID
     */

    public void setAD_Client_ID( int AD_Client_ID ) {
        m_AD_Client_ID = new Integer( AD_Client_ID );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Integer getAD_Client_ID() {
        return m_AD_Client_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     */

    public void setAD_User_ID( int AD_User_ID ) {
        m_AD_User_ID = new Integer( AD_User_ID );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Integer getAD_User_ID() {
        return m_AD_User_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ProcessInfoParameter[] getParameter() {
        return m_parameter;
    }    // getParameter

    /**
     * Descripción de Método
     *
     *
     * @param parameter
     */

    public void setParameter( ProcessInfoParameter[] parameter ) {
        m_parameter = parameter;
    }    // setParameter

    /**
     * Descripción de Método
     *
     *
     * @param Log_ID
     * @param P_ID
     * @param P_Date
     * @param P_Number
     * @param P_Msg
     */

    public void addLog( int Log_ID,int P_ID,Timestamp P_Date,BigDecimal P_Number,String P_Msg ) {
        addLog( new ProcessInfoLog( Log_ID,P_ID,P_Date,P_Number,P_Msg ));
    }    // addLog

    /**
     * Descripción de Método
     *
     *
     * @param P_ID
     * @param P_Date
     * @param P_Number
     * @param P_Msg
     */

    public void addLog( int P_ID,Timestamp P_Date,BigDecimal P_Number,String P_Msg ) {
        addLog( new ProcessInfoLog( P_ID,P_Date,P_Number,P_Msg ));
    }    // addLog

    /**
     * Descripción de Método
     *
     *
     * @param logEntry
     */

    public void addLog( ProcessInfoLog logEntry ) {
        if( logEntry == null ) {
            return;
        }

        if( m_logs == null ) {
            m_logs = new ArrayList();
        }

        m_logs.add( logEntry );
    }    // addLog

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ProcessInfoLog[] getLogs() {
        if( m_logs == null ) {
            return null;
        }

        ProcessInfoLog[] logs = new ProcessInfoLog[ m_logs.size()];

        m_logs.toArray( logs );

        return logs;
    }    // getLogs

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int[] getIDs() {
        if( m_logs == null ) {
            return null;
        }

        int[] ids = new int[ m_logs.size()];

        for( int i = 0;i < m_logs.size();i++ ) {
            ids[ i ] = (( ProcessInfoLog )m_logs.get( i )).getP_ID();
        }

        return ids;
    }    // getIDs

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected ArrayList getLogList() {
        return m_logs;
    }

    /**
     * Descripción de Método
     *
     *
     * @param logs
     */

    protected void setLogList( ArrayList logs ) {
        m_logs = logs;
    }
    
    /**********************************************************************************************
     * 		INDEOS: modificacion para permitir que un proceso siempre se ejecute en cliente
     **********************************************************************************************/
    
    /**
     * Indica si un proceso se debe ejecutar siempre en cliente
     * 
     * @return true si el proceso se debe ejecutar en el cliente, false en caso contrario
     */
    public boolean isAlwaysInClient()
	{
		if(m_AD_Process_ID==0)
			return false;
		
		org.openXpertya.model.MProcess proceso=org.openXpertya.model.MProcess.get(Env.getCtx(), m_AD_Process_ID);
		if(proceso==null || proceso.getAD_Process_ID()==0)
			return false;
		
		return proceso.isAlwaysInClient();
	}	// isAlwaysInClient

	/**
	 * @return Returns the docActionStatusListener.
	 */
	public DocActionStatusListener getDocActionStatusListener() {
		return m_docActionStatusListener;
	}

	/**
	 * @param docActionStatusListener The docActionStatusListener to set.
	 */
	public void setDocActionStatusListener(
			DocActionStatusListener docActionStatusListener) {
		m_docActionStatusListener = docActionStatusListener;
	}

	public void setJasperReportDTO(JasperReportDTO jasperReportDTO) {
		this.jasperReportDTO = jasperReportDTO;
	}

	public JasperReportDTO getJasperReportDTO() {
		return jasperReportDTO;
	}

	/**
	 * Clase que se utiliza para transportar datos necesarios a la impresión de
	 * jasper
	 * 
	 */
	public class JasperReportDTO {
		/** ID del tipo de documento */
	    private Integer docTypeID = null;
	    /** Nro de documento */
	    private String documentNo = null;
	    /** Nro de Copias */
	    private Integer numCopies = null;
	    
	    public JasperReportDTO(){}
	    
	    public JasperReportDTO(Integer docTypeID, String documentNo, Integer numCopies, String printerName){
	    	setDocTypeID(docTypeID);
	    	setDocumentNo(documentNo);
	    	setNumCopies(numCopies);
	    }
	    
	    // Getters y Setters
	    
		public void setDocTypeID(Integer docTypeID) {
			this.docTypeID = docTypeID;
		}

		public Integer getDocTypeID() {
			return docTypeID;
		}

		public void setDocumentNo(String documentNo) {
			this.documentNo = documentNo;
		}

		public String getDocumentNo() {
			return documentNo;
		}

		public void setNumCopies(Integer numCopies) {
			this.numCopies = numCopies;
		}

		public Integer getNumCopies() {
			return numCopies;
		}
	}

	public boolean isReportingProcess() 
	{
		return m_reportingProcess;
	}
	private boolean				m_reportingProcess = false;

	/**
	 *	Timeout
	 * 	@param timeout true still running
	 */
	public void setIsTimeout (boolean timeout)
	{
		m_timeout = timeout;
	}	//	setTimeout
	/** Process timed out				*/
	private boolean				m_timeout = false;
	/**
	 *	Timeout - i.e process did not complete
	 *	@return boolean
	 */
	public boolean isTimeout()
	{
		return m_timeout;
	}	//	isTimeout
	
	/**
	 * Set print preview flag, only relevant if this is a reporting process
	 * @param b
	 */
	public void setPrintPreview(boolean b)
	{
		m_printPreview = b;
	}
	
	private boolean				m_printPreview = false;

}    // ProcessInfo



/*
 *  @(#)ProcessInfo.java   25.03.06
 * 
 *  Fin del fichero ProcessInfo.java
 *  
 *  Versión 2.2
 *
 */
