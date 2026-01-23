import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 1000,
    iterations: 1000,
};

export default function () {
    sleep(Math.random() * 0.01); // 0~10ms

    const url = 'http://localhost:8080/api/test/enlistment';
    const payload = JSON.stringify({ scheduleId: 20 });
    const userId = Math.floor(Math.random() * 25000) + 1;

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-TEST-USER-ID': String(userId),
        },
    };

    let res;
    let maxRetries = 1;
    let attempt = 0;

    while (attempt < maxRetries) {
        res = http.post(url, payload, params);

        const success = check(res, {
            'status is 200 or 201 or 409': (r) =>
                r.status === 200 || r.status === 201 || r.status === 409,
            }
        );

        if (success) {
            break; // 성공했으면 루프 탈출
        }

        attempt++;
    }
}
