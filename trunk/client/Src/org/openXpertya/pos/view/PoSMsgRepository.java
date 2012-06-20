package org.openXpertya.pos.view;

import java.util.HashMap;
import java.util.Map;

import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class PoSMsgRepository {

	/** Singletton instance */
	private static PoSMsgRepository instance = null;
	
	private Map<String, String> msgs;

	public static PoSMsgRepository getInstance() {
		if (instance == null) {
			instance = new PoSMsgRepository();
		}
		return instance;
	}
	
	private PoSMsgRepository() {
		super();
		this.msgs = new HashMap<String, String>();
	}

	public String getMsg(String name) {
		String msg;
		if(getMsgs().containsKey(name))
			msg = getMsgs().get(name);
		else {
			msg = Msg.translate(Env.getCtx(),name);
			getMsgs().put(name, msg);
		}
		return msg;
	}
	
	public Map<String, String> getMsgs() {
		return msgs;
	}

	public void setMsgs(Map<String, String> msgs) {
		this.msgs = msgs;
	}
}
