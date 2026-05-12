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
    for(int i=1; i<=100000000000; i++)
	N++;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);
    solve();
    return 0;
}