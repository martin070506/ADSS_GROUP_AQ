package Domain;

import java.util.List;

public class ProductFile {
    private final int fileNumber;
    private List<ProductPair> products;



    public ProductFile(List<ProductPair> products, int fileNumber){
        this.products = products;
        this.fileNumber = fileNumber;
    }

    public List<ProductPair> getProducts() {
        return products;
    }
    public int getFileNumber() {
        return fileNumber;
    }

    public void addProduct(ProductPair product){
        boolean flag=false;
        ProductPair currentPair=null;
        for(ProductPair pair:products){
            if(pair.product==product.product){
                flag=true;
                currentPair=pair;
                break;
            }
        }
        if(!flag){
            products.add(product);
        }
        else if(currentPair!=null){
            currentPair.setAmount(currentPair.getAmount()+product.getAmount());
        }
    }
    public void removeProduct(ProductPair product){
        boolean flag=false;
        ProductPair currentPair=null;
        for(ProductPair pair:products) {
            if (pair.product == product.product) {
                flag = true;
                currentPair = pair;
                break;
            }
        }
        if(flag){
            if(currentPair.getAmount()<=product.getAmount()){
                products.remove(currentPair);
            }
            else{
                currentPair.setAmount(currentPair.getAmount()-product.getAmount());
            }

        }
    }

    public void setProducts(List<ProductPair> products) {
        this.products = products;
    }
}
