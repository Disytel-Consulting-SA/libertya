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
 * This class creates a &lt;form&gt; tag.
 * 
 * @version $Id: form.java,v 1.3 2004/11/01 03:16:57 jjanke Exp $
 * @author <a href="mailto:snagy@servletapi.com">Stephan Nagy </a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens </a>
 * @author <a href="mailto:bojan@binarix.com">Bojan Smojver </a>
 */
public class form extends MultiPartElement
	implements Printable, FormEvents, MouseEvents, KeyEvents
{

	public static final String	METHOD_GET	= "get";

	public static final String	METHOD_POST	= "post";

	public static final String	ENC_DEFAULT	= "application/x-www-form-urlencoded";

	public static final String	ENC_UPLOAD	= "multipart/form-data";
	/**
	 * Private initialization routine.
	 */
	{
		setElementType ("form");
		setCase (LOWERCASE);
		setAttributeQuote (true);
		setEncType (ENC_DEFAULT);
		setMethod(METHOD_POST);
		//	Only accepted by NN6
	//	setAcceptCharset ("utf-8");
	}

	/**
	 * 	Default URL Encoded utf-8 Post form
	 *	Basic constructor. You need to set the attributes using the set* methods.
	 */
	public form ()
	{
	}

	/**
	 * 	Default URL Encoded utf-8 Post form
	 *	Use the set* methods to set the values of the attributes.
	 * 
	 * @param element
	 *            set the value of &lt;form&gt;value&lt;/form&gt;
	 */
	public form (Element element)
	{
		addElement (element);
	}

	/**
	 * 	Default URL Encoded utf-8 Post form
	 *	Use the set* methods to set the values of the attributes.
	 * 
	 * @param action
	 *            set the value of action=""
	 */
	public form (String action)
	{
		setAction (action);
	}

	/**
	 * 	Default URL Encoded utf-8 Post form
	 *	Use the set* methods to set the values of the attributes.
	 * 
	 * @param element
	 *            set the value of &lt;form&gt;value&lt;/form&gt;
	 * @param action
	 *            set the value of action=""
	 */
	public form (String action, Element element)
	{
		addElement (element);
		setAction (action);
	}

	/**
	 * 	Default URL Encoded utf-8 form
	 *	Use the set* methods to set the values of the attributes.
	 * 
	 * @param action
	 *            set the value of action=""
	 * @param method
	 *            set the value of method=""
	 * @param element
	 *            set the value of &lt;form&gt;value&lt;/form&gt;
	 */
	public form (String action, String method, Element element)
	{
		addElement (element);
		setAction (action);
		setMethod (method);
	}

	/**
	 * 	Default URL Encoded utf-8 form
	 *	Use the set* methods to set the values of the attributes.
	 * 
	 * @param action
	 *            set the value of action=""
	 * @param method
	 *            set the value of method=""
	 */
	public form (String action, String method)
	{
		setAction (action);
		setMethod (method);
	}

	/**
	 * 	Default utf-8 form
	 *	Use the set* methods to set the values of the attributes.
	 * 
	 * @param action
	 *            set the value of action=""
	 * @param method
	 *            set the value of method=""
	 * @param enctype
	 *            set the value of enctype=""
	 */
	public form (String action, String method, String enctype)
	{
		setAction (action);
		setMethod (method);
		setEncType (enctype);
	}

	/**
	 * Sets the action="" attribute
	 * 
	 * @param action
	 *            the action="" attribute
	 */
	public form setAction (String action)
	{
		addAttribute ("action", action);
		return this;
	}

	/**
	 * Sets the method="" attribute
	 * 
	 * @param method
	 *            the method="" attribute
	 */
	public form setMethod (String method)
	{
		addAttribute ("method", method);
		return this;
	}

	/**
	 * Sets the enctype="" attribute
	 * 
	 * @param enctype
	 *            the enctype="" attribute
	 */
	public form setEncType (String enctype)
	{
		addAttribute ("enctype", enctype);
		return this;
	}

	/**
	 * Sets the accept="" attribute
	 * 
	 * @param accept
	 *            the accept="" attribute
	 */
	public form setAccept (String accept)
	{
		addAttribute ("accept", accept);
		return this;
	}

	/**
	 * Sets the name="" attribute
	 * 
	 * @param name
	 *            the name="" attribute
	 */
	public form setName (String name)
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
	public form setTarget (String target)
	{
		addAttribute ("target", target);
		return this;
	}

	/**
	 * Sets the accept-charset="" attribute
	 * 
	 * @param accept
	 *            the accept-charset="" attribute
	 */
	public form setAcceptCharset (String acceptcharset)
	{
		addAttribute ("accept-charset", acceptcharset);
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
	 * @param hashcode
	 *            name of element for hash table
	 * @param element
	 *            Adds an Element to the element.
	 */
	public form addElement (String hashcode, Element element)
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
	public form addElement (String hashcode, String element)
	{
		addElementToRegistry (hashcode, element);
		return (this);
	}

	/**
	 * Adds an Element to the element.
	 * 
	 * @param element
	 *            Adds an Element to the element.
	 */
	public form addElement (Element element)
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
	public form addElement (String element)
	{
		addElementToRegistry (element);
		return (this);
	}

	/**
	 * Removes an Element from the element.
	 * 
	 * @param hashcode
	 *            the name of the element to be removed.
	 */
	public form removeElement (String hashcode)
	{
		removeElementFromRegistry (hashcode);
		return (this);
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
