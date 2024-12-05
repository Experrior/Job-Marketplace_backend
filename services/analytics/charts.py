from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import func, text, and_, Integer, cast, Float
from db import get_db, Chart
from models import Job, Application, Company, Viewed, User, Experience

router = APIRouter()
save_dir = 'charts'


@router.get("/charts/get-jsons", response_model=dict)
def get_jsons(db: Session = Depends(get_db)):
    charts = db.query(Chart).all()

    data = {chart.name: chart.content for chart in charts}

    return data

    return data


# 1. Top 10 locations with the highest average salaries
@router.get("/charts/top-10-locations-by-average-salaries", response_model=dict)
def get_top_10_locations_by_average_salaries(db: Session = Depends(get_db)):
    locations = (
        db.query(Job.location, func.avg(Job.salary).label("average_salary"))
        .group_by(Job.location)
        # .having(func.count(Job.salary) >= 3)
        .order_by(func.avg(Job.salary).desc())
        .limit(10)
        .all()
    )

    data = {"data": [{"location": loc, "average_salary": float(avg)} for loc, avg in locations]}

    chart_name = "top_10_locations_by_average_salaries"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 2. Top 5 industries with the highest average salaries
@router.get("/charts/top-5-industries-by-average-salaries", response_model=dict)
def get_top_5_industries_by_average_salaries(db: Session = Depends(get_db)):
    industries = (
        db.query(Company.industry, func.avg(Job.salary).label("average_salary"))
        .join(Job, Job.company_id == Company.company_id)
        .group_by(Company.industry)
        .order_by(func.avg(Job.salary).desc())
        .limit(5)
        .all()
    )

    data = {"data": [{"industry": ind, "average_salary": float(avg)} for ind, avg in industries]}

    chart_name = "top_5_industries_by_average_salaries"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 3. Top 5 most viewed job offers by industry
@router.get("/charts/top-5-most-viewed-by-industry", response_model=dict)
def get_top_5_most_viewed_jobs_by_industry(db: Session = Depends(get_db)):
    views = (
        db.query(Company.industry, Job.job_title, func.count(Viewed.user_id).label("views"))
        .join(Job, Job.job_id == Viewed.job_id)
        .join(Company, Company.company_id == Job.company_id)
        .group_by(Company.industry, Job.job_title)
        .order_by(func.count(Viewed.user_id).desc())
        .limit(5)
        .all()
    )

    data = {"data": [{"industry": ind, "job_title": job, "views": view_count} for ind, job, view_count in views]}

    chart_name = "top_5_industries_by_average_salaries"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 4. Average number of applications by user
@router.get("/charts/average-applications-by-user", response_model=dict)
def get_avg_applications_by_user(db: Session = Depends(get_db)):
    subquery = (
        db.query(
            Application.user_id,
            func.count(Application.application_id).label("app_count")
        )
        .group_by(Application.user_id)
        .subquery()
    )

    avg_apps = db.query(func.avg(subquery.c.app_count)).scalar()

    if avg_apps:
        data = {"average_applications_per_user": float(avg_apps)}
    else:
        data = {"average_applications_per_user": -1.0}

    chart_name = "avg_applications_by_user"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 5. Average number of applications by job offers grouped by level
@router.get("/charts/average-applications-by-level", response_model=dict)
def get_avg_applications_by_level(db: Session = Depends(get_db)):
    job_application_counts = (
        db.query(
            Job.level,
            func.count(Job.job_id).label("job_count"),
            func.count(Application.application_id).label("application_count")
        )
        .join(Application, Application.job_id == Job.job_id)
        .group_by(Job.level)
        .subquery()
    )

    levels = (
        db.query(
            job_application_counts.c.level,
            cast(job_application_counts.c.application_count / job_application_counts.c.job_count, Float).label("avg_applications")
        )
        .all()
    )

    data = {"data": [{"level": level, "average_applications": float(avg)} for level, avg in levels]}

    chart_name = "avg_applications_by_level"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 6. Total applications last 30 days
@router.get("/charts/total-applications-last-30-days", response_model=dict)
def get_total_applications_last_30_days(db: Session = Depends(get_db)):
    total = (
        db.query(func.count(Application.application_id))
        .filter(Application.created_at >= func.now() - text("interval '30 days'"))
        .scalar()
    )

    data = {"total_applications_last_30_days": total}

    chart_name = "total_applications_last_30_days"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 7. Average number of job offers by companies in each industry
