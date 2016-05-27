package services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

import org.apache.axis.message.MessageElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import models.json.ErrorResponse;
import models.json.JSONResponse;
import play.Configuration;

@Singleton
public class CreditReportServiceTest {

	//@Inject
	//private Configuration conf;
	
	public static void main(String args[]) {
		
        CreditReportServiceTest reportService = new CreditReportServiceTest();
        
        //reportService.authenticate("vitalii.oleksiv@gmail.com");
        reportService.getReport("");
        //reportService.getCreditReport();
        //sendRequestManually();
    }
	
	public void getCreditReport() {
		
		try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            //TODO change with conf parameter
            String url = "https://xml.idcreditservices.com/IDSWebServicesNG/IDSEnrollment.asmx";
            //SOAPMessage soapResponse = soapConnection.call(createSOAPRequestXML(), url);
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequestString(), url);
            
            // Process the SOAP Response
            parseSOAPResponse(soapResponse);
            printSOAPResponse(soapResponse);
            
            soapConnection.close();
        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request to Server");
            e.printStackTrace();
        }
		
	}

	private SOAPMessage createSOAPRequestString() throws Exception {
		
		MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
      
        SOAPPart soapPart = soapMessage.getSOAPPart();
        //TODO change with conf
        String serverURI = "http://tempuri.org/";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.setPrefix("soap12");
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        
        envelope.addNamespaceDeclaration("soap12", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
       

        //SOAP header
        SOAPHeader header = envelope.getHeader();
        header.detachNode();
        
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        soapBody.setPrefix("soap12");
        
        SOAPElement idsEnrollmentString = soapBody.addChildElement("IDSEnrollmentString");
        idsEnrollmentString.addNamespaceDeclaration("", serverURI);
        
        SOAPElement strRequest = idsEnrollmentString.addChildElement("strRequest");
        strRequest.addTextNode("<?xml version=\"1.0\" encoding=\"utf-16\"?>" +
    		" <Request xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">&#xD;" +
    		  "<RequestSource>CRDPRF</RequestSource>&#xD;" +
    		  "<Product>&#xD;" +
    		    "<PackageId>476</PackageId>&#xD;" +
    		    "<ProductUser>&#xD;" +
    		      "<Memberid>vitalii.oleksiv@gmail.com.test.11</Memberid>&#xD;" +
    		      "<EmailAddress>vitalii.oleksiv@gmail.com.test.11</EmailAddress>&#xD;" +
    		      "<Password>123456</Password>&#xD;" +
    		      "<Address>&#xD;" +
    		        "<Address1>Skovorody 19</Address1>&#xD;" +
    		        "<Address2 xsi:type=\"xsd:string\" />&#xD;" +
    		        "<City>Kyiv</City>&#xD;" +
    		        "<State>AK</State>&#xD;" +
    		        "<ZipCode>22209</ZipCode>&#xD;" +
    		      "</Address>&#xD;" +
    		      "<Phone>&#xD;" +
    		        "<PhoneNumber></PhoneNumber>&#xD;" +
    		        "<PhoneType xsi:type=\"xsd:string\"></PhoneType>&#xD;" +
    		      "</Phone>&#xD;" +
    		      "<Person>&#xD;" +
    		        "<FirstName>John</FirstName>&#xD;" +
    		        "<LastName>Smith</LastName>&#xD;" +
    		        "<MiddleName></MiddleName>&#xD;" +
    		      "</Person>&#xD;" +
    		    "</ProductUser>&#xD;" +
    		  "</Product>&#xD;" +
    		  "<Partner>&#xD;" +
    		    "<partnerAccount>CRDPRF</partnerAccount>&#xD;" +
    		    "<partnerCode>CRDPRF</partnerCode>&#xD;" +
    		    "<partnerPassword>kYmfR5@23</partnerPassword>&#xD;" +
    		    "<Branding>CRDPRF</Branding>&#xD;" +
    		  "</Partner>&#xD;" +
    		"</Request>");
        
        
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI + "IDSEnrollmentString");

        soapMessage.saveChanges();

        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
	}
	
	private Document createXMLRequest() {
		
		StringBuilder xmlString = new StringBuilder();
		
		//xmlString.append("<IDSEnrollmentXML xmlns='http://tempuri.org/' xmlns:i='http://www.w3.org/2001/XMLSchema-instance'>\n");
	    
		xmlString.append("<Request xmlns='' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema'>\n");
		xmlString.append("<RequestSource>CRDPRF</RequestSource>\n");
		xmlString.append("<Product>\n");
		xmlString.append("<PackageId>476</PackageId>\n");
		xmlString.append("<ProductUser>\n");
		xmlString.append("<Memberid>vitalii.oleksiv@gmail.com</Memberid>\n");
		xmlString.append("<EmailAddress>vitalii.oleksiv@gmail.com.test.2</EmailAddress>\n");
		xmlString.append("<Password>123456</Password>\n");
		xmlString.append("<Address>\n");
		xmlString.append("<Address1>Skovorody 19</Address1>\n");
		xmlString.append("<Address2 xsi:type='xsd:string' />\n");
		xmlString.append("<City>Kyiv</City>\n");
		xmlString.append("<State>AK</State>\n");
		xmlString.append("<ZipCode>22209</ZipCode>\n");
		xmlString.append("</Address>\n");
		xmlString.append("<Phone>\n");
		xmlString.append("<PhoneNumber>7148884550</PhoneNumber>\n");
		xmlString.append("<PhoneType xsi:type='xsd:string'>Home</PhoneType>\n");
		xmlString.append("</Phone>\n");
		xmlString.append("<Person>\n");
		xmlString.append("<FirstName>John</FirstName>\n");
		xmlString.append("<LastName>Smith</LastName>\n");
		xmlString.append("<MiddleName>M</MiddleName>\n");
		xmlString.append("</Person>\n");
		xmlString.append("</ProductUser>\n");
		xmlString.append("</Product>\n");
		xmlString.append("<Partner>\n");
		xmlString.append("<partnerAccount>CRDPRF</partnerAccount>\n");
		xmlString.append("<partnerCode>CRDPRF</partnerCode>\n");
		xmlString.append("<partnerPassword>kYmfR5@23</partnerPassword>\n");
		xmlString.append("<Branding>CRDPRF</Branding>\n");
		xmlString.append("</Partner>\n");
		xmlString.append("</Request>\n");
		
		//xmlString.append("</IDSEnrollmentXML>\n");
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder;  
		try {  
	        builder = factory.newDocumentBuilder();  
	        Document document = builder.parse(new InputSource(new StringReader(xmlString.toString())));
	       
	        return document;
		} catch (Exception e) {  
		    e.printStackTrace();  
		} 
		return null;
	}
	
	
	
    private SOAPMessage createSOAPRequestXML() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
      
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String serverURI = "http://tempuri.org/";

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
        
        SOAPElement idsEnrollmentXML = soapBody.addChildElement("IDSEnrollmentXML", "", "http://tempuri.org/");
        idsEnrollmentXML.addNamespaceDeclaration("", serverURI);
        
        SOAPElement strRequest = idsEnrollmentXML.addChildElement("xmlRequest");
        Document xml = createXMLRequest();
        MessageElement elem = new MessageElement(xml.getDocumentElement());
        SOAPElement request = elem;
        
        strRequest.addChildElement(request);
        
       /* SOAPElement request = strRequest.addChildElement("Request");
        
        //LIKE IN EXAMPLE
        QName xsi = new QName("xsi");
        QName xsd = new QName("xsd");
        request.addAttribute(xsi, "http://www.w3.org/2001/XMLSchema-instance");
        request.addAttribute(xsd, "http://www.w3.org/2001/XMLSchema");
        
        request.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        request.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        
        SOAPElement requestSource = request.addChildElement("RequestSource");
        requestSource.addTextNode("CRDPRF");
        SOAPElement product = request.addChildElement("Product");
        SOAPElement packageId = product.addChildElement("PackageId");
        packageId.addTextNode("476");
        SOAPElement productUser = product.addChildElement("ProductUser");
        SOAPElement memberId = productUser.addChildElement("Memberid");
        memberId.addTextNode("vitalii.oleksiv@gmail.com");
        SOAPElement emailAddress = productUser.addChildElement("EmailAddress");
        emailAddress.addTextNode("vitalii.oleksiv@gmail.com");
        SOAPElement password = productUser.addChildElement("Password");
        password.addTextNode("123456");
        SOAPElement address = productUser.addChildElement("Address");
        SOAPElement address1 = address.addChildElement("Address1");
        address1.addTextNode("Skovorody 19");
        SOAPElement address2 = address.addChildElement("Address2");
        
        //LIKE IN EXAMPLE
        QName  xsiType = new QName("xsi:type");
        address2.addAttribute(xsiType, "xsd:string");
        
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
        
        //LIKE IN EXAMPLE
        phoneType.addAttribute(xsiType, "xsd:string");
        
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
        partnerAccount.addTextNode("CRDPRF");
        SOAPElement partnerCode = partner.addChildElement("partnerCode");
        partnerCode.addTextNode("CRDPRF");
        SOAPElement partnerPassword = partner.addChildElement("partnerPassword");
        partnerPassword.addTextNode("kYmfR5@23");
        SOAPElement branding = partner.addChildElement("Branding");
        branding.addTextNode("CRDPRF");*/
        
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI + "IDSEnrollmentXML");

        soapMessage.saveChanges();
        
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }
    
    
    private static void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
    }
	
    private static void parseSOAPResponse(SOAPMessage soapResponse) throws SOAPException, TransformerException, XPathExpressionException {
		
    	Iterator itr=soapResponse.getSOAPBody().getChildElements();
    	while (itr.hasNext()) {
    	    Node node=(Node)itr.next();
    	    
    	    if (node.getNodeType()==Node.ELEMENT_NODE) {
    	        Element ele=(Element)node;
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
    	    	        	
    	    	        }
    	    	        
    	    	        //TODO handle no credit report link
    	    	        else {
    	    	        	System.out.println("No credit report link");
    	    	        }
    	    	        
    	        	}
    	        	
	    	        //TODO handle error
    	        	else if (responseStr.contains("<Status>FAIL</Status>")) {
    	        		if (responseStr.contains("<ErrorCode>") && responseStr.contains("<ErrorMessage>")) {
    	        			String errorCode = responseStr.substring(responseStr.indexOf("<ErrorCode>") + "<ErrorCode>".length(), 
    	    	        			responseStr.indexOf("</ErrorCode>"));
    	        			String errorMessage = responseStr.substring(responseStr.indexOf("<ErrorMessage>") + "<ErrorMessage>".length(), 
    	    	        			responseStr.indexOf("</ErrorMessage>"));
    	        			
    	        			System.out.println(errorCode);
    	        			System.out.println(errorMessage);
    	        		}
    	        		else {
    	        			System.out.println("Unknown error");
    	        		}
    	        	}
    	        	//TODO unknown error
    	        	else {
    	        		System.out.println("Unknown error");
    	        	}
    	        	
	    	        //TODO return something
    	        }
    	        
    	    } else if (node.getNodeType()==Node.TEXT_NODE) {
    	        //do nothing here most likely, as the response nearly never has mixed content type
    	        //this is just for your reference
    	    }
    	}
    	
	    
	}
    
    public static void sendRequestManually() {
    	try {
    	      URL u = new URL("https://xml.idcreditservices.com/IDSWebServicesNG/IDSEnrollment.asmx");
    	      URLConnection uc = u.openConnection();
    	      HttpURLConnection connection = (HttpURLConnection) uc;
    	      
    	      connection.setDoOutput(true);
    	      connection.setDoInput(true);
    	      connection.setRequestMethod("POST");
    	      connection.setRequestProperty("SOAPAction", "http://tempuri.org/IDSEnrollmentXML" );
    	      connection.setRequestProperty("content-type", "application/soap+xml;charset=UTF-16");
    	      
    	      OutputStream out = connection.getOutputStream();
    	      Writer wout = new OutputStreamWriter(out);
    	      
    	      wout.write("<s:Envelope xmlns:s=");
    	      wout.write("'http://schemas.xmlsoap.org/soap/envelope/'>\n");
    	      wout.write("<s:Body>\n");
    	      wout.write("<IDSEnrollmentXML xmlns=");
    	      wout.write("'http://tempuri.org/'");
    	      wout.write(" xmlns:i=");
    	      wout.write("'http://www.w3.org/2001/XMLSchema-instance'>\n");
    	      wout.write("<xmlRequest><?xml version='1.0' encoding='utf-16'>\n");
    	      wout.write("<Request xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema'>\n");
    	      wout.write("<RequestSource>CRDPRF</RequestSource>\n");
    	      wout.write("<Product>\n");
    	      wout.write("<PackageId>476</PackageId>\n");
    	      wout.write("<ProductUser>\n");
    	      wout.write("<Memberid>vitalii.oleksiv@gmail.com</Memberid>\n");
    	      wout.write("<EmailAddress>vitalii.oleksiv@gmail.com.test.2</EmailAddress>\n");
    	      wout.write("<Password>123456</Password>\n");
    	      wout.write("<Address>\n");
    	      wout.write("<Address1>Skovorody 19</Address1>\n");
    	      wout.write("<Address2 xsi:type='xsd:string' />\n");
    	      wout.write("<City>Kyiv</City>\n");
    	      wout.write("<State>AK</State>\n");
    	      wout.write("<ZipCode>22209</ZipCode>\n");
    	      wout.write("</Address>\n");
    	      wout.write("<Phone>\n");
    	      wout.write("<PhoneNumber>7148884550</PhoneNumber>\n");
    	      wout.write("<PhoneType xsi:type='xsd:string'>Home</PhoneType>\n");
    	      wout.write("</Phone>\n");
    	      wout.write("<Person>\n");
    	      wout.write("<FirstName>John</FirstName>\n");
    	      wout.write("<LastName>Smith</LastName>\n");
    	      wout.write("<MiddleName>M</MiddleName>\n");
    	      wout.write("</Person>\n");
    	      wout.write("</ProductUser>\n");
    	      wout.write("</Product>\n");
    	      wout.write("<Partner>\n");
    	      wout.write("<partnerAccount>CRDPRF</partnerAccount>\n");
    	      wout.write("<partnerCode>CRDPRF</partnerCode>\n");
    	      wout.write("<partnerPassword>kYmfR5@23</partnerPassword>\n");
    	      wout.write("<Branding>CRDPRF</Branding>\n");
    	      wout.write("</Partner>\n");
    	      wout.write("</Request>\n");
    	      wout.write("</xmlRequest>\n");
    	      wout.write("</IDSEnrollmentXML>\n");
    	      wout.write("</s:Body>\n");
    	      wout.write("</s:Envelope>\n");
    	      
    	      System.out.println(wout);
    	      
    	      /*wout.write("<?xml version='1.0'?>\r\n");  
    	      wout.write("<SOAP-ENV:Envelope ");
    	      wout.write("xmlns:SOAP-ENV=");
    	      wout.write(
    	        "'http://schemas.xmlsoap.org/soap/envelope/' "
    	      );
    	      wout.write("xmlns:xsi=");
    	      wout.write(
    	        "'http://www.w3.org/2001/XMLSchema-instance'>\r\n"); 
    	      wout.write("  <SOAP-ENV:Body>\r\n");
    	      wout.write("    <calculateFibonacci ");
    	      wout.write(
    	    "xmlns='http://namespaces.cafeconleche.org/xmljava/ch3/'\r\n"
    	      ); 
    	      wout.write("    type='xsi:positiveInteger'>" + input 
    	       + "</calculateFibonacci>\r\n"); 
    	      wout.write("  </SOAP-ENV:Body>\r\n"); 
    	      wout.write("</SOAP-ENV:Envelope>\r\n"); */
    	      
    	      wout.flush();
    	      wout.close();
    	      
    	      InputStream in = connection.getInputStream();
    	      int c;
    	      while ((c = in.read()) != -1) System.out.write(c);
    	      in.close();

    	    }
    	    catch (IOException e) {
    	      System.err.println(e); 
    	    }
    }
    
  //AUTHENTICATE
	
  	public JSONResponse authenticate(String username) {
  		
  		try {
              // Create SOAP Connection
              SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
              SOAPConnection soapConnection = soapConnectionFactory.createConnection();

              // Send SOAP Message to SOAP Server
              String url = "https://xml.idcreditservices.com/SIDUpdateServices/MemberUpdate.asmx";
              //String url = conf.getString("idcs.authenticate.url");
              
              SOAPMessage soapResponse = soapConnection.call(createAuthenticationRequest(username), url);
              
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


  	private SOAPMessage createAuthenticationRequest(String usernameStr) throws SOAPException, IOException {
  		MessageFactory messageFactory = MessageFactory.newInstance();
          SOAPMessage soapMessage = messageFactory.createMessage();
        
          SOAPPart soapPart = soapMessage.getSOAPPart();
          
          String serverURI = "http://tempuri.org/";
          //String serverURI = conf.getString("credit.report.server.uri");

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
         
          //String partnerPass = conf.getString("credit.report.partner.password");
          String partnerPass = "kYmfR5@23";
          
          SOAPElement username = authenticate.addChildElement("username");
          username.addTextNode(usernameStr);
          SOAPElement password = authenticate.addChildElement("password");
          password.addTextNode(partnerPass);
          
          
          MimeHeaders headers = soapMessage.getMimeHeaders();
          headers.addHeader("SOAPAction", serverURI + "Authenticate");

          soapMessage.saveChanges();

          soapMessage.writeTo(System.out);
          System.out.println();

          return soapMessage;
  	}
  	

  	private JSONResponse parseAuthenticationResponse(SOAPMessage soapResponse) {
  		// TODO Auto-generated method stub
  		return null;
  	}
    
  	//GET REPORT
	
  	public JSONResponse getReport(String memberId) {
  		
  		try {
              // Create SOAP Connection
              SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
              SOAPConnection soapConnection = soapConnectionFactory.createConnection();

              // Send SOAP Message to SOAP Server
              String url = "https://xml.idcreditservices.com/IDSWebServicesNG/IDSDataMonitoringReport.asmx";
              //String url = conf.getString("idcs.authenticate.url");
              
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
        
        String serverURI = "http://tempuri.org/";

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
        //idsEnrollmentString.addNamespaceDeclaration("", serverURI);
        
        SOAPElement strRequest = getIDSDataMonitoringReportString.addChildElement("strRequest");
        strRequest.addTextNode("<?xml version=\"1.0\" encoding=\"utf-16\"?>" +
        		" <Request xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">&#xD;" +
        		  "<Product>&#xD;" +
        		    "<ProductUser>&#xD;" +
        		      "<MemberId>vitalii.oleksiv@gmail.com.test.2</MemberId>&#xD;" +
        		    "</ProductUser>&#xD;" +
        		  "</Product>&#xD;" +
        		  "<PartnerType>ALL</PartnerType>&#xD;" +  
        		  "<Partner>&#xD;" +
        		    "<partnerAccount>CRDPRF</partnerAccount>&#xD;" +
        		    "<partnerCode>CRDPRF</partnerCode>&#xD;" +
        		    "<partnerPassword>kYmfR5@23</partnerPassword>&#xD;" +
        		  "</Partner>&#xD;" +
        		"</Request>");
        
        
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI + "GetIDSDataMonitoringReportString");

        soapMessage.saveChanges();

        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
  		
  	}
  	

  	private JSONResponse parseGetReportResponse(SOAPMessage soapResponse) {
  		// TODO Auto-generated method stub
  		
  		return null;
  	}
    
}
