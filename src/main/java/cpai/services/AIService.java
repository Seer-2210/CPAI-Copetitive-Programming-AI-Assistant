package cpai.services;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class AIService {
    private static final String API_KEY = ""; // Thay key của bạn vào đây
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=" + API_KEY;

    private final OkHttpClient client;

    public AIService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
    }

    public AIProcessor.GeneratorResponse analyzeProblemForGenerator(String problemContent) throws IOException {
        String prompt = AIProcessor.buildPrompt(problemContent);

        JSONObject jsonBody = new JSONObject();
        JSONArray contents = new JSONArray();
        JSONObject parts = new JSONObject();
        parts.put("text", prompt);
        contents.put(new JSONObject().put("parts", new JSONArray().put(parts)));
        jsonBody.put("contents", contents);

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                System.out.println("ERROR BODY: " + errorBody);
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JSONObject mainObj = new JSONObject(responseBody);

            String aiText = mainObj.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            String cleanJson = aiText.replaceAll("```json", "").replaceAll("```", "").trim();
            
            return AIProcessor.parseResponse(cleanJson);
        }
    }

    public String extractTextFromImage(File imageFile) throws IOException {
        String prompt = "You are an expert at reading and formatting competitive programming problems. " +
                "Extract all the text from this image. Correct any obvious OCR typos. " +
                "Format the problem clearly with markdown using the following sections if applicable: " +
                "Problem Description, Input Format, Output Format, Constraints, and Examples. " +
                "Make it extremely clear and easy to understand.";

        // Read file and convert to Base64
        byte[] fileContent = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(fileContent);

        // Determine mime type
        String fileName = imageFile.getName().toLowerCase();
        String mimeType = "image/jpeg";
        if (fileName.endsWith(".png")) mimeType = "image/png";
        else if (fileName.endsWith(".webp")) mimeType = "image/webp";
        else if (fileName.endsWith(".heic")) mimeType = "image/heic";

        JSONObject jsonBody = new JSONObject();
        JSONArray contents = new JSONArray();
        JSONObject partsText = new JSONObject();
        partsText.put("text", prompt);

        JSONObject partsImage = new JSONObject();
        JSONObject inlineData = new JSONObject();
        inlineData.put("mime_type", mimeType);
        inlineData.put("data", base64Image);
        partsImage.put("inline_data", inlineData);

        JSONArray partsArray = new JSONArray();
        partsArray.put(partsText);
        partsArray.put(partsImage);

        contents.put(new JSONObject().put("parts", partsArray));
        jsonBody.put("contents", contents);

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                System.out.println("ERROR BODY: " + errorBody);
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JSONObject mainObj = new JSONObject(responseBody);

            String aiText = mainObj.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            return aiText.trim();
        }
    }
}