@router.get("/charts/average-offers-per-company-by-industry", response_model=dict)
def get_avg_job_offers_per_company_by_industry(db: Session = Depends(get_db)):
    job_counts = (
        db.query(
            Job.company_id,
            func.count(Job.job_id).label("job_count")
        )
        .join(Company, Company.company_id == Job.company_id)
        .group_by(Job.company_id)
        .subquery()
    )

    industries = (
        db.query(
            Company.industry,
            func.avg(job_counts.c.job_count).label("avg_offers")
        )
        .join(job_counts, job_counts.c.company_id == Company.company_id)
        .group_by(Company.industry)
        .order_by(func.avg(job_counts.c.job_count))
        .all()
    )

    data = {"data": [{"industry": industry, "average_offers": float(avg)} for industry, avg in industries]}

    chart_name = "avg_job_offers_per_company_by_industry"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 8. Percentage change of job offers by industry in last 30 days vs 31-60 days
@router.get("/charts/offers-amount-percentage-change-month-to-month-by-industry", response_model=dict)
def get_offers_amount_percentage_change_month_to_month_by_industry(db: Session = Depends(get_db)):
    current_period = func.now() - text("interval '30 days'")
    previous_period = func.now() - text("interval '60 days'")
    changes = (
        db.query(
            Company.industry,
            func.count(Job.job_id).filter(Job.created_at >= current_period).label("current_count"),
            func.count(Job.job_id).filter(and_(Job.created_at < current_period, Job.created_at >= previous_period)).label("previous_count")
        )
        .join(Job, Job.company_id == Company.company_id)
        .group_by(Company.industry)
        .all()
    )
    result = []
    for industry, current, previous in changes:
        if previous is not None and current is not None and previous != 0:
            percentage_change = ((current - previous) / previous * 100)
        else:
            percentage_change = None
        result.append({"industry": industry, "current_count": current, "previous_count": previous, "percentage_change": percentage_change})

    data = {"data": result}

    chart_name = "offers_amount_percentage_change_month_to_month_by_industry"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 9. Number of offers per seniority with data about average salary
@router.get("/charts/offers-per-seniority-with-avg-salary", response_model=dict)
def get_number_of_offers_per_seniority_with_avg_salary(db: Session = Depends(get_db)):
    offers = (
        db.query(
            Job.level,
            func.count(Job.job_id).label("offer_count"),
            func.avg(Job.salary).label("average_salary")
        )
        .group_by(Job.level)
        .order_by(func.count(Job.job_id).desc())
        .all()
    )

    data = {"data": [{"seniority": level, "offer_count": count, "average_salary": float(avg_salary)} for level, count, avg_salary in offers]}

    chart_name = "number_of_offers_per_seniority_with_avg_salary"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 10. Histogram of years of users' experience (0-3, 3-6, 6-10, 10-15, 15-25, >25)
@router.get("/charts/required-experience-histogram", response_model=dict)
def get_required_experience_histogram(db: Session = Depends(get_db)):
    bins = [
        (0, 3), (4, 6), (7, 10), (11, 15), (16, 25), (26, float('inf'))
    ]
    histogram = {}

    for bin_start, bin_end in bins:
        count = (
            db.query(func.count(User.user_id))
            .join(Experience, Experience.profile_id == User.user_id)
            .filter(
                func.extract('day', func.now() - Experience.start_date) / 365 >= bin_start,
                func.extract('day', func.now() - Experience.start_date) / 365 < bin_end
            )
            .scalar()
        )
        histogram[f"{bin_start}-{bin_end if bin_end != float('inf') else 'more'}"] = count

    data = {"data": histogram}

    chart_name = "required_experience_histogram"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 11. Average required years of experience by seniority
@router.get("/charts/average-required-experience-by-seniority", response_model=dict)
def get_avg_required_experience_by_seniority(db: Session = Depends(get_db)):
    required_experience = (
        db.query(
            Job.level,
            func.avg(func.cast(Job.required_experience, Integer)).label("average_required_experience")
        )
        .group_by(Job.level)
        .all()
    )

    data = {"data": [{"seniority": level, "average_required_experience": float(avg_exp)} for level, avg_exp in required_experience]}

    chart_name = "avg_required_experience_by_seniority"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 12. Average required years of experience by industry
