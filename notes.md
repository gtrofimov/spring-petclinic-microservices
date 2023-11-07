curl -X 'POST' 'http://34.211.11.203:8080/em/api/v3/environments/32/coverage/runtime_coverage_ac10_20231106_2148_0' -H 'accept: application/json' -H 'Content-Type: application/json' -u demo:demo-user -d '{"sessionTag": "string", "analysisType": "UNIT_TEST"}'




curl -iv --raw -X "POST" "http://34.211.11.203:8080/em/api/v3/environments/32/coverage/baselines/foo" -u "demo:demo-user" -H "accept: application/json" -H "Content-Type: application/json" -d ""

curl -iv --raw -X 'POST' -H 'accept: application/json' -H 'Content-Type: application/json' -u 'demo:demo-user' -d '"string"' 'http://34.211.11.203:8080/em/api/v3/environments/32/coverage/baselines/foo'



curl -X 'GET' -u 'demo:demo-user' 'http://localhost:8080/em/api/v3/environments/32/coverage/impactedTests?baselineBuildId=spring-petclinic-microservices-20231107055601' -H 'accept: application/json'

curl -X 'GET' -u 'demo:demo-user' -H 'accept: application/json' 'http://34.211.11.203:8080/em/api/v3/environments/32/coverage/baselines'

curl -X 'GET' -u 'demo:demo-user' -H 'accept: application/json' 'http://localhost:8080/em/api/v3/environments/32/coverage/baselines'