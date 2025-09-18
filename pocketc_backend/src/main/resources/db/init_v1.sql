INSERT INTO events (
    event_name, event_description, badge_image_url, event_image_url,
    start_at, end_at, badge_name, badge_description, created_at, updated_at
) VALUES
      ('퍼즐 도전 이벤트',
       '퍼즐을 완성하면 배지를 획득하는 이벤트',
       'https://example.com/badge1.png',
       'https://example.com/event1.png',
       '2025-09-01 09:00:00',
       '2025-09-30 23:59:59',
       '퍼즐 마스터',
       '퍼즐을 1회 이상 완성한 사용자에게 지급',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      ('달리기 챌린지',
       '매일 5km 달리기를 달성하면 배지를 획득하는 이벤트',
       'https://example.com/badge2.png',
       'https://example.com/event2.png',
       '2025-09-05 00:00:00',
       '2025-10-05 23:59:59',
       '러너 배지',
       '30일 동안 꾸준히 달린 사용자에게 지급',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      ('스터디 미션',
       '스터디 방에서 10회 이상 참여 시 배지 지급',
       'https://example.com/badge3.png',
       'https://example.com/event3.png',
       '2025-09-10 00:00:00',
       '2025-12-31 23:59:59',
       '스터디 배지',
       '스터디 방 참여 10회 이상 시 지급',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      ('팀 빌딩 해커톤',
       '팀을 구성해 미션을 해결하면 배지를 받을 수 있습니다',
       'https://example.com/badge4.png',
       'https://example.com/event4.png',
       '2025-09-15 09:00:00',
       '2025-09-20 18:00:00',
       '해커톤 챔피언',
       '최종 제출까지 완수한 팀원 전원에게 지급',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Room 더미 데이터
INSERT INTO rooms (event_id, max_number, status, room_code, created_at, updated_at) VALUES
                                                                                        (1, 5, 'IN_PROGRESS', 'AB12CD34', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                        (1, 10, 'SUCCESS', 'EF56GH78', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                        (2, 8, 'FAILURE', 'IJ90KL12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                        (2, 6, 'IN_PROGRESS', 'MN34OP56', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                        (3, 4, 'SUCCESS', 'QR78ST90', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                        (3, 12, 'IN_PROGRESS', 'UV12WX34', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (
    room_id, name, email, password,
    budget, puzzle_attempts,
    created_at, updated_at
) VALUES
      (NULL, '홍길동', 'hong@test.com', 'pw1234',
       500000, 3,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      (1, '김철수', 'kim@test.com', 'pw5678',
       300000, 5,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      (2, '이영희', 'lee@test.com', 'pw9012',
       450000, 2,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      (NULL, '박민수', 'park@test.com', 'pw3456',
       200000, 4,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      (NULL, '최수진', 'choi@test.com', 'pw7890',
       600000, 1,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--- 유저 1
INSERT INTO reports (report_id, user_id, report_month, monthly_total_expense, monthly_fixed_expense, monthly_budget)
VALUES (1, 1, '2025-06-01', 820000, 300000, 1000000);
INSERT INTO reports (report_id, user_id, report_month, monthly_total_expense, monthly_fixed_expense, monthly_budget)
VALUES (2, 1, '2025-07-01', 910000, 320000, 1100000);
INSERT INTO reports (report_id, user_id, report_month, monthly_total_expense, monthly_fixed_expense, monthly_budget)
VALUES (3, 1, '2025-08-01', 1030000, 330000, 1100000);
INSERT INTO reports (report_id, user_id, report_month, monthly_total_expense, monthly_fixed_expense, monthly_budget)
VALUES (4, 1, '2025-09-01', 760000, 310000, 1000000);

-- 유저 2
INSERT INTO reports (report_id, user_id, report_month, monthly_total_expense, monthly_fixed_expense, monthly_budget)
VALUES (5, 2, '2025-06-01', 650000, 250000, 900000);
INSERT INTO reports (report_id, user_id, report_month, monthly_total_expense, monthly_fixed_expense, monthly_budget)
VALUES (6, 2, '2025-07-01', 720000, 260000, 900000);
INSERT INTO reports (report_id, user_id, report_month, monthly_total_expense, monthly_fixed_expense, monthly_budget)
VALUES (7, 2, '2025-08-01', 840000, 270000, 1000000);
INSERT INTO reports (report_id, user_id, report_month, monthly_total_expense, monthly_fixed_expense, monthly_budget)
VALUES (8, 2, '2025-09-01', 905000, 280000, 1000000);

-- 유저 3
INSERT INTO reports (report_id, user_id, report_month, monthly_total_expense, monthly_fixed_expense, monthly_budget)
VALUES (9, 3, '2025-07-01', 590000, 220000, 800000);
INSERT INTO reports (report_id, user_id, report_month, monthly_total_expense, monthly_fixed_expense, monthly_budget)
VALUES (10, 3, '2025-08-01', 630000, 230000, 850000);
INSERT INTO reports (report_id, user_id, report_month, monthly_total_expense, monthly_fixed_expense, monthly_budget)
VALUES (11, 3, '2025-09-01', 710000, 240000, 900000);


INSERT INTO transactions (
    user_id, transacted_at, amount, merchant_name,
    major_category, sub_category, is_applied, is_fixed,
    created_at, updated_at
) VALUES
      (1, TIMESTAMP '2025-09-01 10:25:00', 5500,  '스타벅스',     1, 11, TRUE,  TRUE,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (1, TIMESTAMP '2025-09-01 13:40:00', 12000, '이마트24',     1, 12, TRUE,  FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (2, TIMESTAMP '2025-09-02 19:15:00', 45000, '배달의민족',   1, 13, FALSE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (3, TIMESTAMP '2025-09-02 08:50:00', 8000,  'GS25',         1, 11, TRUE,  TRUE,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (1, TIMESTAMP '2025-09-03 22:10:00', 65000, '쿠팡',         2, 21, FALSE, TRUE,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (4, TIMESTAMP '2025-09-04 18:40:00', 20000, 'CGV',          3, 31, TRUE,  TRUE,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (2, TIMESTAMP '2025-09-05 14:10:00', 3500,  '투썸플레이스', 1, 14, TRUE,  FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (5, TIMESTAMP '2025-09-06 16:55:00', 78000, '현대백화점',   2, 22, FALSE, TRUE,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (1, TIMESTAMP '2025-09-07 09:30:00', 110000,'하나투어',     4, 41, TRUE,  TRUE,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (3, TIMESTAMP '2025-09-08 12:45:00', 22000, '교보문고',     2, 23, TRUE,  TRUE,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
