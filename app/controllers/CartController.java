package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.Cart;
import services.Catalog;
import services.Product;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;


/**
 * Cart
 */
@Singleton
public class CartController extends Controller {

    private HashMap<String,Cart> carts = new HashMap<>();

    @Inject
    private Catalog catalog;

    ObjectMapper mapper = new ObjectMapper();
    String jsonStr = "";

    public Cart getCart() {
        if (!session().containsKey("cartId")) {
            session().put("cartId", UUID.randomUUID().toString());
        }
        String cartId = session().get("cartId");

        if (carts.containsKey(cartId)) {
            return carts.get(cartId);
        } else {
            Cart cart =  new Cart();
            carts.put(cartId, cart);
            return cart;
        }
    }

    public void emptyCart() {
        String cartId = session().getOrDefault("cartId", "None");
        carts.remove(cartId);
    }

    public Result addToCart() {
        Cart cart = getCart();
        JsonNode data = request().body().asJson();
        Integer productId = data.get("productId").intValue();
        Integer quantity = data.get("quantity").intValue();
        Boolean status = cart.addProduct(catalog.getProduct(productId), quantity);

        try {
            jsonStr = mapper.writeValueAsString(cart.listCart());
        } catch (JsonProcessingException e) { }

        if (status) {
            return ok(jsonStr);
        } else {
            return status(401, "Failed to add product");
        }
    }

    public Result removeFromCart() {
        Cart cart = getCart();
        JsonNode data = request().body().asJson();
        Integer productId = data.get("productId").intValue();
        Integer quantity = data.get("quantity").intValue();

        Boolean status = cart.removeProduct(catalog.getProduct(productId), quantity);

        try {
            jsonStr = mapper.writeValueAsString(cart.listCart());
        } catch (JsonProcessingException e) { }

        if (status) {
            return ok(jsonStr);
        } else {
            return status(401, "Failed to remove product");
        }
    }


    private ObjectNode cartContent() {
        Cart cart = getCart();
        Integer i=0;
        Integer quantity = 0;
        Double sum=0.0;
        ObjectNode cartContent = Json.newObject();
        for (HashMap.Entry<Product, Integer> item : cart.listCart().entrySet()) {
            ObjectNode itemJson = Json.newObject();
            i++;
            quantity = quantity + item.getValue();
            sum = sum + item.getKey().price*item.getValue();
            itemJson.put("name", item.getKey().title);
            itemJson.put("quantity", item.getValue());
            itemJson.put("price", item.getKey().price);
            cartContent.set(i.toString(), itemJson);
        }

        ObjectNode total = Json.newObject();
        total.put("Total items", quantity);
        total.put("Total price", sum);
        cartContent.set("total", total);
        return cartContent;
    }

    public Result listCart() {
        return ok(cartContent());
    }


    public Result checkout() {
        Cart cart = getCart();
        if (cart.listCart().size() == 0) {
            return ok("Cart empty");
        }

        Boolean stokOk = true;
        for (HashMap.Entry<Product, Integer> item : cart.listCart().entrySet()) {
            if (item.getKey().stock < item.getValue()) {
                stokOk = false;
            }
        }

        if (stokOk) {
            for (HashMap.Entry<Product, Integer> item : cart.listCart().entrySet()) {
                item.getKey().dispatch(item.getValue());
            }
        }

        ObjectNode dispatched = cartContent();
        emptyCart();
        return ok(dispatched);
    }
}
