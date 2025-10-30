package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.SessionService;

import static org.junit.jupiter.api.Assertions.*;

class UserSqlDataAccessTest {


    private UserDataAccess uDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        uDAO = new UserSqlDataAccess();
        uDAO.clear();
    }
    @Test
    void clear() {
        assertDoesNotThrow(()-> {uDAO.clear();});
    }

    @Test
    void poscreateUser()  {

        assertDoesNotThrow(()->{
            UserData test = new UserData("Hewbrewgirl67", "HewbrewRox123", "emileejensen@byu.edu");
            uDAO.createUser(test);});

    }

    @Test
    void posgetUser() throws DataAccessException {

        assertDoesNotThrow(()-> {UserData test = new UserData("Emilee", "HewbrewRox123", "emileeejensen@byu.edu");
            uDAO.createUser(test);
            assertEquals(uDAO.getUser("Emilee"),test);});

    }
}