from sqlalchemy import Column, Integer, String, Boolean, DateTime
from db import Base


class User(Base):
    __tablename__ = "app_users"

    user_id = Column(Integer, primary_key=True, index=True)
    company = Column(Integer, index=True)
    email = Column(String, unique=True, index=True)
    first_name = Column(String)
    last_name = Column(String)
    role = Column(String)
    is_blocked = Column(Boolean)
    email_verified = Column(Boolean)
    employee_verified = Column(Boolean)
    created_at = Column(DateTime)
    password_hash = Column(String)


class Company(Base):
    __tablename__ = "company"

    company_id = Column(Integer, primary_key=True, index=True)
    name = Column(String)
    email = Column(String)
    logoPath = Column(String)
    industry = Column(String)
    description = Column(String)
    verified = Column(Boolean)
    created_at = Column(DateTime)
    updated_at = Column(DateTime)


class Job(Base):
    __tablename__ = "job"

    job_id = Column(Integer, primary_key=True, index=True)
    company_id = Column(Integer, index=True)
    job_title = Column(String)
    category = Column(String)
    job_description = Column(String)
    required_skills = Column(String)
    required_experience = Column(String)
    level = Column(String)
    location = Column(String)
    salary = Column(Integer)
    created_at = Column(DateTime)


class Applications(Base):
    __tablename__ = "user_applications"

    job_id = Column(Integer, primary_key=True)
    user_id = Column(Integer, primary_key=True)
    created_at = Column(DateTime)


class Viewed(Base):
    __tablename__ = "user_viewed"

    job_id = Column(Integer, primary_key=True)
    user_id = Column(Integer, primary_key=True)
