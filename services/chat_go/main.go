package main

import (
	"context"
	"database/sql"
	"encoding/json"
	"flag"
	"fmt"
	"log"
	"math/rand"
	"net/http"
	"time"
	"sync"

	"chat/services"

	"github.com/gorilla/mux"
	"github.com/gorilla/websocket"
	"gorm.io/driver/postgres"

	"gorm.io/gorm"
	// "gorm.io/gorm/logger"
)

const (
	writeWait     = 10 * time.Second    // Time allowed to write a message to the client
	maxConnection = 8 * time.Hour       // Maximum allowed time for a connection (8 hours)
	pongWait      = 28800 * time.Second // Time allowed to read the next pong message (8 hours)
	pingPeriod    = (pongWait * 9) / 10 // Ping period to check connection liveness
	randomStrLen  = 10                  // Length of the random string
)

// type Message struct {
// 	MessageId int    `json:"id" gorm:"primaryKey"`
// 	ChatId    int    `json:"chatId" gorm:"references:Chat"`
// 	Content   string `json:"content"`

// 	CreatedBy int `json:"createdBy" gorm:"references:app_users"`
// 	ReadBy    string `json:"readBy" gorm:"references:app_users"`
// 	DeletedBy string `json:"deletedBy" gorm:"references:app_users"`

// 	CreatedAt time.Time `json:"createdAt" gorm:"default:CURRENT_TIMESTAMP()"`
// 	UpdatedAt time.Time `json:"updatedAt" gorm:"default:CURRENT_TIMESTAMP()"`
// }

type MetaMessage struct {
	Operation string `json:"operation"`
	MessageValue services.Message `json:"message"`
}

// type app_users struct {
// 	UserID           int       `json:"user_id" gorm:"primaryKey"`
// 	CompanyID        int       `json:"company_id" gorm:"references:Company"`
// 	Email            string    `json:"email" gorm:"unique"`
// 	FirstName        string    `json:"first_name"`
// 	LastName         string    `json:"last_name"`
// 	Phone            string    `json:"phone"`
// 	Role             string    `json:"role"`
// 	IsBlocked        bool      `json:"is_blocked"`
// 	EmailVerified    bool      `json:"email_verified"`
// 	EmployeeVerified bool      `json:"employee_verified"`
// 	CreatedAt        time.Time `json:"created_at" gorm:"default:CURRENT_TIMESTAMP"`
// 	PasswordHash     string    `json:"password_hash"`
// }

var assignedString string

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

