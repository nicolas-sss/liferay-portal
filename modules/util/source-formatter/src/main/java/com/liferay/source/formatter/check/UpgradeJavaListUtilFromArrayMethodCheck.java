/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeJavaListUtilFromArrayMethodCheck extends BaseUpgradeCheck {

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		String newContent = content;

		Matcher matcher = _fromArrayPattern.matcher(content);

		while (matcher.find()) {
			String methodCall = JavaSourceUtil.getMethodCall(
				content, matcher.start());

			if (methodCall.contains("return")) {
				addMessage(
					fileName,
					StringBundler.concat(
						"Could not change the method call ",
						"'ListUtil.fromArray'. Replace with class instance, ",
						"construct a 'for' to iterate and add to your list."));

				continue;
			}

			newContent = _replaceFromArray(
				newContent, methodCall, matcher.group(3));
		}

		return newContent;
	}

	private String _clearFile(
		String content, String methodStart, String variableNameList) {

		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				"(\\t*", methodStart, "\\s*", variableNameList,
				"\\s*[^)]+\\s*;)"));

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String duplicateList = matcher.group();

			content = StringUtil.removeSubstring(content, duplicateList);
		}

		return content;
	}

	private String _replaceFromArray(
		String content, String methodCall, String variableNameList) {

		String methodStart = getVariableTypeName(
			content, content, variableNameList, true);

		String nameClass = methodStart.substring(
			methodStart.indexOf("<") + 1, methodStart.indexOf(">"));

		List<String> parameterList = JavaSourceUtil.getParameterList(
			methodCall);

		String variableName = "list" + nameClass;

		StringBuilder sb = new StringBuilder();

		sb.append(JavaSourceUtil.getIndent(methodCall));
		sb.append(methodStart);
		sb.append(StringPool.SPACE);
		sb.append(variableNameList);
		sb.append(" = new ArrayList<>();");
		sb.append("\n\n\t\t");
		sb.append("for (");
		sb.append(nameClass);
		sb.append(StringPool.SPACE);
		sb.append(variableName);
		sb.append(" : ");
		sb.append(StringUtil.merge(parameterList));
		sb.append("){");
		sb.append("\n\t\t\t");
		sb.append(variableNameList);
		sb.append(".add(");
		sb.append(variableName);
		sb.append(");}");

		content = StringUtil.replace(
			content, methodCall + StringPool.SEMICOLON, sb.toString());

		return _clearFile(content, methodStart, variableNameList);
	}

	private static final Pattern _fromArrayPattern = Pattern.compile(
		"(|\\t*\\s?\\w+\\<\\w+\\>\\s*)(|\\t*(\\w+)\\s*\\=|\\t*return?)\\t*\\s?" +
			"ListUtil\\.fromArray\\s*(\\(\\s*.+\\s*\\)\\;)");

}