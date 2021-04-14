import json

from utils import read_sample, write_matrix_and_replace, write_matrix, write_matrix_and_replace_new

DATA_FILE_PATH = "target/classes/scripts/data/ihara_edge_data.json"
SAMPLE_FILE_PATH = "src/main/resources/scripts/templates/ihara_edge.tex"

RES_PREFIX = '$$\\upzeta_E(W, X) ='
EDGES_PREFIX = 'Edges:'


def read_data():
    with open(DATA_FILE_PATH, 'r') as data:
        j = json.load(data)
        W = j['edgeMatrix']
        edge_order = j['edgeOrder']
    return W, edge_order


def form_matrix(W):
    W_symb = [["w_{%d, %d}" % (x + 1, y + 1) if W[x][y] == 1 else "0" for y in range(0, len(W))] for x in
              range(0, len(W))]
    for i in range(len(W)):
        W_symb[i][i] = W_symb[i][i] + "-1" if W_symb[i][i] != "0" else "-1"
    return W_symb


def main():
    W, edge_order = read_data()
    output = read_sample(SAMPLE_FILE_PATH)

    m = form_matrix(W)

    matrix, matrix_obj = write_matrix(RES_PREFIX, m)

    output = output.replace(RES_PREFIX, matrix)

    output = output.replace(EDGES_PREFIX, EDGES_PREFIX + '\\\\\n' + edge_order)
    print(output)


if __name__ == '__main__':
    main()
