package shadow;

import static org.junit.jupiter.api.Assertions.*;

import client.CommandLinesParams;
import org.junit.jupiter.api.Test;

class ShadowTest
{
    @Test
    public void readWriteLoginPassword_Test()
    {
        CommandLinesParams.setSystemDirectory("src/test/java/shadow/");
        Shadow shadow = new Shadow();

        final String login = "iva";
        final String password = "1234";

        shadow.saveLoginPassword(login, password);

        String r_login      = shadow.getLogin();
        String r_password   = shadow.getPassword();

        assertTrue(r_login.equals(login));
        assertTrue(r_password.equals(password));
    }
}