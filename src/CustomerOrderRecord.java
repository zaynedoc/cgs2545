import java.math.BigDecimal;

/**
 * Holds customer order data returned by the order history procedure
 *
 * @param saleId sale id from the sale table
 * @param productName product name joined from the product table
 * @param quantity ordered quantity from the sale table
 * @param total total amount from the sale table
 */
public record CustomerOrderRecord(int saleId, String productName, int quantity, BigDecimal total) {
}