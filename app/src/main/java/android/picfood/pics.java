package android.picfood;

public class pics{
    private String product;
    private String store;
    private String imageUri;
    private String comment;

    public pics() {
    }

    public pics(String product, String store, String imageUri,String comment) {
        this.product = product;
        this.store = store;
        this.imageUri = imageUri;
        this.comment = comment;
    }

    public String getProduct() {
        return product;
    }

    public String getStore() {
        return store;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getComment() {
        return comment;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setStore(String store) {
        this.store = store;
    }
}