from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sklearn.preprocessing import OneHotEncoder, MinMaxScaler
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
from db import get_db
from models import Job, Application, Followed, Viewed

router = APIRouter()


categories = [
        "WEB_DEVELOPMENT", "MOBILE_DEVELOPMENT", "GAME_DEVELOPMENT", "EMBEDDED", "ANALYTICS",
        "MACHINE_LEARNING", "CLOUD_COMPUTING", "NETWORKS",
        "CYBERSECURITY", "ADMINISTRATION", "ERP", "CONSULTING", "COMPILERS"
    ]
levels = ["INTERN", "JUNIOR", "MID", "SENIOR", "LEAD", "MANAGER", "DIRECTOR", "EXECUTIVE"]
employment_types = ["FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "TEMPORARY", "FREELANCE"]
work_settings = ["ONSITE", "REMOTE", "HYBRID"]


@router.get("/recommendations/{user_id}")
def get_recommendations(user_id: str, db: Session = Depends(get_db), count: int = 10):
    applied_jobs = db.query(Application.job_id).filter(Application.user_id == user_id).all()
    followed_jobs = db.query(Followed.job_id).filter(Followed.user_id == user_id).all()
    viewed_jobs = db.query(Viewed.job_id).filter(Viewed.user_id == user_id).all()

    applied_job_ids = set(
        job_id for job_id, in applied_jobs
    )

    interacted_job_ids = set(
        job_id for job_id, in applied_jobs + followed_jobs + viewed_jobs
    )

    jobs = db.query(Job).all()
    if not jobs:
        raise HTTPException(status_code=404, detail="No jobs available for recommendation.")

    encoder = OneHotEncoder(categories=[categories, levels, employment_types, work_settings], sparse_output=False)
    scaler = MinMaxScaler(feature_range=(-1, 1))

    job_data = [
        [
            job.category, job.experience_level, job.employment_type, job.work_location, job.salary
        ]
        for job in jobs
    ]

    encoded_jobs = encoder.fit_transform([data[:4] for data in job_data])
    normalized_salaries = scaler.fit_transform(np.array([data[4] for data in job_data]).reshape(-1, 1))
    job_vectors = np.hstack((encoded_jobs, normalized_salaries))

    interacted_jobs = [job for job in jobs if job.job_id in interacted_job_ids]
    if not interacted_jobs:
        raise HTTPException(status_code=404, detail="No job interactions found for the user.")

    interacted_vectors = np.array([job_vectors[i] for i, job in enumerate(jobs) if job.job_id in interacted_job_ids])
    user_vector = interacted_vectors.mean(axis=0)

    similarities = cosine_similarity([user_vector], job_vectors)[0]

    recommendations = sorted(
        [
            (jobs[i], similarities[i])
            for i in range(len(jobs))
            if (jobs[i].job_id not in applied_job_ids) and (jobs[i].is_deleted is False)
        ],
        key=lambda x: x[1],
        reverse=True
    )[:count]

    return [x[0] for x in recommendations]


@router.get("/similar_jobs/{job_id}")
def get_similar_jobs(job_id: str, db: Session = Depends(get_db), count: int = 10):
    target_job = db.query(Job).filter(Job.job_id == job_id).first()

    if not target_job:
        raise HTTPException(status_code=404, detail="Job not found.")

    jobs = db.query(Job).all()
    
    if not jobs:
        raise HTTPException(status_code=404, detail="No jobs available for recommendation.")

    encoder = OneHotEncoder(categories=[categories, levels, employment_types, work_settings],
                            sparse_output=False)
    scaler = MinMaxScaler(feature_range=(-1, 1))

    job_data = [
        [
            job.category, job.experience_level, job.employment_type, job.work_location, job.salary
        ]
        for job in jobs
    ]

    encoded_jobs = encoder.fit_transform([data[:4] for data in job_data])
    normalized_salaries = scaler.fit_transform(np.array([data[4] for data in job_data]).reshape(-1, 1))
    job_vectors = np.hstack((encoded_jobs, normalized_salaries))

    target_job_index = next((i for i, job in enumerate(jobs) if str(job.job_id) == job_id), None)
    if target_job_index is None:
        raise HTTPException(status_code=404, detail="Target job not found in dataset.")

    target_job_vector = job_vectors[target_job_index]

    similarities = cosine_similarity([target_job_vector], job_vectors)[0]

    similar_jobs = sorted(
        [
            (jobs[i], similarities[i])
            for i in range(len(jobs))
            if str(jobs[i].job_id) != job_id
        ],
        key=lambda x: x[1],
        reverse=True
    )[:count]

    return [ x[0] for x in similar_jobs]
