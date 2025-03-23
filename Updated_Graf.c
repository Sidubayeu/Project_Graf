
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <time.h>

const int qmax = 1000000;
int q[1000000], q1 = 0, q2 = 0;

void InQ(int x) {
    q[q1] = x; 
    q1++;
    if (q1 == qmax) q1 = 0;
}

int OutQ() {
    int t = q[q2]; 
    q2++;
    if (q2 == qmax) q2 = 0;
    return t;
}

bool QEmpty() {
    return q1 == q2;
}

int a[2000][2000];

int ga[2000][2000], b[1000000], c[1000000], nb, nc, x[1000000], y[1000000], m, d[1000000], nd;
int d1[2000], dbest[2000];
char s1[10000000], s2[10000000], s3[10000000], s4[10000000];

void Convert(char s[10000000]) {
    int xx = 0; nb = 0;
    for (int i = 0; i < strlen(s); i++) {
        if ('0' <= s[i] && s[i] <= '9')
            xx = xx * 10 + s[i] - '0';
        else {
            b[nb] = xx;
            nb++;
            xx = 0;
        }
    }
    b[nb++] = xx;
}

int countCuts(int nv, int parts) {
    int cuts = 0;
    for (int i = 0; i < nv; i++) {
        for (int j = i + 1; j < nv; j++) {
            if (a[i][j] == 1 && d1[i] != d1[j])
                cuts++;
        }
    }
    return cuts;
}

int main(int argc, char *argv[]) {
    char *input_file = NULL;
    char *output_file = NULL;
    int parts = 2;
    int margin = 10;
    char *format = "txt";

    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "-in") == 0 && i + 1 < argc) input_file = argv[++i];
        else if (strcmp(argv[i], "-out") == 0 && i + 1 < argc) output_file = argv[++i];
        else if (strcmp(argv[i], "-parts") == 0 && i + 1 < argc) parts = atoi(argv[++i]);
        else if (strcmp(argv[i], "-margin") == 0 && i + 1 < argc) margin = atoi(argv[++i]);
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
    for (int i = 1; i <= n; i++) {
        for (int j = 0; j < c[i]; j++)
            ga[i][b[k++]] = 1;
    }

    Convert(s4);
    nd = nb;
    for (int i = 0; i < nb; i++)
        d[i] = b[i];

    Convert(s3);
    k = 0; 
    int h = 0, first;

    for (int i = 0; i < nb; i++) {
        if (i == d[h]) {
            first = b[i];
            h++;
        } else {
            x[k] = first;
            y[k] = b[i];
            k++;
        }
    }

    for (int i = 0; i < k; i++)
        a[x[i]][y[i]] = a[y[i]][x[i]] = 1;

    srand(time(0));
    int min_size = nv / parts - (nv * margin / 100);
    int max_size = nv / parts + (nv * margin / 100);

    int best_cut = nv * nv;

    for (int j = 0; j < 100000; j++) {
        for (int i = 0; i < nv; i++)
            d1[i] = -1;

        for (int i = 0; i < parts; i++) {
            int x;
            do {
                x = rand() % nv;
            } while (d1[x] != -1);
            d1[x] = i; 
            InQ(x);
        }

        while (!QEmpty()) {
            int x = OutQ();
            for (int i = 0; i < nv; i++) {
                if (a[x][i] == 1 && d1[i] == -1) {
                    d1[i] = d1[x];
                    InQ(i);
                }
            }
        }

        int kparts[parts];
        for (int i = 0; i < parts; i++)
            kparts[i] = 0;
        for (int i = 0; i < nv; i++)
            kparts[d1[i]]++;

        bool valid = true;
        for (int i = 0; i < parts; i++) {
            if (kparts[i] < min_size || kparts[i] > max_size) {
                valid = false;
                break;
            }
        }

        if (valid) {
            int cut = countCuts(nv, parts);
            if (cut < best_cut) {
                best_cut = cut;
                for (int i = 0; i < nv; i++)
                    dbest[i] = d1[i];
            }
        }
    }

    if (output_file) {
        FILE *fout = fopen(output_file, "w");
        for (int i = 0; i < parts; i++) {
            fprintf(fout, "%d", i + 1);
            for (int j = 0; j < nv; j++)
                if (dbest[j] == i) fprintf(fout, " %d", j);
            fprintf(fout, "\n");
        }
        fprintf(fout, "Minimum cuts: %d\n", best_cut);
        fclose(fout);
    } else {
        for (int i = 0; i < parts; i++) {
            printf("%d", i + 1);
            for (int j = 0; j < nv; j++)
                if (dbest[j] == i) printf(" %d", j);
            printf("\n");
        }
        printf("Minimum cuts: %d\n", best_cut);
    }
    return 0;
}
