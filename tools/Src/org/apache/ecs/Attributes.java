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

/**
    This interface describes the attributes within an element. It is 
    implemented by ElementAttributes.

    @version $Id: Attributes.java,v 1.2 2001/11/06 03:43:32 jjanke Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public interface Attributes
{
    /**
        Does this element attribute value need a =""?
    */
    public static final String NO_ATTRIBUTE_VALUE = "ECS_NO_ATTRIBUTE_VALUE";

    /**
        Set the state of the attribute filter.
        @param filter_attribute_state do we need to filter attributes?
    */
    public Element setAttributeFilterState(boolean filter_attribute_state);

    /**
        Set the AttributeFilter that should be used.
        @param attribute_filter set the attribute filter to be used.
    */
    public Element setAttributeFilter(Filter attribute_filter);

    /**
        Get the AttributeFilter that is in use.
    */
    public Filter getAttributeFilter();

    /**
        Add an attribute to the Element.
        @param name name of the attribute
        @param element value of the attribute.
    */
    public Element addAttribute(String name,Object element);

    /**
        Add an attribute to the Element.
        @param name name of the attribute
        @param element value of the attribute.
    */
    public Element addAttribute(String name, int element);

    /**
        Add an attribute to the Element.
        @param name name of the attribute
        @param element value of the attribute.
    */
    public Element addAttribute(String name, String element);

    /**
        Add an attribute to the Element.
        @param name name of the attribute
        @param element value of the attribute.
    */
    public Element addAttribute(String name, Integer element);
    
    /**
        Remove an attribute from the element.
        @param name remove the attribute of this name
    */
    public Element removeAttribute(String name);

    /**
        Does the element have an attribute.
        @param name of the attribute to ask the element for.
    */
    public boolean hasAttribute(String name);

    /**
        Set the character used to quote attributes.
        @param  quote_char character used to quote attributes
    */
    public Element setAttributeQuoteChar(char quote_char);

    /**
        Get the character used to quote attributes.
    */
    public char getAttributeQuoteChar();

    /**
        Set the equality sign for an attribute.
        @param equality_sign The equality sign used for attributes.
    */
    public Element setAttributeEqualitySign(char equality_sign);

    /**
        Get the equality sign for an attribute.
    */
    public char getAttributeEqualitySign();

    /**
        Do we surround attributes with qoutes?
    */
    public boolean getAttributeQuote();

    /**
        Set wether or not we surround the attributes with quotes.
    */
    public Element setAttributeQuote(boolean attribute_quote);
} 
