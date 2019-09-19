FROM golang:alpine as build
RUN mkdir /app 
COPY . /app/
WORKDIR /app
RUN apk add git
RUN go build -o main .
RUN adduser -S -D -H -h /app appuser
# USER appuser
# EXPOSE 8080
# CMD ["./main"]

FROM alpine
RUN adduser -S -D -H -h /app appuser
COPY --from=build /app/main /main
USER appuser
EXPOSE 8080
ENTRYPOINT [ "/main" ]

#ENTRYPOINT [ "dockerize","-timeout","60s","-wait", "tcp://db:3306", "-wait","http://elastic:9200","-wait","tcp://redis:6379", "-wait","tcp://cassandra:9042", "/main" ]