package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.Catalog;
import services.ProductBuilder;

import javax.inject.Singleton;


/**
 * Catalog
 */
@Singleton
public class CatalogController extends Controller {
    private Catalog catalog = new Catalog();

    public Result listProducts() {

        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = "";

        try {
            jsonStr = mapper.writeValueAsString(catalog.listProducts());
        } catch (JsonProcessingException e) {}

        return ok(jsonStr);
    }

    public Result createProduct() {
        JsonNode data = request().body().asJson();
        ProductBuilder constructor = new ProductBuilder();

        constructor.id(data.get("id").intValue())
                .title(data.get("title").textValue())
                .stock(data.get("stock").intValue())
                .price(data.get("price").intValue());

        if (catalog.addProduct(constructor.build())) {
            return created("Product created");
        }
        return notAcceptable("Product not created");
    }

    public Result readProduct(Integer id) {
        return ok(Json.toJson(catalog.getProduct(id)));
    }

    public Result updateProduct(int id) {
        JsonNode data = request().body().asJson();
        ProductBuilder constructor = new ProductBuilder();

        constructor.id(id)
                .title(data.get("title").textValue())
                .stock(data.get("stock").intValue())
                .price(data.get("price").intValue());
        catalog.updateProduct(constructor.build());
        return ok("Product updated");
    }

    public Result deleteProduct(Integer id) {
        catalog.deleteProduct(id);
        return ok("Product removed");
    }

}
