FROM python:alpine3.20

RUN apk add libpq-dev python3-dev gcc musl-dev libc-dev make

COPY db_mockup/mockdata.json .

COPY db_mockup/requirements.txt .

RUN python3 -m pip install -r requirements.txt

CMD python --version && cat mockdata.json && cat requirements.txt && python3 -m db_mockdata.main -f mockdata.json