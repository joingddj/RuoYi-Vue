param([string]$v=$(throw "Parameter missing: -v Version"))


docker build  -t 172.16.30.243:5000/uv-data-frontend:$v  .

docker push 172.16.30.243:5000/uv-data-frontend:$v
