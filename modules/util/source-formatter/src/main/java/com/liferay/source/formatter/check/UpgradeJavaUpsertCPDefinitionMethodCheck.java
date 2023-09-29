/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeJavaUpsertCPDefinitionMethodCheck extends BaseUpgradeCheck {

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

			Matcher upsertCPDefinitionMatcher =
				_upsertCPDefinitionPattern.matcher(javaMethodContent);

			while (upsertCPDefinitionMatcher.find()) {
				String methodCall = JavaSourceUtil.getMethodCall(
					javaMethodContent, upsertCPDefinitionMatcher.start());

				String variableName = upsertCPDefinitionMatcher.group(2);

				if (!variableName.contains("CPDefinitionLocalServiceUtil") &&
					!variableName.contains("CPDefinitionServiceUtil") &&
					!hasClassOrVariableName(
						"CPDefinitionLocalService", content, content,
						variableName) &&
					!hasClassOrVariableName(
						"CPDefinitionService", content, content,
						variableName)) {

					continue;
				}

				String message = StringBundler.concat(
					"Unable to format method upsertCPDefinition from ",
					"CPDefinitionLocalService, CPDefinitionLocalServiceUtil, ",
					"CPDefinitionService and CPDefinitionServiceUtil. Fill ",
					"the new parameters manually and change the name method ",
					"by 'addOrUpdateCPDefinition', see LPS-197613.");

				List<String> parameterList = JavaSourceUtil.getParameterList(
					methodCall);

				String[] parameterTypes = {
					"long", "long", "Map<Locale, String>",
					"Map<Locale, String>", "Map<Locale, String>",
					"Map<Locale, String>", "Map<Locale, String>",
					"Map<Locale, String>", "Map<Locale, String>", "String",
					"boolean", "boolean", "boolean", "boolean", "double",
					"double", "double", "double", "double", "long", "boolean",
					"boolean", "String", "boolean", "int", "int", "int", "int",
					"int", "int", "int", "int", "int", "int", "boolean",
					"String", "boolean", "int", "String", "UnicodeProperties",
					"long", "String", "ServiceContext"
				};

				if (!hasValidParameters(
						parameterTypes.length, fileName, javaMethodContent,
						message, parameterList, parameterTypes)) {

					continue;
				}

				String externalReferenceCode = parameterList.get(41);

				parameterList.remove(41);

				String newMethodStart = StringBundler.concat(
					upsertCPDefinitionMatcher.group(1),
					upsertCPDefinitionMatcher.group(3),
					".addOrUpdateCPDefinition(");

				newContent = StringUtil.replace(
					newContent, methodCall,
					JavaSourceUtil.addMethodNewParameters(
						JavaSourceUtil.getIndent(methodCall) + StringPool.TAB,
						new int[] {0, 42}, newMethodStart,
						new String[] {externalReferenceCode, "0"},
						parameterList));
			}
		}

		return newContent;
	}

	private static final Pattern _upsertCPDefinitionPattern = Pattern.compile(
		"(|\\t*\\w+\\s*\\w+\\s*\\=\\s*|\\t*return?\\s*)\\t*\\s?" +
			"((\\w+)\\.upsertCPDefinition\\()");

}