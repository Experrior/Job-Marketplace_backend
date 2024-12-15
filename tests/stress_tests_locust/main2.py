import json
import requests
import asyncio
import websockets


class WebSocketProvider:
    def __init__(self):
        self.user_id = '0ae30776-5898-4e3d-806f-44fbb993dd08'
        self.username = 'John'
        self.chat_messages = {}
        self._channel = None
        asyncio.run(self._fetch_all_chats_and_connect())

    async def _fetch_all_chats_and_connect(self):
        try:
            response = requests.get(f'http://localhost:8088/getUserChats?userId={self.user_id}')
            if response.status_code == 200:
                for chat in response.json():
                    self.chat_messages[chat['chat_id']] = []
                    print(f"Chat ID: {chat['chat_id']} initialized.")
                await self._connect()
            else:
                print(f'Failed to load chat IDs: {response.status_code}')
        except requests.RequestException as e:
            print(f'Error fetching chat IDs: {e}')

    async def _connect(self):
        try:
            async with websockets.connect('ws://localhost:8088/ws') as websocket:
                self._channel = websocket
                # await self._send_init_message()
                async for message in websocket:
                    self._on_message_received(message)
        except Exception as e:
            print(f"Error connecting to WebSocket: {e}")

    # async def _send_init_message(self):
    #     if self._channel:
    #         for chat_id in self.chat_messages.keys():
    #             special_message = json.dumps({
    #                 'operation': 'get',
    #                 'message': {
    #                     'chatId': chat_id
    #                 }
    #             })
    #             await self._channel.send(special_message)

    async def send_message(self, content, chat_id):
        if self._channel:
            message = json.dumps({
                'operation': 'post',
                'message': {
                    'createdBy': self.user_id,
                    'chatId': chat_id,
                    'content': content,
                    'createdByDisplay': self.username
                }
            })
            await self._channel.send(message)

    def _on_message_received(self, message):
        decoded_messages = json.loads(message)

        for decoded_message in decoded_messages:
            chat_id = decoded_message['chatId']
            new_message = {
                'content': decoded_message['content'],
                'createdBy': decoded_message['createdBy'],
                'createdByDisplay': decoded_message['createdByDisplay'],
                'timestamp': decoded_message['timestamp']
            }

            if chat_id not in self.chat_messages:
                self.chat_messages[chat_id] = []

            self.chat_messages[chat_id].append(new_message)

            # Print the chatId and the new message
            print(f"New message in Chat {chat_id}: {new_message}")

    async def close(self):
        if self._channel:
            await self._channel.close()


# Example usage
if __name__ == "__main__":
    ws_provider = WebSocketProvider()
    # Add asyncio tasks to interact with WebSocket, e.g., send messages
