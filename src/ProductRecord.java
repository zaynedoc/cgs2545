import java.math.BigDecimal;

/**
 * Holds product data returned by the product list procedure
 *
 * @param id product id from the product table
 * @param productName product name from the product table
 * @param price product price from the product table
 */
public record ProductRecord(int id, String productName, BigDecimal price) {
}