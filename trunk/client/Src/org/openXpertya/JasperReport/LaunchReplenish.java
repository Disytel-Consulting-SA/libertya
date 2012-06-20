package org.openXpertya.JasperReport;



import java.net.ConnectException;
import java.util.HashSet;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import org.openXpertya.JasperReport.DataSource.LaunchReplenishDataSource;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MMailText;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MUser;
import org.openXpertya.model.MUserMail;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.EMail;
import org.openXpertya.util.Env;

public class LaunchReplenish extends SvrProcess {
	
	/** Jasper Report			*/
	private int 		AD_JasperReport_ID;
	// Almacen
	private int M_Warehouse_ID;
	// Usuario	
	private int AD_User_ID;
	// Enviar informe?
	private boolean sendMail;
	
	/** Nombre del informe	*/
	private final String p_reportName = "Reposición automática de stock";
	
	/** Descripción de Campos */
    private MMailText m_MailText = null;
    
    /** Descripción de Campos */
    private MClient m_client = null;

   
    /** Descripción de Campos */
    private int m_R_MailText_ID = -1;
    

	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			log.fine("prepare - " + para[i]);
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("M_Warehouse_ID"))	{
				M_Warehouse_ID = para[i].getParameterAsInt();
			}
			else if (name.equals("AD_User_ID"))	{
				AD_User_ID = para[i].getParameterAsInt();
			}
			else if (name.equals("sendMail"))	{
				sendMail = ((String)para[i].getParameter()).equalsIgnoreCase("Y");
			}					
		}
		
		ProcessInfo base_pi=getProcessInfo();
		int AD_Process_ID=base_pi.getAD_Process_ID();
		MProcess proceso=MProcess.get(Env.getCtx(), AD_Process_ID);
		if(proceso.isJasperReport()!=true)
			return;
		AD_JasperReport_ID = proceso.getAD_JasperReport_ID();

	
	}

	@Override
	protected String doIt() throws Exception {
		
		m_client = MClient.get( getCtx());
		
       
        m_MailText = new MMailText( getCtx(),m_R_MailText_ID,get_TrxName());
        
        
		return createReport();

	}
	private String createReport() throws Exception	{
		MJasperReport jasperwrapper = new MJasperReport(getCtx(), AD_JasperReport_ID, get_TrxName());
		
		LaunchReplenishDataSource ds = new LaunchReplenishDataSource(M_Warehouse_ID,sendMail, AD_User_ID);
		// Establecemos parametros
		jasperwrapper.addParameter("TEMPDIR", System.getProperty("java.io.tmpdir"));
		HashSet mails = ds.getResponsables();
		
		
		try {
			jasperwrapper.fillReport(ds);
			if(!sendMail) {
				jasperwrapper.showReport(getProcessInfo());
			} else {
					
				this.sendMail(jasperwrapper.getPdfAttachment(), mails);
			}
				
			
		}
			
		catch (RuntimeException e)	{
			throw new RuntimeException ("No se ha podido rellenar el informe." + e.getMessage(), e);
		}
		catch (MessagingException e)	{
				throw new RuntimeException ("No se ha podido realizar el informe:" + e.getMessage(), e);
		}
		catch (ConnectException e)	{
			throw new RuntimeException ("No se ha podido enviar el informe:" +  e.getMessage(), e);
		}
		
		return "doIt";

		
	}

	private void sendMail(byte[] pdf, HashSet mails) throws MessagingException, ConnectException {
		
		// Armo el archivo
		m_MailText.setName("Informe de reposición");
		m_MailText.setMailHeader("Informe de reposiciones en stock - " +Env.getContextAsDate(getCtx(), "Date"));
		m_MailText.setMailText("Adjunto les enviamos informe de reposición de stock de artículo. "+ Env.getContextAsDate(getCtx(), "Date")+".");
	    /** Descripción de Campos */
	    MUser m_from = new MUser(getCtx(),Env.getAD_User_ID(getCtx()), get_TrxName());
		
	    for (int i = 0; i< mails.size(); i++) {
						
			MUser  to      = new MUser( getCtx(),(Integer)mails.toArray()[i],get_TrxName());	
			if (to.getEMail() != null && to.getEMail().trim().length() > 0  ) {
				String subject = m_MailText.getMailHeader();
				String message = m_MailText.getMailText( true );
				
				m_MailText.save();
                    	
				EMail mail = new EMail( m_client,m_from,to,subject,message );
				// 	adjunto el archivo            
				mail.addAttachment(pdf,"application/pdf","InformeReposicion"+Env.getContextAsDate(getCtx(),"Date")+".pdf" );
				if( !mail.isValid() &&!mail.isValid( true )) {
					log.warning( "NOT VALID - " + mail );
					to.setIsActive( false );
					to.addDescription( "Invalid EMail" );
					to.save();
					
					return;
				}
				// 	envia el mail
				if (!EMail.SENT_OK.equals( mail.send()))
				{
					log.saveError("LaunchReplenish - sendMail","No se pudo enviar el mail");
					throw new ConnectException("No se pudo enviar el mail");
				}
				
				MUserMail um = new MUserMail( m_MailText,AD_User_ID,mail );

				um.save();
			} else {
				log.saveError("LaunchReplenish - sendMail","No tiene dirección de mail para enviar");
				throw new MessagingException("El responsable no posee direccion de mail");
			}
		}
 
	} // sendMail
}
