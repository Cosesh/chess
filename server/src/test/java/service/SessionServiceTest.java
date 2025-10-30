package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;
import dataaccess.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {


    private UserDataAccess uDAO;
    private AuthDataAccess aDAO;
    private GameDataAccess gDAO;
    private SessionService service;

    @BeforeEach
    void setup() {
        uDAO = new UserMemoryDataAccess();
        aDAO = new AuthMemoryDataAccess();
        gDAO = new GameMemoryDataAccess();
        service = new SessionService(uDAO, aDAO, gDAO);
    }

    @Test
    void posregister(){
        //Tests if user is actually added to the server
        assertDoesNotThrow(()->{var user = new UserData("cosesh", "poopypants11", "em@il.com");
            service.register(user);
            assertNotNull(uDAO.getUser("cosesh"));});

    }

    @Test
    void negregister() {
        //Tests if username is already taken

        assertDoesNotThrow(()-> {var user1 = new UserData("cosesh", "poopypants11", "em@il.com");
            var user2 = new UserData("cosesh", "poopypants11", "em@il.com");
            service.register(user1);
            assertThrows(AlreadyTakenException.class, () -> service.register(user2));});

    }

    @Test
    void poslogin()  {
        //Tests if logging in adds auth data into database

        assertDoesNotThrow(()-> {var user = new UserData("cosesh", "poopypants11", "em@il.com");
            service.register(user);
            AuthData auth = service.login(user);
            assertNotNull(aDAO.getAuth(auth.authToken()));});

    }

    @Test
    void neglogin() {

        assertThrows(BadRequestException.class, () ->{var user = new UserData(null, "poopypants11", "em@il.com");
            service.register(user);
            service.login(user);} );
    }

    @Test
    void poslogout()  {
        //checks if authdata is removed from database after logout

        assertDoesNotThrow(()-> {var user = new UserData("cosesh", "poopypants11", "em@il.com");
            service.register(user);
            AuthData auth = service.login(user);
            service.logout(auth.authToken());
            assertNull(aDAO.getAuth(auth.authToken()));});
    }

    @Test
    void neglogout() {
        //Checks if unauthorizedexception is thrown for unauthorized logout token

        assertThrows(UnauthorizedException.class, () -> {var user = new UserData("cosesh", "poopypants11", "em@il.com");
            service.register(user);
            service.login(user);
            service.logout("unauthorized token");} );
    }

    @Test
    void poscreateGame() {
        //Checks if game is actually added to the database


        assertDoesNotThrow(()-> {var user = new UserData("cosesh", "poopypants11", "em@il.com");
            service.register(user);
            AuthData auth = service.login(user);
            var game = new GameData(1234,null, null, "poopoo", null);
            service.createGame(game, auth.authToken());
            assertNotNull(gDAO.getGame(1));});
    }

    @Test
    void negcreateGame() {
        //Verifies thrown exception for a bad request, no game name

        assertThrows(BadRequestException.class, () -> {var user = new UserData("cosesh", "poopypants11", "em@il.com");
            service.register(user);
            AuthData auth = service.login(user);
            var game = new GameData(1234,"peepee", null, null, null);
            service.createGame(game, auth.authToken());}  );
    }

    @Test
    void posjoinGame() {
        //creates game and joins two users and checks if they are the usernames for black and white

        assertDoesNotThrow(()-> {var user1 = new UserData("stinky", "poopypants11", "em@il.com");
            var user2 = new UserData("loser", "poopypants11", "em@il.com");
            service.register(user1);
            service.register(user2);
            AuthData auth1 = service.login(user1);
            AuthData auth2 = service.login(user2);
            var game = new GameData(1234,null, null, "gamebaby", null);
            service.createGame(game, auth1.authToken());
            service.joinGame(1,auth1.authToken(), "WHITE");
            service.joinGame(1,auth2.authToken(), "BLACK");
            assertEquals("stinky", gDAO.getGame(1).whiteUsername());
            assertEquals("loser", gDAO.getGame(1).blackUsername());});
    }

    @Test
    void negjoinGame() {

        assertThrows(AlreadyTakenException.class, () -> {var user1 = new UserData("stinky", "poopypants11", "em@il.com");
            var user2 = new UserData("loser", "poopypants11", "em@il.com");
            service.register(user1);
            service.register(user2);
            AuthData auth1 = service.login(user1);
            AuthData auth2 = service.login(user2);
            var game = new GameData(1234,null, "boludo", "gamebaby", null);
            service.createGame(game, auth1.authToken());
            service.joinGame(1,auth1.authToken(), "WHITE");
            service.joinGame(1,auth2.authToken(), "BLACK");});
    }

    @Test
    void poslistGames() {
        //creates two games and checks to see if the
        // list games lists the games as it should from the database

        assertDoesNotThrow(()-> {var user = new UserData("cosesh", "poopypants11", "em@il.com");
            service.register(user);
            AuthData auth = service.login(user);
            var game1 = new GameData(1,null, "curry", "poopoo", null);
            var game2 = new GameData(1,"loser", null, "colton", null);
            service.createGame(game1, auth.authToken());
            service.createGame(game2, auth.authToken());
            var gamesList = new ArrayList<GameInfo>();
            gamesList.add(new GameInfo(1,null,"curry","poopoo"));
            gamesList.add(new GameInfo(2,"loser",null,"colton"));
            assertEquals(gamesList,service.listGames(auth.authToken()));});


    }

    @Test
    void neglistGames() {
        //checks if unauthorized access throws exception for listgames

        assertThrows(UnauthorizedException.class, () -> {var user = new UserData("cosesh", "poopypants11", "em@il.com");
            service.register(user);
            AuthData auth = service.login(user);
            var game1 = new GameData(1,null, "curry", "poopoo", null);
            var game2 = new GameData(1,"loser", null, "colton", null);
            service.createGame(game1, auth.authToken());
            service.createGame(game2, auth.authToken());
            service.listGames("unauthorized");});
    }


    @Test
    void posclear() {
        //clears database with no error


        assertDoesNotThrow(()-> {var user = new UserData("cosesh", "poopypants11", "em@il.com");
            service.register(user);
            AuthData auth = service.login(user);
            var game1 = new GameData(1,null, "curry", "poopoo", null);
            service.createGame(game1,auth.authToken());
            service.clear();
            assertTrue(aDAO.getMap().isEmpty());});

    }

    @Test
    void negclear() {
        //tries to clear nonexistent databases

        assertThrows(NullPointerException.class, () ->{service = new SessionService(null,null,null);
            service.clear();} );
    }
}