package services

import (
	// "context"
	"database/sql"
	// "time"
)

type PostgresUserService struct {
	db *sql.DB
}

func NewPostgresUserService(db *sql.DB) *PostgresUserService {
	return &PostgresUserService{db: db}
}


// func (p *PostgresUserService) Create(user *User) error {
// 	query := `
//         INSERT INTO users (id, name, occupation) 
//         VALUES ($1, $2, $3)
//         RETURNING id, created_at, updated_at`

// 	newV4, err := uuid.NewV4()
// 	if err != nil {
// 		return err
// 	}

// 	ctx, cancel := context.WithTimeout(context.Background(), 15*time.Second)
// 	defer cancel()

// 	return p.db.QueryRowContext(ctx, query, newV4.String(), user.Name, user.Occupation).
// 		Scan(&user.ID, &user.CreatedAt, &user.UpdatedAt)
// }