package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthSqlDataAccessTest {


    private AuthDataAccess aDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        aDAO = new AuthSqlDataAccess();
        aDAO.clear();
    }
    @Test
    void clear() {
        assertDoesNotThrow(()-> {aDAO.clear();});
    }

    @Test
    void poscreateAuth() {
        assertDoesNotThrow(()-> {AuthData test = new AuthData("12345", "stupidhead");
            aDAO.createAuth(test);});

    }

    @Test
    void posdeleteAuth() {
        assertDoesNotThrow(()-> {AuthData test = new AuthData("12345", "stupidhead");
            AuthData test1 = new AuthData("54321", "idiot");
            aDAO.createAuth(test);
            aDAO.createAuth(test1);
            aDAO.deleteAuth("12345");});


    }

    @Test
    void posgetAuth() {
        assertDoesNotThrow(()->{AuthData test = new AuthData("12345", "stupidhead");
            aDAO.createAuth(test);
            assertEquals(aDAO.getAuth("12345"), test);});

    }
}