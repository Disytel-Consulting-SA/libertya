package org.openXpertya.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openXpertya.model.MAcctProcessor;
import org.openXpertya.model.MAlertProcessor;
import org.openXpertya.model.MRequestProcessor;
import org.openXpertya.model.MScheduler;
import org.openXpertya.model.ProcesadorOXP;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.wf.MWorkflowProcessor;

/**
 * Listener para integrar los procesadores OXP con el ciclo de vida de la
 * aplicación web (Tomcat).
 *
 * - En contextInitialized() arranca todos los procesadores activos
 *   (Request, Workflow, Accounting, Alert, Scheduler).
 * - En contextDestroyed() pide el apagado limpio de todos los hilos.
 *
 * IMPORTANTE:
 *   Esto asume que ServidorOXP tiene:
 *     - static volatile boolean shutdownRequested
 *     - static void requestShutdown()
 *     - static boolean isShutdownRequested()
 *   y que sus loops respetan ese flag + InterruptedException.
 */
public class ServidorOXPContextListener implements ServletContextListener {

    private static final CLogger log = CLogger.getCLogger(ServidorOXPContextListener.class);

    /** Hilos de procesadores levantados por este contexto */
    private final List<ServidorOXP> servers = new ArrayList<ServidorOXP>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("ServidorOXPContextListener.contextInitialized - arrancando procesadores OXP");

        // Contexto global de Libertya/OpenXpertya
        Properties ctx = Env.getCtx();

        // 1) Request Processors
        try {
            MRequestProcessor[] rps = MRequestProcessor.getActive(ctx);
            startProcessors("RequestProcessor", rps);
        } catch (Throwable t) {
            log.log(Level.SEVERE, "Error arrancando RequestProcessors", t);
        }

        // 2) Workflow Processors
        try {
            MWorkflowProcessor[] wps = MWorkflowProcessor.getActive(ctx);
            startProcessors("WorkflowProcessor", wps);
        } catch (Throwable t) {
            log.log(Level.SEVERE, "Error arrancando WorkflowProcessors", t);
        }

        // 3) Accounting (Acct) Processors
        try {
            MAcctProcessor[] aps = MAcctProcessor.getActive(ctx);
            startProcessors("AcctProcessor", aps);
        } catch (Throwable t) {
            log.log(Level.SEVERE, "Error arrancando AcctProcessors", t);
        }

        // 4) Alert Processors
        try {
            MAlertProcessor[] alps = MAlertProcessor.getActive(ctx);
            startProcessors("AlertProcessor", alps);
        } catch (Throwable t) {
            log.log(Level.SEVERE, "Error arrancando AlertProcessors", t);
        }

        // 5) Schedulers 
        try {
            MScheduler[] scheds = MScheduler.getActive(ctx);
            startProcessors("Scheduler", scheds);
        } catch (Throwable t) {
            log.log(Level.SEVERE, "Error arrancando Schedulers", t);
        }

        log.info("ServidorOXPContextListener.contextInitialized - procesadores arrancados: "
                 + servers.size());
    }

    /**
     * Arranca una colección de procesadores que implementan ProcesadorOXP
     * utilizando ServidorOXP.create(model).
     */
    private void startProcessors(String kind, ProcesadorOXP[] models) {
        if (models == null || models.length == 0) {
            log.info("No hay " + kind + " activos");
            return;
        }

        for (ProcesadorOXP model : models) {
            try {
                ServidorOXP server = ServidorOXP.create(model);
                server.setDaemon(true);
                server.start();
                servers.add(server);
                log.info("Iniciado " + kind + " thread: " + server.getName()
                         + " para " + model.toString());
            } catch (Throwable t) {
                log.log(Level.SEVERE,
                        "Error iniciando " + kind + " para " + model.toString(), t);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("ServidorOXPContextListener.contextDestroyed - deteniendo procesadores OXP");

        // 1) Pedimos apagado global
        ServidorOXP.requestShutdown();

        // 2) Interrumpimos hilos para cortar sleeps / IO
        for (ServidorOXP server : servers) {
            try {
                log.info("Interrumpiendo hilo: " + server.getName());
                server.interrupt();
            } catch (Throwable t) {
                log.log(Level.WARNING, "Error interrumpiendo hilo " + server.getName(), t);
            }
        }

        // 3) Esperamos un poco
        for (ServidorOXP server : servers) {
            try {
                log.info("Esperando fin de hilo: " + server.getName());
                server.join(10_000L);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Throwable t) {
                log.log(Level.WARNING, "Error esperando join de " + server.getName(), t);
            }
        }

        log.info("ServidorOXPContextListener.contextDestroyed - procesadores detenidos");
    }

}
