FROM golang:buster
RUN mkdir /app 
COPY . /app/
WORKDIR /app
RUN go build -o server .
RUN adduser --system --disabled-password --no-create-home --home /app appuser
RUN chown -R appuser:nogroup /app
USER appuser
EXPOSE 8080
CMD ["./server"]
