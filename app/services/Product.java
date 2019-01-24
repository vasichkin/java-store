package services;


public class Product {
    public Integer id;
    public String title;
    public Integer stock;
    public Integer price;

    public Product(Integer id, String title, Integer stock, Integer price) {
        this.id = id;
        this.title = title;
        this.stock = stock;
        this.price = price;
    }

    public boolean dispatch(Integer quantity) {
        if (quantity>stock) {
            return false;
        }
        this.stock = stock-quantity;
        return true;
    }

    public boolean stock(Integer quantity) {
        this.stock = stock+quantity;
        return true;
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof Product)
        {
            return this.id.equals(((Product) object).id);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.title +"("+this.id+")";
    }

}
