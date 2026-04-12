import java.util.List;

public class ProductFile {
    private int fileNumber;
    private List<ProductPair> products;


    public ProductFile(List<ProductPair> products, int fileNumber){
        this.products = products;
        this.fileNumber = fileNumber;
    }

    public List<ProductPair> getProducts() {
        return products;
    }

    public void setProducts(List<ProductPair> products) {
        this.products = products;
    }
}
