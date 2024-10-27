package services

import (
	"context"
	// "encoding/json"
	"time"

	"gorm.io/gorm"
)

type DbMessageService struct {
	db *gorm.DB
}

func NewMessageService(db *gorm.DB) *DbMessageService {
	return &DbMessageService{db: db}
}

type MessageService interface {
	// Get(id string) (*app_users, error)
	// Delete(id string) error
	// DeleteAll() error
	// Update(user *User) error
	GetMessages(chatId string) ([]Message)
	Create(message *Message) (Message, error)
	GetAllChats(userId string) ([]user_chats)
	GetUsersByChat(chatId string) ([]string)
}

type Message struct {
	
	MessageId string    `json:"id" gorm:"primaryKey;type:uuid;default:gen_random_uuid()"`
	ChatId    string    `json:"chatId" gorm:"references:Chat;type:uuid"`
	Content   string `json:"content"`

	CreatedBy string `json:"createdBy" gorm:"references:app_users;type:uuid"`
	CreatedByDisplay string `json:"createdByDisplay"`
	ReadBy    string `json:"readBy" gorm:"references:app_users"`
	DeletedBy string `json:"deletedBy" gorm:"references:app_users"`

	CreatedAt time.Time `json:"createdAt"`
	UpdatedAt time.Time `json:"updatedAt"`
}

type app_users struct {
	UserID           string    `json:"user_id" gorm:"primaryKey;type:uuid;default:gen_random_uuid()"`
	CompanyID        string    `json:"company_id" gorm:"references:Company;type:uuid"`
	Email            string    `json:"email" gorm:"unique"`
	FirstName        string    `json:"first_name"`
	LastName         string    `json:"last_name"`
	Phone            string    `json:"phone"`
	Role             string    `json:"role"`
	IsBlocked        bool      `json:"is_blocked"`
	EmailVerified    bool      `json:"email_verified"`
	EmployeeVerified bool      `json:"employee_verified"`
	CreatedAt        time.Time `json:"created_at"`
	PasswordHash     string    `json:"password_hash"`
}

type user_chats struct {
	UserID           string    `json:"user_id" gorm:"primaryKey;type:uuid;default:gen_random_uuid();references:app_users"`
	ChatID           string    `json:"chat_id" gorm:"primaryKey;type:uuid;default:gen_random_uuid();"`
}


func (p *DbMessageService) Create(message *Message) (Message, error) {
	_, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()
	
	result := p.db.Table("chat_messages").Create(&message)

	return *message, result.Error
}


func (p *DbMessageService) GetUsersByChat(chatId string) ([]string) {
	_, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()
	var user_chats []user_chats
	p.db.Table("user_chats").Create(&user_chats)

	// process userchats
    userIDs := make([]string, len(user_chats))
    for i, chat := range user_chats {
        userIDs[i] = chat.ChatID
    }
	return userIDs

}

func (p *DbMessageService) GetMessages(chatId string) (rows []Message) {
	_, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()

	// safe, will be escaped
	var results []Message
	p.db.Table("chat_messages").Where("chat_id = ?", chatId).Find(&results)
	println("[DEBUG]4")
	return results
}


func (p *DbMessageService) GetAllChats(userId string) (chats []user_chats) {
	_, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()

	// safe, will be escaped
	var results []user_chats
	p.db.Table("user_chats").Where("user_id = ?", userId).Find(&results)
	println("[DEBUG]48")

    // Extract only the ChatID values into a new slice

	// println("[DEBUG]65 "+chatIDs[0])
	return results
}

