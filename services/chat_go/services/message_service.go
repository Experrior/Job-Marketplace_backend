package services

import (
	"context"
	"log"
	"time"
	"gorm.io/gorm"
	"github.com/google/uuid"
)

type DbMessageService struct {
	db *gorm.DB
}

func NewMessageService(db *gorm.DB) *DbMessageService {
	return &DbMessageService{db: db}
}

type MessageService interface {
	GetMessages(chatId string) ([]Message)
	Create(message *Message) (Message, error)
	GetAllChats(userId string) ([]chats)
	GetUsersByChat(chatId string) ([]string)
	StartChat(creatorId string, targetId string, recruiterName string, applicantName string) (chatObj chats)
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

type Message struct {
	
	MessageId string    `json:"id" gorm:"primaryKey;type:uuid;default:gen_random_uuid()"`
	ChatId    string    `json:"chatId" gorm:"references:Chat;type:uuid"`
	Content   string `json:"content"`

	CreatedBy string `json:"createdBy" gorm:"references:app_users;type:uuid"`

	CreatedAt time.Time `json:"createdAt"`
	UpdatedAt time.Time `json:"updatedAt"`
}



type user_chats struct {
	UserID           string    `json:"user_id" gorm:"primaryKey;type:uuid;default:gen_random_uuid();references:app_users"`
	ChatID           string    `json:"chat_id" gorm:"primaryKey;type:uuid;default:gen_random_uuid();"`
}


type chats struct {
	ChatId        string    `gorm:"primaryKey;type:uuid;default:gen_random_uuid()"`
	RecruiterId   string    `gorm:"type:uuid;references:app_users"`                                   
	ApplicantId   string    `gorm:"type:uuid;references:app_users"`                                   
	RecruiterName string    `gorm:"type:string"`                                   
	ApplicantName string    `gorm:"type:string"`                                   
	IsDeleted     bool      `gorm:"default:false"`                                 
	CreatedAt     time.Time `gorm:"type:timestamp;default:now()"`                  
	UpdatedAt     time.Time `gorm:"type:timestamp;default:now()"`   
}


type Chat struct {

	ChatId			string 	`json:"chatId" gorm:"PrimaryKey;type:uuid;default:gen_random_uuid()"`
	CreatedBy		string 	`json:"createdBy" gorm:"PrimaryKey;type:uuid;references:app_users"`
}
func (p *DbMessageService) Create(message *Message) (Message, error) {
	_, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()

	message.MessageId = uuid.NewString()
	
	result := p.db.Table("chat_messages").Create(&message)

	return *message, result.Error
}


func (p *DbMessageService) GetUsersByChat(chatId string) ([]string) {
	_, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()
	var user_chats []user_chats
	p.db.Table("user_chats").Where("chat_id = ?", chatId).Find(&user_chats)

	// process userchats
    userIDs := make([]string, len(user_chats))
    for i, chat := range user_chats {
        userIDs[i] = chat.UserID
    }
	return userIDs

}


func (p *DbMessageService) StartChat(recruiterId string, applicantId string, recruiterName string, applicantName string) (chatObj chats) {
	_, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()
	log.Println("DEBUG CREATE CHAT")

	// check if already exists
	var existing chats

	result := p.db.Table("chats").Where("recruiter_id = ?", recruiterId).Where("applicant_id = ?", applicantId).Find(&existing)
	if (result.Error == nil && existing.ChatId != "") {
		log.Println("Found existing chats")
		//existing chat found
		return existing
	}
	
	//todo add error handling later on, sHoUlD never happen
	newChat := chats{
		ChatId: uuid.NewString(),
		RecruiterId: recruiterId,
		ApplicantId: applicantId,
		RecruiterName: recruiterName,
		ApplicantName: applicantName,
	}
	log.Println(newChat)
	p.db.Table("chats").Select("chat_id", "recruiter_id", "applicant_id", "recruiter_name", "applicant_name").Create(&newChat)


    creatorChat := user_chats{
        UserID: recruiterId,
        ChatID: newChat.ChatId,
    }
	p.db.Table("user_chats").Create(&creatorChat)

    targetChat := user_chats{
        UserID: applicantId,
        ChatID: newChat.ChatId,
    }
	p.db.Table("user_chats").Create(&targetChat)

	return newChat

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


func (p *DbMessageService) GetAllChats(userId string) (outputChats []chats) {
	_, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()

	// safe, will be escaped
	var results []user_chats
	p.db.Table("user_chats").Where("user_id = ?", userId).Find(&results)
	println("[DEBUG]48")


	var allChats []chats

    for _, chat := range results {
		var temp chats
		if err := p.db.Table("chats").Where("chat_id = ?", chat.ChatID).Find(&temp).Error; err != nil {
			log.Println("error when retreving chat infor for", chat.ChatID)
			continue
		}		
		allChats = append(allChats, temp)
		log.Println(chat.ChatID)
		log.Println(temp)
    }
	log.Println("doing stupid stuff")

    // Extract only the ChatID values into a new slice

	// println("[DEBUG]65 "+chatIDs[0])
	return allChats
}

