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

package com.liferay.webhook;

import com.liferay.headless.delivery.client.resource.v1_0.DocumentResource;
import com.liferay.headless.delivery.client.resource.v1_0.Document;

import com.liferay.util.DatabaseConnection;
import com.liferay.util.HttpClient;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * @author Nícolas Moura
 */
@RequestMapping("/testray")
@RestController
public class TestrayWebhookRestController {

	@GetMapping("{value}")
	public String getValue(@PathVariable(required = false) String value) {
		return value;
	}

	@PostMapping("{siteId}")
	public void postValue(
		@PathVariable(required = true) Long siteId, @RequestBody String body) {

		try {
			Connection conn = DatabaseConnection.getConnection();

			// Getting Data from DB

			List<Map<String, Object>> testrayArchives = getTestrayArchives(
				conn);
//			List<Map<String, Object>> testrayAssignments =
//				getTestrayAssignments(conn);
//			List<Map<String, Object>> testrayBuilds = getTestrayBuilds(conn);
//			List<Map<String, Object>> testrayCases = getTestrayCases(conn);
//			List<Map<String, Object>> testrayCaseTypes = getTestrayCaseTypes(conn);
//			List<Map<String, Object>> testrayCaseResults =
//				getTestrayCaseResults(conn);
//			List<Map<String, Object>> testrayCaseResultWarnings =
//				getTestrayCaseResultWarnings(conn);
//			List<Map<String, Object>> testrayComponents =
//				getTestrayComponents(conn);
//			List<Map<String, Object>> testrayFactors = getTestrayFactors(conn);
//			List<Map<String, Object>> testrayFactorOptions =
//				getTestrayFactorOptions(conn);
//			List<Map<String, Object>> testrayFactorCategories =
//				getTestrayFactorCategories(conn);
//			List<Map<String, Object>> testrayIssues = getTestrayIssues(conn);
//			List<Map<String, Object>> testrayProductVersions =
//				getTestrayProductVersions(conn);
//			List<Map<String, Object>> testrayProjects =
//				getTestrayProjects(conn);
//			List<Map<String, Object>> testrayRequirements = getTestrayRequirements(conn);
//			List<Map<String, Object>> testrayRoutines = getTestrayRoutines(conn);
//			List<Map<String, Object>> testrayRuns = getTestrayRuns(conn);
//			List<Map<String, Object>> testraySubTasks = getTestraySubTasks(conn);
//			List<Map<String, Object>> testraySuites = getTestraySuites(conn);
//			List<Map<String, Object>> testrayTasks = getTestrayTasks(conn);
//			List<Map<String, Object>> testrayTeams = getTestrayTeams(conn);

			// Post Batch Convertion

			createTestrayObjectEntrys(
				"TestrayArchives", siteId, testrayArchives);
//			createTestrayObjectEntrys(
//				"TestrayAssignments", siteId, testrayAssignments);
//			createTestrayObjectEntrys("TestrayBuilds", siteId, testrayBuilds);
//			createTestrayObjectEntrys("TestrayCases", siteId, testrayCases);
//			createTestrayObjectEntrys("TestrayCaseTypes", siteId, testrayCaseTypes);
//			createTestrayObjectEntrys(
//				"TestrayCaseResults", siteId, testrayCaseResults);
//			createTestrayObjectEntrys(
//				"TestrayCaseResultWarnings",
//				siteId, testrayCaseResultWarnings);
//			createTestrayObjectEntrys(
//				"TestrayComponents", siteId, testrayComponents);
//			createTestrayObjectEntrys("TestrayFactors", siteId, testrayFactors);
//			createTestrayObjectEntrys(
//				"TestrayFactorOptions", siteId, testrayFactorOptions);
//			createTestrayObjectEntrys(
//				"TestrayFactorCategories", siteId, testrayFactorCategories);
//			createTestrayObjectEntrys("TestrayIssues", siteId, testrayIssues);
//			createTestrayObjectEntrys("TestrayProductVersions", siteId, testrayProductVersions);
//			createTestrayObjectEntrys("TestrayProjects", siteId, testrayProjects); // verificar
//			createTestrayObjectEntrys("TestrayRequirements", siteId, testrayRequirements); // verificar
//			createTestrayObjectEntrys("TestrayRoutines", siteId, testrayRoutines);
//			createTestrayObjectEntrys("TestrayRuns", siteId, testrayRuns); //verificar
//			createTestrayObjectEntrys("TestraySubTasks", siteId, testraySubTasks);
//			createTestrayObjectEntrys("TestraySuites", siteId, testraySuites);
//			createTestrayObjectEntrys("TestrayTasks", siteId, testrayTasks);
//			createTestrayObjectEntrys("TestrayTeams", siteId, testrayTeams);

		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}
	}

