package com.hwaipy.utilities.system;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 封装系统属性。属性从多个地方载入，依次为： 内存， PROPERTIES_FILE_PATH(utilitiesproperties.xml)
 * 系统preferences 对属性的修改修改体现在内存中。
 *
 * @author Hwaipy
 */
public class PropertiesUtilities {

    private static final String PROPERTIES_FILE_PATH = "utilitiesproperties.xml";
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(PropertiesUtilities.class);
    private static final Map<String, String> PROPERTIES = new HashMap<>();
    private static final Map<String, String> PROPERTIES_FILE = new HashMap<>();

    static {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            Document document = dbBuilder.parse(PROPERTIES_FILE_PATH);
            Node root = document.getFirstChild();
            if (root != null && "properties".equals(root.getNodeName())) {
                NodeList childNodes = root.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if ("property".equals(node.getNodeName())) {
                        NamedNodeMap attributes = node.getAttributes();
                        Node keyNode = attributes.getNamedItem("key");
                        Node valueNode = attributes.getNamedItem("value");
                        if (keyNode != null && valueNode != null) {
                            String key = keyNode.getNodeValue();
                            String value = valueNode.getNodeValue();
                            if (key != null && !key.isEmpty() && value != null && !value.isEmpty()) {
                                PROPERTIES_FILE.put(key, value);
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
        }
    }

    public static String getProperty(String key) {
        String property = PROPERTIES.get(key);
        if (property == null) {
            property = PROPERTIES_FILE.get(key);
        }
        if (property == null) {
            property = PREFERENCES.get(key, null);
        }
        return property;
    }

    public static String getProperty(String key, String def) {
        String property = getProperty(key);
        return property == null ? def : property;
    }

    public static String setProperty(String key, String value) {
        return PROPERTIES.put(key, value);
    }

    public static String removeProperty(String key) {
        return PROPERTIES.remove(key);
    }
}
