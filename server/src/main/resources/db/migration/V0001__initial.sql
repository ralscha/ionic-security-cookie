CREATE TABLE app_role (
    id      BIGINT NOT NULL AUTO_INCREMENT,
    name    VARCHAR(50) NOT NULL,
    PRIMARY KEY(id)
);

INSERT INTO app_role(id, name) values(1, 'ADMIN'), (2, 'USER');


CREATE TABLE app_user (
	id               BIGINT NOT NULL AUTO_INCREMENT,
	first_name       VARCHAR(255),
	last_name        VARCHAR(255),
	user_name        VARCHAR(255) NOT NULL,
	email            VARCHAR(255) NOT NULL,
	password_hash    VARCHAR(80),
    enabled          BOOLEAN not null,
    failed_logins    INTEGER,
    locked_out_until TIMESTAMP,
    last_access      TIMESTAMP,
    password_reset_token             VARCHAR(48),
    password_reset_token_valid_until TIMESTAMP,
    PRIMARY KEY(id),
    UNIQUE(user_name)
);

CREATE TABLE app_user_roles (
    app_user_id  BIGINT NOT NULL,
    app_role_id  BIGINT NOT NULL,
    PRIMARY KEY (app_user_id, app_role_id),
    FOREIGN KEY (app_user_id) REFERENCES app_user(id),
    FOREIGN KEY (app_role_id) REFERENCES app_role(id)
); 

CREATE TABLE remember_me_token (
	id              BIGINT NOT NULL AUTO_INCREMENT,
	series          VARCHAR(36) NOT NULL,
	token_value     VARCHAR(36) NOT NULL,
	token_date      TIMESTAMP NOT NULL,
	ip_address      VARCHAR(39),
	user_agent      VARCHAR(255),	
	username        VARCHAR(255) NOT NULL,
	PRIMARY KEY(id)
);
