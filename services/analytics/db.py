from sqlalchemy import create_engine, inspect, Column, String, JSON
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from sqlalchemy.exc import OperationalError
from fastapi import APIRouter


router = APIRouter()

DATABASE_URL = "postgresql://admin:test@localhost/JobMarketDB"


engine = create_engine(DATABASE_URL)


SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


Base = declarative_base()


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


class Chart(Base):
    __tablename__ = 'charts'

    name = Column(String, primary_key=True)
    content = Column(JSON)

    @classmethod
    def add_record(cls, session: Session, name: str, content: dict):
        chart = session.query(cls).filter(cls.name == name).first()

        if chart:
            chart.content = content
        else:
            chart = cls(name=name, content=content)
            session.add(chart)

        session.commit()

        return chart


def create_charts_table():
    try:
        inspector = inspect(engine)
        if 'charts' not in inspector.get_table_names():
            Base.metadata.create_all(bind=engine)
            print("Table 'charts' created successfully.")
    except OperationalError as e:
        print(f"Error while checking/creating table: {e}")
