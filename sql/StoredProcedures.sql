use project4;

drop procedure if exists registerNewUser;
drop procedure if exists loginWithCreds;

delimiter $$

create procedure registerNewUser(
    in newUsername varchar(255),
    in newPassword varchar(255)
)
begin
    insert into users (username, pass, userRole)
    values (newUsername, newPassword, 2);
end$$

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

delimiter ;