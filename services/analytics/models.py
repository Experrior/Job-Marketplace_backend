from sqlalchemy import Column, Integer, String, Boolean, DateTime
from db import Base


class User(Base):
    __tablename__ = "app_users"

    user_id = Column(String, primary_key=True, index=True)
    company_id = Column(Integer, index=True)
    email = Column(String, unique=True, index=True)
    first_name = Column(String)
    last_name = Column(String)
    role = Column(String)
    is_blocked = Column(Boolean)
    is_enabled = Column(Boolean)
    is_email_verified = Column(Boolean)
    is_employee_verified = Column(Boolean)
    created_at = Column(DateTime)
    updated_at = Column(DateTime)
    password = Column(String)


class Application(Base):
    __tablename__ = "applications"

    application_id = Column(Integer, primary_key=True)
    status = Column(Integer)
    job_id = Column(Integer)
    user_id = Column(Integer, primary_key=True)
    s3_resume_path = Column(String)
    created_at = Column(DateTime)
    updated_at = Column(DateTime)


class Company(Base):
    __tablename__ = "company"

    company_id = Column(Integer, primary_key=True, index=True)
    name = Column(String)
    email = Column(String)
    logo_path = Column(String)
    location = Column(String)
    industry = Column(String)
    description = Column(String)
    is_email_verified = Column(Boolean)
    created_at = Column(DateTime)
    updated_at = Column(DateTime)


class Education(Base):
    __tablename__ = "educations"

    education_id = Column(Integer, primary_key=True, index=True)
    profile_id = Column(String)
    institution_name = Column(String)
    degree = Column(String)
    start_date = Column(DateTime)
    end_date = Column(DateTime)
    updated_at = Column(DateTime)


class Experience(Base):
    __tablename__ = "experiences"

    experience_id = Column(Integer, primary_key=True, index=True)
    profile_id = Column(String)
    company_name = Column(String)
    company_logo = Column(String)
    level = Column(String)
    start_date = Column(DateTime)
    end_date = Column(DateTime)
    updated_at = Column(DateTime)


class Job(Base):
    __tablename__ = "jobs"

    job_id = Column(Integer, primary_key=True, index=True)
    company_id = Column(Integer, index=True)
    job_title = Column(String)
    category = Column(String)
    job_description = Column(String)
    recruiter_id = Column(Integer)
    required_skills = Column(String)
    required_experience = Column(String)
    employment_type = Column(String)
    work_setting = Column(String)
    is_deleted = Column(Boolean)
    level = Column(String)
    location = Column(String)
    salary = Column(Integer)
    created_at = Column(DateTime)
    updated_at = Column(DateTime)


class Skill(Base):
    __tablename__ = "skills"

    skill_id = Column(Integer, primary_key=True, index=True)
    profile_id = Column(String)
    skill_name = Column(String)
    proficiency_level = Column(String)
    updated_at = Column(DateTime)


class Followed(Base):
    __tablename__ = "user_followed"

    job_id = Column(Integer, primary_key=True)
    user_id = Column(Integer, primary_key=True)
    created_at = Column(DateTime)


class Viewed(Base):
    __tablename__ = "user_viewed"

    job_id = Column(Integer, primary_key=True)
    user_id = Column(Integer, primary_key=True)


class Profile(Base):
    __tablename__ = "user_profiles"

    profile_id = Column(Integer, primary_key=True)
    user_id = Column(Integer)
    resume_path = Column(String)
    profile_picture_path = Column(String)
    updated_at = Column(DateTime)
