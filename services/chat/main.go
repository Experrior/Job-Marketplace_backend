package main

import (
	_"context"
	_"encoding/json"
	"fmt"
	_"log"
	"net/http"

	// "os"
	_"strconv"

	"github.com/gorilla/websocket"
	_"github.com/jackc/pgx/v5"
)

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

var clients = make(map[*websocket.Conn]bool)
var broadcast = make(chan Message)

type Message struct {
	UserId int `json:"userId"`
	ChatId int `json:"chatId"`
	Message  string `json:"message"`
}

func main() {
	http.HandleFunc("/", homePage)
	http.HandleFunc("/ws", handleConnections)

	go handleMessages()

	fmt.Println("Server started on :8088")
	err := http.ListenAndServe(":8088", nil)
	if err != nil {
		panic("Error starting server: " + err.Error())
	}
}

// func main() {
// 	// urlExample := "postgres://username:password@localhost:5432/database_name"
// 	conn, err := pgx.Connect(context.Background(), "postgres://admin:test@localhost:5432/JobMarketDB")
// 	if err != nil {
// 		fmt.Fprintf(os.Stderr, "Unable to connect to database: %v\n", err)
// 		os.Exit(1)
// 	}
// 	defer conn.Close(context.Background())

// 	// var name []string
// 	rows, _ := conn.Query(context.Background(), "select chat_messages from skills")
// 	numbers, err1 := pgx.CollectRows(rows, pgx.RowTo[int32])

// 	if err1 != nil {
// 		fmt.Fprintf(os.Stderr, "QueryRow failed: %v\n", err)
// 		os.Exit(1)
// 	}

// 	fmt.Println(numbers)
// }


func homePage(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Welcome to the Chat Room!")
}

func handleConnections(w http.ResponseWriter, r *http.Request) {
	conn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		fmt.Println(err)
		return
	}
	defer conn.Close()

	clients[conn] = true

	for {
		var msg []byte
		err := conn.ReadJSON(&msg)
		if err != nil {
			fmt.Println(msg)
			// conn, _ := pgx.Connect(context.Background(), "postgres://admin:test@localhost:5432/JobMarketDB")
			// values := fmt.Sprintf("%d, '%s', %d", msg.ChatId,  msg.Message, msg.UserId)
			// println(msg.Message)
			// println("INSERT INTO chat_messages(chat_id,content,created_by) VALUES("+values+");")
			// _, err := conn.Exec(context.Background(), "INSERT INTO chat_messages (chat_id, content, created_by) VALUES("+values+");")
			// if err != nil {
			// 	log.Fatal(err)
			// }else {
			// 	rows, _ := conn.Query(context.Background(), "select chat_id from chat_messages where chatId="+strconv.Itoa(int(msg.ChatId)))
			// 	id, _ := pgx.CollectRows(rows, pgx.RowTo[int32])
			// 	println(id)
			// }
			return
		}

		// broadcast <- msg
	}
}

func handleMessages() {
	for {
		msg := <-broadcast

		for client := range clients {
			err := client.WriteJSON(msg)
			if err != nil {
				fmt.Println(err)
				client.Close()
				delete(clients, client)
			}
		}
	}
}