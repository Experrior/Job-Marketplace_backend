from apscheduler.schedulers.background import BackgroundScheduler
from charts import *
from fastapi import APIRouter
from db import get_db
from sqlalchemy.orm import Session

router = APIRouter()
procedure_scheduler = BackgroundScheduler()


functions = [
    get_top_10_locations_by_average_salaries,
    get_top_5_industries_by_average_salaries,
    get_top_5_most_viewed_jobs_by_industry,
    get_avg_applications_by_user,
    get_avg_applications_by_level,
    get_total_applications_last_30_days,
    get_avg_job_offers_per_company_by_industry,
    get_offers_amount_percentage_change_month_to_month_by_industry,
    get_number_of_offers_per_seniority_with_avg_salary,
    get_required_experience_histogram,
    # get_avg_required_experience_by_seniority,
    # get_avg_required_experience_by_industry,
    get_top_10_most_viewed_offers,
    get_top_5_companies_highest_avg_salary,
    get_top_5_companies_most_job_offers,
    get_ratio_of_levels_by_industry,
    get_salary_difference_by_industry,
]


@router.get("/scheduler/charts")
def trigger_functions(db: Session = Depends(get_db)):
    for function in functions:
        try:
            function(db=db)
            print(f"Triggered {function.__name__}")
        except Exception as e:
            print(f"Failed to trigger {function.__name__}: {e}")


procedure_scheduler.add_job(trigger_functions, 'cron', hour=10, minute=50)


def start_scheduler():
    if not procedure_scheduler.running:
        procedure_scheduler.add_job(trigger_functions, 'cron', hour=4)
        procedure_scheduler.start()


def stop_scheduler():
    procedure_scheduler.shutdown()
