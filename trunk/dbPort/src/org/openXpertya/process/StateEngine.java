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

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class StateEngine {

    /**
     * Constructor de la clase ...
     *
     */

    public StateEngine() {
        this( STATE_NotStarted );
        log = CLogger.getCLogger( getClass());
    }    // State

    /**
     * Constructor de la clase ...
     *
     *
     * @param startState
     */

    public StateEngine( String startState ) {
        if( startState != null ) {
            m_state = startState;
        }
    }    // State

    // Same as AD_WF_Process.WFSTATE

    /** Descripción de Campos */

    public static final String STATE_NotStarted = "ON";

    /** Descripción de Campos */

    public static final String STATE_Running = "OR";

    /** Descripción de Campos */

    public static final String STATE_Suspended = "OS";

    /** Descripción de Campos */

    public static final String STATE_Completed = "CC";

    /** Descripción de Campos */

    public static final String STATE_Aborted = "CA";

    /** Descripción de Campos */

    public static final String STATE_Terminated = "CT";

    /** Descripción de Campos */

    public static final String ACTION_Suspend = "Suspend";

    /** Descripción de Campos */

    public static final String ACTION_Start = "Start";

    /** Descripción de Campos */

    public static final String ACTION_Resume = "Resume";

    /** Descripción de Campos */

    public static final String ACTION_Complete = "Complete";

    /** Descripción de Campos */

    public static final String ACTION_Abort = "Abort";

    /** Descripción de Campos */

    public static final String ACTION_Terminate = "Terminate";

    /** Descripción de Campos */

    private String m_state = STATE_NotStarted;

    /** Descripción de Campos */

    private boolean m_throwException = false;

    /** Descripción de Campos */

    protected CLogger log = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isThrowException() {
        return m_throwException;
    }    // isThrowException

    /**
     * Descripción de Método
     *
     *
     * @param throwException
     */

    public void setThrowException( boolean throwException ) {
        m_throwException = throwException;
    }    // setThrowException

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getState() {
        return m_state;
    }    // getState

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getStateInfo() {
        String state = getState();    // is overwritten to update

        if( STATE_Running.equals( state )) {
            return "Running";
        } else if( STATE_NotStarted.equals( state )) {
            return "Not Started";
        } else if( STATE_Suspended.equals( state )) {
            return "Suspended";
        } else if( STATE_Completed.equals( state )) {
            return "Completed";
        } else if( STATE_Aborted.equals( state )) {
            return "Aborted";
        } else if( STATE_Terminated.equals( state )) {
            return "Terminated";
        }

        return state;
    }    // getStateInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOpen() {
        return STATE_Running.equals( m_state ) || STATE_NotStarted.equals( m_state ) || STATE_Suspended.equals( m_state );
    }    // isOpen

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isNotRunning() {
        return STATE_NotStarted.equals( m_state ) || STATE_Suspended.equals( m_state );
    }    // isNotRunning

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isClosed() {
        return STATE_Completed.equals( m_state ) || STATE_Aborted.equals( m_state ) || STATE_Terminated.equals( m_state );
    }    // isClosed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isNotStarted() {
        return STATE_NotStarted.equals( m_state );
    }    // isNotStarted

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isRunning() {
        return STATE_Running.equals( m_state );
    }    // isRunning

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSuspended() {
        return STATE_Suspended.equals( m_state );
    }    // isSuspended

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCompleted() {
        return STATE_Completed.equals( m_state );
    }    // isCompleted

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isAborted() {
        return STATE_Aborted.equals( m_state );
    }    // isAborted

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTerminated() {
        return STATE_Terminated.equals( m_state );
    }    // isTerminated

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean start() {
        if( log == null ) {
            log = CLogger.getCLogger( getClass());
        }

        if( isNotStarted()) {
            m_state = STATE_Running;
            log.info( "starting ..." );

            return true;
        }

        String msg = "start failed: Not Not Started (" + getState() + ")";

        if( m_throwException ) {
            throw new IllegalStateException( msg );
        }

        log.warning( msg );

        return false;
    }    // start

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean resume()    // raises CannotResume, NotRunning, NotSuspended
    {
        if( log == null ) {
            log = CLogger.getCLogger( getClass());
        }

        if( isSuspended()) {
            m_state = STATE_Running;
            log.info( "resuming ..." );

            return true;
        }

        String msg = "resume failed: Not Suspended (" + getState() + ")";

        if( m_throwException ) {
            throw new IllegalStateException( msg );
        }

        log.warning( msg );

        return false;
    }    // resume

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean suspend()    // raises CannotSuspend, NotRunning, AlreadySuspended
    {
        if( log == null ) {
            log = CLogger.getCLogger( getClass());
        }

        if( isRunning()) {
            m_state = STATE_Suspended;
            log.info( "suspending ..." );

            return true;
        }

        String msg = "suspend failed: Not Running (" + getState() + ")";

        if( m_throwException ) {
            throw new IllegalStateException( msg );
        }

        log.warning( msg );

        return false;
    }    // suspend

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean complete() {
        if( log == null ) {
            log = CLogger.getCLogger( getClass());
        }

        if( isRunning()) {
            m_state = STATE_Completed;
            log.info( "completing ..." );

            return true;
        }

        String msg = "complete failed: Not Running (" + getState() + ")";

        if( m_throwException ) {
            throw new IllegalStateException( msg );
        }

        log.warning( msg );

        return false;
    }    // complete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean abort()    // raises CannotStop, NotRunning
    {
        if( log == null ) {
            log = CLogger.getCLogger( getClass());
        }

        if( isOpen()) {
            m_state = STATE_Aborted;
            log.info( "aborting ..." );

            return true;
        }

        String msg = "abort failed: Not Open (" + getState() + ")";

        if( m_throwException ) {
            throw new IllegalStateException( msg );
        }

        log.warning( msg );

        return false;
    }    // abort

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean terminate()    // raises CannotStop, NotRunning
    {
        if( log == null ) {
            log = CLogger.getCLogger( getClass());
        }

        if( isOpen()) {
            m_state = STATE_Terminated;
            log.info( "terminating ..." );

            return true;
        }

        String msg = "terminate failed: Not Open (" + getState() + ")";

        if( m_throwException ) {
            throw new IllegalStateException( msg );
        }

        log.warning( msg );

        return false;
    }    // terminate

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String[] getNewStateOptions() {
        if( isNotStarted()) {
            return new String[]{ STATE_Running,STATE_Aborted,STATE_Terminated };
        }

        if( isRunning()) {
            return new String[]{ STATE_Suspended,STATE_Completed,STATE_Aborted,STATE_Terminated };
        }

        if( isSuspended()) {
            return new String[]{ STATE_Running,STATE_Aborted,STATE_Terminated };
        }

        //

        return new String[]{};
    }    // getNewStateOptions

    /**
     * Descripción de Método
     *
     *
     * @param newState
     *
     * @return
     */

    public boolean isValidNewState( String newState ) {
        String[] options = getNewStateOptions();

        for( int i = 0;i < options.length;i++ ) {
            if( options[ i ].equals( newState )) {
                return true;
            }
        }

        return false;
    }    // isValidNewState

    /**
     * Descripción de Método
     *
     *
     * @param newState
     *
     * @return
     */

    public boolean setState( String newState )    // raises InvalidState, TransitionNotAllowed
    {
        if( STATE_Running.equals( newState )) {
            if( isNotStarted()) {
                return start();
            } else {
                return resume();
            }
        } else if( STATE_Suspended.equals( newState )) {
            return suspend();
        } else if( STATE_Completed.equals( newState )) {
            return complete();
        } else if( STATE_Aborted.equals( newState )) {
            return abort();
        } else if( STATE_Terminated.equals( newState )) {
            return terminate();
        }

        return false;
    }    // setState

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String[] getActionOptions() {
        if( isNotStarted()) {
            return new String[]{ ACTION_Start,ACTION_Abort,ACTION_Terminate };
        }

        if( isRunning()) {
            return new String[]{ ACTION_Suspend,ACTION_Complete,ACTION_Abort,ACTION_Terminate };
        }

        if( isSuspended()) {
            return new String[]{ ACTION_Resume,ACTION_Abort,ACTION_Terminate };
        }

        //

        return new String[]{};
    }    // getActionOptions

    /**
     * Descripción de Método
     *
     *
     * @param action
     *
     * @return
     */

    public boolean isValidAction( String action ) {
        String[] options = getActionOptions();

        for( int i = 0;i < options.length;i++ ) {
            if( options[ i ].equals( action )) {
                return true;
            }
        }

        return false;
    }    // isValidAction

    /**
     * Descripción de Método
     *
     *
     * @param action
     *
     * @return
     */

    public boolean process( String action )    // raises InvalidState, TransitionNotAllowed
    {
        if( ACTION_Start.equals( action )) {
            return start();
        } else if( ACTION_Complete.equals( action )) {
            return complete();
        } else if( ACTION_Suspend.equals( action )) {
            return suspend();
        } else if( ACTION_Resume.equals( action )) {
            return resume();
        } else if( ACTION_Abort.equals( action )) {
            return abort();
        } else if( ACTION_Terminate.equals( action )) {
            return terminate();
        }

        return false;
    }    // process

    /**
     * Descripción de Método
     *
     *
     * @param action
     *
     * @return
     */

    public String getNewStateIfAction( String action ) {
        if( isValidAction( action )) {
            if( ACTION_Start.equals( action )) {
                return STATE_Running;
            } else if( ACTION_Complete.equals( action )) {
                return STATE_Completed;
            } else if( ACTION_Suspend.equals( action )) {
                return STATE_Suspended;
            } else if( ACTION_Resume.equals( action )) {
                return STATE_Running;
            } else if( ACTION_Abort.equals( action )) {
                return STATE_Aborted;
            } else if( ACTION_Terminate.equals( action )) {
                return STATE_Terminated;
            }
        }

        // Unchanged

        return getState();
    }    // getNewStateIfAction

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return getStateInfo();
    }    // toString
}    // State



/*
 *  @(#)StateEngine.java   25.03.06
 * 
 *  Fin del fichero StateEngine.java
 *  
 *  Versión 2.2
 *
 */
