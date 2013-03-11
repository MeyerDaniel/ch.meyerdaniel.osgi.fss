package ch.meyerdaniel.osgi.fss.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {

	public static NodeList getNodeList(Document doc, String xPathAsString) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile(xPathAsString);
		return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
	}

	public static NodeList getNodeList(Node node, String xPathAsString) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile(xPathAsString);
		return (NodeList) expr.evaluate(node, XPathConstants.NODESET);
	}

	public static String getAttributeValue(Node node, String attributeName) {
		if (node == null || node.getAttributes() == null || node.getAttributes().getNamedItem(attributeName) == null) {
			return null;
		}
		return node.getAttributes().getNamedItem(attributeName).getNodeValue();
	}

	public static Node getUniqueNode(Node node, String xPathAsString) throws XPathExpressionException {
		NodeList nodeList = getNodeList(node, xPathAsString);
		return nodeList == null || nodeList.getLength() == 0 ? null : nodeList.item(0);
	}

	public static String getTextContent(Node node) {
		if (node == null) {
			return null;
		}
		return node.getTextContent();
	}
}
