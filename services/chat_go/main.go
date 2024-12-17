package main

import (
	"context"
	"encoding/json"
	"flag"
	"fmt"
	"log"
	"net/http"
	"os"
	"sync"
	"time"
    "strings"
	"chat/services"

	"github.com/gorilla/mux"
	"github.com/gorilla/websocket"
	"gorm.io/driver/postgres"

	"gorm.io/gorm"
)

const (
	websocketLifetime = 2 * time.Hour // Time after which we check if the user is still connected, if not then drop connection
	maxIdleDbConnections = 10
	maxOpenDbConnections = 100
)

type MetaMessage struct {
	Operation    string           `json:"operation"`
	MessageValue services.Message `json:"message"`
}

var assignedString string

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

func openDB(dsn string) (*gorm.DB, error) {

	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		return nil, err
	}
	sqlDB, err := db.DB()

	if err != nil {
		print(err)
		return nil, err
	}
	fmt.Println("Setting db limits: ")
	sqlDB.SetMaxIdleConns(maxIdleDbConnections)
	sqlDB.SetMaxOpenConns(maxOpenDbConnections)
	log.Println("maxIdleDbConnections"+ string(maxIdleDbConnections))
	log.Println("maxOpenDbConnections"+ string(maxIdleDbConnections))

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	err = sqlDB.PingContext(ctx)
	if err != nil {
		return nil, err
	}

	return db, nil
}

type app struct {
	MessageService services.MessageService
	Connections    sync.Map
}

func (a *app) GetAllChats(w http.ResponseWriter, r *http.Request) {

	var userId = r.Header.Get("X-User-Id")

	println("[DEBUG]234 " + userId)
	data := a.MessageService.GetAllChats(userId)
	if len(data) > 0 {
        println("[DEBUG]2341 " + data[0].ChatId)
    } else {
        println("[DEBUG]2342 The user has no chats")
    }

	// chatIds := make([]string, len(data))


    // for iter, chat_struct := range data {
    //     chatIds[iter] = chat_struct.ChatId
    // }

	w.Header().Set("Content-Type", "application/json")

	w.WriteHeader(http.StatusOK)

	if err := json.NewEncoder(w).Encode(data); err != nil {
		http.Error(w, "Unable to encode JSON", http.StatusInternalServerError)
		return
	}
}

func (a *app) StartChat(w http.ResponseWriter, r *http.Request) {

	var roles = r.Header.Get("X-User-Roles")
	if !strings.Contains(roles, "ROLE_RECRUITER") {
		http.Error(w, "Unauthorized: insufficient permissions", http.StatusUnauthorized)
		return
	}

	var recruiterId = r.Header.Get("X-User-Id")
	var targetUserId string = r.URL.Query().Get("targetUserId")
	var recruiterName string = r.URL.Query().Get("recruiterName")
	var applicantName string = r.URL.Query().Get("applicantName")

	log.Println("testing startChat")
	log.Println(targetUserId, recruiterName, applicantName)
	log.Println(recruiterId)
	log.Println(targetUserId)
	newChat := a.MessageService.StartChat(recruiterId, targetUserId, recruiterName, applicantName)

	w.Header().Set("Content-Type", "application/json")

	w.WriteHeader(http.StatusOK)

	if err := json.NewEncoder(w).Encode(newChat); err != nil {
		http.Error(w, "Unable to encode JSON", http.StatusInternalServerError)
		return
	}
}


