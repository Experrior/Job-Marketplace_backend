package main

import (
	"context"
	"encoding/json"
	"flag"
	"fmt"
	"log"
	"net/http"
	"time"
	"sync"

	"chat/services"

	"github.com/gorilla/mux"
	"github.com/gorilla/websocket"
	"gorm.io/driver/postgres"

	"gorm.io/gorm"

)

const (
	writeWait     = 10 * time.Second    // Time allowed to write a message to the client
	maxConnection = 8 * time.Hour       // Maximum allowed time for a connection (8 hours)
	pongWait      = 28800 * time.Second // Time allowed to read the next pong message (8 hours)
	pingPeriod    = (pongWait * 9) / 10 // Ping period to check connection liveness
	randomStrLen  = 10                  // Length of the random string
)


type MetaMessage struct {
	Operation string `json:"operation"`
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


func openDB(dsn string, setLimits bool) (*gorm.DB, error) {

	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		return nil, err
	}
	sqlDB, err := db.DB()
	if setLimits {
		// Get generic database object sql.DB to use its functions
		if err != nil {
			print(err)
			return nil, err
		}
		fmt.Println("Setting db limits: ")
		sqlDB.SetMaxIdleConns(10)
		sqlDB.SetMaxOpenConns(100)
	}

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
	Connections sync.Map
}

func(a *app) GetAllChats(w http.ResponseWriter, r *http.Request) {

	var userId string = r.URL.Query().Get("userId")
	println("[DEBUG]234 "+userId)
	data := a.MessageService.GetAllChats(userId)
	println("[DEBUG]2341 "+data[0].ChatID)
	w.Header().Set("Content-Type", "application/json")

	w.WriteHeader(http.StatusOK)

	if err := json.NewEncoder(w).Encode(data); err != nil {
		http.Error(w, "Unable to encode JSON", http.StatusInternalServerError)
		return
	}
}

func (a *app) HandleWebSocketConn(w http.ResponseWriter, r *http.Request) {
	conn, err := upgrader.Upgrade(w, r, nil)

	if err != nil {
		log.Println("Error upgrading to WebSocket:", err)
		return
	}

	go func(conn *websocket.Conn) {
		defer conn.Close()

		conn.SetReadDeadline(time.Now().Add(pongWait))
		conn.SetPongHandler(func(string) error {
			conn.SetReadDeadline(time.Now().Add(pongWait))
			return nil
		})

		ticker := time.NewTicker(pingPeriod)
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
			log.Println(err)
			return
		}
		var userId = newReceivedMessage.MessageValue.CreatedBy
		a.Connections.Swap(userId, &conn)

		for {
			select {
			case <-ticker.C:
				// Send periodic pings to keep the connection alive
				if err := conn.WriteMessage(websocket.PingMessage, nil); err != nil {
					log.Println("Error sending ping, closing connection:", err)
					a.Connections.Delete(userId)
					return
				}
				fmt.Println("ticker activated for " + assignedString)

			default:
				// Read messages from the WebSocket
				_, msg, err := conn.ReadMessage()
				if err != nil {
					log.Println("Error reading message, closing connection:", err)
					return
				}

				log.Printf("Received message: %s", string(msg))
				log.Printf("[DEBUG] code:1")
				var newReceivedMessage MetaMessage
				err = json.Unmarshal(msg, &newReceivedMessage)
				if err != nil {
					// If there's an error parsing JSON, respond with an error message
					log.Println("Error parsing JSON:", err)
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
						data:= a.MessageService.GetMessages(newReceivedMessage.MessageValue.ChatId)
						log.Println("[DEBUG]5")
						jsonData, _ := json.Marshal(data)
						if err := conn.WriteMessage(websocket.TextMessage, jsonData); err != nil {
							log.Println("Error sending error message:", err)
						}

					} else if newReceivedMessage.Operation == "post" {
						//db query to insert chat message, send msg to all corresponding websockets TODO
						data, _ := a.MessageService.Create(&newReceivedMessage.MessageValue)

						var target_users = a.MessageService.GetUsersByChat(newReceivedMessage.MessageValue.ChatId);
						print("[DEBUG]943")
						for _, target_user := range target_users {
							//send message to each target user
							retrieved_conn, ok := a.Connections.Load(target_user)
							if !ok {
								print("no active connection found for conn_id: %s", target_user)
							}
							conn, ok := retrieved_conn.(*websocket.Conn) // type assertion for value any from sync.Map
							if !ok {
								print("[CRITICAL ERROR] The connection stored in sync.Map is not of type: *websocket.Conn")
							}
							jsonData, err := json.Marshal(newReceivedMessage.MessageValue)
							if err != nil {
								print("[ERROR] Couldn't parse json for newReceivedMessage.MessageValue")
							}
							if err = conn.WriteMessage(websocket.TextMessage, jsonData); err != nil {
								print("[ERROR] Couldn't send message to target, even though connection existed")
							
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
				conn.SetReadDeadline(time.Now().Add(pongWait))
			}
		}
	}(conn)
}

func main() {
	var dsn string
	var port string
	var setLimits bool
	flag.StringVar(&dsn, "dsn", "host=172.17.0.1 user=admin password=test dbname=JobMarketDB port=5432", "PostgreSQL DSN")
	flag.StringVar(&port, "port", "8088", "Service Port")
	flag.BoolVar(&setLimits, "limits", false, "Sets DB limits")
	flag.Parse()

	db, err := openDB(dsn, setLimits)
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
	// userRouter.HandleFunc("/post", application.CreateMessage).Methods(http.MethodPost)
	// http.Handle("/messages/post", r)
	//userRouter.HandleFunc("", application.AddUser).Methods(http.MethodPost)
	http.Handle("/", r)

	fmt.Println("WebSocket server started on :8088 on /ws endpoint")
	if err := http.ListenAndServe(":8088", nil); err != nil {
		log.Fatal("ListenAndServe:", err)
	}
}
