FROM golang:alpine
RUN mkdir /app 
COPY . /app/
WORKDIR /app
RUN apk add git
RUN go build -o main .
RUN adduser -S -D -H -h /app appuser
USER appuser
EXPOSE 8080
CMD ["./main"]
