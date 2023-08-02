/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeJavaLayoutServicesCheck extends BaseUpgradeCheck {

	public boolean validateParameters(
		String fileName, String javaMethodContent, List<String> parameterList,
		String[] parameterTypes) {

		if (!hasParameterTypes(
				javaMethodContent, javaMethodContent,
				ArrayUtil.toStringArray(parameterList), parameterTypes)) {

			addMessage(
				fileName,
				StringBundler.concat(
					"Could not conclude upgrade of one the addLayout() or ",
					"updateLayout() methods. The method signature has changed ",
					"with new parameters. Fill the new parameters manually."));

			return false;
		}

		return true;
	}

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		String newContent = content;

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			String javaMethodContent = javaMethod.getContent();

			Matcher matcher = _addOrUpdateLayoutPattern.matcher(
				javaMethodContent);

			while (matcher.find()) {
				String methodCall = JavaSourceUtil.getMethodCall(
					javaMethodContent, matcher.start());

				String variable = getVariableName(methodCall);

				if (!variable.contains("LayoutLocalServiceUtil") &&
					!variable.contains("LayoutServiceUtil") &&
					!hasClassOrVariableName(
						"LayoutLocalService", newContent, newContent,
						methodCall) &&
					!hasClassOrVariableName(
						"LayoutService", newContent, newContent, methodCall)) {

					continue;
				}

				List<String> parameterList = JavaSourceUtil.getParameterList(
					methodCall);

				if (methodCall.contains(".addLayout")) {
					String[] parameterTypes = {
						"long", "long", "boolean", "long", "long", "long",
						"Map<java.util.Locale, String>",
						"Map<java.util.Locale, String>",
						"Map<java.util.Locale, String>",
						"Map<java.util.Locale, String>",
						"Map<java.util.Locale, String>", "String", "String",
						"boolean", "boolean", "Map<java.util.Locale, String>",
						"ServiceContext"
					};

					newContent = _replaceAddOrUpdateLayout(
						newContent, 17, fileName, javaMethodContent,
						Arrays.asList(parameterList.size() - 1), methodCall,
						".addLayout(", Arrays.asList("0"), parameterList,
						parameterTypes);
				}
				else if (methodCall.contains(".updateLayout")) {
					String[] parameterTypes = {
						"long", "boolean", "long", "long",
						"Map<java.util.Locale, String>",
						"Map<java.util.Locale, String>",
						"Map<java.util.Locale, String>",
						"Map<java.util.Locale, String>",
						"Map<java.util.Locale, String>", "String", "boolean",
						"Map<java.util.Locale, String>", "boolean", "byte[]",
						"ServiceContext"
					};

					int index = parameterList.size() - 1;

					newContent = _replaceAddOrUpdateLayout(
						newContent, 15, fileName, javaMethodContent,
						Arrays.asList(index, index, index), methodCall,
						".updateLayout(", Arrays.asList("0", "0", "0"),
						parameterList, parameterTypes);
				}
			}
		}

		return newContent;
	}

	private String _replaceAddOrUpdateLayout(
		String content, int expectedParameters, String fileName,
		String javaMethodContent, List<Integer> index, String methodCall,
		String newMethodCall, List<String> newParameters,
		List<String> parameterList, String[] parameterTypes) {

		if ((parameterList.size() != expectedParameters) ||
			!validateParameters(
				fileName, javaMethodContent, parameterList, parameterTypes)) {

			return content;
		}

		String newMethod = JavaSourceUtil.addMethodNewParameters(
			index, methodCall, newMethodCall, newParameters,
			getVariableName(methodCall));

		return StringUtil.replace(content, methodCall, newMethod);
	}

	private static final Pattern _addOrUpdateLayoutPattern = Pattern.compile(
		"\\t*\\w+\\.(?:updateLayout|addLayout)\\(");

}