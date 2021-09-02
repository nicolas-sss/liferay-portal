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

package com.liferay.headless.admin.taxonomy.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyCategoryProperty;
import com.liferay.headless.admin.taxonomy.client.http.HttpInvoker;
import com.liferay.headless.admin.taxonomy.client.pagination.Page;
import com.liferay.headless.admin.taxonomy.client.resource.v1_0.TaxonomyCategoryPropertyResource;
import com.liferay.headless.admin.taxonomy.client.serdes.v1_0.TaxonomyCategoryPropertySerDes;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Generated;

import javax.ws.rs.core.MultivaluedHashMap;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.time.DateUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseTaxonomyCategoryPropertyResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_taxonomyCategoryPropertyResource.setContextCompany(testCompany);

		TaxonomyCategoryPropertyResource.Builder builder =
			TaxonomyCategoryPropertyResource.builder();

		taxonomyCategoryPropertyResource = builder.authentication(
			"test@liferay.com", "test"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};

		TaxonomyCategoryProperty taxonomyCategoryProperty1 =
			randomTaxonomyCategoryProperty();

		String json = objectMapper.writeValueAsString(
			taxonomyCategoryProperty1);

		TaxonomyCategoryProperty taxonomyCategoryProperty2 =
			TaxonomyCategoryPropertySerDes.toDTO(json);

		Assert.assertTrue(
			equals(taxonomyCategoryProperty1, taxonomyCategoryProperty2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};

		TaxonomyCategoryProperty taxonomyCategoryProperty =
			randomTaxonomyCategoryProperty();

		String json1 = objectMapper.writeValueAsString(
			taxonomyCategoryProperty);
		String json2 = TaxonomyCategoryPropertySerDes.toJSON(
			taxonomyCategoryProperty);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		TaxonomyCategoryProperty taxonomyCategoryProperty =
			randomTaxonomyCategoryProperty();

		taxonomyCategoryProperty.setDescription(regex);
		taxonomyCategoryProperty.setExternalReferenceCode(regex);
		taxonomyCategoryProperty.setId(regex);
		taxonomyCategoryProperty.setName(regex);
		taxonomyCategoryProperty.setValue(regex);

		String json = TaxonomyCategoryPropertySerDes.toJSON(
			taxonomyCategoryProperty);

		Assert.assertFalse(json.contains(regex));

		taxonomyCategoryProperty = TaxonomyCategoryPropertySerDes.toDTO(json);

		Assert.assertEquals(regex, taxonomyCategoryProperty.getDescription());
		Assert.assertEquals(
			regex, taxonomyCategoryProperty.getExternalReferenceCode());
		Assert.assertEquals(regex, taxonomyCategoryProperty.getId());
		Assert.assertEquals(regex, taxonomyCategoryProperty.getName());
		Assert.assertEquals(regex, taxonomyCategoryProperty.getValue());
	}

	@Test
	public void testGetTaxonomyCategoryPropertiesPage() throws Exception {
		Page<TaxonomyCategoryProperty> page =
			taxonomyCategoryPropertyResource.getTaxonomyCategoryPropertiesPage(
				testGetTaxonomyCategoryPropertiesPage_getTaxonomyCategoryId());

		Assert.assertEquals(0, page.getTotalCount());

		String taxonomyCategoryId =
			testGetTaxonomyCategoryPropertiesPage_getTaxonomyCategoryId();
		String irrelevantTaxonomyCategoryId =
			testGetTaxonomyCategoryPropertiesPage_getIrrelevantTaxonomyCategoryId();

		if (irrelevantTaxonomyCategoryId != null) {
			TaxonomyCategoryProperty irrelevantTaxonomyCategoryProperty =
				testGetTaxonomyCategoryPropertiesPage_addTaxonomyCategoryProperty(
					irrelevantTaxonomyCategoryId,
					randomIrrelevantTaxonomyCategoryProperty());

			page =
				taxonomyCategoryPropertyResource.
					getTaxonomyCategoryPropertiesPage(
						irrelevantTaxonomyCategoryId);

			Assert.assertEquals(1, page.getTotalCount());

			assertEquals(
				Arrays.asList(irrelevantTaxonomyCategoryProperty),
				(List<TaxonomyCategoryProperty>)page.getItems());
			assertValid(page);
		}

		TaxonomyCategoryProperty taxonomyCategoryProperty1 =
			testGetTaxonomyCategoryPropertiesPage_addTaxonomyCategoryProperty(
				taxonomyCategoryId, randomTaxonomyCategoryProperty());

		TaxonomyCategoryProperty taxonomyCategoryProperty2 =
			testGetTaxonomyCategoryPropertiesPage_addTaxonomyCategoryProperty(
				taxonomyCategoryId, randomTaxonomyCategoryProperty());

		page =
			taxonomyCategoryPropertyResource.getTaxonomyCategoryPropertiesPage(
				taxonomyCategoryId);

		Assert.assertEquals(2, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(taxonomyCategoryProperty1, taxonomyCategoryProperty2),
			(List<TaxonomyCategoryProperty>)page.getItems());
		assertValid(page);
	}

	protected TaxonomyCategoryProperty
			testGetTaxonomyCategoryPropertiesPage_addTaxonomyCategoryProperty(
				String taxonomyCategoryId,
				TaxonomyCategoryProperty taxonomyCategoryProperty)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetTaxonomyCategoryPropertiesPage_getTaxonomyCategoryId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetTaxonomyCategoryPropertiesPage_getIrrelevantTaxonomyCategoryId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetTaxonomyCategoryPropertiesPage()
		throws Exception {

		String taxonomyCategoryId =
			testGetTaxonomyCategoryPropertiesPage_getTaxonomyCategoryId();

		GraphQLField graphQLField = new GraphQLField(
			"taxonomyCategoryProperties",
			new HashMap<String, Object>() {
				{
					put("taxonomyCategoryId", "\"" + taxonomyCategoryId + "\"");
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject taxonomyCategoryPropertiesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/taxonomyCategoryProperties");

		Assert.assertEquals(
			0, taxonomyCategoryPropertiesJSONObject.get("totalCount"));

		TaxonomyCategoryProperty taxonomyCategoryProperty1 =
			testGraphQLTaxonomyCategoryProperty_addTaxonomyCategoryProperty();
		TaxonomyCategoryProperty taxonomyCategoryProperty2 =
			testGraphQLTaxonomyCategoryProperty_addTaxonomyCategoryProperty();

		taxonomyCategoryPropertiesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/taxonomyCategoryProperties");

		Assert.assertEquals(
			2, taxonomyCategoryPropertiesJSONObject.get("totalCount"));

		assertEqualsIgnoringOrder(
			Arrays.asList(taxonomyCategoryProperty1, taxonomyCategoryProperty2),
			Arrays.asList(
				TaxonomyCategoryPropertySerDes.toDTOs(
					taxonomyCategoryPropertiesJSONObject.getString("items"))));
	}

	protected TaxonomyCategoryProperty
			testGraphQLTaxonomyCategoryProperty_addTaxonomyCategoryProperty()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		TaxonomyCategoryProperty taxonomyCategoryProperty1,
		TaxonomyCategoryProperty taxonomyCategoryProperty2) {

		Assert.assertTrue(
			taxonomyCategoryProperty1 + " does not equal " +
				taxonomyCategoryProperty2,
			equals(taxonomyCategoryProperty1, taxonomyCategoryProperty2));
	}

	protected void assertEquals(
		List<TaxonomyCategoryProperty> taxonomyCategoryProperties1,
		List<TaxonomyCategoryProperty> taxonomyCategoryProperties2) {

		Assert.assertEquals(
			taxonomyCategoryProperties1.size(),
			taxonomyCategoryProperties2.size());

		for (int i = 0; i < taxonomyCategoryProperties1.size(); i++) {
			TaxonomyCategoryProperty taxonomyCategoryProperty1 =
				taxonomyCategoryProperties1.get(i);
			TaxonomyCategoryProperty taxonomyCategoryProperty2 =
				taxonomyCategoryProperties2.get(i);

			assertEquals(taxonomyCategoryProperty1, taxonomyCategoryProperty2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<TaxonomyCategoryProperty> taxonomyCategoryProperties1,
		List<TaxonomyCategoryProperty> taxonomyCategoryProperties2) {

		Assert.assertEquals(
			taxonomyCategoryProperties1.size(),
			taxonomyCategoryProperties2.size());

		for (TaxonomyCategoryProperty taxonomyCategoryProperty1 :
				taxonomyCategoryProperties1) {

			boolean contains = false;

			for (TaxonomyCategoryProperty taxonomyCategoryProperty2 :
					taxonomyCategoryProperties2) {

				if (equals(
						taxonomyCategoryProperty1, taxonomyCategoryProperty2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				taxonomyCategoryProperties2 + " does not contain " +
					taxonomyCategoryProperty1,
				contains);
		}
	}

	protected void assertValid(
			TaxonomyCategoryProperty taxonomyCategoryProperty)
		throws Exception {

		boolean valid = true;

		if (taxonomyCategoryProperty.getDateCreated() == null) {
			valid = false;
		}

		if (taxonomyCategoryProperty.getDateModified() == null) {
			valid = false;
		}

		if (taxonomyCategoryProperty.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (taxonomyCategoryProperty.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (taxonomyCategoryProperty.getAvailableLanguages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (taxonomyCategoryProperty.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (taxonomyCategoryProperty.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (taxonomyCategoryProperty.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (taxonomyCategoryProperty.getExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (taxonomyCategoryProperty.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (taxonomyCategoryProperty.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("value", additionalAssertFieldName)) {
				if (taxonomyCategoryProperty.getValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (taxonomyCategoryProperty.getViewableBy() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<TaxonomyCategoryProperty> page) {
		boolean valid = false;

		java.util.Collection<TaxonomyCategoryProperty>
			taxonomyCategoryProperties = page.getItems();

		int size = taxonomyCategoryProperties.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (Field field :
				getDeclaredFields(
					com.liferay.headless.admin.taxonomy.dto.v1_0.
						TaxonomyCategoryProperty.class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(
		TaxonomyCategoryProperty taxonomyCategoryProperty1,
		TaxonomyCategoryProperty taxonomyCategoryProperty2) {

		if (taxonomyCategoryProperty1 == taxonomyCategoryProperty2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)taxonomyCategoryProperty1.getActions(),
						(Map)taxonomyCategoryProperty2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyCategoryProperty1.getAvailableLanguages(),
						taxonomyCategoryProperty2.getAvailableLanguages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategoryProperty1.getCreator(),
						taxonomyCategoryProperty2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategoryProperty1.getDateCreated(),
						taxonomyCategoryProperty2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategoryProperty1.getDateModified(),
						taxonomyCategoryProperty2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategoryProperty1.getDescription(),
						taxonomyCategoryProperty2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)taxonomyCategoryProperty1.getDescription_i18n(),
						(Map)taxonomyCategoryProperty2.getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyCategoryProperty1.getExternalReferenceCode(),
						taxonomyCategoryProperty2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategoryProperty1.getId(),
						taxonomyCategoryProperty2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategoryProperty1.getName(),
						taxonomyCategoryProperty2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)taxonomyCategoryProperty1.getName_i18n(),
						(Map)taxonomyCategoryProperty2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("value", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategoryProperty1.getValue(),
						taxonomyCategoryProperty2.getValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyCategoryProperty1.getViewableBy(),
						taxonomyCategoryProperty2.getViewableBy())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected Field[] getDeclaredFields(Class clazz) throws Exception {
		Stream<Field> stream = Stream.of(
			ReflectionUtil.getDeclaredFields(clazz));

		return stream.filter(
			field -> !field.isSynthetic()
		).toArray(
			Field[]::new
		);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_taxonomyCategoryPropertyResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_taxonomyCategoryPropertyResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		java.util.Collection<EntityField> entityFields = getEntityFields();

		Stream<EntityField> stream = entityFields.stream();

		return stream.filter(
			entityField ->
				Objects.equals(entityField.getType(), type) &&
				!ArrayUtil.contains(
					getIgnoredEntityFieldNames(), entityField.getName())
		).collect(
			Collectors.toList()
		);
	}

	protected String getFilterString(
		EntityField entityField, String operator,
		TaxonomyCategoryProperty taxonomyCategoryProperty) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("availableLanguages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(
					_dateFormat.format(
						DateUtils.addSeconds(
							taxonomyCategoryProperty.getDateCreated(), -2)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(
					_dateFormat.format(
						DateUtils.addSeconds(
							taxonomyCategoryProperty.getDateCreated(), 2)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(
					_dateFormat.format(
						taxonomyCategoryProperty.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(
					_dateFormat.format(
						DateUtils.addSeconds(
							taxonomyCategoryProperty.getDateModified(), -2)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(
					_dateFormat.format(
						DateUtils.addSeconds(
							taxonomyCategoryProperty.getDateModified(), 2)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(
					_dateFormat.format(
						taxonomyCategoryProperty.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			sb.append("'");
			sb.append(
				String.valueOf(taxonomyCategoryProperty.getDescription()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("description_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			sb.append("'");
			sb.append(
				String.valueOf(
					taxonomyCategoryProperty.getExternalReferenceCode()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			sb.append("'");
			sb.append(String.valueOf(taxonomyCategoryProperty.getId()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			sb.append("'");
			sb.append(String.valueOf(taxonomyCategoryProperty.getName()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("name_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("value")) {
			sb.append("'");
			sb.append(String.valueOf(taxonomyCategoryProperty.getValue()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("viewableBy")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword("test@liferay.com:test");

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected TaxonomyCategoryProperty randomTaxonomyCategoryProperty()
		throws Exception {

		return new TaxonomyCategoryProperty() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				value = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected TaxonomyCategoryProperty
			randomIrrelevantTaxonomyCategoryProperty()
		throws Exception {

		TaxonomyCategoryProperty randomIrrelevantTaxonomyCategoryProperty =
			randomTaxonomyCategoryProperty();

		return randomIrrelevantTaxonomyCategoryProperty;
	}

	protected TaxonomyCategoryProperty randomPatchTaxonomyCategoryProperty()
		throws Exception {

		return randomTaxonomyCategoryProperty();
	}

	protected TaxonomyCategoryPropertyResource taxonomyCategoryPropertyResource;
	protected Group irrelevantGroup;
	protected Company testCompany;
	protected Group testGroup;

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(
			BaseTaxonomyCategoryPropertyResourceTestCase.class);

	private static BeanUtilsBean _beanUtilsBean = new BeanUtilsBean() {

		@Override
		public void copyProperty(Object bean, String name, Object value)
			throws IllegalAccessException, InvocationTargetException {

			if (value != null) {
				super.copyProperty(bean, name, value);
			}
		}

	};
	private static DateFormat _dateFormat;

	@Inject
	private com.liferay.headless.admin.taxonomy.resource.v1_0.
		TaxonomyCategoryPropertyResource _taxonomyCategoryPropertyResource;

}