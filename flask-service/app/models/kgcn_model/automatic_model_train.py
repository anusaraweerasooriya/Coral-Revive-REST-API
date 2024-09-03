import time
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from kgcnt import train
from kgcnm import KGCN
from kgcnd import load_data

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

class FileChangeHandler(FileSystemEventHandler):
    def on_modified(self, event):
        if event.src_path.endswith('ratings_final.txt') or event.src_path.endswith('kg_final.txt'):
            print(f"File changed: {event.src_path}")
            retrain_and_update_model()

def retrain_and_update_model():
    args = Args()  
    rating_file_path = '/Users/seminipeiris/Desktop/Coral-Revive-REST-API/user-recommendation-service/ratings_final.txt'
    kg_file_path = '/Users/seminipeiris/Desktop/Coral-Revive-REST-API/user-recommendation-service/kg_final.txt'

    data = load_data(args, rating_file_path, kg_file_path)

    model = KGCN(args, data[0], data[2], data[3], data[7], data[8])
    train(args, data, show_loss=True, show_topk=True)

    model.save('/Users/seminipeiris/Desktop/Coral-Revive-REST-API/flask-service/app/models/kgcn_model') 

if __name__ == "__main__":
    path = "/Users/seminipeiris/Desktop/Coral-Revive-REST-API/user-recommendation-service/"  
    event_handler = FileChangeHandler()
    observer = Observer()
    observer.schedule(event_handler, path, recursive=False)
    observer.start()
    print("Watching for file changes...")
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        observer.stop()
    observer.join()
