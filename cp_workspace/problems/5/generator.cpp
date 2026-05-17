#include <iostream>
#include <vector>
#include <random>
#include <algorithm>

using namespace std;

int main(int argc, char* argv[]) {
    if (argc < 2) return 1;
    int seed = atoi(argv[1]);
    mt19937 rng(seed);

    int max_sum_n = 200000;
    int max_t = 10000;
    
    // Distribution of t
    uniform_int_distribution<int> dist_t(1, max_t);
    int t = dist_t(rng);
    
    vector<int> n_values(t, 1);
    int remaining = max_sum_n - t;
    
    // To have some variety, we distribute the remaining sum
    // First, let's potentially give large portions to a few cases
    int num_large = uniform_int_distribution<int>(0, min(t, 50))(rng);
    for (int i = 0; i < num_large && remaining > 0; ++i) {
        int idx = uniform_int_distribution<int>(0, t - 1)(rng);
        int add = uniform_int_distribution<int>(0, remaining)(rng);
        n_values[idx] += add;
        remaining -= add;
    }
    
    // Then distribute what's left among all cases more evenly
    for (int i = 0; i < t && remaining > 0; ++i) {
        int add = uniform_int_distribution<int>(0, remaining / (t - i) + 1)(rng);
        add = min(add, remaining);
        n_values[i] += add;
        remaining -= add;
    }
    
    // Any leftover goes to a random element
    if (remaining > 0) {
        n_values[rng() % t] += remaining;
    }
    
    shuffle(n_values.begin(), n_values.end(), rng);

    cout << t << "\n";
    for (int n : n_values) {
        cout << n << "\n";
    }

    return 0;
}