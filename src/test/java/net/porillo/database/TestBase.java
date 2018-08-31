package net.porillo.database;

public class TestBase {

	public static ConnectionManager getConnectionManager() {
		return new ConnectionManager("localhost", 3306, "GlobalWarming", "jenkins", "tests");
	}
}
