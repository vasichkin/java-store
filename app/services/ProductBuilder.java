package services;


public class ProductBuilder {
    public Integer id = 000;
    public String title = "Default title";
    public Integer stock = 0;
    public Integer price = 0;

    public Product build() {
        Product prd = new Product(id, title, stock, price);
        return prd;
    }

    public ProductBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ProductBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ProductBuilder stock(Integer stock) {
        this.stock = stock;
        return this;
    }

    public ProductBuilder price(Integer price) {
        this.price = price;
        return this;
    }
}