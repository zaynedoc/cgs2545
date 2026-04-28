import java.math.BigDecimal;

// holds the product data returned by the product-list procedure
public record ProductRecord(int id, String productName, BigDecimal price) {
}