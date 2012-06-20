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

    This class creates an &lt;object&gt; tag.



    @version $Id: object.java,v 1.1 2002/08/23 21:49:07 jjanke Exp $

    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>

    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>

    @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>

*/

public class object extends MultiPartElement implements Printable, MouseEvents, KeyEvents

{

    /**

        Private initializer.

    */

    {

        setElementType("object");

        setCase(LOWERCASE);

        setAttributeQuote(true);

    }



    /**

        Default constructor.  Creates the &lt;object/&gt; element.<br>

        use set* methods.

    */

    public object()

    {

    }



    /**

        Sets the declare attribute.  (declare this object but don't instantiate it.

        @param declare  declare on or off

    */

    public object setDeclare(boolean declare)

    {

        if(declare)

            addAttribute("declare","declare");

        else

            removeAttribute("declare");

        return(this);

    }



    /**

        Identifies an implementation.

        @param url  location of classid.

    */

    public object setClassId(String url)

    {

        addAttribute("classid",url);

        return(this);

    }



    /**

        Sets the Internet content type for the code.

        @param codetype  Sets the Internet content type for the code.

    */

    public object setCodeType(String codetype)

    {

        addAttribute("codetype",codetype);

        return(this);

    }



    /**

        Determines the base path to resolve relative urls specified by classid.

        @param url base path to resolve relative urls specified by classid.

    */

    public object setCodeBase(String url)

    {

        addAttribute("codebase",url);

        return(this);

    }



    /**

        This attribute specifies the location of the data to be rendered.

        @param url this attribute specifies the location of the data to be rendered.

    */

    public object setData(String url)

    {

        addAttribute("data",url);

        return(this);

    }



    /**

        This attribute specifies the Internet Media Type for the data specified by data.<br>

        This should be a mime type.

        @param type a mime type for the data specifed by the data attribute.

    */

    public object setType(String type)

    {

        addAttribute("type",type);

        return(this);

    }



    /**

        Space seperated archive list.

        @param url Space seperate archive list.

    */

    // Anyone know what the hell this is?  the spec is rather vague in its definition.

    public object setArchive(String url)

    {

        addAttribute("archive",url);

        return(this);

    }



    /**

        Message to show while the object is loading.

        @param cdata the message to show while the object is loading.

    */

    public object setStandBy(String cdata)

    {

        addAttribute("standby",cdata);

        return(this);

    }



    /**

        Suggested link border width.

        @param border suggested link border width.

    */

    public object setBorder(String border)

    {

        addAttribute("border",border);

        return(this);

    }



    /**

        Suggested link border width.

        @param border suggested link border width.

    */

    public object setBorder(int border)

    {

        addAttribute("border",Integer.toString(border));

        return(this);

    }



    /**

        Suggested link border width.

        @param border suggested link border width.

    */

    public object setBorder(double border)

    {

        addAttribute("border",Double.toString(border));

        return(this);

    }



    /**

        Suggested height of object.

        @param height suggested link height.

    */

    public object setHeight(String height)

    {

        addAttribute("height",height);

        return(this);

    }



    /**

        Suggested height of object.

        @param height suggested link height.

    */

    public object setHeight(int height)

    {

        addAttribute("height",Integer.toString(height));

        return(this);

    }



    /**

        Suggested height of object.

        @param height suggested link height.

    */

    public object setHeight(double height)

    {

        addAttribute("height",Double.toString(height));

        return(this);

    }



    /**

        Suggested width of object.

        @param height suggested link width.

    */

    public object setWidth(String width)

    {

        addAttribute("width",width);

        return(this);

    }



    /**

        Suggested width of object.

        @param height suggested link width.

    */

    public object setWidth(int width)

    {

        addAttribute("width",Integer.toString(width));

        return(this);

    }



    /**

        Suggested width of object.

        @param height suggested link width.

    */

    public object setWidth(double width)

    {

        addAttribute("width",Double.toString(width));

        return(this);

    }



    /**

        Suggested horizontal gutter.

        @param hspace suggested horizontal gutter.

    */

    public object setHSpace(String hspace)

    {

        addAttribute("hspace",hspace);

        return(this);

    }



    /**

        Suggested horizontal gutter.

        @param hspace suggested horizontal gutter.

    */

    public object setHSpace(int hspace)

    {

        addAttribute("hspace",Integer.toString(hspace));

        return(this);

    }



    /**

        Suggested horizontal gutter.

        @param hspace suggested horizontal gutter.

    */

    public object setHSpace(double hspace)

    {

        addAttribute("hspace",Double.toString(hspace));

        return(this);

    }



    /**

        Suggested vertical gutter.

        @param hspace suggested vertical gutter.

    */

    public object setVSpace(String vspace)

    {

        addAttribute("vspace",vspace);

        return(this);

    }



    /**

        Suggested vertical gutter.

        @param hspace suggested vertical gutter.

    */

    public object setVSpace(int vspace)

    {

        addAttribute("vspace",Integer.toString(vspace));

        return(this);

    }



    /**

        Suggested vertical gutter.

        @param hspace suggested vertical gutter.

    */

    public object setVSpace(double vspace)

    {

        addAttribute("vspace",Double.toString(vspace));

        return(this);

    }



    /**

        Set the horizontal or vertical alignment of this object.<br>

        Convience variables are in the AlignTypes interface.

        @param alignment Set the horizontal or vertical alignment of this object.<br>

        Convience variables are in the AlignTypes interface.

    */

    public object setAlign(String alignment)

    {

        addAttribute("align",alignment);

        return(this);

    }



    /**

        Location of image map to use.

        @param url location of image map to use.

    */

    public object setUseMap(String url)

    {

        addAttribute("usemap",url);

        return(this);

    }



    /**

        Object has shaped hypertext links.

        @param shape    does the object have shaped hypertext links?

    */

    public object setShapes(boolean shape)

    {

        if(shape)

            addAttribute("shapes","shapes");

        else

            removeAttribute("shapes");

        return(this);

    }



    /**

        Set the name of this object.

        @param name set the name of this object.

    */

    public object setName(String name)

    {

        addAttribute("name",name);

        return(this);

    }



    /**

        Set the elements position in the tabbing order.

        @param number set the elements position in the tabbing order.

    */

    public object setTabIndex(int number)

    {

        addAttribute("tabindex",Integer.toString(number));

        return(this);

    }



    /**

        Set the elements position in the tabbing order.

        @param number set the elements position in the tabbing order.

    */

    public object setTabIndex(String number)

    {

        addAttribute("tabindex",number);

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

    public object addElement(String hashcode,Element element)

    {

        addElementToRegistry(hashcode,element);

        return(this);

    }



    /**

        Adds an Element to the element.

        @param  hashcode name of element for hash table

        @param  element Adds an Element to the element.

     */

    public object addElement(String hashcode,String element)

    {

        addElementToRegistry(hashcode,element);

        return(this);

    }



    /**

        Add an element to the element

        @param element a string representation of the element

    */

    public object addElement(String element)

    {

        addElementToRegistry(element);

        return(this);

    }



    /**

        Add an element to the element

        @param element  an element to add

    */

    public object addElement(Element element)

    {

        addElementToRegistry(element);

        return(this);

    }

    /**

        Removes an Element from the element.

        @param hashcode the name of the element to be removed.

    */

    public object removeElement(String hashcode)

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

