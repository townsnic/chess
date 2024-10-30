package dataaccess;

import model.AuthData;

import java.sql.*;
import java.util.UUID;

public class MySqlAuthDAO extends MySqlDataAccess implements AuthDAO {

    public MySqlAuthDAO() throws DataAccessException {
    }

    public void clearAuth() throws DataAccessException {
        String statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        String authToken = UUID.randomUUID().toString();
        executeUpdate(statement, authToken, username);
        return new AuthData(authToken, username);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new AuthData(authToken, username);
    }
}
