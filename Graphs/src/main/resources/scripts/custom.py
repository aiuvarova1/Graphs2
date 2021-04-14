import json

from utils import read_sample, write_matrix_and_replace, write_matrix, write_matrix_and_replace_new
from sympy import sympify, symbols, latex
from sympy.functions import exp

DATA_FILE_PATH = "target/classes/scripts/data/custom_data.json"
SAMPLE_FILE_PATH = "src/main/resources/scripts/templates/custom.tex"

RES_PREFIX = '$$\\upzeta_G(t) ='
VERTEX_PREFIX = 'Chosen vertex:'

t = symbols('t')


def read_data():
    with open(DATA_FILE_PATH, 'r') as data:
        j = json.load(data)
        paths = j['paths']
        vertex = j['vertex']
        paths = [sympify(p) for p in paths]
    return paths, vertex


def evaluate(paths):
    res = 1
    for l in paths:
        res *= t / (1 - exp(l * t))
    return str(latex(res))


def main():
    paths, vertex = read_data()
    output = read_sample(SAMPLE_FILE_PATH)

    output = output.replace(VERTEX_PREFIX, VERTEX_PREFIX + ' ' + str(vertex) + '\\\\')
    evaluated = evaluate(paths)

    output = output.replace(RES_PREFIX, RES_PREFIX + evaluated)
    print(output)


if __name__ == '__main__':
    main()
