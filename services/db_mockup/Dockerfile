FROM python:alpine3.20

WORKDIR /mockdata
RUN mkdir -p /mockdata/config
RUN apk add libpq-dev python3-dev gcc musl-dev libc-dev make

COPY db_mockup/requirements.txt requirements.txt

RUN python3 -m pip install -r requirements.txt

COPY db_mockup/mockdata.json config/mockdata.json

# CMD python --version && cat /mockdata/mockdata.json &&
CMD cat /mockdata/requirements.txt && python3 -m db_mockdata.main -f config/mockdata.json