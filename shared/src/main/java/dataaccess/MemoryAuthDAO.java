package dataaccess;

import model.AuthData;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() {
        auths.clear();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        auths.remove(authToken);
    }
}