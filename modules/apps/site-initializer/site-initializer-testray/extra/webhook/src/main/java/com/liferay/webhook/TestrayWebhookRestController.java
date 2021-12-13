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

import com.liferay.util.DatabaseConnection;
import com.liferay.util.HttpClient;
import com.liferay.petra.string.StringBundler;
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
				conn); //Mudar pra todos usarem a mesma variavel?
			List<Map<String, Object>> testrayAssignments =
				getTestrayAssignments(conn);
			List<Map<String, Object>> testrayBuilds = getTestrayBuilds(conn);
			List<Map<String, Object>> testrayCases = getTestrayCases(conn);
			List<Map<String, Object>> testrayCaseTypes = getTestrayCaseTypes(conn);
			List<Map<String, Object>> testrayCaseResults =
				getTestrayCaseResults(conn);
			List<Map<String, Object>> testrayCaseResultWarnings =
				getTestrayCaseResultWarnings(conn);
			List<Map<String, Object>> testrayComponents =
				getTestrayComponents(conn);
			List<Map<String, Object>> testrayFactors = getTestrayFactors(conn);


			// Post Batch Convertion

			createTestrayObjectEntrys(
				"TestrayArchives", siteId, testrayArchives);
			createTestrayObjectEntrys(
				"TestrayAssignments", siteId, testrayAssignments);
			createTestrayObjectEntrys("TestrayBuilds", siteId, testrayBuilds);
			createTestrayObjectEntrys("TestrayCases", siteId, testrayCases);
			createTestrayObjectEntrys("TestrayCaseTypes", siteId, testrayCaseTypes);
			createTestrayObjectEntrys(
				"TestrayCaseResults", siteId, testrayCaseResults);
			createTestrayObjectEntrys(
				"TestrayCaseResultWarnings",
				siteId, testrayCaseResultWarnings);
			createTestrayObjectEntrys(
				"TestrayComponents", siteId, testrayComponents);
			createTestrayObjectEntrys("TestrayFactors", siteId, testrayFactors);

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
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT classNameId, classPK FROM TestrayArchive");

			while (resultSet.next()) {
				Map<String, Object> params = new LinkedHashMap<>();

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

	private List<Map<String, Object>> getTestrayAssignments(Connection conn) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(
				"SELECT assignedUserId, classNameId, classPK FROM TestrayAssignment");

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
							"testrayProjectId, testrayRoutineId FROM TestrayBuild");

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
				"testrayProjectId FROM TestrayCase");

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
				"SELECT name FROM TestrayCaseType");

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
				"testrayRunId FROM TestrayCaseResult");

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
				"SELECT content, testrayCaseResultId FROM TestrayCaseResultWarning");

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
				"testrayTeamId FROM TestrayComponent");

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
				"testrayFactorOptionName FROM TestrayFactor");

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

	private static final Log _log = LogFactory.getLog(
		TestrayWebhookRestController.class);

}