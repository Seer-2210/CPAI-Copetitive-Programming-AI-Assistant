#include <iostream>
#include <vector>
#include <algorithm>
#include <random>
#include <string>

using namespace std;

int main(int argc, char* argv[]) {
    if (argc < 2) return 1;
    unsigned int seed = (unsigned int)stoul(argv[1]);
    mt19937 rng(seed);

    int type = seed % 10;
    int n;
    if (type == 0) {
        n = uniform_int_distribution<int>(1, 10)(rng);
    } else if (type >= 1 && type <= 3) {
        n = uniform_int_distribution<int>(1000, 2000)(rng);
    } else {
        n = uniform_int_distribution<int>(1, 2000)(rng);
    }

    vector<long long> s(n);
    if (type == 1) { // s_i = i (permuted)
        for (int i = 0; i < n; ++i) s[i] = i + 1;
        shuffle(s.begin(), s.end(), rng);
    } else if (type == 2) { // s_i = some consecutive sequence
        long long start = uniform_int_distribution<long long>(1, 1000000000 - n)(rng);
        for (int i = 0; i < n; ++i) s[i] = start + i;
        shuffle(s.begin(), s.end(), rng);
    } else if (type == 3) { // All s_i same
        long long val = uniform_int_distribution<long long>(1, 1000000000)(rng);
        for (int i = 0; i < n; ++i) s[i] = val;
    } else { // General random s_i
        uniform_int_distribution<long long> dist_s(1, 1000000000);
        for (int i = 0; i < n; ++i) s[i] = dist_s(rng);
    }

    cout << n << "\n";
    for (int i = 0; i < n; ++i) {
        cout << s[i] << (i == n - 1 ? "" : " ");
    }
    cout << endl;

    return 0;
}