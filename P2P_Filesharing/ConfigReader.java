import java.io.File;
import java.net.InetAddress;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import at.ac.tuwien.cn2010.SuperPeerInformation;

public class ConfigReader {

	private String filename_;

	public ConfigReader(String filename) {
		this.filename_ = filename;
	}

	public ConfigReaderResult ReadConfig() {

		ConfigReaderResult result = new ConfigReaderResult();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			Document doc = dbf.newDocumentBuilder().parse(new File(filename_));
			
			/* My config */
			result.setMyAddress(InetAddress.getByName(doc.getDocumentElement().getAttribute("myHostaddress")));
			result.setMyPort(Integer.parseInt(doc.getDocumentElement().getAttribute("myPort")));

			/* Neighbours */
			NodeList nodeLst = doc.getElementsByTagName("peer");

			for (int s = 0; s < nodeLst.getLength(); s++) {

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

					SuperPeerInformation info = new SuperPeerInformation();

					String peerURL = getElement((Element) fstNode, "peerurl");
					String serviceName = getElement((Element) fstNode, "servicename");
					String namespace = getElement((Element) fstNode, "namespace");
					
					info.setPeerURL(new URL(peerURL));
					info.setServiceName(new QName(namespace, serviceName));
					
					result.getPeersListReference().add(info);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private String getElement(Element element, String name) {
		NodeList fstNmElmntLst = element.getElementsByTagName(name);
		Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
		NodeList fstNm = fstNmElmnt.getChildNodes();

		return ((Node) fstNm.item(0)).getNodeValue();
	}
}
