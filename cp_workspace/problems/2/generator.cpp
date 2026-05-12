#include <iostream>
#include <random>
#include <string>

using namespace std;

int main(int argc, char* argv[]) {
    if (argc < 2) return 1;
    int seed = stoi(argv[1]);
    mt19937 rng(seed);

    int N;
    int type = uniform_int_distribution<int>(0, 2)(rng);
    if (type == 0) {
        N = uniform_int_distribution<int>(1, 10)(rng);
    } else if (type == 1) {
        N = uniform_int_distribution<int>(1, 1000)(rng);
    } else {
        N = uniform_int_distribution<int>(1, 100000)(rng);
    }

    cout << N << endl;

    return 0;
}