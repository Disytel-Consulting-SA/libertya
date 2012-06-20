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

import java.util.ResourceBundle;

/**
    This class is responsible for loading the ecs.properties file and 
    getting the default settings for ECS. This allows you to edit a 
    simple text file instead of having to edit the .java files and 
    recompile.

    @version $Id: ECSDefaults.java,v 1.2 2001/11/06 03:43:32 jjanke Exp $
*/
public class ECSDefaults
{
   protected static ResourceBundle resource;

	// private initializer.
	static {
		try
		{   
			resource = ResourceBundle.getBundle("org.apache.ecs.ecs");
		}
		catch(Exception e)
		{
			System.err.println("Cannot find org.apache.ecs.ecs.properties.");
		}
	}

	private static boolean filter_state = new Boolean(resource.getString("filter_state")).booleanValue();

    private static boolean filter_attribute_state = new Boolean(resource.getString("filter_attribute_state")).booleanValue();

    private static char attribute_equality_sign = resource.getString("attribute_equality_sign").charAt(1);

	private static char begin_start_modifier = resource.getString("begin_start_modifier").charAt(1);

	private static char end_start_modifier = resource.getString("end_start_modifier").charAt(1);

	private static char begin_end_modifier = resource.getString("begin_end_modifier").charAt(1);

	private static char end_end_modifier = resource.getString("end_end_modifier").charAt(1);

    private static char attribute_quote_char = resource.getString("attribute_quote_char").charAt(0);

    private static boolean attribute_quote = new Boolean(resource.getString("attribute_quote")).booleanValue();

	private static boolean end_element = new Boolean(resource.getString("end_element")).booleanValue();

	private static String codeset = resource.getString("codeset");

	private static int position = Integer.parseInt(resource.getString("position"));
	
	private static int case_type = Integer.parseInt(resource.getString("case_type"));

	private static char start_tag = resource.getString("start_tag").charAt(0);

	private static char end_tag = resource.getString("end_tag").charAt(0);

	private static boolean pretty_print = new Boolean(resource.getString("pretty_print")).booleanValue();


	/**
		Should we filter the value of &lt;&gt;VALUE&lt;/&gt;
	*/
	public static boolean getDefaultFilterState()
	{
		return filter_state;
	}

    /**
        Should we filter the value of the element attributes
    */
    public static boolean getDefaultFilterAttributeState()
	{
		return filter_attribute_state;
	}
	
    /**
        What is the equality character for an attribute.
    */
    public static char getDefaultAttributeEqualitySign()
	{
		return attribute_equality_sign;
	}

	/**
		What the start modifier should be
	*/
	public static char getDefaultBeginStartModifier()
	{
		return begin_start_modifier;
	}

	/**
		What the start modifier should be
	*/
	public static char getDefaultEndStartModifier()
	{
		return end_start_modifier;
	}
	
	/**
		What the end modifier should be
	*/
	public static char getDefaultBeginEndModifier()
	{
		return begin_end_modifier;
	}

	/**
		What the end modifier should be
	*/
	public static char getDefaultEndEndModifier()
	{
		return end_end_modifier;
	}

    /*
        What character should we use for quoting attributes.
    */
    public static char getDefaultAttributeQuoteChar()
	{
		return attribute_quote_char;
	}

    /*
        Should we wrap quotes around an attribute?
    */
    public static boolean getDefaultAttributeQuote()
	{
		return attribute_quote;
	}

	/**
		Does this element need a closing tag?
	*/
	public static boolean getDefaultEndElement()
	{
		return end_element;
	}

	/**
		What codeset are we going to use the default is 8859_1
	*/
	public static String getDefaultCodeset()
	{
		return codeset;
	}

	/**
		position of tag relative to start and end.
	*/
	public static int getDefaultPosition()
	{
		return position;
	}
	
	/**
		Default value to set case type
	*/
	public static int getDefaultCaseType()
	{
		return case_type;
	}

	public static char getDefaultStartTag()
	{
		return start_tag;
	}

	public static char getDefaultEndTag()
	{
		return end_tag;
	}

	/**
		Should we print html in a more readable format?
	*/
	public static boolean getDefaultPrettyPrint()
	{
		return pretty_print;
	}

}
