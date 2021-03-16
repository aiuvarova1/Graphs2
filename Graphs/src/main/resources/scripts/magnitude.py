from sympy import Matrix, symbols, sympify, latex, cse
from utils import convert_matrix_row_to_string, tex_replace, BEGIN_MATRIX, END_MATRIX

DELIMITER = ' '
REPLACE_Z = 'Z_G = '
REPLACE_INV_Z = 'Z_G^{-1} = '
REPLACE_SUM = 'e: $$\#G(q) ='

FULL_DATA_PATH = "target/classes/scripts/data/matrix.txt"
FULL_TEMPLATE_PATH = "src/main/resources/scripts/templates/magnitude.tex"
SHORT_DATA_PATH = "data/matrix.txt"
SHORT_TEMPLATE_PATH = "templates/magnitude.tex"

matrix = []
output = ""

q = symbols('q')

COMPLEX = False


def insert_matrix():
    global output
    z = REPLACE_Z + BEGIN_MATRIX
    z_g = []

    for row in matrix:

        r = [q ** sympify(x, evaluate=True) if x != "-1" else 0 for x in row]
        z_g.append(r)
        z += convert_matrix_row_to_string(r)
    z += END_MATRIX

    output = output.replace(REPLACE_Z, z)
    return Matrix(z_g)


def insert_inverted_matrix(z_g):
    global output
    # print(z_g)
    # method : ('GE', 'LU', 'ADJ', 'CH', 'LDL')
    m = 'GE' if not COMPLEX else 'LU'
    inverted = z_g.inv(method=m)
    # print(inverted)
    z_inv = REPLACE_INV_Z + BEGIN_MATRIX
    for r in range(0, inverted.shape[0]):
        z_inv += convert_matrix_row_to_string(inverted[r, :])
    z_inv += END_MATRIX
    output = output.replace(REPLACE_INV_Z, z_inv)
    return inverted


def insert_sum(z_inv):
    global output
    s = sum([x for x in z_inv])
    # print(s)
    output = output.replace(REPLACE_SUM, REPLACE_SUM + ' ' + str(latex(s)))
    return s


def parse_args():
    global matrix, COMPLEX

    with open(FULL_DATA_PATH, 'r') as data:
        for line in data:
            row = [x for x in line.strip().split(DELIMITER)]
            if not COMPLEX:
                for x in row:
                    if any(c.isalpha() for c in x):
                        COMPLEX = True
                        break
            matrix.append(row)

    # print(matrix)


def read_sample():
    global output
    with open(FULL_TEMPLATE_PATH, 'r') as sample:
        for s in sample:
            output += s


def main():
    # print(sys.path)
    parse_args()
    read_sample()
    z_g = insert_matrix()
    z_inv = insert_inverted_matrix(z_g)
    s = insert_sum(z_inv)
    print(output)


if __name__ == '__main__':
    main()
