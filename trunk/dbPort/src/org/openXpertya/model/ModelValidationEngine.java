/*
 * @(#)ModelValidationEngine.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;

//~--- Importaciones JDK ------------------------------------------------------

import java.beans.VetoableChangeSupport;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 *      Model Validation Engine
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: ModelValidationEngine.java,v 1.3 2005/03/11 20:28:38 jjanke Exp $
 */
public class ModelValidationEngine {

    /** Engine Singleton */
    private static ModelValidationEngine	s_engine	= null;

    /** Logger */
    private static CLogger	log	= CLogger.getCLogger(ModelValidationEngine.class);

    /** Change Support */
    private VetoableChangeSupport	m_changeSupport	= new VetoableChangeSupport(this);

    /** Validators */
    private ArrayList	m_validators	= new ArrayList();

    /** Model Change Listeners */
    private Hashtable	m_modelChangeListeners	= new Hashtable();

    /** Document Validation Listeners */
    private Hashtable	m_docValidateListeners	= new Hashtable();

    /**
     *      Constructor.
     *      Creates Model Validators
     */
    private ModelValidationEngine() {

        super();

        // Go through all Clients and start Validators
        MClient[]	clients	= MClient.getAll(new Properties());

        for (int i = 0; i < clients.length; i++) {

            String	classNames	= clients[i].getModelValidationClasses();

            if ((classNames == null) || (classNames.length() == 0)) {
                continue;
            }

            StringTokenizer	st	= new StringTokenizer(classNames, ";");

            while (st.hasMoreTokens()) {

                String	className	= null;

                try {

                    className	= st.nextToken();

                    if (className == null) {
                        continue;
                    }

                    className	= className.trim();

                    if (className.length() == 0) {
                        continue;
                    }

                    //
                    Class		clazz		= Class.forName(className);
                    ModelValidator	validator	= (ModelValidator) clazz.newInstance();

                    initialize(validator, clients[i]);

                } catch (Exception e) {
                    log.log(Level.SEVERE, className + ": " + e.getMessage());
                }
            }
        }

        log.config(toString());

    }		// ModelValidatorEngine

    /**
     *      Add Document Validation Listener
     *      @param tableName table name
     * @param listener
     */
    public void addDocValidate(String tableName, ModelValidator listener) {

        if ((tableName == null) || (listener == null)) {
            return;
        }

        //
        String		propertyName	= tableName + listener.getAD_Client_ID();
        ArrayList	list		= (ArrayList) m_docValidateListeners.get(propertyName);

        if (list == null) {

            list	= new ArrayList();
            list.add(listener);
            m_docValidateListeners.put(propertyName, list);

        } else {
            list.add(listener);
        }

    }		// addDocValidate

    /**
     *      Add Model Change Listener
     *      @param tableName table name
     * @param listener
     */
    public void addModelChange(String tableName, ModelValidator listener) {

        if ((tableName == null) || (listener == null)) {
            return;
        }

        //
        String		propertyName	= tableName + listener.getAD_Client_ID();
        ArrayList	list		= (ArrayList) m_modelChangeListeners.get(propertyName);

        if (list == null) {

            list	= new ArrayList();
            list.add(listener);
            m_modelChangeListeners.put(propertyName, list);

        } else {
            list.add(listener);
        }

    }		// addModelValidator

    /**
     *      Fire Document Validation.
     *      Call docValidate method of added validators
     *      @param po persistent objects
     *      @param timing see ModelValidator.TIMING_ constants
     *  @return error message or null
     */
    public String fireDocValidate(PO po, int timing) {

        if ((po == null) || (m_docValidateListeners.size() == 0)) {
            return null;
        }

        //
        String		propertyName	= po.get_TableName() + po.getAD_Client_ID();
        ArrayList	list		= (ArrayList) m_docValidateListeners.get(propertyName);

        if (list == null) {
            return null;
        }

        //
        for (int i = 0; i < list.size(); i++) {

            ModelValidator	validator	= null;

            try {

                validator	= (ModelValidator) list.get(i);

                String	error	= validator.docValidate(po, timing);

                if ((error != null) && (error.length() > 0)) {
                    return error;
                }

            } catch (Exception e) {
                log.log(Level.SEVERE, validator.toString(), e);
            }
        }

        return null;

    }		// fireModelChange

