package cpai.services;

import org.json.JSONObject;

public class AIProcessor {

    /**
     * Constructs the English prompt to send to the AI model (Gemini/OpenAI).
     * We explicitly ask for C++ code since the user requested compiling generator.cpp.
     *
     * @param problemDescription The full text of the problem.
     * @return The formatted prompt.
     */
    public static String buildPrompt(String problemDescription) {
        return """
            You are an expert Competitive Programming problem setter.
            Analyze the following competitive programming problem description.
            
            Based on the problem's input format and constraints, write a C++17 script to generate random valid testcases.
            
            REQUIREMENTS FOR THE C++ SCRIPT:
            1. The script MUST take exactly one command-line argument: an integer seed.
            2. Initialize your random number generator with this seed (e.g., mt19937 rng(seed);).
            3. Generate exactly one valid input instance for the problem.
            4. Print the generated input to standard output (stdout) exactly as the problem requires.
            5. Ensure all generated data strictly adheres to the problem's constraints (bounds, data types, graph properties, etc.).
            6. Do not print any debug information or extra text, ONLY the input data.
            
            Return the result EXACTLY as a JSON object with the following structure (do not wrap in markdown blocks like ```json):
            {
                "input_format": "Short description of the input format",
                "constraints": "Short description of the constraints",
                "generator_code": "The complete C++ source code as a single string",
            "checker_code": "The complete C++ source code for checking correctness if multiple solutions are valid. MUST read files from argv[1] (input), argv[2] (expected output), argv[3] (user output). Return exit code 0 if correct, 1 if wrong. Return empty string if checker is not needed.",
                "solution_code": "The complete C++ source code of the optimal or brute-force solution"
            }
            
            PROBLEM DESCRIPTION:
            """ + problemDescription;
    }
    
    /**
     * Parses the JSON response from the AI.
     */
    public static GeneratorResponse parseResponse(String jsonResponse) {
        try {
            JSONObject obj = new JSONObject(jsonResponse);
            GeneratorResponse res = new GeneratorResponse();
            res.inputFormat = obj.optString("input_format");
            res.constraints = obj.optString("constraints");
            res.generatorCode = obj.optString("generator_code");
            res.checkerCode = obj.optString("checker_code");
            res.solutionCode = obj.optString("solution_code");
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class GeneratorResponse {
        public String inputFormat;
        public String constraints;
        public String generatorCode;
        public String checkerCode;
        public String solutionCode;
    }
}
