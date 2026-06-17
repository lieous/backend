-- 1. Таблица пользователей
CREATE TABLE users (
                       id VARCHAR(36) PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255), -- Из твоей V2
                       profile_data JSONB,
                       settings JSONB,
                       created_at BIGINT NOT NULL
);

-- 2. Метрики дня
CREATE TABLE day_metrics (
                             id VARCHAR(36) PRIMARY KEY,
                             user_id VARCHAR(36) NOT NULL REFERENCES users(id),
                             date_str VARCHAR(10) NOT NULL,
                             steps INT DEFAULT 0,
                             water_liters REAL DEFAULT 0.0,
                             energy_kcal_consumed INT DEFAULT 0,
                             energy_kcal_burned INT DEFAULT 0,
                             sleep_minutes INT DEFAULT 0,
                             protein_grams INT DEFAULT 0,
                             carb_grams INT DEFAULT 0,
                             fat_grams INT DEFAULT 0,
                             pulse_bpm INT,
                             glucose REAL,
                             systolic_bp INT,
                             diastolic_bp INT,
                             sleep_quality VARCHAR(50),
                             mood_emotion VARCHAR(50),
                             diary_text TEXT,
                             updated_at BIGINT NOT NULL,
                             deleted_at BIGINT, -- <--- ВОТ ЭТА КОЛОНКА БЫЛА ПРОПУЩЕНА
                             CONSTRAINT unique_user_date UNIQUE (user_id, date_str)
);

-- 3. Привычки (Справочник пользователя)
CREATE TABLE habits_dict (
                             id VARCHAR(36) PRIMARY KEY,
                             user_id VARCHAR(36) NOT NULL REFERENCES users(id),
                             name VARCHAR(255) NOT NULL,
                             icon_name VARCHAR(100),
                             color_hex VARCHAR(9),
                             is_active BOOLEAN DEFAULT TRUE,
                             updated_at BIGINT NOT NULL
);

-- 4. Привычки (Логи)
CREATE TABLE habit_logs (
                            habit_id VARCHAR(36) NOT NULL,
                            user_id VARCHAR(36) NOT NULL REFERENCES users(id),
                            date_str VARCHAR(10) NOT NULL,
                            is_completed BOOLEAN DEFAULT FALSE,
                            updated_at BIGINT NOT NULL,
                            PRIMARY KEY (habit_id, user_id, date_str)
);

-- 5. Задачи и цели
CREATE TABLE goals (
                       id VARCHAR(36) PRIMARY KEY,
                       user_id VARCHAR(36) NOT NULL REFERENCES users(id),
                       title VARCHAR(255) NOT NULL,
                       date_str VARCHAR(10) NOT NULL,
                       time_str VARCHAR(5),
                       is_completed BOOLEAN DEFAULT FALSE,
                       updated_at BIGINT NOT NULL,
                       deleted_at BIGINT
);

-- ==========================================
-- ГЛОБАЛЬНЫЕ СПРАВОЧНИКИ (Без user_id)
-- ==========================================

-- 6. Симптомы (Глобальный Справочник)
CREATE TABLE symptoms_dict (
                               id BIGINT PRIMARY KEY,
                               name VARCHAR(255) NOT NULL,
                               icon_name VARCHAR(100) NOT NULL,
                               is_active BOOLEAN DEFAULT TRUE, -- ДОБАВЛЕНО
                               updated_at BIGINT NOT NULL      -- ДОБАВЛЕНО
);

-- 7. Настроения (Глобальный Справочник) - НОВАЯ ТАБЛИЦА
CREATE TABLE moods_dict (
                            id VARCHAR(50) PRIMARY KEY,     -- 'happy', 'sad' и т.д.
                            name VARCHAR(255) NOT NULL,
                            icon_name VARCHAR(100) NOT NULL,
                            is_active BOOLEAN DEFAULT TRUE,
                            updated_at BIGINT NOT NULL
);

-- 8. Симптомы (Логи юзера)
CREATE TABLE symptom_logs (
                              id VARCHAR(36) PRIMARY KEY,
                              symptom_id BIGINT NOT NULL REFERENCES symptoms_dict(id),
                              user_id VARCHAR(36) NOT NULL REFERENCES users(id),
                              date_str VARCHAR(255) NOT NULL,
                              is_active BOOLEAN DEFAULT TRUE, -- ДОБАВЛЕНО (вместо deleted_at)
                              updated_at BIGINT NOT NULL
);

-- ==========================================
-- ПЕРВИЧНОЕ НАПОЛНЕНИЕ БД БЭКЕНДА
-- ==========================================
-- Теперь при старте БД сразу готова отдавать эти данные телефонам

-- ==========================================
-- ПЕРВИЧНОЕ НАПОЛНЕНИЕ БД БЭКЕНДА (РУССКИЙ ЯЗЫК)
-- ==========================================

-- Настроения (ID - английский slug для логики/иконок, NAME - для UI и Нейросети)
INSERT INTO moods_dict (id, name, icon_name, is_active, updated_at) VALUES
                                                                        ('happy', 'Счастье', 'ic_mood_happy', true, 1700000000000),
                                                                        ('radiant', 'Восторг', 'ic_mood_radiant', true, 1700000000000),
                                                                        ('peaceful', 'Спокойствие', 'ic_mood_peaceful', true, 1700000000000),
                                                                        ('neutral', 'Нормально', 'ic_mood_neutral', true, 1700000000000),
                                                                        ('sad', 'Грусть', 'ic_mood_sad', true, 1700000000000),
                                                                        ('anxious', 'Тревога', 'ic_mood_anxious', true, 1700000000000),
                                                                        ('stressed', 'Стресс', 'ic_mood_stressed', true, 1700000000000),
                                                                        ('angry', 'Злость', 'ic_mood_angry', true, 1700000000000),
                                                                        ('apathetic', 'Апатия', 'ic_mood_apathetic', true, 1700000000000),
                                                                        ('sleepy', 'Усталость', 'ic_mood_sleepy', true, 1700000000000);

-- Симптомы (ID - цифры, NAME - для UI и Нейросети)
INSERT INTO symptoms_dict (id, name, icon_name, is_active, updated_at) VALUES
                                                                           (1, 'Головная боль', 'ic_headache', true, 1700000000000),
                                                                           (2, 'Слабость', 'ic_fatigue', true, 1700000000000),
                                                                           (3, 'Тошнота', 'ic_nausea', true, 1700000000000),
                                                                           (4, 'Боль в животе', 'ic_cramps', true, 1700000000000),
                                                                           (5, 'Учащенное сердцебиение', 'ic_heart', true, 1700000000000),
                                                                           (6, 'Боль в спине', 'ic_back_pain', true, 1700000000000),
                                                                           (7, 'Головокружение', 'ic_dizziness', true, 1700000000000),
                                                                           (8, 'Кашель', 'ic_cough', true, 1700000000000),
                                                                           (9, 'Насморк', 'ic_runny_nose', true, 1700000000000),
                                                                           (10, 'Бессонница', 'ic_insomnia', true, 1700000000000);