/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.issues;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;

public class JXPath113Test extends JXPathTestCase {

    public void testIssue113() throws Exception {
        Document doc = JAXP.getDocument("<xml/>");
        JXPathContext context = JXPathContext.newContext(doc);
        context.selectNodes("//following-sibling::node()");
    }

    static class JAXP {

        public static Document getDocument(String xml) throws Exception {
            return getDocument(new InputSource(new StringReader(xml)));
        }

        public static Document getDocument(InputSource is) throws Exception {

            final DocumentBuilder builder = getDocumentBuilder();
            return builder.parse(is);
        }

        private static DocumentBuilder getDocumentBuilder() {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(false);
                factory.setNamespaceAware(true);
                factory.setExpandEntityReferences(false);
                return factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new Error("JAXP config error:" + e.getMessage(), e);
            }

        }
    }

}
