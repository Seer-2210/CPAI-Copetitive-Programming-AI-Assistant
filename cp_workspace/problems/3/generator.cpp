#include <iostream>
#include <vector>
#include <random>
#include <algorithm>

using namespace std;

int main(int argc, char* argv[]) {
    if (argc < 2) return 1;
    int seed = atoi(argv[1]);
    mt19937 rng(seed);

    int n_max = 100000;
    int n;
    if (seed % 10 == 0) n = uniform_int_distribution<int>(1, 5000)(rng);
    else n = uniform_int_distribution<int>(n_max / 2, n_max)(rng);

    long long k_limit = 1000000000;
    long long k = uniform_int_distribution<long long>(0, k_limit)(rng);

    cout << n << " " << k << endl;

    vector<int> a(n);
    if (seed % 7 == 1) {
        // Sorted
        for (int i = 0; i < n; ++i) a[i] = uniform_int_distribution<int>(0, k_limit)(rng);
        sort(a.begin(), a.end());
    } else if (seed % 7 == 2) {
        // Unimodal
        int t = uniform_int_distribution<int>(0, n - 1)(rng);
        for (int i = 0; i < n; ++i) a[i] = uniform_int_distribution<int>(0, k_limit)(rng);
        sort(a.begin(), a.begin() + t + 1);
        sort(a.begin() + t + 1, a.end(), greater<int>());
    } else {
        // Random
        for (int i = 0; i < n; ++i) a[i] = uniform_int_distribution<int>(0, k_limit)(rng);
    }

    for (int i = 0; i < n; ++i) {
        cout << a[i] << (i == n - 1 ? "" : " ");
    }
    cout << endl;

    return 0;
}