import argparse
import random
import time
import requests
import threading
from concurrent.futures import ThreadPoolExecutor
import logging
import sys
import signal
import csv
from urllib3.connection import HTTPConnection
import socket
# Увеличиваем лимиты Windows на сокеты
socket.setdefaulttimeout(30)  # 30 секунд таймаут
HTTPConnection.default_socket_options = (
    HTTPConnection.default_socket_options + [
        (socket.SOL_SOCKET, socket.SO_REUSEADDR, 1),
        (socket.SOL_SOCKET, socket.SO_KEEPALIVE, 1),
    ]
)

# Настройка сессии requests
session = requests.Session()
adapter = requests.adapters.HTTPAdapter(
    pool_connections=50,
    pool_maxsize=50,
    max_retries=3
)
session.mount('http://', adapter)
session.mount('https://', adapter)
# Настройка логирования
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Функция для измерения времени ответа сервера
def measureResponseDelay(func):
    def wrapper(*args, **kwargs):
        start_time = time.time()
        result = func(*args, **kwargs)
        end_time = time.time()
        delay_ms = (end_time - start_time) * 1000  # Преобразуем в миллисекунды
        
        # Логируем в файл
        with open('response_times.csv', 'a', newline='') as csvfile:
            writer = csv.writer(csvfile)
            writer.writerow([start_time, delay_ms])
        
        return result
    return wrapper

# Обертки для всех методов, выполняющих HTTP-запросы
class MeasuredRequests:
    @staticmethod
    @measureResponseDelay
    def post(url, **kwargs):
        return session.post(url, **kwargs)
    
    @staticmethod
    @measureResponseDelay
    def get(url, **kwargs):
        return session.get(url, **kwargs)
    
    @staticmethod
    @measureResponseDelay
    def delete(url, **kwargs):
        return session.delete(url, **kwargs)

