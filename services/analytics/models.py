from sqlalchemy import Column, Integer, String, Boolean, DateTime
from db import Base


class User(Base):
    __tablename__ = "app_users"

    user_id = Column(String, primary_key=True, index=True)
    company_id = Column(Integer, index=True)
    first_name = Column(String)
    last_name = Column(String)
    role = Column(String)
    created_at = Column(DateTime)


class Application(Base):
    __tablename__ = "applications"

    application_id = Column(Integer, primary_key=True)
    job_id = Column(Integer)
    user_id = Column(Integer, primary_key=True)
    created_at = Column(DateTime)


class Company(Base):
    __tablename__ = "company"

    company_id = Column(Integer, primary_key=True, index=True)
    name = Column(String)
    logo_path = Column(String)
    location = Column(String)
    industry = Column(String)
    description = Column(String)
    created_at = Column(DateTime)


class Education(Base):
    __tablename__ = "educations"

    education_id = Column(Integer, primary_key=True, index=True)
    profile_id = Column(String)
    institution_name = Column(String)
    degree = Column(String)
    start_date = Column(DateTime)
    end_date = Column(DateTime)


class Experience(Base):
    __tablename__ = "experiences"

    experience_id = Column(Integer, primary_key=True, index=True)
    profile_id = Column(String)
    company_name = Column(String)
    company_logo = Column(String)
    role = Column(String)
    start_date = Column(DateTime)
    end_date = Column(DateTime)


class Job(Base):
    __tablename__ = "jobs"

    job_id = Column(Integer, primary_key=True, index=True)
    company_id = Column(Integer, index=True)
    job_title = Column(String)
    job_description = Column(String)
    recruiter_id = Column(Integer)
    required_skills = Column(String)
    required_experience = Column(String)
    category = Column(String)
    employment_type = Column(String)
    work_location = Column(String)
    experience_level = Column(String)
    is_deleted = Column(Boolean)
    location = Column(String)
    salary = Column(Integer)
    created_at = Column(DateTime)


class Skill(Base):
    __tablename__ = "skills"

    skill_id = Column(Integer, primary_key=True, index=True)
    profile_id = Column(String)
    skill_name = Column(String)
    proficiency_level = Column(String)


class Followed(Base):
    __tablename__ = "followed_jobs"

    user_id = Column(Integer, primary_key=True)
    job_id = Column(Integer, primary_key=True)
    created_at = Column(DateTime)


class Viewed(Base):
    __tablename__ = "viewed_jobs"

    user_id = Column(Integer, primary_key=True)
    job_id = Column(Integer, primary_key=True)


class Profile(Base):
    __tablename__ = "user_profiles"

    profile_id = Column(Integer, primary_key=True)
    user_id = Column(Integer)
