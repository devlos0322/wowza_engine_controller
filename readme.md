# Wowza stream engine controller
Wowza stream engine에서 제공하는 REST API를 이용하여 incoming stream을 관리하는 서비스입니다. \
서비스의 자원은 incoming stream입니다. \
Wowza stream engine incoming stream을 Create, Delete 하는 기능을 지원합니다.

## API Example
### 1.Incoming stream 추가
#### Request
```shell
curl --location --request POST 'http://localhost:8080/api/incoming_stream' \
--header 'Authorization: Basic d2luaXRlY2g6d2luaXRlY2g=' \
--header 'Content-Type: application/json' \
--data-raw '{
"applicationName": "rnd_test",
"streamFileName": "rnd_test_stream_file",
"mediaCasterType": "applehls"
}'
```

#### Response
```shell
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 24 Aug 2021 09:13:59 GMT

{"success":true,"message":"Stream service가 정상적으로 실행 되었습니다."}
```

### 2.Incoming stream 삭제
#### Request
```shell
curl -i --location --request DELETE 'http://localhost:8080/api/incoming_stream' \
--header 'Authorization: Basic d2luaXRlY2g6d2luaXRlY2g=' \
--header 'Content-Type: application/json' \
--data-raw '{
"applicationName": "rnd_test",
"streamFileName": "rnd_test_stream_file"
}'
```

#### Response
```shell
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 24 Aug 2021 09:21:24 GMT

{"success":true,"message":"Stream service가 정상적으로 중지 되었습니다."}
```
