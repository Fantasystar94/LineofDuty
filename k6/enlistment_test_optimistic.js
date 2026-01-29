import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

export const options = {
    vus: 1000,
    iterations: 1000,
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.05'],
    },
};

export let successRate = new Rate('success_rate');
export let conflictRate = new Rate('conflict_rate');
export let errorRate = new Rate('error_rate');


export default function () {
    sleep(Math.random() * 0.01);

    const url = 'http://localhost:8080/api/test/enlistment/optimistic';
    const payload = JSON.stringify({ scheduleId: 19 });
    const userId = Math.floor(Math.random() * 25000) + 1;

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-TEST-USER-ID': String(userId),
        },
    };

    const res = http.post(url, payload, params);

    if (res.status === 200 || res.status === 201) {
        successRate.add(1);
    } else if (res.status === 409) {
        conflictRate.add(1);
    } else {
        errorRate.add(1);
    }

    check(res, {
        'status valid': (r) =>
            r.status === 200 ||
            r.status === 201 ||
            r.status === 409,
    });
}
