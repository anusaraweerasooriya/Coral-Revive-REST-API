import tensorflow as tf
from abc import abstractmethod

LAYER_IDS = {}

def get_layer_id(layer_name=''):
    if layer_name not in LAYER_IDS:
        LAYER_IDS[layer_name] = 0
        return 0
    else:
        LAYER_IDS[layer_name] += 1
        return LAYER_IDS[layer_name]

class Aggregator(object):
    def __init__(self, batch_size, dim, dropout, act, name):
        if not name:
            layer = self.__class__.__name__.lower()
            name = layer + '_' + str(get_layer_id(layer))
        self.name = name
        self.dropout = dropout
        self.act = act
        self.batch_size = batch_size
        self.dim = dim

    def __call__(self, self_vectors, neighbor_vectors, neighbor_relations, user_embeddings):
        outputs = self._call(self_vectors, neighbor_vectors, neighbor_relations, user_embeddings)
        return outputs

    @abstractmethod
    def _call(self, self_vectors, neighbor_vectors, neighbor_relations, user_embeddings):
        pass

    def _mix_neighbor_vectors(self, neighbor_vectors, neighbor_relations, user_embeddings):
        avg = False
        if not avg:
            user_embeddings = tf.reshape(user_embeddings, [self.batch_size, 1, 1, self.dim])
            user_relation_scores = tf.reduce_mean(user_embeddings * neighbor_relations, axis=-1)
            user_relation_scores_normalized = tf.nn.softmax(user_relation_scores, axis=-1)
            user_relation_scores_normalized = tf.expand_dims(user_relation_scores_normalized, axis=-1)
            neighbors_aggregated = tf.reduce_mean(user_relation_scores_normalized * neighbor_vectors, axis=2)
        else:
            neighbors_aggregated = tf.reduce_mean(neighbor_vectors, axis=2)

        return neighbors_aggregated

class SumAggregator(Aggregator):
    def __init__(self, batch_size, dim, dropout=0., act=tf.nn.relu, name=None):
        super(SumAggregator, self).__init__(batch_size, dim, dropout, act, name)

        self.weights = tf.Variable(tf.keras.initializers.GlorotUniform()(shape=[self.dim, self.dim]), name='weights')
        self.bias = tf.Variable(tf.zeros_initializer()(shape=[self.dim]), name='bias')

    def _call(self, self_vectors, neighbor_vectors, neighbor_relations, user_embeddings):
        neighbors_agg = self._mix_neighbor_vectors(neighbor_vectors, neighbor_relations, user_embeddings)
        output = tf.reshape(self_vectors + neighbors_agg, [-1, self.dim])
        output = tf.nn.dropout(output, rate=self.dropout)  # Convert dropout from keep_prob
        output = tf.matmul(output, self.weights) + self.bias
        output = tf.reshape(output, [self.batch_size, -1, self.dim])

        return self.act(output)

class ConcatAggregator(Aggregator):
    def __init__(self, batch_size, dim, dropout=0., act=tf.nn.relu, name=None):
        super(ConcatAggregator, self).__init__(batch_size, dim, dropout, act, name)

        self.weights = tf.Variable(tf.keras.initializers.GlorotUniform()(shape=[self.dim * 2, self.dim]), name='weights')
        self.bias = tf.Variable(tf.zeros_initializer()(shape=[self.dim]), name='bias')

    def _call(self, self_vectors, neighbor_vectors, neighbor_relations, user_embeddings):
        neighbors_agg = self._mix_neighbor_vectors(neighbor_vectors, neighbor_relations, user_embeddings)
        output = tf.concat([self_vectors, neighbors_agg], axis=-1)
        output = tf.reshape(output, [-1, self.dim * 2])
        output = tf.nn.dropout(output, rate=self.dropout)  # Convert dropout from keep_prob
        output = tf.matmul(output, self.weights) + self.bias
        output = tf.reshape(output, [self.batch_size, -1, self.dim])

        return self.act(output)

class NeighborAggregator(Aggregator):
    def __init__(self, batch_size, dim, dropout=0., act=tf.nn.relu, name=None):
        super(NeighborAggregator, self).__init__(batch_size, dim, dropout, act, name)

        self.weights = tf.Variable(tf.keras.initializers.GlorotUniform()(shape=[self.dim, self.dim]), name='weights')
        self.bias = tf.Variable(tf.zeros_initializer()(shape=[self.dim]), name='bias')

    def _call(self, self_vectors, neighbor_vectors, neighbor_relations, user_embeddings):
        neighbors_agg = self._mix_neighbor_vectors(neighbor_vectors, neighbor_relations, user_embeddings)
        output = tf.reshape(neighbors_agg, [-1, self.dim])
        output = tf.nn.dropout(output, rate=self.dropout)  # Convert dropout from keep_prob
        output = tf.matmul(output, self.weights) + self.bias
        output = tf.reshape(output, [self.batch_size, -1, self.dim])

        return self.act(output)