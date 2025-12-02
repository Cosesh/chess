package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

public record SessionInfo(Session session, String user, ChessGame.TeamColor color) {
}
