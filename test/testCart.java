import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class testCart extends WithApplication {

    public void addToCart(Integer id, Integer q) {
        ObjectNode data = Json.newObject();
        data.put("productId", id);
        data.put("quantity", q);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .bodyJson(data)
                .session("cartId", "test-test")
                .uri("/cart/add");
        route(app, request);
    }

    /*
    Add item to cart. Example request: {“id”:”363”, “quantity”:”2”}
     */
    @Test
    public void addToCart() {
        ObjectNode data = Json.newObject();
        data.put("productId", 2411);
        data.put("quantity", 2);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .bodyJson(data)
                .uri("/cart/add");
        Result result = route(app, request);
        JsonNode response = Json.parse(contentAsString(result));

        assertTrue(response.hasNonNull("Nail gun(2411)"));
        assertThat(response.get("Nail gun(2411)").asInt(), equalTo(2));
    }

    /*
    Remove item from cart.
     */
    @Test
    public void removeFromCart() {
        addToCart(2411, 3);

        ObjectNode data = Json.newObject();
        data.put("productId", 2411);
        data.put("quantity", 1);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .bodyJson(data)
                .session("cartId", "test-test")
                .uri("/cart/remove");
        Result result = route(app, request);
        JsonNode response = Json.parse(contentAsString(result));

        assertTrue(response.hasNonNull("Nail gun(2411)"));
        assertThat(response.get("Nail gun(2411)").asInt(), equalTo(2));
    }

    /*
        Allow adding only one position at a time. If you don’t have this quantity in store - respond with an error.
     */
    @Test
    public void addToCartFailOnOverQuantity() {
        ObjectNode data = Json.newObject();
        data.put("productId", 2411);
        data.put("quantity", 100);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .bodyJson(data)
                .uri("/cart/add");
        Result result = route(app, request);
        assertThat(contentAsString(result), containsString("Failed to add product"));
    }
    /*
        The information has to be session-scoped: once session expires - user will get new empty cart.
    */
    @Test
    public void cartEmtyOnSessionExpiration() {
        ObjectNode data = Json.newObject();
        data.put("email", "my@email.com");
        data.put("password", "123");

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(data)
                .uri("/register");
        route(app, request);

        request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(data)
                .uri("/login");
        Result result = route(app, request);
        JsonNode response = Json.parse(contentAsString(result));

        data.put("productId", 2411);
        data.put("quantity", 2);
        request = new Http.RequestBuilder()
                .method(PUT)
                .bodyJson(data)
                .uri("/cart/add");
        result = route(app, request);
        response = Json.parse(contentAsString(result));

        assertTrue(response.hasNonNull("Nail gun(2411)"));
        assertThat(response.get("Nail gun(2411)").asInt(), equalTo(2));

        request = new Http.RequestBuilder()
                .method(GET)
                .bodyJson(data)
                .uri("/logout");
        route(app, request);

        request = new Http.RequestBuilder()
                .method(GET)
                .bodyJson(data)
                .uri("/cart");
        result = route(app, request);
        response = Json.parse(contentAsString(result));
        assertTrue(response.get("total").hasNonNull("Total items"));
        assertEquals(response.get("total").get("Total items").intValue(), 0);
        assertEquals(response.get("total").get("Total price").intValue(), 0);
    }

    /*
    Display your cart content.
    Respond with list of product names with their quantities added. Calculate subtotal. Assign an ordinal to each cart item.
     */
    @Test
    public void listCart() {
        addToCart(231, 1);
        addToCart(2411, 2);
        addToCart(231, 1);

        ArrayList<String> titles = new ArrayList();

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .session("cartId", "test-test")
                .uri("/cart");
        Result result = route(app, request);

        JsonNode response = Json.parse(contentAsString(result));

        assertTrue(response.hasNonNull("1"));
        assertTrue(response.get("1").hasNonNull("name"));
        titles.add(response.get("1").get("name").textValue());
        assertEquals(response.get("1").get("quantity").intValue(), 2);

        assertTrue(response.hasNonNull("2"));
        assertTrue(response.get("2").hasNonNull("name"));
        titles.add(response.get("2").get("name").textValue());
        assertEquals(response.get("2").get("quantity").intValue(), 2);

        assertTrue(titles.contains("Shotgun"));
        assertTrue(titles.contains("Nail gun"));

        assertTrue(response.hasNonNull("total"));

        assertTrue(response.get("total").hasNonNull("Total items"));
        assertEquals(response.get("total").get("Total items").intValue(), 4);
        assertEquals(response.get("total").get("Total price").intValue(), 5400);
    }


    @Test
    public void checkout() {
        addToCart(231, 1);
        addToCart(2411, 2);
        addToCart(231, 1);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .session("cartId", "test-test")
                .uri("/checkout");
        Result result = route(app, request);
        JsonNode response = Json.parse(contentAsString(result));

        assertTrue(response.hasNonNull("total"));

        assertTrue(response.get("total").hasNonNull("Total items"));
        assertEquals(response.get("total").get("Total items").intValue(), 4);
        assertEquals(response.get("total").get("Total price").intValue(), 5400);

    }
}
