import os
import time
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from kgcnt import train
from kgcnm import KGCN
from kgcnd import load_data

# Class to hold configuration arguments
class Args:
    def __init__(self):
        self.aggregator = 'sum'
        self.n_epochs = 40
        self.neighbor_sample_size = 8
        self.dim = 16
        self.n_iter = 1
        self.batch_size = 128
        self.l2_weight = 1e-7
        self.lr = 2e-2
        self.ratio = 1

# Event handler for file changes
class FileChangeHandler(FileSystemEventHandler):
    def on_modified(self, event):
        if event.src_path.endswith('ratings_final.txt') or event.src_path.endswith('kg_final.txt'):
            print(f"File changed: {event.src_path}")
            retrain_and_update_model()

# Function to retrain and update the model
def retrain_and_update_model():
    args = Args()  
    
    # Get paths from environment variables or use defaults for Docker environment
    rating_file_path = os.getenv("RATINGS_FILE_PATH", "/app/user-recommendation-service/ratings_final.txt")
    kg_file_path = os.getenv("KG_FILE_PATH", "/app/user-recommendation-service/kg_final.txt")
    
    # Set a specific path for the model save location
    model_save_path = "/app/models/new_kgcn_model"

    # Load data
    data = load_data(args, rating_file_path, kg_file_path)

    # Create and train the model
    model = KGCN(args, data[0], data[2], data[3], data[7], data[8])
    train(args, data, show_loss=True, show_topk=True)

    # Save the model
    model.save(model_save_path)

# Main function to start watching for file changes
if __name__ == "__main__":
    # Use environment variable to set the path, or a default inside the container
    watch_path = os.getenv("WATCH_PATH", "/app/user-recommendation-service/")
    
    event_handler = FileChangeHandler()
    observer = Observer()
    observer.schedule(event_handler, watch_path, recursive=False)
    observer.start()
    
    print("Watching for file changes...")
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        observer.stop()
    observer.join()
