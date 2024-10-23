package a1




// package main

// import (
// 	_ "chat/a1"
// 	"log"
// 	"os"

// 	"time"

// 	"gorm.io/driver/postgres"
// 	"gorm.io/gorm"
// 	"gorm.io/gorm/logger"
// )

// type Message struct {
// 	MessageId int    `json:"id" gorm:"primaryKey"`
// 	ChatId    int    `json:"chatId" gorm:"references:Chat"`
// 	Content   string `json:"content"`

// 	CreatedBy int       `json:"CreatedBy" gorm:"references:app_users"`
// 	ReadBy    app_users `json:"readBy" gorm:"references:app_users"`
// 	DeletedBy app_users `json:"deletedBy" gorm:"references:app_users"`

// 	CreatedAt time.Time `json:"createdAt" gorm:"default:CURRENT_TIMESTAMP()"`
// 	UpdatedAt time.Time `json:"updatedAt" gorm:"default:CURRENT_TIMESTAMP()"`
// }

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

// type Chat struct {
// 	ChatID      int       `json:"chat_id" gorm:"primaryKey"`
// 	Name        string    `json:"name"`
// 	Members     string    `json:"members" gorm:"references:app_users"`
// 	CreatedBy   int       `json:"created_by" gorm:"references:app_users"`
// 	DeletedBy   int       `json:"deleted_by" gorm:"references:app_users"`
// 	LastMessage string    `json:"last_message"`
// 	Tags        string    `json:"tags"`
// 	CreatedAt   time.Time `json:"created_at" gorm:"default:CURRENT_TIMESTAMP"`
// 	UpdatedAt   time.Time `json:"updated_at" gorm:"default:CURRENT_TIMESTAMP"`
// }

// // type Tabler interface {
// // 	TableName() string
// //   }

// // // TableName overrides the table name used by User to `profiles`
// // func (User) TableName() string {
// // return "profiles"
// // }

// func connDB() {

// 	newLogger := logger.New(
// 		log.New(os.Stdout, "\r\n", log.LstdFlags), // io writer
// 		logger.Config{
// 			SlowThreshold:             time.Second,   // Slow SQL threshold
// 			LogLevel:                  logger.Silent, // Log level
// 			IgnoreRecordNotFoundError: true,          // Ignore ErrRecordNotFound error for logger
// 			ParameterizedQueries:      true,          // Don't include params in the SQL log
// 			Colorful:                  false,         // Disable color
// 		},
// 	)

// 	var dfn = "host=172.17.0.1 user=admin password=test dbname=JobMarketDB port=5432"
// 	// var dfn = "admin:test@tcp(172.22.0.1:5432)/JobMarketDB"
// 	db, err := gorm.Open(postgres.Open(dfn), &gorm.Config{
// 		Logger: newLogger,
// 	})
// 	if err == nil {

// 		var user app_users
// 		// var users []app_users

// 		// works because destination struct is passed in
// 		db.First(&user)
// 		// SELECT * FROM `users` ORDER BY `users`.`id` LIMIT 1

// 		// works because model is specified using `db.Model()`
// 		result := map[string]interface{}{}
// 		db.Model(&app_users{}).First(&result)
// 		// SELECT * FROM `users` ORDER BY `users`.`id` LIMIT 1
// 		println(result)

// 		db.First(&user, 10)
// 		// result = db.Find(&users)
// 		// SELECT * FROM users;
// 		println(user.Email)
// 		println("test")
// 		// doesn't work
// 		// result := map[string]interface{}{}
// 		// db.Table("users").First(&result)

// 		// works with Take
// 		// result := map[string]interface{}{}
// 		// db.Table("users").Take(&result)

// 		// no primary key defined, results will be ordered by first field (i.e., `Code`)
// 		type Language struct {
// 			Code string
// 			Name string
// 		}
// 		db.First(&Language{})
// 		// SELECT * FROM `languages` ORDER BY `languages`.`code` LIMIT 1

// 		// var result []map[string]interface{}
// 		// var user app_users
// 		// var result []map[string]interface{}
// 		// tx := db.First(&user, 1).Scan(result)
// 		//   if tx.Error != nil {
// 		// 	fmt.Println(tx.Error)
// 		// 	return
// 		// }
// 		// bytes, _ := json.Marshal(result)
// 		// fmt.Println(string(bytes))
// 	} else {
// 		println("error")
// 	}

// }

// func main() {
// 	connDB()
// }
