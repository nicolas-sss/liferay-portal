/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeJavaStorageTypeAwareCheck
	extends BaseUpgradeMatcherReplacementCheck {

	@Override
	protected String afterFormat(
		String fileName, String absolutePath, String content,
		String newContent) {

		Matcher importStorageTypeAwareMatcher =
			_importStorageTypeAwarePattern.matcher(newContent);

		if (importStorageTypeAwareMatcher.find()) {
			return StringUtil.removeSubstring(
				newContent, importStorageTypeAwareMatcher.group() + "\n\n");
		}

		return newContent;
	}

	@Override
	protected String formatMatcherIteration(
		String content, String newContent, Matcher matcher) {

		String interfaceDepreciated = matcher.group(1);

		if (interfaceDepreciated == null) {
			interfaceDepreciated = matcher.group(2);
		}

		return StringUtil.removeSubstring(newContent, interfaceDepreciated);
	}

	@Override
	protected Pattern getPattern() {
		return Pattern.compile(
			"implements\\s*\\w*(\\,\\s*StorageTypeAware)|(\\t*" +
				"@Override\\n+)\\s*\\w*\\s*StorageType");
	}

	private static final Pattern _importStorageTypeAwarePattern =
		Pattern.compile("(import\\s*[\\.\\w*]+\\.StorageTypeAware;)");

}