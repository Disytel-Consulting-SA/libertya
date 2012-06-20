package nickyb.fqb;

import javax.swing.JComponent;

public interface ClauseOwner
{
	public void fireRefreshSyntax();
	
	public Object[] getColumns();
	public Object[] getFunctions();
	
	public JComponent getComponent();
}
