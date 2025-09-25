-- User
INSERT INTO users (
    room_id, name, email, password, profile_image_url, budget, puzzle_attempts, created_at, updated_at
) VALUES
      (NULL,
       '홍길동',
       'hong@example.com',
       'password123',
       'https://example.com/profiles/user1.png',
       100000,
       20,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP),

      (NULL,
       '김철수',
       'kim@example.com',
       'password123',
       'https://example.com/profiles/user2.png',
       200000,
       1,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP),

      (NULL,
       '이영희',
       'lee@example.com',
       'password123',
       'https://example.com/profiles/user3.png',
       150000,
       2,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP),

      (NULL,
       '박민수',
       'park@example.com',
       'password123',
       'https://example.com/profiles/user4.png',
       50000,
       0,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP),

      (NULL,
       '최지우',
       'choi@example.com',
       'password123',
       'https://example.com/profiles/user5.png',
       300000,
       3,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP);


-- Event
INSERT INTO events (
    event_name, event_description, badge_image_url, event_image_url,
    start_at, end_at, badge_name, badge_description, created_at, updated_at
) VALUES
      ('STREAK', 'STREAK',
       'https://example.com/badges/streak1.png',
       'https://example.com/events/streak1.png',
       '2025-09-01 00:00:00', '2099-12-31 23:59:59',
       'streak1', '1원',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      ('STREAK', 'STREAK',
       'https://example.com/badges/streak2.png',
       'https://example.com/events/streak2.png',
       '2025-09-01 00:00:00', '2099-12-31 23:59:59',
       'streak2', '5원',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      ('STREAK', 'STREAK',
       'https://example.com/badges/streak3.png',
       'https://example.com/events/streak3.png',
       '2025-09-01 00:00:00', '2099-12-31 23:59:59',
       'streak3', '10원',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      ('STREAK', 'STREAK',
       'https://example.com/badges/streak4.png',
       'https://example.com/events/streak4.png',
       '2025-09-01 00:00:00', '2099-12-31 23:59:59',
       'streak4', '50원',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      ('STREAK', 'STREAK',
       'https://example.com/badges/streak5.png',
       'https://example.com/events/streak5.png',
       '2025-09-01 00:00:00', '2099-12-31 23:59:59',
       'streak5', '100원',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      ('STREAK', 'STREAK',
       'https://example.com/badges/streak6.png',
       'https://example.com/events/streak6.png',
       '2025-09-01 00:00:00', '2099-12-31 23:59:59',
       'streak6', '500원',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      ('STREAK', 'STREAK',
       'https://example.com/badges/streak7.png',
       'https://example.com/events/streak7.png',
       '2025-09-01 00:00:00', '2099-12-31 23:59:59',
       'streak7', '1000원',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      ('STREAK', 'STREAK',
       'https://example.com/badges/streak8.png',
       'https://example.com/events/streak8.png',
       '2025-09-01 00:00:00', '2099-12-31 23:59:59',
       'streak8', '5000원',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      ('STREAK', 'STREAK',
       'https://example.com/badges/streak9.png',
       'https://example.com/events/streak9.png',
       '2025-09-01 00:00:00', '2099-12-31 23:59:59',
       'streak9', '10000원',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      ('EVENT', 'EVENT',
       'https://example.com/badges/event-america.png',
       'https://example.com/events/event-america.png',
       '2025-09-01 00:00:00', '2099-12-31 23:59:59',
       'event-america', '1센트',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Follower
INSERT INTO follows (follower_id, followee_id, created_at, updated_at) VALUES
                                                                           (1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                           (1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                           (1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                           (1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Badge
INSERT INTO badges (event_id, user_id, level, created_at, updated_at) VALUES
                                                                          (1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                          (2, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                          (3, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Streak
INSERT INTO streaks (date, user_id, mission_completed_count, spent_amount, status) VALUES
                                                                                       ('2025-08-27', 1, 0, 0, FALSE),
                                                                                       ('2025-08-28', 1, 3, 10000, TRUE),
                                                                                       ('2025-08-29', 1, 1, 10000, TRUE),
                                                                                       ('2025-08-30', 1, 0, 0, FALSE),
                                                                                       ('2025-08-31', 1, 2, 0, TRUE),
                                                                                       ('2025-09-01', 1, 2, 1000, TRUE),
                                                                                       ('2025-09-02', 1, 2, 10000, TRUE),
                                                                                       ('2025-09-03', 1, 1, 10000, TRUE),
                                                                                       ('2025-09-04', 1, 2, 5000, TRUE),
                                                                                       ('2025-09-05', 1, 0, 0, FALSE),
                                                                                       ('2025-09-06', 1, 0, 0, FALSE),
                                                                                       ('2025-09-07', 1, 1, 0, TRUE),
                                                                                       ('2025-09-08', 1, 0, 0, FALSE),
                                                                                       ('2025-09-09', 1, 3, 5000, TRUE),
                                                                                       ('2025-09-10', 1, 1, 10000, TRUE),
                                                                                       ('2025-09-11', 1, 2, 5000, TRUE),
                                                                                       ('2025-09-12', 1, 0, 0, FALSE),
                                                                                       ('2025-09-13', 1, 1, 0, TRUE),
                                                                                       ('2025-09-14', 1, 3, 5000, TRUE),
                                                                                       ('2025-09-15', 1, 3, 0, TRUE),
                                                                                       ('2025-09-16', 1, 0, 0, FALSE),
                                                                                       ('2025-09-17', 1, 0, 0, FALSE),
                                                                                       ('2025-09-18', 1, 3, 1000, TRUE),
                                                                                       ('2025-09-19', 1, 3, 10000, TRUE),
                                                                                       ('2025-09-20', 1, 1, 10000, TRUE),
                                                                                       ('2025-09-21', 1, 1, 0, TRUE),
                                                                                       ('2025-09-22', 1, 1, 5000, TRUE),
                                                                                       ('2025-09-23', 1, 1, 0, TRUE),
                                                                                       ('2025-09-24', 1, 0, 0, FALSE),
                                                                                       ('2025-09-25', 1, 1, 0, TRUE);


INSERT INTO reports (user_id, report_month, monthly_total_expense, monthly_fixed_expense, monthly_budget)
VALUES
    (1, '2025-04-01', 1200000, 300000, 1500000),
    (1, '2025-05-01', 1350000, 310000, 1500000),
    (1, '2025-06-01', 1420000, 320000, 1600000),
    (1, '2025-07-01', 1280000, 305000, 1600000),
    (1, '2025-08-01', 1500000, 330000, 1700000),
    (1, '2025-09-01', 980000,  280000, 1500000);