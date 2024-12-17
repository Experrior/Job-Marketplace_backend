from locust import HttpUser, task, between

class HeavyDataUser(HttpUser):
    wait_time = between(5, 10)

    @task(1)
    def taskEndpoint(self):
        self.client.get(f"http://{self.host}:8080/job-service/getJobs")



class BadAuthUser(HttpUser):
    wait_time = between(5, 10)

    @task(1)
    def taskEndpoint(self):
        self.client.get(f"http://{self.host}:8080/job-service/getJobs")

    @task(1)
    def taskEndpoint(self):
        self.client.get(f"http://{self.host}:8080/user-service/register")

class ChatUser(HttpUser):
    wait_time = between(5, 10)

    @task(1)
    def taskEndpoint(self):
        self.client.get(f"http://{self.host}:8080/chat-service/getUserChats")

    @task(1)
    def taskEndpoint(self):
        self.client.get(f"http://{self.host}:8080/chat-service/startChat")