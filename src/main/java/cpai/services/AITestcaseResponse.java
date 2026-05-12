package cpai.services;
public class AITestcaseResponse {
    public String input;
    public String output;
    public boolean isManual;

    public AITestcaseResponse(String input, String output, boolean isManual) {
        this.input = input;
        this.output = output;
        this.isManual = isManual;
    }
}