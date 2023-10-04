/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Cavalcanti
 */
public class UpgradeJavaCommerceDestinationNamesCheck
	extends BaseUpgradeMatcherReplacementCheck {

	@Override
	protected String formatMatcherIteration(
		String content, String newContent, Matcher matcher) {

		String constant = matcher.group(1);

		if (_deprecatedConstants.contains(constant)) {
			String constantCall = matcher.group();

			return StringUtil.replace(
				newContent, constantCall,
				StringUtil.replace(
					constantCall, constant, "COMMERCE_" + constant));
		}

		return newContent;
	}

	@Override
	protected Pattern getPattern() {
		return Pattern.compile("DestinationNames.([A-Z_]+)");
	}

	private static final List<String> _deprecatedConstants = Arrays.asList(
		"BASE_PRICE_LIST", "ORDER_STATUS", "PAYMENT_STATUS", "SHIPMENT_STATUS",
		"STOCK_QUANTITY", "SUBSCRIPTION_STATUS");

}