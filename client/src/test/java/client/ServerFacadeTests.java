package client;

import model.AuthData;
import model.GameName;
import model.JoinGamer;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void setUp() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void posRegister() {
        UserData user = new UserData("myplayer", "password", "p1@email.com");
        assertDoesNotThrow(() -> {facade.register(user);});

    }

    @Test
    public void negRegister() {
        UserData user = new UserData(null, "password", "p1@email.com");
        assertThrows(Throwable.class,() -> {facade.register(user);});

    }

    @Test
    public void posLogin() {
        UserData user = new UserData("myplayer", "password", "p1@email.com");
        assertDoesNotThrow(() -> {facade.register(user);
            facade.login(user);});

    }

    @Test
    public void negLogin() {
        assertThrows(Throwable.class,() ->
        {facade.login(new UserData("hello", "yuh","boomer"));});
    }

    @Test
    public void posCreate() {

        assertDoesNotThrow(() ->
        {facade.create(new GameName("name"),new AuthData("auth", "user"));});

    }

    @Test
    public void negCreate() {
        assertThrows(Throwable.class, () ->
        {facade.create(new GameName("name"),new AuthData(null, "user"));});
    }

    @Test
    public void posList() {

        assertDoesNotThrow(() ->
        {UserData user = new UserData("thisguy", "my", "p1@email.com");
            AuthData auth = facade.register(user);
            facade.create(new GameName("Name"),auth);
            facade.listGames(auth);});

    }

    @Test
    public void negList() {
        assertThrows(Throwable.class,() ->
        {   AuthData auth = new AuthData("fake","fakeuser");
            facade.create(new GameName("Name"),auth);
            facade.listGames(auth);});
    }

    @Test
    public void posJoin() {

        assertDoesNotThrow(() ->
        {UserData user = new UserData("myplayer", "password", "p1@email.com");
            AuthData auth = facade.register(user);
            facade.create(new GameName("Name"),auth);
            });

    }

    @Test
    public void negJoin() {

        assertThrows(Throwable.class,() ->
        {UserData user = new UserData("myplayer", "password", "p1@email.com");
            AuthData auth = facade.register(user);
            facade.create(new GameName("Name"),auth);
            facade.listGames(auth);
            JoinGamer joiner = new JoinGamer("white", 12);
            facade.joinGame(joiner,auth);
        });

    }




}
