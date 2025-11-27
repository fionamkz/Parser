# Programa traducido desde pseudocódigo
def main():
    # Variables: numeroDeElementos, promedio, i, elemento
    numeroDeElementos = int(input())
    promedio = 0
    for i in range(1, numeroDeElementos + 1):
        elemento = int(input())
        promedio = promedio + elemento
    promedio = promedio / numeroDeElementos
    print("El promedio es", promedio)

if __name__ == '__main__':
    main()
