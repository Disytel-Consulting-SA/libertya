package org.openXpertya.process;

import org.openXpertya.model.MOrder;
import org.openXpertya.model.MTransfer;
import org.openXpertya.util.CLogger;

public class CrearPedidoDesde extends SvrProcess {

	/** Pedido */
	private int pedido;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("C_Order_ID")) {
				setPedido(para[i].getParameterAsInt());
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		int recordID = getRecord_ID();
		MTransfer transfer = new MTransfer(getCtx(), recordID, get_TrxName());
		transfer.setC_Order_ID(pedido);
		if (!transfer.save(get_TrxName())) {
			throw new Exception("Error creando cabecera de transferencia: "
					+ CLogger.retrieveErrorAsString());
		}
		MOrder order = new MOrder(getCtx(), pedido, get_TrxName());
		transfer.addLines(transfer, order, get_TrxName());
		return "";
	}

	public int getPedido() {
		return pedido;
	}

	public void setPedido(int pedido) {
		this.pedido = pedido;
	}

}
