import plotly.graph_objects as go
import random
from fastapi.responses import HTMLResponse
from fastapi import FastAPI, Depends
from sqlalchemy.orm import Session
from sqlalchemy import func, desc
from db import get_db
from models import User, Job, Applications, Viewed, Company
from pathlib import Path
import plotly.io as pio
pio.kaleido.scope.default_format = "png"

app = FastAPI()


@app.get("/")
def root():
    return {"": ""}


@app.get("/test_db")
def db_test(db: Session = Depends(get_db)):
    users = db.query(User).limit(5).all()
    return users


@app.get("/test_chart", response_class=HTMLResponse)
def chart():
    labels = [f"Item {i}" for i in range(1, 6)]
    values = [random.randint(1, 100) for _ in range(5)]

    fig = go.Figure([go.Bar(x=labels, y=values)])

    fig.update_layout(
        title={
            'text': "Randomly Generated Bar Chart",
            'y': 0.95,
            'x': 0.5,
            'xanchor': 'center',
            'yanchor': 'top'
        },
        xaxis_title="Items",
        yaxis_title="Values",
        template="plotly_white",
        font=dict(
            family="Arial, sans-serif",
            size=20,
            color="#333"
        ),
        title_font=dict(
            size=40,
            color="#2a3f5f"
        ),
        xaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        ),
        yaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        )
    )

    graph_html = fig.to_html(full_html=False)
    return f"<html><body>{graph_html}</body></html>"


@app.get("/users", response_class=HTMLResponse)
def read_users(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    users = db.query(User).offset(skip).limit(limit).all()

    labels = [user.first_name for user in users]  # Using first names as labels for example
    values = [user.user_id for user in users]  # Assuming we plot user IDs for simplicity

    if not users:
        return "No users found"

    fig = go.Figure([go.Bar(x=labels, y=values)], layout=go.Layout(width=100, height=100))

    fig.update_layout(
        title={
            'text': "User Data Bar Chart",
            'y': 0.9,
            'x': 0.5,
            'xanchor': 'center',
            'yanchor': 'top'
        },
        xaxis_title="User",
        yaxis_title="Count",
        template="plotly_white",
        font=dict(
            family="Arial, sans-serif",
            size=14,
            color="#333"
        ),
        title_font=dict(
            size=20,
            color="#2a3f5f"
        ),
        xaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        ),
        yaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        )
    )

    try:
        output_folder = Path("graphs")
        output_folder.mkdir(exist_ok=True)
        print("log")
        fig_path = output_folder / "user_data_bar_chart.png"
        print(fig_path)
        pio.write_image(fig, fig_path)
        print('test1')
    except Exception as e:
        print(e)
        raise e

    graph_html = fig.to_html(full_html=False)
    return f"<html><body>{graph_html}</body></html>"


@app.get("/location_avg", response_class=HTMLResponse)
def location_avg(limit: int = 10, db: Session = Depends(get_db)):
    result = (
        db.query(Job.location, func.avg(Job.salary).label("average_salary"))
        .group_by(Job.location)
        .order_by(desc("average_salary"))
        .limit(limit)
        .all()
    )

    labels = [row.location for row in result]
    values = [row.average_salary for row in result]

    if not result:
        return "No result"

    fig = go.Figure([go.Bar(x=labels, y=values)])

    fig.update_layout(
        title={
            'text': "Average salary per location",
            'y': 0.95,
            'x': 0.5,
            'xanchor': 'center',
            'yanchor': 'top'
        },
        xaxis_title="Location",
        yaxis_title="Salary",
        template="plotly_white",
        font=dict(
            family="Arial, sans-serif",
            size=20,
            color="#333"
        ),
        title_font=dict(
            size=40,
            color="#2a3f5f"
        ),
        xaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        ),
        yaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        )
    )

    # try:
    #     output_folder = Path("graphs")
    #     output_folder.mkdir(exist_ok=True)
    #     fig_path = output_folder / "location_avg.png"
    #     fig.write_image(fig_path, format="png")
    # except Exception as e:
    #     print(e)
    #     raise e

    graph_html = fig.to_html(full_html=False)
    return f"<html><body>{graph_html}</body></html>"


@app.get("/level_avg", response_class=HTMLResponse)
def level_avg(limit: int = 5, db: Session = Depends(get_db)):
    result = (
        db.query(Job.level, func.avg(Job.salary).label("average_salary"))
        .group_by(Job.level)
        .order_by(desc("average_salary"))
        .limit(limit)
        .all()
    )

    labels = [row.level for row in result]
    values = [row.average_salary for row in result]

    if not result:
        return "No result"

    fig = go.Figure([go.Bar(x=labels, y=values)])

    fig.update_layout(
        title={
            'text': "Average salary per level",
            'y': 0.95,
            'x': 0.5,
            'xanchor': 'center',
            'yanchor': 'top'
        },
        xaxis_title="Level",
        yaxis_title="Salary",
        template="plotly_white",
        font=dict(
            family="Arial, sans-serif",
            size=20,
            color="#333"
        ),
        title_font=dict(
            size=40,
            color="#2a3f5f"
        ),
        xaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        ),
        yaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        )
    )

    # try:
    #     output_folder = Path("graphs")
    #     output_folder.mkdir(exist_ok=True)
    #     fig_path = output_folder / "level_avg.png"
    #     fig.write_image(fig_path, format="png")
    # except Exception as e:
    #     print(e)
    #     raise e

    graph_html = fig.to_html(full_html=False)
    return f"<html><body>{graph_html}</body></html>"


