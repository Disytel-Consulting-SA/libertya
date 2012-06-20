package nickyb.fqb.ext;

import it.frb.DataEngine;
import it.frb.JPreviewPanel;
import it.frb.PrintThread;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.print.PageFormat;

import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import nickyb.fqb.util.ImageStore;
import nickyb.fqb.util.UIUtilities;

public class ReportPreview extends JFrame
{
    private Thread engineThread;
    private DataEngine engine;
    private BlankPage blank;
    
    public ReportPreview()
    {
        super("Preview - " + ViewBuildReport.DEFAULT_TITLE);
        
        Dimension screen = this.getToolkit().getScreenSize();
		setSize(screen.width-20,screen.height-20);
		
		UIUtilities.centerOnScreen(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
        
		JScrollPane scroll = new JScrollPane(blank = new BlankPage());
		scroll.getVerticalScrollBar().setUnitIncrement(25);
        getContentPane().add(scroll);
    }
    
	public ReportPreview(ViewBuildReport builder)
	{
		this();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		toolbar.add(new ActionPrevPage());
		toolbar.add(new ActionNextPage());
		toolbar.add(new JToolBar.Separator(new Dimension(10,10)));
		toolbar.add(new ActionPrint());
		
		getContentPane().add(toolbar,BorderLayout.NORTH);
		
		WindowListener wl = new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
			    ReportPreview.this.engineThread = null;
			    ReportPreview.this.engine = null;
				ReportPreview.this.dispose();
			}
		};
		addWindowListener(wl);
		
        try
        {
            engine = new DataEngine(builder,builder);
            engine.setVisibleRowsCountFrame(false);
            
	        engineThread = new Thread(engine){
	            public void run()
	            {
	                super.run();
	                ReportPreview.this.outputIsReady();
	            }
	        };
		    engineThread.start();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
	}
	
	private void outputIsReady()
	{
	    blank.viewPageContent();
	}

// Page class	
	private class BlankPage extends JPanel
	{
	    private Image offscreen;
	    private Graphics offgraphics;
	    
	    private Dimension offscreensize;
	    private Dimension pagesize;
	    private Rectangle pagearea;
	    
	    private BlankPage()
	    {
	        super(null);
	        
            PageFormat page = new PageFormat();
            
            double wp = page.getWidth()/72;
            double ws = this.getToolkit().getScreenSize().getWidth()/this.getToolkit().getScreenResolution();

            double hp = page.getHeight()/72;
            double hs = this.getToolkit().getScreenSize().getHeight()/this.getToolkit().getScreenResolution();
            
            double w = (this.getToolkit().getScreenSize().getWidth()/100)*((wp*100)/ws);
            double h = (this.getToolkit().getScreenSize().getHeight()/100)*((hp*100)/hs);
            pagesize = new Dimension((int)w,(int)h);
            
            pagearea = new Rectangle();
            pagearea.x = (int)((pagesize.width/100)*((page.getImageableX()*100)/page.getWidth()));
            pagearea.y = (int)((pagesize.height/100)*((page.getImageableY()*100)/page.getHeight()));
            pagearea.width = (int)((pagesize.width/100)*((page.getImageableWidth()*100)/page.getWidth()));
            pagearea.height = (int)((pagesize.height/100)*((page.getImageableHeight()*100)/page.getHeight()));
	    }
	    
	    private void viewPageContent()
	    {
	        JPreviewPanel preview = ReportPreview.this.engine.getOutputPanel();
			if(preview==null) return;
//	        preview.setLocation(pagearea.x,pagearea.y);
//	        preview.setSize(pagearea.getSize());
/*	        
	        Rectangle bounds = new Rectangle();
	        
	        bounds.width = preview.getWidthDataPanel();
	        bounds.height = preview.getHeightDataPanel();
	        
	        if(bounds.width > pagesize.width)
	            bounds.width = pagesize.width;
	        
	        bounds.x = (pagesize.width/2)-(bounds.width/2);
	        bounds.y = (pagesize.height/2)-(bounds.height/2);

	        preview.setBounds(bounds);
*/	        
	        add(preview);
	        repaint();
	    }
	 