class LoadTester:
    def __init__(self, args):
        self.base_url = args.server_base_url
        self.users_count = args.users_count
        self.workers = args.workers
        self.messages_count = args.messages_count
        self.min_interval = args.min_interval / 1000  # Перевод в секунды
        self.random_interval = args.random_interval / 1000  # Перевод в секунды
        self.clear_only = args.clear
        self.user_prefix = args.user_prefix
        self.admin = args.admin
        self.chat_name = args.chat_name  # Новое поле для имени чата
        self.users = []
        self.chat_id = None
        self.executor = None  # Для управления ThreadPoolExecutor
        self.shutdown_event = threading.Event()  # Для сигнализации остановки

        # Инициализация файла для логирования времени ответа
        with open('response_times.csv', 'w', newline='') as csvfile:
            writer = csv.writer(csvfile)
            writer.writerow(['timestamp', 'delay_ms'])

    def signin_user(self, login, password="test_password"):
        """Авторизация пользователя для получения токена"""
        payload = {
            "login": login,
            "password": password
        }
        try:
            response = session.post(f"{self.base_url}/auth/signin", json=payload)
            if response.status_code == 200:
                return response.json()["token"]
            return None
        except requests.RequestException as e:
            logger.debug(f"Не удалось войти под пользователем {login}: {e}")
            return None

    def delete_user(self, user):
        """Удаление одного пользователя"""
        headers = {"Authorization": f"Bearer {user['token']}"}
        try:
            response = session.delete(f"{self.base_url}/user", headers=headers, json='')
            response.raise_for_status()
            logger.info(f"Пользователь {user['login']} удален")
            return True
        except requests.RequestException as e:
            logger.error(f"Ошибка при удалении пользователя {user['login']}: {e}")
            return False

    def delete_chat(self, token=None):
        """Удаление чата с использованием предоставленного токена"""
        if not self.chat_id:
            logger.debug("Chat ID не задан, удаление чата не требуется")
            return
        
        if not token:
            logger.error("Токен для удаления чата не предоставлен")
            return

        headers = {"Authorization": f"Bearer {token}"}
        try:
            response = session.delete(f"{self.base_url}/chat/{self.chat_id}", headers=headers, json='')
            response.raise_for_status()
            logger.info(f"Чат {self.chat_id} удален")
            self.chat_id = None
        except requests.RequestException as e:
            logger.error(f"Ошибка при удалении чата {self.chat_id}: {e} (статус: {e.response.status_code if e.response else 'нет ответа'})")

    def register_user(self, user_num):
        """Регистрация одного пользователя с предварительной очисткой"""
        login = f"{self.user_prefix}_{user_num}"
        password = "test_password"
        payload = {
            "login": login,
            "password": password
        }
        
        # Проверяем, существует ли пользователь, и удаляем его
        existing_token = self.signin_user(login, password)
        if existing_token:
            headers = {"Authorization": f"Bearer {existing_token}"}
            try:
                session.delete(f"{self.base_url}/user", headers=headers)
                logger.info(f"Существующий пользователь {login} удален")
            except requests.RequestException as e:
                logger.error(f"Не удалось удалить существующего пользователя {login}: {e}")

        # Регистрация нового пользователя
        try:
            response = session.post(f"{self.base_url}/auth/signup", json=payload)
            response.raise_for_status()
            
            # Авторизация пользователя
            response = session.post(f"{self.base_url}/auth/signin", json=payload)
            response.raise_for_status()
            token_data = response.json()
            
            return {
                "login": login,
                "token": token_data["token"],
                "user_id": token_data["userId"]
            }
        except requests.RequestException as e:
            logger.error(f"Ошибка при регистрации пользователя {login}: {e}")
            return None

    def register_users(self):
        """Регистрация всех пользователей"""
        logger.info(f"Регистрация {self.users_count} пользователей...")
        with ThreadPoolExecutor(max_workers=self.workers) as executor:
            self.executor = executor
            futures = [executor.submit(self.register_user, i) for i in range(self.users_count)]
            self.users = [f.result() for f in futures if f.result() is not None]
        logger.info(f"Успешно зарегистрировано {len(self.users)} пользователей")

    def create_chat(self):
        """Создание чата с использованием второго пользователя как создателя"""
        if len(self.users) < 2:
            logger.error("Недостаточно пользователей для создания чата (нужен как минимум второй пользователь)")
            return

        creator = self.users[1]  # Используем второго пользователя как создателя
        headers = {"Authorization": f"Bearer {creator['token']}"}
        users_list = [user["login"] for user in self.users]
        if self.admin:
            users_list.append(self.admin)
        
        payload = {
            "name": self.chat_name,  # Используем заданное имя чата
            "users": users_list
        }
        
        # Удаление существующего чата, если он есть
        self.delete_chat(token=creator['token'])
        
        try:
            response = session.post(f"{self.base_url}/chat", json=payload, headers=headers)
            response.raise_for_status()
            self.chat_id = response.json()["id"]
            logger.info(f"Чат '{self.chat_name}' создан с ID: {self.chat_id} пользователем {creator['login']}")
        except requests.RequestException as e:
            logger.error(f"Ошибка при создании чата '{self.chat_name}': {e}")

    def send_message(self, user):
        """Отправка сообщения от одного пользователя"""
        headers = {"Authorization": f"Bearer {user['token']}"}
        payload = {
            "message": f"Test message from {user['login']} at {time.time()}"
        }
        
        for _ in range(self.messages_count):
            if self.shutdown_event.is_set():
                logger.info(f"Прерывание отправки сообщений для {user['login']}")
                break
            try:
                response = MeasuredRequests.post(
                    f"{self.base_url}/chat/{self.chat_id}/send",
                    json=payload,
                    headers=headers
                )
                response.raise_for_status()
                logger.debug(f"Сообщение отправлено от {user['login']}")
            except requests.RequestException as e:
                logger.error(f"Ошибка при отправке сообщения от {user['login']}: {e}")
            
            # Случайная задержка
            delay = self.min_interval + (random.random() * self.random_interval)
            time.sleep(delay)

    def cleanup(self):
        """Очистка всех ресурсов"""
        logger.info("Начинаем очистку...")
        
        # Проверяем и удаляем существующих пользователей
        existing_users = []
        for i in range(self.users_count):
            login = f"{self.user_prefix}_{i}"
            token = self.signin_user(login)
            if token:
                existing_users.append({"login": login, "token": token})
        
        # Удаляем чат, если он существует
        if self.chat_id:
            if self.clear_only:
                # В режиме --clear используем {prefix}_0 как возможного создателя
                creator_login = f"{self.user_prefix}_0"
                creator_token = self.signin_user(creator_login)
                if creator_token:
                    self.delete_chat(token=creator_token)
                else:
                    logger.warning(f"Не удалось авторизоваться под {creator_login} для удаления чата")
            elif len(existing_users) > 1:
                # В обычном режиме используем второго пользователя как создателя
                self.delete_chat(token=existing_users[1]["token"])
            elif self.admin:
                # Пробуем удалить чат с использованием токена админа, если он задан
                admin_token = self.signin_user(self.admin)
                if admin_token:
                    self.delete_chat(token=admin_token)
        
        # Удаляем всех пользователей
        with ThreadPoolExecutor(max_workers=self.workers) as executor:
            self.executor = executor
            executor.map(self.delete_user, existing_users + self.users)
        
        self.users = []
        logger.info("Очистка завершена")

    def run_test(self):
        """Запуск теста"""
        def signal_handler(sig, frame):
            logger.info("Получен сигнал прерывания (Ctrl+C)")
            self.shutdown_event.set()
            if self.executor:
                self.executor.shutdown(wait=False)
            self.cleanup()
            sys.exit(0)

        # Устанавливаем обработчик сигнала
        signal.signal(signal.SIGINT, signal_handler)

        if self.clear_only:
            logger.info("Запущен режим только очистки")
            self.cleanup()
            return

        try:
            # Регистрация пользователей
            self.register_users()
            
            # Создание чата
            self.create_chat()
            
            if not self.chat_id:
                logger.error("Не удалось создать чат, тест прерван")
                return

            # Отправка сообщений
            logger.info(f"Начинаем отправку {self.messages_count} сообщений от каждого пользователя...")
            start_time = time.time()
            
            with ThreadPoolExecutor(max_workers=self.workers) as executor:
                self.executor = executor
                futures = [executor.submit(self.send_message, user) for user in self.users]
                for future in futures:
                    try:
                        future.result()  # Ждем завершения задач, но прерываем при shutdown_event
                    except Exception as e:
                        logger.error(f"Ошибка в задаче: {e}")
            
            end_time = time.time()
            duration = end_time - start_time
            total_messages = self.messages_count * len(self.users)
            logger.info(f"Тест завершен за {duration:.2f} секунд")
            logger.info(f"Отправлено сообщений: {total_messages}")
            logger.info(f"Средняя скорость: {total_messages/duration:.2f} сообщений/сек")
        
        except KeyboardInterrupt:
            logger.info("Прерывание теста пользователем (Ctrl+C)")
            self.shutdown_event.set()
            if self.executor:
                self.executor.shutdown(wait=False)
            self.cleanup()
            sys.exit(0)
        finally:
            # Очистка в любом случае, если не в режиме --clear
            if not self.clear_only:
                self.cleanup()

def parse_args():
    parser = argparse.ArgumentParser(description='Load testing script for chat application')
    parser.add_argument('--users-count', type=int, default=20, help='Number of users to register')
    parser.add_argument('--workers', type=int, default=20, help='Number of worker threads')
    parser.add_argument('--messages-count', type=int, default=10, help='Number of messages per user')
    parser.add_argument('--min-interval', type=int, default=100, help='Minimum interval between messages (ms)')
    parser.add_argument('--random-interval', type=int, default=100, help='Random interval between messages (ms)')
    parser.add_argument('--server-base-url', type=str, default='http://localhost:9087', help='Server base URL')
    parser.add_argument('--clear', action='store_true', default=False, help='Only clear existing users and chat')
    parser.add_argument('--user-prefix', type=str, default='test_user', help='Prefix for test user logins')
    parser.add_argument('--admin', type=str, default=None, help='Existing admin user to add to chat')
    parser.add_argument('--chat-name', type=str, default='Test Chat', help='Name of the chat to create')
    return parser.parse_args()

if __name__ == "__main__":
    args = parse_args()
    tester = LoadTester(args)
    tester.run_test()