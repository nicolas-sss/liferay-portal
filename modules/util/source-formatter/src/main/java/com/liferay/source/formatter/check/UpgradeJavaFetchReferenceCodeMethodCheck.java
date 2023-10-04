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
 * @author Micaelle Silva
 */
public class UpgradeJavaFetchReferenceCodeMethodCheck extends BaseUpgradeCheck {

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

			String newJavaMethodContent = javaMethodContent;

			Matcher matcher = _fetchExternalReferenceCodePattern.matcher(
				javaMethodContent);

			while (matcher.find()) {
				String methodCall = JavaSourceUtil.getMethodCall(
					javaMethodContent, matcher.start());

				String variableName = getVariableName(methodCall);

				variableName = variableName.trim();

				if (!variableName.equals("CPInstanceLocalServiceUtil") &&
					!variableName.equals("CPInstanceServiceUtil") &&
					!hasClassOrVariableName(
						"CPInstanceLocalService", content, content,
						methodCall) &&
					!hasClassOrVariableName(
						"CPInstanceService", content, content, methodCall)) {

					continue;
				}

				String message = StringBundler.concat(
					"Unable to format method fetchByExternalReferenceCode and ",
					"fetchCPInstanceByExternalReferenceCode from ",
					"'CPInstanceLocalServiceUtil', 'CPInstanceLocalService', ",
					"'CPInstanceService' and 'CPInstanceServiceUtil'. Fill ",
					"the new parameter manually, see LPS-197724 and ",
					"LPS-197965.");

				List<String> parameterList = JavaSourceUtil.getParameterList(
					methodCall);

				if (!hasValidParameters(
						2, fileName, javaMethodContent, message, parameterList,
						new String[] {"long", "String"})) {

					continue;
				}

				StringBundler sb = new StringBundler(6);

				sb.append(StringPool.NEW_LINE);
				sb.append(JavaSourceUtil.getIndent(methodCall));
				sb.append(StringPool.TAB);
				sb.append(parameterList.get(1));
				sb.append(StringPool.COMMA_AND_SPACE);
				sb.append(parameterList.get(0));

				String newMethodCall = StringUtil.replace(
					methodCall, JavaSourceUtil.getParameters(methodCall),
					sb.toString());

				newJavaMethodContent = StringUtil.replace(
					newJavaMethodContent, methodCall, newMethodCall);
			}

			newContent = StringUtil.replace(
				newContent, javaMethodContent, newJavaMethodContent);
		}

		return newContent;
	}

	private static final Pattern _fetchExternalReferenceCodePattern =
		Pattern.compile(
			"(\\t*\\w*.fetchCPInstanceByExternalReferenceCode" +
				"\\(\\s*.\\s*.+\\))|(\\t*\\w*.fetchByExternalReferenceCode" +
					"\\(\\s*.\\s*.+\\))");

}