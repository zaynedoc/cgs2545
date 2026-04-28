use project4;

drop procedure if exists registerNewUser;
drop procedure if exists loginWithCreds;
drop procedure if exists submitNewProduct;

delimiter $$

-- Creates a new customer account
create procedure registerNewUser(
    in newUsername varchar(255),
    in newPassword varchar(255)
)
begin
    insert into users (username, pass, userRole)
    values (newUsername, newPassword, 2);
end$$

-- Looks up a user by username and password
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

-- Adds a product for the admin flow
create procedure submitNewProduct(
    in productName varchar(255),
    in productPrice decimal(10,2)
)
begin
    insert into product (prodName, price)
    values (productName, productPrice);
end$$

-- Returns the shared product list for both roles
create procedure getAllProducts()
begin
    select id, prodName, price
    from product
    order by id;
end$$

delimiter ;