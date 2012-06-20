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

import java.io.Serializable;

import org.apache.ecs.xhtml.html;

import org.apache.ecs.xhtml.body;

import org.apache.ecs.xhtml.title;

import org.apache.ecs.xhtml.head;

import org.apache.ecs.xhtml.frameset;

import org.apache.ecs.xhtml.noframes;



/**

    This class creates a XhtmlFrameSetDocument container, for convience.



    @version $Id: XhtmlFrameSetDocument.java,v 1.4 2002/08/23 21:48:17 jjanke Exp $

    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>

    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>

    @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>

*/

public class XhtmlFrameSetDocument implements Serializable,Cloneable

{

    /** @serial html html */

    private html html; // this is the actual container for head and body

    /** @serial head head */

    private head head;

    /** @serial body body */

    private body body;

    /** @serial title title */

    private title title;

    /** @serial frameset frameset */

    private frameset frameset;

    /** @serial noframes frameset */

    private noframes noframes;



    /** @serial codeset codeset */

    private String codeset = null;

    

    {

        html = new html();

        head = new head();

        title = new title();

        frameset = new frameset();

        noframes = new noframes();

        body = new body();

        

        head.addElement(title);

        html.addElement(head);

        html.addElement(frameset);

        html.addElement(noframes);

        noframes.addElement(body);        

    }

    

    /**

        Basic constructor.

    */

    public XhtmlFrameSetDocument()

    {

    }



    /**

        Basic constructor. Sets the codeset for the page output.

    */

    public XhtmlFrameSetDocument(String codeset)

    {

        setCodeset(codeset);

    }



    /**

        Get the html element for this document container.

    */

    public html getHtml()

    {

        return(html);

    }

    

    /**

        Set the html element for this XhtmlFrameSetDocument container.

    */

    public XhtmlFrameSetDocument setHtml(html set_html)

    {

        this.html = set_html;

        return(this);

    }

    

    /**

        Get the head element for this XhtmlFrameSetDocument container.

    */

    public head getHead()

    {

        return(head);

    }



    /**

        Set the head element for this XhtmlFrameSetDocument container.

    */

    public XhtmlFrameSetDocument setHead(head set_head)

    {

        this.head = set_head;

        return(this);

    }



    /**

        Append to the head element for this XhtmlFrameSetDocument container.

        @param value adds to the value between the head tags

    */

    public XhtmlFrameSetDocument appendHead(Element value)

    {

        head.addElement(value);

        return(this);

    }



    /**

        Append to the head element for this XhtmlFrameSetDocument container.

        @param value adds to the value between the head tags

    */

    public XhtmlFrameSetDocument appendHead(String value)

    {

        head.addElement(value);

        return(this);

    }



    /**

        Get the frameset element for this XhtmlFrameSetDocument container.

    */

    public frameset getFrameSet()

    {

        return(frameset);

    }



    /**

        Set the frameset element for this XhtmlFrameSetDocument container.

    */

    public XhtmlFrameSetDocument setHead(frameset set_frameset)

    {

        this.frameset = set_frameset;

        return(this);

    }



    /**

        Append to the head element for this FrameSetDocument container.

        @param value adds to the value between the head tags

    */

    public XhtmlFrameSetDocument appendFrameSet(Element value)

    {

        frameset.addElement(value);

        return(this);

    }



    /**

        Append to the head element for this XhtmlFrameSetDocument container.

        @param value adds to the value between the head tags

    */

    public XhtmlFrameSetDocument appendFrameSet(String value)

    {

        frameset.addElement(value);

        return(this);

    }

    /**

        Get the body element for this XhtmlFrameSetDocument container.

    */

    public body getBody()

    {

        return(body);

    }



    /**

        Set the body element for this XhtmlFrameSetDocument container.

    */

    public XhtmlFrameSetDocument setBody(body set_body)

    {

        this.body = set_body;

        return(this);

    }

    

    /**

        Append to the body element for this XhtmlFrameSetDocument container.

        @param value adds to the value between the body tags

    */

    public XhtmlFrameSetDocument appendBody(Element value)

    {

        body.addElement(value);

        return(this);

    }



    /**

        Append to the body element for this XhtmlFrameSetDocument container.

        @param value adds to the value between the body tags

    */

    public XhtmlFrameSetDocument appendBody(String value)

    {

        body.addElement(value);

        return(this);

    }



    /**

        Get the title element for this XhtmlFrameSetDocument container.

    */

    public title getTitle()

    {

        return(title);

    }



    /**

        Set the title element for this XhtmlFrameSetDocument container.

    */

    public XhtmlFrameSetDocument setTitle(title set_title)

    {

        this.title = set_title;

        return(this);

    }

    

    /**

        Append to the title element for this XhtmlFrameSetDocument container.

        @param value adds to the value between the title tags

    */

    public XhtmlFrameSetDocument appendTitle(Element value)

    {

        title.addElement(value);

        return(this);

    }



    /**

        Append to the title element for this XhtmlFrameSetDocument container.

        @param value adds to the value between the title tags

    */

    public XhtmlFrameSetDocument appendTitle(String value)

    {

        title.addElement(value);

        return(this);

    }



    /**

     * Sets the codeset for this XhtmlFrameSetDocument

     */

    public void setCodeset ( String codeset )

    {

        this.codeset = codeset;

    }



    /**

     * Gets the codeset for this XhtmlFrameSetDocument

     *

     * @return    the codeset 

     */

    public String getCodeset()

    {

        return this.codeset;

    }

    

    /**

        Write the container to the OutputStream

    */

    public void output(OutputStream out)

    {

        // XhtmlFrameSetDocument is just a convient wrapper for html call html.output

        html.output(out);

    }



    /**

        Write the container to the PrinteWriter

    */

    public void output(PrintWriter out)

    {

        // XhtmlFrameSetDocument is just a convient wrapper for html call html.output

        html.output(out);

    }



    /**

        Override the toString() method so that it prints something meaningful.

    */

    public final String toString()

    {

        if ( getCodeset() != null )

            return (html.toString(getCodeset()));

        else

            return(html.toString());

    }



    /**

        Override the toString() method so that it prints something meaningful.

    */

    public final String toString(String codeset)

    {

        return(html.toString(codeset));

    }

    /**

        Allows the XhtmlFrameSetDocument to be cloned.  Doesn't return an instanceof XhtmlFrameSetDocument returns instance of html.



    */



    public Object clone()

    {

        return(html.clone());

    }

}

