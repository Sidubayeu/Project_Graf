#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <time.h>

const int qmax = 1000000;
int q[1000000], q1 = 0, q2 = 0;

void help(){
printf("Uzycie:\n");
printf("    program -in [plik_wejsciowy] -out [plik_wyjsciowy] -parts [liczba_czesci] -margin [margines] -format [txt/csrrg]\n\n");

printf("Opcje:\n");
printf("    -h            Wyswietla ten tekst pomocy.\n");
printf("    -in           Okresla sciezke do pliku wejsciowego.\n");
printf("    -out          Okresla sciezke do pliku wyjsciowego.\n");
printf("    -parts        Okresla liczbe czesci, na ktore ma byc podzielony graf (domyslnie 2).\n");
printf("    -margin       Okresla dopuszczalny margines procentowy roznicy miedzy czesciami (domyslnie 10%%).\n");
printf("    -format       Okresla format pliku wyjsciowego: \"txt\" lub \"csrrg\" (domyslnie \"txt\").\n\n");

printf("Przyklad uzycia:\n");
printf("    program -in graf.csrrg -out wynik.txt -parts 3 -margin 15 -format txt\n\n");

printf("Opis:\n");
printf("    Program dzieli graf na okreslona liczbe czesci, minimalizujac liczbe przecietych krawedzi i\n");
printf("    uwzgledniajac zadany margines roznicy miedzy liczba wierzcholkow w czesciach.\n");
}



void InQ(int x){
    q[q1] = x; q1++;
    if (q1 == qmax)
        q1 = 0;
}

int OutQ(){
    int t = q[q2]; q2++;
    if (q2 == qmax)
        q2 = 0;
    return t;
}

bool QEmpty(){
    return q1 == q2;
}

int a[6000][6000]; /// agency matrix

int ga[6000][6000], b[1000000], c[1000000], nb, nc, x[1000000], y[1000000], m, d[1000000], nd;
int d1[6000], dbest[6000];

char s1[10000000], s2[10000000], s3[10000000], s4[10000000];

void Convert(char s[10000000]){
    int xx = 0; nb = 0;
    for (int i = 0; i < strlen(s); i++)
        if ('0' <= s[i] && s[i] <= '9')
            xx = xx * 10 + s[i] - '0';
        else{
            b[nb] = xx;
            nb++;
            xx = 0;
        } 
    b[nb] = xx;
    nb++;
}

int countCuts(int nv) {
    int cut_edges = 0;
    for (int i = 0; i < nv; i++) {
        for (int j = i + 1; j < nv; j++) {
            if (a[i][j] == 1 && dbest[i] != dbest[j]) {
                cut_edges++;
            }
        }
    }
    return cut_edges;
}

