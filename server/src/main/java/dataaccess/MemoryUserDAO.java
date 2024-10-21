package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> userDataMap = new HashMap<>();

    public void clear() {
        userDataMap.clear();
    }

    public void createUser(UserData userData) {
        userDataMap.put(userData.username(), userData);
    }

    public UserData getUser(String username) {
        return userDataMap.get(username);
    }
}