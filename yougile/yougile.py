import argparse
from typing import Any, List, Dict, Iterable
import requests


API_KEY = '+xWruaQpAzhlT9FNx4oUMgNXAPrUZBeQBg0V88EPfy-+D6xA+cAk+BeibK09NOP6' # API-ключ
R_COLUMN_ID = '370af1e6-a24c-4800-9004-6163a78a52b7' # Id колонки "Релиз"
RC_COLUMN_ID = '7891eaaf-c1b5-483c-bc7e-29e8154f52db' # ID колонки "В релиз (релиз-кандидат)"
TEST_COLUMN_ID = '30861013-4b63-447d-85c4-5b096d2007c0' # ID колонки "Тестирование"
TASKS_URL = 'https://ru.yougile.com/api-v2/tasks' # Адрес для запроса списка карточек.
COLUMNS_URL = 'https://ru.yougile.com/api-v2/columns' # Адрес для запроса списка колонок.
HEADERS = { # Обязательные заголовки.
    'Authorization': 'Bearer ' + API_KEY,
    'Content-Type': 'application/json'
}


class Task:
    """ Карточка. """
    def __init__(self, json: Dict[str, Any]):
        self.id: str = json['id']
        self.spr: str = json['idTaskProject']
        self.title: str = json['title']


def get_columns() -> str:
    """ Запрос списка колонок. """
    with requests.get(COLUMNS_URL, headers=HEADERS, timeout=100) as resp:
        resp.raise_for_status()
        return resp.text


def task_url(id: str) -> str:
    """ Адрес для запроса данных карточки по ID. """
    return f'{TASKS_URL}/{id}'


def get_task(id: str) -> Task:
    """ Запрос карточки по id. """
    with requests.get(task_url(id), headers=HEADERS, timeout=100) as response:
        response.raise_for_status()
        return Task(response.json())


def get_tasks(col_id: str) -> List[Task]:
    """ Запрос списка карточек. 
        @param col_id: id колонки. """
    params = { 'columnId': col_id }
    with requests.get(TASKS_URL, params=params, headers=HEADERS, timeout=100) as resp:
        resp.raise_for_status()
        return [get_task(task['id']) for task in resp.json()['content']]


def make_report(tasks: List[Task]) -> Iterable[str]:
    """ Генерация отчёта из списка карточек для вставки в письмо. """
    return (f'{(i + 1):02}. {task.spr}. {task.title}.' for i, task in enumerate(tasks))


def move_task(task_id: str, col_id: str) -> int:
    """ Переместить карточку в список.
        @param task_id ID карточки.
        @param col_id ID колонки.
        @returns HTTP код ответа. """
    body = { 'columnId': col_id }
    with requests.put(task_url(task_id), json=body, headers=HEADERS, timeout=100) as resp:
        resp.raise_for_status()
        return resp.status_code


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-m', '--move', help='Переместить карточки в "Тестирование".', action='store_true')
    args = parser.parse_args()

    print('--- Запрос карточек из релиза')
    tasks = get_tasks(R_COLUMN_ID)
    for line in make_report(tasks):
        print(line)

    if args.move:
        print('--- Перемещение карточек в тестирование')
        for task in tasks:
            status_code = move_task(task.id, TEST_COLUMN_ID)
            print(f'{task.spr} -- {status_code}')
    
    print("--- Завершено")
