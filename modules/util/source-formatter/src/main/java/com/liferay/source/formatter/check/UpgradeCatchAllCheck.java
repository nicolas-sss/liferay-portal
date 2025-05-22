/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.exception.UpgradeCatchAllException;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nícolas Moura
 */
public class UpgradeCatchAllCheck extends BaseFileCheck {

	public static String[] getExpectedMessages() throws Exception {
		List<String> expectedMessages = new ArrayList<>();

		JSONArray jsonArray = _getReplacementsJSONArray("replacements.json");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String[] validExtensions = JSONUtil.toStringArray(
				jsonObject.getJSONArray("validExtensions"));

			if ((validExtensions.length > 0) &&
				!ArrayUtil.contains(validExtensions, "java")) {

				continue;
			}

			if ((_issueKey != null) &&
				!Objects.equals(_issueKey, jsonObject.getString("issueKey"))) {

				continue;
			}

			String from = jsonObject.getString("from");

			Set<String> keys = jsonObject.keySet();

			boolean skipValidation = false;

			if (jsonObject.getBoolean("skipParametersValidation") ||
				from.startsWith("regex:")) {

				skipValidation = true;
			}

			if ((from.contains(StringPool.OPEN_PARENTHESIS) &&
				 !skipValidation) ||
				!keys.contains("to")) {

				expectedMessages.add(_getMessage(jsonObject));
			}
		}

