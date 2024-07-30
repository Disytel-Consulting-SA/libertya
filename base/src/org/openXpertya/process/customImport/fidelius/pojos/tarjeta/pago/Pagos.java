
package org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago;

import java.util.List;

import org.openXpertya.process.customImport.fidelius.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagos extends Pojo {

	@SerializedName("data")
	@Expose
	private List<List<String>> data = null;
	
	@SerializedName("rows")
	@Expose
	private Integer rows;
	
	@SerializedName("status")
	@Expose
	private Integer status;

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer s) {
		this.status = s;
	}


	public List<List<String>> getData() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}
}
