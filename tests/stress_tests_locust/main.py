import asyncio
import string

import websockets
import json
from time import sleep
import requests

userId = '978fac63-0c05-42bd-bfc8-116e11f5e2e8'
out = requests.get(f"http://localhost:8088/getUserChats?userId={userId}")

chatIds = [x['chat_id'] for x in out.json()]

target_ip = "192.168.10.10"
target_port = '8088'
uri = f"ws://{target_ip}:{target_port}/ws"

message = {
    "operation" : "get",
    "message": {
    "createdBy": 12,
    "chatId": 1,
    "content": "Hello, World!"
}
}

async def connect_to_websocket():
    async with websockets.connect(uri) as ws:
        await ws.send(json.dumps(message))
        a = await ws.recv()
        print(a)
        sleep(10)
        await ws.send(json.dumps(message))
        a = await ws.recv()
        print(a)


asyncio.run(connect_to_websocket())
