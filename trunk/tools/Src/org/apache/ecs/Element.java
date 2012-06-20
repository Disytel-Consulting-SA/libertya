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

/**
    This class describes an ElementFactory.

    @version $Id: Element.java,v 1.2 2001/11/06 03:43:32 jjanke Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public interface Element extends ElementRegistry
{
    /**
        Element to be rendered in all CAPS
    */
    public static final int UPPERCASE = 1;

    /**
        Element to be rendered in all lowercase
    */
    public static final int LOWERCASE = 2;

    /**
        Element to be rendered as specified by subclass
    */
    public static final int MIXEDCASE = 3;

    /**
        Element tag to be rendered to the center of start_tag end_tag &lt;_tag&gt; <br>
        This is the default.
    */
    public static final int CENTER = 4;

    /**
        Element tag to be rendered to the left of start_tag end_tag _tag&lt;&gt;
    */
    public static final int LEFT = 5;

    /**
        Element tag to be rendered to the right of start_tag end_tag &lt;&gt;_tag
    */
    public static final int RIGHT = 6;

    /**
        Set case type
    */
    public void setCase(int type);

    /**
        Used to determine case setting
    */
    public int getCase();

    /**
        Get the version number of this codebase
    */
    public String getVersion();

    /**
        Set the HtmlElement type
    */
    public void setElementType(String element_type);

    /**
        Get the HtmlElement type
    */
    public String getElementType();

    /**
        Set wether or not this Element needs a closing tag
    */
    public void setNeedClosingTag(boolean close_tag);

    /**
        Get whether or not this Element needs a closing tag
    */
    public  boolean getNeedClosingTag();

    /**
        Determine if this element needs a line break, if pretty printing.
    */
    public boolean getNeedLineBreak();

    /**
        Set tag position. ElementFactory CENTER | LEFT | RIGHT
    */
    public void setTagPosition(int position);

    /**
        Get tag position. How is the element supposed to be rendered.
    */
    public int getTagPosition();

    /**
        Set the start tag character.
    */
    public void setStartTagChar(char start_tag);

    /**
        Get the start tag character.
    */
    public char getStartTagChar();

    /**
        Set the end tag character.
    */
    public void setEndTagChar(char end_tag);

    /**
        Get the end tag character.
    */
    public char getEndTagChar();

    /*
        Set a modifer for the start of the tag.
    */
    public Element setBeginStartModifier(char start_modifier);

    /**
        Get a modifier for the start of the tag if one exists.
    */
    public char getBeginStartModifier();

    /**
        Set a modifer for the end of the tag.
    */
    public Element setBeginEndModifier(char start_modifier);

    /**
        Get the modifier for the end of the tag if one exists.
    */
    public char getBeginEndModifier();

    /*
        Set a modifer for the start of the tag.
    */
    public Element setEndStartModifier(char start_modifier);

    /**
        Get a modifier for the start of the tag if one exists.
    */
    public char getEndStartModifier();

    /**
        Set a modifer for the end of the tag.
    */
    public Element setEndEndModifier(char start_modifier);

    /**
        Get the modifier for the end of the tag if one exists.
    */
    public char getEndEndModifier();

    /**
        Set the filter state of the element.
    */
    public Element setFilterState(boolean state);

    /**
        Get the filter state of the element.
    */
    public boolean getFilterState();

    /**
        Set the prettyPrint state of the element.
    */
    public Element setPrettyPrint(boolean pretty_print);

    /**
        Get the prettyPrint state of the element.
    */
    public boolean getPrettyPrint();

    /**
		Set the tab level for pretty printing
	*/
	public void setTabLevel(int tabLevel);

    /**
        Get the tab level for pretty printing.
    */
    
	public int getTabLevel();

    /**
        Provide a way to output the element to a stream.
    */
    public void output(OutputStream out);

    /**
        Provide a way to output the element to a PrintWriter.
    */
    public void output(PrintWriter out);

}
