#include <stdio.h>

int main() {
    int numeroDeElementos, promedio, i, elemento;
    scanf("%d", &numeroDeElementos);
    promedio = 0;
    for (i = 1; i <= numeroDeElementos; i++) {
    scanf("%d", &elemento);
    promedio = promedio + elemento;
    }
    promedio = promedio / numeroDeElementos;
    printf("El promedio es %d\n", promedio);
    return 0;
}
