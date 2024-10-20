package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> userDataMap = new HashMap<>();

    public void clear() {
        userDataMap.clear();
    }

    public void createUser(UserData userData) throws DataAccessException {
        if (userDataMap.containsKey(userData.username())) {
            throw new DataAccessException("Username already exists!");
        }
        userDataMap.put(userData.username(), userData);
    }

    public UserData getUser(String username) throws DataAccessException {
        if (!userDataMap.containsKey(username)) {
            throw new DataAccessException("Username doesn't exist!");
        }
        return userDataMap.get(username);
    }
}