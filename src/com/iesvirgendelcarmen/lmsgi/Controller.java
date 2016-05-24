/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iesvirgendelcarmen.lmsgi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SchemaManager;
import net.sf.saxon.s9api.SchemaValidator;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;




/**
 *
 * @author matinal
 */
public class Controller {
       private File file;
       //File file1;
       private File fileXSL; //para hojas de transformación
       private File fileHTML; //para salida HTML de hojas de transformación
       private File fileXSD;
       private File fileDTD;
   

    public Controller() {
        this.file = null;
        this.fileXSL= null;
        this.fileHTML = null;
        this.fileXSD = null;
        this.fileDTD = null;
        
    }
    public Controller(File myfile){
        this.file = myfile;
        this.fileXSL =null;
        this.fileHTML = null;
        this.fileXSD = null;
        this.fileDTD = null;
    }
    
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFileXSL() {
        return fileXSL;
    }

    public void setFileXSL(File fileXSL) {
        this.fileXSL = fileXSL;
    }

    public File getFileHTML() {
        return fileHTML;
    }

    public void setFileHTML(File fileHTML) {
        this.fileHTML = fileHTML;
    }

    public File getFileDTD() {
        return fileDTD;
    }

    public void setFieDTD(File fileDTD) {
        this.fileDTD = fileDTD;
    }
    private boolean save2File(File fichero, String contenido){
        boolean resultado = true;
        if(fichero != null && contenido != null) {
       
           try( FileWriter fw = new FileWriter(fichero)){
               fw.write(contenido);
               fw.flush();
           } catch (IOException ex) {
               resultado = false;
           } 
            }else{
                 resultado = false;
            }
       
                
        
        return resultado;
    }
    public boolean save2XML(String contenido){
       return save2File(this.file, contenido);
    }
    public boolean save2XSL(String contenido){
       return save2File(this.fileXSL, contenido);
    }
    
    //método para evaluar la consulta Xpath  
    public String xPathEvaluate(String stringXpath){
        String resultado ="";  
        try {
            //le pasamos la consulta XPath como argumento
            //usaremos la API  saxon.s9api.Processor;
            //creamos el compilador XPath
               Processor proc =  new Processor(false);
            //creamos un DocumentBuilder
               DocumentBuilder builder = proc.newDocumentBuilder();
               builder.setLineNumbering(true);
               //borra los espacios en blanco
               builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
               //creamos un documento XML utilizand el DocumentBuilder
               //el file que usamos es el creado en la clase Controller
               XdmNode documentoXML = builder.build(file);
               //creamos un compilador de XPath con el que controlaremos la consulta
               XPathCompiler xpath = proc.newXPathCompiler();
               XPathSelector selector = xpath.compile(stringXpath).load();
               selector.setContextItem(documentoXML);
               //en este objeto evaluate tenemos todos los resultados del Xpath, ahora lo pasamos a String
               XdmValue evaluate = selector.evaluate();
               //recorremos el evaluate anterior
               for(XdmItem item : evaluate){
                   //cada item lo vamos añadiendo al resultado
                   resultado += item.getStringValue()+"\n";
               }
                              
           } catch (SaxonApiException ex) {
               Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
           }
        return resultado;
    }
    public String validar(){
        String resultado = "Procesando fichero "+file.getPath().toString();
           try {
               DomUtil.parse(file, true);
               resultado += "\n Fichero Procesado";
           } catch (ParserConfigurationException |IOException | SAXException ex) {
               Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
               resultado += ex.getLocalizedMessage()+"\n";
           }
        
        
        
        return resultado;
    }
    String validateDTD(){
        String resultado ="Procesando fichero"+this.file.getPath()+"\n";
           try{
            DomUtil.parse(file,true);
            resultado+= "\nFichero Procesado";
            
        }catch (ParserConfigurationException | SAXException | IOException ex){
            resultado+= ex.getLocalizedMessage()+"\n";
        }
            
        return resultado;
        
    
    }
    String validateXSD(){
        String resultado="Validación XSD correcta";
        
           try {
               Document doc = DomUtil.parseXSD(this.file);
           } catch (ParserConfigurationException|IOException | SAXException ex) {
              resultado= ex.getLocalizedMessage();
           }
        return resultado;
    }
    public  String xslTransform(){
        File xmlFile = this.file;
        File xslFile = this.fileXSL;
        File htmlOut =  this.fileHTML;
        String resultado ="Transformación completada correctamente.";   
        if(xmlFile != null && xslFile != null && htmlOut !=null){
        try {
               
               Processor proc = new Processor(false);
               XsltCompiler comp = proc.newXsltCompiler();
               XsltExecutable exp = comp.compile(new StreamSource(xslFile));
               XdmNode source = proc.newDocumentBuilder().build(new StreamSource(xmlFile));
               Serializer out = proc.newSerializer(htmlOut);
               out.setOutputProperty(Serializer.Property.METHOD, "html");
               out.setOutputProperty(Serializer.Property.INDENT, "yes");
               XsltTransformer trans = exp.load();
               trans.setInitialContextNode(source);
               trans.setDestination(out);
               trans.transform();
               resultado = new String (Files.readAllBytes(htmlOut.toPath()));
               
               
              
           } catch (IOException | SaxonApiException ex) {
              // Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
              resultado=ex.getLocalizedMessage();
           }
            }else{
                   resultado="Error procesando ficheros.";
                   }
         return resultado;
                
    }    
   
}
