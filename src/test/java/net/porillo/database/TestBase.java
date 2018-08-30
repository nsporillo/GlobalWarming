package net.porillo.database;

import org.testng.annotations.DataProvider;

public class TestBase {

	/**
	 * Configures this test to use the default data source such that
	 * these tests will pass when ran on the Jenkins server. A user
	 * and pass that is only permitted to execute locally is setup.
	 *
	 * @return 2d array of datasource credentials
	 */
	@DataProvider(name = "mysqlDataSource")
	public static Object[][] createTestDataSource() {
		return new Object[][]{
				{"localhost", 3306, "GlobalWarming", "jenkins", "tests"}
		};
	}

}
