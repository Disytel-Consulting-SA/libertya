package org.openXpertya.grid.ed;

import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;

import org.compiere.swing.CPassword;

public class VPasswordSimple extends CPassword {
	
	private String m_oldText;
	private volatile boolean m_setting = false;

	public VPasswordSimple(){
		this.addKeyListener( this );
	}	
	
	@Override
	public void setValue( Object value ) {
        if( value == null ) {
            m_oldText = "";
        } else {
            m_oldText = value.toString();
        }

        if( !m_setting ) {
            setText( m_oldText );
        }
    }
	
	@Override
	public void keyReleased( KeyEvent e ) {
		 String newText = String.valueOf(getPassword());

        m_setting = true;

        try {
            fireVetoableChange( getName(),m_oldText,newText );
        } catch( PropertyVetoException pve ) {
        }

        m_setting = false;
    }   
}