@app.get("/category_avg", response_class=HTMLResponse)
def category_avg(limit: int = 10, db: Session = Depends(get_db)):
    result = (
        db.query(Job.category, func.avg(Job.salary).label("average_salary"))
        .group_by(Job.category)
        .order_by(desc("average_salary"))
        .limit(limit)
        .all()
    )

    labels = [row.category for row in result]
    values = [row.average_salary for row in result]

    if not result:
        return "No result"

    fig = go.Figure([go.Bar(x=labels, y=values)])

    fig.update_layout(
        title={
            'text': "Average salary per category",
            'y': 0.95,
            'x': 0.5,
            'xanchor': 'center',
            'yanchor': 'top'
        },
        xaxis_title="Category",
        yaxis_title="Salary",
        template="plotly_white",
        font=dict(
            family="Arial, sans-serif",
            size=20,
            color="#333"
        ),
        title_font=dict(
            size=40,
            color="#2a3f5f"
        ),
        xaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        ),
        yaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        )
    )

    # try:
    #     output_folder = Path("graphs")
    #     output_folder.mkdir(exist_ok=True)
    #     fig_path = output_folder / "category_avg.png"
    #     fig.write_image(fig_path, format="png")
    # except Exception as e:
    #     print(e)
    #     raise e

    graph_html = fig.to_html(full_html=False)
    return f"<html><body>{graph_html}</body></html>"


@app.get("/applications_by_industry", response_class=HTMLResponse)
def applications_by_role(limit: int = 10, db: Session = Depends(get_db)):
    result = (
        db.query(User.role, func.count(Applications.user_id).label("applications_count"))
        .join(Applications, User.user_id == Applications.user_id)
        .group_by(User.role)
        .order_by(desc("applications_count"))
        .limit(limit)
        .all()
    )

    labels = [row.role for row in result]
    values = [row.applications_count for row in result]

    if not result:
        return "No result"

    fig = go.Figure([go.Bar(x=labels, y=values)])

    fig.update_layout(
        title={
            'text': "Applications per role",
            'y': 0.95,
            'x': 0.5,
            'xanchor': 'center',
            'yanchor': 'top'
        },
        xaxis_title="Role",
        yaxis_title="Applications",
        template="plotly_white",
        font=dict(
            family="Arial, sans-serif",
            size=20,
            color="#333"
        ),
        title_font=dict(
            size=40,
            color="#2a3f5f"
        ),
        xaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        ),
        yaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        )
    )

    # try:
    #     output_folder = Path("graphs")
    #     output_folder.mkdir(exist_ok=True)
    #     fig_path = output_folder / "category_avg.png"
    #     fig.write_image(fig_path, format="png")
    # except Exception as e:
    #     print(e)
    #     raise e

    graph_html = fig.to_html(full_html=False)
    return f"<html><body>{graph_html}</body></html>"


@app.get("/viewed_by_industry", response_class=HTMLResponse)
def viewed_by_role(limit: int = 10, db: Session = Depends(get_db)):
    result = (
        db.query(User.role, func.count(Viewed.user_id).label("viewed_count"))
        .join(Viewed, User.user_id == Viewed.user_id)
        .group_by(User.role)
        .order_by(desc("viewed_count"))
        .limit(limit)
        .all()
    )

    labels = [row.role for row in result]
    values = [row.applications_count for row in result]

    if not result:
        return "No result"

    fig = go.Figure([go.Bar(x=labels, y=values)])

    fig.update_layout(
        title={
            'text': "Viewed per role",
            'y': 0.95,
            'x': 0.5,
            'xanchor': 'center',
            'yanchor': 'top'
        },
        xaxis_title="Role",
        yaxis_title="Viewed",
        template="plotly_white",
        font=dict(
            family="Arial, sans-serif",
            size=20,
            color="#333"
        ),
        title_font=dict(
            size=40,
            color="#2a3f5f"
        ),
        xaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        ),
        yaxis=dict(
            showgrid=True,
            gridcolor='rgba(204, 204, 204, 0.5)'
        )
    )

    # try:
    #     output_folder = Path("graphs")
    #     output_folder.mkdir(exist_ok=True)
    #     fig_path = output_folder / "category_avg.png"
    #     fig.write_image(fig_path, format="png")
    # except Exception as e:
    #     print(e)
    #     raise e

    graph_html = fig.to_html(full_html=False)
    return f"<html><body>{graph_html}</body></html>"


@app.get("/recommendation/{user_id}")
def get_recommendation(user_id: str, limit: int = 10, db: Session = Depends(get_db)):
    recommendation = (
        db.query(Job)
        # .join(User, Job.category == User.role)
        # .filter(User.user_id == user_id)
        .order_by(func.random())
        .limit(limit)
        .all()
    )
    return recommendation
