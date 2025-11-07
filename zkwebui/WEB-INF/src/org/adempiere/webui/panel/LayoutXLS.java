package org.adempiere.webui.panel;

public class LayoutXLS {

	private static final long serialVersionUID = 1L;
	private int colWidth = 50;
	private int rowHeight = 20;
	private String colHeader = null;
	private boolean totalize = false;
	private boolean count = false;
	private String colSQL = null;
	private boolean bold = false;
	private String color = null;
	
	public LayoutXLS() {
        // Constructor vacío
    }
	
	public LayoutXLS(String colSQL2, boolean b, boolean equals, String colHeader2, int i, int j, boolean c,
			String string) {
		this.colSQL = colSQL2;
		this.totalize = b;
		this.count = equals;
		this.colHeader = colHeader2;
		this.colWidth = i;
		this.rowHeight = j;
		this.bold = c;
		this.color = string;
	}

	public int getColWidth() {
		return colWidth;
	}

	public void setColWidth(int colWidth) {
		this.colWidth = colWidth;
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}

	public String getColHeader() {
		return colHeader;
	}

	public void setColHeader(String colHeader) {
		this.colHeader = colHeader;
	}

	public boolean isTotalize() {
		return totalize;
	}

	public void setTotalize(boolean totalize) {
		this.totalize = totalize;
	}

	public boolean isCount() {
		return count;
	}

	public void setCount(boolean count) {
		this.count = count;
	}

	public String getColSQL() {
		return colSQL;
	}

	public void setColSQL(String colSQL) {
		this.colSQL = colSQL;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public void setColWidth(String colWidth) {
		try {
			this.colWidth = Integer.parseInt(colWidth);
		} catch (NumberFormatException e) {
			this.colWidth = 50; // Valor por defecto si no se puede parsear
		}
	}

	public void setRowHeight(String rowHeight) {
        try {
            this.rowHeight = Integer.parseInt(rowHeight);
        } catch (NumberFormatException e) {
            this.rowHeight = 20; // Valor por defecto si no se puede parsear
        }
	}

	public void setColHeader(String colHeader, boolean bold) {
		this.colHeader = colHeader;
		this.bold = bold;
	}

	public void setColSQL(String colSQL, boolean totalize, boolean count) {
        this.colSQL = colSQL;
        this.totalize = totalize;
        this.count = count;
	}

	public void setColSQL(String colSQL, boolean totalize, boolean count, boolean bold) {
		this.colSQL = colSQL;
		this.totalize = totalize;
		this.count = count;
		this.bold = bold;
	}

	public void setColSQL(String colSQL, boolean totalize, boolean count, String colHeader) {
		this.colSQL = colSQL;
		this.totalize = totalize;
		this.count = count;
		this.colHeader = colHeader;
		this.bold = false; // Por defecto no es negrita
	}

	public void setColSQL(String colSQL, boolean totalize, boolean count, String colHeader, boolean bold) {
		this.colSQL = colSQL;
		this.totalize = totalize;
		this.count = count;
		this.colHeader = colHeader;
		this.bold = bold;
	}

	public void setColSQL(String colSQL, boolean totalize, boolean count, String colHeader, int colWidth) {
		this.colSQL = colSQL;
		this.totalize = totalize;
		this.count = count;
		this.colHeader = colHeader;
		this.bold = false; // Por defecto no es negrita
		this.colWidth = colWidth;
	}

	public void setColSQL(String colSQL, boolean totalize, boolean count, String colHeader, int colWidth,
			boolean bold) {
		this.colSQL = colSQL;
		this.totalize = totalize;
		this.count = count;
		this.colHeader = colHeader;
		this.bold = bold;
		this.colWidth = colWidth;
	}

	public void setColSQL(String colSQL, boolean totalize, boolean count, String colHeader, int colWidth,
			int rowHeight) {
		this.colSQL = colSQL;
		this.totalize = totalize;
		this.count = count;
		this.colHeader = colHeader;
		this.bold = false; // Por defecto no es negrita
		this.colWidth = colWidth;
		this.rowHeight = rowHeight;
	}

	public void setColSQL(String colSQL, boolean totalize, boolean count, String colHeader, int colWidth, int rowHeight,
			boolean bold) {
		this.colSQL = colSQL;
		this.totalize = totalize;
		this.count = count;
		this.colHeader = colHeader;
		this.bold = bold;
		this.colWidth = colWidth;
		this.rowHeight = rowHeight;
	}

	public void setColSQL(String colSQL, boolean totalize, boolean count, String colHeader, int colWidth, int rowHeight,
            boolean bold, String color) {
        this.colSQL = colSQL;
        this.totalize = totalize;
        this.count = count;
        this.colHeader = colHeader;
        this.bold = bold;
        this.colWidth = colWidth;
        this.rowHeight = rowHeight;
        this.color = color;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
