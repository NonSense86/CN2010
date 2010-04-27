
package at.ac.tuwien.cn2010.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the at.ac.tuwien.cn2010.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetAllSuperPeersResponse_QNAME = new QName("http://client.cn2010.tuwien.ac.at", "getAllSuperPeersResponse");
    private final static QName _PeerInformation_QNAME = new QName("http://client.cn2010.tuwien.ac.at", "peerInformation");
    private final static QName _GetAllSuperPeers_QNAME = new QName("http://client.cn2010.tuwien.ac.at", "getAllSuperPeers");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: at.ac.tuwien.cn2010.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetAllSuperPeers }
     * 
     */
    public GetAllSuperPeers createGetAllSuperPeers() {
        return new GetAllSuperPeers();
    }

    /**
     * Create an instance of {@link GetAllSuperPeersResponse }
     * 
     */
    public GetAllSuperPeersResponse createGetAllSuperPeersResponse() {
        return new GetAllSuperPeersResponse();
    }

    /**
     * Create an instance of {@link PeerInformation }
     * 
     */
    public PeerInformation createPeerInformation() {
        return new PeerInformation();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllSuperPeersResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://client.cn2010.tuwien.ac.at", name = "getAllSuperPeersResponse")
    public JAXBElement<GetAllSuperPeersResponse> createGetAllSuperPeersResponse(GetAllSuperPeersResponse value) {
        return new JAXBElement<GetAllSuperPeersResponse>(_GetAllSuperPeersResponse_QNAME, GetAllSuperPeersResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PeerInformation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://client.cn2010.tuwien.ac.at", name = "peerInformation")
    public JAXBElement<PeerInformation> createPeerInformation(PeerInformation value) {
        return new JAXBElement<PeerInformation>(_PeerInformation_QNAME, PeerInformation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllSuperPeers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://client.cn2010.tuwien.ac.at", name = "getAllSuperPeers")
    public JAXBElement<GetAllSuperPeers> createGetAllSuperPeers(GetAllSuperPeers value) {
        return new JAXBElement<GetAllSuperPeers>(_GetAllSuperPeers_QNAME, GetAllSuperPeers.class, null, value);
    }

}
