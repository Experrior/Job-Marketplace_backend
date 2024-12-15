FROM python:3.12.3
# todo change to python:alpine3.20
COPY requirements.txt .
RUN python3 -m pip install --no-cache-dir -r requirements.txt

COPY main4.py .

CMD locust -f main4.py
