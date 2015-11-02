package net.ralphpina.activitymapper;

import org.junit.Before;

public class BaseTest {

    private Account mAccount;

    @Before
    public void setUp() {
        mAccount = new Account();
        mAccount.logout();
    }

    protected void connectTestUser() {
        mAccount.login(false, "testuser", "Test1234");
    }

}
