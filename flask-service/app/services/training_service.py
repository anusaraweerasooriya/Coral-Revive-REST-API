import os
import tensorflow as tf
from app.models.kgcn_model import KGCN, train, load_data

# Explicitly set the model save path and keep the other two paths as they are
MODEL_SAVE_PATH = '/app/models/kgcn_model/model.ckpt'
RATINGS_FILE_PATH = os.getenv('RATINGS_FILE_PATH', '/app/user-recommendation-service/ratings_final.txt')
KG_FILE_PATH = os.getenv('KG_FILE_PATH', '/app/user-recommendation-service/kg_final.txt')

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

        # Load data
        rating_file_path = RATINGS_FILE_PATH
        kg_file_path = KG_FILE_PATH
        data = load_data(args, rating_file_path, kg_file_path)

        # Calculate dataset sizes and maximum indices
        max_user_index = max(max(data[4][:, 0]), data[0] - 1)
        max_item_index = max(max(data[4][:, 1]), data[2] - 1)
        max_entity_index = max(data[2], max(data[7].flatten()))  # Check both item and entity indices
        max_relations = data[3]

        # Set adaptive sizes based on dataset properties
        num_users = max_user_index + 1
        num_items = max_item_index + 1
        num_entities = max_entity_index + 1

        # Adaptive batch size: set as a small fraction of total users or a minimum threshold
        args.batch_size = max(2, min(128, int(0.05 * num_users)))  # 5% of users or max 128

        # Adaptive neighbor sample size: e.g., 20% of average neighbors up to max_relations
        avg_neighbors = int(num_entities / max(1, num_items))  # approximate neighbors per item
        args.neighbor_sample_size = min(max_relations, max(1, int(0.2 * avg_neighbors)))

        # Adaptive embedding dimension based on the square root of total items
        args.dim = max(4, int(num_items ** 0.5))  # Increase with data size, up to practical limits

        print(f"Adaptive args: batch_size={args.batch_size}, neighbor_sample_size={args.neighbor_sample_size}, dim={args.dim}")

        # Initialize the model with dynamically set dimensions
        self.current_model = KGCN(args, num_users, num_entities, max_relations, data[7], data[8])

        # Train the model and save within the training session
        with tf.compat.v1.Session() as sess:
            sess.run(tf.compat.v1.global_variables_initializer())
            train(args, data, show_loss=True, show_topk=True)
            self._save_model(sess)

    def _save_model(self, session):
        saver = tf.compat.v1.train.Saver()
        saver.save(session, self.model_save_path)
        print(f"Model saved to {self.model_save_path}")

    def _load_model(self):
        # Close any existing session
        self._close_session()

        # Load the model
        saver = tf.compat.v1.train.Saver()
        self.session = tf.compat.v1.Session()
        saver.restore(self.session, self.model_save_path)
        print(f"Model restored from {self.model_save_path}")

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
                self.neighbor_sample_size = 1  # Placeholder; will be updated dynamically
                self.dim = 2  # Placeholder; will be updated dynamically
                self.n_iter = 1
                self.batch_size = 2  # Placeholder; will be updated dynamically
                self.l2_weight = 1e-3
                self.lr = 1e-3
                self.ratio = 0.8

        return Args()

    def get_current_model(self):
        if self.current_model is None:
            self._load_model()
        return self.current_model
