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

import java.util.*;

/**
    This class provides a common set of attributes set* methods for all classes.
    It is abstract to prevent direct instantiation.
    @version $Id: ElementAttributes.java,v 1.6 2005/02/14 06:47:53 jjanke Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/

public abstract class ElementAttributes extends GenericElement implements Attributes
{
    /**
        Basic Constructor.
    */
    public ElementAttributes()
    {
    }

    /**
        Filter to use to escape attribute input.
        By default the attribute filter and the element filter are the same.
        @serial attribute_filter attribute_filter
    */
    private Filter attribute_filter = getFilter();   

    /**
        Should we filter the value of the element attributes
        @serial filter_attribute_state filter_attribute_state
    */
    private boolean filter_attribute_state = ECSDefaults.getDefaultFilterAttributeState();

    /**
        What is the equality character for an attribute.
        @serial attribute_equality_sign attribute_equality_sign
    */
    private char attribute_equality_sign = ECSDefaults.getDefaultAttributeEqualitySign();

    /**
        What character should we use for quoting attributes.
        @serial attribute_quote_char attribute_quote_char
    */
    private char attribute_quote_char = ECSDefaults.getDefaultAttributeQuoteChar();

    /**
        Should we wrap quotes around an attribute?
        @serial attribute_quote attribute_quote
    */
    private boolean attribute_quote = ECSDefaults.getDefaultAttributeQuote();

    /**
        Set the character used to quote attributes.
        @param  quote_char character used to quote attributes
    */
    public Element setAttributeQuoteChar(char quote_char)
    {
        attribute_quote_char = quote_char;
        return(this);
    }

    /**
        Get the character used to quote attributes.
    */
    public char getAttributeQuoteChar()
    {
        return(attribute_quote_char);
    }

    /**
        Set the equality sign for an attribute.
        @param equality_sign The equality sign used for attributes.
    */
    public Element setAttributeEqualitySign(char equality_sign)
    {
        attribute_equality_sign = equality_sign;
        return(this);
    }

    /**
        Get the equality sign for an attribute.
    */
    public char getAttributeEqualitySign()
    {
        return(attribute_equality_sign);
    }

    /*
        Do we surround attributes with qoutes?
    */
    public boolean getAttributeQuote()
    {
        return(attribute_quote);
    }

    /**
        Set wether or not we surround the attributes with quotes.
    */
    public Element setAttributeQuote(boolean attribute_quote)
    {
        this.attribute_quote = attribute_quote;
        return(this);
    }

    /**
        Set the element id for Cascading Style Sheets.
    */
    public Element setID(String id)
    {
        addAttribute("id",id);
        return(this);
    }

    /**
        Set the element class for Cascading Style Sheets.
    */
    public Element setClass(String element_class)
    {
        addAttribute("class",element_class);
        return(this);
    }

    /**
        Sets the LANG="" attribute
        @param   lang  the LANG="" attribute
    */
    public Element setLang(String lang)
    {
        addAttribute("lang",lang);
        return(this);
    }

    /**
        Sets the STYLE="" attribute
        @param   style  the STYLE="" attribute
    */
    public Element setStyle(String style)
    {
        addAttribute("style",style);
        return(this);
    }

    /**
        Sets the DIR="" attribute
        @param   dir  the DIR="" attribute
    */
    public Element setDir(String dir)
    {
        addAttribute("dir",dir);
        return(this);
    }
    /**
        Sets the TITLE="" attribute
        @param   title  the TITLE="" attribute
    */
    public Element setTitle(String title)
    {
        addAttribute("title",title);
        return(this);
    }

    /**
        Find out if we want to filter the elements attributes or not.
    */
    protected boolean getAttributeFilterState()
    {
        return(filter_attribute_state);
    }

    /**
        Tell the element if we want to filter its attriubtes.
        @param filter_attribute_state do we want to filter the attributes of this element?
    */
    public Element setAttributeFilterState(boolean filter_attribute_state)
    {
        this.filter_attribute_state = filter_attribute_state;
        return(this);
    }

    /**
        Set up a new filter for all element attributes.
        @param Filter the filter we want to use for element attributes.  By <br>
        default it is the same as is used for the value of the tag. It is assumed<br>
        that if you create a new filter you must want to use it.
    */
    public Element setAttributeFilter(Filter attribute_filter)
    {
        filter_attribute_state = true; // If your setting up a filter you must want to filter.
        this.attribute_filter = attribute_filter;
        return(this);
    }

    /**
        Get the filter for all element attributes.
        @param Filter the filter we want to use for element attributes.  By <br>
        default it is the same as is used for the value of the tag. It is assumed<br>
        that if you create a new filter you must want to use it.
    */
    public Filter getAttributeFilter()
    {
        return(this.attribute_filter);
    }


    /** Add an attribute to the element. */
    public Element addAttribute(String attribute_name, Object attribute_value)
    {
        getElementHashEntry().put(attribute_name, attribute_value);
        return(this);
    }

    /** Add an attribute to the element. */
    public Element addAttribute(String attribute_name, int attribute_value)
    {
        getElementHashEntry().put(attribute_name, new Integer(attribute_value));
        return(this);
    }

    /** Add an attribute to the element. */
    public Element addAttribute(String attribute_name, String attribute_value)
    {
    	if (attribute_name != null && attribute_value != null)
    		getElementHashEntry().put(attribute_name, attribute_value);
        return(this);
    }

    /** Add an attribute to the element. */
    public Element addAttribute(String attribute_name, Integer attribute_value)
    {
        getElementHashEntry().put(attribute_name, attribute_value);
        return(this);
    }

    /** remove an attribute from the element */
    public Element removeAttribute(String attribute_name)
    {
        try
        {
            getElementHashEntry().remove(attribute_name);
        }
        catch ( Exception e )
        {
        }
        return(this);
    }

    /** does the element have a particular attribute? */
    public boolean hasAttribute(String attribute)
    {
        return(getElementHashEntry().containsKey(attribute));
    }

    /** Return a list of the attributes associated with this element. */
    public Enumeration attributes()
    {
        return getElementHashEntry().keys();
    }

    /**
     * Return the specified attribute.
     * @param attribute The name of the attribute to fetch
     */
    public String getAttribute(String attribute)
    {
        return (String)getElementHashEntry().get(attribute);
    }

    /**
        This method overrides createStartTag() in Generic Element.
        It provides a way to print out the attributes of an element.
    */
    protected String createStartTag()
    {
        StringBuffer out = new StringBuffer();

        out.append(getStartTagChar());

        if(getBeginStartModifierDefined())
        {
            out.append(getBeginStartModifier());
        }
        out.append(getElementType());

        Enumeration en = getElementHashEntry().keys();
        String value = null; // avoid creating a new string object on each pass through the loop

        while (en.hasMoreElements())
        {
            String attr = (String) en.nextElement();
            if(getAttributeFilterState())
            {
                value = getAttributeFilter().process(getElementHashEntry().get(attr).toString());
            }
            else
            {
                value = (String) getElementHashEntry().get(attr);
            }
            out.append(' ');
            out.append(alterCase(attr));
            if(!value.equalsIgnoreCase(NO_ATTRIBUTE_VALUE) && getAttributeQuote())
            {
                out.append(getAttributeEqualitySign());
                out.append(getAttributeQuoteChar());
                out.append(value);
                out.append(getAttributeQuoteChar());
            }
            else if( !getAttributeQuote() )
            {
                out.append(getAttributeEqualitySign());
                out.append(value);
            }
        }
        if(getBeginEndModifierDefined())
        {
            out.append(getBeginEndModifier());
        }
        out.append(getEndTagChar());

        return(out.toString());
    }
}
