package com.liferay.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Nícolas Moura
 */
public class HttpClient {

	public static JSONArray get(String path) {
		JSONArray jsonArray = null;

		BufferedReader reader;
		String line;
		StringBuffer responseContent = new StringBuffer();

		try {
			URL url = new URL(path);

			connection = (HttpURLConnection)url.openConnection();

			// Request setup

			String auth = "test@liferay.com:test";

			byte[] encodedAuth = Base64.getEncoder(
			).encode(
				auth.getBytes(StandardCharsets.UTF_8)
			);
			String authHeaderValue = "Basic " + new String(encodedAuth);

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", authHeaderValue);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			int status = connection.getResponseCode();

			if (status > 299) {
				reader = new BufferedReader(
					new InputStreamReader(connection.getErrorStream()));

				while ((line = reader.readLine()) != null) {
					responseContent.append(line);
				}

				reader.close();
			}
			else {
				reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

				while ((line = reader.readLine()) != null) {
					responseContent.append(line);
				}

				reader.close();
			}

			jsonArray = new JSONArray(responseContent.toString());

			System.out.println(jsonArray);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			connection.disconnect();
		}

		return jsonArray;
	}

	public static JSONObject post(
		String urlTarget, HashMap<String, String> params) {

		JSONObject jsonObject = null;

		BufferedReader reader;
		String line;
		StringBuffer responseContent = new StringBuffer();

		try {
			URL url = new URL(urlTarget);

			// bodyPost

			byte[] postDataBytes = parseBodyData(params);

			// Request setup

			String auth = "test@liferay.com:test";

			byte[] encodedAuth = Base64.getEncoder(
			).encode(
				auth.getBytes(StandardCharsets.UTF_8)
			);
			String authHeaderValue = "Basic " + new String(encodedAuth);

			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", authHeaderValue);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty(
				"Content-Length", String.valueOf(postDataBytes.length));
			connection.setDoOutput(true);
			connection.getOutputStream(
			).write(
				postDataBytes
			);

			int status = connection.getResponseCode();

			if (status > 299) {
				reader = new BufferedReader(
					new InputStreamReader(connection.getErrorStream()));

				while ((line = reader.readLine()) != null) {
					responseContent.append(line);
				}

				reader.close();
			}
			else {
				reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

				while ((line = reader.readLine()) != null) {
					responseContent.append(line);
				}

				reader.close();
			}
File auu = new File("/home/me/Downloads/auu.json");
			jsonObject = new JSONObject(auu);

			System.out.println(jsonObject);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			connection.disconnect();
		}

		return jsonObject;
	}

	private static byte[] parseBodyData(HashMap<String, String> params)
		throws IOException, MalformedURLException {

		JSONObject postData = new JSONObject();

		for (HashMap.Entry<String, String> param : params.entrySet()) {
			postData.put(
				URLEncoder.encode(param.getKey(), "UTF-8"),
				String.valueOf(param.getValue()));
		}

		return postData.toString(
		).getBytes(
			"UTF-8"
		);
	}

	private static HttpURLConnection connection;

}