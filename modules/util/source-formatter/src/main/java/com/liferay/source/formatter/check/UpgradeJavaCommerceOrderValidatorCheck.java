/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
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
 * @author Micaelle Silva
 */
public class UpgradeJavaCommerceOrderValidatorCheck extends BaseUpgradeCheck {

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		List<String> implementedClassNames =
			javaClass.getImplementedClassNames();

		if (!implementedClassNames.contains("CommerceOrderValidator")) {
			return content;
		}

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			String javaMethodContent = javaMethod.getContent();

			Matcher validateMatcher = _validatePattern.matcher(
				javaMethodContent);

			if (!validateMatcher.find()) {
				continue;
			}

			String newJavaMethodContent = _replaceParameter(
				javaMethodContent, validateMatcher.group(1));

			content = StringUtil.replace(
				content, javaMethodContent, newJavaMethodContent);
		}

		return content;
	}

	@Override
	protected String[] getNewImports() {
		return new String[] {"java.math.BigDecimal"};
	}

	private String _addQuantityIntParameter(
		String javaMethodContent, String javaParameterName) {

		String indent = JavaSourceUtil.getIndent(javaMethodContent);

		StringBundler sb = new StringBundler(5);

		sb.append("{\n\n");
		sb.append(indent);
		sb.append("\tint ");
		sb.append(javaParameterName);
		sb.append("Int = quantity.intValue();");

		return sb.toString();
	}

	private String _replaceParameter(
		String javaMethodContent, String javaParameterName) {

		Pattern pattern = Pattern.compile(
			StringBundler.concat("\\b", javaParameterName, "\\b"));

		Matcher matcher = pattern.matcher(javaMethodContent);

		String newJavaMethodContent = javaMethodContent;

		while (matcher.find()) {
			newJavaMethodContent = StringUtil.replaceFirst(
				newJavaMethodContent, matcher.group(),
				javaParameterName + "Int", matcher.start());
		}

		String parameters = JavaSourceUtil.getParameters(newJavaMethodContent);

		List<String> parameterList = JavaSourceUtil.splitParameters(parameters);

		String newParameters = StringUtil.replace(
			parameters, parameterList.get(3), "BigDecimal quantity");

		newJavaMethodContent = StringUtil.replace(
			newJavaMethodContent, parameters, newParameters);

		return StringUtil.replaceFirst(
			newJavaMethodContent, CharPool.OPEN_CURLY_BRACE,
			_addQuantityIntParameter(newJavaMethodContent, javaParameterName));
	}

	private static final Pattern _validatePattern = Pattern.compile(
		"validate\\(\\s*Locale .+,\\s*CommerceOrder .+,\\s*CPInstance .+," +
			"\\s*int (.+)\\)");

}