package org.openXpertya.install;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.apache.tools.ant.Main;
import org.openXpertya.saas.Constants;
import org.openXpertya.saas.SaaSUtils;
import org.openXpertya.util.CLogFile;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Ini;

public class SilentSetup {

	public SilentSetup()
	{

		String oxpHome = System.getProperty(ConfigurationData.OXP_HOME);
		if (oxpHome == null || oxpHome.length() == 0)
			oxpHome = System.getProperty("user.dir");
		
		boolean envLoaded = false;
		String fileName = oxpHome + File.separator + ConfigurationData.ARCHIVO_VAR_OXP;
		File env = new File(fileName);
		if (!env.exists())
		{
			System.err.println("Usage: Please edit LibertyaEnvTemplate.properties and save as LibertyaEnv.properties");
			return;
		}
		
		Ini.setShowLicenseDialog(false);
		ConfigurationData data = new ConfigurationData(null);
		if (!data.load()) return;
		if (!data.test()) 
		{
			System.err.println("");
			System.err.println("Warning: One or more of the configuration test failed.");
			System.err.println("");
		}
		if (!data.save()) return;
		
		/**	Run Ant	**/
		try
		{
			CLogger.get().info("Starting Ant ... ");
			System.setProperty("ant.home", ".");
			//System.setProperty("ant.home", Ini.getOXPHome());
			String[] 	args = new String[] {"setup"};
			
            if (SaaSUtils.isSaasInstance() && SaaSUtils.getInstanceID() > 0) {
            	args	= new String[] { "setupSaaS", "-DportShiftDir="+SaaSUtils.getSaasGeneralDir()+Constants.SUBDIR_SAAS_GENERAL_BIN, "-DportShiftScript="+Constants.EXEC_INSTANCE_PORTS, "-DinstanceID=" + SaaSUtils.getInstanceID() };
            }

			
		//	Launcher.main (args);	//	calls System.exit
			Main antMain = new Main();
			antMain.startAnt(args, null, null);
		}
		catch (Exception e)
		{
			CLogger.get().log(Level.SEVERE, "ant", e);
		}
	}
	
	/**
	 * 	Start
	 * 	@param args Log Level e.g. ALL, FINE
	 */
	public static void main(String[] args)
	{
		CLogMgt.initialize(true);
		Handler fileHandler = new CLogFile(System.getProperty("user.dir"), false, false);
		CLogMgt.addHandler(fileHandler);
		//	Log Level
		if (args.length > 0)
			CLogMgt.setLevel(args[0]);
		else
			CLogMgt.setLevel(Level.INFO);
		//	File Loger at least FINE
		if (fileHandler.getLevel().intValue() > Level.FINE.intValue())
			fileHandler.setLevel(Level.FINE);
		
		new SilentSetup();
	}	//	main
}
