import os

import numpy as np


def load_data(args,rating_file_path, kg_file_path):
    n_user, n_item, train_data, eval_data, test_data = load_rating(args, rating_file_path)
    n_entity, n_relation, adj_entity, adj_relation = load_kg(args, kg_file_path)
    print('Data loaded.')

    return n_user, n_item, n_entity, n_relation, train_data, eval_data, test_data, adj_entity, adj_relation


def load_rating(args, rating_file_path):
    print('Reading rating file...')

    rating_np = np.loadtxt(rating_file_path, dtype=np.int64)
    print("Loaded rating data:")
    print(rating_np)
    n_user = len(set(rating_np[:, 0]))
    n_item = len(set(rating_np[:, 1]))
    train_data, eval_data, test_data = dataset_split(rating_np, args)

    return n_user, n_item, train_data, eval_data, test_data


def dataset_split(rating_np, args):
    print('Splitting dataset...')

    eval_ratio = 0.2
    test_ratio = 0.2
    n_ratings = rating_np.shape[0]

    eval_indices = np.random.choice(list(range(n_ratings)), size=int(n_ratings * eval_ratio), replace=False)
    left = set(range(n_ratings)) - set(eval_indices)
    test_indices = np.random.choice(list(left), size=int(n_ratings * test_ratio), replace=False)
    train_indices = list(left - set(test_indices))
    if args.ratio < 1:
        train_indices = np.random.choice(list(train_indices), size=int(len(train_indices) * args.ratio), replace=False)

    train_data = rating_np[train_indices]
    eval_data = rating_np[eval_indices]
    test_data = rating_np[test_indices]

    return train_data, eval_data, test_data


def load_kg(args, kg_file_path):
    print('Reading KG file...')

    
    kg_np = np.loadtxt(kg_file_path, dtype=np.int64)

    n_entity = len(set(kg_np[:, 0]) | set(kg_np[:, 2]))
    n_relation = len(set(kg_np[:, 1]))

    kg = construct_kg(kg_np)
    adj_entity, adj_relation = construct_adj(args, kg, n_entity)

    return n_entity, n_relation, adj_entity, adj_relation


def construct_kg(kg_np):
    print('Constructing knowledge graph...')
    kg = dict()
    for triple in kg_np:
        head = triple[0]
        relation = triple[1]
        tail = triple[2]
        if head not in kg:
            kg[head] = []
        kg[head].append((tail, relation))
        if tail not in kg:
            kg[tail] = []
        kg[tail].append((head, relation))
    return kg


def construct_adj(args, kg, entity_num):
    print('Constructing adjacency matrix...')
    adj_entity = np.zeros([entity_num, args.neighbor_sample_size], dtype=np.int64)
    adj_relation = np.zeros([entity_num, args.neighbor_sample_size], dtype=np.int64)

    print(f"Knowledge graph contains {len(kg)} entities.")
    
    for entity in range(entity_num):
        if entity not in kg:
            print(f"Entity {entity} not found in knowledge graph. Skipping.")
            continue  

        neighbors = kg[entity]
        n_neighbors = len(neighbors)

        print(f"Entity {entity} has {n_neighbors} neighbors.")

        if n_neighbors == 0:
            print(f"Entity {entity} has no neighbors. Skipping.")
            continue 

        if n_neighbors >= args.neighbor_sample_size:
            sampled_indices = np.random.choice(list(range(n_neighbors)), size=args.neighbor_sample_size, replace=False)
        else:
            sampled_indices = np.random.choice(list(range(n_neighbors)), size=args.neighbor_sample_size, replace=True)

        print(f"Sampled indices for entity {entity}: {sampled_indices}")

        adj_entity[entity] = np.array([neighbors[i][0] for i in sampled_indices])
        adj_relation[entity] = np.array([neighbors[i][1] for i in sampled_indices])

    return adj_entity, adj_relation