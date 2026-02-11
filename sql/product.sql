INSERT INTO products
(name, description, price, stock, status, created_at, modified_at)
VALUES
    (
        '군용 세면도구 세트',
        '입영 시 필수 세면도구를 한 번에 준비할 수 있는 세트입니다.',
        12000,
        50,
        'ON_SALE',
        NOW(6),
        NOW(6)
    ),
    (
        '군용 슬리퍼',
        '내구성이 뛰어난 군용 슬리퍼입니다.',
        9000,
        30,
        'ON_SALE',
        NOW(6),
        NOW(6)
    ),
    (
        '훈련용 수건 세트',
        '흡수력이 뛰어난 훈련용 수건 3매 세트입니다.',
        15000,
        20,
        'ON_SALE',
        NOW(6),
        NOW(6)
    ),
    (
        '군용 양말 세트',
        '장시간 착용에도 편안한 군용 양말 5켤레 세트.',
        18000,
        0,
        'SOLD_OUT',
        NOW(6),
        NOW(6)
    ),
    (
        '군장 파우치',
        '소지품 정리에 유용한 다용도 군장 파우치입니다.',
        22000,
        15,
        'ON_SALE',
        NOW(6),
        NOW(6)
    ),
    (
        '훈련용 장갑',
        '훈련 시 손을 보호해주는 군용 장갑입니다.',
        14000,
        40,
        'ON_SALE',
        NOW(6),
        NOW(6)
    ),
    (
        '군용 보온 텀블러',
        '보온·보냉 기능이 뛰어난 군용 텀블러.',
        25000,
        10,
        'ON_SALE',
        NOW(6),
        NOW(6)
    ),
    (
        '훈련용 귀마개',
        '사격 및 훈련 시 소음을 줄여주는 귀마개입니다.',
        7000,
        100,
        'ON_SALE',
        NOW(6),
        NOW(6)
    ),
    (
        '군용 속옷 세트',
        '흡한속건 기능의 군용 속옷 상하의 세트.',
        28000,
        25,
        'ON_SALE',
        NOW(6),
        NOW(6)
    ),
    (
        '군장 정리 가방',
        '군장 및 개인 물품을 정리하기 좋은 대형 가방.',
        35000,
        0,
        'SOLD_OUT',
        NOW(6),
        NOW(6)
    );

SELECT product_id, name, price, stock, status
FROM products