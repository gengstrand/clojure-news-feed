FROM golang:alpine
RUN mkdir /app 
ADD . /app/
WORKDIR /app 
RUN apk add git
RUN go get -u github.com/prometheus/client_golang/prometheus
RUN go get -u github.com/prometheus/client_golang/prometheus/promauto
RUN go get -u github.com/prometheus/client_golang/prometheus/promhttp
RUN go build -o main .
RUN adduser -S -D -H -h /app appuser
USER appuser
EXPOSE 8000
CMD ["./main"]
