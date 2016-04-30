package services;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import models.User;
import models.json.CreditReportSuccessResponse;
import models.json.ErrorResponse;
import models.json.JSONResponse;
import play.Configuration;

@Singleton
public class CreditReportService {
	
	@Inject
	private Configuration conf;
	
	public JSONResponse getCreditReport(User user) {
		
		try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            String url = conf.getString("credit.report.url");
            
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequestString(user), url);
            
            JSONResponse response = parseSOAPResponse(soapResponse);
            
            //TODO log it or delete - Print the SOAP Response
            printSOAPResponse(soapResponse);

            soapConnection.close();
            
            return response;
            
        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request to Server");
            e.printStackTrace();
            
            JSONResponse response = new ErrorResponse("ERROR", "104", "Error occurred while sending SOAP Request to Server");
            return response;
            
        }
		
	}
	
	private SOAPMessage createSOAPRequestString(User user) throws Exception {
		
		MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
      
        SOAPPart soapPart = soapMessage.getSOAPPart();
        
        String serverURI = conf.getString("credit.report.server.uri");

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.setPrefix("soap12");
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap12", "http://www.w3.org/2003/05/soap-envelope");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        
        //SOAP header
        SOAPHeader header = envelope.getHeader();
        header.detachNode();
        
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        soapBody.setPrefix("soap12");
        
        SOAPElement idsEnrollmentString = soapBody.addChildElement("IDSEnrollmentString", "", serverURI );
        //idsEnrollmentString.addNamespaceDeclaration("", serverURI);
        
        String requestSource = conf.getString("credit.report.request.source");
        String packageId = conf.getString("credit.report.package.id");
        String partnerPass = conf.getString("credit.report.partner.password");
        
        SOAPElement strRequest = idsEnrollmentString.addChildElement("strRequest");
        strRequest.addTextNode("<?xml version=\"1.0\" encoding=\"utf-16\"?>" +
    		" <Request xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">&#xD;" +
    		  "<RequestSource>" + requestSource + "</RequestSource>&#xD;" +
    		  "<Product>&#xD;" +
    		    "<PackageId>" + packageId + "</PackageId>&#xD;" +
    		    "<ProductUser>&#xD;" +
    		      "<Memberid>" + user.email + "</Memberid>&#xD;" +
    		      "<EmailAddress>" + user.email + "</EmailAddress>&#xD;" +
    		      "<Password>" + user.password + "</Password>&#xD;" +
    		      "<Address>&#xD;" +
    		        "<Address1>" + user.address + "</Address1>&#xD;" +
    		        "<Address2 xsi:type=\"xsd:string\" />&#xD;" +
    		        "<City>" + user.city + "</City>&#xD;" +
    		        "<State>" + user.state + "</State>&#xD;" +
    		        "<ZipCode>" + user.zip + "</ZipCode>&#xD;" +
    		      "</Address>&#xD;" +
    		      "<Phone>&#xD;" +
    		        "<PhoneNumber></PhoneNumber>&#xD;" +
    		        "<PhoneType xsi:type=\"xsd:string\"></PhoneType>&#xD;" +
    		      "</Phone>&#xD;" +
    		      "<Person>&#xD;" +
    		        "<FirstName>" + user.firstName + "</FirstName>&#xD;" +
    		        "<LastName>" + user.lastName + "</LastName>&#xD;" +
    		        "<MiddleName></MiddleName>&#xD;" +
    		      "</Person>&#xD;" +
    		    "</ProductUser>&#xD;" +
    		  "</Product>&#xD;" +
    		  "<Partner>&#xD;" +
    		    "<partnerAccount>" + requestSource + "</partnerAccount>&#xD;" +
    		    "<partnerCode>" + requestSource + "</partnerCode>&#xD;" +
    		    "<partnerPassword>" + partnerPass + "</partnerPassword>&#xD;" +
    		    "<Branding>" + requestSource + "</Branding>&#xD;" +
    		  "</Partner>&#xD;" +
    		"</Request>");
        
        
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI + "IDSEnrollmentString");

        soapMessage.saveChanges();

        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
	}

	@SuppressWarnings("rawtypes")
	private JSONResponse parseSOAPResponse(SOAPMessage soapResponse) throws SOAPException, 
		TransformerException, XPathExpressionException {
			
		JSONResponse response = null;
		 
    	Iterator itr = soapResponse.getSOAPBody().getChildElements();
    	while (itr.hasNext()) {
    	    Node node = (Node)itr.next();
    	    
    	    if (node.getNodeType() == Node.ELEMENT_NODE) {
    	        Element ele = (Element)node;
    	        System.out.println(ele.getNodeName() + " = " + ele.getTextContent());
    	        if (ele.getNodeName().equals("IDSEnrollmentStringResponse")) {
    	        	
    	        	String responseStr = ele.getTextContent();
    	        	//handle success
    	        	if (responseStr.contains("<Status>SUCCESS</Status>")) {
    	        		
    	    	        if (responseStr.contains("<CreditReportUrl>")) {
    	    	        	String url = responseStr.substring(responseStr.indexOf("<CreditReportUrl>") + "<CreditReportUrl>".length(), 
    	    	        			responseStr.indexOf("</CreditReportUrl>"));
    	    	        	System.out.println(url);
    	    	        	
    	    	        	response = new CreditReportSuccessResponse("SUCCESS", url); 
    	    	        	return response;
    	    	        }
    	    	        
    	    	        else {
    	    	        	System.out.println("No credit report link");
    	    	        	
    	    	        	response = new ErrorResponse("ERROR", "101", "User with such is already registered"); 
    	    	        	return response;
    	    	        }
    	    	        
    	        	}
    	        	
    	        	else if (responseStr.contains("<Status>FAIL</Status>")) {
    	        		if (responseStr.contains("<ErrorCode>") && responseStr.contains("<ErrorMessage>")) {
    	        			String errorCode = responseStr.substring(responseStr.indexOf("<ErrorCode>") + "<ErrorCode>".length(), 
    	    	        			responseStr.indexOf("</ErrorCode>"));
    	        			String errorMessage = responseStr.substring(responseStr.indexOf("<ErrorMessage>") + "<ErrorMessage>".length(), 
    	    	        			responseStr.indexOf("</ErrorMessage>"));
    	        			
    	        			System.out.println(errorCode);
    	        			System.out.println(errorMessage);
    	        			
    	        			response = new ErrorResponse("ERROR", errorCode, errorMessage); 
    	    	        	return response;
    	        		}
    	        		else {
    	        			System.out.println("Unknown error");
    	        			
    	        			response = new ErrorResponse("ERROR", "102", "Unknown error"); 
    	    	        	return response;
    	        		}
    	        	}
    	        	else {
    	        		response = new ErrorResponse("ERROR", "102", "Unknown error"); 
	    	        	return response;
    	        	}
    	        	
	    	        
    	        }
    	        
    	    } else if (node.getNodeType() == Node.TEXT_NODE) {
    	        
    	    }
    	}
    	
    	response = new ErrorResponse("ERROR", "103", "Invalid response"); 
    	return response;
	    
	}
	
	private void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    Source sourceContent = soapResponse.getSOAPPart().getContent();
	    
	    StreamResult result = new StreamResult(System.out);
	    transformer.transform(sourceContent, result);
	}

}
