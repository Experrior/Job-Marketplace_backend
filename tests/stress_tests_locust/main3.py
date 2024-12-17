import json
import random
from time import sleep

from locust import task, between, HttpUser, FastHttpUser
from locust_plugins.users.socketio import SocketIOUser
from websockets.sync.client import connect


class MySocketIOUser(FastHttpUser):
    wait_time = between(5, 10)

    def on_start(self):
        self.user_id = '29b00b60-b4c5-431d-afed-081e43887398'
        response = self.client.get(f"http://{self.host}/getUserChats?userId={self.user_id}")
        self.chat_ids = [chat for chat in response.json()]
        self.client1 = connect(f"ws://{self.host}/ws")

        # self.connect(f"ws://{self.host}/ws")

    # @task(2)
    # def get_chat_messages(self):
    #     self.my_value = None
    #
    #     self.connect("ws://localhost:8088/getUserChats")
    #
    #     # example of subscribe
    #     self.send('42["subscribe",{"url":"/sport/matches/11995208/draws","sendInitialUpdate": true}]')
    #
    #     # wait until I get a push message to on_message
    #     while not self.my_value:
    #         time.sleep(0.1)
    #
    #     # wait for additional pushes, while occasionally sending heartbeats, like a real client would
    #     self.sleep_with_heartbeat(10)

    @task(1)
    def post_message(self):
        for chat_id in self.chat_ids:
            json_message = {"operation": "post",
                            "message": {
                                "createdBy": self.user_id,
                                "chatId": chat_id,
                                "content": "random message",
                                "createdByDisplay": "john"
                            }
                            }
            # socketIO bullshit, check in socketio.py class def
            self.client1.send(json.dumps(json_message))
            sleep(random.randint(5, 10))
            # self.send('42["subscribe",{"url":"/sport/matches/11995208/draws","sendInitialUpdate": true}]')
            # print(response.json())

    # def on_message(self, message):
    #     self.my_value = json.loads(message)["my_value"]
