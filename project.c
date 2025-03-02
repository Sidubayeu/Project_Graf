#include <stdlib.h>
#include <stdio.h>
#include <string.h>

//pomoc dla uytkownika
void help(){

}

//rozbicie
void Cutting(){

}

int main(int argc, char* argv[]){
	int wezly, zebra;
	if(argc<1)
		help();

	FILE *cin = fopen(argv[1], "rt");

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



return 0;
}
