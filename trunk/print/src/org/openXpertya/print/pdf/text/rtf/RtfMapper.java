/*
 * $Id: RtfMapper.java,v  1.0 $
 * $Name:  $
 *
 * Copyright 2003, 2004 by Mark Hall
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the ?GNU LIBRARY GENERAL PUBLIC LICENSE?), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */

package org.openXpertya.print.pdf.text.rtf;

import org.openXpertya.print.pdf.text.Anchor;
import org.openXpertya.print.pdf.text.Annotation;
import org.openXpertya.print.pdf.text.Chapter;
import org.openXpertya.print.pdf.text.Chunk;
import org.openXpertya.print.pdf.text.DocumentException;
import org.openXpertya.print.pdf.text.Element;
import org.openXpertya.print.pdf.text.Image;
import org.openXpertya.print.pdf.text.List;
import org.openXpertya.print.pdf.text.ListItem;
import org.openXpertya.print.pdf.text.Meta;
import org.openXpertya.print.pdf.text.Paragraph;
import org.openXpertya.print.pdf.text.Phrase;
import org.openXpertya.print.pdf.text.Section;
import org.openXpertya.print.pdf.text.Table;
import org.openXpertya.print.pdf.text.rtf.document.RtfDocument;
import org.openXpertya.print.pdf.text.rtf.document.RtfInfoElement;
import org.openXpertya.print.pdf.text.rtf.field.RtfAnchor;
import org.openXpertya.print.pdf.text.rtf.graphic.RtfImage;
import org.openXpertya.print.pdf.text.rtf.list.RtfList;
import org.openXpertya.print.pdf.text.rtf.list.RtfListItem;
import org.openXpertya.print.pdf.text.rtf.table.RtfTable;
import org.openXpertya.print.pdf.text.rtf.text.RtfAnnotation;
import org.openXpertya.print.pdf.text.rtf.text.RtfChapter;
import org.openXpertya.print.pdf.text.rtf.text.RtfChunk;
import org.openXpertya.print.pdf.text.rtf.text.RtfNewPage;
import org.openXpertya.print.pdf.text.rtf.text.RtfParagraph;
import org.openXpertya.print.pdf.text.rtf.text.RtfPhrase;
import org.openXpertya.print.pdf.text.rtf.text.RtfSection;


/**
 * The RtfMapper provides mappings between org.openXpertya.print.pdf.text.* classes
 * and the corresponding org.openXpertya.print.pdf.text.rtf.** classes.
 * 
 * @version $Version:$
 * @author Mark Hall (mhall@edu.uni-klu.ac.at)
 */
public class RtfMapper {

    /**
     * The RtfDocument this RtfMapper belongs to
     */
    RtfDocument rtfDoc;
    
    /**
     * Constructs a RtfMapper for a RtfDocument
     * 
     * @param doc The RtfDocument this RtfMapper belongs to
     */
    public RtfMapper(RtfDocument doc) {
        this.rtfDoc = doc;
    }
    
    /**
     * Takes an Element subclass and returns the correct RtfBasicElement
     * subclass, that wraps the Element subclass.
     * 
     * @param element The Element to wrap
     * @return A RtfBasicElement wrapping the Element
     * @throws DocumentException
     */
    public RtfBasicElement mapElement(Element element) throws DocumentException {
        RtfBasicElement rtfElement = null;

        if(element instanceof RtfBasicElement) {
            rtfElement = (RtfBasicElement) element;
            rtfElement.setRtfDocument(this.rtfDoc);
            return rtfElement;
        }
        switch(element.type()) {
    		case Element.CHUNK:
    		    if(((Chunk) element).getImage() != null) {
                    rtfElement = new RtfImage(rtfDoc, ((Chunk) element).getImage());
    		    } else if(((Chunk) element).hasAttributes() && ((Chunk) element).getAttributes().containsKey(Chunk.NEWPAGE)) {
    		        rtfElement = new RtfNewPage(rtfDoc);
    		    } else {
    		        rtfElement = new RtfChunk(rtfDoc, (Chunk) element);
    		    }
    			break;
    		case Element.PHRASE:
    		    rtfElement = new RtfPhrase(rtfDoc, (Phrase) element);
    			break;
    		case Element.PARAGRAPH:
    		    rtfElement = new RtfParagraph(rtfDoc, (Paragraph) element);
    			break;
    		case Element.ANCHOR:
    			rtfElement = new RtfAnchor(rtfDoc, (Anchor) element);
    			break;
    		case Element.ANNOTATION:
    		    rtfElement = new RtfAnnotation(rtfDoc, (Annotation) element);
    			break;
            case Element.IMGRAW:
            case Element.IMGTEMPLATE:
            case Element.JPEG:
                rtfElement = new RtfImage(rtfDoc, (Image) element);
            	break;
    		case Element.AUTHOR: 
    		case Element.SUBJECT:
    		case Element.KEYWORDS:
    		case Element.TITLE:
    		case Element.PRODUCER:
    		case Element.CREATIONDATE:
    		    rtfElement = new RtfInfoElement(rtfDoc, (Meta) element);
    			break;
    		case Element.LIST:
    		    rtfElement = new RtfList(rtfDoc, (List) element);
    			break;
    		case Element.LISTITEM:
    		    rtfElement = new RtfListItem(rtfDoc, (ListItem) element);
    			break;
    		case Element.SECTION:
    		    rtfElement = new RtfSection(rtfDoc, (Section) element);
    			break;
    		case Element.CHAPTER:
    		    rtfElement = new RtfChapter(rtfDoc, (Chapter) element);
    			break;
    		case Element.TABLE:
    		    rtfElement = new RtfTable(rtfDoc, (Table) element);
    			break;
        }
        
        return rtfElement;
    }
}
