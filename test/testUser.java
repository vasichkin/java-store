import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.libs.Time;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class testUser extends WithApplication {

    public static ObjectNode fixtureUser() {
        ObjectNode data = Json.newObject();
        data.put("email", "test@email.com");
        data.put("password", "test");
        return data;
    }

    @Before
    public void fixture() {

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(fixtureUser())
                .uri("/register");
        route(app, request);
    }

    /*
    Register new user. Example request: {“email”:”my@email.com”, “password”:”123”}
    Respond with an appropriate HTTP codes (200 for ok, 409 for existing user)
     */
    @Test
    public void userCouldBeRegistered() {
        ObjectNode data = Json.newObject();
        data.put("email", "my@email.com");
        data.put("password", "123");

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(data)
                .uri("/register");
        Result result = route(app, request);
        assertThat(contentAsString(result), containsString("User my@email.com registered"));
    }

    @Test
    public void registeringExistingUserGivesError() {
        ObjectNode data = Json.newObject();
        data.put("email", "user1@email.com");
        data.put("password", "123");

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(data)
                .uri("/register");
        Result result = route(app, request);
        assertThat(contentAsString(result), containsString("User user1@email.com registered"));


        request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(data)
                .uri("/register");
        result = route(app, request);
        assertThat(contentAsString(result), containsString("User exists"));
    }

    /*
    Login into system. Example request: {“email”:”my@email.com”, “password”:”123”}
    Respond with JSON containing sessionId.
     */
    @Test
    public void userCouldLogin() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(fixtureUser())
                .uri("/login");
        Result result = route(app, request);

        JsonNode response = Json.parse(contentAsString(result));
        assertThat(response.get("username").asText(), equalTo("test@email.com"));
    }


    /*
    Think about preventing an intruder from bruteforcing
    */
    @Test
    public void bruteForceProtection() {
        ObjectNode data = Json.newObject();
        data.put("email", "non-existent@email.com");
        data.put("password", "123");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(data)
                .uri("/login");
        Result result = route(app, request);

        route(app, request);
        route(app, request);
        result = route(app, request);
        assertThat(contentAsString(result), containsString("Bruteforce protection activated"));
        result = route(app, request);
        assertThat(contentAsString(result), containsString("Bruteforce protection active"));

    }


    /*
    Reset password.
    */
    @Test
    public void passwordReset() {
    }



}
