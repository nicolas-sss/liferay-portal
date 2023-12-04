/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeJavaScreenContributorClassCheck extends BaseUpgradeCheck {

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		String oldContent = javaClass.getContent();

		String newContent = oldContent;

		Matcher screenContributorMatcher = _screenContributorPattern.matcher(
			oldContent);

		if (screenContributorMatcher.find()) {
			String javaContent = null;

			List<String> referenceAnnotationMethods = new ArrayList<>();

			List<String> overrideAnnotationMethods = new ArrayList<>();

			for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
				if (childJavaTerm.isJavaVariable()) {
					JavaVariable javaVariable = (JavaVariable)childJavaTerm;

					javaContent = javaVariable.getContent();

					referenceAnnotationMethods.add(javaContent);
				}
				else if (childJavaTerm.isJavaMethod()) {
					JavaMethod javaMethod = (JavaMethod)childJavaTerm;

					javaContent = javaMethod.getContent();

					overrideAnnotationMethods.add(javaContent);
				}
			}

			newContent = _getNewContent(
				referenceAnnotationMethods, oldContent,
				overrideAnnotationMethods, screenContributorMatcher);
		}

		return StringUtil.replace(content, oldContent, newContent);
	}

	protected String[] getNewImports() {
		return new String[] {
			"com.liferay.portal.settings.configuration.admin.display." +
				"PortalSettingsConfigurationScreenFactory"
		};
	}

	private String _getNewContent(
		List<String> annotationReferenceMethods, String oldContent,
		List<String> overrideAnnotationMethods,
		Matcher screenContributorMatcher) {

		String clazzName = screenContributorMatcher.group(2);

		if (clazzName.contains("ScreenContributor")) {
			clazzName = StringUtil.removeSubstring(
				clazzName, "ScreenContributor");
		}
		else {
			clazzName = clazzName + "InnerClass";
		}

		String newMethods = StringBundler.concat(
			"@Override\n\tprotected ConfigurationScreen ",
			"getConfigurationScreen() {\n\t\treturn _portalSettings",
			"ConfigurationScreenFactory.create(\n\t\t\tnew ", clazzName,
			"());\n\t}\n\n\t@Reference\n\tprivate PortalSettings",
			"ConfigurationScreenFactory _portalSettingsConfiguration",
			"ScreenFactory;\n\n\t");

		String innerClassName = StringBundler.concat(
			"private class ", clazzName,
			"\n\t\timplements PortalSettingsConfigurationScreenContributor {");

		StringBundler sb = new StringBundler(13);

		sb.append("@Component(service = ConfigurationScreen.class)\n");
		sb.append(screenContributorMatcher.group(1));
		sb.append("extends ConfigurationScreenWrapper {\n\n\t");
		sb.append(newMethods);
		sb.append(StringUtil.merge(annotationReferenceMethods, "\n\n\t"));
		sb.append("\n\n\t");
		sb.append(innerClassName);
		sb.append("\n\n\t");
		sb.append(StringUtil.merge(overrideAnnotationMethods, "\n\n\t"));
		sb.append(StringPool.NEW_LINE);
		sb.append(CharPool.CLOSE_CURLY_BRACE);
		sb.append(StringPool.NEW_LINE);
		sb.append(CharPool.CLOSE_CURLY_BRACE);

		return StringUtil.replace(oldContent, oldContent, sb.toString());
	}

	private static final Pattern _screenContributorPattern = Pattern.compile(
		"(\\t*public\\s*class\\s*(\\w*)\\s*)implements\\s*" +
			"PortalSettingsConfigurationScreenContributor");

}