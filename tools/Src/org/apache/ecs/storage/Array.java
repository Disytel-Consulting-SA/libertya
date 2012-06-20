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
package org.apache.ecs.storage;

import java.io.Serializable;

public class Array implements java.util.Enumeration, Serializable
{
    private int current = 0;
    private int size = 10;
    private int grow = 2;
    private int place = 0;
    private Object[] elements = null;
    private Object[] tmpElements = null;

    public Array()
    {
        init();
    }

    public Array(int size)
    {
        setSize(size);
        init();
    }

    public Array(int size,int grow)
    {
        setSize(size);
        setGrow(grow);
        init();
    }

    private void init()
    {
        elements = new Object[size];
    }

    public Object nextElement() throws java.util.NoSuchElementException
    {
        if ( elements[place] != null && place != current)
        {
            place++;
            return elements[place - 1];
        }
        else
        {
            place = 0;
            throw new java.util.NoSuchElementException();
        }
    }

    public boolean hasMoreElements()
    {
        if( place < elements.length && current != place )
            return true;
        return false;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getCurrentSize()
    {
        return current;
    }

    public void rehash()
    {
        tmpElements = new Object[size];
        int count = 0;
        for ( int x = 0; x < elements.length; x++ )
        {
            if( elements[x] != null )
            {
                tmpElements[count] = elements[x];
                count++;
            }
        }
        elements = (Object[])tmpElements.clone();
        tmpElements = null;
        current = count;
    }

    public void setGrow(int grow)
    {
        this.grow = grow;
    }

    public void grow()
    {
        size = size+=(size/grow);
        rehash();
    }

    public void add(Object o)
    {
        if( current == elements.length )
            grow();

        try
        {
            elements[current] = o;
            current++;
        }
        catch(java.lang.ArrayStoreException ase)
        {
        }
    }

    public void add(int location,Object o)
    {
        try
        {
            elements[location] = o;
        }
        catch(java.lang.ArrayStoreException ase)
        {
        }
    }

    public void remove(int location)
    {
        elements[location] = null;
    }

    public int location(Object o) throws NoSuchObjectException
    {
        int loc = -1;
        for ( int x = 0; x < elements.length; x++ )
        {
            if((elements[x] != null && elements[x] == o )||
               (elements[x] != null && elements[x].equals(o)))
            {
                loc = x;
                break;
            }
        }
        if( loc == -1 )
            throw new NoSuchObjectException();
        return(loc);
    }

    public Object get(int location)
    {
        return elements[location];
    }

    public java.util.Enumeration elements()
    {
        return this;
    }
}
