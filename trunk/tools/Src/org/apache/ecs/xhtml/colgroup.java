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

    This class creates a &lt;colgroup&gt; object.



    @version $Id: colgroup.java,v 1.1 2002/08/23 21:49:07 jjanke Exp $

    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>

    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>

    @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>

*/

public class colgroup extends MultiPartElement implements Printable

{

    /**

        private initializer.

    */

    {

        setElementType("colgroup");

        setCase(LOWERCASE);

        setAttributeQuote(true);

    }

    public colgroup()

    {

    }



    /**

        Sets the span="" attribute.

        @param span    sets the span="" attribute.

    */

    public colgroup setSpan(String span)

    {

        addAttribute("span",span);

        return(this);

    }



    /**

        Sets the span="" attribute.

        @param span    sets the span="" attribute.

    */

    public colgroup setSpan(int span)

    {

        addAttribute("span",Integer.toString(span));

        return(this);

    }



    /**

        Supplies user agents with a recommended cell width.  (Pixel Values)

        @param width    how many pixels to make cell

    */

    public colgroup setWidth(int width)

    {

        addAttribute("width",Integer.toString(width));

        return(this);

    }

    

    /**

        Supplies user agents with a recommended cell width.  (Pixel Values)

        @param width    how many pixels to make cell

    */

    public colgroup setWidth(String width)

    {

        addAttribute("width",width);

        return(this);

    }



    /**

        Sets the align="" attribute convience variables are provided in the AlignType interface

        @param  align   Sets the align="" attribute

    */

    public colgroup setAlign(String align)

    {

        addAttribute("align",align);

        return(this);

    }



    /**

        Sets the valign="" attribute convience variables are provided in the AlignType interface

        @param  valign   Sets the valign="" attribute

    */

    public colgroup setVAlign(String valign)

    {

        addAttribute("valign",valign);

        return(this);

    }



    /**

        Sets the char="" attribute.

        @param character    the character to use for alignment.

    */

    public colgroup setChar(String character)

    {

        addAttribute("char",character);

        return(this);

    }



    /**

        Sets the charoff="" attribute.

        @param char_off When present this attribute specifies the offset

        of the first occurrence of the alignment character on each line.

    */

    public colgroup setCharOff(int char_off)

    {

        addAttribute("charoff",Integer.toString(char_off));

        return(this);

    }



    /**

        Sets the charoff="" attribute.

        @param char_off When present this attribute specifies the offset

        of the first occurrence of the alignment character on each line.

    */

    public colgroup setCharOff(String char_off)

    {

        addAttribute("charoff",char_off);

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

    public colgroup addElement(String hashcode,Element element)

    {

        addElementToRegistry(hashcode,element);

        return(this);

    }



    /**

        Adds an Element to the element.

        @param  hashcode name of element for hash table

        @param  element Adds an Element to the element.

     */

    public colgroup addElement(String hashcode,String element)

    {

        addElementToRegistry(hashcode,element);

        return(this);

    }

    /**

        Adds an Element to the element.

        @param  element Adds an Element to the element.

     */

    public colgroup addElement(Element element)

    {

        addElementToRegistry(element);

        return(this);

    }



    /**

        Adds an Element to the element.

        @param  element Adds an Element to the element.

     */

    public colgroup addElement(String element)

    {

        addElementToRegistry(element);

        return(this);

    }

    /**

        Removes an Element from the element.

        @param hashcode the name of the element to be removed.

    */

    public colgroup removeElement(String hashcode)

    {

        removeElementFromRegistry(hashcode);

        return(this);

    }

}

