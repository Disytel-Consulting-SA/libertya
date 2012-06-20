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



package org.openXpertya.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ServerStatus extends HttpServlet {

    /**
     * Descripción de Método
     *
	 * 	doGet
	 *	@see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 *	@param arg0
	 *	@param arg1
	 *	@throws javax.servlet.ServletException
	 *	@throws java.io.IOException
     */

    protected void doGet( HttpServletRequest arg0,HttpServletResponse arg1 ) throws ServletException,IOException {

        // TODO Auto-generated method stub

        super.doGet( arg0,arg1 );
    }

    /**
     * Descripción de Método
     *
	 * 	doPost
	 *	@see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 *	@param arg0
	 *	@param arg1
	 *	@throws javax.servlet.ServletException
	 *	@throws java.io.IOException
     */

    protected void doPost( HttpServletRequest arg0,HttpServletResponse arg1 ) throws ServletException,IOException {

        // TODO Auto-generated method stub

        super.doPost( arg0,arg1 );
    }

    /**
     * Descripción de Método
     *
	 * 	getServletInfo
	 *	@see javax.servlet.Servlet#getServletInfo()
	 *	@return
     */

    public String getServletInfo() {

        // TODO Auto-generated method stub

        return super.getServletInfo();
    }

    /**
     * Descripción de Método
     *
	 * 	init
	 *	@see javax.servlet.GenericServlet#init()
	 *	@throws javax.servlet.ServletException
     */

    public void init() throws ServletException {

        // TODO Auto-generated method stub

        super.init();
    }

    /**
     * Descripción de Método
     *
     *
	 * 	init
	 *	@see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 *	@param arg0
	 *	@throws javax.servlet.ServletException
     */

    public void init( ServletConfig arg0 ) throws ServletException {

        // TODO Auto-generated method stub

        super.init( arg0 );
    }
}



/*
 *  @(#)ServerStatus.java   25.03.06
 * 
 *  Fin del fichero ServerStatus.java
 *  
 *  Versión 2.2
 *
 */
