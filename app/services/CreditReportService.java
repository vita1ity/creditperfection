package services;

import java.io.IOException;
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
import models.json.AuthenticationSuccessResponse;
import models.json.CreditReportSuccessResponse;
import models.json.ErrorResponse;
import models.json.JSONResponse;
import play.Configuration;

@Singleton
public class CreditReportService {
	
	@Inject
	private Configuration conf;
	
	public JSONResponse getKBAQuestionsUrl(User user) {
		
		try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            String url = conf.getString("idcs.enroll.url");
            
            SOAPMessage soapResponse = soapConnection.call(createKBASOAPRequest(user), url);
            
            JSONResponse response = parseKBASOAPResponse(soapResponse);
            
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
	
	private SOAPMessage createKBASOAPRequest(User user) throws Exception {
		
		MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
      
        SOAPPart soapPart = soapMessage.getSOAPPart();
        
        String serverURI = conf.getString("idcs.server.uri");

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
    		      "<Memberid>" + user.getEmail() + "</Memberid>&#xD;" +
    		      "<EmailAddress>" + user.getEmail() + "</EmailAddress>&#xD;" +
    		      "<Password>" + user.getPassword() + "</Password>&#xD;" +
    		      "<Address>&#xD;" +
    		        "<Address1>" + user.getAddress() + "</Address1>&#xD;" +
    		        "<Address2 xsi:type=\"xsd:string\" />&#xD;" +
    		        "<City>" + user.getCity() + "</City>&#xD;" +
    		        "<State>" + user.getState() + "</State>&#xD;" +
    		        "<ZipCode>" + user.getZip() + "</ZipCode>&#xD;" +
    		      "</Address>&#xD;" +
    		      "<Phone>&#xD;" +
    		        "<PhoneNumber></PhoneNumber>&#xD;" +
    		        "<PhoneType xsi:type=\"xsd:string\"></PhoneType>&#xD;" +
    		      "</Phone>&#xD;" +
    		      "<Person>&#xD;" +
    		        "<FirstName>" + user.getFirstName() + "</FirstName>&#xD;" +
    		        "<LastName>" + user.getLastName() + "</LastName>&#xD;" +
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
	private JSONResponse parseKBASOAPResponse(SOAPMessage soapResponse) throws SOAPException, 
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
    	    	        	url = url.replaceAll("&amp;", "&");
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
	
	
	//AUTHENTICATE
	
	public JSONResponse authenticate(String username, String password) {
		
		try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            String url = conf.getString("idcs.authenticate.url");
            
            SOAPMessage soapResponse = soapConnection.call(createAuthenticationRequest(username, password), url);
            
            JSONResponse response = parseAuthenticationResponse(soapResponse);
            
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


	private SOAPMessage createAuthenticationRequest(String usernameStr, String passwordStr) throws SOAPException, IOException {
		MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
      
        SOAPPart soapPart = soapMessage.getSOAPPart();
        
        String serverURI = conf.getString("idcs.server.uri");

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
        
        SOAPElement authenticate = soapBody.addChildElement("Authenticate", "", serverURI );
        //idsEnrollmentString.addNamespaceDeclaration("", serverURI);
        
        SOAPElement username = authenticate.addChildElement("username");
        username.addTextNode(usernameStr);
        SOAPElement password = authenticate.addChildElement("password");
        password.addTextNode(passwordStr);
        
        
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI + "Authenticate");

        soapMessage.saveChanges();

        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
	}
	

	@SuppressWarnings("rawtypes")
	private JSONResponse parseAuthenticationResponse(SOAPMessage soapResponse) throws SOAPException {
		JSONResponse response = null;
		 
    	Iterator itr = soapResponse.getSOAPBody().getChildElements();
    	while (itr.hasNext()) {
    	    Node node = (Node)itr.next();
    	    
    	    if (node.getNodeType() == Node.ELEMENT_NODE) {
    	        Element ele = (Element)node;
    	        System.out.println(ele.getNodeName() + " = " + ele.getTextContent());
    	        if (ele.getNodeName().equals("AuthenticateResponse")) {
    	        	
    	        	String responseStr = ele.getTextContent();
    	        	//handle success
    	        	if (responseStr.contains("TRUE")) {
    	        		
    	    	        
	    	        	String memberId = responseStr.substring(responseStr.indexOf("TRUE,") + "TRUE,".length(), 
	    	        			responseStr.indexOf("</AuthenticateResult>"));
	    	        	
	    	        	System.out.println("MemberId: " + memberId);
	    	        	
	    	        	response = new AuthenticationSuccessResponse("SUCCESS", memberId); 
	    	        	return response;
    	    	        
    	        	}
    	        	
    	        	else if (responseStr.contains("FALSE")) {
    	        		
    	        		response = new ErrorResponse("ERROR", "105", "IDCS authentication failed"); 
    	        		return response;
    	        	}
    	        	else {
    	        		response = new ErrorResponse("ERROR", "106", "IDCS authentication unknown error"); 
	    	        	return response;
    	        	}
    	        	
	    	        
    	        }
    	        
    	    } else if (node.getNodeType() == Node.TEXT_NODE) {
    	        
    	    }
    	}
    	
    	response = new ErrorResponse("ERROR", "103", "Invalid response"); 
    	return response;
	}
	
	//GET REPORT
	
  	public JSONResponse getReport(String memberId) {
  		
  		try {
              // Create SOAP Connection
              SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
              SOAPConnection soapConnection = soapConnectionFactory.createConnection();

              // Send SOAP Message to SOAP Server
              String url = conf.getString("idcs.getreport.url");
              
              SOAPMessage soapResponse = soapConnection.call(createGetReportRequest(memberId), url);
              
              JSONResponse response = parseGetReportResponse(soapResponse);
              
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


  	private SOAPMessage createGetReportRequest(String memberId) throws SOAPException, IOException {
  		
  		MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
      
        SOAPPart soapPart = soapMessage.getSOAPPart();
        
        String serverURI = conf.getString("idcs.server.uri");

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
        
        SOAPElement getIDSDataMonitoringReportString = soapBody.addChildElement("GetIDSDataMonitoringReportString", "", serverURI);
        
        String requestSource = conf.getString("credit.report.request.source");
        String partnerPass = conf.getString("credit.report.partner.password");
        
        SOAPElement strRequest = getIDSDataMonitoringReportString.addChildElement("strRequest");
        strRequest.addTextNode("<?xml version=\"1.0\" encoding=\"utf-16\"?>" +
        		" <Request xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">&#xD;" +
        		  "<Product>&#xD;" +
        		    "<ProductUser>&#xD;" +
        		      "<memberId>" + memberId + "</memberId>&#xD;" +
        		    "</ProductUser>&#xD;" +
        		  "</Product>&#xD;" +
        		  "<PartnerType>ALL</PartnerType>&#xD;" +  
        		  "<Partner>&#xD;" +
        		    "<partnerAccount>" + requestSource + "</partnerAccount>&#xD;" +
        		    "<partnerCode>" + requestSource + "</partnerCode>&#xD;" +
        		    "<partnerPassword>" + partnerPass + "</partnerPassword>&#xD;" +
        		  "</Partner>&#xD;" +
        		"</Request>");
        
        
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI + "GetIDSDataMonitoringReportString");

        soapMessage.saveChanges();

        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
  		
  	}
  	

  	@SuppressWarnings("rawtypes")
	private JSONResponse parseGetReportResponse(SOAPMessage soapResponse) throws SOAPException {
  		JSONResponse response = null;
		 
    	Iterator itr = soapResponse.getSOAPBody().getChildElements();
    	while (itr.hasNext()) {
    	    Node node = (Node)itr.next();
    	    
    	    if (node.getNodeType() == Node.ELEMENT_NODE) {
    	        Element ele = (Element)node;
    	        System.out.println(ele.getNodeName() + " = " + ele.getTextContent());
    	        if (ele.getNodeName().equals("GetIDSDataMonitoringReportStringResponse")) {
    	        	
    	        	String responseStr = ele.getTextContent();
    	        	//handle success
    	        	if (responseStr.contains("<Status>SUCCESS</Status>")) {
    	        		
    	    	        // get report from the response
    	        		if (responseStr.contains("<Report>")) {
    	    	        	String report = responseStr.substring(responseStr.indexOf("<Report>") + "<Report>".length(), 
    	    	        			responseStr.indexOf("</Report>"));
    	    	        	report = report.replaceAll("&amp;", "&");
    	    	        	System.out.println(report);
    	    	        	
    	    	        	response = new CreditReportSuccessResponse("SUCCESS", report); 
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
	
	
	
}
