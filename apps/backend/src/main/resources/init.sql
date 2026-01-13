CREATE DATABASE IF NOT EXISTS MONGEUL;

USE MONGEUL;

-- 1. User 테이블 생성 및 더미 데이터 삽입
CREATE TABLE IF NOT EXISTS user (
                                    user_id INT NOT NULL AUTO_INCREMENT,
                                    name VARCHAR(255) NOT NULL,
                                    email VARCHAR(255) NOT NULL,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    privacy_policy_accepted VARCHAR(255),
                                    new_user boolean,
                                    PRIMARY KEY (user_id)
);


-- 2. Baby 테이블 생성 및 더미 데이터 삽입
CREATE TABLE IF NOT EXISTS baby (
                                    baby_id INT NOT NULL AUTO_INCREMENT,
                                    user_id INT NOT NULL,
                                    baby_name VARCHAR(255) NOT NULL,
                                    birth DATETIME NOT NULL,
                                    gender VARCHAR(10) NOT NULL,
                                    PRIMARY KEY (baby_id),
                                    CONSTRAINT fk_baby_user FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

-- 3. Book 테이블 생성 및 더미 데이터 삽입
CREATE TABLE IF NOT EXISTS book (
                                    book_id INT NOT NULL AUTO_INCREMENT,
                                    user_id INT NOT NULL,
                                    book_inf_id INT,
                                    title VARCHAR(255) NOT NULL,
                                    cover_path VARCHAR(255) NOT NULL,
                                    generated_date DATETIME NOT NULL,
                                    PRIMARY KEY (book_id),
                                    CONSTRAINT fk_book_user FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

-- 4. Baby_Photo 테이블 생성 및 더미 데이터 삽입
CREATE TABLE IF NOT EXISTS baby_Photo (
                                          baby_photo_id INT NOT NULL AUTO_INCREMENT,
                                          baby_id INT NOT NULL,
                                          file_path VARCHAR(255) NOT NULL,
                                          upload_date DATETIME NOT NULL,
                                          PRIMARY KEY (baby_photo_id),
                                          CONSTRAINT fk_baby_photo_baby FOREIGN KEY (baby_id) REFERENCES Baby(baby_id) ON DELETE CASCADE
);

-- 5. Page 테이블 생성 및 더미 데이터 삽입
CREATE TABLE IF NOT EXISTS page (
                                    page_id INT NOT NULL AUTO_INCREMENT,
                                    book_id INT NOT NULL,
                                    page_num INT NOT NULL,
                                    text VARCHAR(255) NOT NULL,
                                    illust_prompt VARCHAR(255) NOT NULL,
                                    image_path VARCHAR(255) NOT NULL,
                                    PRIMARY KEY (page_id),
                                    CONSTRAINT fk_page_book FOREIGN KEY (book_id) REFERENCES Book(book_id) ON DELETE CASCADE
);

-- 6. Today_sum 테이블 생성 및 더미 데이터 삽입
CREATE TABLE IF NOT EXISTS today_sum (
                                         today_id INT NOT NULL AUTO_INCREMENT,
                                         user_id INT NOT NULL,
                                         baby_id INT NULL,
                                         content TEXT NULL,
                                         date DATE NOT NULL,
                                         PRIMARY KEY (today_id),
                                         CONSTRAINT fk_today_sum_user FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE,
                                         CONSTRAINT fk_today_sum_baby FOREIGN KEY (baby_id) REFERENCES Baby(baby_id) ON DELETE CASCADE
);

-- 7. Calendar_Photo 테이블 생성 및 더미 데이터 삽입
CREATE TABLE IF NOT EXISTS calendar_Photo (
                                              calendar_photo_id INT NOT NULL AUTO_INCREMENT,
                                              user_id INT NOT NULL,
                                              baby_id INT NOT NULL,
                                              file_path VARCHAR(255) NULL,
                                              date DATETIME NULL,
                                              PRIMARY KEY (calendar_photo_id),
                                              CONSTRAINT fk_calendar_photo_user FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE,
                                              CONSTRAINT fk_calendar_photo_baby FOREIGN KEY (baby_id) REFERENCES Baby(baby_id) ON DELETE CASCADE
);

-- 8. Calendar 테이블 생성 및 더미 데이터 삽입
CREATE TABLE IF NOT EXISTS calendar (
                                        calendar_id INT NOT NULL AUTO_INCREMENT,
                                        user_id INT NOT NULL,
                                        baby_id INT NOT NULL,
                                        calendar_photo_id INT NULL,
                                        today_id INT NULL,
                                        book_id INT NULL,
                                        title VARCHAR(255) NULL,
                                        start_time DATETIME NULL,
                                        end_time DATETIME NULL,
                                        location VARCHAR(255) NULL,
                                        PRIMARY KEY (calendar_id),
                                        CONSTRAINT fk_calendar_user FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_calendar_baby FOREIGN KEY (baby_id) REFERENCES Baby(baby_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_calendar_calendar_photo FOREIGN KEY (calendar_photo_id) REFERENCES Calendar_Photo(calendar_photo_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_calendar_today_sum FOREIGN KEY (today_id) REFERENCES Today_sum(today_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_calendar_book FOREIGN KEY (book_id) REFERENCES Book(book_id) ON DELETE CASCADE
);


-- 9. Memo 테이블 생성 및 더미 데이터 삽입
CREATE TABLE IF NOT EXISTS memo (
                                    memo_id INT NOT NULL AUTO_INCREMENT,
                                    user_id INT NOT NULL,
                                    today_id INT NULL,
                                    book_id INT NULL,
                                    date DATETIME NULL,
                                    content VARCHAR(255) NULL,
                                    sendToML boolean,
                                    PRIMARY KEY (memo_id),
                                    CONSTRAINT fk_memo_user FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE,
                                    CONSTRAINT fk_memo_today_sum FOREIGN KEY (today_id) REFERENCES Today_sum(today_id) ON DELETE CASCADE,
                                    CONSTRAINT fk_memo_book FOREIGN KEY (book_id) REFERENCES Book(book_id) ON DELETE CASCADE
);

-- 10. Alim 테이블 생성 및 더미 데이터 삽입
CREATE TABLE IF NOT EXISTS alim (
                                    alim_id INT NOT NULL AUTO_INCREMENT,
                                    user_id INT NOT NULL,
                                    baby_id INT NOT NULL,
                                    content VARCHAR(255) NULL,
                                    date DATETIME NOT NULL,
                                    PRIMARY KEY (alim_id),
                                    CONSTRAINT fk_alim_user FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE,
                                    CONSTRAINT fk_alim_baby FOREIGN KEY (baby_id) REFERENCES Baby(baby_id) ON DELETE CASCADE
);

-- 11. Alim_inf 테이블 생성 및 더미 데이터 삽입
CREATE TABLE IF NOT EXISTS alim_inf (
                                        aliminf_id INT NOT NULL AUTO_INCREMENT,
                                        alim_id INT NOT NULL,
                                        user_id INT NOT NULL,
                                        baby_id INT NOT NULL,
                                        today_id INT NULL,
                                        name VARCHAR(255) NULL,
                                        emotion VARCHAR(255) NULL,
                                        health VARCHAR(255) NULL,
                                        nutrition VARCHAR(255) NULL,
                                        activities VARCHAR(255) NULL,
                                        social VARCHAR(255) NULL,
                                        special VARCHAR(255) NULL,
                                        keywords VARCHAR(255) NULL,
                                        diary TEXT NULL,
                                        date DATETIME NOT NULL,
                                        role VARCHAR(255) NULL,
                                        PRIMARY KEY (aliminf_id),
                                        CONSTRAINT fk_alim_inf_alim FOREIGN KEY (alim_id) REFERENCES Alim(alim_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_alim_inf_user FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_alim_inf_baby FOREIGN KEY (baby_id) REFERENCES Baby(baby_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_alim_inf_today_sum FOREIGN KEY (today_id) REFERENCES Today_sum(today_id) ON DELETE CASCADE
);

