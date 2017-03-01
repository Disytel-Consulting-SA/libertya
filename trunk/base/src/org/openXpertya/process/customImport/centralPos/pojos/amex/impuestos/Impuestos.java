
package org.openXpertya.process.customImport.centralPos.pojos.amex.impuestos;

import java.util.List;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Impuestos extends Pojo {

	@SerializedName("total")
	@Expose
	private Integer total;
	@SerializedName("per_page")
	@Expose
	private String perPage;
	@SerializedName("current_page")
	@Expose
	private Integer currentPage;
	@SerializedName("last_page")
	@Expose
	private Integer lastPage;
	@SerializedName("next_page_url")
	@Expose
	private String nextPageUrl;
	@SerializedName("prev_page_url")
	@Expose
	private Object prevPageUrl;
	@SerializedName("from")
	@Expose
	private Integer from;
	@SerializedName("to")
	@Expose
	private Integer to;
	@SerializedName("data")
	@Expose
	private List<Datum> data = null;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getPerPage() {
		return perPage;
	}

	public void setPerPage(String perPage) {
		this.perPage = perPage;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getLastPage() {
		return lastPage;
	}

	public void setLastPage(Integer lastPage) {
		this.lastPage = lastPage;
	}

	public String getNextPageUrl() {
		return nextPageUrl;
	}

	public void setNextPageUrl(String nextPageUrl) {
		this.nextPageUrl = nextPageUrl;
	}

	public Object getPrevPageUrl() {
		return prevPageUrl;
	}

	public void setPrevPageUrl(Object prevPageUrl) {
		this.prevPageUrl = prevPageUrl;
	}

	public Integer getFrom() {
		return from;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public Integer getTo() {
		return to;
	}

	public void setTo(Integer to) {
		this.to = to;
	}

	public List<Datum> getData() {
		return data;
	}

	public void setData(List<Datum> data) {
		this.data = data;
	}

}