func (a *app) HandleWebSocketConn(w http.ResponseWriter, r *http.Request) {

	var userId string = r.URL.Query().Get("userId")

	conn, err := upgrader.Upgrade(w, r, nil)

	if err != nil {
		log.Println("Error upgrading to WebSocket:", err)
		return
	}


    ticker := time.NewTicker(websocketLifetime)
    defer ticker.Stop()

	go func(conn *websocket.Conn) {
		defer conn.Close()

		// conn.SetReadDeadline(time.Now().Add(pongWait))
		// conn.SetPongHandler(func(string) error {
		// 	conn.SetReadDeadline(time.Now().Add(pongWait))
		// 	return nil
		// })

		ticker := time.NewTicker(websocketLifetime)
		defer ticker.Stop()

		//establish first message
		_, msg, err := conn.ReadMessage()
		if err != nil {
			log.Println("Error reading message, closing connection:", err)
			return
		}
		var newReceivedMessage MetaMessage
		err = json.Unmarshal(msg, &newReceivedMessage)
		if err != nil {
			log.Println("Error while establishig websocket connection")
			log.Println("Erroneus message: "+string(msg))
			log.Println(err)
			return
		}
		a.Connections.Swap(userId, conn)
		defer a.Connections.Delete(userId)
		log.Println("Saved connection by key: ", userId)

		// listen for following messages
		for {
			// channels for websocket signals
			messageChan := make(chan []byte)
			errorChan := make(chan error)

			// start a goroutine for websocket read
			go func() {
				_, msg, err := conn.ReadMessage()
				if err != nil {
					errorChan <- err
					return
				}
				messageChan <- msg
			}()

			select {
				case <-ticker.C:
					log.Println("Websocket timeout reached. Closing the WebSocket connection.")
					conn.Close()
					return

				case msg := <-messageChan:
					ticker.Reset(websocketLifetime)

					var newReceivedMessage MetaMessage
					err = json.Unmarshal(msg, &newReceivedMessage)
					if err != nil {
						// If there's an error parsing JSON, respond with an error message
						log.Println("Error parsing JSON:", err)
						log.Println(newReceivedMessage)
						errorMessage := fmt.Sprintf("Error: Invalid JSON format: %v", err)
						if err := conn.WriteMessage(websocket.TextMessage, []byte(errorMessage)); err != nil {
							log.Println("Error sending error message:", err)
						}
						continue
					} else {
						// process correct message
						log.Println("[DEBUG] code:23")
						log.Println(newReceivedMessage.MessageValue.CreatedBy)
						if newReceivedMessage.Operation == "getAll" {
							var userId string = newReceivedMessage.MessageValue.CreatedBy
							//db query to get chat_id
							data := a.MessageService.GetAllChats(userId)
							println("[DEBUG]6")
							jsonData, _ := json.Marshal(data)
							if err := conn.WriteMessage(websocket.TextMessage, jsonData); err != nil {
								log.Println("Error sending error message:", err)
							}

						} else if newReceivedMessage.Operation == "get" {
							// var chat_id = newReceivedMessage.MessageValue.ChatId
							// db query to get chat_id
							data := a.MessageService.GetMessages(newReceivedMessage.MessageValue.ChatId)
							log.Println("[DEBUG]5")
							jsonData, _ := json.Marshal(data)
							if err := conn.WriteMessage(websocket.TextMessage, jsonData); err != nil {
								log.Println("Error sending error message:", err)
							}

						} else if newReceivedMessage.Operation == "post" {
							log.Println("testing post operation:")
							log.Println(&newReceivedMessage.MessageValue)
							log.Println(newReceivedMessage.MessageValue)
							//db query to insert chat message, send msg to all corresponding websockets TODO
							data, _ := a.MessageService.Create(&newReceivedMessage.MessageValue)

							var target_users = a.MessageService.GetUsersByChat(newReceivedMessage.MessageValue.ChatId)
							print("[DEBUG]943")
							for _, target_user := range target_users {
								//send message to each target user
								log.Println("Trying to retrieve connection by userId: ", target_user)
								retrieved_conn, ok := a.Connections.Load(target_user)
								if !ok {
									log.Printf("no active connection found for conn_id: %s", target_user)
								}
								conn, ok := retrieved_conn.(*websocket.Conn) // type assertion for value any from sync.Map
								if !ok {
									log.Println("[CRITICAL ERROR] The connection stored in sync.Map is not of type: *websocket.Conn")
								}else{
									jsonData, err := json.Marshal(newReceivedMessage.MessageValue)
									if err != nil {
										log.Println("[ERROR] Couldn't parse json for newReceivedMessage.MessageValue")
									}
									if err = conn.WriteMessage(websocket.TextMessage, jsonData); err != nil {
										log.Println("[ERROR] Couldn't send message to target, even though connection existed")
	
									}
								}


							}
							var msgs []services.Message
							msgs = append(msgs, data)
							jsonData, _ := json.Marshal(msgs)
							log.Println("[DEBUG]51")
							log.Println(data)
							if err := conn.WriteMessage(websocket.TextMessage, jsonData); err != nil {
								log.Println("Error sending error message:	", err)
							}
						}
					}
				case err := <-errorChan:
					// Error encountered; log and close connection
					log.Println("Error reading message:", err)
					conn.Close()
					return
			}
		}
	}(conn)
}

func parse_args() string {

	host := os.Getenv("DB_HOST")
	if host == "" {
		host = "172.22.0.2"
	}

	user := os.Getenv("DB_USER")
	if user == "" {
		user = "admin"
	}

	password := os.Getenv("DB_PASSWORD")
	if password == "" {
		password = "test"
	}

	dbname := os.Getenv("DB_NAME")
	if dbname == "" {
		dbname = "JobMarketDB"
	}

	port := os.Getenv("DB_PORT")
	if port == "" {
		port = "5432"
	}

	dsn := flag.String("dsn", fmt.Sprintf("host=%s user=%s password=%s dbname=%s port=%s", host, user, password, dbname, port), "PostgreSQL DSN")
	flag.Parse()
	return *dsn

}

func main() {
	var port string
	var setLimits bool
	// flag.StringVar(&dsn, "dsn", "host=172.22.0.2 user=admin password=test dbname=JobMarketDB port=5432", "PostgreSQL DSN")
	flag.StringVar(&port, "port", "8088", "Service Port")
	flag.BoolVar(&setLimits, "limits", false, "Sets DB limits")
	flag.Parse()
	var dsn = parse_args()
	println("[DEBUG] Starting using dsn: " + dsn)
	db, err := openDB(dsn)
	if err != nil {
		log.Fatalln(err)
	}
	application := app{MessageService: services.NewMessageService(db)}

	// application := services.NewMessageService(db)

	r := mux.NewRouter()

	userRouter := r.PathPrefix("").Subrouter()
	// userRouter.HandleFunc("/ws_old", handleWebSocket)
	userRouter.HandleFunc("/ws", application.HandleWebSocketConn)
	userRouter.HandleFunc("/getUserChats", application.GetAllChats)
	userRouter.HandleFunc("/startChat", application.StartChat)
	// userRouter.HandleFunc("/post", application.CreateMessage).Methods(http.MethodPost)
	// http.Handle("/messages/post", r)
	//userRouter.HandleFunc("", application.AddUser).Methods(http.MethodPost)
	http.Handle("/", r)

	fmt.Println("WebSocket server started on :" + port + " on /ws endpoint")
	if err := http.ListenAndServe(":"+port, nil); err != nil {
		log.Fatal("ListenAndServe:", err)
	}
}

/// TODO change all println to log.something, for better logging (adds datetime, by default)
