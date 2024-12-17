from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import scheduler
from scheduler import *
import charts
import recommendation
from db import create_charts_table

app = FastAPI()

app.include_router(charts.router, tags=["charts"])
app.include_router(recommendation.router, tags=["recommendation"])
app.include_router(scheduler.router, tags=["scheduler"])


@app.on_event("startup")
def startup_event():
    print("startup_event()")
    create_charts_table()
    start_scheduler()


@app.on_event("shutdown")
def shutdown_event():
    stop_scheduler()


procedure_scheduler.start()


@app.get("/")
def root():
    return {"": ""}
