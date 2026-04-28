use project4;

drop procedure if exists registerNewUser;
drop procedure if exists loginWithCreds;
drop procedure if exists submitNewProduct;
drop procedure if exists getAllProducts;
drop procedure if exists editExistingProduct;
drop procedure if exists getSalesTotal;
drop procedure if exists submitOrder;
drop procedure if exists viewCustomerOrders;
drop procedure if exists cancelOrder;

delimiter $$

-- New customer account
create procedure registerNewUser(
    in newUsername varchar(255),
    in newPassword varchar(255)
)
begin
    insert into users (username, pass, userRole)
    values (newUsername, newPassword, 2);
end$$

-- Look up user by username & password
create procedure loginWithCreds(
    in loginUsername varchar(255),
    in loginPassword varchar(255)
)
begin
    select id, username, userRole
    from users
    where username = loginUsername
    and pass = loginPassword;
end$$

-- Append product to table
create procedure submitNewProduct(
    in productName varchar(255),
    in productPrice decimal(10,2)
)
begin
    insert into product (prodName, price)
    values (productName, productPrice);
end$$

-- Returns shared product list for both roles
create procedure getAllProducts()
begin
    select id, prodName, price
    from product
    order by id;
end$$

-- Updates an existing product in table
create procedure editExistingProduct(
    in productId integer,
    in productName varchar(255),
    in productPrice decimal(10,2),
    out rowsUpdated integer
)
begin
    update product
    set prodName = productName,
        price = productPrice
    where id = productId;

    set rowsUpdated = row_count();
end$$

-- Returns current sum of all order totals
create procedure getSalesTotal()
begin
    select coalesce(sum(total), 0.00) as salesTotal
    from sale;
end$$

-- Inserts a sale row for a customer order
create procedure submitOrder(
    in productId integer,
    in customerId integer,
    in orderQuantity integer,
    out rowsInserted integer,
    out orderTotal decimal(10,2)
)
begin
    declare productPrice decimal(10,2);

    set rowsInserted = 0;
    set orderTotal = null;

    select price into productPrice
    from product
    where id = productId;

    if productPrice is not null then
        set orderTotal = productPrice * orderQuantity;

        insert into sale (prodID, userID, qty, total)
        values (productId, customerId, orderQuantity, orderTotal);

        set rowsInserted = row_count();
    end if;
end$$

-- Returns all orders for one customer
create procedure viewCustomerOrders(
    in customerId integer
)
begin
    select sale.saleID, product.prodName, sale.qty, sale.total
    from sale
    join product on product.id = sale.prodID
    where sale.userID = customerId
    order by sale.saleID;
end$$

-- Deletes one order that belongs to the current customer
create procedure cancelOrder(
    in orderSaleId integer,
    in customerId integer,
    out rowsDeleted integer
)
begin
    delete from sale
    where saleID = orderSaleId
    and userID = customerId;

    set rowsDeleted = row_count();
end$$

DELIMITER ;