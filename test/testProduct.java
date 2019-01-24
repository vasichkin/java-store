import com.fasterxml.jackson.databind.JsonNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;


import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.*;

public class testProduct extends WithApplication {

    public static class exampleProduct {
        public Integer id = 777;
        public String title = "Fire Thrower";
        public Integer stock = 8;
        public Double price = 999.95;
    }

    public static class exampleProduct2 {
        public Integer id = 888;
        public String title = "Fire extinguisher";
        public Integer stock = 4;
        public Double price = 19.95;
    }

    @Before
    public void fixture() {
        JsonNode data = Json.toJson(new exampleProduct());
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(data)
                .uri("/product");
        route(app, request);
    }

    @Test
    public void getProduct() {
        exampleProduct prd = new exampleProduct();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/product/"+prd.id);
        Result result = route(app, request);
        assertThat(contentAsString(result), containsString(prd.title));
        assertThat(contentAsString(result), containsString(prd.stock.toString()));
    }

    @Test
    public void addProduct() {
        JsonNode data = Json.toJson(new exampleProduct2());
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(data)
                .uri("/product");
        Result result = route(app, request);
        assertThat(contentAsString(result), containsString("Product created"));

        request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(data)
                .uri("/product");
        result = route(app, request);
        assertThat(contentAsString(result), containsString("Product not created"));
    }

    /*
    Get all products in store.
    Respond with JSON list of items you have, e.g.:
    {“id”:”2411”, “title”:”Nail gun”, “available”:8, “price”: “23.95”}
     */
    @Test
    public void listProducts() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/products");
        Result result = route(app, request);
        assertThat(contentAsString(result), containsString("Fire Thrower"));
    }


    @Test
    public void removeProduct() {
        int id = new exampleProduct().id;
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .uri("/product/"+id);
        Result result = route(app, request);
        assertThat(contentAsString(result), containsString("Product removed"));
    }

    @Test
    public void updateProduct() {
        exampleProduct prd = new exampleProduct();
        prd.title = "Updated"+prd.title;
        prd.stock = prd.stock++;

        JsonNode data = Json.toJson(prd);
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .bodyJson(data)
                .uri("/product/"+prd.id);
        Result result = route(app, request);
        assertThat(contentAsString(result), containsString("Product updated"));

        request = new Http.RequestBuilder()
                .method(GET)
                .uri("/product/"+prd.id);
        result = route(app, request);
        assertThat(contentAsString(result), containsString(prd.title));
        assertThat(contentAsString(result), containsString(prd.stock.toString()));
    }

    @After
    public void cleanup() {
        int id = new exampleProduct().id;
        JsonNode data = Json.toJson(new exampleProduct());
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .bodyJson(data)
                .uri("/product"+id);
        route(app, request);
    }
}
