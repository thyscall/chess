package dataaccess;

import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLDataAccess implements DataAccess {

    public MySQLDataAccess() throws DataAccessException {
        DatabaseManager.initDB();
        DatabaseManager.initTables();
    }

    // METHODS STRAIGHT FROM DATAACCESS.java

    // clear methods from DataAccess
    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             Statement statement = conn.createStatement()) {

            statement.executeUpdate("DELETE FROM auth_tokens");
            statement.executeUpdate("DELETE FROM users");
            statement.executeUpdate("DELETE FROM games");

        } catch (SQLException error) {
            throw new DataAccessException("Error clearing db: " + error.getMessage());
        }
    }

    // ---------
    // user methods from data access
    @Override
    public void insertUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            String hashPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

            statement.setString(1, user.username());
            statement.setString(2, hashPassword);
            statement.setString(3, user.email());

            statement.executeUpdate();

        } catch (SQLException error) {
            throw new DataAccessException("Error creating user: " + error.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password_hash, email FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new UserData(resultSet.getString("username"), resultSet.getString("password_hash"), resultSet.getString("email"));
                }
            }

        } catch (SQLException error) {
            throw new DataAccessException("Error getting user: " + error.getMessage());
        }

        return null;
    }

    // ---------
    // auth token methods from data access
    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auth_tokens (token, username) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, auth.authToken());
            statement.setString(2, auth.username());

            statement.executeUpdate();

        } catch (SQLException error) {
            throw new DataAccessException("Error creating token: " + error.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        String sql = "SELECT token, username FROM auth_tokens WHERE token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, token);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new AuthData(resultSet.getString("username"), resultSet.getString("token"));
                }
            }

        } catch (SQLException error) {
            throw new DataAccessException("Error retrieving auth token: " + error.getMessage());
        }

        return null;
    }

    // delete auth from DA
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth_tokens WHERE token = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch (SQLException error) {
            throw new DataAccessException("Error deleting auth token: " + error.getMessage());
        }
    }



//    // ---------
//    // games methods from DataAccess
//    @Override
//    public void createGame(GameData game) throws DataAccessException {
//        // TODO: insert with JSON serialization
//    }
//
//    @Override
//    public List<GameData> listGames() throws DataAccessException {
//        return new ArrayList<>(); // TODO: Implement select
//    }
//
//    @Override
//    public GameData getGame(int gameId) throws DataAccessException {
//        return null; // TODO: Implement lookup
//    }
//
//    @Override
//    public void updateGame(GameData game) throws DataAccessException {
//        // TODO: Implement update with deserialized state
//    }
//}
