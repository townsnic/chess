package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> userDataMap = new HashMap<>();

    public void clearUser() {
        userDataMap.clear();
    }

    public void createUser(UserData userData) {
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        userDataMap.put(userData.username(), new UserData(userData.username(), hashedPassword, userData.email()));
    }

    public UserData getUser(String username) {
        return userDataMap.get(username);
    }
}