import json

from sympy import symbols, eye, latex, sympify

from utils import read_sample, write_matrix_and_replace, write_matrix, write_matrix_and_replace_new

DATA_FILE_PATH = "target/classes/scripts/data/ihara_data.json"
SAMPLE_FILE_PATH = "src/main/resources/scripts/templates/ihara.tex"

A_PREFIX = 'Adjacency matrix: $$ A = '
W_PREFIX = 'Weighted matrix: $$ W = '
Q_PREFIX = 'Q = '
DET_PREFIX = 'det(I-Au+Qu^2) ='
INV_PREFIX = 'n: $$\\upzeta_X(u)^{-1} = '
RES_PREFIX = '\\upzeta_X(u) ='

A = []
Q = []
rm1 = 0
weighted = False

u = symbols('u')


def read_data():
    global A, Q, rm1, weighted
    with open(DATA_FILE_PATH, 'r') as data:
        j = json.load(data)
        rm1 = j['rm1']
        Q = j['q']
        weighted = j['weighted']
        A = j['w'] if weighted else j['a']
        A = [sympify(a) for a in A]


def calc_det(output, m_A, m_Q):
    I = eye(m_A.shape[0])

    det_m = I - m_A * u + m_Q * u * u

    matrix, m_obj = write_matrix(DET_PREFIX + '\\det', det_m.tolist())

    calculated = det_m.det()
    matrix += ' = $$ \\ $$= ' + str(latex(calculated))
    output = output.replace(DET_PREFIX, matrix)
    return output, calculated


def write_result(output, det):
    inv = (1 - u ** 2) ** rm1 * det
    output = output.replace(INV_PREFIX, INV_PREFIX + str(latex(inv)))
    res = inv ** (-1)
    output = output.replace(RES_PREFIX, RES_PREFIX + str(latex(res)))
    return output


def main():
    read_data()
    output = read_sample(SAMPLE_FILE_PATH)
    prefix = W_PREFIX if weighted else A_PREFIX
    output, m_A = write_matrix_and_replace_new(A_PREFIX, prefix, output, A)
    output, m_Q = write_matrix_and_replace(Q_PREFIX, output, Q)
    output, calculated = calc_det(output, m_A, m_Q)
    output = write_result(output, calculated)

    if weighted:
        output = output.replace('Au', 'Wu')
    print(output)


if __name__ == '__main__':
    main()
