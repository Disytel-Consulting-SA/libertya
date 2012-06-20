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
package org.apache.ecs.xml;

import java.io.Serializable;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.xml.XML;
import org.apache.ecs.xml.PI;

/**
 * XMLDocument
 *
 * This is the container for XML elements that can be used similar to
 *   org.apache.ecs.Document.  However, it correctly handles XML elements
 *   and doesn't have any notion of a head, body, etc., that is associated
 *   with HTML documents.
 *
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 */
public class XMLDocument implements Serializable, Cloneable {
    
    /** Default Version */
    private static final float DEFAULT_XML_VERSION = 1.0f;
    
    /** Version Declaration - FIXME!! */
    private String versionDecl;
    
    /** Prolog */
    private Vector prolog;
    
    /** "Body" of document */
    private XML content;
    
    /** @serial codeset codeset */
    private String codeset = null;
    
    /**
     * This sets the document up.  Since an XML document can be 
     *   pretty much anything you want, all it does is create an
     *   XML Instruction for the default version and sets the 
     *   document to be standalone.
     */
    public XMLDocument() {
        this(DEFAULT_XML_VERSION, true);
    }
    
    /**
     * This sets the document up. Since an XML document can
     *   be pretty much anything, all this does is create the
     *   XML Instruction for the version specified and set the
     *   document to be standalone.
     *
     * @param version - version of XML this document is
     */
    public XMLDocument(double version) {
        this(version, true);
    }
    
    /**
     * This sets the document up.  Since an XML document can be
     *   pretty much anything, all this does is create the 
     *   XML Instruction with the version specified, and 
     *   identifies the document as standalone if set
     *
     * @param version - version of XML document is
     * @param standalone - boolean: <code>true</code> if standalone, else false
     */
    public XMLDocument(double version, boolean standalone) {
        prolog = new Vector(2);
        StringBuffer versionStr = new StringBuffer();
        versionStr.append("<?xml version=\"");
        versionStr.append(version);
        versionStr.append("\" standalone=\"");
        if (standalone)
            versionStr.append("yes\"?>");
        else
            versionStr.append("no\"?>\n");
            
        this.versionDecl = versionStr.toString();
        
        /**
         * FIXME: ECS currently does not do any ordering of attributes.
         *   Although about 99% of the time, this has no problems,
         *   in the initial XML declaration, it can be a problem in
         *   certain frameworks (e.g. Cocoon/Xerces/Xalan).  So instead
         *   of adding an element here, we have to store this first command
         *   in a String and add it to the output at output time.
         */
        /**
        PI versionDecl = new PI().setTarget("xml");
                
        if (standalone)
            versionDecl.addInstruction("standalone", "yes");
        else
            versionDecl.addInstruction("standalone", "no");
            
        versionDecl.setVersion(version);            
        
        prolog.addElement(versionDecl);
        */
    }
    
    /**
     * This sets the document up.  Since an XML document can be
     *   pretty much anything, all this does is create the 
     *   XML Instruction with the version specified, and 
     *   identifies the document as standalone if set.  This also
     *   allows the codeset to be set as well.
     *
     * @param version - version of XML document is
     * @param standalone - boolean: <code>true</code if standalone, else false
     * @param codeset - String codeset to use
     */
    public XMLDocument(double version, boolean standalone, String codeset) {
        this(version, standalone);
        setCodeset(codeset);
    }    
    
    /**
     * This sets the codeset for this document
     *
     * @param codeset - String representation of codeset for this
     *                  document
     */
    public void setCodeset(String codeset) {
        this.codeset = codeset;
    }
    
    /** 
     * This gets the codeset for this document
     *
     * @return String the codeset for this document
     */
    public String getCodeset() {
        return codeset;
    }
    
