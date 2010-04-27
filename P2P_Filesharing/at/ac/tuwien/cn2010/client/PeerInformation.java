
package at.ac.tuwien.cn2010.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for peerInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="peerInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PeerAdress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PeerPort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="IsSuperPeer" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "peerInformation", propOrder = {
    "peerAdress",
    "peerPort",
    "isSuperPeer"
})
public class PeerInformation {

    @XmlElement(name = "PeerAdress")
    protected String peerAdress;
    @XmlElement(name = "PeerPort")
    protected int peerPort;
    @XmlElement(name = "IsSuperPeer")
    protected boolean isSuperPeer;

    /**
     * Gets the value of the peerAdress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeerAdress() {
        return peerAdress;
    }

    /**
     * Sets the value of the peerAdress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeerAdress(String value) {
        this.peerAdress = value;
    }

    /**
     * Gets the value of the peerPort property.
     * 
     */
    public int getPeerPort() {
        return peerPort;
    }

    /**
     * Sets the value of the peerPort property.
     * 
     */
    public void setPeerPort(int value) {
        this.peerPort = value;
    }

    /**
     * Gets the value of the isSuperPeer property.
     * 
     */
    public boolean isIsSuperPeer() {
        return isSuperPeer;
    }

    /**
     * Sets the value of the isSuperPeer property.
     * 
     */
    public void setIsSuperPeer(boolean value) {
        this.isSuperPeer = value;
    }

}
