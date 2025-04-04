import argparse
import pandas as pd
import matplotlib.pyplot as plt

def plot_response_time(file_path, ignore_timestamp=False):
    # Чтение данных из файла
    data = pd.read_csv(file_path)
    
    # Построение графика
    plt.figure(figsize=(12, 6))
    
    if ignore_timestamp:
        # Игнорируем timestamp, используем индекс как ось X
        x = range(len(data))
        plt.plot(x, data['delay_ms'], 'b-', linewidth=1)
        plt.xlabel('Request Sequence Number')
    else:
        # Используем timestamp как ось X
        plt.plot(data['timestamp'], data['delay_ms'], 'b-', linewidth=1)
        plt.xlabel('Timestamp')
    
    # Настройка осей и заголовка
    plt.ylabel('Response Time (ms)')
    title = 'Server Response Time' + (' Over Time' if not ignore_timestamp else ' By Request Sequence')
    plt.title(title)
    plt.grid(True)
    
    # Автоматическое масштабирование осей
    plt.tight_layout()
    
    # Показать график
    plt.show()

if __name__ == "__main__":
    # Настройка парсера аргументов командной строки
    parser = argparse.ArgumentParser(description='Visualize server response times from a log file.')
    parser.add_argument('file', help='Path to the log file containing timestamp and delay_ms columns')
    parser.add_argument('--ignore-timestamp', action='store_true',default='true',
                       help='Ignore timestamp and plot with fixed step (request sequence)')
    
    args = parser.parse_args()
    
    # Вызов функции построения графика
    plot_response_time(args.file, args.ignore_timestamp)