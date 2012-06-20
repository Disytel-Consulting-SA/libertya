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
import java.util.Enumeration;
import java.util.Vector;

/**
    This class is a Element container class. You can place elements into 
    this class and then you can place this class into other elements in order 
    to combine elements together.

<code><pre>
    P p = new P().addElement("foo");
    P p1 = new P().addElement("bar");
    ElementContainer ec = new ElementContainer(p).addElement(p1);
    System.out.println(ec.toString());
</pre></code>

    @version $Id: ElementContainer.java,v 1.2 2001/11/06 03:43:32 jjanke Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class ElementContainer extends ConcreteElement implements Printable
{
    /** 
        internal use only
        @serial ec ec
    */
    private Vector ec = new Vector(2);
    
    /** 
        Basic constructor
    */
    public ElementContainer()
    {
    }

    /** 
        Basic constructor
    */
    public ElementContainer(Element element)
    {
        addElement(element);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public ElementContainer addElement(Element element)
    {
        ec.addElement(element);
        return(this);
    }
    
    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public ElementContainer addElement(String element)
    {
        ec.addElement(new StringElement(element));
        return(this);
    }

    /**
        Implements the output method in Element
    */
    public void output(OutputStream out)
    {
        Element element = null;
        Enumeration data = ec.elements();
        while ( data.hasMoreElements() )
        {
            element = (Element) data.nextElement();
            element.output(out);
        }
    }
    
    /**
        Implements the output method in Element
    */
    public void output(PrintWriter out)
    {
        Element element = null;
        Enumeration data = ec.elements();
        while ( data.hasMoreElements() )
        {
            element = (Element) data.nextElement();
            element.output(out);
        }
    }
    /**
        returns an enumeration of the elements in this container
    */
    public Enumeration elements()
    {
        return ec.elements();
    }
}
