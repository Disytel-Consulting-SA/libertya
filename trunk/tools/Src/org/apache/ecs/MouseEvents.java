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
    This interface is intended to be implemented by elements that require
    javascript mouse event attributes.

    @version $Id: MouseEvents.java,v 1.2 2001/11/06 03:43:32 jjanke Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public interface MouseEvents
{
    /**
        make sure implementing classes have a setOnClick method.

        The onclick event occurs when the pointing device button is clicked
        over an element. This attribute may be used with most elements.
    */
    public abstract void setOnClick(String script);

    /**
        make sure implementing classes have a setOnDblClick method.

        The ondblclick event occurs when the pointing device button is double
        clicked over an element. This attribute may be used with most elements.
    */
    public abstract void setOnDblClick(String script);

    /**
        make sure implementing classes have a setOnMouseDown method.

        The onmousedown event occurs when the pointing device button is pressed
        over an element. This attribute may be used with most elements.
    */
    public abstract void setOnMouseDown(String script);

    /**
        make sure implementing classes have a setOnMouseUp method.

        The onmouseup event occurs when the pointing device button is released
        over an element. This attribute may be used with most elements.
    */
    public abstract void setOnMouseUp(String script);

    /**
        make sure implementing classes have a setOnMouseOver method.

        The onmouseover event occurs when the pointing device is moved onto an
        element. This attribute may be used with most elements.
    */
    public abstract void setOnMouseOver(String script);

    /**
        make sure implementing classes have a setOnMouseMove method.

        The onmousemove event occurs when the pointing device is moved while it
        is over an element. This attribute may be used with most elements.
    */
    public abstract void setOnMouseMove(String script);

    /**
        make sure implementing classes have a setOnMouseOut method.

        The onmouseout event occurs when the pointing device is moved away from
        an element. This attribute may be used with most elements.
    */
    public abstract void setOnMouseOut(String script);
}
