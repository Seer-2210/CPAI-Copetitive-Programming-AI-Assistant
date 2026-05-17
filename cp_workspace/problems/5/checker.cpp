#include <iostream>
#include <vector>
#include <fstream>
#include <set>

using namespace std;

int main(int argc, char* argv[]) {
    if (argc < 4) return 1;
    ifstream inf(argv[1]);
    ifstream exp(argv[2]);
    ifstream ouf(argv[3]);

    int t;
    if (!(inf >> t)) return 1;
    while (t--) {
        int n;
        if (!(inf >> n)) break;
        int first_ouf;
        if (!(ouf >> first_ouf)) return 1;
        if (first_ouf == -1) {
            if (n == 1 || n % 2 == 0) return 1;
        } else {
            if (n > 1 && n % 2 != 0) return 1;
            vector<int> a(n);
            a[0] = first_ouf;
            for (int i = 1; i < n; i++) {
                if (!(ouf >> a[i])) return 1;
            }
            vector<int> count_a(n + 1, 0);
            for (int x : a) {
                if (x < 1 || x > n) return 1;
                count_a[x]++;
            }
            for (int i = 1; i <= n; i++) if (count_a[i] != 1) return 1;
            long long sum = 0;
            vector<int> count_b(n + 1, 0);
            for (int i = 0; i < n; i++) {
                sum += a[i];
                int b_val = (sum % n) + 1;
                count_b[b_val]++;
            }
            for (int i = 1; i <= n; i++) if (count_b[i] != 1) return 1;
        }
    }
    return 0;
}