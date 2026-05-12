#include <iostream>
#include <vector>

using namespace std;

void solve() {
    int N;
    if (!(cin >> N)) return;
    if (N == 1) {
        cout << 1 << endl;
        return;
    }
    if (N >= 2 && N <= 4) {
        cout << -1 << endl;
        return;
    }

    // For N >= 5, we can use the bridge O=5 and E=4 (5+4=9, composite)
    // Any other odd pairs or even pairs will sum to an even number >= 4, which is composite.
    vector<int> res;
    // Collect all odds except 5
    for (int i = 1; i <= N; i += 2) {
        if (i != 5) res.push_back(i);
    }
    // Add bridge elements
    res.push_back(5);
    res.push_back(4);
    // Collect all evens except 4
    for (int i = 2; i <= N; i += 2) {
        if (i != 4) res.push_back(i);
    }

    for (int i = 0; i < res.size(); i++) {
        cout << res[i] << (i == (int)res.size() - 1 ? "" : " ");
    }
    cout << endl;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);
    solve();
    return 0;
}