FROM golang:alpine
RUN mkdir /app 
COPY . /app/
WORKDIR /app
RUN apk add git
RUN go get -u github.com/gorilla/mux
RUN go get -u github.com/go-sql-driver/mysql
RUN go get -u github.com/go-redis/redis
RUN go get -u github.com/gocql/gocql
RUN go build -o main .
RUN adduser -S -D -H -h /app appuser
USER appuser
EXPOSE 8080
CMD ["./main"]
