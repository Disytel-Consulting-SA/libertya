var isIE = (navigator.userAgent.indexOf("MSIE") != -1);
						
/****************************************************************************
 * Compiere (c) Jorg Janke - All rights reseverd
 * $Id: menu.js,v 1.4 2004/11/26 20:47:36 jjanke Exp $
 *
 * Menu - show/hide sub-menues - tested in IE 6 end Mozila 1.7
 ***************************************************************************/
function changeMenu (evt) 
{
	evt = (evt) ? evt : (window.event) ? window.event : "";
	//	get LI tag
	var liTag;
	if (isIE)
		liTag = evt.srcElement;
	else
		liTag = evt.target;
	evt.cancelBubble = true;
		
	if (liTag.nodeName != 'LI')
		return;
	//	get nested UL tag (first child is the text)
	var ulTag = liTag.firstChild.nextSibling;
	/**
	alert ('event=' + evt 
		+ '\n - li=' + liTag.nodeName + "-" + liTag.id
	//	+ '\n - sibling=' + liTag.nextSibling + "-" + liTag.nextSibling.nodeName + "--" + liTag.nextSibling.id
	//	+ '\n - child=' + liTag.firstChild + "-" + liTag.firstChild.nodeName + "--" + liTag.firstChild.id
	//	+ '\n - child-sibling=' + liTag.firstChild.nextSibling + "-" + liTag.firstChild.nextSibling.nodeName + "--" + liTag.firstChild.nextSibling.id
		+ '\n - ul=' + ulTag.nodeName + "-" + ulTag.id
		);
	/* **/
	if (!ulTag || ulTag.nodeName != 'UL')
		return;

	if (ulTag.style.display=="none") 
	{
		ulTag.style.display="";
		liTag.style.listStyleImage="url(/compiere/images/mOpen.gif)";	
	} 
	else
	{
		ulTag.style.display="none";
		liTag.style.listStyleImage="url(/compiere/images/mClosed.gif)";	
	}
	
}	// 	changeMenu

