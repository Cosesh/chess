package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;
import dataaccess.*;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {


    private UserDataAccess UDAO;
    private AuthDataAccess ADAO;
    private GameDataAccess GDAO;
    private SessionService service;

    @BeforeEach
    void setup() {
        UDAO = new UserMemoryDataAccess();
        ADAO = new AuthMemoryDataAccess();
        GDAO = new GameMemoryDataAccess();
        service = new SessionService(UDAO, ADAO, GDAO);
    }

    @Test
    void posregister() throws BadRequestException, AlreadyTakenException {
        //Tests if user is actually added to the server
        var user = new UserData("cosesh", "poopypants11", "em@il.com");
        service.register(user);
        assertNotNull(UDAO.getUser("cosesh"));

    }

    @Test
    void negregister() throws BadRequestException, AlreadyTakenException {
        //Tests if username is already taken

        var user1 = new UserData("cosesh", "poopypants11", "em@il.com");
        var user2 = new UserData("cosesh", "poopypants11", "em@il.com");
        service.register(user1);
        assertThrows(AlreadyTakenException.class, () -> service.register(user2));

    }

    @Test
    void poslogin() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        //Tests if logging in adds auth data into database
        var user = new UserData("cosesh", "poopypants11", "em@il.com");
        service.register(user);
        AuthData auth = service.login(user);
        assertNotNull(ADAO.getAuth(auth.authToken()));

    }

    @Test
    void neglogin() throws BadRequestException, AlreadyTakenException {
        var user = new UserData(null, "poopypants11", "em@il.com");
        service.register(user);
        assertThrows(BadRequestException.class, () -> service.login(user));
    }

    @Test
    void poslogout() {
    }

    @Test
    void neglogout() {
    }

    @Test
    void poscreateGame() {
    }

    @Test
    void negcreateGame() {
    }

    @Test
    void posjoinGame() {
    }

    @Test
    void negjoinGame() {
    }

    @Test
    void poslistGames() {
    }

    @Test
    void neglistGames() {
    }
}