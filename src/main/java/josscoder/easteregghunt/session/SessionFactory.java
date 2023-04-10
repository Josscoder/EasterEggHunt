package josscoder.easteregghunt.session;

import josscoder.easteregghunt.session.data.UserSession;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class SessionFactory {

    @Getter
    private static SessionFactory instance;


    @Getter
    private final Map<String, UserSession> sessionStorage = new HashMap<>();

    public SessionFactory() {
        instance = this;
    }

    public static void make() {
        new SessionFactory();
    }

    public void store(UserSession userSession) {
        sessionStorage.put(userSession.getXuid(), userSession);
    }

    public void remove(String xuid) {
        sessionStorage.remove(xuid);
    }

    public UserSession getSession(String xuid) {
        return sessionStorage.get(xuid);
    }
}
