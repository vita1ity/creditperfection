package services;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import javax.xml.soap.Name;

import play.Configuration;

@Singleton
public class CreditReportService {

	@Inject
	private Configuration conf;
	
	public static void main(String args[]) {
		
        CreditReportService reportService = new CreditReportService();
        reportService.getCreditReport();
    }
	
	public void getCreditReport() {
		
		try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            //TODO change with conf parameter
            String url = "https://xml.idcreditservices.com/IDSWebServicesNG/IDSEnrollment.asmx";
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);

            // Process the SOAP Response
            printSOAPResponse(soapResponse);

            soapConnection.close();
        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request to Server");
            e.printStackTrace();
        }
		
	}

    private SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String serverURI = "http://tempuri.org/";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("report", serverURI);

        /*
        Constructed SOAP Request Message:
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:example="http://ws.cdyne.com/">
            <SOAP-ENV:Header/>
            <SOAP-ENV:Body>
                <example:VerifyEmail>
                    <example:email>mutantninja@gmail.com</example:email>
                    <example:LicenseKey>123</example:LicenseKey>
                </example:VerifyEmail>
            </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
         */

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        
        SOAPElement idsEnrollmentString = soapBody.addChildElement("IDSEnrollmentString");
        SOAPElement strRequest = idsEnrollmentString.addChildElement("strRequest");
        strRequest.addTextNode("CRDPRF");
        
        /*
        SOAPElement request = strRequest.addChildElement("Request");
        
        SOAPElement requestSource = request.addChildElement("RequestSource");
        requestSource.addTextNode("CRDPRF");
        SOAPElement product = request.addChildElement("Product");
        SOAPElement packageId = product.addChildElement("PackageId");
        packageId.addTextNode("476");
        SOAPElement productUser = product.addChildElement("ProductUser");
        SOAPElement memberId = productUser.addChildElement("MemberId");
        memberId.addTextNode("vitalii.oleksiv@gmail.com");
        SOAPElement emailAddress = productUser.addChildElement("EmailAddress");
        emailAddress.addTextNode("vitalii.oleksiv@gmail.com");
        SOAPElement password = productUser.addChildElement("Password");
        password.addTextNode("123456");
        SOAPElement address = productUser.addChildElement("Address");
        SOAPElement address1 = address.addChildElement("Address1");
        address1.addTextNode("Skovorody 19");
        SOAPElement city = address.addChildElement("City");
        city.addTextNode("Kyiv");
        SOAPElement state = address.addChildElement("State");
        state.addTextNode("AK");
        SOAPElement zipCode = address.addChildElement("ZipCode");
        zipCode.addTextNode("22209");
        SOAPElement phone = productUser.addChildElement("Phone");
        SOAPElement phoneNumber = phone.addChildElement("PhoneNumber");
        phoneNumber.addTextNode("7148884550");
        SOAPElement phoneType = phone.addChildElement("PhoneType");
        phoneType.addTextNode("Home");
        SOAPElement person = productUser.addChildElement("Person");
        SOAPElement firstName = person.addChildElement("FirstName");
        firstName.addTextNode("John");
        SOAPElement lastName = person.addChildElement("LastName");
        lastName.addTextNode("Smith");
        SOAPElement middleName = person.addChildElement("MiddleName");
        middleName.addTextNode("M");
        SOAPElement partner = request.addChildElement("Partner");
        SOAPElement partnerAccount = partner.addChildElement("partnerAccount");
        partnerAccount.addTextNode("PartnerAccount");
        SOAPElement partnerCode = partner.addChildElement("partnerCode");
        partnerCode.addTextNode("CRDPRF");
        SOAPElement partnerPassword = partner.addChildElement("partnerPassword");
        partnerPassword.addTextNode("kYmfR5@23");
        SOAPElement branding = partner.addChildElement("Branding");
        branding.addTextNode("CRDPRF");*/
        
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI + "IDSEnrollmentString");

        soapMessage.saveChanges();

        /* Print the request message */
        System.out.print("Request SOAP Message = ");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }
    
    
    private static void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
    }
	
}