@router.get("/charts/average-required-experience-by-industry", response_model=dict)
def get_avg_required_experience_by_industry(db: Session = Depends(get_db)):
    required_experience = (
        db.query(
            Company.industry,
            func.avg(func.cast(Job.required_experience, Integer)).label("average_required_experience")
        )
        .join(Job, Job.company_id == Company.company_id)
        .group_by(Company.industry)
        .all()
    )

    data = {"data": [{"industry": industry, "average_required_experience": float(avg_exp)} for industry, avg_exp in required_experience]}

    chart_name = "avg_required_experience_by_industry"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 13. Top 10 most viewed job offers
@router.get("/charts/top-10-most-viewed-offers", response_model=dict)
def get_top_10_most_viewed_offers(db: Session = Depends(get_db)):
    most_viewed_jobs = (
        db.query(
            Job.job_title,
            func.count(Viewed.user_id).label("views")
        )
        .join(Viewed, Viewed.job_id == Job.job_id)
        .group_by(Job.job_title)
        .order_by(func.count(Viewed.user_id).desc())
        .limit(10)
        .all()
    )

    data = {"data": [{"job_title": title, "views": view_count} for title, view_count in most_viewed_jobs]}

    chart_name = "top_10_most_viewed_offers"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 14. Top 5 companies with the highest average salary in job offers
@router.get("/charts/top-5-companies-highest-average-salary", response_model=dict)
def get_top_5_companies_highest_avg_salary(db: Session = Depends(get_db)):
    companies = (
        db.query(
            Company.name,
            func.avg(Job.salary).label("average_salary")
        )
        .join(Job, Job.company_id == Company.company_id)
        .group_by(Company.name)
        .order_by(func.avg(Job.salary).desc())
        .limit(5)
        .all()
    )

    data = {"data": [{"company_name": name, "average_salary": float(avg_salary)} for name, avg_salary in companies]}

    chart_name = "top_5_companies_highest_avg_salary"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 15. Top 5 companies with most job offers
@router.get("/charts/top-5-companies-most-job-offers", response_model=dict)
def get_top_5_companies_most_job_offers(db: Session = Depends(get_db)):
    companies = (
        db.query(
            Company.name,
            func.count(Job.job_id).label("job_offers_count")
        )
        .join(Job, Job.company_id == Company.company_id)
        .group_by(Company.name)
        .order_by(func.count(Job.job_id).desc())
        .limit(5)
        .all()
    )

    data = {"data": [{"company_name": name, "job_offers_count": count} for name, count in companies]}

    chart_name = "top_5_companies_most_job_offers"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 16. Ratio of level required in offers by industry
@router.get("/charts/ratio-of-levels-by-industry", response_model=dict)
def get_ratio_of_levels_by_industry(db: Session = Depends(get_db)):
    levels_by_industry = (
        db.query(
            Company.industry,
            Job.level,
            func.count(Job.job_id).label("count")
        )
        .join(Job, Job.company_id == Company.company_id)
        .group_by(Company.industry, Job.level)
        .all()
    )

    industry_data = {}
    for industry, level, count in levels_by_industry:
        if industry not in industry_data:
            industry_data[industry] = {}
        industry_data[industry][level] = count

    data = {"data": [{"industry": industry, "level_ratio": levels} for industry, levels in industry_data.items()]}

    chart_name = "ratio_of_levels_by_industry"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data


# 17. Difference of highest and lowest salary by industry
@router.get("/charts/salary-difference-by-industry", response_model=dict)
def get_salary_difference_by_industry(db: Session = Depends(get_db)):
    salary_diff = (
        db.query(
            Company.industry,
            func.max(Job.salary).label("highest_salary"),
            func.min(Job.salary).label("lowest_salary")
        )
        .join(Job, Job.company_id == Company.company_id)
        .group_by(Company.industry)
        .all()
    )

    data = {
        "data": [
            {
                "industry": industry,
                "highest_salary": highest,
                "lowest_salary": lowest,
                "difference": highest - lowest if highest is not None and lowest is not None else None
            }
            for industry, highest, lowest in salary_diff
        ]
    }

    chart_name = "salary_difference_by_industry"
    try:
        Chart.add_record(session=db, name=chart_name, content=data)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"An error occurred: {str(e)}")

    return data
