package services;


public class User {
    private String user;
    private String pass;
    private String session;

    public User (String username, String password) {
        user = username;
        pass = password;
    }

    public String getPass() {
        return "*****";
    }

    public String getName() {
        return user;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean userMatch = false;
        boolean passwordMatch = false;

        if (object != null && object instanceof User)
        {
            userMatch = this.user.equals(((User) object).user);
            passwordMatch = this.pass.equals(((User) object).pass);
        }
        return userMatch && passwordMatch;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}

