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
 * This class creates an &lt;a&gt; tag.
 * <P>
 * Please refer to the TestBed.java file for example code usage.
 * 
 * @version $Id: a.java,v 1.5 2004/11/01 03:16:57 jjanke Exp $
 * @author <a href="mailto:snagy@servletapi.com">Stephan Nagy </a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens </a>
 * @author <a href="mailto:bojan@binarix.com">Bojan Smojver </a>
 */
public class a extends MultiPartElement
	implements Printable, FocusEvents, MouseEvents, KeyEvents
{

	/**
	 * Private initialization routine.
	 */
	{
		setElementType ("a");
		setCase (LOWERCASE); // XHTML specific
		setAttributeQuote (true); // XHTML specific
		setPrettyPrint(false);
	}

	public static final String 	TARGET_BLANK 	= "_blank";
	public static final String 	TARGET_PARENT 	= "_parent";
	public static final String 	TARGET_SELF 	= "_self";
	public static final String 	TARGET_TOP 		= "_top";
	
	/**
	 * Basic constructor. You need to set the attributes using the set* methods.
	 */
	public a ()
	{
	}

	/**
	 * This constructor creates ah &lt;a&gt; tag.
	 * 
	 * @param href
	 *            the URI that goes between double quotes
	 */
	public a (String href)
	{
		setHref (href);
	}

	/**
	 * This constructor creates an &lt;a&gt; tag.
	 * 
	 * @param href
	 *            the URI that goes between double quotes
	 * @param value
	 *            what goes between &lt;start_tag&gt; &lt;end_tag&gt;
	 */
	public a (String href, String value)
	{
		setHref (href);
		addElement (value);
	}

	/**
	 * This constructor creates an &lt;a&gt; tag.
	 * 
	 * @param href
	 *            the URI that goes between double quotes
	 * @param value
	 *            what goes between &lt;start_tag&gt; &lt;end_tag&gt;
	 */
	public a (String href, Element value)
	{
		setHref (href);
		addElement (value);
	}

	/**
	 * This constructor creates an &lt;a&gt; tag.
	 * 
	 * @param href
	 *            the URI that goes between double quotes
	 * @param name
	 *            the name="" attribute
	 * @param value
	 *            what goes between &lt;start_tag&gt; &lt;end_tag&gt;
	 */
	public a (String href, String name, String value)
	{
		setHref (href);
		setName (name);
		addElement (value);
	}

	/**
	 * This constructor creates an &lt;a&gt; tag.
	 * 
	 * @param href
	 *            the URI that goes between double quotes
	 * @param name
	 *            the name="" attribute
	 * @param value
	 *            what goes between &lt;start_tag&gt; &lt;end_tag&gt;
	 */
	public a (String href, String name, Element value)
	{
		setHref (href);
		setName (name);
		addElement (value);
	}

	/**
	 * This constructor creates an &lt;a&gt; tag.
	 * 
	 * @param href
	 *            the URI that goes between double quotes
	 * @param name
	 *            the optional name="" attribute
	 * @param target
	 *            the target="" attribute
	 * @param value
	 *            the value that goes between &lt;start_tag&gt; &lt;end_tag&gt;
	 */
	public a (String href, String name, String target, Element value)
	{
		setHref (href);
		if (name != null)
			setName (name);
		setTarget (target);
		addElement (value);
	}

	/**
	 * This constructor creates an &lt;a&gt; tag.
	 * 
	 * @param href
	 *            the URI that goes between double quotes
	 * @param name
	 *            the optional name="" attribute
	 * @param target
	 *            the optional target="" attribute
	 * @param value
	 *            the value that goes between &lt;start_tag&gt; &lt;end_tag&gt;
	 */
	public a (String href, String name, String target, String value)
	{
		setHref (href);
		if (name != null)
			setName (name);
		if (target != null)
			setTarget (target);
		addElement (value);
	}

	/**
	 * This constructor creates an &lt;a&gt; tag.
	 * 
	 * @param href
	 *            the URI that goes between double quotes
	 * @param name
	 *            the optional name="" attribute
	 * @param target
	 *            the target="" attribute
	 * @param lang
	 *            the lang="" and xml:lang="" attributes
	 * @param value
	 *            the value that goes between &lt;start_tag&gt; &lt;end_tag&gt;
	 */
	public a (String href, String name, String target, String lang, String value)
	{
		setHref (href);
		if (name != null)
			setName (name);
		setTarget (target);
		setLang (lang);
		addElement (value);
	}

	/**
	 * This constructor creates an &lt;a&gt; tag.
	 * 
	 * @param href
	 *            the URI that goes between double quotes
	 * @param name
	 *            the optional name="" attribute
	 * @param target
	 *            the target="" attribute
	 * @param lang
	 *            the lang="" and xml:lang="" attributes
	 * @param value
	 *            the value that goes between &lt;start_tag&gt; &lt;end_tag&gt;
	 */
	public a (String href, String name, String target, String lang,	Element value)
	{
		setHref (href);
		if (name != null)
			setName (name);
		setTarget (target);
		setLang (lang);
		addElement (value);
	}

	/**
	 * Sets the href="" attribute
	 * 
	 * @param href
	 *            the href="" attribute
	 */
	public a setHref (String href)
	{
		addAttribute ("href", href);
		return this;
	}

	/**
	 * Sets the name="" attribute
	 * 
	 * @param name
	 *            the name="" attribute
	 */
	public a setName (String name)
	{
		addAttribute ("name", name);
		return this;
	}

	/**
	 * Sets the target="" attribute
	 * 
	 * @param target
	 *            the target="" attribute
	 */
	public a setTarget (String target)
	{
		addAttribute ("target", target);
		return this;
	}

	/**
	 * Sets the rel="" attribute
	 * 
	 * @param rel
	 *            the rel="" attribute
	 */
	public a setRel (String rel)
	{
		addAttribute ("rel", rel);
		return this;
	}

	/**
	 * Sets the rev="" attribute
	 * 
	 * @param rev
	 *            the rev="" attribute
	 */
	public a setRev (String rev)
	{
		addAttribute ("rev", rev);
		return this;
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
	 * @param element
	 *            Adds an Element to the element.
	 */
	public a addElement (Element element)
	{
		addElementToRegistry (element);
		return (this);
	}

	/**
	 * Adds an Element to the element.
	 * 
	 * @param element
	 *            Adds an Element to the element.
	 */
	public a addElement (String element)
	{
		addElementToRegistry (element);
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
	public a addElement (String hashcode, Element element)
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
	public a addElement (String hashcode, String element)
	{
		addElementToRegistry (hashcode, element);
		return (this);
	}

	/**
	 * Removes an Element from the element.
	 * 
	 * @param hashcode
	 *            the name of the element to be removed.
	 */
	public a removeElement (String hashcode)
	{
		removeElementFromRegistry (hashcode);
		return (this);
	}

	/**
	 * The onfocus event occurs when an element receives focus either by the
	 * pointing device or by tabbing navigation. This attribute may be used with
	 * the following elements: label, input, select, textarea, and button.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnFocus (String script)
	{
		addAttribute ("onfocus", script);
	}

	/**
	 * The onblur event occurs when an element loses focus either by the
	 * pointing device or by tabbing navigation. It may be used with the same
	 * elements as onfocus.
	 * 
	 * @param The
	 *            script
	 */
	public void setOnBlur (String script)
	{
		addAttribute ("onblur", script);
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
		addAttribute ("onnlouseup", script);
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

	/**
	 * Determine if this element needs a line break, if pretty printing.
	 */
	public boolean getNeedLineBreak ()
	{
		java.util.Enumeration en = elements ();
		int i = 0;
		int j = 0;
		while (en.hasMoreElements ())
		{
			j++;
			Object obj = en.nextElement ();
			if (obj instanceof img)
				i++;
		}
		if (i == j)
			return false;
		return true;
	}
}
