FROM golang:alpine as build
# We use a two-stage build.
# It gives us a clean build environment and a smaller image to distribute.
RUN mkdir /app 
COPY . /app/
WORKDIR /app
RUN apk add git
RUN go build -o main .
RUN adduser -S -D -H -h /app appuser


FROM alpine
RUN adduser -S -D -H -h /app appuser
COPY --from=build /app/main /main
USER appuser
EXPOSE 8080
ENTRYPOINT [ "/main" ]