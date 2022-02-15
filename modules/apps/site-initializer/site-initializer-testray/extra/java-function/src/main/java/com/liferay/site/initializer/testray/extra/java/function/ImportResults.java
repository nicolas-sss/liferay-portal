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
import java.util.HashMap; // import the HashMap class
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import com.liferay.util.HttpClient;
import com.liferay.petra.string.StringBundler;


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

	public static void main(String[] args) {
	// 	try {
	// 		System.out.println("Hello World!");

	// 	//	listBuckets("wise-aegis-340917");
	// 	}
	// 	catch (Exception exception) {
	// 		exception.printStackTrace();
	// 	}
 	// 	try {
	// 		File inputFile = new File("/home/me/Downloads/key.xml");
	// 		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	// 		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	// 		Document doc = dBuilder.parse(inputFile);
	// 		doc.getDocumentElement().normalize();
		 
    //     	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
    //     	NodeList nList = doc.getElementsByTagName("environment");

	// 		for (int temp = 0; temp < nList.getLength(); temp++) {
	// 			Node nNode = nList.item(temp);
	// 			System.out.println("\nCurrent Element :" + nNode.getNodeName());
         
    //         if (nNode.getNodeType() == Node.ELEMENT_NODE) {

	// 			String type = nNode.getAttributes().getNamedItem("type").getTextContent();
	// 			System.out.println(type);
    //          }
    //      }
    //   } catch (Exception e) {
    //      e.printStackTrace();
    //   }

	  HashMap<String, String> params = new HashMap<String, String>();

		params.put("name","testray.testcase.priority");
		params.put("value","5");
			HttpClient.post(
					"http://localhost:8080/o/c/"+
					"testrayprojects"+"/scopes/"+"42537",
				params);
	}

}