// Function to generate a random string
func generateRandomString(n int) string {
	letters := []rune("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
	rand.Seed(time.Now().UnixNano())
	s := make([]rune, n)
	for i := range s {
		s[i] = letters[rand.Intn(len(letters))]
	}
	return string(s)
}

// WebSocket handler, launched in a goroutine for each connection
// func handleWebSocket(w http.ResponseWriter, r *http.Request) {
// 	conn, err := upgrader.Upgrade(w, r, nil)
// 	if err != nil {
// 		log.Println("Error upgrading to WebSocket:", err)
// 		return
// 	}

// 	// Start a new goroutine for each WebSocket connection
// 	go func(conn *websocket.Conn) {
// 		defer conn.Close()

// 		assignedString = generateRandomString(randomStrLen)

// 		// Timer for inactivity (8 hours)
// 		conn.SetReadDeadline(time.Now().Add(pongWait))
// 		conn.SetPongHandler(func(string) error {
// 			conn.SetReadDeadline(time.Now().Add(pongWait))
// 			return nil
// 		})

// 		ticker := time.NewTicker(pingPeriod)
// 		defer ticker.Stop()

// 		// Write the assigned random string to the client once connected
// 		message := fmt.Sprintf("Your assigned string is: %s", assignedString)
// 		if err := conn.WriteMessage(websocket.TextMessage, []byte(message)); err != nil {
// 			log.Println("Error writing initial message:", err)
// 			return
// 		}

// 		// Main loop to keep the connection alive and manage messages
// 		for {
// 			select {
// 			case <-ticker.C:
// 				// Send periodic pings to keep the connection alive
// 				if err := conn.WriteMessage(websocket.PingMessage, nil); err != nil {
// 					log.Println("Error sending ping, closing connection:", err)
// 					return
// 				}
// 				fmt.Println("ticker activated for " + assignedString)

// 			default:
// 				// Read messages from the WebSocket
// 				_, msg, err := conn.ReadMessage()
// 				if err != nil {
// 					log.Println("Error reading message, closing connection:", err)
// 					return
// 				}

// 				// Log the message received from the client
// 				log.Printf("Received message: %s", string(msg))

// 				// Try to parse the message as JSON into the Message struct
// 				var receivedMessage Message
// 				err = json.Unmarshal(msg, &receivedMessage)
// 				if err != nil {
// 					// If there's an error parsing JSON, respond with an error message
// 					log.Println("Error parsing JSON:", err)
// 					errorMessage := fmt.Sprintf("Error: Invalid JSON format: %v", err)
// 					if err := conn.WriteMessage(websocket.TextMessage, []byte(errorMessage)); err != nil {
// 						log.Println("Error sending error message:", err)
// 					}
// 					continue
// 				} else {
// 					log.Println(receivedMessage.Content, receivedMessage.CreatedBy, receivedMessage.ChatId)
// 				}

// 				// Respond back with the assigned random string
// 				response := fmt.Sprintf("Your assigned string is: %s", assignedString)
// 				if err := conn.WriteMessage(websocket.TextMessage, []byte(response)); err != nil {
// 					log.Println("Error writing response message:", err)
// 					return
// 				}

// 				// Refresh the timeout (extend connection lifespan)
// 				conn.SetReadDeadline(time.Now().Add(pongWait))
// 			}
// 		}
// 	}(conn) // Pass the WebSocket connection to the goroutine
// }

// func aaa(){
//     go func(msg string) {
// 		defer println("a")
//         fmt.Println(msg)
// 		defer println("b")
//     }("going")
// }

// func (a *app) AddUser(w http.ResponseWriter, r *http.Request) {
// 	var user services.User

// 	err := json.NewDecoder(r.Body).Decode(&user)
// 	if err != nil {
// 		ReturnError(w, err)
// 		return
// 	}

// 	err = a.UserService.Create(&user)
// 	if err != nil {
// 		ReturnError(w, err)
// 		return
// 	}
// 	userJson, err := json.Marshal(user)
// 	if err != nil {
// 		ReturnError(w, err)
// 		return
// 	}

// 	w.Header().Add("Content-Type", "application/json")
// 	_, _ = w.Write(userJson)
// 	return
// }

func openDB(dsn string, setLimits bool) (*sql.DB, error) {
	db, err := sql.Open("postgresql", dsn)
	if err != nil {
		return nil, err
	}

	if setLimits {
		fmt.Println("setting limits")
		db.SetMaxOpenConns(5)
		db.SetMaxIdleConns(5)
	}

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	err = db.PingContext(ctx)
	if err != nil {
		return nil, err
	}

	return db, nil
}

type app struct {
	MessageService services.MessageService
	Connections sync.Map 
}

// func (a *app) CreateMessage(w http.ResponseWriter, r *http.Request) {
// 	var message services.Message
// 	println("asdasdasdasdasd")
// 	err := json.NewDecoder(r.Body).Decode(&message)
// 	print("etets")
// 	err, _ = a.MessageService.Create(&message)
// 	if err != nil {
// 		return
// 	}
// 	userJson, err := json.Marshal(message)
// 	w.Header().Add("Content-Type", "application/json")
// 	_, _ = w.Write(userJson)
// 	print("asdasd")
// 	return
// }

func (a *app) GetUser(writer http.ResponseWriter, request *http.Request) {
	println("testsetsetsetsets")

}

// func (a *app) GetAllMessages(w http.ResponseWriter, r *http.Request) {
// 	vars := mux.Vars(r)

// 	if vars["id"] == "" {
// 		w.WriteHeader(http.StatusBadRequest)
// 		fmt.Println("no id sent in url")
// 		return
// 	}

// 	user, err := a.MessageService.GetByChat(1)
// 	if err != nil {
// 		w.WriteHeader(http.StatusBadRequest)
// 		fmt.Println(err)
// 		return
// 	}
// 	userJson, err := json.Marshal(user)
// 	if err != nil {
// 		w.WriteHeader(http.StatusBadRequest)
// 		fmt.Println(err)
// 		return
// 	}

// 	w.Header().Add("Content-Type", "application/json")
// 	_, _ = w.Write(userJson)
// 	return
// }

func(a *app) GetAllChats(w http.ResponseWriter, r *http.Request) {

	//TODO use actual userId
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

	// Start a new goroutine for each WebSocket connection
	go func(conn *websocket.Conn) {
		defer conn.Close()

		assignedString = generateRandomString(randomStrLen)

		// Timer for inactivity (8 hours)
		conn.SetReadDeadline(time.Now().Add(pongWait))
		conn.SetPongHandler(func(string) error {
			conn.SetReadDeadline(time.Now().Add(pongWait))
			return nil
		})

		ticker := time.NewTicker(pingPeriod)
		defer ticker.Stop()

		// Write the assigned random string to the client once connected
		// message := fmt.Sprintf("Your assigned string is: %s", assignedString)
		// if err := conn.WriteMessage(websocket.TextMessage, []byte(message)); err != nil {
		// 	log.Println("Error writing initial message:", err)
		// 	return
		// }


		//establish first message
		_, msg, err := conn.ReadMessage()
		if err != nil {
			log.Println("Error reading message, closing connection:", err)
			return
		}
		var newReceivedMessage MetaMessage
		// err_old = json.Unmarshal(msg, &receivedMessage)
		err = json.Unmarshal(msg, &newReceivedMessage)
		if err != nil {
			log.Println("Error while establishig websocket connection")
			log.Println(err)
			return
		}
		var userId = newReceivedMessage.MessageValue.CreatedBy
		a.Connections.Swap(userId, conn)


		// Main loop to keep the connection alive and manage messages
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

				// Log the message received from the client
				log.Printf("Received message: %s", string(msg))
				log.Printf("[DEBUG] code:1")
				// Try to parse the message as JSON into the Message struct
				// var receivedMessage Message
				var newReceivedMessage MetaMessage
				// err_old = json.Unmarshal(msg, &receivedMessage)
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
						//send 
						println("[DEBUG]6")
						jsonData, _ := json.Marshal(data)
						if err := conn.WriteMessage(websocket.TextMessage, jsonData); err != nil {
							log.Println("Error sending error message:", err)
						}

					} else if newReceivedMessage.Operation == "get" {
						// var chat_id = newReceivedMessage.MessageValue.ChatId
						//db query to get chat_id
						data:= a.MessageService.GetByChat(newReceivedMessage.MessageValue.ChatId)
						log.Println("[DEBUG]5")
						jsonData, _ := json.Marshal(data)
						if err := conn.WriteMessage(websocket.TextMessage, jsonData); err != nil {
							log.Println("Error sending error message:", err)
						}

					} else if newReceivedMessage.Operation == "post" {
						//db query to insert chat message, send msg to all corresponding websockets
						data, _ := a.MessageService.Create(&newReceivedMessage.MessageValue)
						var msgs []services.Message
						msgs = append(msgs, data)
						jsonData, _ := json.Marshal(msgs)
						log.Println("[DEBUG]51")
						log.Println(data)
						if err := conn.WriteMessage(websocket.TextMessage, jsonData); err != nil {
							log.Println("Error sending error message:", err)
						}
					}
				}

				// Respond back with the assigned random string
				// response := fmt.Sprintf("Your assigned string is: %s", assignedString)
				// if err := conn.WriteMessage(websocket.TextMessage, []byte(response)); err != nil {
				// 	log.Println("Error writing response message:", err)
				// 	return
				// }

				// Refresh the timeout (extend connection lifespan)
				conn.SetReadDeadline(time.Now().Add(pongWait))
			}
		}
	}(conn)
}

func main() {
	var dsn string
	var port string
	var setLimits bool
	flag.StringVar(&dsn, "dsn", "admin:test@tcp(172.22.0.1:5432)/JobMarketDB", "PostgreSQL DSN")
	flag.StringVar(&port, "port", "8088", "Service Port")
	flag.BoolVar(&setLimits, "limits", false, "Sets DB limits")
	flag.Parse()

	var dfn = "host=172.17.0.1 user=admin password=test dbname=JobMarketDB port=5432"
	db, err := gorm.Open(postgres.Open(dfn), &gorm.Config{})
	// print(db.ConnPool)

	// db, err := openDB(dsn, setLimits)
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
