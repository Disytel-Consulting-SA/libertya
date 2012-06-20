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

import java.sql.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.*;

/**
 * Esta clase que hereda de SvrProcess borra un cliente en la Base de Datos
 * o lo que es lo mismo una Compañía según los conceptos de openXpertya
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public class DeleteClient extends SvrProcess {//Extiende de SvrProcess

    /**
     * Constructor de la clase. Simplemente llama al constructor padre y
     * envía al log "Borra todos los datos de la Compañia"
     * 
     * 
     */
    public DeleteClient() {
        super();
        log.finest( "Borra todos los datos de la Compañia" );
    }    // DeleteClient

    /** ID del cliente a borrar */

    private int m_Client_ID = 0;



    /** Descripción de Campos*/

    //private int m_Iterations = 1;//No se usa

    /** Descripción de Campos*/

    //private boolean m_IgnoreError = false;//No se usa



    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();//Creates an ProcessInfoParameter array

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();
            //Descomentar esto para obtener el codigo de la version anterior
            if( name.equals( "AD_Client_ID" )) {
                m_Client_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"CopyColumnsFromTable.prepare - Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws java.lang.Exception
     */

    protected String doIt() throws java.lang.Exception {
    	ArrayList listaTablas= new ArrayList();//Array con todas las tablas de la BD
		String consultaTablas="SELECT tablename FROM AD_table WHERE isview='N'";//Consulta de todos los nombres de BD
		int registrosBorrados=0;//Registros que vamos a borrar
		try{
			PreparedStatement pstmt=DB.prepareStatement(consultaTablas);
			ResultSet rs1=pstmt.executeQuery();
			//Ejecutada la consulta
			while(rs1.next()){
				listaTablas.add((String)rs1.getString(1));//Todas las tablas en listaTablas
			}

			//Ahora vamos a recorrer todas las tablas quitando los triggers
			for(int i=0;i<listaTablas.size();i++){
				String quitarTriggers="ALTER TABLE "+(String)listaTablas.get(i)+" DISABLE TRIGGER ALL";
				DB.executeUpdate(quitarTriggers);
				//En este punto ya deberian estar quitados los triggers

			}
			//Ahora vamos a ir a borrar los clientes que no existen

			for(int i=0;i<listaTablas.size();i++){
				String borrar="DELETE FROM "+(String)listaTablas.get(i)+" WHERE AD_client_ID="+m_Client_ID;
				int borrados=DB.executeUpdate(borrar);
				registrosBorrados+=borrados;
			}
			//Despues de esto ya deberia estar borrado todo
			for(int i=0;i<listaTablas.size();i++){
				String quitarTriggers="ALTER TABLE "+(String)listaTablas.get(i)+" ENABLE TRIGGER ALL";
				DB.executeUpdate(quitarTriggers);
				//En este punto ya deberian estar puestos de nuevo los triggers
			}

		}catch(Exception e){}

		return "REALIZADOS CAMBIOS EN "+registrosBorrados+" REGISTROS EN LA BASE DE DATOS";


    }    // doIt
}    // CopyColumnsFromTable



/*
 *  @(#)DeleteClient.java   02.07.07
 *
 *  Fin del fichero DeleteClient.java
 *
 *  Versión 2.2
 *
 */
