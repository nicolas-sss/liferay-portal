/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
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
public class UpgradeJavaUpdateStatusMethodCheck extends BaseUpgradeCheck {

	public static String addMethodNewParameters(
		List<Integer> indexNewParameters, String methodCall,
		String newMethodCall, List<String> newParameters, String variableName) {

		List<String> parameterList = JavaSourceUtil.getParameterList(
			methodCall);

		for (int i = 0; i < indexNewParameters.size(); i++) {
			parameterList.add(indexNewParameters.get(i), newParameters.get(i));
		}

		StringBundler sb = new StringBundler(7);

		sb.append(variableName);
		sb.append(newMethodCall);
		sb.append(StringPool.NEW_LINE);
		sb.append(JavaSourceUtil.getIndent(methodCall));
		sb.append(StringPool.TAB);
		sb.append(StringUtil.merge(parameterList, StringPool.COMMA_AND_SPACE));
		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	public boolean validateParameters(
		String fileName, String javaMethodContent, List<String> parameterList,
		String[] parameterTypes) {

		if (!hasParameterTypes(
				javaMethodContent, javaMethodContent,
				ArrayUtil.toStringArray(parameterList), parameterTypes)) {

			addMessage(
				fileName,
				StringBundler.concat(
					"Could not resolve types of updateStatus method. The ",
					"method signature has changed to updateStatus(long userId,",
					"int status, ServiceContext serviceContext). Fill the new ",
					"parameter manually."));

			return false;
		}

		return true;
	}

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			String javaMethodContent = javaMethod.getContent();

			Matcher matcher = _updateStatusPattern.matcher(javaMethodContent);

			while (matcher.find()) {
				String methodCall = JavaSourceUtil.getMethodCall(
					javaMethodContent, matcher.start());

				String variable = matcher.group(2);

				if (!methodCall.contains("UserLocalServiceUtil") &&
					!methodCall.contains("UserServiceUtil") &&
					!hasClassOrVariableName(
						"UserLocalService", content, content, variable) &&
					!hasClassOrVariableName(
						"UserService", content, content, variable)) {

					continue;
				}

				List<String> parameterList = JavaSourceUtil.getParameterList(
					methodCall);

				if ((parameterList.size() != 2) ||
					!validateParameters(
						fileName, javaMethodContent, parameterList,
						new String[] {"long", "int"})) {

					continue;
				}

				StringBundler sb = new StringBundler(6);

				sb.append(JavaSourceUtil.getIndent(methodCall));
				sb.append("ServiceContext serviceContextThreadLocal = ");
				sb.append("ServiceContextThreadLocal.getServiceContext();");
				sb.append(StringPool.NEW_LINE);
				sb.append(StringPool.NEW_LINE);
				sb.append(matcher.group(1));

				String newMethod = addMethodNewParameters(
					Arrays.asList(parameterList.size()), methodCall, variable,
					Arrays.asList(
						"(ServiceContext)serviceContextThreadLocal.clone()"),
					sb.toString());

				content = StringUtil.replace(content, methodCall, newMethod);
			}
		}

		return content;
	}

	private static final Pattern _updateStatusPattern = Pattern.compile(
		"(|\\t*\\w+\\s*\\=|\\t*return?)(\\t*\\s?\\w+\\.updateStatus\\()");

}