/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
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
public class UpgradeJavaDLFolderMethodCheck extends BaseUpgradeCheck {

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

			Matcher matcher = _addFolderPattern.matcher(javaMethodContent);

			while (matcher.find()) {
				String methodCall = JavaSourceUtil.getMethodCall(
					javaMethodContent, matcher.start());

				String message = StringBundler.concat(
					"Unable to format method addFolder from DLFolderService, ",
					"DLFolderLocalService, DLFolderServiceUtil and ",
					"DLFolderLocalServiceUtil. Fill the new parameter ",
					"manually, see LPS-194001.");

				String variableName = getVariableName(methodCall);

				String newMethodCall = null;

				List<String> parameterList = JavaSourceUtil.getParameterList(
					methodCall);

				if (variableName.contains("DLFolderLocalServiceUtil") ||
					hasClassOrVariableName(
						"DLFolderLocalService", newContent, newContent,
						methodCall)) {

					String[] parameterTypes = {
						"long", "long", "long", "boolean", "long", "String",
						"String", "boolean", "ServiceContext"
					};

					if (!hasValidParameters(
							9, fileName, javaMethodContent, message,
							parameterList, parameterTypes)) {

						continue;
					}

					newMethodCall = JavaSourceUtil.addMethodNewParameters(
						JavaSourceUtil.getIndent(methodCall), new int[] {0},
						matcher.group(1), new String[] {"null"}, parameterList);

					newContent = StringUtil.replace(
						newContent, methodCall, newMethodCall);
				}
				else if (variableName.contains("DLFolderServiceUtil") ||
						 hasClassOrVariableName(
							 "DLFolderService", newContent, newContent,
							 methodCall)) {

					String[] parameterTypes = {
						"long", "long", "boolean", "long", "String", "String",
						"ServiceContext"
					};

					if (!hasValidParameters(
							7, fileName, javaMethodContent, message,
							parameterList, parameterTypes)) {

						continue;
					}

					newMethodCall = JavaSourceUtil.addMethodNewParameters(
						JavaSourceUtil.getIndent(methodCall), new int[] {0},
						matcher.group(1), new String[] {"null"}, parameterList);

					newContent = StringUtil.replace(
						newContent, methodCall, newMethodCall);
				}
			}
		}

		return newContent;
	}

	private static final Pattern _addFolderPattern = Pattern.compile(
		"(\\t*(|_dlFolderLocalService|DLFolderLocalServiceUtil|" +
			"_dlFolderService|DLFolderServiceUtil)(\\.addFolder\\())");

}