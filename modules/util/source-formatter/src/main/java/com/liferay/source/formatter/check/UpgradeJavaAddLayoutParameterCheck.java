/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeJavaAddLayoutParameterCheck
	extends BaseUpgradeMatcherReplacementCheck {

	@Override
	protected String formatMatcherIteration(
		String content, String newContent, Matcher matcher) {

		String methodCall = JavaSourceUtil.getMethodCall(
			content, matcher.start());

		String variableName = getVariableName(methodCall);

		if (!hasClassOrVariableName(
				"LayoutLocalService", newContent, newContent, methodCall) &&
			!variableName.contains("LayoutLocalServiceUtil")) {

			return newContent;
		}

		List<String> parameterList = JavaSourceUtil.getParameterList(
			methodCall);

		if (parameterList.size() != 17) {
			return newContent;
		}

		String[] parameterTypes = {
			"long", "long", "boolean", "long", "long", "long",
			"Map<java.util.Locale, String>", "Map<java.util.Locale, String>",
			"Map<java.util.Locale, String>", "Map<java.util.Locale, String>",
			"Map<java.util.Locale, String>", "String", "String", "boolean",
			"boolean", "Map<java.util.Locale, String>", "ServiceContext"
		};

		if (!hasParameterTypes(
				content, content, ArrayUtil.toStringArray(parameterList),
				parameterTypes)) {

			addMessage(
				newContent,
				StringBundler.concat(
					"Could not resolve types of addLayout method. The method ",
					"signature has changed to addLayout(long userId, long ",
					"groupId, boolean privateLayout, long parentLayoutId, long",
					"classNameId, long classPK, Map<java.util.Locale, String> ",
					"nameMap, Map<java.util.Locale, String> titleMap, ",
					"Map<java.util.Locale, String> descriptionMap, ",
					"Map<java.util.Locale, String> keywordsMap, ",
					"Map<java.util.Locale, String> robotsMap, String type, ",
					"String typeSettings, boolean hidden, boolean system, ",
					"Map<java.util.Locale, String> friendlyURLMap, long ",
					"masterLayoutPlid, ServiceContext serviceContext). Fill ",
					"the new parameters manually."));
		}

		String newMethod = JavaSourceUtil.addMethodNewParameters(
			parameterList.size() - 1, methodCall, ".addLayout(",
			Arrays.asList("0"), variableName);

		return StringUtil.replace(newContent, methodCall, newMethod);
	}

	@Override
	protected Pattern getPattern() {
		return Pattern.compile("\\t*?\\w+\\.addLayout\\(");
	}

}