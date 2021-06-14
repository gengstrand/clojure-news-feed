FROM golang:buster
RUN mkdir /app 
COPY . /app/
WORKDIR /app
RUN go get -u github.com/go-oauth2/oauth2/generates
RUN go get -u github.com/go-oauth2/oauth2/manage
RUN go get -u github.com/go-oauth2/oauth2/models
RUN go get -u github.com/go-oauth2/oauth2/server
RUN go get -u github.com/go-oauth2/oauth2/store
RUN go get -u github.com/go-session/session
RUN go get -u github.com/gorilla/websocket
RUN go get -u github.com/go-redis/redis
RUN go get -u github.com/google/uuid
RUN go get -u golang.org/x/net/context
RUN go build -o server .
RUN adduser -S -D -H -h /app appuser
USER appuser
EXPOSE 8080
CMD ["./server"]
