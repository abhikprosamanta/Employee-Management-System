CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE employees (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(60) NOT NULL,
    last_name VARCHAR(60) NOT NULL,
    email VARCHAR(120) NOT NULL,
    phone_number VARCHAR(20),
    department VARCHAR(80) NOT NULL,
    job_title VARCHAR(100) NOT NULL,
    salary DECIMAL(12, 2) NOT NULL,
    date_of_joining DATE NOT NULL,
    created_by_user_id BIGINT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_employee_email UNIQUE (email),
    CONSTRAINT fk_employee_created_by_user
        FOREIGN KEY (created_by_user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE INDEX idx_employee_first_last_name ON employees (first_name, last_name);
CREATE INDEX idx_employee_department ON employees (department);
