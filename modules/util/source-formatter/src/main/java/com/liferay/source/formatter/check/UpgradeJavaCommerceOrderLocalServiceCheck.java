/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Cavalcanti
 */
public class UpgradeJavaCommerceOrderLocalServiceCheck
	extends BaseUpgradeCheck {

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

			String newJavaMethodContent = javaMethodContent;

			Matcher matcher = _addCommerceOrderPattern.matcher(
				javaMethodContent);

			while (matcher.find()) {
				int position = matcher.start();

				String methodCall = JavaSourceUtil.getMethodCall(
					javaMethodContent, position);

				String newMethodCall = methodCall;

				if (_checkMethodCall(
						content, fileName, javaMethodContent, methodCall)) {

					List<String> parameterList =
						JavaSourceUtil.getParameterList(methodCall);

					newMethodCall = StringUtil.replace(
						newMethodCall, JavaSourceUtil.getParameters(methodCall),
						_reorderParameters(
							SourceUtil.getIndent(
								getLine(
									javaMethodContent,
									getLineNumber(
										javaMethodContent, position))),
							parameterList));
				}

				newJavaMethodContent = StringUtil.replace(
					newJavaMethodContent, methodCall, newMethodCall);
			}

			content = StringUtil.replace(
				content, javaMethodContent, newJavaMethodContent);
		}

		return content;
	}

	private boolean _checkMethodCall(
		String content, String fileName, String javaMethodContent,
		String methodCall) {

		String variableName = getVariableName(methodCall);

		if (!variableName.contains("CommerceOrderLocalServiceUtil") &&
			!hasClassOrVariableName(
				"CommerceOrderLocalService", javaMethodContent, content,
				methodCall)) {

			return false;
		}

		String message = StringBundler.concat(
			"Unable to format method addCommerceOrder from ",
			"CommerceOrderLocalService, CommerceOrderLocalServiceUtil. Fill ",
			"the new parameters manually, see LPS-LPS-196619");

		List<String> parameterList = JavaSourceUtil.getParameterList(
			methodCall);

		String[] parameterTypes = {
			"long", "long", "long", "long", "long", "long", "long", "long",
			"String", "int", "int", "int", "int", "int", "int", "int", "String",
			"BigDecimal", "String", "BigDecimal", "BigDecimal", "BigDecimal",
			"BigDecimal", "BigDecimal", "BigDecimal", "ServiceContext"
		};

		if (!hasValidParameters(
				26, fileName, javaMethodContent, message, parameterList,
				parameterTypes)) {

			return false;
		}

		return true;
	}

	private String _reorderParameters(
		String indent, List<String> oldParameters) {

		StringBundler sb = new StringBundler();

		sb.append(StringPool.NEW_LINE);
		sb.append(indent);
		sb.append(StringPool.TAB);
		sb.append(oldParameters.get(0));

		for (int i = 1; i < _NEW_ORDER_PARAMETERS.length; i++) {
			if ((i % 4) == 0) {
				sb.append(StringPool.COMMA);
				sb.append(StringPool.NEW_LINE);
				sb.append(indent);
				sb.append(StringPool.TAB);
				sb.append(oldParameters.get(i));

				continue;
			}

			sb.append(StringPool.COMMA_AND_SPACE);
			sb.append(oldParameters.get(i));
		}

		return sb.toString();
	}

	private static final int[] _NEW_ORDER_PARAMETERS = {
		0, 1, 3, 4, 2, 7, 8, 6, 18, 16, 20, 17, 23, 21, 19, 24, 15, 14, 25
	};

	private static final Pattern _addCommerceOrderPattern = Pattern.compile(
		"\\w+\\.addCommerceOrder\\(");

}