		return ArrayUtil.toStringArray(expectedMessages);
	}

	public static void setIssueKey(String issueKey) {
		_issueKey = issueKey;
	}

	public static void setTestMode(boolean testMode) {
		_testMode = testMode;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (_testMode && fileName.endsWith(".java")) {
			UpgradeCatchAllJavaTermOrderCheck termOrderCheck =
				new UpgradeCatchAllJavaTermOrderCheck();

			JavaClass javaClass = JavaClassParser.parseJavaClass(
				fileName, content);

			if (!StringUtil.equals(
					javaClass.getContent(),
					termOrderCheck.doProcess(
						fileName, absolutePath, javaClass, content))) {

				throw new UpgradeCatchAllException(
					fileName + " missing javaTerms sorting");
			}
		}

		JSONArray jsonArray = _getReplacementsJSONArray("replacements.json");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (((_issueKey != null) &&
				 !Objects.equals(
					 _issueKey, jsonObject.getString("issueKey"))) ||
				!_hasValidExtension(fileName, jsonObject)) {

				continue;
			}

			String oldContent = content;

			_newMessage = false;

			if (fileName.endsWith(".java")) {
				content = _formatJava(content, fileName, jsonObject);
			}
			else {
				content = _formatGeneral(content, fileName, jsonObject);
			}

			if (_testMode && oldContent.equals(content)) {
				String to = jsonObject.getString("to");

				if (to.isEmpty() && _newMessage) {
					continue;
				}

				throw new UpgradeCatchAllException(
					"Unable to process pattern " +
						jsonObject.getString("from") +
							" or there is no test associated with it");
			}
		}

		_testMode = false;

		return content;
	}

	private static List<String> _getInterpolatedNewParameterNames(
		List<String> parameterNames, List<String> newParameterNames,
		String prefix) {

		List<String> interpolatedNewParameterNames = new ArrayList<>();

		for (String newParameterName : newParameterNames) {
			if (newParameterName.contains(prefix)) {
				List<Integer> indexes = new ArrayList<>();

				Matcher matcher = _parameterNamePattern.matcher(
					newParameterName);

				while (matcher.find()) {
					int index = GetterUtil.getInteger(matcher.group(1));

					indexes.add(index);
				}

				for (int index : indexes) {
					newParameterName = StringUtil.replace(
						newParameterName, prefix + index + CharPool.POUND,
						parameterNames.get(index));
				}
			}

			interpolatedNewParameterNames.add(newParameterName);
		}

		return interpolatedNewParameterNames;
	}

	private static String _getMessage(JSONObject jsonObject) {
		StringBundler sb = new StringBundler(6);

		sb.append("See ");
		sb.append(jsonObject.getString("issueKey"));
		sb.append(StringPool.COMMA_AND_SPACE);

		String[] classNames = JSONUtil.toStringArray(
			jsonObject.getJSONArray("classNames"));

		String classNamesFormated = null;

		if (classNames.length > 0) {
			classNamesFormated = StringUtil.merge(classNames, StringPool.SLASH);
		}

		String from = jsonObject.getString("from");

		int index = from.indexOf(CharPool.PERIOD);

		if (index != -1) {
			from = StringUtil.replace(from, CharPool.PERIOD, CharPool.POUND);
		}
		else if (classNamesFormated != null) {
			sb.append(classNamesFormated);
			sb.append(StringPool.POUND);
		}

		sb.append(from);

		return sb.toString();
	}

	private static Pattern _getMethodSignaturePattern(JSONObject jsonObject) {
		String from = jsonObject.getString("from");

		String regex = StringUtil.replace(from, " ", "\\s+");

		regex = StringUtil.replace(regex, CharPool.OPEN_PARENTHESIS, "\\(");

		regex = StringUtil.replace(regex, CharPool.CLOSE_PARENTHESIS, "\\)");

		regex = StringUtil.replace(regex, CharPool.PERIOD, "\\.\\s*");

		regex = StringUtil.replace(regex, CharPool.COMMA, "\\,\\s*");


		regex =  StringBundler.concat("(?:@[A-Za-z]+\\s+)?(?:public|private|protected|static|final|abstract|\\s+)*?\\s*",
			regex, "(?:\\s+throws\\s+[A-Za-z0-9._<>\\s,]+)?");

		return Pattern.compile(regex);
	}

	private static Pattern _getPattern(JSONObject jsonObject) {
		String from = jsonObject.getString("from");

		String regex = StringBundler.concat("\\b", from, "\\b");

		if (from.startsWith("regex:")) {
			return Pattern.compile(from.replaceFirst("regex:", ""));
		}
		else if (regex.contains(StringPool.SLASH)) {
			return Pattern.compile(
				StringUtil.replace(regex, CharPool.SLASH, "\\/"));
		}

		if (regex.contains(StringPool.OPEN_PARENTHESIS)) {
			regex = StringUtil.replace(
				regex, CharPool.OPEN_PARENTHESIS, "\\b\\(");

			if (jsonObject.getBoolean("skipParametersValidation") &&
				!from.matches(_CONSTRUCTOR_REGEX)) {

				regex = StringUtil.replace(
					regex, CharPool.CLOSE_PARENTHESIS, "\\)");

				regex = StringUtil.removeSubstring(regex, "\\b");
			}
			else {
				regex = regex.substring(
					0, regex.indexOf(CharPool.OPEN_PARENTHESIS) + 1);
			}
		}
		else if (regex.endsWith(">\\b")) {
			regex = StringUtil.removeLast(regex, "\\b");

			regex = StringUtil.replaceFirst(
				regex, CharPool.LESS_THAN, "\\s*<[?\\s\\w]*");

			regex = StringUtil.replace(
				regex, StringPool.COMMA_AND_SPACE, ",[?\\s\\w]*");
		}
		else {
			regex = regex + "[,;> (){]";
		}

		if (regex.contains(StringPool.PERIOD)) {
			return Pattern.compile(
				StringUtil.replace(regex, CharPool.PERIOD, "\\.\\s*"));
		}

		if (Character.isUpperCase(from.charAt(0)) ||
			StringUtil.startsWith(from, "new")) {

			String[] classNames = JSONUtil.toStringArray(
				jsonObject.getJSONArray("classNames"));

			if (classNames.length == 0) {
				return Pattern.compile(regex);
			}
		}

		return Pattern.compile("\\w+\\.[\\w\\(\\)\\s\\.]*" + regex);
	}

	private static JSONArray _getReplacementsJSONArray(String fileName)
		throws Exception {

		ClassLoader classLoader = UpgradeCatchAllCheck.class.getClassLoader();

		return new JSONArrayImpl(
			StringUtil.read(
				classLoader.getResourceAsStream("dependencies/" + fileName)));
	}

	private String _addNewReference(String content, String newReference) {
		if (!newReference.equals(StringPool.BLANK)) {
			content = JavaSourceUtil.addImports(
				content, "org.osgi.service.component.annotations.Reference");

			return StringUtil.replaceLast(
				content, CharPool.CLOSE_CURLY_BRACE,
				"\t@Reference\n\tprivate " + newReference + ";\n\n}");
		}

		return content;
	}

	private String _addOrReplaceMethodParameters(
		List<String> methodParameterNames, String newMethodCall,
		List<String> newMethodParameterNames) {

		return _addOrReplaceParameters(
			StringPool.CLOSE_PARENTHESIS, newMethodCall,
			newMethodParameterNames, methodParameterNames, "param#");
	}

	private String _addOrReplaceParameters(
		String lastCharacter, String newMethodCall,
		List<String> newParameterNames, List<String> parameterNames,
		String prefix) {

		StringBundler sb = new StringBundler(2 + newParameterNames.size());

		sb.append(newMethodCall);

		sb.append(
			StringUtil.merge(
				_getInterpolatedNewParameterNames(
					parameterNames, newParameterNames, prefix),
				StringPool.COMMA_AND_SPACE));

		sb.append(lastCharacter);

		return sb.toString();
	}

	private String _addOrReplaceTypeParameters(
		String newMethodCall, List<String> newTypeParameterNames,
		List<String> typeParameterNames) {

		return _addOrReplaceParameters(
			StringPool.GREATER_THAN, newMethodCall, newTypeParameterNames,
			typeParameterNames, "typeParam#");
	}

	private String _addReplacementDependencies(
		String fileName, JSONObject jsonObject, String newContent) {

		String[] newImports = JSONUtil.toStringArray(
			jsonObject.getJSONArray("newImports"));

		if (fileName.endsWith(".java")) {
			newContent = JavaSourceUtil.addImports(newContent, newImports);

			return _addNewReference(
				newContent, jsonObject.getString("newReference"));
		}
		else if (fileName.endsWith(".jsp")) {
			newContent = BaseUpgradeCheck.addNewImportsJSPHeader(
				newContent, newImports);
		}

		return newContent;
	}

	private String _formatGeneral(
		String content, String fileName, JSONObject jsonObject) {

		String newContent = content;

		Pattern pattern = _getPattern(jsonObject);

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String methodCall = matcher.group();

			String from = jsonObject.getString("from");
			String to = jsonObject.getString("to");

			if (from.startsWith("regex:")) {
				newContent = newContent.replaceAll(pattern.toString(), to);
			}
			else if (from.contains(StringPool.OPEN_PARENTHESIS)) {
				newContent = _formatMethodCall(
					fileName, from, newContent, jsonObject, matcher, newContent,
					to);
			}
			else {
				Set<String> keys = jsonObject.keySet();

				if (!keys.contains("to")) {
					addMessage(fileName, _getMessage(jsonObject));

					_newMessage = true;

					continue;
				}

				newContent = StringUtil.replaceFirst(
					newContent, methodCall,
					StringUtil.replace(methodCall, from, to), matcher.start());
			}
		}

		if (!content.equals(newContent)) {
			newContent = _addReplacementDependencies(
				fileName, jsonObject, newContent);
		}

		return newContent;
	}

	private String _formatJava(
			String content, String fileName, JSONObject jsonObject)
		throws Exception {

		String newContent = content;

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		if(!jsonObject.getBoolean("classStructurePattern")) {
			return _formatCalls(content, fileName, jsonObject, javaClass);
		}

		String[] classNames = JSONUtil.toStringArray(
			jsonObject.getJSONArray("classNames"));

		if (ArrayUtil.isEmpty(classNames)) {
			return newContent;
		}

		for (String className : classNames) {
			if(!javaClass.getExtendedClassNames().contains(className) && !javaClass.getImplementedClassNames().contains(className)){
				return newContent;
			}
		}

		String[] newMethods = JSONUtil.toStringArray(
			jsonObject.getJSONArray("newMethods"));

		// Add method Logic

		if (!ArrayUtil.isEmpty(newMethods)) {
			for (String newMethod : newMethods){
				newContent = _insertMethodAlphabetically(newContent, newMethod);
			}
		}

		// Modify Method logic

		JSONArray methodsToFormat = jsonObject.getJSONArray("methodsToFormat");

		if (JSONUtil.isEmpty(methodsToFormat)) {
			return newContent;
		}

		for (Object method : methodsToFormat){
			JSONObject methodJSONObject = (JSONObject)method;

			newContent = _formatMethodSignature(newContent, methodJSONObject);
		}

		return newContent;
	}

	private String _formatMethodSignature(String content, JSONObject jsonObject){
		String from = jsonObject.getString("from");
		String to = jsonObject.getString("to");

		Pattern pattern = _getPattern(jsonObject); // TODO: talvez precise de um novo pra methodos?

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			if (from.startsWith("regex:")) {
				content = content.replaceAll(pattern.toString(), to);
			}
			else if (from.contains(StringPool.OPEN_PARENTHESIS)) {
				content = StringUtil.replace(content, matcher.group(), to);
			}
		}

		return content;
	}


	private String _insertMethodAlphabetically(String fileContent, String methodToInsert) {
		// Regular expression to find method signatures
		Pattern methodPattern = Pattern.compile("^(?:public|private|protected|static|final|synchronized|abstract|native)\\s+[^\\s]+\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\([^)]*\\)\\s*(?:throws\\s+[^\\s]+(?:,\\s+[^\\s]+)*)?\\s*\\{", Pattern.MULTILINE);
		List<String> methodNames = new ArrayList<>();
		List<String> existingMethods = new ArrayList<>();
		int classStartIndex = fileContent.indexOf("{");
		int classEndIndex = fileContent.lastIndexOf("}");
		if (classStartIndex == -1 || classEndIndex <= classStartIndex) {
			System.err.println("Could not find class definition in the file content.");
			return fileContent;
		}
		String classBody = fileContent.substring(classStartIndex + 1, classEndIndex);
		Matcher bodyMatcher = methodPattern.matcher(classBody);

		while (bodyMatcher.find()) {
			String methodName = bodyMatcher.group(1);
			int methodStartIndexInBody = bodyMatcher.start();
			int methodEndIndexInBody = findMatchingClosingBrace(classBody, bodyMatcher.end() - 1);
			if (methodEndIndexInBody != -1) {
				existingMethods.add(classBody.substring(methodStartIndexInBody, methodEndIndexInBody + 1));
				methodNames.add(methodName);
			}
		}

		// Extract the name of the method to be inserted
		Pattern newMethodNamePattern = Pattern.compile("^(?:public|private|protected|static|final|synchronized|abstract|native)\\s+[^\\s]+\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(", Pattern.MULTILINE);
		Matcher newMethodMatcher = newMethodNamePattern.matcher(methodToInsert);
		String newMethodName = null;
		if (newMethodMatcher.find()) {
			newMethodName = newMethodMatcher.group(1);
		} else {
			System.err.println("Could not extract the name of the method to be inserted.");
			return fileContent;
		}

		// Add the new method name to the list and sort
		methodNames.add(newMethodName);
		Collections.sort(methodNames);

		// Determine the insertion index
		int insertIndex = methodNames.indexOf(newMethodName);

		StringBuilder newClassBody = new StringBuilder();
		if (existingMethods.isEmpty()) {
			newClassBody.append(methodToInsert);
		} else {
			if (insertIndex == 0) {
				newClassBody.append(methodToInsert).append("\n\n").append(existingMethods.get(0)).append("\n\n");
				for (int i = 1; i < existingMethods.size(); i++) {
					newClassBody.append(existingMethods.get(i)).append("\n\n");
				}
			} else if (insertIndex == existingMethods.size()) {
				for (String existingMethod : existingMethods) {
					newClassBody.append(existingMethod).append("\n\n");
				}
				newClassBody.append(methodToInsert);
			} else {
				for (int i = 0; i < insertIndex; i++) {
					newClassBody.append(existingMethods.get(i)).append("\n\n");
				}
				newClassBody.append(methodToInsert).append("\n\n");
				for (int i = insertIndex; i < existingMethods.size(); i++) {
					newClassBody.append(existingMethods.get(i)).append("\n\n");
				}
			}
		}

		return new StringBuilder(fileContent.substring(0, classStartIndex + 1))
			.append(newClassBody.toString().trim())
			.append("\n}")
			.toString();
	}

	// Helper function to find the matching closing brace for a method
	private static int findMatchingClosingBrace(String text, int startIndex) {
		int braceCount = 1;
		for (int i = startIndex + 1; i < text.length(); i++) {
			if (text.charAt(i) == '{') {
				braceCount++;
			} else if (text.charAt(i) == '}') {
				braceCount--;
				if (braceCount == 0) {
					return i;
				}
			}
		}
		return -1; // Matching brace not found
	}

	private String _formatCalls(
		String content, String fileName, JSONObject jsonObject,
		JavaClass javaClass) throws Exception {

		String newContent = content;

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			String javaContent = null;

			if (childJavaTerm.isJavaMethod()) {
				JavaMethod javaMethod = (JavaMethod)childJavaTerm;

				javaContent = javaMethod.getContent();
			}
			else if (childJavaTerm.isJavaVariable()) {
				JavaVariable javaVariable = (JavaVariable)childJavaTerm;

				javaContent = javaVariable.getContent();
			}

			if (javaContent == null) {
				continue;
			}

			int index = newContent.indexOf(javaContent);

			Pattern pattern = _getPattern(jsonObject);

			Matcher matcher = pattern.matcher(javaContent);

			while (matcher.find()) {
				String methodCall = matcher.group();

				String[] classNames = JSONUtil.toStringArray(
					jsonObject.getJSONArray("classNames"));

				if ((classNames.length > 0) &&
					!_hasValidClassName(
						classNames, javaContent, content, fileName,
						methodCall)) {

					continue;
				}

				String from = jsonObject.getString("from");
				String to = jsonObject.getString("to");

				if (from.startsWith("regex:")) {
					if (classNames.length > 0) {
						newContent = StringUtil.replaceFirst(
							newContent, methodCall,
							methodCall.replaceFirst(pattern.toString(), to),
							index + matcher.start());
					}
					else {
						newContent = newContent.replaceAll(
							pattern.toString(), to);
					}
				}
				else if (from.contains(StringPool.OPEN_PARENTHESIS)) {
					newContent = _formatMethodCall(
						fileName, from, javaContent, jsonObject, matcher,
						newContent, to);
				}
				else if (from.contains(StringPool.LESS_THAN)) {
					newContent = _formatTypeParameters(
						methodCall, newContent, to);
				}
				else {
					newContent = StringUtil.replaceFirst(
						newContent, methodCall,
						StringUtil.replace(methodCall, from, to),
						matcher.start());
				}
			}
		}

		if (!content.equals(newContent)) {
			newContent = _addReplacementDependencies(
				fileName, jsonObject, newContent);
		}
		else if (!_newMessage) {
			Set<String> keys = jsonObject.keySet();

			if (!keys.contains("to")) {
				Pattern pattern = _getPattern(jsonObject);

				Matcher matcher = pattern.matcher(content);

				if (matcher.find()) {
					addMessage(fileName, _getMessage(jsonObject));

					_newMessage = true;
				}
			}
		}

		return newContent;
	}

	private String _formatMethodCall(
		String fileName, String from, String javaMethodContent,
		JSONObject jsonObject, Matcher matcher, String newContent, String to) {

		String methodCall = JavaSourceUtil.getMethodCall(
			javaMethodContent, matcher.start());

		List<String> parameterNames = JavaSourceUtil.getParameterNames(
			methodCall);

		if (!_hasValidMethodCall(
				fileName, from, javaMethodContent, jsonObject, newContent,
				parameterNames)) {

			return newContent;
		}

		if (to.isEmpty()) {
			String newJavaMethodContent = StringUtil.removeFirst(
				javaMethodContent, methodCall);

			String line = getLine(
				newJavaMethodContent,
				getLineNumber(newJavaMethodContent, matcher.start()));

			return StringUtil.replaceFirst(
				newContent, javaMethodContent,
				StringUtil.removeFirst(
					newJavaMethodContent, line + CharPool.NEW_LINE));
		}

		return _formatParameters(methodCall, newContent, parameterNames, to);
	}

	private String _formatParameters(
		String methodCall, String newContent, List<String> parameterNames,
		String to) {

		String newMethodCall = to.substring(
			0, to.indexOf(CharPool.OPEN_PARENTHESIS) + 1);

		if (!newMethodCall.contains(StringPool.PERIOD) &&
			!Character.isUpperCase(newMethodCall.charAt(0)) &&
			!newMethodCall.contains(StringPool.SPACE)) {

			newMethodCall = StringBundler.concat(
				getVariableName(methodCall), CharPool.PERIOD, newMethodCall);
		}

		newMethodCall = _addOrReplaceMethodParameters(
			parameterNames, newMethodCall, JavaSourceUtil.getParameterList(to));

		String removedFirstMethodCall = StringUtil.removeSubstring(
			to, JavaSourceUtil.getMethodCall(to, 0));

		newMethodCall = newMethodCall + removedFirstMethodCall;

		return StringUtil.replaceFirst(newContent, methodCall, newMethodCall);
	}

	private String _formatTypeParameters(
		String methodCall, String newContent, String to) {

		String newMethodCall = methodCall.substring(
			0, methodCall.indexOf(CharPool.LESS_THAN) + 1);

		String newTypeParameterName = to.substring(
			to.indexOf(CharPool.LESS_THAN) + 1,
			to.lastIndexOf(CharPool.GREATER_THAN));

		List<String> newTypeParameterNames = Arrays.asList(
			newTypeParameterName.split(StringPool.COMMA_AND_SPACE));

		String typeParameterName = methodCall.substring(
			methodCall.indexOf(CharPool.LESS_THAN) + 1,
			methodCall.lastIndexOf(CharPool.GREATER_THAN));

		List<String> typeParameterNames = Arrays.asList(
			typeParameterName.split(StringPool.COMMA_AND_SPACE));

		newMethodCall = _addOrReplaceTypeParameters(
			newMethodCall, newTypeParameterNames, typeParameterNames);

		return StringUtil.replace(newContent, methodCall, newMethodCall);
	}

	private boolean _hasValidClassName(
			String[] classNames, String content, String fileContent,
			String fileName, String methodCall)
		throws Exception {

		String variableName = getVariableName(methodCall);

		for (String className : classNames) {
			if (Character.isUpperCase(variableName.charAt(0)) &&
				StringUtil.equals(variableName, className)) {

				return true;
			}
			else if (!Character.isUpperCase(variableName.charAt(0)) &&
					 hasClassOrVariableName(
						 className, content, fileContent, fileName,
						 methodCall)) {

				return true;
			}
		}

		return false;
	}

	private boolean _hasValidExtension(String fileName, JSONObject jsonObject) {
		String[] validExtensions = JSONUtil.toStringArray(
			jsonObject.getJSONArray("validExtensions"));

		if (validExtensions.length == 0) {
			validExtensions = new String[] {"java"};
		}

		for (String validExtension : validExtensions) {
			if (fileName.endsWith(CharPool.PERIOD + validExtension)) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasValidMethodCall(
		String fileName, String from, String javaMethodContent,
		JSONObject jsonObject, String newContent, List<String> parameterNames) {

		List<String> fromParameters = JavaSourceUtil.getParameterNames(from);

		boolean skipParametersValidation = jsonObject.getBoolean(
			"skipParametersValidation");

		if (!skipParametersValidation) {
			fromParameters = JavaSourceUtil.getParameterTypes(from);
		}

		if (parameterNames.size() != fromParameters.size()) {
			return false;
		}

		if (skipParametersValidation) {
			return true;
		}

		boolean sendMessage = false;

		Set<String> keys = jsonObject.keySet();

		if (!keys.contains("to")) {
			sendMessage = true;
		}
		else if (fileName.endsWith(".java")) {
			for (int i = 0; i < fromParameters.size(); i++) {
				String parameterName = parameterNames.get(i);

				String variableTypeName = getVariableTypeName(
					javaMethodContent, null, newContent, fileName,
					parameterName.trim(), true, false);

				if (variableTypeName == null) {
					sendMessage = true;
				}
				else if (!StringUtil.equals(
							fromParameters.get(i), variableTypeName)) {

					return false;
				}
			}
		}

		if (sendMessage) {
			addMessage(fileName, _getMessage(jsonObject));

			_newMessage = true;

			return false;
		}

		return true;
	}

	private static final String _CONSTRUCTOR_REGEX =
		"n?e?w? ?(:?[A-Z][a-z]*)+\\(.*\\)";

	private static String _issueKey;
	private static final Pattern _parameterNamePattern = Pattern.compile(
		"\\w+#(\\d+)#");
	private static boolean _testMode;

	private boolean _newMessage;

}