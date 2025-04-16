import matplotlib.pyplot as plt
import pandas as pd
import argparse

def visualize_response_times(file_path):
    # Чтение данных из файла
    data = pd.read_csv(file_path)
    
    # Создание графика
    plt.figure(figsize=(12, 6))
    
    # Построение графика времени ответа
    plt.plot(data['delay_ms'], 'b-', label='Response Time (ms)')
    
    # Отметка ошибок вертикальными красными линиями
    errors = data[data['error'] == 1]
    for idx, row in errors.iterrows():
        plt.axvline(x=idx, color='r', alpha=0.5)
    
    # Настройка графика
    plt.title('Server Response Time with Error Indicators')
    plt.xlabel('Request Number')
    plt.ylabel('Response Time (ms)')
    plt.legend()
    plt.grid(True)
    
    # Показать график
    plt.tight_layout()
    plt.show()

if __name__ == "__main__":
    # Настройка аргументов командной строки
    parser = argparse.ArgumentParser(description='Visualize server response times with error indicators.')
    parser.add_argument('file', type=str, help='Path to the data file (e.g., file.txt)')
    
    args = parser.parse_args()
    
    # Запуск визуализации
    visualize_response_times(args.file)