CREATE DATABASE IF NOT EXISTS mongeul_<dev|prod>;

USE mongeul_<dev|prod>;

-- 1. users 테이블 생성
CREATE TABLE IF NOT EXISTS users (
                                    user_id INT NOT NULL AUTO_INCREMENT,
                                    name VARCHAR(255) NOT NULL,
                                    email VARCHAR(255) NOT NULL,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    privacy_policy_accepted VARCHAR(255),
                                    new_user boolean,
                                    PRIMARY KEY (user_id)
);


-- 2. babies 테이블 생성
CREATE TABLE IF NOT EXISTS babies (
                                    baby_id INT NOT NULL AUTO_INCREMENT,
                                    user_id INT NOT NULL,
                                    baby_name VARCHAR(255) NOT NULL,
                                    birth DATETIME NOT NULL,
                                    gender VARCHAR(10) NOT NULL,
                                    PRIMARY KEY (baby_id),
                                    CONSTRAINT fk_babies_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. books 테이블 생성
CREATE TABLE IF NOT EXISTS books (
                                    book_id INT NOT NULL AUTO_INCREMENT,
                                    user_id INT NOT NULL,
                                    book_inf_id INT,
                                    title VARCHAR(255) NOT NULL,
                                    cover_path VARCHAR(255) NOT NULL,
                                    generated_date DATETIME NOT NULL,
                                    PRIMARY KEY (book_id),
                                    CONSTRAINT fk_books_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 4. baby_photos 테이블 생성
CREATE TABLE IF NOT EXISTS baby_photos (
                                          baby_photo_id INT NOT NULL AUTO_INCREMENT,
                                          baby_id INT NOT NULL,
                                          file_path VARCHAR(255) NOT NULL,
                                          upload_date DATETIME NOT NULL,
                                          PRIMARY KEY (baby_photo_id),
                                          CONSTRAINT fk_baby_photos_baby FOREIGN KEY (baby_id) REFERENCES babies(baby_id) ON DELETE CASCADE
);

-- 5. pages 테이블 생성
CREATE TABLE IF NOT EXISTS pages (
                                    page_id INT NOT NULL AUTO_INCREMENT,
                                    book_id INT NOT NULL,
                                    page_num INT NOT NULL,
                                    text VARCHAR(255) NOT NULL,
                                    illust_prompt VARCHAR(255) NOT NULL,
                                    image_path VARCHAR(255) NOT NULL,
                                    PRIMARY KEY (page_id),
                                    CONSTRAINT fk_pages_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
);

-- 6. today_sums 테이블 생성
CREATE TABLE IF NOT EXISTS today_sums (
                                         today_id INT NOT NULL AUTO_INCREMENT,
                                         user_id INT NOT NULL,
                                         baby_id INT NULL,
                                         content TEXT NULL,
                                         date DATE NOT NULL,
                                         PRIMARY KEY (today_id),
                                         CONSTRAINT fk_today_sums_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                         CONSTRAINT fk_today_sums_baby FOREIGN KEY (baby_id) REFERENCES babies(baby_id) ON DELETE CASCADE
);

-- 7. calendar_photos 테이블 생성
CREATE TABLE IF NOT EXISTS calendar_photos (
                                              calendar_photo_id INT NOT NULL AUTO_INCREMENT,
                                              user_id INT NOT NULL,
                                              baby_id INT NOT NULL,
                                              file_path VARCHAR(255) NULL,
                                              date DATETIME NULL,
                                              PRIMARY KEY (calendar_photo_id),
                                              CONSTRAINT fk_calendar_photos_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                              CONSTRAINT fk_calendar_photos_baby FOREIGN KEY (baby_id) REFERENCES babies(baby_id) ON DELETE CASCADE
);

-- 8. calendars 테이블 생성
CREATE TABLE IF NOT EXISTS calendars (
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
                                        CONSTRAINT fk_calendars_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_calendars_baby FOREIGN KEY (baby_id) REFERENCES babies(baby_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_calendars_calendar_photo FOREIGN KEY (calendar_photo_id) REFERENCES calendar_photos(calendar_photo_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_calendars_today_sum FOREIGN KEY (today_id) REFERENCES today_sums(today_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_calendars_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
);


-- 9. memos 테이블 생성
CREATE TABLE IF NOT EXISTS memos (
                                    memo_id INT NOT NULL AUTO_INCREMENT,
                                    user_id INT NOT NULL,
                                    today_id INT NULL,
                                    book_id INT NULL,
                                    date DATETIME NULL,
                                    content VARCHAR(255) NULL,
                                    send_to_ml boolean,
                                    PRIMARY KEY (memo_id),
                                    CONSTRAINT fk_memos_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                    CONSTRAINT fk_memos_today_sum FOREIGN KEY (today_id) REFERENCES today_sums(today_id) ON DELETE CASCADE,
                                    CONSTRAINT fk_memos_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
);

-- 10. alims 테이블 생성
CREATE TABLE IF NOT EXISTS alims (
                                    alim_id INT NOT NULL AUTO_INCREMENT,
                                    user_id INT NOT NULL,
                                    baby_id INT NOT NULL,
                                    content VARCHAR(255) NULL,
                                    date DATETIME NOT NULL,
                                    PRIMARY KEY (alim_id),
                                    CONSTRAINT fk_alims_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                    CONSTRAINT fk_alims_baby FOREIGN KEY (baby_id) REFERENCES babies(baby_id) ON DELETE CASCADE
);

-- 11. alim_infs 테이블 생성
CREATE TABLE IF NOT EXISTS alim_infs (
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
                                        CONSTRAINT fk_alim_infs_alim FOREIGN KEY (alim_id) REFERENCES alims(alim_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_alim_infs_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_alim_infs_baby FOREIGN KEY (baby_id) REFERENCES babies(baby_id) ON DELETE CASCADE,
                                        CONSTRAINT fk_alim_infs_today_sum FOREIGN KEY (today_id) REFERENCES today_sums(today_id) ON DELETE CASCADE
);
