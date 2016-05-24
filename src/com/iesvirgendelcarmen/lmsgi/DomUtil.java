/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iesvirgendelcarmen.lmsgi;

import static com.sun.org.apache.xerces.internal.jaxp.JAXPConstants.JAXP_SCHEMA_LANGUAGE;
import static com.sun.org.apache.xerces.internal.jaxp.JAXPConstants.W3C_XML_SCHEMA;
import org.w3c.dom.Document;
// import org.w3c.dom.NamedNodeMap;
// import org.w3c.dom.Node;
// import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;
// import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


/**
 *
 * @author Administrador
 */
public class DomUtil {
    
    public static DocumentBuilder newBuilder(boolean validation)
        throws ParserConfigurationException {
        
            // Creamos la factoría JAXP para los constructores de documentos
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            // Configuramos la factoría
            domFactory.setValidating(validation);
            domFactory.setNamespaceAware(true);
            // Creamos el contructor de documentos (posteriormente se podrá usar 
            // para leer archivos XML o bien crearlos y almacenarlos o transmitirlos
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
            // Establecemos el manejador de errores (XML bien formado, válido...)
            
            OutputStreamWriter errorWriter = null;
        try {
            errorWriter = new OutputStreamWriter(System.err, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DomUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
            domBuilder.setErrorHandler(new MyErrorHandler(new PrintWriter(errorWriter, true)));
            return domBuilder;
    }
    
    public static Document newDocument()
            throws ParserConfigurationException {
        
        DocumentBuilder domBuilder = newBuilder(false);

        Document document = domBuilder.newDocument();
        
        return document;
    }
    
    public static Document parse(File aFile, boolean validation) 
        throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilder domBuilder = newBuilder(validation);
        
        Document document = domBuilder.parse(aFile);
        
        return document;
    }
    // Error handler to report errors and warnings
    private static class MyErrorHandler implements ErrorHandler {
        /** Error handler output goes here */
        private PrintWriter out;

        MyErrorHandler(PrintWriter out) {
            this.out = out;
        }

        /**
         * Returns a string describing parse exception details
         */
        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }
            String info = "URI=" + systemId +
                " Line=" + spe.getLineNumber() +
                ": " + spe.getMessage();
            return info;
        }

        // The following methods are standard SAX ErrorHandler methods.
        // See SAX documentation for more info.

        public void warning(SAXParseException spe) throws SAXException {
            out.println("Warning: " + getParseExceptionInfo(spe));
        }
        
        public void error(SAXParseException spe) throws SAXException {
            String message = "Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }

        public void fatalError(SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }
     public static Document parseXSD(File fichero) 
        throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);     
        dbf.setValidating(true);
        dbf.setNamespaceAware(true);
         
        DocumentBuilder domBuilder = dbf.newDocumentBuilder();
            // Establecemos el manejador de errores (XML bien formado, válido...)
            
            OutputStreamWriter errorWriter = null;
        try {
            errorWriter = new OutputStreamWriter(System.err, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DomUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
            domBuilder.setErrorHandler(new MyErrorHandler(new PrintWriter(errorWriter, true)));
            
        Document document = domBuilder.parse(fichero);
     
        return document;
    }
     
     
}