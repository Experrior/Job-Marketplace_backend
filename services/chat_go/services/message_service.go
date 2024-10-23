package services

import (
	"context"
	"encoding/json"
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
	GetByChat(chatId int) ([]Message, error)
	Create(message *Message) (Message, error)
	GetAllChats(userId int) ([]int, error)
}

type Message struct {
	
	MessageId int    `json:"id" gorm:"primaryKey"`
	ChatId    int    `json:"chatId" gorm:"references:Chat"`
	Content   string `json:"content"`

	CreatedBy int `json:"createdBy" gorm:"references:app_users"`
	CreatedByDisplay string `json:"createdByDisplay"`
	ReadBy    string `json:"readBy" gorm:"references:app_users"`
	DeletedBy string `json:"deletedBy" gorm:"references:app_users"`

	CreatedAt time.Time `json:"createdAt"`
	UpdatedAt time.Time `json:"updatedAt"`
}

type app_users struct {
	UserID           int       `json:"user_id" gorm:"primaryKey"`
	CompanyID        int       `json:"company_id" gorm:"references:Company"`
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


func (p *DbMessageService) Create(message *Message) (Message, error) {
	_, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()
	
	result := p.db.Table("chat_messages").Create(&message)

	return *message, result.Error
}

func (p *DbMessageService) GetByChat(chatId int) (rows []Message, eer error) {
	_, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()

	// safe, will be escaped
	var results []Message
	p.db.Table("chat_messages").Where("chat_id = ?", chatId).Find(&results)
	println("[DEBUG]4")
	println(results)
	ałtuput, err := json.Marshal(results)
	println(ałtuput)
	return results, err
}

func (p *DbMessageService) GetAllChats(userId int) (chats []int, eer error) {
	// _, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	// defer cancel()

	// // safe, will be escaped
	// var results []Message
	// p.db.Table("chat_messages").Where("chat_id = ?", chatId).Find(&results)
	// println("[DEBUG]4")
	// println(results)
	// ałtuput, err := json.Marshal(results)
	// println(ałtuput)
	return []int {1,2,3,4}, nil
}

