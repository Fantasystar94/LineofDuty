import http from 'k6/http';
import { check } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 50 },
        { duration: '30s', target: 200 },
        { duration: '30s', target: 500 },
        { duration: '30s', target: 1000 },
        { duration: '30s', target: 0 },
    ],
};


export default function () {
    const params = {
    };

    const res = http.get(
        `http://localhost:8080/api/enlistment?page=0&size=100`,
        params
    );

    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}
