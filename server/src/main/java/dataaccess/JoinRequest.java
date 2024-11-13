package dataaccess;

import chess.ChessGame;

public record JoinRequest(ChessGame.TeamColor playerColor, int gameID) {}
