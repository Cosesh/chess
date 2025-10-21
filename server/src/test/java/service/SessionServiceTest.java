package service;

import chess.ChessGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;
import dataaccess.*;
import service.*;

import java.util.ArrayList;
import java.util.Map;

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
    void poslogout() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        //checks if authdata is removed from database after logout
        var user = new UserData("cosesh", "poopypants11", "em@il.com");
        service.register(user);
        AuthData auth = service.login(user);
        service.logout(auth.authToken());
        assertNull(ADAO.getAuth(auth.authToken()));
    }

    @Test
    void neglogout() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        //Checks if unauthorizedexception is thrown for  unauthorized logout token
        var user = new UserData("cosesh", "poopypants11", "em@il.com");
        service.register(user);
        service.login(user);
        assertThrows(UnauthorizedException.class, () ->  service.logout("unauthorized token"));
    }

    @Test
    void poscreateGame() throws UnauthorizedException, BadRequestException, AlreadyTakenException {
        //Checks if game is actually added to the database
        var user = new UserData("cosesh", "poopypants11", "em@il.com");
        service.register(user);
        AuthData auth = service.login(user);
        var game = new GameData(1234,null, null, "poopoo", null);
        service.createGame(game, auth.authToken());
        assertNotNull(GDAO.getGame(1));
    }

    @Test
    void negcreateGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        //Verifies thrown exception for a bad request, no game name
        var user = new UserData("cosesh", "poopypants11", "em@il.com");
        service.register(user);
        AuthData auth = service.login(user);
        var game = new GameData(1234,"peepee", null, null, null);
        assertThrows(BadRequestException.class, () ->  service.createGame(game, auth.authToken()));
    }

    @Test
    void posjoinGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        //creates game and joins two users and checks if they are the usernames for black and white
        var user1 = new UserData("stinky", "poopypants11", "em@il.com");
        var user2 = new UserData("loser", "poopypants11", "em@il.com");
        service.register(user1);
        service.register(user2);
        AuthData auth1 = service.login(user1);
        AuthData auth2 = service.login(user2);
        var game = new GameData(1234,null, null, "gamebaby", null);
        service.createGame(game, auth1.authToken());
        service.joinGame(1,auth1.authToken(), "WHITE");
        service.joinGame(1,auth2.authToken(), "BLACK");
        assertEquals("stinky",GDAO.getGame(1).whiteUsername());
        assertEquals("loser",GDAO.getGame(1).blackUsername());
    }

    @Test
    void negjoinGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        var user1 = new UserData("stinky", "poopypants11", "em@il.com");
        var user2 = new UserData("loser", "poopypants11", "em@il.com");
        service.register(user1);
        service.register(user2);
        AuthData auth1 = service.login(user1);
        AuthData auth2 = service.login(user2);
        var game = new GameData(1234,null, "boludo", "gamebaby", null);
        service.createGame(game, auth1.authToken());
        service.joinGame(1,auth1.authToken(), "WHITE");
        assertThrows(AlreadyTakenException.class, () -> service.joinGame(1,auth2.authToken(), "BLACK"));
    }

    @Test
    void poslistGames() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        //creates two games and checks to see if the
        // list games lists the games as it shoulf from the database
        var user = new UserData("cosesh", "poopypants11", "em@il.com");
        service.register(user);
        AuthData auth = service.login(user);
        var game1 = new GameData(1,null, "curry", "poopoo", null);
        var game2 = new GameData(1,"loser", null, "colton", null);
        service.createGame(game1, auth.authToken());
        service.createGame(game2, auth.authToken());
        var gamesList = new ArrayList<GameInfo>();
        gamesList.add(new GameInfo(1,null,"curry","poopoo"));
        gamesList.add(new GameInfo(2,"loser",null,"colton"));
        assertEquals(gamesList,service.listGames(auth.authToken()));


    }

    @Test
    void neglistGames() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        //checks if unauthorized access throws exception for listgames
        var user = new UserData("cosesh", "poopypants11", "em@il.com");
        service.register(user);
        AuthData auth = service.login(user);
        var game1 = new GameData(1,null, "curry", "poopoo", null);
        var game2 = new GameData(1,"loser", null, "colton", null);
        service.createGame(game1, auth.authToken());
        service.createGame(game2, auth.authToken());
        assertThrows(UnauthorizedException.class, () -> service.listGames("unauthorized"));

    }

}