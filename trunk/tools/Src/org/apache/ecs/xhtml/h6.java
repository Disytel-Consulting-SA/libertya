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

package org.apache.ecs.xhtml;



import org.apache.ecs.*;



/**

    This class creates a &lt;h6&gt; tag.



    @version $Id: h6.java,v 1.1 2002/08/23 21:49:07 jjanke Exp $

    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>

    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>

    @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>

*/

public class h6 extends MultiPartElement implements Printable, MouseEvents, KeyEvents

{

    /**

        Private initialization routine.

    */

    {

        setElementType("h6");

        setCase(LOWERCASE);

        setAttributeQuote(true);

    }

    /**

        Basic constructor. You need to set the attributes using the

        set* methods.

    */

    public h6()

    {

    }



    /**

        Use the set* methods to set the values

        of the attributes.



        @param   element  set the value of &lt;h6&gt;value&lt;/h6&gt;

    */

    public h6(Element element)

    {

        addElement(element);

    }



    /**

        Use the set* methods to set the values

        of the attributes.



        @param   value  set the value of &lt;h6&gt;value&lt;/h6&gt;

    */

    public h6(String value)

    {

        addElement(value);

    }



    /**

        Sets the lang="" and xml:lang="" attributes

        @param   lang  the lang="" and xml:lang="" attributes

    */

    public Element setLang(String lang)

    {

        addAttribute("lang",lang);

        addAttribute("xml:lang",lang);

        return this;

    }



    /**

        Adds an Element to the element.

        @param  hashcode name of element for hash table

        @param  element Adds an Element to the element.

     */

    public h6 addElement(String hashcode,Element element)

    {

        addElementToRegistry(hashcode,element);

        return(this);

    }



    /**

        Adds an Element to the element.

        @param  hashcode name of element for hash table

        @param  element Adds an Element to the element.

     */

    public h6 addElement(String hashcode,String element)

    {

        addElementToRegistry(hashcode,element);

        return(this);

    }



    /**

        Adds an Element to the element.

        @param  element Adds an Element to the element.

     */

    public h6 addElement(Element element)

    {

        addElementToRegistry(element);

        return(this);

    }

    

    /**

        Adds an Element to the element.

        @param  element Adds an Element to the element.

     */

    public h6 addElement(String element)

    {

        addElementToRegistry(element);

        return(this);

    }

    /**

        Removes an Element from the element.

        @param hashcode the name of the element to be removed.

    */

    public h6 removeElement(String hashcode)

    {

        removeElementFromRegistry(hashcode);

        return(this);

    }



    /**

        The onclick event occurs when the pointing device button is clicked

        over an element. This attribute may be used with most elements.

        

        @param The script

    */

    public void setOnClick(String script)

    {

        addAttribute ( "onclick", script );

    }

    /**

        The ondblclick event occurs when the pointing device button is double

        clicked over an element. This attribute may be used with most elements.



        @param The script

    */

    public void setOnDblClick(String script)

    {

        addAttribute ( "ondblclick", script );

    }

    /**

        The onmousedown event occurs when the pointing device button is pressed

        over an element. This attribute may be used with most elements.



        @param The script

    */

    public void setOnMouseDown(String script)

    {

        addAttribute ( "onmousedown", script );

    }

    /**

        The onmouseup event occurs when the pointing device button is released

        over an element. This attribute may be used with most elements.



        @param The script

    */

    public void setOnMouseUp(String script)

    {

        addAttribute ( "onmouseup", script );

    }

    /**

        The onmouseover event occurs when the pointing device is moved onto an

        element. This attribute may be used with most elements.



        @param The script

    */

    public void setOnMouseOver(String script)

    {

        addAttribute ( "onmouseover", script );

    }

    /**

        The onmousemove event occurs when the pointing device is moved while it

        is over an element. This attribute may be used with most elements.



        @param The script

    */

    public void setOnMouseMove(String script)

    {

        addAttribute ( "onmousemove", script );

    }

    /**

        The onmouseout event occurs when the pointing device is moved away from

        an element. This attribute may be used with most elements.



        @param The script

    */

    public void setOnMouseOut(String script)

    {

        addAttribute ( "onmouseout", script );

    }



    /**

        The onkeypress event occurs when a key is pressed and released over an

        element. This attribute may be used with most elements.

        

        @param The script

    */

    public void setOnKeyPress(String script)

    {

        addAttribute ( "onkeypress", script );

    }



    /**

        The onkeydown event occurs when a key is pressed down over an element.

        This attribute may be used with most elements.

        

        @param The script

    */

    public void setOnKeyDown(String script)

    {

        addAttribute ( "onkeydown", script );

    }



    /**

        The onkeyup event occurs when a key is released over an element. This

        attribute may be used with most elements.

        

        @param The script

    */

    public void setOnKeyUp(String script)

    {

        addAttribute ( "onkeyup", script );

    }

}

