package org.adempiere.webui.apps.form;

import java.util.ArrayList;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Window;
import org.openXpertya.model.MSocialConversation;
import org.openXpertya.model.MSocialConversation.SocialConversationSummary;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

public class WSocialConversationList extends Window {
	private static final long serialVersionUID = 1L;
	private Integer selectedConversationID;
	private final ArrayList<SocialConversationSummary> conversations;

	public WSocialConversationList() {
		conversations = MSocialConversation.getSubscribedConversationsForUser(Env.getAD_User_ID(Env.getCtx()));
		setTitle("Listar conversaciones");
		setWidth("650px");
		setHeight("400px");
		setBorder("normal");
		setClosable(true);
		setSizable(true);
		setPosition("center");
		setAttribute(Window.MODE_KEY, Window.MODE_MODAL);
		Grid grid = GridFactory.newGridLayout();
		grid.setWidth("100%");
		grid.setHeight("100%");
		grid.setStyle("overflow: auto;");
		Columns columns = new Columns();
		Column idColumn = new Column(Msg.translate(Env.getCtx(), "Conversation"));
		idColumn.setWidth("150px");
		columns.appendChild(idColumn);
		columns.appendChild(new Column(Msg.translate(Env.getCtx(), "Subject")));
		grid.appendChild(columns);
		Rows rows = new Rows();
		grid.appendChild(rows);
		for (final SocialConversationSummary conversation : conversations) {
			Row row = new Row();
			String style = conversation.isRead() ? "" : "font-weight: bold;";
			Label id = new Label(String.valueOf(conversation.getConversationID()));
			Label subject = new Label(conversation.getSubject() == null ? "" : conversation.getSubject());
			id.setStyle(style);
			subject.setStyle(style);
			row.setStyle("cursor: pointer;");
			row.appendChild(id);
			row.appendChild(subject);
			row.addEventListener(Events.ON_CLICK, new EventListener() {
				public void onEvent(Event event) {
					selectedConversationID = conversation.getConversationID();
					detach();
				}
			});
			rows.appendChild(row);
		}
		appendChild(grid);
	}

	public Integer selectConversation() {
		AEnv.showWindow(this);
		return selectedConversationID;
	}

	public ArrayList<Integer> getConversationIDs() {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (SocialConversationSummary conversation : conversations)
			ids.add(conversation.getConversationID());
		return ids;
	}
}