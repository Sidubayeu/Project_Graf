#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <limits.h>

int Max_Wezly = INT_MAX;

int numer_czesci[Max_Wezly];
int k;
int rozmiar_czesci[Max_Wezly] = {0};

//pomoc dla uytkownika
void help(){
	printf("Czesc, uzytkownik/n Nasz program jest przyznaczony dla rozbicia grafa na zadana ilosc czesci i z zadanym marginesem\n");
	printf("Zeby");
}

int Best(int wezel_numer, int k, int wezly, int (*matrix)[wezly]){
	int min_cut = Max_Wezly;
    int best_part = 0;

    for (int p = 0; p < k; p++) {
        int cut_edges = 0;

        for (int j = 0; j < wezly; j++) {
            if (matrix[wezly][j] == 1 && numer_czesci[j] != -1 && numer_czesci[j] != p) {
                cut_edges++;  // Подсчёт разорванных рёбер
            }
        }

        // Проверяем, чтобы часть не превысила допуск 10%
        int max_size = (wezly / k) * 1.1;  
        if (cut_edges < min_cut && rozmiar_czesci[p] < max_size) {
            min_cut = cut_edges;
            best_part = p;
        }
    }
return best_part;
}

//rozbicie
void Cutting(int k, int wezly, int (*matrix)[wezly]){
	for(int i = 0;i < wezly; i++){
		numer_czesci[i] = -1; //ani jeden wezel nie wrzucony do czesci
	}
	for (int i = 0; i<wezly; i++){
		int best_part = Best(i, k, wezly,  matrix);
		numer_czesci[i] = best_part;
		rozmiar_czesci[best_part]++;
	}
}

int main(int argc, char* argv[]){
	int wezly, zebra;
	if(argc<2)
		help();

	FILE *cin = fopen(argv[1], "rt");
	k = atoi(argv[2]);
	if(!cin) {
		printf("Read help");
		help();
	}
	fscanf(cin, "%d%d", &wezly, &zebra);

//macierz dla grafa
	int matrix[wezly][wezly];
	for(int i = 0; i < wezly; i++){
		for(int j = 0; j < wezly; j++){
			matrix[i][j] = 0;
		}
	}
	int a = 0, b = 0;
	while(fscanf(cin, "%d%d", &a, &b) == 2){
		matrix[a][b] = 1;
		matrix[b][a] = 1;
	}
	fclose(cin);

	Cutting(k, wezly, matrix);

	for(int p = 0; p < k; p++){
		printf("numer czesci: %d", p);
		for( int i = 0; i < wezly; i++){
			if(numer_czesci[i] == p)
				printf("%d", i);
		}
	}

return 0;
}
