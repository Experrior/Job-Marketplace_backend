FROM golang:1.23.2

WORKDIR /app

COPY chat_go/ .
RUN go mod download

COPY *.go ./

RUN CGO_ENABLED=0 GOOS=linux go build -o /chat_service

EXPOSE 8088

CMD ["/chat_service"]