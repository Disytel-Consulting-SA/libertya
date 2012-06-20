/*
 * Copyright (c) 1999 The Java Apache Project. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. All advertising materials
 * mentioning features or use of this software must display the following
 * acknowledgment: "This product includes software developed by the Java Apache
 * Project. <http://java.apache.org/>" 4. The names "Java Apache Element
 * Construction Set", "Java Apache ECS" and "Java Apache Project" must not be
 * used to endorse or promote products derived from this software without prior
 * written permission. 5. Products derived from this software may not be called
 * "Java Apache Element Construction Set" nor "Java Apache ECS" appear in their
 * names without prior written permission of the Java Apache Project. 6.
 * Redistributions of any form whatsoever must retain the following
 * acknowledgment: "This product includes software developed by the Java Apache
 * Project. <http://java.apache.org/>" THIS SOFTWARE IS PROVIDED BY THE JAVA
 * APACHE PROJECT "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE JAVA APACHE
 * PROJECT OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. This software consists of
 * voluntary contributions made by many individuals on behalf of the Java Apache
 * Project. For more information on the Java Apache Project please see
 * <http://java.apache.org/>.
 */
package org.apache.ecs.xhtml;

import org.apache.ecs.*;

/**
 * This class creates a &lt;select&gt; tag.
 * 
 * @version $Id: select.java,v 1.3 2005/05/17 05:30:30 jjanke Exp $
 * @author <a href="mailto:snagy@servletapi.com">Stephan Nagy </a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens </a>
 * @author <a href="mailto:bojan@binarix.com">Bojan Smojver </a>
 */
