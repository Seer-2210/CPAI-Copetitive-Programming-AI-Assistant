#include <iostream>
#include <vector>

using namespace std;

void solve() {
    int n;
    if (!(cin >> n)) return;
    if (n == 1) {
        cout << "1\n";
        return;
    }
    if (n % 2 != 0) {
        cout << "-1\n";
        return;
    }
    for (int i = 0; i < n; i++) {
        if (i % 2 == 0) {
            if (i == 0) cout << n;
            else cout << i;
        } else {
            cout << n - i;
        }
        cout << (i == n - 1 ? "" : " ");
    }
    cout << "\n";
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(NULL);
    int t;
    if (!(cin >> t)) return 0;
    while (t--) {
        solve();
    }
    return 0;
}