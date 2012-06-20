 /*
 * Copyright (c) 1999 The Java Apache Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment:
 *    "This product includes software developed by the Java Apache
 *    Project. <http://java.apache.org/>"
 *
 * 4. The names "Java Apache Element Construction Set", "Java Apache ECS" and
 *    "Java Apache Project" must not be used to endorse or promote products
 *    derived from this software without prior written permission.
 *
 * 5. Products derived from this software may not be called
 *    "Java Apache Element Construction Set" nor "Java Apache ECS" appear
 *    in their names without prior written permission of the
 *    Java Apache Project.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the Java Apache
 *    Project. <http://java.apache.org/>"
 *
 * THIS SOFTWARE IS PROVIDED BY THE JAVA APACHE PROJECT "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JAVA APACHE PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Java Apache Project. For more information
 * on the Java Apache Project please see <http://java.apache.org/>.
 *
 */
package org.apache.ecs;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

/**
	This class is to be subclassed by those elements that are made up of
	other elements. i.e. BODY,HEAD,etc.

	@version $Id: ConcreteElement.java,v 1.6 2004/10/30 06:00:28 jjanke Exp $
	@author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
	@author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class ConcreteElement extends ElementAttributes implements Cloneable
{
	/** HTML nbsp				*/
	public static final String	NBSP = "&nbsp;";

	/**
	 * keep a list of elements that need to be added to the element 
	 * @serial registry registry */
	private Hashtable registry = new Hashtable(4); 
	/** Maintain an ordered list of elements */
	private Vector registryList = new Vector(2);

	public ConcreteElement()
	{
	}

	/**
		If the object is in the registry return otherwise return null.
		@param element the name of the object to locate.
	*/
	public ConcreteElement getElement(String element)
	{
		if(registry.containsKey(element))
		{
			return (ConcreteElement)registry.get(element);
		}
		return null;
	}

	/**
		Registers an element in the head element list
		@param   element element to be added to the registry.
	*/
	public Element addElementToRegistry(Element element)
	{
		if ( element == null )
			return(this);
		int hc = element.hashCode();    //  causes error when compiles in 1.4 ??
		String s = Integer.toString(hc);
		addElementToRegistry(s, element);
		return(this);
	}

	/**
		Registers an element in the head element list
		@param   hashcode internal name of element
		@param   element element to be added to the registry.
	*/
	public Element addElementToRegistry(String hashcode,Element element)
	{
		if ( hashcode == null || element == null )
			return(this);

		 element.setFilterState(getFilterState());
		 if(ECSDefaults.getDefaultPrettyPrint() != element.getPrettyPrint())
			  element.setPrettyPrint(getPrettyPrint());
		 registry.put(hashcode,element);
		 if(!registryList.contains(hashcode))
			registryList.addElement(hashcode);
		 return(this);
	}

	/**
		Registers an element in the head element list
		@hashcode named element for hashcode
		@param   element element to be added to the registry.
		@param   filter does this need to be filtered?
	*/
	public Element addElementToRegistry(Element element,boolean filter)
	{
		if ( element == null )
			return(this);
		setFilterState(filter);
		addElementToRegistry(Integer.toString(element.hashCode()),element);
		return(this);
	}

	/**
		Registers an element in the head element list
		@param   element element to be added to the registry.
		@param   filter  should we filter this element?
	*/
	public Element addElementToRegistry(String hashcode, Element element,boolean filter)
	{
		if ( hashcode == null )
			return(this);
		setFilterState(filter);
		addElementToRegistry(hashcode,element);
		return(this);
	}

	/**
		Registers an element in the head element list
		@param   element element to be added to the registry.
		@param   filter does this need to be filtered?
	*/
	public Element addElementToRegistry(String value,boolean filter)
	{
		if ( value == null )
			return(this);
		setFilterState(filter);
		addElementToRegistry(Integer.toString(value.hashCode()),value);
		return(this);
	}

	/**
		Registers an element in the head element list
		@hashcode named element for hashcode
		@param   element element to be added to the registry.
		@param   filter does this need to be filtered?
	*/
	public Element addElementToRegistry(String hashcode, String value,boolean filter)
	{
		if ( hashcode == null )
			return(this);
		setFilterState(filter);
		addElementToRegistry(hashcode,value);
		return(this);
	}

	/**
		Registers an element in the head element list
		@param   element element to be added to the registry.
	*/
	public Element addElementToRegistry(String value)
	{
		if ( value == null )
			return(this);
		addElementToRegistry(new StringElement(value));
		return(this);
	}

	/**
		Registers an element in the head element list
		@param   element element to be added to the registry.
	*/
	public Element addElementToRegistry(String hashcode,String value)
	{
		if ( hashcode == null )
			return(this);

		// We do it this way so that filtering will work.
		// 1. create a new StringElement(element) - this is the only way that setTextTag will get called
		// 2. copy the filter state of this string element to this child.
		// 3. copy the prettyPrint state of the element to this child
		// 4. copy the filter for this string element to this child.

		StringElement se = new StringElement(value);
		se.setFilterState(getFilterState());
		se.setFilter(getFilter());
		se.setPrettyPrint(getPrettyPrint());
		addElementToRegistry(hashcode,se);
		return(this);
	}

	/**
		Removes an element from the element registry
		@param   element element to be added to the registry.
	*/
	public Element removeElementFromRegistry(Element element)
	{
		removeElementFromRegistry(Integer.toString(element.hashCode()));
		return(this);
	}

	/**
		Removes an element from the head element registry
		@param   hashcode element to be added to the registry.
	*/
	public Element removeElementFromRegistry(String hashcode)
	{
		registry.remove(hashcode);
		registryList.removeElement(hashcode);
		return(this);
	}

	/**
		Find out if this element is in the element registry.
		@param element find out if this element is in the registry
	*/
	public boolean registryHasElement(Element element)
	{
		return(registry.contains(element));
	}

	/**
		Get the keys of this element.
	*/
	public Enumeration keys()
	{
		return(registryList.elements());
	}

	/**
		Get an enumeration of the elements that this element contains.
	*/
	public Enumeration elements()
	{
		return(registry.elements());
	}

	/**
		Find out if this element is in the element registry.
		@param element find out if this element is in the registry
	*/
	public boolean registryHasElement(String hashcode)
	{
		return(registry.containsKey(hashcode));
	}

	/**
		Override output(OutputStream) incase any elements are in the registry.
		@param output OutputStream to write to.
	*/
	public void output(OutputStream out)
	{
		boolean prettyPrint = getPrettyPrint();
		int tabLevel = getTabLevel();
		try
		{
			if (registry.size() == 0)
			{
				if ((prettyPrint && this instanceof Printable) && (tabLevel > 0))
					putTabs(tabLevel, out);
				super.output(out);
			}
			else
			{
				if ((prettyPrint && this instanceof Printable) && (tabLevel > 0))
					putTabs(tabLevel, out);

				out.write(createStartTag().getBytes());

				// If this is a StringElement that has ChildElements still print the TagText
				if(getTagText() != null)
					out.write(getTagText().getBytes());

				Enumeration en = registryList.elements();

				while(en.hasMoreElements())
				{
					Object obj = registry.get(en.nextElement());
					if(obj instanceof GenericElement)
					{
						Element e = (Element)obj;
						if (prettyPrint && this instanceof Printable)
						{
							if ( getNeedLineBreak() )
							{
								out.write('\n');
								e.setTabLevel(tabLevel + 1);
							}
						}
						e.output(out);
					}
					else
					{
						if (prettyPrint && this instanceof Printable)
						{
							if ( getNeedLineBreak() )
							{
								out.write('\n');
								putTabs(tabLevel + 1, out);
							}
						}
						String string = obj.toString();
						out.write(string.getBytes());
					}
				}
				if (getNeedClosingTag())
				{
					if (prettyPrint && this instanceof Printable)
					{
						if ( getNeedLineBreak() )
						{
							out.write('\n');
							if (tabLevel > 0)
								putTabs(tabLevel, out);
						}
					}
				   out.write(createEndTag().getBytes());
				}
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(new PrintWriter(out));
		}
	}

	/**
		Writer version of this method.
	*/
	public void output(Writer out)
	{
		output ( new PrintWriter(out) );
	}

	/**
		Override output(BufferedWriter) incase any elements are in the registry.
		@param output OutputStream to write to.
	*/
	public void output(PrintWriter out)
	{
		boolean prettyPrint = getPrettyPrint();
		int tabLevel = getTabLevel();
		if (registry.size() == 0)
		{
			if ((prettyPrint && this instanceof Printable) && (tabLevel > 0))
				putTabs(tabLevel, out);

			super.output(out);
		}
		else
		{
			if ((prettyPrint && this instanceof Printable) && (tabLevel > 0))
				putTabs(tabLevel, out);

			out.write(createStartTag());
			// If this is a StringElement that has ChildElements still print the TagText
			if(getTagText() != null)
				out.write(getTagText());

			Enumeration en = registryList.elements();
			while(en.hasMoreElements())
			{
				Object obj = registry.get(en.nextElement());
				if(obj instanceof GenericElement)
				{
					Element e = (Element)obj;
					if (prettyPrint && this instanceof Printable)
					{
						if (getNeedLineBreak()) {
							out.write('\n');
							e.setTabLevel(tabLevel + 1);
						}
					}
					e.output(out);
				}
				else
				{
					if (prettyPrint && this instanceof Printable)
					{
						if (getNeedLineBreak()) {
							out.write('\n');
							putTabs(tabLevel + 1, out);
						}
					}
					String string = obj.toString();
					if(getFilterState())
						out.write(getFilter().process(string));
					else
						out.write(string);
				}
			}
			if (getNeedClosingTag())
			{
				if (prettyPrint && this instanceof Printable)
				{
					if (getNeedLineBreak()) {
						out.write('\n');
						if (tabLevel > 0)
							putTabs(tabLevel, out);
					}
				}
			   out.write(createEndTag());
			}
		}
	}

	/**
	 * Allows all Elements the ability to be cloned.
	 */
	public Object clone()
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(this);
			out.close();
			ByteArrayInputStream bin = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream in = new ObjectInputStream(bin);
			Object clone =  in.readObject();
			in.close();
			return(clone);
		}
		catch(ClassNotFoundException cnfe)
		{
			throw new InternalError(cnfe.toString());
		}
		catch(StreamCorruptedException sce)
		{
			throw new InternalError(sce.toString());
		}
		catch(IOException ioe)
		{
			throw new InternalError(ioe.toString());
		}
	}
}
