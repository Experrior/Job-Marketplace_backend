FROM ubuntu:20.04

RUN apt-get update && apt-get install -y python3 python3-dev python3-pip

COPY analytics/requirements.txt requirements.txt

RUN pip install --no-cache-dir --upgrade -r /requirements.txt

COPY analytics .    

CMD ["fastapi", "run", "main.py", "--port", "80"]