    /**
     * This adds a stylesheet to the XML document.
     *
     * @param href - String reference to stylesheet
     * @param type - String type of stylesheet
     */
    public XMLDocument addStylesheet(String href, String type) {
        PI pi = new PI();
        pi.setTarget("xml-stylesheet")
          .addInstruction("href", href)
          .addInstruction("type", type);
        prolog.addElement(pi);
        
        return(this);
    }
    
    /**
     * This adds a stylesheet to the XML document, and assumes
     *   the default <code>text/xsl</code> type.
     *
     * @param href = String reference to stylesheet
     */
    public XMLDocument addStylesheet(String href) {
        return addStylesheet(href, "text/xsl");
    }
    
    /**
     * This adds the specified element to the prolog of the document
     *
     * @param element - Element to add
     */
    public XMLDocument addToProlog(ConcreteElement element) {
        prolog.addElement(element);
        return(this);
    }
    
    /**
     * This adds an element to the XML document.  If the
     *   document is empty, it sets the passed in element
     *   as the root element.
     *
     * @param element - XML Element to add
     * @return XMLDocument - modified document
     */
    public XMLDocument addElement(XML element) {
        if (content == null)
            content = element;
        else
            content.addElement(element);
            
        return(this);
    }
    
    /**
     * Write the document to the OutputStream
     *
     * @param out - OutputStream to write to
     */
    public void output(OutputStream out)
    {        
        /** 
         * FIXME: The other part of the version hack!
         *   Add the version declaration to the beginning of the document.
         */
        try {
            out.write(versionDecl.getBytes());
        } catch (Exception e) { }
        
        for (int i=0; i<prolog.size(); i++) {
            ConcreteElement e = (ConcreteElement)prolog.elementAt(i);
            e.output(out);
        }
         
        if (content != null)
            content.output(out);
    }

    /**
     * Write the document to the PrinteWriter
     *   
     * @param out - PrintWriter to write to
     */
    public void output(PrintWriter out)
    {
        
        /** 
         * FIXME: The other part of the version hack!
         *   Add the version declaration to the beginning of the document.
         */
        out.write(versionDecl);
        
        for (int i=0; i<prolog.size(); i++) {
            ConcreteElement e = (ConcreteElement)prolog.elementAt(i);
            e.output(out);
        }
        
        if (content != null)
            content.output(out);
    }    
    
    /**
     * Override toString so it does something useful
     *
     * @return String - representation of the document
     */
    public final String toString() {
        StringBuffer retVal = new StringBuffer();
        
        if (codeset != null) {
            for (int i=0; i<prolog.size(); i++) {
                ConcreteElement e = (ConcreteElement)prolog.elementAt(i);
                retVal.append(e.toString(getCodeset()) + "\n");
            }   
            
            if (content != null)
                retVal.append(content.toString(getCodeset()));
        } else {
            for (int i=0; i<prolog.size(); i++) {
                ConcreteElement e = (ConcreteElement)prolog.elementAt(i);
                retVal.append(e.toString() + "\n");
            }        
            
            if (content != null)
                retVal.append(content.toString());
        }
        
        /** 
         * FIXME: The other part of the version hack!
         *   Add the version declaration to the beginning of the document.
         */
        return versionDecl + retVal.toString();
    }
    
    /**
     * Override toString so it prints something useful
     *
     * @param codeset - String codeset to use
     * @return String - representation of the document
     */
    public final String toString(String codeset) {
        StringBuffer retVal = new StringBuffer();
        
        for (int i=0; i<prolog.size(); i++) {
            ConcreteElement e = (ConcreteElement)prolog.elementAt(i);
            retVal.append(e.toString(getCodeset()) + "\n");
        }        
        if (content != null)
            retVal.append(content.toString(getCodeset()) + "\n");
        
        /** 
         * FIXME: The other part of the version hack!
         *   Add the version declaration to the beginning of the document.
         */
        return versionDecl + retVal.toString();
    }
    
    /**
     * Clone this document
     *
     * @return Object - cloned XMLDocument
     */
    public Object clone() {
        return content.clone();
    }
    
}
    
