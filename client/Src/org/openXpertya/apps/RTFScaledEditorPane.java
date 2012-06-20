package org.openXpertya.apps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.InputStream;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.rtf.RTFEditorKit;

import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;


public class RTFScaledEditorPane extends JEditorPane 
{
	private CComboBox zoom;
	private InputStream isRTF;
	private CPanel panel;
	
	public RTFScaledEditorPane(InputStream is, CPanel graphPanel)  throws Exception
	{
		super();
		isRTF = is;
		panel = graphPanel;
		zoom = new CComboBox();
		zoom.addItem("25");
		zoom.addItem("50");
		zoom.addItem("100");
		zoom.addItem("150");
		zoom.addItem("200");
		zoom.setPreferredSize(new Dimension(100,25));

		ActionListener actionListener = new ActionListener() {
		      public void actionPerformed(ActionEvent actionEvent) {
		    	  try{
		    		  setRTF();
		    	  }catch (Exception e) {}
		      }
		};
		
		JScrollPane jScrollPane = new JScrollPane();
		this.setEditable(false); 
		jScrollPane.setViewportView(this);
		this.setPreferredSize(new Dimension(800,600));
		CPanel zoomPanel = new CPanel();
		zoomPanel.add(new CLabel("Zoom:"));
		zoomPanel.add(zoom);
	    panel.add( zoomPanel, BorderLayout.NORTH );
	    panel.add( jScrollPane, BorderLayout.CENTER );		
	    
		zoom.addActionListener(actionListener);
		zoom.setSelectedIndex(2);
		
		ScaledEditorKit sek = new ScaledEditorKit();
		this.setEditorKit(sek);		
		sek.read(isRTF, this.getDocument(), 0);
	}

	private Double getZoom()
	{
		return new Double(Double.parseDouble((String)zoom.getSelectedItem())/100);
	}

	private void setRTF() throws Exception
	{
		this.getDocument().putProperty("ZOOM_FACTOR", getZoom());
		this.setMargin(new Insets(0, 75-getZoom().intValue()*20, 0, 75-getZoom().intValue()*20));				
		RTFScaledEditorPane.this.getDocument().insertString(0, "", null);    //refresh		    		  
	}
	
	
	  public void repaint(int x, int y, int width, int height) {
		    super.repaint(0,0,getWidth(),getHeight());
		  }

}
class ScaledEditorKit extends RTFEditorKit 
{
	  public ViewFactory getViewFactory() 
	  {
	    return new StyledViewFactory();
	  }
	  
	  class StyledViewFactory implements ViewFactory 
	  {
		  public View create(Element elem) 
		  {
			  String kind = elem.getName();
			  if (kind != null) 
			  {
				  if (kind.equals(AbstractDocument.ContentElementName)) {
					  return new LabelView(elem);
				  } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					  return new ParagraphView(elem);
				  } else if (kind.equals(AbstractDocument.SectionElementName)) {
					  return new ScaledView(elem, View.Y_AXIS);
				  } else if (kind.equals(StyleConstants.ComponentElementName)) {
					  return new ComponentView(elem);
				  } else if (kind.equals(StyleConstants.IconElementName)) {
					  return new IconView(elem);
				  }
			  }
			  return new LabelView(elem);
		  }
	  }
}

class ScaledView extends BoxView
{
	  public ScaledView(Element elem, int axis) 
	  {
	    super(elem,axis);
	  }
	  
	  public double getZoomFactor()
	  {
	      Double scale=(Double)getDocument().getProperty("ZOOM_FACTOR");
	      if (scale!=null) 
	          return scale.doubleValue();
	      return 1;
	  }

	  public void paint(Graphics g, Shape allocation) 
	  {
		  Graphics2D g2d = (Graphics2D)g;
		  g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		  double zoomFactor = getZoomFactor();
		  AffineTransform old=g2d.getTransform();
		  g2d.scale(zoomFactor, zoomFactor);
		  super.paint(g2d, allocation);
		  g2d.setTransform(old);
	  }

	  public float getMinimumSpan(int axis) 
	  {
		  float f = super.getMinimumSpan(axis);
		  f *= getZoomFactor();
		  return f;
	  }

	  public float getMaximumSpan(int axis) 
	  {
		  float f = super.getMaximumSpan(axis);
		  f *= getZoomFactor();
		  return f;
	  }

	  public float getPreferredSpan(int axis) 
	  {
		  float f = super.getPreferredSpan(axis);
		  f *= getZoomFactor();
		  return f;
	  }

	  protected void layout(int width, int height) 
	  {
		  super.layout(new Double(width / getZoomFactor()).intValue(), new Double(height * getZoomFactor()).intValue());
	  }
}