    /**
     *      Fire Model Change.
     *      Call modelChange method of added validators
     *      @param po persistent objects
     *      @param type ModelValidator.TYPE_
     *      @return error message or NULL for no veto
     */
    public String fireModelChange(PO po, int type) {

        if ((po == null) || (m_modelChangeListeners.size() == 0)) {
            return null;
        }

        //
        String		propertyName	= po.get_TableName() + po.getAD_Client_ID();
        ArrayList	list		= (ArrayList) m_modelChangeListeners.get(propertyName);

        if (list == null) {
            return null;
        }

        //
        for (int i = 0; i < list.size(); i++) {

            try {

                ModelValidator	validator	= (ModelValidator) list.get(i);
                String		error		= validator.modelChange(po, type);

                if ((error != null) && (error.length() > 0)) {
                    return error;
                }

            } catch (Exception e) {

                String	error	= e.getMessage();

                if (error == null) {
                    error	= e.toString();
                }

                return error;
            }
        }

        return null;

    }		// fireModelChange

    /**
     *      Initialize and add validator
     *      @param validator
     *      @param client
     */
    private void initialize(ModelValidator validator, MClient client) {

        validator.initialize(this, client);
        m_validators.add(validator);

    }		// initialize

    /**
     *      Called when login is complete
     *      @param AD_Client_ID client
     *      @param AD_Org_ID org
     *      @param AD_Role_ID role
     *      @param AD_User_ID user
     *      @return error message or null
     */
    public CallResult loginComplete(int AD_Client_ID, int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
    	CallResult result = new CallResult();
        for (int i = 0; i < m_validators.size(); i++) {

            ModelValidator	validator	= (ModelValidator) m_validators.get(i);

            if (AD_Client_ID == validator.getAD_Client_ID()) {

                result = validator.login(AD_Org_ID, AD_Role_ID, AD_User_ID);

                if (result != null && result.isError()) {
                    return result;
                }
            }
        }

        return result;

    }		// loginComplete

    /**
     *      Remove Document Validation Listener
     *      @param tableName table name
     * @param listener
     */
    public void removeDocValidate(String tableName, ModelValidator listener) {

        if ((tableName == null) || (listener == null)) {
            return;
        }

        String		propertyName	= tableName + listener.getAD_Client_ID();
        ArrayList	list		= (ArrayList) m_docValidateListeners.get(propertyName);

        if (list == null) {
            return;
        }

        list.remove(listener);

        if (list.size() == 0) {
            m_docValidateListeners.remove(propertyName);
        }

    }		// removeModelValidator

    /**
     *      Remove Model Change Listener
     *      @param tableName table name
     * @param listener
     */
    public void removeModelChange(String tableName, ModelValidator listener) {

        if ((tableName == null) || (listener == null)) {
            return;
        }

        String		propertyName	= tableName + listener.getAD_Client_ID();
        ArrayList	list		= (ArrayList) m_modelChangeListeners.get(propertyName);

        if (list == null) {
            return;
        }

        list.remove(listener);

        if (list.size() == 0) {
            m_modelChangeListeners.remove(propertyName);
        }

    }		// removeModelValidator

    /**
     *       String Representation
     *       @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("ModelValidationEngine[");

        sb.append("Validators=#").append(m_validators.size()).append(", ModelChange=#").append(m_modelChangeListeners.size()).append(", DocValidate=#").append(m_docValidateListeners.size()).append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Singleton
     *      @return engine
     */
    public static ModelValidationEngine get() {

        if (s_engine == null) {
            s_engine	= new ModelValidationEngine();
        }

        return s_engine;

    }		// get
}	// ModelValidatorEngine



/*
 * @(#)ModelValidationEngine.java   02.jul 2007
 * 
 *  Fin del fichero ModelValidationEngine.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
