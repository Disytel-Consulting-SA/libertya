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

package org.openXpertya.session;

import java.rmi.RemoteException;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.openXpertya.OpenXpertya;
import org.openXpertya.db.CConnection;
import org.openXpertya.util.CLogger;


/**
 * 	OpenXpertya Status Bean
 *
 *  @ejb:bean name="openXpertya/Status"
 *           display-name="OpenXpertya Status Session Bean"
 *           type="Stateless"
 *           transaction-type="Bean"
 *           jndi-name="ejb/openXpertya/Status"
 *
 *  @ejb:ejb-ref ejb-name="openXpertya/Status"
 *              ref-name="openXpertya/Status"
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya 
 * 
 */
public class StatusBean implements SessionBean
{
	/**	Context				*/
	private SessionContext 	m_Context;
	/**	Logging				*/
	private static CLogger	log = CLogger.getCLogger(StatusBean.class);

	private static int		s_no = 0;
	private int				m_no = 0;
	//
	private int				m_versionCount = 0;
	private int				m_databaseCount = 0;


	/**
	 * 	Get Version (Date)
	 *  @ejb:interface-method view-type="remote"
	 *  @return version e.g. 2002-09-02
	 *  @throws RemoteException
	 */
    
	public String getDateVersion() throws RemoteException
	{
		m_versionCount++;
		log.info ("getDateVersion " + m_versionCount);
		return OpenXpertya.DATE_VERSION;
	}	//	getDateVersion

	/**
	 * 	Get Main Version
	 *  @ejb:interface-method view-type="remote"
	 *  @return main version - e.g. Version 2.4.3b
	 *  @throws RemoteException
	 */
    
	public String getMainVersion() throws RemoteException
	{
		return OpenXpertya.MAIN_VERSION;
	}	//	getMainVersion

	/**
	 *  Get Database Type
	 *  @ejb:interface-method view-type="remote"
	 *  @return Database Type
	 *  @throws RemoteException
	 */
    
	public String getDbType() throws RemoteException
	{
		return CConnection.get().getType();
	}   //  getDbType

	/**
	 *  Get Database Host
	 *  @ejb:interface-method view-type="remote"
	 *  @return Database Host Name
	 *  @throws RemoteException
	 */
    
	public String getDbHost() throws RemoteException
	{
		m_databaseCount++;
		log.info ("getDbHost " + m_databaseCount);
		return CConnection.get().getDbHost();
	}   //  getDbHost

	/**
	 *  Get Database Port
	 *  @ejb:interface-method view-type="remote"
	 *  @return Database Posrt
	 *  @throws RemoteException
	 */
    
	public int getDbPort() throws RemoteException
	{
		return CConnection.get().getDbPort();
	}   //  getDbPort

	/**
	 *  Get Database SID
	 *  @ejb:interface-method view-type="remote"
	 *  @return Database SID
	 *  @throws RemoteException
	 */
    
	public String getDbName() throws RemoteException
	{
		return CConnection.get().getDbName();
	}   //  getDbSID

	/**
	 *  Get Database URL
	 *  @ejb:interface-method view-type="remote"
	 *  @return Database URL
	 *  @throws RemoteException
	 */
    
	public String getConnectionURL() throws RemoteException
	{
		return CConnection.get().getConnectionURL();
	}   //  getConnectionURL
	
	/**
	 *  Get Database UID
	 *  @ejb:interface-method view-type="remote"
	 *  @return Database User Name
	 *  @throws RemoteException
	 */
	public String getDbUid() throws RemoteException
	{
		return CConnection.get().getDbUid();
	}   //  getDbUID

	/**
	 *  Get Database PWD
	 *  @ejb:interface-method view-type="remote"
	 *  @return Database User Password
	 *  @throws RemoteException
	 */
	public String getDbPwd() throws RemoteException
	{
		return CConnection.get().getDbPwd();
	}   //  getDbPWD

	/**
	 *  Get Connection Manager Host
	 *  @ejb:interface-method view-type="remote"
	 *  @return Connection Manager Host
	 *  @throws RemoteException
	 */
	public String getFwHost() throws RemoteException
	{
		return CConnection.get().getFwHost();
	}   //  getCMHost

	/**
	 *  Get Connection Manager Port
	 *  @ejb:interface-method view-type="remote"
	 *  @return Connection Manager Port
	 *  @throws RemoteException
	 */
	public int getFwPort() throws RemoteException
	{
		return CConnection.get().getFwPort();
	}   //  getCMPort

	
	/**************************************************************************
	 * 	Get Version Count
	 *  @ejb:interface-method view-type="remote"
	 * 	@return number of version inquiries
	 */
	public int getVersionCount()
	{
		return m_versionCount;
	}	//	getVersionCount

	/**
	 * 	Get Database Count
	 *  @ejb:interface-method view-type="remote"
	 * 	@return number of database inquiries
	 */
	public int getDatabaseCount()
	{
		return m_databaseCount;
	}	//	getVersionCount

	/**
	 * 	Describes the instance and its content for debugging purpose
	 *  @ejb:interface-method view-type="remote"
	 * 	@return Debugging information about the instance and its content
	 */
    
	public String getStatus()
	{
		StringBuffer sb = new StringBuffer("StatusBean[No=");
		sb.append(m_no)
			.append(",VersionCount=").append(m_versionCount)
			.append(",DatabaseCount=").append(m_versionCount)
			.append("]");
		return sb.toString();
	}	//	getStatus


	/**
	 * 	String Representation
	 * 	@return info
	 */
	public String toString()
	{
		return getStatus();
	}	//	toString

	
	/**************************************************************************
	 * 	Create the Session Bean
	 * 	@throws CreateException
	 *  @ejb:create-method view-type="remote"
	 */
    
	public void ejbCreate() throws CreateException
	{
		m_no = ++s_no;
		log.info("#" + m_no);
		try
		{
			org.openXpertya.OpenXpertya.startup(false);
		}
		catch (Exception ex)
		{
			log.log(Level.SEVERE, "ejbCreate", ex);
		//	throw new CreateException ();
		}
	}	//	ejbCreate


	// -------------------------------------------------------------------------
	// Llamadas al entorno de trabajo
	// -------------------------------------------------------------------------

	public void setSessionContext (SessionContext aContext) throws EJBException
	{
		m_Context = aContext;
	}

	public void ejbActivate() throws EJBException
	{
		if (log == null)
			log = CLogger.getCLogger(getClass());
		log.fine("ejbActivate");
	}

	public void ejbPassivate() throws EJBException
	{
		log.fine("ejbPassivate");
	}

	public void ejbRemove() throws EJBException
	{
		log.fine("ejbRemove");
	}

}	
/*
 *  @(#)StatusBean.java   30.03.06
 * 
 *  Fin del fichero StatusBean.java
 *  
 *  Versión 2.2
 *
 */


