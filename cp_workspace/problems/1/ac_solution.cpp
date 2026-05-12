#include <iostream>
#include <algorithm>

using namespace std;

int main() {
    int a, b;
    if (!(cin >> a >> b)) return 0;
    int mx = max(a, b);
    int mn = min(a, b);
    // Result is the largest digit first, followed by the smaller digit.
    // Since a + b > 0, mx must be at least 1, so mx * 10 + mn will not start with 0.
    cout << mx * 10 + mn << endl;
    return 0;
}