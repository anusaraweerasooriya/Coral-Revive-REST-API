import os
import tensorflow as tf
import numpy as np
from app.models.kgcn_model import KGCN, load_data

# Specify the path for the model save path directly
MODEL_SAVE_PATH = "/app/models/kgcn_model/model.ckpt"
RATINGS_FILE_PATH = os.getenv("RATINGS_FILE_PATH", "/app/user-recommendation-service/ratings_final.txt")
KG_FILE_PATH = os.getenv("KG_FILE_PATH", "/app/user-recommendation-service/kg_final.txt")

class KGCNModelService:
    def __init__(self, model_save_path=MODEL_SAVE_PATH):
        self.model_save_path = model_save_path
        self.current_model = None
        self.session = None
        self.graph = tf.Graph()  # Create a new graph
        self._load_model()

    def _load_model(self):
        self._close_session()

        with self.graph.as_default():
            self.session = tf.compat.v1.Session(graph=self.graph)
            
            # Build the model architecture before restoring
            self._initialize_model()
            
            saver = tf.compat.v1.train.Saver()
            
            # Load the latest checkpoint if it exists
            checkpoint_path = tf.train.latest_checkpoint(os.path.dirname(self.model_save_path))
            if checkpoint_path:
                saver.restore(self.session, checkpoint_path)
                print(f"Model restored from {checkpoint_path}")
            else:
                print(f"No valid checkpoint found at {self.model_save_path}")
                raise ValueError(f"The passed save_path is not a valid checkpoint: {self.model_save_path}")

    def _initialize_model(self):
        # Assuming args are needed for model initialization
        args = self._get_args()
        rating_file_path = RATINGS_FILE_PATH
        kg_file_path = KG_FILE_PATH

        data = load_data(args, rating_file_path, kg_file_path)

        # Initialize the KGCN model
        self.current_model = KGCN(args, data[0], data[2], data[3], data[7], data[8])

    def _close_session(self):
        if self.session is not None:
            self.session.close()
            self.session = None

    def validate_indices(self, indices, max_index):
        invalid_indices = [i for i in indices if i >= max_index or i < 0]
        if invalid_indices:
            raise ValueError(f"Invalid indices found: {invalid_indices}")

    def recommend_posts(self, user_index, item_indices):
        if self.current_model is None:
            self._load_model()

        # Validate and adjust user and item indices
        max_user_index = self.current_model.user_emb_matrix.shape[0]
        max_item_index = self.current_model.entity_emb_matrix.shape[0]

        print(f"Validating user index {user_index} with max_user_index {max_user_index}")
        print(f"Validating item indices {item_indices} with max_item_index {max_item_index}")

        # Check if the user index is valid
        if user_index >= max_user_index or user_index < 0:
            print(f"Invalid user index: {user_index}. Must be in range [0, {max_user_index - 1}].")
            return {"error": f"Invalid user index: {user_index}. Must be in range [0, {max_user_index - 1}]."}
        
        # Filter out invalid item indices
        valid_item_indices = [i for i in item_indices if 0 <= i < max_item_index]
        if not valid_item_indices:
            print(f"All item indices are invalid. Valid item index range is [0, {max_item_index - 1}].")
            return {"error": "All item indices are invalid."}

        if len(valid_item_indices) != len(item_indices):
            print(f"Some item indices were out of bounds and have been ignored. Valid item indices: {valid_item_indices}")

        feed_dict = {
            self.current_model.user_indices: [user_index],
            self.current_model.item_indices: valid_item_indices
        }

        scores = self.session.run(self.current_model.scores_normalized, feed_dict=feed_dict)
        return scores

    def get_recommended_items(self, user_index, item_indices):
        scores = self.recommend_posts(user_index, item_indices)
        recommended_items = np.argsort(scores)[::-1]  # Sort in descending order of scores
        return recommended_items

    def _get_args(self):
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
        
        return Args()
