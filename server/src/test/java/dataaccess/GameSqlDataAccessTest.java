package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class GameSqlDataAccessTest {


    private GameDataAccess gDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        gDAO = new GameSqlDataAccess();
        gDAO.clear();
    }

    @Test
    void clear() {
        assertDoesNotThrow(()-> {gDAO.clear();});
    }

    @Test
    void createGame() {
        assertDoesNotThrow(()-> {gDAO.createGame("createGametest");});


    }

    @Test
    void listGames() {
    }

    @Test
    void updateGame() {
        assertDoesNotThrow(()-> {int iD = gDAO.createGame("updateCheck");
            gDAO.updateGame(iD,"BLACK", "colton");
            gDAO.updateGame(iD,"BLACK", "seth");
            gDAO.updateGame(iD,"WHITE", "colton");


        });


    }

    @Test
    void getGame() {
        assertDoesNotThrow(()-> {int iD = gDAO.createGame("New Game");
            GameData test = new GameData(iD, null,null,"New Game",new ChessGame());
            assertEquals(gDAO.getGame(iD), test);});


    }
}