package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSqlDataAccessTest {

    @Test
    void clear() {
    }

    @Test
    void poscreateUser()  {

        assertDoesNotThrow(()->{UserSqlDataAccess uDAO = new UserSqlDataAccess();
            UserData test = new UserData("Tristan", "IloveCS", "email@byu.edu");
            uDAO.createUser(test);});

    }

    @Test
    void getUser() {
    }
}