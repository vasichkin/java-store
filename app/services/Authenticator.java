package services;


import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class Authenticator {
    public static User testUser = new User("testuser", "testpassword");

    private ArrayList<User> users = new ArrayList();
    public User currentUser;

    public Authenticator() {
        users.add(testUser);
    }

    public boolean addUser(User user){

        if (users.contains(user)) {
            return false;
        }
        users.add(user);
        return true;
    }

    //TODO
    public String loginUser(User user) {
        if (authenticate(user)) {
            return "Authenticated";
        }
        return "";
    }

    public String loginUser(String userName, String password) {
        return loginUser(new User(userName, password));
    }

    public void logoutUser() {
        currentUser = null;
    }

    public boolean authenticate(User user) {
        if (users.contains(user)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public boolean authenticate(String userName, String password) {
        return authenticate(new User(userName, password));
    }

}