	// Headless Requests

	private void createTestrayObjectEntrys(
		String objectName, Long siteId, List<Map<String, Object>> rows) {

		for (Map<String, Object> params : rows) {
			HttpClient.post(
				StringBundler.concat(
					"http://localhost:8080/o/c/",
					StringUtil.toLowerCase(objectName), "/scopes/", siteId),
				params);
		}
	}

	// Database Requests

	private List<Map<String, Object>> getTestrayArchives(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			DocumentResource.Builder builder = DocumentResource.builder();

			DocumentResource documentResource = builder.authentication(
				"test@liferay.com", "test"
			).locale(
				LocaleUtil.getDefault()
			).build();
			
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT classNameId, classPK, compressedData FROM OSB_TestrayArchive");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("classNameId", resultSet.getLong("classNameId"));
				params.put("classPK", resultSet.getLong("classPK"));


				byte[] imageByte = resultSet.getBlob("compressedData");
				byte[] encodedImage = Base64.encodeBase64(imageByte);

				// TODO Logic for blob convertion to documents and media

//				documentResource.postSiteDocument()

				params.put("dataId", 548484); // put document ID

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayAssignments(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT assignedUserId, classNameId, classPK FROM OSB_TestrayAssignment");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put(
					"assignedUserId", resultSet.getLong("assignedUserId"));
				params.put("classNameId", resultSet.getLong("classNameId"));
				params.put("classPK", resultSet.getLong("classPK"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayBuilds(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT description, dueDate, dueStatus, gitHash, " +
					"githubCompareURLs, name, promoted, template, " +
						"templateTestrayBuildId, testrayProductVersionId, " +
							"testrayProjectId, testrayRoutineId FROM OSB_TestrayBuild");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("description", resultSet.getString("description"));
				params.put("dueDate", resultSet.getDate("dueDate"));
				params.put("dueStatus", resultSet.getInt("dueStatus"));
				params.put("gitHash", resultSet.getString("gitHash"));
				params.put(
					"githubCompareURLs",
					resultSet.getString("githubCompareURLs"));
				params.put("name", resultSet.getString("name"));
				params.put("promoted", resultSet.getBoolean("promoted"));
				params.put("template", resultSet.getBoolean("template"));
				params.put(
					"templateTestrayBuildId",
					resultSet.getLong("templateTestrayBuildId"));
				params.put(
					"testrayProductVersionId",
					resultSet.getLong("testrayProductVersionId"));
				params.put(
					"testrayProjectId", resultSet.getLong("testrayProjectId"));
				params.put(
					"testrayRoutineId", resultSet.getLong("testrayRoutineId"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayCases(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT caseNumber, description, descriptionType, " +
				"estimatedDuration, name, originationKey, priority, steps, " +
				"stepsType, testrayCaseTypeId, testrayComponentId, " +
				"testrayProjectId FROM OSB_TestrayCase");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("caseNumber", resultSet.getLong("caseNumber"));
				params.put("description", resultSet.getString("description"));
				params.put(
					"descriptionType", resultSet.getString("descriptionType"));
				params.put(
					"estimatedDuration", resultSet.getInt("estimatedDuration"));
				params.put("name", resultSet.getString("name"));
				params.put("originationKey", resultSet.getLong("originationKey"));
				params.put("priority", resultSet.getInt("priority"));
				params.put("steps", resultSet.getString("steps"));
				params.put("stepsType", resultSet.getString("stepsType"));
				params.put("testrayCaseTypeId", resultSet.getLong("testrayCaseTypeId"));
				params.put(
					"testrayComponentId", resultSet.getLong("testrayComponentId"));
				params.put(
					"testrayProjectId", resultSet.getLong("testrayProjectId"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayCaseTypes(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT name FROM OSB_TestrayCaseType");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("name", resultSet.getString("name"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayCaseResults(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT assignedUserId, attachments, closedDate, " +
				"commentMBMessageId, dueStatus, errors, startDate, " +
				"testrayBuildId, testrayCaseId, testrayComponentId, " +
				"testrayRunId FROM OSB_TestrayCaseResult");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put(
					"assignedUserId", resultSet.getLong("assignedUserId"));
				params.put("attachments", resultSet.getString("attachments"));
				params.put("closedDate", resultSet.getDate("closedDate"));
				params.put(
					"commentMBMessageId", resultSet.getLong("commentMBMessageId"));
				params.put("dueStatus", resultSet.getInt("dueStatus"));
				params.put("errors", resultSet.getString("errors"));
				params.put("startDate", resultSet.getDate("startDate"));
				params.put("testrayBuildId", resultSet.getLong("testrayBuildId"));
				params.put("testrayCaseId", resultSet.getLong("testrayCaseId"));
				params.put(
					"testrayComponentId", resultSet.getLong("testrayComponentId"));
				params.put("testrayRunId", resultSet.getLong("testrayRunId"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayCaseResultWarnings(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT content, testrayCaseResultId FROM OSB_TestrayCaseResultWarning");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("content", resultSet.getString("content"));
				params.put(
					"testrayCaseResultId",
					resultSet.getLong("testrayCaseResultId"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayComponents(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT name, originationKey, testrayProjectId, " +
				"testrayTeamId FROM OSB_TestrayComponent");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("name", resultSet.getString("name"));
				params.put("originationKey", resultSet.getLong("originationKey"));
				params.put(
					"testrayProjectId", resultSet.getLong("testrayProjectId"));
				params.put("testrayTeamId", resultSet.getLong("testrayTeamId"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayFactors(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT classNameId, classPK, testrayFactorCategoryId, " +
				"testrayFactorCategoryName, testrayFactorOptionId, " +
				"testrayFactorOptionName FROM OSB_TestrayFactor");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("classNameId", resultSet.getLong("classNameId"));
				params.put("classPK", resultSet.getLong("classPK"));
				params.put(
					"testrayFactorCategoryId",
					resultSet.getLong("testrayFactorCategoryId"));
				params.put(
					"testrayFactorCategoryName",
					resultSet.getString("testrayFactorCategoryName"));
				params.put(
					"testrayFactorOptionId",
					resultSet.getLong("testrayFactorOptionId"));
				params.put(
					"testrayFactorOptionName",
					resultSet.getString("testrayFactorOptionName"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayFactorOptions(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT name, testrayFactorCategoryId FROM OSB_TestrayFactorOption");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("name", resultSet.getString("name"));
				params.put(
					"testrayFactorCategoryId",
					resultSet.getLong("testrayFactorCategoryId"));


				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayFactorCategories(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT name FROM OSB_TestrayFactorCategory");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("name", resultSet.getString("name"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayIssues(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT name FROM OSB_TestrayIssue");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("name", resultSet.getString("name"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayProductVersions(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT name, testrayProjectId FROM OSB_TestrayProductVersion");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("name", resultSet.getString("name"));
				params.put("testrayProjectId", resultSet.getLong("testrayProjectId"));


				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayProjects(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT description, name FROM OSB_TestrayProject");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("description", resultSet.getString("description"));
				params.put("name", resultSet.getString("name"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayRequirements(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT components, description, descriptionType, tr.key, " +
				"linkTitle, linkURL, summary, testrayComponentId, " +
				"testrayProjectId FROM OSB_TestrayRequirement as tr");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("components", resultSet.getString("components"));
				params.put("description", resultSet.getString("description"));
				params.put(
					"descriptionType", resultSet.getString("descriptionType"));
				params.put("key", resultSet.getString("key"));
				params.put("linkTitle", resultSet.getString("linkTitle"));
				params.put("linkURL", resultSet.getString("linkURL"));
				params.put("summary", resultSet.getString("summary"));
				params.put(
					"testrayComponentId", resultSet.getLong("testrayComponentId"));
				params.put("testrayProjectId", resultSet.getLong("testrayProjectId"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayRoutines(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT autoanalyze, name, testrayProjectId FROM OSB_TestrayRoutine");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("autoanalyze", resultSet.getBoolean("autoanalyze"));
				params.put("name", resultSet.getString("name"));
				params.put(
					"testrayProjectId", resultSet.getLong("testrayProjectId"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayRuns(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT description, environmentHash, externalReferencePK, " +
				"externalReferenceType, jenkinsJobKey, " +
				"name, number, testrayBuildId FROM OSB_TestrayRun");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("description", resultSet.getString("description"));
				params.put(
					"environmentHash", resultSet.getString("environmentHash"));
				params.put(
					"externalReferencePK", resultSet.getString("externalReferencePK"));
				params.put(
					"externalReferenceType", resultSet.getInt("externalReferenceType"));
				params.put("jenkinsJobKey", resultSet.getLong("jenkinsJobKey")); // aqui zuou
				params.put("name",resultSet.getString("name"));
				params.put("number", resultSet.getLong("number"));
				params.put("testrayBuildId", resultSet.getLong("testrayBuildId"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestraySubTasks(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT commentMBMessageId, dueStatus, " +
				"mergedToTestraySubtaskId, name, score, " +
				"splitFromTestraySubtaskId, statusUpdateDate, " +
				"testrayTaskId FROM OSB_TestraySubTask");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put(
					"commentMBMessageId", resultSet.getLong("commentMBMessageId"));
				params.put("dueStatus", resultSet.getInt("dueStatus"));
				params.put(
					"mergedToTestraySubtaskId",
					resultSet.getLong("mergedToTestraySubtaskId"));
				params.put("name", resultSet.getString("name"));
				params.put("score", resultSet.getInt("score"));
				params.put(
					"splitFromTestraySubtaskId",
					resultSet.getLong("splitFromTestraySubtaskId"));
				params.put("statusUpdateDate", resultSet.getDate("statusUpdateDate"));
				params.put("testrayTaskId", resultSet.getLong("testrayTaskId"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestraySuites(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT caseParameters, description, name, " +
				"testrayProjectId FROM OSB_TestraySuite");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put(
					"caseParameters", resultSet.getString("caseParameters"));
				params.put("description", resultSet.getString("description"));
				params.put("name", resultSet.getString("name"));
				params.put(
					"testrayProjectId", resultSet.getLong("testrayProjectId"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayTasks(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT dueStatus, name, statusUpdateDate, " +
				"testrayBuildId FROM OSB_TestrayTask");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("dueStatus", resultSet.getInt("dueStatus"));
				params.put("name", resultSet.getString("name"));
				params.put("statusUpdateDate", resultSet.getDate("statusUpdateDate"));
				params.put("testrayBuildId", resultSet.getLong("testrayBuildId"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private List<Map<String, Object>> getTestrayTeams(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT name, testrayProjectId FROM OSB_TestrayTeam");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

				params.put("name", resultSet.getString("name"));
				params.put(
					"testrayProjectId", resultSet.getLong("testrayProjectId"));

				rows.add(params);
			}
		}
		catch (Exception exception) {
			_log.info("Exception: " + exception);
		}

		return rows;
	}

	private static final Log _log = LogFactory.getLog(
		TestrayWebhookRestController.class);

}