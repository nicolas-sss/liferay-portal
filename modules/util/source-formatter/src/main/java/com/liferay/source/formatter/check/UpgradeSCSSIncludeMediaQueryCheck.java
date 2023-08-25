/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeSCSSIncludeMediaQueryCheck extends BaseUpgradeCheck {

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		Matcher matcher = _includeMediaQueryPattern.matcher(content);

		if (matcher.find()) {
			String message = StringBundler.concat(
				"Do not use 'media-query' mixing, replace with its equivalent ",
				"(e.g., media-breakpoint-up, media-breakpoint-only, ",
				"media-breakpoint-down, etc.), see LPS-194507.");

			addMessage(fileName, message);
		}

		return content;
	}

	@Override
	protected String[] getValidExtensions() {
		return new String[] {"scss"};
	}

	private static final Pattern _includeMediaQueryPattern = Pattern.compile(
		"@include media-query\\(\\s*\\w+\\,\\s*\\$?\\w+.*\\)");

}