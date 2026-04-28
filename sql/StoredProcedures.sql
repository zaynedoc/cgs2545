use project4;

drop procedure if exists registerNewUser;
drop procedure if exists loginWithCreds;
drop procedure if exists submitNewProduct;
drop procedure if exists getAllProducts;
drop procedure if exists editExistingProduct;

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

DELIMITER ;