package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
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
        // connect to db, pass SQL command to MySQL
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
        // connect to db, pass SQL command to MySQL
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

        // connect to db, pass SQL command to MySQL
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, token);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new AuthData(resultSet.getString("token"),
                            resultSet.getString("username"));
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

        // connect to db, pass SQL command to MySQL
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch (SQLException error) {
            throw new DataAccessException("Error deleting auth token: " + error.getMessage());
        }
    }



    // ---------
    // games methods from DataAccess
    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO games (game_name, white_username, black_username, game_state) VALUES (?, ?, ?, ?)";

        // connect to db, pass SQL command above to MySQL
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String json = new Gson().toJson(game.game());

            statement.setString(1, game.gameName());
            statement.setString(2, game.whiteUsername());
            statement.setString(3, game.blackUsername());
            statement.setString(4, json);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int gameID = keys.getInt(1);
                    return new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
                }
            }
            throw new DataAccessException("Failed to get game ID");
        } catch (SQLException error) {
            throw new DataAccessException("Error creating game: " + error.getMessage());
        }
//        return null;
    }


    // 1. connect to db
    // 2. while loop thorugh rows in table
    // 3. rebuild chess games from JSON
    // 4. return list of GameData records of what is in db
    @Override
    public List<GameData> listGames() throws DataAccessException {
        String sql = "SELECT * FROM games";
        List<GameData> gamesList = new ArrayList<>();


        // connect to db, pass SQL command to MySQL
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                // all columns of data in SQL tables
                int gameID = resultSet.getInt("game_id");
                String gameName = resultSet.getString("game_name");
                String whiteUsername = resultSet.getString("white_username");
                String blackUsername = resultSet.getString("black_username");
                String gameStateJSON = resultSet.getString("game_state");

                // deserial json, rebuild to GAmeData obj
                ChessGame game = new Gson().fromJson(gameStateJSON, ChessGame.class);
                GameData data = new GameData(gameID, whiteUsername, blackUsername, gameName, game);

                // add info to list of GameData objs
                gamesList.add(data);

            }
        } catch (SQLException error) {
            throw new DataAccessException("Error with games list: " + error.getMessage());
        }
        return gamesList;
    }


    // use ID to find game -> use game to rebuild GameData obj, if not, errors
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE game_id = ?";

        // connect to db, pass SQL command to MySQL
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, gameID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String gameName = resultSet.getString("game_name");
                    String whiteUsername = resultSet.getString("white_username");
                    String blackUsername = resultSet.getString("black_username");
                    String gameStateJSON = resultSet.getString("game_state");

                    // deserial json, rebuild to GAmeData obj
                    ChessGame game = new Gson().fromJson(gameStateJSON, ChessGame.class);

                    return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                }
            }
        } catch (SQLException error) {
            throw new DataAccessException("Error getting game: " + error.getMessage());
        }
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = """
                UPDATE games
                SET white_username = ?, black_username = ?, game_state = ?
                WHERE game_id = ?
                """;
        // db connect + deserialize GSON
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){

            String json = new Gson().toJson(game.game());

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, json);
            statement.setInt(4, game.gameID());

            statement.executeUpdate();
        } catch (SQLException error) {
            throw new DataAccessException("Error updating game: " + error.getMessage());
        }
    }
}
