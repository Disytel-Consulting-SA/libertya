package org.openXpertya.process.customImport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;

import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Clase que abstrae el método prepare para los procesos custom de importación de archivos.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public abstract class FileImportProcess extends SvrProcess {

	/** Archivo a importar. */
	protected File p_file;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null) {
				;
			} else if (name.equals("File")) {
				p_file = (File) para[i].getParameter();
			} else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}
	}

	/**
	 * Substring. Permite ingresar los parámetros desde y hasta contando desde 1.
	 * @param input String de entrada.
	 * @param from índice desde.
	 * @param to índice hasta.
	 * @return string resultante.
	 */
	protected String read(String input, int from, int to) {
		return input.substring(from - 1, to).trim();
	}

	/**
	 * Mensaje informativo tras operación exitosa.
	 * @param params Parámetros del mensaje.
	 * @return String con el mensaje resultante.
	 */
	protected String successMsg(Object[] params) {
		String AD_Message = "CustomImportSuccess";
		if (params == null) {
			return Msg.getMsg(Env.getAD_Language(getCtx()), AD_Message);
		}
		return Msg.getMsg(Env.getAD_Language(getCtx()), AD_Message, params);
	}

	/**
	 * Mensaje de error.
	 * @param e Excepción.
	 * @param saveErr Error al guardar el registro en la tabla de importación.
	 * @return String con el mensaje de error resultante.
	 */
	protected String errorMsg(Exception e, boolean saveErr) {
		if (saveErr) {
			String AD_Message = "CustomImportSaveError";
			return Msg.getMsg(Env.getAD_Language(getCtx()), AD_Message);
		}
		String AD_Message = "CustomImportUnexpectedError";

		if (e instanceof FileNotFoundException) {
			AD_Message = "CustomImportFileNotFound";
		}
		if (e instanceof IOException) {
			AD_Message = "CustomImportIOError";
		}
		if (e instanceof ParseException) {
			AD_Message = "CustomImportParseError";
		}
		if (e instanceof IndexOutOfBoundsException) {
			AD_Message = "CustomImportFormatError";
		}
		return Msg.getMsg(Env.getAD_Language(getCtx()), AD_Message);
	}

}
