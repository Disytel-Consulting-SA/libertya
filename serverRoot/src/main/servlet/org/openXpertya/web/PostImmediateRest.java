package org.openXpertya.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openXpertya.acct.Doc;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.util.Env;

/**
 * Minimal REST endpoint to execute Doc.postImmediate from Swing clients
 * when RMI is unavailable.
 */
public class PostImmediateRest extends HttpServlet {

    private static final String CONTENT_TYPE = "text/plain; charset=UTF-8";

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType(CONTENT_TYPE);

        PrintWriter out = response.getWriter();

        try {
            int AD_Client_ID = Integer.parseInt(request.getParameter("AD_Client_ID"));
            int AD_Table_ID = Integer.parseInt(request.getParameter("AD_Table_ID"));
            int Record_ID = Integer.parseInt(request.getParameter("Record_ID"));
            boolean force = "true".equalsIgnoreCase(request.getParameter("force"));

            Properties ctx = Env.getCtx();
            MAcctSchema[] ass = MAcctSchema.getClientAcctSchema(ctx, AD_Client_ID);
            boolean ok = Doc.postImmediate(ass, AD_Table_ID, Record_ID, force);

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(ok);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(e.getMessage());
        } finally {
            out.flush();
        }
    }
}