int main(int argc, char* argv[]){
    char *input_file = NULL;
    char *output_file = NULL;
    int parts = 2;
    int margin = 10;
    char *format = "txt";

    for (int i = 1; i < argc; i++) {
	if (strcmp(argv[i], "-h") == 0) help();
        else if (strcmp(argv[i], "-in") == 0 && i + 1 < argc) input_file = argv[++i];
        else if (strcmp(argv[i], "-out") == 0 && i + 1 < argc) output_file = argv[++i];
        else if (strcmp(argv[i], "-parts") == 0 && i + 1 < argc) parts = atoi(argv[++i]);
        else if (strcmp(argv[i], "-margin") == 0 && i + 1 < argc) margin = atoi(argv[++i]);
        else if (strcmp(argv[i], "-format") == 0 && i + 1 < argc) format = argv[++i];
    }

    if (!input_file) {
        printf("Input file required (-in <filename>)\n");
        return 1;
    }

    FILE *fin = fopen(input_file, "rt");
    if (!fin) {
        printf("Cannot open input file\n");
        return 1;
    }

    int n, nv;
    fscanf(fin, "%d", &n);
    fscanf(fin, "%s", s1);
    fscanf(fin, "%s", s2);
    fscanf(fin, "%s", s3);
    fscanf(fin, "%s", s4);
    fclose(fin);
    Convert(s2);
    nv = b[nb - 1];
    for (int i = nb - 1; i > 0; i--)
        b[i] -= b[i - 1];
    nc = nb;
    for (int i = 0; i < nb; i++)
        c[i] = b[i];
    Convert(s1);
    int k = 0;
    for (int i = 1; i <= n; i++){
        for (int j = 0; j < c[i]; j++)
            ga[i][b[k++]] = 1;
    }

    Convert(s4);
    nd = nb;
    for (int i = 0; i < nb; i++)
        d[i] = b[i];
    Convert(s3);
    k = 0; int h = 0, first;
    for (int i = 0; i < nb; i++){
        if (i == d[h]){
            first = b[i];
            h++;
        } else{
            x[k] = first;
            y[k] = b[i];
            k++;
        }
    }
    
    for (int i = 0; i < k; i++)
        a[x[i]][y[i]] = a[y[i]][x[i]] = 1;


  
    
    for (int i = 0; i < nv; i++)
        d1[i] = -1;
   
    srand(time(0));
    for (int i = 0; i < parts; i++){
        int x;
        do{
            x = rand() % nv;
        } while (d1[x] != -1);
        d1[x] = i; InQ(x);
    }
    while (!QEmpty()){
        int x = OutQ();
        for (int i = 0; i < nv; i++)
            if (a[x][i] == 1 && d1[i] == -1){
                d1[i] = d1[x];
                InQ(i);
            }
    }
    int kparts[parts];
    for (int i = 0; i < parts; i++)
        kparts[i] = 0;
    for (int i = 0; i < nv; i++)
        kparts[d1[i]]++;

    int bestmin = kparts[0], bestmax = kparts[0];
    for (int i = 1; i < parts; i++){
        if (kparts[i] < bestmin)
            bestmin = kparts[i];
        if (kparts[i] > bestmax)
            bestmax = kparts[i];
    }
    for (int i = 0; i < nv; i++)
        dbest[i] = d1[i];

    for (int j = 0; j < 100000; j++){
        for (int i = 0; i < nv; i++)
            d1[i] = -1;
        for (int i = 0; i < parts; i++){
            int x;
            do{
                x = rand() % nv;
            } while (d1[x] != -1);
            d1[x] = i; InQ(x);
        }
        while (!QEmpty()){
            int x = OutQ();
            for (int i = 0; i < nv; i++)
                if (a[x][i] == 1 && d1[i] == -1){
                    d1[i] = d1[x];
                    InQ(i);
                }
        }
        int kparts[parts];
        for (int i = 0; i < parts; i++)
            kparts[i] = 0;
        for (int i = 0; i < nv; i++)
            kparts[d1[i]]++;
        int localmin = kparts[0], localmax = kparts[0];
        for (int i = 1; i < parts; i++){
            if (kparts[i] < localmin)
                localmin = kparts[i];
            if (kparts[i] > localmax)
                localmax = kparts[i];
        }

        int max_allowed_diff = (margin * nv) / 100;
        if (localmax - localmin <= max_allowed_diff && localmax - localmin < bestmax - bestmin){
            bestmax = localmax;
            bestmin = localmin;
            for (int i = 0; i < nv; i++)
                dbest[i] = d1[i];
        }
    }

    int cuts = countCuts(nv);
        
    if (output_file) {
        if (strcmp(format, "bin") == 0) {
            FILE *fout = fopen(output_file, "wb");
            if (!fout) {
                printf("nie mozna otworzyc pliku\n");
                return 1;
            }
            fwrite(&parts, sizeof(int), 1, fout);
            fwrite(&cuts, sizeof(int), 1, fout);
            for (int i = 0; i < parts; i++) {
                int count = 0;
                for (int j = 0; j < nv; j++)
                    if (dbest[j] == i) count++;
                fwrite(&count, sizeof(int), 1, fout);
                for (int j = 0; j < nv; j++)
                    if (dbest[j] == i)
                        fwrite(&j, sizeof(int), 1, fout);
            }
            fclose(fout);
        } else {
            FILE *fout = fopen(output_file, "w");
            if (!fout) {
                printf("nie mozna otworzyc pliku\n");
                return 1;
            }
            for (int i = 0; i < parts; i++) {
                fprintf(fout, "Czesc numer: %d\n", i + 1);
                int count2 = 0;
                for (int j = 0; j < nv; j++)
                    if (dbest[j] == i){ 
                        fprintf(fout, " %d", j);
                        count2++;
                    }
                fprintf(fout, "\n");
                fprintf(fout, "Ilosc wierzcholkow: %d\n", count2);
            }
            fprintf(fout, "Ilosc preciec: %d\n", cuts);
            fclose(fout);
        }
    } else {
        for (int i = 0; i < parts; i++) {
            printf("Czesc numer: %d\n", i + 1);
            int count2 = 0;
            for (int j = 0; j < nv; j++)
                if (dbest[j] == i){
                    printf(" %d", j);
                    count2++;
                }
            printf("\nIlosc wierzcholkow: %d\n", count2);
        }
        printf("Ilosc przeciec: %d\n", cuts);
    }
    
    return 0;
}
