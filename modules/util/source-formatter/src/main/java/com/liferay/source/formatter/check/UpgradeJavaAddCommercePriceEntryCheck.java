/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
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
 * @author Albert Gomes Cabral
 */
public class UpgradeJavaAddCommercePriceEntryCheck extends BaseUpgradeCheck {

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

			Matcher matcher = _addCommercePriceEntryPattern.matcher(
				javaMethodContent);

			while (matcher.find()) {
				String methodCall = JavaSourceUtil.getMethodCall(
					javaMethodContent, matcher.start());

				String variableName = getVariableName(methodCall);

				if (!variableName.contains(
						"CommercePriceEntryLocalServiceUtil") &&
					!hasClassOrVariableName(
						"CommercePriceEntryLocalService", content,
						javaMethodContent, methodCall)) {

					continue;
				}

				List<String> parameterList = JavaSourceUtil.getParameterList(
					methodCall);

				if (parameterList.size() != 7) {
					continue;
				}

				String message = StringBundler.concat(
					"Unable to format method addCommercePriceEntry from ",
					"CommercePriceEntryLocalService, ",
					"CommercePriceEntryLocalServiceUtil Fill the new ",
					"parameter manually, see LPS-194001.");

				String[] parameterTypes = {
					"String", "long", "String", "long", "BigDecimal",
					"BigDecimal", "ServiceContext"
				};

				if (!hasValidParameters(
						7, fileName, javaMethodContent, message, parameterList,
						parameterTypes)) {

					continue;
				}

				String newMethod = JavaSourceUtil.addMethodNewParameters(
					JavaSourceUtil.getIndent(methodCall),
					new int[] {parameterList.size() - 2, parameterList.size()},
					matcher.group(), new String[] {"false", "null"},
					parameterList);

				newContent = StringUtil.replace(content, methodCall, newMethod);
			}
		}

		return newContent;
	}

	private static final Pattern _addCommercePriceEntryPattern =
		Pattern.compile("\\t*\\w+\\.?addCommercePriceEntry\\(");

}