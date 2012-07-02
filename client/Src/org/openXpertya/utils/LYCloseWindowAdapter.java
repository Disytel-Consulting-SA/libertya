package org.openXpertya.utils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.openXpertya.apps.ADialog;
import org.openXpertya.util.Env;

public class LYCloseWindowAdapter extends WindowAdapter {
	
	/** Componente a realizar dispose */
	private Disposable disposable = null;
	
	/** Mostrar o no el mensaje si no se puede hacer dispose */
	private boolean showMsg = false;
	
	/** Mensaje a mostrar */
	private static String msg;
	
	public LYCloseWindowAdapter(Disposable disposable, boolean showMsg){
		setDisposable(disposable);
		setShowMsg(showMsg);
	}
	
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(Env.closeApp(Env.getCtx())){
			getDisposable().dispose();
		}
		else if(isShowMsg()){
			ADialog.info(getDisposable().getWindowNo(), getDisposable()
					.getContainerForMsg(), getMsg());
		}
	}

	public void setDisposable(Disposable disposable) {
		this.disposable = disposable;
	}

	public Disposable getDisposable() {
		return disposable;
	}

	public void setShowMsg(boolean showMsg) {
		this.showMsg = showMsg;
	}

	public boolean isShowMsg() {
		return showMsg;
	}

	public static void setMsg(String msg) {
		LYCloseWindowAdapter.msg = msg;
	}

	public static String getMsg() {
		return msg;
	}
}
