/******************************************************************************
 * Copyright (C) 2008 Low Heng Sin  All Rights Reserved.                      *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/

package org.adempiere.webui.part;

import java.util.List;

import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.component.Tabpanels;
import org.adempiere.webui.component.Tabs;
import org.adempiere.webui.component.ToolBar;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.openXpertya.model.MSysConfig;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Style;

/**
 * 
 * @author Low Heng Sin
 *
 */
public class WindowContainer extends AbstractUIPart implements EventListener // <- dREHER implementa listeners
{
	private static final int MAX_TITLE_LENGTH = 30;
    
    private Tabbox           tabbox;

    // dREHER
    // Mejoras en manejo de teclas y pestañas abiertas, basadas en iDempiere (www.idempiere.org)
    
    private ToolBar toolbar;
    private ToolBarButton tabListBtn;
    public static final String ON_MOBILE_SET_SELECTED_TAB = "onMobileSetSelectedTab";
	private static final String ON_AFTER_TAB_CLOSE = "onAfterTabClose";
	private static final String ON_DEFER_SET_SELECTED_TAB = "onDeferSetSelectedTab";
	public static final String ON_WINDOW_CONTAINER_SELECTION_CHANGED_EVENT = "onWindowContainerSelectionChanged";
	public static final String DEFER_SET_SELECTED_TAB = "deferSetSelectedTab";

	private static final String ZK_DESKTOP_SHOW_HOME_BUTTON = "ZK_DESKTOP_SHOW_HOME_BUTTON";

	private static final String ZK_DESKTOP_SHOW_TAB_LIST_BUTTON = "ZK_DESKTOP_SHOW_TAB_LIST_BUTTON";

	private static final String ZK_DESKTOP_TAB_AUTO_SHRINK_TO_FIT = "ZK_DESKTOP_TAB_AUTO_SHRINK_TO_FIT";

	private static final String OPTION_CLOSE = "Close";
	private static final String OPTION_CLOSE_OTHER_WINDOWS = "CloseOtherWindows";
	private static final String OPTION_CLOSE_WINDOWS_TO_THE_LEFT = "CloseWindowsToTheLeft";
	private static final String OPTION_CLOSE_WINDOWS_TO_THE_RIGHT = "CloseWindowsToTheRight";
	private static final String OPTION_CLOSE_ALL_WINDOWS = "CloseAllWindows";
    
    public WindowContainer()
    {
    }
    
    /**
     * 
     * @param tb
     * @return WindowContainer
     */
    public static WindowContainer createFrom(Tabbox tb) 
    {
    	WindowContainer wc = new WindowContainer();
    	wc.tabbox = tb;
    	
    	return wc;
    }

