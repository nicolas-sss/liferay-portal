/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nícolas Moura
 */
public abstract class BaseUpgradeCheck extends BaseFileCheck {

	public boolean hasValidParameters(
		int expectedParametersSize, String fileName, String javaMethodContent,
		String message, List<String> parameterList, String[] parameterTypes) {

		if (parameterList.size() != expectedParametersSize) {
			return false;
		}

		if (!hasParameterTypes(
				javaMethodContent, javaMethodContent,
				ArrayUtil.toStringArray(parameterList), parameterTypes)) {

			addMessage(fileName, message);

			return false;
		}

		return true;
	}

	protected String addNewImports(String newContent) {
		String[] newImports = getNewImports();

		if (newImports != null) {
			newContent = JavaSourceUtil.addImports(newContent, newImports);
		}

		return newContent;
	}

	protected String addNewImportsJSP(String newContent, String... newImports) {
		if (newImports.length == 0) {
			return newContent;
		}

		Set<String> missingImports = new TreeSet<>();

		Collections.addAll(missingImports, newImports);

		Matcher directiveMatcher = _includesPattern.matcher(newContent);

		if (directiveMatcher.find()) {
			String includeDirective = directiveMatcher.group();

			String[] includeDirectives = StringUtil.splitLines(
				includeDirective);

			String newHeaderPackage = createImportsPackageJsp(
				missingImports, includeDirectives);

			newContent = StringUtil.replaceFirst(
				newContent,
				com.liferay.petra.string.StringUtil.merge(
					includeDirectives, "\n"),
				newHeaderPackage);
		}
		else {
			Matcher commentMatcher = _commentsPattern.matcher(newContent);

			if (commentMatcher.find()) {
				String comment = commentMatcher.group(1);

				if (commentMatcher.start() > 1) {
					return newContent;
				}

				String newMissingImports = createImportsPackageJsp(
					missingImports, comment);

				newContent = StringUtil.replaceFirst(
					newContent, comment, newMissingImports);
			}
			else {
				for (String missingImport : missingImports) {
					newContent = StringBundler.concat(
						"<%@ page import=\"", missingImport, "\" %>",
						StringPool.NEW_LINE, newContent);
				}
			}
		}

		return newContent;
	}

	protected String afterFormat(
		String fileName, String absolutePath, String content,
		String newContent) {

		return addNewImports(newContent);
	}

	protected String createImportsPackageJsp(
		Set<String> missingImports, String... oldHeaderPackage) {

		StringBundler sb = new StringBundler(4);

		for (String oldHeaderLine : oldHeaderPackage) {
			sb.append(oldHeaderLine);
			sb.append(StringPool.NEW_LINE);
		}

		for (String missingImport : missingImports) {
			sb.append("<%@ page import=\"");
			sb.append(missingImport);
			sb.append("\" %>");
		}

		return sb.toString();
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!isValidExtension(fileName)) {
			return content;
		}

		String newContent = format(fileName, absolutePath, content);

		if (!content.equals(newContent)) {
			newContent = afterFormat(
				fileName, absolutePath, content, newContent);
		}

		return newContent;
	}

	protected abstract String format(
			String fileName, String absolutePath, String content)
		throws Exception;

	protected String[] getNewImports() {
		return null;
	}

	protected String[] getValidExtensions() {
		return new String[] {"java"};
	}

	protected boolean isValidExtension(String fileName) {
		for (String extension : getValidExtensions()) {
			if (fileName.endsWith(CharPool.PERIOD + extension)) {
				return true;
			}
		}

		return false;
	}

	private static final Pattern _commentsPattern = Pattern.compile(
		"(<%--([0-9]*[\\*\\w\\s\\-\\:\\(\\)\\.\\,\\/]+)--%>)");
	private static final Pattern _includesPattern = Pattern.compile(
		"(<%@\\s*include\\s*(.+)%>\n)+", Pattern.MULTILINE);

}