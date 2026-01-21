#
# DELIMITER $$
#
# CREATE PROCEDURE insert_2026_tuesday_schedules()
# BEGIN
#     DECLARE year INT DEFAULT 2026;
#     DECLARE month INT DEFAULT 1;
#     DECLARE i INT;
#     DECLARE first_tuesday DATE;
#     DECLARE enlistment_date DATE;
#
#     WHILE month <= 12 DO
#
#             SET first_tuesday =
#                     DATE_ADD(
#                             DATE(CONCAT(year, '-', LPAD(month, 2, '0'), '-01')),
#                             INTERVAL ( (9 - DAYOFWEEK(DATE(CONCAT(year, '-', LPAD(month, 2, '0'), '-01')))) % 7 ) DAY
#                     );
#
#             SET i = 0;
#             WHILE i < 4 DO
#                     SET enlistment_date = DATE_ADD(first_tuesday, INTERVAL i WEEK);
#
#                     INSERT INTO enlistment_schedules
#                     (enlistment_date, capacity, remaining_slots, created_at, modified_at)
#                     VALUES
#                         (enlistment_date, 100, 100, NOW(), NOW());
#
#                     SET i = i + 1;
#                 END WHILE;
#
#             SET month = month + 1;
#         END WHILE;
#
# END$$
#
# DELIMITER ;

CALL insert_2026_tuesday_schedules();
INSERT INTO enlistment_applications
(application_id, application_status, user_id, schedule_id,enlistment_date, created_at, modified_at)
VALUES
    (1, 'PENDING', 1, 5, '2026-02-03', NOW(), NOW()),
    (2, 'PENDING', 2, 5, '2026-02-03', NOW(), NOW()),
    (3, 'PENDING', 3, 5, '2026-02-03', NOW(), NOW()),
    (4, 'PENDING', 4, 5, '2026-02-03', NOW(), NOW()),
    (5, 'PENDING', 5, 5, '2026-02-03', NOW(), NOW()),
    (6, 'PENDING', 6, 5, '2026-02-03', NOW(), NOW()),
    (7, 'PENDING', 7, 5, '2026-02-03', NOW(), NOW()),
    (8, 'PENDING', 8, 5, '2026-02-03', NOW(), NOW()),
    (9, 'PENDING', 9, 5, '2026-02-03', NOW(), NOW()),
    (10, 'PENDING', 10, 5, '2026-02-03', NOW(), NOW());

INSERT INTO deferments
(deferment_id, application_id, user_id, reason, status, requested_until, created_at, modified_at)
VALUES
    (2, 1, 1, '질병', 'ILLNESS', '2026-06-01', NOW(), NOW()),
    (3, 2, 2, '학업', 'ILLNESS', '2026-07-01', NOW(), NOW()),
    (4, 3, 3, '가족 사유', 'ILLNESS', '2026-05-01', NOW(), NOW()),
    (5, 4, 4, '해외 체류', 'ILLNESS', '2026-04-01', NOW(), NOW()),
    (6, 5, 5, '질병', 'ILLNESS', '2026-08-01', NOW(), NOW()),
    (7, 6, 6, '학업', 'ILLNESS', '2026-09-01', NOW(), NOW()),
    (8, 7, 7, '개인 사유', 'ILLNESS', '2026-03-01', NOW(), NOW()),
    (9, 8, 8, '질병', 'ILLNESS', '2026-10-01', NOW(), NOW()),
    (10, 9, 9, '학업', 'ILLNESS', '2026-11-01', NOW(), NOW()),
    (11, 10, 10, '가족 사유', 'ILLNESS', '2026-12-01', NOW(), NOW());


