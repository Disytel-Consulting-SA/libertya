package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.compiere.swing.CDialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.model.MSocialConversation;
import org.openXpertya.model.MSocialConversation.SocialConversationSummary;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/** Listado de conversaciones a las que esta suscripto el usuario. */
public class VSocialConversationList extends CDialog {
	private static final long serialVersionUID = 1L;
	private static final int DEF_WIDTH = 600;
	private static final int DEF_HEIGHT = 400;
	private Integer selectedConversationID;
	private final ConversationListTableModel tableModel;
	private final JTable conversationsTable;

	public VSocialConversationList(Frame owner) {
		super(owner, "Listar conversaciones", true);
		ArrayList<SocialConversationSummary> conversations = MSocialConversation.getSubscribedConversationsForUser(Env.getAD_User_ID(Env.getCtx()));
		tableModel = new ConversationListTableModel(conversations);
		conversationsTable = new JTable(tableModel);
		initialize(owner);
	}

	private void initialize(Frame owner) {
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(DEF_WIDTH, DEF_HEIGHT);
		conversationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		conversationsTable.setRowSelectionAllowed(true);
		ConversationCellRenderer renderer = new ConversationCellRenderer(tableModel);
		conversationsTable.setDefaultRenderer(Object.class, renderer);
		conversationsTable.setDefaultRenderer(Integer.class, renderer);
		conversationsTable.getColumnModel().getColumn(0).setPreferredWidth(120);
		conversationsTable.getColumnModel().getColumn(1).setPreferredWidth(480);
		conversationsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				selectRow(event.getPoint());
			}
		});
		add(new JScrollPane(conversationsTable), BorderLayout.CENTER);
		AEnv.positionCenterWindow(owner, this);
	}

	private void selectRow(Point point) {
		int viewRow = conversationsTable.rowAtPoint(point);
		if (viewRow < 0)
			return;
		int modelRow = conversationsTable.convertRowIndexToModel(viewRow);
		selectedConversationID = tableModel.getConversationAt(modelRow).getConversationID();
		dispose();
	}

	public Integer selectConversation() {
		setVisible(true);
		return selectedConversationID;
	}

	public ArrayList<Integer> getConversationIDs() {
		ArrayList<Integer> conversationIDs = new ArrayList<Integer>();
		for (SocialConversationSummary conversation : tableModel.getConversations())
			conversationIDs.add(conversation.getConversationID());
		return conversationIDs;
	}

	private static class ConversationListTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private final ArrayList<SocialConversationSummary> conversations;
		public ConversationListTableModel(ArrayList<SocialConversationSummary> conversations) {
			this.conversations = conversations;
		}
		@Override public int getRowCount() { return conversations.size(); }
		@Override public int getColumnCount() { return 2; }
		@Override public String getColumnName(int column) {
			return column == 0 ? Msg.translate(Env.getCtx(), "Conversation") : Msg.translate(Env.getCtx(), "Subject");
		}
		@Override public Class<?> getColumnClass(int column) { return column == 0 ? Integer.class : String.class; }
		@Override public Object getValueAt(int row, int column) {
			SocialConversationSummary conversation = getConversationAt(row);
			return column == 0 ? conversation.getConversationID() : conversation.getSubject();
		}
		public SocialConversationSummary getConversationAt(int row) { return conversations.get(row); }
		public ArrayList<SocialConversationSummary> getConversations() { return conversations; }
	}

	private static class ConversationCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private final ConversationListTableModel tableModel;
		public ConversationCellRenderer(ConversationListTableModel tableModel) { this.tableModel = tableModel; }
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			boolean read = tableModel.getConversationAt(table.convertRowIndexToModel(row)).isRead();
			component.setFont(component.getFont().deriveFont(read ? Font.PLAIN : Font.BOLD));
			return component;
		}
	}
}