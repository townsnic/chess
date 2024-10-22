package server;

import chess.ChessGame;

public record GameRequest(ChessGame.TeamColor playerColor, int gameID) {}
