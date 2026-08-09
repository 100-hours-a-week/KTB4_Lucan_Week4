import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 300,
    duration: '1m',
};

export default function () {
    const response = http.get(
        'http://54.116.56.59/api/posts?team=SAMSUNG&page=0&size=10'
    );

    check(response, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}