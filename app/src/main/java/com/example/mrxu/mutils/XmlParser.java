package com.example.mrxu.mutils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by MrXu on 2017/5/14.
 */

public class XmlParser {


    private HashMap<String, String> data;
    private DocumentBuilder builder;

    public XmlParser() throws ParserConfigurationException {
        data = new HashMap<String, String>();
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    public HashMap<String, String> parse(String xmlResourse)
            throws SAXException, IOException {
        NodeList list = getData(xmlResourse);
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            // 由于可能需要解析<head>的内容 此处不再过滤
            // if (node.getNodeName().startsWith("Field")) {
            data.put(node.getNodeName(), node.getTextContent());
            // }
        }
        return data;
    }

    private NodeList getData(String xmlResourse) throws SAXException,
            IOException {
        Document document = builder.parse(new InputSource(
                new ByteArrayInputStream(xmlResourse
                        .substring(
                                xmlResourse.indexOf("<root>"),
                                xmlResourse.lastIndexOf("</root>")
                                        + "</root>".length()).getBytes())));
        return document.getDocumentElement().getElementsByTagName("*");
    }
}
