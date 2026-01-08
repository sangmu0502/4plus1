import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 20,
    duration: '30s',
};

const BASE_URL = 'http://host.docker.internal:8080/api/songs';

export default function () {
    const res = http.get(`${BASE_URL}/top/v2`);

    check(res, {
        'V2 status is 200': (r) => r.status === 200,
    });

    sleep(1);
}