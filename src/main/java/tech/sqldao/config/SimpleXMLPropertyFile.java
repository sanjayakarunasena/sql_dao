/**
 * Copyright 2015 Sanjaya Karunasena
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.sqldao.config;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Sanjaya Karunasena
 *
 */
public class SimpleXMLPropertyFile {
    /**
     * Property document created from the XML property file.
     */
    protected Document propertyDocument;

    public static SimpleXMLPropertyFile load(File xmlPropertyFile) throws ParserConfigurationException, SAXException,
            IOException {
        SimpleXMLPropertyFile propertyFile = new SimpleXMLPropertyFile();
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        propertyFile.setPropertyDocument(docBuilder.parse(xmlPropertyFile));
        return propertyFile;
    }

    /**
     * @param document
     *            document to set as the property document
     */
    protected void setPropertyDocument(Document document) {
        propertyDocument = document;
    }

    /**
     * Given the tag name retrieve the value.
     * 
     * @param tagName
     *            Tag name to find the value.
     * @return Value of the given tag
     * @throws SimpleXMLPropertyException
     */
    public String getValue(String tagName) throws SimpleXMLPropertyException {
        NodeList nodes = propertyDocument.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Node childNode = element.getFirstChild();
                return (childNode != null) ? childNode.getNodeValue() : null;
            } else {
                throw new SimpleXMLPropertyException(tagName + " is not a element!");
            }
        } else {
            throw new SimpleXMLPropertyException("No nodes with the name " + tagName);
        }
    }

    /**
     * Given the tag name retrieve all matching tag values.
     * 
     * @param tagName
     *            Tag name to find the matching tag values.
     * @return Matching values for the given tag
     * @throws SimpleXMLPropertyException
     */
    public String[] getValues(String tagName) throws SimpleXMLPropertyException {
        NodeList nodes = propertyDocument.getElementsByTagName(tagName);
        int length = nodes.getLength();
        if (length > 0) {
            String[] values = new String[length];
            for (int i = 0; i < length; i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Node childNode = element.getFirstChild();
                    values[i] = (childNode != null) ? childNode.getNodeValue() : null;
                } else {
                    throw new SimpleXMLPropertyException(tagName + " is not a element!");
                }
            }
            return values;
        } else {
            throw new SimpleXMLPropertyException("No nodes with the name " + tagName);
        }
    }

    /**
     * Given the tag name and the attribute name retrieve the attribute value.
     * 
     * @param tagName
     *            Tag name to find the attribute
     * @param attributeName
     *            Attribute name to retrieve the value
     * @return Attribute value
     * @throws SimpleXMLPropertyException
     */
    public String getAttribute(String tagName, String attributeName) throws SimpleXMLPropertyException {
        NodeList nodes = propertyDocument.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                return element.getAttribute(attributeName);
            } else {
                throw new SimpleXMLPropertyException(tagName + " is not a element!");
            }
        } else {
            throw new SimpleXMLPropertyException("No nodes with the name " + tagName);
        }
    }

    /**
     * Given the tag name and the attribute name retrieve the attribute values of matching tags.
     * 
     * @param tagName
     *            Tag name to find the attribute
     * @param attributeName
     *            Attribute name to retrieve the value
     * @return Attribute values
     * @throws SimpleXMLPropertyException
     */
    public String[] getAttributes(String tagName, String attributeName) throws SimpleXMLPropertyException {
        NodeList nodes = propertyDocument.getElementsByTagName(tagName);
        int length = nodes.getLength();
        if (length > 0) {
            String[] values = new String[length];
            for (int i = 0; i < length; i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    values[i] = element.getAttribute(attributeName);
                } else {
                    throw new SimpleXMLPropertyException(tagName + " is not a element!");
                }
            }
            return values;
        } else {
            throw new SimpleXMLPropertyException("No nodes with the name " + tagName);
        }
    }
}
