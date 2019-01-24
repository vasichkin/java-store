package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import javax.inject.Singleton;

import play.mvc.Result;
import services.Authenticator;
import services.User;

/**
 * Authentication and authorization here
 */
@Singleton
public class AuthController extends Controller {
    private Authenticator auth = new Authenticator();
    private Long FailedLoginBlockTime = System.currentTimeMillis();
    private Integer lastFailedLoginCount = 0;
    private Boolean bruteforceProtection = false;
    private Integer bruteforceSleep = 3;

    public Result login() {
        JsonNode data = request().body().asJson();
        String userName = data.get("email").textValue();
        String password = data.get("password").textValue();

        /*
        To prevent bruteforcing allow 3 attempt, then block any login attempts for bruteforceSleep seconds.
        bruteforceSleep is doubled each time protection activated.
         */

        if (bruteforceProtection) {
            Long timePassedSec = (System.currentTimeMillis() - FailedLoginBlockTime)/1000;
            if (timePassedSec < bruteforceSleep) {
                return badRequest("Bruteforce protection active "+timePassedSec);
            } else {
                bruteforceSleep += bruteforceSleep;
                bruteforceProtection = false;
            }
        }

        if (auth.loginUser(userName, password).isEmpty()) {
            lastFailedLoginCount++;
            if (lastFailedLoginCount>3) {
                lastFailedLoginCount = 0;
                bruteforceProtection = true;
                return badRequest("Bruteforce protection activated");
            }
            return status(409, "Login failed!");
        } else
        {
            session().put("email", userName);
            ObjectNode result = Json.newObject();
            result.put("username", auth.currentUser.getName());
            result.put("password", auth.currentUser.getPass());
            result.put("session", auth.currentUser.getSession());
            return ok(result);
        }
    }

    public Result register() {
        JsonNode data = request().body().asJson();
        String user = data.get("email").textValue();
        String password = data.get("password").textValue();

        if (auth.addUser(new User(user, password))) {
            return ok("User "+user+" registered");
        } else {
            return status(409, "User exists");
        }
    }

    public Result userInfo(String userName) {
        if (session().get("email") == userName) {
            ObjectNode result = Json.newObject();
            result.put("username", auth.currentUser.getName());
            result.put("password", auth.currentUser.getPass());
            result.put("catId", session().get("catId"));
            return ok(result);
        }
        return ok("Login first");
    }

    public Result resetPassword(String userName) {
        // TODO
        return ok();
    }

    public Result logout() {
        auth.logoutUser();
        session().clear();
        return ok("Bye");
    }
}
