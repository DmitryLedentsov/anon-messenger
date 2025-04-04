import argparse
import pandas as pd
import matplotlib.pyplot as plt
import time
import os
from threading import Thread, Event
from queue import Queue

class RealTimePlotter:
    def __init__(self, file_path, ignore_timestamp):
        self.file_path = file_path
        self.ignore_timestamp = ignore_timestamp
        self.stop_event = Event()
        self.data_queue = Queue()
        self.last_size = 0
        self.data = pd.DataFrame(columns=['timestamp', 'delay_ms'])

    def file_monitor(self):
        while not self.stop_event.is_set():
            try:
                current_size = os.path.getsize(self.file_path)
                if current_size > self.last_size:
                    with open(self.file_path, 'r') as f:
                        f.seek(self.last_size)
                        new_lines = f.readlines()
                        self.last_size = current_size
                    
                    new_data = []
                    for line_str in new_lines:
                        if line_str.strip() and not line_str.startswith('timestamp'):
                            parts = line_str.strip().split(',')
                            if len(parts) == 2:
                                try:
                                    new_data.append({
                                        'timestamp': float(parts[0]),
                                        'delay_ms': float(parts[1])
                                    })
                                except ValueError:
                                    continue
                    
                    if new_data:
                        self.data_queue.put(pd.DataFrame(new_data))
            
            except Exception as e:
                print(f"Error reading file: {e}")
            
            time.sleep(0.1)

    def update_plot(self):
        plt.ion()
        fig, ax = plt.subplots(figsize=(12, 6))
        line, = ax.plot([], [], 'b-', linewidth=1)
        
        ax.set_xlabel('Timestamp' if not self.ignore_timestamp else 'Request Sequence Number')
        ax.set_ylabel('Response Time (ms)')
        ax.set_title('Server Response Time' + (' Over Time' if not self.ignore_timestamp else ' By Request Sequence'))
        ax.grid(True)
        
        while not self.stop_event.is_set():
            try:
                if not self.data_queue.empty():
                    new_df = self.data_queue.get()
                    self.data = pd.concat([self.data, new_df], ignore_index=True)
                    
                    if self.ignore_timestamp:
                        line.set_data(range(len(self.data)), self.data['delay_ms'])
                    else:
                        line.set_data(self.data['timestamp'], self.data['delay_ms'])
                    
                    ax.relim()
                    ax.autoscale_view()
                    fig.canvas.draw()
                    fig.canvas.flush_events()
            
            except Exception as e:
                print(f"Error updating plot: {e}")
            
            time.sleep(0.05)

def plot_response_time(file_path, ignore_timestamp=False, real_time=False):
    if not real_time:
        # Static mode
        data = pd.read_csv(file_path)
        plt.figure(figsize=(12, 6))
        
        if ignore_timestamp:
            plt.plot(range(len(data)), data['delay_ms'], 'b-', linewidth=1)
            plt.xlabel('Request Sequence Number')
        else:
            plt.plot(data['timestamp'], data['delay_ms'], 'b-', linewidth=1)
            plt.xlabel('Timestamp')
        
        plt.ylabel('Response Time (ms)')
        plt.title('Server Response Time' + (' Over Time' if not ignore_timestamp else ' By Request Sequence'))
        plt.grid(True)
        plt.tight_layout()
        plt.show()
    else:
        # Real-time mode
        plotter = RealTimePlotter(file_path, ignore_timestamp)
        
        monitor_thread = Thread(target=plotter.file_monitor)
        plot_thread = Thread(target=plotter.update_plot)
        
        monitor_thread.start()
        plot_thread.start()
        
        try:
            while True:
                time.sleep(0.1)
        except KeyboardInterrupt:
            print("\nStopping real-time monitoring...")
            plotter.stop_event.set()
            monitor_thread.join()
            plot_thread.join()
            plt.close('all')

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Visualize server response times from a log file.')
    parser.add_argument('file', help='Path to the log file containing timestamp and delay_ms columns')
    parser.add_argument('--ignore-timestamp', action='store_true',
                       help='Ignore timestamp and plot with fixed step (request sequence)')
    parser.add_argument('--real-time', action='store_true',
                       help='Monitor file updates and plot in real time')
    
    args = parser.parse_args()
    
    plot_response_time(args.file, args.ignore_timestamp, args.real_time)