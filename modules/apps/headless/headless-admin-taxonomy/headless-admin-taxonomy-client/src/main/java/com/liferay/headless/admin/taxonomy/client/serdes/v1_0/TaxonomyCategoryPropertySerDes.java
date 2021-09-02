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

package com.liferay.headless.admin.taxonomy.client.serdes.v1_0;

import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyCategoryProperty;
import com.liferay.headless.admin.taxonomy.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class TaxonomyCategoryPropertySerDes {

	public static TaxonomyCategoryProperty toDTO(String json) {
		TaxonomyCategoryPropertyJSONParser taxonomyCategoryPropertyJSONParser =
			new TaxonomyCategoryPropertyJSONParser();

		return taxonomyCategoryPropertyJSONParser.parseToDTO(json);
	}

	public static TaxonomyCategoryProperty[] toDTOs(String json) {
		TaxonomyCategoryPropertyJSONParser taxonomyCategoryPropertyJSONParser =
			new TaxonomyCategoryPropertyJSONParser();

		return taxonomyCategoryPropertyJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		TaxonomyCategoryProperty taxonomyCategoryProperty) {

		if (taxonomyCategoryProperty == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		if (taxonomyCategoryProperty.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(taxonomyCategoryProperty.getActions()));
		}

		if (taxonomyCategoryProperty.getAvailableLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0;
				 i < taxonomyCategoryProperty.getAvailableLanguages().length;
				 i++) {

				sb.append("\"");

				sb.append(
					_escape(
						taxonomyCategoryProperty.getAvailableLanguages()[i]));

				sb.append("\"");

				if ((i + 1) <
						taxonomyCategoryProperty.
							getAvailableLanguages().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (taxonomyCategoryProperty.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(taxonomyCategoryProperty.getCreator()));
		}

		if (taxonomyCategoryProperty.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					taxonomyCategoryProperty.getDateCreated()));

			sb.append("\"");
		}

		if (taxonomyCategoryProperty.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					taxonomyCategoryProperty.getDateModified()));

			sb.append("\"");
		}

		if (taxonomyCategoryProperty.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategoryProperty.getDescription()));

			sb.append("\"");
		}

		if (taxonomyCategoryProperty.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(taxonomyCategoryProperty.getDescription_i18n()));
		}

		if (taxonomyCategoryProperty.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(taxonomyCategoryProperty.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (taxonomyCategoryProperty.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategoryProperty.getId()));

			sb.append("\"");
		}

		if (taxonomyCategoryProperty.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategoryProperty.getName()));

			sb.append("\"");
		}

		if (taxonomyCategoryProperty.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(taxonomyCategoryProperty.getName_i18n()));
		}

		if (taxonomyCategoryProperty.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategoryProperty.getValue()));

			sb.append("\"");
		}

		if (taxonomyCategoryProperty.getViewableBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(taxonomyCategoryProperty.getViewableBy());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaxonomyCategoryPropertyJSONParser taxonomyCategoryPropertyJSONParser =
			new TaxonomyCategoryPropertyJSONParser();

		return taxonomyCategoryPropertyJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TaxonomyCategoryProperty taxonomyCategoryProperty) {

		if (taxonomyCategoryProperty == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		if (taxonomyCategoryProperty.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(taxonomyCategoryProperty.getActions()));
		}

		if (taxonomyCategoryProperty.getAvailableLanguages() == null) {
			map.put("availableLanguages", null);
		}
		else {
			map.put(
				"availableLanguages",
				String.valueOf(
					taxonomyCategoryProperty.getAvailableLanguages()));
		}

		if (taxonomyCategoryProperty.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator",
				String.valueOf(taxonomyCategoryProperty.getCreator()));
		}

		if (taxonomyCategoryProperty.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					taxonomyCategoryProperty.getDateCreated()));
		}

		if (taxonomyCategoryProperty.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					taxonomyCategoryProperty.getDateModified()));
		}

		if (taxonomyCategoryProperty.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(taxonomyCategoryProperty.getDescription()));
		}

		if (taxonomyCategoryProperty.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(taxonomyCategoryProperty.getDescription_i18n()));
		}

		if (taxonomyCategoryProperty.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					taxonomyCategoryProperty.getExternalReferenceCode()));
		}

		if (taxonomyCategoryProperty.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(taxonomyCategoryProperty.getId()));
		}

		if (taxonomyCategoryProperty.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(taxonomyCategoryProperty.getName()));
		}

		if (taxonomyCategoryProperty.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put(
				"name_i18n",
				String.valueOf(taxonomyCategoryProperty.getName_i18n()));
		}

		if (taxonomyCategoryProperty.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put(
				"value", String.valueOf(taxonomyCategoryProperty.getValue()));
		}

		if (taxonomyCategoryProperty.getViewableBy() == null) {
			map.put("viewableBy", null);
		}
		else {
			map.put(
				"viewableBy",
				String.valueOf(taxonomyCategoryProperty.getViewableBy()));
		}

		return map;
	}

	public static class TaxonomyCategoryPropertyJSONParser
		extends BaseJSONParser<TaxonomyCategoryProperty> {

		@Override
		protected TaxonomyCategoryProperty createDTO() {
			return new TaxonomyCategoryProperty();
		}

		@Override
		protected TaxonomyCategoryProperty[] createDTOArray(int size) {
			return new TaxonomyCategoryProperty[size];
		}

		@Override
		protected void setField(
			TaxonomyCategoryProperty taxonomyCategoryProperty,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setActions(
						(Map)TaxonomyCategoryPropertySerDes.toMap(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setDescription_i18n(
						(Map)TaxonomyCategoryPropertySerDes.toMap(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setName_i18n(
						(Map)TaxonomyCategoryPropertySerDes.toMap(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setValue(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setViewableBy(
						TaxonomyCategoryProperty.ViewableBy.create(
							(String)jsonParserFieldValue));
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			Class<?> valueClass = value.getClass();

			if (value instanceof Map) {
				sb.append(_toJSON((Map)value));
			}
			else if (valueClass.isArray()) {
				Object[] values = (Object[])value;

				sb.append("[");

				for (int i = 0; i < values.length; i++) {
					sb.append("\"");
					sb.append(_escape(values[i]));
					sb.append("\"");

					if ((i + 1) < values.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(entry.getValue()));
				sb.append("\"");
			}
			else {
				sb.append(String.valueOf(entry.getValue()));
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

}