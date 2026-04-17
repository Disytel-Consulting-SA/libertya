package org.openXpertya.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openXpertya.db.CConnection;

/**
 * Minimal REST endpoint used by Swing login to bootstrap DB connection details
 * without RMI/JNDI dependencies.
 */
public class ConnectionInfoRest extends HttpServlet {

    private static final String CONTENT_TYPE = "text/plain; charset=UTF-8";

    /**
     * Return current server connection attributes as CConnection#toStringLong().
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(CONTENT_TYPE);

        PrintWriter out = response.getWriter();
        out.print(CConnection.get().toStringLong());
        out.flush();
    }

    /**
     * POST behaves same as GET to simplify clients.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}