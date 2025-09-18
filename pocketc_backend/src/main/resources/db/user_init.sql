-- users 테이블에 초기 3명 사용자 데이터 삽입
INSERT INTO users (room_id, name, email, password, budget, puzzle_attempts, created_at, updated_at)
VALUES
    (NULL, 'robo', 'alice@example.com', '123', 50000, 3, NOW(), NOW()),
    (NULL, 'dijk', 'bob@example.com', '123', 70000, 2, NOW(), NOW()),
    (NULL, 'alison', 'charlie@example.com', '123', 60000, 1, NOW(), NOW());
