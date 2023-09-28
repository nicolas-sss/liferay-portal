/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringUtil;
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
public class UpgradeJavaCommerceOrderValidatorCheck extends BaseUpgradeCheck {

	@Override
	protected String format(
		String fileName, String absolutePath, String content) throws Exception {

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		List<String> implementedClassNames =
			javaClass.getImplementedClassNames();

		if (!implementedClassNames.contains("CommerceOrderValidator")){
			return content;
		}

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			String javaMethodContent = javaMethod.getContent();

			String newJavaMethodContent = javaMethodContent;

			Matcher matcher = _addBigDecimalParameterPattern.matcher(
				javaMethodContent);

			if (!matcher.find()) {
				continue;
			}

			String methodCall = JavaSourceUtil.getMethodCall(
				javaMethodContent, matcher.start());

			String parameters = JavaSourceUtil.getParameters(methodCall);

			String newParameters = StringUtil.replace(parameters, "int", "BigDecimal");

			String newMethodCall = StringUtil.replace(methodCall, parameters, newParameters);

			newJavaMethodContent = StringUtil.replace(javaMethodContent, methodCall, newMethodCall);

			content = StringUtil.replace(
				content, javaMethodContent, newJavaMethodContent);

		}

		return content;

	}

	private static final Pattern _addBigDecimalParameterPattern =
		Pattern.compile(
			"validate\\(\\s*.+,\\s*.+,\\s*.+,\\s*int .+\\)");

}
