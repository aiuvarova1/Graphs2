from sympy import Matrix, latex, symbols, simplify, radsimp

BEGIN_MATRIX = "\\begin{pmatrix}\n"
END_MATRIX = '\\end{pmatrix}\n'
MATRIX_DELIMITER = ' & '

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


def read_sample(file_path):
    output = ""
    with open(file_path, 'r') as sample:
        for line in sample:
            output += line
    return output


def convert_matrix_row_to_string(row):
    r = [(str(latex(x))) if not isinstance(x, str) else x for x in row]
    return MATRIX_DELIMITER.join(r) + '\\\\\n'


def write_matrix_and_replace(prefix, output, input_matrix):
    matrix, matrix_obj = write_matrix(prefix, input_matrix)

    output = output.replace(prefix, matrix)
    return output, Matrix(matrix_obj)


def write_matrix_and_replace_new(prefix, new_prefix, output, input_matrix):
    matrix, matrix_obj = write_matrix(new_prefix, input_matrix)

    output = output.replace(prefix, matrix)
    return output, Matrix(matrix_obj)


def write_matrix(prefix, input_matrix):
    matrix = prefix + BEGIN_MATRIX
    m = []

    for row in input_matrix:
        m.append(row)
        matrix += convert_matrix_row_to_string(row)
    matrix += END_MATRIX

    return matrix, m
