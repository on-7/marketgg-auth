CREATE TABLE `roles`
(
    `role_no` BIGINT      NOT NULL,
    `name`    VARCHAR(10) NOT NULL
);

CREATE TABLE `auth`
(
    `auth_no`             BIGINT       NOT NULL PRIMARY KEY,
    `password`            VARCHAR(255) NOT NULL,
    `email`               VARCHAR(30)  NOT NULL,
    `name`                VARCHAR(15)  NOT NULL,
    `phone_number`        VARCHAR(15)  NOT NULL,
    `password_updated_at` DATE         NOT NULL,
    `provider`            VARCHAR(10)  NOT NULL
);

CREATE TABLE `auth_roles`
(
    `role_no` BIGINT NOT NULL ,
    `auth_no` BIGINT NOT NULL ,

    PRIMARY KEY(`role_no`, `auth_no`),

    FOREIGN KEY (`role_no`)
    REFERENCES `roles` (`role_no`),

    FOREIGN KEY (`auth_no`)
    REFERENCES `auth` (`auth_no`)
);


