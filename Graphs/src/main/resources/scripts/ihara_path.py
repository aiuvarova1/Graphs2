import json

from utils import read_sample, write_matrix_and_replace, write_matrix, write_matrix_and_replace_new

DATA_FILE_PATH = "target/classes/scripts/data/ihara_path_data.json"
SAMPLE_FILE_PATH = "src/main/resources/scripts/templates/ihara_path.tex"

RES_PREFIX = '$$\\upzeta_F(Z, X) ='
EDGES_PREFIX = 'Edges:'
TREE_PREFIX = "Spanning tree:"
NOT_TREE_PREFIX = "Left out of spanning tree:"


def read_data():
    with open(DATA_FILE_PATH, 'r') as data:
        j = json.load(data)
        W = j['edgeMatrix']
        edge_order = j['edgeOrder']
        spanning_tree = j['spanningTree']
        not_spanning_tree = j['notSpanningTree']
    return W, edge_order, spanning_tree, not_spanning_tree


def main():
    W, edge_order, spanning_tree, not_spanning_tree = read_data()
    output = read_sample(SAMPLE_FILE_PATH)

    matrix, matrix_obj = write_matrix(RES_PREFIX, W)

    output = output.replace(RES_PREFIX, matrix)

    output = output.replace(EDGES_PREFIX, EDGES_PREFIX + '\\\\\n' + edge_order)
    output = output.replace(TREE_PREFIX, TREE_PREFIX + ' ' + spanning_tree)
    output = output.replace(NOT_TREE_PREFIX, NOT_TREE_PREFIX + ' ' + not_spanning_tree)
    print(output)


if __name__ == '__main__':
    main()
