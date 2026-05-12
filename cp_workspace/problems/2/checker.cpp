#include <iostream>
#include <fstream>
#include <vector>
#include <numeric>
#include <algorithm>
#include <string>

using namespace std;

bool is_prime(int n) {
    if (n < 2) return false;
    if (n == 2 || n == 3) return true;
    if (n % 2 == 0 || n % 3 == 0) return false;
    for (int i = 5; i * i <= n; i += 6) {
        if (n % i == 0 || n % (i + 2) == 0) return false;
    }
    return true;
}

int main(int argc, char* argv[]) {
    if (argc < 4) return 1;
    ifstream input(argv[1]);
    ifstream user_output(argv[3]);
    if (!input.is_open() || !user_output.is_open()) return 1;

    int N;
    if (!(input >> N)) return 1;

    string first_token;
    if (!(user_output >> first_token)) return 1;

    if (first_token == "-1") {
        if (N >= 2 && N <= 4) return 0;
        return 1;
    }

    vector<int> p(N);
    try {
        p[0] = stoi(first_token);
        for (int i = 1; i < N; i++) {
            if (!(user_output >> p[i])) return 1;
        }
    } catch (...) {
        return 1;
    }

    vector<int> count(N + 1, 0);
    for (int i = 0; i < N; i++) {
        if (p[i] < 1 || p[i] > N) return 1;
        if (++count[p[i]] > 1) return 1;
    }

    for (int i = 0; i < N - 1; i++) {
        if (is_prime(p[i] + p[i+1])) return 1;
    }

    string extra;
    if (user_output >> extra) return 1;

    return 0;
}