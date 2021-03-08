from sympy import Matrix, symbols

DELIMITER = ' '
REPLACE_Z = 'Z_G = '
REPLACE_INV_Z = 'Z_G^{-1} = '
REPLACE_SUM = 'e: $$\#G(q) ='

BEGIN_MATRIX = "\\begin{pmatrix}\n"
END_MATRIX = '\\end{pmatrix}\n'

MATRIX_DELIMITER = ' & '

matrix = []
output = ""

q = symbols('q')


def tex_replace(el):
    if '/' in el:
        parts = el.split('/')
        frac = '\\dfrac{%s}{%s}' % (parts[0], parts[1].replace('(', '').replace(')', ''))
        el = frac
    if '**' in el:
        parts = el.split('**')
        index = 0
        el = parts[0]
        for i in range(1, len(parts)):
            while index < len(parts[i]) and parts[i][index].isdigit():
                index += 1
            el += "^{%s}%s" % (parts[i][:index], parts[i][index:])
    return el


def convert_matrix_row_to_string(row):
    r = [tex_replace(str(x)) for x in row]
    return MATRIX_DELIMITER.join(r) + '\\\\\n'


def insert_matrix():
    global output
    z = REPLACE_Z + BEGIN_MATRIX
    z_g = []

    for row in matrix:
        r = [q ** x if x != -1 else 0 for x in row]
        z_g.append(r)
        z += convert_matrix_row_to_string(r)
    z += END_MATRIX

    output = output.replace(REPLACE_Z, z)
    return Matrix(z_g)


def insert_inverted_matrix(z_g):
    global output
    # print(z_g)
    inverted = Matrix(z_g) ** -1
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
    output = output.replace(REPLACE_SUM, REPLACE_SUM + ' ' + tex_replace(str(s)))
    return s


def parse_args():
    global matrix

    with open("target/classes/scripts/data/matrix.txt", 'r') as data:
        for line in data:
            row = [int(x) for x in line.strip().split(DELIMITER)]
            matrix.append(row)

    # print(matrix)


def read_sample():
    global output
    with open("src/main/resources/scripts/templates/magnitude.tex", 'r') as sample:
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
