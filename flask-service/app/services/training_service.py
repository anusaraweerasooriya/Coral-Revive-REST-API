import os
import tensorflow as tf
from app.models.kgcn_model import KGCN, train, load_data

MODEL_SAVE_PATH = '/Users/seminipeiris/Desktop/Coral-Revive-REST-API/flask-service/app/models/kgcn_model/model.ckpt'

class TrainingService:
    def __init__(self, model_save_path=MODEL_SAVE_PATH):
        self.model_save_path = model_save_path
        self.current_model = None
        self.session = None

    def load_or_train_model(self):
        if os.path.exists(self.model_save_path + ".index"):
            self._load_model()
        else:
            self.train_model()

    def train_model(self):
        self._close_session()

        args = self._get_args()
        rating_file_path = os.path.join('/Users/seminipeiris/Desktop/Coral-Revive-REST-API/user-recommendation-service/', 'ratings_final.txt')
        kg_file_path = os.path.join('/Users/seminipeiris/Desktop/Coral-Revive-REST-API/user-recommendation-service/', 'kg_final.txt')

        data = load_data(args, rating_file_path, kg_file_path)

        num_users = data[0]  
        num_items = data[2]  
        num_relations = data[3] 

        args.batch_size = min(args.batch_size, num_users)  
        args.dim = max(2, int(num_items ** 0.5)) 
        args.neighbor_sample_size = min(args.neighbor_sample_size, num_relations) 

        print(f"Updated args based on dataset: {vars(args)}")
        self.current_model = KGCN(args, num_users, num_items, num_relations, data[7], data[8])
        with tf.compat.v1.Session() as sess:
            self.session = sess
            sess.run(tf.compat.v1.global_variables_initializer())
            train(args, data, show_loss=True, show_topk=True)
            self._save_model()

    def _save_model(self):
        saver = tf.compat.v1.train.Saver()
        saver.save(self.session, self.model_save_path)

    def _load_model(self):
        # Close any existing session
        self._close_session()

        # Load the model
        saver = tf.compat.v1.train.Saver()
        self.session = tf.compat.v1.Session()
        saver.restore(self.session, self.model_save_path)
        self.current_model = self.session

    def _close_session(self):
        # Close the current session to avoid conflicts
        if self.session is not None:
            self.session.close()
            self.session = None

    def _get_args(self):
        # Define the arguments for training the model
        class Args:
            def __init__(self):
                self.aggregator = 'sum'
                self.n_epochs = 2
                self.neighbor_sample_size = 1
                self.dim = 2
                self.n_iter = 1
                self.batch_size = 2
                self.l2_weight = 1e-3
                self.lr = 1e-3
                self.ratio = 0.8
        
        return Args()

    def get_current_model(self):
        if self.current_model is None:
            self._load_model()
        return self.current_model
