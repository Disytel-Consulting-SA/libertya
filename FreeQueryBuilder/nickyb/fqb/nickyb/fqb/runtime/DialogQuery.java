package nickyb.fqb.runtime;

import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.border.TitledBorder;

import nickyb.fqb.util.ConfirmDialog;

public class DialogQuery extends ConfirmDialog
{
	private ViewHistory history;
	private JTextArea comment;
	
	protected DialogQuery(ViewHistory history)
	{
		super(history, "properties",320,400);
		this.history = history;
		
		JPanel pnltxt = new JPanel(new GridLayout(2,1));
		
		JScrollPane scroll = new JScrollPane(comment = new JTextArea(history.getSelectedPlusValue(1)));
		scroll.setBorder(new TitledBorder("comment"));
		pnltxt.add(scroll);
		
		comment.setWrapStyleWord(true);
		comment.setLineWrap(true);
		
		JTextArea syntax = new JTextArea(history.getSelectedSyntax());
		syntax.setWrapStyleWord(true);
		syntax.setLineWrap(true);
		syntax.setEditable(false);
		syntax.setOpaque(false);
		
		scroll = new JScrollPane(syntax);
		scroll.setBorder(new TitledBorder("syntax"));
		pnltxt.add(scroll);
		
		Box detail = new Box(BoxLayout.Y_AXIS);
		getContentPane().add(detail);
			
		detail.add(new JLabel("created:"));
		detail.add(new JLabel(history.getSelectedPlusValue(0)));
		detail.add(new JLabel("database:"));
		detail.add(new JLabel(history.getSelectedPlusValue(2)));
		detail.add(pnltxt);
	}
	
	protected boolean onConfirm()
	{
		history.setSelectedComment(comment.getText());
		
		return true;
	}

	protected void onRunning()
	{
	}
}
