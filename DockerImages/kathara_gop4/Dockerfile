FROM kathara/p4

RUN apt update -y
RUN apt upgrade -y
RUN apt install -y golang
RUN go version
RUN apt clean

WORKDIR .
COPY go.mod go.sum .
RUN go mod download
