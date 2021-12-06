package com.liferay.util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author Nícolas Moura
 */
public class DatabaseConnection {

	public static Connection getConnection() {
		Connection conn = null;

		try {
			conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/master?characterEncoding=UTF-8&dontTrackOpenResources=true&holdResultsOpenOverStatementClose=true&serverTimezone=GMT&useFastDateParsing=false&useUnicode=true",
				"root", "lovethepoor");

			System.out.println("Conectado com sucesso!");
		}
		catch (Exception e) {
			System.err.println(e);
		}

		return conn;
	}

}