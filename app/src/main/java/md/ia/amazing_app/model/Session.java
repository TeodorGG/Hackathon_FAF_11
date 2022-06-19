package md.ia.amazing_app.model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Session {
    long startSession;
    long endSession;
    int index;
    int bg;
    int ph;
    public List<User> users = new ArrayList<>();


    public Session(long startSession, long endSession, String name, String email, int index, int bg, int ph) {
        this.startSession = startSession;
        this.endSession = endSession;
        this.bg = bg;
        this.ph = ph;

        this.index = index;

        users.add(new User(name, email));
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("startSession", startSession);
        result.put("endSession", endSession);
        result.put("index", index);
        result.put("bg", bg);
        result.put("ph", ph);

        return result;
    }
}
