/*
 * Copyright (C) 2004 Nicky BRAMANTE
 * 
 * This file is part of FreeQueryBuilder
 * 
 * FreeQueryBuilder is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * Send questions or suggestions to nickyb@interfree.it
 */

package nickyb.fqb;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;

import java.util.Vector;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.openXpertya.util.Env;

public class QueryDesktop extends JDesktopPane
{
	private static int FRAME_OFFSET = 50;
	private InternalDesktopManager manager;
	
	private Point nextGoodPoint = new Point(10,10);
	private Point maxCorner = new Point(0,0); 

	private DesktopRelation joining;
	
	Vector joins;
	QueryBuilder builder;
	
	QueryDesktop(QueryBuilder builder)
	{
		this.builder = builder;
		joins = new Vector();
		
		setDesktopManager(manager = new InternalDesktopManager());
		setDragMode(OUTLINE_DRAG_MODE);
	}
	
	void add(DesktopEntity item)
	{
		if(item.getWidth() + nextGoodPoint.x > this.getBounds().getWidth())
		{
			nextGoodPoint.setLocation(10, maxCorner.y + FRAME_OFFSET);
		}
		item.setLocation(nextGoodPoint);
		item.setVisible(true);
		
		super.add(item);
		
		checkDesktopSize();
		builder.syntax.refresh();
	}
	
	DesktopEntity find(QueryTokens.Table table)
	{
		JInternalFrame allFrames[] = this.getAllFramesInLayer(QueryDesktop.PALETTE_LAYER.intValue());

		for(int i=0; i<allFrames.length; i++)
		{
			DesktopEntity item = (DesktopEntity)allFrames[i];
			//begin vpj-cd e-evolution 05/16/2005
			org.openXpertya.model.M_Table t = org.openXpertya.model.M_Table.get(org.openXpertya.util.Env.getCtx(),table.getName());
			String alias = org.openXpertya.util.Msg.getMsg(Env.getCtx(), t.getName())+ " [" + table.getName() + "]" ;
			//if(table.getAlias().equals(item.getHeader()))
			System.out.println("Alias:"+ alias + "Heder:"+ item.getHeader());
			if(alias.equals(item.getHeader()))
			//end vpj-cd e-evolution 05/16/2005
			{
				return item;
			}
		}
			
		return null;
	}
	
	DesktopEntity find(String schema, String table)
	{
		return find(new QueryTokens.Table(schema,table));
	}
	
	private void checkDesktopSize()
	{
		if(getParent()!=null && isVisible())
			manager.resizeDesktop();
	}
	
	public DesktopEntity[] getAllEntities()
	{
		JInternalFrame[] internalframes = this.getAllFramesInLayer(QueryDesktop.PALETTE_LAYER.intValue());
		DesktopEntity[] entities = new DesktopEntity[internalframes.length];
		
		System.arraycopy(internalframes,0,entities,0,internalframes.length);
		
		return entities;
	}
	
	public int getEntityCount()
	{
		return this.getAllFramesInLayer(QueryDesktop.PALETTE_LAYER.intValue()).length;
	}
	
	void addRelation(DesktopRelation relation)
	{
		if(relation==null || relation.leftEntity == null || relation.leftField == null
		|| relation.rightEntity == null || relation.rightField == null) return;
		
		joins.addElement(relation);
		
		this.add(relation);
		this.repaint();
			
		builder.syntax.refresh();
	}
	
	void removeRelation(DesktopRelation relation)
	{
		relation.rightEntity.unjoined(relation.rightField);
		relation.leftEntity.unjoined(relation.leftField);
		
		joins.removeElement(relation);
		
		this.remove(relation);
		this.repaint();
		
		builder.syntax.refresh();
	}
	
	void removeAllRelation(DesktopEntity table)
	{
		Vector validJoins = new Vector();
		
		for(int i=0; i<joins.size(); i++)
		{
			boolean valid = false;

			DesktopRelation join = (DesktopRelation)joins.elementAt(i);
			if(join.leftEntity == table)
			{
				join.rightEntity.unjoined(join.rightField);
			}
			else if(join.rightEntity == table)
			{
				join.leftEntity.unjoined(join.leftField);
			}
			else
			{
				valid = true;
				validJoins.addElement(join);
			}

			if(!valid) this.remove(join);
		}
		
		joins = validJoins;
	}

	void resetJoin()
	{
		joining = null;
	}
	
