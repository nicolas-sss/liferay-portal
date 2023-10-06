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
public class UpgradeJavaCPInstanceServicesCheck extends BaseUpgradeCheck {

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

			Matcher matcher = _fetchByExternalReferenceCodePattern.matcher(
				javaMethodContent);

			while (matcher.find()) {
				String methodCall = JavaSourceUtil.getMethodCall(
					javaMethodContent, matcher.start());

				String variableName = getVariableName(methodCall);

				List<String> parameterList = JavaSourceUtil.getParameterList(
					methodCall);

				if (!_validMethodCall(
						fileName, content, variableName, javaMethodContent,
						methodCall, parameterList)) {

					continue;
				}

				StringBundler sb = new StringBundler(9);

				sb.append(variableName);
				sb.append(matcher.group(1));
				sb.append(StringPool.NEW_LINE);
				sb.append(JavaSourceUtil.getIndent(methodCall));
				sb.append(StringPool.TAB);
				sb.append(parameterList.get(1));
				sb.append(StringPool.COMMA_AND_SPACE);
				sb.append(parameterList.get(0));
				sb.append(StringPool.CLOSE_PARENTHESIS);

				String newMethodCall = sb.toString();

				newJavaMethodContent = StringUtil.replace(
					newJavaMethodContent, methodCall, newMethodCall);
			}

			newContent = StringUtil.replace(
				newContent, javaMethodContent, newJavaMethodContent);
		}

		return newContent;
	}

	private boolean _validMethodCall(
		String fileName, String content, String variableName,
		String javaMethodContent, String methodCall,
		List<String> parameterList) {

		variableName = variableName.trim();

		if (!variableName.equals("CPInstanceLocalServiceUtil") &&
			!variableName.equals("CPInstanceServiceUtil") &&
			!hasClassOrVariableName(
				"CPInstanceLocalService", javaMethodContent, content,
				methodCall) &&
			!hasClassOrVariableName(
				"CPInstanceService", javaMethodContent, content, methodCall)) {

			return false;
		}

		String message = StringBundler.concat(
			"Unable to format method fetchByExternalReferenceCode from ",
			"CPInstanceLocalService, CPInstanceLocalServiceUtil, ",
			"CPInstanceService, CPInstanceServiceUtil and method ",
			"fetchCPInstanceByExternalReferenceCode from ",
			"CPInstanceLocalService, CPInstanceLocalServiceUtil. Fill the new ",
			"parameter manually, see LPS-197724 and LPS-197965.");

		if (!hasValidParameters(
				2, fileName, javaMethodContent, message, parameterList,
				new String[] {"long", "String"})) {

			return false;
		}

		if (methodCall.contains(".fetchCPInstanceByExternalReferenceCode") &&
			!variableName.equals("CPInstanceLocalServiceUtil") &&
			!hasClassOrVariableName(
				"CPInstanceLocalService", javaMethodContent, content,
				methodCall)) {

			return false;
		}

		return true;
	}

	private static final Pattern _fetchByExternalReferenceCodePattern =
		Pattern.compile(
			"\\t*\\w+(\\.fetch(?:|CPInstance)ByExternalReferenceCode\\()");

}