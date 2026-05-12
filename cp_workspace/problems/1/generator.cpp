#include <iostream>
#include <random>
#include <cstdlib>

using namespace std;

int main(int argc, char* argv[]) {
    if (argc < 2) return 1;
    unsigned int seed = (unsigned int)atoi(argv[1]);
    mt19937 rng(seed);
    uniform_int_distribution<int> dist(0, 9);
    int a, b;
    do {
        a = dist(rng);
        b = dist(rng);
    } while (a + b == 0);
    cout << a << " " << b << endl;
    return 0;
}