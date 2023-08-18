/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeFTLPortletTopperCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith(".ftl")) {
			return content;
		}

		String newContent = content;

		Matcher portletTopperMatcher = _portletTopperPattern.matcher(content);

		while (portletTopperMatcher.find()) {
			newContent = StringUtil.replace(
				newContent, portletTopperMatcher.group(1),
				"\"cadmin portlet-topper\"");
		}

		Matcher liferayPortletMatcher = _liferayPortletPattern.matcher(content);

		while (liferayPortletMatcher.find()) {
			String groupMatcher = liferayPortletMatcher.group(1);

			if (!groupMatcher.equals("liferay_portlet")) {
				continue;
			}

			newContent = StringUtil.replace(
				newContent, liferayPortletMatcher.group(),
				"<@liferay_frontend[\"icon-options\"] direction=" +
					"\"right cadmin\" " + liferayPortletMatcher.group(2));
		}

		return newContent;
	}

	private static final Pattern _liferayPortletPattern = Pattern.compile(
		"<[^;](\\w+)[^;]\"icon-options\"[^;]\\s*(\\w+[^;]\\w+\\s*\\/>)");
	private static final Pattern _portletTopperPattern = Pattern.compile(
		"\\w+[^;](\"portlet-topper\")");

}