	    public boolean isDoubleBuffered()
	    {
	        return true;
	    }
	    
	    public boolean isLightweight()
	    {
	        return false;
	    }
	    
        protected void paintComponent(Graphics g)
        {
	        g.setColor(Color.darkGray);
	        g.fillRect(0,0,offscreensize.width,offscreensize.height);

	        g.translate(10,10);
	        g.setColor(Color.lightGray);
	        g.drawRect(0,0,pagesize.width,pagesize.height);
	        
	        g.setColor(Color.blue);
//	        g.drawRect(pagearea.x-1,pagearea.y-1,pagearea.width+2,pagearea.height+2);
	        
	        g.drawLine(pagearea.x,-10,pagearea.x,10);
	        g.drawLine(pagearea.x+pagearea.width,-10,pagearea.x+pagearea.width,10);
	        
	        g.drawLine(-10,pagearea.y,10,pagearea.y);
	        g.drawLine(-10,pagearea.y+pagearea.height,10,pagearea.y+pagearea.height);
	    }
	    
	    //override the rendering method 
	    public void paint(Graphics g)
	    {
	        Dimension d = getSize();
	        //offscreen needs to be updated if not initialized or 
	        // because of size change
	        if ((offscreen == null) || 
	            (d.width != offscreensize.width) || 
	            (d.height != offscreensize.height))
	        {
	            //Creates an off-screen drawable
	            //image to be used for double buffering.
	            offscreen = createImage(d.width, d.height);
	            offscreensize = d;
	            if (offgraphics != null)
	            {
	                //release resource
	                offgraphics.dispose();
	            }
	            offgraphics = offscreen.getGraphics();
	            offgraphics.setFont(getFont());
	        }
	        //render in the offscreen buffer !
	        super.paint(offgraphics);
	 
	        //render offscreen in the front 
	        g.drawImage(offscreen, 0, 0, null);
	    }
        
	    public Dimension getPreferredSize()
	    {
	        return new Dimension(pagesize.width+20,pagesize.height+20);
	    }
	}

// Actions
	private class ActionNextPage extends AbstractAction
	{
		ActionNextPage()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("report.page.next"));
			this.putValue(SHORT_DESCRIPTION, "next page");
		}
		
		public void actionPerformed(ActionEvent ae)
		{
		    try
		    {
		        ReportPreview.this.engine.getOutputPanel().nextPage();
		        repaint();
		    }
		    catch(SQLException sqle)
		    {
		        sqle.printStackTrace();
		    }
		}
	}
	
	private class ActionPrevPage extends AbstractAction
	{
		ActionPrevPage()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("report.page.prev"));
			this.putValue(SHORT_DESCRIPTION, "previous page");
		}
		
		public void actionPerformed(ActionEvent ae)
		{
		    try
		    {
		        ReportPreview.this.engine.getOutputPanel().prevPage();
		        repaint();
		    }
		    catch(SQLException sqle)
		    {
		        sqle.printStackTrace();
		    }
		}
	}
	
	private class ActionPrint extends AbstractAction
	{
		ActionPrint()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("report.page.print"));
			this.putValue(SHORT_DESCRIPTION, "print");
		}
		
		public void actionPerformed(ActionEvent ae)
		{
		    PrintThread printJPrew = new PrintThread(ReportPreview.this, ReportPreview.this.engine.getOutputPanel(), 1);
		    new Thread(printJPrew).start();		    
		    
//		    PrinterJob job = PrinterJob.getPrinterJob();
//		    job.setPrintable(ReportPreview.this.engine.getOutputPanel());
//		    
//		    Rectangle bounds = ReportPreview.this.engine.getOutputPanel().getBounds();
//		    job.defaultPage().getPaper().setSize(bounds.getWidth(),bounds.getHeight());
//		    
//		    try
//            {
//                job.print();
//            }
//            catch (PrinterException e)
//            {
//                e.printStackTrace();
//            }
		}
	}
	
	public static void main(String[] args)
    {
        new ReportPreview().setVisible(true);
    }
}