	void join(DesktopEntity entity, DesktopEntity.Field field)
	{
		if(joining==null)
		{
			joining = new DesktopRelation(this);
			joining.leftEntity = entity;
			joining.leftField = field;
			
			if(builder.isJoinMode())
				joining.leftField.setBackground(DesktopEntity.BGCOLOR_START_JOIN);
		}
		else if(entity==joining.leftEntity)
		{
			joining.leftField.setBackground(DesktopEntity.BGCOLOR_DEFAULT);
			if(joining.leftEntity.joinedFields.contains(joining.leftField))
			{
				if(builder.isJoinMode())
					joining.leftField.setBackground(DesktopEntity.BGCOLOR_JOINED);
			}
			
			if(field != joining.leftField)
			{
				joining.leftField = field;
				if(builder.isJoinMode())
					joining.leftField.setBackground(DesktopEntity.BGCOLOR_START_JOIN);
			}
			else
			{
				joining = null;
			}
		}
		else
		{
			joining.rightEntity = entity;
			joining.rightField = field;
			
			joining.leftEntity.joined(joining.leftField);
			joining.rightEntity.joined(joining.rightField);
			
			this.addRelation(joining);
			
			joining = null;
		}
	}

	void workModeChanged()
	{
		JInternalFrame allFrames[] = this.getAllFramesInLayer(QueryDesktop.PALETTE_LAYER.intValue());
			
		for(int i=0,j=0; i<allFrames.length; i++)
		{
			((DesktopEntity)allFrames[i]).workModeChanged();
		}
		
		joining = null;
	}

	protected void paintChildren(Graphics g)
	{
		for(int i=0; i<joins.size(); i++)
		{
			((DesktopRelation)joins.elementAt(i)).drawIt(g);
		}
		
		super.paintChildren(g);
	}
	
	void closeAllFrames()
	{
		JInternalFrame allFrames[] = this.getAllFramesInLayer(QueryDesktop.PALETTE_LAYER.intValue());
			
		for(int i=0,j=0; i<allFrames.length; i++)
		{
			allFrames[i].doDefaultCloseAction();
		}
	}
	
	private void setAllSize(Dimension d)
	{
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);
	}
	
	private class InternalDesktopManager extends DefaultDesktopManager
	{
		public void closeFrame(JInternalFrame f)
		{
			super.closeFrame(f);
			resizeDesktop();
			
			QueryDesktop.this.builder.syntax.refresh();
		}
		
		public void endResizingFrame(JComponent f)
		{
			super.endResizingFrame(f);
			resizeDesktop();
		}

		public void endDraggingFrame(JComponent f)
		{
			super.endDraggingFrame(f);
			resizeDesktop();
		}

		private Insets getScrollPaneInsets()
		{
			JScrollPane scrollPane = getScrollPane();
			if(scrollPane == null || scrollPane.getBorder() == null)
				return new Insets(0,0,0,0);
			else
				return scrollPane.getBorder().getBorderInsets(scrollPane);
		}
		
		private JScrollPane getScrollPane()
		{
			if(QueryDesktop.this.getParent() instanceof JViewport)
			{
				JViewport viewPort = (JViewport)QueryDesktop.this.getParent();
				if(viewPort.getParent() instanceof JScrollPane)
					return (JScrollPane)viewPort.getParent();
			}
			
			return null;
		}
		
		private void resizeDesktop()
		{
			maxCorner.setLocation(0,0);
			nextGoodPoint.setLocation(10,10);

			JInternalFrame allFrames[] = QueryDesktop.this.getAllFramesInLayer(QueryDesktop.PALETTE_LAYER.intValue());
			for(int i = 0; i < allFrames.length; i++)
			{
				Point corner = new Point(allFrames[i].getX()+allFrames[i].getWidth(),
										 allFrames[i].getY()+allFrames[i].getHeight());

				if(corner.x > maxCorner.x) maxCorner.x = corner.x;
				if(corner.y > maxCorner.y) maxCorner.y = corner.y;
				
				if(allFrames[i].getY() >= nextGoodPoint.y)
				{
					nextGoodPoint.x = corner.x + FRAME_OFFSET;
					nextGoodPoint.y = allFrames[i].getY();
				}
			}
			
			JScrollPane scrollPane = this.getScrollPane();
			Insets scrollInsets = this.getScrollPaneInsets();

			if(scrollPane != null)
			{
				Dimension dimView = scrollPane.getVisibleRect().getSize();
				if(scrollPane.getBorder() != null)
				{
					dimView.setSize(dimView.getWidth() - scrollInsets.left - scrollInsets.right,
									dimView.getHeight() - scrollInsets.top - scrollInsets.bottom);
				}
				
				Dimension dimDesk = new Dimension(maxCorner.x, maxCorner.y);

				if(dimDesk.getWidth() <= dimView.getWidth()) dimDesk.width = ((int)dimView.getWidth()) - 20;
				if(dimDesk.getHeight() <= dimView.getHeight()) dimDesk.height = ((int)dimView.getHeight()) - 20;
				
				QueryDesktop.this.setAllSize(dimDesk);
				QueryDesktop.this.repaint();
				
				scrollPane.validate();
			}
		}
	}
}
