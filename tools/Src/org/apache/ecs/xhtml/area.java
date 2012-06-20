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

    This class creates a &lt;area&gt; tag.



    @version $Id: area.java,v 1.1 2002/08/23 21:49:07 jjanke Exp $

    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>

    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>

    @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>

*/

public class area extends SinglePartElement implements Printable, FocusEvents, MouseEvents, KeyEvents

{

    public static final String DEFAULT = "default";



    public static final String rect = "rect";

    public static final String circle = "circle";

    public static final String poly = "poly";

    

    /**

        Private initialization routine.

    */

    {

        setElementType("area");

        setCase(LOWERCASE);

        setAttributeQuote(true);

        setBeginEndModifier('/');

        setNoHref(true);

    }

    

    /**

        Basic constructor. Use the set* methods to set the values

        of the attributes.

    */

    public area()

    {

    }



    /**

        Use the set* methods to set the values

        of the attributes.



        @param   shape  the shape="" attribute

    */

    public area(String shape)

    {

        setShape(shape);

    }



    /**

        Use the set* methods to set the values

        of the attributes.



        @param   shape  the shape="" attribute

        @param   coords  the coords="" attribute

    */

    public area(String shape, String coords)

    {

        setShape(shape);

        setCoords(coords);

    }



    /**

        Use the set* methods to set the values

        of the attributes.



        @param   shape  the shape="" attribute

        @param   coords  the coords="" attribute

    */

    public area(String shape, int[] coords)

    {

        setShape(shape);

        setCoords(coords);

    }



    /**

        Use the set* methods to set the values

        of the attributes.



        @param   shape  the shape="" attribute

        @param   coords  the coords="" attribute

        @param   href  the href="" attribute

    */

    public area(String shape, String coords, String href)

    {

        setShape(shape);

        setCoords(coords);

        setHref(href);

    }



    /**

        Use the set* methods to set the values

        of the attributes.



        @param   shape  the shape="" attribute

        @param   coords  the coords="" attribute

        @param   href  the href="" attribute

    */

    public area(String shape, int[] coords, String href)

    {

        setShape(shape);

        setCoords(coords);

        setHref(href);

    }



    /**

        Sets the shape="" attribute

        @param   shape  the shape="" attribute

    */

    public area setShape(String shape)

    {

        addAttribute("shape",shape);

        return this;

    }



    /**

        Sets the coords="" attribute

        @param   coords  the coords="" attribute

    */

    public area setCoords(String coords)

    {

        addAttribute("coords",coords);

        return this;

    }

    

    /**

        Sets the coords="" attribute

        @param   coords  the coords="" attribute

    */

    public area setCoords(int[] coords)

    {

        addAttribute("coords", coords[0] + "," + coords[1] + "," + 

                                      coords[2] + "," + coords[3]);

        return this;

    }



    /**

        Sets the href="" attribute

        @param   href  the href="" attribute

    */

    public area setHref(String href)

    {

        addAttribute("href",href);

        setNoHref(false);

        return this;

    }

    

    /**

        Sets the alt="" attribute

        @param   alt  the alt="" attribute

    */

    public area setAlt(String alt)

    {

        addAttribute("alt",alt);

        return this;

    }

    

    /**

        Sets the tabindex="" attribute

        @param   alt  the tabindex="" attribute

    */

    public area setTabindex(String index)

    {

        addAttribute("tabindex",index);

        return this;

    }

    

    /**

        Sets the tabindex="" attribute

        @param   alt  the tabindex="" attribute

    */

    public area setTabindex(int index)

    {

        setTabindex(Integer.toString(index));

        return this;

    }

    

    /**

        Sets the nohref

        @param   href  true or false

    */

    public area setNoHref(boolean href)

    {

        if ( href == true )

            addAttribute("nohref", "nohref");

        else

            removeAttribute("nohref");

            

        return(this);

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

    public area addElement(String hashcode,Element element)

    {

        addElementToRegistry(hashcode,element);

        return(this);

    }



    /**

        Adds an Element to the element.

        @param  hashcode name of element for hash table

        @param  element Adds an Element to the element.

     */

    public area addElement(String hashcode,String element)

    {

        addElementToRegistry(hashcode,element);

        return(this);

    }



    /**

        Add an element to the element

        @param element a string representation of the element

    */

    public area addElement(String element)

    {

        addElementToRegistry(element);

        return(this);

    }



    /**

        Add an element to the element

        @param element  an element to add

    */

    public area addElement(Element element)

    {

        addElementToRegistry(element);

        return(this);

    }

    /**

        Removes an Element from the element.

        @param hashcode the name of the element to be removed.

    */

    public area removeElement(String hashcode)

    {

        removeElementFromRegistry(hashcode);

        return(this);

    }



    /**

        The onfocus event occurs when an element receives focus either by the

        pointing device or by tabbing navigation. This attribute may be used

        with the following elements: LABEL, INPUT, SELECT, TEXTAREA, and

        BUTTON.

        

        @param The script

    */

    public void setOnFocus(String script)

    {

        addAttribute ( "onfocus", script );

    }



    /**

        The onblur event occurs when an element loses focus either by the

        pointing device or by tabbing navigation. It may be used with the same

        elements as onfocus.

        

        @param The script

    */

    public void setOnBlur(String script)

    {

        addAttribute ( "onblur", script );

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