    protected Component doCreatePart(Component parent)
    {
    	
    	if (isDesktopAutoShrinkTabTitle())
    	{
    		//style to enable auto shrink tab title and hide tab scroll buttons
    		Style style = new Style();
    		style.setContent(".desktop-tabbox > .z-tabs > .z-tabs-content {display:flex;width: auto !important;} "
    				+ ".desktop-tabbox > .z-tabs > .z-tabs-content > .z-tab {text-overflow: ellipsis;flex-shrink: 1;flex-basis: auto;min-width: 70px;} "
    				+ ".desktop-tabbox.z-tabbox > .z-tabbox-icon.z-tabbox-left-scroll,"
    				+ ".desktop-tabbox.z-tabbox > .z-tabbox-icon.z-tabbox-right-scroll {color:transparent;border:none;background:none;width:0px;} "
    				+ ".desktop-tabbox.z-tabbox-scroll > .z-tabs {margin:0px;} ");
    		parent.getParent().getParent().appendChild(style);    		
    	}
    	
        tabbox = new Tabbox();
        
        Tabpanels tabpanels = new Tabpanels();
        Tabs tabs = new Tabs();

        tabbox.appendChild(tabs);
        tabbox.appendChild(tabpanels);
        tabbox.setWidth("100%");
        tabbox.setHeight("100%");
        
        if (parent != null)
        	tabbox.setParent(parent);
        else
        	tabbox.setPage(page);
        
        // dREHER para mostrar home y tabs abiertos -
        tabbox.addEventListener(ON_DEFER_SET_SELECTED_TAB, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				Tab tab = (Tab) event.getData();
				if (tab != null)
					setSelectedTab(tab);
				
				// dREHER
				if (isShowTabList()) {
	        		updateTabListButton();
	        	}
				
			}
		});
        
        // dREHER cuando cierro los tabs, actualizar lista de ventanas abiertas
        tabbox.addEventListener(ON_AFTER_TAB_CLOSE, new EventListener() { // 
        	@Override
        	public void onEvent(Event event) throws Exception {
        		
        		if (isMobile()) {
        			updateMobileTabState(tabbox.getSelectedTab());	        	
        		}
        		if (isShowTabList()) {
        			updateTabListButton();
        		}
        	}
        });
        
        
        toolbar = new ToolBar();
        toolbar.setSclass("window-container-toolbar");
        tabbox.appendChild(toolbar);
        
        if (isShowHomeButton())
        {
        	
        	System.out.println("Mostrar home button!");
        	
        	ToolBarButton homeButton = new ToolBarButton();
        	/*
        	if (ThemeManager.isUseFontIconForImage())
        		homeButton.setIconSclass("z-icon-Home");
        	else
        		homeButton.setImage(ThemeManager.getThemeResource("images/Home16.png"));
        	*/
        	homeButton.setImage("/images/Home24.png");
        	homeButton.setSclass("window-container-toolbar-btn");
        	homeButton.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Home")));
        	homeButton.addEventListener(Events.ON_CLICK, evt -> setSelectedTab(tabbox.getTabpanel(0).getLinkedTab()));
        	toolbar.appendChild(homeButton);
        }
        
        if (isShowTabList())
        {        	        	
        	tabListBtn = new ToolBarButton();
        	
        	System.out.println("Mostrar Tabs List!");
        	/*
        	if (ThemeManager.isUseFontIconForImage()) 
        	{
        		tabListBtn.setIconSclass("z-icon-Expand");
        	} 
        	else 
        	{
        		tabListBtn.setImage(ThemeManager.getThemeResource("images/expand-header.png"));
        	}
        	*/
        	
        	tabListBtn.setImage("/images/Detail24.png"); // dREHER
        	tabListBtn.setLabel("Ventanas");
        	tabListBtn.setSclass("window-container-toolbar-btn tab-list");
        	tabListBtn.setTooltiptext("Mostrar Todo " + "   Alt+W");
        	
        	// dREHER 
        	// java 13 tabListBtn.addEventListener(Events.ON_CLICK, evt -> showTabList());
        	tabListBtn.addEventListener(Events.ON_CLICK, new EventListener() {
        	    @Override
        	    public void onEvent(Event evt) {
        	        showTabList();
        	    }
        	});
        	
        	tabListBtn.setVisible(false);
        	toolbar.appendChild(tabListBtn);        	        	 
        }
        
        // dREHER
        // SessionManager.getSessionApplication().getKeylistener().addEventListener(Events.ON_CTRL_KEY, (KeyEvent e) -> onCtrlKey(e));
        // Java 13

        if(SessionManager.getSessionApplication()!=null && SessionManager.getSessionApplication().getKeylistener()!=null)
        	SessionManager.getSessionApplication().getKeylistener().addEventListener(Events.ON_CTRL_KEY, new EventListener() {
        		@Override
        		public void onEvent(Event e) throws Exception {
        			onCtrlKey(e);
        		}
        	});
                
        return tabbox;
    }
    
    // dREHER -------------------------------------------------------------------- inicio de metodos  basados en iDempiere (www.idempiere.org)
    private void onCtrlKey(Event ev) {
    	KeyEvent e = (KeyEvent)ev;
    	//alt+w
    	System.out.println("onCtrlKey: " + e);
		if (e.isAltKey() && !e.isCtrlKey() && !e.isShiftKey()) {
			if (e.getKeyCode() == 87) {
				
				System.out.println("ALT+W pressed");
				
				if (tabListBtn != null && tabListBtn.isVisible()) {
					System.out.println("ALT+W Dispara evento...");
					Events.postEvent(Events.ON_CLICK, tabListBtn, null);
				}
			}
		}if(e.isAltKey()) {
			System.out.println("Tecla ALT. KeyCode=" + e.getKeyCode());
			if(e.getKeyCode()==37 || e.getKeyCode() == KeyEvent.PAGE_UP
				|| e.getKeyCode() == KeyEvent.UP || e.getKeyCode() == 109
				|| e.getKeyCode() == 65) { // Izq / menos
				if(tabbox.getSelectedTab() != null && tabbox.getSelectedTab().getPreviousSibling() != null) {
					tabbox.setSelectedTab((org.zkoss.zul.Tab)tabbox.getSelectedTab().getPreviousSibling());
				}
			}else if(e.getKeyCode()==39 || e.getKeyCode() == KeyEvent.PAGE_DOWN  
					|| e.getKeyCode() == KeyEvent.DOWN
					|| e.getKeyCode() == 80) { // Der
				if(tabbox.getSelectedTab() != null && tabbox.getSelectedTab().getNextSibling() != null) {
					tabbox.setSelectedTab((org.zkoss.zul.Tab)tabbox.getSelectedTab().getNextSibling());
				}
			}else if(e.getKeyCode()==72 || e.getKeyCode()==36) { // H o Inicio
				setSelectedTab(tabbox.getTabpanel(0).getLinkedTab());
			}
		}else {
			System.out.println("Toco otra tecla (NO alt/shift/ctrl), voy a onEvent..." + e);
		}
	}
    
    @Override
	public void onEvent(Event event) throws Exception {
		
		System.out.println("WindowContainer.onEvent=" + event);
		
		if (event.getTarget() == tabbox && "onPageDetached".equals(event.getName())) {
			try {
				SessionManager.getSessionApplication().getKeylistener().removeEventListener(Events.ON_CTRL_KEY, this);
			} catch (Exception e) {}
		}else if (event.getTarget() == tabbox && "onPageAttached".equals(event.getName())) {
			try {
				SessionManager.getSessionApplication().getKeylistener().addEventListener(Events.ON_CTRL_KEY, this);
			} catch (Exception e) {}
		}else if (event instanceof OpenEvent && event.getTarget() instanceof Menupopup) {
			if (((OpenEvent)event).isOpen()) {
				Menupopup popup = (Menupopup) event.getTarget();
				List<Component> tabs = tabbox.getTabs().getChildren();
				int tabsSize = tabs.size();
				int currentTabIdx = -1;
				if (popup.getAttribute("tab") != null) {
					Tab currentTab = (Tab) popup.getAttribute("tab");
					for ( int i = tabsSize - 1; i > 0; i-- ) {
						Tab tab = ((Tab)tabs.get(i));
						if (currentTab.equals(tab)) {
							currentTabIdx = i;
							break;
						}
					}
				}
				if (currentTabIdx > 0) {
					List<Component> items = popup.getChildren();
					for (Component item : items) {
						if (item instanceof Menuitem) {
							String option = (String) item.getAttribute("option");
							boolean visible =
									   (OPTION_CLOSE.equals(option))
									|| (tabsSize > 2 && (OPTION_CLOSE_OTHER_WINDOWS.equals(option) || OPTION_CLOSE_ALL_WINDOWS.equals(option)))
									|| (currentTabIdx < tabsSize - 1 && OPTION_CLOSE_WINDOWS_TO_THE_RIGHT.equals(option))
									|| (currentTabIdx > 1 && OPTION_CLOSE_WINDOWS_TO_THE_LEFT.equals(option));
							item.setVisible(visible);
						}
					}
				}
			}
		}else if (Events.ON_CTRL_KEY.equals(event.getName())) {
			
			System.out.println("onEvent.Evento onCtrlKey...");
			
			KeyEvent keyEvent = (KeyEvent) event;
			if (keyEvent.isAltKey() && keyEvent.getKeyCode() == KeyEvent.PAGE_DOWN
					&& tabbox.getSelectedTab() != null && tabbox.getSelectedTab().getNextSibling() != null) {
				tabbox.setSelectedTab((org.zkoss.zul.Tab)tabbox.getSelectedTab().getNextSibling());
				keyEvent.stopPropagation();
			}else if (keyEvent.isAltKey() && keyEvent.getKeyCode() == KeyEvent.PAGE_UP
					&& tabbox.getSelectedTab() != null && tabbox.getSelectedTab().getPreviousSibling() != null) {
				tabbox.setSelectedTab((org.zkoss.zul.Tab)tabbox.getSelectedTab().getPreviousSibling());
				keyEvent.stopPropagation();
			}
		}
		
	}
    
    private void updateTabListButton() {
		if (isShowTabList() && tabListBtn != null) {
			int cnt = tabbox.getTabs().getChildren().size()-1;
			if (cnt > 0) {
				tabListBtn.setLabel(Integer.toString(cnt));
				tabListBtn.setVisible(true);
			} else {
				tabListBtn.setLabel("");
				tabListBtn.setVisible(false);
			}
		}
	}
    
    private boolean isMobile() {
    	return false;
    	
		// dREHER TODO: return ClientInfo.isMobile();
	}
    
	private boolean isShowHomeButton() {
		return isMobile() || isDesktopShowHomeButton();
	}

	private boolean isDesktopShowHomeButton() {
		// dREHER TODO return MSysConfig.getBooleanValue(ZK_DESKTOP_SHOW_HOME_BUTTON, true, Env.getAD_Client_ID(Env.getCtx()));
		return true;
	}

	private boolean isShowTabList() {
		// dREHER TODO return isMobile() || isDesktopAutoShrinkTabTitle() || isDesktopShowTabList();
		return true;
	}

	private boolean isDesktopShowTabList() {
		// dREHER TODO return MSysConfig.getBooleanValue(ZK_DESKTOP_SHOW_TAB_LIST_BUTTON, true, Env.getAD_Client_ID(Env.getCtx()));}
		return true;
	}

	private boolean isDesktopAutoShrinkTabTitle() {
		// dREHER TODO return MSysConfig.getBooleanValue(ZK_DESKTOP_TAB_AUTO_SHRINK_TO_FIT, false, Env.getAD_Client_ID(Env.getCtx()));
		return false;
	}

	private void showTabList() {
		
		if(tabbox == null) {
			return;
		}
		org.zkoss.zul.Tabs tabs = tabbox.getTabs();
		if(tabs==null) {
			return;
		}
		
		List<Component> list = tabs.getChildren();
		Menupopup popup = new Menupopup();
		for(int i = 1; i < list.size(); i++) {
			Tab tab = (Tab) list.get(i);
			Menuitem item = new Menuitem(tab.getLabel().endsWith("...") && !Util.isEmpty(tab.getTooltiptext(), true) ? tab.getTooltiptext() : tab.getLabel());
			item.setValue(Integer.toString(i));
			if (!Util.isEmpty(tab.getTooltiptext(), true) && !(item.getLabel().equals(tab.getTooltiptext())))
				item.setTooltiptext(tab.getTooltiptext());
			if (i == tabbox.getSelectedIndex())
				item.setSclass("selected");
			popup.appendChild(item);
			item.addEventListener(Events.ON_CLICK, evt -> {
				Menuitem t = (Menuitem) evt.getTarget();
				String s = t.getValue();
				Integer ti = Integer.parseInt(s);
				setSelectedTab(tabbox.getTabpanel(ti.intValue()).getLinkedTab());				
			});
		}
		popup.setPage(tabbox.getPage());
		popup.open(tabListBtn, "after_start");
		
		/*
		 * dREHER no soportado por la version de zk Libertya...
		popup.addEventListener(Events.ON_CTRL_KEY, new EventListener() {
			@Override
			public void onEvent(Event e) throws Exception {
				System.out.println("Evento: " + e);
				onCtrlKey(e);
			}
		});
		*/
	}
	
	/**
     * 
     * @param tab
     */
    public void setSelectedTab(org.zkoss.zul.Tab tab)
    {
    	if (isMobile())
    		updateMobileTabState(tab);
    	tabbox.setSelectedTab(tab); 
    }

	private void updateMobileTabState(org.zkoss.zul.Tab tab) {
		if (isMobile() && tabListBtn != null)
    	{
    		List<Component> tabs = tabbox.getTabs().getChildren();
    		for(Component c: tabs) {
    			if (c instanceof Tab) {
    				Tab t = (Tab) c;
    				t.setVisible(t == tab);
    				t.getLinkedPanel().setVisible(t == tab);
    				if (t.isVisible()) {
    					Events.postEvent(ON_MOBILE_SET_SELECTED_TAB, t.getLinkedPanel(), null);
    				}
    			}
    		}    		
    	}
	}
	
	protected void closeTabs(Tab tab, int start, int end, int focus) {
    	List<Component> tabs = tabbox.getTabs().getChildren();
    	if (end == -1) {
    		end = tabs.size() - 1;
    	}
    	for (int i = end; i >= start; i--) {
    		((Tab)tabs.get( i )).setSelected(false);
    		((Tab)tabs.get( i )).onClose();
    	}
    	tabbox.setSelectedIndex(focus);
    	
    	Events.postEvent(ON_AFTER_TAB_CLOSE, tabbox, null);
    }
    
    // dREHER -------------------------------------------------------------------- fin de metodos basados en iDempiere
    
    /**
     * 
     * @param comp
     * @param title
     * @param closeable
     */
    public void addWindow(Component comp, String title, boolean closeable)
    {
        addWindow(comp, title, closeable, true);
    }
    
    /**
     * 
     * @param comp
     * @param title
     * @param closeable
     * @param enable
     */
    public void addWindow(Component comp, String title, boolean closeable, boolean enable) 
    {
    	insertBefore(null, comp, title, closeable, enable);
    }
    
    /**
     * 
     * @param refTab
     * @param comp
     * @param title
     * @param closeable
     * @param enable
     */
    public void insertBefore(Tab refTab, Component comp, String title, boolean closeable, boolean enable)
    {
        Tab tab = new Tab();
        title = title.replaceAll("[&]", "");
        if (title.length() <= MAX_TITLE_LENGTH) 
        {
        	tab.setLabel(title);
        }
        else
        {
        	tab.setTooltiptext(title);
        	title = title.substring(0, 27) + "...";
        	tab.setLabel(title);
        }
        tab.setClosable(closeable);
        
        // fix scroll position lost coming back into a grid view tab
        tab.addEventListener(Events.ON_SELECT, new EventListener() {
			public void onEvent(Event event) throws Exception {
				Tab tab = (Tab)event.getTarget();
				org.zkoss.zul.Tabpanel panel = tab.getLinkedPanel();
				Component component = panel.getFirstChild();
				if (component != null && component.getAttribute(ITabOnSelectHandler.ATTRIBUTE_KEY) instanceof ITabOnSelectHandler)
				{
					ITabOnSelectHandler handler = (ITabOnSelectHandler) component.getAttribute(ITabOnSelectHandler.ATTRIBUTE_KEY);
					handler.onSelect();
				}
			}
		});

        Tabpanel tabpanel = null;
        if (comp instanceof Tabpanel) {
        	tabpanel = (Tabpanel) comp;
        } else {
        	tabpanel = new Tabpanel();
        	tabpanel.appendChild(comp);
        }
        tabpanel.setZclass("desktop-tabpanel");
        tabpanel.setHeight("100%");
        tabpanel.setWidth("100%");
        tabpanel.setStyle("position: relative;");
        
        if (refTab == null)  
        {
        	tabbox.getTabs().appendChild(tab);
        	tabbox.getTabpanels().appendChild(tabpanel);
        }
        else
        {
        	org.zkoss.zul.Tabpanel refpanel = refTab.getLinkedPanel();
        	tabbox.getTabs().insertBefore(tab, refTab);
        	tabbox.getTabpanels().insertBefore(tabpanel, refpanel);
        }

        if (enable)
        	setSelectedTab(tab);
        
        // dREHER
        if (isShowTabList())
        	updateTabListButton();
        
    }
    
    /**
     * 
     * @param refTab
     * @param comp
     * @param title
     * @param closeable
     * @param enable
     */
    public void insertAfter(Tab refTab, Component comp, String title, boolean closeable, boolean enable)
    {
    	if (refTab == null)
    		addWindow(comp, title, closeable, enable);
    	else
    		insertBefore((Tab)refTab.getNextSibling(), comp, title, closeable, enable);
    }

    /**
     * 
     * @param tab
     */
    public void setSelectedTab(Tab tab)
    {
    	tabbox.setSelectedTab(tab);
    }

    /**
     * 
     * @return true if successfully close the active window
     */
    public boolean closeActiveWindow()
    {
    	Tab tab = (Tab) tabbox.getSelectedTab();
    	tabbox.getSelectedTab().onClose();
    	
    	updateTabListButton();
    	
    	if (tab.getParent() == null)
    		return true;
    	else
    		return false;
    }
    
    /**
     * 
     * @return Tab
     */
    public Tab getSelectedTab() {
    	return (Tab) tabbox.getSelectedTab();
    }
    
    // Elaine 2008/07/21
    /**
     * @param tabNo
     * @param title
     * @param tooltip 
     */
    public void setTabTitle(int tabNo, String title, String tooltip)
    {
    	org.zkoss.zul.Tabs tabs = tabbox.getTabs();
    	Tab tab = (Tab) tabs.getChildren().get(tabNo);
    	tab.setLabel(title);
    	tab.setTooltiptext(tooltip);
    }
    //

	/**
	 * @return Tabbox
	 */
	public Tabbox getComponent() {
		return tabbox;
	}
	
}