public class select extends MultiPartElement
	implements Printable, PageEvents, FormEvents, MouseEvents, KeyEvents
{

	/**
	 * Private initializer
	 */
	{
		setElementType ("select");
		setCase (LOWERCASE);
		setAttributeQuote (true);
	}

	/**
	 * Basic constructor. Use the set* methods.
	 */
	public select ()
	{
	}

	/**
	 * Basic Constructor.
	 * 
	 * @param name
	 *            set the NAME="" attribute
	 */
	public select (String name)
	{
		setName (name);
	}

	/**
	 * Basic Constructor.
	 * 
	 * @param name
	 *            set the name="" attribute
	 * @param size
	 *            set the size="" attribute
	 */
	public select (String name, String size)
	{
		setName (name);
		setSize (size);
	}

	/**
	 * Basic Constructor.
	 * 
	 * @param name
	 *            set the name="" attribute
	 * @param size
	 *            set the size="" attribute
	 */
	public select (String name, int size)
	{
		setName (name);
		setSize (size);
	}

	/**
	 * Basic Constructor.
	 * 
	 * @param name
	 *            set the name="" attribute
	 * @param element
	 *            provide a group of strings to be converted to options
	 *            elements.
	 */
	public select (String name, String[] element)
	{
		setName (name);
		addElement (element);
	}

	/**
	 * Basic Constructor.
	 * 
	 * @param name
	 *            set the name="" attribute
	 * @param element
	 *            provide a group of strings to be converted to options
	 *            elements.
	 */
	public select (String name, option[] element)
	{
		setName (name);
		addElement (element);
	}

	/**
	 * Sets the lang="" and xml:lang="" attributes
	 * 
	 * @param lang
	 *            the lang="" and xml:lang="" attributes
	 */
	public Element setLang (String lang)
	{
		addAttribute ("lang", lang);
		addAttribute ("xml:lang", lang);
		return this;
	}

	/**
	 * Adds an Element to the element.
	 * 
	 * @param hashcode
	 *            name of element for hash table
	 * @param element
	 *            Adds an Element to the element.
	 */
	public select addElement (String hashcode, Element element)
	{
		addElementToRegistry (hashcode, element);
		return (this);
	}

	/**
	 * Adds an Element to the element.
	 * 
	 * @param hashcode
	 *            name of element for hash table
	 * @param element
	 *            Adds an Element to the element.
	 */
	public select addElement (String hashcode, String element)
	{
		addElementToRegistry (hashcode, element);
		return (this);
	}

	/**
	 * Adds an Element to the Element.
	 * 
	 * @param element
	 *            adds and Element to the Element.
	 */
	public select addElement (Element element)
	{
		addElementToRegistry (element);
		return (this);
	}

	/**
	 * Adds a group of elements to the select element.
	 * 
	 * @param element
	 *            adds a group of elements to the select element.
	 */
	public select addElement (option[] element)
	{
		for (int x = 0; x < element.length; x++)
		{
			addElementToRegistry (element[x]);
		}
		return (this);
	}

	/**
	 * Adds an Element to the Element.
	 * 
	 * @param element
	 *            adds and Element to the Element.
	 */
	public select addElement (String element)
	{
		addElementToRegistry (element);
		return (this);
	}

	/**
	 * Creates a group of option elements and adds them to this select.
	 * 
	 * @param element
	 *            adds a group of option elements to this select.
	 */
	public select addElement (String[] element)
	{
		option[] options = new option ().addElement (element);
		addElement (options);
		return (this);
	}

	/**
	 * Sets the name="" attribute
	 * 
	 * @param name
	 *            the name="" attribute
	 */
	public select setName (String name)
	{
		addAttribute ("name", name);
		return this;
	}

	/**
	 * Sets the size="" attribute
	 * 
	 * @param size
	 *            the size="" attribute
	 */
	public select setSize (String size)
	{
		addAttribute ("size", size);
		return this;
	}

	/**
	 * Sets the size="" attribute
	 * 
	 * @param size
	 *            the size="" attribute
	 */
	public select setSize (int size)
	{
		setSize (Integer.toString (size));
		return this;
	}

	/**
	 * Sets the multiple value
	 * 
	 * @param multiple
	 *            true or false
	 */
	public select setMultiple (boolean multiple)
	{
		if (multiple == true)
			addAttribute ("multiple", "multiple");
		else
			removeAttribute ("multiple");
		return (this);
	}

	/**
	 * Sets the tabindex="" attribute
	 * 
	 * @param alt
	 *            the tabindex="" attribute
	 */
	public select setTabindex (String index)
	{
		addAttribute ("tabindex", index);
		return this;
	}

	/**
	 * Sets the tabindex="" attribute
	 * 
	 * @param alt
	 *            the tabindex="" attribute
	 */
	public select setTabindex (int index)
	{
		setTabindex (Integer.toString (index));
		return this;
	}

	/**
	 * Sets the disabled value
	 * 
	 * @param disabled
	 *            true or false
	 */
	public select setDisabled (boolean disabled)
	{
		if (disabled == true)
			addAttribute ("disabled", "disabled");
		else
			removeAttribute ("disabled");
		return (this);
	}

	/**
	 * Removes an Element from the element.
	 * 
	 * @param hashcode
	 *            the name of the element to be removed.
	 */
	public select removeElement (String hashcode)
	{
		removeElementFromRegistry (hashcode);
		return (this);
	}

	/**
	 * The onload event occurs when the user agent finishes loading a window or
	 * all frames within a frameset. This attribute may be used with body and
	 * frameset elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnLoad (String script)
	{
		addAttribute ("onload", script);
	}

	/**
	 * The onunload event occurs when the user agent removes a document from a
	 * window or frame. This attribute may be used with body and frameset
	 * elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnUnload (String script)
	{
		addAttribute ("onunload", script);
	}

	/**
	 * The onsubmit event occurs when a form is submitted. It only applies to
	 * the FORM element.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnSubmit (String script)
	{
		addAttribute ("onsubmit", script);
	}

	/**
	 * The onreset event occurs when a form is reset. It only applies to the
	 * FORM element.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnReset (String script)
	{
		addAttribute ("onreset", script);
	}

	/**
	 * The onselect event occurs when a user selects some text in a text field.
	 * This attribute may be used with the input and textarea elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnSelect (String script)
	{
		addAttribute ("onselect", script);
	}

	/**
	 * The onchange event occurs when a control loses the input focus and its
	 * value has been modified since gaining focus. This attribute applies to
	 * the following elements: input, select, and textarea.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnChange (String script)
	{
		addAttribute ("onchange", script);
	}

	/**
	 * The onclick event occurs when the pointing device button is clicked over
	 * an element. This attribute may be used with most elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnClick (String script)
	{
		addAttribute ("onclick", script);
	}

	/**
	 * The ondblclick event occurs when the pointing device button is double
	 * clicked over an element. This attribute may be used with most elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnDblClick (String script)
	{
		addAttribute ("ondblclick", script);
	}

	/**
	 * The onmousedown event occurs when the pointing device button is pressed
	 * over an element. This attribute may be used with most elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnMouseDown (String script)
	{
		addAttribute ("onmousedown", script);
	}

	/**
	 * The onmouseup event occurs when the pointing device button is released
	 * over an element. This attribute may be used with most elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnMouseUp (String script)
	{
		addAttribute ("onmouseup", script);
	}

	/**
	 * The onmouseover event occurs when the pointing device is moved onto an
	 * element. This attribute may be used with most elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnMouseOver (String script)
	{
		addAttribute ("onmouseover", script);
	}

	/**
	 * The onmousemove event occurs when the pointing device is moved while it
	 * is over an element. This attribute may be used with most elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnMouseMove (String script)
	{
		addAttribute ("onmousemove", script);
	}

	/**
	 * The onmouseout event occurs when the pointing device is moved away from
	 * an element. This attribute may be used with most elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnMouseOut (String script)
	{
		addAttribute ("onmouseout", script);
	}

	/**
	 * The onkeypress event occurs when a key is pressed and released over an
	 * element. This attribute may be used with most elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnKeyPress (String script)
	{
		addAttribute ("onkeypress", script);
	}

	/**
	 * The onkeydown event occurs when a key is pressed down over an element.
	 * This attribute may be used with most elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnKeyDown (String script)
	{
		addAttribute ("onkeydown", script);
	}

	/**
	 * The onkeyup event occurs when a key is released over an element. This
	 * attribute may be used with most elements.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnKeyUp (String script)
	{
		addAttribute ("onkeyup", script);
	}
}
