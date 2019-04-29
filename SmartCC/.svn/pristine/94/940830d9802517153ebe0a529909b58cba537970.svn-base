package com.lzc.demo.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

public class Testt {
	public String readWord(String path) {  
        String buffer = "";  
        try {  
            if (path.endsWith(".doc")) {  
                InputStream is = new FileInputStream(new File(path));  
                WordExtractor ex = new WordExtractor(is);  
                buffer = ex.getText();  
            } else if (path.endsWith("docx")) {  
                OPCPackage opcPackage = POIXMLDocument.openPackage(path);  
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);  
                buffer = extractor.getText();  
            } else {  
                System.out.println("此文件不是word文件！");  
            }  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return buffer;  
    }  
	
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
    	Testt tp = new Testt();  
        String content = tp.readWord("D:\\0614.docx");  
        System.out.println("content===="+content);  
    }  
  

}
