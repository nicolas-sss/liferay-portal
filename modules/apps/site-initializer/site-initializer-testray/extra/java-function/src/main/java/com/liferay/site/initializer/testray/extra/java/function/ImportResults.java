/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.site.initializer.testray.extra.java.function;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.FileInputStream;

import java.nio.file.Paths;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


/**
 * @author José Abelenda
 */
public class ImportResults {

	public static void listBuckets(String projectId) throws Exception {
		GoogleCredentials credentials = GoogleCredentials.fromStream(
			new FileInputStream("/home/me/Downloads/key.json"));

		Storage storage = StorageOptions.newBuilder(
		).setProjectId(
			projectId
		).setCredentials(
			credentials
		).build(
		).getService();

		Page<Bucket> bucketsPage = storage.list();

		for (Bucket bucket : bucketsPage.iterateAll()) {
			System.out.println(bucket.getName());

			Page<Blob> blobsPage = storage.list(bucket.getName());

			for (Blob blob : blobsPage.iterateAll()) {
				System.out.println(blob.getName());

				blob.downloadTo(Paths.get("/home/me/Downloads/key.xml"));
			}
		}
	}
	// public static void processXML() throws Exception {
		
	// 	DocumentBuilderFactory factory =
	// 	DocumentBuilderFactory.newInstance();
	// 	DocumentBuilder builder = factory.newDocumentBuilder();

	// 	StringBuilder xmlStringBuilder = new StringBuilder();
	// 	xmlStringBuilder.append("<?xml version="1.0"?> <class> </class>");
	// 	ByteArrayInputStream input = new ByteArrayInputStream(
	// 	xmlStringBuilder.toString().getBytes("UTF-8"));
	// 	Document doc = builder.parse(input);

	// 	Element root = document.getDocumentElement();

	// 	//returns specific attribute
	// 	getAttribute("attributeName");

	// 	//returns a Map (table) of names/values
	// 	getAttributes();

	// 	//returns a list of subelements of specified name
	// 	getElementsByTagName("subelementName");

	// 	//returns a list of all child nodes
	// 	getChildNodes();
	// }

	public static void main(String[] args) {
		try {
			System.out.println("Hello World!");

			listBuckets("wise-aegis-340917");
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
 try {
         File inputFile = new File("/home/me/Downloads/key.xml");
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(inputFile);
         doc.getDocumentElement().normalize();
         System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
         NodeList nList = doc.getElementsByTagName("name");
         System.out.println("----------------------------");
         
         for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;
               System.out.println("Student name no : " 
                  + eElement.getAttribute("name"));
               System.out.println("Value : " 
                  + eElement
                  .getElementsByTagName("value")
                  .item(0)
                  .getTextContent());
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
	}

}