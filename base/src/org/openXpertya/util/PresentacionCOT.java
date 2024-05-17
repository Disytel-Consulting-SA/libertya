package org.openXpertya.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.openXpertya.model.MExternalService;

public class PresentacionCOT {
	
	//Configuracion de ExternalService
	MExternalService config;
	File cotFile;
	
	public PresentacionCOT(MExternalService config, File cotFile) {
		this.config = config;
		this.cotFile = cotFile;
	}


	//Presentar archivo COT en Arba
	public String presentar() throws IOException {
        
        // Setear conexion
        URL url = new URL(config.getURL());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Configurar como POST
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        // Crear un límite único para el formulario multipart
        String boundary = "===" + System.currentTimeMillis() + "===";
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        // Crear el body
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        
        
        wr.writeBytes("--" + boundary + "\r\n");
        wr.writeBytes("Content-Disposition: form-data; name=\"user\"\r\n\r\n");
        wr.writeBytes(config.getUserName().toString() + "\r\n");
        wr.writeBytes("--" + boundary + "\r\n");
        wr.writeBytes("Content-Disposition: form-data; name=\"password\"\r\n\r\n");
        wr.writeBytes(config.getPassword().toString() + "\r\n");

        // Agregar el archivo como parte del formulario
        wr.writeBytes("--" + boundary + "\r\n");
        wr.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + cotFile.getName() + "\"\r\n");
        wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
        FileInputStream inputStream = new FileInputStream(cotFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            wr.write(buffer, 0, bytesRead);
        }
        wr.writeBytes("\r\n");
        wr.writeBytes("--" + boundary + "--\r\n");
        
        // Cerrar
        wr.flush();
        wr.close();
        inputStream.close();

        // Leer respuesta de Arba
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Imprimir la respuesta del servidor
        String res = " ****** Código: " + responseCode + " - Respuesta del servidor: " + response.toString();
		
        return res;		
	}
	
}
