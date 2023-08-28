/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeSASSCheck extends BaseUpgradeMatcherReplacementCheck {

	@Override
	protected String formatMatcherIteration(
		String content, String newContent, Matcher matcher) {

		String methodCall = matcher.group();

		StringBuilder sb = new StringBuilder();

		sb.append("math.div(");
		sb.append(matcher.group(1));
		sb.append(StringPool.COMMA_AND_SPACE);
		sb.append(matcher.group(2));
		sb.append(StringPool.CLOSE_PARENTHESIS);

		return StringUtil.replace(newContent, methodCall, sb.toString());
	}

	@Override
	protected Pattern getPattern() {
		return Pattern.compile("(\\$\\w+|[0-9.]+)\\s*\\/\\s*(\\$\\w+|[0-9.]+)");
	}

	@Override
	protected String[] getValidExtensions() {
		return new String[] {"sass"};
	}

}