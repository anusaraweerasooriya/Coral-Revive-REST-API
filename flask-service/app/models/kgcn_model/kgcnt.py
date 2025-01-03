import tensorflow as tf
import numpy as np
from sklearn.metrics import f1_score, roc_auc_score
from .kgcna import SumAggregator, NeighborAggregator, ConcatAggregator
from .kgcnm import KGCN

def train(args, data, show_loss, show_topk):
    # Ensure eager execution is only disabled in this function
    _disable_eager_execution()
    
    graph = tf.Graph()  # Create a new graph
    with graph.as_default():
        n_user, n_item, n_entity, n_relation = data[0], data[1], data[2], data[3]
        train_data, eval_data, test_data = data[4], data[5], data[6]
        adj_entity, adj_relation = data[7], data[8]

        model = KGCN(args, n_user, n_entity, n_relation, adj_entity, adj_relation)

        user_list, train_record, test_record, item_set, k_list = topk_settings(show_topk, train_data, test_data, n_item)

        with tf.compat.v1.Session(graph=graph) as sess:
            sess.run(tf.compat.v1.global_variables_initializer())

            for step in range(args.n_epochs):
                np.random.shuffle(train_data)
                start = 0
                while start + args.batch_size <= train_data.shape[0]:
                    _, loss = model.train(sess, get_feed_dict(model, train_data, start, start + args.batch_size))
                    start += args.batch_size
                    if show_loss:
                        print(start, loss)

                train_auc, train_f1 = ctr_eval(sess, model, train_data, args.batch_size)
                eval_auc, eval_f1 = ctr_eval(sess, model, eval_data, args.batch_size)
                test_auc, test_f1 = ctr_eval(sess, model, test_data, args.batch_size)

                print(f'epoch {step} train auc: {train_auc:.4f} f1: {train_f1:.4f} eval auc: {eval_auc:.4f} f1: {eval_f1:.4f} test auc: {test_auc:.4f} f1: {test_f1:.4f}')

                if show_topk:
                    precision, recall = topk_eval(sess, model, user_list, train_record, test_record, item_set, k_list, args.batch_size)
                    print('precision: ', '\t'.join([f'{p:.4f}' for p in precision]))
                    print('recall: ', '\t'.join([f'{r:.4f}' for r in recall]))

def _disable_eager_execution():
    if tf.executing_eagerly():
        tf.compat.v1.disable_eager_execution()
        print("Eager execution disabled for the training process")

def topk_settings(show_topk, train_data, test_data, n_item):
    if show_topk:
        user_num = 100
        k_list = [1, 2, 5, 10, 20, 50, 100]
        train_record = get_user_record(train_data, True)
        test_record = get_user_record(test_data, False)
        user_list = list(set(train_record.keys()) & set(test_record.keys()))
        if len(user_list) > user_num:
            user_list = np.random.choice(user_list, size=user_num, replace=False)
        item_set = set(list(range(n_item)))
        return user_list, train_record, test_record, item_set, k_list
    else:
        return [None] * 5

def get_feed_dict(model, data, start, end):
    feed_dict = {model.user_indices: data[start:end, 0],
                 model.item_indices: data[start:end, 1],
                 model.labels: data[start:end, 2]}
    return feed_dict

def ctr_eval(sess, model, data, batch_size):
    start = 0
    auc_list = []
    f1_list = []
    
    while start + batch_size <= data.shape[0]:
        labels, scores = sess.run([model.labels, model.scores_normalized], feed_dict=get_feed_dict(model, data, start, start + batch_size))
        
        if len(set(labels)) == 1:
            print("Warning: Only one class present in y_true. Skipping ROC AUC calculation for this batch.")
            auc = 0.5  
        else:
            auc = roc_auc_score(y_true=labels, y_score=scores)

        scores[scores >= 0.5] = 1
        scores[scores < 0.5] = 0
        f1 = f1_score(y_true=labels, y_pred=scores)
        
        auc_list.append(auc)
        f1_list.append(f1)
        start += batch_size
    
    return float(np.mean(auc_list)), float(np.mean(f1_list))

def topk_eval(sess, model, user_list, train_record, test_record, item_set, k_list, batch_size):
    precision_list = {k: [] for k in k_list}
    recall_list = {k: [] for k in k_list}

    for user in user_list:
        test_item_list = list(item_set - train_record[user])
        item_score_map = dict()
        start = 0
        while start + batch_size <= len(test_item_list):
            items, scores = model.get_scores(sess, {model.user_indices: [user] * batch_size,
                                                    model.item_indices: test_item_list[start:start + batch_size]})
            for item, score in zip(items, scores):
                item_score_map[item] = score
            start += batch_size

        # padding the last incomplete minibatch if exists
        if start < len(test_item_list):
            items, scores = model.get_scores(
                sess, {model.user_indices: [user] * batch_size,
                       model.item_indices: test_item_list[start:] + [test_item_list[-1]] * (
                               batch_size - len(test_item_list) + start)})
            for item, score in zip(items, scores):
                item_score_map[item] = score

        item_score_pair_sorted = sorted(item_score_map.items(), key=lambda x: x[1], reverse=True)
        item_sorted = [i[0] for i in item_score_pair_sorted]

        for k in k_list:
            hit_num = len(set(item_sorted[:k]) & test_record[user])
            precision_list[k].append(hit_num / k)
            recall_list[k].append(hit_num / len(test_record[user]))

    precision = [np.mean(precision_list[k]) for k in k_list]
    recall = [np.mean(recall_list[k]) for k in k_list]

    return precision, recall

def get_user_record(data, is_train):
    user_history_dict = dict()
    for interaction in data:
        user = interaction[0]
        item = interaction[1]
        label = interaction[2]
        if is_train or label == 1:
            if user not in user_history_dict:
                user_history_dict[user] = set()
            user_history_dict[user].add(item)
    return user_history_dict
