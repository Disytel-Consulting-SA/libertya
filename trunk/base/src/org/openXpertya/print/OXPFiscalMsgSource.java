package org.openXpertya.print;

import java.util.Properties;

import org.openXpertya.print.fiscal.msg.DefaultMsgSource;
import org.openXpertya.print.fiscal.msg.MsgSource;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;


public class OXPFiscalMsgSource implements MsgSource {

	private DefaultMsgSource defaultMsgSource = DefaultMsgSource.getInstance();
	
	public String get(String key) {
		Properties ctx = Env.getCtx(); 
		String result = Msg.translate(ctx, key);
		if(result.equals(key))
			result = getDefaultMsgSource().get(key);
		return result;
	}

	/**
	 * @return Returns the defaultMsgSource.
	 */
	private DefaultMsgSource getDefaultMsgSource() {
		return defaultMsgSource;
	}
	
	

}
