import os
import tensorflow as tf
import numpy as np
from app.models.kgcn_model import KGCN, load_data
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Paths for saved model, ratings, and KG files
MODEL_SAVE_PATH = "/app/models/kgcn_model/model.ckpt"
RATINGS_FILE_PATH = os.getenv("RATINGS_FILE_PATH", "/app/user-recommendation-service/ratings_final.txt")
KG_FILE_PATH = os.getenv("KG_FILE_PATH", "/app/user-recommendation-service/kg_final.txt")

class KGCNModelService:
    def __init__(self, model_save_path=MODEL_SAVE_PATH):
        self.model_save_path = model_save_path
        self.current_model = None
        self.session = None
        self.graph = tf.Graph()  # Create a new graph for model operations
        self._load_model()

    def _load_model(self):
        # Close any existing session
        self._close_session()

        with self.graph.as_default():
            self.session = tf.compat.v1.Session(graph=self.graph)
            # Initialize model architecture within the graph before restoring weights
            self._initialize_model()
            
            saver = tf.compat.v1.train.Saver()
            # Load the latest checkpoint if it exists
            checkpoint_path = tf.train.latest_checkpoint(os.path.dirname(self.model_save_path))
            if checkpoint_path:
                saver.restore(self.session, checkpoint_path)
                logger.info(f"Model restored from {checkpoint_path}")
            else:
                logger.error(f"No valid checkpoint found at {self.model_save_path}")
                raise ValueError(f"The passed save_path is not a valid checkpoint: {self.model_save_path}")

    def _initialize_model(self):
        # Arguments for model initialization
        args = self._get_args()
        rating_file_path = RATINGS_FILE_PATH
        kg_file_path = KG_FILE_PATH

        # Calculate max indices from data files for embeddings
        n_user, n_item, n_entity, n_relation = self.get_max_indices(rating_file_path, kg_file_path)
        args.n_user = n_user
        args.n_item = n_item
        args.n_entity = n_entity
        args.n_relation = n_relation

        # Log dimensions for debugging
        logger.info(f"Initializing model with dimensions: Users={n_user}, Items={n_item}, Entities={n_entity}, Relations={n_relation}")

        # Load data for initializing model (KG and ratings)
        data = load_data(args, rating_file_path, kg_file_path)
        # Log data shapes
        logger.info(f"Data shapes - Ratings: {data[0].shape}, KG: {data[2].shape}, Entity Embeddings: {data[3].shape}")

        # Build the KGCN model with the calculated dimensions and adjacency matrices
        self.current_model = KGCN(args, data[0], data[2], data[3], data[7], data[8])

    def get_max_indices(self, rating_file_path, kg_file_path):
        logger.info(f"Attempting to load rating file from: {rating_file_path}")
        logger.info(f"Attempting to load KG file from: {kg_file_path}")

        if not os.path.exists(rating_file_path) or not os.path.exists(kg_file_path):
            logger.error("One or more data files not found.")
            raise FileNotFoundError("One or more data files not found.")

        ratings = np.loadtxt(rating_file_path, dtype=int)
        kg = np.loadtxt(kg_file_path, dtype=int)

        if ratings.shape[1] != 3:
            logger.error("Rating file must have exactly 3 columns (user, item, rating).")
            raise ValueError("Rating file must have exactly 3 columns (user, item, rating).")
        if kg.shape[1] != 3:
            logger.error("KG file must have exactly 3 columns (head, relation, tail).")
            raise ValueError("KG file must have exactly 3 columns (head, relation, tail).")

        # Summarize loaded data
        logger.info(f"Ratings loaded with {ratings.shape[0]} rows.")
        logger.info(f"KG data loaded with {kg.shape[0]} triples.")
        logger.info(f"Unique users: {len(np.unique(ratings[:, 0]))}, Unique items: {len(np.unique(ratings[:, 1]))}")
        logger.info(f"Unique entities: {len(np.unique(np.concatenate((kg[:, 0], kg[:, 2]))))}, Unique relations: {len(np.unique(kg[:, 1]))}")

        n_user = ratings[:, 0].max() + 1
        n_item = ratings[:, 1].max() + 1
        n_entity = max(kg[:, 0].max(), kg[:, 2].max()) + 1
        n_relation = kg[:, 1].max() + 1

        return n_user, n_item, n_entity, n_relation

    def _close_session(self):
        if self.session is not None:
            self.session.close()
            self.session = None

    def recommend_posts(self, user_index):
        if self.current_model is None:
            self._load_model()

        # Log shapes of critical model components
        logger.info(f"Entity embedding matrix shape: {self.current_model.entity_emb_matrix.shape}")
        max_item_index = self.current_model.entity_emb_matrix.shape[0]
        item_indices = list(range(max_item_index))  

        # Log the length of item indices for confirmation
        logger.info(f"Total items for recommendation (item_indices length): {len(item_indices)}")

        # Adjust item indices to fit batch size
        remainder = len(item_indices) % self._get_args().batch_size
        if remainder != 0:
            item_indices = item_indices[:len(item_indices) - remainder]  # Adjust for batch size

        # Log item indices after adjustment
        logger.info(f"Adjusted item indices length for batching: {len(item_indices)}")

        max_user_index = self.current_model.user_emb_matrix.shape[0]
        if user_index >= max_user_index or user_index < 0:
            logger.error(f"Invalid user index: {user_index}. Must be in range [0, {max_user_index - 1}].")
            return {"error": f"Invalid user index: {user_index}. Must be in range [0, {max_user_index - 1}]."}

        # Prepare the feed dictionary
        feed_dict = {
            self.current_model.user_indices: [user_index],
            self.current_model.item_indices: item_indices
        }

        # Retrieve scores via get_scores method
        try:
            item_indices, scores = self.current_model.get_scores(self.session, feed_dict)
            logger.info("Model ran successfully and scores were retrieved.")
        except tf.errors.InvalidArgumentError as e:
            logger.error("Error during model execution: InvalidArgumentError")
            logger.error(e)
            return {"error": "Model execution error: InvalidArgumentError"}
        except Exception as e:
            logger.error("Unexpected error during model execution.")
            logger.error(e)
            return {"error": f"Unexpected error: {e}"}

        return scores

    def get_recommended_items(self, user_index):
        scores = self.recommend_posts(user_index)
        recommended_items = np.argsort(scores)[::-1]
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
